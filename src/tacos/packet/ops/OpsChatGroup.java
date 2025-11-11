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
public enum OpsChatGroup implements IPacketOps {
    CG_Friend(0),
    CG_Party(1),
    CG_Guild(2),
    CG_Alliance(3), // JMS147 OK
    CG_Couple(-1),
    CG_ToCouple(-1),
    CG_Expedition(4), // JMS186-302 OK
    UNKNOWN(-1);

    private int value;

    OpsChatGroup(int val) {
        this.value = val;
    }

    OpsChatGroup() {
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

    public static OpsChatGroup find(int val) {
        for (final OpsChatGroup ops : OpsChatGroup.values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {

    }

}
