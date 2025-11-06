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
    DM_NO_ALIVE_CHECK(true),
    DM_FIRST_MAP_ID(0),
    DM_ERROR_MAP_ID(800000000),
    DM_CHECK_DAMAGE,
    DM_SKILL_COOL_TIME(3000),
    DM_LOG_DEV,
    DM_LOG_DEBUG,
    DM_LOG_INFO(true),
    DM_LOG_SETUP(true),
    DM_LOG_NETWORK(false),
    DM_LOG_WZ,
    DM_LOG_ADMIN,
    DM_GM_ACCOUNT,
    DM_FULL_ITEM_SET,
    DM_ADMIN_TOOL,
    // TODO : move
    DM_CODEPAGE_UTF8(false),
    DM_INV_SLOT_EQUIP(72),
    DM_INV_SLOT_USE(72),
    DM_INV_SLOT_ETC(24),
    DM_INV_SLOT_SETUP(24),
    DM_INV_SLOT_CASH(96),
    DM_INV_SLOT_STORAGE(4), // not used.
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
