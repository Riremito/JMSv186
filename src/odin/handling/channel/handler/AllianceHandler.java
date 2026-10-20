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
package odin.handling.channel.handler;

import tacos.client.TacosCharacter;
import odin.handling.world.guild.MapleGuild;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ClientPacket;
import tacos.packet.ServerPacket;

public class AllianceHandler {

    public static void HandleAlliance(ClientPacket cp, TacosCharacter player, boolean denied) {
        TacosCharacter chr = player;
        if (chr.getGuildId() <= 0) {
            chr.updateStat();
            return;
        }
        MapleGuild gs = player.getWorld().getGuild().getGuild(chr.getGuildId());
        if (gs == null) {
            chr.updateStat();
            return;
        }
        //System.out.println("Unhandled GuildAlliance \n" + slea.toString());
        byte op = cp.Decode1();
        if (chr.getGuildRank() != 1 && op != 1) { //only updating doesn't need guild leader
            return;
        }
        if (op == 22) {
            denied = true;
        }
        int leaderid = 0;
        if (gs.getAllianceId() > 0) {
            leaderid = player.getWorld().getAlliance().getAllianceLeader(gs.getAllianceId());
        }
        //accept invite, and deny invite don't need allianceid.
        if (op != 4 && !denied) {
            if (gs.getAllianceId() <= 0 || leaderid <= 0) {
                return;
            }
        } else if (leaderid > 0 || gs.getAllianceId() > 0) { //infact, if they have allianceid it's suspicious
            return;
        }
        if (denied) {
            DenyInvite(player, gs);
            return;
        }
        int inviteid;
        switch (op) {
            case 1: //load... must be in world op

                for (ServerPacket pack : player.getWorld().getAlliance().getAllianceInfo(gs.getAllianceId(), false)) {
                    if (pack != null) {
                        chr.SendPacket(pack);
                    }
                }
                break;
            case 3: //invite
                int newGuild = player.getWorld().getGuild().getGuildLeader(cp.DecodeStr());
                if (newGuild > 0 && chr.getAllianceRank() == 1 && leaderid == chr.getId()) {
                    chr = player.getChannelServer().getOnlinePlayers().findById(newGuild);
                    if (chr != null && chr.getGuildId() > 0 && player.getWorld().getAlliance().canInvite(gs.getAllianceId())) {
                        chr.SendPacket(ResCWvsContext.sendAllianceInvite(player.getWorld().getAlliance().getAlliance(gs.getAllianceId()).getName(), player));
                        player.getWorld().getGuild().setInvitedId(chr.getGuildId(), gs.getAllianceId());
                    }
                }
                break;
            case 4: //accept invite... guildid that invited(int, a/b check) -> guildname that was invited? but we dont care about that
                inviteid = player.getWorld().getGuild().getInvitedId(player.getGuildId());
                if (inviteid > 0) {
                    if (!player.getWorld().getAlliance().addGuildToAlliance(inviteid, player.getGuildId())) {
                        player.Notice("An error occured when adding guild.");
                    }
                    player.getWorld().getGuild().setInvitedId(player.getGuildId(), 0);
                }
                break;
            case 2: //leave; nothing
            case 6: //expel, guildid(int) -> allianceid(don't care, a/b check)
                int gid;
                if (op == 6/* && slea.available() >= 4*/) {
                    gid = cp.Decode4();
                    if (/*slea.available() >= 4 &&*/gs.getAllianceId() != cp.Decode4()) {
                        break;
                    }
                } else {
                    gid = player.getGuildId();
                }
                if (player.getAllianceRank() <= 2 && (player.getAllianceRank() == 1 || player.getGuildId() == gid) && !player.getWorld().getAlliance().removeGuildFromAlliance(gs.getAllianceId(), gid, player.getGuildId() != gid)) {
                    player.Notice("An error occured when removing guild.");
                }
                break;
            case 7: //change leader
                if (player.getAllianceRank() == 1 && leaderid == player.getId() && !player.getWorld().getAlliance().changeAllianceLeader(gs.getAllianceId(), cp.Decode4())) {
                    player.Notice("An error occured when changing leader.");
                }
                break;
            case 8: //title update
                if (player.getAllianceRank() == 1 && leaderid == player.getId()) {
                    String[] ranks = new String[5];
                    for (int i = 0; i < 5; i++) {
                        ranks[i] = cp.DecodeStr();
                    }
                    player.getWorld().getAlliance().updateAllianceRanks(gs.getAllianceId(), ranks);
                }
                break;
            case 9:
                if (player.getAllianceRank() <= 2 && !player.getWorld().getAlliance().changeAllianceRank(gs.getAllianceId(), cp.Decode4(), cp.Decode1())) {
                    player.Notice("An error occured when changing rank.");
                }
                break;
            case 10: //notice update
                if (player.getAllianceRank() <= 2) {
                    String notice = cp.DecodeStr();
                    if (notice.length() > 100) {
                        break;
                    }
                    player.getWorld().getAlliance().updateAllianceNotice(gs.getAllianceId(), notice);
                }
                break;
            default:
                System.out.println("Unhandled GuildAlliance op: " + op + ", \n"/* + slea.toString()*/);
                break;
        }
    }

    public static void DenyInvite(TacosCharacter player, MapleGuild gs) { //playername that invited -> guildname that was invited but we also don't care
        int inviteid = player.getWorld().getGuild().getInvitedId(player.getGuildId());
        if (inviteid > 0) {
            int newAlliance = player.getWorld().getAlliance().getAllianceLeader(inviteid);
            if (newAlliance > 0) {
                TacosCharacter chr = player.getChannelServer().getOnlinePlayers().findById(newAlliance);
                if (chr != null) {
                    chr.Notice(gs.getName() + " Guild has rejected the Guild Union invitation.");
                }
                player.getWorld().getGuild().setInvitedId(player.getGuildId(), 0);
            }
        }
    }
}
