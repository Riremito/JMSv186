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
public class NpcElNathMagicSpot extends NpcScriptMan {

    private final int item_magic_stone = 4001019; // 魔法石の書
    private final int map_tower = 200080200; // オルビス : オルビス塔<20層>

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b魔法石の書#kを使って#bエルナス魔法石#kを活性化できます。#bオルビス魔法石#kがあるところへ移動しますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : 魔法石の書(item_magic_stone)の確認と消費
                warp(this.map_tower); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
