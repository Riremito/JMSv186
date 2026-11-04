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
public class Npc_Zipangu_Dao extends NpcScriptMan {

    // 金箔寺
    private final int[] map_golden_temples = {
        809060000, // 隠れ道 : 金箔寺
        950100000 // 隠れ道 : 金箔寺
    };
    // 猿の寺 (809060000)
    private final int[] map_rooms_809 = {
        809060100, // 隠れ道 : 猿の寺1
        809060200, // 隠れ道 : 猿の寺2
        809060300, // 隠れ道 : 猿の寺3
        809060400 // 隠れ道 : 猿の寺4
    };
    // 猿の寺 (950100000)
    private final int[] map_rooms_950 = {
        950100100, // 隠れ道 : 猿の寺1
        950100200, // 隠れ道 : 猿の寺2
        950100300, // 隠れ道 : 猿の寺3
        950100400 // 隠れ道 : 猿の寺4
    };
    private final int item_ticket = 4001431; // ゴールデンチケット
    private final int item_premium_ticket = 4001432; // プレミアムゴールデンチケット

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("こんにちは。猿の寺の入口へようこそ。どこへ行きますか？#r入場には金箔寺のチケットが必要です。#k\r\n\r\n");
                // TODO : 文章 (適当)
                nt.addMenu(0, "猿の寺1 - ワイルドモンキー (HP 250 / EXP 52)");
                // TODO : 文章 (適当)
                nt.addMenu(1, "猿の寺2 - ママモンキー (HP 350 / EXP 70)");
                // TODO : 文章 (適当)
                nt.addMenu(2, "猿の寺3 - ホワイトベビーモンキー (HP 650 / EXP 120)");
                // TODO : 文章 (適当)
                nt.addMenu(3, "猿の寺4 - ホワイトママモンキー (HP 1040 / EXP 200)");
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_rooms_809.length <= answer) {
                    return false;
                }
                int map_id = 0;
                if (getMapId() == this.map_golden_temples[0]) {
                    map_id = this.map_rooms_809[answer];
                } else if (getMapId() == this.map_golden_temples[1]) {
                    map_id = this.map_rooms_950[answer];
                } else {
                    return false;
                }
                if (!checkItem(this.item_ticket) && !checkItem(this.item_premium_ticket)) {
                    // TODO : 文章 (適当)
                    say("金箔寺のチケットが必要です。");
                    return false;
                }
                if (0 < getPlayer().findMap(map_id).getCharactersSize()) {
                    // TODO : 文章 (適当)
                    say("すでに誰かがマップの中にいます。");
                    return false;
                }
                if (!checkItem(this.item_premium_ticket)) {
                    MapleInventoryManipulator.removeById(getPlayer(), MapleInventoryType.ETC, this.item_ticket, 1, true, false);
                }
                warp(map_id);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
