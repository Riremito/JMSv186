/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.content;

import handling.MaplePacket;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class SocketPacket {

    // CClientSocket::OnMigrateCommand
    // getChannelChange
    public static final MaplePacket MigrateCommand(final int port) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MigrateCommand);

        p.Encode1(1);
        p.Encode4(0x0100007F); // IP, 127.0.0.1
        p.Encode2(port);
        return p.Get();
    }

    // CClientSocket::OnAliveReq
    // getPing
    public static final MaplePacket AliveReq() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_AliveReq);

        return p.Get();
    }

    // Internet Cafe
    // プレミアムクーポン itemid 5420007
    // CClientSocket::OnAuthenCodeChanged
    public static final MaplePacket AuthenCodeChanged() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_AuthenCodeChanged);

        p.Encode1(2); // Open UI
        p.Encode4(1);
        return p.Get();
    }

    // CClientSocket::OnAuthenMessage
    public static final MaplePacket AuthenMessage() {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_AuthenMessage);

        p.Encode4(1); // id
        p.Encode1(1);
        return p.Get();
    }
}
