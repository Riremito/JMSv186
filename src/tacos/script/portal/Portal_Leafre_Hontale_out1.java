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
// Map.wz portal script : hontale_out1 (JMS186), lua の処理を利用
public class Portal_Leafre_Hontale_out1 extends PortalScriptMan {

    private final int item_crystal_key1 = 4001087; // 一番目の迷路部屋の水晶鍵
    private final int item_crystal_key7 = 4001093;
    private final int map_horntail_entrance = 240050400; // リプレ : ホーンテイルの洞窟入口

    @Override
    public boolean enter() {
        // lua の hontale_takeawayitem (4001087 ~ 4001093)
        for (int item_id = this.item_crystal_key1; item_id <= this.item_crystal_key7; item_id++) {
            removeItemAll(item_id);
        }
        return transferField(this.map_horntail_entrance, "st00");
    }
}
