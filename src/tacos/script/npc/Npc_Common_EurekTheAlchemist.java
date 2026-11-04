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
public class Npc_Common_EurekTheAlchemist extends NpcScriptMan {

    private final int[] item_ids = {
        4006000, // 魔法の石
        4006001 // 召喚の石
    };
    // {材料1, 個数, 材料2, 個数, 材料3 (1個)}
    private final int[][][] recipes = {
        // 魔法の石
        {
            {4000046, 20, 4000027, 20, 4021001}, // タウロスピアーの角, ワイルドカーゴの瞳, 紫水晶
            {4000025, 20, 4000049, 20, 4021006}, // Ｄ.ゴーレムのかけら, イェティの角, トパーズ
            {4000129, 15, 4000130, 15, 4021002}, // レであってボピッの玉, パープンの掛け時計, アクアマリン
            {4000074, 15, 4000057, 15, 4021005}, // ルイネルの尻尾, ダークペペの口ばし, サファイア
            {4000054, 7, 4000053, 7, 4021003} // ライカンスロープの爪, ウェアウルフの爪, エメラルド
        },
        // 召喚の石
        {
            {4000028, 20, 4000027, 20, 4011001}, // タウロマシスの角, ワイルドカーゴの瞳, 鋼鉄
            {4000014, 20, 4000056, 20, 4011003}, // ドレイクの頭骨, ウェンディゴの角, アダマンティウム
            {4000132, 15, 4000128, 15, 4011005}, // パイレーツの舵, バーフィーの帽子, オリハルコン
            {4000074, 15, 4000069, 15, 4011002}, // ルイネルの尻尾, ゾンビのなくした奥歯, ミスリル
            {4000080, 7, 4000079, 7, 4011004} // ファイアブルのネックレス, ラッシュブルの歯, 銀
        }
    };
    private final int cost = 4000;
    private final int make_count = 5;
    private int selected_item = -1;
    private int selected_recipe = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("モグラの舌とフクロウの嘴を1:1の比率で混ぜ合わせて…あれ！ほのかな光の粉を入れることを忘れてた。大変～！あれ？いつからそこにいました？あ…僕があまりにも仕事に夢中になっていて気づいていなかったのか。これは失敬。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("ご覧の通り、僕は風来坊の錬金術師です。まだ未熟者ですが、あなたが必要なものを作れるかもしれません。一度見てみますか？");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.addMenu(i, "#t" + this.item_ids[i] + "#作り");
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                // 作成確認でいいえを選択した
                if (0 <= this.selected_recipe) {
                    // 翻訳
                    say("材料が足りないのかい？問題ないさ。必要なアイテムがそろったらまた来てくれ。モンスターを狩ったり、ほかの人から買ったり、手に入れる方法はいろいろあるから、がっかりしないでくれ。");
                    return false;
                }
                if (answer < 0 || this.item_ids.length <= answer) {
                    return false;
                }
                this.selected_item = answer;
                int item_id = this.item_ids[this.selected_item];
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("はは…#b#t" + item_id + "##kは、僕にしか作れない神秘の石なんだ。多くの旅人が、MPやHPを多く消費する強力なスキルを使うために必要としているみたいだよ。#t" + item_id + "#の作り方は" + this.recipes[this.selected_item].length + "通りあるんだ。どの方法で作りたい？");
                nt.add("");
                for (int i = 0; i < this.recipes[this.selected_item].length; i++) {
                    int[] recipe = this.recipes[this.selected_item][i];
                    nt.addMenu(i, "#t" + recipe[0] + "#と#t" + recipe[2] + "#で作る");
                }
                askMenu(nt);
                return true;
            }
            case 3 -> {
                if (answer < 0 || this.recipes[this.selected_item].length <= answer) {
                    return false;
                }
                this.selected_recipe = answer;
                int[] recipe = this.recipes[this.selected_item][this.selected_recipe];
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b#t" + this.item_ids[this.selected_item] + "##kを" + this.make_count + "個作るには、以下のアイテムが必要だ。ほとんどはモンスターを倒せば手に入るから、集めるのはそれほど難しくないだろう。どうだい？作るかい？");
                nt.add("");
                nt.add("#b#v" + recipe[0] + "# #t" + recipe[0] + "# " + recipe[1] + "個");
                nt.add("#v" + recipe[2] + "# #t" + recipe[2] + "# " + recipe[3] + "個");
                nt.add("#v" + recipe[4] + "# #t" + recipe[4] + "#");
                nt.add("#v4031138# " + this.cost + "メル");
                askYesNo(nt);
                return true;
            }
            case 4 -> {
                int item_id = this.item_ids[this.selected_item];
                int[] recipe = this.recipes[this.selected_item][this.selected_recipe];
                if (getPlayer().getMeso() < this.cost || getPlayer().getItemQuantity(recipe[0], false) < recipe[1] || getPlayer().getItemQuantity(recipe[2], false) < recipe[3] || !checkItem(recipe[4]) || !MapleInventoryManipulator.checkSpace(getPlayer(), item_id, this.make_count, "")) {
                    // 翻訳
                    say("必要なアイテムがすべてそろっているか、ETCインベントリがいっぱいでないか確認してくれ。");
                    return false;
                }
                payMoney(this.cost);
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, recipe[0], recipe[1], true, false);
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, recipe[2], recipe[3], true, false);
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, recipe[4], 1, true, false);
                MapleInventoryManipulator.addById(getPlayer(), item_id, (short) this.make_count);
                // 翻訳
                say("ほら、#b#t" + item_id + "##kを" + this.make_count + "個どうぞ。我ながら芸術作品だと認めざるを得ないね。さて、また手伝いが必要になったら、ここに来て話しかけてくれ！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
