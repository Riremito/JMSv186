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
package tacos.config;

/**
 *
 * @author Riremito
 */
public enum ContentCustom {
    CC_WZ_MAP_ADDED,
    CC_EQUIP_STAT_RANDOMIZER,
    CC_MASTER_MONSTER_TIMER,
    UNKNOWN;

    int value;

    ContentCustom(boolean val) {
        set(val);
    }

    ContentCustom(int val) {
        setInt(val);
    }

    ContentCustom() {
        this.value = 0;
    }

    public boolean get() {
        return (this.value != 0);
    }

    public void set(boolean val) {
        this.value = val ? 1 : 0;
    }

    public int getInt() {
        return this.value;
    }

    public void setInt(int val) {
        this.value = val;
    }
}
