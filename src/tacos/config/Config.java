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

    public static boolean JMS146orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Config.REGION) {
            case JMS: {
                if (146 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }

            case KMS: {
                if (47 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (62 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (73 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (61 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case BMS: {
                if (24 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                if (35 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean JMS147orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Config.REGION) {
            case JMS: {
                if (147 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (48 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (63 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (74 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (62 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (0 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // only 5 jobs
    // シグナス実装前まではほぼ変わらないはずなのでバージョンの誤差は多少あっても問題ない
    public static boolean JMS164orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Config.REGION) {
            case JMS: {
                if (164 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                // v2.66
                if (65 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (73 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (94 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (72 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (54 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                return false;
            }
        }
        return false;
    }

    // Knights of Cygnus update
    public static boolean JMS165orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Config.REGION) {
            case JMS: {
                if (165 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (67 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (74 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (96 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (73 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (55 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case VMS: {
                return false;
            }
        }
        return false;
    }

    // stable pre bb
    public static boolean JMS180orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Config.REGION) {
            case JMS: {
                if (180 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (92 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (85 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (121 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (91 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (70 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // pre bb with potential
    public static boolean JMS186orLater() {
        if (Version.PostBB()) {
            return true;
        }

        switch (Config.REGION) {
            case JMS: {
                if (186 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case CMS: {
                if (85 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case TWMS: {
                if (121 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case THMS: {
                if (87 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case GMS: {
                if (91 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (70 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case MSEA: {
                if (100 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    // near Chaos update
    public static boolean JMS194orLater() {
        if (!Version.PostBB()) {
            return false;
        }

        switch (Config.REGION) {
            case JMS: {
                if (194 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case JMST: {
                if (110 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMS: {
                if (114 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case KMST: {
                if (391 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            case EMS: {
                if (76 <= Version.getVersion()) {
                    return true;
                }
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }
}
