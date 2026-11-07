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
// Map.wz portal script : s4firehawk (JMS186), lua の処理を利用
public class Portal_ElNath_S4firehawk extends PortalScriptMan {

    private final int quest_phoenix = 6240; // フェニックスのクエスト
    private final int quest_phoenix2 = 6241; // フェニックスのクエスト (2回目)
    private final int item_phoenix_egg = 4001113; // フェニックスの卵
    private final int map_phoenix_nest = 921100200; // フェニックスの巣

    @Override
    public boolean enter() {
        boolean active = getQuestStatus(this.quest_phoenix) == 1 || getQuestStatus(this.quest_phoenix2) == 1;
        boolean again = getQuestStatus(this.quest_phoenix) == 2 && getQuestStatus(this.quest_phoenix2) == 0;
        if (!active && !again) {
            // 翻訳
            message("封印された場所には入れません。");
            return false;
        }
        if (0 < getItemCount(this.item_phoenix_egg)) {
            // 翻訳
            message("すでにフェニックスの卵を持っています。入場できません。");
            return false;
        }
        return transferField(this.map_phoenix_nest, "");
    }
}
