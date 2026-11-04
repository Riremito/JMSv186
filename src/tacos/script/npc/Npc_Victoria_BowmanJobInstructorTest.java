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

/**
 *
 * @author Riremito
 */
public class Npc_Victoria_BowmanJobInstructorTest extends NpcScriptMan {

    private final int npc_job = 1012100; // ヘレナ
    private final int item_letter = 4031010; // ヘレナの手紙
    private final int item_dark_marble = 4031013; // 黒い玉
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int map_job = 100000000; // ヘネシス : ヘネシス
    private final int map_exit = 106010000; // へネシス : ダンジョンへの道
    private final int job_1st = 300; // 弓使い
    private final int job_2nd_level = 30;
    private final int required_quantity = 30;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // 翻訳
                say("その意気だ！文句を言わずに玉を集め始めろ。#b#t" + this.item_dark_marble + "##kを" + this.required_quantity + "個集めたら話しかけてくれ。");
                return false;
            }
            case 0 -> {
                if (getJob() != this.job_1st || getLevel() < this.job_2nd_level) {
                    // 翻訳
                    say("何だ？どうやってここに来たんだ？…おかしいな…まあいい、出してやろう。ここはとても危険な場所だ。出ていかないと、もっと危ない目に遭うぞ。", false, true);
                    return true;
                }
                if (!haveItem(this.item_dark_marble, this.required_quantity)) {
                    // 翻訳
                    askYesNo("ふむ…どうしましたか？まだ#b#t" + this.item_dark_marble + "##kを" + this.required_quantity + "個集めていないようですね…手こずっているなら、今出ていって後でまた挑戦してもいいですよ。それで…諦めてここから出ますか？");
                    return true;
                }
                // 翻訳
                say("おおお…#t" + this.item_dark_marble + "#を" + this.required_quantity + "個集めたのですね！！大変じゃありませんでしたか？？よくやりました。試験に合格です。#b#t" + this.item_proof_of_hero + "##kをあげましょう。これを持って#m" + this.map_job + "#へ戻ってください。", false, true);
                return true;
            }
            case 1 -> {
                if (getJob() != this.job_1st || getLevel() < this.job_2nd_level) {
                    warp(this.map_exit);
                    return false;
                }
                if (!haveItem(this.item_dark_marble, this.required_quantity)) {
                    // 翻訳
                    say("本当か…？わかった、出してやろう。だが諦めるなよ。いつでもまた挑戦できるんだから、諦めるな。それまで、さらばだ…", false, true);
                    return true;
                }
                if (!checkItem(this.item_letter) || !canHold(this.item_proof_of_hero, 1)) {
                    // 翻訳
                    say("ふむ…何かがおかしいですね…#t" + this.item_dark_marble + "#が" + this.required_quantity + "個あるか、#b" + getNpcName(this.npc_job) + "#kの手紙があるか、ETCインベントリに空きスロットがあるか確認してください。");
                    return false;
                }
                int quantity = getItemCount(this.item_dark_marble);
                removeItem(this.item_dark_marble, quantity);
                removeItem(this.item_letter, 1);
                gainItem(this.item_proof_of_hero, 1); // TODO : item effect.
                warp(this.map_exit);
                return false;
            }
            case 2 -> {
                warp(this.map_exit);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
