/*
 * Copyright (C) 2024 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package packet.client.request;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import debug.Debug;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.handler.InterServerHandler;
import handling.world.CharacterTransfer;
import handling.world.World;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import packet.client.ClientPacket;
import packet.ops.CashItemOps;
import packet.server.response.MapleTradeSpaceResponse;
import packet.server.response.PointShopResponse;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MTSStorage;
import server.MapleInventoryManipulator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Riremito
 */
public class PointShopRequest {

    /*
        @00F8 : CP_CashShopChargeParamRequest
        @00F9 : CP_CashShopQueryCashRequest
        @00FA : CP_CashShopCashItemRequest
        @00FB : CP_CashShopCheckCouponRequest
        @00FE : CP_JMS_RECOMMENDED_AVATAR

        @00AB : CP_CashGachaponOpenRequest
     */
    public static boolean OnPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) {
        switch (header) {
            // 入場リクエスト
            case CP_UserMigrateToCashShopRequest: {
                InterServerHandler.EnterCS(c, c.getPlayer(), false);
                return true;
            }
            // 退出
            case CP_UserTransferFieldRequest: {
                LeaveCS(c, c.getPlayer());
                return true;
            }
            // 入場
            case CP_MigrateIn: {
                int character_id = cp.Decode4();
                EnterCS(character_id, c);
                return true;
            }
            // 充填ボタンをクリックした場合の処理
            case CP_CashShopChargeParamRequest: {
                // ブラウザが開いてしまうので無効化
                //c.SendPacket(PointShopResponse.ChargeParamResult());
                c.enableCSActions();
                return true;
            }
            case CP_CashShopQueryCashRequest: {
                c.enableCSActions();
                return true;
            }
            case CP_CashShopCashItemRequest: {
                OnCashItem(cp, c);
                c.enableCSActions();
                return false;
            }
            case CP_CashShopCheckCouponRequest: {
                String character_name = cp.DecodeStr();
                String coupon_code = cp.DecodeStr();
                byte coupon_15 = cp.Decode1();
                if (!character_name.equals("")) {
                    String message = cp.DecodeStr();
                }
                // PointShopRequest.CouponCode
                Debug.DebugLog("Coupon Code = " + coupon_code);
                c.enableCSActions();
                return false;
            }
            // アバターランダムボックスのオープン処理
            case CP_CashGachaponOpenRequest: {
                c.enableCSActions();
                return false;
            }
            // オススメアバターを選択した時の処理
            case CP_JMS_RECOMMENDED_AVATAR: {
                c.enableCSActions();
                return false;
            }
            default: {
                break;
            }
        }

        c.enableCSActions();
        return false;
    }

    public static void EnterCS(final int playerid, final MapleClient c) {
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        boolean mts = false;
        if (transfer == null) {
            transfer = CashShopServer.getPlayerStorageMTS().getPendingCharacter(playerid);
            mts = true;
            if (transfer == null) {
                c.getSession().close();
                return;
            }
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);
        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());
        if (!c.CheckIPAddress()) {
            // Remote hack
            c.getSession().close();
            return;
        }
        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
            if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                allowLogin = true;
            }
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGEDIN, c.getSessionIPAddress());
        if (mts) {
            CashShopServer.getPlayerStorageMTS().registerPlayer(chr);
            c.getSession().write(MapleTradeSpaceResponse.SetITC(chr));
            MapleTradeSpaceRequest.MTSUpdate(MTSStorage.getInstance().getCart(c.getPlayer().getId()), c);
        } else {
            CashShopServer.getPlayerStorage().registerPlayer(chr);
            c.getSession().write(PointShopResponse.SetCashShop(c));
            c.getSession().write(PointShopResponse.QueryCashResult(c.getPlayer()));
            c.getSession().write(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_LoadLocker_Done, c));
        }
    }

    public static void LeaveCS(MapleClient c, MapleCharacter chr) {
        CashShopServer.getPlayerStorageMTS().deregisterPlayer(chr);
        CashShopServer.getPlayerStorage().deregisterPlayer(chr);
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());
        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.SendPacket(SocketPacket.MigrateCommand(ChannelServer.getInstance(c.getChannel()).getPort()));
        } finally {
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
        }
    }

    public static void CSUpdate(final MapleClient c) {
        c.getSession().write(PointShopResponse.getCSGifts(c));
        doCSPackets(c);
        c.getSession().write(PointShopResponse.sendWishList(c.getPlayer(), false));
    }

    public static final void BuyCashItem(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int action = slea.readByte();
        if (action == 0) {
            slea.skip(2);
            CouponCode(slea.readMapleAsciiString(), c);
        } else if (action == 4 || action == 32) {
            //gift, package
            slea.readMapleAsciiString(); // as13
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            String partnerName = slea.readMapleAsciiString();
            String msg = slea.readMapleAsciiString();
            if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                //dont want packet editors gifting random stuff =P
                c.getSession().write(PointShopResponse.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if (info == null || info.getLeft().intValue() <= 0 || info.getLeft().intValue() == c.getPlayer().getId() || info.getRight().getLeft().intValue() == c.getAccID()) {
                c.getSession().write(PointShopResponse.sendCSFail(162)); //9E v75
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(info.getRight().getRight().intValue())) {
                c.getSession().write(PointShopResponse.sendCSFail(163));
                doCSPackets(c);
                return;
            } else {
                for (int i : GameConstants.cashBlock) {
                    if (item.getId() == i) {
                        c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                        doCSPackets(c);
                        return;
                    }
                }
                c.getPlayer().getCashInventory().gift(info.getLeft().intValue(), c.getPlayer().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                c.getSession().write(PointShopResponse.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName));
            }
        } else if (action == 5) {
            // Wishlist
            chr.clearWishlist();
            if (slea.available() < 40) {
                c.getSession().write(PointShopResponse.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            int[] wishlist = new int[10];
            for (int i = 0; i < 10; i++) {
                wishlist[i] = slea.readInt();
            }
            chr.setWishlist(wishlist);
            c.getSession().write(PointShopResponse.sendWishList(chr, true));
        } else if (action == 36) {
            //36 = friendship, 30 = crush
            //c.getSession().write(MTSCSPacket.sendCSFail(0));
            slea.readMapleAsciiString(); // as13
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            final String partnerName = slea.readMapleAsciiString();
            final String msg = slea.readMapleAsciiString();
            if (item == null || !GameConstants.isEffectRing(item.getId()) || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                c.getSession().write(PointShopResponse.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(c.getPlayer().getGender())) {
                c.getSession().write(PointShopResponse.sendCSFail(166));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                c.getSession().write(PointShopResponse.sendCSFail(177));
                doCSPackets(c);
                return;
            }
            for (int i : GameConstants.cashBlock) {
                //just incase hacker
                if (item.getId() == i) {
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    doCSPackets(c);
                    return;
                }
            }
            Pair<Integer, Pair<Integer, Integer>> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if (info == null || info.getLeft().intValue() <= 0 || info.getLeft().intValue() == c.getPlayer().getId()) {
                c.getSession().write(PointShopResponse.sendCSFail(180)); //9E v75
                doCSPackets(c);
                return;
            } else if (info.getRight().getLeft().intValue() == c.getAccID()) {
                c.getSession().write(PointShopResponse.sendCSFail(163)); //9D v75
                doCSPackets(c);
                return;
            } else {
                if (info.getRight().getRight().intValue() == c.getPlayer().getGender() && action == 30) {
                    c.getSession().write(PointShopResponse.sendCSFail(161)); //9B v75
                    doCSPackets(c);
                    return;
                }
                int err = MapleRing.createRing(item.getId(), c.getPlayer(), partnerName, msg, info.getLeft().intValue(), item.getSN());
                if (err != 1) {
                    c.getSession().write(PointShopResponse.sendCSFail(0)); //9E v75
                    doCSPackets(c);
                    return;
                }
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                //c.getSession().write(MTSCSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                c.getSession().write(PointShopResponse.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName));
            }
        } else if (action == 31) {
            slea.skip(1);
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            List<CashItemInfo> ccc = null;
            if (item != null) {
                ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
            }
            if (item == null || ccc == null || c.getPlayer().getCSPoints(1) < item.getPrice()) {
                c.getSession().write(PointShopResponse.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(c.getPlayer().getGender())) {
                c.getSession().write(PointShopResponse.sendCSFail(166));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
                c.getSession().write(PointShopResponse.sendCSFail(177));
                doCSPackets(c);
                return;
            }
            for (int iz : GameConstants.cashBlock) {
                if (item.getId() == iz) {
                    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                    doCSPackets(c);
                    return;
                }
            }
            Map<Integer, IItem> ccz = new HashMap<Integer, IItem>();
            for (CashItemInfo i : ccc) {
                for (int iz : GameConstants.cashBlock) {
                    if (i.getId() == iz) {
                        continue;
                    }
                }
                IItem itemz = c.getPlayer().getCashInventory().toItem(i);
                if (itemz == null || itemz.getUniqueId() <= 0 || itemz.getItemId() != i.getId()) {
                    continue;
                }
                ccz.put(i.getSN(), itemz);
                c.getPlayer().getCashInventory().addToInventory(itemz);
            }
            chr.modifyCSPoints(1, -item.getPrice(), false);
            c.getSession().write(PointShopResponse.showBoughtCSPackage(ccz, c.getAccID()));
        } else {
            c.getSession().write(PointShopResponse.sendCSFail(0));
        }
        doCSPackets(c);
    }

    // BuyCashItem
    public static boolean OnCashItem(ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();

        if (chr == null) {
            return false;
        }

        byte type = cp.Decode1();
        CashItemOps flag = CashItemOps.find(type);

        switch (flag) {
            // 0x03
            case CashItemReq_Buy: {
                byte unk1 = cp.Decode1();
                int item_SN = cp.Decode4();
                return BuyCashItem(item_SN, c);
            }
            // 0x06
            case CashItemReq_IncSlotCount: {
                byte unk1 = cp.Decode1();
                byte is_slot8 = cp.Decode1();

                // 8 slot
                if (is_slot8 != 0) {
                    int item_SN = cp.Decode4();
                    return BuyCashItem(item_SN, c);
                }
                // 4 slot
                byte inv_type = cp.Decode1();
                if (IncSlotCount4(inv_type, chr)) {
                    c.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_IncSlotCount_Done, c, new PointShopResponse.CashItemStruct(MapleInventoryType.getByType(inv_type))));
                } else {
                    // faield   
                }
                return true;
            }
            // 0x07
            case CashItemReq_IncTrunkCount: {
                byte unk1 = cp.Decode1();
                byte unk2 = cp.Decode1();
                if (IncTrunkCount4(chr)) {
                    c.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_IncTrunkCount_Done, c));
                } else {
                    // failed
                }
                return true;
            }
            // 0x1B
            case CashItemReq_Destroy: {
                String nexon_id = cp.DecodeStr();
                long item_unique_id = cp.Decode8(); // buffer8
                return DestoryItem(chr, item_unique_id);
            }
            // 0x21
            case CashItemReq_BuyNormal: {
                int item_SN = cp.Decode4();
                return BuyCashItem(item_SN, c);
            }
            default: {
                Debug.ErrorLog("OnCashItem not coded : " + type);
                break;
            }
        }

        return false;
    }

    private static boolean BuyCashItem(int item_SN, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        CashItemInfo cashitem = CashItemFactory.getInstance().getItem(item_SN);

        if (chr == null) {
            Debug.ErrorLog("BuyCashItem : chr");
            return false;
        }

        if (cashitem == null) {
            Debug.ErrorLog("BuyCashItem : Invalid Item");
            return false;
        }

        // Nexon Point 残高不足
        if (!chr.checkNexonPoint(cashitem.getPrice())) {
            Debug.ErrorLog("BuyCashItem : np");
            return false;
        }

        IItem item = chr.getCashInventory().toItem(cashitem);
        if (item != null && item.getUniqueId() > 0 && item.getItemId() == cashitem.getId() && item.getQuantity() == cashitem.getCount()) {
            chr.getCashInventory().addToInventory(item);
            c.getSession().write(PointShopResponse.showBoughtCSItem(item, cashitem.getSN(), c.getAccID()));
            chr.useNexonPoint(cashitem.getPrice());
        } else {
            Debug.ErrorLog("BuyCashItem : ERR");
        }

        return true;
    }

    private static final int INC_INVENTORY_SLOT_PRICE = 390;
    private static final int SLOT_LIMIT = 96;
    private static final int TRUNK_SLOT_LIMIT = 60;

    private static boolean IncSlotCount4(byte inv_type, MapleCharacter chr) {
        return IncSlotCount(inv_type, chr, 4);
    }

    private static boolean IncSlotCount8(int item_SN, MapleCharacter chr) {
        // not coded
        return IncSlotCount((byte) 0, chr, 8);
    }

    private static boolean IncSlotCount(byte inv_type, MapleCharacter chr, int inc_slot) {
        if (!chr.checkNexonPoint(INC_INVENTORY_SLOT_PRICE)) {
            // 残高不足
            return false;
        }

        MapleInventoryType type = MapleInventoryType.getByType(inv_type);
        // スロット数上限確認
        if (type == null || SLOT_LIMIT < (chr.getInventory(type).getSlotLimit() + inc_slot)) {
            return false;
        }

        chr.useNexonPoint(INC_INVENTORY_SLOT_PRICE);
        chr.getInventory(type).addSlot((byte) inc_slot);
        return true;
    }

    private static boolean IncTrunkCount4(MapleCharacter chr) {
        if (!chr.checkNexonPoint(INC_INVENTORY_SLOT_PRICE)) {
            // 残高不足
            return false;
        }

        // スロット数上限確認
        if (TRUNK_SLOT_LIMIT < (chr.getStorage().getSlots() + 4)) {
            return false;
        }

        chr.useNexonPoint(INC_INVENTORY_SLOT_PRICE);
        chr.getStorage().increaseSlots((byte) 4);
        return true;
    }

    private static boolean DestoryItem(MapleCharacter chr, long item_unique_id) {
        IItem item = chr.getCashInventory().findByCashId((int) item_unique_id);

        if (item != null && item.getQuantity() > 0 && item.getOwner().equals(chr.getName())) {
            chr.getCashInventory().removeFromInventory(item);
            //chr.SendPacket(PointShopResponse.confirmFromCSInventory(item, pos));
            return true;
        }

        return false;
    }

    public static void CouponCode(final String code, final MapleClient c) {
        boolean validcode = false;
        int type = -1;
        int item = -1;
        try {
            validcode = MapleCharacterUtil.getNXCodeValid(code.toUpperCase(), validcode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (validcode) {
            try {
                type = MapleCharacterUtil.getNXCodeType(code);
                item = MapleCharacterUtil.getNXCodeItem(code);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (type != 4) {
                try {
                    MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            /*
             * Explanation of type!
             * Basically, this makes coupon codes do
             * different things!
             *
             * Type 1: A-Cash,
             * Type 2: Maple Points
             * Type 3: Item.. use SN
             * Type 4: A-Cash Coupon that can be used over and over
             * Type 5: Mesos
             */
            Map<Integer, IItem> itemz = new HashMap<Integer, IItem>();
            int maplePoints = 0;
            int mesos = 0;
            switch (type) {
                case 1:
                case 2:
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                case 3:
                    CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                    if (itez == null) {
                        c.getSession().write(PointShopResponse.sendCSFail(0));
                        doCSPackets(c);
                        return;
                    }
                    byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short) 1, "");
                    if (slot <= -1) {
                        c.getSession().write(PointShopResponse.sendCSFail(0));
                        doCSPackets(c);
                        return;
                    } else {
                        itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                    }
                    break;
                case 4:
                    c.getPlayer().modifyCSPoints(1, item, false);
                    maplePoints = item;
                    break;
                case 5:
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                    break;
            }
            c.getSession().write(PointShopResponse.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        } else {
            c.getSession().write(PointShopResponse.sendCSFail(validcode ? 165 : 167)); //A1, 9F
        }
        doCSPackets(c);
    }

    private static final void doCSPackets(MapleClient c) {
        c.getSession().write(PointShopResponse.QueryCashResult(c.getPlayer()));
        //c.getSession().write(MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }

    private static final MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200075:
                return MapleInventoryType.EQUIP;
            case 50200074:
                return MapleInventoryType.USE;
            case 50200073:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }

}
