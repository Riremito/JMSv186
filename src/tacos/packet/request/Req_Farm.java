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

import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;

/**
 *
 * @author Riremito
 */
public class Req_Farm {

    public static boolean OnPacket(TacosCharacter chr, ClientPacketHeader header, ClientPacket cp) {
        if (chr == null) {
            return false;
        }

        TacosMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        switch (header) {
            case CP_JMS_FarmEnter: {
                chr.changeMapById(809100000);
                return true;
            }
            case CP_JMS_FarmLeave: {
                chr.changeMapById(100000000); // test
                return true;
            }
            default: {
                break;
            }

        }

        return false;
    }

}
