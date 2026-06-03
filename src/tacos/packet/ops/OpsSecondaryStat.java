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
    CTS_MagicGuard(9),
    CTS_DarkSight(10),
    CTS_Booster(11),
    CTS_PowerGuard(12),
    CTS_MaxHP(13),
    CTS_MaxMP(14),
    CTS_Invincible(15),
    CTS_SoulArrow(16),
    //
    CTS_ComboCounter(21),
    //
    CTS_MesoUp(25),
    CTS_ShadowPartner(26),
    CTS_PickPocket(27),
    CTS_MesoGuard(28),
    //
    CTS_Thaw(32),
    CTS_Curse(33),
    CTS_Regen(34),
    CTS_BasicStatUp(35),
    CTS_Stance(36),
    CTS_SharpEyes(37),
    CTS_ManaReflection(38),
    //
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
