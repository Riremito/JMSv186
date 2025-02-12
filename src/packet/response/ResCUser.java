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

    //magnify glass
    public static MaplePacket getPotentialReset(int chr, short pos) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserItemReleaseEffect);
        sp.Encode4(chr);
        sp.Encode2(pos);
        return sp.get();
    }

    //miracle cube?
    public static MaplePacket getPotentialEffect(int chr, short pos) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserItemUnreleaseEffect);
        sp.Encode4(chr);
        sp.Encode1(1);
        if (ServerConfig.JMS302orLater()) {
            sp.Encode4(0); // 金印 2049500
        }
        return sp.get();
    }

    public static MaplePacket getScrollEffect(int chr, IEquip.ScrollResult scrollSuccess, boolean legendarySpirit) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserItemUpgradeEffect);
        sp.Encode4(chr);
        if (ServerConfig.JMS302orLater()) {
            /*
                0 = 失敗
                1 = 成功
                2 = 破壊
                3 = 使用不可
             */
            int result = 0;
            if (scrollSuccess == IEquip.ScrollResult.SUCCESS) {
                result = 1;
            }
            if (scrollSuccess == IEquip.ScrollResult.CURSE) {
                result = 2;
            }
            sp.Encode1(result);
            sp.Encode1(legendarySpirit ? 1 : 0); // bEnchantSkill
            sp.Encode4(0); // item ID
            sp.Encode4(0); // 1 = 書の名前表示
            sp.Encode1(0); // White Scroll
            sp.Encode1(0);
            sp.Encode4(0); // 2 = 装備のアップグレードに成功しました。
        } else {
            sp.Encode1(scrollSuccess == IEquip.ScrollResult.SUCCESS ? 1 : 0); // bSuccess
            sp.Encode1(scrollSuccess == IEquip.ScrollResult.CURSE ? 1 : 0);
            sp.Encode1(legendarySpirit ? 1 : 0); // bEnchantSkill
            sp.Encode1(0); // White Scroll
            if (ServerConfig.JMS186orLater()) {
                sp.Encode1(0);
                sp.Encode4(0); // 2 = 装備のアップグレードに成功しました。
            }
        }
        return sp.get();
    }

    public static MaplePacket getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_UserChat);
        p.Encode4(cidfrom);
        p.Encode1(whiteBG ? 1 : 0);
        p.EncodeStr(text);
        if (ServerConfig.JMS146orLater()) {
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
