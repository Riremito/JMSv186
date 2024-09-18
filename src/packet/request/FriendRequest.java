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
package packet.request;

import client.BuddyList;
import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import debug.Debug;
import handling.channel.ChannelServer;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import packet.ClientPacket;
import packet.ops.FriendOps;
import packet.response.FriendResponse;

/**
 *
 * @author Riremito
 */
public class FriendRequest {

    public static boolean OnPacket(ClientPacket cp, MapleClient c) {
        byte bf = cp.Decode1();
        FriendOps flag = FriendOps.find(bf);

        switch (flag) {
            case FriendReq_LoadFriend: {
                break;
            }
            case FriendReq_SetFriend: {
                String friend_name = cp.DecodeStr();
                String friend_tag = cp.DecodeStr();

                SetFriend(c, friend_name, friend_tag);
                break;
            }
            case FriendReq_AcceptFriend: {
                int friend_id = cp.Decode4();

                AcceptFriend(c, friend_id);
                break;
            }
            case FriendReq_DeleteFriend: {
                int friend_id = cp.Decode4();

                DeleteFriend(c, friend_id);
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
            default: {
                Debug.ErrorLog("FriendRequest not coded : " + bf);
                break;
            }
        }
        return true;
    }

    private static void SetFriend(MapleClient c, String friend_name, String friend_tag) {
        final BuddyList buddylist = c.getPlayer().getBuddylist();
        final BuddylistEntry ble = buddylist.get(friend_name);
        if (friend_name.length() > 13 || friend_tag.length() > 16) {
            return;
        }
        if (ble != null && (ble.getGroup().equals(friend_tag) || !ble.isVisible())) {
            c.getSession().write(FriendResponse.buddylistMessage(FriendOps.FriendRes_SetFriend_FullMe));
        } else if (ble != null && ble.isVisible()) {
            ble.setGroup(friend_tag);
            c.getSession().write(FriendResponse.updateBuddylist(c.getPlayer()));
        } else if (buddylist.isFull()) {
            c.getSession().write(FriendResponse.buddylistMessage(FriendOps.FriendRes_SetFriend_FullMe));
        } else {
            try {
                CharacterIdNameBuddyCapacity charWithId = null;
                int channel = World.Find.findChannel(friend_name);
                MapleCharacter otherChar = null;
                if (channel > 0) {
                    otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(friend_name);
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
                        c.getSession().write(FriendResponse.buddylistMessage(FriendOps.FriendRes_SetFriend_FullOther));
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
                        c.getSession().write(FriendResponse.updateBuddylist(c.getPlayer()));
                    }
                } else {
                    c.getSession().write(FriendResponse.buddylistMessage(FriendOps.FriendRes_SetFriend_UnknownUser));
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
                    final MapleCharacter otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterById(friend_id);
                    otherName = otherChar.getName();
                    otherLevel = otherChar.getLevel();
                    otherJob = otherChar.getJob();
                }
                if (otherName != null) {
                    buddylist.put(new BuddylistEntry(otherName, friend_id, "マイ友未指定", channel, true, otherLevel, otherJob));
                    c.getSession().write(FriendResponse.updateBuddylist(c.getPlayer()));
                    notifyRemoteChannel(c, channel, friend_id, "マイ友未指定", BuddyList.BuddyOperation.ADDED);
                }
            } catch (SQLException e) {
                System.err.println("SQL THROW" + e);
            }
        } else {
            c.getSession().write(FriendResponse.buddylistMessage(FriendOps.FriendRes_SetFriend_FullMe));
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
        c.getSession().write(FriendResponse.updateBuddylist(c.getPlayer()));
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
            c.getSession().write(FriendResponse.requestBuddylistAdd(pendingBuddyRequest.getId(), pendingBuddyRequest.getName(), pendingBuddyRequest.getLevel(), pendingBuddyRequest.getJob()));
        }
    }

    private static final CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(final String name, final String group) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ?");
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
    }

    private static final void notifyRemoteChannel(final MapleClient c, final int remoteChannel, final int otherCid, final String group, final BuddyList.BuddyOperation operation) {
        final MapleCharacter player = c.getPlayer();
        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, player.getLevel(), player.getJob(), group);
        }
    }

}
