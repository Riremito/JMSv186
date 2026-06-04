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
    CTS_EMHP,
    CTS_EMMP,
    CTS_EPAD,
    CTS_EPDD,
    CTS_EMDD,
    CTS_MagicGuard(9),
    CTS_DarkSight(10),
    CTS_Booster(11),
    CTS_PowerGuard(12),
    CTS_Guard,
    CTS_SafetyDamage,
    CTS_SafetyAbsorb,
    CTS_MaxHP(13),
    CTS_MaxMP(14),
    CTS_Invincible(15),
    CTS_SoulArrow(16),
    CTS_Stun(17),
    CTS_Poison(18),
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
    CTS_Thaw(32),
    CTS_Weakness,
    CTS_Curse(33),
    CTS_Slow,
    CTS_Morph,
    CTS_Ghost,
    CTS_Regen(34),
    CTS_BasicStatUp(35),
    CTS_Stance(36),
    CTS_SharpEyes(37),
    CTS_ManaReflection(38),
    CTS_Attract(39),
    CTS_SpiritJavelin(40),
    CTS_Infinity(41),
    CTS_Holyshield(42),
    CTS_HamString(43),
    CTS_Blind(44),
    CTS_Concentration(45),
    CTS_BanMap(46),
    CTS_MaxLevelBuff(47),
    CTS_Barrier(48),
    CTS_DojangShield(49),
    CTS_ReverseInput(50),
    CTS_MesoUpByItem,
    CTS_ItemUpByItem,
    CTS_RespectPImmune,
    CTS_RespectMImmune,
    CTS_DefenseAtt,
    CTS_DefenseState,
    CTS_DojangBerserk,
    CTS_DojangInvincible,
    CTS_Spark,
    CTS_SoulMasterFinal,
    CTS_WindBreakerFinal,
    CTS_ElementalReset,
    CTS_WindWalk,
    CTS_EventRate,
    CTS_ComboAbilityBuff,
    CTS_ComboDrain,
    CTS_ComboBarrier,
    CTS_BodyPressure,
    CTS_SmartKnockback,
    CTS_RepeatEffect,
    CTS_ExpBuffRate,
    CTS_StopPortion,
    CTS_StopMotion,
    CTS_Fear,
    CTS_EvanSlow,
    CTS_MagicShield,
    CTS_MagicResistance,
    CTS_SoulStone,
    CTS_Flying,
    CTS_Frozen,
    CTS_AssistCharge,
    CTS_Enrage,
    CTS_SuddenDeath,
    CTS_NotDamaged,
    CTS_FinalCut,
    CTS_ThornsEffect,
    CTS_SwallowAttackDamage,
    CTS_MorewildDamageUp,
    CTS_Mine,
    CTS_Cyclone,
    CTS_SwallowCritical,
    CTS_SwallowMaxMP,
    CTS_SwallowDefence,
    CTS_SwallowEvasion,
    CTS_Conversion,
    CTS_Revive,
    CTS_Sneak,
    CTS_Mechanic,
    CTS_Aura,
    CTS_DarkAura,
    CTS_BlueAura,
    CTS_YellowAura,
    CTS_SuperBody,
    CTS_MorewildMaxHP,
    CTS_Dice,
    CTS_BlessingArmor,
    CTS_DamR,
    CTS_TeleportMasteryOn,
    CTS_InfightingMastery,
    CTS_CombatOrders,
    CTS_Beholder,
    CTS_SwallowBuff,
    UNKNOWN;

    private int value;

    OpsSecondaryStat(int value) {
        this.value = value;
    }

    OpsSecondaryStat() {
        this.value = -1;
    }

    @Override
    public int get() {
        return value;
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

    public static void clear() {
        for (OpsSecondaryStat ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
    }
}
