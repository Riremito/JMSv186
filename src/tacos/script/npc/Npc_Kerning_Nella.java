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
// Npc.wz script : party1_out (JMS131-302), 会話文は lua_script_jp (party1_out) を利用
public class Npc_Kerning_Nella extends NpcScriptMan {

    private final int map_bonus = 103000805; // 隠れ道 : 一つ目の同行<Bonus>
    private final int map_exit = 103000890; // 隠れ道 : 一つ目の同行<出口>
    private final int map_kerning_city = 103000000; // カニングシティー : カニングシティー
    private final int item_coupon = 4001007; // クーポン
    private final int item_pass = 4001008; // 通行券

    @Override
    public boolean action(int status, int answer) {
        int map_id = getMapId();
        if (map_id == this.map_exit) {
            int count = getItemCount(this.item_coupon);
            if (0 < count && !exchange(0, this.item_coupon, -count)) {
                // 翻訳
                say("クーポンの枚数は本当に正しい？もう一度確認してね。");
                return false;
            }
            count = getItemCount(this.item_pass);
            if (0 < count && !exchange(0, this.item_pass, -count)) {
                // 翻訳
                say("通行証の枚数は本当に正しい？もう一度確認してね。");
                return false;
            }
            warp(this.map_kerning_city);
            return false;
        }
        boolean bonus = map_id == this.map_bonus;
        switch (status) {
            case 0 -> {
                // 翻訳
                if (bonus) {
                    askYesNo("ボーナスマップでたくさん狩りをした？ここから出たら、もう戻って狩りをすることはできないわ。本当に出る？");
                } else {
                    askYesNo("このマップから出ると、もう一度挑戦するにはクエストを最初からやり直さないといけないわ。それでもこのマップから出る？");
                }
                return true;
            }
            case 1 -> {
                warp(this.map_exit);
                return false;
            }
            default -> {
                // 翻訳
                if (bonus) {
                    say("わかったわ。このマップは時間切れになるまでできるだけたくさん狩りをするためのものよ。このステージから出たいときは、私に話しかけてね。");
                } else {
                    say("わかったわ。ここではチームワークがとても大切なの。パーティーメンバーと一緒にもっと頑張ってね。");
                }
                return false;
            }
        }
    }
}
