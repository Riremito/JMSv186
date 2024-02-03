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

import client.MapleCharacter;
import client.PlayerStats;
import wz.LoadData;

/**
 *
 * @author Riremito
 */
public class DebugJob {

    // ステータス初期化
    public static void ResetStat(MapleCharacter chr) {
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

    public void AutoLevelUp(MapleCharacter chr) {
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

    public static boolean DefStat(MapleCharacter chr, int job_id) {
        ResetStat(chr);

        if (!LoadData.IsValidJobID(job_id)) {
            return false;
        }

        int next_level = getNextLevel(job_id);
        for (int level = chr.getLevel(); level < next_level; level++) {
            chr.levelUp();
        }

        chr.setJob(job_id);
        chr.setExp(0);
        chr.setRemainingSp(1);
        chr.UpdateStat(true);
        return true;
    }
}
