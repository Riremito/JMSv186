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

import odin.server.maps.SavedLocationType;

/**
 *
 * @author Riremito
 */
public class NpcMuLungDojoBulletinBoard extends NpcScriptMan {

    private final int map_dojo = 925020000; // 武陵道場 : 武陵道場
    private final int MENU_CHALLENGE = 0;
    private final int MENU_READ = 1;
    private int selected = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                return false;
            }
            case 0 -> {
                if (0 <= this.selected) {
                    // いいえを選択した
                    // TODO : 文章 (適当)
                    say("#b(掲示板から慌てて手を離すと、不思議なオーラも消えてしまった。)");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("#e<告知>#n\r\n武陵道場に挑戦する度胸のある者は、すぐに道場を訪れよ。 - ムゴン -\r\n");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_CHALLENGE, "武陵道場に挑戦する");
                // TODO : 文章 (適当)
                nt.addMenu(MENU_READ, "告知を詳しく読む");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (this.selected < 0) {
                    this.selected = answer;
                }
                if (this.selected == MENU_READ) {
                    // TODO : 文章 (適当)
                    say("#e<告知 : 挑戦状！>#n\r\n私は武陵道場の真の主、ムゴンだ。昔、達人になることを夢見て武陵に来た私は、今やすべての武術を極めた。武陵道場の前の主は、戦いを嫌う弱き者だった。だからこそ、これからは私が武陵道場を引き継ぐ。\r\n武陵道場は、武陵で最も強い者が持つべきものだ。\r\n最強の者から学びたければ、挑戦するがいい！私に直接挑んでも構わない。お前の体の隅々にまで偉大さを刻み込んでやろう。", false, true);
                    return true;
                }
                // TODO : 文章 (適当)
                askYesNo("#b(掲示板に手を触れると、不思議なオーラが全身を包み始めた。)#k\r\n\r\n武陵道場へ移動しますか？");
                return true;
            }
            case 2 -> {
                if (this.selected == MENU_READ) {
                    // TODO : 文章 (適当)
                    say("追伸 : 一人で来ても構わないが、もしお前が私の思う通りの臆病者なら、仲間と一緒に来てもいいぞ。", true, false);
                    return false;
                }
                getPlayer().saveLocation(SavedLocationType.MULUNG_TC);
                warp(this.map_dojo);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
