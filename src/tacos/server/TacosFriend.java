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
package tacos.server;

import odin.client.MapleCharacter;
import tacos.database.query.DQ_Characters;

/**
 *
 * @author Riremito
 */
public class TacosFriend {

    private int id;
    private String name;
    private int level;
    private int job;
    private boolean hidden;
    private int channel;
    private String tag;
    private boolean online;
    private MapleCharacter chr;

    public TacosFriend() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public int getJob() {
        return this.job;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean getHidden() {
        return this.hidden;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getChannel() {
        return this.channel;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getOnline() {
        return this.online;
    }

    public MapleCharacter getCharacter() {
        return this.chr;
    }

    public static TacosFriend findByName(MapleCharacter chr, String friend_name) {
        // online
        MapleCharacter online_player = chr.getWorld().findOnlinePlayer(friend_name);
        if (online_player != null) {
            TacosFriend online_friend = new TacosFriend();
            online_friend.id = online_player.getId();
            online_friend.name = online_player.getName();
            online_friend.level = online_player.getLevel();
            online_friend.job = online_player.getJob();
            online_friend.channel = -1;
            online_friend.online = true;
            online_friend.chr = online_player;
            return online_friend;
        }
        // offine
        TacosFriend offline_friend = DQ_Characters.findOfflineFriend(chr.getWorldId(), friend_name);
        if (offline_friend != null) {
            offline_friend.online = false;
            offline_friend.chr = null;
            return offline_friend;
        }
        // not found.
        return null;
    }

}
