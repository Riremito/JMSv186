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
// Npc.wz script : pigmy_guide (JMS131-302), 会話文は lua_script_jp (pigmy_guide) を利用
public class NpcPigmyGuide extends NpcScriptMan {

    private final int item_feed = 2120008; // ピグミーのエサ
    private boolean menu = true;

    @Override
    public boolean action(int status, int answer) {
        if (this.menu) {
            this.menu = false;
            // 翻訳
            NpcScriptTag nt = new NpcScriptTag();
            nt.add("#b<ピグミーの専門家エトラン>#k \r  ピグミーについての詳しい情報をまとめたガイドを用意したよ。ピグミーのことをもっとよく知る手助けになるはずさ！\r\r\n#b#L0# ピグミーって何？#l \r\n#b#L1# ピグミーは何を食べるの？#l \r\n#b#L2# ピグミーの卵って何？#l", false);
            askMenu(nt);
            return true;
        }
        this.menu = true;
        // 翻訳
        switch (answer) {
            case 0 -> {
                say("ピグミーは、僕の魔法実験のひとつで偶然生まれたんだ。おとなしい動物さ。でも、すごくたくさん食べるんだよ…", false, true);
                return true;
            }
            case 1 -> {
                say("ピグミーはお店で売っている#b#t" + this.item_feed + "##kしか食べないんだ。お店でエサを買って、ピグミーにあげてね。ああ、そうそう！ピグミーは1日に5回しか食べないんだ。だから、ずっとエサをあげ続けようとしないでね。", false, true);
                return true;
            }
            case 2 -> {
                say("#bピグミーの卵#kは、ピグミーが産んだ卵だよ。#bピグミーにエサをあげると機嫌がよくなって、卵を産むことがあるんだ。#kこの卵の中には面白いものがたくさん入っているよ。でも、殻は特別な道具でしか割れないんだ。ピグミーの卵を開けるには、キャッシュショップのETCタブで#b孵化器を買う必要がある#kよ。", false, true);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
