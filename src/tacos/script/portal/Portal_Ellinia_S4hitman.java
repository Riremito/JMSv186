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
// Map.wz portal script : s4hitman (JMS186), lua の処理を利用
public class Portal_Ellinia_S4hitman extends PortalScriptMan {

    private final int quest_hitman = 6201; // シャンの依頼のクエスト
    private final int item_request = 4031452; // シャンの依頼品

    @Override
    public boolean enter() {
        if (getQuestStatus(this.quest_hitman) != 1) {
            // 翻訳
            message("行けないぞ。ショーンの依頼を受けていないだろう。");
            return false;
        }
        if (0 < getItemCount(this.item_request)) {
            // 翻訳
            message("ショーンの依頼は完了している。もう一度入る必要はない。");
            return false;
        }
        TacosFieldSet field_set = getFieldSet("S4hitman");
        if (field_set == null || field_set.enter(getPlayer(), random(0, 2)) != TacosFieldSet.ENTER_SUCCESS) {
            // 翻訳
            message("ほかのキャラクターが依頼を受けています。入場できません。");
            return false;
        }
        return true;
    }
}
