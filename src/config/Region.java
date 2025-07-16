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

    private static Region region = Region.JMS;

    public static Region getRegion() {
        return region;
    }

    public static int getRegionNumber() {
        return region.get();
    }

    public static String GetRegionName() {
        return "" + region;
    }

    public static boolean check(Region r) {
        if (region == r) {
            return true;
        }
        return false;
    }

    public static boolean check(Region[] regions) {
        for (Region r : regions) {
            if (region == r) {
                return true;
            }
        }
        return false;
    }

    public static boolean setRegion(String region_name) {
        switch (region_name) {
            case "KMS": {
                region = KMS;
                return true;
            }
            case "KMST": {
                region = KMST;
                return true;
            }
            case "JMS": {
                region = JMS;
                return true;
            }
            case "JMST": {
                region = JMST;
                return true;
            }
            case "CMS": {
                region = CMS;
                return true;
            }
            case "TWMS": {
                region = TWMS;
                return true;
            }
            case "THMS": {
                region = THMS;
                return true;
            }
            case "MSEA": {
                region = MSEA;
                return true;
            }
            case "VMS": {
                region = VMS;
                return true;
            }
            case "GMS": {
                region = GMS;
                return true;
            }
            case "EMS": {
                region = EMS;
                return true;
            }
            case "BMS": {
                region = BMS;
                return true;
            }
            case "IMS": {
                region = IMS;
                return true;
            }
            default: {
                break;
            }
        }

        region = unk;
        return false;
    }

    // TODO : replace
    public static boolean IsEMS() {
        return Region.getRegion() == Region.EMS;
    }

    public static boolean IsKMS() {
        return Region.getRegion() == Region.KMS || Region.getRegion() == Region.KMST;
    }

    public static boolean IsMSEA() {
        return Region.getRegion() == Region.MSEA;
    }

    public static boolean IsCMS() {
        return Region.getRegion() == Region.CMS;
    }

    public static boolean IsJMS() {
        return Region.getRegion() == Region.JMS || Region.getRegion() == Region.JMST;
    }

    public static boolean IsBMS() {
        return Region.getRegion() == Region.BMS;
    }

    public static boolean IsIMS() {
        return Region.getRegion() == Region.IMS;
    }

    public static boolean IsTWMS() {
        return Region.getRegion() == Region.TWMS;
    }

    public static boolean IsTHMS() {
        return Region.getRegion() == Region.THMS;
    }

    public static boolean IsGMS() {
        return Region.getRegion() == Region.GMS;
    }

    public static boolean IsVMS() {
        return Region.getRegion() == Region.VMS;
    }

}
