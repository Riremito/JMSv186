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
import packet.ops.CashItemFailReasonOps;
import packet.ops.CashItemOps;
import packet.server.ServerPacket;
import packet.server.response.struct.CharacterData;
import packet.server.response.struct.GW_CashItemInfo;
import packet.server.response.struct.GW_ItemSlotBase;
import server.CashShop;
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
            // test
            // CCashShop::OnCashItemResSetWishFailed
            case CashItemRes_SetWish_Failed: {
                sp.Encode1(CashItemFailReasonOps.CashItemFailReason_NoStock.get());
                break;
            }
            // showBoughtCSItem
            // CCashShop::OnCashItemResBuyDone
            case CashItemRes_Buy_Done: {
                sp.EncodeBuffer(GW_CashItemInfo.Encode(cis.item, c));
                break;
            }
            // CCashShop::OnCashItemResBuyFailed
            case CashItemRes_Buy_Failed: {
                sp.Encode1(CashItemFailReasonOps.CashItemFailReason_NoRemainCash.get());
                break;
            }
            // CCashShop::OnCashItemResIncSlotCountDone
            case CashItemRes_IncSlotCount_Done: {
                sp.Encode1(cis.inc_slot_type.getType());
                sp.Encode2(c.getPlayer().getInventory(cis.inc_slot_type).getSlotLimit());
                break;
            }
            // CCashShop::OnCashItemResIncSlotCountFailed
            case CashItemRes_IncSlotCount_Failed: {
                break;
            }
            // CCashShop::OnCashItemResIncTrunkCountDone
            case CashItemRes_IncTrunkCount_Done: {
                sp.Encode2(c.getPlayer().getStorage().getSlots());
                break;
            }
            // CCashShop::OnCashItemResIncTrunkCountFailed
            case CashItemRes_IncTrunkCount_Failed: {
                break;
            }
            // CCashShop::OnCashItemResMoveLtoSDone
            case CashItemRes_MoveLtoS_Done: {
                sp.Encode2(cis.item.getPosition()); // 2 bytes 固定
                sp.EncodeBuffer(GW_ItemSlotBase.Encode(cis.item));
                break;
            }
            // CCashShop::OnCashItemResMoveLtoSFailed
            case CashItemRes_MoveLtoS_Failed: {
                sp.Encode1(CashItemFailReasonOps.CashItemFailReason_NoEmptyPos.get());
                break;
            }
            // CCashShop::OnCashItemResMoveStoLDone
            case CashItemRes_MoveStoL_Done: {
                sp.EncodeBuffer(GW_CashItemInfo.Encode(cis.item, c));
                break;
            }
            // CCashShop::OnCashItemResMoveStoLFailed
            case CashItemRes_MoveStoL_Failed: {
                sp.Encode1(CashItemFailReasonOps.CashItemFailReason_NoEmptyPos.get());
                break;
            }
            // CCashShop::OnCashItemResDestroyDone
            case CashItemRes_Destroy_Done: {
                sp.Encode8(cis.item.getUniqueId());
                break;
            }
            // CCashShop::OnCashItemResDestroyFailed
            case CashItemRes_Destroy_Failed: {
                sp.Encode1(CashItemFailReasonOps.CashItemFailReason_InvalidPassportID.get()); // msg
                break;
            }
            // CCashShop::OnCashItemResBuyNormalDone
            case CashItemRes_BuyNormal_Done: {
                break;
            }
            // CCashShop::OnCashItemResBuyNormalFailed
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

    // アバターランダムボックス
    public static MaplePacket OnCashItemGachaponResult(IItem box_item, IItem item, MapleClient c) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopCashItemGachaponResult);

        sp.Encode1(CashItemOps.CashItemRes_CashItemGachapon_Done.get());
        sp.Encode8(box_item.getUniqueId());
        sp.Encode4(0); // nNumber
        sp.EncodeBuffer(GW_CashItemInfo.Encode(item, c));
        // CUICashItemGachapon::OnCashItemGachaponResult
        {
            sp.Encode4(item.getItemId()); // m_nSelectedItemID
            sp.Encode1(1); // m_nSelectedItemCount
            sp.Encode1(1); // m_bJackpot, 0 (CashGachaponNormal) or 1 (CashGachaponJackpot)
        }
        return sp.Get();
    }

    public static MaplePacket cashItemExpired(int uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.Get());
        mplew.write(113); //use to be 5d
        mplew.writeLong(uniqueid);
        return mplew.getPacket();
    }
}
