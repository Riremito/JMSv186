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
package tacos.script.portal;

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : statuegate_open, speargate_open, stonegate_open, metalgate_open, watergate_open, secretgate1_open, secretgate2_open, secretgate3_open, ghostgate_open, kinggate_open, kinggate2_open (JMS186), lua の処理を利用
// ギルドクエストの扉, 扉が開いている場合は移動できる
public class Portal_GuildQuest_GateOpen extends PortalScriptMan {

    private final int item_guard_stone = 1032033; // 守護石
    private final int map_castle_road = 990000301; // 城塞への道
    private final int map_center_passage = 990000401; // 中央通路
    private final int map_room_faith = 990000430; // 信念の部屋
    private final int map_room_restriction = 990000431; // 制約の部屋
    private final int map_waterway = 990000600; // 地下水路
    private final int map_maze_end_a = 990000611; // 迷路の終りA
    private final int map_maze_end_b = 990000631; // 迷路の終りB
    private final int map_maze_end_c = 990000641; // 迷路の終りC
    private final int map_king_corridor = 990000800; // 王の回廊
    private final int map_prince = 990000900; // エレゴスの王子

    @Override
    public boolean enter() {
        String key;
        int map_id;
        String portal_name;
        int gp = 0;
        String key_gp = "";
        switch (getScriptName()) {
            case "statuegate_open" -> {
                key = "statuegateopen";
                map_id = this.map_castle_road;
                portal_name = "st00";
                gp = 15;
                key_gp = "isIncGuildPointState01";
            }
            case "speargate_open" -> {
                key = "speargateopen";
                map_id = this.map_center_passage;
                portal_name = "st00";
                gp = 20;
                key_gp = "isIncGuildPointState02";
            }
            case "stonegate_open" -> {
                key = "stonegateopen";
                map_id = this.map_room_faith;
                portal_name = "out00";
            }
            case "metalgate_open" -> {
                key = "metalgateopen";
                map_id = this.map_room_restriction;
                portal_name = "out00";
            }
            case "watergate_open" -> {
                key = "watergateopen";
                map_id = this.map_waterway;
                portal_name = "st00";
                gp = 25;
                key_gp = "isIncGuildPointState03";
            }
            case "secretgate1_open" -> {
                key = "secretgate1open";
                map_id = this.map_maze_end_a;
                portal_name = "out00";
            }
            case "secretgate2_open" -> {
                key = "secretgate2open";
                map_id = this.map_maze_end_b;
                portal_name = "out00";
            }
            case "secretgate3_open" -> {
                key = "secretgate3open";
                map_id = this.map_maze_end_c;
                portal_name = "out00";
            }
            case "ghostgate_open" -> {
                key = "ghostgateopen";
                map_id = this.map_king_corridor;
                portal_name = "st00";
                gp = 10;
                key_gp = "isIncGuildPointState09";
            }
            case "kinggate_open" -> {
                key = "kinggateopen";
                map_id = this.map_prince;
                portal_name = "st00";
            }
            default -> {
                // kinggate2_open
                key = "kinggateopen";
                map_id = this.map_prince;
                portal_name = "st01";
            }
        }
        TacosFieldSet field_set = getFieldSet("Guild1");
        if (field_set == null) {
            return false;
        }
        if (!field_set.getVar(key).equals("yes")) {
            if (getScriptName().equals("kinggate2_open")) {
                // 翻訳
                message("ポータルは現在閉じています。");
            } else {
                // 翻訳
                message("入口は閉ざされています。");
            }
            return false;
        }
        if (getScriptName().equals("statuegate_open") && getItemCount(this.item_guard_stone) != 0) {
            // 翻訳
            message("なぜか守護石の力がおかしいようです。守護石は1つしか持てません。");
            return false;
        }
        if (0 < gp && !field_set.getVar(key_gp).equals("1")) {
            incGuildPoint(gp);
            field_set.setVar(key_gp, "1");
        }
        return transferField(map_id, portal_name);
    }
}
