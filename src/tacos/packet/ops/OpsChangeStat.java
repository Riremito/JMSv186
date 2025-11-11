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
public enum OpsChangeStat {
    // JMS v186, GMS v95
    CS_SKIN(0x1),
    CS_FACE(0x2),
    CS_HAIR(0x4),
    CS_PETSN(0x8),
    CS_LEV(0x10),
    CS_JOB(0x20),
    CS_STR(0x40),
    CS_DEX(0x80),
    CS_INT(0x100),
    CS_LUK(0x200),
    CS_HP(0x400),
    CS_MHP(0x800),
    CS_MP(0x1000),
    CS_MMP(0x2000),
    CS_AP(0x4000),
    CS_SP(0x8000),
    CS_EXP(0x10000),
    CS_POP(0x20000),
    CS_MONEY(0x40000),
    CS_PETSN2(0x80000),
    CS_PETSN3(0x100000),
    CS_TEMPEXP(0x200000),
    UNKNOWN(-1);

    private int value;

    OpsChangeStat(int flag) {
        value = flag;
    }

    OpsChangeStat() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int val) {
        value = val;
    }

    public static OpsChangeStat find(int val) {
        for (final OpsChangeStat o : OpsChangeStat.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.PostBB()) {
            CS_PETSN2.set(0x00100000);
            CS_PETSN3.set(0x00200000);
            CS_TEMPEXP.set(0x00400000);
            return;
        }

        // JMS131 and 147+ is asme as JMS186
        if (ServerConfig.JMS147orLater()) {
            return;
        }
        // JMS146 only?
        if (Region.IsJMS() && ServerConfig.JMS146orLater()) {
            CS_SKIN.set(1);
            CS_FACE.set(1 << 1);
            CS_HAIR.set(1 << 2);
            CS_PETSN.set(1 << 3);
            CS_PETSN2.set(1 << 4);
            CS_PETSN3.set(1 << 5);
            CS_LEV.set(1 << 6);
            CS_JOB.set(1 << 7);
            CS_STR.set(1 << 8);
            CS_DEX.set(1 << 9);
            CS_INT.set(1 << 10);
            CS_LUK.set(1 << 11);
            CS_HP.set(1 << 12);
            CS_MHP.set(1 << 13);
            CS_MP.set(1 << 14);
            CS_MMP.set(1 << 15);
            CS_AP.set(1 << 16);
            CS_SP.set(1 << 17);
            CS_EXP.set(1 << 18);
            CS_POP.set(1 << 19);
            CS_MONEY.set(1 << 20);
            CS_TEMPEXP.set(1 << 21);
            return;
        }
    }
}
