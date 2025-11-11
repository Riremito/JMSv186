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
    ITCReq_GetMaplePoint,
    ITCReq_CharacterModifiedNFlush,
    ITCReq_RegisterSaleEntry,
    ITCReq_SaleCurrentItemToWish,
    ITCReq_RegisterBuyOrder,
    ITCReq_GetITCList,
    ITCReq_GetSearchITCList,
    ITCReq_CancelSaleItem,
    ITCReq_MoveITCPurchaseItemLtoS,
    ITCReq_SetZzim,
    ITCReq_DeleteZzim,
    ITCReq_LoadWishSaleList,
    ITCReq_BuyWish,
    ITCReq_CancelWish,
    ITCReq_BuyWishChargeCash,
    ITCReq_BuyWishCancel,
    ITCReq_BuyItem,
    ITCReq_BuyZzimItem,
    ITCReq_RegAuction,
    ITCReq_BidAuction,
    ITCReq_BuyAuctionImm,
    ITCRes_GetITCList_Done,
    ITCRes_GetITCList_Failed,
    ITCRes_GetSearchITCList_Done,
    ITCRes_GetSearchITCList_Failed,
    ITCRes_GetMaplePoint_Done,
    ITCRes_GetMaplePoint_Failed,
    ITCRes_CharacterModifiedNFlush_Done,
    ITCRes_CharacterModifiedNFlush_Failed,
    ITCRes_RegisterSaleEntry_Done,
    ITCRes_RegisterSaleEntry_Failed,
    ITCRes_SaleCurrentItemToWish_Done,
    ITCRes_SaleCurrentItemToWish_Failed,
    ITCRes_GetUserPurchaseItem_Done,
    ITCRes_GetUserPurchaseItem_Failed,
    ITCRes_GetUserSaleItem_Done,
    ITCRes_GetUserSaleItem_Failed,
    ITCRes_CancelSaleItem_Done,
    ITCRes_CancelSaleItem_Failed,
    ITCRes_MoveITCPurchaseItemLtoS_Done,
    ITCRes_MoveITCPurchaseItemLtoS_Failed,
    ITCRes_SetZzim_Done,
    ITCRes_SetZzim_Failed,
    ITCRes_DeleteZzim_Done,
    ITCRes_DeleteZzim_Failed,
    ITCRes_LoadWishSaleList_Done,
    ITCRes_LoadWishSaleList_Failed,
    ITCRes_BuyWish_Done,
    ITCRes_BuyWish_Failed,
    ITCRes_CancelWish_Done,
    ITCRes_CancelWish_Failed,
    ITCRes_BuyItem_Done,
    ITCRes_BuyItem_Failed,
    ITCRes_BuyZzimItem_Done,
    ITCRes_BuyZzimItem_Failed,
    ITCRes_RegisterBuyOrder_Done,
    ITCRes_RegisterBuyOrder_Failed,
    ITCRes_RegAuction_Done,
    ITCRes_RegAuction_Failed,
    ITCRes_BidAuction_Done,
    ITCRes_BidAuction_Failed,
    ITCRes_GetNotifyCancelWishResult,
    ITCRes_GetSuccessBidInfoResult,
    ITCFailReason_Unknown,
    ITCFailReason_Timeout,
    ITCFailReason_DBFailed,
    ITCFailReason_NoRemainCash,
    ITCFailReason_NoRemainMeso,
    ITCFailReason_DeductCashFailed,
    ITCFailReason_CannotFindUser,
    ITCFailReason_SaveFailed,
    ITCFailReason_NoEmptyPos,
    ITCFailReason_SlotMaxMismatch,
    ITCFailReason_FirstLoadingFailed,
    ITCFailReason_ItemFailed,
    ITCFailReason_CashChargeFailed,
    ITCFailReason_CashFailed,
    ITCFailReason_DuplicateBid,
    ITCFailReason_AlreadyBid,
    ITCFailReason_CannotRegisterItem,
    ITCFailReason_AlreadyCancelled_or_SoldOut,
    ITCFailReason_AlreadySoldOut,
    ITCFailReason_DurabilityItem,
    ITCFailReason_ThrowingStar,
    ITCFailReason_LowCharacterLevel,
    UNKNOWN(-1);

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
        for (final OpsITC ops : OpsITC.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        ITCReq_GetMaplePoint.set(0);
        ITCReq_CharacterModifiedNFlush.set(1);
        ITCReq_RegisterSaleEntry.set(2);
        ITCReq_SaleCurrentItemToWish.set(3);
        ITCReq_RegisterBuyOrder.set(4);
        ITCReq_GetITCList.set(5);
        ITCReq_GetSearchITCList.set(6);
        ITCReq_CancelSaleItem.set(7);
        ITCReq_MoveITCPurchaseItemLtoS.set(8);
        ITCReq_SetZzim.set(9);
        ITCReq_DeleteZzim.set(10);
        ITCReq_LoadWishSaleList.set(11);
        ITCReq_BuyWish.set(12);
        ITCReq_CancelWish.set(13);
        ITCReq_BuyWishChargeCash.set(14);
        ITCReq_BuyWishCancel.set(15);
        ITCReq_BuyItem.set(16);
        ITCReq_BuyZzimItem.set(17);
        ITCReq_RegAuction.set(18);
        ITCReq_BidAuction.set(19);
        ITCReq_BuyAuctionImm.set(20);
        ITCRes_GetITCList_Done.set(21); // JMS147, GMS95 OK
        ITCRes_GetITCList_Failed.set(22);
        ITCRes_GetSearchITCList_Done.set(23);
        ITCRes_GetSearchITCList_Failed.set(24);
        ITCRes_GetMaplePoint_Done.set(25);
        ITCRes_GetMaplePoint_Failed.set(26);
        ITCRes_CharacterModifiedNFlush_Done.set(27);
        ITCRes_CharacterModifiedNFlush_Failed.set(28);
        ITCRes_RegisterSaleEntry_Done.set(29);
        ITCRes_RegisterSaleEntry_Failed.set(30);
        ITCRes_SaleCurrentItemToWish_Done.set(31);
        ITCRes_SaleCurrentItemToWish_Failed.set(32);
        ITCRes_GetUserPurchaseItem_Done.set(33);
        ITCRes_GetUserPurchaseItem_Failed.set(34);
        ITCRes_GetUserSaleItem_Done.set(35);
        ITCRes_GetUserSaleItem_Failed.set(36);
        ITCRes_CancelSaleItem_Done.set(37);
        ITCRes_CancelSaleItem_Failed.set(38);
        ITCRes_MoveITCPurchaseItemLtoS_Done.set(39);
        ITCRes_MoveITCPurchaseItemLtoS_Failed.set(40);
        ITCRes_SetZzim_Done.set(41);
        ITCRes_SetZzim_Failed.set(42);
        ITCRes_DeleteZzim_Done.set(43);
        ITCRes_DeleteZzim_Failed.set(44);
        ITCRes_LoadWishSaleList_Done.set(45);
        ITCRes_LoadWishSaleList_Failed.set(46);
        ITCRes_BuyWish_Done.set(47);
        ITCRes_BuyWish_Failed.set(48);
        ITCRes_CancelWish_Done.set(49);
        ITCRes_CancelWish_Failed.set(50);
        ITCRes_BuyItem_Done.set(51);
        ITCRes_BuyItem_Failed.set(52);
        ITCRes_BuyZzimItem_Done.set(53);
        ITCRes_BuyZzimItem_Failed.set(54);
        ITCRes_RegisterBuyOrder_Done.set(55);
        ITCRes_RegisterBuyOrder_Failed.set(56);
        ITCRes_RegAuction_Done.set(57);
        ITCRes_RegAuction_Failed.set(58);
        ITCRes_BidAuction_Done.set(59);
        ITCRes_BidAuction_Failed.set(60);
        ITCRes_GetNotifyCancelWishResult.set(61);
        ITCRes_GetSuccessBidInfoResult.set(62); // JMS147, GMS95 OK
        ITCFailReason_Unknown.set(63);
        ITCFailReason_Timeout.set(64);
        ITCFailReason_DBFailed.set(65);
        ITCFailReason_NoRemainCash.set(66); // JMS147, GMS95 OK
        ITCFailReason_NoRemainMeso.set(67);
        ITCFailReason_DeductCashFailed.set(68);
        ITCFailReason_CannotFindUser.set(69);
        ITCFailReason_SaveFailed.set(70);
        ITCFailReason_NoEmptyPos.set(71);
        ITCFailReason_SlotMaxMismatch.set(72);
        ITCFailReason_FirstLoadingFailed.set(73);
        ITCFailReason_ItemFailed.set(74);
        ITCFailReason_CashChargeFailed.set(75);
        ITCFailReason_CashFailed.set(76);
        ITCFailReason_DuplicateBid.set(77);
        ITCFailReason_AlreadyBid.set(78); // JMS147, GMS95 OK
        ITCFailReason_CannotRegisterItem.set(79); // JMS147, GMS95 OK
        // GMS things.
        ITCFailReason_AlreadyCancelled_or_SoldOut.set(80);
        ITCFailReason_AlreadySoldOut.set(81);
        ITCFailReason_DurabilityItem.set(82);
        ITCFailReason_ThrowingStar.set(83);
        ITCFailReason_LowCharacterLevel.set(84);
    }

}
