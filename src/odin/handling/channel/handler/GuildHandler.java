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

import odin.handling.world.guild.MapleGuildResponse;
import odin.handling.world.guild.MapleGuild;
import java.util.Iterator;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ClientPacket;
import tacos.packet.ServerPacket;

public class GuildHandler {

    public static final void DenyGuildRequest(ClientPacket cp, final TacosClient client) {
        byte unk1 = cp.Decode1();
        String from = cp.DecodeStr();

        final MapleCharacter cfrom = client.getChannelServer().getOnlinePlayers().findByName(from);
        if (cfrom != null) {
            cfrom.SendPacket(ResCWvsContext.denyGuildInvitation(client.getPlayer().getName()));
        }
    }

    private static boolean isGuildNameAcceptable(final String name) {
        if (name.getBytes().length < 3 || name.getBytes().length > 12) {
            return false;
        }
        /*        for (int i = 0; i < name.length(); i++) {
            if (!Character.isLowerCase(name.charAt(i)) && !Character.isUpperCase(name.charAt(i))) {
                return false;
            }
        }*/
        return true;
    }

    private static final class Invited {

        public String name;
        public int gid;
        public long expiration;

        public Invited(final String n, final int id) {
            name = n.toLowerCase();
            gid = id;
            expiration = System.currentTimeMillis() + 60 * 60 * 1000; // 1 hr expiration
        }

        @Override
        public final boolean equals(Object other) {
            if (!(other instanceof Invited)) {
                return false;
            }
            Invited oth = (Invited) other;
            return (gid == oth.gid && name.equals(oth.name));
        }
    }
    private static final java.util.List<Invited> invited = new java.util.LinkedList<>();
    private static long nextPruneTime = System.currentTimeMillis() + 20 * 60 * 1000;

    public static final void Guild(ClientPacket cp, final TacosClient client) {
        MapleCharacter chr = client.getPlayer();
        if (System.currentTimeMillis() >= nextPruneTime) {
            Iterator<Invited> itr = invited.iterator();
            Invited inv;
            while (itr.hasNext()) {
                inv = itr.next();
                if (System.currentTimeMillis() >= inv.expiration) {
                    itr.remove();
                }
            }
            nextPruneTime = System.currentTimeMillis() + 20 * 60 * 1000;
        }

        switch (cp.Decode1()) {
            case 0x02: // Create guild
                if (chr.getGuildId() > 0 || chr.getMapId() != 200000301) {
                    chr.dropMessage(1, "You cannot create a new Guild while in one.");
                    return;
                } else if (chr.getMeso() < 5000000) {
                    chr.dropMessage(1, "You do not have enough mesos to create a client.getWorld().getGuild().");
                    return;
                }
                final String guildName = cp.DecodeStr();

                if (!isGuildNameAcceptable(guildName)) {
                    chr.dropMessage(1, "The Guild name you have chosen is not accepted.");
                    return;
                }
                int guildId = client.getWorld().getGuild().createGuild(chr.getId(), guildName);
                if (guildId == 0) {
                    client.SendPacket(ResCWvsContext.genericGuildMessage((byte) 0x1c));
                    return;
                }
                chr.gainMeso(-5000000, true, false, true);
                chr.setGuildId(guildId);
                chr.setGuildRank((byte) 1);
                chr.saveGuildStatus();
                client.SendPacket(ResCWvsContext.showGuildInfo(chr));
                client.getWorld().getGuild().setGuildMemberOnline(chr.getMGC(), true, client.getChannelId());
                chr.dropMessage(1, "You have successfully created a client.getWorld().getGuild().");
                break;
            case 0x05: // invitation
                if (chr.getGuildId() <= 0 || chr.getGuildRank() > 2) { // 1 == guild master, 2 == jr
                    return;
                }
                String name = cp.DecodeStr();
                final MapleGuildResponse mgr = MapleGuild.sendInvite(client, name);

                if (mgr != null) {
                    client.SendPacket(mgr.getPacket());
                } else {
                    Invited inv = new Invited(name, chr.getGuildId());
                    if (!invited.contains(inv)) {
                        invited.add(inv);
                    }
                }
                break;
            case 0x06: // accepted guild invitation
                if (chr.getGuildId() > 0) {
                    return;
                }
                guildId = cp.Decode4();
                int cid = cp.Decode4();

                if (cid != chr.getId()) {
                    return;
                }
                name = chr.getName().toLowerCase();
                Iterator<Invited> itr = invited.iterator();

                while (itr.hasNext()) {
                    Invited inv = itr.next();
                    if (guildId == inv.gid && name.equals(inv.name)) {
                        chr.setGuildId(guildId);
                        chr.setGuildRank((byte) 5);
                        itr.remove();

                        int s = client.getWorld().getGuild().addGuildMember(chr.getMGC());
                        if (s == 0) {
                            chr.dropMessage(1, "The Guild you are trying to join is already full.");
                            chr.setGuildId(0);
                            return;
                        }
                        client.SendPacket(ResCWvsContext.showGuildInfo(chr));
                        final MapleGuild gs = client.getWorld().getGuild().getGuild(guildId);
                        for (ServerPacket pack : client.getWorld().getAlliance().getAllianceInfo(gs.getAllianceId(), true)) {
                            if (pack != null) {
                                client.SendPacket(pack);
                            }
                        }
                        chr.saveGuildStatus();
                        break;
                    }
                }
                break;
            case 0x07: // leaving
                cid = cp.Decode4();
                name = cp.DecodeStr();

                if (cid != chr.getId() || !name.equals(chr.getName()) || chr.getGuildId() <= 0) {
                    return;
                }
                client.getWorld().getGuild().leaveGuild(chr.getMGC());
                client.SendPacket(ResCWvsContext.showGuildInfo(null));
                break;
            case 0x08: // Expel
                cid = cp.Decode4();
                name = cp.DecodeStr();

                if (chr.getGuildRank() > 2 || chr.getGuildId() <= 0) {
                    return;
                }
                client.getWorld().getGuild().expelMember(chr.getMGC(), name, cid);
                break;
            case 0x0d: // Guild rank titles change
                if (chr.getGuildId() <= 0 || chr.getGuildRank() != 1) {
                    return;
                }
                String ranks[] = new String[5];
                for (int i = 0; i < 5; i++) {
                    ranks[i] = cp.DecodeStr();
                }

                client.getWorld().getGuild().changeRankTitle(chr.getGuildId(), ranks);
                break;
            case 0x0e: // Rank change
                cid = cp.Decode4();
                byte newRank = cp.Decode1();

                if ((newRank <= 1 || newRank > 5) || chr.getGuildRank() > 2 || (newRank <= 2 && chr.getGuildRank() != 1) || chr.getGuildId() <= 0) {
                    return;
                }

                client.getWorld().getGuild().changeRank(chr.getGuildId(), cid, newRank);
                break;
            case 0x0f: // guild emblem change
                if (chr.getGuildId() <= 0 || chr.getGuildRank() != 1 || chr.getMapId() != 200000301) {
                    return;
                }

                if (chr.getMeso() < 15000000) {
                    chr.dropMessage(1, "You do not have enough mesos to create a client.getWorld().getGuild().");
                    return;
                }
                final short bg = cp.Decode2();
                final byte bgcolor = cp.Decode1();
                final short logo = cp.Decode2();
                final byte logocolor = cp.Decode1();

                client.getWorld().getGuild().setGuildEmblem(chr.getGuildId(), bg, bgcolor, logo, logocolor);

                chr.gainMeso(-15000000, true, false, true);
                break;
            case 0x10: // guild notice change
                final String notice = cp.DecodeStr();
                if (notice.length() > 100 || chr.getGuildId() <= 0 || chr.getGuildRank() > 2) {
                    return;
                }
                client.getWorld().getGuild().setGuildNotice(chr.getGuildId(), notice);
                break;
        }
    }
}
