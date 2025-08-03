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
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import debug.Debug;
import packet.ClientPacket;
import packet.ops.OpsContiMove;
import packet.response.ResCField_ContiMove;
import scripting.EventManager;
import scripting.EventScriptManager;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqCField {

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_CONTISTATE: {
                int unused_map_id = cp.Decode4();
                byte unk1 = cp.Decode1();
                OnContiState(chr, map.getId());
                return true;
            }
            case CP_RequestFootHoldInfo: {
                return true;
            }
            case CP_FootHoldInfo: {
                return true;
            }
            case CP_JMS_PachinkoPrizes: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    private static boolean OnContiState(MapleCharacter chr, int map_id) {
        EventScriptManager esm = chr.getClient().getChannelServer().getEventSM();
        EventManager em = null;

        switch (map_id) {
            case 101000300: // Ellinia Station >> Orbis
            case 200000111: // Orbis Station >> Ellinia
            {
                chr.SendPacket(ResCField_ContiMove.ContiState(OpsContiMove.CONTI_WAIT));
                return true;
            }
            case 200000121: // Orbis Station >> Ludi
            case 220000110: // Ludi Station >> Orbis
            {
                chr.SendPacket(ResCField_ContiMove.ContiState(OpsContiMove.CONTI_WAIT));
                return true;
            }
            case 200000151: // Orbis Station >> Ariant
            case 260000100: // Ariant Station >> Orbis
            {
                chr.SendPacket(ResCField_ContiMove.ContiState(OpsContiMove.CONTI_WAIT));
                return true;
            }
            case 240000110: // Leafre Station >> Orbis
            case 200000131: // Orbis Station >> Leafre
            {
                chr.SendPacket(ResCField_ContiMove.ContiState(OpsContiMove.CONTI_WAIT));
                return true;
            }
            case 200090010: // During the ride to Orbis
            case 200090000: // During the ride to Ellinia
            {
                chr.SendPacket(ResCField_ContiMove.ContiMove(OpsContiMove.CONTI_TARGET_MOVEFIELD, OpsContiMove.CONTI_MOBGEN));
                return true;
            }
            default: {
                Debug.ErrorLog("OnContiState not coded, map id = " + map_id);
                break;
            }
        }

        return false;
    }
}
