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
package tacos.packet.ops;

import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsCashItemFailReason implements IPacketOps {
    // GMS v95
    CashItemFailReason_Unknown(0),
    CashItemFailReason_Timeout(1),
    CashItemFailReason_CashDaemonDBError(2),
    CashItemFailReason_NoRemainCash(3),
    CashItemFailReason_GiftUnderAge(4),
    CashItemFailReason_GiftLimitOver(5),
    CashItemFailReason_GiftSameAccount(6),
    CashItemFailReason_GiftUnknownRecipient(7),
    CashItemFailReason_GiftRecipientGenderMismatch(8),
    CashItemFailReason_GiftRecipientLockerFull(9),
    CashItemFailReason_BuyStoredProcFailed(10),
    CashItemFailReason_GiftStoredProcFailed(11),
    CashItemFailReason_GiftNoReceiveCharacter(12),
    CashItemFailReason_GiftNoSenderCharacter(13),
    CashItemFailReason_InvalidCoupon(14),
    CashItemFailReason_InvalidCoupon_UserBan(15),
    CashItemFailReason_ExpiredCoupon(16),
    CashItemFailReason_UsedCoupon(17),
    CashItemFailReason_CouponForCafeOnly(18),
    CashItemFailReason_CouponForCafeOnly_Used(19),
    CashItemFailReason_CouponForCafeOnly_Expired(20),
    CashItemFailReason_NotAvailableCoupon(21),
    CashItemFailReason_GenderMisMatch(22),
    CashItemFailReason_GiftNormalItem(23),
    CashItemFailReason_GiftMaplePoint(24),
    CashItemFailReason_NoEmptyPos(25),
    CashItemFailReason_ForPremiumUserOnly(26),
    CashItemFailReason_BuyCoupleStoredProcFailed(27),
    CashItemFailReason_BuyFriendshipStoredProcFailed(28),
    CashItemFailReason_NotAvailableTime(29),
    CashItemFailReason_NoStock(30),
    CashItemFailReason_PurchaseLimitOver(31),
    CashItemFailReason_NoRemainMeso(32),
    CashItemFailReason_NotAuthorizedUser(33),
    CashItemFailReason_InvalidBirthDate(34),
    CashItemFailReason_InvalidPassportID(35),
    CashItemFailReason_IncorrectSSN2(36),
    CashItemFailReason_ForNoPurchaseExpUsersOnly(37),
    CashItemFailReason_AlreadyApplied(38),
    CashItemFailReason_WebShopUnknown(39),
    CashItemFailReason_WebShopInventoryCount(40),
    CashItemFailReason_WebShopBuyStoredProcFailed(41),
    CashItemFailReason_WebShopInvalidOrder(42),
    CashItemFailReason_GachaponLimitOver(43),
    CashItemFailReason_NoUser(44),
    CashItemFailReason_WrongCommoditySN(45),
    CashItemFailReason_CouponLimitError(46),
    CashItemFailReason_BridgeNotConnected(47),
    CashItemFailReason_UnderConstruction(48),
    CashItemFailReason_Account_Age_limit(49),
    CashItemFailReason_GiftNoMoney(50),
    CashItemFailReason_DBError(51),
    CashItemFailReason_AgeLimit(52),
    CashItemFailReason_RestrictSender(53),
    CashItemFailReason_RestrictReceiver(54),
    CashItemFailReason_ExceedLimit(55),
    CashItemFailReason_UnknownError(56),
    CashItemFailReason_LevelLimit_20(57),
    CashItemFailReason_TransferWorldFailed_InvalidWorld_SameWorld(58),
    CashItemFailReason_TransferWorldFailed_InvalidWorld_NewWorld(59),
    CashItemFailReason_TransferWorldFailed_InvalidWorld_FromNewWorld(60),
    CashItemFailReason_TransferWorldFailed_MaxCharacter(61),
    CashItemFailReason_EventError(62),
    CashItemFailReason_OnlyNXCash(63),
    CashItemFailReason_TryAgainRandomBox(64),
    CashItemFailReason_CannotBuyOneADayItem(65),
    CashItemFailReason_TooYoungToBuy(66),
    CashItemFailReason_GiftTooYoungToRecv(67),
    CashItemFailReason_LimitOverTheItem(68),
    CashItemFailReason_CashLock(69),
    UNKNOWN;

    private final int gms95;
    private int value;

    OpsCashItemFailReason(int val) {
        this.gms95 = val;
        this.value = val;
    }

    OpsCashItemFailReason() {
        this.gms95 = -1;
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

    public static OpsCashItemFailReason find(int val) {
        for (OpsCashItemFailReason ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        for (OpsCashItemFailReason ops : values()) {
            ops.set(ops.gms95);
        }
        if (!Region.JMS.check()) {
            return;
        }
        // JMS v186
        for (OpsCashItemFailReason ops : values()) {
            ops.set(UNKNOWN.get());
        }
        CashItemFailReason_Timeout.set(178);
        CashItemFailReason_NoRemainCash.set(180);
        CashItemFailReason_GiftUnderAge.set(181);
        CashItemFailReason_GiftLimitOver.set(182);
        CashItemFailReason_GiftSameAccount.set(183);
        CashItemFailReason_GiftUnknownRecipient.set(184);
        CashItemFailReason_GiftRecipientGenderMismatch.set(185);
        CashItemFailReason_GiftRecipientLockerFull.set(186);
        CashItemFailReason_BuyStoredProcFailed.set(187);
        CashItemFailReason_InvalidCoupon.set(191);
        CashItemFailReason_ExpiredCoupon.set(192);
        CashItemFailReason_UsedCoupon.set(193);
        CashItemFailReason_CouponForCafeOnly.set(194);
        CashItemFailReason_CouponForCafeOnly_Used.set(195);
        CashItemFailReason_CouponForCafeOnly_Expired.set(196);
        CashItemFailReason_NotAvailableCoupon.set(197);
        CashItemFailReason_GenderMisMatch.set(198);
        CashItemFailReason_GiftNormalItem.set(199);
        CashItemFailReason_GiftMaplePoint.set(200);
        CashItemFailReason_NoEmptyPos.set(201);
        CashItemFailReason_ForPremiumUserOnly.set(202);
        CashItemFailReason_BuyCoupleStoredProcFailed.set(203);
        CashItemFailReason_BuyFriendshipStoredProcFailed.set(204);
        CashItemFailReason_NotAvailableTime.set(205);
        CashItemFailReason_NoStock.set(206);
        CashItemFailReason_PurchaseLimitOver.set(207);
        CashItemFailReason_NoRemainMeso.set(208);
        CashItemFailReason_InvalidPassportID.set(209);
        CashItemFailReason_IncorrectSSN2.set(210); // インベントリに空きがないため、プレゼントが渡せませんでした。
        CashItemFailReason_ForNoPurchaseExpUsersOnly.set(211);
        CashItemFailReason_AlreadyApplied.set(212);
        CashItemFailReason_GachaponLimitOver.set(217);
        CashItemFailReason_CouponLimitError.set(220);
        CashItemFailReason_Account_Age_limit.set(222);
        CashItemFailReason_GiftNoMoney.set(223);
        CashItemFailReason_ExceedLimit.set(225);
        CashItemFailReason_UnknownError.set(227);
        CashItemFailReason_LevelLimit_20.set(228);
        CashItemFailReason_TransferWorldFailed_InvalidWorld_SameWorld.set(229);
        CashItemFailReason_TransferWorldFailed_InvalidWorld_NewWorld.set(230);
        CashItemFailReason_TransferWorldFailed_MaxCharacter.set(231);
        CashItemFailReason_OnlyNXCash.set(236);
        CashItemFailReason_TryAgainRandomBox.set(237); // しばらく後もう一度行ってください。
        // 0xE8 Lv30以上, 0xE9 Lv70以上
    }
}
