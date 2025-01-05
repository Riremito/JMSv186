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

import client.MapleCharacter;
import handling.MaplePacket;
import packet.ServerPacket;
import packet.response.struct.TestHelper;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCUser {

    public static final MaplePacket sendPlayerShopBox(final MapleCharacter c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserMiniRoomBalloon.Get());
        mplew.writeInt(c.getId());
        TestHelper.addAnnounceBox(mplew, c);
        return mplew.getPacket();
    }

    public static final MaplePacket addCharBox(final MapleCharacter c, final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserMiniRoomBalloon.Get());
        mplew.writeInt(c.getId());
        TestHelper.addAnnounceBox(mplew, c);
        return mplew.getPacket();
    }

    public static final MaplePacket removeCharBox(final MapleCharacter c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserMiniRoomBalloon.Get());
        mplew.writeInt(c.getId());
        mplew.write(0);
        return mplew.getPacket();
    }
    
}
