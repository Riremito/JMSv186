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
package tacos.constants;

import tacos.packet.ops.OpsSkill;

/**
 *
 * @author Riremito
 */
public class TacosConstants {

    // server setup.
    public static final int SERVER_GLOBAL_IP_VALUE = 0x0100007F; // 127.0.0.1

    // game server info for GMS116 or later.
    public static final String FAKE_GLOBAL_IP_GMS116 = "8.31.98.52";
    public static final int FAKE_GLOBAL_IP_GMS116_VALUE = 0x34621F08; // 8.31.98.52

    // def
    public static final int DEFAULT_CHARSLOT = 6;
    public static final int DEFAULT_FORCED_RETURN_MAP_ID = 999999999;
    public static final int DEFAULT_RETURN_MAP_ID = 100000000;
    public static final int MAP_ID_PERION = 102000000;
    public static final int MAP_ID_FREE_MARKET = 910000000;
    public static final String PORTAL_FREE_MARKET = "out00";
    public static final int MAP_ID_MEISTER_VILLE = 910001000;
    public static final String PORTAL_MEISTER_VILLE = "st00";

    public static final int KANNA_SKILL_PET_ID = 40020109;

    public static boolean is_evan(int job_id, boolean dragon_job) {
        switch (job_id) {
            case 2001: // evan 0
            {
                if (dragon_job) {
                    return false;
                }
                return true;
            }
            case 2200: // evan 1
            case 2210: // evan 2
            case 2211: // evan 3
            case 2212: // evan 4
            case 2213: // evan 5
            case 2214: // evan 6
            case 2215: // evan 7
            case 2216: // evan 8
            case 2217: // evan 9
            case 2218: // evan 10
            {
                return true;
            }
            default: {
                break;
            }
        }

        return false;

    }

    public static boolean is_kanna(int job_id) {
        switch (job_id) {
            case 4002:
            case 4200:
            case 4210:
            case 4211:
            case 4212: {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean is_coconut(int map_id) {
        // 109080000
        if ((map_id / 10000) == 10908) {
            return true;
        }
        return false;
    }

    public static boolean is_bath(int map_id) {
        // 809000101, 809000201
        if ((map_id / 10000) == 80900) {
            return true;
        }
        return false;
    }

    public static final int MAP_ID_ZAKUM = 280030000;
    public static final int MAP_ID_HORNTAIL = 240060200;
    public static final int MAP_ID_PINKBEAN = 270050100;
    public static final int MAP_ID_CHAOS_ZAKUM = 280030001;
    public static final int MAP_ID_CHAOS_HORNTAIL = 240060201;
    public static final int MOB_ID_ZAKUM = 8800002;
    public static final int MOB_ID_HORNTAIL = 8810018;
    public static final int MOB_ID_PINKBEAN = 8820001;
    public static final int MOB_ID_CHAOS_ZAKUM = 8800102;
    public static final int MOB_ID_CHAOS_HORNTAIL = 8810122;

    public static boolean is_park_or_roppongi(int map_id) {
        // 802000300, 802000800
        if ((map_id / 100) == 8020003 || (map_id / 100) == 8020008) {
            return true;
        }
        return false;
    }

    public static boolean is_aran_tutorial(int map_id) {
        if (map_id == 914000000) {
            return true;
        }
        return false;
    }

    public static boolean is_keydown_skill(int skill_id) {
        if (is_keydown_skill_remote(skill_id)) {
            return true;
        }

        switch (OpsSkill.find(skill_id)) {
            case BOWMASTER_STORM_ARROW:
            case CROSSBOWMASTER_PIERCING:
            // pirate
            case INFIGHTER_SCREW_PUNCH:
            case GUNSLINGER_THROWING_BOMB:
            case CAPTAIN_RAPID_FIRE:
            // cygnus
            case NIGHTWALKER_POISON_BOMB:
            case STRIKER_SCREW_PUNCH:
            // dual blade
            case DUAL5_FINAL_CUT:
            case DUAL5_MONSTER_BOMB:
            // resistance
            case WINDBREAKER_STORM_ARROW:
            case WILDHUNTER_WILD_SHOOT: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean is_keydown_skill_remote(int skill_id) {
        switch (OpsSkill.find(skill_id)) {
            case ARCHMAGE1_BIGBANG:
            case ARCHMAGE2_BIGBANG:
            case BISHOP_BIGBANG:
            // evan
            case EVAN_ICE_BREATH:
            case EVAN_BREATH: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean is_mesp_explosion(int skill_id) {
        return OpsSkill.find(skill_id) == OpsSkill.THIEFMASTER_MESO_EXPLOSION;
    }

    public static boolean is_shadow_meso(int skill_id) {
        return OpsSkill.find(skill_id) == OpsSkill.HERMIT_SHADOW_MESO;
    }
}
