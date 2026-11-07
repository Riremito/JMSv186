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
// Quest.wz startscript : q21010s, endscript : q21010e (JMS186), 英雄の帰還, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_ReturnOfTheHero extends QuestScriptMan {

    private final int item_red_potion = 2000022; // リエン特製赤い薬
    private final int item_blue_potion = 2000023; // リエン特製青い薬
    private final int reward_exp = 16;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 2) {
            // TODO : 文章 (js の英文を翻訳)
            say("いやいや、断らなくていいよ。ただの薬なんだから。それに、あなたのような英雄になら、いくらでもあげるよ！気が変わったら言ってね。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("んん？人間がこんなところで何をしているんだ？あ、#p1201000#じゃないか。どうしてここに？それと…#p1201000#、この人を知っているのかい？え？英雄？", false, true);
                return true;
            }
            case 1 -> {
                say("     #i4001170#", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ということは、君たちの種族が何百年も待ち続けていた、まさにその人が目の前にいるってことかい？すごい！！どうりでほかの人とは少し違って見えたわけだ…", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("でも、暗黒の魔法使いの呪いで何百年も氷に閉じ込められていたせいで、ずいぶん弱っているみたいだね。#b回復用の薬だよ。受け取って#k。");
                return true;
            }
            case 4 -> {
                // TODO : js のコメントでは HP を半分にする処理が必要
                // TODO : 文章 (js の英文を翻訳)
                say("まずはそれを飲んで、それから話を続けよう！", false, true);
                if (getQuestStatus(getQuestId()) == 0) {
                    gainItem(this.item_red_potion, 1);
                    forceStartQuest();
                }
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b（あれ、これはどうやって飲むんだっけ？思い出せない…）#k", true, true);
                return true;
            }
            case 6 -> {
                tutorMsg(0xE);
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("英雄が見つかるかもしれないと思って洞窟の氷の塊を探していたけど…まさか本当に目の前に現れるなんて思わなかったよ！予言は正しかったんだ！#p1201000#、君の言うとおりだったね！英雄がよみがえったからには、もう暗黒の魔法使いの心配をしなくてもいいんだよね？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("おっと、ずいぶん引き止めてしまったね。ごめんよ。でも、ほかのペンギンたちもきっと僕と同じ反応をすると思うな。忙しいのはわかっているけど、村へ行く途中で#bほかのペンギンたちにも話しかけてみて#k。英雄のほうから話しかけたら、みんな驚くはずだよ！ \n\r #fUI/UIWindow.img/QuestIcon/4/0# \r #i" + this.item_red_potion + "# #t" + this.item_red_potion + "# 5 \r #i" + this.item_blue_potion + "# #t" + this.item_blue_potion + "# 5 \n\r #fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " 経験値", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("おお、レベルが上がったね！ということは、スキルポイントも手に入ったはずだよ。メイプルの世界では、レベルが1上がるごとにスキルポイントが3もらえるんだ。#bK#kを押してスキルウィンドウを開いて確かめてみて。", true, true);
                if (getQuestStatus(getQuestId()) == 1) {
                    gainExp(this.reward_exp);
                    gainItem(this.item_red_potion, 5);
                    gainItem(this.item_blue_potion, 5);
                    forceCompleteQuest();
                    questEndEffect();
                }
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(3, "#b（このペンギンたちはこんなにも親切にしてくれるのに、まったく思い出せない。まずはスキルウィンドウを確認しよう…でも、どうやって開くんだっけ？）#k", true, true);
                return true;
            }
            case 4 -> {
                tutorMsg(0xF);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
