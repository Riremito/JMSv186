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
public class Npc_Orbis_ElNathMagicSpot extends NpcScriptMan {

    private final int item_magic_stone = 4001019; // 魔法石の書
    private final int map_tower = 200080200; // オルビス : オルビス塔<20層>

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (!checkItem(this.item_magic_stone)) {
                    // 翻訳
                    say("#b#p2012014##kのある場所へテレポートできる#b#p2012015##kがありますが、書がないと起動できません。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b魔法石の書#kを使って#bエルナス魔法石#kを活性化できます。#bオルビス魔法石#kがあるところへ移動しますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                if (!checkItem(this.item_magic_stone)) {
                    // 翻訳
                    say("#b#t" + this.item_magic_stone + "##kを持っていないので、#b#p2012015##kを起動できません。");
                    return false;
                }
                removeItem(this.item_magic_stone, 1);
                warp(this.map_tower, "sp");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
