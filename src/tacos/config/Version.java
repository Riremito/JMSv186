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

    // good versions
    public static boolean GreaterOrEqual(Region region, int version) {
        if (region == Region.GMS && version <= 95) {
            if (Region.GMST.check()) {
                return true;
            }
        }
        if (region.check()) {
            if (version <= Config.VERSION) {
                return true;
            }
        }
        return false;
    }

    // bad versions
    public static boolean Between(Region region, int version_l, int version_r) {
        if (region.check()) {
            if (version_l <= Config.VERSION && Config.VERSION <= version_r) {
                return true;
            }
        }
        return false;
    }

    // really bad version
    public static boolean Equal(Region region, int version) {
        if (region.check()) {
            if (Config.VERSION == version) {
                return true;
            }
        }
        return false;
    }

    // pre-bb older versions
    public static boolean LessOrEqual(Region region, int version) {
        if (region == Region.GMS && version == 95) {
            if (Region.GMST.check()) {
                return true;
            }
        }
        if (region.check()) {
            if (Config.VERSION <= version) {
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
