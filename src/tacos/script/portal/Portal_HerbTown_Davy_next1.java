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
// Map.wz portal script : davy_next1 (JMS186), lua の処理を利用
public class Portal_HerbTown_Davy_next1 extends PortalScriptMan {

    private final int map_stage3 = 925100200;
    private final int time_limit = 360;

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("DavyJohn2");
        if (field_set == null) {
            return false;
        }
        if (this.time_limit < field_set.getQuestTime()) {
            // 翻訳
            message("時間切れのため、次のステージへ進むことはできません。");
            return false;
        }
        if (field_set.getVar("clear_2").equals("1")) {
            return transferField(this.map_stage3, "");
        }
        if (!field_set.getVar("mobGen").equals("end")) {
            // 翻訳
            message("ポータルは現在封印されています。");
            return false;
        }
        TacosFieldSet field_set_next = getFieldSet("DavyJohn3");
        int res = field_set_next == null ? TacosFieldSet.ENTER_ERROR : field_set_next.enter(getPlayer(), 0);
        if (res == TacosFieldSet.ENTER_ERROR) {
            // 翻訳
            message("現在このポータルは使用できません。しばらくしてからもう一度お試しください。");
            return false;
        }
        if (res != TacosFieldSet.ENTER_SUCCESS) {
            return false;
        }
        field_set.setVar("clear_2", "1");
        return true;
    }
}
