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
package tacos.task;

import odin.client.MapleCharacter;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleMapItem;
import tacos.packet.ops.OpsMobAppear;
import tacos.packet.response.ResCDropPool;
import tacos.packet.response.ResCMobPool;
import tacos.server.map.TacosSpawnPoint;

/**
 *
 * @author Riremito
 */
public class MapTask {

    private static final int DROP_ITEM_EXPIRED = 15000;

    public static boolean update(MapleCharacter chr, MapleMap map, long time) {
        if (!map.updateTime(time, 5000)) {
            return false;
        }
        // drop removal.
        for (MapleMapItem mmi : map.getAllItems()) {
            long object_created_time = mmi.getTime();
            if (object_created_time == 0) {
                continue;
            }
            long delta = time - object_created_time;
            if (DROP_ITEM_EXPIRED <= delta) {
                map.removeMapObject(mmi);
                map.broadcastMessage(ResCDropPool.DropLeaveField(mmi, ResCDropPool.LeaveType.EXPIRED));
            }
        }
        // mob respawn.
        for (TacosSpawnPoint sp : chr.getMap().getMonsterSpawnPoint()) {
            if (sp.getLastRegenTime() + map.getCreateMobInterval() <= time) {
                MapleMonster monster = sp.regen(map);
                if (monster != null) {
                    map.addMonster(monster);
                    map.broadcastMessage(ResCMobPool.MobEnterField(monster));
                    chr.SendPacket(ResCMobPool.MobChangeController(monster, false));
                    monster.setAT(OpsMobAppear.MOBAPPEAR_NORMAL);
                    monster.setATEx(OpsMobAppear.MOBAPPEAR_NORMAL.get());
                }
            }
        }

        //map.updateSpawn();
        return true;
    }
}
