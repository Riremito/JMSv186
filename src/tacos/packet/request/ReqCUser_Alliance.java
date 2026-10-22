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
package tacos.packet.request;

import odin.handling.world.Alliance;
import odin.handling.world.guild.MapleGuild;
import odin.handling.world.guild.MapleGuildAlliance;
import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsAlliance;
import tacos.packet.response.ResCWvsContext;

/**
 *
 * @author Riremito
 */
public class ReqCUser_Alliance {

    public static void HandleAlliance(ClientPacket cp, TacosCharacter chr, boolean result) {
        MapleGuild guild = (chr.getGuildId() > 0) ? chr.getWorld().getGuild().getGuild(chr.getGuildId()) : null;
        if (guild == null) {
            chr.updateStat();
            return;
        }
        int op = cp.Decode1();
        OpsAlliance ops = OpsAlliance.find(op);
        // only loading does not need the guild master.
        if (ops != OpsAlliance.AllianceReq_Load && chr.getGuildRank() != 1) {
            return;
        }

        Alliance world_alliance = chr.getWorld().getAlliance();
        int alliance_id = guild.getAllianceId();
        int leader_id = (alliance_id > 0) ? world_alliance.getAllianceLeader(alliance_id) : 0;

        // CFadeWnd::SendCloseMessage, CWvsContext::OnAllianceResult
        if (result) {
            switch (ops) {
                case AllianceRes_InviteGuild_BlockedByOpt:
                case AllianceRes_InviteGuild_AlreadyInvited:
                case AllianceRes_InviteGuild_Rejected: {
                    if (alliance_id <= 0 && leader_id <= 0) {
                        denyInvite(chr, guild);
                    }
                    break;
                }
                default: {
                    DebugLogger.ErrorLog("AllianceResult : not coded " + op);
                    break;
                }
            }
            return;
        }

        // joining needs no alliance, the others need an alliance.
        if (ops == OpsAlliance.AllianceReq_Join) {
            if (alliance_id > 0 || leader_id > 0) {
                return;
            }
        } else if (alliance_id <= 0 || leader_id <= 0) {
            return;
        }

        boolean is_leader = chr.getAllianceRank() == 1 && leader_id == chr.getId();
        switch (ops) {
            case AllianceReq_Load: {
                for (ServerPacket sp : world_alliance.getAllianceInfo(alliance_id, false)) {
                    chr.SendPacket(sp);
                }
                break;
            }
            case AllianceReq_Withdraw: {
                if (chr.getAllianceRank() <= 2 && !world_alliance.removeGuildFromAlliance(alliance_id, guild.getId(), false)) {
                    chr.Notice("An error occured when removing guild.");
                }
                break;
            }
            case AllianceReq_Invite: {
                String guild_name = cp.DecodeStr();
                int new_guild_leader = chr.getWorld().getGuild().getGuildLeader(guild_name);
                if (new_guild_leader <= 0 || !is_leader) {
                    break;
                }
                TacosCharacter target = chr.getChannelServer().getOnlinePlayers().findById(new_guild_leader);
                if (target == null || target.getGuildId() <= 0 || !world_alliance.canInvite(alliance_id)) {
                    break;
                }
                MapleGuildAlliance alliance = world_alliance.getAlliance(alliance_id);
                target.SendPacket(ResCWvsContext.AllianceResult_Invite(alliance.getName(), chr));
                chr.getWorld().getGuild().setInvitedId(target.getGuildId(), alliance_id);
                break;
            }
            case AllianceReq_Join: {
                // dwInviterID and the name follow, not used.
                int invited_alliance_id = chr.getWorld().getGuild().getInvitedId(guild.getId());
                if (invited_alliance_id <= 0) {
                    break;
                }
                if (!world_alliance.addGuildToAlliance(invited_alliance_id, guild.getId())) {
                    chr.Notice("An error occured when adding guild.");
                }
                chr.getWorld().getGuild().setInvitedId(guild.getId(), 0);
                break;
            }
            case AllianceReq_Kick: {
                int guild_id = cp.Decode4();
                int kick_alliance_id = cp.Decode4();
                boolean can_kick = chr.getAllianceRank() == 1 || (chr.getAllianceRank() == 2 && guild_id == guild.getId());
                if (kick_alliance_id != alliance_id || !can_kick) {
                    break;
                }
                if (!world_alliance.removeGuildFromAlliance(alliance_id, guild_id, guild_id != guild.getId())) {
                    chr.Notice("An error occured when removing guild.");
                }
                break;
            }
            case AllianceReq_ChangeMaster: {
                int new_leader_id = cp.Decode4();
                if (is_leader && !world_alliance.changeAllianceLeader(alliance_id, new_leader_id)) {
                    chr.Notice("An error occured when changing leader.");
                }
                break;
            }
            case AllianceReq_SetGradeName: {
                String[] grade_names = new String[5];
                for (int i = 0; i < grade_names.length; i++) {
                    grade_names[i] = cp.DecodeStr();
                }
                if (is_leader) {
                    world_alliance.updateAllianceRanks(alliance_id, grade_names);
                }
                break;
            }
            case AllianceReq_ChangeGrade: {
                int character_id = cp.Decode4();
                int up = cp.Decode1(); // bUp
                if (chr.getAllianceRank() <= 2 && !world_alliance.changeAllianceRank(alliance_id, character_id, up)) {
                    chr.Notice("An error occured when changing rank.");
                }
                break;
            }
            case AllianceReq_SetNotice: {
                String notice = cp.DecodeStr();
                if (chr.getAllianceRank() <= 2 && notice.length() <= 100) {
                    world_alliance.updateAllianceNotice(alliance_id, notice);
                }
                break;
            }
            default: {
                DebugLogger.ErrorLog("AllianceRequest : not coded " + op);
                break;
            }
        }
    }

    private static void denyInvite(TacosCharacter chr, MapleGuild guild) {
        int invited_alliance_id = chr.getWorld().getGuild().getInvitedId(guild.getId());
        if (invited_alliance_id <= 0) {
            return;
        }
        int alliance_leader_id = chr.getWorld().getAlliance().getAllianceLeader(invited_alliance_id);
        if (alliance_leader_id <= 0) {
            return;
        }
        TacosCharacter alliance_leader = chr.getChannelServer().getOnlinePlayers().findById(alliance_leader_id);
        if (alliance_leader != null) {
            alliance_leader.Notice(guild.getName() + " Guild has rejected the Guild Union invitation.");
        }
        chr.getWorld().getGuild().setInvitedId(guild.getId(), 0);
    }
}
