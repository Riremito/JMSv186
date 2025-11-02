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
package server.network;

import client.MapleClient;
import debug.DebugLogger;
import handling.login.LoginServer;
import org.apache.mina.common.IoSession;
import packet.ClientPacket;
import packet.request.ReqCClientSocket;
import packet.request.ReqCLogin;
import packet.request.ReqCUser;

/**
 *
 * @author Riremito
 */
public class PH_Login extends PacketHandler implements IPacketHandler {

    public PH_Login() {
        super(-1);
    }

    @Override
    public void sessionOpened(final IoSession session) throws Exception {
        String client_ip = session.getRemoteAddress().toString();
        if (LoginServer.isShutdown()) {
            session.close();
            DebugLogger.ErrorLog("[Server : Login] sessionOpened. (" + client_ip + ")");
            return;
        }
        DebugLogger.InfoLog("[Server : Login] sessionOpened. (" + client_ip + ")");
        super.sessionOpened(session);
    }

    @Override
    public boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacket.Header.CP_BEGIN_SOCKET, ClientPacket.Header.CP_END_SOCKET)) {
            if (ReqCClientSocket.OnPacket_Login(c, header, cp)) {
                return true;
            }
            return ReqCLogin.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_USER, ClientPacket.Header.CP_END_USER)) {
            return ReqCUser.OnPacket_Login(c, header, cp);
        }
        return false;
    }
}
