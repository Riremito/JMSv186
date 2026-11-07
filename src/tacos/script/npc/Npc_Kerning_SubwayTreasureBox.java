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
public class Npc_Kerning_SubwayTreasureBox extends NpcScriptMan {

    // NPCごとの設定 (B1, B2, B3)
    private final int[] npc_ids = {
        1052008, // 宝箱
        1052009, // 宝箱
        1052010 // 宝箱
    };
    private final int[] quest_ids = {
        2055, // シュミがなくした小銭
        2056, // シュミがなくした札束
        2057 // シュミがなくしたしお金袋
    };
    private final int[] item_ids = {
        4031039, // シュミのコイン
        4031040, // シュミの札束
        4031041 // シュミの財布
    };
    // 翻訳
    private final String[] found_texts = {
        "#p1052008#をよく見ると、何か光るものがあるようだ。手を伸ばすと、小さなコインをつかむことができた。",
        "#p1052008#をよく見ると、紙の束のようなものがある。手を伸ばしてみると…あった！巨大な札束だ。",
        "#p1052008#をよく見ると、何か光るものが入った袋のようなものがある。手を伸ばすと、重いコインの袋をつかむことができた。"
    };
    // 翻訳
    private final String[] full_texts = {
        "#p1052008#をよく見ると、何か光るものがあるようだが、ETCインベントリがいっぱいなので、アイテムを取ることができない。",
        "#p1052008#をよく見ると、紙の束のようなものがあるが、ETCインベントリがいっぱいなので、アイテムを取ることができない。",
        "#p1052008#をよく見ると、何か光るものが入った袋のようなものがあるが、ETCインベントリがいっぱいなので、アイテムを取ることができない。"
    };
    // 翻訳
    private final String[] have_texts = {
        "もうコインを持っている。もう少し探してみたが、何も見つからなかった。",
        "もう札束を持っている。もう少し探してみたが、何も見つからなかった。",
        "もうコインの袋を持っている。もう少し探してみたが、何も見つからなかった。"
    };
    // クエストを受けていない場合の報酬
    private final int[][] prize_ids = {
        {
            4010000, // 青銅の原料
            4010001, // 鋼鉄の原料
            4010002, // ミスリルの原料
            4010003, // アダマンティウムの原料
            4010004, // 銀の原料
            4010005 // オリハルコンの原料
        },
        {
            4020000, // ガーネットの原石
            4020001, // 紫水晶の原石
            4020002, // アクアマリンの原石
            4020003, // エメラルドの原石
            4020004, // オパールの原石
            4020005, // サファイアの原石
            4020006 // トパーズの原石
        },
        {
            4010006, // 金の原料
            4020007, // ダイヤモンドの原石
            4020008 // 黒水晶の原石
        }
    };
    private final int map_exit = 103000100; // 地下鉄 : 切符売り場
    private boolean found = false;

    @Override
    public boolean action(int status, int answer) {
        if (this.found) {
            warp(this.map_exit);
            return false;
        }
        int index = -1;
        for (int i = 0; i < this.npc_ids.length; i++) {
            if (getNpcId() == this.npc_ids[i]) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return false;
        }
        int item_id = this.item_ids[index];
        if (checkItem(item_id)) {
            this.found = true;
            say(this.have_texts[index], false, true);
            return true;
        }
        int quest_status = getQuestStatus(this.quest_ids[index]);
        if (quest_status == 1) {
            if (!canHold(item_id, 1)) {
                say(this.full_texts[index]);
                return false;
            }
            gainItem(item_id, 1);
            this.found = true;
            say(this.found_texts[index], false, true);
            return true;
        }
        // クエストを完了している場合は2個
        int prize_count = quest_status == 2 ? 2 : 1;
        int[] prizes = this.prize_ids[index];
        int prize_id = prizes[Randomizer.nextInt(prizes.length)];
        if (!canHold(prize_id, prize_count)) {
            // 翻訳
            say("ETCインベントリがいっぱいなので、見つけたアイテムを持つことができない。アイテムのための空きを作ってから、また来よう。");
            return false;
        }
        gainItem(prize_id, prize_count);
        warp(this.map_exit);
        return false;
    }
}
