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

import odin.server.Randomizer;

/**
 *
 * @author Riremito
 */
public class Npc_Ellinia_MagicianJobInstructor extends NpcScriptMan {

    private final int npc_job = 1032001; // ハインズ
    private final int item_letter = 4031009; // ハインズの手紙
    private final int item_dark_marble = 4031013; // 黒い玉
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int job_1st = 200; // 魔法使い
    private final int[] job_2nd = {
        210, // ウィザード(火・毒)
        220, // ウィザード(氷・雷)
        230 // クレリック
    };
    private final int job_2nd_level = 30;
    private final int[] map_tests = {
        108000200, // 隠れ道 : 木のダンジョン1
        108000201, // 隠れ道 : 木のダンジョン2
        108000202 // 隠れ道 : 木のダンジョン3
    };
    private final int map_job = 101000000; // エリニア : エリニア
    private boolean retry = false;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (status == -1 || (status == 2 && this.asked)) {
            if (this.retry) {
                // 翻訳
                say("まだ準備ができていないようですね。準備ができたら私のところに来てください。向こうにはポータルも店もないので、100%準備しておいた方がいいですよ。");
                return false;
            }
            // 翻訳
            say("あなたにはまだ資格がないようですね。もっと準備ができてから話しかけてください。向こうにはポータルも店もないので、今のうちに準備しておいた方がいいですよ。");
            return false;
        }
        switch (status) {
            case 0 -> {
                int job = getJob();
                for (int job_id : this.job_2nd) {
                    if (job == job_id) {
                        // 翻訳
                        say("ああ、この間私の試験に合格したのはあなたですね！どうですか？強くなれましたか？いいですね！今のあなたからは間違いなく魔法使いの風格を感じます…");
                        return false;
                    }
                }
                if (job != this.job_1st) {
                    return false;
                }
                if (getLevel() < this.job_2nd_level) {
                    // 翻訳
                    say("今より強い魔法使いになりたいのですか？私にお任せください。ただ、あなたは少し弱そうですね。もう少し修行して強くなってから戻ってきてください。");
                    return false;
                }
                if (!checkItem(this.item_letter)) {
                    // 翻訳
                    say("今より強い魔法使いになりたいのですか？私にお任せください。あなたには資格がありそうですね。まずは#m" + this.map_job + "#の#b" + getNpcName(this.npc_job) + "#kを訪ねてください。");
                    return false;
                }
                // 黒い玉を持っている場合は再挑戦
                if (checkItem(this.item_dark_marble)) {
                    this.retry = true;
                    // 翻訳
                    askYesNo("一度諦めたんだな。心配するな、試験はいつでも受け直せる。さて…もう一度中に入って挑戦するか？");
                    return true;
                }
                // 翻訳
                say("ふむ…間違いなく#b" + getNpcName(this.npc_job) + "#kからの手紙ですね…つまり、魔法使いとして2次転職するための試験を受けにここまで来たのですね？よろしい、試験について説明しましょう。心配いりません、難しくはありませんよ。", false, true);
                return true;
            }
            case 1 -> {
                if (this.retry) {
                    // 翻訳
                    say("よろしい、中に入れてあげましょう！申し訳ありませんが、先にあなたの持っている玉はすべて回収させてもらいます。マップに入ったら、モンスターを倒して#t" + this.item_dark_marble + "#を30個集めてください。その後、そこにいる私の仲間に話しかければ、試験に合格した証である#b#t" + this.item_proof_of_hero + "##kがもらえます。幸運を祈ります。", false, true);
                    return true;
                }
                // 翻訳
                say("これからあなたを秘密のマップへ送ります。そこには普段どこでも見かけないモンスターがいます。普通の生き物のように見えますが、行動はまったく違います。経験値もアイテムもくれないモンスターです。", false, true);
                return true;
            }
            case 2 -> {
                if (this.retry) {
                    int quantity = getItemCount(this.item_dark_marble);
                    if (0 < quantity) {
                        removeItem(this.item_dark_marble, quantity);
                    }
                    warp(this.map_tests[Randomizer.nextInt(this.map_tests.length)]);
                    return false;
                }
                // 翻訳
                say("そのモンスターを倒すと、#b#t" + this.item_dark_marble + "##kというアイテムが手に入る。邪悪で不気味な心が作り出した特別なガラス玉だ。それを30個集めて、マップにいる私の仲間に話しかけるんだ。そうすれば試験に合格だ。", false, true);
                return true;
            }
            case 3 -> {
                this.asked = true;
                // 翻訳
                askYesNo("一度マップに入ったら、ミッションを終えるまで出られない。死ぬと経験値が減ってしまう…だから気を引き締めて準備しておけ…さて、今すぐ始めるか？");
                return true;
            }
            case 4 -> {
                // 翻訳
                say("よろしい、中に入れてあげましょう！マップに入ったら、モンスターを倒して#t" + this.item_dark_marble + "#を30個集めてください。その後、そこにいる私の仲間に話しかければ、試験に合格した証として#b#t" + this.item_proof_of_hero + "##kがもらえます。幸運を祈ります。", false, true);
                return true;
            }
            case 5 -> {
                warp(this.map_tests[Randomizer.nextInt(this.map_tests.length)]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
