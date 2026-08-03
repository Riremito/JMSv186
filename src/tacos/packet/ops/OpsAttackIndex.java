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

import tacos.config.Config;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public enum OpsAttackIndex implements IPacketOps {
    AttackIndex_Mob_Physical(0),
    AttackIndex_Mob_Magic(-1),
    AttackIndex_Counter(-2),
    AttackIndex_Obstacle(-3),
    AttackIndex_Stat(-4),
    UNKNOWN(-999);

    private int value;

    OpsAttackIndex(int val) {
        this.value = val;
    }

    OpsAttackIndex() {
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

    public static OpsAttackIndex find(int val) {
        for (OpsAttackIndex ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Config.GreaterOrEqual(Region.JMS, 147)) {
            AttackIndex_Mob_Magic.set(0);
            AttackIndex_Mob_Physical.set(-1);
            AttackIndex_Obstacle.set(-2);
            AttackIndex_Stat.set(-3);
            AttackIndex_Counter.set(-1000);
        }
        if (Config.GreaterOrEqual(Region.JMS, 187)) {
            AttackIndex_Mob_Magic.set(0);
            AttackIndex_Mob_Physical.set(-1);
            AttackIndex_Counter.set(-2);
            AttackIndex_Obstacle.set(-3);
            AttackIndex_Stat.set(-4);
        }
    }
}
