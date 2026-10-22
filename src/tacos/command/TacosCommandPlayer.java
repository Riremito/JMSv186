/*
 * Copyright (C) 2026 Riremito
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
package tacos.command;

import odin.server.maps.SavedLocationType;
import tacos.packet.response.ResCWvsContext;
import odin.handling.world.guild.MapleGuild;
import odin.handling.world.Guild;
import odin.handling.world.Alliance;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.packet.request.ReqCUser;

/**
 *
 * @author Riremito
 */
public class TacosCommandPlayer {

    public static boolean executeCommand(TacosCommander dcmd, TacosCharacter chr) {
        TacosMap map = chr.getMap();

        switch (dcmd.get(0)) {
            // enable actions.
            case "/ea" -> {
                chr.sendStatChanged(true);
                return true;
            }
            case "/save" -> {
                chr.saveToDB(false);
                chr.DebugMsg("save : done.");
                return true;
            }
            // player command.
            case "/fm" -> {
                chr.saveLocation(SavedLocationType.FREE_MARKET, map.getReturnMap().getId());
                chr.changeMapById(910000000);
                return true;
            }
            case "/henesys" -> {
                chr.changeMapById(100000000);
                return true;
            }
            case "/leafre" -> {
                chr.changeMapById(240000000);
                return true;
            }
            case "/magatia" -> {
                chr.changeMapById(261000000);
                return true;
            }
            case "/autosp" -> {
                int skill_id = chr.getSpUsed().get();
                if (skill_id != 0) {
                    while (ReqCUser.OnSkillUpRequestInternal(chr, skill_id));
                }

                chr.DebugMsg("autosp : " + skill_id);
                return true;
            }
            case "/wh" -> {
                for (TacosCharacter player : chr.getChannelServer().getOnlinePlayers().get()) {
                    if (player.getId() != chr.getId()) {
                        player.changeMapWithCoordinate(map.getId(), chr.getX(), chr.getY());
                    }
                }
                return true;
            }
            // guild, no cost and no map check.
            case "/guildcreate" -> {
                String guild_name = dcmd.get(1);
                if (chr.getGuildId() > 0) {
                    chr.DebugMsg("guildcreate : already in a guild.");
                    return true;
                }
                int length = guild_name.getBytes().length;
                if (length < 3 || 12 < length) {
                    chr.DebugMsg("guildcreate : /guildcreate <name (3-12 bytes)>");
                    return true;
                }
                Guild world_guild = chr.getWorld().getGuild();
                int guild_id = world_guild.createGuild(chr.getId(), guild_name);
                if (guild_id == 0) {
                    chr.DebugMsg("guildcreate : the name is already used.");
                    return true;
                }
                chr.setGuildId(guild_id);
                chr.setGuildRank((byte) 1);
                chr.saveGuildStatus();
                chr.SendPacket(ResCWvsContext.GuildResult_LoadGuild_Done(chr));
                world_guild.setGuildMemberOnline(chr.getMGC(), true, chr.getClient().getChannelId());
                chr.DebugMsg("guildcreate : " + guild_name);
                return true;
            }
            case "/guilddisband" -> {
                int guild_id = chr.getGuildId();
                if (guild_id <= 0 || chr.getGuildRank() != 1) {
                    chr.DebugMsg("guilddisband : only the guild master can disband the guild.");
                    return true;
                }
                MapleGuild guild = chr.getWorld().getGuild().getGuild(guild_id);
                if (guild != null && guild.getAllianceId() > 0) {
                    chr.DebugMsg("guilddisband : leave the alliance first.");
                    return true;
                }
                chr.getWorld().getGuild().disbandGuild(guild_id);
                chr.DebugMsg("guilddisband : done.");
                return true;
            }
            // alliance between the own guild and another guild.
            case "/alliancecreate" -> {
                String alliance_name = dcmd.get(1);
                String other_guild_name = dcmd.get(2);
                if (alliance_name.isEmpty() || other_guild_name.isEmpty()) {
                    chr.DebugMsg("alliancecreate : /alliancecreate <alliance name> <other guild name>");
                    return true;
                }
                Guild world_guild = chr.getWorld().getGuild();
                MapleGuild guild = world_guild.getGuild(chr.getGuildId());
                MapleGuild other_guild = world_guild.getGuildByName(other_guild_name);
                if (guild == null || chr.getGuildRank() != 1) {
                    chr.DebugMsg("alliancecreate : only a guild master can create an alliance.");
                    return true;
                }
                if (other_guild == null || other_guild.getId() == guild.getId()) {
                    chr.DebugMsg("alliancecreate : the other guild is not found.");
                    return true;
                }
                if (guild.getAllianceId() > 0 || other_guild.getAllianceId() > 0) {
                    chr.DebugMsg("alliancecreate : a guild is already in an alliance.");
                    return true;
                }
                if (!chr.getWorld().getAlliance().createAlliance(alliance_name, chr.getId(), other_guild.getLeaderId(), guild.getId(), other_guild.getId())) {
                    chr.DebugMsg("alliancecreate : failed.");
                    return true;
                }
                chr.DebugMsg("alliancecreate : " + alliance_name);
                return true;
            }
            // the alliance leader disbands the alliance, other guild masters withdraw their guild.
            case "/allianceleave" -> {
                MapleGuild guild = chr.getWorld().getGuild().getGuild(chr.getGuildId());
                if (guild == null || guild.getAllianceId() <= 0 || chr.getGuildRank() != 1) {
                    chr.DebugMsg("allianceleave : only a guild master in an alliance can leave.");
                    return true;
                }
                Alliance world_alliance = chr.getWorld().getAlliance();
                int alliance_id = guild.getAllianceId();
                if (world_alliance.getAllianceLeader(alliance_id) == chr.getId()) {
                    boolean disbanded = world_alliance.disbandAlliance(alliance_id);
                    chr.DebugMsg("allianceleave : " + (disbanded ? "disbanded." : "failed."));
                    return true;
                }
                boolean removed = world_alliance.removeGuildFromAlliance(alliance_id, guild.getId(), false);
                chr.DebugMsg("allianceleave : " + (removed ? "left." : "failed."));
                return true;
            }
            default -> {
            }
        }

        return false;
    }
}
