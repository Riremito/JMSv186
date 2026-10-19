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
package tacos.server.map.object;

import java.awt.Rectangle;
import odin.client.Skill;
import odin.client.SkillFactory;
import odin.server.MapleStatEffect;
import odin.server.life.MobSkill;

/**
 *
 * @author Riremito
 */
public class TacosMist extends TacosMapObject {

    private int duration;
    private Rectangle box;
    private MapleStatEffect source;
    private MobSkill skill;
    private boolean isMobMist;
    private int skillDelay;
    private int skilllevel;
    private int isPoisonMist;

    // mob : skill != null, player : source != null
    public TacosMist(Rectangle box, int owner_id, int skill_level, int duration, MobSkill skill, MapleStatEffect source) {
        this.skill = skill;
        this.source = source;
        this.skilllevel = skill_level;
        this.isMobMist = (skill != null);
        this.skillDelay = this.isMobMist ? 0 : 8;
        this.isPoisonMist = 0;

        if (source != null) {
            switch (source.getSourceId()) {
                case 4221006 -> // Smoke Screen
                    isPoisonMist = 0;
                case 14111006, 2111003, 12111005 -> // Flame wizard, [Flame Gear]
                    isPoisonMist = 1;
                case 22161003 -> // FP mist
                    //Recovery Aura
                    isPoisonMist = 2;
            }
        }

        setOwnerId(owner_id);
        setPosition(box.getLocation());
        setDuration(duration);
        setBox(box);
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Rectangle getBox() {
        return this.box;
    }

    public void setBox(Rectangle box) {
        this.box = box;
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
