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
package odin.server.life;

import odin.constants.GameConstants;
import tacos.packet.ops.OpsMobSkill;
import tacos.client.TacosCharacter;
import tacos.server.map.object.TacosMonster;
import tacos.server.map.object.TacosMist;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import tacos.packet.ops.OpsMobStat;
import tacos.packet.response.ResCMobPool;
import java.util.EnumMap;
import tacos.wz.MobWz.MobBanInfo;
import tacos.wz.WzXML;

public class MobSkill {

    private int skillId;
    private int skillLevel;
    private int mpCon;
    private int spawnEffect;
    private int hp;
    private int x;
    private int y;
    private long duration;
    private long cooltime;
    private float prop;
//    private short effect_delay;
    private short limit;
    private List<Integer> toSummon = new ArrayList<>();
    private Point lt;
    private Point rb;

    public MobSkill(int skillId, int level) {
        this.skillId = skillId;
        this.skillLevel = level;
    }

    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    public void addSummons(List<Integer> toSummon) {
        this.toSummon = toSummon;
    }

    /*   public void setEffectDelay(short effect_delay) {
    this.effect_delay = effect_delay;
    }*/
    public void setSpawnEffect(int spawnEffect) {
        this.spawnEffect = spawnEffect;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCoolTime(long cooltime) {
        this.cooltime = cooltime;
    }

    public void setProp(float prop) {
        this.prop = prop;
    }

    public void setLtRb(Point lt, Point rb) {
        this.lt = lt;
        this.rb = rb;
    }

    public void setLimit(short limit) {
        this.limit = limit;
    }

    public void applyEffect(TacosCharacter player, TacosMonster monster, boolean skill) {
        Map<OpsMobStat, Integer> stats = new EnumMap<>(OpsMobStat.class);
        List<Integer> reflection = new LinkedList<>();

        switch (OpsMobSkill.find(skillId)) {
            case MOBSKILL_POWERUP:
            case MOBSKILL_POWERUP_M:
                stats.put(OpsMobStat.MS_PowerUp, x);
                break;
            case MOBSKILL_PAD:
                stats.put(OpsMobStat.MS_PAD, x);
                break;
            case MOBSKILL_MAGICUP:
            case MOBSKILL_MAGICUP_M:
                stats.put(OpsMobStat.MS_MagicUp, x);
                break;
            case MOBSKILL_MAD:
                stats.put(OpsMobStat.MS_MAD, x);
                break;
            case MOBSKILL_PGUARDUP:
            case MOBSKILL_PGUARDUP_M:
                stats.put(OpsMobStat.MS_PGuardUp, x);
                break;
            case MOBSKILL_PDR:
                stats.put(OpsMobStat.MS_PDR, x);
                break;
            case MOBSKILL_MGUARDUP:
            case MOBSKILL_MGUARDUP_M:
                stats.put(OpsMobStat.MS_MGuardUp, x);
                break;
            case MOBSKILL_MDR:
                stats.put(OpsMobStat.MS_MDR, x);
                break;
            case MOBSKILL_ACC:
                stats.put(OpsMobStat.MS_ACC, x);
                break;
            case MOBSKILL_EVA:
                stats.put(OpsMobStat.MS_EVA, x);
                break;
            case MOBSKILL_SPEED:
                stats.put(OpsMobStat.MS_Speed, x);
                break;
            case MOBSKILL_SEALSKILL:
                stats.put(OpsMobStat.MS_SealSkill, x);
                break;
            case MOBSKILL_HEAL_M:
                if (lt != null && rb != null && skill && monster != null) {
                    Rectangle bounds = calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
                    int hp = (getX() / 1000) * (int) (950 + 1050 * Math.random());
                    for (TacosMonster target : monster.getMap().getMonstersInRect(bounds)) {
                        target.heal(hp, getY(), true);
                    }
                } else if (monster != null) {
                    monster.heal(getX(), getY(), true);
                }
                break;
            case MOBSKILL_DISPEL:
                // dispel.
                break;
            case MOBSKILL_BANMAP: // Banish
                if (monster != null) {
                    MobBanInfo info = monster.getStats().getBanishInfo();
                    if (info != null) {
                        if (lt != null && rb != null && skill && player != null) {
                            for (TacosCharacter chr : getPlayersInRange(monster, player)) {
                                chr.changeMapBanish(info.getField(), info.getPortal(), info.getBanMsg());
                            }
                        } else if (player != null) {
                            player.changeMapBanish(info.getField(), info.getPortal(), info.getBanMsg());
                        }
                    }
                }
                break;
            case MOBSKILL_AREA_POISON: // Mist
                if (monster != null) {
                    monster.getMap().addMist(new TacosMist(calculateBoundingBox(monster.getPosition(), true), monster.getId(), getSkillLevel(), x * 10, this, null));
                }
                break;
            case MOBSKILL_PHYSICALIMMUNE:
                stats.put(OpsMobStat.MS_PImmune, x);
                break;
            case MOBSKILL_MAGICIMMUNE:
                stats.put(OpsMobStat.MS_MImmune, x);
                break;
            case MOBSKILL_HARDSKIN: // Weapon / Magic Immunity
                stats.put(OpsMobStat.MS_HardSkin, x);
                break;
            case MOBSKILL_PCOUNTER: // Weapon Reflect
                stats.put(OpsMobStat.MS_PCounter, x);
                stats.put(OpsMobStat.MS_PImmune, x);
                reflection.add(x);
                break;
            case MOBSKILL_MCOUNTER: // Magic Reflect
                stats.put(OpsMobStat.MS_MCounter, x);
                stats.put(OpsMobStat.MS_MImmune, x);
                reflection.add(x);
                break;
            case MOBSKILL_PMCOUNTER: // Weapon / Magic reflect
                stats.put(OpsMobStat.MS_PCounter, x);
                stats.put(OpsMobStat.MS_PImmune, x);
                stats.put(OpsMobStat.MS_MCounter, x);
                stats.put(OpsMobStat.MS_MImmune, x);
                reflection.add(x);
                reflection.add(x);
                break;
            case MOBSKILL_SUMMON:
                if (monster == null) {
                    return;
                }
                for (Integer mobId : getSummons()) {
                    TacosMonster toSpawn = null;
                    try {
                        toSpawn = WzXML.MOB.createMob(GameConstants.getCustomSpawnID(monster.getId(), mobId));
                    } catch (RuntimeException e) { //monster doesn't exist
                        continue;
                    }
                    if (toSpawn == null) {
                        continue;
                    }
                    toSpawn.setPosition(monster.getPosition());
                    int ypos = (int) monster.getPosition().getY(), xpos = (int) monster.getPosition().getX();

                    switch (mobId) {
                        case 8500003: // Pap bomb high
                            toSpawn.setFootholdId((int) Math.ceil(Math.random() * 19.0));
                            ypos = -590;
                            break;
                        case 8500004: // Pap bomb
                            //Spawn between -500 and 500 from the monsters X position
                            xpos = (int) (monster.getPosition().getX() + Math.ceil(Math.random() * 1000.0) - 500);
                            ypos = (int) monster.getPosition().getY();
                            break;
                        case 8510100: //Pianus bomb
                            if (Math.ceil(Math.random() * 5) == 1) {
                                ypos = 78;
                                xpos = (int) (0 + Math.ceil(Math.random() * 5)) + ((Math.ceil(Math.random() * 2) == 1) ? 180 : 0);
                            } else {
                                xpos = (int) (monster.getPosition().getX() + Math.ceil(Math.random() * 1000.0) - 500);
                            }
                            break;
                        case 8820007: //mini bean
                            continue;
                    }
                    // Get spawn coordinates (This fixes monster lock)
                    // TODO get map left and right wall.
                    switch (monster.getMap().getId()) {
                        case 220080001: //Pap map
                            if (xpos < -890) {
                                xpos = (int) (-890 + Math.ceil(Math.random() * 150));
                            } else if (xpos > 230) {
                                xpos = (int) (230 - Math.ceil(Math.random() * 150));
                            }
                            break;
                        case 230040420: // Pianus map
                            if (xpos < -239) {
                                xpos = (int) (-239 + Math.ceil(Math.random() * 150));
                            } else if (xpos > 371) {
                                xpos = (int) (371 - Math.ceil(Math.random() * 150));
                            }
                            break;
                    }
                    monster.getMap().spawnMonsterWithEffect(toSpawn, getSpawnEffect(), monster.getMap().calcPointBelow(new Point(xpos, ypos - 1)));
                }
                break;
            default:
                break;
        }

        if (monster != null && !stats.isEmpty()) {
            int buff_id = skillId | (skillLevel << 16);
            boolean updated = false;
            for (Map.Entry<OpsMobStat, Integer> stat : stats.entrySet()) {
                if (monster.getBuff().update(stat.getKey(), buff_id, stat.getValue(), (int) duration)) {
                    updated = true;
                }
            }
            if (updated) {
                monster.getMap().broadcastPacket(ResCMobPool.MobStatSet(monster, buff_id));
            }
        }

        if (monster != null) {
            monster.setMp(monster.getMp() - getMpCon());
        }
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getMpCon() {
        return mpCon;
    }

    public List<Integer> getSummons() {
        return Collections.unmodifiableList(toSummon);
    }

    /*    public short getEffectDelay() {
    return effect_delay;
    }*/
    public int getSpawnEffect() {
        return spawnEffect;
    }

    public int getHP() {
        return hp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getDuration() {
        return duration;
    }

    public long getCoolTime() {
        return cooltime;
    }

    public boolean makeChanceResult() {
        return prop >= 1.0 || Math.random() < prop;
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point mylt, myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    private List<TacosCharacter> getPlayersInRange(TacosMonster monster, TacosCharacter player) {
        Rectangle bounds = calculateBoundingBox(monster.getPosition(), monster.isFacingLeft());
        List<TacosCharacter> players = new ArrayList<>();
        players.add(player);
        return monster.getMap().getPlayersInRectAndInList(bounds, players);
    }
}
