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

import java.awt.Point;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : enterDisguise1, enterDisguise2, enterDisguise3, enterDisguise4, enterDisguise5 (JMS186), js の処理を利用
// エレヴ捜索中は偽者の騎士団長がいる隠れ道の森へ移動する
public class Portal_Ereve_EnterDisguise extends PortalScriptMan {

    private final int quest_search_first = 20301; // 20301 ~ 20305
    private final int quest_search_last = 20305;
    private final int item_search_warrant = 4032179; // エレヴ捜索許可証

    @Override
    public boolean enter() {
        int map_forest;
        int npc_fake;
        Point npc_pos;
        int map_out;
        String portal_out;
        String portal_forest;
        switch (getScriptName()) {
            case "enterDisguise1" -> {
                map_forest = 108010600; // 隠れ道 : ティノの森
                portal_forest = "out00";
                npc_fake = 1104101; // オズ(偽者)
                npc_pos = new Point(2517, 88);
                map_out = 130010010; // クィーンズロード : ティノの森
                portal_out = "out00";
            }
            case "enterDisguise2" -> {
                map_forest = 108010610; // 隠れ道 : ティヴの森
                portal_forest = "out00";
                npc_fake = 1104102; // イリーナ(偽者)
                npc_pos = new Point(3307, 88);
                map_out = 130010020; // クィーンズロード : ティヴの森
                portal_out = "out00";
            }
            case "enterDisguise3" -> {
                map_forest = 108010620; // 隠れ道 : ティムの森
                portal_forest = "out00";
                npc_fake = 1104100; // ミハエル(偽者)
                npc_pos = new Point(263, 88);
                map_out = 130010110; // クィーンズロード : ティムの森
                portal_out = "out00";
            }
            case "enterDisguise4" -> {
                map_forest = 108010630; // 隠れ道 : ティルの森
                portal_forest = "out00";
                npc_fake = 1104103; // イカルト(偽者)
                npc_pos = new Point(-1766, 88);
                map_out = 130010120; // クィーンズロード : ティルの森
                portal_out = "out00";
            }
            case "enterDisguise5" -> {
                map_forest = 108010640; // 隠れ道 : 訓練場の入口
                portal_forest = "east00";
                npc_fake = 1104104; // ホークアイ(偽者)
                npc_pos = new Point(542, 88);
                map_out = 130020000; // クィーンズロード : 訓練場の入口
                portal_out = "east00";
            }
            default -> {
                return false;
            }
        }
        boolean searching = false;
        for (int quest_id = this.quest_search_first; quest_id <= this.quest_search_last; quest_id++) {
            if (getQuestStatus(quest_id) == 1) {
                searching = true;
                break;
            }
        }
        if (!searching) {
            return warp(map_out, portal_out);
        }
        if (getPlayerCount(map_forest) != 0) {
            // TODO : 文章 (js の英文を翻訳, 原文 : The forest is already being searched by someone else. Better come back later.)
            playerMessage("この森はすでに他の誰かが捜索しています。後でまた来ましょう。");
            return false;
        }
        if (!haveItem(this.item_search_warrant, 1)) {
            // TODO : 文章 (js の英文を翻訳, 原文 : You do not have the Erev Search Warrent to do so, please get it from Nineheart.)
            playerMessage("エレヴ捜索許可証を持っていません。ナインハートから受け取ってください。");
            return false;
        }
        removeNpc(map_forest, npc_fake);
        TacosMap map = getMapById(map_forest);
        map.killAllMonsters(false);
        map.spawnNpc(npc_fake, npc_pos);
        return warp(map_forest, portal_forest);
    }
}
