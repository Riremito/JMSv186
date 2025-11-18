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
import tacos.debug.DebugLogger;
import odin.handling.channel.handler.BeanGame;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsContiMove;
import tacos.packet.response.ResCField_ContiMove;
import odin.scripting.EventManager;
import odin.scripting.EventScriptManager;
import odin.server.maps.MapleMap;
import tacos.packet.ClientPacketHeader;

/**
 *
 * @author Riremito
 */
public class ReqCField {

    public static boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) {
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
            case BEANS_OPERATION: {
                BeanGame.BeanGame1(c, cp);
                return true;
            }
            case BEANS_UPDATE: {
                chr.UpdateStat(true);
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
                DebugLogger.ErrorLog("OnContiState not coded, map id = " + map_id);
                break;
            }
        }

        return false;
    }
}
