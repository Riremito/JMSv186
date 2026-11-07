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
// Map.wz portal script : guild1F01, guild1F02, guild1F03, guild1F04 (JMS186), lua の処理を利用
// 迷路の終りからシャレン3世の墓へ移動
public class Portal_GuildQuest_Guild1F extends PortalScriptMan {

    private final int quest_guild_maze = 7600; // 通った迷路
    private final int map_sharen_grave = 990000700; // シャレン3世の墓
    private final int gp = 5;

    @Override
    public boolean enter() {
        // guild1F01 = 0, guild1F02 = 1, ...
        int index = integer(substring(getScriptName(), 7, 2)) - 1;
        TacosFieldSet field_set = getFieldSet("Guild1");
        if (field_set == null) {
            return false;
        }
        String key_gp = "isIncGuildPointState0" + (index + 4);
        if (!field_set.getVar(key_gp).equals("1")) {
            incGuildPoint(this.gp);
            field_set.setVar(key_gp, "1");
        }
        setQuestData(this.quest_guild_maze, String.valueOf(index));
        return transferField(this.map_sharen_grave, "st00");
    }
}
