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
package tacos.script.npc;

/**
 *
 * @author Riremito
 */
// Npc.wz script : Go_boss2_out (JMS164-302), lua が存在しないため旧 js (scripts_msea102) を参考に実装
public class Npc_Zipangu_Marr extends NpcScriptMan {

    private final int map_wait = 802000310; // ジパング : 公園 2095年
    private final int map_boss = 802000311; // ジパング : 公園 2095年
    private final int map_clear = 802000313; // ジパング : 公園 2095年
    private final int item_transmitter = 4032192; // エネルギー転送装置
    private final int transmitter_count = 10;

    @Override
    public boolean action(int status, int answer) {
        // ボスマップ以外では何もしない
        if (getMapId() != this.map_boss) {
            return false;
        }
        if (status == 0) {
            NpcScriptTag nt = new NpcScriptTag();
            // TODO : 文章 (適当)
            nt.add("うわあああん！\r\n#b#L0# #t" + this.item_transmitter + "#を渡す#l\r\n#b#L1# 今は逃げる#l", false);
            askMenu(nt);
            return true;
        }
        if (status != 1) {
            return false;
        }
        if (answer == 0) {
            if (!exchange(0, this.item_transmitter, -this.transmitter_count)) {
                // TODO : 文章 (適当)
                say("#t" + this.item_transmitter + "#を" + this.transmitter_count + "個集めてきて！");
                return false;
            }
            warp(this.map_clear);
            return false;
        }
        if (answer == 1) {
            warp(this.map_wait);
        }
        return false;
    }
}
