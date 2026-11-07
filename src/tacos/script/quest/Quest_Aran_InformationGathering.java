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
// Quest.wz startscript : q21712s, q21716s (JMS186), 1番目の情報収集の完了, 2番目の情報収集 完了, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_InformationGathering extends QuestScriptMan {

    private final int quest_first = 21712; // 1番目の情報収集の完了

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_first) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == 1) {
                // TODO : 文章 (js の英文を翻訳)
                say("まだ状況がわからないのかい？もう一度話しかけてくれれば、また説明してあげるよ。", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say("#t4032315#…#rこの人形が変な音を出しているんだ#k。もちろん耳では聞こえないよ。#o1210102#にしか聞こえない音だからね。この音のせいで#o1210102#の性格が変わってしまったんだと思う。", false, true);
                    return true;
                }
                case 1 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("音の影響を受けた#o1210102#はひねくれてしまって、影響を受けていない#o1210102#と争い始めたんだ。そのせいで#o1210102#がみんな戦う態勢になってしまった。#b#o1210102#がこんなふうに変わってしまった原因は、この人形なんだ#k！わかったかい？");
                    return true;
                }
                case 2 -> {
                    forceStartQuest();
                    // TODO : 文章 (js の英文を翻訳)
                    say(9, "そもそも何がきっかけだったんだろう。この人形が自然にできるはずがないから、誰かが仕組んだということだ。#o1210102#から目を離さないようにしないと。", false, true);
                    return true;
                }
                case 3 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "#b（#o1210102#が変わってしまった原因がわかった。#p1002104#に報告して、集めた情報を伝えよう。）#k", true, false);
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            // TODO : 文章 (js の英文を翻訳)
            say("何ですって？あの子以外に怪しい人はいないと思うけど。もう一度よく考えてみて。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "#p1032112#は何て言っていたの？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（#p1032112#が見たことを話した。）#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("人形を持った子ども？それはとても怪しいわね。緑キノコが急に凶暴になったのは、きっとその子のせいだわ。");
                return true;
            }
            case 3 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say(2, "南の森をこんなに荒らすなんて、許せない子ね。森が元に戻るまでどのくらいかかるかわからないわ…しばらくは後片付けにかかりきりになりそう。", false, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（緑キノコが変わってしまった原因がわかった。#p1002104#に報告して、集めた情報を伝えよう。）#k", true, false);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
