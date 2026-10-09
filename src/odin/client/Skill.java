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
import java.util.ArrayList;
import java.util.List;
import odin.server.MapleStatEffect;
import odin.server.life.Element;

public class Skill {

    private String name = "";
    private final List<MapleStatEffect> effects = new ArrayList<>();
    private Element element;
    private byte level;
    private int id;
    private int animationTime;
    private int requiredSkill;
    private int masterLevel;
    private boolean action;
    private boolean invisible;
    private boolean chargeskill;
    private boolean timeLimited;

    public Skill(int id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setElement(final Element element) {
        this.element = element;
    }

    public void setLevel(final byte level) {
        this.level = level;
    }

    public void setAnimationTime(final int animationTime) {
        this.animationTime = animationTime;
    }

    public void setRequiredSkill(final int requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    public void setMasterLevel(final int masterLevel) {
        this.masterLevel = masterLevel;
    }

    public void setAction(final boolean action) {
        this.action = action;
    }

    public void setInvisible(final boolean invisible) {
        this.invisible = invisible;
    }

    public void setChargeSkill(final boolean chargeskill) {
        this.chargeskill = chargeskill;
    }

    public void setTimeLimited(final boolean timeLimited) {
        this.timeLimited = timeLimited;
    }

    public void addEffect(final MapleStatEffect effect) {
        this.effects.add(effect);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MapleStatEffect getEffect(final int level) {
        if (effects.size() < level) {
            if (!effects.isEmpty()) { //incAllskill
                return effects.get(effects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    public boolean getAction() {
        return action;
    }

    public boolean isChargeSkill() {
        return chargeskill;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public boolean hasRequiredSkill() {
        return level > 0;
    }

    public int getRequiredSkillLevel() {
        return level;
    }

    public int getRequiredSkillId() {
        return requiredSkill;
    }

    public byte getMaxLevel() {
        return (byte) effects.size();
    }

    public boolean canBeLearnedBy(int job) {
        int jid = job;
        int skillForJob = id / 10000;
        if (skillForJob == 2001 && GameConstants.isEvan(job)) {
            return true; //special exception for evan -.-
        } else if (jid / 100 != skillForJob / 100) { // wrong job
            return false;
        } else if (jid / 1000 != skillForJob / 1000) { // wrong job
            return false;
        } else if (GameConstants.isAdventurer(skillForJob) && !GameConstants.isAdventurer(job)) {
            return false;
        } else if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
            return false;
        } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        } else if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
            return false;
        } else if (GameConstants.isResist(skillForJob) && !GameConstants.isResist(job)) {
            return false;
        } else if ((skillForJob / 10) % 10 > (jid / 10) % 10) { // wrong 2nd job
            return false;
        } else if (skillForJob % 10 > jid % 10) { // wrong 3rd/4th job
            return false;
        }
        return true;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    public boolean isFourthJob() {
        int job_id = id / 10000;
        // エヴァン
        if (2200 <= job_id && job_id <= 2218) {
            if (7 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // デュアルブレイド
        if (430 <= job_id && job_id <= 434) {
            if (4 <= (job_id % 10)) {
                return true;
            }
            return false;
        }
        // JMS v164
        if (2 <= (job_id % 10)) {
            return true;
        }

        return false;
    }

    public Element getElement() {
        return element;
    }

    public int getAnimationTime() {
        return animationTime;
    }

    public int getMasterLevel() {
        return masterLevel;
    }

    public boolean isBeginnerSkill() {
        int jobId = id / 10000;
        return jobId == 0 || jobId == 1000 || jobId == 2000 || jobId == 2001 || jobId == 3000;
    }
}
