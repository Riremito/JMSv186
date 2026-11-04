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
// Map.wz portal script : evanGolemDoor (JMS186), js の処理を利用
public class Portal_Victoria_EvanGolemDoor extends PortalScriptMan {

    private final int quest_rescue_camilla = 22557;
    private final int map_golem_temple_entrance = 106010102; // へネシス : ゴーレム寺院の入口

    @Override
    public boolean enter() {
        if (isQuestActive(this.quest_rescue_camilla)) {
            forceCompleteQuest(this.quest_rescue_camilla);
            // TODO : 文章 (js の英文を翻訳, 原文 : Camilla rescued!)
            playerMessage(5, "カミラを救出した！");
            gainSp(1);
        }
        return warp(this.map_golem_temple_entrance, 0);
    }
}
