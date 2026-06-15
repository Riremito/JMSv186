/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.constants;

import odin.client.inventory.MapleInventoryType;
import odin.client.status.MonsterStatus;
import java.util.Calendar;
import tacos.config.Region;
import tacos.config.Version;

public class GameConstants {

    private static final int[] mountexp = {0, 6, 25, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247,
        2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550};

    public static int getMountExpNeededForLevel(final int level) {
        return mountexp[level - 1];
    }

    public static int getTimelessRequiredEXP(final int level) {
        return 70 + (level * 10);
    }

    public static int getReverseRequiredEXP(final int level) {
        return 60 + (level * 5);
    }

    public static boolean isJobFamily(final int baseJob, final int currentJob) {
        return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
    }

    public static boolean isKOC(final int job) {
        return job >= 1000 && job < 2000;
    }

    public static boolean isEvan(final int job) {
        return job == 2001 || (job >= 2200 && job <= 2218);
    }

    public static boolean isAran(final int job) {
        return job >= 2000 && job <= 2112 && job != 2001;
    }

    public static boolean isResist(final int job) {
        return job >= 3000 && job <= 3512;
    }

    public static boolean is_demonslayer(int job_id) {
        if (job_id == 3001) {
            return true;
        }
        if (3100 <= job_id && job_id <= 3112) {
            return true;
        }
        return false;
    }

    public static boolean is_mercedes(int job_id) {
        if (job_id == 2002 || (2300 <= job_id && job_id <= 2312)) {
            return true;
        }
        return false;
    }

    public static boolean is_extendsp_job(int job_id) {
        if (Version.GreaterOrEqual(Region.KMS, 197)) {
            return true;
        }
        // エヴァン
        if (job_id == 2001 || (2200 <= job_id && job_id <= 2218)) {
            return true;
        }
        // メルセデス
        if (job_id == 2002 || (2300 <= job_id && job_id <= 2312)) {
            return true;
        }
        // ファントム
        if (job_id == 2003 || (2400 <= job_id && job_id <= 2412)) {
            return true;
        }
        // ルミナス
        if (job_id == 2004 || (2700 <= job_id && job_id <= 2712)) {
            return true;
        }
        // レジスタンス
        if (job_id / 1000 == 3) {
            return true;
        }
        // ハヤト, カンナ
        if (job_id / 1000 == 4) {
            return true;
        }
        if (job_id == 5000 || (5100 <= job_id && job_id <= 5112)) {
            return true;
        }
        // カイザー
        if (job_id == 6000 || (6100 <= job_id && job_id <= 6112)) {
            return true;
        }
        // エンジェリックバスター
        if (job_id == 6001 || (6500 <= job_id && job_id <= 6512)) {
            return true;
        }
        return false;
    }

    public static boolean isAdventurer(final int job) {
        return job >= 0 && job < 1000;
    }

    public static boolean isRecoveryIncSkill(final int id) {
        switch (id) {
            case 1110000:
            case 2000000:
            case 1210000:
            case 11110000:
            case 4100002:
            case 4200001:
                return true;
        }
        return false;
    }

    public static boolean isLinkedAranSkill(final int id) {
        return getLinkedAranSkill(id) != id;
    }

    public static int getLinkedAranSkill(final int id) {
        switch (id) {
            case 21110007:
            case 21110008:
                return 21110002;
            case 21120009:
            case 21120010:
                return 21120002;
            case 4321001:
                return 4321000;
            case 33101006:
            case 33101007:
                return 33101005;
            case 33101008:
                return 33101004;
            case 35101009:
            case 35101010:
                return 35100008;
            case 35111009:
            case 35111010:
                return 35111001;
        }
        return id;
    }

    public static int getBOF_ForJob(final int job) {
        if (isAdventurer(job)) {
            return 12;
        } else if (isKOC(job)) {
            return 10000012;
        } else if (isResist(job)) {
            return 30000012;
        } else if (isEvan(job)) {
            return 20010012;
        }
        return 20000012;
    }

    public static boolean isElementAmp_Skill(final int skill) {
        switch (skill) {
            case 2110001:
            case 2210001:
            case 12110001:
            case 22150000:
                return true;
        }
        return false;
    }

    public static int getMPEaterForJob(final int job) {
        switch (job) {
            case 210:
            case 211:
            case 212:
                return 2100000;
            case 220:
            case 221:
            case 222:
                return 2200000;
            case 230:
            case 231:
            case 232:
                return 2300000;
        }
        return 2100000; // Default, in case GM
    }

    public static boolean isPyramidSkill(final int skill) {
        switch (skill) {
            case 1020:
            case 10001020:
            case 20001020:
            case 20011020:
            case 30001020:
                return true;
        }
        return false;
    }

    public static boolean isMulungSkill(final int skill) {
        switch (skill) {
            case 1009:
            case 1010:
            case 1011:
            case 10001009:
            case 10001010:
            case 10001011:
            case 20001009:
            case 20001010:
            case 20001011:
            case 20011009:
            case 20011010:
            case 20011011:
            case 30001009:
            case 30001010:
            case 30001011:
                return true;
        }
        return false;
    }

    public static boolean isThrowingStar(final int itemId) {
        return itemId / 10000 == 207;
    }

    public static boolean isBullet(final int itemId) {
        return itemId / 10000 == 233;
    }

    public static boolean isRechargable(final int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }

    public static boolean isOverall(final int itemId) {
        return itemId / 10000 == 105;
    }

    public static boolean isPet(final int itemId) {
        return itemId / 10000 == 500;
    }

    public static boolean isMagicWeapon(final int itemId) {
        final int s = itemId / 10000;
        return s == 137 || s == 138;
    }

    public static boolean isWeapon(final int itemId) {
        return itemId >= 1300000 && itemId < 1500000;
    }

    public static MapleInventoryType getInventoryType(final int itemId) {
        final byte type = (byte) (itemId / 1000000);
        if (type < 1 || type > 5) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }

    public static boolean isEquip(final int itemId) {
        return itemId / 1000000 == 1;
    }

    public static boolean isCleanSlate(int itemId) {
        return itemId / 100 == 20490;
    }

    public static boolean isAccessoryScroll(int itemId) {
        return itemId / 100 == 20492;
    }

    public static boolean isChaosScroll(int itemId) {
        if (itemId >= 2049105 && itemId <= 2049110) {
            return false;
        }
        return itemId / 100 == 20491;
    }

    public static int getChaosNumber(int itemId) {
        return itemId == 2049116 ? 10 : 5;
    }

    public static boolean isEquipScroll(int scrollId) {
        return scrollId / 100 == 20493;
    }

    public static boolean isPotentialScroll(int scrollId) {
        return scrollId / 100 == 20494;
    }

    public static boolean isSpecialScroll(final int scrollId) {
        switch (scrollId) {
            case 2040727: // Spikes on show
            case 2041058: // Cape for Cold protection
                return true;
        }
        return false;
    }

    public static boolean isTwoHanded(int item_id) {
        int cat = (item_id / 10000) % 100;

        switch (cat) {
            case 41:
            case 49:
            case 48:
            case 42:
            case 45:
            case 47:
            case 46:
            case 44:
            case 43:
            case 40: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean isUse(final int id) {
        return id >= 2000000 && id <= 2490000;
    }

    public static boolean isSummonSack(final int id) {
        return id / 10000 == 210;
    }

    public static boolean isMonsterCard(int id) {
        return id / 10000 == 238;
    }

    public static boolean isSpecialCard(final int id) {
        return id / 1000 >= 2388;
    }

    public static int getCardShortId(final int id) {
        return id % 10000;
    }

    public static boolean isGem(final int id) {
        return id >= 4250000 && id <= 4251402;
    }

    public static boolean isOtherGem(final int id) {
        switch (id) {
            case 4001174:
            case 4001175:
            case 4001176:
            case 4001177:
            case 4001178:
            case 4001179:
            case 4001180:
            case 4001181:
            case 4001182:
            case 4001183:
            case 4001184:
            case 4001185:
            case 4001186:
            case 4031980:
            case 2041058:
            case 2040727:
            case 1032062:
            case 4032334:
            case 4032312:
            case 1142156:
            case 1142157:
                return true; //mostly quest items
        }
        return false;
    }

    public static boolean isCustomQuest(final int id) {
        return id > 99999;
    }

    public static int getTaxAmount(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.06 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.05 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.04 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.018 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.008 * meso);
        }
        return 0;
    }

    public static int EntrustedStoreTax(final int meso) {
        if (meso >= 100000000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 25000000) {
            return (int) Math.round(0.025 * meso);
        } else if (meso >= 10000000) {
            return (int) Math.round(0.02 * meso);
        } else if (meso >= 5000000) {
            return (int) Math.round(0.015 * meso);
        } else if (meso >= 1000000) {
            return (int) Math.round(0.009 * meso);
        } else if (meso >= 100000) {
            return (int) Math.round(0.004 * meso);
        }
        return 0;
    }

    public static byte gachaponRareItem(final int id) {
        switch (id) {
            case 2340000: // White Scroll
            case 2049100: // Chaos Scroll
            case 2049000: // Reverse Scroll
            case 2049001: // Reverse Scroll
            case 2049002: // Reverse Scroll
            case 2040006: // Miracle
            case 2040007: // Miracle
            case 2040303: // Miracle
            case 2040403: // Miracle
            case 2040506: // Miracle
            case 2040507: // Miracle
            case 2040603: // Miracle
            case 2040709: // Miracle
            case 2040710: // Miracle
            case 2040711: // Miracle
            case 2040806: // Miracle
            case 2040903: // Miracle
            case 2041024: // Miracle
            case 2041025: // Miracle
            case 2043003: // Miracle
            case 2043103: // Miracle
            case 2043203: // Miracle
            case 2043303: // Miracle
            case 2043703: // Miracle
            case 2043803: // Miracle
            case 2044003: // Miracle
            case 2044103: // Miracle
            case 2044203: // Miracle
            case 2044303: // Miracle
            case 2044403: // Miracle
            case 2044503: // Miracle
            case 2044603: // Miracle
            case 2044908: // Miracle
            case 2044815: // Miracle
            case 2044019: // Miracle
            case 2044703: // Miracle
            case 1372039: // Elemental wand lvl 130
            case 1372040: // Elemental wand lvl 130
            case 1372041: // Elemental wand lvl 130
            case 1372042: // Elemental wand lvl 130
            case 1092049: // Dragon Khanjar
            case 1382037: // Blade Staff
                return 2;
            case 1102084: // Pink Gaia Cape
            case 1102041: // Pink Adventurer Cape
            case 1402044: // Pumpkin Lantern
            case 1082149: // Brown Work glove
            case 1102086: // Purple Gaia Cape
            case 1102042: // Purple Adventurer Cape

            case 3010065: // Pink Parasol
            case 3010064: // Brown Sand Bunny Cushion
            case 3010063: // Starry Moon Cushion
            case 3010068: // Teru Teru Chair
            case 3010054: // Baby Bear's Dream
            case 3012001: // Round the Campfire
            case 3012002: // Rubber Ducky Bath
            case 3010020: // Portable Meal Table
            case 3010041: // Skull Throne

            case 1082179: //yellow marker
                return 2;
            //1 = wedding msg o.o
        }
        return 0;
    }
    public final static int[] goldrewards = {
        2340000, 1, // white scroll
        2070018, 1, // balance fury
        1402037, 1, // Rigbol Sword
        2290096, 1, // Maple Warrior 20
        2290049, 1, // Genesis 30
        2290041, 1, // Meteo 30
        2290047, 1, // Blizzard 30
        2290095, 1, // Smoke 30
        2290017, 1, // Enrage 30
        2290075, 1, // Snipe 30
        2290085, 1, // Triple Throw 30
        2290116, 1, // Areal Strike
        1302059, 3, // Dragon Carabella
        2049100, 1, // Chaos Scroll
        2340000, 1, // White Scroll
        1092049, 1, // Dragon Kanjar
        1102041, 1, // Pink Cape
        1432018, 3, // Sky Ski
        1022047, 3, // Owl Mask
        3010051, 1, // Chair
        3010020, 1, // Portable meal table
        2040914, 1, // Shield for Weapon Atk

        1432011, 3, // Fair Frozen
        1442020, 3, // HellSlayer
        1382035, 3, // Blue Marine
        1372010, 3, // Dimon Wand
        1332027, 3, // Varkit
        1302056, 3, // Sparta
        1402005, 3, // Bezerker
        1472053, 3, // Red Craven
        1462018, 3, // Casa Crow
        1452017, 3, // Metus
        1422013, 3, // Lemonite
        1322029, 3, // Ruin Hammer
        1412010, 3, // Colonian Axe

        1472051, 1, // Green Dragon Sleeve
        1482013, 1, // Emperor's Claw
        1492013, 1, // Dragon fire Revlover

        1382050, 1, // Blue Dragon Staff
        1382045, 1, // Fire Staff, Level 105
        1382047, 1, // Ice Staff, Level 105
        1382048, 1, // Thunder Staff
        1382046, 1, // Poison Staff

        1332032, 4, // Christmas Tree
        1482025, 3, // Flowery Tube

        4001011, 4, // Lupin Eraser
        4001010, 4, // Mushmom Eraser
        4001009, 4, // Stump Eraser

        2030008, 5, // Bottle, return scroll
        1442018, 3, // Frozen Tuna
        2040900, 4, // Shield for DEF
        2000005, 10, // Power Elixir
        2000004, 10, // Elixir
        4280000, 4}; // Gold Box
    public final static int[] silverrewards = {
        3010041, 1, // skull throne
        1002452, 3, // Starry Bandana
        1002455, 3, // Starry Bandana
        2290084, 1, // Triple Throw 20
        2290048, 1, // Genesis 20
        2290040, 1, // Meteo 20
        2290046, 1, // Blizzard 20
        2290074, 1, // Sniping 20
        2290064, 1, // Concentration 20
        2290094, 1, // Smoke 20
        2290022, 1, // Berserk 20
        2290056, 1, // Bow Expert 30
        2290066, 1, // xBow Expert 30
        2290020, 1, // Sanc 20
        1102082, 1, // Black Raggdey Cape
        1302049, 1, // Glowing Whip
        2340000, 1, // White Scroll
        1102041, 1, // Pink Cape
        1452019, 2, // White Nisrock
        4001116, 3, // Hexagon Pend
        4001012, 3, // Wraith Eraser
        1022060, 2, // Foxy Racoon Eye

        1432011, 3, // Fair Frozen
        1442020, 3, // HellSlayer
        1382035, 3, // Blue Marine
        1372010, 3, // Dimon Wand
        1332027, 3, // Varkit
        1302056, 3, // Sparta
        1402005, 3, // Bezerker
        1472053, 3, // Red Craven
        1462018, 3, // Casa Crow
        1452017, 3, // Metus
        1422013, 3, // Lemonite
        1322029, 3, // Ruin Hammer
        1412010, 3, // Colonian Axe

        1002587, 3, // Black Wisconsin
        1402044, 1, // Pumpkin lantern
        2101013, 4, // Summoning Showa boss
        1442046, 1, // Super Snowboard
        1422031, 1, // Blue Seal Cushion
        1332054, 3, // Lonzege Dagger
        1012056, 3, // Dog Nose
        1022047, 3, // Owl Mask
        3012002, 1, // Bathtub
        1442012, 3, // Sky snowboard
        1442018, 3, // Frozen Tuna
        1432010, 3, // Omega Spear
        1432036, 1, // Fishing Pole
        2000005, 10, // Power Elixir
        2000004, 10, // Elixir
        4280001, 4}; // Silver Box
    public static int[] eventCommonReward = {
        0, 40,
        1, 10,
        5060003, 18,
        4170023, 18,
        4280000, 3,
        4280001, 4,
        5490000, 3,
        5490001, 4
    };
    public static int[] eventUncommonReward = {
        2, 4,
        3, 4,
        2022179, 5,
        5062000, 10,
        2430082, 10,
        2430092, 10,
        2430103, 1,
        2430117, 1,
        2430118, 1,
        2430201, 2,
        2430228, 2,
        2430229, 2,
        2430136, 2,
        5220000, 14,
        2022459, 5,
        2022460, 5,
        2022461, 5,
        2022462, 5,
        2022463, 5,
        5050000, 2,
        4080100, 5,
        4080000, 5
    };
    public static int[] eventRareReward = {
        2049100, 5,
        2049116, 1,
        2049401, 10,
        2049301, 20,
        2049400, 3,
        2340000, 1,
        3010130, 5,
        3010131, 5,
        3010132, 5,
        3010133, 5,
        3010136, 5,
        3010116, 5,
        3010117, 5,
        3010118, 5,
        1112405, 1,
        1022097, 1,
        2040211, 1,
        2040212, 1,
        2049000, 2,
        2049001, 2,
        2049002, 2,
        2049003, 2,
        1012058, 2,
        1012059, 2,
        1012060, 2,
        1012061, 2
    };
    public static int[] eventSuperReward = {
        2022121, 10,
        4031307, 50,
        3010127, 10,
        3010128, 10,
        3010137, 10,
        2049300, 10,
        1112427, 10,
        1112428, 10,
        1112429, 10
    };
    public static int[] fishingReward = {
        0, 90, // Meso
        1, 70, // EXP
        2022179, 1, // Onyx Apple
        //1302021, 5, // Pico Pico Hammer
        //1072238, 1, // Voilet Snowshoe
        //1072239, 1, // Yellow Snowshoe
        2049100, 1, // Chaos Scroll
        2049301, 1, // Equip Enhancer Scroll
        2049401, 1, // Potential Scroll
        //1302000, 3, // Sword
        //1442011, 1, // Surfboard
        4000517, 8, // Golden Fish
        4000518, 25, // Golden Fish Egg
        4031627, 2, // White Bait (3cm)
        4031628, 1, // Sailfish (120cm)
        4031630, 1, // Carp (30cm)
        4031631, 1, // Salmon(150cm)
        4031632, 1, // Shovel
        4031633, 2, // Whitebait (3.6cm)
        4031634, 1, // Whitebait (5cm)
        4031635, 1, // Whitebait (6.5cm)
        4031636, 1, // Whitebait (10cm)
        4031637, 2, // Carp (53cm)
        4031638, 2, // Carp (60cm)
        4031639, 1, // Carp (100cm)
        4031640, 1, // Carp (113cm)
        4031641, 2, // Sailfish (128cm)
        4031642, 2, // Sailfish (131cm)
        4031643, 1, // Sailfish (140cm)
        4031644, 1, // Sailfish (148cm)
        4031645, 2, // Salmon (166cm)
        4031646, 2, // Salmon (183cm)
        4031647, 1, // Salmon (227cm)
        4031648, 1, // Salmon (288cm)
        4031629, 1 // Pot
    };

    public static boolean isReverseItem(int itemId) {
        switch (itemId) {
            case 1002790:
            case 1002791:
            case 1002792:
            case 1002793:
            case 1002794:
            case 1082239:
            case 1082240:
            case 1082241:
            case 1082242:
            case 1082243:
            case 1052160:
            case 1052161:
            case 1052162:
            case 1052163:
            case 1052164:
            case 1072361:
            case 1072362:
            case 1072363:
            case 1072364:
            case 1072365:

            case 1302086:
            case 1312038:
            case 1322061:
            case 1332075:
            case 1332076:
            case 1372045:
            case 1382059:
            case 1402047:
            case 1412034:
            case 1422038:
            case 1432049:
            case 1442067:
            case 1452059:
            case 1462051:
            case 1472071:
            case 1482024:
            case 1492025:

            case 1342012:
                return true;
            default:
                return false;
        }
    }

    public static boolean isTimelessItem(int itemId) {
        switch (itemId) {
            case 1032031: //shield earring, but technically
            case 1102172:
            case 1002776:
            case 1002777:
            case 1002778:
            case 1002779:
            case 1002780:
            case 1082234:
            case 1082235:
            case 1082236:
            case 1082237:
            case 1082238:
            case 1052155:
            case 1052156:
            case 1052157:
            case 1052158:
            case 1052159:
            case 1072355:
            case 1072356:
            case 1072357:
            case 1072358:
            case 1072359:
            case 1092057:
            case 1092058:
            case 1092059:

            case 1122011:
            case 1122012:

            case 1302081:
            case 1312037:
            case 1322060:
            case 1332073:
            case 1332074:
            case 1372044:
            case 1382057:
            case 1402046:
            case 1412033:
            case 1422037:
            case 1432047:
            case 1442063:
            case 1452057:
            case 1462050:
            case 1472068:
            case 1482023:
            case 1492023:
            case 1342011:
                return true;
            default:
                return false;
        }
    }

    //if only there was a way to find in wz files -.-
    public static boolean isEffectRing(int itemid) {
        return isFriendshipRing(itemid) || isCrushRing(itemid);
    }

    public static boolean isFriendshipRing(int itemId) {
        switch (itemId) {
            case 1112800:
            case 1112801:
            case 1112802:
            case 1112810: //new
            case 1112811: //new, doesnt work in friendship?
            case 1112812: //new, im ASSUMING it's friendship cuz of itemID, not sure.

            case 1049000:
                return true;
        }
        return false;
    }

    public static boolean isCrushRing(int itemId) {
        switch (itemId) {
            case 1112001:
            case 1112002:
            case 1112003:
            case 1112005: //new
            case 1112006: //new
            case 1112007:
            case 1112012:
            case 1112015: //new

            case 1048000:
                return true;
        }
        return false;
    }
    public static int[] Equipments_Bonus = {1122017};

    public static int Equipment_Bonus_EXP(final int itemid) { // TODO : Add Time for more exp increase
        switch (itemid) {
            case 1122017:
                return 10;
        }
        return 0;
    }

    public static int getExpForLevel(int i, int itemId) {
        if (isReverseItem(itemId)) {
            return getReverseRequiredEXP(i);
        } else if (getMaxLevel(itemId) > 0) {
            return getTimelessRequiredEXP(i);
        }
        return 0;
    }

    public static int getMaxLevel(final int itemId) {
        if (isTimelessItem(itemId)) {
            return 5;
        } else if (isReverseItem(itemId)) {
            return 3;
        } else {
            switch (itemId) {
                case 1302109:
                case 1312041:
                case 1322067:
                case 1332083:
                case 1372048:
                case 1382064:
                case 1402055:
                case 1412037:
                case 1422041:
                case 1432052:
                case 1442073:
                case 1452064:
                case 1462058:
                case 1472079:
                case 1482035:

                case 1302108:
                case 1312040:
                case 1322066:
                case 1332082:
                case 1372047:
                case 1382063:
                case 1402054:
                case 1412036:
                case 1422040:
                case 1432051:
                case 1442072:
                case 1452063:
                case 1462057:
                case 1472078:
                case 1482036:
                    return 1;

                case 1072376:
                    return 2;
            }
        }
        return 0;
    }

    public static int getStatChance() {
        return 25;
    }

    public static MonsterStatus getStatFromWeapon(final int itemid) {
        switch (itemid) {
            case 1302109:
            case 1312041:
            case 1322067:
            case 1332083:
            case 1372048:
            case 1382064:
            case 1402055:
            case 1412037:
            case 1422041:
            case 1432052:
            case 1442073:
            case 1452064:
            case 1462058:
            case 1472079:
            case 1482035:
                return MonsterStatus.ACC;
            case 1302108:
            case 1312040:
            case 1322066:
            case 1332082:
            case 1372047:
            case 1382063:
            case 1402054:
            case 1412036:
            case 1422040:
            case 1432051:
            case 1442072:
            case 1452063:
            case 1462057:
            case 1472078:
            case 1482036:
                return MonsterStatus.SPEED;
        }
        return null;
    }

    public static int getXForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC:
                return -70;
            case SPEED:
                return -50;
        }
        return 0;
    }

    public static int getSkillForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC:
                return 3221006;
            case SPEED:
                return 3121007;
        }
        return 0;
    }

    public static int getSkillBook(final int job) {
        if (job >= 2210 && job <= 2218) {
            return job - 2209;
        }
        switch (job) {
            case 3210:
            case 3310:
            case 3510:
                return 1;
            case 3211:
            case 3311:
            case 3511:
                return 2;
            case 3212:
            case 3312:
            case 3512:
                return 3;
        }
        return 0;
    }

    public static int getSkillBookForSkill(final int skillid) {
        return getSkillBook(skillid / 10000);
    }

    public static int getMountItem(final int sourceid) {
        switch (sourceid) {
            case 5221006:
                return 1932000;
            case 33001001: //temp.
                return 1932015;
            case 35001002:
            case 35120000:
                return 1932016;
            case 1013:
            case 10001013:
            case 20001013:
            case 20011013:
            case 30001013:
            case 1046:
            case 10001046:
            case 20001046:
            case 20011046:
            case 30001046:
                return 1932001;
            case 1015:
            case 10001015:
            case 20001015:
            case 20011015:
            case 30001015:
            case 1048:
            case 10001048:
            case 20001048:
            case 20011048:
            case 30001048:
                return 1932002;
            case 1016:
            case 10001016:
            case 20001016:
            case 20011016:
            case 30001016:
            case 1017:
            case 10001017:
            case 20001017:
            case 20011017:
            case 30001017:
            case 1027:
            case 10001027:
            case 20001027:
            case 20011027:
            case 30001027:
                return 1932007;
            case 1018:
            case 10001018:
            case 20001018:
            case 20011018:
            case 30001018:
                return 1932003;
            case 1019:
            case 10001019:
            case 20001019:
            case 20011019:
            case 30001019:
                return 1932005;
            case 1025:
            case 10001025:
            case 20001025:
            case 20011025:
            case 30001025:
                return 1932006;
            case 1028:
            case 10001028:
            case 20001028:
            case 20011028:
            case 30001028:
                return 1932008;
            case 1029:
            case 10001029:
            case 20001029:
            case 20011029:
            case 30001029:
                return 1932009;
            case 1030:
            case 10001030:
            case 20001030:
            case 20011030:
            case 30001030:
                return 1932011;
            case 1031:
            case 10001031:
            case 20001031:
            case 20011031:
            case 30001031:
                return 1932010;
            case 1034:
            case 10001034:
            case 20001034:
            case 20011034:
            case 30001034:
                return 1932014;
            case 1035:
            case 10001035:
            case 20001035:
            case 20011035:
            case 30001035:
                return 1932012;
            case 1036:
            case 10001036:
            case 20001036:
            case 20011036:
            case 30001036:
                return 1932017;
            case 1037:
            case 10001037:
            case 20001037:
            case 20011037:
            case 30001037:
                return 1932018;
            case 1038:
            case 10001038:
            case 20001038:
            case 20011038:
            case 30001038:
                return 1932019;
            case 1039:
            case 10001039:
            case 20001039:
            case 20011039:
            case 30001039:
                return 1932020;
            case 1040:
            case 10001040:
            case 20001040:
            case 20011040:
            case 30001040:
                return 1932021;
            case 1042:
            case 10001042:
            case 20001042:
            case 20011042:
            case 30001042:
                return 1932022;
            case 1044:
            case 10001044:
            case 20001044:
            case 20011044:
            case 30001044:
                return 1932023;
            case 1045:
            case 10001045:
            case 20001045:
            case 20011045:
            case 30001045:
                return 1932030; //wth? helicopter? i didnt see one, so we use hog
            case 1049:
            case 10001049:
            case 20001049:
            case 20011049:
            case 30001049:
                return 1932025;
            case 1050:
            case 10001050:
            case 20001050:
            case 20011050:
            case 30001050:
                return 1932004;
            case 1051:
            case 10001051:
            case 20001051:
            case 20011051:
            case 30001051:
                return 1932026;
            case 1052:
            case 10001052:
            case 20001052:
            case 20011052:
            case 30001052:
                return 1932027;
            case 1053:
            case 10001053:
            case 20001053:
            case 20011053:
            case 30001053:
                return 1932028;
            case 1054:
            case 10001054:
            case 20001054:
            case 20011054:
            case 30001054:
                return 1932029;
            case 1069:
            case 10001069:
            case 20001069:
            case 20011069:
            case 30001069:
                return 1932038;
            case 1096:
            case 10001096:
            case 20001096:
            case 20011096:
            case 30001096:
                return 1932045;
            case 1101:
            case 10001101:
            case 20001101:
            case 20011101:
            case 30001101:
                return 1932046;
            case 1102:
            case 10001102:
            case 20001102:
            case 20011102:
            case 30001102:
                return 1932047;
            default:
                return 0;
        }
    }

    public static boolean isKatara(int itemId) {
        return itemId / 10000 == 134;
    }

    public static boolean isDagger(int itemId) {
        return itemId / 10000 == 133;
    }

    // ?_? kanna OK, skil < 70000000 is something wrong
    public static boolean isApplicableSkill(int skil) {
        return skil < 70000000 && (skil % 10000 < 8000 || skil % 10000 > 8003); //no additional/decent skills
    }

    public static boolean isApplicableSkill_(int skil) { //not applicable to saving but is more of temporary
        return skil >= 90000000 || (skil % 10000 >= 8000 && skil % 10000 <= 8003);
    }

    public static boolean isTablet(int itemId) {
        return itemId / 1000 == 2047;
    }

    public static int getSuccessTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) { //2047_2_00 = armor, 2047_3_00 = accessory
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 55;
                case 2:
                    return 43;
                case 3:
                    return 33;
                case 4:
                    return 26;
                case 5:
                    return 20;
                case 6:
                    return 16;
                case 7:
                    return 12;
                case 8:
                    return 10;
                default:
                    return 7;
            }
        } else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 35;
                case 2:
                    return 18;
                case 3:
                    return 12;
                default:
                    return 7;
            }
        } else {
            switch (level) {
                case 0:
                    return 70;
                case 1:
                    return 50; //-20
                case 2:
                    return 36; //-14
                case 3:
                    return 26; //-10
                case 4:
                    return 19; //-7
                case 5:
                    return 14; //-5
                case 6:
                    return 10; //-4
                default:
                    return 7;  //-3
            }
        }
    }

    public static int getCurseTablet(final int scrollId, final int level) {
        if (scrollId % 1000 / 100 == 2) { //2047_2_00 = armor, 2047_3_00 = accessory
            switch (level) {
                case 0:
                    return 10;
                case 1:
                    return 12;
                case 2:
                    return 16;
                case 3:
                    return 20;
                case 4:
                    return 26;
                case 5:
                    return 33;
                case 6:
                    return 43;
                case 7:
                    return 55;
                case 8:
                    return 70;
                default:
                    return 100;
            }
        } else if (scrollId % 1000 / 100 == 3) {
            switch (level) {
                case 0:
                    return 12;
                case 1:
                    return 18;
                case 2:
                    return 35;
                case 3:
                    return 70;
                default:
                    return 100;
            }
        } else {
            switch (level) {
                case 0:
                    return 10;
                case 1:
                    return 14; //+4
                case 2:
                    return 19; //+5
                case 3:
                    return 26; //+7
                case 4:
                    return 36; //+10
                case 5:
                    return 50; //+14
                case 6:
                    return 70; //+20
                default:
                    return 100;  //+30
            }
        }
    }

    public static boolean isAccessory(final int itemId) {
        return (itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1153000) || (itemId >= 1112000 && itemId < 1113000);
    }

    public static final boolean isMountItemAvailable(final int mountid, final int jobid) {
        if (jobid != 900 && mountid / 10000 == 190) {
            if (isKOC(jobid)) {
                if (mountid < 1902005 || mountid > 1902007) {
                    return false;
                }
            } else if (isAdventurer(jobid)) {
                if (mountid < 1902000 || mountid > 1902002) {
                    return false;
                }
            } else if (isAran(jobid)) {
                if (mountid < 1902015 || mountid > 1902018) {
                    return false;
                }
            } else if (isEvan(jobid)) {
                if (mountid < 1902040 || mountid > 1902042) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isEvanDragonItem(final int itemId) {
        return itemId >= 1940000 && itemId < 1980000; //194 = mask, 195 = pendant, 196 = wings, 197 = tail
    }

    public static boolean canScroll(final int itemId) {
        return itemId / 100000 != 19 && itemId / 100000 != 16; //no mech/taming/dragon
    }

    public static boolean canHammer(final int itemId) {
        switch (itemId) {
            case 1122000:
            case 1122076: //ht, chaos ht
                return false;
        }
        if (!canScroll(itemId)) {
            return false;
        }
        return true;
    }

    public static int getMasterySkill(final int job) {
        if (job >= 1410 && job <= 1412) {
            return 14100000;
        } else if (job >= 410 && job <= 412) {
            return 4100000;
        } else if (job >= 520 && job <= 522) {
            return 5200000;
        }
        return 0;
    }

    public static int getExpRate_Quest(final int level) {
        return (level >= 30 ? (level >= 70 ? (level >= 120 ? 4 : 3) : 2) : 1);
    }

    public static boolean isCustomReactItem(final int rid, final int iid, final int original) {
        if (rid == 2008006) { //orbis pq LOL
            return iid == (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 4001055);
            //4001056 = sunday. 4001062 = saturday
        } else {
            return iid == original;
        }
    }

    public static int getJobNumber(int jobz) {
        int job = (jobz % 1000);
        if (job / 100 == 0) {
            return 0; //beginner
        } else if (job / 10 == 0) {
            return 1;
        } else {
            return 2 + (job % 10);
        }
    }

    public static int getFishingTime(boolean vip, boolean gm) {
        return gm ? 100 : (vip ? 100 : 1000);
    }

    public static int getCustomSpawnID(int summoner, int def) {
        switch (summoner) {
            case 9400589:
            case 9400748: //MV
                return 9400706; //jr
            default:
                return def;
        }
    }

    public static boolean canForfeit(int questid) {
        switch (questid) {
            case 20000:
            case 20010:
            case 20015: //cygnus quests
            case 20020:
                return false;
            default:
                return true;
        }
    }
}
