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
package packet.response;

import client.MapleClient;
import config.ServerConfig;
import server.network.MaplePacket;
import packet.ServerPacket;
import packet.response.data.DataAvatarLook;
import server.life.MapleNPC;
import server.life.PlayerNPC;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCNpcPool {

    public static MaplePacket ImitatedNPCData(PlayerNPC npc) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ImitatedNPCData);

        int number_of_npcs = (npc.getCharacter() == null) ? 0 : 1;
        sp.Encode1(number_of_npcs); // number of npcs
        if (number_of_npcs != 0) {
            sp.Encode4(npc.getId()); // dwTemplateID
            sp.EncodeStr(npc.getName()); // sName
            sp.EncodeBuffer(DataAvatarLook.Encode(npc.getCharacter())); // AvatarLook::Decode
        }

        return sp.get();
    }

    public static MaplePacket NpcEnterField(MapleNPC npc, boolean show) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_NpcEnterField);

        sp.Encode4(npc.getObjectId()); // dwNpcId
        sp.Encode4(npc.getId()); // NpcTemplate
        sp.EncodeBuffer(CNpc_Init(npc, show)); // CNpc::Init
        return sp.get();
    }

    // CNpc::Init
    public static byte[] CNpc_Init(MapleNPC npc, boolean show) {
        ServerPacket data = new ServerPacket();

        data.Encode2(npc.getPosition().x); // m_ptPos.x
        data.Encode2(npc.getPosition().y); // m_ptPos.y
        data.Encode1(npc.getF() == 1 ? 0 : 1); // m_nMoveAction
        data.Encode2(npc.getFh()); // Foothold
        data.Encode2(npc.getRx0()); // m_rgHorz.low
        data.Encode2(npc.getRx1()); // m_rgHorz.high
        data.Encode1(show ? 1 : 0); // m_bEnabled

        if (ServerConfig.JMS194orLater()) {
            data.Encode1(0);
        }

        return data.get().getBytes();
    }

    public static MaplePacket NpcLeaveField(MapleNPC npc) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_NpcLeaveField);

        sp.Encode4(npc.getObjectId());
        return sp.get();
    }

    public static MaplePacket NpcChangeController(MapleNPC npc, boolean is_local, boolean show) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_NpcChangeController);

        sp.Encode1(is_local ? 1 : 0);
        sp.Encode4(npc.getObjectId());
        if (is_local) {
            sp.Encode4(npc.getId());
            sp.EncodeBuffer(CNpc_Init(npc, show)); // CNpc::Init
        }

        return sp.get();
    }

    public static final void NpcMove(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_NpcMove.get());
        final int length = (int) slea.available();
        if (length == 6) {
            // NPC Talk
            mplew.writeInt(slea.readInt());
            mplew.writeShort(slea.readShort());
        } else if (length > 6) {
            // NPC Move
            mplew.write(slea.read(length - 9));
        } else {
            return;
        }
        c.getSession().write(mplew.getPacket());
    }

}
