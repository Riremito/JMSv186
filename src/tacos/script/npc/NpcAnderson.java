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

import odin.server.quest.MapleQuest;

/**
 *
 * @author Riremito
 */
// Npc.wz script : party2_out (JMS131-302), 会話文は lua_script_jp (party2_out) を利用
public class NpcAnderson extends NpcScriptMan {

    private final int map_exit = 922010000; // ルディブリアム : 放置された塔<冒険の終わり>
    private final int map_eos_tower_101 = 221024500; // ルディブリアム : エオス塔101階
    private final int item_pass = 4001022; // 次元の通行券
    private final int item_key = 4001023; // 次元のカギ
    private final int quest_party = 7011; // パーティークエストのデータ

    @Override
    public boolean action(int status, int answer) {
        if (getMapId() == this.map_exit) {
            int count = getPlayer().getItemQuantity(this.item_pass, false);
            if (0 < count && !exchange(0, this.item_pass, -count)) {
                // 翻訳
                say("#t" + this.item_pass + "#の枚数は本当に正しい？もう一度確認してね。");
                return false;
            }
            count = getPlayer().getItemQuantity(this.item_key, false);
            if (0 < count && !exchange(0, this.item_key, -count)) {
                // 翻訳
                say("#t" + this.item_key + "#の数は本当に正しい？もう一度確認してね。");
                return false;
            }
            MapleQuest.getInstance(this.quest_party).forfeit(getPlayer());
            warp(this.map_eos_tower_101);
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                askYesNo("このステージから出た後でもう一度このクエストに挑戦するなら、最初からやり直しになるぞ。本当にこのマップから出るか？");
                return true;
            }
            case 1 -> {
                MapleQuest.getInstance(this.quest_party).forfeit(getPlayer());
                warp(this.map_exit);
                return false;
            }
            default -> {
                // 翻訳
                say("わかった。パーティーメンバーと力を合わせて、もっと頑張れ！");
                return false;
            }
        }
    }
}
