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
public class NpcPileOfFlowers extends NpcScriptMan {

    private final int quest_id = 2050; // サビトラマのダイエット薬作り
    private final int npc_quest = 1061005; // サビトラマ
    private final int item_flower = 4031020; // ピンクアンセリウム
    private final int required_level = 25;
    // クエスト完了後の報酬
    private final int[] prize_ids = {
        4010000, // 青銅の原料
        4010001, // 鋼鉄の原料
        4010002, // ミスリルの原料
        4010003, // アダマンティウムの原料
        4010004, // 銀の原料
        4010005, // オリハルコンの原料
        4020000, // ガーネットの原石
        4020001, // 紫水晶の原石
        4020002, // アクアマリンの原石
        4020003, // エメラルドの原石
        4020004, // オパールの原石
        4020005, // サファイアの原石
        4020006 // トパーズの原石
    };
    private final int prize_count = 2;
    private final int map_exit = 101000000; // エリニア : エリニア

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getPlayer().getLevel() < this.required_level) {
                    // 翻訳
                    say("これらの花の中に、神秘的なオーラに包まれたものがいくつかある。周りを包むオーラのせいで、摘むことができない。");
                    return false;
                }
                switch (getPlayer().getQuestStatus(this.quest_id)) {
                    case 1 -> {
                        // 翻訳
                        askYesNo("本当に#b#t" + this.item_flower + "##kを持っていきますか？");
                        return true;
                    }
                    case 2 -> {
                        int prize_id = this.prize_ids[(int) (Math.random() * this.prize_ids.length)];
                        if (!MapleInventoryManipulator.checkSpace(getPlayer(), prize_id, this.prize_count, "")) {
                            // 翻訳
                            say("花の中で見つけたアイテムをしまうには、ETCインベントリに空きスロットが少なくとも1つ必要です。空きを作ってからもう一度試してください。");
                            return false;
                        }
                        MapleInventoryManipulator.addById(getPlayer(), prize_id, (short) this.prize_count);
                        warp(this.map_exit);
                        return false;
                    }
                    default -> {
                        // 翻訳
                        say("これらの花の中に、神秘的なオーラに包まれたものがいくつかあるが、どれを摘めばいいのか#b" + getNpcName(this.npc_quest) + "#kからまだ説明を聞いていないので、摘むことができない…");
                        return false;
                    }
                }
            }
            case 1 -> {
                if (getPlayer().getQuestStatus(this.quest_id) != 1) {
                    return false;
                }
                if (!MapleInventoryManipulator.checkSpace(getPlayer(), this.item_flower, 1, "")) {
                    // 翻訳
                    say("ETCインベントリがいっぱいのようです。アイテムを拾えるように空きを作ってください。");
                    return false;
                }
                MapleInventoryManipulator.addById(getPlayer(), this.item_flower, (short) 1);
                warp(this.map_exit);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
