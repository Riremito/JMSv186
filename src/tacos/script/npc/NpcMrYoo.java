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
public class NpcMrYoo extends NpcScriptMan {

    // チケット
    private final int[] item_tickets = {
        4001431, // ゴールデンチケット
        4001432 // プレミアムゴールデンチケット
    };
    private final int[] costs = {
        5000000,
        50000000
    };

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("金箔寺へようこそ！ゴールデンチケットを発行いたします。\r\n\r\n");
                // TODO : 文章 (適当)
                nt.addMenu(0, "#t" + this.item_tickets[0] + "# : " + this.costs[0] + "メル (1回のみ使用可能)");
                // TODO : 文章 (適当)
                nt.addMenu(1, "#t" + this.item_tickets[1] + "# : " + this.costs[1] + "メル");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.item_tickets.length <= answer) {
                    return false;
                }
                int item_id = this.item_tickets[answer];
                int cost = this.costs[answer];
                if (getPlayer().getMeso() < cost) {
                    // TODO : 文章 (適当)
                    say("メルが足りません。");
                    return false;
                }
                if (checkItem(item_id) || !MapleInventoryManipulator.checkSpace(getPlayer(), item_id, 1, "")) {
                    // TODO : 文章 (適当)
                    say("すでにお持ちか、持ち物に空きがありません。");
                    return false;
                }
                payMoney(cost);
                MapleInventoryManipulator.addById(getPlayer(), item_id, (short) 1);
                // TODO : 文章 (適当)
                say("ありがとうございます。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
