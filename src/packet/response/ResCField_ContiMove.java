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

import handling.MaplePacket;
import packet.ServerPacket;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCField_ContiMove {

    public static MaplePacket boatPacket(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 1034: balrog boat comes, 1548: boat comes, 3: boat leaves
        mplew.writeShort(ServerPacket.Header.LP_CONTISTATE.get());
        mplew.writeShort(effect); // 0A 04 balrog
        //this packet had 3: boat leaves
        return mplew.getPacket();
    }

    public static MaplePacket boatEffect(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 1034: balrog boat comes, 1548: boat comes, 3: boat leaves
        mplew.writeShort(ServerPacket.Header.LP_CONTIMOVE.get());
        mplew.writeShort(effect); // 0A 04 balrog
        //this packet had the other ones o.o
        return mplew.getPacket();
    }
    
}
