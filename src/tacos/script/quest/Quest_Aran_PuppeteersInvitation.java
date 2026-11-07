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
// Quest.wz startscript : q21719s (JMS186), 人形使いの招待, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_PuppeteersInvitation extends QuestScriptMan {

    private final int map_cave = 910510200; // 隠れ道 : 人形使いの洞窟

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "少し前まで#m101000000#にいた奴じゃないか？やっと見つけたぞ！見つけるのにどれだけかかったと思ってるんだ？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "お前は誰だ？", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("俺か？知りたければ俺の洞窟に来い。招待状を送ってやろう。受け取ればすぐに俺の洞窟へ送られる。待っているぞ。");
                return true;
            }
            case 3 -> {
                forceCompleteQuest();
                questEndEffect();
                warp(this.map_cave);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
