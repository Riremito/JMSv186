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
    AUTO(0),
    STR(1), // 0x1
    DEX(1 << 1), // 0x2
    INT(1 << 2), // 0x4
    LUK(1 << 3), // 0x8
    PAD(1 << 4), // 0x10
    PDD(1 << 5), // 0x20
    MAD(1 << 6), // 0x40
    MDD(1 << 7), // 0x80
    ACC(1 << 8), // 0x100
    EVA(1 << 9), // 0x200
    SPEED(1 << 10), // 0x400
    JUMP(1 << 11), // 0x800
    SPEEDMAX(1 << 12), // 0x1000
    ALL((1 << 13) - 1), // 0x1FFF
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
        for (OpsForcedStat ops : OpsForcedStat.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
