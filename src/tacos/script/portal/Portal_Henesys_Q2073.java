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
// Map.wz portal script : q2073 (JMS186), lua の処理を利用
public class Portal_Henesys_Q2073 extends PortalScriptMan {

    private final int quest_yoota = 2073; // ユータの農場のクエスト

    @Override
    public boolean enter() {
        if (getQuestStatus(this.quest_yoota) != 1) {
            // 翻訳
            message("どこかへ通じる扉があるが、中に入ることができない。");
            return false;
        }
        TacosFieldSet field_set = getFieldSet("Yoota");
        if (field_set == null || 0 < field_set.getUserCount() || field_set.enter(getPlayer(), 0) != TacosFieldSet.ENTER_SUCCESS) {
            // 翻訳
            message("誰かがもうユータの農場を訪れたようだ。");
            return false;
        }
        return true;
    }
}
