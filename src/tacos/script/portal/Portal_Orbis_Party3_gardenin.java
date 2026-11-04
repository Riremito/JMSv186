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
// Map.wz portal script : party3_gardenin (JMS186), lua の処理を利用
public class Portal_Orbis_Party3_gardenin extends PortalScriptMan {

    private final int item_grass_of_life = 4001055; // 生命の草
    private final int map_center_tower = 920010100; // 女神の塔 : 中央塔

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Party5");
        if (field_set == null) {
            return false;
        }
        if (!isPartyBoss()) {
            // 翻訳
            message("この部屋から出るかどうかを決められるのはパーティーリーダーだけです。");
            return false;
        }
        if (getItemCount(this.item_grass_of_life) <= 0) {
            // 翻訳
            message("生命の草の力が必要です。");
            return false;
        }
        field_set.transferFieldAll(this.map_center_tower, "st02");
        return true;
    }
}
