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
import tacos.server.ServerOdinCashShop;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.request.ReqCCashShop;
import tacos.packet.request.ReqCClientSocket;
import tacos.packet.request.ReqCITC;
import tacos.packet.request.ReqCUser;

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
        return ServerOdinCashShop.isShutdown();
    }

    @Override
    public boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) throws Exception {
        if (OnCashShopPacket(c, header, cp)) {
            return true;
        }
        if (OnITCPacket(c, header, cp)) {
            return true;
        }
        return false;
    }

    public boolean OnCashShopPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacketHeader.CP_BEGIN_SOCKET, ClientPacketHeader.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_USER, ClientPacketHeader.CP_END_USER)) {
            return ReqCUser.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_CASHSHOP, ClientPacketHeader.CP_END_CASHSHOP)) {
            return ReqCCashShop.OnPacket(c, header, cp);
        }
        return false;
    }

    public boolean OnITCPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) throws Exception {
        if (header.between(ClientPacketHeader.CP_BEGIN_SOCKET, ClientPacketHeader.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_USER, ClientPacketHeader.CP_END_USER)) {
            return ReqCUser.OnPacket_CS_ITC(c, header, cp);
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_ITC, ClientPacketHeader.CP_END_ITC)) {
            return ReqCITC.OnPacket(c, header, cp);
        }
        return false;
    }

}
