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

import odin.client.MapleCharacter;

public class MaplePartyCharacter {

    private String name;
    private int id;
    private int level;
    private int channel;
    private int jobid;
    private int mapid;
    private boolean online;

    public MaplePartyCharacter(MapleCharacter maplechar) {
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
