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
public class Npc_Victoria_BowmanJobInstructor extends NpcScriptMan {

    private final int npc_job = 1012100; // ヘレナ
    private final int item_letter = 4031010; // ヘレナの手紙
    private final int item_dark_marble = 4031013; // 黒い玉
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int job_1st = 300; // 弓使い
    private final int[] job_2nd = {
        310, // ハンター
        320 // クロスボウマン
    };
    private final int job_2nd_level = 30;
    private final int[] map_tests = {
        108000100, // 隠れ道 : アリの巣1
        108000101, // 隠れ道 : アリの巣2
        108000102 // 隠れ道 : アリの巣3
    };
    private final int map_job = 100000000; // ヘネシス : ヘネシス
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
            say("まだ準備ができていないようですね。準備ができたら私のところに来てください。向こうにはポータルも店もないので、100%準備しておいた方がいいですよ。");
            return false;
        }
        switch (status) {
            case 0 -> {
                int job = getJob();
                for (int job_id : this.job_2nd) {
                    if (job == job_id) {
                        // 翻訳
                        say("ふむ…私の試験に合格したのはあなたですね！どうですか？前より強くなった気がしますか？いいですね！今のあなたには間違いなく弓使いの風格がありますよ…");
                        return false;
                    }
                }
                if (job != this.job_1st) {
                    return false;
                }
                if (getLevel() < this.job_2nd_level) {
                    // 翻訳
                    say("もっと強い弓使いになりたいのですか？私にお任せください。ただ…あなたはかなり弱そうですね。強くなるまで修行してから戻ってきた方がずっといいですよ。");
                    return false;
                }
                if (!checkItem(this.item_letter)) {
                    // 翻訳
                    say("もっと強い弓使いになりたいのですか？私にお任せください。あなたには間違いなく資格がありそうですね。とりあえず、まずは#m" + this.map_job + "#の#b" + getNpcName(this.npc_job) + "#kを訪ねてください。");
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
                say("ふむ…間違いなく#b" + getNpcName(this.npc_job) + "#kからの手紙ですね…つまり、弓使いとして2次転職するための試験を受けにここまで来たのですね？いいでしょう、試験について説明しますね。でも心配いりません、難しくはありませんよ。", false, true);
                return true;
            }
            case 1 -> {
                if (this.retry) {
                    // 翻訳
                    say("よし！中に入れてあげましょう！申し訳ありませんが、集めた玉は回収させてもらいます。マップに入ったら、#t" + this.item_dark_marble + "#を30個集めるまでモンスターを倒して、そこにいる私の仲間に話しかけてください。試験に合格した証として#t" + this.item_proof_of_hero + "#をくれますよ。", false, true);
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
                say("そのモンスターを倒すと、#b#t" + this.item_dark_marble + "##kという玉が手に入ります。邪悪で不気味な心が作り出した特別なガラス玉です。それを30個集めて、マップにいる私の仲間に話しかけてください。そうすれば試験に合格です。", false, true);
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
                say("よし！中に入れてあげましょう！モンスターを倒して#t" + this.item_dark_marble + "#を30個集めたら、そこにいる私の仲間に話しかけてください。試験に合格した証として#b#t" + this.item_proof_of_hero + "##kをくれますよ。幸運を祈ります！", false, true);
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
