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
package tacos.packet.response;

import tacos.network.MaplePacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.ServerPacket;
import odin.server.maps.MapleDragon;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCUser_Dragon {

    public static MaplePacket moveDragon(MapleDragon dragon, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DragonMove);
        sp.Encode4(dragon.getOwner());
        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    // not coded in GMS v95, but KMST v2.1029 removes dragon when you change other job.
    public static MaplePacket removeDragon(MapleDragon dragon) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DragonLeaveField);

        sp.Encode4(dragon.getOwner());
        return sp.get();
    }

    public static MaplePacket spawnDragon(MapleDragon dragon) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DragonEnterField);

        sp.Encode4(dragon.getOwner());
        sp.Encode4(dragon.getPosition().x);
        sp.Encode4(dragon.getPosition().y);
        sp.Encode1(dragon.getStance()); // move action (left, right)
        sp.Encode2(0); // not used
        sp.Encode2(dragon.getJobId());
        return sp.get();
    }

}
