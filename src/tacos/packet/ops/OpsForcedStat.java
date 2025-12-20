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
public enum OpsForcedStat implements IPacketOps {
    AUTO(0x0),
    STR(0x1),
    DEX(0x2),
    INT(0x4),
    LUK(0x8),
    PAD(0x10),
    PDD(0x20),
    MAD(0x40),
    MDD(0x80),
    ACC(0x100),
    EVA(0x200),
    SPEED(0x400),
    JUMP(0x800),
    SPEEDMAX(0x1000),
    ALL(0x1FFF),
    UNKNOWN;

    private int value;

    OpsForcedStat(int val) {
        this.value = val;
    }

    OpsForcedStat() {
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

    public static OpsForcedStat find(int val) {
        for (final OpsForcedStat ops : OpsForcedStat.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
