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
public class NpcThirdEosRock extends NpcScriptMan {

    private final int item_eos_rock = 4001020; // エオス石の書
    private final int[] npc_ids = {
        2040025,
        2040027
    };
    private final int[] map_ids = {
        221022900, // ルディブリアム : エオス塔71階
        221020000 // ルディブリアム : エオス塔1階
    };
    private final int[] floors = {
        71,
        1
    };
    private final String[] rock_names = {
        "二番目のエオス石(71階)",
        "四番目のエオス石(1階)"
    };
    private int to_index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (!checkItem(this.item_eos_rock)) {
                    // 翻訳
                    say("#b#p" + this.npc_ids[0] + "#か#p" + this.npc_ids[1] + "##kへテレポートできる石がありますが、書がないと起動できません。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#bエオス石の書#kを使って#b三番目のエオス石#kを活性化できます。どの石へ移動しますか？");
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
                this.to_index = answer;
                // 翻訳
                askYesNo("#b#t" + this.item_eos_rock + "##kを使って#b#p2040026##kを起動できます。#b#p" + this.npc_ids[this.to_index] + "##k（" + this.floors[this.to_index] + "階）へテレポートしますか？");
                return true;
            }
            case 2 -> {
                if (!checkItem(this.item_eos_rock)) {
                    // 翻訳
                    say("#b#t" + this.item_eos_rock + "##kがないと#b#p2040026##kを起動できません。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_eos_rock, 1, true, false);
                warp(this.map_ids[this.to_index], "go00");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
