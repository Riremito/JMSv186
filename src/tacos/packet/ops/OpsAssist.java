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
public enum OpsAssist implements IPacketOps {
    ASSIST_NONE(0),
    ASSIST_ATTACK(1),
    ASSIST_HEAL(2),
    ASSIST_ATTACK_EX(3),
    ASSIST_SUMMON(4),
    ASSIST_ATTACK_MANUAL(5),
    ASSIST_ATTACK_COUNTER(6),
    UNKNOWN;

    private int value;

    OpsAssist(int val) {
        this.value = val;
    }

    OpsAssist() {
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
        for (OpsAssist ops : values()) {
            ops.set(UNKNOWN.get());
        }
    }

    public static void init() {
    }
}
