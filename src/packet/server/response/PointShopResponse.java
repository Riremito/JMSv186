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
package packet.server.response;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import handling.MaplePacket;
import java.util.List;
import java.util.Map;
import packet.ops.CashItemFailReasonOps;
import packet.ops.CashItemOps;
import packet.server.ServerPacket;
import packet.server.response.struct.CharacterData;
import packet.server.response.struct.GW_CashItemInfo;
import packet.server.response.struct.TestHelper;
import server.CashItemFactory;
import server.CashItemInfo;
import server.CashShop;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class PointShopResponse {

    /*
        @016D : LP_CashShopChargeParamResult
        @016E : LP_JMS_POINTSHOP_PRESENT_DIALOG
        @016F : LP_CashShopQueryCashResult
        @0170 : LP_CashShopCashItemResult
        @0171 : LP_CashShopPurchaseExpChanged
        @0172 : LP_CashShopGiftMateInfoResult
        @0173 : LP_JMS_
        @0174 : LP_JMS_POINTSHOP_KOC_PRESENT_DIALOG
        @0175 : LP_JMSD
        LP_CashShopCheckDuplicatedIDResult
        LP_CashShopCheckNameChangePossibleResult
        LP_CashShopRegisterNewCharacterResult
        @0177 : LP_CashShopGachaponStampItemResult
        @0178 : LP_CashShopCheckTransferWorldPossibleResult
        LP_CashShopCashItemGachaponResult
        @0179 : LP_CashShopCashGachaponOpenResult
        LP_ChangeMaplePointResult
        LP_CashShopOneADay
        LP_CashShopNoticeFreeCashItem
        LP_CashShopMemberShopResult
     */
    // CStage::OnSetCashShop
    public static MaplePacket SetCashShop(MapleClient c) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetCashShop);
        sp.EncodeBuffer(CharacterData.Encode(c.getPlayer()));
        // CCashShop::LoadData
        {
            sp.EncodeStr(c.getAccountName());
            // CWvsContext::SetSaleInfo
            {
                if (ServerConfig.IsPostBB()) {
                    sp.Encode4(0); // NotSaleCount
                }
                sp.Encode2(0); // non 0, CS_COMMODITY::DecodeModifiedData
                if (ServerConfig.IsJMS() && 165 <= ServerConfig.GetVersion()) {
                    sp.Encode2(0); // non 0, Decode4, DecodeStr
                }
                sp.Encode1(0); // DiscountRate
            }
            sp.EncodeZeroBytes(1080);
            sp.Encode2(0); // CCashShop::DecodeStock
            sp.Encode2(0); // CCashShop::DecodeLimitGoods
        }
        sp.Encode1(0); // m_bEventOn
        // m_nHighestCharacterLevelInThisAccount
        return sp.Get();
    }

    // CCashShop::OnChargeParamResult, 充填ボタン
    public static MaplePacket ChargeParamResult() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopChargeParamResult);
        sp.EncodeStr("nexon_id"); // nexon id
        return sp.Get();
    }

    // おめでとうございます！ポイントショップのインベントリにのプレゼントをお送りしました。
    public static MaplePacket presentDialog() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_POINTSHOP_PRESENT_DIALOG);
        // 多分未実装
        return sp.Get();
    }

    // CCashShop::OnQueryCashResult
    public static MaplePacket QueryCashResult(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopQueryCashResult);
        sp.Encode4(chr.getCSPoints(1)); // NEXON POINT
        sp.Encode4(chr.getCSPoints(2)); // MAPLE POINT
        return sp.Get();
    }

    public static class CashItemStruct {

        public MapleInventoryType inc_slot_type;
        public IItem item;

        public CashItemStruct(MapleInventoryType inc_slot_type) {
            this.inc_slot_type = inc_slot_type;
        }

        public CashItemStruct(IItem item) {
            this.item = item;
        }

    }

    public static MaplePacket CashItemResult(CashItemOps ops, MapleClient c) {
        return CashItemResult(ops, c, null);
    }

    // CCashShop::OnCashItemResult
    public static MaplePacket CashItemResult(CashItemOps ops, MapleClient c, CashItemStruct cis) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopCashItemResult);

        sp.Encode1(ops.get());
        switch (ops) {
            // getCSInventory
            case CashItemRes_LoadLocker_Done: {
                CashShop csi = c.getPlayer().getCashInventory();

                sp.Encode2(csi.getItemsSize()); // cash item count
                for (IItem item : csi.getInventory()) {
                    sp.EncodeBuffer(GW_CashItemInfo.Encode(item, c));
                }
                sp.Encode2(c.getPlayer().getStorage().getSlots()); // m_nTrunkCount
                sp.Encode2(c.getCharacterSlots()); // m_nCharacterSlotCount
                sp.Encode2(0);// m_nBuyCharacterCount
                sp.Encode2(c.getCharaterCount());// m_nCharacterCount
                break;
            }
            // showBoughtCSItem
            case CashItemRes_Buy_Done: {
                sp.EncodeBuffer(GW_CashItemInfo.Encode(cis.item, c));
                break;
            }
            case CashItemRes_Buy_Failed: {
                break;
            }
            case CashItemRes_IncSlotCount_Done: {
                sp.Encode1(cis.inc_slot_type.getType());
                sp.Encode2(c.getPlayer().getInventory(cis.inc_slot_type).getSlotLimit());
                break;
            }
            case CashItemRes_IncSlotCount_Failed: {
                break;
            }
            case CashItemRes_IncTrunkCount_Done: {
                sp.Encode2(c.getPlayer().getStorage().getSlots());
                break;
            }
            case CashItemRes_IncTrunkCount_Failed: {
                break;
            }
            case CashItemRes_MoveLtoS_Done: {
                break;
            }
            case CashItemRes_MoveLtoS_Failed: {
                break;
            }
            case CashItemRes_MoveStoL_Done: {
                break;
            }
            case CashItemRes_MoveStoL_Failed: {
                break;
            }
            // onfirmFromCSInventory
            case CashItemRes_Destroy_Done: {
                sp.Encode8(cis.item.getUniqueId());
                break;
            }
            case CashItemRes_Destroy_Failed: {
                sp.Encode1(CashItemFailReasonOps.CashItemFailReason_InvalidPassportID.get()); // msg
                break;
            }
            case CashItemRes_BuyNormal_Done: {
                break;
            }
            case CashItemRes_BuyNormal_Failed: {
                break;
            }
            /*
            case CashItemRes_TransferWorld_Done: {
                break;
            }
            case CashItemRes_TransferWorld_Failed: {
                break;
            }
             */
            default: {
                break;
            }
        }

        return sp.Get();
    }

    // CCashShop::OnPurchaseExpChanged
    public static MaplePacket PurchaseExpChanged() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopPurchaseExpChanged);
        sp.Encode1(0); // m_nPurchaseExp
        return sp.Get();
    }

    // CCashShop::OnGiftMateInfoResult
    public static MaplePacket GiftMateInfoResult() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopGiftMateInfoResult);
        // not coded
        return sp.Get();
    }

    // 謎処理
    // -> @00FA [2E] [item_id?]
    public static MaplePacket ForceRequest(int item_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_POINTSHOP_FORCE_REQUEST);
        sp.Encode4(1); // item list size
        sp.Encode4(item_id); // buffer4
        sp.Encode1(1); // force request or not
        return sp.Get();
    }

    // 騎士団ショッピングのおまけアイテム"アイテム名"をプレゼントしました。インベントリをご確認ください。
    public static MaplePacket PresentForKOC(int item_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_POINTSHOP_KOC_PRESENT_DIALOG);
        sp.Encode4(item_id);
        return sp.Get();
    }

    // フリークーポンの期限の告知
    public static MaplePacket FreeCouponDialog() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_POINTSHOP_FREE_COUPON_DIALOG);
        sp.Encode1(1); // show dialog
        sp.Encode8(-1); // date
        return sp.Get();
    }

    // CCashShop::OnCheckTransferWorldPossibleResult
    // -> CP_CashShopCashItemRequest, @00FA [31] [FFFFFFFF] [WORLD_ID (4 bytes)]
    public static MaplePacket CheckTransferWorldPossibleResult() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopCheckTransferWorldPossibleResult);
        sp.Encode4(0); // not used
        sp.Encode1(0); // dialog message
        sp.Encode1(1); // having world list

        String world_list[] = {"かえで", "もみじ"};

        sp.Encode4(world_list.length); // world list size
        for (String world : world_list) {
            sp.EncodeStr(world);
        }
        return sp.Get();
    }

    public static MaplePacket confirmToCSInventory(IItem item, int accId, int sn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(109);
        addCashItemInfo(mplew, item, accId, sn, false);
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSQuestItem(int price, short quantity, byte position, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(144);
        mplew.writeInt(price);
        mplew.writeShort(quantity);
        mplew.writeShort(position);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static MaplePacket showCouponRedeemedItem(int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.writeShort(96);
        mplew.writeInt(0);
        mplew.writeInt(1);
        mplew.writeShort(1);
        mplew.writeShort(26);
        mplew.writeInt(itemid);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket showCouponRedeemedItem(Map<Integer, IItem> items, int mesos, int maplePoints, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(96); //use to be 4c
        mplew.write(items.size());
        for (Map.Entry<Integer, IItem> item : items.entrySet()) {
            addCashItemInfo(mplew, item.getValue(), c.getAccID(), item.getKey().intValue());
        }
        mplew.writeLong(maplePoints);
        mplew.writeInt(mesos);
        return mplew.getPacket();
    }

    public static MaplePacket sendWishList(MapleCharacter chr, boolean update) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(update ? 86 : 82); //+12
        int[] list = chr.getWishlist();
        for (int i = 0; i < 10; i++) {
            mplew.writeInt(list[i] != -1 ? list[i] : 0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showBoughtCSPackage(Map<Integer, IItem> ccc, int accid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(140); //use to be 7a
        mplew.write(ccc.size());
        for (Map.Entry<Integer, IItem> sn : ccc.entrySet()) {
            addCashItemInfo(mplew, sn.getValue(), accid, sn.getKey().intValue());
        }
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket increasedInvSlots(int inv, int slots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(99);
        mplew.write(inv);
        mplew.writeShort(slots);
        return mplew.getPacket();
    }

    public static MaplePacket cashItemExpired(int uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(113); //use to be 5d
        mplew.writeLong(uniqueid);
        return mplew.getPacket();
    }

    public static void addModCashItemInfo(MaplePacketLittleEndianWriter mplew, CashItemInfo.CashModInfo item) {
        int flags = item.flags;
        mplew.writeInt(item.sn);
        mplew.writeInt(flags);
        if ((flags & 1) != 0) {
            mplew.writeInt(item.itemid);
        }
        if ((flags & 2) != 0) {
            mplew.writeShort(item.count);
        }
        if ((flags & 4) != 0) {
            mplew.writeInt(item.discountPrice);
        }
        if ((flags & 8) != 0) {
            mplew.write(item.unk_1 - 1);
        }
        if ((flags & 16) != 0) {
            mplew.write(item.priority);
        }
        if ((flags & 32) != 0) {
            mplew.writeShort(item.period);
        }
        if ((flags & 64) != 0) {
            mplew.writeInt(0);
        }
        if ((flags & 128) != 0) {
            mplew.writeInt(item.meso);
        }
        if ((flags & 256) != 0) {
            mplew.write(item.unk_2 - 1);
        }
        if ((flags & 512) != 0) {
            mplew.write(item.gender);
        }
        if ((flags & 1024) != 0) {
            mplew.write(item.showUp ? 1 : 0);
        }
        if ((flags & 2048) != 0) {
            mplew.write(item.mark);
        }
        if ((flags & 4096) != 0) {
            mplew.write(item.unk_3 - 1);
        }
        if ((flags & 8192) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 16384) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 32768) != 0) {
            mplew.writeShort(0);
        }
        if ((flags & 65536) != 0) {
            List<CashItemInfo> pack = CashItemFactory.getInstance().getPackageItems(item.sn);
            if (pack == null) {
                mplew.write(0);
            } else {
                mplew.write(pack.size());
                for (int i = 0; i < pack.size(); i++) {
                    mplew.writeInt(pack.get(i).getSN());
                }
            }
        }
    }

    //work on this packet a little more
    public static MaplePacket getCSGifts(MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(80); //use to be 40
        List<Pair<IItem, String>> mci = c.getPlayer().getCashInventory().loadGifts();
        mplew.writeShort(mci.size());
        for (Pair<IItem, String> mcz : mci) {
            mplew.writeLong(mcz.getLeft().getUniqueId());
            mplew.writeInt(mcz.getLeft().getItemId());
            mplew.writeAsciiString(mcz.getLeft().getGiftFrom(), 13);
            mplew.writeAsciiString(mcz.getRight(), 73);
        }
        return mplew.getPacket();
    }

    public static MaplePacket sendGift(int price, int itemid, int quantity, String receiver) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(142); //use to be 7C
        mplew.writeMapleAsciiString(receiver);
        mplew.writeInt(itemid);
        mplew.writeShort(quantity);
        mplew.writeShort(0); //maplePoints
        mplew.writeInt(price);
        return mplew.getPacket();
    }

    public static final void addCashItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, int accId, int sn) {
        addCashItemInfo(mplew, item, accId, sn, true);
    }

    public static final void addCashItemInfo(MaplePacketLittleEndianWriter mplew, IItem item, int accId, int sn, boolean isFirst) {
        addCashItemInfo(mplew, item.getUniqueId(), accId, item.getItemId(), sn, item.getQuantity(), item.getGiftFrom(), item.getExpiration(), isFirst); //owner for the lulz
    }

    public static final void addCashItemInfo(MaplePacketLittleEndianWriter mplew, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire) {
        addCashItemInfo(mplew, uniqueid, accId, itemid, sn, quantity, sender, expire, true);
    }

    public static final void addCashItemInfo(MaplePacketLittleEndianWriter mplew, int uniqueid, int accId, int itemid, int sn, int quantity, String sender, long expire, boolean isFirst) {
        mplew.writeLong(uniqueid > 0 ? uniqueid : 0);
        mplew.writeLong(accId);
        mplew.writeInt(itemid);
        mplew.writeInt(isFirst ? sn : 0);
        mplew.writeShort(quantity);
        mplew.writeAsciiString(sender, 13); //owner for the lulzlzlzl
        TestHelper.addExpirationTime(mplew, expire);
        mplew.writeLong(isFirst ? 0 : sn);
        //if (isFirst && uniqueid > 0 && GameConstants.isEffectRing(itemid)) {
        //	MapleRing ring = MapleRing.loadFromDb(uniqueid);
        //	if (ring != null) { //or is this only for friendship rings, i wonder. and does isFirst even matter
        //		mplew.writeMapleAsciiString(ring.getPartnerName());
        //		mplew.writeInt(itemid);
        //		mplew.writeShort(quantity);
        //	}
        //}
    }

    public static MaplePacket enableCSUse() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(10);
        mplew.write(1);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket sendCSFail(int err) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(104);
        mplew.write(err);
        return mplew.getPacket();
    }

}
