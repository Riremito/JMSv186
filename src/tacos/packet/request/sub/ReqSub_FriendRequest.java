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
import odin.client.CharacterNameAndId;
import odin.client.MapleCharacter;
import tacos.config.Region;
import tacos.config.Version;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsFriend;
import tacos.packet.ops.arg.ArgFriend;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;

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
                BuddylistEntry ble = buddylist.get(friend_name);
                if (ble != null) {
                    if (ble.getGroup().equals(friend_tag)) {
                        chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_AlreadySet));
                        return true;
                    }
                    ble.setGroup(friend_tag);
                    chr.SendPacket(ResWrapper.updateBuddylist(chr));
                    return true;
                }

                // online
                MapleCharacter friend = chr.getWorld().findOnlinePlayer(friend_name);
                if (friend != null) {
                    if (friend.getBuddylist().isFull()) {
                        chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_FullOther));
                        return true;
                    }
                    BuddylistEntry ble_online = new BuddylistEntry(friend_name, friend.getId(), friend_tag, 0, true, friend.getLevel(), friend.getJob());
                    chr.getBuddylist().put(ble_online);

                    BuddylistEntry ble_hidden = new BuddylistEntry(chr.getName(), chr.getId(), friend_tag, chr.getChannelId(), true, chr.getLevel(), chr.getJob());
                    ble_hidden.setHidden(true);
                    friend.getBuddylist().put(ble_hidden);
                    ArgFriend arg = new ArgFriend();
                    arg.flag = OpsFriend.FriendRes_SetFriend_Done;
                    arg.chr = friend;
                    friend.SendPacket(ResCWvsContext.FriendResult(arg));
                    // 事前に友達リストに追加しないと拒否を押した場合に無限ループが発生する
                    friend.SendPacket(ResWrapper.requestBuddylistAdd(chr.getId(), chr.getName(), chr.getLevel(), chr.getJob()));
                } else {
                    CharacterIdNameBuddyCapacity cibc = getCharacterIdAndNameFromDatabase(friend_name, friend_tag);
                    // invalid character name
                    if (cibc == null) {
                        chr.SendPacket(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_UnknownUser));
                        return true;
                    }
                    // TODO : check friend is full or not.
                    // offline
                    BuddylistEntry ble_offline = new BuddylistEntry(friend_name, cibc.getId(), friend_tag, 0, true, cibc.getLevel(), cibc.getJob());
                    chr.getBuddylist().put(ble_offline);
                }

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

    private static class CharacterIdNameBuddyCapacity extends CharacterNameAndId {

        private int buddyCapacity;

        public CharacterIdNameBuddyCapacity(int id, String name, int level, int job, String group, int buddyCapacity) {
            super(id, name, level, job, group);
            this.buddyCapacity = buddyCapacity;
        }

        public int getBuddyCapacity() {
            return buddyCapacity;
        }
    }

    private static CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(String name, String group) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            CharacterIdNameBuddyCapacity ret = null;
            if (rs.next()) {
                if (rs.getInt("gm") == 0) {
                    ret = new CharacterIdNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), rs.getInt("level"), rs.getInt("job"), group, rs.getInt("buddyCapacity"));
                }
            }
            rs.close();
            ps.close();
            return ret;
        } catch (SQLException ex) {
            Logger.getLogger(ReqSub_FriendRequest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
