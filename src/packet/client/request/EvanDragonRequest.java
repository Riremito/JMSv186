/*
 * Copyright (C) 2024 Riremito
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
package packet.client.request;

import client.MapleCharacter;
import client.MapleClient;
import packet.client.ClientPacket;
import packet.client.request.struct.CMovePath;
import packet.server.response.EvanDragonResponse;
import server.maps.MapleDragon;

/**
 *
 * @author Riremito
 */
public class EvanDragonRequest {

    // CDragon::OnMove
    public static boolean OnMove(ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null || chr.isHidden()) {
            return false;
        }

        MapleDragon dragon = chr.getDragon();
        if (dragon == null) {
            return false;
        }

        // CMovePath::Decode
        CMovePath data = CMovePath.Decode(cp);
        dragon.setStance(data.getAction());
        dragon.setPosition(data.getEnd());
        chr.getMap().broadcastMessage(chr, EvanDragonResponse.moveDragon(dragon, data), chr.getPosition());
        return true;
    }

}
