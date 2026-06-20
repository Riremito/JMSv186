/*
 * Copyright (C) 2026 Riremito
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
public enum OpsMobStat implements IPacketOps {
    MS_PAD(0),
    MS_PDR(1),
    MS_MAD(2),
    MS_MDR(3),
    MS_ACC(4),
    MS_EVA(5),
    MS_Speed(6),
    MS_Stun(7),
    MS_Freeze(8),
    MS_Poison(9),
    MS_Seal(10),
    MS_Darkness(11),
    MS_PowerUp(12),
    MS_MagicUp(13),
    MS_PGuardUp(14),
    MS_MGuardUp(15),
    MS_Doom(16),
    MS_Web(17),
    MS_PImmune(18),
    MS_MImmune(19),
    MS_Showdown(20),
    MS_HardSkin(21),
    MS_Ambush(22),
    MS_DamagedElemAttr(23),
    MS_Venom(24),
    MS_Blind(25),
    MS_SealSkill(26),
    MS_Burned(27),
    MS_Dazzle(28),
    MS_PCounter(29),
    MS_MCounter(30),
    MS_Disable(31),
    MS_RiseByToss(32),
    MS_BodyPressure(33),
    MS_Weakness(34),
    MS_TimeBomb(35),
    MS_MagicCrash(36),
    MS_HealByDamage(37),
    UNKNOWN;
    private int value;

    OpsMobStat(int value) {
        this.value = value;
    }

    OpsMobStat() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
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

    public static OpsMobStat find(int val) {
        for (final OpsMobStat ops : OpsMobStat.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void clear() {
        for (OpsMobStat ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        // UINT160
        if (Version.GreaterOrEqual(Region.JMS, 187)) {
            clear();
        }
    }
}
