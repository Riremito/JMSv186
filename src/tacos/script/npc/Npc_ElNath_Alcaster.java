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
public class Npc_ElNath_Alcaster extends NpcScriptMan {

    private final int quest_old_book = 3035; // 古代の本の封印
    private final int[] item_ids = {
        2050003, // 聖水
        2050004, // 万病治療薬
        4006000, // 魔法の石
        4006001 // 召喚の石
    };
    private final int[] costs = {
        300,
        400,
        5000,
        5000
    };
    // 翻訳
    private final String[] descs = {
        "封印状態と呪い状態を治すアイテム。",
        "すべてを治すアイテム。",
        "高度なスキルに使われる魔力のアイテム。",
        "高度なスキルに使われる召喚の力のアイテム。"
    };
    private final int buy_max = 100;
    private int item_index = -1;
    private int buy_count = 0;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getQuestStatus(this.quest_old_book) != 2) {
                    if (54 < getLevel()) {
                        // 翻訳
                        say("私を手伝ってくれると決めてくれたら、お礼にアイテムを売ってあげよう。");
                        return false;
                    }
                    // 翻訳
                    say("私は魔術師アルケスター。300年以上この町に住み、多くのまじないや呪文を研究してきた。");
                    return false;
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 原文ママ
                nt.add("君のお陰で#b古代の本#kは無事に封印できた。だがそのせいで８００年間に修練してきたわしの力の半分を無くしたがね、もう死んでも惜しくない。そうだ、どころで君、珍しいアイテムは欲しくないか？今まで手伝ってくれたお礼だ。わしが持っているもので必要なものがあるなら安く売ってあげよう。");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.addMenu(i, "#t" + this.item_ids[i] + "#(値段：" + this.costs[i] + " メル)");
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (this.asked) {
                    // 購入確認でいいえを選択した
                    // 翻訳
                    say("わかった。ほら、ここにはいろんなアイテムがあるよ。見ていってくれ。このアイテムは君にだけ売っているんだ。だから、ぼったくるつもりはまったくないよ。");
                    return false;
                }
                if (answer < 0 || this.item_ids.length <= answer) {
                    return false;
                }
                this.item_index = answer;
                int item_id = this.item_ids[this.item_index];
                // 翻訳
                askNumber("#b#t" + item_id + "##kは本当に君が必要なアイテムかな？" + this.descs[this.item_index] + "。手に入れるのは簡単じゃないかもしれないが、いい値で売ってあげよう。1個#b" + this.costs[this.item_index] + "メル#kだ。いくつ買いたい？", 0, 0, this.buy_max);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.buy_max < answer) {
                    return false;
                }
                this.buy_count = answer;
                this.asked = true;
                int item_id = this.item_ids[this.item_index];
                int cost = this.costs[this.item_index];
                // 翻訳
                askYesNo("本当に#r" + this.buy_count + " #t" + item_id + "##kを買うのかい？1個" + cost + "メルで#t" + item_id + "#あたりの値段だから、合計#r" + (cost * this.buy_count) + "#kメルになるよ。");
                return true;
            }
            case 3 -> {
                int item_id = this.item_ids[this.item_index];
                int price = this.costs[this.item_index] * this.buy_count;
                if (getMeso() < price || (0 < this.buy_count && !canHold(item_id, this.buy_count))) {
                    // 翻訳
                    say("本当にメルは足りているかい？消費インベントリかETCインベントリがいっぱいでないか、少なくとも#r" + price + "#kメル持っているか確認してくれ。");
                    return false;
                }
                if (0 < this.buy_count) {
                    payMoney(price);
                    gainItem(item_id, this.buy_count);
                }
                // 翻訳
                say("ありがとう。またいつかアイテムが必要になったら寄ってくれ。年を取ったとはいえ、魔法のアイテムならまだ簡単に作れるからね。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
