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
import org.apache.mina.common.IoSession;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.request.ReqCClientSocket;
import tacos.packet.request.ReqCLogin;
import tacos.packet.request.ReqCUser;
import tacos.server.TacosLogin;

/**
 *
 * @author Riremito
 */
public class PacketHandler_Login extends PacketHandler implements IPacketHandler {

    public PacketHandler_Login(TacosLogin login_server) {
        super(login_server, -1);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        if (client != null) {
            client.getLoginServer().getClients().add(client);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        MapleClient client = (MapleClient) session.getAttribute(MapleClient.CLIENT_KEY);
        if (client != null) {
            client.getLoginServer().getClients().remove(client);
            client.getLoginServer().getAuthorizedClients().remove(client);
        }
        super.sessionClosed(session);
    }

    @Override
    public boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacketHeader.CP_BEGIN_SOCKET, ClientPacketHeader.CP_END_SOCKET)) {
            if (ReqCClientSocket.OnPacket_Login(client, header, cp)) {
                return true;
            }
            return ReqCLogin.OnPacket(client, header, cp);
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_USER, ClientPacketHeader.CP_END_USER)) {
            return ReqCUser.OnPacket_Login(client, header, cp);
        }
        return false;
    }
}
