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

import odin.client.MapleCharacter;
import odin.client.inventory.IEquip;
import config.Region;
import config.ServerConfig;
import config.Version;
import server.network.MaplePacket;
import java.awt.Point;
import packet.ServerPacket;
import packet.response.struct.TestHelper;
import odin.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
// CUserPool::OnUserCommonPacket & CUser
public class ResCUser {

    // CUser::OnChat
    public static MaplePacket UserChat(MapleCharacter chr, String message, boolean bOnlyBalloon) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserChat);

        sp.Encode4(chr.getId());
        sp.Encode1(chr.isGM() ? 1 : 0);
        sp.EncodeStr(message);

        if (ServerConfig.JMS146orLater()) {
            sp.Encode1(bOnlyBalloon ? 1 : 0); // skill macro
        }

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }

        return sp.get();
    }

    // CUser::OnADBoard
    public static MaplePacket UserADBoard(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserADBoard);

        String message = chr.getADBoard();
        boolean open = (message != null);

        sp.Encode4(chr.getId());
        sp.Encode1(open ? 1 : 0);

        if (open) {
            sp.EncodeStr(message);
        }

        return sp.get();
    }

    // CUser::OnMiniRoomBalloon
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

    // CUser::SetConsumeItemEffect
    // CUser::ShowItemUpgradeEffect
    public static MaplePacket getScrollEffect(int chr, IEquip.ScrollResult scrollSuccess, boolean legendarySpirit) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserItemUpgradeEffect);
        sp.Encode4(chr);
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
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

    // CUser::ShowItemHyperUpgradeEffect
    // CUser::ShowItemOptionUpgradeEffect
    // CUser::ShowItemReleaseEffect
    public static MaplePacket UserItemReleaseEffect(MapleCharacter chr, short equip_item_slot) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserItemReleaseEffect);
        sp.Encode4(chr.getId());
        sp.Encode2(equip_item_slot);
        return sp.get();
    }

    // CUser::ShowItemUnreleaseEffect
    public static MaplePacket UserItemUnreleaseEffect(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserItemUnreleaseEffect);
        sp.Encode4(chr.getId());
        sp.Encode1(1);
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode4(0); // 金印 2049500
        }
        return sp.get();
    }

    // CUser::OnHitByUser
    // CUser::OnTeslaTriangle
    // CUser::OnFollowCharacter
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

    // CUser::OnShowPQReward
    // JMS
    public static MaplePacket fishingCaught(int chrid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_Fishing_Caught);
        sp.Encode4(chrid);
        return sp.get();
    }

}
