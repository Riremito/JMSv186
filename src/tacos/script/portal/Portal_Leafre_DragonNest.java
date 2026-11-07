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
// Map.wz portal script : dragonNest (JMS186), lua の処理を利用
public class Portal_Leafre_DragonNest extends PortalScriptMan {

    private final int item_nine_spirit_egg = 4001094; // ナインスピリットの卵

    @Override
    public boolean enter() {
        if (getItemCount(this.item_nine_spirit_egg) <= 0) {
            // 翻訳
            message("この場所に入るには、九霊の卵を持っている必要があります..");
            return false;
        }
        TacosFieldSet field_set = getFieldSet("DragonBaby");
        if (field_set == null || 0 < field_set.getUserCount() || field_set.enter(getPlayer(), 0) != TacosFieldSet.ENTER_SUCCESS) {
            // 翻訳
            message("すでにほかの人がクエストを完了しようと中に入っています。しばらくしてからもう一度お試しください。");
            return false;
        }
        return true;
    }
}
