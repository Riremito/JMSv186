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
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCMessageBoxPool {

    // CMessageBoxPool::OnCreateFailed, not tested.
    public static ServerPacket CreateMessgaeBoxFailed() {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_CreateMessgaeBoxFailed);

        return sp;
    }

    // CMessageBoxPool::OnMessageBoxEnterField, not tested.
    public static ServerPacket MessageBoxEnterField(int dwMessageBoxID, int nItemID, String sHostName, String sMessage, int x, int y) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MessageBoxEnterField);

        sp.Encode4(dwMessageBoxID); // dwMessageBoxID
        sp.Encode4(nItemID); // nItemID
        sp.EncodeStr(sHostName);
        sp.EncodeStr(sMessage);
        sp.Encode2(x); // ptHost.x
        sp.Encode2(y); // ptHost.y
        return sp;
    }

    // CMessageBoxPool::OnMessageBoxLeaveField, not tested.
    public static ServerPacket MessageBoxLeaveField(int nLeaveType, int dwID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MessageBoxLeaveField);

        sp.Encode1(nLeaveType);
        sp.Encode4(dwID); // dwID
        return sp;
    }
}
