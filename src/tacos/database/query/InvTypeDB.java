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
package tacos.database.query;

/**
 *
 * @author Riremito
 */
public enum InvTypeDB {
    INVENTORY(0),
    STORAGE(1),
    UNKNOWN;

    private int value = -1;

    InvTypeDB() {

    }

    InvTypeDB(int value) {
        this.value = value;
    }

    public int get() {
        return this.value;
    }

}
