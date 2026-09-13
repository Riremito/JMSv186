/*
 * Copyright (C) 2026 Riremito
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
import tacos.client.TacosClient;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCUser_FoxMan;

/**
 *
 * @author Riremito
 */
public class ReqCUser_FoxMan {

    // CUser::OnFoxManPacket
    public static boolean OnPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }

        switch (header) {
            case CP_FoxManMove: {
                OnFoxManMove(chr, cp);
                return true;
            }
            case CP_FoxManActionSetUseRequest: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public static boolean OnFoxManMove(MapleCharacter chr, ClientPacket cp) {

        // TODO fox check.
        // CMovePath::Decode
        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
        }

        chr.getMap().broadcastMessageTo(chr, ResCUser_FoxMan.FoxManMove(chr, move_path), chr.getPosition());
        return true;
    }

}
