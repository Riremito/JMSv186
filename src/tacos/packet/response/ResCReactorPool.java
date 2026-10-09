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

import tacos.packet.ServerPacket;
import odin.server.maps.MapleReactor;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCReactorPool {

    // CReactorPool::OnReactorChangeState
    public static ServerPacket ReactorChangeState(MapleReactor reactor, int tActionDelay) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ReactorChangeState);

        sp.Encode4(reactor.getObjectId()); // dwID
        sp.Encode1(reactor.getState()); // nState
        sp.Encode2(reactor.getX()); // ptPos.x
        sp.Encode2(reactor.getY()); // ptPos.y
        sp.Encode2(tActionDelay); // tHitStart, tActionDelay
        sp.Encode1(0); // nProperEventIdx
        sp.Encode1(4); // tStateEnd
        return sp;
    }

    // CReactorPool::OnReactorMove
    public static ServerPacket ReactorMove(MapleReactor reactor) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ReactorMove);

        sp.Encode4(reactor.getObjectId()); // dwID
        sp.Encode2(reactor.getX()); // ptPos.x
        sp.Encode2(reactor.getY()); // ptPos.y
        return sp;
    }

    // CReactorPool::OnReactorEnterField
    public static ServerPacket ReactorEnterField(MapleReactor reactor) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ReactorEnterField);

        sp.Encode4(reactor.getObjectId()); // dwID
        sp.Encode4(reactor.getId()); // dwTemplateID
        sp.Encode1(reactor.getState()); // nState, nOldState
        sp.Encode2(reactor.getX()); // ptPos.x
        sp.Encode2(reactor.getY()); // ptPos.y
        sp.Encode1(reactor.getFacingDirection()); // bFlip
        sp.EncodeStr(reactor.getName()); // sName
        return sp;
    }

    // CReactorPool::OnReactorLeaveField
    public static ServerPacket ReactorLeaveField(MapleReactor reactor) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ReactorLeaveField);

        sp.Encode4(reactor.getObjectId()); // dwID
        sp.Encode1(reactor.getState()); // nState
        sp.Encode2(reactor.getX()); // ptPos.x
        sp.Encode2(reactor.getY()); // ptPos.y
        return sp;
    }
}
