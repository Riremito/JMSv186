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
// Quest.wz startscript : q20600s, q20610s (JMS186), 終わらない修行, まだ終わらない修行, 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_EndlessTraining extends QuestScriptMan {

    private final int quest_endless_training = 20600; // 終わらない修行

    @Override
    public boolean questStart(int status, int answer) {
        boolean endless = (getQuestId() == this.quest_endless_training);
        switch (status) {
            case -1 -> {
                // 受諾しなかった
                if (!endless) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("今のあなたの態度は、謙虚な人には見えませんね。ただ慢心しているように見えるだけです。それは決してよいことではありません。");
                }
                return false;
            }
            case 0 -> {
                if (endless) {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("#h0#。レベル100になってから修行を怠けていませんか？あなたが強いことはみんな知っていますが、修行はまだ終わっていません。騎士団長たちを見てください。暗黒の魔法使いと出会うときに備えて、昼も夜も修行しているのですよ。");
                } else {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("スキルを極めていますか？もうすべてのスキルを極めたのでしょう。ということは…#b新しいスキル#kを覚える時が来たということですね？");
                }
                return true;
            }
            case 1 -> {
                forceStartQuest();
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
