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
public class NpcChico extends NpcScriptMan {

    private static final int MENU_MAKE = 0;
    private static final int MENU_ABOUT = 1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("こんにちは！俺はこの周辺のミニゲームを統括している#b#p" + getNpcId() + "##kだよ。君、ミニゲームに関心があるようだな。 助けてあげようか。じゃ、やりたいことは？");
                nt.addMenu(MENU_MAKE, "ミニゲームのアイテム作成");
                nt.addMenu(MENU_ABOUT, "ミニゲームに対する説明を聞く");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                // TODO : ミニゲームアイテム (五目並べ, 神経衰弱) の作成, ミニゲームの説明 (文章が不明)
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
