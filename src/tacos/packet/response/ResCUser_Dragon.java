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
import tacos.client.TacosDragon;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCUser_Dragon {

    // CDragon::OnCreated
    public static MaplePacket DragonEnterField(TacosDragon dragon) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DragonEnterField);

        sp.Encode4(dragon.getId()); // m_dwCharacterId
        sp.Encode4(dragon.getX()); // m_ptPos.x
        sp.Encode4(dragon.getY()); // m_ptPos.y
        sp.Encode1(dragon.getMoveAction()); // m_nMoveAction
        sp.Encode2(0); // unused
        sp.Encode2(dragon.getJobCode()); // m_nJobCode
        return sp.get();
    }

    // CDragon::OnMove
    public static MaplePacket DragonMove(TacosDragon dragon, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DragonMove);

        sp.Encode4(dragon.getId()); // m_dwCharacterId
        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    // not coded in GMS v95, but KMST v2.1029 removes dragon when you change other job.
    public static MaplePacket DragonLeaveField(TacosDragon dragon) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_DragonLeaveField);

        sp.Encode4(dragon.getId()); // m_dwCharacterId
        return sp.get();
    }

}
