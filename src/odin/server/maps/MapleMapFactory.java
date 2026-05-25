/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server.maps;

import java.util.HashMap;
import java.util.Map;
import tacos.server.map.MasterMonster;
import tacos.unofficial.CustomMap;

public class MapleMapFactory {

    private Map<Integer, MapleMap> maps = new HashMap<>();
    private int channel;

    public MapleMap getMap(int map_id) {
        MapleMap map = maps.get(map_id);
        if (map != null) {
            return map;
        }

        map = new MapleMap(map_id, channel);
        if (!map.loadData()) {
            return null;
        }
        // add custom npc.
        CustomMap.addNPCtoMap(map);

        MasterMonster.addAreaBossSpawn(map);
        map.loadMonsterRate(true);

        maps.put(map_id, map);
        return map;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
