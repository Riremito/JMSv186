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
public class NpcCloto extends NpcScriptMan {

    private final int item_coupon = 4001007; // クーポン
    private final int item_pass = 4001008; // 通行券
    private final int map_bonus = 103000805; // 一つ目の同行<Bonus>

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : グループクエスト (KerningPQ) の各ステージの処理 (中国語の文章が文字化けしているため未移行)
                // イベント外 : please talk on kpq
                // 103000800 <1st> : メンバーは問題 (8, 10, 20, 25, 25, 30, 35) と同じ数のクーポン (item_coupon) を集めて通行券 (item_pass) と交換, リーダーは人数-1枚の通行券でクリア, グループ経験値 100
                //                   I'm sorry, but you are short on the number of passes. You need to give me the right number of passes; it should be the number of members of your party minus the leader, " + strpasses + " to clear the stage. Tell your party members to solve the questions, gather up the passes, and give them to you.
                //                   You gathered up " + strpasses + "! Congratulations on clearing the stage! I'll make the portal that sends you to the next stage. There's a time limit on getting there, so please hurry. Best of luck to you all!
                // 103000801 - 103000803 <2nd> - <4th> : ロープ, 足場, 樽の上に3人が乗って正しい組み合わせならクリア, グループ経験値 2^stage * 50
                //                   Hi. Welcome to the " + nthtext + " stage. Next to me, you'll see a number of " + nthobj + ". Out of these " + nthobj + ", #b3 are connected to the portal that sends you to the next stage#k. ...
                //                   It looks like you haven't found the 3 " + nthobj + " just yet. Please think of a different combination of " + nthobj + ". ...
                // 103000804 <Last> : リーダーは通行券 10枚でクリア, グループ経験値 1500, クリア後は報酬 (呪文書, 消費, 装備, ETC) を渡してボーナスマップ (map_bonus) に移動
                //                   Incredible! You cleared all the stages to get to this point. Here's a small prize for your job well done. Before you accept it, however, please make sure your use and etc. inventories have empty slots available.
                //                   #bYou will not receive a prize if you have no free slots!#k
                // その他のマップ : Invalid map, this means the stage is incomplete.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
