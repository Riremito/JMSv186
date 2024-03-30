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
import client.inventory.MapleInventory;
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
import packet.server.response.MapleTradeSpaceResponse;
import packet.server.response.PointShopResponse;
import server.CashItemFactory;
import server.CashItemInfo;
import server.CashShop;
import server.MTSStorage;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Riremito
 */
public class PointShopRequest {

    public enum Ops {
        CashItemReq_WebShopOrderGetList(0x0),
        CashItemReq_LoadLocker(0x1),
        CashItemReq_LoadWish(0x2),
        CashItemReq_Buy(0x3),
        CashItemReq_Gift(0x4),
        CashItemReq_SetWish(0x5),
        CashItemReq_IncSlotCount(0x6),
        CashItemReq_IncTrunkCount(0x7),
        CashItemReq_IncCharSlotCount(0x8),
        CashItemReq_IncBuyCharCount(0x9),
        CashItemReq_EnableEquipSlotExt(0xA),
        CashItemReq_CancelPurchase(0xB),
        CashItemReq_ConfirmPurchase(0xC),
        CashItemReq_Destroy(0xD),
        CashItemReq_MoveLtoS(0xE),
        CashItemReq_MoveStoL(0xF),
        CashItemReq_Expire(0x10),
        CashItemReq_Use(0x11),
        CashItemReq_StatChange(0x12),
        CashItemReq_SkillChange(0x13),
        CashItemReq_SkillReset(0x14),
        CashItemReq_DestroyPetItem(0x15),
        CashItemReq_SetPetName(0x16),
        CashItemReq_SetPetLife(0x17),
        CashItemReq_SetPetSkill(0x18),
        CashItemReq_SetItemName(0x19),
        CashItemReq_SendMemo(0x1A),
        CashItemReq_GetMaplePoint(0x1B),
        CashItemReq_Rebate(0x1C),
        CashItemReq_UseCoupon(0x1D),
        CashItemReq_GiftCoupon(0x1E),
        CashItemReq_Couple(0x1F),
        CashItemReq_BuyPackage(0x20),
        CashItemReq_GiftPackage(0x21),
        CashItemReq_BuyNormal(0x22),
        CashItemReq_ApplyWishListEvent(0x23),
        CashItemReq_MovePetStat(0x24),
        CashItemReq_FriendShip(0x25),
        CashItemReq_ShopScan(0x26),
        CashItemReq_LoadPetExceptionList(0x27),
        CashItemReq_UpdatePetExceptionList(0x28),
        CashItemReq_FreeCashItem(0x29),
        CashItemReq_LoadFreeCashItem(0x2A),
        CashItemReq_Script(0x2B),
        CashItemReq_PurchaseRecord(0x2C),
        CashItemReq_TradeDone(0x2D),
        CashItemReq_BuyDone(0x2E),
        CashItemReq_TradeSave(0x2F),
        CashItemReq_TradeLog(0x30),
        CashItemReq_EvolPet(0x31),
        CashItemReq_BuyNameChange(0x32),
        CashItemReq_CancelChangeName(0x33),
        CashItemRes_CancelNameChangeFail(0x34),
        CashItemReq_BuyTransferWorld(0x35),
        CashItemReq_CancelTransferWorld(0x36),
        CashItemReq_CharacterSale(0x37),
        CashItemRes_CharacterSaleSuccess(0x38),
        CashItemRes_CharacterSaleFail(0x39),
        CashItemRes_CharacterSaleInvalidName(0x3A),
        CashItemRes_CharacterSaleInvalidItem(0x3B),
        CashItemReq_ItemUpgrade(0x3C),
        CashItemRes_ItemUpgradeSuccess(0x3D),
        CashItemReq_ItemUpgradeFail(0x3E),
        CashItemReq_ItemUpgradeReq(0x3F),
        CashItemReq_ItemUpgradeDone(0x40),
        CashItemRes_ItemUpgradeDone(0x41),
        CashItemRes_ItemUpgradeErr(0x42),
        CashItemReq_Vega(0x43),
        CashItemRes_VegaSuccess1(0x44),
        CashItemRes_VegaSuccess2(0x45),
        CashItemRes_VegaErr(0x46),
        CashItemRes_VegaErr2(0x47),
        CashItemRes_VegaErr_InvalidItem(0x48),
        CashItemRes_VegaFail(0x49),
        CashItemReq_CashItemGachapon(0x4A),
        CashItemReq_CashGachaponOpen(0x4B),
        CashItemReq_CashGachaponCopy(0x4C),
        CashItemReq_ChangeMaplePoint(0x4D),
        CashItemReq_CheckFreeCashItemTable(0x4E),
        CashItemRes_CheckFreeCashItemTable_Done(0x4F),
        CashItemRes_CheckFreeCashItemTable_Failed(0x50),
        CashItemReq_SetFreeCashItemTable(0x51),
        CashItemRes_SetFreeCashItemTable_Done(0x52),
        CashItemRes_SetFreeCashItemTable_Failed(0x53),
        CashItemRes_LimitGoodsCount_Changed(0x54),
        CashItemRes_WebShopOrderGetList_Done(0x55),
        CashItemRes_WebShopOrderGetList_Failed(0x56),
        CashItemRes_WebShopReceive_Done(0x57),
        CashItemRes_LoadLocker_Done(0x58),
        CashItemRes_LoadLocker_Failed(0x59),
        CashItemRes_LoadGift_Done(0x5A),
        CashItemRes_LoadGift_Failed(0x5B),
        CashItemRes_LoadWish_Done(0x5C),
        CashItemRes_LoadWish_Failed(0x5D),
        CashItemRes_MapleTV_Failed_Wrong_User_Name(0x5E),
        CashItemRes_MapleTV_Failed_User_Not_Connected(0x5F),
        CashItemRes_AvatarMegaphone_Queue_Full(0x60),
        CashItemRes_AvatarMegaphone_Level_Limit(0x61),
        CashItemRes_SetWish_Done(0x62),
        CashItemRes_SetWish_Failed(0x63),
        CashItemRes_Buy_Done(0x64),
        CashItemRes_Buy_Failed(0x65),
        CashItemRes_UseCoupon_Done(0x66),
        CashItemRes_UseCoupon_Done_NormalItem(0x67),
        CashItemRes_GiftCoupon_Done(0x68),
        CashItemRes_UseCoupon_Failed(0x69),
        CashItemRes_UseCoupon_CashItem_Failed(0x6A),
        CashItemRes_Gift_Done(0x6B),
        CashItemRes_Gift_Failed(0x6C),
        CashItemRes_IncSlotCount_Done(0x6D),
        CashItemRes_IncSlotCount_Failed(0x6E),
        CashItemRes_IncTrunkCount_Done(0x6F),
        CashItemRes_IncTrunkCount_Failed(0x70),
        CashItemRes_IncCharSlotCount_Done(0x71),
        CashItemRes_IncCharSlotCount_Failed(0x72),
        CashItemRes_IncBuyCharCount_Done(0x73),
        CashItemRes_IncBuyCharCount_Failed(0x74),
        CashItemRes_EnableEquipSlotExt_Done(0x75),
        CashItemRes_EnableEquipSlotExt_Failed(0x76),
        CashItemRes_MoveLtoS_Done(0x77),
        CashItemRes_MoveLtoS_Failed(0x78),
        CashItemRes_MoveStoL_Done(0x79),
        CashItemRes_MoveStoL_Failed(0x7A),
        CashItemRes_Destroy_Done(0x7B),
        CashItemRes_Destroy_Failed(0x7C),
        CashItemRes_Expire_Done(0x7D),
        CashItemRes_Expire_Failed(0x7E),
        CashItemRes_Use_Done(0x7F),
        CashItemRes_Use_Failed(0x80),
        CashItemRes_StatChange_Done(0x81),
        CashItemRes_StatChange_Failed(0x82),
        CashItemRes_SkillChange_Done(0x83),
        CashItemRes_SkillChange_Failed(0x84),
        CashItemRes_SkillReset_Done(0x85),
        CashItemRes_SkillReset_Failed(0x86),
        CashItemRes_DestroyPetItem_Done(0x87),
        CashItemRes_DestroyPetItem_Failed(0x88),
        CashItemRes_SetPetName_Done(0x89),
        CashItemRes_SetPetName_Failed(0x8A),
        CashItemRes_SetPetLife_Done(0x8B),
        CashItemRes_SetPetLife_Failed(0x8C),
        CashItemRes_MovePetStat_Failed(0x8D),
        CashItemRes_MovePetStat_Done(0x8E),
        CashItemRes_SetPetSkill_Failed(0x8F),
        CashItemRes_SetPetSkill_Done(0x90),
        CashItemRes_SendMemo_Done(0x91),
        CashItemRes_SendMemo_Warning(0x92),
        CashItemRes_SendMemo_Failed(0x93),
        CashItemRes_GetMaplePoint_Done(0x94),
        CashItemRes_GetMaplePoint_Failed(0x95),
        CashItemRes_Rebate_Done(0x96),
        CashItemRes_Rebate_Failed(0x97),
        CashItemRes_Couple_Done(0x98),
        CashItemRes_Couple_Failed(0x99),
        CashItemRes_BuyPackage_Done(0x9A),
        CashItemRes_BuyPackage_Failed(0x9B),
        CashItemRes_GiftPackage_Done(0x9C),
        CashItemRes_GiftPackage_Failed(0x9D),
        CashItemRes_BuyNormal_Done(0x9E),
        CashItemRes_BuyNormal_Failed(0x9F),
        CashItemRes_ApplyWishListEvent_Done(0xA0),
        CashItemRes_ApplyWishListEvent_Failed(0xA1),
        CashItemRes_Friendship_Done(0xA2),
        CashItemRes_Friendship_Failed(0xA3),
        CashItemRes_LoadExceptionList_Done(0xA4),
        CashItemRes_LoadExceptionList_Failed(0xA5),
        CashItemRes_UpdateExceptionList_Done(0xA6),
        CashItemRes_UpdateExceptionList_Failed(0xA7),
        CashItemRes_LoadFreeCashItem_Done(0xA8),
        CashItemRes_LoadFreeCashItem_Failed(0xA9),
        CashItemRes_FreeCashItem_Done(0xAA),
        CashItemRes_FreeCashItem_Failed(0xAB),
        CashItemRes_Script_Done(0xAC),
        CashItemRes_Script_Failed(0xAD),
        CashItemRes_Bridge_Failed(0xAE),
        CashItemRes_PurchaseRecord_Done(0xAF),
        CashItemRes_PurchaseRecord_Failed(0xB0),
        CashItemRes_EvolPet_Failed(0xB1),
        CashItemRes_EvolPet_Done(0xB2),
        CashItemRes_NameChangeBuy_Done(0xB3),
        CashItemRes_NameChangeBuy_Failed(0xB4),
        CashItemRes_TransferWorld_Done(0xB5),
        CashItemRes_TransferWorld_Failed(0xB6),
        CashItemRes_CashGachaponOpen_Done(0xB7),
        CashItemRes_CashGachaponOpen_Failed(0xB8),
        CashItemRes_CashGachaponCopy_Done(0xB9),
        CashItemRes_CashGachaponCopy_Failed(0xBA),
        CashItemRes_ChangeMaplePoint_Done(0xBB),
        CashItemRes_ChangeMaplePoint_Failed(0xBC),
        CashItemReq_Give(0xBD),
        CashItemRes_Give_Done(0xBE),
        CashItemRes_Give_Failed(0xBF),
        CashItemRes_GashItemGachapon_Failed(0xC0),
        CashItemRes_CashItemGachapon_Done(0xC1),
        UNKNOWN(-1);

        private int value;

        Ops(int flag) {
            value = flag;
        }

        Ops() {
            value = -1;
        }

        public int get() {
            return value;
        }

        public static Ops find(int val) {
            for (final Ops o : Ops.values()) {
                if (o.get() == val) {
                    return o;
                }
            }
            return UNKNOWN;
        }
    }

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
                CSUpdate(c);
                return true;
            }
            case CP_CashShopQueryCashRequest: {
                CSUpdate(c);
                return true;
            }
            case CP_CashShopCashItemRequest: {
                // PointShopRequest.BuyCashItem(p, c, c.getPlayer());
                OnCashItem(cp, c);
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
                CSUpdate(c);
                return false;
            }
            case CP_CashShopMemberShopRequest: {
                return false;
            }
            case CP_CashShopGiftMateInfoRequest: {
                return false;
            }
            case CP_CashShopSearchLog: {
                return false;
            }
            case CP_CashShopCoodinationRequest: {
                return false;
            }
            case CP_CashShopCheckMileageRequest: {
                return false;
            }
            case CP_CashShopNaverUsageInfoRequest: {
                return false;
            }
            // アバターランダムボックスのオープン処理
            case CP_CashGachaponOpenRequest: {
                CSUpdate(c);
                return false;
            }
            // オススメアバターを選択した時の処理
            case CP_JMS_RECOMMENDED_AVATAR: {
                CSUpdate(c);
                return false;
            }
            default: {
                break;
            }
        }
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
            CSUpdate(c);
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
        } else if (action == 3) {
            slea.skip(1);
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item != null && chr.getCSPoints(1) >= item.getPrice()) {
                if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.getSession().write(PointShopResponse.sendCSFail(166));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.getSession().write(PointShopResponse.sendCSFail(177));
                    doCSPackets(c);
                    return;
                }
                /*
                for (int i : GameConstants.cashBlock) {
                if (item.getId() == i) {
                c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getId()));
                doCSPackets(c);
                return;
                }
                }
                 */
                chr.modifyCSPoints(1, -item.getPrice(), false);
                IItem itemz = chr.getCashInventory().toItem(item);
                if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                    chr.getCashInventory().addToInventory(itemz);
                    //c.getSession().write(MTSCSPacket.confirmToCSInventory(itemz, c.getAccID(), item.getSN()));
                    c.getSession().write(PointShopResponse.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                } else {
                    c.getSession().write(PointShopResponse.sendCSFail(0));
                }
            } else {
                c.getSession().write(PointShopResponse.sendCSFail(0));
            }
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
        } else if (action == 6) {
            // Increase inv
            slea.skip(1);
            final boolean coupon = slea.readByte() > 0;
            if (coupon) {
                final MapleInventoryType type = getInventoryType(slea.readInt());
                if (chr.getCSPoints(1) >= 12000 && chr.getInventory(type).getSlotLimit() < 89) {
                    chr.modifyCSPoints(1, -12000, false);
                    chr.getInventory(type).addSlot((byte) 8);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                } else {
                    c.getSession().write(PointShopResponse.sendCSFail(164));
                }
            } else {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                if (chr.getCSPoints(1) >= 8000 && chr.getInventory(type).getSlotLimit() < 93) {
                    chr.modifyCSPoints(1, -8000, false);
                    chr.getInventory(type).addSlot((byte) 4);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                } else {
                    c.getSession().write(PointShopResponse.sendCSFail(164));
                }
            }
        } else if (action == 7) {
            // Increase slot space
            if (chr.getCSPoints(1) >= 8000 && chr.getStorage().getSlots() < 45) {
                chr.modifyCSPoints(1, -8000, false);
                chr.getStorage().increaseSlots((byte) 4);
                chr.getStorage().saveToDB();
                c.getSession().write(PointShopResponse.increasedStorageSlots(chr.getStorage().getSlots()));
            } else {
                c.getSession().write(PointShopResponse.sendCSFail(164));
            }
        } else if (action == 8) {
            //...9 = pendant slot expansion
            slea.readByte();
            CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            int slots = c.getCharacterSlots();
            if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || slots > 15) {
                c.getSession().write(PointShopResponse.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
            if (c.gainCharacterSlot()) {
                c.getSession().write(PointShopResponse.increasedStorageSlots(slots + 1));
            } else {
                c.getSession().write(PointShopResponse.sendCSFail(0));
            }
        } else if (action == 14) {
            // 購物商城→道具欄位
            //uniqueid, 00 01 01 00, type->position(short)
            IItem item = c.getPlayer().getCashInventory().findByCashId((int) slea.readLong());
            if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                IItem item_ = item.copy();
                short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                if (pos >= 0) {
                    if (item_.getPet() != null) {
                        item_.getPet().setInventoryPosition(pos);
                        c.getPlayer().addPet(item_.getPet());
                    }
                    c.getPlayer().getCashInventory().removeFromInventory(item);
                    c.getSession().write(PointShopResponse.confirmFromCSInventory(item_, pos));
                } else {
                    c.getSession().write(PointShopResponse.sendCSFail(177));
                }
            } else {
                c.getSession().write(PointShopResponse.sendCSFail(177));
            }
        } else if (action == 15) {
            // 道具欄位→購物商城
            IItem item1;
            int sn;
            CashShop cs = chr.getCashInventory();
            int cashId = (int) slea.readLong();
            byte type = slea.readByte();
            MapleInventory mi = chr.getInventory(MapleInventoryType.getByType(type));
            item1 = mi.findByUniqueId(cashId);
            if (item1 == null) {
                c.getSession().write(PointShopResponse.QueryCashResult(chr));
                return;
            }
            if (cs.getItemsSize() < 100) {
                sn = CashItemFactory.getInstance().getItemSN(item1.getItemId());
                cs.addToInventory(item1);
                mi.removeSlot(item1.getPosition());
                c.getSession().write(PointShopResponse.confirmToCSInventory(item1, c.getAccID(), sn));
            } else {
                chr.dropMessage(1, "\u79fb\u52d5\u5931\u6557\u3002");
            }
            /*            int uniqueid = (int) slea.readLong();
            MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
            IItem item = c.getPlayer().getInventory(type).findByUniqueId(uniqueid);
            if (item != null && item.getQuantity() > 0 && item.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
            IItem item_ = item.copy();
            MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), false);
            if (item_.getPet() != null) {
            c.getPlayer().removePetCS(item_.getPet());
            }
            item_.setPosition((byte) 0);
            c.getPlayer().getCashInventory().addToInventory(item_);
            //warning: this d/cs
            //c.getSession().write(MTSCSPacket.confirmToCSInventory(item, c.getAccID(), c.getPlayer().getCashInventory().getSNForItem(item)));
            } else {
            c.getSession().write(MTSCSPacket.sendCSFail(0xB1));
            }*/
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
        } else if (action == 33) {
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                c.getSession().write(PointShopResponse.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getMeso() < item.getPrice()) {
                c.getSession().write(PointShopResponse.sendCSFail(184));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
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
            byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short) item.getCount(), null);
            if (pos < 0) {
                c.getSession().write(PointShopResponse.sendCSFail(177));
                doCSPackets(c);
                return;
            }
            chr.gainMeso(-item.getPrice(), false);
            c.getSession().write(PointShopResponse.showBoughtCSQuestItem(item.getPrice(), (short) item.getCount(), pos, item.getId()));
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
        Ops flag = Ops.find(type);

        switch (flag) {
            case CashItemReq_WebShopOrderGetList: {
                break;
            }
            case CashItemReq_LoadLocker: {
                break;
            }
            case CashItemReq_LoadWish: {
                break;
            }
            case CashItemReq_Buy: {
                break;
            }
            case CashItemReq_Gift: {
                break;
            }
            case CashItemReq_SetWish: {
                break;
            }
            case CashItemReq_IncSlotCount: {
                break;
            }
            case CashItemReq_IncTrunkCount: {
                break;
            }
            case CashItemReq_IncCharSlotCount: {
                break;
            }
            case CashItemReq_IncBuyCharCount: {
                break;
            }
            case CashItemReq_EnableEquipSlotExt: {
                break;
            }
            default: {
                Debug.ErrorLog("OnCashItem not coded : " + type);
                break;
            }
        }

        return true;
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
        c.getSession().write(PointShopResponse.getCSInventory(c));
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
