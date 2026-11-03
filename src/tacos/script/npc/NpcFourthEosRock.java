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
public class NpcFourthEosRock extends NpcScriptMan {

    private final int item_eos_rock = 4001020; // エオス石の書
    private final int map_eos_tower = 221021700; // ルディブリアム : エオス塔41階

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#bエオス石の書#kを使って#b四番目のエオス石#kを活性化できます。#b三番目のエオス石#kがあるところへ移動しますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : エオス石の書(item_eos_rock)の確認と消費, portal 3
                warp(this.map_eos_tower); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
