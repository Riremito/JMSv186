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
import tacos.client.TacosCharacter;
import java.util.Map;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import tacos.client.TacosClient;
import tacos.server.TacosWorld;
import tacos.config.Region;
import odin.handling.world.guild.MapleBBSThread.MapleBBSReply;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import tacos.config.Config;
import tacos.database.query.DQ_BbsReplies;
import tacos.database.query.DQ_BbsThreads;
import tacos.database.query.DQ_Characters;
import tacos.database.query.DQ_Guilds;
import tacos.database.query.DQ_Notes;
import tacos.packet.ops.OpsChatGroup;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ops.OpsMessage;
import tacos.packet.response.builder.PB_Message;
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
    private boolean init = false;

    public MapleGuild(int guildid) {
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

        for (MapleBBSThread thread : DQ_BbsThreads.loadByGuildId(guildid)) {
            bbs.put(thread.localthreadID, thread);
        }
    }

    public boolean isProper() {
        return proper;
    }

    public static Collection<MapleGuild> loadAll() {
        Collection<MapleGuild> ret = new ArrayList<>();
        MapleGuild g;
        for (int guildid : DQ_Guilds.getAllGuildIds()) {
            g = new MapleGuild(guildid);
            if (g.getId() > 0) {
                ret.add(g);
            }
        }
        return ret;
    }

    public void writeToDB(boolean bDisband) {
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
                MapleGuildAlliance alliance = TacosWorld.find(0).getAlliance().getAlliance(allianceid);
                if (alliance != null) {
                    alliance.removeGuild(id, false);
                }
            }

            broadcast(ResCWvsContext.guildDisband(id));
        }
    }

    public int getLeaderId() {
        return leader;
    }

    public TacosCharacter getLeader(TacosClient client) {
        return client.getChannelServer().getOnlinePlayers().findById(leader);
    }

    public int getLogo() {
        return logo;
    }

    public int getLogoColor() {
        return logoColor;
    }

    public int getLogoBG() {
        return logoBG;
    }

    public int getLogoBGColor() {
        return logoBGColor;
    }

    public String getNotice() {
        if (notice == null) {
            return "";
        }
        return notice;
    }

    public int getCapacity() {
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
        buildNotifications();

        for (MapleGuildCharacter mgc : members) {
            if (bcop == BCOp.DISBAND) {
                if (mgc.isOnline()) {
                    TacosWorld.find(0).getGuild().setGuildAndRank(mgc.getId(), 0, 5, 5);
                } else {
                    setOfflineGuildStatus(0, (byte) 5, (byte) 5, mgc.getId());
                }
            } else if (mgc.isOnline() && mgc.getId() != exceptionId) {
                if (bcop == BCOp.EMBELMCHANGE) {
                    TacosWorld.find(0).getGuild().changeEmblem(id, mgc.getId(), new MapleGuildSummary(this));
                } else {
                    TacosWorld.find(0).getGuild().sendGuildPacket(mgc.getId(), packet, exceptionId, id);
                }
            }
        }

    }

    private void buildNotifications() {
        if (!bDirty) {
            return;
        }
        List<Integer> mem = new LinkedList<>();
        Iterator<MapleGuildCharacter> toRemove = members.iterator();
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

    public void setOnline(int cid, boolean online, int channel) {
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
                TacosWorld.find(0).getAlliance().sendGuild(ResCWvsContext.allianceMemberOnline(allianceid, id, cid, online), id, allianceid);
            }
        }
        bDirty = true; // member formation has changed, update notifications
        init = true;
    }

    public void guildChat(String name, int cid, String msg) {
        broadcast(ResCField.GroupMessage(OpsChatGroup.CG_Guild, name, msg), cid);
    }

    public void allianceChat(String name, int cid, String msg) {
        broadcast(ResCField.GroupMessage(OpsChatGroup.CG_Alliance, name, msg), cid);
    }

    public String getRankTitle(int rank) {
        return rankTitles[rank - 1];
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
    public static int createGuild(int leaderId, String name) {
        if (name.length() > 12) {
            return 0;
        }
        if (DQ_Guilds.findIdByName(name) != -1) {
            return 0;
        }
        return DQ_Guilds.create(leaderId, name, (int) (System.currentTimeMillis() / 1000));
    }

    public int addGuildMember(MapleGuildCharacter mgc) {
        // first of all, insert it into the members keeping alphabetical order of lowest ranks ;)
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
        gainGP(50);
        broadcast(ResCWvsContext.newGuildMember(mgc));
        if (allianceid > 0) {
            TacosWorld.find(0).getAlliance().sendGuild(allianceid);
        }
        return 1;
    }

    public void leaveGuild(MapleGuildCharacter mgc) {
        broadcast(ResCWvsContext.memberLeft(mgc, false));
        gainGP(-50);
        bDirty = true;
        members.remove(mgc);
        if (mgc.isOnline()) {
            TacosWorld.find(0).getGuild().setGuildAndRank(mgc.getId(), 0, 5, 5);
        } else {
            setOfflineGuildStatus((short) 0, (byte) 5, (byte) 5, mgc.getId());
        }
        if (allianceid > 0) {
            TacosWorld.find(0).getAlliance().sendGuild(allianceid);
        }
    }

    public void expelMember(MapleGuildCharacter initiator, String name, int cid) {
        Iterator<MapleGuildCharacter> itr = members.iterator();
        while (itr.hasNext()) {
            MapleGuildCharacter mgc = itr.next();

            if (mgc.getId() == cid && initiator.getGuildRank() < mgc.getGuildRank()) {
                broadcast(ResCWvsContext.memberLeft(mgc, true));

                bDirty = true;

                gainGP(-50);
                if (allianceid > 0) {
                    TacosWorld.find(0).getAlliance().sendGuild(allianceid);
                }
                if (mgc.isOnline()) {
                    TacosWorld.find(0).getGuild().setGuildAndRank(cid, 0, 5, 5);
                } else {
                    DQ_Notes.sendNote(mgc.getName(), initiator.getName(), "You have been expelled from the guild.", 0);
                    setOfflineGuildStatus((short) 0, (byte) 5, (byte) 5, cid);
                }
                members.remove(mgc);
                break;
            }
        }
    }

    public void changeARank() {
        changeARank(false);
    }

    public void changeARank(boolean leader) {
        for (MapleGuildCharacter mgc : members) {
            if (this.leader == mgc.getId()) {
                changeARank(mgc.getId(), leader ? 1 : 2);
            } else {
                changeARank(mgc.getId(), 3);
            }
        }
    }

    public void changeARank(int newRank) {
        for (MapleGuildCharacter mgc : members) {
            changeARank(mgc.getId(), newRank);
        }
    }

    public void changeARank(int cid, int newRank) {
        if (allianceid <= 0) {
            return;
        }
        for (MapleGuildCharacter mgc : members) {
            if (cid == mgc.getId()) {
                if (mgc.isOnline()) {
                    TacosWorld.find(0).getGuild().setGuildAndRank(cid, this.id, mgc.getGuildRank(), newRank);
                } else {
                    setOfflineGuildStatus((short) this.id, (byte) mgc.getGuildRank(), (byte) newRank, cid);
                }
                mgc.setAllianceRank((byte) newRank);
                //WorldRegistryImpl.getInstance().sendGuild(MaplePacketCreator.changeAllianceRank(allianceid, mgc), -1, allianceid);
                //WorldRegistryImpl.getInstance().sendGuild(MaplePacketCreator.updateAllianceRank(allianceid, mgc), -1, allianceid);
                TacosWorld.find(0).getAlliance().sendGuild(allianceid);
                return;
            }
        }
        // it should never get to this point unless cid was incorrect o_O
        System.err.println("INFO: unable to find the correct id for changeRank({" + cid + "}, {" + newRank + "})");
    }

    public void changeRank(int cid, int newRank) {
        for (MapleGuildCharacter mgc : members) {
            if (cid == mgc.getId()) {
                if (mgc.isOnline()) {
                    TacosWorld.find(0).getGuild().setGuildAndRank(cid, this.id, newRank, mgc.getAllianceRank());
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

    public void setGuildNotice(String notice) {
        this.notice = notice;
        broadcast(ResCWvsContext.guildNotice(id, notice));
    }

    public void memberLevelJobUpdate(MapleGuildCharacter mgc) {
        for (MapleGuildCharacter member : members) {
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
                    TacosWorld.find(0).getAlliance().sendGuild(ResCWvsContext.updateAlliance(mgc, allianceid), id, allianceid);
                }
                break;
            }
        }
    }

    public void changeRankTitle(String[] ranks) {
        for (int i = 0; i < 5; i++) {
            rankTitles[i] = ranks[i];
        }
        broadcast(ResCWvsContext.rankTitleChange(id, ranks));
    }

    public void setGuildEmblem(short bg, byte bgcolor, short logo, byte logocolor) {
        this.logoBG = bg;
        this.logoBGColor = bgcolor;
        this.logo = logo;
        this.logoColor = logocolor;
        broadcast(null, -1, BCOp.EMBELMCHANGE);

        DQ_Guilds.updateEmblem(id, logo, logoColor, logoBG, logoBGColor);
    }

    public MapleGuildCharacter getMGC(int cid) {
        for (MapleGuildCharacter mgc : members) {
            if (mgc.getId() == cid) {
                return mgc;
            }
        }
        return null;
    }

    public boolean increaseCapacity() {
        if (capacity >= 100 || ((capacity + 5) > 100)) {
            return false;
        }
        capacity += 5;
        broadcast(ResCWvsContext.guildCapacityChange(this.id, this.capacity));

        DQ_Guilds.updateCapacity(id, capacity);
        return true;
    }

    public void gainGP(int amount, boolean broadcast) {
        if (amount == 0) { //no change, no broadcast and no sql.
            return;
        }
        if (amount + gp < 0) {
            amount = -gp;
        } //0 lowest
        gp += amount;
        broadcast(ResCWvsContext.updateGP(id, gp));
        if (broadcast) {
            broadcast(ResCWvsContext.Message(OpsMessage.MS_IncGPMessage, PB_Message.builder().Inc_GP(amount).build()));
        }
    }

    public byte[] addMemberData() {
        ServerPacket data = new ServerPacket();

        data.Encode1(members.size());

        for (MapleGuildCharacter mgc : members) {
            data.Encode4(mgc.getId());
        }
        for (MapleGuildCharacter mgc : members) {
            data.EncodeBuffer(mgc.getName(), 13);
            data.Encode4(mgc.getJobId());
            data.Encode4(mgc.getLevel());
            data.Encode4(mgc.getGuildRank());
            data.Encode4(mgc.isOnline() ? 1 : 0);
            data.Encode4(signature);
            data.Encode4(mgc.getAllianceRank(), Config.GreaterOrEqual(Region.JMS, 164));
        }
        return data.getBytes();
    }

    // null indicates successful invitation being sent
    // keep in mind that this will be called by a handler most of the time
    // so this will be running mostly on a channel server, unlike the rest
    // of the class
    public static MapleGuildResponse sendInvite(TacosCharacter chr, String targetName) {
        TacosCharacter mc = chr.getChannelServer().getOnlinePlayers().findByName(targetName);
        if (mc == null) {
            return MapleGuildResponse.NOT_IN_CHANNEL;
        }
        if (mc.getGuildId() > 0) {
            return MapleGuildResponse.ALREADY_IN_GUILD;
        }
        mc.SendPacket(ResCWvsContext.guildInvite(chr.getGuildId(), chr.getName(), chr.getLevel(), chr.getJob()));
        return null;
    }

    public List<MapleBBSThread> getBBS() {
        List<MapleBBSThread> ret = new ArrayList<>(bbs.values());
        Collections.sort(ret, new MapleBBSThread.ThreadComparator());
        return ret;
    }

    public int addBBSThread(String title, String text, int icon, boolean bNotice, int posterID) {
        int add = bbs.get(0) == null ? 1 : 0; //add 1 if no notice
        int ret = bNotice ? 0 : Math.max(1, bbs.size() + add);
        bbs.put(ret, new MapleBBSThread(ret, title, text, System.currentTimeMillis(), this.id, posterID, icon));
        return ret;
    }

    public void editBBSThread(int localthreadid, String title, String text, int icon, int posterID, int guildRank) {
        MapleBBSThread thread = bbs.get(localthreadid);
        if (thread != null && (thread.ownerID == posterID || guildRank <= 2)) {
            bbs.put(localthreadid, new MapleBBSThread(localthreadid, title, text, System.currentTimeMillis(), this.id, thread.ownerID, icon));
        }
    }

    public void deleteBBSThread(int localthreadid, int posterID, int guildRank) {
        MapleBBSThread thread = bbs.get(localthreadid);
        if (thread != null && (thread.ownerID == posterID || guildRank <= 2)) {
            bbs.remove(localthreadid);
        }
    }

    public void addBBSReply(int localthreadid, String text, int posterID) {
        MapleBBSThread thread = bbs.get(localthreadid);
        if (thread != null) {
            thread.replies.put(thread.replies.size(), new MapleBBSReply(thread.replies.size(), posterID, text, System.currentTimeMillis()));
        }
    }

    public void deleteBBSReply(int localthreadid, int replyid, int posterID, int guildRank) {
        MapleBBSThread thread = bbs.get(localthreadid);
        if (thread != null) {
            MapleBBSReply reply = thread.replies.get(replyid);
            if (reply != null && (reply.ownerID == posterID || guildRank <= 2)) {
                thread.replies.remove(replyid);
            }
        }
    }

    public static void setOfflineGuildStatus(int guildid, int guildrank, int alliancerank, int cid) {
        DQ_Characters.setOfflineGuildStatus(cid, guildid, guildrank, alliancerank);
    }

    // used by script
    public int getId() {
        return id;
    }

    // used by script
    public int getGP() {
        return gp;
    }

    // used by script
    public String getName() {
        return name;
    }

    // used by script
    public int getAllianceId() {
        //return alliance.getId();
        return this.allianceid;
    }

    // used by script
    public void disbandGuild() {
        writeToDB(true);
        broadcast(null, -1, BCOp.DISBAND);
    }

    // used by script
    public void gainGP(int amount) {
        gainGP(amount, true);
    }

    // used by script
    public java.util.Collection<MapleGuildCharacter> getMembers() {
        return java.util.Collections.unmodifiableCollection(members);
    }


    public enum MapleGuildResponse {

        NOT_IN_CHANNEL(0x2a),
        ALREADY_IN_GUILD(0x28),
        NOT_IN_GUILD(0x2d);
        private int value;

        private MapleGuildResponse(int val) {
            value = val;
        }

        public int getValue() {
            return value;
        }

        // used by script
        public ServerPacket getPacket() {
            return ResCWvsContext.genericGuildMessage((byte) value);
        }

    }

    public static class MapleGuildSummary {

        private String name;
        private short logoBG;
        private short logo;
        private byte logoBGColor;
        private byte logoColor;
        private int allianceid;

        public MapleGuildSummary(MapleGuild g) {
            name = g.getName();
            logoBG = (short) g.getLogoBG();
            logoBGColor = (byte) g.getLogoBGColor();
            logo = (short) g.getLogo();
            logoColor = (byte) g.getLogoColor();
            allianceid = g.getAllianceId();
        }

        public String getName() {
            return name;
        }

        public short getLogoBG() {
            return logoBG;
        }

        public byte getLogoBGColor() {
            return logoBGColor;
        }

        public short getLogo() {
            return logo;
        }

        public byte getLogoColor() {
            return logoColor;
        }

        public int getAllianceId() {
            return allianceid;
        }
    }
}
