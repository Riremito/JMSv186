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

import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class Npc_Ellinia_ArwenTheFairy extends NpcScriptMan {

    private final int required_level = 40;
    // 作成するアイテム
    private final int[] item_ids = {
        4011007, // 月の石
        4021009, // 星の石
        4031042 // 黒い羽
    };
    private final int[] costs = {
        10000,
        15000,
        30000
    };
    // 材料 (各1個)
    private final int[][] materials = {
        {
            4011000, // 青銅
            4011001, // 鋼鉄
            4011002, // ミスリル
            4011003, // アダマンティウム
            4011004, // 銀
            4011005, // オリハルコン
            4011006 // 金
        },
        {
            4021000, // ガーネット
            4021001, // 紫水晶
            4021002, // アクアマリン
            4021003, // エメラルド
            4021004, // オパール
            4021005, // サファイア
            4021006, // トパーズ
            4021007, // ダイヤモンド
            4021008 // 黒水晶
        },
        {
            4001006, // 炎の羽毛
            4011007, // 月の石
            4021008 // 黒水晶
        }
    };
    // 翻訳
    private final String[] asks = {
        "#t4011007#を作りたいのね。それには、次の精錬済みアイテムが1つずつ必要よ：#b#t4011000#、#t4011001#、#t4011002#、#t4011003#、#t4011004#、#t4011005#、#t4011006##k。10,000メルくれたら作ってあげるわ。",
        "#t4021009#を作りたいの？それには、次の精錬済みアイテムがそれぞれ必要よ：#b#t4021000#、#t4021001#、#t4021002#、#t4021003#、#t4021004#、#t4021005#、#t4021006#、#t4021007#、#t4021008##k。15,000メルくれたら作ってあげるわ。",
        "#t4031042#を作りたいのね。それには、#b#t4001006#1個、#t4011007#1個、#t4021008#1個#kが必要よ。30,000メルくれたら作ってあげるわ。そうそう、この羽根はとても特別なアイテムなの。落としてしまうと消えてしまうし、誰かに渡すこともできないのよ。"
    };
    // 翻訳
    private final String[] fails = {
        "本当にお金は足りている？精錬済みの#b#t4011000#、#t4011001#、#t4011002#、#t4011003#、#t4011004#、#t4011005#、#t4011006##kを1つずつ持っているか確認してね。",
        "本当にお金は足りている？精錬済みの#b#t4021000#、#t4021001#、#t4021002#、#t4021003#、#t4021004#、#t4021005#、#t4021006#、#t4021007#、#t4021008##kを1つずつ持っているか確認してね。",
        "本当にお金は足りている？#b#t4001006#1個、#t4011007#1個、#t4021008#1個#kを用意しているか確認してね。"
    };
    private int selected = -1;
    private boolean asked = false;

    @Override
    public boolean action(int status, int answer) {
        // いいえを選択した
        if (status == 1 && this.asked) {
            // 翻訳
            say("#t" + this.item_ids[this.selected] + "#を作るのは簡単じゃないわ。早く材料を集めてきてね。");
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getPlayer().getJob() == 0 || getPlayer().getLevel() < this.required_level) {
                    // 翻訳
                    say("貴重で珍しいアイテムを作っているけど、残念ながら、あなたのようなよそ者には作ってあげられないわ。");
                    return false;
                }
                // 翻訳
                say("ええ…私は妖精の錬金術師長よ。妖精はたぶん長い間人間と接することはないけれど…あなたのような強い人なら大丈夫ね。材料を手に入れてくれたら、特別なアイテムを作ってあげるわ。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("何を作りたいの？");
                for (int i = 0; i < this.item_ids.length; i++) {
                    nt.addMenu(i, "#t" + this.item_ids[i] + "#");
                }
                askMenu(nt);
                return true;
            }
            case 2 -> {
                if (answer < 0 || this.item_ids.length <= answer) {
                    return false;
                }
                this.selected = answer;
                this.asked = true;
                askYesNo(this.asks[this.selected]);
                return true;
            }
            case 3 -> {
                int item_id = this.item_ids[this.selected];
                boolean ok = this.costs[this.selected] <= getPlayer().getMeso() && MapleInventoryManipulator.checkSpace(getPlayer(), item_id, 1, "");
                for (int material : this.materials[this.selected]) {
                    if (!checkItem(material)) {
                        ok = false;
                    }
                }
                if (!ok) {
                    say(this.fails[this.selected]);
                    return false;
                }
                for (int material : this.materials[this.selected]) {
                    MapleInventoryManipulator.removeById(getPlayer(), GameConstants.getInventoryType(material), material, 1, true, false);
                }
                payMoney(this.costs[this.selected]);
                MapleInventoryManipulator.addById(getPlayer(), item_id, (short) 1);
                // 翻訳
                say("はい、どうぞ。#t" + item_id + "#よ。とてもよくできているでしょう。良い材料を使うことが大事なの。いつか私の助けが必要になったら、頼ってちょうだいね。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
