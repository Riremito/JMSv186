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
package odin.client;

/**
 *
 * @author Riremito
 */
public enum MapleClientState {
    LOGIN_NOTLOGGEDIN(0),
    LOGIN_SERVER_TRANSITION(1),
    LOGIN_LOGGEDIN(2),
    LOGIN_WAITING(3),
    CASH_SHOP_TRANSITION(4),
    LOGIN_CS_LOGGEDIN(5),
    CHANGE_CHANNEL(6),
    UNKNOWN;
    private int value = -1;

    MapleClientState(int val) {
        this.value = val;
    }

    MapleClientState() {
        this.value = -1;
    }

    public int get() {
        return this.value;
    }

    public static MapleClientState find(int val) {
        for (final MapleClientState o : MapleClientState.values()) {
            if (o.get() == val) {
                return o;
            }
        }
        return UNKNOWN;
    }
}
