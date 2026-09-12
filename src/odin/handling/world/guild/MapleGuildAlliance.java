/*
This file is part of the ZeroFusion MapleStory Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>
ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

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

import tacos.database.query.DQ_Alliances;
import tacos.database.query.DQ_Characters;
import odin.handling.world.OdinWorld;
import java.util.ArrayList;
import java.util.Collection;
import tacos.packet.ServerPacket;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.wrapper.ResWrapper;

public class MapleGuildAlliance implements java.io.Serializable {

    private static enum GAOp {

        NONE, DISBAND, NEWGUILD
    }
    public static final long serialVersionUID = 24081985245L;
    public static final int CHANGE_CAPACITY_COST = 10000000;
    private final int[] guilds = new int[5];
    private int allianceid, leaderid, capacity; //make SQL for this auto-increment
    private String name, notice;
    private String ranks[] = new String[5];

    public MapleGuildAlliance(final int id) {
        super();

        DQ_Alliances.AllianceRow row = DQ_Alliances.load(id);
        if (row == null) {
            allianceid = -1;
            return;
        }
        allianceid = id;
        name = row.name;
        capacity = row.capacity;
        for (int i = 0; i < 5; i++) {
            guilds[i] = row.guilds[i];
            ranks[i] = row.ranks[i];
        }
        leaderid = row.leaderid;
        notice = row.notice;
    }

    public static final Collection<MapleGuildAlliance> loadAll() {
        final Collection<MapleGuildAlliance> ret = new ArrayList<MapleGuildAlliance>();
        MapleGuildAlliance g;
        for (int id : DQ_Alliances.getAllAllianceIds()) {
            g = new MapleGuildAlliance(id);
            if (g.getId() > 0) {
                ret.add(g);
            }
        }
        return ret;
    }

    public int getNoGuilds() {
        int ret = 0;
        for (int i = 0; i < capacity; i++) {
            if (guilds[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    public static final int createToDb(final int leaderId, final String name, final int guild1, final int guild2) {
        if (name.length() > 12) {
            return -1;
        }
        return DQ_Alliances.create(leaderId, name, guild1, guild2);
    }

    public final boolean deleteAlliance() {
        for (int i = 0; i < getNoGuilds(); i++) {
            DQ_Characters.resetAllianceRankForGuild(guilds[i]);
        }

        return DQ_Alliances.delete(allianceid);
    }

    public  void broadcast( ServerPacket packet) {
        broadcast(packet, -1, GAOp.NONE, false);
    }

    public  void broadcast( ServerPacket packet,  int exception) {
        broadcast(packet, exception, GAOp.NONE, false);
    }

    public  void broadcast( ServerPacket packet,  int exceptionId,  GAOp op,  boolean expelled) {
        if (op == GAOp.DISBAND) {
            OdinWorld.Alliance.setOldAlliance(exceptionId, expelled, allianceid); //-1 = alliance gone, exceptionId = guild left/expelled
        } else if (op == GAOp.NEWGUILD) {
            OdinWorld.Alliance.setNewAlliance(exceptionId, allianceid); //exceptionId = guild that just joined
        } else {
            OdinWorld.Alliance.sendGuild(packet, exceptionId, allianceid); //exceptionId = guild to broadcast to only
        }

    }

    public final boolean disband() {
        final boolean ret = deleteAlliance();
        if (ret) {
            broadcast(null, -1, GAOp.DISBAND, false);
        }
        return ret;
    }

    public final void saveToDb() {
        DQ_Alliances.update(allianceid, guilds, ranks, capacity, leaderid, notice);
    }

    public void setRank(String[] ranks) {
        this.ranks = ranks;
        broadcast(ResCWvsContext.getAllianceUpdate(this));
        saveToDb();
    }

    public String getRank(int rank) {
        return ranks[rank - 1];
    }

    public String[] getRanks() {
        return ranks;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String newNotice) {
        this.notice = newNotice;
        broadcast(ResCWvsContext.getAllianceUpdate(this));
        broadcast(ResWrapper.BroadCastMsgEvent("Alliance Notice : " + newNotice));
        saveToDb();
    }

    public int getGuildId(int i) {
        return guilds[i];
    }

    public int getId() {
        return allianceid;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public boolean setCapacity() {
        if (capacity >= 5) {
            return false;
        }
        this.capacity += 1;
        broadcast(ResCWvsContext.getAllianceUpdate(this));
        saveToDb();
        return true;
    }

    public boolean addGuild(final int guildid) {
        if (getNoGuilds() >= getCapacity()) {
            return false;
        }
        guilds[getNoGuilds()] = guildid;
        saveToDb();
        broadcast(null, guildid, GAOp.NEWGUILD, false);
        return true;
    }

    public boolean removeGuild(final int guildid, final boolean expelled) {
        for (int i = 0; i < getNoGuilds(); i++) {
            if (guilds[i] == guildid) {
                broadcast(null, guildid, GAOp.DISBAND, expelled);
                if (i > 0 && i != getNoGuilds() - 1) { // if guild isnt the last guild.. damnit
                    for (int x = i + 1; x < getNoGuilds(); x++) {
                        if (guilds[x] > 0) {
                            guilds[x - 1] = guilds[x];
                            if (x == getNoGuilds() - 1) {
                                guilds[x] = -1;
                            }
                        }
                    }
                } else {
                    guilds[i] = -1;
                }
                if (i == 0) { //leader guild.. FUCK THIS ALLIANCE! xD
                    return disband();
                } else {
                    broadcast(ResCWvsContext.getAllianceUpdate(this));
                    broadcast(ResCWvsContext.getGuildAlliance(this));
                    saveToDb();
                    return true;
                }
            }
        }
        return false;
    }

    public int getLeaderId() {
        return leaderid;
    }

    public boolean setLeaderId(final int c) {
        if (leaderid == c) {
            return false;
        }
        //re-arrange the guilds so guild1 is always the leader guild
        int g = -1; //this shall be leader
        String leaderName = null;
        for (int i = 0; i < getNoGuilds(); i++) {
            MapleGuild g_ = OdinWorld.Guild.getGuild(guilds[i]);
            if (g_ != null) {
                MapleGuildCharacter newLead = g_.getMGC(c);
                MapleGuildCharacter oldLead = g_.getMGC(leaderid);
                if (newLead != null && oldLead != null) { //same guild
                    return false;
                } else if (newLead != null && newLead.getGuildRank() == 1 && newLead.getAllianceRank() == 2) { //guild1 should always be leader so no worries about g being -1
                    g_.changeARank(c, 1);
                    g = i;
                    leaderName = newLead.getName();
                } else if (oldLead != null && oldLead.getGuildRank() == 1 && oldLead.getAllianceRank() == 1) {
                    g_.changeARank(leaderid, 2);
                } else if (oldLead != null || newLead != null) {
                    return false;
                }
            }
        }
        if (g == -1) {
            return false; //nothing was done
        }
        final int oldGuild = guilds[g];
        guilds[g] = guilds[0];
        guilds[0] = oldGuild;
        if (leaderName != null) {
            broadcast(ResWrapper.BroadCastMsgEvent(leaderName + " has become the leader of the alliance."));
        }
        broadcast(ResCWvsContext.changeAllianceLeader(allianceid, leaderid, c));
        broadcast(ResCWvsContext.updateAllianceLeader(allianceid, leaderid, c));
        broadcast(ResCWvsContext.getAllianceUpdate(this));
        broadcast(ResCWvsContext.getGuildAlliance(this));
        this.leaderid = c;
        saveToDb();
        return true;
    }

    public boolean changeAllianceRank(final int cid, final int change) {
        if (leaderid == cid || change < 0 || change > 1) {
            return false;
        }
        for (int i = 0; i < getNoGuilds(); i++) {
            MapleGuild g_ = OdinWorld.Guild.getGuild(guilds[i]);
            if (g_ != null) {
                MapleGuildCharacter chr = g_.getMGC(cid);
                if (chr != null && chr.getAllianceRank() > 2) {
                    if ((change == 0 && chr.getAllianceRank() >= 5) || (change == 1 && chr.getAllianceRank() <= 3)) {
                        return false;
                    }
                    g_.changeARank(cid, chr.getAllianceRank() + (change == 0 ? 1 : -1));
                    return true;
                }
            }
        }
        return false;
    }
}
