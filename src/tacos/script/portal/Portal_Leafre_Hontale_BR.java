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
// Map.wz portal script : hontale_BR (JMS186), lua の処理を利用
// 試験の洞窟のホーンテイルの頭を倒すと次のマップへ移動できる
public class Portal_Leafre_Hontale_BR extends PortalScriptMan {

    private final int mob_horntail_left = 8810000; // ホーンテイルの左頭
    private final int mob_horntail_right = 8810001; // ホーンテイルの右頭
    private final int map_trial_cave1 = 240060000; // リプレ : 試験の洞窟1
    private final int map_trial_cave2 = 240060100; // リプレ : 試験の洞窟2
    private final int map_horntail_cave = 240060200; // リプレ : ホーンテイルの洞窟

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Hontale3");
        if (field_set == null) {
            return false;
        }
        if (getMapId() == this.map_trial_cave1) {
            if (field_set.getVar("boss1").equals("1") && getMobCount(getMapId(), this.mob_horntail_left) < 1) {
                return transferField(this.map_trial_cave2, "st00");
            }
        } else if (getMapId() == this.map_trial_cave2) {
            if (field_set.getVar("boss2").equals("1") && getMobCount(getMapId(), this.mob_horntail_right) < 1) {
                return transferField(this.map_horntail_cave, "st00");
            }
        } else {
            return false;
        }
        // 翻訳
        message("現在ポータルは作動していません。");
        return false;
    }
}
