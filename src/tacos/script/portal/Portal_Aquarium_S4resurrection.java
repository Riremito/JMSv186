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
// Map.wz portal script : s4resurrection (JMS186), lua の処理を利用
public class Portal_Aquarium_S4resurrection extends PortalScriptMan {

    private final int quest_resurrection = 6132; // リザレクションのクエスト
    private final int item_charon_note = 4161017; // カロンのノート

    @Override
    public boolean enter() {
        if (getQuestStatus(this.quest_resurrection) != 1) {
            // 翻訳
            message("洞窟から漂う冷気のせいで入れません。");
            return false;
        }
        if (0 < getItemCount(this.item_charon_note)) {
            // 翻訳
            message("すでにカロンのメモを持っているので入場できません。");
            return false;
        }
        TacosFieldSet field_set = getFieldSet("S4resurrection1");
        if (field_set == null || field_set.enter(getPlayer(), 0) != TacosFieldSet.ENTER_SUCCESS) {
            // 翻訳
            message("冷たい洞窟の中に誰かがいる気配がします。");
            return false;
        }
        return true;
    }
}
