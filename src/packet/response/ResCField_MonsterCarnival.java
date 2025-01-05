/*
 * Copyright (C) 2024 Riremito
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

import client.MapleCharacter;
import handling.MaplePacket;
import packet.ServerPacket;
import server.MapleCarnivalParty;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCField_MonsterCarnival {

    public static MaplePacket CPUpdate(boolean party, int curCP, int totalCP, int team) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        if (!party) {
            mplew.writeShort(ServerPacket.Header.LP_MCarnivalPersonalCP.Get());
        } else {
            mplew.writeShort(ServerPacket.Header.LP_MCarnivalTeamCP.Get());
            mplew.write(team);
        }
        mplew.writeShort(curCP);
        mplew.writeShort(totalCP);
        return mplew.getPacket();
    }

    public static MaplePacket playerSummoned(String name, int tab, int number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MCarnivalResultSuccess.Get());
        mplew.write(tab);
        mplew.write(number);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static MaplePacket startMonsterCarnival(final MapleCharacter chr, final int enemyavailable, final int enemytotal) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MCarnivalEnter.Get());
        final MapleCarnivalParty friendly = chr.getCarnivalParty();
        mplew.write(friendly.getTeam());
        mplew.writeShort(chr.getAvailableCP());
        mplew.writeShort(chr.getTotalCP());
        mplew.writeShort(friendly.getAvailableCP());
        mplew.writeShort(friendly.getTotalCP());
        mplew.writeShort(enemyavailable);
        mplew.writeShort(enemytotal);
        mplew.writeLong(0);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket playerDiedMessage(String name, int lostCP, int team) {
        //CPQ
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MCarnivalDeath.Get());
        mplew.write(team); //team
        mplew.writeMapleAsciiString(name);
        mplew.write(lostCP);
        return mplew.getPacket();
    }
    
}
