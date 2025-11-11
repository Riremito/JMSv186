/*
 * Copyright (C) 2025 Riremito
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
package tacos.packet.request;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.handling.channel.handler.PlayerHandler;
import tacos.packet.ClientPacket;
import odin.server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class Req_Farm {

    public static boolean OnPacket(ClientPacket.Header header, ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        switch (header) {
            case CP_JMS_FarmEnter: {
                PlayerHandler.ChangeMap(c, 809100000);
                return true;
            }
            case CP_JMS_FarmLeave: {
                PlayerHandler.ChangeMap(c, 100000000); // test
                return true;
            }
            default: {
                break;
            }

        }

        return false;
    }

}
