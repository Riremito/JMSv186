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
import config.Region;
import config.Version;
import odin.handling.channel.handler.InventoryHandler;
import packet.ClientPacket;
import odin.server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqCDropPool {

    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }
        // InventoryHandler.Pickup_Player(p, c, c.getPlayer());
        switch (header) {
            case CP_DropPickUpRequest: // CWvsContext::SendDropPickUpRequest
            {
                byte unk1 = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
                int update_time = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
                short drop_x = cp.Decode2();
                short drop_y = cp.Decode2();
                int object_id = cp.Decode4();
                // CRC

                if (!InventoryHandler.PickUp(chr, object_id)) {
                    chr.UpdateStat(true);
                }
                chr.updateTick(update_time);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
