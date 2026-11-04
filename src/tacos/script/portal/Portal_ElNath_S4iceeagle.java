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
// Map.wz portal script : s4iceeagle (JMS186), lua の処理を利用
public class Portal_ElNath_S4iceeagle extends PortalScriptMan {

    private final int quest_freezer = 6242; // フリズベルクのクエスト
    private final int quest_freezer2 = 6243; // フリズベルクのクエスト (2回目)
    private final int item_freezer_egg = 4001114; // フリズベルクの卵
    private final int map_freezer_nest = 921100210; // フリズベルクの巣

    @Override
    public boolean enter() {
        boolean active = getQuestStatus(this.quest_freezer) == 1 || getQuestStatus(this.quest_freezer2) == 1;
        boolean again = getQuestStatus(this.quest_freezer) == 2 && getQuestStatus(this.quest_freezer2) == 0;
        if (!active && !again) {
            // 翻訳
            message("封印された場所には入れません。");
            return false;
        }
        if (0 < getItemCount(this.item_freezer_egg)) {
            // 翻訳 (lua の文章のまま, 卵を持っている場合のメッセージ)
            message("フリーザーの卵を持っていません。入場できません。");
            return false;
        }
        return transferField(this.map_freezer_nest, "");
    }
}
