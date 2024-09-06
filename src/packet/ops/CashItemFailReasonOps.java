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
package packet.ops;

/**
 *
 * @author Riremito
 */
public enum CashItemFailReasonOps {
    // JMS v186
    CashItemFailReason_Timeout(0xB2),
    CashItemFailReason_NoRemainCash(0xB4),
    CashItemFailReason_GiftUnderAge(0xB5),
    CashItemFailReason_GiftLimitOver(0xB6),
    CashItemFailReason_GiftSameAccount(0xB7),
    CashItemFailReason_GiftUnknownRecipient(0xB8),
    CashItemFailReason_GiftRecipientGenderMismatch(0xB9),
    CashItemFailReason_GiftRecipientLockerFull(0xBA),
    CashItemFailReason_BuyStoredProcFailed(0xBB),
    CashItemFailReason_InvalidCoupon(0xBF),
    CashItemFailReason_ExpiredCoupon(0xC0),
    CashItemFailReason_UsedCoupon(0xC1),
    CashItemFailReason_CouponForCafeOnly(0xC2),
    CashItemFailReason_CouponForCafeOnly_Used(0xC3),
    CashItemFailReason_CouponForCafeOnly_Expired(0xC4),
    CashItemFailReason_NotAvailableCoupon(0xC5),
    CashItemFailReason_GenderMisMatch(0xC6),
    CashItemFailReason_GiftNormalItem(0xC7),
    CashItemFailReason_GiftMaplePoint(0xC8),
    CashItemFailReason_NoEmptyPos(0xC9),
    CashItemFailReason_ForPremiumUserOnly(0xCA),
    CashItemFailReason_BuyCoupleStoredProcFailed(0xCB),
    CashItemFailReason_BuyFriendshipStoredProcFailed(0xCC),
    CashItemFailReason_NotAvailableTime(0xCD),
    CashItemFailReason_NoStock(0xCE),
    CashItemFailReason_PurchaseLimitOver(0xCF),
    CashItemFailReason_NoRemainMeso(0xD0),
    CashItemFailReason_InvalidPassportID(0xD1),
    CashItemFailReason_IncorrectSSN2(0xD2), // インベントリに空きがないため、プレゼントが渡せませんでした。
    CashItemFailReason_ForNoPurchaseExpUsersOnly(0xD3),
    CashItemFailReason_AlreadyApplied(0xD4),
    CashItemFailReason_GachaponLimitOver(0xD9),
    CashItemFailReason_CouponLimitError(0xDC),
    CashItemFailReason_Account_Age_limit(0xDE),
    CashItemFailReason_GiftNoMoney(0xDF),
    CashItemFailReason_ExceedLimit(0xE1),
    CashItemFailReason_UnknownError(0xE3),
    CashItemFailReason_LevelLimit_20(0xE4),
    CashItemFailReason_TransferWorldFailed_InvalidWorld_SameWorld(0xE5),
    CashItemFailReason_TransferWorldFailed_InvalidWorld_NewWorld(0xE6),
    CashItemFailReason_TransferWorldFailed_MaxCharacter(0xE7),
    // 0xE8 Lv30以上
    // 0xE9 Lv70以上
    CashItemFailReason_OnlyNXCash(0xEC),
    CashItemFailReason_TryAgainRandomBox(0xED), // しばらく後もう一度行ってください。
    // GMS v95
    /*
    CashItemFailReason_Unknown(0x0),
    CashItemFailReason_Timeout(0x1),
    CashItemFailReason_CashDaemonDBError(0x2),
    CashItemFailReason_NoRemainCash(0x3),
    CashItemFailReason_GiftUnderAge(0x4),
    CashItemFailReason_GiftLimitOver(0x5),
    CashItemFailReason_GiftSameAccount(0x6),
    CashItemFailReason_GiftUnknownRecipient(0x7),
    CashItemFailReason_GiftRecipientGenderMismatch(0x8),
    CashItemFailReason_GiftRecipientLockerFull(0x9),
    CashItemFailReason_BuyStoredProcFailed(0xA),
    CashItemFailReason_GiftStoredProcFailed(0xB),
    CashItemFailReason_GiftNoReceiveCharacter(0xC),
    CashItemFailReason_GiftNoSenderCharacter(0xD),
    CashItemFailReason_InvalidCoupon(0xE),
    CashItemFailReason_InvalidCoupon_UserBan(0xF),
    CashItemFailReason_ExpiredCoupon(0x10),
    CashItemFailReason_UsedCoupon(0x11),
    CashItemFailReason_CouponForCafeOnly(0x12),
    CashItemFailReason_CouponForCafeOnly_Used(0x13),
    CashItemFailReason_CouponForCafeOnly_Expired(0x14),
    CashItemFailReason_NotAvailableCoupon(0x15),
    CashItemFailReason_GenderMisMatch(0x16),
    CashItemFailReason_GiftNormalItem(0x17),
    CashItemFailReason_GiftMaplePoint(0x18),
    CashItemFailReason_NoEmptyPos(0x19),
    CashItemFailReason_ForPremiumUserOnly(0x1A),
    CashItemFailReason_BuyCoupleStoredProcFailed(0x1B),
    CashItemFailReason_BuyFriendshipStoredProcFailed(0x1C),
    CashItemFailReason_NotAvailableTime(0x1D),
    CashItemFailReason_NoStock(0x1E),
    CashItemFailReason_PurchaseLimitOver(0x1F),
    CashItemFailReason_NoRemainMeso(0x20),
    CashItemFailReason_NotAuthorizedUser(0x21),
    CashItemFailReason_InvalidBirthDate(0x22),
    CashItemFailReason_InvalidPassportID(0x23),
    CashItemFailReason_IncorrectSSN2(0x24),
    CashItemFailReason_ForNoPurchaseExpUsersOnly(0x25),
    CashItemFailReason_AlreadyApplied(0x26),
    CashItemFailReason_WebShopUnknown(0x27),
    CashItemFailReason_WebShopInventoryCount(0x28),
    CashItemFailReason_WebShopBuyStoredProcFailed(0x29),
    CashItemFailReason_WebShopInvalidOrder(0x2A),
    CashItemFailReason_GachaponLimitOver(0x2B),
    CashItemFailReason_NoUser(0x2C),
    CashItemFailReason_WrongCommoditySN(0x2D),
    CashItemFailReason_CouponLimitError(0x2E),
    CashItemFailReason_BridgeNotConnected(0x2F),
    CashItemFailReason_UnderConstruction(0x30),
    CashItemFailReason_Account_Age_limit(0x31),
    CashItemFailReason_GiftNoMoney(0x32),
    CashItemFailReason_DBError(0x33),
    CashItemFailReason_AgeLimit(0x34),
    CashItemFailReason_RestrictSender(0x35),
    CashItemFailReason_RestrictReceiver(0x36),
    CashItemFailReason_ExceedLimit(0x37),
    CashItemFailReason_UnknownError(0x38),
    CashItemFailReason_LevelLimit_20(0x39),
    CashItemFailReason_TransferWorldFailed_InvalidWorld_SameWorld(0x3A),
    CashItemFailReason_TransferWorldFailed_InvalidWorld_NewWorld(0x3B),
    CashItemFailReason_TransferWorldFailed_InvalidWorld_FromNewWorld(0x3C),
    CashItemFailReason_TransferWorldFailed_MaxCharacter(0x3D),
    CashItemFailReason_EventError(0x3E),
    CashItemFailReason_OnlyNXCash(0x3F),
    CashItemFailReason_TryAgainRandomBox(0x40),
    CashItemFailReason_CannotBuyOneADayItem(0x41),
    CashItemFailReason_TooYoungToBuy(0x42),
    CashItemFailReason_GiftTooYoungToRecv(0x43),
    CashItemFailReason_LimitOverTheItem(0x44),
    CashItemFailReason_CashLock(0x45),
     */
    UNKNOWN(-1);

    private int value;

    CashItemFailReasonOps(int flag) {
        value = flag;
    }

    CashItemFailReasonOps() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public static CashItemFailReasonOps find(int val) {
        for (final CashItemFailReasonOps o : CashItemFailReasonOps.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }
}
