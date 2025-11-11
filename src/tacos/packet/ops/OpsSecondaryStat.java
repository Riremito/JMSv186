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
import tacos.config.ServerConfig;
import tacos.config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsSecondaryStat {
    // JMS v186
    // 0, 0x00000001
    CTS_PAD(0, 0),
    // 0, 0x00000002
    CTS_PDD(0, 1),
    // 0, 0x0000004
    CTS_MAD(0, 2),
    // 0, 0x00000008
    CTS_MDD(0, 3),
    // 0, 0x00000010
    CTS_ACC(0, 4),
    // 0, 0x00000020
    CTS_EVA(0, 5),
    // 0, 0x00000040
    CTS_Craft(0, 6),
    // 0, 0x00000080
    CTS_Speed(0, 7),
    // 0, 0x00000100
    CTS_Jump(0, 8),
    // 0, 0x00000200
    CTS_MagicGuard(0, 9),
    // 0, 0x00000400
    CTS_DarkSight(0, 10),
    // 0, 0x00000800
    CTS_Booster(0, 11),
    // 0, 0x00001000
    CTS_PowerGuard(0, 12),
    // 0, 0x00002000
    CTS_MaxHP(0, 13),
    // 0, 0x00004000
    CTS_MaxMP(0, 14),
    // 0, 0x00008000
    CTS_Invincible(0, 15),
    // 0, 0x00010000
    CTS_SoulArrow(0, 16),
    // 0, 0x00200000
    CTS_ComboCounter(0, 21),
    // 0, 0x02000000
    CTS_MesoUp(0, 25),
    // 0, 0x04000000
    CTS_ShadowPartner(0, 26),
    // 0, 0x08000000
    CTS_PickPocket(0, 27), // odin puppet
    // 0, 0x10000000
    CTS_MesoGuard(0, 28),
    // 1, 0x00000001
    CTS_Thaw(1, 0),
    // 1, 0x00000002
    CTS_Curse(1, 1),
    // 1, 0x00000004
    CTS_Regen(1, 2),
    // 1, 0x00000008
    CTS_BasicStatUp(1, 3),
    // 1, 0x00000010
    CTS_Stance(1, 4),
    // 1, 0x00000020
    CTS_SharpEyes(1, 5),
    // 1, 0x00000040
    CTS_ManaReflection(1, 6),
    // 1, 0x00000100
    CTS_SpiritJavelin(1, 8),
    // 1, 0x00000200
    CTS_Infinity(1, 9),
    // 1, 0x00000400
    CTS_Holyshield(1, 10),
    // 1, 0x00000800
    CTS_HamString(1, 11),
    // 1, 0x00001000
    CTS_Blind(1, 12),
    // 1, 0x00002000
    CTS_Concentration(1, 13),
    //CTS_BanMap(1, 13),
    //CTS_MaxLevelBuff(1, 14),
    //CTS_Barrier(1, 15),
    //CTS_DojangShield(1, 16),
    //CTS_ReverseInput(1, 17),
    // 2, 0x00000000
    // 3, 0x00000000
    // 4, 0x00000000
    UNKNOWN(0, -1);

    private int order;
    private int mask;

    OpsSecondaryStat(int order, int mask) {
        this.order = order;
        this.mask = mask;
    }

    OpsSecondaryStat() {
        mask = -1;
    }

    public void set(int order, int mask) {
        this.order = order;
        this.mask = mask;
    }

    public int get() {
        return mask;
    }

    public int getN() {
        return order;
    }

    public static void init() {
        if (Version.LessOrEqual(Region.JMS, 131)) {
            return;
        }
        if (ServerConfig.JMS194orLater()) {
            // fix
            return;
        }

        if (Version.LessOrEqual(Region.JMS, 131)) {
            // fixs
            return;
        }
    }
}
