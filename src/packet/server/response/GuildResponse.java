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
package packet.server.response;

import client.MapleCharacter;
import handling.MaplePacket;
import handling.channel.MapleGuildRanking;
import handling.world.World;
import handling.world.guild.MapleBBSThread;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import handling.world.guild.MapleGuildCharacter;
import java.util.Collection;
import java.util.List;
import packet.server.ServerPacket;
import packet.server.response.struct.TestHelper;
import tools.MaplePacketCreator;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class GuildResponse {

    public static MaplePacket getGuildAlliance(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(13);
        if (alliance == null) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return MaplePacketCreator.enableActions();
            }
        }
        mplew.writeInt(noGuilds);
        for (MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showGuildRanks(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(73);
        mplew.writeInt(npcid);
        mplew.writeInt(all.size());
        for (MapleGuildRanking.GuildRankingInfo info : all) {
            mplew.writeMapleAsciiString(info.getName());
            mplew.writeInt(info.getGP());
            mplew.writeInt(info.getLogo());
            mplew.writeInt(info.getLogoColor());
            mplew.writeInt(info.getLogoBg());
            mplew.writeInt(info.getLogoBgColor());
        }
        return mplew.getPacket();
    }

    public static MaplePacket changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(2);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(58);
        mplew.writeInt(gid);
        mplew.write(capacity);
        return mplew.getPacket();
    }

    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(64);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.write(mgc.getGuildRank());
        return mplew.getPacket();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(66);
        mplew.writeInt(gid);
        mplew.writeShort(bg);
        mplew.write(bgcolor);
        mplew.writeShort(logo);
        mplew.write(logocolor);
        return mplew.getPacket();
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(61);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.write(bOnline ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket showThread(MapleBBSThread thread) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildBBS.Get());
        mplew.write(7);
        mplew.writeInt(thread.localthreadID);
        mplew.writeInt(thread.ownerID);
        mplew.writeLong(TestHelper.getKoreanTimestamp(thread.timestamp));
        mplew.writeMapleAsciiString(thread.name);
        mplew.writeMapleAsciiString(thread.text);
        mplew.writeInt(thread.icon);
        mplew.writeInt(thread.getReplyCount());
        for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
            mplew.writeInt(reply.replyid);
            mplew.writeInt(reply.ownerID);
            mplew.writeLong(TestHelper.getKoreanTimestamp(reply.timestamp));
            mplew.writeMapleAsciiString(reply.content);
        }
        return mplew.getPacket();
    }

    public static MaplePacket changeAllianceRank(int allianceid, MapleGuildCharacter player) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(5);
        mplew.writeInt(allianceid);
        mplew.writeInt(player.getId());
        mplew.writeInt(player.getAllianceRank());
        return mplew.getPacket();
    }

    public static MaplePacket changeAlliance(MapleGuildAlliance alliance, final boolean in) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(1);
        mplew.write(in ? 1 : 0);
        mplew.writeInt(in ? alliance.getId() : 0);
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < noGuilds; i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return MaplePacketCreator.enableActions();
            }
        }
        mplew.write(noGuilds);
        for (int i = 0; i < noGuilds; i++) {
            mplew.writeInt(g[i].getId());
            //must be world
            Collection<MapleGuildCharacter> members = g[i].getMembers();
            mplew.writeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                mplew.writeInt(mgc.getId());
                mplew.write(in ? mgc.getAllianceRank() : 0);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateAllianceRank(int allianceid, MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(27);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getAllianceRank());
        return mplew.getPacket();
    }

    public static MaplePacket getAllianceInfo(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(12);
        mplew.write(alliance == null ? 0 : 1); //in an alliance
        if (alliance != null) {
            addAllianceInfo(mplew, alliance);
        }
        return mplew.getPacket();
    }

    private static void addThread(MaplePacketLittleEndianWriter mplew, MapleBBSThread rs) {
        mplew.writeInt(rs.localthreadID);
        mplew.writeInt(rs.ownerID);
        mplew.writeMapleAsciiString(rs.name);
        mplew.writeLong(TestHelper.getKoreanTimestamp(rs.timestamp));
        mplew.writeInt(rs.icon);
        mplew.writeInt(rs.getReplyCount());
    }

    public static MaplePacket updateAlliance(MapleGuildCharacter mgc, int allianceid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(24);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());
        return mplew.getPacket();
    }

    private static void addAllianceInfo(MaplePacketLittleEndianWriter mplew, MapleGuildAlliance alliance) {
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRank(i));
        }
        mplew.write(alliance.getNoGuilds());
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            mplew.writeInt(alliance.getGuildId(i));
        }
        mplew.writeInt(alliance.getCapacity()); // ????
        mplew.writeMapleAsciiString(alliance.getNotice());
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(bExpelled ? 47 : 44);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeMapleAsciiString(mgc.getName());
        return mplew.getPacket();
    }

    public static MaplePacket addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(18);
        addAllianceInfo(mplew, alliance);
        mplew.writeInt(newGuild.getId()); //???
        getGuildInfo(mplew, newGuild);
        mplew.write(0); //???
        return mplew.getPacket();
    }

    public static MaplePacket BBSThreadList(final List<MapleBBSThread> bbs, int start) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildBBS.Get());
        mplew.write(6);
        if (bbs == null) {
            mplew.write(0);
            mplew.writeLong(0);
            return mplew.getPacket();
        }
        int threadCount = bbs.size();
        MapleBBSThread notice = null;
        for (MapleBBSThread b : bbs) {
            if (b.isNotice()) {
                //notice
                notice = b;
                break;
            }
        }
        final int ret = notice == null ? 0 : 1;
        mplew.write(ret);
        if (notice != null) {
            //has a notice
            addThread(mplew, notice);
            threadCount--; //one thread didn't count (because it's a notice)
        }
        if (threadCount < start) {
            //seek to the thread before where we start
            //uh, we're trying to start at a place past possible
            start = 0;
        }
        //each page has 10 threads, start = page # in packet but not here
        mplew.writeInt(threadCount);
        final int pages = Math.min(10, threadCount - start);
        mplew.writeInt(pages);
        for (int i = 0; i < pages; i++) {
            addThread(mplew, bbs.get(start + i + ret)); //because 0 = notice
        }
        return mplew.getPacket();
    }

    public static MaplePacket getAllianceUpdate(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(23);
        addAllianceInfo(mplew, alliance);
        return mplew.getPacket();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(60);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());
        return mplew.getPacket();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(39);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\u0000', 13));
        mplew.writeInt(mgc.getJobId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getGuildRank()); //should be always 5 but whatevs
        mplew.writeInt(mgc.isOnline() ? 1 : 0); //should always be 1 too
        mplew.writeInt(1); //? could be guild signature, but doesn't seem to matter
        mplew.writeInt(mgc.getAllianceRank()); //should always 3
        return mplew.getPacket();
    }

    public static MaplePacket guildNotice(int gid, String notice) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(68);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(notice);
        return mplew.getPacket();
    }

    private static void getGuildInfo(MaplePacketLittleEndianWriter mplew, MapleGuild guild) {
        mplew.writeInt(guild.getId());
        mplew.writeMapleAsciiString(guild.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
        }
        guild.addMemberData(mplew);
        mplew.writeInt(guild.getCapacity());
        mplew.writeShort(guild.getLogoBG());
        mplew.write(guild.getLogoBGColor());
        mplew.writeShort(guild.getLogo());
        mplew.write(guild.getLogoColor());
        mplew.writeMapleAsciiString(guild.getNotice());
        mplew.writeInt(guild.getGP());
        mplew.writeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
    }

    public static MaplePacket updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(25);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static MaplePacket genericGuildMessage(byte code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(code);
        return mplew.getPacket();
    }

    public static MaplePacket denyGuildInvitation(String charname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(55);
        mplew.writeMapleAsciiString(charname);
        return mplew.getPacket();
    }

    public static MaplePacket sendAllianceInvite(String allianceName, MapleCharacter inviter) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(3);
        mplew.writeInt(inviter.getGuildId());
        mplew.writeMapleAsciiString(inviter.getName());
        //alliance invite did NOT change
        mplew.writeMapleAsciiString(allianceName);
        return mplew.getPacket();
    }

    public static MaplePacket showGuildInfo(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(26); //signature for showing guild info
        if (c == null || c.getMGC() == null) {
            //show empty guild (used for leaving, expelled)
            mplew.write(0);
            return mplew.getPacket();
        }
        MapleGuild g = World.Guild.getGuild(c.getGuildId());
        if (g == null) {
            //failed to read from DB - don't show a guild
            mplew.write(0);
            return mplew.getPacket();
        }
        mplew.write(1); //bInGuild
        getGuildInfo(mplew, g);
        return mplew.getPacket();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(62);
        mplew.writeInt(gid);
        for (String r : ranks) {
            mplew.writeMapleAsciiString(r);
        }
        return mplew.getPacket();
    }

    public static MaplePacket removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(16);
        addAllianceInfo(mplew, alliance);
        getGuildInfo(mplew, expelledGuild);
        mplew.write(expelled ? 1 : 0); //1 = expelled, 0 = left
        return mplew.getPacket();
    }

    public static MaplePacket allianceMemberOnline(int alliance, int gid, int id, boolean online) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(14);
        mplew.writeInt(alliance);
        mplew.writeInt(gid);
        mplew.writeInt(id);
        mplew.write(online ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, final boolean add) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(4);
        mplew.writeInt(add ? alliance.getId() : 0);
        mplew.writeInt(guild.getId());
        Collection<MapleGuildCharacter> members = guild.getMembers();
        mplew.writeInt(members.size());
        for (MapleGuildCharacter mgc : members) {
            mplew.writeInt(mgc.getId());
            mplew.write(add ? mgc.getAllianceRank() : 0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket disbandAlliance(int alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(29);
        mplew.writeInt(alliance);
        return mplew.getPacket();
    }

    public static MaplePacket guildDisband(int gid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(50);
        mplew.writeInt(gid);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket createGuildAlliance(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.Get());
        mplew.write(15);
        addAllianceInfo(mplew, alliance);
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return MaplePacketCreator.enableActions();
            }
        }
        for (MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(5);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(charName);
        mplew.writeInt(levelFrom);
        mplew.writeInt(jobFrom);
        return mplew.getPacket();
    }

    public static MaplePacket updateGP(int gid, int GP) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.Get());
        mplew.write(72);
        mplew.writeInt(gid);
        mplew.writeInt(GP);
        return mplew.getPacket();
    }
    
}
