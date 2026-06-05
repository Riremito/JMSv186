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

import tacos.config.Region;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsSecondaryStat implements IPacketOps {
    CTS_PAD(0),
    CTS_PDD(1),
    CTS_MAD(2),
    CTS_MDD(3),
    CTS_ACC(4),
    CTS_EVA(5),
    CTS_Craft(6),
    CTS_Speed(7),
    CTS_Jump(8),
    CTS_MagicGuard(9),
    CTS_DarkSight(10), // JMS146-186
    CTS_Booster(11),
    CTS_PowerGuard(12),
    CTS_MaxHP(13),
    CTS_MaxMP(14),
    CTS_Invincible(15),
    CTS_SoulArrow(16),
    CTS_Stun(17),
    CTS_Poison(18), // JMS146-186
    CTS_Seal(19),
    CTS_Darkness(20),
    CTS_ComboCounter(21),
    CTS_WeaponCharge(22),
    CTS_DragonBlood(23),
    CTS_HolySymbol(24),
    CTS_MesoUp(25),
    CTS_ShadowPartner(26),
    CTS_PickPocket(27),
    CTS_MesoGuard(28),
    CTS_Thaw(29),
    CTS_Weakness(30),
    CTS_Curse(31),
    CTS_Slow(32),
    CTS_Morph(33),
    CTS_Regen(34),
    CTS_BasicStatUp(35),
    CTS_Stance(36),
    CTS_SharpEyes(37),
    CTS_ManaReflection(38),
    CTS_Attract(39), // JMS146-186
    CTS_SpiritJavelin(40),
    CTS_Infinity(41),
    CTS_Holyshield(42),
    CTS_HamString(43),
    CTS_Blind(44),
    CTS_Concentration(45),
    CTS_BanMap(46),
    CTS_MaxLevelBuff(47), // JMS186
    CTS_MesoUpByItem(48),
    CTS_Ghost(49),
    CTS_Barrier(50),
    CTS_ReverseInput(51),
    CTS_ItemUpByItem(52),
    CTS_RespectPImmune(53),
    CTS_RespectMImmune(54),
    CTS_DefenseAtt(55),
    CTS_DefenseState(56),
    CTS_IncEffectHPPotion(57),
    CTS_IncEffectMPPotion(58),
    CTS_DojangBerserk(59),
    CTS_DojangInvincible(60),
    CTS_Spark(61),
    CTS_DojangShield(62),
    CTS_SoulMasterFinal(63),
    CTS_WindBreakerFinal(64),
    CTS_ElementalReset(65),
    CTS_WindWalk(66),
    CTS_EventRate(67),
    CTS_ComboAbilityBuff(68),
    CTS_ComboDrain(69),
    CTS_ComboBarrier(70),
    CTS_BodyPressure(71),
    CTS_SmartKnockback(72),
    CTS_RepeatEffect(73),
    CTS_ExpBuffRate(74),
    CTS_StopPortion(75),
    CTS_StopMotion(76),
    CTS_Fear(77),
    CTS_EvanSlow(78),
    CTS_MagicShield(79),
    CTS_MagicResistance(80),
    CTS_SoulStone(81),
    CTS_Flying(82),
    CTS_Frozen(83),
    CTS_AssistCharge(84),
    CTS_Enrage(85),
    CTS_SuddenDeath(86),
    CTS_NotDamaged(87),
    CTS_FinalCut(88),
    CTS_ThornsEffect(89),
    CTS_SwallowAttackDamage(90),
    CTS_MorewildDamageUp(91),
    CTS_Mine(92),
    CTS_EMHP(93),
    CTS_EMMP(94),
    CTS_EPAD(95),
    CTS_EPDD(96),
    CTS_EMDD(97),
    CTS_Guard(98),
    CTS_SafetyDamage(99),
    CTS_SafetyAbsorb(100),
    CTS_Cyclone(101),
    CTS_SwallowCritical(102),
    CTS_SwallowMaxMP(103),
    CTS_SwallowDefence(104),
    CTS_SwallowEvasion(105),
    CTS_Conversion(106),
    CTS_Revive(107),
    CTS_Sneak(108),
    CTS_Mechanic(109),
    CTS_Aura(110),
    CTS_DarkAura(111),
    CTS_BlueAura(112),
    CTS_YellowAura(113),
    CTS_SuperBody(114),
    CTS_MorewildMaxHP(115),
    CTS_Dice(116),
    CTS_BlessingArmor(117),
    CTS_DamR(118),
    CTS_TeleportMasteryOn(119),
    CTS_CombatOrders(120),
    CTS_Beholder(121),
    CTS_EnergyCharged(122, TwoStateType.EXPIRE_LAST),
    CTS_Dash_Speed(123, TwoStateType.EXPIRE_LAST),
    CTS_Dash_Jump(124, TwoStateType.EXPIRE_LAST),
    CTS_RideVehicle(125, TwoStateType.RIDING),
    CTS_PartyBooster(126, TwoStateType.EXPIRE_CURRENT),
    CTS_GuidedBullet(127, TwoStateType.GUIDED_BULLET),
    CTS_Undead(128, TwoStateType.EXPIRE_LAST),
    CTS_SummonBomb(129, TwoStateType.NO_EXPIRE), // overflow in GMS95.
    CTS_SwallowBuff, // CTS_SwallowAttackDamage | CTS_SwallowDefence | CTS_SwallowCritical | CTS_SwallowMaxMP | CTS_SwallowEvasion
    UNKNOWN;

    private int value;
    private TwoStateType two_state;

    OpsSecondaryStat(int value) {
        this.value = value;
        this.two_state = TwoStateType.NOT_TWO_STATE;
    }

    OpsSecondaryStat(int value, TwoStateType two_state) {
        this.value = value;
        this.two_state = two_state;
    }

    OpsSecondaryStat() {
        this.value = -1;
        this.two_state = TwoStateType.NOT_TWO_STATE;
    }

    @Override
    public int get() {
        return this.value;
    }

    public static enum TwoStateType {
        NOT_TWO_STATE,
        RIDING,
        NO_EXPIRE,
        EXPIRE_CURRENT,
        EXPIRE_LAST,
        GUIDED_BULLET;
    }

    public boolean isTwoState() {
        return this.two_state != TwoStateType.NOT_TWO_STATE;
    }

    public TwoStateType getTwoState() {
        return this.two_state;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }

    public int getNl() {
        return this.value / 32;
    }

    public int getNr() {
        return 1 << (this.value % 32);
    }

    public static OpsSecondaryStat find(int val) {
        for (final OpsSecondaryStat ops : OpsSecondaryStat.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsSecondaryStat ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        // UINT160
        if (Version.GreaterOrEqual(Region.JMS, 187)) {
            clear();
            CTS_PAD.set(0);
            CTS_PDD.set(1);
            CTS_MAD.set(2);
            CTS_MDD.set(3);
            CTS_ACC.set(4);
            CTS_EVA.set(5);
            CTS_Craft.set(6);
            CTS_Speed.set(7);
            CTS_Jump.set(8);
            CTS_MagicGuard.set(9);
            CTS_DarkSight.set(10);
            CTS_Booster.set(11);
            CTS_PowerGuard.set(12);
            CTS_MaxHP.set(13);
            CTS_MaxMP.set(14);
            CTS_Invincible.set(15);
            CTS_SoulArrow.set(16);
            CTS_Stun.set(17);
            CTS_Poison.set(18);
            CTS_Seal.set(19);
            CTS_Darkness.set(20);
            CTS_ComboCounter.set(21);
            CTS_WeaponCharge.set(22);
            CTS_DragonBlood.set(23);
            CTS_HolySymbol.set(24);
            CTS_MesoUp.set(25);
            CTS_ShadowPartner.set(26);
            CTS_PickPocket.set(27);
            CTS_MesoGuard.set(28);
            CTS_Thaw.set(29);
            CTS_Weakness.set(30);
            CTS_Curse.set(31);
            CTS_Slow.set(32);
            CTS_Morph.set(33);
            CTS_Regen.set(34);
            CTS_BasicStatUp.set(35);
            CTS_Stance.set(36);
            CTS_SharpEyes.set(37);
            CTS_ManaReflection.set(38);
            CTS_Attract.set(39);
            CTS_SpiritJavelin.set(40);
            CTS_Infinity.set(41);
            CTS_Holyshield.set(42);
            CTS_HamString.set(43);
            CTS_Blind.set(44);
            CTS_Concentration.set(45);
            CTS_BanMap.set(46);
            CTS_MaxLevelBuff.set(47);
            CTS_MesoUpByItem.set(48);
            CTS_Ghost.set(49);
            CTS_Barrier.set(50);
            CTS_ReverseInput.set(51); // OK
            /*
            CTS_ItemUpByItem.set(0);
            CTS_RespectPImmune.set(0);
            CTS_RespectMImmune.set(0);
            CTS_DefenseAtt.set(0);
            CTS_DefenseState.set(0);
            CTS_IncEffectHPPotion.set(0);
            CTS_IncEffectMPPotion.set(0);
             */
            CTS_DojangBerserk.set(57); // -2
            CTS_DojangInvincible.set(58);
            CTS_Spark.set(59);
            CTS_DojangShield.set(60);
            CTS_SoulMasterFinal.set(61);
            CTS_WindBreakerFinal.set(62);
            CTS_ElementalReset.set(63);
            CTS_WindWalk.set(64);
            CTS_EventRate.set(65);
            CTS_ComboAbilityBuff.set(66);
            CTS_ComboDrain.set(67);
            CTS_ComboBarrier.set(68);
            CTS_BodyPressure.set(69);
            CTS_SmartKnockback.set(70);
            CTS_RepeatEffect.set(71);
            CTS_ExpBuffRate.set(72);
            CTS_StopPortion.set(73);
            CTS_StopMotion.set(74);
            CTS_Fear.set(75);
            CTS_EvanSlow.set(76);
            CTS_MagicShield.set(77);
            CTS_MagicResistance.set(78);
            CTS_SoulStone.set(79);
            CTS_Flying.set(80);
            CTS_Frozen.set(81);
            CTS_AssistCharge.set(82);
            CTS_Enrage.set(83);
            CTS_SuddenDeath.set(84);
            CTS_NotDamaged.set(85);
            CTS_FinalCut.set(86);
            CTS_ThornsEffect.set(87);
            CTS_SwallowAttackDamage.set(88);
            CTS_MorewildDamageUp.set(89);
            CTS_Mine.set(90);
            CTS_EMHP.set(91);
            CTS_EMMP.set(92);
            CTS_EPAD.set(93);
            CTS_EPDD.set(94);
            CTS_EMDD.set(95);
            CTS_Guard.set(96);
            CTS_SafetyDamage.set(97);
            CTS_SafetyAbsorb.set(98);
            CTS_Cyclone.set(99);
            CTS_SwallowCritical.set(100);
            CTS_SwallowMaxMP.set(101);
            CTS_SwallowDefence.set(102);
            CTS_SwallowEvasion.set(103);
            CTS_Conversion.set(104);
            CTS_Revive.set(105);
            CTS_Sneak.set(106);
            CTS_Mechanic.set(107);
            CTS_Aura.set(108);
            CTS_DarkAura.set(109);
            CTS_BlueAura.set(110);
            CTS_YellowAura.set(111);
            CTS_SuperBody.set(112);
            CTS_MorewildMaxHP.set(113);
            CTS_Dice.set(114);
            CTS_BlessingArmor.set(115);
            CTS_DamR.set(116);
            CTS_TeleportMasteryOn.set(117);
            CTS_CombatOrders.set(118);
            CTS_Beholder.set(119);
            // 120
            // 121
            // 122
            CTS_EnergyCharged.set(123);
            CTS_Dash_Speed.set(124);
            CTS_Dash_Jump.set(125);
            CTS_RideVehicle.set(126);
            CTS_PartyBooster.set(127);
            CTS_GuidedBullet.set(128);
            // 129 = battle mage aura.
            return;
        }
        // UINT128
        if (Version.GreaterOrEqual(Region.JMS, 186)) {
            clear();
            CTS_PAD.set(0);
            CTS_PDD.set(1);
            CTS_MAD.set(2);
            CTS_MDD.set(3);
            CTS_ACC.set(4);
            CTS_EVA.set(5);
            CTS_Craft.set(6);
            CTS_Speed.set(7);
            CTS_Jump.set(8);
            CTS_MagicGuard.set(9);
            CTS_DarkSight.set(10);
            CTS_Booster.set(11);
            CTS_PowerGuard.set(12);
            CTS_MaxHP.set(13);
            CTS_MaxMP.set(14);
            CTS_Invincible.set(15);
            CTS_SoulArrow.set(16);
            CTS_Stun.set(17);
            CTS_Poison.set(18);
            CTS_Seal.set(19);
            CTS_Darkness.set(20);
            CTS_ComboCounter.set(21);
            CTS_WeaponCharge.set(22);
            CTS_DragonBlood.set(23);
            CTS_HolySymbol.set(24);
            CTS_MesoUp.set(25);
            CTS_ShadowPartner.set(26);
            CTS_PickPocket.set(27);
            CTS_MesoGuard.set(28);
            CTS_Thaw.set(29);
            CTS_Weakness.set(30);
            CTS_Curse.set(31);
            CTS_Slow.set(32);
            CTS_Morph.set(33);
            CTS_Regen.set(34);
            CTS_BasicStatUp.set(35);
            CTS_Stance.set(36);
            CTS_SharpEyes.set(37);
            CTS_ManaReflection.set(38);
            CTS_Attract.set(39);
            CTS_SpiritJavelin.set(40);
            CTS_Infinity.set(41);
            CTS_Holyshield.set(42);
            CTS_HamString.set(43);
            CTS_Blind.set(44);
            CTS_Concentration.set(45);
            CTS_BanMap.set(46);
            CTS_MaxLevelBuff.set(47);
            CTS_MesoUpByItem.set(48);
            CTS_Ghost.set(49);
            CTS_Barrier.set(50);
            CTS_ReverseInput.set(51); // OK
            /*
            CTS_ItemUpByItem.set(0);
            CTS_RespectPImmune.set(0);
            CTS_RespectMImmune.set(0);
            CTS_DefenseAtt.set(0);
            CTS_DefenseState.set(0);
            CTS_IncEffectHPPotion.set(0);
            CTS_IncEffectMPPotion.set(0);
             */
            CTS_DojangBerserk.set(57); // -2
            CTS_DojangInvincible.set(58);
            CTS_Spark.set(59);
            CTS_DojangShield.set(60);
            CTS_SoulMasterFinal.set(61);
            CTS_WindBreakerFinal.set(62);
            CTS_ElementalReset.set(63);
            CTS_WindWalk.set(64);
            CTS_EventRate.set(65);
            CTS_ComboAbilityBuff.set(66);
            CTS_ComboDrain.set(67);
            CTS_ComboBarrier.set(68);
            CTS_BodyPressure.set(69);
            CTS_SmartKnockback.set(70);
            CTS_RepeatEffect.set(71);
            CTS_ExpBuffRate.set(72);
            CTS_StopPortion.set(73);
            CTS_StopMotion.set(74);
            CTS_Fear.set(75);
            CTS_EvanSlow.set(76);
            CTS_MagicShield.set(77);
            CTS_MagicResistance.set(78);
            CTS_SoulStone.set(79);
            CTS_Flying.set(80);
            CTS_Frozen.set(81);
            CTS_AssistCharge.set(82);
            CTS_Enrage.set(83);
            CTS_SuddenDeath.set(84);
            CTS_NotDamaged.set(85);
            CTS_FinalCut.set(86);
            CTS_ThornsEffect.set(87);
            CTS_SwallowAttackDamage.set(88);
            CTS_MorewildDamageUp.set(89);
            CTS_Mine.set(90);
            CTS_EMHP.set(91);
            CTS_EMMP.set(92);
            CTS_EPAD.set(93);
            CTS_EPDD.set(94);
            CTS_EMDD.set(95);
            CTS_Guard.set(96);
            CTS_SafetyDamage.set(97);
            CTS_SafetyAbsorb.set(98);
            CTS_Cyclone.set(99);
            CTS_SwallowCritical.set(100);
            CTS_SwallowMaxMP.set(101);
            CTS_SwallowDefence.set(102);
            CTS_SwallowEvasion.set(103);
            CTS_Conversion.set(104);
            CTS_Revive.set(105);
            CTS_Sneak.set(106); // OK
            //
            CTS_DarkAura.set(109);
            CTS_BlueAura.set(110);
            CTS_YellowAura.set(111); // broken?
            //
            CTS_EnergyCharged.set(113); // OK
            CTS_Dash_Speed.set(114);
            CTS_Dash_Jump.set(115);
            CTS_RideVehicle.set(116);
            CTS_PartyBooster.set(117);
            CTS_GuidedBullet.set(118);
            // 119 last.
            return;
        }
    }
}
