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
public enum OpsTemporaryStat {
    TS_ENERGY_CHARGED(0),
    TS_DASH_SPEED(1),
    TS_DASH_JUMP(2),
    TS_RIDE_VEHICLE(3),
    TS_PARTY_BOOSTER(4),
    TS_GUIDED_BULLET(5),
    TS_UNDEAD(6),
    _TS_NO(7),
    UNKNOWN(-1);

    int value;

    OpsTemporaryStat(int val) {
        this.value = val;
    }

    OpsTemporaryStat() {
        this.value = -1;
    }

    public int get() {
        return this.value;
    }
}
