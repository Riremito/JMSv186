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
package config.property;

import config.Region;
import config.Version;

/**
 *
 * @author Riremito
 */
public class Property_Database {

    private static String url;
    private static String user;
    private static String password;

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }

    public static String getPassword() {
        return password;
    }

    public static boolean init() {
        Property conf = new Property("properties/database.properties");
        if (!conf.open()) {
            return false;
        }
        // jdbc:mysql://127.0.0.1:3306/jms_v186?autoReconnect=true&characterEncoding=utf8
        url = conf.get("database.url");
        if (url.isEmpty()) {
            String database_host = conf.get("database.host");
            String database_port = conf.get("database.port");
            url = "jdbc:mysql://" + database_host + ":" + database_port + "/" + Region.GetRegionName() + "_v" + Version.getVersion() + "?autoReconnect=true&characterEncoding=utf8";
        }
        user = conf.get("database.user");
        password = conf.get("database.password");
        return true;
    }
}
