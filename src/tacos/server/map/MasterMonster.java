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
package tacos.server.map;

import java.awt.Point;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.life.SpawnPointAreaBoss;
import odin.server.maps.MapleMap;
import tacos.debug.DebugLogger;
import tacos.wz.ids.DWI_Validation;

/**
 *
 * @author Riremito
 */
public class MasterMonster {

    public static boolean addAreaBossSpawn(MapleMap map) {
        int monsterid;
        int mobtime;
        String msg;
        Point pos1, pos2, pos3;

        switch (map.getId()) {
            // マノ
            case 104000400: {
                mobtime = 2700;
                monsterid = 2220000;
                // OK
                msg = "涼しい気運が濃く立ち込めながらマノが現れました。";
                pos1 = new Point(439, 185);
                pos2 = new Point(301, -85);
                pos3 = new Point(107, -355);
                break;
            }
            // スタンピ
            case 101030404: {
                mobtime = 2700;
                monsterid = 3220000;
                // OK
                msg = "岩山を響く足音とともにスタンピが現れました。";
                pos1 = new Point(867, 1282);
                pos2 = new Point(810, 1570);
                pos3 = new Point(838, 2197);
                break;
            }
            // キンクラン
            case 110040000: {
                mobtime = 1200;
                monsterid = 5220001;
                // OK
                msg = "砂浜に怪しいキンクランが現れました。";
                pos1 = new Point(-355, 179);
                pos2 = new Point(-1283, -113);
                pos3 = new Point(-571, -593);
                break;
            }
            // タイルン
            case 250010304: {
                mobtime = 2100;
                monsterid = 7220000;
                // メッセージ探し中
                //msg = "Tae Roon appeared with a loud growl.";
                // 適当
                msg = "唸り声と共にタイルンが現れました。";
                pos1 = new Point(-210, 33);
                pos2 = new Point(-234, 393);
                pos3 = new Point(-654, 33);
                break;
            }
            // エリジャー
            case 200010300: {
                mobtime = 1200;
                monsterid = 8220000;
                // OK
                msg = "黒い旋風を巻き起こしながらエリジャーが現れました。";
                pos1 = new Point(665, 83);
                pos2 = new Point(672, -217);
                pos3 = new Point(-123, -217);
                break;
            }
            // 神仙妖怪
            case 250010503: {
                mobtime = 1800;
                monsterid = 7220002;
                // OK
                msg = "気持ち悪い猫の鳴き声が聞えます。";
                pos1 = new Point(-303, 543);
                pos2 = new Point(227, 543);
                pos3 = new Point(719, 543);
                break;
            }
            // おキツネ様
            case 222010310: {
                mobtime = 2700;
                monsterid = 7220001;
                // OK
                msg = "月の光が薄くなり、長い狐の鳴き声とともにおキツネ様の気運が感じられます。";
                pos1 = new Point(-169, -147);
                pos2 = new Point(-517, 93);
                pos3 = new Point(247, 93);
                break;
            }
            // ダイル, マップ2つ
            case 107000300: {
                mobtime = 1800;
                monsterid = 6220000;
                //msg = "The huge crocodile Dale has come out from the swamp.";
                // 適当
                msg = "沼から巨大ワニのダイルが出てきました。";
                pos1 = new Point(710, 118);
                pos2 = new Point(95, 119);
                pos3 = new Point(-535, 120);
                break;
            }
            // パウスト
            case 100040105: {
                mobtime = 1800;
                monsterid = 5220002;
                //msg = "The blue fog became darker when Faust appeared.";
                // msg = "パウストが出ました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "青い霧が暗くなりパウストが現れました。";
                pos1 = new Point(1000, 278);
                pos2 = new Point(557, 278);
                pos3 = new Point(95, 278);
                break;
            }
            case 100040106: {
                mobtime = 1800;
                monsterid = 5220002;
                //msg = "The blue fog became darker when Faust appeared.";
                //msg = "パウストが出ました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "青い霧が暗くなりパウストが現れました。";
                pos1 = new Point(1000, 278);
                pos2 = new Point(557, 278);
                pos3 = new Point(95, 278);
                break;
            }
            // タイマー, マップ3つ
            case 220050100: {
                mobtime = 1500;
                monsterid = 5220003;
                //msg = "Click clock! Timer has appeared with an irregular clock sound.";
                //msg = "タイマーが出ました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "不規則な針音と共にタイマーが現れました。";
                pos1 = new Point(-467, 1032);
                pos2 = new Point(532, 1032);
                pos3 = new Point(-47, 1032);
                break;
            }
            // ジェノ
            case 221040301: {
                mobtime = 2400;
                monsterid = 6220001;
                //msg = "Jeno has appeared with a heavy sound of machinery.";
                //msg = "ジェノが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "騒音と共にジェノが現れました。";
                pos1 = new Point(-4134, 416);
                pos2 = new Point(-4283, 776);
                pos3 = new Point(-3292, 776);
                break;
            }
            // レヴィアタン
            case 240040401: {
                mobtime = 7200;
                monsterid = 8220003;
                //msg = "Leviathan has appeared with a cold wind from over the gorge.";
                //msg = "レヴィアタンが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "渓谷から冷風と共にレビィアタンが現れました。";
                pos1 = new Point(-15, 2481);
                pos2 = new Point(127, 1634);
                pos3 = new Point(159, 1142);
                break;
            }
            // デウ
            case 260010201: {
                mobtime = 3600;
                monsterid = 3220001;
                //msg = "Dewu slowly appeared out of the sand dust.";
                //msg = "デウが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "砂塵からゆっくりとデウが出てきました。";
                pos1 = new Point(-215, 275);
                pos2 = new Point(298, 275);
                pos3 = new Point(592, 275);
                break;
            }
            // キメラ
            case 261030000: {
                mobtime = 2700;
                monsterid = 8220002;
                //msg = "Chimera has appeared out of the darkness of the underground with a glitter in her eyes.";
                //msg = "キメラが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "地下の暗闇から目を光らせながらキメラが現れました。";
                pos1 = new Point(-1094, -405);
                pos2 = new Point(-772, -116);
                pos3 = new Point(-108, 181);
                break;
            }
            // セルフ
            case 230020100: {
                mobtime = 2700;
                monsterid = 4220000;
                //msg = "A strange shell has appeared from a grove of seaweed.";
                //msg = "セルフが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "海草の塔から怪しいセルフが現れました。";
                pos1 = new Point(-291, -20);
                pos2 = new Point(-272, -500);
                pos3 = new Point(-462, 640);
                break;
            }
            default: {
                return false;
            }
        }

        if (!DWI_Validation.isValidMobID(monsterid)) {
            DebugLogger.ErrorLog("Invalid Mob ID = " + monsterid);
            return false;
        }

        addAreaMonsterSpawn(map, MapleLifeFactory.getMonster(monsterid), pos1, pos2, pos3, mobtime, msg);
        return true;
    }

    public static void addAreaMonsterSpawn(MapleMap map, MapleMonster monster, Point pos1, Point pos2, Point pos3, int mobTime, String msg) {
        pos1 = map.calcPointBelow(pos1);
        pos2 = map.calcPointBelow(pos2);
        pos3 = map.calcPointBelow(pos3);
        if (pos1 != null) {
            pos1.y -= 1;
        }
        if (pos2 != null) {
            pos2.y -= 1;
        }
        if (pos3 != null) {
            pos3.y -= 1;
        }
        if (pos1 == null && pos2 == null && pos3 == null) {
            System.out.println("WARNING: mapid " + map.getId() + ", monster " + monster.getId() + " could not be spawned.");

            return;
        } else if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        } else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        } else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        map.getMonsterSpawn().add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg));
    }
}
