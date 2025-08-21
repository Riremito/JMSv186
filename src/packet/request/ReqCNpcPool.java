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
import packet.ClientPacket;
import packet.request.parse.ParseCMovePath;
import packet.response.ResCNpcPool;
import server.life.MapleNPC;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqCNpcPool {

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }
        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        int npc_oid = cp.Decode4();

        MapleNPC npc = map.getNPCByOid(npc_oid);
        if (npc == null) {
            return false;
        }

        switch (header) {
            case CP_NpcMove: {
                byte nChatIdx = cp.Decode1();
                byte m_nOneTimeAction = cp.Decode1();

                ParseCMovePath move_path = move_path = new ParseCMovePath();
                if (move_path.Decode(cp)) {
                    move_path.update(npc);
                } else {
                    move_path = null;
                }

                map.broadcastMessage(ResCNpcPool.NpcMove(npc, nChatIdx, m_nOneTimeAction, move_path));
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

}
