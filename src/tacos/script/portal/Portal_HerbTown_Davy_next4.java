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
// Map.wz portal script : davy_next4 (JMS186), lua の処理を利用
public class Portal_HerbTown_Davy_next4 extends PortalScriptMan {

    private final int mob_crocky = 9300120; // デイビーゾーンの乱暴なクロコファミリー
    private final int mob_crew = 9300121; // デイビーゾーンの乱暴なクルー
    private final int mob_master_crew = 9300122; // デイビーゾーンの乱暴なマスタークルー
    private final int mob_ginseng_jar = 9300126; // デイビーゾーンの乱暴な人参ルツボ
    private final int map_stage5 = 925100400;
    private final int map_stage6 = 925100500;

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("DavyJohn5");
        if (field_set == null) {
            return false;
        }
        if (field_set.getVar("clear_5").equals("1")) {
            return transferField(this.map_stage6, "");
        }
        int mob_count = getMobCount(this.map_stage5, this.mob_crocky) + getMobCount(this.map_stage5, this.mob_crew) + getMobCount(this.map_stage5, this.mob_master_crew) + getMobCount(this.map_stage5, this.mob_ginseng_jar);
        boolean reactors = true;
        for (int i = 1; i <= 4; i++) {
            if (field_set.getReactorState(0, "sMob" + i) != 1) {
                reactors = false;
            }
        }
        if (mob_count != 0 || !reactors) {
            // 翻訳
            message("ポータルは現在封印されています。");
            return false;
        }
        TacosFieldSet field_set_next = getFieldSet("DavyJohn6");
        int res = field_set_next == null ? TacosFieldSet.ENTER_ERROR : field_set_next.enter(getPlayer(), 0);
        if (res == TacosFieldSet.ENTER_ERROR) {
            // 翻訳
            message("現在このポータルは使用できません。しばらくしてからもう一度お試しください。");
            return false;
        }
        if (res != TacosFieldSet.ENTER_SUCCESS) {
            return false;
        }
        field_set.setVar("clear_5", "1");
        return true;
    }
}
