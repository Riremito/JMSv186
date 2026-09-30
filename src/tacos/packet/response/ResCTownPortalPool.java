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

import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;
import tacos.server.map.object.TacosMysticDoor;

/**
 *
 * @author Riremito
 */
public class ResCTownPortalPool {

    // CTownPortalPool::OnTownPortalCreated
    public static ServerPacket TownPortalCreated(TacosMysticDoor door) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TownPortalCreated);

        sp.Encode1(door.getState()); // nState, town or not.
        sp.Encode4(door.getOwnerId()); // dwCharacterID
        sp.Encode2(door.getX()); // x
        sp.Encode2(door.getY()); // y
        return sp;
    }

    // CTownPortalPool::OnTownPortalRemoved
    public static ServerPacket TownPortalRemoved(TacosMysticDoor door) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TownPortalRemoved);

        sp.Encode1(1);
        sp.Encode4(door.getOwnerId()); // dwCharacterID
        return sp;
    }

    // CWvsContext::OnTownPortal
    public static ServerPacket TownPortal(TacosMysticDoor door) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_TownPortal);

        int m_dwTownID = (door != null) ? door.getTownMapId() : 999999999;
        int m_dwFieldID = (door != null) ? door.getFieldMapId() : 999999999;

        sp.Encode4(m_dwTownID);
        sp.Encode4(m_dwFieldID);

        if (door != null && m_dwTownID != 999999999 && m_dwFieldID != 999999999) {
            sp.Encode4(door.getSkillId(), Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186)); // m_nSKillID
            sp.Encode2(door.getFieldX()); // m_ptFieldPortal.x
            sp.Encode2(door.getFieldY()); // m_ptFieldPortal.y
        }

        return sp;
    }
}
