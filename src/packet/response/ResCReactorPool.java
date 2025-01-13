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
package packet.response;

import handling.MaplePacket;
import packet.ServerPacket;
import server.maps.MapleReactor;

/**
 *
 * @author Riremito
 */
public class ResCReactorPool {

    // triggerReactor
    public static MaplePacket Hit(MapleReactor reactor, int stance) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ReactorChangeState);
        p.Encode4(reactor.getObjectId());
        p.Encode1(reactor.getState());
        p.Encode2(reactor.getPosition().x);
        p.Encode2(reactor.getPosition().y);
        p.Encode2(stance);
        p.Encode1(0);
        p.Encode1(4);
        return p.Get();
    }

    // destroyReactor
    public static MaplePacket Destroy(MapleReactor reactor) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ReactorLeaveField);
        p.Encode4(reactor.getObjectId());
        p.Encode1(reactor.getState());
        p.Encode2(reactor.getPosition().x);
        p.Encode2(reactor.getPosition().y);
        return p.Get();
    }

    // spawnReactor
    public static MaplePacket Spawn(MapleReactor reactor) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ReactorEnterField);
        p.Encode4(reactor.getObjectId());
        p.Encode4(reactor.getReactorId());
        p.Encode1(reactor.getState());
        p.Encode2(reactor.getPosition().x);
        p.Encode2(reactor.getPosition().y);
        p.Encode1(reactor.getFacingDirection()); // stance
        p.EncodeStr(reactor.getName());
        return p.Get();
    }
    
}
