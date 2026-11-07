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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : MD_error, MD_golem, MD_mushroom, MD_pig, MD_protect, MD_rabbit, MD_remember, MD_roundTable, MD_sand (JMS186), js の処理を利用
// ミニダンジョンの入口と出口, 空いているミニダンジョンへ移動する
public class Portal_Common_MD extends PortalScriptMan {

    @Override
    public boolean enter() {
        int map_base;
        int map_dungeon;
        int dungeons;
        switch (getScriptName()) {
            case "MD_drakeroom" -> {
                map_base = 105090311; // 中央ダンジョン : 冷たい揺りかご
                map_dungeon = 105090320;
                dungeons = 20;
            }
            case "MD_error" -> {
                map_base = 261020300; // アルカドノ研究所 : 研究所C-1区域
                map_dungeon = 261020301;
                dungeons = 39;
            }
            case "MD_golem" -> {
                map_base = 105040304; // 中央ダンジョン : スリーピーダンジョン4
                map_dungeon = 105040320;
                dungeons = 30;
            }
            case "MD_mushroom" -> {
                map_base = 105050100; // 中央ダンジョン : アリの巣2
                map_dungeon = 105050101;
                dungeons = 30;
            }
            case "MD_pig" -> {
                map_base = 100020000; // ヘネシス : 東の草むら
                map_dungeon = 100020100;
                dungeons = 30;
            }
            case "MD_protect" -> {
                map_base = 240040520; // リプレ : 壊れた龍の巣
                map_dungeon = 240040900;
                dungeons = 19;
            }
            case "MD_rabbit" -> {
                map_base = 221023400; // ルディブリアム : エオス塔76階-90階
                map_dungeon = 221023401;
                dungeons = 30;
            }
            case "MD_remember" -> {
                map_base = 240040511; // リプレ : 残された龍の巣
                map_dungeon = 240040800;
                dungeons = 19;
            }
            case "MD_roundTable" -> {
                map_base = 240020500; // リプレ : 火と水の戦場
                map_dungeon = 240020501;
                dungeons = 30;
            }
            case "MD_sand" -> {
                map_base = 260020600; // サンセットロード : サヘル地帯2
                map_dungeon = 260020630;
                dungeons = 30;
            }
            case "MD_treasure" -> {
                map_base = 251010402; // 白草村 : 赤鼻海賊団の盗掘2
                map_dungeon = 251010410;
                dungeons = 20;
            }
            default -> {
                return false;
            }
        }
        if (getMapId() != map_base) {
            return warp(map_base, "MD00");
        }
        if (isPartyMember() && !isLeader()) {
            // TODO : 文章 (js の英文を翻訳, 原文 : You are not the leader of the party.)
            playerMessage(5, "パーティーリーダーではありません。");
            return false;
        }
        for (int i = 0; i < dungeons; i++) {
            if (getPlayerCount(map_dungeon + i) == 0) {
                if (isPartyMember()) {
                    return warpParty(map_dungeon + i, "out00");
                }
                return warp(map_dungeon + i, "out00");
            }
        }
        // TODO : 文章 (js の英文を翻訳, 原文 : All of the Mini-Dungeons are in use right now, please try again later.)
        playerMessage(5, "現在、全てのミニダンジョンが使用中です。後でもう一度お試しください。");
        return false;
    }
}
