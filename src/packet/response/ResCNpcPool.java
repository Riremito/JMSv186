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
import handling.MaplePacket;
import java.util.LinkedHashMap;
import java.util.Map;
import packet.ServerPacket;
import server.life.MapleNPC;
import server.life.PlayerNPC;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCNpcPool {

    public static MaplePacket removeNPC(final int objectid) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_NpcLeaveField);
        p.Encode4(objectid);
        return p.Get();
    }

    public static MaplePacket spawnNPC(MapleNPC life, boolean show) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_NpcEnterField);
        p.Encode4(life.getObjectId());
        p.Encode4(life.getId());
        p.Encode2(life.getPosition().x);
        p.Encode2(life.getCy());
        p.Encode1(life.getF() == 1 ? 0 : 1);
        p.Encode2(life.getFh());
        p.Encode2(life.getRx0());
        p.Encode2(life.getRx1());
        p.Encode1(show ? 1 : 0);
        if (194 <= ServerConfig.GetVersion()) {
            p.Encode1(0);
        }
        return p.Get();
    }

    public static MaplePacket spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_NpcChangeController.Get());
        mplew.write(1);
        mplew.writeInt(life.getObjectId());
        // フォーマット不明
        mplew.writeInt(life.getId());
        mplew.writeShort(life.getPosition().x);
        mplew.writeShort(life.getCy());
        mplew.write(life.getF() == 1 ? 0 : 1);
        mplew.writeShort(life.getFh());
        mplew.writeShort(life.getRx0());
        mplew.writeShort(life.getRx1());
        mplew.write(MiniMap ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket spawnPlayerNPC(PlayerNPC npc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ImitatedNPCData.Get());
        mplew.write(npc.getF() == 1 ? 0 : 1);
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        mplew.write(npc.getGender());
        mplew.write(npc.getSkin());
        mplew.writeInt(npc.getFace());
        mplew.write(0);
        mplew.writeInt(npc.getHair());
        Map<Byte, Integer> equip = npc.getEquips();
        Map<Byte, Integer> myEquip = new LinkedHashMap<Byte, Integer>();
        Map<Byte, Integer> maskedEquip = new LinkedHashMap<Byte, Integer>();
        for (Map.Entry<Byte, Integer> position : equip.entrySet()) {
            byte pos = (byte) (position.getKey() * -1);
            if (pos < 100 && myEquip.get(pos) == null) {
                myEquip.put(pos, position.getValue());
            } else if ((pos > 100 || pos == -128) && pos != 111) {
                // don't ask. o.o
                pos = (byte) (pos == -128 ? 28 : pos - 100);
                if (myEquip.get(pos) != null) {
                    maskedEquip.put(pos, myEquip.get(pos));
                }
                myEquip.put(pos, position.getValue());
            } else if (myEquip.get(pos) != null) {
                maskedEquip.put(pos, position.getValue());
            }
        }
        for (Map.Entry<Byte, Integer> entry : myEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(255);
        for (Map.Entry<Byte, Integer> entry : maskedEquip.entrySet()) {
            mplew.write(entry.getKey());
            mplew.writeInt(entry.getValue());
        }
        mplew.write(255);
        Integer cWeapon = equip.get((byte) -111);
        if (cWeapon != null) {
            mplew.writeInt(cWeapon);
        } else {
            mplew.writeInt(0);
        }
        for (int i = 0; i < 3; i++) {
            mplew.writeInt(npc.getPet(i));
        }
        return mplew.getPacket();
    }

    public static final void NPCAnimation(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_NpcMove.Get());
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
