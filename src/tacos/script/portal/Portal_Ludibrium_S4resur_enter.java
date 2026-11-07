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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : s4resur_enter (JMS186), lua の処理を利用
public class Portal_Ludibrium_S4resur_enter extends PortalScriptMan {

    private final int quest_resurrection = 6134; // リザレクションのクエスト
    private final int item_fear_of_death = 4031448; // 死の恐怖

    @Override
    public boolean enter() {
        if (getQuestStatus(this.quest_resurrection) != 1) {
            // 翻訳
            message("扉の向こうに恐怖を感じます。入れません。扉には鍵がかかっています。");
            return false;
        }
        if (0 < getItemCount(this.item_fear_of_death)) {
            // 翻訳
            message("あなたは闇に打ち勝ちました。闇はあなたを受け入れません。");
            return false;
        }
        TacosFieldSet field_set = getFieldSet("S4resurrection2");
        if (field_set == null || field_set.enter(getPlayer(), 0) != TacosFieldSet.ENTER_SUCCESS) {
            // 翻訳
            message("扉に鍵がかかっていて入れません。");
            return false;
        }
        return true;
    }
}
