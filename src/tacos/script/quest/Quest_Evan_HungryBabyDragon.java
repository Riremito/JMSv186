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
// Quest.wz startscript : q22501s - q22504s (JMS186), お腹が空いたちびドラゴン, 乾草を一口, オオカミ肉を一口, 美味しい牛乳1, 旧 js (scripts_JMS) を参考に実装
public class Quest_Evan_HungryBabyDragon extends QuestScriptMan {

    private final int quest_hungry = 22501; // お腹が空いたちびドラゴン
    private final int quest_hay = 22502; // 乾草を一口
    private final int quest_pork = 22503; // オオカミ肉を一口

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_hungry) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == 2) {
                // TODO : 文章 (js の英文を翻訳)
                say("*はっ* 自分のドラゴンに餌をくれないなんて！これは虐待だぞ！", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say("よう、ご主人様。俺の力は見せたから、今度はお前の番だ。食べ物を見つけられるってことを証明してみせろ！腹ぺこなんだ。もう俺の力が使えるんだから、俺の面倒を見てくれよな。", false, true);
                    return true;
                }
                case 1 -> {
                    forceStartQuest();
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "うーん、まだよくわからないけど、君みたいなかわいそうなちびを飢えさせるわけにはいかないよね？食べ物だって？何が食べたいの？", true, true);
                    return true;
                }
                case 2 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say("あのな、俺はほんの数分前に生まれたばかりなんだぞ。何を食べるかなんてわかるわけないだろ？わかってるのは、俺がドラゴンだってことだけだ…お前のドラゴンだ。そして、お前は俺のご主人様だ。ちゃんと大事にしてくれよ！", true, true);
                    return true;
                }
                case 3 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("一緒に覚えていくしかないみたいだな。でも腹が減った。ご主人様、ご飯がほしい。俺は赤ちゃんなんだぞ！もうすぐ泣いちゃうぞ！");
                    return true;
                }
                case 4 -> {
                    forceStartQuest();
                    // js の sendOkS は type が反映されない
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "#b（ちびドラゴンのミルはとてもお腹を空かせているようだ。何か食べさせなければ。お父さんなら、ドラゴンが何を食べるか教えてくれるかもしれない。）", false, false);
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        if (getQuestId() == this.quest_hay) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == -1) {
                // TODO : 文章 (js の英文を翻訳)
                say("ふむ、やってみなければわからないさ。あのトカゲは、メイプルのびっくり大全に載りそうなくらい大きいからな。干し草を食べるかもしれないぞ", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("トカゲも牛みたいに#b干し草ひとつかみ#kを喜ぶんじゃないか？近くに#b干し草の山#kがたくさんあるから、それを食べさせてみなさい。");
                    return true;
                }
                case 1 -> {
                    forceStartQuest();
                    evanTutorial("UI/tutorial/evan/12/0");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        boolean pork = (getQuestId() == this.quest_pork);
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            if (pork) {
                // TODO : 文章 (js の英文を翻訳)
                say("こんなふうに俺を飢えさせるなんて。俺はまだ赤ちゃんなんだぞ。こんなのおかしいよ！", false, true);
            } else {
                // TODO : 文章 (js の英文を翻訳)
                say("自分で答えを探しても無駄みたいだ。#bご主人様より年上で物知りな人#kを探さないと！", false, true);
            }
            return false;
        }
        switch (status) {
            case 0 -> {
                if (pork) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("違う、違う、違う。これじゃないんだ。もっと栄養のあるものがほしいんだよ、ご主人様！", false, true);
                } else {
                    // TODO : 文章 (js の英文を翻訳)
                    say("うえっ。これじゃだめだ。何か別のものがいい。草はいやだ。肉もいやだ。なんだ、わからないのか？でもご主人様だし、俺より年上だろ。俺に合うものくらいわかるはずだ！", false, true);
                }
                return true;
            }
            case 1 -> {
                if (pork) {
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "#bうーん…じゃあ草食じゃないんだね。肉食なのかも。なにしろドラゴンだし。豚肉なんてどう？#k", true, true);
                } else {
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "#bわからないよ。年は関係ないと思うけど…", true, true);
                }
                return true;
            }
            case 2 -> {
                if (pork) {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("ブタニク…って何だ？聞いたことないけど、おいしいならそれでいい！とにかく、おいしいものを食べさせてくれ。草以外ならな！");
                } else {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("年上なんだから、世の中のことにも詳しいはずだろ。俺より物知りなのは当然だ。まあいいや。ご主人様よりもっと年上の人に聞いてみるよ！");
                }
                return true;
            }
            case 3 -> {
                forceStartQuest();
                // js の sendOkS は type が反映されない
                if (pork) {
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "#b（ミルに豚肉をあげてみよう。農場で豚を何匹か狩らないと。10匹もいれば十分だろう…）#k", false, false);
                } else {
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "#b（お父さんにはもう一度聞いたけど、ほかにいい考えも浮かばない。もう一度聞いてみよう！）#k", false, false);
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
