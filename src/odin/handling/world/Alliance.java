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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import odin.handling.world.guild.MapleGuild;
import odin.handling.world.guild.MapleGuildAlliance;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.server.TacosWorld;

public class Alliance {

    private final Map<Integer, MapleGuildAlliance> alliances = new LinkedHashMap<>();

    public Alliance() {
        //System.out.println("[MapleGuildAlliance] Loading GuildAlliances");
        Collection<MapleGuildAlliance> allGuilds = MapleGuildAlliance.loadAll();
        for (MapleGuildAlliance g : allGuilds) {
            alliances.put(g.getId(), g);
        }
    }

    public MapleGuildAlliance getAlliance(int allianceid) {
        MapleGuildAlliance ret = alliances.get(allianceid);
        if (ret == null) {
            ret = new MapleGuildAlliance(allianceid);
            if (ret == null || ret.getId() <= 0) { //failed to load
                return null;
            }
            alliances.put(allianceid, ret);
        }
        return ret;
    }

    public int getAllianceLeader(int allianceid) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            return mga.getLeaderId();
        }
        return 0;
    }

    public void updateAllianceRanks(int allianceid, String[] ranks) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            mga.setRank(ranks);
        }
    }

    public void updateAllianceNotice(int allianceid, String notice) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            mga.setNotice(notice);
        }
    }

    public boolean canInvite(int allianceid) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            return mga.getCapacity() > mga.getNoGuilds();
        }
        return false;
    }

    public boolean changeAllianceLeader(int allianceid, int cid) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            return mga.setLeaderId(cid);
        }
        return false;
    }

    public boolean changeAllianceRank(int allianceid, int cid, int change) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            return mga.changeAllianceRank(cid, change);
        }
        return false;
    }

    public boolean changeAllianceCapacity(int allianceid) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            return mga.setCapacity();
        }
        return false;
    }

    public boolean disbandAlliance(int allianceid) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            return mga.disband();
        }
        return false;
    }

    public boolean addGuildToAlliance(int allianceid, int gid) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            return mga.addGuild(gid);
        }
        return false;
    }

    public boolean removeGuildFromAlliance(int allianceid, int gid, boolean expelled) {
        MapleGuildAlliance mga = getAlliance(allianceid);
        if (mga != null) {
            return mga.removeGuild(gid, expelled);
        }
        return false;
    }

    public void sendGuild(int allianceid) {
        MapleGuildAlliance alliance = getAlliance(allianceid);
        if (alliance != null) {
            sendGuild(ResCWvsContext.getAllianceUpdate(alliance), -1, allianceid);
            sendGuild(ResCWvsContext.getGuildAlliance(alliance), -1, allianceid);
        }
    }

    public void sendGuild(ServerPacket packet, int exceptionId, int allianceid) {
        MapleGuildAlliance alliance = getAlliance(allianceid);
        if (alliance != null) {
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                int gid = alliance.getGuildId(i);
                if (gid > 0 && gid != exceptionId) {
                    TacosWorld.find(0).getGuild().guildPacket(gid, packet);
                }
            }
        }
    }

    public boolean createAlliance(String alliancename, int cid, int cid2, int gid, int gid2) {
        int allianceid = MapleGuildAlliance.createToDb(cid, alliancename, gid, gid2);
        if (allianceid <= 0) {
            return false;
        }
        MapleGuild g = TacosWorld.find(0).getGuild().getGuild(gid), g_ = TacosWorld.find(0).getGuild().getGuild(gid2);
        g.setAllianceId(allianceid);
        g_.setAllianceId(allianceid);
        g.changeARank(true);
        g_.changeARank(false);

        MapleGuildAlliance alliance = getAlliance(allianceid);

        sendGuild(ResCWvsContext.createGuildAlliance(alliance), -1, allianceid);
        sendGuild(ResCWvsContext.getAllianceInfo(alliance), -1, allianceid);
        sendGuild(ResCWvsContext.getGuildAlliance(alliance), -1, allianceid);
        sendGuild(ResCWvsContext.changeAlliance(alliance, true), -1, allianceid);
        return true;
    }

    public void allianceChat(int gid, String name, int cid, String msg) {
        MapleGuild g = TacosWorld.find(0).getGuild().getGuild(gid);
        if (g != null) {
            MapleGuildAlliance ga = getAlliance(g.getAllianceId());
            if (ga != null) {
                for (int i = 0; i < ga.getNoGuilds(); i++) {
                    MapleGuild g_ = TacosWorld.find(0).getGuild().getGuild(ga.getGuildId(i));
                    if (g_ != null) {
                        g_.allianceChat(name, cid, msg);
                    }
                }
            }
        }
    }

    public void setNewAlliance(int gid, int allianceid) {
        MapleGuildAlliance alliance = getAlliance(allianceid);
        MapleGuild guild = TacosWorld.find(0).getGuild().getGuild(gid);
        if (alliance != null && guild != null) {
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                if (gid == alliance.getGuildId(i)) {
                    guild.setAllianceId(allianceid);
                    guild.broadcast(ResCWvsContext.getAllianceInfo(alliance));
                    guild.broadcast(ResCWvsContext.getGuildAlliance(alliance));
                    guild.broadcast(ResCWvsContext.changeAlliance(alliance, true));
                    guild.changeARank();
                    guild.writeToDB(false);
                } else {
                    MapleGuild g_ = TacosWorld.find(0).getGuild().getGuild(alliance.getGuildId(i));
                    if (g_ != null) {
                        g_.broadcast(ResCWvsContext.addGuildToAlliance(alliance, guild));
                        g_.broadcast(ResCWvsContext.changeGuildInAlliance(alliance, guild, true));
                    }
                }
            }
        }
    }

    public void setOldAlliance(int gid, boolean expelled, int allianceid) {
        MapleGuildAlliance alliance = getAlliance(allianceid);
        MapleGuild g_ = TacosWorld.find(0).getGuild().getGuild(gid);
        if (alliance != null) {
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                MapleGuild guild = TacosWorld.find(0).getGuild().getGuild(alliance.getGuildId(i));
                if (guild == null) {
                    if (gid != alliance.getGuildId(i)) {
                        alliance.removeGuild(gid, false);
                    }
                    continue; //just skip
                }
                if (g_ == null || gid == alliance.getGuildId(i)) {
                    guild.changeARank(5);
                    guild.setAllianceId(0);
                    guild.broadcast(ResCWvsContext.disbandAlliance(allianceid));
                } else if (g_ != null) {
                    guild.broadcast(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("[" + g_.getName() + "] Guild has left the alliance.").build()));
                    guild.broadcast(ResCWvsContext.changeGuildInAlliance(alliance, g_, false));
                    guild.broadcast(ResCWvsContext.removeGuildFromAlliance(alliance, g_, expelled));
                }

            }
        }

        if (gid == -1) {
            alliances.remove(allianceid);
        }
    }

    public List<ServerPacket> getAllianceInfo(int allianceid, boolean start) {
        List<ServerPacket> ret = new ArrayList<>();
        MapleGuildAlliance alliance = getAlliance(allianceid);
        if (alliance != null) {
            if (start) {
                ret.add(ResCWvsContext.getAllianceInfo(alliance));
                ret.add(ResCWvsContext.getGuildAlliance(alliance));
            }
            ret.add(ResCWvsContext.getAllianceUpdate(alliance));
        }
        return ret;
    }

    public void save() {
        System.out.println("Saving alliances...");
        for (MapleGuildAlliance a : alliances.values()) {
            a.saveToDb();
        }
    }
}
