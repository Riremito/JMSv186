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
// Map.wz portal script : s4nest (JMS186), lua の処理を利用
public class Portal_Leafre_S4nest extends PortalScriptMan {

    private final int quest_phoenix2 = 6241; // フェニックスのクエスト (2回目)
    private final int quest_freezer2 = 6243; // フリズベルクのクエスト (2回目)
    private final int item_phoenix_egg = 4001113; // フェニックスの卵
    private final int item_freezer_egg = 4001114; // フリズベルクの卵
    private final int job_bowmaster = 312;
    private final int job_crossbow_master = 322;

    @Override
    public boolean enter() {
        if (getQuestStatus(this.quest_phoenix2) != 1 && getQuestStatus(this.quest_freezer2) != 1) {
            // 翻訳
            message("封印された場所には入れません。");
            return false;
        }
        if (getJob() == this.job_bowmaster) {
            if (getItemCount(this.item_phoenix_egg) <= 0) {
                // 翻訳
                message("フェニックスの卵を持っていません。入場できません。");
                return false;
            }
        } else if (getJob() == this.job_crossbow_master) {
            if (getItemCount(this.item_freezer_egg) <= 0) {
                // 翻訳
                message("フリーザーの卵を持っていません。入場できません。");
                return false;
            }
        } else {
            return false;
        }
        TacosFieldSet field_set = getFieldSet("S4nest");
        if (field_set == null || field_set.enter(getPlayer(), 0) != TacosFieldSet.ENTER_SUCCESS) {
            // 翻訳
            message("ほかのキャラクターが依頼を受けています。入場できません。");
            return false;
        }
        return true;
    }
}
