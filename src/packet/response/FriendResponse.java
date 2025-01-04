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

import client.BuddylistEntry;
import client.MapleCharacter;
import debug.Debug;
import handling.MaplePacket;
import java.util.Collection;
import packet.ops.OpsFriend;
import packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class FriendResponse {

    public static class FriendResultStruct {

        OpsFriend flag;
        int nFriendMax;
        MapleCharacter chr;
        int friend_id;
        int friend_channel;
        int friend_level;
        int friend_job;
        String friend_name;
        String friend_tag;
    }

    // CWvsContext::OnFriendResult
    public static MaplePacket FriendResult(FriendResultStruct frs) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FriendResult);

        sp.Encode1(frs.flag.get());

        switch (frs.flag) {
            case FriendRes_LoadFriend_Done:
            case FriendRes_SetFriend_Done:
            case FriendRes_DeleteFriend_Done: {
                sp.EncodeBuffer(Reset_Encode(frs.chr));
                break;
            }
            case FriendRes_NotifyChange_FriendInfo: {
                break;
            }
            case FriendRes_Invite: {
                // 9
                sp.Encode4(frs.friend_id);
                sp.EncodeStr(frs.friend_name);
                sp.Encode4(frs.friend_level);
                sp.Encode4(frs.friend_job);
                // CWvsContext::CFriend::Insert, 39 bytes
                sp.Encode4(frs.friend_id);
                sp.EncodeBuffer(frs.friend_name, 13);
                sp.Encode1(0);
                sp.Encode4(frs.friend_channel == -1 ? -1 : frs.friend_channel - 1); // please add channel
                sp.EncodeBuffer(frs.friend_tag, 17);
                // 1 byte
                sp.Encode1(1);
                break;
            }
            case FriendRes_SetFriend_FullMe: {
                // none
                break;
            }
            case FriendRes_SetFriend_FullOther: {
                // none
                break;
            }
            case FriendRes_SetFriend_AlreadySet: {
                break;
            }
            case FriendRes_SetFriend_Master: {
                break;
            }
            case FriendRes_SetFriend_UnknownUser: {
                // none
                break;
            }
            case FriendRes_SetFriend_Unknown: {
                break;
            }
            case FriendRes_AcceptFriend_Unknown: {
                break;
            }
            case FriendRes_DeleteFriend_Unknown: {
                break;
            }
            case FriendRes_Notify: {
                sp.Encode4(frs.friend_id);
                sp.Encode1(0);
                sp.Encode4(frs.friend_channel);
                break;
            }
            case FriendRes_IncMaxCount_Done: {
                sp.Encode1(frs.nFriendMax);
                break;
            }
            case FriendRes_IncMaxCount_Unknown: {
                break;
            }
            case FriendRes_PleaseWait: {
                break;
            }
            default: {
                Debug.ErrorLog("FriendResult not coded : " + frs.flag);
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
        FriendResultStruct frs = new FriendResultStruct();
        frs.flag = OpsFriend.FriendRes_IncMaxCount_Done;
        frs.nFriendMax = capacity;

        return FriendResult(frs);
    }

    // test
    public static MaplePacket buddylistMessage(OpsFriend flag) {
        FriendResultStruct frs = new FriendResultStruct();
        frs.flag = flag;
        return FriendResult(frs);
    }

    public static MaplePacket updateBuddylist(MapleCharacter chr) {
        FriendResultStruct frs = new FriendResultStruct();
        frs.flag = OpsFriend.FriendRes_LoadFriend_Done;
        frs.chr = chr;

        return FriendResult(frs);
    }

    public static MaplePacket updateBuddyChannel(int friend_id, int friend_channel) {
        FriendResultStruct frs = new FriendResultStruct();
        frs.flag = OpsFriend.FriendRes_Notify;
        frs.friend_id = friend_id;
        frs.friend_channel = friend_channel;

        return FriendResult(frs);
    }

    public static MaplePacket requestBuddylistAdd(int friend_id, String name, int level, int job) {
        FriendResultStruct frs = new FriendResultStruct();
        frs.flag = OpsFriend.FriendRes_Invite;
        frs.friend_id = friend_id;
        frs.friend_channel = 0; // todo
        frs.friend_name = name;
        frs.friend_level = level;
        frs.friend_job = job;
        frs.friend_tag = "マイ友未指定"; // JMS

        return FriendResult(frs);
    }

}
