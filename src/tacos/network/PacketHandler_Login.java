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
package tacos.network;

import odin.client.MapleClient;
import tacos.server.ServerOdinLogin;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.request.ReqCClientSocket;
import tacos.packet.request.ReqCLogin;
import tacos.packet.request.ReqCUser;

/**
 *
 * @author Riremito
 */
public class PacketHandler_Login extends PacketHandler implements IPacketHandler {

    public PacketHandler_Login() {
        super(-1);
        this.server_name = "Login";
    }

    @Override
    public boolean isShutdown() {
        return ServerOdinLogin.isShutdown();
    }

    @Override
    public boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacketHeader.CP_BEGIN_SOCKET, ClientPacketHeader.CP_END_SOCKET)) {
            if (ReqCClientSocket.OnPacket_Login(c, header, cp)) {
                return true;
            }
            return ReqCLogin.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_USER, ClientPacketHeader.CP_END_USER)) {
            return ReqCUser.OnPacket_Login(c, header, cp);
        }
        return false;
    }
}
