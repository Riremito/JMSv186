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
public enum OpsCakePieEvent implements IPacketOps {

    CakePieEvent_Step_None(0),
    CakePieEvent_Step_Ready(1),
    CakePieEvent_Step_Event(2),
    CakePieEvent_Step_Winner(3),
    CakePieEvent_CakeTeam(1),
    CakePieEvent_PieTeam(2),
    UNKNOWN;

    private int value;

    OpsCakePieEvent(int val) {
        this.value = val;
    }

    OpsCakePieEvent() {
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
}
