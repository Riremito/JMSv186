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
// Npc.wz script : NLC_ticketing (BMS8-24, CMS86-88, EMS70-76, GMS92-95), 会話文は lua_script_jp (NLC_ticketing) を利用
// lua ではスクリプト全体がコメントアウトされているが、コメントアウトされている処理を参考に実装
public class Npc_NLC_Bell extends NpcScriptMan {

    private final int map_nlc_station = 600010001; // New Leaf City : NLC Subway Station
    private final int map_kc_station = 103000100; // Victoria Road : Subway Ticketing Booth
    private final int map_nlc_waiting = 600010002; // New Leaf City : Waiting Room(From NLC to KC)
    private final int map_kc_waiting = 600010004; // Kerning City Town Street : Waiting Room(From KC to NLC)
    private final int item_ticket_nlc_basic = 4031710; // Subway Ticket to NLC (Basic)
    private final int item_ticket_nlc = 4031711; // Subway Ticket to NLC (Regular)
    private final int item_ticket_kc_basic = 4031712; // Subway Ticket to Kerning City (Basic)
    private final int item_ticket_kc = 4031713; // Subway Ticket to Kerning City (Regular)
    private final int level_basic = 10;
    private final int cost_basic = 1000;
    private final int cost = 5000;
    private int ticket = 0;
    private int ticket_cost = 0;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        int map_id = getMapId();
        // 控え室
        if (map_id == this.map_nlc_waiting || map_id == this.map_kc_waiting) {
            if (status == 0) {
                if (map_id == this.map_nlc_waiting) {
                    // 翻訳
                    askYesNo("今すぐニューリーフシティの地下鉄駅に戻りますか？");
                } else {
                    // 翻訳
                    askYesNo("今すぐカニングシティの地下鉄駅に戻りますか？");
                }
                return true;
            }
            if (!ok) {
                // 翻訳
                say("わかりました。少々お待ちください～！");
                return false;
            }
            warp(map_id == this.map_nlc_waiting ? this.map_nlc_station : this.map_kc_station, "st00");
            return false;
        }
        // 地下鉄のチケットの販売
        if (map_id != this.map_nlc_station && map_id != this.map_kc_station) {
            return false;
        }
        boolean basic = getPlayer().getLevel() <= this.level_basic;
        if (!ok) {
            // 翻訳
            say("ここに何か用事があるんですよね？");
            return false;
        }
        if (status == 0) {
            NpcScriptTag nt = new NpcScriptTag();
            if (map_id == this.map_nlc_station) {
                // 翻訳
                nt.add("こんにちは。地下鉄のチケットを購入しますか？\r\n#b#L0# ビクトリアアイランドのカニングシティ#l#k", false);
            } else {
                // 翻訳
                nt.add("こんにちは。地下鉄のチケット販売を担当しております。どのチケットをお求めですか？\r\n#b#L0# マステリアのニューリーフシティ#l#k", false);
            }
            askMenu(nt);
            return true;
        }
        if (status == 1) {
            if (answer != 0) {
                return false;
            }
            this.ticket_cost = basic ? this.cost_basic : this.cost;
            if (map_id == this.map_nlc_station) {
                this.ticket = basic ? this.item_ticket_kc_basic : this.item_ticket_kc;
                // 翻訳
                askYesNo("ビクトリアアイランドのカニングシティ行きの便は、毎時0分から10分おきに出発し、料金は#b" + this.ticket_cost + "メル#kです。#b#t" + this.ticket + "##kを購入しますか？");
            } else {
                this.ticket = basic ? this.item_ticket_nlc_basic : this.item_ticket_nlc;
                // 翻訳
                askYesNo("マステリアのニューリーフシティ行きの便は、毎時0分から10分おきに出発し、料金は#b" + this.ticket_cost + "メル#kです。#b#t" + this.ticket + "##kを購入しますか？");
            }
            return true;
        }
        if (!exchange(-this.ticket_cost, this.ticket, 1)) {
            // 翻訳
            say("本当に#b" + this.ticket_cost + "メル#kをお持ちですか？お持ちでしたら、ETCインベントリがいっぱいでないか確認してください。");
        }
        return false;
    }
}
