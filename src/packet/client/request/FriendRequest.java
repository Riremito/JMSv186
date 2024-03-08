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
package packet.client.request;

import client.BuddyList;
import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import packet.server.response.FriendResponse;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Riremito
 */
public class FriendRequest {

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

    public static final void BuddyOperation(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        final int mode = slea.readByte();
        final BuddyList buddylist = c.getPlayer().getBuddylist();
        if (mode == 1) {
            // add
            final String addName = slea.readMapleAsciiString();
            final String groupName = slea.readMapleAsciiString();
            final BuddylistEntry ble = buddylist.get(addName);
            if (addName.length() > 13 || groupName.length() > 16) {
                return;
            }
            if (ble != null && (ble.getGroup().equals(groupName) || !ble.isVisible())) {
                c.getSession().write(FriendResponse.buddylistMessage(FriendResponse.FriendOps.FriendRes_SetFriend_FullMe));
            } else if (ble != null && ble.isVisible()) {
                ble.setGroup(groupName);
                c.getSession().write(FriendResponse.updateBuddylist(c.getPlayer()));
            } else if (buddylist.isFull()) {
                c.getSession().write(FriendResponse.buddylistMessage(FriendResponse.FriendOps.FriendRes_SetFriend_FullMe));
            } else {
                try {
                    CharacterIdNameBuddyCapacity charWithId = null;
                    int channel = World.Find.findChannel(addName);
                    MapleCharacter otherChar = null;
                    if (channel > 0) {
                        otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(addName);
                        if (!otherChar.isGM() || c.getPlayer().isGM()) {
                            charWithId = new CharacterIdNameBuddyCapacity(otherChar.getId(), otherChar.getName(), otherChar.getLevel(), otherChar.getJob(), groupName, otherChar.getBuddylist().getCapacity());
                        }
                    } else {
                        charWithId = getCharacterIdAndNameFromDatabase(addName, groupName);
                    }
                    if (charWithId != null) {
                        BuddyList.BuddyAddResult buddyAddResult = null;
                        if (channel > 0) {
                            buddyAddResult = World.Buddy.requestBuddyAdd(addName, c.getChannel(), c.getPlayer().getId(), c.getPlayer().getName(), c.getPlayer().getLevel(), c.getPlayer().getJob());
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
                            c.getSession().write(FriendResponse.buddylistMessage(FriendResponse.FriendOps.FriendRes_SetFriend_FullOther));
                        } else {
                            int displayChannel = -1;
                            int otherCid = charWithId.getId();
                            if (buddyAddResult == BuddyList.BuddyAddResult.ALREADY_ON_LIST && channel > 0) {
                                displayChannel = channel;
                                notifyRemoteChannel(c, channel, otherCid, groupName, BuddyList.BuddyOperation.ADDED);
                            } else if (buddyAddResult != BuddyList.BuddyAddResult.ALREADY_ON_LIST && channel > 0) {
                                Connection con = DatabaseConnection.getConnection();
                                PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `groupname`, `pending`) VALUES (?, ?, ?, 1)");
                                ps.setInt(1, charWithId.getId());
                                ps.setInt(2, c.getPlayer().getId());
                                ps.setString(3, groupName);
                                ps.executeUpdate();
                                ps.close();
                            }
                            buddylist.put(new BuddylistEntry(charWithId.getName(), otherCid, groupName, displayChannel, true, charWithId.getLevel(), charWithId.getJob()));
                            c.getSession().write(FriendResponse.updateBuddylist(c.getPlayer()));
                        }
                    } else {
                        c.getSession().write(FriendResponse.buddylistMessage(FriendResponse.FriendOps.FriendRes_SetFriend_UnknownUser));
                    }
                } catch (SQLException e) {
                    System.err.println("SQL THROW" + e);
                }
            }
            nextPendingRequest(c);
        } else if (mode == 2) {
            // accept buddy
            int otherCid = slea.readInt();
            if (!buddylist.isFull()) {
                try {
                    final int channel = World.Find.findChannel(otherCid);
                    String otherName = null;
                    int otherLevel = 0;
                    int otherJob = 0;
                    if (channel < 0) {
                        Connection con = DatabaseConnection.getConnection();
                        PreparedStatement ps = con.prepareStatement("SELECT name, level, job FROM characters WHERE id = ?");
                        ps.setInt(1, otherCid);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            otherName = rs.getString("name");
                            otherLevel = rs.getInt("level");
                            otherJob = rs.getInt("job");
                        }
                        rs.close();
                        ps.close();
                    } else {
                        final MapleCharacter otherChar = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterById(otherCid);
                        otherName = otherChar.getName();
                        otherLevel = otherChar.getLevel();
                        otherJob = otherChar.getJob();
                    }
                    if (otherName != null) {
                        buddylist.put(new BuddylistEntry(otherName, otherCid, "ETC", channel, true, otherLevel, otherJob));
                        c.getSession().write(FriendResponse.updateBuddylist(c.getPlayer()));
                        notifyRemoteChannel(c, channel, otherCid, "ETC", BuddyList.BuddyOperation.ADDED);
                    }
                } catch (SQLException e) {
                    System.err.println("SQL THROW" + e);
                }
            } else {
                c.getSession().write(FriendResponse.buddylistMessage(FriendResponse.FriendOps.FriendRes_SetFriend_FullMe));
            }
            nextPendingRequest(c);
        } else if (mode == 3) {
            // delete
            final int otherCid = slea.readInt();
            final BuddylistEntry blz = buddylist.get(otherCid);
            if (blz != null && blz.isVisible()) {
                notifyRemoteChannel(c, World.Find.findChannel(otherCid), otherCid, blz.getGroup(), BuddyList.BuddyOperation.DELETED);
            }
            buddylist.remove(otherCid);
            c.getSession().write(FriendResponse.updateBuddylist(c.getPlayer()));
            nextPendingRequest(c);
        } else {
            System.out.println("Unknown buddylist: " + slea.toString());
        }
    }

    private static final void notifyRemoteChannel(final MapleClient c, final int remoteChannel, final int otherCid, final String group, final BuddyList.BuddyOperation operation) {
        final MapleCharacter player = c.getPlayer();
        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, player.getLevel(), player.getJob(), group);
        }
    }

}
