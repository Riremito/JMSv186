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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcSecondEosRock extends NpcScriptMan {

    private final int item_eos_rock = 4001020; // エオス石の書
    private final int[] map_ids = {
        221024400, // ルディブリアム : エオス塔100階
        221021700 // ルディブリアム : エオス塔41階
    };
    private final String[] rock_names = {
        "一番目のエオス石(100階)",
        "三番目のエオス石(41階)"
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : エオス石の書を持っていない場合の文章
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#bエオス石の書#kを使って#b二番目のエオス石#kを活性化できます。どの石へ移動しますか？");
                for (int i = 0; i < this.map_ids.length; i++) {
                    nt.addMenu(i, this.rock_names[i]);
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                if (!checkItem(this.item_eos_rock)) {
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_eos_rock, 1, true, false);
                warp(this.map_ids[answer]); // TODO : portal 3
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
