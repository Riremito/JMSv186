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

/**
 *
 * @author Riremito
 */
// Npc.wz script : party_ludimaze_success (JMS131-302), 会話文は lua_script_jp (party_ludimaze_success) を利用
public class Npc_Zipangu_GuideLala extends NpcScriptMan {

    private final int item_coupon = 4001106; // 迷路クーポン
    private final int map_event_exit = 809050017; // イベント出口
    // 報酬, lua の random( 1, 91000 ) の上限, アイテムID, 個数
    private final int[][] rewards = {
        {300, 2040001, 1}, {600, 2040002, 1}, {900, 2040401, 1}, {1200, 2040402, 1}, {1500, 2040504, 1}, {1800, 2040505, 1},
        {2100, 2040601, 1}, {2400, 2040602, 1}, {2700, 2040901, 1}, {3000, 2040902, 1}, {3200, 2041017, 1}, {3400, 2041020, 1},
        {3700, 2041004, 1}, {4000, 2041005, 1}, {4300, 2040008, 1}, {4600, 2040009, 1}, {4900, 2040404, 1}, {5200, 2040405, 1},
        {5500, 2040510, 1}, {5800, 2040511, 1}, {6100, 2040604, 1}, {6400, 2040605, 1}, {6700, 2040904, 1}, {7000, 2040905, 1},
        {7300, 2041026, 1}, {7600, 2041027, 1}, {7900, 2041028, 1}, {8200, 2041029, 1}, {10200, 2020006, 100}, {13200, 2020007, 100},
        {18200, 4031562, 1}, {23200, 2022019, 50}, {28200, 2020008, 20}, {33200, 2001001, 5}, {38200, 2000006, 100}, {43200, 2020009, 100},
        {48990, 2022000, 50}, {53990, 2020010, 20}, {58990, 2001002, 5}, {63990, 2001000, 50}, {68990, 2000004, 5}, {73990, 2000005, 1},
        {78990, 2030008, 20}, {83990, 2030009, 20}, {88990, 2000006, 100}, {88991, 1072263, 1}, {89991, 1032013, 1}, {89999, 1302016, 1},
        {90000, 1332030, 1}, {90500, 1442017, 1}, {91000, 1322025, 1}
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                askYesNo("君たちのパーティーは素晴らしい頑張りで、クーポンを30枚以上集めたね。だから、一人ひとりにプレゼントがあるよ。プレゼントを受け取ったら、ルディブリアムに戻されるよ。じゃあ、今すぐプレゼントを受け取るかい？");
                return true;
            }
            case 1 -> {
                if (!hasEmptySlot(MapleInventoryType.EQUIP) || !hasEmptySlot(MapleInventoryType.USE)) {
                    // 翻訳
                    say("インベントリに少なくとも1つ空きがあるか確認してね。");
                    return false;
                }
                int rnum = random(1, 91000);
                int item_id = 0;
                int item_count = 0;
                for (int[] reward : this.rewards) {
                    if (rnum <= reward[0]) {
                        item_id = reward[1];
                        item_count = reward[2];
                        break;
                    }
                }
                int count = getItemCount(this.item_coupon);
                boolean rwd = (0 < count) ? exchange(0, this.item_coupon, -count, item_id, item_count) : exchange(0, item_id, item_count);
                if (!rwd) {
                    // 翻訳
                    say("ここで問題が起きているみたいだ。もう一度試してね。");
                    return false;
                }
                warp(this.map_event_exit);
                return false;
            }
            default -> {
                // 翻訳
                say("報酬を受け取ってルディブリアムに戻りたければ、教えてね！");
                return false;
            }
        }
    }
}
