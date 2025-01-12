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
package packet.ops;

import config.ServerConfig;

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
        if (ServerConfig.IsPostBB()) {
            CS_PETSN2.set(0x00100000);
            CS_PETSN3.set(0x00200000);
            CS_TEMPEXP.set(0x00400000);
        }
    }
}
