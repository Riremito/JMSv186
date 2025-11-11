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
public class Version {

    private static int version = 186;
    private static int version_sub = 1;

    public static int getVersion() {
        return version;
    }

    public static int getSubVersion() {
        return version_sub;
    }

    public static void setVersion(int ver_main, int ver_sub) {
        version = ver_main;
        version_sub = ver_sub;
    }

    // good versions
    public static boolean GreaterOrEqual(Region region, int version) {
        if (Region.check(region)) {
            if (version <= getVersion()) {
                return true;
            }
        }
        return false;
    }

    // bad versions
    public static boolean Between(Region region, int version_l, int version_r) {
        if (Region.check(region)) {
            if (version_l <= getVersion() && getVersion() <= version_r) {
                return true;
            }
        }
        return false;
    }

    // really bad version
    public static boolean Equal(Region region, int version) {
        if (Region.check(region)) {
            if (getVersion() == version) {
                return true;
            }
        }
        return false;
    }

    // pre-bb older versions
    public static boolean LessOrEqual(Region region, int version) {
        if (Region.check(region)) {
            if (getVersion() <= version) {
                return true;
            }
        }
        return false;
    }

    public static boolean PostBB() {
        return Content.BIGBANG.get();
    }

    public static boolean PreBB() {
        return !PostBB();
    }

}
