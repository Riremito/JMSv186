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
package odin.handling.world;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import odin.client.MapleCharacter;
import odin.handling.world.guild.MapleBBSThread;
import odin.handling.world.guild.MapleGuild;
import odin.handling.world.guild.MapleGuildCharacter;
import odin.handling.world.guild.MapleGuildSummary;
import tacos.packet.ServerPacket;
import tacos.packet.response.ResCWvsContext;
import tacos.server.TacosWorld;

public class Guild {


    private static final Map<Integer, MapleGuild> guilds = new LinkedHashMap<>();
    static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    static {
        //System.out.println("[MapleGuild] Loading Guilds");
        Collection<MapleGuild> allGuilds = MapleGuild.loadAll();
        for (MapleGuild g : allGuilds) {
            if (g.isProper()) {
                guilds.put(g.getId(), g);
            }
        }
    }

    public static int createGuild(int leaderId, String name) {
        return MapleGuild.createGuild(leaderId, name);
    }

    public static MapleGuild getGuild(int id) {
        MapleGuild ret = null;
        lock.readLock().lock();
        try {
            ret = guilds.get(id);
        } finally {
            lock.readLock().unlock();
        }
        if (ret == null) {
            lock.writeLock().lock();
            try {
                ret = new MapleGuild(id);
                if (ret == null || ret.getId() <= 0 || !ret.isProper()) { //failed to load
                    return null;
                }
                guilds.put(id, ret);
            } finally {
                lock.writeLock().unlock();
            }
        }
        return ret; //Guild doesn't exist?
    }

    public static MapleGuild getGuildByName(String guildName) {
        lock.readLock().lock();
        try {
            for (MapleGuild g : guilds.values()) {
                if (g.getName().equalsIgnoreCase(guildName)) {
                    return g;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static MapleGuild getGuild(MapleCharacter mc) {
        return getGuild(mc.getGuildId());
    }

    public static void setGuildMemberOnline(MapleGuildCharacter mc, boolean bOnline, int channel) {
        MapleGuild g = getGuild(mc.getGuildId());
        if (g != null) {
            g.setOnline(mc.getId(), bOnline, channel);
        }
    }

    public static void guildPacket(int gid, ServerPacket message) {
        MapleGuild g = getGuild(gid);
        if (g != null) {
            g.broadcast(message);
        }
    }

    public static int addGuildMember(MapleGuildCharacter mc) {
        MapleGuild g = getGuild(mc.getGuildId());
        if (g != null) {
            return g.addGuildMember(mc);
        }
        return 0;
    }

    public static void leaveGuild(MapleGuildCharacter mc) {
        MapleGuild g = getGuild(mc.getGuildId());
        if (g != null) {
            g.leaveGuild(mc);
        }
    }

    public static void guildChat(int gid, String name, int cid, String msg) {
        MapleGuild g = getGuild(gid);
        if (g != null) {
            g.guildChat(name, cid, msg);
        }
    }

    public static void changeRank(int gid, int cid, int newRank) {
        MapleGuild g = getGuild(gid);
        if (g != null) {
            g.changeRank(cid, newRank);
        }
    }

    public static void expelMember(MapleGuildCharacter initiator, String name, int cid) {
        MapleGuild g = getGuild(initiator.getGuildId());
        if (g != null) {
            g.expelMember(initiator, name, cid);
        }
    }

    public static void setGuildNotice(int gid, String notice) {
        MapleGuild g = getGuild(gid);
        if (g != null) {
            g.setGuildNotice(notice);
        }
    }

    public static void memberLevelJobUpdate(MapleGuildCharacter mc) {
        MapleGuild g = getGuild(mc.getGuildId());
        if (g != null) {
            g.memberLevelJobUpdate(mc);
        }
    }

    public static void changeRankTitle(int gid, String[] ranks) {
        MapleGuild g = getGuild(gid);
        if (g != null) {
            g.changeRankTitle(ranks);
        }
    }

    public static void setGuildEmblem(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MapleGuild g = getGuild(gid);
        if (g != null) {
            g.setGuildEmblem(bg, bgcolor, logo, logocolor);
        }
    }

    public static void disbandGuild(int gid) {
        MapleGuild g = getGuild(gid);
        lock.writeLock().lock();
        try {
            if (g != null) {
                g.disbandGuild();
                guilds.remove(gid);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void deleteGuildCharacter(int guildid, int charid) {

        //ensure it's loaded on world server
        //setGuildMemberOnline(mc, false, -1);
        MapleGuild g = getGuild(guildid);
        if (g != null) {
            MapleGuildCharacter mc = g.getMGC(charid);
            if (mc != null) {
                if (mc.getGuildRank() > 1) //not leader
                {
                    g.leaveGuild(mc);
                } else {
                    g.disbandGuild();
                }
            }
        }
    }

    public static boolean increaseGuildCapacity(int gid) {
        MapleGuild g = getGuild(gid);
        if (g != null) {
            return g.increaseCapacity();
        }
        return false;
    }

    public static void gainGP(int gid, int amount) {
        MapleGuild g = getGuild(gid);
        if (g != null) {
            g.gainGP(amount);
        }
    }

    public static int getGP(final int gid) {
        final MapleGuild g = getGuild(gid);
        if (g != null) {
            return g.getGP();
        }
        return 0;
    }

    public static int getInvitedId(final int gid) {
        final MapleGuild g = getGuild(gid);
        if (g != null) {
            return g.getInvitedId();
        }
        return 0;
    }

    public static void setInvitedId(final int gid, final int inviteid) {
        final MapleGuild g = getGuild(gid);
        if (g != null) {
            g.setInvitedId(inviteid);
        }
    }

    public static int getGuildLeader(final String guildName) {
        final MapleGuild mga = getGuildByName(guildName);
        if (mga != null) {
            return mga.getLeaderId();
        }
        return 0;
    }

    public static void save() {
        System.out.println("Saving guilds...");
        lock.writeLock().lock();
        try {
            for (MapleGuild a : guilds.values()) {
                a.writeToDB(false);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static List<MapleBBSThread> getBBS(final int gid) {
        final MapleGuild g = getGuild(gid);
        if (g != null) {
            return g.getBBS();
        }
        return null;
    }

    public static int addBBSThread(final int guildid, final String title, final String text, final int icon, final boolean bNotice, final int posterID) {
        final MapleGuild g = getGuild(guildid);
        if (g != null) {
            return g.addBBSThread(title, text, icon, bNotice, posterID);
        }
        return -1;
    }

    public static final void editBBSThread(final int guildid, final int localthreadid, final String title, final String text, final int icon, final int posterID, final int guildRank) {
        final MapleGuild g = getGuild(guildid);
        if (g != null) {
            g.editBBSThread(localthreadid, title, text, icon, posterID, guildRank);
        }
    }

    public static final void deleteBBSThread(final int guildid, final int localthreadid, final int posterID, final int guildRank) {
        final MapleGuild g = getGuild(guildid);
        if (g != null) {
            g.deleteBBSThread(localthreadid, posterID, guildRank);
        }
    }

    public static final void addBBSReply(final int guildid, final int localthreadid, final String text, final int posterID) {
        final MapleGuild g = getGuild(guildid);
        if (g != null) {
            g.addBBSReply(localthreadid, text, posterID);
        }
    }

    public static final void deleteBBSReply(final int guildid, final int localthreadid, final int replyid, final int posterID, final int guildRank) {
        final MapleGuild g = getGuild(guildid);
        if (g != null) {
            g.deleteBBSReply(localthreadid, replyid, posterID, guildRank);
        }
    }

    public static void changeEmblem(int gid, int affectedPlayers, MapleGuildSummary mgs) {
        sendGuildPacket(affectedPlayers, ResCWvsContext.guildEmblemChange(gid, mgs.getLogoBG(), mgs.getLogoBGColor(), mgs.getLogo(), mgs.getLogoColor()), -1, gid);
        setGuildAndRank(affectedPlayers, -1, -1, -1);	//respawn player
    }

    public static void setGuildAndRank(int cid, int guildid, int rank, int alliancerank) {
        MapleCharacter mc = TacosWorld.find(0).findOnlinePlayerById(cid, false);
        if (mc == null) {
            return;
        }
        boolean bDifferentGuild;
        if (guildid == -1 && rank == -1) { //just need a respawn
            bDifferentGuild = true;
        } else {
            bDifferentGuild = guildid != mc.getGuildId();
            mc.setGuildId(guildid);
            mc.setGuildRank((byte) rank);
            mc.setAllianceRank((byte) alliancerank);
            mc.saveGuildStatus();
        }
    }

    public static void sendGuildPacket(int targetIds, ServerPacket packet, int exception, int guildid) {
        if (targetIds == exception) {
            return;
        }
        final MapleCharacter player = TacosWorld.find(0).findOnlinePlayerById(targetIds, false);
        if (player != null && player.getGuildId() == guildid) {
            player.SendPacket(packet);
        }
    }
}
