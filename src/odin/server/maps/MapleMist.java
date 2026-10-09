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
import tacos.server.map.object.TacosMist;

public class MapleMist extends TacosMist {

    private MapleStatEffect source;
    private MobSkill skill;
    private boolean isMobMist;
    private int skillDelay;
    private int skilllevel;
    private int isPoisonMist;

    public MapleMist(Rectangle mistPosition, MapleMonster mob, MobSkill skill, int dur) {
        this.skill = skill;
        this.skilllevel = skill.getSkillLevel();

        isMobMist = true;
        isPoisonMist = 0;
        skillDelay = 0;

        setOwnerId(mob.getId());
        setPosition(mistPosition.getLocation());
        setDuration(dur);
        setBox(mistPosition);
    }

    public MapleMist(Rectangle mistPosition, MapleCharacter owner, MapleStatEffect source, int dur) {
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

        setOwnerId(owner.getId());
        setPosition(mistPosition.getLocation());
        setDuration(dur);
        setBox(mistPosition);
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

    public MobSkill getMobSkill() {
        return this.skill;
    }

    public MapleStatEffect getSource() {
        return source;
    }

    public boolean makeChanceResult() {
        return source.makeChanceResult();
    }
}
