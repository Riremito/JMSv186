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
package tacos.config;

/**
 *
 * @author Riremito
 */
public class Config {

    // server vesion.
    public static Region REGION = Region.JMS;
    public static int VERSION = 147;
    public static int VERSION_SUB = 0;

    public static boolean setVersion(String name, int version, int version_sub) {
        VERSION = version;
        VERSION_SUB = version_sub;
        REGION = Region.find(name);

        if (Region.UNKNOWN.check()) {
            return false;
        }

        return true;
    }
}
