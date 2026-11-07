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

import tacos.client.TacosCharacter;
import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCOpenGatePool {

    // COpenGatePool::OnOpenGateCreated, not tested.
    public static ServerPacket OpenGateCreated(int nState, TacosCharacter chr, int x, int y, boolean bFirst, int nPartyID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_OpenGateCreated);

        sp.Encode1(nState); // nState
        sp.Encode4(chr.getId());
        sp.Encode2(x);
        sp.Encode2(y);
        sp.Encode1(bFirst ? 1 : 0); // bFirst
        sp.Encode4(nPartyID); // nPartyID
        return sp;
    }

    // COpenGatePool::OnOpenGateRemoved, not tested.
    public static ServerPacket OpenGateRemoved(int nLeaveType, TacosCharacter chr, boolean bFirst) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_OpenGateRemoved);

        sp.Encode1(nLeaveType);
        sp.Encode4(chr.getId());
        sp.Encode1(bFirst ? 1 : 0);
        return sp;
    }
}
