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
package packet.response;

import client.MapleCharacter;
import server.network.MaplePacket;
import packet.ServerPacket;
import static packet.ops.OpsITC.ITCRes_GetITCList_Failed;
import packet.ops.arg.ArgITCNormalItemResult;

/**
 *
 * @author Riremito
 */
public class ResCITC {

    // CITC::OnChargeParamResult
    public static final MaplePacket ITCChargeParamResult() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ITCChargeParamResult);

        return sp.get();
    }

    // CITC::OnQueryCashResult
    public static MaplePacket ITCQueryCashResult(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ITCQueryCashResult);

        sp.Encode4(chr.getNexonPoint()); // nNexonCash (signed)
        sp.Encode4(chr.getMaplePoint()); // nMaplePoint (signed)
        return sp.get();
    }

    // CITC::OnNormalItemResult
    public static final MaplePacket ITCNormalItemResult(ArgITCNormalItemResult arg) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ITCNormalItemResult);

        sp.Encode1(arg.ops_res.get());
        switch (arg.ops_res) {
            case ITCRes_GetITCList_Done: {
                break;
            }
            case ITCRes_GetITCList_Failed: {
                sp.Encode1(arg.ops_fail_reason.get());
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
        return sp.get();
    }

}
