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
public class Npc_Zipangu_Chan extends NpcScriptMan {

    // 金箔寺
    private final int[] map_golden_temples = {
        809060000, // 隠れ道 : 金箔寺
        950100000 // 隠れ道 : 金箔寺
    };
    // 鬼の洞窟 (809060000)
    private final int[] map_rooms_809 = {
        809060500, // 隠れ道 : 鬼の洞窟1
        809060600, // 隠れ道 : 鬼の洞窟2
        809060700 // 隠れ道 : 鬼の洞窟3
    };
    // 鬼の洞窟 (950100000)
    private final int[] map_rooms_950 = {
        950100500, // 隠れ道 : 鬼の洞窟1
        950100600, // 隠れ道 : 鬼の洞窟2
        950100700 // 隠れ道 : 鬼の洞窟3
    };
    private final int item_ticket = 4001431; // ゴールデンチケット
    private final int item_premium_ticket = 4001432; // プレミアムゴールデンチケット

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                // TODO : 文章 (適当)
                nt.add("こんにちは。鬼の洞窟の入口へようこそ。どこへ行きますか？#r入場には金箔寺のチケットが必要です。ここのモンスターはすべて太陽の炎を落とします。ボスのラーヴァナに挑むにはこれが必要です。#k\r\n\r\n");
                // TODO : 文章 (適当)
                nt.addMenu(0, "鬼の洞窟1 - ブルーゴブリン (HP 2200 / EXP 170)");
                // TODO : 文章 (適当)
                nt.addMenu(1, "鬼の洞窟2 - レッドゴブリン (HP 4150 / EXP 336)");
                // TODO : 文章 (適当)
                nt.addMenu(2, "鬼の洞窟3 - ストーンゴブリン (HP 9300 / EXP 501)");
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
