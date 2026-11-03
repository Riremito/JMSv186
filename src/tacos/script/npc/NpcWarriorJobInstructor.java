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
public class NpcWarriorJobInstructor extends NpcScriptMan {

    private final int npc_job = 1022000; // コブシを開いて立て
    private final int item_letter = 4031008; // コブシを開いて立ての手紙
    private final int item_dark_marble = 4031013; // 黒い玉
    private final int item_proof_of_hero = 4031012; // 英雄の証
    private final int map_test = 108000300; // 隠れ道 : 戦士の岩山1

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (!checkItem(this.item_letter)) {
                    // TODO : 手紙を持っていない場合の文章 (コメントアウトされた旧スクリプトの文章)
                    say("I can show you the way once your ready for it.");
                    return false;
                }
                // 原文ママ, 魔法使いも同じ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("うん…これは間違いなく#b" + getNpcName(this.npc_job) + "#k様の手紙だな…君、戦士の2次転職試験を受けるため、ここまで私を尋ねて来たようだね。よし…それでは戦士の2次転職試験について簡単に説明しよう。あまり複雑ではないから心配しないでいいよ。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                // 原文ママ, 魔法使いも同じ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("私が君をある秘密のマップに送ってあげよう。そこには普通のフィールドには見られないかわったモンスターが現われる。もちろん姿は同じだが性格は全然違う。やつらからは経験値も一般アイテムも貰えない。");
                say(nt, true, true);
                return true;
            }
            case 2 -> {
                // 原文ママ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("そいつらを倒すと#b#z" + this.item_dark_marble + "##kというアイテムを得ることができる。それはやつらの醜くて汚い心が集まって作られた特別な玉だ。この玉を30個集めて中にいる私の仲間に声を掛ければ合格だ。");
                say(nt, true, true);
                return true;
            }
            case 3 -> {
                // 原文ママ, 魔法使いも同じ
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("一度中に入ったら任務を果すまで外に出ることはできない。そして、その中で死んでもやっぱり経験値は落ちるから気を付けた方が良いよ。ちゃんと準備をしなければならないということ。さあ…それじゃ今すぐ試験を受けるか？");
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
                // TODO : クエスト (100004) の開始, 同一マップが3つ存在する (108000300 - 108000302)
                warp(this.map_test); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
