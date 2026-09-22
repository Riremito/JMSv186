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
package odin.server.maps;

import java.awt.Rectangle;
import odin.client.Skill;
import odin.client.MapleCharacter;
import odin.client.SkillFactory;
import odin.server.MapleStatEffect;
import odin.server.life.MapleMonster;
import odin.server.life.MobSkill;
import tacos.server.map.object.TacosMapObject;

public class MapleMist extends TacosMapObject {

    private Rectangle mistPosition;

    private MapleStatEffect source;
    private MobSkill skill;
    private boolean isMobMist;
    private int skillDelay;
    private int skilllevel;
    private int isPoisonMist;
    private int ownerId;
    private long time_created;
    private long time_removal;
    private int duration;

    public MapleMist(Rectangle mistPosition, MapleMonster mob, MobSkill skill, int dur) {
        this.mistPosition = mistPosition;
        this.ownerId = mob.getId();
        this.skill = skill;
        this.skilllevel = skill.getSkillLevel();

        isMobMist = true;
        isPoisonMist = 0;
        skillDelay = 0;

        this.time_created = System.currentTimeMillis();
        this.time_removal = this.time_created + dur;
        this.duration = dur;

        setPosition(mistPosition.getLocation());
    }

    public MapleMist(Rectangle mistPosition, MapleCharacter owner, MapleStatEffect source, int dur) {
        this.mistPosition = mistPosition;
        this.ownerId = owner.getId();
        this.source = source;
        this.skillDelay = 8;
        this.isMobMist = false;
        this.skilllevel = owner.getSkillLevel(SkillFactory.getSkill(source.getSourceId()));

        switch (source.getSourceId()) {
            case 4221006 -> // Smoke Screen
                isPoisonMist = 0;
            case 14111006, 2111003, 12111005 -> // Flame wizard, [Flame Gear]
                isPoisonMist = 1;
            case 22161003 -> // FP mist
                //Recovery Aura
                isPoisonMist = 2;
        }

        this.time_created = System.currentTimeMillis();
        this.time_removal = this.time_created + dur;
        this.duration = dur;

        setPosition(mistPosition.getLocation());
    }

    public Skill getSourceSkill() {
        return SkillFactory.getSkill(source.getSourceId());
    }

    public boolean isMobMist() {
        return isMobMist;
    }

    public int isPoisonMist() {
        return isPoisonMist;
    }

    public int getSkillDelay() {
        return skillDelay;
    }

    public int getSkillLevel() {
        return skilllevel;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public MobSkill getMobSkill() {
        return this.skill;
    }

    public Rectangle getBox() {
        return mistPosition;
    }

    public MapleStatEffect getSource() {
        return source;
    }

    public boolean makeChanceResult() {
        return source.makeChanceResult();
    }

    public long getTime() {
        return this.time_created;
    }

    public long getTimeRemoval() {
        return this.time_removal;
    }

    public int getDuration() {
        return this.duration;
    }
}
