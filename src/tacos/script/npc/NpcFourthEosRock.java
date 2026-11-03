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
public class NpcFourthEosRock extends NpcScriptMan {

    private final int item_eos_rock = 4001020; // エオス石の書
    private final int map_eos_tower = 221021700; // ルディブリアム : エオス塔41階

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (!checkItem(this.item_eos_rock)) {
                    // 翻訳
                    say("#b#p2040026##kへテレポートできる石がありますが、書がないと起動できません。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#bエオス石の書#kを使って#b四番目のエオス石#kを活性化できます。#b三番目のエオス石#kがあるところへ移動しますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                if (!checkItem(this.item_eos_rock)) {
                    // 翻訳
                    say("#b#t" + this.item_eos_rock + "##kがないと#b#p2040027##kを起動できません。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_eos_rock, 1, true, false);
                warp(this.map_eos_tower, "go00");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
