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
package config;

/**
 *
 * @author Riremito
 */
public enum DeveloperMode {
    // Developer Mode
    DM_NO_XML,
    DM_FIRST_MAP_ID(0),
    DM_ERROR_MAP_ID(800000000),
    DM_CHECK_DAMAGE,
    DM_SKILL_COOL_TIME(3000),
    DM_DEBUG_LOG,
    DM_ADMIN_LOG,
    DM_GM_ACCOUNT,
    DM_FULL_ITEM_SET,
    DM_ADMIN_TOOL,
    // TODO : move
    DM_CODEPAGE_UTF8(false),
    UNKNOWN;
    private int value;

    DeveloperMode(boolean val) {
        set(val);
    }

    DeveloperMode(int val) {
        this.value = val;
    }

    DeveloperMode() {
        this.value = 0;
    }

    public boolean get() {
        return (this.value != 0);
    }

    public int getInt() {
        return this.value;
    }

    public void set(boolean val) {
        this.value = val ? 1 : 0;
    }

    public void setInt(int val) {
        this.value = val;
    }

}
