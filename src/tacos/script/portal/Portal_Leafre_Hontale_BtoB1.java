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
// Map.wz portal script : hontale_BtoB1 (JMS186), lua の処理を利用
public class Portal_Leafre_Hontale_BtoB1 extends PortalScriptMan {

    private final int item_crystal_key1 = 4001087; // 一番目の迷路部屋の水晶鍵
    private final int map_maze_room1 = 240050101; // リプレ : 一番目の迷路部屋

    @Override
    public boolean enter() {
        int count = getItemCount(this.item_crystal_key1);
        if (count <= 0) {
            // 翻訳
            message("このポータルを通れるのは、最初の迷路の部屋の水晶を持つ者だけです。");
            return false;
        }
        if (!exchange(0, this.item_crystal_key1, -count)) {
            return false;
        }
        transferField(this.map_maze_room1, "st00");
        // 翻訳
        message("最初の迷路の部屋の水晶の力でどこかへ移動しました。");
        return true;
    }
}
