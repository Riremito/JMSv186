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

/**
 *
 * @author Riremito
 */
// Map.wz portal script : guild1F00 (JMS186), lua の処理を利用
// シャレン3世の墓から来た迷路の終りへ戻る
public class Portal_GuildQuest_Guild1F00 extends PortalScriptMan {

    private final int quest_guild_maze = 7600; // 通った迷路
    private final int map_maze_end_a = 990000611; // 迷路の終りA
    private final int map_waterway_maze2 = 990000620; // 水路の迷路2
    private final int map_maze_end_b = 990000631; // 迷路の終りB
    private final int map_maze_end_c = 990000641; // 迷路の終りC

    @Override
    public boolean enter() {
        String val = getQuestData(this.quest_guild_maze);
        int map_id;
        switch (val) {
            case "0" -> {
                map_id = this.map_maze_end_a;
            }
            case "1" -> {
                map_id = this.map_waterway_maze2;
            }
            case "2" -> {
                map_id = this.map_maze_end_b;
            }
            default -> {
                map_id = this.map_maze_end_c;
            }
        }
        transferField(map_id, "st00");
        forceCompleteQuest(this.quest_guild_maze);
        return true;
    }
}
