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
package tacos.packet.request.sub;

import odin.client.BuddyList;
import odin.client.BuddylistEntry;
import odin.client.MapleCharacter;
import tacos.config.Region;
import tacos.config.Version;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsFriend;
import tacos.packet.ops.arg.ArgFriend;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.server.TacosFriend;

/**
 *
 * @author Riremito
 */
public class ReqSub_FriendRequest {

    // CP_FriendRequest
    public static boolean OnFriendRequest(ClientPacket cp, MapleCharacter chr) {
        byte flag = cp.Decode1();
        OpsFriend ops_req = OpsFriend.find(flag);
        DebugLogger.DebugLog("OnFriendRequest : " + ops_req + " (" + flag + ")");
        BuddyList buddylist = chr.getBuddylist();

        switch (ops_req) {
            case FriendReq_LoadFriend: {
                return true;
            }
            case FriendReq_SetFriend: {
                String friend_name = cp.DecodeStr();
                String friend_tag = (Version.LessOrEqual(Region.KMS, 55) || Version.LessOrEqual(Region.JMS, 147)) ? "" : cp.DecodeStr(); // KMS65, JMS164

                if (12 < friend_name.length() || 16 < friend_tag.length()) {
                    chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_Unknown));
                    return true;
                }
                if (buddylist.isFull()) {
                    chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_FullMe));
                    return true;
                }
                BuddylistEntry ble_found = buddylist.get(friend_name);
                if (ble_found != null) {
                    if (ble_found.getGroup().equals(friend_tag)) {
                        chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_AlreadySet));
                        return true;
                    }
                    ble_found.setGroup(friend_tag);
                    chr.SendPacket(ResWrapper.updateBuddylist(chr));
                    return true;
                }

                TacosFriend friend = TacosFriend.findByName(chr, friend_name);
                // invalid character name
                if (friend == null) {
                    chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_UnknownUser));
                    return true;
                }
                if (friend.getOnline()) {
                    if (friend.getCharacter().getBuddylist().isFull()) {
                        chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_FullOther));
                        return true;
                    }
                    MapleCharacter online_friend = friend.getCharacter();
                    BuddylistEntry ble_hidden = new BuddylistEntry(chr.getName(), chr.getId(), friend_tag, chr.getChannelId(), true, chr.getLevel(), chr.getJob());
                    ble_hidden.setHidden(true);
                    online_friend.getBuddylist().put(ble_hidden);
                    ArgFriend arg = new ArgFriend();
                    arg.flag = OpsFriend.FriendRes_SetFriend_Done;
                    arg.chr = online_friend;
                    online_friend.SendPacket(ResCWvsContext.FriendResult(arg));
                    // 事前に友達リストに追加しないと拒否を押した場合に無限ループが発生する
                    online_friend.SendPacket(ResWrapper.requestBuddylistAdd(chr.getId(), chr.getName(), chr.getLevel(), chr.getJob()));
                }
                BuddylistEntry ble_new = new BuddylistEntry(friend.getName(), friend.getId(), friend_tag, 0, true, friend.getLevel(), friend.getJob());
                chr.getBuddylist().put(ble_new);
                ArgFriend arg = new ArgFriend();
                arg.flag = OpsFriend.FriendRes_SetFriend_Done;
                arg.chr = chr;
                chr.SendPacket(ResCWvsContext.FriendResult(arg));
                return true;
            }
            case FriendReq_AcceptFriend: {
                // 友達申し込み承認
                int friend_id = cp.Decode4();

                if (buddylist.isFull()) {
                    chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_FullMe));
                    return true;
                }
                BuddylistEntry ble_hidden = chr.getBuddylist().get(friend_id);

                if (ble_hidden == null) {
                    return true;
                }

                MapleCharacter friend = chr.getWorld().findOnlinePlayerById(friend_id);
                if (friend != null) {
                    BuddylistEntry ble_offline = friend.getBuddylist().get(chr.getId());
                    if (ble_offline != null) {
                        ble_offline.setChannel(chr.getChannelId());
                    }
                    ArgFriend arg = new ArgFriend();
                    arg.flag = OpsFriend.FriendRes_SetFriend_Done;
                    arg.chr = friend;
                    friend.SendPacket(ResCWvsContext.FriendResult(arg));
                }

                ble_hidden.setHidden(false);
                ArgFriend arg = new ArgFriend();
                arg.flag = OpsFriend.FriendRes_SetFriend_Done;
                arg.chr = chr;
                chr.SendPacket(ResCWvsContext.FriendResult(arg));
                return true;
            }
            case FriendReq_DeleteFriend: {
                // 友達申し込み拒否
                int friend_id = cp.Decode4();
                chr.getBuddylist().remove(friend_id);

                MapleCharacter friend = chr.getWorld().findOnlinePlayerById(friend_id);
                if (friend != null) {
                    BuddylistEntry ble_offline = friend.getBuddylist().get(chr.getId());
                    if (ble_offline != null) {
                        ble_offline.setChannel(0);
                    }
                    ArgFriend arg = new ArgFriend();
                    arg.flag = OpsFriend.FriendRes_SetFriend_Done;
                    arg.chr = friend;
                    friend.SendPacket(ResCWvsContext.FriendResult(arg));
                }

                ArgFriend arg = new ArgFriend();
                arg.flag = OpsFriend.FriendRes_DeleteFriend_Done;
                arg.chr = chr;
                chr.SendPacket(ResCWvsContext.FriendResult(arg));
                return true;
            }
            case FriendReq_NotifyLogin: {
                return true;
            }
            case FriendReq_NotifyLogout: {
                return true;
            }
            case FriendReq_IncMaxCount: {
                return true;
            }
            default: {
                DebugLogger.ErrorLog("OnFriendRequest not coded : " + flag);
                break;
            }
        }

        return false;
    }

}
