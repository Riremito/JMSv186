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

/**
 *
 * @author Riremito
 */
public class Property_Dummy_World {

    private static int channels;
    private static int port_default;
    private static int flags;
    private static String name;
    private static String message;
    private static String event;

    public static int getChannels() {
        return channels;
    }

    public static int getPort() {
        return port_default;
    }

    public static int getFlags() {
        return flags;
    }

    public static String getName() {
        return name;
    }

    public static String getMessage() {
        return message;
    }

    public static String getEvent() {
        return event;
    }

    public static boolean init() {
        Property conf = new Property("properties/momiji.properties");
        if (!conf.open()) {
            return false;
        }
        channels = conf.getInt("server.channels");
        port_default = conf.getInt("server.port");
        flags = conf.getInt("server.flags");
        name = conf.get("server.name");
        message = conf.get("server.message");
        event = conf.get("server.event");
        return true;
    }
}
