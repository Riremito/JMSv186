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

import odin.constants.GameConstants;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import odin.server.quest.MapleQuest;
import tacos.wz.WzXML;

public class MapleQuestStatus {

    private MapleQuest quest;
    private int status;
    private Map<Integer, Integer> killedMobs = null;
    private int npc_id;
    private int forfeited = 0;
    private long completionTime;
    private String customData;

    public MapleQuestStatus(MapleQuest quest, int status, int npc_id) {
        this.quest = quest;
        this.status = status;
        this.npc_id = npc_id;
        this.completionTime = System.currentTimeMillis();
        if (status == 1) { // Started
            if (!quest.getRelevantMobs().isEmpty()) {
                registerMobs();
            }
        }
    }

    public MapleQuest getQuest() {
        return this.quest;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isCustom() {
        return GameConstants.isCustomQuest(this.quest.getId());
    }

    private void registerMobs() {
        this.killedMobs = new LinkedHashMap<>();
        for (int i : this.quest.getRelevantMobs().keySet()) {
            this.killedMobs.put(i, 0);
        }
    }

    private int maxMob(int mob_id) {
        for (Map.Entry<Integer, Integer> qs : this.quest.getRelevantMobs().entrySet()) {
            if (qs.getKey() == mob_id) {
                return qs.getValue();
            }
        }
        return 0;
    }

    public boolean mobKilled(int mob_id, int skill_id) {
        if (this.quest != null && this.quest.getSkillID() > 0 && this.quest.getSkillID() != skill_id) {
            return false;
        }
        Integer mob = this.killedMobs.get(mob_id);
        if (mob != null) {
            int mo = maxMob(mob_id);
            if (mob >= mo) {
                return false; //nothing happened
            }
            this.killedMobs.put(mob_id, Math.min(mob + 1, mo));
            return true;
        }
        for (Entry<Integer, Integer> mo : this.killedMobs.entrySet()) {
            if (WzXML.MOB.checkQuestCountGroup(mo.getKey(), mob_id)) {
                int mobb = maxMob(mo.getKey());
                if (mo.getValue() >= mobb) {
                    return false; //nothing
                }
                this.killedMobs.put(mo.getKey(), Math.min(mo.getValue() + 1, mobb));
                return true;
            }
        }
        return false;
    }

    public void setMobKills(int mob_id, int count) {
        if (this.killedMobs == null) {
            registerMobs(); //lol
        }
        this.killedMobs.put(mob_id, count);
    }

    public boolean hasMobKills() {
        if (this.killedMobs == null) {
            return false;
        }
        return !this.killedMobs.isEmpty();
    }

    public int getMobKills(int mob_id) {
        Integer mob = this.killedMobs.get(mob_id);
        if (mob == null) {
            return 0;
        }
        return mob;
    }

    public Map<Integer, Integer> getMobKills() {
        return this.killedMobs;
    }

    public int getForfeited() {
        return this.forfeited;
    }

    public void setForfeited(int forfeited) {
        if (this.forfeited <= forfeited) {
            this.forfeited = forfeited;
        }
    }

    // used by script
    public int getNpc() {
        return this.npc_id;
    }

    // used by script
    public long getCompletionTime() {
        return this.completionTime;
    }

    // used by script
    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    // used by script
    public void setCustomData(String customData) {
        this.customData = customData;
    }

    // used by script
    public String getCustomData() {
        return this.customData;
    }
}
