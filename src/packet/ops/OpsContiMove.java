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

/**
 *
 * @author Riremito
 */
public enum OpsContiMove implements IPacketOps {
    CONTI_DORMANT(0),
    CONTI_WAIT(1),
    CONTI_START(2),
    CONTI_MOVE(3),
    CONTI_MOBGEN(4),
    CONTI_MOBDESTROY(5),
    CONTI_END(6),
    CONTI_TARGET_STARTFIELD(7),
    CONTI_TARGET_START_SHIPMOVE_FIELD(8),
    CONTI_TARGET_WAITFIELD(9),
    CONTI_TARGET_MOVEFIELD(10),
    CONTI_TARGET_ENDFIELD(11),
    CONTI_TARGET_END_SHIPMOVE_FIELD(12),
    UNKNOWN(-1);

    private int value;

    OpsContiMove(int val) {
        this.value = val;
    }

    OpsContiMove() {
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

    public static void init() {

    }
}
