/*
 * Copyright (C) 2024 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package debug;

import client.ISkill;
import client.MapleCharacter;
import client.PlayerStats;
import client.SkillFactory;
import config.ServerConfig;
import java.util.ArrayList;
import server.Randomizer;
import wz.LoadData;

/**
 *
 * @author Riremito
 */
public class DebugJob {

    public enum Stat {
        STR,
        DEX,
        INT,
        LUK
    }

    // ステータス初期化
    public static void ResetStat(MapleCharacter chr) {
        //int ability_point = 25;
        DebugJob.AllSkill(chr, true);
        PlayerStats stat = chr.getStat();
        chr.setRemainingAp(0);
        chr.setRemainingSp(0);
        chr.setExp(0);
        chr.setLevel(1);
        chr.setJob(0);
        stat.setHp(50);
        stat.setMaxHp(50);
        stat.setMp(50);
        stat.setMaxMp(50);
        stat.setStr(12);
        stat.setDex(5);
        stat.setInt(4);
        stat.setLuk(4);
        chr.UpdateStat(true);
    }

    public static boolean IsBeginnerJob(int job_id) {
        switch (job_id) {
            case 0:
            case 1000:
            case 2000:
            case 2001:
            case 3000: {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static int GetSkillEffect(MapleCharacter chr, int skill_id) {
        ISkill skill = SkillFactory.getSkill(skill_id);
        if (skill == null) {
            return 0;
        }
        return skill.getEffect(chr.getSkillLevel(skill)).getX();
    }

    public static void LevelUpStat(MapleCharacter chr) {
        int job_id = chr.getJob();
        int level = chr.getLevel() + 1;
        int ability_point = chr.getRemainingAp() + 5;
        int skill_point = chr.getRemainingSp() + 3;
        PlayerStats player_stat = chr.getStat();
        int maxhp = player_stat.getMaxHp();
        int maxmp = player_stat.getMaxMp();
        int mp_by_int = player_stat.getInt() / 10; // player_stat.getTotalInt();

        // AP KOC under level 70
        if ((job_id / 1000) == 1 && level <= 70) {
            ability_point += 1;
        }
        // set AP
        chr.setRemainingAp(ability_point);
        // auto SP beginner under level 10
        if (IsBeginnerJob(job_id)) {
            player_stat.setStr(player_stat.getStr() + ability_point);
            chr.setRemainingAp(0);
        } else {
            // set SP
            chr.setRemainingSp(skill_point);
        }

        if ((job_id / 1000) == 2 && ((job_id % 1000) / 100) == 1) {
            // Aran
            maxhp += Randomizer.rand(50, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if ((job_id / 1000) == 2 && ((job_id % 1000) / 100) == 2) {
            // Evan
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(50, 52);
            maxmp += mp_by_int;
        } else {
            switch (((job_id % 1000) / 100)) {
                // 戦士
                case 1: {
                    maxhp += Randomizer.rand(24, 28);
                    maxmp += Randomizer.rand(4, 6);

                    int additional_hp = GetSkillEffect(chr, 1000001);
                    if (additional_hp == 0) {
                        additional_hp = GetSkillEffect(chr, 11000000);
                    }
                    maxhp += additional_hp;
                    break;
                }
                // 魔法使い
                case 2: {
                    maxhp += Randomizer.rand(10, 14);
                    maxmp += Randomizer.rand(22, 24);

                    int additional_mp = GetSkillEffect(chr, 2000001);
                    if (additional_mp == 0) {
                        additional_mp = GetSkillEffect(chr, 12000000);
                    }
                    maxmp += additional_mp;
                    maxmp += mp_by_int;
                    break;
                }
                // 弓使い
                case 3: {
                    maxhp += Randomizer.rand(20, 24);
                    maxmp += Randomizer.rand(14, 16);
                    break;
                }
                // 盗賊
                case 4: {
                    maxhp += Randomizer.rand(20, 24);
                    maxmp += Randomizer.rand(14, 16);
                    break;
                }
                // 海賊
                case 5: {
                    maxhp += Randomizer.rand(22, 26);
                    maxmp += Randomizer.rand(18, 22);

                    int additional_hp = GetSkillEffect(chr, 5100000);
                    if (additional_hp == 0) {
                        additional_hp = GetSkillEffect(chr, 15100000);
                    }
                    maxhp += additional_hp;
                    break;
                }
                default: {
                    maxhp += Randomizer.rand(12, 16);
                    maxmp += Randomizer.rand(10, 12);
                    break;
                }
            }
        }
        //maxmp += player_stat.getTotalInt() / 10;
        maxhp = Math.min(ServerConfig.IsPreBB() ? 30000 : 500000, Math.abs(maxhp));
        maxmp = Math.min(ServerConfig.IsPreBB() ? 30000 : 500000, Math.abs(maxmp));

        player_stat.setMaxHp(maxhp);
        player_stat.setMaxMp(maxmp);
        player_stat.setHp(maxhp);
        player_stat.setMp(maxmp);
        chr.setLevel(level);
        chr.setExp(0);
    }

    public static int getNextLevel(int job_id) {
        switch (job_id) {
            // 冒険家 1次
            case 100:
            case 300:
            case 400:
            case 500: {
                return 10;
            }
            case 200: {
                return 8;
            }
            // 冒険家 2次
            case 110:
            case 120:
            case 130:
            case 210:
            case 220:
            case 230:
            case 310:
            case 320:
            case 410:
            case 420:
            case 510:
            case 520: {
                return 30;
            }
            default: {
                break;
            }
        }
        return 0;
    }

    public static boolean addStat(MapleCharacter chr, PlayerStats player_stat, Stat stat_main) {
        int ability_point = chr.getRemainingAp();
        int def_stat_sub_point = 0;
        int level = chr.getLevel();

        switch (stat_main) {
            case STR: {
                def_stat_sub_point = level + 30 - player_stat.getDex();
                if (def_stat_sub_point > 0) {
                    if (ability_point < def_stat_sub_point) {
                        player_stat.setDex(player_stat.getDex() + ability_point);
                        chr.setRemainingAp(0);
                        return true;
                    }
                    player_stat.setDex(player_stat.getDex() + def_stat_sub_point);
                    ability_point -= def_stat_sub_point;
                }

                chr.setRemainingAp(0);
                player_stat.setStr(player_stat.getStr() + ability_point);
                return true;
            }
            case DEX: {
                def_stat_sub_point = level + 5 - player_stat.getStr();
                if (def_stat_sub_point > 0) {
                    if (ability_point < def_stat_sub_point) {
                        player_stat.setStr(player_stat.getStr() + ability_point);
                        chr.setRemainingAp(0);
                        return true;
                    }
                    player_stat.setStr(player_stat.getStr() + def_stat_sub_point);
                    ability_point -= def_stat_sub_point;
                }

                chr.setRemainingAp(0);
                player_stat.setDex(player_stat.getDex() + ability_point);
                return true;
            }
            case INT: {
                def_stat_sub_point = level + 3 - player_stat.getLuk();
                if (def_stat_sub_point > 0) {
                    if (ability_point < def_stat_sub_point) {
                        player_stat.setLuk(player_stat.getLuk() + ability_point);
                        chr.setRemainingAp(0);
                        return true;
                    }
                    player_stat.setLuk(player_stat.getLuk() + def_stat_sub_point);
                    ability_point -= def_stat_sub_point;
                }

                chr.setRemainingAp(0);
                player_stat.setInt(player_stat.getInt() + ability_point);
                return true;
            }
            case LUK: {
                def_stat_sub_point = level + 40 - player_stat.getDex();
                if (def_stat_sub_point > 0) {
                    if (ability_point < def_stat_sub_point) {
                        player_stat.setDex(player_stat.getDex() + ability_point);
                        chr.setRemainingAp(0);
                        return true;
                    }
                    player_stat.setDex(player_stat.getDex() + def_stat_sub_point);
                    ability_point -= def_stat_sub_point;
                }

                chr.setRemainingAp(0);
                player_stat.setLuk(player_stat.getLuk() + ability_point);
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean DefStat(MapleCharacter chr, int job_id, int level) {
        ResetStat(chr);

        if (!LoadData.IsValidJobID(job_id)) {
            return false;
        }
        int next_level = 1;
        Stat stat_main = Stat.STR;

        if ((job_id % 10) == 2) {
            // 4th
            next_level = 120;
        } else if ((job_id % 10) == 1) {
            // 3rd
            next_level = 70;
        } else if ((job_id % 100) > 0) {
            // 2nd
            next_level = 30;
        } else if (job_id > 0) {
            // 1st
            next_level = 10;
        } else {
            // beginner
            next_level = 1;
        }

        if (next_level < level) {
            next_level = level;
        }

        if (200 < next_level) {
            next_level = 200;
        }

        switch (((job_id % 1000) / 100)) {
            // 戦士
            case 1: {
                stat_main = Stat.STR;
                break;
            }
            // 魔法使い
            case 2: {
                stat_main = Stat.INT;
                break;
            }
            // 弓使い
            case 3: {
                stat_main = Stat.DEX;
                break;
            }
            // 盗賊
            case 4: {
                stat_main = Stat.LUK;
                break;
            }
            // 海賊
            case 5: {
                // バイパー
                stat_main = Stat.STR;
                // キャプテン
                if ((job_id % 100) >= 20) {
                    stat_main = Stat.DEX;
                }
                break;
            }
            default: {
                break;
            }
        }

        chr.setJob(job_id);
        PlayerStats player_stat = chr.getStat();
        for (int i = chr.getLevel(); i < next_level; i++) {
            LevelUpStat(chr);
            addStat(chr, player_stat, stat_main);
        }
        chr.UpdateStat(true);
        return true;
    }

    public static boolean AllSkill(MapleCharacter chr) {
        return AllSkill(chr, false);
    }

    public static boolean AllSkill(MapleCharacter chr, boolean reset) {
        int job_id = chr.getJob();

        if (!LoadData.IsValidJobID(job_id)) {
            return false;
        }

        // 初心者系統
        if ((job_id / 100) == 0) {
            return false;
        }
        // エヴァン
        if (2200 <= job_id && job_id <= 2218) {
            return false;
        }
        // デュアルブレイド
        if (430 <= job_id && job_id <= 434) {
            return false;
        }
        ArrayList<Integer> job_list = new ArrayList<>();
        job_list.add(job_id);

        // 4次転職済み
        switch ((job_id % 10)) {
            // 4次転職
            case 2: {
                job_id -= 1;
                if (!LoadData.IsValidJobID(job_id)) {
                    return false;
                }
                job_list.add(job_id);
            }
            // 3次転職
            case 1: {
                job_id -= 1;
                if (!LoadData.IsValidJobID(job_id)) {
                    return false;
                }
                job_list.add(job_id);
            }
            case 0: {
                // 2次転職
                if ((job_id % 100) != 0) {
                    job_id -= job_id % 100;
                    if (!LoadData.IsValidJobID(job_id)) {
                        return false;
                    }
                    job_list.add(job_id);
                }
                break;
            }
            default: {
                return false;
            }
        }

        for (Integer v : job_list) {
            for (Integer skill_id : SkillFactory.getSkillsByJob(v)) {
                ISkill skill = SkillFactory.getSkill(skill_id);
                chr.changeSkillLevel(skill, reset ? 0 : skill.getMaxLevel(), (v % 10 == 2) ? (byte) skill.getMaxLevel() : (byte) 0);
            }
        }

        int level = 180;
        switch (job_list.size()) {
            case 3:
                level = 120;
                break;
            case 2:
                level = 70;
                break;
            case 1:
                level = 30;
                break;
            default:
                break;
        }
        AllStat(chr, level);
        return true;
    }

    public static void AllStat(MapleCharacter chr) {
        AllStat(chr, 180);
    }

    public static void AllStat(MapleCharacter chr, int level) {
        PlayerStats stat = chr.getStat();
        chr.setRemainingAp(150);
        chr.setRemainingSp(550);
        chr.setFame((short) 300);
        chr.setExp(0);
        chr.setLevel(level);
        stat.setMaxHp(level * 100);
        stat.setHp((int) (level * 100 * 0.8));
        stat.setMaxMp(level * 50);
        stat.setMp((int) (level * 50 * 0.8));
        stat.setStr(level * 5);
        stat.setDex(level * 5);
        stat.setInt(level * 5);
        stat.setLuk(level * 5);
        chr.UpdateStat(true);
    }
}
