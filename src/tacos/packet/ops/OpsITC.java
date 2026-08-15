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
package tacos.packet.ops;

/**
 *
 * @author Riremito
 */
public enum OpsITC implements IPacketOps {
    ITCReq_GetMaplePoint(0),
    ITCReq_CharacterModifiedNFlush(1),
    ITCReq_RegisterSaleEntry(2),
    ITCReq_SaleCurrentItemToWish(3),
    ITCReq_RegisterBuyOrder(4),
    ITCReq_GetITCList(5),
    ITCReq_GetSearchITCList(6),
    ITCReq_CancelSaleItem(7),
    ITCReq_MoveITCPurchaseItemLtoS(8),
    ITCReq_SetZzim(9),
    ITCReq_DeleteZzim(10),
    ITCReq_LoadWishSaleList(11),
    ITCReq_BuyWish(12),
    ITCReq_CancelWish(13),
    ITCReq_BuyWishChargeCash(14),
    ITCReq_BuyWishCancel(15),
    ITCReq_BuyItem(16),
    ITCReq_BuyZzimItem(17),
    ITCReq_RegAuction(18),
    ITCReq_BidAuction(19),
    ITCReq_BuyAuctionImm(20),
    ITCRes_GetITCList_Done(21),
    ITCRes_GetITCList_Failed(22),
    ITCRes_GetSearchITCList_Done(23),
    ITCRes_GetSearchITCList_Failed(24),
    ITCRes_GetMaplePoint_Done(25),
    ITCRes_GetMaplePoint_Failed(26),
    ITCRes_CharacterModifiedNFlush_Done(27),
    ITCRes_CharacterModifiedNFlush_Failed(28),
    ITCRes_RegisterSaleEntry_Done(29),
    ITCRes_RegisterSaleEntry_Failed(30),
    ITCRes_SaleCurrentItemToWish_Done(31),
    ITCRes_SaleCurrentItemToWish_Failed(32),
    ITCRes_GetUserPurchaseItem_Done(33),
    ITCRes_GetUserPurchaseItem_Failed(34),
    ITCRes_GetUserSaleItem_Done(35),
    ITCRes_GetUserSaleItem_Failed(36),
    ITCRes_CancelSaleItem_Done(37),
    ITCRes_CancelSaleItem_Failed(38),
    ITCRes_MoveITCPurchaseItemLtoS_Done(39),
    ITCRes_MoveITCPurchaseItemLtoS_Failed(40),
    ITCRes_SetZzim_Done(41),
    ITCRes_SetZzim_Failed(42),
    ITCRes_DeleteZzim_Done(43),
    ITCRes_DeleteZzim_Failed(44),
    ITCRes_LoadWishSaleList_Done(45),
    ITCRes_LoadWishSaleList_Failed(46),
    ITCRes_BuyWish_Done(47),
    ITCRes_BuyWish_Failed(48),
    ITCRes_CancelWish_Done(49),
    ITCRes_CancelWish_Failed(50),
    ITCRes_BuyItem_Done(51),
    ITCRes_BuyItem_Failed(52),
    ITCRes_BuyZzimItem_Done(53),
    ITCRes_BuyZzimItem_Failed(54),
    ITCRes_RegisterBuyOrder_Done(55),
    ITCRes_RegisterBuyOrder_Failed(56),
    ITCRes_RegAuction_Done(57),
    ITCRes_RegAuction_Failed(58),
    ITCRes_BidAuction_Done(59),
    ITCRes_BidAuction_Failed(60),
    ITCRes_GetNotifyCancelWishResult(61),
    ITCRes_GetSuccessBidInfoResult(62),
    ITCFailReason_Unknown(63),
    ITCFailReason_Timeout(64),
    ITCFailReason_DBFailed(65),
    ITCFailReason_NoRemainCash(66),
    ITCFailReason_NoRemainMeso(67),
    ITCFailReason_DeductCashFailed(68),
    ITCFailReason_CannotFindUser(69),
    ITCFailReason_SaveFailed(70),
    ITCFailReason_NoEmptyPos(71),
    ITCFailReason_SlotMaxMismatch(72),
    ITCFailReason_FirstLoadingFailed(73),
    ITCFailReason_ItemFailed(74),
    ITCFailReason_CashChargeFailed(75),
    ITCFailReason_CashFailed(76),
    ITCFailReason_DuplicateBid(77),
    ITCFailReason_AlreadyBid(78),
    ITCFailReason_CannotRegisterItem(79),
    ITCFailReason_AlreadyCancelled_or_SoldOut(80),
    ITCFailReason_AlreadySoldOut(81),
    ITCFailReason_DurabilityItem(82),
    ITCFailReason_ThrowingStar(83),
    ITCFailReason_LowCharacterLevel(84),
    UNKNOWN;

    private int value;

    OpsITC(int val) {
        this.value = val;
    }

    OpsITC() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsITC find(int val) {
        for (OpsITC ops : values()) {
            if (ops.get() == val) {
                if (val != UNKNOWN.get()) {
                    return ops;
                }
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsITC ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }
}
