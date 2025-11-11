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

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import packet.ClientPacket;
import packet.response.ResCField_SnowBall;
import odin.server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqCField_SnowBall {

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        // 109060002
        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        switch (header) {
            case CP_EventStart: {
                return false;
            }
            case CP_SnowBallHit: {
                // PlayerHandler.snowBall(p, c);
                return false;
            }
            case CP_SnowBallTouch: {
                chr.SendPacket(ResCField_SnowBall.SnowBallTouch());
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
