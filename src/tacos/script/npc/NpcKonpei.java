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
public class NpcKonpei extends NpcScriptMan {

    private final int map_hideout = 801040000; // ジパング : アジト前

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("用はなんだ。");
                nt.addMenu(0, "アジトの説明を聞く。");
                nt.addMenu(1, "アジトに連れて行ってくれ。");
                nt.addMenu(2, "用はない。");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (answer == 0) {
                    nt.add("オイラはアジトまで案内してやることができるぜ。しかしアジトはガラの悪い連中がウヨウヨいるからよ、力つけて腹くくってから行くことにしなぁ。アジトにはこの近辺の親分たちを取り仕切っている大親分がいる。アジトにはまあ簡単に行けるんだが、アジトの最上階にある部屋には1日1回しか行けねーぞ。大親分の部屋は恐ろしいところだ。あんまり長居しない方が良いから、短時間でケリつけてくれよ！あの大親分もただもんじゃねーが、大親分に会うまでにも腕っぷしの強いやつらがいるから、やっかいだぜ～。");
                    say(nt);
                    return false;
                }
                if (answer == 1) {
                    nt.add("おお、勇者よ待っていたぞ。このまま奴等を放置すれば取り返しがつかなくなる。その前にお前さんの力で、５階にいる大親分を懲らしめてくれ。くれぐれも油断はすんなよ。大親分は幾人もの猛者が敵わなかった兵だ。だが、お前さんの目を見ているとやってくれると確信が持てたぜ。さあ行くぞ。");
                    say(nt, false, true);
                    return true;
                }
                return false;
            }
            case 2 -> {
                warp(this.map_hideout); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
