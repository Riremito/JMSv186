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

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Npc.wz script : party_ludimaze_goal (JMS131-302), 会話文は lua_script_jp (party_ludimaze_goal) を利用
public class NpcPietoru extends NpcScriptMan {

    private final String field_set_name = "PartyLudiMaze";
    private final int item_coupon = 4001106; // 迷路クーポン
    private final int map_exchange = 809050016; // 商品交換所
    private final int map_event_exit = 809050017; // イベント出口
    private final int coupon_bonus = 30;
    private final int exp_per_coupon = 50;
    private int coupon_count = 0;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        TacosFieldSet field_set = getFieldSet(this.field_set_name);
        if (field_set == null) {
            return false;
        }
        if (field_set.getUserCount() != getUserCount()) {
            // 翻訳
            say("今はパーティーメンバーが全員そろっていないようだね。全員の準備ができたら教えてね。");
            return false;
        }
        if (!isPartyBoss()) {
            // 翻訳
            String[] says = {
                "迷路の脱出おめでとう！迷路で行く手をふさぐモンスターからクーポンは集めたかい？",
                "パーティーが集めたクーポンの枚数によっては、サプライズボーナスがあるかもしれないよ！",
                "パーティーメンバーからクーポンを全部集めたら、#bパーティーリーダー#kに僕に話しかけるよう伝えてね。"
            };
            if (status < 0 || says.length <= status) {
                return false;
            }
            say(says[status], 0 < status, status < says.length - 1);
            return status < says.length - 1;
        }
        // 次へ・はい の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        if (!ok) {
            // 翻訳
            if (status < 0) {
                say("もう一度確認して、準備ができたら教えてね。");
            } else {
                say("もっとクーポンを集めたいんだね。出口ステージに入りたくなったら教えてね。");
            }
            return false;
        }
        switch (status) {
            case 0 -> {
                this.coupon_count = getItemCount(this.item_coupon);
                // 翻訳
                askYesNo("みんなで力を合わせて#b" + this.coupon_count + "枚のクーポン#kを集めたんだね。パーティーが集めたのはこれで全部かい？");
                return true;
            }
            case 1 -> {
                // 翻訳
                askYesNo("よくやったね！迷路のクーポンを30枚集めれば、素敵な賞品がもらえるよ！出口に向かうかい？");
                return true;
            }
            case 2 -> {
                if (removeItemAll(this.item_coupon)) {
                    field_set.incExpAll(this.exp_per_coupon * this.coupon_count);
                }
                // TODO : lua の transferParty の第3引数 (2) の用途は不明
                if (this.coupon_count < this.coupon_bonus) {
                    transferParty(this.map_event_exit, "");
                } else {
                    transferParty(this.map_exchange, "");
                }
                return false;
            }
            default -> {
                return false;
            }
        }
    }
}
