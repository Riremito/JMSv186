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
// Map.wz portal script : enterBlackFrog (JMS186), js の処理を利用
public class Portal_Ludibrium_EnterBlackFrog extends PortalScriptMan {

    private final int quest_black_frog = 22596;
    private final int map_after = 922030001;
    private final int map_before = 922030000;

    @Override
    public boolean enter() {
        if (isQuestActive(this.quest_black_frog)) {
            forceCompleteQuest(this.quest_black_frog);
            gainAp(5);
            // TODO : 文章 (js の英文を翻訳, 原文 : Ibech has ran away! Gained 5 AP!)
            playerMessage(5, "イベックが逃げ出した！APを5獲得しました！");
            return warp(this.map_after, "out00");
        }
        if (isQuestFinished(this.quest_black_frog)) {
            return warp(this.map_after, "out00");
        }
        return warp(this.map_before, "out00");
    }
}
