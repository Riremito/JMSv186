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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tacos.odin.OdinPair;

public class MapleMonsterStats {

    private byte cp;
    private byte selfDestruction_action;
    private byte tagColor;
    private byte tagBgColor;
    private byte rareItemDropLevel;
    private byte HPDisplayType;
    private short level;
    private short PhysicalDefense;
    private short MagicDefense;
    private short eva;
    private long hp;
    private int exp;
    private int mp;
    private int removeAfter;
    private int buffToGive;
    private int fixedDamage;
    private int selfDestruction_hp;
    private int dropItemPeriod;
    private int point;
    private boolean boss;
    private boolean undead;
    private boolean ffaLoot;
    private boolean firstAttack;
    private boolean isExplosiveReward;
    private boolean mobile;
    private boolean fly;
    private boolean onlyNormalAttack;
    private boolean friendly;
    private boolean noDoom;
    private String name;
    private Map<Element, ElementalEffectiveness> resistance = new HashMap<>();
    private List<Integer> revives = new ArrayList<>();
    private List<OdinPair<Integer, Integer>> skills = new ArrayList<>();
    private BanishInfo banish;

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;//(hp * 3L / 2L);
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public void setSelfD(byte selfDestruction_action) {
        this.selfDestruction_action = selfDestruction_action;
    }

    public byte getSelfD() {
        return selfDestruction_action;
    }

    public void setSelfDHP(int selfDestruction_hp) {
        this.selfDestruction_hp = selfDestruction_hp;
    }

    public int getSelfDHp() {
        return selfDestruction_hp;
    }

    public void setFixedDamage(int damage) {
        this.fixedDamage = damage;
    }

    public void setPhysicalDefense(final short PhysicalDefense) {
        this.PhysicalDefense = PhysicalDefense;
    }

    public final void setMagicDefense(final short MagicDefense) {
        this.MagicDefense = MagicDefense;
    }

    public final void setEva(final short eva) {
        this.eva = eva;
    }

    public void setOnlyNormalAttack(boolean onlyNormalAttack) {
        this.onlyNormalAttack = onlyNormalAttack;
    }

    public BanishInfo getBanishInfo() {
        return banish;
    }

    public void setBanishInfo(BanishInfo banish) {
        this.banish = banish;
    }

    public int getRemoveAfter() {
        return removeAfter;
    }

    public void setRemoveAfter(int removeAfter) {
        this.removeAfter = removeAfter;
    }

    public void setrareItemDropLevel(byte rareItemDropLevel) {
        this.rareItemDropLevel = rareItemDropLevel;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    public boolean isBoss() {
        return boss;
    }

    public void setFfaLoot(boolean ffaLoot) {
        this.ffaLoot = ffaLoot;
    }

    public boolean isFfaLoot() {
        return ffaLoot;
    }

    public void setExplosiveReward(boolean isExplosiveReward) {
        this.isExplosiveReward = isExplosiveReward;
    }

    public boolean isExplosiveReward() {
        return isExplosiveReward;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public boolean getMobile() {
        return mobile;
    }

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public List<Integer> getRevives() {
        return revives;
    }

    public void setRevives(List<Integer> revives) {
        this.revives = revives;
    }

    public void setUndead(boolean undead) {
        this.undead = undead;
    }

    public void setEffectiveness(Element e, ElementalEffectiveness ee) {
        resistance.put(e, ee);
    }

    public ElementalEffectiveness getEffectiveness(Element e) {
        ElementalEffectiveness elementalEffectiveness = resistance.get(e);
        if (elementalEffectiveness == null) {
            return ElementalEffectiveness.NORMAL;
        } else {
            return elementalEffectiveness;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getTagColor() {
        return tagColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = (byte) tagColor;
    }

    public byte getTagBgColor() {
        return tagBgColor;
    }

    public void setTagBgColor(int tagBgColor) {
        this.tagBgColor = (byte) tagBgColor;
    }

    public void setSkills(List<OdinPair<Integer, Integer>> skill_) {
        for (OdinPair<Integer, Integer> skill : skill_) {
            skills.add(skill);
        }
    }

    public List<OdinPair<Integer, Integer>> getSkills() {
        return Collections.unmodifiableList(this.skills);
    }

    public byte getNoSkills() {
        return (byte) skills.size();
    }

    public boolean hasSkill(int skillId, int level) {
        for (OdinPair<Integer, Integer> skill : skills) {
            if (skill.getLeft() == skillId && skill.getRight() == level) {
                return true;
            }
        }
        return false;
    }

    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }

    public boolean isFirstAttack() {
        return firstAttack;
    }

    public void setCP(byte cp) {
        this.cp = cp;
    }

    public void setPoint(int cp) {
        this.point = cp;
    }

    public int getPoint() {
        return point;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public void setNoDoom(boolean doom) {
        this.noDoom = doom;
    }

    public boolean isNoDoom() {
        return noDoom;
    }

    public void setBuffToGive(int buff) {
        this.buffToGive = buff;
    }

    public int getBuffToGive() {
        return buffToGive;
    }

    public byte getHPDisplayType() {
        return HPDisplayType;
    }

    public void setHPDisplayType(byte HPDisplayType) {
        this.HPDisplayType = HPDisplayType;
    }

    public void setDropItemPeriod(int d) {
        this.dropItemPeriod = d;
    }
}
