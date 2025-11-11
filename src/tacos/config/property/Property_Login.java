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
package tacos.config.property;

/**
 *
 * @author Riremito
 */
public class Property_Login {

    private static int port = 8484;
    private static int user_limit = 100;
    private static boolean anti_cheat = false;

    public static int getPort() {
        return port;
    }

    public static int getUserLimit() {
        return user_limit;
    }

    public static boolean getAntiCheat() {
        return anti_cheat;
    }

    public static boolean init() {
        Property conf = new Property("properties/login.properties");
        if (!conf.open()) {
            return false;
        }

        port = conf.getInt("server.port");
        user_limit = conf.getInt("server.userlimit");
        anti_cheat = conf.getBoolean("server.antihack");
        return true;
    }
}
