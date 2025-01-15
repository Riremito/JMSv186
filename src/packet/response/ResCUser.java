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
import client.inventory.IEquip;
import config.ServerConfig;
import handling.MaplePacket;
import java.awt.Point;
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
        mplew.writeShort(ServerPacket.Header.LP_UserMiniRoomBalloon.get());
        mplew.writeInt(c.getId());
        TestHelper.addAnnounceBox(mplew, c);
        return mplew.getPacket();
    }

    public static final MaplePacket addCharBox(final MapleCharacter c, final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserMiniRoomBalloon.get());
        mplew.writeInt(c.getId());
        TestHelper.addAnnounceBox(mplew, c);
        return mplew.getPacket();
    }

    public static final MaplePacket removeCharBox(final MapleCharacter c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserMiniRoomBalloon.get());
        mplew.writeInt(c.getId());
        mplew.write(0);
        return mplew.getPacket();
    }

    //miracle cube?
    public static MaplePacket getPotentialEffect(final int chr, final int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserItemUnreleaseEffect.get());
        mplew.writeInt(chr);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    //magnify glass
    public static MaplePacket getPotentialReset(final int chr, final short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserItemReleaseEffect.get());
        mplew.writeInt(chr);
        mplew.writeShort(pos);
        return mplew.getPacket();
    }

    public static MaplePacket getScrollEffect(int chr, IEquip.ScrollResult scrollSuccess, boolean legendarySpirit) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserItemUpgradeEffect.get());
        mplew.writeInt(chr);
        switch (scrollSuccess) {
            case SUCCESS:
                mplew.writeShort(1);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            case FAIL:
                mplew.writeShort(0);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
            case CURSE:
                mplew.write(0);
                mplew.write(1);
                mplew.writeShort(legendarySpirit ? 1 : 0);
                break;
        }
        mplew.write(0); //? pam's song?
        // テスト
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_UserChat);
        p.Encode4(cidfrom);
        p.Encode1(whiteBG ? 1 : 0);
        p.EncodeStr(text);
        if (ServerConfig.JMS164orLater()) {
            p.Encode1((byte) show);
        }
        if (ServerConfig.JMS302orLater()) {
            p.Encode1(0);
        }
        // if LP_UserChatNLCPQ, add more str
        // p.EncodeStr("");
        return p.get();
    }

    public static MaplePacket followEffect(int initiator, int replier, Point toMap) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserFollowCharacter.get());
        mplew.writeInt(initiator);
        mplew.writeInt(replier);
        if (replier == 0) {
            //cancel
            mplew.write(toMap == null ? 0 : 1); //1 -> x (int) y (int) to change map
            if (toMap != null) {
                mplew.writeInt(toMap.x);
                mplew.writeInt(toMap.y);
            }
        }
        return mplew.getPacket();
    }
    
}
