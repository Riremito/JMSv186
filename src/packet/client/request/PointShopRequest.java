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
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import debug.Debug;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.channel.handler.InterServerHandler;
import handling.world.CharacterTransfer;
import handling.world.World;
import packet.client.ClientPacket;
import packet.ops.CashItemOps;
import packet.server.response.MapleTradeSpaceResponse;
import packet.server.response.PointShopResponse;
import server.CashItemFactory;
import server.CashItemInfo;
import server.MTSStorage;
import server.MapleInventoryManipulator;
import wz.LoadData;

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
                long box_SN = cp.Decode8();
                return OnGachaponOpen(c, box_SN);
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
            // 0x0E
            case CashItemReq_MoveLtoS: {
                long cash_item_SN = cp.Decode8();
                byte inv_type = cp.Decode1();
                short inv_slot = cp.Decode2();
                return MoveLtoS(c, cash_item_SN, inv_type, inv_slot);
            }
            // 0x0F
            case CashItemReq_MoveStoL: {
                long inv_item_SN = cp.Decode8();
                byte inv_type = cp.Decode1();
                return MoveStoL(c, inv_item_SN, inv_type);
            }
            // 0x1B
            case CashItemReq_Destroy: {
                String nexon_id = cp.DecodeStr();
                long item_unique_id = cp.Decode8(); // buffer8
                return DestoryItem(chr, nexon_id, item_unique_id);
            }
            // 0x05
            case CashItemReq_SetWish:
            // 0x1F
            case CashItemReq_BuyPackage: {
                // not coded
                c.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_SetWish_Failed, c));
                return true;
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
            c.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_Buy_Done, c, new PointShopResponse.CashItemStruct(item)));
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

    // ポイントショップからアイテム欄へ移動
    private static boolean MoveLtoS(MapleClient c, long cash_item_SN, byte inv_type, short inv_slot) {
        MapleCharacter chr = c.getPlayer();
        IItem item_src = chr.getCashInventory().findByCashId(cash_item_SN);
        if (item_src == null || item_src.getQuantity() < 1) {
            chr.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_MoveLtoS_Failed, c));
            return false;
        }
        IItem item_dst = item_src.copy();
        // アイテム欄へ移動
        short dst_slot = MapleInventoryManipulator.addbyItem(chr.getClient(), item_dst, true);
        // ポイントショップ上から削除
        chr.getCashInventory().removeFromInventory(item_src);
        chr.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_MoveLtoS_Done, c, new PointShopResponse.CashItemStruct(item_dst)));
        return true;
    }

    // アイテム欄からポイントショップへ移動
    private static boolean MoveStoL(MapleClient c, long inv_item_SN, byte inv_type) {
        MapleCharacter chr = c.getPlayer();
        MapleInventoryType inv_item_type = MapleInventoryType.getByType(inv_type);

        if (inv_item_type == null) {
            chr.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_MoveStoL_Failed, c));
            return false;
        }

        MapleInventory inv = chr.getInventory(inv_item_type);
        IItem item_src = inv.findByUniqueId(inv_item_SN);

        if (item_src == null || item_src.getQuantity() < 1) {
            chr.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_MoveStoL_Failed, c));
            return false;
        }
        IItem item_dst = item_src.copy();
        // ポイントショップへ移動
        chr.getCashInventory().addToInventory(item_dst);
        // アイテム欄から削除
        inv.removeSlot(item_src.getPosition());
        chr.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_MoveStoL_Done, c, new PointShopResponse.CashItemStruct(item_dst)));
        return true;
    }

    private static boolean DestoryItem(MapleCharacter chr, String nexon_id, long item_unique_id) {
        IItem item = chr.getCashInventory().findByCashId(item_unique_id);

        if (item == null || item.getQuantity() < 1 || !chr.getClient().getAccountName().equals(nexon_id)) {
            chr.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_Destroy_Failed, chr.getClient()));
            return false;
        }

        chr.getCashInventory().removeFromInventory(item);
        chr.SendPacket(PointShopResponse.CashItemResult(CashItemOps.CashItemRes_Destroy_Done, chr.getClient(), new PointShopResponse.CashItemStruct(item)));
        return true;
    }

    private static boolean OnGachaponOpen(MapleClient c, long box_SN) {
        MapleCharacter chr = c.getPlayer();

        if (chr == null) {
            return false;
        }

        IItem box_item = chr.getCashInventory().findByCashId(box_SN);

        if (box_item == null || box_item.getQuantity() < 1) {
            return false;
        }

        int test_item_SN = CashItemFactory.getInstance().getItemSN(1002239); // test
        CashItemInfo cashitem = CashItemFactory.getInstance().getItem(test_item_SN);
        IItem item = chr.getCashInventory().toItem(cashitem);

        if (item != null && item.getUniqueId() > 0 && item.getItemId() == cashitem.getId() && item.getQuantity() == cashitem.getCount() && LoadData.IsValidItemID(item.getItemId())) {
            chr.getCashInventory().removeFromInventory(box_item);
            chr.getCashInventory().addToInventory(item);
            c.SendPacket(PointShopResponse.OnCashItemGachaponResult(box_item, item, c));
        }

        return true;
    }

    private static final void doCSPackets(MapleClient c) {
        c.getSession().write(PointShopResponse.QueryCashResult(c.getPlayer()));
        c.getPlayer().getCashInventory().checkExpire(c);
    }

}
