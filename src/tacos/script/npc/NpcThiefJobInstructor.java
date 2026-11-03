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

import odin.client.inventory.MapleInventoryType;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcThiefJobInstructor extends NpcScriptMan {

    private final int npc_job = 1052001; // ダークロード
    private final int item_letter = 4031011; // ダークロードの手紙
    private final int item_dark_marble = 4031013; // 黒い玉
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int job_1st = 400; // 盗賊
    private final int[] job_2nd = {
        410, // アサシン
        420, // シーフ
        430 // ブレードリクルート
    };
    private final int job_2nd_level = 30;
    private final int[] map_tests = {
        108000400, // 隠れ道 : 盗賊の工事現場1
        108000401, // 隠れ道 : 盗賊の工事現場2
        108000402 // 隠れ道 : 盗賊の工事現場3
    };
    private final int map_job = 103000000; // カニングシティー : カニングシティー
    private boolean retry = false;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (status == -1 || (status == 2 && this.asked)) {
            if (this.retry) {
                // 翻訳
                say("まだ準備ができていないようだな。準備ができたら私のところに来い。向こうにはポータルも店もないから、100%準備しておいた方がいい。");
                return false;
            }
            // 翻訳
            say("まだ準備ができていないようですね。準備ができたら私のところに来てください。向こうにはポータルも店もないので、100%準備しておいた方がいいですよ。");
            return false;
        }
        switch (status) {
            case 0 -> {
                int job = getPlayer().getJob();
                for (int job_id : this.job_2nd) {
                    if (job == job_id) {
                        // 翻訳
                        say("ふむ…俺の試験に合格したのはお前だな！！どうだ？あれから強くなったか？いいぞ！今のお前からは間違いなく盗賊の風格を感じる…");
                        return false;
                    }
                }
                if (job != this.job_1st) {
                    return false;
                }
                if (getPlayer().getLevel() < this.job_2nd_level) {
                    // 翻訳
                    say("本当にもっと優れた盗賊になりたいのか？それなら俺に任せておけ。ただ、お前はまだかなり弱そうだな。強くなるまで修行してから戻ってこい。");
                    return false;
                }
                if (!checkItem(this.item_letter)) {
                    // 翻訳
                    say("盗賊として本当に腕を磨きたいのか？それなら俺に任せておけ…お前には必要な素質がありそうだ…とりあえず、#m" + this.map_job + "#の#b" + getNpcName(this.npc_job) + "#kに会いに行ってこい。");
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
                say("ふむ…間違いなく#b" + getNpcName(this.npc_job) + "#kからの手紙だな…つまり、盗賊として2次転職するための試験を受けにここまで来たんだな？よし、試験について説明しよう。心配するな、難しくはない。", false, true);
                return true;
            }
            case 1 -> {
                if (this.retry) {
                    // 翻訳
                    say("よし！中に入れてやろう！悪いが、入る前にお前の持っている玉はすべて回収させてもらう。モンスターを倒して#t" + this.item_dark_marble + "#を30個集めたら、そこにいる俺の仲間に話しかけろ。勝利の証として#b#t" + this.item_proof_of_hero + "##kをくれるはずだ。幸運を祈る。", false, true);
                    return true;
                }
                // 翻訳
                say("これからあなたを秘密のマップへ送ります。そこには普段どこでも見かけないモンスターがいます。普通の生き物のように見えますが、行動はまったく違います。経験値もアイテムもくれないモンスターです。", false, true);
                return true;
            }
            case 2 -> {
                if (this.retry) {
                    int quantity = getPlayer().getItemQuantity(this.item_dark_marble, false);
                    if (0 < quantity) {
                        MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_dark_marble, quantity, true, false);
                    }
                    warp(this.map_tests[(int) (Math.random() * this.map_tests.length)]);
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
                say("よし！中に入れてやろう！マップのモンスターを倒して#t" + this.item_dark_marble + "#を30個集めたら、そこにいる俺の仲間に話しかけろ。試験に合格した証として#b#t" + this.item_proof_of_hero + "##kをくれるはずだ。幸運を祈る。", false, true);
                return true;
            }
            case 5 -> {
                warp(this.map_tests[(int) (Math.random() * this.map_tests.length)]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
