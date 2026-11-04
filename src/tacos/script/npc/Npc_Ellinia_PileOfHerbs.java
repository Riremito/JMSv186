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
public class Npc_Ellinia_PileOfHerbs extends NpcScriptMan {

    private final int quest_id = 2051; // サビトラマの若返りの薬の製作
    private final int npc_quest = 1061005; // サビトラマ
    private final int item_herb = 4031032; // 二股紅参
    private final int required_level = 50;
    // クエスト完了後の報酬 {確率 (30分率), アイテムID, 個数}
    private final int[][] prizes = {
        {10, 4020007, 2}, // ダイヤモンドの原石
        {10, 4020008, 2}, // 黒水晶の原石
        {9, 4010006, 2}, // 金の原料
        {1, 1032013, 1} // ハートイヤリング(赤)
    };
    private final int prize_total = 30;
    private final int map_exit = 101000000; // エリニア : エリニア

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getLevel() < this.required_level) {
                    // 翻訳
                    say("薬草の茂みの中に神秘的なエネルギーを持つ根があるが、周りの奇妙なオーラのせいで採ることができない。");
                    return false;
                }
                switch (getQuestStatus(this.quest_id)) {
                    case 1 -> {
                        // 翻訳
                        askYesNo("本当に#b#t" + this.item_herb + "##kを持っていきますか？");
                        return true;
                    }
                    case 2 -> {
                        int rand = Randomizer.nextInt(this.prize_total);
                        int[] prize = this.prizes[this.prizes.length - 1];
                        for (int[] p : this.prizes) {
                            if (rand < p[0]) {
                                prize = p;
                                break;
                            }
                            rand -= p[0];
                        }
                        if (!canHold(prize[1], prize[2])) {
                            // 翻訳
                            say("薬草の茂みで見つけたアイテムを入れられるように、装備インベントリとETCインベントリに空きを作る必要があります。整理してからもう一度確認してください。");
                            return false;
                        }
                        gainItem(prize[1], prize[2]);
                        warp(this.map_exit);
                        return false;
                    }
                    default -> {
                        // 翻訳
                        say("薬草の茂みの中に神秘的なエネルギーを持つ根があるが、#b" + getNpcName(this.npc_quest) + "#kから説明を受けていないので、どの根を採ればいいのかわからない…");
                        return false;
                    }
                }
            }
            case 1 -> {
                if (getQuestStatus(this.quest_id) != 1) {
                    return false;
                }
                if (!canHold(this.item_herb, 1)) {
                    // 翻訳
                    say("ETCインベントリがいっぱいのようです。アイテムを拾えるように空きを作ってください。");
                    return false;
                }
                gainItem(this.item_herb, 1);
                warp(this.map_exit);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
