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
import odin.server.MapleInventoryManipulator;

/**
 *
 * @author Riremito
 */
public class NpcGeras extends NpcScriptMan {

    private final int item_ticket = 4031576; // アリアント行きチケット(一般)
    private final int item_ticket_child = 4031575; // アリアント行きチケット(ビギナー)
    private final int map_station = 260000100; // アリアント : アリアント乗降場

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
                // TODO : アリアント航路のチケットの条件が不明 (オルビス航路と同様に扱う)
                // レベル30未満は子供用のチケットでも搭乗できる
                int ticket = this.item_ticket;
                if (getPlayer().getLevel() < 30 && !checkItem(this.item_ticket)) {
                    ticket = this.item_ticket_child;
                }
                if (!checkItem(ticket)) {
                    // 翻訳
                    say("あら…チケットをお持ちでないようですね。チケットがないと搭乗できません。チケット売り場でチケットをお買い求めください。");
                    return false;
                }
                MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, ticket, 1, true, false);
                warp(this.map_station);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
