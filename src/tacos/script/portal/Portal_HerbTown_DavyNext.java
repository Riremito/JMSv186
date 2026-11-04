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
// Map.wz portal script : davy_next0, davy_next2, davy_next3 (JMS186), lua の処理を利用
// モンスターを全て倒すと次のステージへ移動できる
public class Portal_HerbTown_DavyNext extends PortalScriptMan {

    private final int mob_ginseng_jar = 9300108; // デイビーゾーンのルツボ
    private final int mob_ginseng_jar2 = 9300109; // デイビーゾーンの人参ルツボ
    private final int mob_bellflower = 9300110; // デイビーゾーンのキキョウ
    private final int mob_bellflower2 = 9300111; // デイビーゾーンのキキョウ仙人
    private final int mob_crocky = 9300123; // デイビーゾーンのクロコファミリー
    private final int mob_crew = 9300124; // デイビーゾーンのクルー
    private final int mob_master_crew = 9300125; // デイビーゾーンのマスタークルー
    private final int map_stage2 = 925100100;
    private final int map_stage4 = 925100300;
    private final int map_stage5 = 925100400;

    @Override
    public boolean enter() {
        String name;
        String name_next;
        String key_clear;
        int map_next;
        int mob_count;
        switch (getScriptName()) {
            case "davy_next0" -> {
                name = "DavyJohn1";
                name_next = "DavyJohn2";
                key_clear = "clear_1";
                map_next = this.map_stage2;
                mob_count = getMobCount(getMapId(), this.mob_ginseng_jar) + getMobCount(getMapId(), this.mob_ginseng_jar2) + getMobCount(getMapId(), this.mob_bellflower) + getMobCount(getMapId(), this.mob_bellflower2);
            }
            case "davy_next2" -> {
                name = "DavyJohn3";
                name_next = "DavyJohn4";
                key_clear = "clear_3";
                map_next = this.map_stage4;
                mob_count = getMobCount(getMapId(), this.mob_crocky) + getMobCount(getMapId(), this.mob_crew) + getMobCount(getMapId(), this.mob_master_crew);
            }
            default -> {
                // lua では davy_next3 も clear_3 を利用している
                name = "DavyJohn4";
                name_next = "DavyJohn5";
                key_clear = "clear_3";
                map_next = this.map_stage5;
                mob_count = getMobCount(getMapId(), this.mob_crocky) + getMobCount(getMapId(), this.mob_crew) + getMobCount(getMapId(), this.mob_master_crew);
            }
        }
        TacosFieldSet field_set = getFieldSet(name);
        if (field_set == null) {
            return false;
        }
        if (0 < mob_count) {
            if (getScriptName().equals("davy_next0")) {
                // 翻訳
                message("次のステージへ進むには、このエリアのモンスターをすべて倒す必要があります。");
            } else {
                // 翻訳
                message("ポータルは現在封印されています。");
            }
            return false;
        }
        if (field_set.getVar(key_clear).equals("1")) {
            return transferField(map_next, "");
        }
        TacosFieldSet field_set_next = getFieldSet(name_next);
        int res = field_set_next == null ? TacosFieldSet.ENTER_ERROR : field_set_next.enter(getPlayer(), 0);
        if (res == TacosFieldSet.ENTER_ERROR) {
            // 翻訳
            message("現在このポータルは使用できません。しばらくしてからもう一度お試しください。");
            return false;
        }
        if (res != TacosFieldSet.ENTER_SUCCESS) {
            return false;
        }
        field_set.setVar(key_clear, "1");
        return true;
    }
}
