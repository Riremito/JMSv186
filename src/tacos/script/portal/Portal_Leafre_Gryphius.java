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
// Map.wz portal script : gryphius (JMS186), lua の処理を利用
// テストサーバー (serverType == 2) の分岐は削除
public class Portal_Leafre_Gryphius extends PortalScriptMan {

    private final int map_griffey_forest = 240020101; // リプレ : グリフォンの森
    private final int user_max = 6;

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Gryphius");
        if (field_set == null) {
            return false;
        }
        if (field_set.getUserCount() < this.user_max) {
            return transferField(this.map_griffey_forest, "");
        }
        // 翻訳
        message("グリフィーの森には入れません。");
        return false;
    }
}
