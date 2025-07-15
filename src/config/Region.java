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
public enum Region {
    KMS(1),
    KMST(2),
    JMS(3),
    JMST(3),
    CMS(4),
    TWMS(6),
    THMS(7),
    MSEA(7),
    VMS(7),
    GMS(8),
    EMS(9),
    BMS(9),
    IMS(1),
    unk;

    private int value = 3;

    Region(int val) {
        this.value = val;
    }

    Region() {
        this.value = 0;
    }

    public int get() {
        return this.value;
    }

    private static Region region_type = Region.JMS;

    public static Region getRegion() {
        return region_type;
    }

    public static int getRegionNumber() {
        return region_type.get();
    }

    public static boolean setRegion(String region_name) {
        switch (region_name) {
            case "KMS": {
                region_type = KMS;
                return true;
            }
            case "KMST": {
                region_type = KMST;
                return true;
            }
            case "JMS": {
                region_type = JMS;
                return true;
            }
            case "JMST": {
                region_type = JMST;
                return true;
            }
            case "CMS": {
                region_type = CMS;
                return true;
            }
            case "TWMS": {
                region_type = TWMS;
                return true;
            }
            case "THMS": {
                region_type = THMS;
                return true;
            }
            case "MSEA": {
                region_type = MSEA;
                return true;
            }
            case "VMS": {
                region_type = VMS;
                return true;
            }
            case "GMS": {
                region_type = GMS;
                return true;
            }
            case "EMS": {
                region_type = EMS;
                return true;
            }
            case "BMS": {
                region_type = BMS;
                return true;
            }
            case "IMS": {
                region_type = IMS;
                return true;
            }
            default: {
                break;
            }
        }

        region_type = unk;
        return false;
    }

    public static String GetRegionName() {
        return "" + region_type;
    }
}
