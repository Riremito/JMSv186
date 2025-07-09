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
public enum OpsMoveAbility {
    MOVEABILITY_STOP(0),
    MOVEABILITY_WALK(1),
    MOVEABILITY_WALK_RANDOM(2),
    MOVEABILITY_JUMP(3),
    MOVEABILITY_FLY(4),
    MOVEABILITY_FLY_RANDOM(5),
    MOVEABILITY_ESCORT(6);

    int value;

    OpsMoveAbility(int val) {
        value = val;
    }

    OpsMoveAbility() {
        value = -1;
    }

    public int get() {
        return value;
    }

    public void set(int val) {
        value = val;
    }

    public static void init() {
        // SummonMovementType
    }
}
