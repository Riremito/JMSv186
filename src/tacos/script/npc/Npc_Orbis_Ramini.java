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
public class Npc_Orbis_Ramini extends NpcScriptMan {

    private final int item_ticket = 4031331; // リプレ行きのチケット(大人用)
    private final int item_ticket_child = 4031330; // リプレ行きのチケット(子供用)
    private final int map_ticket_office = 240000100; // リプレ : チケット売場

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case -1 -> {
                // 翻訳
                say("ここに何か用事があるんですよね？");
                return false;
            }
            case 0 -> {
                // TODO : 船の運航時間 (出発の5分前から搭乗可能) と乗客数 (50人) の確認, 現在は直接目的地へ移動する
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("いったん船に乗ると長旅になりますので急な用があれば先に解決してください。いかがですか？船に乗りますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // レベル30未満は子供用のチケットでも搭乗できる
                int ticket = this.item_ticket;
                if (getLevel() < 30 && !checkItem(this.item_ticket)) {
                    ticket = this.item_ticket_child;
                }
                if (!checkItem(ticket)) {
                    // 翻訳
                    say("あら…チケットをお持ちでないようですね。チケットがないと搭乗できません。チケット売り場でチケットをお買い求めください。");
                    return false;
                }
                removeItem(ticket, 1);
                warp(this.map_ticket_office);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
