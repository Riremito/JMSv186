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

import tacos.packet.ClientPacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCUser_Dragon;
import tacos.client.TacosCharacter;
import tacos.client.TacosClient;
import tacos.client.TacosDragon;
import tacos.packet.ClientPacketHeader;
import tacos.server.map.TacosMap;

/**
 *
 * @author Riremito
 */
public class ReqCUser_Dragon {

    // CUser::OnDragonPacket
    public static boolean OnDragonPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        TacosCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }

        TacosMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        TacosDragon dragon = chr.getDragon();
        if (dragon == null) {
            return false;
        }

        switch (header) {
            case CP_DragonMove: {
                OnMove(map, chr, dragon, cp);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnMove(TacosMap map, TacosCharacter chr, TacosDragon dragon, ClientPacket cp) {
        // CMovePath::Decode
        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            dragon.update(move_path);
            map.broadcastMessage(chr, ResCUser_Dragon.DragonMove(dragon, move_path), false);
        }
        return true;
    }

}
