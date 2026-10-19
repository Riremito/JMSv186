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
package odin.client;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BuddyList {

    private Map<Integer, BuddylistEntry> buddies = new LinkedHashMap<>();
    private int capacity;

    public BuddyList(int capacity) {
        this.capacity = capacity;
    }

    public boolean containsVisible(int characterId) {
        BuddylistEntry ble = buddies.get(characterId);
        if (ble == null) {
            return false;
        }
        return ble.isVisible();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BuddylistEntry get(int characterId) {
        return buddies.get(characterId);
    }

    public BuddylistEntry get(String characterName) {
        String lowerCaseName = characterName.toLowerCase();
        for (BuddylistEntry ble : buddies.values()) {
            if (ble.getName().toLowerCase().equals(lowerCaseName)) {
                return ble;
            }
        }
        return null;
    }

    public void put(BuddylistEntry entry) {
        buddies.put(entry.getCharacterId(), entry);
    }

    public void remove(int characterId) {
        buddies.remove(characterId);
    }

    public Collection<BuddylistEntry> getBuddies() {
        return buddies.values();
    }

    public boolean isFull() {
        return buddies.size() >= capacity;
    }

    public int[] getBuddyIds() {
        int buddyIds[] = new int[buddies.size()];
        int i = 0;
        for (BuddylistEntry ble : buddies.values()) {
            buddyIds[i++] = ble.getCharacterId();
        }
        return buddyIds;
    }


    public static class BuddylistEntry {

        private String name;
        private String group;
        private int cid;
        private int channel;
        private int level;
        private int job;
        private boolean visible;
        private boolean hidden = false;

        public BuddylistEntry(String name, int characterId, String group, int channel, boolean visible, int level, int job) {
            this.name = name;
            this.cid = characterId;
            this.group = group;
            this.channel = channel;
            this.visible = visible;
            this.level = level;
            this.job = job;
        }

        public boolean getHidden() {
            return this.hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public boolean isOnline() {
            return channel >= 0;
        }

        public String getName() {
            return name;
        }

        public int getCharacterId() {
            return cid;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean isVisible() {
            return visible;
        }

        public String getGroup() {
            return group;
        }

        public int getLevel() {
            return level;
        }

        public int getJob() {
            return job;
        }

        public void setGroup(String g) {
            this.group = g;
        }

    }
}
