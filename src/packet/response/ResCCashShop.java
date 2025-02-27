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
package packet.response;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import handling.MaplePacket;
import java.util.ArrayList;
import packet.ops.OpsCashItemFailReason;
import packet.ops.OpsCashItem;
import packet.ServerPacket;
import packet.response.struct.GW_CashItemInfo;
import packet.response.struct.GW_ItemSlotBase;
import server.CashShop;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCCashShop {

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
    public static byte[] getModifiedData() {
        ServerPacket data = new ServerPacket();
        //data.Encode2(0); // count
        data.Encode2(1); // count
        data.Encode4(50200133); // SN
        // CS_COMMODITY::DecodeModifiedData
        {
            int flag = 0x01 | 0x02 | 0x04 | 0x0400;
            if ((ServerConfig.JMS147orLater() && !ServerConfig.IsGMS()) || ServerConfig.IsVMS() || ServerConfig.IsBMS()) {
                data.Encode4(flag);
            } else {
                data.Encode2(flag);
            }
            data.Encode4(5062000); // 0x01 : itemid
            data.Encode2(77); // 0x02 : count
            data.Encode4(7777); // 0x04: price
            data.Encode1(1); //0x0400 : OnSale
        }
        return data.get().getBytes();
    }

    public static byte[] getDiscountRates() {
        ServerPacket data = new ServerPacket();
        data.Encode1(0); // count
        /*
        data.Encode1(6 * 10); // count max 9*30, ただし1 byteなので全ては利用不可
        for (int category = 2; category < 8; category++) {
            for (int sub_category = 0; sub_category < 10; sub_category++) {
                data.Encode1(category); // category
                data.Encode1(sub_category); // sub category
                data.Encode1(99); // discount rate
            }
        }
         */
        return data.get().getBytes();
    }

    public static enum BestItemCategory {
        BestItemCategory_Main(1),
        BestItemCategory_Event(2),
        BestItemCategory_Equip(3),
        BestItemCategory_Consume(4), // use
        BestItemCategory_Install(5), // special
        BestItemCategory_Etc(6),
        BestItemCategory_Pet(7),
        BestItemCategory_Package(8),
        UNKNOWN(-1);

        private int value;

        BestItemCategory(int flag) {
            value = flag;
        }

        BestItemCategory() {
            value = -1;
        }

        public int get() {
            return value;
        }
    }
    private static boolean best_item_initilized = false;
    private static int best_item_category[] = new int[9 * 5 * 2];
    private static int best_item_gender[] = new int[9 * 5 * 2];
    private static int best_item_item_SN[] = new int[9 * 5 * 2];

    // 1080 bytes buffer
    public static byte[] getBestItems() {
        ServerPacket data = new ServerPacket();

        if (!best_item_initilized) {
            best_item_initilized = true;
            // equip
            best_item_category[BestItemCategory.BestItemCategory_Equip.get() * 5 * 2] = BestItemCategory.BestItemCategory_Equip.get();
            best_item_gender[BestItemCategory.BestItemCategory_Equip.get() * 5 * 2] = 0;
            best_item_item_SN[BestItemCategory.BestItemCategory_Equip.get() * 5 * 2] = 20900059;
            best_item_category[BestItemCategory.BestItemCategory_Equip.get() * 5 * 2 + 1] = BestItemCategory.BestItemCategory_Equip.get();
            best_item_gender[BestItemCategory.BestItemCategory_Equip.get() * 5 * 2 + 1] = 0;
            best_item_item_SN[BestItemCategory.BestItemCategory_Equip.get() * 5 * 2 + 1] = 20900031;
            // consume
            best_item_category[BestItemCategory.BestItemCategory_Consume.get() * 5 * 2] = BestItemCategory.BestItemCategory_Consume.get();
            best_item_gender[BestItemCategory.BestItemCategory_Consume.get() * 5 * 2] = 0;
            best_item_item_SN[BestItemCategory.BestItemCategory_Consume.get() * 5 * 2] = 10003562;
            // install or special
            best_item_category[BestItemCategory.BestItemCategory_Install.get() * 5 * 2] = BestItemCategory.BestItemCategory_Install.get();
            best_item_gender[BestItemCategory.BestItemCategory_Install.get() * 5 * 2] = 0;
            best_item_item_SN[BestItemCategory.BestItemCategory_Install.get() * 5 * 2] = 80000184;
            // etc
            best_item_category[BestItemCategory.BestItemCategory_Etc.get() * 5 * 2] = BestItemCategory.BestItemCategory_Etc.get();
            best_item_gender[BestItemCategory.BestItemCategory_Etc.get() * 5 * 2] = 0;
            best_item_item_SN[BestItemCategory.BestItemCategory_Etc.get() * 5 * 2] = 50200009;
            // pet
            best_item_category[BestItemCategory.BestItemCategory_Pet.get() * 5 * 2] = BestItemCategory.BestItemCategory_Pet.get();
            best_item_gender[BestItemCategory.BestItemCategory_Pet.get() * 5 * 2] = 0;
            best_item_item_SN[BestItemCategory.BestItemCategory_Pet.get() * 5 * 2] = 60000038;
        }

        for (int i = 0; i < best_item_category.length; i++) {
            data.Encode4(best_item_category[i]);
            data.Encode4(best_item_gender[i]);
            data.Encode4(best_item_item_SN[i]);
        }

        return data.get().getBytes();
    }

    // CCashShop::OnChargeParamResult, 充填ボタン
    public static MaplePacket ChargeParamResult() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopChargeParamResult);
        sp.EncodeStr("nexon_id"); // nexon id
        return sp.get();
    }

    // おめでとうございます！ポイントショップのインベントリにのプレゼントをお送りしました。
    public static MaplePacket presentDialog() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_POINTSHOP_PRESENT_DIALOG);
        // 多分未実装
        return sp.get();
    }

    // CCashShop::OnQueryCashResult
    public static MaplePacket QueryCashResult(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopQueryCashResult);
        sp.Encode4(chr.getCSPoints(1)); // NEXON POINT
        sp.Encode4(chr.getCSPoints(2)); // MAPLE POINT
        return sp.get();
    }

    public static class CashItemStruct {

        public MapleInventoryType inc_slot_type;
        public IItem item;
        // coupon
        public ArrayList<IItem> coupon_items_cash;
        public int coupon_maple_point;
        public ArrayList<IItem> coupon_items_normal;
        public int coupon_meso;

        public CashItemStruct(MapleInventoryType inc_slot_type) {
            this.inc_slot_type = inc_slot_type;
        }

        public CashItemStruct(IItem item) {
            this.item = item;
        }

        public CashItemStruct(ArrayList<IItem> coupon_items_cash, int coupon_maple_point, ArrayList<IItem> coupon_items_normal, int coupon_meso) {
            this.coupon_items_cash = coupon_items_cash;
            this.coupon_maple_point = coupon_maple_point;
            this.coupon_items_normal = coupon_items_normal;
            this.coupon_meso = coupon_meso;
        }

    }

    public static MaplePacket CashItemResult(OpsCashItem ops, MapleClient c) {
        return CashItemResult(ops, c, null);
    }

    // CCashShop::OnCashItemResult
    public static MaplePacket CashItemResult(OpsCashItem ops, MapleClient c, CashItemStruct cis) {
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
                sp.Encode1(OpsCashItemFailReason.CashItemFailReason_NoStock.get());
                break;
            }
            // showBoughtCSItem
            // CCashShop::OnCashItemResBuyDone
            case CashItemRes_Buy_Done:
            case CashItemRes_FreeCashItem_Done: {
                sp.EncodeBuffer(GW_CashItemInfo.Encode(cis.item, c));
                break;
            }
            // CCashShop::OnCashItemResBuyFailed
            case CashItemRes_Buy_Failed: {
                sp.Encode1(OpsCashItemFailReason.CashItemFailReason_NoRemainCash.get());
                break;
            }
            case CashItemRes_UseCoupon_Done: {
                int cash_item_count = cis.coupon_items_cash == null ? 0 : cis.coupon_items_cash.size();
                int normal_item_count = cis.coupon_items_normal == null ? 0 : cis.coupon_items_normal.size();
                sp.Encode1(cash_item_count);
                if (0 < cash_item_count) {
                    // buffer 55 bytes
                    for (IItem item : cis.coupon_items_cash) {
                        sp.EncodeBuffer(GW_CashItemInfo.Encode(item, c));
                    }
                }
                sp.Encode4(cis.coupon_maple_point);
                sp.Encode4(normal_item_count);
                if (0 < normal_item_count) {
                    // buffer 8 bytes
                    for (IItem item : cis.coupon_items_normal) {
                        sp.Encode2(item.getQuantity());
                        sp.Encode2(0);
                        sp.Encode4(item.getItemId());
                    }
                }
                sp.Encode4(cis.coupon_meso);
                break;
            }
            case CashItemRes_GiftCoupon_Done: {
                break;
            }
            case CashItemRes_UseCoupon_Failed: {
                sp.Encode1(OpsCashItemFailReason.CashItemFailReason_InvalidCoupon.get());
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
                sp.Encode1(OpsCashItemFailReason.CashItemFailReason_NoEmptyPos.get());
                break;
            }
            // CCashShop::OnCashItemResMoveStoLDone
            case CashItemRes_MoveStoL_Done: {
                sp.EncodeBuffer(GW_CashItemInfo.Encode(cis.item, c));
                break;
            }
            // CCashShop::OnCashItemResMoveStoLFailed
            case CashItemRes_MoveStoL_Failed: {
                sp.Encode1(OpsCashItemFailReason.CashItemFailReason_NoEmptyPos.get());
                break;
            }
            // CCashShop::OnCashItemResDestroyDone
            case CashItemRes_Destroy_Done: {
                sp.Encode8(cis.item.getUniqueId());
                break;
            }
            // CCashShop::OnCashItemResDestroyFailed
            case CashItemRes_Destroy_Failed: {
                sp.Encode1(OpsCashItemFailReason.CashItemFailReason_InvalidPassportID.get()); // msg
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

        return sp.get();
    }

    // CCashShop::OnPurchaseExpChanged
    public static MaplePacket PurchaseExpChanged() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopPurchaseExpChanged);
        sp.Encode1(0); // m_nPurchaseExp
        return sp.get();
    }

    // CCashShop::OnGiftMateInfoResult
    public static MaplePacket GiftMateInfoResult() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopGiftMateInfoResult);
        // not coded
        return sp.get();
    }

    // 謎処理
    // -> @00FA [2E] [item_id?]
    public static MaplePacket ForceRequest(int item_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_POINTSHOP_FORCE_REQUEST);
        sp.Encode4(1); // item list size
        sp.Encode4(item_id); // buffer4
        sp.Encode1(1); // force request or not
        return sp.get();
    }

    // 騎士団ショッピングのおまけアイテム"アイテム名"をプレゼントしました。インベントリをご確認ください。
    public static MaplePacket PresentForKOC(int item_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_POINTSHOP_KOC_PRESENT_DIALOG);
        sp.Encode4(item_id);
        return sp.get();
    }

    // フリークーポンの期限の告知
    public static MaplePacket FreeCouponDialog(boolean has_free_coupon, long free_coupon_date) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_POINTSHOP_FREE_COUPON_DIALOG);
        sp.Encode1(has_free_coupon ? 1 : 0); // enable free coupon
        if (has_free_coupon) {
            sp.Encode8(free_coupon_date);
        }
        return sp.get();
    }

    // ガシャポンスタンプとお年玉の累積ポイント告知
    public static MaplePacket GachaponStampAndOtoshidamaDialog() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_GACHAPON_STAMP_AND_OTOSHIDAMA_DIALOG);
        sp.Encode4(5); // Xポイントで購入
        sp.Encode4(4); // X個のスタンプGET
        sp.Encode4(3); // 次はXポイントでスタンプをGETできます
        sp.Encode4(2); // 累積Xポイント
        sp.Encode4(1); // 入手した個数は計X個
        sp.Encode4(4000426); // item id 4031351 or 4000426
        return sp.get();
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
        return sp.get();
    }

    // アバターランダムボックス
    public static MaplePacket OnCashItemGachaponResult(IItem box_item, IItem item, MapleClient c) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CashShopCashItemGachaponResult);

        sp.Encode1(OpsCashItem.CashItemRes_CashItemGachapon_Done.get());
        sp.Encode8(box_item.getUniqueId());
        sp.Encode4(0); // nNumber
        sp.EncodeBuffer(GW_CashItemInfo.Encode(item, c));
        // CUICashItemGachapon::OnCashItemGachaponResult
        {
            sp.Encode4(item.getItemId()); // m_nSelectedItemID
            sp.Encode1(1); // m_nSelectedItemCount
            sp.Encode1(1); // m_bJackpot, 0 (CashGachaponNormal) or 1 (CashGachaponJackpot)
        }
        return sp.get();
    }

    public static MaplePacket cashItemExpired(int uniqueid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_CashShopCashItemResult.get());
        mplew.write(113); //use to be 5d
        mplew.writeLong(uniqueid);
        return mplew.getPacket();
    }
}
