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

/**
 *
 * @author Riremito
 */
public enum OpsCommodity implements IPacketOps {
    CM_ITEMID(1), // 0x01
    CM_COUNT(1 << 1), // 0x02
    CM_PRICE(1 << 2), // 0x04
    CM_BONUS(1 << 3), // 0x08
    CM_PRIORITY(1 << 4), // 0x10
    CM_PERIOD(1 << 5), // 0x20
    CM_MAPLEPOINT(1 << 6), // 0x40
    CM_MESO(1 << 7), // 0x80
    CM_FORPREMIUMUSER(1 << 8), // 0x100
    CM_COMMODITYGENDER(1 << 9), // 0x200
    CM_ONSALE(1 << 10), // 0x400
    CM_CLASS(1 << 11), // 0x800
    CM_LIMIT(1 << 12), // 0x1000
    CM_PBCASH(1 << 13), // 0x2000
    CM_PBPOINT(1 << 14), // 0x4000
    CM_PBGIFT(1 << 15), // 0x8000
    CM_PACKAGESN(1 << 16), // 0x10000
    CM_REQPOP(1 << 17), // 0x20000
    CM_REQLEV(1 << 18), // 0x40000
    CM_ALL(0x7FFFF),
    UNKNOWN(-1);

    private int value;

    OpsCommodity(int val) {
        this.value = val;
    }

    OpsCommodity() {
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

    public static OpsCommodity find(int val) {
        for (final OpsCommodity ops : OpsCommodity.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

}
