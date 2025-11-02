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
import handling.channel.ChannelServer;
import org.apache.mina.common.IoSession;
import packet.ClientPacket;
import packet.request.ReqCClientSocket;
import packet.request.ReqCDropPool;
import packet.request.ReqCField;
import packet.request.ReqCField_Coconut;
import packet.request.ReqCField_MonsterCarnival;
import packet.request.ReqCField_SnowBall;
import packet.request.ReqCMobPool;
import packet.request.ReqCNpcPool;
import packet.request.ReqCReactorPool;
import packet.request.ReqCSummonedPool;
import packet.request.ReqCUIItemUpgrade;
import packet.request.ReqCUser;
import packet.request.ReqCUser_Dragon;
import packet.request.ReqCUser_Pet;
import packet.request.Req_MapleTV;

/**
 *
 * @author Riremito
 */
public class PH_Game extends PacketHandler implements IPacketHandler {

    private final String server_name;

    public PH_Game(int channel) {
        super(channel);
        this.server_name = "Channel" + String.format("%02d", this.channel);
    }

    @Override
    public void sessionOpened(final IoSession session) throws Exception {
        String client_ip = session.getRemoteAddress().toString();
        if (ChannelServer.getInstance(this.channel).isShutdown()) {
            session.close();
            DebugLogger.ErrorLog("[Server : " + server_name + "] sessionOpened. (" + client_ip + ")");
            return;
        }
        DebugLogger.InfoLog("[Server : " + server_name + "] sessionOpened. (" + client_ip + ")");
        super.sessionOpened(session);
    }

    @Override
    public boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) throws Exception {
        // socket
        if (header.between(ClientPacket.Header.CP_BEGIN_SOCKET, ClientPacket.Header.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket(c, header, cp);
        }
        // user
        if (header.between(ClientPacket.Header.CP_BEGIN_USER, ClientPacket.Header.CP_END_USER)) {
            // family
            if (header.between(ClientPacket.Header.CP_FamilyChartRequest, ClientPacket.Header.CP_FamilySummonResult)) {
                return ReqCUser.OnFamilyPacket(c, header, cp);
            }
            // pet
            if (header.between(ClientPacket.Header.CP_BEGIN_PET, ClientPacket.Header.CP_END_PET)) {
                return ReqCUser_Pet.OnPetPacket(c, header, cp);
            }
            // summon
            if (header.between(ClientPacket.Header.CP_BEGIN_SUMMONED, ClientPacket.Header.CP_END_SUMMONED)) {
                return ReqCSummonedPool.OnPacket(c, header, cp);
            }
            // dragon
            if (header.between(ClientPacket.Header.CP_BEGIN_DRAGON, ClientPacket.Header.CP_END_DRAGON)) {
                return ReqCUser_Dragon.OnMove(c, header, cp);
            }
            return ReqCUser.OnPacket(c, header, cp);
        }
        // field
        if (header.between(ClientPacket.Header.CP_BEGIN_FIELD, ClientPacket.Header.CP_END_FIELD)) {
            // life
            if (header.between(ClientPacket.Header.CP_BEGIN_LIFEPOOL, ClientPacket.Header.CP_END_LIFEPOOL)) {
                // mob
                if (header.between(ClientPacket.Header.CP_BEGIN_MOB, ClientPacket.Header.CP_END_MOB)) {
                    return ReqCMobPool.OnPacket(c, header, cp);
                }
                // npc
                if (header.between(ClientPacket.Header.CP_BEGIN_NPC, ClientPacket.Header.CP_END_NPC)) {
                    ReqCNpcPool.OnPacket(c, header, cp);
                    return true;
                }
                return false;
            }
            // drop
            if (header.between(ClientPacket.Header.CP_BEGIN_DROPPOOL, ClientPacket.Header.CP_END_DROPPOOL)) {
                return ReqCDropPool.OnPacket(c, header, cp);
            }
            // reactor
            if (header.between(ClientPacket.Header.CP_BEGIN_REACTORPOOL, ClientPacket.Header.CP_END_REACTORPOOL)) {
                return ReqCReactorPool.OnPacket(c, header, cp);
            }
            // event field
            if (header.between(ClientPacket.Header.CP_BEGIN_EVENT_FIELD, ClientPacket.Header.CP_END_EVENT_FIELD)) {
                if (ReqCField_SnowBall.OnPacket(c, header, cp)) {
                    return true;
                }
                if (ReqCField_Coconut.OnPacket(c, header, cp)) {
                    return true;
                }
                return false;
            }
            // monster carnival field
            if (header.between(ClientPacket.Header.CP_BEGIN_MONSTER_CARNIVAL_FIELD, ClientPacket.Header.CP_END_MONSTER_CARNIVAL_FIELD)) {
                return ReqCField_MonsterCarnival.OnPacket(c, header, cp);
            }
            if (header.between(ClientPacket.Header.CP_BEGIN_PARTY_MATCH, ClientPacket.Header.CP_END_PARTY_MATCH)) {
                return true;
            }
            return ReqCField.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_RAISE, ClientPacket.Header.CP_END_RAISE)) {
            // 布製の人形などETCアイテムからUIを開くタイプの処理
            return true;
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_ITEMUPGRADE, ClientPacket.Header.CP_END_ITEMUPGRADE)) {
            ReqCUIItemUpgrade.Accept(c, cp);
            return true;
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_BATTLERECORD, ClientPacket.Header.CP_END_BATTLERECORD)) {
            return true;
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_MAPLETV, ClientPacket.Header.CP_END_MAPLETV)) {
            return Req_MapleTV.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacket.Header.CP_BEGIN_CHARACTERSALE, ClientPacket.Header.CP_END_CHARACTERSALE)) {
            return true;
        }

        return false;
    }
}
