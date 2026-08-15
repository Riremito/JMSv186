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
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsITC;
import tacos.packet.response.builder.PB_ITC;

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

    public static ServerPacket ITCNormalItemResult(OpsITC ops, PB_ITC pb) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ITCNormalItemResult);

        sp.Encode1(ops.get());
        switch (ops) {
            case ITCRes_GetITCList_Done: {
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
                break;
            }
            case ITCRes_GetUserPurchaseItem_Failed: {
                break;
            }
            case ITCRes_GetUserSaleItem_Done: {
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
}
