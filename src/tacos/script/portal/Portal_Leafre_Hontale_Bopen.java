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

import tacos.packet.ops.OpsBroadcastMsg;
import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : hontale_Bopen (JMS186), lua の処理を利用
// 迷路部屋の扉が開いている場合はマップにいるキャラクターを全員次の部屋へ移動させる
public class Portal_Leafre_Hontale_Bopen extends PortalScriptMan {

    private final int item_red_key = 4001092; // 赤い鍵
    private final int map_maze_room = 240050100; // リプレ : 迷路部屋
    private final int map_maze_room1 = 240050101; // リプレ : 一番目の迷路部屋
    private final int map_maze_room5 = 240050105; // リプレ : 五番目の迷路部屋

    @Override
    public boolean enter() {
        TacosFieldSet field_set = getFieldSet("Hontale1");
        if (field_set == null) {
            return false;
        }
        if (getMapId() == this.map_maze_room5) {
            if (getItemCount(this.item_red_key) <= 0) {
                // 翻訳
                message("移動するには赤い鍵の力が必要です。");
                return false;
            }
            // 翻訳
            fieldNotice(OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX, "赤い鍵の力でどこかへ移動しました。");
            return transferFieldAll(this.map_maze_room, "st00");
        }
        // 一番目の迷路部屋 = Bdoor1, 二番目の迷路部屋 = Bdoor2, ...
        int index = getMapId() - this.map_maze_room1 + 1;
        if (index < 1 || 4 < index) {
            return false;
        }
        if (!field_set.getVar("Bdoor" + index).equals("1")) {
            // 翻訳
            message("現在ポータルは作動していません。");
            return false;
        }
        return transferFieldAll(getMapId() + 1, "st00");
    }
}
