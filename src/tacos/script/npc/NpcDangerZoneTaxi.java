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
public class NpcDangerZoneTaxi extends NpcScriptMan {

    private final int[] town_map_ids = {
        211000000, // エルナス : エルナス
        220000000, // ルディブリアム : ルディブリアム
        240000000 // リプレ : リプレ
    };
    private final int[] dungeon_map_ids = {
        211040200, // エルナス : 氷の谷2
        220050300, // ルディブリアム : 時間の通路
        240030000 // リプレ : 龍の森の入口
    };
    // TODO : 料金
    private final int[] costs = {
        0,
        0,
        0
    };
    private int to_map_id = 910000000; // フリーマーケット入口
    private int cost = 0;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                for (int i = 0; i < this.town_map_ids.length; i++) {
                    if (getMapId() == this.town_map_ids[i]) {
                        this.to_map_id = this.dungeon_map_ids[i];
                        this.cost = this.costs[i];
                        break;
                    }
                }
                NpcScriptTag nt = new NpcScriptTag();
                // 原文ママ
                nt.add("こんにちは！ダンジョン行きの特急タクシーです。" + getMapName(getMapId()) + "に乗って#b" + getMapName(this.to_map_id) + "#kに移動しますか。費用は#b" + this.cost + "メル#kです。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                // TODO : 料金の支払い
                warp(this.to_map_id);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
