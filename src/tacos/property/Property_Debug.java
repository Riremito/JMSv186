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
package tacos.property;

import tacos.config.DeveloperMode;
import tacos.config.Region;

/**
 *
 * @author Riremito
 */
public class Property_Debug {

    public static boolean init() {
        Property conf = new Property("properties/test.properties");
        if (!conf.open()) {
            return false;
        }

        DeveloperMode.DM_FIRST_MAP_ID.setInt(Region.check(Region.KMSB) ? 100000000 : conf.getInt("config.first_mapid"));
        DeveloperMode.DM_ERROR_MAP_ID.setInt(conf.getInt("config.error_mapid"));
        // debug
        DeveloperMode.DM_LOG_DEBUG.set(conf.getBoolean("debug.show_debug_log"));
        DeveloperMode.DM_LOG_WZ.set(conf.getBoolean("debug.show_xml_log"));
        DeveloperMode.DM_LOG_ADMIN.set(conf.getBoolean("debug.show_admin_log"));
        DeveloperMode.DM_GM_ACCOUNT.set(conf.getBoolean("debug.gm_mode"));
        DeveloperMode.DM_FULL_ITEM_SET.set(conf.getBoolean("debug.starter_set"));
        DeveloperMode.DM_ADMIN_TOOL.set(conf.getBoolean("debug.admin_ui"));
        // TODO : move
        DeveloperMode.DM_CODEPAGE_UTF8.set(conf.getBoolean("codepage.use_utf8"));
        return true;
    }
}
