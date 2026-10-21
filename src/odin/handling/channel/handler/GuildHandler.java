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
package odin.handling.channel.handler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import odin.handling.world.Guild;
import odin.handling.world.guild.MapleGuild;
import odin.handling.world.guild.MapleGuild.MapleGuildResponse;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsGuild;
import tacos.packet.response.ResCWvsContext;

public class GuildHandler {

    private static final int GUILD_OFFICE_MAP_ID = 200000301;
    private static final int CREATE_GUILD_COST = 5000000;
    private static final int SET_MARK_COST = 15000000;
    private static final long INVITE_EXPIRATION = 60 * 60 * 1000; // 1 hour
    private static final long PRUNE_INTERVAL = 20 * 60 * 1000;

    private static final class Invited {

        public final String name;
        public final int gid;
        public final long expiration;

        public Invited(String name, int gid) {
            this.name = name.toLowerCase();
            this.gid = gid;
            this.expiration = System.currentTimeMillis() + INVITE_EXPIRATION;
        }

        public boolean matches(String name, int gid) {
            return this.gid == gid && this.name.equals(name.toLowerCase());
        }
    }

    private static final List<Invited> invited = new LinkedList<>();
    private static long nextPruneTime = System.currentTimeMillis() + PRUNE_INTERVAL;

    // CP_GuildResult : the invitation was blocked or rejected.
    public static void DenyGuildRequest(ClientPacket cp, TacosCharacter chr) {
        OpsGuild ops = OpsGuild.find(cp.Decode1());
        String from = cp.DecodeStr(); // inviter
        switch (ops) {
            case GuildRes_InviteGuild_BlockedUser:
            case GuildRes_InviteGuild_AlreadyInvited:
            case GuildRes_InviteGuild_Rejected: {
                break;
            }
            default: {
                ops = OpsGuild.GuildRes_InviteGuild_Rejected;
                break;
            }
        }
        TacosCharacter inviter = chr.getChannelServer().getOnlinePlayers().findByName(from);
        if (inviter != null) {
            inviter.SendPacket(ResCWvsContext.denyGuildInvitation(ops, chr.getName()));
        }
    }

    private static boolean isGuildNameAcceptable(String name) {
        int length = name.getBytes().length;
        return 3 <= length && length <= 12;
    }

    private static void pruneInvited() {
        long now = System.currentTimeMillis();
        if (now < nextPruneTime) {
            return;
        }
        invited.removeIf(inv -> inv.expiration <= now);
        nextPruneTime = now + PRUNE_INTERVAL;
    }

    // CP_GuildRequest
    public static void Guild(ClientPacket cp, TacosCharacter chr) {
        pruneInvited();
        Guild world_guild = chr.getWorld().getGuild();
        int op = cp.Decode1();
        switch (OpsGuild.find(op)) {
            case GuildReq_CheckGuildName: { // create guild
                if (chr.getGuildId() > 0 || chr.getMapId() != GUILD_OFFICE_MAP_ID) {
                    chr.Alert("You cannot create a new Guild while in one.");
                    return;
                }
                if (chr.getMeso() < CREATE_GUILD_COST) {
                    chr.Alert("You do not have enough mesos to create a guild.");
                    return;
                }
                String guild_name = cp.DecodeStr();
                if (!isGuildNameAcceptable(guild_name)) {
                    chr.Alert("The Guild name you have chosen is not accepted.");
                    return;
                }
                int guild_id = world_guild.createGuild(chr.getId(), guild_name);
                if (guild_id == 0) {
                    chr.SendPacket(ResCWvsContext.genericGuildMessage(OpsGuild.GuildRes_CheckGuildName_AlreadyUsed));
                    return;
                }
                chr.gainMeso(-CREATE_GUILD_COST, true, false, true);
                chr.setGuildId(guild_id);
                chr.setGuildRank((byte) 1);
                chr.saveGuildStatus();
                chr.SendPacket(ResCWvsContext.showGuildInfo(chr));
                world_guild.setGuildMemberOnline(chr.getMGC(), true, chr.getClient().getChannelId());
                chr.Alert("You have successfully created a guild.");
                return;
            }
            case GuildReq_InviteGuild: {
                String name = cp.DecodeStr();
                if (chr.getGuildId() <= 0 || chr.getGuildRank() > 2) { // 1 = guild master, 2 = jr. master
                    return;
                }
                MapleGuildResponse response = MapleGuild.sendInvite(chr, name);
                if (response != null) {
                    chr.SendPacket(response.getPacket());
                    return;
                }
                if (invited.stream().noneMatch(inv -> inv.matches(name, chr.getGuildId()))) {
                    invited.add(new Invited(name, chr.getGuildId()));
                }
                return;
            }
            case GuildReq_JoinGuild: {
                int guild_id = cp.Decode4(); // m_dwInviterID (guild id)
                int character_id = cp.Decode4();
                if (chr.getGuildId() > 0 || character_id != chr.getId()) {
                    return;
                }
                Iterator<Invited> itr = invited.iterator();
                while (itr.hasNext()) {
                    if (itr.next().matches(chr.getName(), guild_id)) {
                        itr.remove();
                        joinGuild(chr, guild_id);
                        return;
                    }
                }
                return;
            }
            case GuildReq_WithdrawGuild: {
                int character_id = cp.Decode4();
                String name = cp.DecodeStr();
                if (character_id != chr.getId() || !name.equals(chr.getName()) || chr.getGuildId() <= 0) {
                    return;
                }
                world_guild.leaveGuild(chr.getMGC());
                chr.SendPacket(ResCWvsContext.showGuildInfo(null));
                return;
            }
            case GuildReq_KickGuild: {
                int character_id = cp.Decode4();
                String name = cp.DecodeStr();
                if (chr.getGuildId() <= 0 || chr.getGuildRank() > 2) {
                    return;
                }
                world_guild.expelMember(chr.getMGC(), name, character_id);
                return;
            }
            case GuildReq_SetGradeName: {
                String[] ranks = new String[5];
                for (int i = 0; i < ranks.length; i++) {
                    ranks[i] = cp.DecodeStr();
                }
                if (chr.getGuildId() <= 0 || chr.getGuildRank() != 1) {
                    return;
                }
                world_guild.changeRankTitle(chr.getGuildId(), ranks);
                return;
            }
            case GuildReq_SetMemberGrade: {
                int character_id = cp.Decode4();
                int new_rank = cp.Decode1();
                if (chr.getGuildId() <= 0 || chr.getGuildRank() > 2 || new_rank <= 1 || 5 < new_rank || (new_rank <= 2 && chr.getGuildRank() != 1)) {
                    return;
                }
                world_guild.changeRank(chr.getGuildId(), character_id, new_rank);
                return;
            }
            case GuildReq_SetMark: {
                short bg = cp.Decode2();
                byte bgcolor = cp.Decode1();
                short logo = cp.Decode2();
                byte logocolor = cp.Decode1();
                if (chr.getGuildId() <= 0 || chr.getGuildRank() != 1 || chr.getMapId() != GUILD_OFFICE_MAP_ID) {
                    return;
                }
                if (chr.getMeso() < SET_MARK_COST) {
                    chr.Alert("You do not have enough mesos to change the guild emblem.");
                    return;
                }
                world_guild.setGuildEmblem(chr.getGuildId(), bg, bgcolor, logo, logocolor);
                chr.gainMeso(-SET_MARK_COST, true, false, true);
                return;
            }
            case GuildReq_SetNotice: {
                String notice = cp.DecodeStr();
                if (notice.length() > 100 || chr.getGuildId() <= 0 || chr.getGuildRank() > 2) {
                    return;
                }
                world_guild.setGuildNotice(chr.getGuildId(), notice);
                return;
            }
            default: {
                DebugLogger.ErrorLog("GuildRequest : not coded " + op);
                return;
            }
        }
    }

    private static void joinGuild(TacosCharacter chr, int guild_id) {
        Guild world_guild = chr.getWorld().getGuild();
        chr.setGuildId(guild_id);
        chr.setGuildRank((byte) 5);
        if (world_guild.addGuildMember(chr.getMGC()) == 0) {
            chr.Alert("The Guild you are trying to join is already full.");
            chr.setGuildId(0);
            return;
        }
        chr.SendPacket(ResCWvsContext.showGuildInfo(chr));
        MapleGuild guild = world_guild.getGuild(guild_id);
        for (ServerPacket sp : chr.getWorld().getAlliance().getAllianceInfo(guild.getAllianceId(), true)) {
            chr.SendPacket(sp);
        }
        chr.saveGuildStatus();
    }
}
