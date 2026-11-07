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
// Quest.wz startscript : q21200s, endscript : q21200e, q21201e (JMS186), 主を待つ武器, 念願の鏡, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_WeaponAwaitingItsMaster extends QuestScriptMan {

    private final int quest_weapon = 21200; // 主を待つ武器
    private final int quest_best_weapon = 21202; // 最高の武器を探して
    private final int quest_unknown = 21203; // TODO : JMS186 には存在しないクエスト (js のまま)
    private final int map_movie = 914090200; // アランの動画 (マップ名なし)

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("本当に急ぎの用なのです。断ったら後悔しますよ。#bあなたの鉾に関することなのです。#kつまり、あなたの過去に関わることです。もしかすると…鉾があなたの能力を目覚めさせる鍵なのかもしれません…", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("修行は順調ですか？まあ、ずいぶんレベルが上がったようですね。すごいです…それはそうと、忙しいところ申し訳ないのですが、少しの間島に戻ってきてもらえますか。");
                return true;
            }
            case 1 -> {
                // TODO : js ではクエストのデータに "3" を設定している (用途不明)
                forceStartQuest(getQuestId(), "3");
                forceCompleteQuest();
                questEndEffect();
                forceStartQuest(this.quest_best_weapon);
                forceStartQuest(this.quest_unknown, "0");
                // TODO : 文章 (js の英文を翻訳)
                say("#bリエン#kで保管している#b巨大な鉾#kが、急に様子がおかしくなったのです。本によると、鉾が主を呼んでいるときにこのような反応をするそうです。#bもしかしてあなたを呼んでいるのでは？#k島に戻って確かめてみてください。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        if (getQuestId() != this.quest_weapon) {
            // 念願の鏡
            forceStartQuest(this.quest_unknown, "0");
            forceStartQuest(this.quest_best_weapon);
            return false;
        }
        // いいえを選択した
        if (getLastAction() == 0 && status == 10) {
            // TODO : 文章 (js の英文を翻訳)
            say("おい、せめてやってみたとくらい言ってくれよ！", false, true);
            return false;
        }
        // 動画を見る
        if (getLastAction() == 0 && status == 12) {
            setStandAloneMode(true);
            setDirectionMode(true);
            warp(this.map_movie);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "うううううん…", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（巨大な鉾が震えている。でも、あそこに立っている少年は誰だろう？）#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（今まで会ったことがない。人間ではなさそうだ。）#k", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("おい、アラン！まだ聞こえないのか？本当に聞こえないのかよ？ああ、もう、イライラする！", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（うわっ、今のは誰だ？怒っている少年の声みたいだ…）#k", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("まったく、たった一人の主は武器をほったらかして何百年も氷に閉じ込められていたうえに、今度はその「主」が俺の声も聞こえないなんて！", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "お前は誰だ？", true, true);
                return true;
            }
            case 7 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("アラン？今度は聞こえるか？俺だよ、俺！お前の武器、#b鉾のマッハ#kだ！", true, true);
                return true;
            }
            case 8 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（…マッハ？巨大な鉾がしゃべっている？）#k", true, true);
                return true;
            }
            case 9 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("なんで信じられないって顔をしてるんだよ？記憶を全部なくしたのはわかるけど…俺のことまで忘れたのか？そんなのひどいだろ？？", true, true);
                return true;
            }
            case 10 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "すまない、でも本当に…何も覚えていないんだ。", true, true);
                return true;
            }
            case 11 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("何百年ぶりなのに、言うことはそれだけか？すまない？何百年もひとりぼっちで、俺がどれだけ退屈だったかわかるか？思い出せるものなら思い出してみろ。記憶を引っぱり出せ！全部だ！掘り起こしてでも思い出せ！");
                return true;
            }
            case 12 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（巨大な鉾のマッハだと名乗る声は、かなり腹を立てているようだ。このままでは話が進まない。まずは#p1201000#と話してみよう。）#k", false, true);
                forceCompleteQuest();
                questEndEffect();
                return true;
            }
            case 13 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("映像をスキップしますか？スキップしてもゲームの進行には影響ありません。");
                return true;
            }
            default -> {
            }
        }
        // 動画を見ない
        return false;
    }
}
