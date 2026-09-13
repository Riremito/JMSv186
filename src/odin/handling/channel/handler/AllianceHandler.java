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

import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import odin.handling.world.OdinWorld;
import odin.handling.world.guild.MapleGuild;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ClientPacket;
import tacos.packet.ServerPacket;

public class AllianceHandler {

    public static final void HandleAlliance(ClientPacket cp, final TacosClient client, boolean denied) {
        MapleCharacter chr = client.getPlayer();
        if (client.getPlayer().getGuildId() <= 0) {
            chr.updateStat();
            return;
        }
        final MapleGuild gs = OdinWorld.Guild.getGuild(client.getPlayer().getGuildId());
        if (gs == null) {
            chr.updateStat();
            return;
        }
        //System.out.println("Unhandled GuildAlliance \n" + slea.toString());
        byte op = cp.Decode1();
        if (client.getPlayer().getGuildRank() != 1 && op != 1) { //only updating doesn't need guild leader
            return;
        }
        if (op == 22) {
            denied = true;
        }
        int leaderid = 0;
        if (gs.getAllianceId() > 0) {
            leaderid = OdinWorld.Alliance.getAllianceLeader(gs.getAllianceId());
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
            DenyInvite(client, gs);
            return;
        }
        int inviteid;
        switch (op) {
            case 1: //load... must be in world op

                for (ServerPacket pack : OdinWorld.Alliance.getAllianceInfo(gs.getAllianceId(), false)) {
                    if (pack != null) {
                        chr.SendPacket(pack);
                    }
                }
                break;
            case 3: //invite
                final int newGuild = OdinWorld.Guild.getGuildLeader(cp.DecodeStr());
                if (newGuild > 0 && client.getPlayer().getAllianceRank() == 1 && leaderid == client.getPlayer().getId()) {
                    chr = client.getChannelServer().getOnlinePlayers().findById(newGuild);
                    if (chr != null && chr.getGuildId() > 0 && OdinWorld.Alliance.canInvite(gs.getAllianceId())) {
                        chr.SendPacket(ResCWvsContext.sendAllianceInvite(OdinWorld.Alliance.getAlliance(gs.getAllianceId()).getName(), client.getPlayer()));
                        OdinWorld.Guild.setInvitedId(chr.getGuildId(), gs.getAllianceId());
                    }
                }
                break;
            case 4: //accept invite... guildid that invited(int, a/b check) -> guildname that was invited? but we dont care about that
                inviteid = OdinWorld.Guild.getInvitedId(client.getPlayer().getGuildId());
                if (inviteid > 0) {
                    if (!OdinWorld.Alliance.addGuildToAlliance(inviteid, client.getPlayer().getGuildId())) {
                        client.getPlayer().dropMessage(5, "An error occured when adding guild.");
                    }
                    OdinWorld.Guild.setInvitedId(client.getPlayer().getGuildId(), 0);
                }
                break;
            case 2: //leave; nothing
            case 6: //expel, guildid(int) -> allianceid(don't care, a/b check)
                final int gid;
                if (op == 6/* && slea.available() >= 4*/) {
                    gid = cp.Decode4();
                    if (/*slea.available() >= 4 &&*/gs.getAllianceId() != cp.Decode4()) {
                        break;
                    }
                } else {
                    gid = client.getPlayer().getGuildId();
                }
                if (client.getPlayer().getAllianceRank() <= 2 && (client.getPlayer().getAllianceRank() == 1 || client.getPlayer().getGuildId() == gid)) {
                    if (!OdinWorld.Alliance.removeGuildFromAlliance(gs.getAllianceId(), gid, client.getPlayer().getGuildId() != gid)) {
                        client.getPlayer().dropMessage(5, "An error occured when removing guild.");
                    }
                }
                break;
            case 7: //change leader
                if (client.getPlayer().getAllianceRank() == 1 && leaderid == client.getPlayer().getId()) {
                    if (!OdinWorld.Alliance.changeAllianceLeader(gs.getAllianceId(), cp.Decode4())) {
                        client.getPlayer().dropMessage(5, "An error occured when changing leader.");
                    }
                }
                break;
            case 8: //title update
                if (client.getPlayer().getAllianceRank() == 1 && leaderid == client.getPlayer().getId()) {
                    String[] ranks = new String[5];
                    for (int i = 0; i < 5; i++) {
                        ranks[i] = cp.DecodeStr();
                    }
                    OdinWorld.Alliance.updateAllianceRanks(gs.getAllianceId(), ranks);
                }
                break;
            case 9:
                if (client.getPlayer().getAllianceRank() <= 2) {
                    if (!OdinWorld.Alliance.changeAllianceRank(gs.getAllianceId(), cp.Decode4(), cp.Decode1())) {
                        client.getPlayer().dropMessage(5, "An error occured when changing rank.");
                    }
                }
                break;
            case 10: //notice update
                if (client.getPlayer().getAllianceRank() <= 2) {
                    final String notice = cp.DecodeStr();
                    if (notice.length() > 100) {
                        break;
                    }
                    OdinWorld.Alliance.updateAllianceNotice(gs.getAllianceId(), notice);
                }
                break;
            default:
                System.out.println("Unhandled GuildAlliance op: " + op + ", \n"/* + slea.toString()*/);
                break;
        }
    }

    public static final void DenyInvite(TacosClient client, final MapleGuild gs) { //playername that invited -> guildname that was invited but we also don't care
        final int inviteid = OdinWorld.Guild.getInvitedId(client.getPlayer().getGuildId());
        if (inviteid > 0) {
            final int newAlliance = OdinWorld.Alliance.getAllianceLeader(inviteid);
            if (newAlliance > 0) {
                final MapleCharacter chr = client.getChannelServer().getOnlinePlayers().findById(newAlliance);
                if (chr != null) {
                    chr.dropMessage(5, gs.getName() + " Guild has rejected the Guild Union invitation.");
                }
                OdinWorld.Guild.setInvitedId(client.getPlayer().getGuildId(), 0);
            }
        }
    }
}
