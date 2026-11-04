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

import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : enterFirstDH, enterthirdDH, enterfourthDH (JMS186), js の処理を利用
// クエスト進行中のみ訓練場へ入れる
public class Portal_Ereve_EnterDH extends PortalScriptMan {

    @Override
    public boolean enter() {
        int map_hall = 0;
        switch (getScriptName()) {
            case "enterFirstDH" -> {
                if (getQuestStatus(20701) == 1) {
                    map_hall = 913000000; // 隠れ道 : 第1訓練場
                } else if (getQuestStatus(20702) == 1) {
                    map_hall = 913000100; // 隠れ道 : 第1訓練場
                } else if (getQuestStatus(20703) == 1) {
                    map_hall = 913000200; // 隠れ道 : 第1訓練場
                }
                if (map_hall == 0) {
                    // TODO : 文章 (js の英文を翻訳, 原文 : Hall #1 can only be entered if you're engaged in Kiku's Acclimation Training.)
                    playerMessage("第1訓練場はキクの適応訓練を受けている場合のみ入ることができます。");
                    return false;
                }
                if (getPlayerCount(map_hall) != 0) {
                    // TODO : 文章 (js の英文を翻訳, 原文 : Someone is already in this map.)
                    playerMessage("すでに誰かがこのマップにいます。");
                    return false;
                }
                // TODO : js では resetFully の後に respawn(true)
                getMapById(map_hall).resetFully();
                return warp(map_hall, 0);
            }
            case "enterthirdDH" -> {
                for (int quest_id = 20601; quest_id <= 20605; quest_id++) {
                    if (getQuestStatus(quest_id) == 1) {
                        map_hall = 913010200; // 隠れ道 : 第3訓練場
                        break;
                    }
                }
                if (map_hall == 0) {
                    // TODO : 文章 (js の英文を翻訳, 原文 : The only way to enter the hall #3 is if you're training for the Level 100 skills.)
                    playerMessage("第3訓練場はレベル100スキルの修練をしている場合のみ入ることができます。");
                    return false;
                }
            }
            case "enterfourthDH" -> {
                for (int quest_id = 20611; quest_id <= 20615; quest_id++) {
                    if (getQuestStatus(quest_id) == 1) {
                        map_hall = 913020300; // 隠れ道 : 第4訓練場
                        break;
                    }
                }
                if (map_hall == 0) {
                    // TODO : 文章 (js の英文を翻訳, 原文 : Hall #4 is only available to those that are training for Level 110 skill.)
                    playerMessage("第4訓練場はレベル110スキルの修練をしている場合のみ入ることができます。");
                    return false;
                }
            }
            default -> {
                return false;
            }
        }
        if (getPlayerCount(map_hall) != 0) {
            // TODO : 文章 (js の英文を翻訳, 原文 : Someone is already attempting to defeat the boss. Better come back later.)
            playerMessage("すでに誰かがボスに挑戦しています。後でまた来ましょう。");
            return false;
        }
        TacosMap map = getMapById(map_hall);
        map.killAllMonsters(false);
        // TODO : js では killAllMonsters の後に respawn(true)
        return warp(map_hall, 0);
    }
}
