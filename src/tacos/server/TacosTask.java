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
package tacos.server;

import odin.client.MapleCharacter;
import odin.server.maps.MapleMap;
import tacos.client.TacosClient;

/**
 *
 * @author Riremito
 */
public class TacosTask {

    // update task.
    public static boolean update(TacosClient client) {
        MapleCharacter player = client.getPlayer();
        if (player == null) {
            return false;
        }

        MapleMap map = player.getMap();
        if (map == null) {
            return false;
        }

        long time_current = System.currentTimeMillis();
        // world.
        player.getWorld().update(player, time_current);
        // channel.
        player.getChannelServer().update(player, time_current);
        // map.
        map.update(player, time_current);
        // player.
        player.update(time_current);
        return true;
    }

    private long time_updated;

    public TacosTask() {
        this.time_updated = System.currentTimeMillis();
    }

    public boolean check(long current_time, long interval) {
        if ((this.time_updated + interval) <= current_time) {
            this.time_updated = current_time;
            return true;
        }
        return false;
    }
}
