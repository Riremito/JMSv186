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
public class NpcWarriorJobInstructor extends NpcScriptMan {

    private final int npc_job = 1022000; // コブシを開いて立て
    private final int item_letter = 4031008; // コブシを開いて立ての手紙
    private final int item_dark_marble = 4031013; // 黒い玉
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int job_1st = 100; // 戦士
    private final int[] job_2nd = {
        110, // ファイター
        120, // ページ
        130 // スピアマン
    };
    private final int job_2nd_level = 30;
    // 同一マップが3つ存在する
    private final int[] map_tests = {
        108000300, // 隠れ道 : 戦士の岩山1
        108000301, // 隠れ道 : 戦士の岩山1
        108000302 // 隠れ道 : 戦士の岩山1
    };
    private final int map_job = 102000000; // ぺリオン : ぺリオン
    private boolean retry = false;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (status == -1 || (status == 2 && this.asked)) {
            // 翻訳
            say("まだ準備ができていないようだな。準備ができたら私のところに来い。向こうにはポータルも店もないから、100%準備しておいた方がいい。");
            return false;
        }
        switch (status) {
            case 0 -> {
                int job = getPlayer().getJob();
                for (int job_id : this.job_2nd) {
                    if (job == job_id) {
                        // 翻訳
                        say("ふむ…この間私の試験に合格したのは君だな！どうだ？強くなったか？いいぞ！今の君からは間違いなく戦士の風格を感じる。");
                        return false;
                    }
                }
                if (job != this.job_1st) {
                    return false;
                }
                if (getPlayer().getLevel() < this.job_2nd_level) {
                    // 翻訳
                    say("今よりずっと強い戦士になりたいのか？私に任せてくれと言いたいところだが…君はまだ弱すぎるようだ。修行を始めて、もっと強くなってから戻ってこい。");
                    return false;
                }
                if (!checkItem(this.item_letter)) {
                    // 翻訳
                    say("今よりずっと強い戦士になりたいのか？私に任せておけ。君には十分すぎるほどの資格がありそうだ。まずは#m" + this.map_job + "#の#b" + getNpcName(this.npc_job) + "#kを訪ねてくれ…");
                    return false;
                }
                // 黒い玉を持っている場合は再挑戦
                if (checkItem(this.item_dark_marble)) {
                    this.retry = true;
                    // 翻訳
                    askYesNo("一度諦めたんだな。心配するな、試験はいつでも受け直せる。さて…もう一度中に入って挑戦するか？");
                    return true;
                }
                // 原文ママ, 魔法使いも同じ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("うん…これは間違いなく#b" + getNpcName(this.npc_job) + "#k様の手紙だな…君、戦士の2次転職試験を受けるため、ここまで私を尋ねて来たようだね。よし…それでは戦士の2次転職試験について簡単に説明しよう。あまり複雑ではないから心配しないでいいよ。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                if (this.retry) {
                    // 翻訳
                    say("よし！中に入れてやろう！悪いが、先に君の持っている玉はすべて回収させてもらう。中にいるモンスターをすべて倒して#t" + this.item_dark_marble + "#を30個集め、マップにいる私の仲間に話しかけろ。試験に合格した証として#b#t" + this.item_proof_of_hero + "##kがもらえる。幸運を祈る。", false, true);
                    return true;
                }
                // 原文ママ, 魔法使いも同じ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私が君をある秘密のマップに送ってあげよう。そこには普通のフィールドには見られないかわったモンスターが現われる。もちろん姿は同じだが性格は全然違う。やつらからは経験値も一般アイテムも貰えない。");
                say(nt, false, true);
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
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                // 魔法使いver
                // その中でやつらを倒すと#b#z4031013#というアイテムを得ることができる。それはやつらの醜くて汚い心が集まって作られた特別な玉だ。この玉を30個集めて中にいる私の仲間に声を掛ければ合格だ。
                nt.add("そいつらを倒すと#b#z" + this.item_dark_marble + "##kというアイテムを得ることができる。それはやつらの醜くて汚い心が集まって作られた特別な玉だ。この玉を30個集めて中にいる私の仲間に声を掛ければ合格だ。");
                say(nt, false, true);
                return true;
            }
            case 3 -> {
                // 原文ママ, 魔法使いも同じ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("一度中に入ったら任務を果すまで外に出ることはできない。そして、その中で死んでもやっぱり経験値は落ちるから気を付けた方が良いよ。ちゃんと準備をしなければならないということ。さあ…それじゃ今すぐ試験を受けるか？");
                this.asked = true;
                askYesNo(nt);
                return true;
            }
            case 4 -> {
                // 原文ママ , 魔法使いも同じ, アイテム名だけ青く変更
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("よし！それじゃあ中に入れてやろう。中でモンスターを倒して#b#z" + this.item_dark_marble + "##kを30個集めた後、中にいる私の仲間に声を掛ければ試験合格の証拠品である#b#z" + this.item_proof_of_hero + "##kを受け取ることができる。それじゃ検討を祈る。");
                say(nt, false, true);
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
