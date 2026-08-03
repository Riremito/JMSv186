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

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsMoveAbility implements IPacketOps {
    MOVEABILITY_STOP(0),
    MOVEABILITY_WALK(1),
    MOVEABILITY_WALK_RANDOM(2),
    MOVEABILITY_JUMP(3),
    MOVEABILITY_FLY(4),
    MOVEABILITY_FLY_RANDOM(5),
    MOVEABILITY_ESCORT(6),
    UNKNOWN;

    private int value;

    OpsMoveAbility(int val) {
        this.value = val;
    }

    OpsMoveAbility() {
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

    public static void clear() {
        for (OpsMoveAbility ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
        if (Config.PostBB()) {
            return;
        }
        if (Config.LessOrEqual(Region.JMS, 147)) {
            clear();
            MOVEABILITY_STOP.set(0);
            MOVEABILITY_WALK.set(1);
            MOVEABILITY_JUMP.set(2);
            MOVEABILITY_FLY.set(3);
            MOVEABILITY_FLY_RANDOM.set(4);
        }
    }
}
