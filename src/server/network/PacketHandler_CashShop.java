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
import server.server.Server_CashShop;
import packet.ClientPacket;
import packet.request.ReqCCashShop;
import packet.request.ReqCClientSocket;
import packet.request.ReqCITC;
import packet.request.ReqCUser;

/**
 *
 * @author Riremito
 */
public class PacketHandler_CashShop extends PacketHandler implements IPacketHandler {

    public PacketHandler_CashShop() {
        super(-1);
        this.server_name = "CashShop";
    }

    @Override
    public boolean isShutdown() {
        return Server_CashShop.isShutdown();
    }

    @Override
    public boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        if (OnCashShopPacket(c, header, cp)) {
            return true;
        }
        if (OnITCPacket(c, header, cp)) {
            return true;
        }
        return false;
    }

    public boolean OnCashShopPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacket.Header.CP_BEGIN_SOCKET, ClientPacket.Header.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_USER, ClientPacket.Header.CP_END_USER)) {
            return ReqCUser.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_CASHSHOP, ClientPacket.Header.CP_END_CASHSHOP)) {
            return ReqCCashShop.OnPacket(c, header, cp);
        }
        return false;
    }

    public boolean OnITCPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacket.Header.CP_BEGIN_SOCKET, ClientPacket.Header.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_USER, ClientPacket.Header.CP_END_USER)) {
            return ReqCUser.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_ITC, ClientPacket.Header.CP_END_ITC)) {
            return ReqCITC.OnPacket(c, header, cp);
        }
        return false;
    }

}
