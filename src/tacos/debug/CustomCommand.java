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
package tacos.debug;

import java.awt.Point;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.server.maps.MapleDynamicPortal;
import odin.server.maps.MapleMap;
import tacos.packet.response.Res_JMS_CInstancePortalPool;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.wz.ids.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class CustomCommand {

    public static boolean executeCommand(DebugCommander dcmd, MapleCharacter chr) {
        MapleClient client = chr.getClient();
        MapleMap map = chr.getMap();

        switch (dcmd.get(0)) {
            case "/petcharacter":
            case "/petchr":
            case "/clone": {
                if (chr.getPetCharacter().remove()) {
                    chr.DebugMsg("PetCharacter : remove.");
                    return true;
                }
                chr.getPetCharacter().spawn(chr.getId());
                chr.DebugMsg("PetCharacter : sapwn.");
                return true;
            }
            case "/petmob": {
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
            case "/petnpc": {
                if (!dcmd.check(1)) {
                    chr.getPetNPC().remove();
                    chr.DebugMsg("PetNPC : remove.");
                    return true;
                }

                int mob_id = dcmd.getInt(1);
                chr.getPetNPC().spawn(mob_id);
                chr.DebugMsg("PetNPC : sapwn.");
                return true;
            }
            case "/slot": {
                ResWrapper.MiroSlot(chr);
                return true;
            }
            case "/wh": {
                for (MapleCharacter victim : chr.getChannelServer().getOnlinePlayers().get()) {
                    if (victim != chr) {
                        victim.changeMapWithCoordinate(map.getId(), chr.getPosition().x, chr.getPosition().y);
                    }
                }
                return true;
            }
            case "/addportal": {
                if (!dcmd.check(1)) {
                    return true;
                }
                int map_id_to = dcmd.getInt(1);

                if (map_id_to == 0 || !WzDataStorage.MAP.check(map_id_to)) {
                    chr.DebugMsg("[AddPortal] Invalid MapID.");
                    return true;
                }

                Point player_xy = chr.getPosition();
                MapleDynamicPortal dynamic_portal = new MapleDynamicPortal(2420004, map_id_to, player_xy.x, player_xy.y);
                map.addMapObject(dynamic_portal);
                map.broadcastMessage(Res_JMS_CInstancePortalPool.InstancePortalCreated(dynamic_portal));
                chr.DebugMsg("[AddPortal] " + chr.getPosMap() + " -> " + map_id_to);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
