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
package tacos.packet.request;

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.packet.ClientPacket;
import odin.server.maps.MapleDoor;
import odin.server.maps.MapleMapObject;

/**
 *
 * @author Riremito
 */
public class ReqCTownPortalPool {

    // UseDoor
    // CField::TryEnterTownPortal
    public static boolean TryEnterTownPortal(ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        int door_character_id = cp.Decode4();
        byte unk1 = cp.Decode1();

        for (MapleMapObject obj : chr.getMap().getAllDoorsThreadsafe()) {
            final MapleDoor door = (MapleDoor) obj;
            if (door.getOwnerId() == door_character_id) {
                chr.enterTownPortal(door);
                return true;
            }
        }

        return false;
    }
}