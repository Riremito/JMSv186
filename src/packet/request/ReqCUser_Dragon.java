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
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import packet.ClientPacket;
import packet.request.parse.ParseCMovePath;
import packet.response.ResCUser_Dragon;
import server.maps.MapleDragon;

/**
 *
 * @author Riremito
 */
public class ReqCUser_Dragon {

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
        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            move_path.update(dragon);
        }

        chr.getMap().broadcastMessage(chr, ResCUser_Dragon.moveDragon(dragon, move_path), chr.getPosition());
        return true;
    }

}
