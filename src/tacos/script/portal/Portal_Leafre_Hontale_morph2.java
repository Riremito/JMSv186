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

/**
 *
 * @author Riremito
 */
// Map.wz portal script : hontale_morph2 (JMS186), lua の処理を利用
public class Portal_Leafre_Hontale_morph2 extends PortalScriptMan {

    private final int map_big_nest = 240040600; // リプレ : 大きい巣の峰

    @Override
    public boolean enter() {
        // TODO : 変身 (morph 4, ドラゴン) の解除は未実装
        return transferField(this.map_big_nest, "east00");
    }
}
