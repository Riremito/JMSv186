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
import java.util.LinkedList;
import java.util.List;
import tacos.client.TacosCharacter;

public class MapleParty {

    private MaplePartyCharacter leader;
    private List<MaplePartyCharacter> members = new LinkedList<>();
    private int id;

    public MapleParty(int id, MaplePartyCharacter chrfor) {
        this.leader = chrfor;
        this.members.add(this.leader);
        this.id = id;
    }

    public void addMember(MaplePartyCharacter member) {
        members.add(member);
    }

    public void updateMember(MaplePartyCharacter member) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equals(member)) {
                members.set(i, member);
            }
        }
        if (leader.equals(member)) {
            leader = member;
        }
    }

    // party slot, the client places the member's town door on the slot-th door portal.
    public int getMemberIndex(int id) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public MaplePartyCharacter getMemberById(int id) {
        for (MaplePartyCharacter chr : members) {
            if (chr.getId() == id) {
                return chr;
            }
        }
        return null;
    }

    public MaplePartyCharacter getMemberByIndex(int index) {
        return members.get(index);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLeader(MaplePartyCharacter nLeader) {
        leader = nLeader;
    }

    // used by script
    public void removeMember(MaplePartyCharacter member) {
        members.remove(member);
    }

    // used by script
    public Collection<MaplePartyCharacter> getMembers() {
        return new LinkedList<>(members);
    }

    // used by script
    public int getId() {
        return id;
    }

    // used by script
    public MaplePartyCharacter getLeader() {
        return leader;
    }


    public enum PartyOperation {

        JOIN, LEAVE, EXPEL, DISBAND, SILENT_UPDATE, LOG_ONOFF, CHANGE_LEADER, CHANGE_LEADER_DC
    }

    public static class MaplePartyCharacter {

        private String name;
        private int id;
        private int level;
        private int channel;
        private int jobid;
        private int mapid;
        private boolean online;

        public MaplePartyCharacter(TacosCharacter maplechar) {
            this.name = maplechar.getName();
            this.level = maplechar.getLevel();
            this.channel = maplechar.getClient().getChannelId();
            this.id = maplechar.getId();
            this.jobid = maplechar.getJob();
            this.mapid = maplechar.getMapId();
            this.online = true;
        }

        public MaplePartyCharacter() {
            this.name = "";
        }

        // members are rebuilt from the character on every update, compare by character id.
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MaplePartyCharacter)) {
                return false;
            }
            return this.id == ((MaplePartyCharacter) obj).id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(this.id);
        }

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }

        // used by script
        public int getLevel() {
            return level;
        }

        // used by script
        public int getChannel() {
            return channel;
        }

        // used by script
        public int getMapid() {
            return mapid;
        }

        // used by script
        public String getName() {
            return name;
        }

        // used by script
        public int getId() {
            return id;
        }

        // used by script
        public int getJobId() {
            return jobid;
        }
    }
}
