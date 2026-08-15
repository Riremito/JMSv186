/*
 * Copyright (C) 2025 Riremito
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
package tacos.packet.response;

import odin.client.MapleCharacter;
import odin.client.inventory.IItem;
import odin.server.MTSStorage;
import tacos.config.Config;
import static tacos.config.Region.JMS;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsITC;
import tacos.packet.response.builder.PB_ITC;
import tacos.packet.response.data.RD_GW_ItemSlotBase;
import tacos.shared.SharedDate;

/**
 *
 * @author Riremito
 */
public class ResCITC {

    // CITC::OnChargeParamResult
    public static ServerPacket ITCChargeParamResult() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCChargeParamResult);

        return sp;
    }

    // CITC::OnQueryCashResult
    public static ServerPacket ITCQueryCashResult(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCQueryCashResult);

        sp.Encode4(chr.getNexonPoint()); // nNexonCash (signed)
        sp.Encode4(chr.getMaplePoint()); // nMaplePoint (signed)
        return sp;
    }

    // CITC::OnNormalItemResult
    public static ServerPacket ITCNormalItemResult(OpsITC ops) {
        return ITCNormalItemResult(ops, null);
    }

    // CITC::OnNormalItemResult
    public static ServerPacket ITCNormalItemResult(OpsITC ops, PB_ITC pb) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCNormalItemResult);

        sp.Encode1(ops.get());
        switch (ops) {
            case ITCRes_GetITCList_Done: {
                sp.Encode4(0); // m_nCurrentCategoryItemCnt
                sp.Encode4(pb.mts_items.size()); // m_nCurrentPageItemCnt
                sp.Encode4(pb.mts_cart.getTab()); // nCategory
                sp.Encode4(pb.mts_cart.getType()); // nSubCategory
                sp.Encode4(pb.mts_cart.getPage()); // nPagea
                sp.Encode1(1); // nSortType
                sp.Encode1(1); // nSortColumn
                for (MTSStorage.MTSItemInfo mts_item : pb.mts_items) {
                    sp.EncodeBuffer(ITCITEM_Encode(mts_item));
                }
                sp.Encode1(pb.unlock ? 1 : 0); // unlock
                break;
            }
            case ITCRes_GetITCList_Failed: {
                sp.Encode1(pb.fail_reason.get());
                break;
            }
            case ITCRes_GetSearchITCList_Done: {
                break;
            }
            case ITCRes_GetSearchITCList_Failed: {
                break;
            }
            case ITCRes_GetMaplePoint_Done: {
                break;
            }
            case ITCRes_GetMaplePoint_Failed: {
                break;
            }
            case ITCRes_CharacterModifiedNFlush_Done: {
                break;
            }
            case ITCRes_CharacterModifiedNFlush_Failed: {
                break;
            }
            case ITCRes_RegisterSaleEntry_Done: {
                break;
            }
            case ITCRes_RegisterSaleEntry_Failed: {
                break;
            }
            case ITCRes_SaleCurrentItemToWish_Done: {
                break;
            }
            case ITCRes_SaleCurrentItemToWish_Failed: {
                break;
            }
            case ITCRes_GetUserPurchaseItem_Done: {
                sp.Encode4(pb.items.size()); // nTotalCount
                for (IItem item : pb.items) {
                    sp.EncodeBuffer(ITCITEM_Encode(item));
                }
                sp.Encode4(0); // hidden item count.
                sp.Encode1(pb.unlock ? 1 : 0); // unlock, m_bITCRequestSent reset.
                break;
            }
            case ITCRes_GetUserPurchaseItem_Failed: {
                break;
            }
            case ITCRes_GetUserSaleItem_Done: {
                sp.Encode4(pb.mts_items.size()); // nTotalCount
                for (MTSStorage.MTSItemInfo mts_item : pb.mts_items) {
                    sp.EncodeBuffer(ITCITEM_Encode(mts_item));
                }
                break;
            }
            case ITCRes_GetUserSaleItem_Failed: {
                break;
            }
            case ITCRes_CancelSaleItem_Done: {
                break;
            }
            case ITCRes_CancelSaleItem_Failed: {
                break;
            }
            case ITCRes_MoveITCPurchaseItemLtoS_Done: {
                sp.Encode4(pb.item.getItemId() / 1000000); // nTab
                sp.Encode4(pb.item.getPosition()); // nPos
                break;
            }
            case ITCRes_MoveITCPurchaseItemLtoS_Failed: {
                break;
            }
            case ITCRes_SetZzim_Done: {
                break;
            }
            case ITCRes_SetZzim_Failed: {
                break;
            }
            case ITCRes_DeleteZzim_Done: {
                break;
            }
            case ITCRes_DeleteZzim_Failed: {
                break;
            }
            case ITCRes_LoadWishSaleList_Done: {
                break;
            }
            case ITCRes_LoadWishSaleList_Failed: {
                break;
            }
            case ITCRes_BuyWish_Done: {
                break;
            }
            case ITCRes_BuyWish_Failed: {
                break;
            }
            case ITCRes_CancelWish_Done: {
                break;
            }
            case ITCRes_CancelWish_Failed: {
                break;
            }
            case ITCRes_BuyItem_Done: {
                break;
            }
            case ITCRes_BuyItem_Failed: {
                break;
            }
            case ITCRes_BuyZzimItem_Done: {
                break;
            }
            case ITCRes_BuyZzimItem_Failed: {
                break;
            }
            case ITCRes_RegisterBuyOrder_Done: {
                break;
            }
            case ITCRes_RegisterBuyOrder_Failed: {
                break;
            }
            case ITCRes_RegAuction_Done: {
                break;
            }
            case ITCRes_RegAuction_Failed: {
                break;
            }
            case ITCRes_BidAuction_Done: {
                break;
            }
            case ITCRes_BidAuction_Failed: {
                break;
            }
            case ITCRes_GetNotifyCancelWishResult: {
                sp.Encode4(0);
                sp.Encode4(0);
                break;
            }
            case ITCRes_GetSuccessBidInfoResult: {
                break;
            }
            default: {
                break;
            }
        }

        return sp;
    }

    // ITCITEM::Decode
    public static byte[] ITCITEM_Encode(IItem item) {
        ServerPacket data = new ServerPacket();

        data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(item));
        data.Encode4(0); // nITCSN
        data.Encode4(0); // nPrice
        data.Encode4(0); // nContractFee

        switch (Config.REGION) {
            case JMS: {
                // JMS187
                data.Encode4(0);
                data.Encode4(0);
                break;
            }
            default: {
                // GMS95
                data.EncodeStr(""); // sContractFeeTxId
                data.EncodeStr(""); // sRollbackUsageID
                break;
            }
        }

        data.Encode8(0); // ftITCDateExpired
        data.EncodeStr(""); // sUserID
        data.EncodeStr(""); // sGameID
        data.EncodeStr(""); // sComment
        data.Encode4(0); // nBidCount
        data.Encode4(0); // nBidRange
        data.Encode4(0); // nBidPrice
        data.Encode4(0); // nMinPrice
        data.Encode4(0); // nMaxPrice
        data.Encode4(0); // nUnitPrice
        data.Encode2(0); // nProcessStatus
        return data.getBytes();
    }

    public static byte[] ITCITEM_Encode(MTSStorage.MTSItemInfo mts_item) {
        ServerPacket data = new ServerPacket();

        data.EncodeBuffer(RD_GW_ItemSlotBase.Encode(mts_item.getItem()));
        data.Encode4(mts_item.getId()); // nITCSN
        data.Encode4(mts_item.getPrice()); // nPrice
        data.Encode4(mts_item.getTaxes()); // nContractFee

        switch (Config.REGION) {
            case JMS: {
                // JMS187
                data.Encode4(0);
                data.Encode4(0);
                break;
            }
            default: {
                // GMS95
                data.EncodeStr(""); // sContractFeeTxId
                data.EncodeStr(""); // sRollbackUsageID
                break;
            }
        }

        data.Encode8(SharedDate.getTimestamp(mts_item.getEndingDate())); // ftITCDateExpired
        data.EncodeStr(""); // sUserID
        data.EncodeStr(mts_item.getSeller()); // sGameID
        data.EncodeStr(""); // sComment
        data.Encode4(0); // nBidCount
        data.Encode4(0); // nBidRange
        data.Encode4(0); // nBidPrice
        data.Encode4(0); // nMinPrice
        data.Encode4(0); // nMaxPrice
        data.Encode4(0); // nUnitPrice
        data.Encode2(0); // nProcessStatus
        return data.getBytes();
    }
}
