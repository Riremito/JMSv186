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
// Npc.wz script : jane (JMS131-302), 会話文は lua_script_jp (jane) を利用
public class Npc_LithHarbor_Jane extends NpcScriptMan {

    private final int quest_jane = 2013; // ジェーンの最後の挑戦
    private final int[] item_ids = {
        2000002, // 白い薬
        2022003, // ウナギ焼
        2022000, // ミネラルウォーター
        2001000 // スイカ
    };
    private final int[] costs = {
        310,
        1060,
        1600,
        3120
    };
    // 翻訳
    private final String[] descs = {
        "HPを300回復できる",
        "HPを1000回復できる",
        "HPを800回復できる",
        "HPとMPを1000ずつ回復できる"
    };
    private final int buy_max = 100;
    private int item_index = -1;
    private int buy_count = 0;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getQuestStatus(this.quest_jane) != 2) {
                    if (40 <= getLevel() && getJob() != 0) {
                        // 原文ママ
                        NpcScriptTag nt = new NpcScriptTag();
                        nt.add("私の依頼を全部聞いてくだされば、薬の購入ができます。");
                        say(nt);
                        return false;
                    }
                    if (25 <= getLevel() && getJob() != 0) {
                        // 翻訳
                        say("あなたはまだ私のポーションを買えるほど強くなさそうね…");
                        return false;
                    }
                    // 翻訳
                    say("私の夢は、あなたみたいにいろいろな所を旅することなの。でも、お父さんは危なすぎるって言って行かせてくれないの。何とかして、私がお父さんの思っているようなか弱い女の子じゃないってことを見せられたら、行かせてくれるはずなんだけど…");
                    return false;
                }
                // 翻訳
                say("あなたね…あなたのおかげで、いろいろなことができたわ。今はアイテムのセットを作っているの。何か必要なものがあったら言ってね。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どのアイテムを買いたい？ ");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.addMenu(i, "#t" + this.item_ids[i] + "# (価格: " + this.costs[i] + "メル) ");
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (this.asked) {
                    // 購入確認でいいえを選択した
                    // 翻訳
                    say("あなたが前に私から買った材料がまだ少し残っているの。全部そこにあるから、自由に選んでね。");
                    return false;
                }
                if (answer < 0 || this.item_ids.length <= answer) {
                    return false;
                }
                this.item_index = answer;
                int item_id = this.item_ids[this.item_index];
                // 翻訳
                askNumber("#b#t" + item_id + "##kがほしいの？#t" + item_id + "#は" + this.descs[this.item_index] + "わ。いくつ買う？", 0, 0, this.buy_max);
                return true;
            }
            case 3 -> {
                if (answer < 0 || this.buy_max < answer) {
                    return false;
                }
                this.buy_count = answer;
                this.asked = true;
                int item_id = this.item_ids[this.item_index];
                int cost = this.costs[this.item_index];
                // 翻訳
                askYesNo("#r" + this.buy_count + "#k個の#b#t" + item_id + "##kを買う？#b#t" + item_id + "#は1個" + cost + "メルだから、合計は#k" + (cost * this.buy_count) + "#kメル持っているか確認してくれ。");
                return true;
            }
            case 4 -> {
                int price = this.costs[this.item_index] * this.buy_count;
                if (!exchange(-price, this.item_ids[this.item_index], this.buy_count)) {
                    // 翻訳
                    say("もしかしてお金が足りない？ETCインベントリに空きスロットがあるか確認してね。必要な金額は最低#r" + price + "#kメル持っているか確認してくれ。");
                    return false;
                }
                // 翻訳
                say("来てくれてありがとう！ここではいつでも取引できるから、何か必要になったらまた来てね！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
