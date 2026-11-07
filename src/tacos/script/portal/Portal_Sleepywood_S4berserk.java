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
// Map.wz portal script : s4berserk (JMS186), lua の処理を利用
public class Portal_Sleepywood_S4berserk extends PortalScriptMan {

    private final int quest_berserk = 6153; // 4次転職のスキルのクエスト
    private final int item_sallam_shield = 4031471; // セイラムの盾
    private final int item_temple_key = 4031475; // 忘れられた神殿の鍵

    @Override
    public boolean enter() {
        if (getQuestStatus(this.quest_berserk) != 1) {
            // 翻訳
            message("封印された場所には入れません。");
            return false;
        }
        if (0 < getItemCount(this.item_sallam_shield)) {
            // 翻訳
            message("セイラムにはすでに盾があります。");
            return false;
        }
        if (getItemCount(this.item_temple_key) <= 0) {
            // 翻訳
            message("入場するには、忘れられた神殿の鍵が必要です。");
            return false;
        }
        TacosFieldSet field_set = getFieldSet("S4berserk");
        if (field_set == null || field_set.enter(getPlayer(), 0) != TacosFieldSet.ENTER_SUCCESS) {
            // 翻訳
            message("現在ほかのキャラクターがクエスト中です。しばらくしてからもう一度お試しください。");
            return false;
        }
        return true;
    }
}
