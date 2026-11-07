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
package tacos.script.quest;

/**
 *
 * @author Riremito
 */
// Quest.wz startscript : q3320s (JMS186), ファウェンが知っていること, lua の処理を利用
public class Quest_Magatia_WhatParwenKnows extends QuestScriptMan {

    private final int map_drang_lab = 926120200; // ドランの研究室
    private final String portal_drang_lab = "out00";

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            // 翻訳
            say("えっ？嫌なのかい？君が嫌なら仕方ないけど…それじゃあ、ここで研究していた錬金術師を教えてあげられないよ？");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("おお！君か！よく来た、よく来た！君のおかげで最近は退屈しないよ～…ん？何だって？ここで研究していた錬金術師は誰かって？うーん…彼の名前は知っていたんだが…", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("何だったかな？何だったか…何だったっけ…ああ！どうしても思い出せない。もしかして、その人は君にとって大事な人なのかい？できれば忘れてしまえば…だめだって？じゃあどうしようか…", true, true);
                return true;
            }
            case 2 -> {
                // 翻訳
                askAccept("ええい！わからん。君が直接見てくれ！");
                return true;
            }
            case 3 -> {
                forceStartQuest();
                warp(this.map_drang_lab, this.portal_drang_lab);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
