/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.handling.world.guild;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import tacos.config.Region;
import odin.handling.world.OdinWorld;
import odin.handling.world.guild.MapleBBSThread.MapleBBSReply;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import tacos.config.Config;
import tacos.database.query.DQ_BbsReplies;
import tacos.database.query.DQ_BbsThreads;
import tacos.database.query.DQ_Characters;
import tacos.database.query.DQ_Guilds;
import tacos.database.query.DQ_Notes;
import tacos.packet.ops.OpsChatGroup;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.packet.ServerPacket;

public class MapleGuild {

    private static enum BCOp {

        NONE, DISBAND, EMBELMCHANGE
    }
    private final List<MapleGuildCharacter> members = new CopyOnWriteArrayList<>();
    private final String rankTitles[] = new String[5]; // 1 = master, 2 = jr, 5 = lowest member
    private String name;
    private String notice;
    private int id;
    private int gp;
    private int logo;
    private int logoColor;
    private int leader;
    private int capacity;
    private int logoBG;
    private int logoBGColor;
    private int signature;
    private int allianceid = 0;
    private int invitedid = 0;
    private boolean bDirty = true;
    private boolean proper = true;
    private final Map<Integer, MapleBBSThread> bbs = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rL = lock.readLock();
    private final Lock wL = lock.writeLock();
    private boolean init = false;

    public MapleGuild(final int guildid) {
        super();

        DQ_Guilds.GuildRow row = DQ_Guilds.load(guildid);
        if (row == null) {
            id = -1;
            return;
        }
        id = guildid;
        name = row.name;
        gp = row.gp;
        logo = row.logo;
        logoColor = row.logoColor;
        logoBG = row.logoBG;
        logoBGColor = row.logoBGColor;
        capacity = row.capacity;
        rankTitles[0] = row.rankTitles[0];
        rankTitles[1] = row.rankTitles[1];
        rankTitles[2] = row.rankTitles[2];
        rankTitles[3] = row.rankTitles[3];
        rankTitles[4] = row.rankTitles[4];
        leader = row.leader;
        notice = row.notice;
        signature = row.signature;
        allianceid = row.alliance;

        List<DQ_Characters.GuildMemberRow> memberRows = DQ_Characters.getGuildMembers(guildid);
        if (memberRows.isEmpty()) {
            System.err.println("No members in guild " + id + ".  Impossible... guild is disbanding");
            writeToDB(true);
            proper = false;
            return;
        }
        boolean leaderCheck = false;
        for (DQ_Characters.GuildMemberRow m : memberRows) {
            if (m.id == leader) {
                leaderCheck = true;
            }
            members.add(new MapleGuildCharacter(m.id, m.level, m.name, (byte) -1, m.job, m.guildRank, m.allianceRank, guildid, false));
        }

        if (!leaderCheck) {
            System.err.println("Leader " + leader + " isn't in guild " + id + ".  Impossible... guild is disbanding.");
            writeToDB(true);
            proper = false;
            return;
        }

        for (final MapleBBSThread thread : DQ_BbsThreads.loadByGuildId(guildid)) {
            bbs.put(thread.localthreadID, thread);
        }
    }

    public boolean isProper() {
        return proper;
    }

    public static final Collection<MapleGuild> loadAll() {
        final Collection<MapleGuild> ret = new ArrayList<>();
        MapleGuild g;
        for (int guildid : DQ_Guilds.getAllGuildIds()) {
            g = new MapleGuild(guildid);
            if (g.getId() > 0) {
                ret.add(g);
            }
        }
        return ret;
    }

    public final void writeToDB(final boolean bDisband) {
        if (!bDisband) {
            DQ_Guilds.update(id, gp, logo, logoColor, logoBG, logoBGColor, rankTitles, capacity, notice, allianceid);
            DQ_BbsThreads.deleteByGuildId(id);
            DQ_BbsReplies.deleteByGuildId(id);
            DQ_BbsThreads.saveAll(id, bbs.values());
        } else {
            DQ_Characters.resetGuildForMembers(id);
            DQ_BbsThreads.deleteByGuildId(id);
            DQ_BbsReplies.deleteByGuildId(id);
            DQ_Guilds.delete(id);

            if (allianceid > 0) {
                final MapleGuildAlliance alliance = OdinWorld.Alliance.getAlliance(allianceid);
                if (alliance != null) {
                    alliance.removeGuild(id, false);
                }
            }

            broadcast(ResCWvsContext.guildDisband(id));
        }
    }

    public final int getId() {
        return id;
    }

    public final int getLeaderId() {
        return leader;
    }

    public final MapleCharacter getLeader(final TacosClient c) {
        return c.getChannelServer().getOnlinePlayers().findById(leader);
    }

    public final int getGP() {
        return gp;
    }

    public final int getLogo() {
        return logo;
    }

    public final int getLogoColor() {
        return logoColor;
    }

    public final int getLogoBG() {
        return logoBG;
    }

    public final int getLogoBGColor() {
        return logoBGColor;
    }

    public final String getNotice() {
        if (notice == null) {
            return "";
        }
        return notice;
    }

    public final String getName() {
        return name;
    }

    public final int getCapacity() {
        return capacity;
    }

    public void broadcast(ServerPacket packet) {
        broadcast(packet, -1, BCOp.NONE);
    }

    public void broadcast(ServerPacket packet, int exception) {
        broadcast(packet, exception, BCOp.NONE);
    }

    // multi-purpose function that reaches every member of guild (except the character with exceptionId) in all channels with as little access to rmi as possible
    public void broadcast(ServerPacket packet, int exceptionId, BCOp bcop) {
        wL.lock();
        try {
            buildNotifications();
        } finally {
            wL.unlock();
        }

        rL.lock();
        try {
            for (MapleGuildCharacter mgc : members) {
                if (bcop == BCOp.DISBAND) {
                    if (mgc.isOnline()) {
                        OdinWorld.Guild.setGuildAndRank(mgc.getId(), 0, 5, 5);
                    } else {
                        setOfflineGuildStatus(0, (byte) 5, (byte) 5, mgc.getId());
                    }
                } else if (mgc.isOnline() && mgc.getId() != exceptionId) {
                    if (bcop == BCOp.EMBELMCHANGE) {
                        OdinWorld.Guild.changeEmblem(id, mgc.getId(), new MapleGuildSummary(this));
                    } else {
                        OdinWorld.Broadcast.sendGuildPacket(mgc.getId(), packet, exceptionId, id);
                    }
                }
            }
        } finally {
            rL.unlock();
        }

    }

    private final void buildNotifications() {
        if (!bDirty) {
            return;
        }
        final List<Integer> mem = new LinkedList<>();
        final Iterator<MapleGuildCharacter> toRemove = members.iterator();
        while (toRemove.hasNext()) {
            MapleGuildCharacter mgc = toRemove.next();
            if (!mgc.isOnline()) {
                continue;
            }
            if (mem.contains(mgc.getId()) || mgc.getGuildId() != id) {
                members.remove(mgc);
                continue;
            }
            mem.add(mgc.getId());

        }
        bDirty = false;
    }

    public final void setOnline(final int cid, final boolean online, final int channel) {
        boolean bBroadcast = true;
        for (MapleGuildCharacter mgc : members) {
            if (mgc.getGuildId() == id && mgc.getId() == cid) {
                if (mgc.isOnline() == online) {
                    bBroadcast = false;
                }
                mgc.setOnline(online);
                mgc.setChannel((byte) channel);
                break;
            }
        }
        if (bBroadcast) {
            broadcast(ResCWvsContext.guildMemberOnline(id, cid, online), cid);
            if (allianceid > 0) {
                OdinWorld.Alliance.sendGuild(ResCWvsContext.allianceMemberOnline(allianceid, id, cid, online), id, allianceid);
            }
        }
        bDirty = true; // member formation has changed, update notifications
        init = true;
    }

    public final void guildChat(final String name, final int cid, final String msg) {
        broadcast(ResCField.GroupMessage(OpsChatGroup.CG_Guild, name, msg), cid);
    }

    public final void allianceChat(final String name, final int cid, final String msg) {
        broadcast(ResCField.GroupMessage(OpsChatGroup.CG_Alliance, name, msg), cid);
    }

    public final String getRankTitle(final int rank) {
        return rankTitles[rank - 1];
    }

    public int getAllianceId() {
        //return alliance.getId();
        return this.allianceid;
    }

    public int getInvitedId() {
        return this.invitedid;
    }

    public void setInvitedId(int iid) {
        this.invitedid = iid;
    }

    public void setAllianceId(int a) {
        this.allianceid = a;
        DQ_Guilds.updateAlliance(id, a);
    }

    // function to create guild, returns the guild id if successful, 0 if not
    public static final int createGuild(final int leaderId, final String name) {
        if (name.length() > 12) {
            return 0;
        }
        if (DQ_Guilds.findIdByName(name) != -1) {
            return 0;
        }
        return DQ_Guilds.create(leaderId, name, (int) (System.currentTimeMillis() / 1000));
    }

    public final int addGuildMember(final MapleGuildCharacter mgc) {
        // first of all, insert it into the members keeping alphabetical order of lowest ranks ;)
        wL.lock();
        try {
            if (members.size() >= capacity) {
                return 0;
            }
            for (int i = members.size() - 1; i >= 0; i--) {
                if (members.get(i).getGuildRank() < 5 || members.get(i).getName().compareTo(mgc.getName()) < 0) {
                    members.add(i + 1, mgc);
                    bDirty = true;
                    break;
                }
            }
        } finally {
            wL.unlock();
        }
        gainGP(50);
        broadcast(ResCWvsContext.newGuildMember(mgc));
        if (allianceid > 0) {
            OdinWorld.Alliance.sendGuild(allianceid);
        }
        return 1;
    }

    public final void leaveGuild(final MapleGuildCharacter mgc) {
        broadcast(ResCWvsContext.memberLeft(mgc, false));
        gainGP(-50);
        wL.lock();
        try {
            bDirty = true;
            members.remove(mgc);
            if (mgc.isOnline()) {
                OdinWorld.Guild.setGuildAndRank(mgc.getId(), 0, 5, 5);
            } else {
                setOfflineGuildStatus((short) 0, (byte) 5, (byte) 5, mgc.getId());
            }
            if (allianceid > 0) {
                OdinWorld.Alliance.sendGuild(allianceid);
            }
        } finally {
            wL.unlock();
        }
    }

    public final void expelMember(final MapleGuildCharacter initiator, final String name, final int cid) {
        wL.lock();
        try {
            final Iterator<MapleGuildCharacter> itr = members.iterator();
            while (itr.hasNext()) {
                final MapleGuildCharacter mgc = itr.next();

                if (mgc.getId() == cid && initiator.getGuildRank() < mgc.getGuildRank()) {
                    broadcast(ResCWvsContext.memberLeft(mgc, true));

                    bDirty = true;

                    gainGP(-50);
                    if (allianceid > 0) {
                        OdinWorld.Alliance.sendGuild(allianceid);
                    }
                    if (mgc.isOnline()) {
                        OdinWorld.Guild.setGuildAndRank(cid, 0, 5, 5);
                    } else {
                        DQ_Notes.sendNote(mgc.getName(), initiator.getName(), "You have been expelled from the guild.", 0);
                        setOfflineGuildStatus((short) 0, (byte) 5, (byte) 5, cid);
                    }
                    members.remove(mgc);
                    break;
                }
            }
        } finally {
            wL.unlock();
        }
    }

    public final void changeARank() {
        changeARank(false);
    }

    public final void changeARank(final boolean leader) {
        for (final MapleGuildCharacter mgc : members) {
            if (this.leader == mgc.getId()) {
                changeARank(mgc.getId(), leader ? 1 : 2);
            } else {
                changeARank(mgc.getId(), 3);
            }
        }
    }

    public final void changeARank(final int newRank) {
        for (final MapleGuildCharacter mgc : members) {
            changeARank(mgc.getId(), newRank);
        }
    }

    public final void changeARank(final int cid, final int newRank) {
        if (allianceid <= 0) {
            return;
        }
        for (final MapleGuildCharacter mgc : members) {
            if (cid == mgc.getId()) {
                if (mgc.isOnline()) {
                    OdinWorld.Guild.setGuildAndRank(cid, this.id, mgc.getGuildRank(), newRank);
                } else {
                    setOfflineGuildStatus((short) this.id, (byte) mgc.getGuildRank(), (byte) newRank, cid);
                }
                mgc.setAllianceRank((byte) newRank);
                //WorldRegistryImpl.getInstance().sendGuild(MaplePacketCreator.changeAllianceRank(allianceid, mgc), -1, allianceid);
                //WorldRegistryImpl.getInstance().sendGuild(MaplePacketCreator.updateAllianceRank(allianceid, mgc), -1, allianceid);
                OdinWorld.Alliance.sendGuild(allianceid);
                return;
            }
        }
        // it should never get to this point unless cid was incorrect o_O
        System.err.println("INFO: unable to find the correct id for changeRank({" + cid + "}, {" + newRank + "})");
    }

    public final void changeRank(final int cid, final int newRank) {
        for (final MapleGuildCharacter mgc : members) {
            if (cid == mgc.getId()) {
                if (mgc.isOnline()) {
                    OdinWorld.Guild.setGuildAndRank(cid, this.id, newRank, mgc.getAllianceRank());
                } else {
                    setOfflineGuildStatus((short) this.id, (byte) newRank, (byte) mgc.getAllianceRank(), cid);
                }
                mgc.setGuildRank((byte) newRank);
                broadcast(ResCWvsContext.changeRank(mgc));
                return;
            }
        }
        // it should never get to this point unless cid was incorrect o_O
        System.err.println("INFO: unable to find the correct id for changeRank({" + cid + "}, {" + newRank + "})");
    }

    public final void setGuildNotice(final String notice) {
        this.notice = notice;
        broadcast(ResCWvsContext.guildNotice(id, notice));
    }

    public final void memberLevelJobUpdate(final MapleGuildCharacter mgc) {
        for (final MapleGuildCharacter member : members) {
            if (member.getId() == mgc.getId()) {
                int old_level = member.getLevel();
                int old_job = member.getJobId();
                member.setJobId(mgc.getJobId());
                member.setLevel((short) mgc.getLevel());
                if (mgc.getLevel() > old_level) {
                    gainGP((mgc.getLevel() - old_level) * mgc.getLevel() / 10, false); //level 199->200 = 20 gp
                }
                if (old_level != mgc.getLevel()) {
                    this.broadcast(ResCWvsContext.NotifyLevelUp(false, mgc.getLevel(), mgc.getName()), mgc.getId());
                }
                if (old_job != mgc.getJobId()) {
                    this.broadcast(ResCWvsContext.NotifyJobChange(false, mgc.getJobId(), mgc.getName()), mgc.getId());
                }
                broadcast(ResCWvsContext.guildMemberLevelJobUpdate(mgc));
                if (allianceid > 0) {
                    OdinWorld.Alliance.sendGuild(ResCWvsContext.updateAlliance(mgc, allianceid), id, allianceid);
                }
                break;
            }
        }
    }

    public final void changeRankTitle(final String[] ranks) {
        for (int i = 0; i < 5; i++) {
            rankTitles[i] = ranks[i];
        }
        broadcast(ResCWvsContext.rankTitleChange(id, ranks));
    }

    public final void disbandGuild() {
        writeToDB(true);
        broadcast(null, -1, BCOp.DISBAND);
    }

    public final void setGuildEmblem(final short bg, final byte bgcolor, final short logo, final byte logocolor) {
        this.logoBG = bg;
        this.logoBGColor = bgcolor;
        this.logo = logo;
        this.logoColor = logocolor;
        broadcast(null, -1, BCOp.EMBELMCHANGE);

        DQ_Guilds.updateEmblem(id, logo, logoColor, logoBG, logoBGColor);
    }

    public final MapleGuildCharacter getMGC(final int cid) {
        for (final MapleGuildCharacter mgc : members) {
            if (mgc.getId() == cid) {
                return mgc;
            }
        }
        return null;
    }

    public final boolean increaseCapacity() {
        if (capacity >= 100 || ((capacity + 5) > 100)) {
            return false;
        }
        capacity += 5;
        broadcast(ResCWvsContext.guildCapacityChange(this.id, this.capacity));

        DQ_Guilds.updateCapacity(id, capacity);
        return true;
    }

    public final void gainGP(final int amount) {
        gainGP(amount, true);
    }

    public final void gainGP(int amount, final boolean broadcast) {
        if (amount == 0) { //no change, no broadcast and no sql.
            return;
        }
        if (amount + gp < 0) {
            amount = -gp;
        } //0 lowest
        gp += amount;
        broadcast(ResCWvsContext.updateGP(id, gp));
        if (broadcast) {
            broadcast(ResWrapper.getGPMsg(amount));
        }
    }

    public final byte[] addMemberData() {
        ServerPacket data = new ServerPacket();

        data.Encode1(members.size());

        for (final MapleGuildCharacter mgc : members) {
            data.Encode4(mgc.getId());
        }
        for (final MapleGuildCharacter mgc : members) {
            data.EncodeBuffer(mgc.getName(), 13);
            data.Encode4(mgc.getJobId());
            data.Encode4(mgc.getLevel());
            data.Encode4(mgc.getGuildRank());
            data.Encode4(mgc.isOnline() ? 1 : 0);
            data.Encode4(signature);

            if (Config.GreaterOrEqual(Region.JMS, 164)) {
                data.Encode4(mgc.getAllianceRank());
            }
        }
        return data.getBytes();
    }

    // null indicates successful invitation being sent
    // keep in mind that this will be called by a handler most of the time
    // so this will be running mostly on a channel server, unlike the rest
    // of the class
    public static final MapleGuildResponse sendInvite(final TacosClient client, final String targetName) {
        final MapleCharacter mc = client.getChannelServer().getOnlinePlayers().findByName(targetName);
        if (mc == null) {
            return MapleGuildResponse.NOT_IN_CHANNEL;
        }
        if (mc.getGuildId() > 0) {
            return MapleGuildResponse.ALREADY_IN_GUILD;
        }
        mc.SendPacket(ResCWvsContext.guildInvite(client.getPlayer().getGuildId(), client.getPlayer().getName(), client.getPlayer().getLevel(), client.getPlayer().getJob()));
        return null;
    }

    public java.util.Collection<MapleGuildCharacter> getMembers() {
        return java.util.Collections.unmodifiableCollection(members);
    }

    public final List<MapleBBSThread> getBBS() {
        final List<MapleBBSThread> ret = new ArrayList<>(bbs.values());
        Collections.sort(ret, new MapleBBSThread.ThreadComparator());
        return ret;
    }

    public final int addBBSThread(final String title, final String text, final int icon, final boolean bNotice, final int posterID) {
        final int add = bbs.get(0) == null ? 1 : 0; //add 1 if no notice
        final int ret = bNotice ? 0 : Math.max(1, bbs.size() + add);
        bbs.put(ret, new MapleBBSThread(ret, title, text, System.currentTimeMillis(), this.id, posterID, icon));
        return ret;
    }

    public final void editBBSThread(final int localthreadid, final String title, final String text, final int icon, final int posterID, final int guildRank) {
        final MapleBBSThread thread = bbs.get(localthreadid);
        if (thread != null && (thread.ownerID == posterID || guildRank <= 2)) {
            bbs.put(localthreadid, new MapleBBSThread(localthreadid, title, text, System.currentTimeMillis(), this.id, thread.ownerID, icon));
        }
    }

    public final void deleteBBSThread(final int localthreadid, final int posterID, final int guildRank) {
        final MapleBBSThread thread = bbs.get(localthreadid);
        if (thread != null && (thread.ownerID == posterID || guildRank <= 2)) {
            bbs.remove(localthreadid);
        }
    }

    public final void addBBSReply(final int localthreadid, final String text, final int posterID) {
        final MapleBBSThread thread = bbs.get(localthreadid);
        if (thread != null) {
            thread.replies.put(thread.replies.size(), new MapleBBSReply(thread.replies.size(), posterID, text, System.currentTimeMillis()));
        }
    }

    public final void deleteBBSReply(final int localthreadid, final int replyid, final int posterID, final int guildRank) {
        final MapleBBSThread thread = bbs.get(localthreadid);
        if (thread != null) {
            final MapleBBSReply reply = thread.replies.get(replyid);
            if (reply != null && (reply.ownerID == posterID || guildRank <= 2)) {
                thread.replies.remove(replyid);
            }
        }
    }

    public static void setOfflineGuildStatus(int guildid, int guildrank, int alliancerank, int cid) {
        DQ_Characters.setOfflineGuildStatus(cid, guildid, guildrank, alliancerank);
    }
}
