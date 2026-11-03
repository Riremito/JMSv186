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
public class NpcShipTicketSeller extends NpcScriptMan {

    private final int[] map_stations = {
        220000100, // ルディブリアム : チケット売場
        101000300, // ビクトリアロード : エリニアステーション
        260000100 // アリアント : アリアント乗降場
    };
    // 翻訳
    private final String[] infos = {
        "こんにちは。オシリア大陸のオルビス駅行きの船のチケット販売を担当しております。オルビス行きの便は、毎時0分から10分おきに出発し、",
        "こんにちは。オシリア大陸のオルビス駅行きの船のチケット販売を担当しております。オルビス行きの便は、毎時0分から15分おきに出発し、",
        "こんにちは。オシリア大陸のオルビス駅行きの船のチケット販売を担当しております。オルビス行きの便は、毎時0分から10分おきに出発し、" // TODO : luaに記載なし
    };
    private final int item_ticket = 4031045; // オルビス行きのチケット(大人用)
    private final int item_ticket_child = 4031044; // オルビス行きのチケット(子供用)
    // レベル30以上
    private final int[] costs = {
        6000,
        5000,
        6000 // TODO : luaに記載なし
    };
    // レベル30未満
    private final int[] costs_child = {
        2000,
        1000,
        6000 // TODO : luaに記載なし, 子供用のチケットの有無が不明
    };
    private int ticket = 0;
    private int cost = 0;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // 購入確認でいいえを選択した
                if (this.ticket != 0) {
                    // 翻訳
                    say("ここに何か用事があるんですよね？");
                }
                return false;
            }
            case 0 -> {
                if (getPlayer().getLevel() < 10) {
                    // 翻訳
                    say("あなたのレベルはまだ低すぎるようです。安全上の理由から、レベル10未満の方はこの航路をご利用いただけません。");
                    return false;
                }
                int index = -1;
                for (int i = 0; i < this.map_stations.length; i++) {
                    if (getMapId() == this.map_stations[i]) {
                        index = i;
                    }
                }
                if (index < 0) {
                    return false;
                }
                boolean child = getPlayer().getLevel() < 30 && getMapId() != this.map_stations[2];
                this.ticket = child ? this.item_ticket_child : this.item_ticket;
                this.cost = child ? this.costs_child[index] : this.costs[index];
                // 翻訳
                askYesNo(this.infos[index] + "料金は#b" + this.cost + "メル#kです。#b#t" + this.ticket + "##kを購入しますか？");
                return true;
            }
            case 1 -> {
                if (getPlayer().getMeso() < this.cost || !MapleInventoryManipulator.checkSpace(getPlayer(), this.ticket, 1, "")) {
                    // 翻訳
                    say("本当に#b" + this.cost + "メル#kをお持ちですか？お持ちでしたら、ETCインベントリがいっぱいでないか確認してみてください。");
                    return false;
                }
                payMoney(this.cost);
                MapleInventoryManipulator.addById(getPlayer(), this.ticket, (short) 1);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
