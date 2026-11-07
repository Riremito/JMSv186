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
// Quest.wz startscript : q21012s, endscript : q21012e (JMS186), 消えた能力, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_MissingAbility extends QuestScriptMan {

    private final int item_red_potion = 2000022; // リエン特製赤い薬
    private final int item_blue_potion = 2000023; // リエン特製青い薬
    private final int reward_exp = 57;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            // TODO : 文章 (js の英文を翻訳)
            say("ふむ…役に立たないと思うのかい？考えてみなよ。役に立つかもしれないよ…", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ようこそ、英雄さん！え？どうしてあなたのことを知っているのかって？簡単さ。隣で大声で話している人たちの話を聞いちゃったんだ。噂はもう島中に広まっているはずだよ。あなたが帰ってきたことは、みんな知ってるさ！", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ところで、浮かない顔だね。何かあったのかい？ん？本当に英雄なのかどうか自信がない？記憶をなくした？！そんな…何百年も氷の中に閉じ込められていたせいに違いないね。", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("ふむ、その剣を使ってみたらどうだい？何か思い出すかもしれないよ。#bモンスターと戦ってみる#kのはどうだい？");
                return true;
            }
            case 3 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("ちょうどこの近くに#r#o9300383##kがたくさんいるんだ。#r3匹#kだけ倒してみたらどうだい？何か思い出すかもしれないよ。", false, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(1, "あれ、スキルの使い方も忘れちゃったのかい？#bスキルをクイックスロットに登録すると使いやすくなるよ。#k消費アイテムも登録できるから、うまく活用してね。", true, true);
                return true;
            }
            case 5 -> {
                tutorMsg(17);
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("え？薬はいらないのかい？", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("ふむ…その顔を見ると、体を動かしても何も思い出せなかったみたいだね。でも心配いらないよ。そのうち思い出すさ。ほら、この薬を飲んで元気を出して！ \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_red_potion + "# 10 #t" + this.item_red_potion + "# \r\n#i" + this.item_blue_potion + "# 10 #t" + this.item_blue_potion + "# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " 経験値");
                return true;
            }
            case 1 -> {
                gainItem(this.item_red_potion, 10);
                gainItem(this.item_blue_potion, 10);
                gainExp(this.reward_exp);
                forceCompleteQuest();
                questEndEffect();
                // js の sendOkS は type が反映されない
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（みんなが言うように本当に英雄だとしても…スキルが使えないのでは何の役にも立たない。）#k", false, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
