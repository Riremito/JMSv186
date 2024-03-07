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
package packet.server.response;

import client.BuddylistEntry;
import client.MapleCharacter;
import debug.Debug;
import handling.MaplePacket;
import java.util.Collection;
import packet.server.ServerPacket;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class FriendResponse {

    public enum FriendOps {
        FriendReq_LoadFriend(0x0),
        FriendReq_SetFriend(0x1),
        FriendReq_AcceptFriend(0x2),
        FriendReq_DeleteFriend(0x3),
        FriendReq_NotifyLogin(0x4),
        FriendReq_NotifyLogout(0x5),
        FriendReq_IncMaxCount(0x6),
        FriendRes_LoadFriend_Done(0x7),
        FriendRes_NotifyChange_FriendInfo(0x8),
        FriendRes_Invite(0x9),
        FriendRes_SetFriend_Done(0xA),
        FriendRes_SetFriend_FullMe(0xB),
        FriendRes_SetFriend_FullOther(0xC),
        FriendRes_SetFriend_AlreadySet(0xD),
        FriendRes_SetFriend_Master(0xE),
        FriendRes_SetFriend_UnknownUser(0xF),
        FriendRes_SetFriend_Unknown(0x10),
        FriendRes_AcceptFriend_Unknown(0x11),
        FriendRes_DeleteFriend_Done(0x12),
        FriendRes_DeleteFriend_Unknown(0x13),
        FriendRes_Notify(0x14),
        FriendRes_IncMaxCount_Done(0x15),
        FriendRes_IncMaxCount_Unknown(0x16),
        FriendRes_PleaseWait(0x17),
        UNKNOWN(-1);

        private int value;

        FriendOps(int flag) {
            value = flag;
        }

        FriendOps() {
            value = -1;
        }

        public int get() {
            return value;
        }

        public static FriendOps find(int val) {
            for (final FriendOps o : FriendOps.values()) {
                if (o.get() == val) {
                    return o;
                }
            }
            return UNKNOWN;
        }
    }

    // CWvsContext::OnFriendResult
    public static MaplePacket FriendResult(FriendOps flag, MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FriendResult);

        sp.Encode1(flag.get());

        switch (flag) {
            case FriendReq_LoadFriend: {
                break;
            }
            case FriendReq_SetFriend: {
                break;
            }
            case FriendReq_AcceptFriend: {
                break;
            }
            case FriendReq_DeleteFriend: {
                break;
            }
            case FriendReq_NotifyLogin: {
                break;
            }
            case FriendReq_NotifyLogout: {
                break;
            }
            case FriendReq_IncMaxCount: {
                break;
            }
            case FriendRes_LoadFriend_Done: {
                sp.EncodeBuffer(Reset_Encode(chr));
                break;
            }
            case FriendRes_NotifyChange_FriendInfo: {
                break;
            }
            case FriendRes_Invite: {
                break;
            }
            case FriendRes_SetFriend_Done: {
                sp.EncodeBuffer(Reset_Encode(chr));
                break;
            }
            case FriendRes_SetFriend_FullMe: {
                break;
            }
            case FriendRes_SetFriend_FullOther: {
                break;
            }
            case FriendRes_SetFriend_AlreadySet: {
                break;
            }
            case FriendRes_SetFriend_Master: {
                break;
            }
            case FriendRes_SetFriend_UnknownUser: {
                break;
            }
            case FriendRes_SetFriend_Unknown: {
                break;
            }
            case FriendRes_AcceptFriend_Unknown: {
                break;
            }
            case FriendRes_DeleteFriend_Done: {
                sp.EncodeBuffer(Reset_Encode(chr));
                break;
            }
            case FriendRes_DeleteFriend_Unknown: {
                break;
            }
            case FriendRes_Notify: {
                break;
            }
            case FriendRes_IncMaxCount_Done: {
                break;
            }
            case FriendRes_IncMaxCount_Unknown: {
                break;
            }
            case FriendRes_PleaseWait: {
                break;
            }
            default: {
                Debug.ErrorLog("FieldEffect not coded : " + flag);
                break;
            }
        }

        return sp.Get();
    }

    // CWvsContext::CFriend::Reset
    public static byte[] Reset_Encode(MapleCharacter chr) {
        Collection<BuddylistEntry> friend_list = chr.getBuddylist().getBuddies();
        ServerPacket data_friend = new ServerPacket();
        ServerPacket data_in_shop = new ServerPacket();

        for (BuddylistEntry friend : friend_list) {
            // 39 bytes
            data_friend.Encode4(friend.getCharacterId());
            data_friend.EncodeBuffer(friend.getName(), 13);
            data_friend.Encode1(0);
            data_friend.Encode4(friend.getChannel() == -1 ? -1 : friend.getChannel() - 1);
            data_friend.EncodeBuffer(friend.getGroup(), 17);
            // 4 bytes
            data_in_shop.Encode4(0);
        }

        ServerPacket data = new ServerPacket();
        data.Encode1(friend_list.size());
        data.EncodeBuffer(data_friend.Get().getBytes());
        data.EncodeBuffer(data_in_shop.Get().getBytes());
        return data.Get().getBytes();
    }

    public static MaplePacket updateBuddyCapacity(int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(21);
        mplew.write(capacity);
        return mplew.getPacket();
    }

    public static MaplePacket buddylistMessage(byte message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(message);
        return mplew.getPacket();
    }

    public static MaplePacket updateBuddylist(Collection<BuddylistEntry> buddylist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(7);
        mplew.write(buddylist.size());
        for (BuddylistEntry buddy : buddylist) {
            if (buddy.isVisible()) {
                mplew.writeInt(buddy.getCharacterId());
                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getName(), '\u0000', 13));
                mplew.write(0);
                mplew.writeInt(buddy.getChannel() == -1 ? -1 : buddy.getChannel() - 1);
                mplew.writeAsciiString(StringUtil.getRightPaddedStr(buddy.getGroup(), '\u0000', 17));
            }
        }
        for (int x = 0; x < buddylist.size(); x++) {
            mplew.writeInt(0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateBuddyChannel(int characterid, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(20);
        mplew.writeInt(characterid);
        mplew.write(0);
        mplew.writeInt(channel);
        return mplew.getPacket();
    }

    public static MaplePacket requestBuddylistAdd(int cidFrom, String nameFrom, int levelFrom, int jobFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FriendResult.Get());
        mplew.write(9);
        mplew.writeInt(cidFrom);
        mplew.writeMapleAsciiString(nameFrom);
        mplew.writeInt(levelFrom);
        mplew.writeInt(jobFrom);
        mplew.writeInt(cidFrom);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(nameFrom, '\u0000', 13));
        mplew.write(1);
        mplew.writeInt(0);
        mplew.writeAsciiString(StringUtil.getRightPaddedStr("ETC", '\u0000', 16));
        mplew.writeShort(1);
        return mplew.getPacket();
    }

}
