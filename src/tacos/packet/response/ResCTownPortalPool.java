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
import java.awt.Point;
import tacos.packet.ServerPacket;
import odin.server.maps.MapleDoor;

/**
 *
 * @author Riremito
 */
public class ResCTownPortalPool {

    // spawnPortal
    // CWvsContext::OnTownPortal
    public static MaplePacket setMysticDoorInfo(MapleDoor door) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TownPortal);

        if (door == null) {
            sp.Encode4(999999999);
            sp.Encode4(999999999);
        } else {
            sp.Encode4(door.getMapId());
            sp.Encode4(door.getLink().getMapId());
            sp.Encode4(door.getSkillId());
            sp.Encode2((short) door.getLink().getPosition().x);
            sp.Encode2((short) door.getLink().getPosition().y);
        }

        return sp.get();
    }

    public static MaplePacket resetMysticDoorInfo() {
        return setMysticDoorInfo(null);
    }

    // spawnDoor
    public static final MaplePacket spawnDoor(MapleDoor door, boolean isTown) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TownPortalCreated);
        sp.Encode1(isTown ? 1 : 0);
        sp.Encode4(door.getOwnerId());
        sp.Encode2((short) door.getPosition().x);
        sp.Encode2((short) door.getPosition().y);
        return sp.get();
    }

    // removeDoor
    public static MaplePacket removeDoor(MapleDoor door) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TownPortalRemoved);
        sp.Encode1(1);
        sp.Encode4(door.getOwnerId());
        return sp.get();
    }

    // partyPortal
    public static MaplePacket partyPortal(MapleDoor door) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PartyResult);
        sp.Encode1(40);
        sp.Encode1(door.getTownPortal().getMysticDoorId()); // number
        sp.Encode4(door.getMapId());
        sp.Encode4(door.getLink().getMapId());
        sp.Encode4(door.getSkillId());
        sp.Encode2((short) door.getLink().getPosition().x);
        sp.Encode2((short) door.getLink().getPosition().y);
        return sp.get();
    }

}
