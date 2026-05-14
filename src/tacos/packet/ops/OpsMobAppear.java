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
public enum OpsMobAppear implements IPacketOps {
    MOBAPPEAR_NORMAL(-1),
    MOBAPPEAR_REGEN(-2),
    MOBAPPEAR_REVIVED(-3),
    MOBAPPEAR_SUSPENDED(-4),
    MOBAPPEAR_DELAY(-5),
    MOBAPPEAR_EFFECT(0),
    UNKNOWN;

    private int value;

    OpsMobAppear(int val) {
        this.value = val;
    }

    OpsMobAppear() {
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

    public static OpsMobAppear find(int val) {
        for (OpsMobAppear ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }
}
