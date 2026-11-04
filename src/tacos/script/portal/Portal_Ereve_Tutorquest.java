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
// Map.wz portal script : tutorquest (JMS186), js の処理を利用
// シグナス騎士団のチュートリアル
public class Portal_Ereve_Tutorquest extends PortalScriptMan {

    @Override
    public boolean enter() {
        int map_id = getMapId();
        int quest_id;
        int state = 2;
        switch (map_id) {
            case 130030001 -> {
                quest_id = 20010;
                state = 1;
            }
            case 130030002 -> {
                quest_id = 20011;
            }
            case 130030003 -> {
                quest_id = 20012;
            }
            case 130030004 -> {
                quest_id = 20013;
            }
            default -> {
                return false;
            }
        }
        if (getQuestStatus(quest_id) == state) {
            return warp(map_id + 1, "sp");
        }
        if (map_id == 130030001) {
            // TODO : 文章 (js の英文を翻訳, 原文 : Please click the NPC to receive a quest.)
            playerMessage(5, "NPCをクリックしてクエストを受けてください。");
        } else {
            // TODO : 文章 (js の英文を翻訳, 原文 : Please complete the quest.)
            playerMessage(5, "クエストを完了してください。");
        }
        return false;
    }
}
