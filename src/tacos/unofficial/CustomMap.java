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
package tacos.unofficial;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleNPC;
import odin.server.maps.MapleMap;
import tacos.config.Config;
import tacos.config.Region;
import tacos.debug.DebugLogger;
import tacos.property.Property_Java;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class CustomMap {

    public static boolean addNPCtoMap(MapleMap map) {
        if (!Config.GreaterOrEqual(Region.JMS, 186)) {
            return false;
        }

        int map_id = map.getId();
        Path file = Paths.get(Property_Java.getDir_Scripts() + "map/" + map_id + ".txt");
        if (Files.notExists(file)) {
            return false;
        }

        List<String> file_text = null;
        try {
            file_text = Files.readAllLines(file); // UTF-8
        } catch (IOException ex) {
            DebugLogger.ExceptionLog("addNPCtoMap : " + map_id);
            return false;
        }

        for (String cnpc_data : file_text) {
            String[] npc_data = cnpc_data.split(",");
            if (npc_data.length != 4) {
                DebugLogger.ErrorLog("addNPCtoMap : " + map_id + ", txt format.");
                continue;
            }

            int npc_id = Integer.parseInt(npc_data[0]);
            int npc_x = Integer.parseInt(npc_data[1]);
            int npc_y = Integer.parseInt(npc_data[2]);
            int npc_fh = Integer.parseInt(npc_data[3]);

            if (!WzDataStorage.NPC.check(npc_id)) {
                DebugLogger.ErrorLog("addNPCtoMap : " + map_id + ", invalid npc id = " + npc_id);
                continue;
            }

            MapleNPC npc = MapleLifeFactory.getNPC(npc_id);
            if (npc == null) {
                DebugLogger.ErrorLog("addNPCtoMap : " + map_id + ", npc is null = " + npc_id);
                continue;
            }

            npc.setPosition(new Point(npc_x, npc_y));
            npc.setCy(npc_y);
            npc.setRx0(npc_x + 50);
            npc.setRx1(npc_x - 50);
            npc.setFh(npc_fh);
            npc.setCustom(true);
            map.addMapObject(npc);
        }

        return true;
    }
}
