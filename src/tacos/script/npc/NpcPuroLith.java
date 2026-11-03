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
public class NpcPuroLith extends NpcScriptMan {

    private final int map_penguin_port = 140020300; // スノーアイランド : ペンギン港

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("もしやビクトリアアイランドを離れ、我々の村に行くつもりか？この船に乗ると#bリエン#kまで乗せていってやれるが…#b料金800#kメル必要だ。リエンに行くかい？行くのにかかる時間は、訳1分だ。");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : 800 メルの支払い, 乗船
                warp(this.map_penguin_port); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
