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
package odin.server.life;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import tacos.wz.WzXML;

public class MapleLifeFactory {

    private static Map<Integer, MapleMonsterStats> monsterStats = new HashMap<>();
    private static Map<Integer, String> npcNames = new HashMap<>();

    public static Object getLife(int id, String type) {
        if (type.equalsIgnoreCase("n")) {
            return getNPC(id);
        } else if (type.equalsIgnoreCase("m")) {
            // randomize mob
            //id = DWI_LoadXML.getMob().getRandom();
            return getMonster(id);
        } else {
            System.err.println("Unknown Life type: " + type + "");
            return null;
        }
    }

    public static List<Integer> getQuestCount(int id) {
        return WzXML.MOB.getQuestCountGroup().get(id);
    }

    public static MapleMonster getMonster(int mob_id) {
        MapleMonsterStats stats = monsterStats.get(mob_id);

        if (stats == null) {
            stats = WzXML.MOB.loadMonsterStats(mob_id);
            if (stats == null) {
                return null;
            }
            monsterStats.put(mob_id, stats);
        }
        return new MapleMonster(mob_id, stats);
    }


    public static MapleNPC getNPC(int npc_id) {
        String name = npcNames.get(npc_id);
        if (name == null) {
            name = WzXML.STRING.getNpcName(npc_id);
            npcNames.put(npc_id, name);
        }
        return new MapleNPC(npc_id, name);
    }
}
