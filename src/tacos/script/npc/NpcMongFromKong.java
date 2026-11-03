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
public class NpcMongFromKong extends NpcScriptMan {

    private final int map_internet_cafe = 193000000; // ネットカフェ
    private final int item_internet_cafe = 5420007; // TODO : アイテム名

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("もしかしてネットカフェから接続してるんじゃないのか?ならさ、中に入ってきなよ。君のよく知った場所にいけるはずさ。どうだ、ちょっくら入ってみないか?");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : ネットカフェから接続しているかの確認 (item_internet_cafe)
                // あれ、君はネットカフェから接続してないみたいだね?もし、そうなら中には絶対入れないよ…。
                warp(this.map_internet_cafe);
                return false;
            }
            case -1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("今は忙しそうだな?でもな?せっかくネットカフェから接続してるんなら、中に入ってみればいいのに。不思議な場所に行けるかもしれないよ。");
                say(nt);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
