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

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsCashItem implements IPacketOps {
    // GMS v95
    CashItemReq_WebShopOrderGetList(0),
    CashItemReq_LoadLocker(1),
    CashItemReq_LoadWish(2),
    CashItemReq_Buy(3),
    CashItemReq_Gift(4),
    CashItemReq_SetWish(5),
    CashItemReq_IncSlotCount(6),
    CashItemReq_IncTrunkCount(7),
    CashItemReq_IncCharSlotCount(8),
    CashItemReq_IncBuyCharCount(9),
    CashItemReq_EnableEquipSlotExt(10),
    CashItemReq_CancelPurchase(11),
    CashItemReq_ConfirmPurchase(12),
    CashItemReq_Destroy(13),
    CashItemReq_MoveLtoS(14),
    CashItemReq_MoveStoL(15),
    CashItemReq_Expire(16),
    CashItemReq_Use(17),
    CashItemReq_StatChange(18),
    CashItemReq_SkillChange(19),
    CashItemReq_SkillReset(20),
    CashItemReq_DestroyPetItem(21),
    CashItemReq_SetPetName(22),
    CashItemReq_SetPetLife(23),
    CashItemReq_SetPetSkill(24),
    CashItemReq_SetItemName(25),
    CashItemReq_SendMemo(26),
    CashItemReq_GetMaplePoint(27),
    CashItemReq_Rebate(28),
    CashItemReq_UseCoupon(29),
    CashItemReq_GiftCoupon(30),
    CashItemReq_Couple(31),
    CashItemReq_BuyPackage(32),
    CashItemReq_GiftPackage(33),
    CashItemReq_BuyNormal(34),
    CashItemReq_ApplyWishListEvent(35),
    CashItemReq_MovePetStat(36),
    CashItemReq_FriendShip(37),
    CashItemReq_ShopScan(38),
    CashItemReq_LoadPetExceptionList(39),
    CashItemReq_UpdatePetExceptionList(40),
    CashItemReq_FreeCashItem(41),
    CashItemReq_LoadFreeCashItem(42),
    CashItemReq_Script(43),
    CashItemReq_PurchaseRecord(44),
    CashItemReq_TradeDone(45),
    CashItemReq_BuyDone(46),
    CashItemReq_TradeSave(47),
    CashItemReq_TradeLog(48),
    CashItemReq_EvolPet(49),
    CashItemReq_BuyNameChange(50),
    CashItemReq_CancelChangeName(51),
    CashItemRes_CancelNameChangeFail(52),
    CashItemReq_BuyTransferWorld(53),
    CashItemReq_CancelTransferWorld(54),
    CashItemReq_CharacterSale(55),
    CashItemRes_CharacterSaleSuccess(56),
    CashItemRes_CharacterSaleFail(57),
    CashItemRes_CharacterSaleInvalidName(58),
    CashItemRes_CharacterSaleInvalidItem(59),
    CashItemReq_ItemUpgrade(60),
    CashItemRes_ItemUpgradeSuccess(61),
    CashItemReq_ItemUpgradeFail(62),
    CashItemReq_ItemUpgradeReq(63),
    CashItemReq_ItemUpgradeDone(64),
    CashItemRes_ItemUpgradeDone(65),
    CashItemRes_ItemUpgradeErr(66),
    CashItemReq_Vega(67),
    CashItemRes_VegaSuccess1(68),
    CashItemRes_VegaSuccess2(69),
    CashItemRes_VegaErr(70),
    CashItemRes_VegaErr2(71),
    CashItemRes_VegaErr_InvalidItem(72),
    CashItemRes_VegaFail(73),
    CashItemReq_CashItemGachapon(74),
    CashItemReq_CashGachaponOpen(75),
    CashItemReq_CashGachaponCopy(76),
    CashItemReq_ChangeMaplePoint(77),
    CashItemReq_CheckFreeCashItemTable(78),
    CashItemRes_CheckFreeCashItemTable_Done(79),
    CashItemRes_CheckFreeCashItemTable_Failed(80),
    CashItemReq_SetFreeCashItemTable(81),
    CashItemRes_SetFreeCashItemTable_Done(82),
    CashItemRes_SetFreeCashItemTable_Failed(83),
    CashItemRes_LimitGoodsCount_Changed(84),
    CashItemRes_WebShopOrderGetList_Done(85),
    CashItemRes_WebShopOrderGetList_Failed(86),
    CashItemRes_WebShopReceive_Done(87),
    CashItemRes_LoadLocker_Done(88),
    CashItemRes_LoadLocker_Failed(89),
    CashItemRes_LoadGift_Done(90),
    CashItemRes_LoadGift_Failed(91),
    CashItemRes_LoadWish_Done(92),
    CashItemRes_LoadWish_Failed(93),
    CashItemRes_MapleTV_Failed_Wrong_User_Name(94),
    CashItemRes_MapleTV_Failed_User_Not_Connected(95),
    CashItemRes_AvatarMegaphone_Queue_Full(96),
    CashItemRes_AvatarMegaphone_Level_Limit(97),
    CashItemRes_SetWish_Done(98),
    CashItemRes_SetWish_Failed(99),
    CashItemRes_Buy_Done(100),
    CashItemRes_Buy_Failed(101),
    CashItemRes_UseCoupon_Done(102),
    CashItemRes_UseCoupon_Done_NormalItem(103),
    CashItemRes_GiftCoupon_Done(104),
    CashItemRes_UseCoupon_Failed(105),
    CashItemRes_UseCoupon_CashItem_Failed(106),
    CashItemRes_Gift_Done(107),
    CashItemRes_Gift_Failed(108),
    CashItemRes_IncSlotCount_Done(109),
    CashItemRes_IncSlotCount_Failed(110),
    CashItemRes_IncTrunkCount_Done(111),
    CashItemRes_IncTrunkCount_Failed(112),
    CashItemRes_IncCharSlotCount_Done(113),
    CashItemRes_IncCharSlotCount_Failed(114),
    CashItemRes_IncBuyCharCount_Done(115),
    CashItemRes_IncBuyCharCount_Failed(116),
    CashItemRes_EnableEquipSlotExt_Done(117),
    CashItemRes_EnableEquipSlotExt_Failed(118),
    CashItemRes_MoveLtoS_Done(119),
    CashItemRes_MoveLtoS_Failed(120),
    CashItemRes_MoveStoL_Done(121),
    CashItemRes_MoveStoL_Failed(122),
    CashItemRes_Destroy_Done(123),
    CashItemRes_Destroy_Failed(124),
    CashItemRes_Expire_Done(125),
    CashItemRes_Expire_Failed(126),
    CashItemRes_Use_Done(127),
    CashItemRes_Use_Failed(128),
    CashItemRes_StatChange_Done(129),
    CashItemRes_StatChange_Failed(130),
    CashItemRes_SkillChange_Done(131),
    CashItemRes_SkillChange_Failed(132),
    CashItemRes_SkillReset_Done(133),
    CashItemRes_SkillReset_Failed(134),
    CashItemRes_DestroyPetItem_Done(135),
    CashItemRes_DestroyPetItem_Failed(136),
    CashItemRes_SetPetName_Done(137),
    CashItemRes_SetPetName_Failed(138),
    CashItemRes_SetPetLife_Done(139),
    CashItemRes_SetPetLife_Failed(140),
    CashItemRes_MovePetStat_Failed(141),
    CashItemRes_MovePetStat_Done(142),
    CashItemRes_SetPetSkill_Failed(143),
    CashItemRes_SetPetSkill_Done(144),
    CashItemRes_SendMemo_Done(145),
    CashItemRes_SendMemo_Warning(146),
    CashItemRes_SendMemo_Failed(147),
    CashItemRes_GetMaplePoint_Done(148),
    CashItemRes_GetMaplePoint_Failed(149),
    CashItemRes_Rebate_Done(150),
    CashItemRes_Rebate_Failed(151),
    CashItemRes_Couple_Done(152),
    CashItemRes_Couple_Failed(153),
    CashItemRes_BuyPackage_Done(154),
    CashItemRes_BuyPackage_Failed(155),
    CashItemRes_GiftPackage_Done(156),
    CashItemRes_GiftPackage_Failed(157),
    CashItemRes_BuyNormal_Done(158),
    CashItemRes_BuyNormal_Failed(159),
    CashItemRes_ApplyWishListEvent_Done(160),
    CashItemRes_ApplyWishListEvent_Failed(161),
    CashItemRes_Friendship_Done(162),
    CashItemRes_Friendship_Failed(163),
    CashItemRes_LoadExceptionList_Done(164),
    CashItemRes_LoadExceptionList_Failed(165),
    CashItemRes_UpdateExceptionList_Done(166),
    CashItemRes_UpdateExceptionList_Failed(167),
    CashItemRes_LoadFreeCashItem_Done(168),
    CashItemRes_LoadFreeCashItem_Failed(169),
    CashItemRes_FreeCashItem_Done(170),
    CashItemRes_FreeCashItem_Failed(171),
    CashItemRes_Script_Done(172),
    CashItemRes_Script_Failed(173),
    CashItemRes_Bridge_Failed(174),
    CashItemRes_PurchaseRecord_Done(175),
    CashItemRes_PurchaseRecord_Failed(176),
    CashItemRes_EvolPet_Failed(177),
    CashItemRes_EvolPet_Done(178),
    CashItemRes_NameChangeBuy_Done(179),
    CashItemRes_NameChangeBuy_Failed(180),
    CashItemRes_TransferWorld_Done(181),
    CashItemRes_TransferWorld_Failed(182),
    CashItemRes_CashGachaponOpen_Done(183),
    CashItemRes_CashGachaponOpen_Failed(184),
    CashItemRes_CashGachaponCopy_Done(185),
    CashItemRes_CashGachaponCopy_Failed(186),
    CashItemRes_ChangeMaplePoint_Done(187),
    CashItemRes_ChangeMaplePoint_Failed(188),
    CashItemReq_Give(189),
    CashItemRes_Give_Done(190),
    CashItemRes_Give_Failed(191),
    CashItemRes_GashItemGachapon_Failed(192),
    CashItemRes_CashItemGachapon_Done(193),
    UNKNOWN;

    private final int gms95;
    private int value;

    OpsCashItem(int val) {
        this.gms95 = val;
        this.value = val;
    }

    OpsCashItem() {
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

    public static OpsCashItem find(int val) {
        for (OpsCashItem ops : values()) {
            if (ops.get() == val && val != UNKNOWN.get()) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        for (OpsCashItem ops : values()) {
            ops.set(ops.gms95);
        }
        if (!Region.JMS.check()) {
            return;
        }
        // JMS v186 (CCashShop::OnCashItemResult), others are not used.
        for (OpsCashItem ops : values()) {
            ops.set(UNKNOWN.get());
        }
        CashItemReq_Buy.set(3);
        CashItemReq_Gift.set(4);
        CashItemReq_SetWish.set(5);
        CashItemReq_IncSlotCount.set(6);
        CashItemReq_IncTrunkCount.set(7);
        CashItemReq_MoveLtoS.set(14);
        CashItemReq_MoveStoL.set(15);
        CashItemReq_Destroy.set(27);
        CashItemReq_BuyPackage.set(31);
        CashItemReq_BuyNormal.set(33);
        CashItemReq_FreeCashItem.set(42);
        CashItemReq_ItemUpgrade.set(51);
        CashItemRes_ItemUpgradeSuccess.set(52);
        CashItemReq_ItemUpgradeFail.set(53);
        CashItemReq_ItemUpgradeReq.set(54);
        CashItemReq_ItemUpgradeDone.set(55);
        CashItemRes_ItemUpgradeDone.set(56);
        CashItemRes_ItemUpgradeErr.set(57);
        CashItemReq_Vega.set(58);
        CashItemRes_VegaSuccess1.set(59);
        CashItemRes_VegaSuccess2.set(60);
        CashItemRes_VegaErr.set(61);
        CashItemRes_VegaErr2.set(62);
        CashItemRes_VegaErr_InvalidItem.set(63);
        CashItemRes_VegaFail.set(64);
        CashItemRes_LoadLocker_Done.set(78);
        CashItemRes_LoadLocker_Failed.set(79);
        CashItemRes_LoadGift_Done.set(80);
        CashItemRes_LoadGift_Failed.set(81);
        CashItemRes_LoadWish_Done.set(82);
        CashItemRes_LoadWish_Failed.set(83);
        CashItemRes_SetWish_Done.set(86);
        CashItemRes_SetWish_Failed.set(87);
        CashItemRes_Buy_Done.set(88);
        CashItemRes_Buy_Failed.set(89);
        CashItemRes_UseCoupon_Done.set(90);
        CashItemRes_GiftCoupon_Done.set(92);
        CashItemRes_UseCoupon_Failed.set(93);
        CashItemRes_IncSlotCount_Done.set(97);
        CashItemRes_IncSlotCount_Failed.set(98);
        CashItemRes_IncTrunkCount_Done.set(99);
        CashItemRes_IncTrunkCount_Failed.set(100);
        CashItemRes_MoveLtoS_Done.set(107);
        CashItemRes_MoveLtoS_Failed.set(108);
        CashItemRes_MoveStoL_Done.set(109);
        CashItemRes_MoveStoL_Failed.set(110);
        CashItemRes_Destroy_Done.set(111);
        CashItemRes_Destroy_Failed.set(112);
        CashItemRes_Expire_Done.set(113);
        CashItemRes_BuyNormal_Done.set(144);
        CashItemRes_BuyNormal_Failed.set(145);
        CashItemRes_BuyPackage_Done.set(-1);
        CashItemRes_BuyPackage_Failed.set(-1);
        CashItemRes_FreeCashItem_Done.set(165);
        CashItemRes_GashItemGachapon_Failed.set(234);
        CashItemRes_CashItemGachapon_Done.set(235);
        // JMS v147 : the results from 0x4A to 0x6D are 28 smaller.
        if (Config.LessOrEqual(Region.JMS, 164)) {
            for (OpsCashItem ops : values()) {
                if (0x4A <= ops.get() && ops.get() <= 0x6D && ops.name().startsWith("CashItemRes_")) {
                    ops.set(ops.get() - 28);
                }
            }
        }
        if (Config.GreaterOrEqual(Region.JMS, 187)) {
            CashItemReq_ItemUpgrade.set(52);
            CashItemRes_ItemUpgradeSuccess.set(53);
            CashItemReq_ItemUpgradeFail.set(54);
            CashItemReq_ItemUpgradeReq.set(55);
            CashItemReq_ItemUpgradeDone.set(56);
            CashItemRes_ItemUpgradeDone.set(57);
            CashItemRes_ItemUpgradeErr.set(58);
            CashItemReq_Vega.set(59);
            CashItemRes_VegaSuccess1.set(60);
            CashItemRes_VegaSuccess2.set(61);
            CashItemRes_VegaErr.set(62);
            CashItemRes_VegaErr2.set(63);
            CashItemRes_VegaErr_InvalidItem.set(64);
            CashItemRes_VegaFail.set(65);
        }
        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            CashItemReq_ItemUpgrade.set(57);
            CashItemRes_ItemUpgradeSuccess.set(CashItemReq_ItemUpgrade.get() + 1);
            CashItemReq_ItemUpgradeFail.set(CashItemReq_ItemUpgrade.get() + 2);
            CashItemReq_ItemUpgradeReq.set(CashItemReq_ItemUpgrade.get() + 3);
            CashItemReq_ItemUpgradeDone.set(CashItemReq_ItemUpgrade.get() + 4);
            CashItemRes_ItemUpgradeDone.set(CashItemReq_ItemUpgrade.get() + 5);
            CashItemRes_ItemUpgradeErr.set(CashItemReq_ItemUpgrade.get() + 6);
            CashItemReq_Vega.set(CashItemReq_ItemUpgrade.get() + 7);
            CashItemRes_VegaSuccess1.set(CashItemReq_ItemUpgrade.get() + 8);
            CashItemRes_VegaSuccess2.set(CashItemReq_ItemUpgrade.get() + 9);
            CashItemRes_VegaErr.set(CashItemReq_ItemUpgrade.get() + 10);
            CashItemRes_VegaErr2.set(CashItemReq_ItemUpgrade.get() + 11);
            CashItemRes_VegaErr_InvalidItem.set(CashItemReq_ItemUpgrade.get() + 12);
            CashItemRes_VegaFail.set(CashItemReq_ItemUpgrade.get() + 13);
        }
    }
}
