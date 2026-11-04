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

import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class Npc_Sleepywood_PileOfViolas extends NpcScriptMan {

    // NPCごとの設定 (桃色, 青色, 白色)
    private final int[] npc_ids = {
        1063000, // 桃色の花山
        1063001, // 青色の花山
        1063002 // 白色の花山
    };
    private final int[] quest_ids = {
        2052, // ゾーンの花箱(桃)
        2053, // ゾーンが用意したプレゼント
        2054 // ゾーンが用意した最後のプレゼント
    };
    private final int[] item_ids = {
        4031025, // ピンクビオラ
        4031026, // ブルービオラ
        4031028 // ホワイトビオラ
    };
    private final int[] item_counts = {
        10,
        20,
        30
    };
    // クエスト完了後の報酬
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
    private final int prize_count = 2;
    private final int map_exit = 105040300; // 中央ダンジョン : スリーピーウッド
    private final int map_john = 104000000; // リス : 港口

    @Override
    public boolean action(int status, int answer) {
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
        switch (getPlayer().getQuestStatus(this.quest_ids[index])) {
            case 0 -> {
                // 翻訳
                say("このあたりにはたくさんの花が咲いているが、#b#t" + item_id + "##kだけは咲いていない。");
                return false;
            }
            case 1 -> {
                if (checkItem(item_id)) {
                    // 翻訳
                    say("#b#t" + item_id + "##kがたくさん咲いているが、すでに持っているので、今は摘むことができない。#m" + this.map_john + "#のジョンから花をもらう必要がある。");
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), item_id, this.item_counts[index], "")) {
                    // 翻訳
                    say("申し訳ありませんが、ETCインベントリがいっぱいなので花をしまうことができません。花のために空きスロットを少なくとも1つ作ってください。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), item_id, (short) this.item_counts[index]);
                warp(this.map_exit);
                return false;
            }
            default -> {
                int[] prizes = this.prize_ids[index];
                int prize_id = prizes[(int) (Math.random() * prizes.length)];
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), prize_id, this.prize_count, "")) {
                    // 翻訳
                    say("ETCインベントリがいっぱいなので、これ以上アイテムを受け取れません。空きスロットを少なくとも1つ作ってください。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), prize_id, (short) this.prize_count);
                warp(this.map_exit);
                return false;
            }
        }
    }
}
