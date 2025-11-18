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
import tacos.server.ServerOdinGame;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.request.ReqCClientSocket;
import tacos.packet.request.ReqCDropPool;
import tacos.packet.request.ReqCField;
import tacos.packet.request.ReqCField_Coconut;
import tacos.packet.request.ReqCField_MonsterCarnival;
import tacos.packet.request.ReqCField_SnowBall;
import tacos.packet.request.ReqCMobPool;
import tacos.packet.request.ReqCNpcPool;
import tacos.packet.request.ReqCReactorPool;
import tacos.packet.request.ReqCSummonedPool;
import tacos.packet.request.ReqCUIItemUpgrade;
import tacos.packet.request.ReqCUser;
import tacos.packet.request.ReqCUser_Dragon;
import tacos.packet.request.ReqCUser_Pet;
import tacos.packet.request.Req_MapleTV;

/**
 *
 * @author Riremito
 */
public class PacketHandler_Game extends PacketHandler implements IPacketHandler {

    public PacketHandler_Game(int channel) {
        super(channel);
        this.server_name = "Channel" + String.format("%02d", this.channel);
    }

    @Override
    public boolean isShutdown() {
        return ServerOdinGame.getInstance(this.channel).isShutdown();
    }

    @Override
    public boolean OnPacket(MapleClient c, ClientPacketHeader header, ClientPacket cp) throws Exception {
        // socket
        if (header.between(ClientPacketHeader.CP_BEGIN_SOCKET, ClientPacketHeader.CP_END_SOCKET)) {
            return ReqCClientSocket.OnPacket(c, header, cp);
        }
        // user
        if (header.between(ClientPacketHeader.CP_BEGIN_USER, ClientPacketHeader.CP_END_USER)) {
            // family
            if (header.between(ClientPacketHeader.CP_FamilyChartRequest, ClientPacketHeader.CP_FamilySummonResult)) {
                return ReqCUser.OnFamilyPacket(c, header, cp);
            }
            // pet
            if (header.between(ClientPacketHeader.CP_BEGIN_PET, ClientPacketHeader.CP_END_PET)) {
                return ReqCUser_Pet.OnPetPacket(c, header, cp);
            }
            // summon
            if (header.between(ClientPacketHeader.CP_BEGIN_SUMMONED, ClientPacketHeader.CP_END_SUMMONED)) {
                return ReqCSummonedPool.OnPacket(c, header, cp);
            }
            // dragon
            if (header.between(ClientPacketHeader.CP_BEGIN_DRAGON, ClientPacketHeader.CP_END_DRAGON)) {
                return ReqCUser_Dragon.OnMove(c, header, cp);
            }
            return ReqCUser.OnPacket(c, header, cp);
        }
        // field
        if (header.between(ClientPacketHeader.CP_BEGIN_FIELD, ClientPacketHeader.CP_END_FIELD)) {
            // life
            if (header.between(ClientPacketHeader.CP_BEGIN_LIFEPOOL, ClientPacketHeader.CP_END_LIFEPOOL)) {
                // mob
                if (header.between(ClientPacketHeader.CP_BEGIN_MOB, ClientPacketHeader.CP_END_MOB)) {
                    return ReqCMobPool.OnPacket(c, header, cp);
                }
                // npc
                if (header.between(ClientPacketHeader.CP_BEGIN_NPC, ClientPacketHeader.CP_END_NPC)) {
                    ReqCNpcPool.OnPacket(c, header, cp);
                    return true;
                }
                return false;
            }
            // drop
            if (header.between(ClientPacketHeader.CP_BEGIN_DROPPOOL, ClientPacketHeader.CP_END_DROPPOOL)) {
                return ReqCDropPool.OnPacket(c, header, cp);
            }
            // reactor
            if (header.between(ClientPacketHeader.CP_BEGIN_REACTORPOOL, ClientPacketHeader.CP_END_REACTORPOOL)) {
                return ReqCReactorPool.OnPacket(c, header, cp);
            }
            // event field
            if (header.between(ClientPacketHeader.CP_BEGIN_EVENT_FIELD, ClientPacketHeader.CP_END_EVENT_FIELD)) {
                if (ReqCField_SnowBall.OnPacket(c, header, cp)) {
                    return true;
                }
                if (ReqCField_Coconut.OnPacket(c, header, cp)) {
                    return true;
                }
                return false;
            }
            // monster carnival field
            if (header.between(ClientPacketHeader.CP_BEGIN_MONSTER_CARNIVAL_FIELD, ClientPacketHeader.CP_END_MONSTER_CARNIVAL_FIELD)) {
                return ReqCField_MonsterCarnival.OnPacket(c, header, cp);
            }
            if (header.between(ClientPacketHeader.CP_BEGIN_PARTY_MATCH, ClientPacketHeader.CP_END_PARTY_MATCH)) {
                return true;
            }
            return ReqCField.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_RAISE, ClientPacketHeader.CP_END_RAISE)) {
            // 布製の人形などETCアイテムからUIを開くタイプの処理
            return true;
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_ITEMUPGRADE, ClientPacketHeader.CP_END_ITEMUPGRADE)) {
            ReqCUIItemUpgrade.Accept(c, cp);
            return true;
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_BATTLERECORD, ClientPacketHeader.CP_END_BATTLERECORD)) {
            return true;
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_MAPLETV, ClientPacketHeader.CP_END_MAPLETV)) {
            return Req_MapleTV.OnPacket(c, header, cp);
        }
        if (header.between(ClientPacketHeader.CP_BEGIN_CHARACTERSALE, ClientPacketHeader.CP_END_CHARACTERSALE)) {
            return true;
        }

        return false;
    }
}
