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
package packet.request.sub;

import client.BuddyList;
import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import config.Region;
import config.Version;
import database.DatabaseConnection;
import debug.DebugLogger;
import server.server.Server_Game;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import packet.ClientPacket;
import packet.ops.OpsFriend;
import packet.ops.arg.ArgFriend;
import packet.response.ResCWvsContext;
import packet.response.wrapper.ResWrapper;

/**
 *
 * @author Riremito
 */
public class ReqSub_FriendRequest {

    private static final String fakeFriend = "FakeFriend";

    // CP_FriendRequest
    public static boolean OnFriendRequest(MapleCharacter chr, ClientPacket cp) {
        byte flag = cp.Decode1();
        OpsFriend ops_req = OpsFriend.find(flag);

        switch (ops_req) {
            case FriendReq_LoadFriend: {
                return true;
            }
            case FriendReq_SetFriend: {
                String name = cp.DecodeStr();
                String tag = (Version.LessOrEqual(Region.KMS, 55) || Version.LessOrEqual(Region.JMS, 147)) ? null : cp.DecodeStr(); // KMS65, JMS164

                // fake data
                if (name.equals(fakeFriend)) {
                    BuddylistEntry be_fakefriend = new BuddylistEntry(name, chr.getId() + 1000, tag, chr.getClient().getChannel(), true, chr.getLevel(), chr.getJob());
                    chr.getBuddylist().put(be_fakefriend);
                } else {
                    int ch = World.Find.findChannel(name);
                    if (ch != -1) {
                        MapleCharacter chr_friend = Server_Game.getInstance(ch).getPlayerStorage().getCharacterByName(name);
                        if (chr_friend != null) {
                            BuddylistEntry be_friend = new BuddylistEntry(name, chr_friend.getId(), tag, chr_friend.getClient().getChannel(), true, chr_friend.getLevel(), chr_friend.getJob());
                            chr.getBuddylist().put(be_friend);
                        }
                    } else {
                        CharacterIdNameBuddyCapacity cibc = getCharacterIdAndNameFromDatabase(name, tag);
                        if (cibc != null) {
                            BuddylistEntry be_friend = new BuddylistEntry(name, cibc.getId(), tag, 0, true, cibc.getLevel(), cibc.getJob());
                            chr.getBuddylist().put(be_friend);
                        }
                    }
                }

                ArgFriend arg = new ArgFriend();
                arg.flag = OpsFriend.FriendRes_SetFriend_Done;
                arg.chr = chr;
                chr.SendPacket(ResCWvsContext.FriendResult(arg));
                //SetFriend(c, friend_name, friend_tag);
                return true;
            }
            case FriendReq_AcceptFriend: {
                int friend_id = cp.Decode4();

                //AcceptFriend(c, friend_id);
                return true;
            }
            case FriendReq_DeleteFriend: {
                int friend_id = cp.Decode4();
                chr.getBuddylist().remove(friend_id);

                ArgFriend arg = new ArgFriend();
                arg.flag = OpsFriend.FriendRes_DeleteFriend_Done;
                arg.chr = chr;
                chr.SendPacket(ResCWvsContext.FriendResult(arg));
                //DeleteFriend(c, friend_id);
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

    private static void SetFriend(MapleClient c, String friend_name, String friend_tag) {
        final BuddyList buddylist = c.getPlayer().getBuddylist();
        final BuddylistEntry ble = buddylist.get(friend_name);
        if (friend_name.length() > 13 || friend_tag.length() > 16) {
            return;
        }
        if (ble != null && (ble.getGroup().equals(friend_tag) || !ble.isVisible())) {
            c.getSession().write(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_FullMe));
        } else if (ble != null && ble.isVisible()) {
            ble.setGroup(friend_tag);
            c.getSession().write(ResWrapper.updateBuddylist(c.getPlayer()));
        } else if (buddylist.isFull()) {
            c.getSession().write(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_FullMe));
        } else {
            try {
                CharacterIdNameBuddyCapacity charWithId = null;
                int channel = World.Find.findChannel(friend_name);
                MapleCharacter otherChar = null;
                if (channel > 0) {
                    otherChar = Server_Game.getInstance(channel).getPlayerStorage().getCharacterByName(friend_name);
                    if (!otherChar.isGM() || c.getPlayer().isGM()) {
                        charWithId = new CharacterIdNameBuddyCapacity(otherChar.getId(), otherChar.getName(), otherChar.getLevel(), otherChar.getJob(), friend_tag, otherChar.getBuddylist().getCapacity());
                    }
                } else {
                    charWithId = getCharacterIdAndNameFromDatabase(friend_name, friend_tag);
                }
                if (charWithId != null) {
                    BuddyList.BuddyAddResult buddyAddResult = null;
                    if (channel > 0) {
                        buddyAddResult = World.Buddy.requestBuddyAdd(friend_name, c.getChannel(), c.getPlayer().getId(), c.getPlayer().getName(), c.getPlayer().getLevel(), c.getPlayer().getJob());
                    } else {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0");
                        ps.setInt(1, charWithId.getId());
                        ResultSet rs = ps.executeQuery();
                        if (!rs.next()) {
                            ps.close();
                            rs.close();
                            throw new RuntimeException("Result set expected");
                        } else {
                            int count = rs.getInt("buddyCount");
                            if (count >= charWithId.getBuddyCapacity()) {
                                buddyAddResult = BuddyList.BuddyAddResult.BUDDYLIST_FULL;
                            }
                        }
                        rs.close();
                        ps.close();
                        ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?");
                        ps.setInt(1, charWithId.getId());
                        ps.setInt(2, c.getPlayer().getId());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            buddyAddResult = BuddyList.BuddyAddResult.ALREADY_ON_LIST;
                        }
                        rs.close();
                        ps.close();
                    }
                    if (buddyAddResult == BuddyList.BuddyAddResult.BUDDYLIST_FULL) {
                        c.getSession().write(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_FullOther));
                    } else {
                        int displayChannel = -1;
                        int otherCid = charWithId.getId();
                        if (buddyAddResult == BuddyList.BuddyAddResult.ALREADY_ON_LIST && channel > 0) {
                            displayChannel = channel;
                            notifyRemoteChannel(c, channel, otherCid, friend_tag, BuddyList.BuddyOperation.ADDED);
                        } else if (buddyAddResult != BuddyList.BuddyAddResult.ALREADY_ON_LIST && channel > 0) {
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `groupname`, `pending`) VALUES (?, ?, ?, 1)");
                            ps.setInt(1, charWithId.getId());
                            ps.setInt(2, c.getPlayer().getId());
                            ps.setString(3, friend_tag);
                            ps.executeUpdate();
                            ps.close();
                        }
                        buddylist.put(new BuddylistEntry(charWithId.getName(), otherCid, friend_tag, displayChannel, true, charWithId.getLevel(), charWithId.getJob()));
                        c.getSession().write(ResWrapper.updateBuddylist(c.getPlayer()));
                    }
                } else {
                    c.getSession().write(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_UnknownUser));
                }
            } catch (SQLException e) {
                System.err.println("SQL THROW" + e);
            }
        }
        nextPendingRequest(c);
    }

    private static void AcceptFriend(MapleClient c, int friend_id) {
        final BuddyList buddylist = c.getPlayer().getBuddylist();
        if (!buddylist.isFull()) {
            try {
                final int channel = World.Find.findChannel(friend_id);
                String otherName = null;
                int otherLevel = 0;
                int otherJob = 0;
                if (channel < 0) {
                    Connection con = DatabaseConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement("SELECT name, level, job FROM characters WHERE id = ?");
                    ps.setInt(1, friend_id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        otherName = rs.getString("name");
                        otherLevel = rs.getInt("level");
                        otherJob = rs.getInt("job");
                    }
                    rs.close();
                    ps.close();
                } else {
                    final MapleCharacter otherChar = Server_Game.getInstance(channel).getPlayerStorage().getCharacterById(friend_id);
                    otherName = otherChar.getName();
                    otherLevel = otherChar.getLevel();
                    otherJob = otherChar.getJob();
                }
                if (otherName != null) {
                    buddylist.put(new BuddylistEntry(otherName, friend_id, "マイ友未指定", channel, true, otherLevel, otherJob));
                    c.getSession().write(ResWrapper.updateBuddylist(c.getPlayer()));
                    notifyRemoteChannel(c, channel, friend_id, "マイ友未指定", BuddyList.BuddyOperation.ADDED);
                }
            } catch (SQLException e) {
                System.err.println("SQL THROW" + e);
            }
        } else {
            c.getSession().write(ResWrapper.buddylistMessage(OpsFriend.FriendRes_SetFriend_FullMe));
        }
        nextPendingRequest(c);
    }

    private static void DeleteFriend(MapleClient c, int friend_id) {
        final BuddyList buddylist = c.getPlayer().getBuddylist();
        final BuddylistEntry blz = buddylist.get(friend_id);
        if (blz != null && blz.isVisible()) {
            notifyRemoteChannel(c, World.Find.findChannel(friend_id), friend_id, blz.getGroup(), BuddyList.BuddyOperation.DELETED);
        }
        buddylist.remove(friend_id);
        c.getSession().write(ResWrapper.updateBuddylist(c.getPlayer()));
        nextPendingRequest(c);
    }

    private static final class CharacterIdNameBuddyCapacity extends CharacterNameAndId {

        private int buddyCapacity;

        public CharacterIdNameBuddyCapacity(int id, String name, int level, int job, String group, int buddyCapacity) {
            super(id, name, level, job, group);
            this.buddyCapacity = buddyCapacity;
        }

        public int getBuddyCapacity() {
            return buddyCapacity;
        }
    }

    private static final void nextPendingRequest(final MapleClient c) {
        CharacterNameAndId pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
        if (pendingBuddyRequest != null) {
            c.getSession().write(ResWrapper.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
    }

    private static final CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(final String name, final String group) {
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

    private static final void notifyRemoteChannel(final MapleClient c, final int remoteChannel, final int otherCid, final String group, final BuddyList.BuddyOperation operation) {
        final MapleCharacter player = c.getPlayer();
        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, player.getLevel(), player.getJob(), group);
        }
    }

}
