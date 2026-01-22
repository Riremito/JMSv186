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
package tacos.database;

/**
 *
 * @author Riremito
 */
public enum LazyDataNames {
    PET_ITEM_HP("pet_hp", LazyDataTypes.TYPE_INT),
    PET_ITEM_MP("pet_mp", LazyDataTypes.TYPE_INT),
    PET_ITEM_CURE("pet_cure", LazyDataTypes.TYPE_INT),
    UNKNOWN;

    private String name;
    private LazyDataTypes type;

    LazyDataNames() {
        type = LazyDataTypes.UNKNOWN;
    }

    LazyDataNames(String name, LazyDataTypes type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public LazyDataTypes getType() {
        return this.type;
    }

}
