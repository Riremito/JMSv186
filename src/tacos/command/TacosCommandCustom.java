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
package tacos.command;

import java.awt.Point;
import odin.client.MapleCharacter;
import odin.server.maps.MapleMap;
import tacos.server.map.object.TacosDynamicPortal;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class TacosCommandCustom {

    public static boolean executeCommand(TacosCommander dcmd, MapleCharacter chr) {
        MapleMap map = chr.getMap();

        switch (dcmd.get(0)) {
            case "/petcharacter" -> {
                if (chr.getPetCharacter().remove()) {
                    chr.DebugMsg("PetCharacter : remove.");
                    return true;
                }

                chr.getPetCharacter().spawn(chr.getId());
                chr.DebugMsg("PetCharacter : sapwn.");
                return true;
            }
            case "/petmob" -> {
                if (!dcmd.check(1)) {
                    chr.getPetMob().remove();
                    chr.DebugMsg("PetMob : remove.");
                    return true;
                }

                int mob_id = dcmd.getInt(1);
                chr.getPetMob().spawn(mob_id);
                chr.DebugMsg("PetMob : sapwn.");
                return true;
            }
            case "/petnpc" -> {
                if (!dcmd.check(1)) {
                    chr.getPetNPC().remove();
                    chr.DebugMsg("PetNPC : remove.");
                    return true;
                }

                int npc_id = dcmd.getInt(1);
                chr.getPetNPC().spawn(npc_id);
                chr.DebugMsg("PetNPC : sapwn.");
                return true;
            }
            case "/addportal" -> {
                if (!dcmd.check(1)) {
                    return true;
                }
                int map_id_to = dcmd.getInt(1);

                if (map_id_to == 0 || !WzDataStorage.MAP.check(map_id_to)) {
                    chr.DebugMsg("AddPortal : invalid map id.");
                    return true;
                }

                Point player_xy = chr.getPosition();
                TacosDynamicPortal dynamic_portal = new TacosDynamicPortal(2420004, map_id_to, player_xy.x, player_xy.y);
                map.addDynamicPortal(dynamic_portal);
                chr.DebugMsg("AddPortal : " + chr.getPosMap() + " -> " + map_id_to);
                return true;
            }
            default -> {
            }
        }

        return false;
    }
}
