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
package tacos.packet.response;

import tacos.network.MaplePacket;
import tacos.packet.ServerPacket;
import odin.server.maps.MapleReactor;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCReactorPool {

    // triggerReactor
    public static MaplePacket Hit(MapleReactor reactor, int stance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ReactorChangeState);
        sp.Encode4(reactor.getObjectId());
        sp.Encode1(reactor.getState());
        sp.Encode2(reactor.getPosition().x);
        sp.Encode2(reactor.getPosition().y);
        sp.Encode2(stance);
        sp.Encode1(0);
        sp.Encode1(4);
        return sp.get();
    }

    // spawnReactor
    public static MaplePacket Spawn(MapleReactor reactor) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ReactorEnterField);
        sp.Encode4(reactor.getObjectId());
        sp.Encode4(reactor.getReactorId());
        sp.Encode1(reactor.getState());
        sp.Encode2(reactor.getPosition().x);
        sp.Encode2(reactor.getPosition().y);
        sp.Encode1(reactor.getFacingDirection()); // stance
        sp.EncodeStr(reactor.getName());
        return sp.get();
    }

    // destroyReactor
    public static MaplePacket Destroy(MapleReactor reactor) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ReactorLeaveField);
        sp.Encode4(reactor.getObjectId());
        sp.Encode1(reactor.getState());
        sp.Encode2(reactor.getPosition().x);
        sp.Encode2(reactor.getPosition().y);
        return sp.get();
    }

}
