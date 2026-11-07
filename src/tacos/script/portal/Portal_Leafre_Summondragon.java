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
// Map.wz portal script : summondragon (JMS186), lua の処理を利用
public class Portal_Leafre_Summondragon extends PortalScriptMan {

    private final int item_nine_spirit_egg = 4001094; // ナインスピリットの卵

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("DragonBaby");
        if (field_set == null) {
            return false;
        }
        if (field_set.getVar("action").equals("1") || getItemCount(this.item_nine_spirit_egg) <= 0) {
            return false;
        }
        if (!exchange(0, this.item_nine_spirit_egg, -1)) {
            // 翻訳
            message("しばらくしてからもう一度お試しください。");
            return false;
        }
        field_set.setReactorState(0, "dragonBaby", 1);
        // 翻訳
        message("心地よく巣に収まっていた九霊の卵が神秘的な光を放ち、巣に戻っていきました。");
        field_set.setVar("action", "1");
        return false;
    }
}
