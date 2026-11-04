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
// Npc.wz script : NLC_Move (BMS8-24, CMS86-88, EMS70-76, GMS92-95), 会話文は lua_script_jp (NLC_Move) を利用
// TODO : 地下鉄の運行時間 (出発の1分前から搭乗可能) と乗客数 (50人) の確認, 現在は控え室 (600010002) を経由せず直接目的地へ移動する
public class Npc_NLC_NlcTicketGate extends NpcScriptMan {

    private final int map_kc_station = 103000100; // Victoria Road : Subway Ticketing Booth
    private final int item_ticket_kc_basic = 4031712; // Subway Ticket to Kerning City (Basic)
    private final int item_ticket_kc = 4031713; // Subway Ticket to Kerning City (Regular)
    private final int level_basic = 10;
    private int last_status = 0;

    @Override
    public boolean action(int status, int answer) {
        // 次へ・はい・選択 の場合は status が増える
        boolean ok = this.last_status <= status;
        this.last_status = status;
        if (status == 0) {
            // 翻訳
            askYesNo("この便はまだ空席がたくさんあるようです。ご乗車の際はチケットをご用意ください。長旅になりますが、目的地には無事に到着しますよ。いかがですか？この便に乗りますか？");
            return true;
        }
        if (!ok) {
            // 翻訳
            say("ここに何か用事があるんですよね？");
            return false;
        }
        int ticket = getLevel() <= this.level_basic ? this.item_ticket_kc_basic : this.item_ticket_kc;
        if (!exchange(0, ticket, -1)) {
            // 翻訳
            say("あら…チケットをお持ちでないようですね。チケットがないとご乗車いただけません。チケット売り場でチケットをお買い求めください。");
            return false;
        }
        warp(this.map_kc_station);
        return false;
    }
}
