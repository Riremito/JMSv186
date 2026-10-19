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

import tacos.packet.ops.OpsMobAppear;
import odin.client.inventory.Equip;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import odin.constants.GameConstants;
import odin.client.inventory.Item;
import odin.client.Skill;
import odin.client.MapleCharacter;
import odin.client.inventory.MapleInventoryType;
import tacos.client.TacosClient;
import odin.client.SkillFactory;
import odin.client.status.MonsterStatus;
import odin.client.status.MonsterStatusEffect;
import odin.handling.world.MapleParty;
import odin.handling.world.MaplePartyCharacter;
import java.awt.Point;
import tacos.packet.ops.OpsFieldEffect;
import tacos.packet.response.builder.PB_FieldEffect;
import tacos.packet.response.ResCField;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.ops.OpsBroadcastMsg;
import tacos.packet.response.builder.PB_BroadcastMsg;
import tacos.odin.OdinEventInstanceManager;
import odin.server.MapleItemInformationProvider;
import odin.server.Randomizer;
import odin.server.maps.MapScriptMethods;
import odin.server.maps.MapleMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsMobLeaveField;
import tacos.server.map.TacosReward;
import tacos.wz.WzXML;
import odin.server.life.Element;
import odin.server.life.MapleMonsterStats;
import odin.server.life.MonsterDropEntry;
import odin.server.life.OverrideMonsterStats;

/**
 *
 * @author Riremito
 */
public class TacosMonster extends TacosMapObject {

    private int id;
    private int f;
    private int cy;
    private int rx0;
    private int rx1;
    private int nHomeFoothold = 0;
    private int nAppearType = -1;
    private OpsMobAppear appear_type = OpsMobAppear.MOBAPPEAR_NORMAL;
    private int dwSummonOption = 0;
    public boolean bNextAttackPossible = false;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getF() {
        return this.f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getCy() {
        return this.cy;
    }

    public void setCy(int cy) {
        this.cy = cy;
    }

    public int getRx0() {
        return this.rx0;
    }

    public void setRx0(int rx0) {
        this.rx0 = rx0;
    }

    public int getRx1() {
        return this.rx1;
    }

    public void setRx1(int rx1) {
        this.rx1 = rx1;
    }

    public int getHomeFoothold() {
        return this.nHomeFoothold;
    }

    public void setHomeFoothold(int nHomeFoothold) {
        this.nHomeFoothold = nHomeFoothold;
    }

    public OpsMobAppear getAT() {
        return this.appear_type;
    }

    public void setAT(OpsMobAppear appear_type) {
        this.appear_type = appear_type;
    }

    public int getATEx() {
        return this.nAppearType;
    }

    public void setATEx(int nAppearType) {
        this.nAppearType = nAppearType;
    }

    public int getSummonOption() {
        return this.dwSummonOption;
    }

    public void setSummonOption(int dwSummonOption) {
        this.dwSummonOption = dwSummonOption;
    }

    public boolean getNextAttackPossible() {
        return this.bNextAttackPossible;
    }

    public void setNextAttackPossible(boolean bNextAttackPossible) {
        this.bNextAttackPossible = bNextAttackPossible;
    }

    private int last_hit_skill_id = 0;

    public int getLastHitSkillId() {
        return this.last_hit_skill_id;
    }

    public void setLastHitSkillId(int last_hit_skill_id) {
        this.last_hit_skill_id = last_hit_skill_id;
    }


    private int stance;

    public int getStance() {
        return stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public boolean isFacingLeft() {
        return getStance() % 2 != 0;
    }

    public int getFacingDirection() {
        return getStance() % 2;
    }

    private MapleMonsterStats stats;
    private OverrideMonsterStats ostats = null;
    private long hp;
    private int mp;
    private int lastNode = -1;
    private int lastNodeController = -1;
    private TacosMonster sponge = null;
    private int highestDamageChar = 0; // Just a reference for monster EXP distribution after dead
    private int stolen = -1; //monster can only be stolen ONCE
    private byte venom_counter;
    private byte carnivalTeam;
    private MapleMap map;
    private boolean fake;
    private boolean dropsDisabled;
    private final Collection<AttackerEntry> attackers = new LinkedList<>();
    private OdinEventInstanceManager eventInstance;
    private ServerPacket reflectpack = null;
    private ServerPacket nodepack = null;
    private Map<Integer, Long> usedSkills;
    private ScheduledFuture<?> dropItemSchedule;

    public TacosMonster(int id, MapleMonsterStats stats) {
        setId(id);
        initWithStats(stats);
    }

    public TacosMonster(TacosMonster monster) {
        setId(monster.getId());
        setF(monster.getF());
        setCy(monster.getCy());
        setRx0(monster.getRx0());
        setRx1(monster.getRx1());
        setFootholdId(monster.getFootholdId());
        setHomeFoothold(monster.getFootholdId());
        initWithStats(monster.stats);
    }

    private void initWithStats(MapleMonsterStats stats) {
        setStance(5);
        this.stats = stats;
        hp = stats.getHp();
        mp = stats.getMp();
        venom_counter = 0;
//	showdown = 100;
        carnivalTeam = -1;
        fake = false;
        dropsDisabled = false;

        if (stats.getNoSkills() > 0) {
            usedSkills = new HashMap<>();
        }
    }

    public MapleMonsterStats getStats() {
        return stats;
    }

    public void disableDrops() {
        this.dropsDisabled = true;
    }

    public boolean dropsDisabled() {
        return dropsDisabled;
    }

    public void setSponge(TacosMonster mob) {
        sponge = mob;
    }

    public void setMap(MapleMap map) {
        this.map = map;
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public long getMobMaxHp() {
        if (ostats != null) {
            return ostats.getHp();
        }
        return stats.getHp();
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public int getMobMaxMp() {
        if (ostats != null) {
            return ostats.getMp();
        }
        return stats.getMp();
    }

    public int getMobExp() {
        if (ostats != null) {
            return ostats.getExp();
        }
        return stats.getExp();
    }

    public void setOverrideStats(OverrideMonsterStats ostats) {
        this.ostats = ostats;
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }

    public TacosMonster getSponge() {
        return sponge;
    }

    public void damage(MapleCharacter from, long damage, boolean updateAttackTime) {
        damage(from, damage, updateAttackTime, 0);
    }

    public boolean isRaid() {
        if (!stats.isBoss()) {
            return false;
        }

        // 屋台等の状態変化系は初期状態は調整しない
        if (!stats.getRevives().isEmpty()) {
            return false;
        }

        int mapid = map.getId();
        // ボスモンスターレイド v155
        if (970030100 <= mapid && mapid <= 970042717) {
            return true;
        }
        // 海外ボスモンスターレイド v180
        if (889210100 <= mapid && mapid <= 889222517) {
            return true;
        }

        return false;
    }

    public void damage(MapleCharacter from, long damage, boolean updateAttackTime, int lastSkill) {
        if (from == null || damage <= 0 || !isAlive()) {
            return;
        }

        // ボスモンスターレイド
        // ボスが弱すぎるのでダメージ計算式を変更して最低20回の攻撃ヒット回数を求める
        if (isRaid()) {
            damage = Math.min(damage, (getMobMaxHp() / 20));
        } // test
        else {
            //damage *= 500;
            //damage = Math.min(damage, (getMobMaxHp() / 3));
        }

        AttackerEntry attacker = null;

        if (from.getParty() != null) {
            attacker = new PartyAttackerEntry(from.getParty().getId(), map.getChannel());
        } else {
            attacker = new SingleAttackerEntry(from, map.getChannel());
        }
        boolean replaced = false;
        for (AttackerEntry aentry : attackers) {
            if (aentry.equals(attacker)) {
                attacker = aentry;
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            attackers.add(attacker);
        }
        long rDamage = Math.max(0, Math.min(damage, hp));
        attacker.addDamage(from, rDamage, updateAttackTime);

        MapleMap from_map = from.getMap();
        if (stats.getSelfD() != -1) {
            hp -= rDamage;
            if (hp > 0) {
                for (AttackerEntry mattacker : attackers) {
                    for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                        if (cattacker.getAttacker().getMap() == from_map) { // current attacker is on the map of the monster
                            if (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000) {
                                cattacker.getAttacker().SendPacket(ResCMobPool.MobHPIndicator(this, (int) Math.ceil((hp * 100.0) / getMobMaxHp())));
                            }
                        }
                    }
                }
            } else { // Character killed it without explosing :(
                map.killMonster(this, from, true, false, OpsMobLeaveField.MOBLEAVEFIELD_ETC, lastSkill);
            }
        } else {
            if (sponge != null) {
                if (sponge.hp > 0) { // If it's still alive, dont want double/triple rewards
                    // Sponge are always in the same map, so we can use this.map
                    // The only mob that uses sponge are PB/HT
                    sponge.hp -= rDamage;

                    if (sponge.hp <= 0) {
                        map.killMonster(sponge, from, true, false, OpsMobLeaveField.MOBLEAVEFIELD_ETC, lastSkill);
                    } else {
                        map.broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_MobHPTag, PB_FieldEffect.builder().monster(sponge).build()));
                    }
                }
            }
            if (hp > 0) {
                hp -= rDamage;
                if (sponge == null/* && hp > 0*/) {
                    switch (stats.getHPDisplayType()) {
                        case 0:
                            map.broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_MobHPTag, PB_FieldEffect.builder().monster(this).build()));
                            break;
                        case 1:
                            map.broadcastPacket(ResCMobPool.MobDamaged(this, (int) damage, 1));
                            break;
                        case 2:
                            map.broadcastPacket(ResCMobPool.MobHPIndicator(this, (int) Math.ceil((hp * 100.0) / getMobMaxHp())));
                            from.mulung_EnergyModify(true);
                            break;
                        case 3:
                            for (AttackerEntry mattacker : attackers) {
                                for (AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                                    if (cattacker.getAttacker().getMap() == from_map) { // current attacker is on the map of the monster
                                        if (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000) {
                                            cattacker.getAttacker().SendPacket(ResCMobPool.MobHPIndicator(this, (int) Math.ceil((hp * 100.0) / getMobMaxHp())));
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }

                if (hp <= 0) {
                    if (stats.getHPDisplayType() == 0) {
                        this.setHp(0);
                        map.broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_MobHPTag, PB_FieldEffect.builder().monster(this).build()));
                    }
                    map.killMonster(this, from, true, false, OpsMobLeaveField.MOBLEAVEFIELD_ETC, lastSkill);
                }
            }
        }
    }

    public void heal(int hp, int mp, boolean broadcast) {
        long TotalHP = getHp() + hp;
        int TotalMP = getMp() + mp;

        if (TotalHP >= getMobMaxHp()) {
            setHp(getMobMaxHp());
        } else {
            setHp(TotalHP);
        }
        if (TotalMP >= getMp()) {
            setMp(getMp());
        } else {
            setMp(TotalMP);
        }
        if (broadcast) {
            map.broadcastPacket(ResCMobPool.MobDamaged(this, -hp, 0));
        } else if (sponge != null) { // else if, since only sponge doesn't broadcast
            sponge.hp += hp;
        }
    }

    private void giveExpToCharacter(MapleCharacter attacker, int exp, boolean highestDamage, int numExpSharers, byte pty, byte Class_Bonus_EXP_PERCENT, byte Premium_Bonus_EXP_PERCENT, int lastskillID) {
        if (highestDamage) {
            highestDamageChar = attacker.getId();
        }
        if (exp > 0) {
            exp *= attacker.getEXPMod() * (int) (attacker.getStat().expBuff / 100.0);
            exp *= attacker.getChannelServer().getExpRate();
            exp *= 1.25; // JMS
            //do this last just incase someone has a 2x exp card and its set to max value
            int Class_Bonus_EXP = 0;
            if (Class_Bonus_EXP_PERCENT > 0) {
                Class_Bonus_EXP = (int) ((exp / 100.0) * Class_Bonus_EXP_PERCENT);
            }
            int Premium_Bonus_EXP = 0;
            if (Premium_Bonus_EXP_PERCENT > 0) {
                Premium_Bonus_EXP = (int) ((exp / 100.0) * Premium_Bonus_EXP_PERCENT);
            }
            int Equipment_Bonus_EXP = (int) ((exp / 100.0) * attacker.getStat().equipmentBonusExp);
            if (0 < attacker.getFairyExp()) {
                Equipment_Bonus_EXP += (int) ((exp / 100.0) * attacker.getFairyExp());
            }

            attacker.gainExpMonster(exp, true, highestDamage, pty, Class_Bonus_EXP, Equipment_Bonus_EXP, Premium_Bonus_EXP);
        }
        attacker.mobKilled(getId(), lastskillID);
    }

    public int killBy(MapleCharacter killer, int lastSkill) {
        int totalBaseExp = getMobExp();
        AttackerEntry highest = null;
        long highdamage = 0;
        for (AttackerEntry attackEntry : attackers) {
            if (attackEntry.getDamage() > highdamage) {
                highest = attackEntry;
                highdamage = attackEntry.getDamage();
            }
        }
        int baseExp;
        for (AttackerEntry attackEntry : attackers) {
            baseExp = (int) Math.ceil(totalBaseExp * ((double) attackEntry.getDamage() / getMobMaxHp()));
            attackEntry.killedMob(getMap(), baseExp, attackEntry == highest, lastSkill);
        }

        spawnRevives();
        if (killer != null && killer.getPyramidSubway() != null) {
            killer.getPyramidSubway().onKill(killer);
        }
        TacosMonster oldSponge = getSponge();
        sponge = null;
        if (oldSponge != null && oldSponge.isAlive()) {
            boolean set = true;
            for (TacosMonster mons : map.getAllMonsters()) {
                if (mons.getObjectId() != oldSponge.getObjectId() && mons.getObjectId() != this.getObjectId() && (mons.getSponge() == oldSponge || mons.getSummonOption() == oldSponge.getObjectId())) { //sponge was this, please update
                    set = false;
                    break;
                }
            }
            if (set) { //all sponge monsters are dead, please kill off the sponge
                map.killMonster(oldSponge, killer, true, false, OpsMobLeaveField.MOBLEAVEFIELD_ETC);
            }
        }

        nodepack = null;
        reflectpack = null;
        cancelDropItem();
        int v1 = highestDamageChar;
        this.highestDamageChar = 0; //reset so we dont kill twice
        return v1;
    }

    public void spawnRevives() {
        // chaos zakum
        List<Integer> toSpawn = stats.getRevives();

        if (toSpawn == null) {
            return;
        }
        TacosMonster spongy = null;
        switch (getId()) {
            case 8810118:
            case 8810119:
            case 8810120:
            case 8810121: //must update sponges
                for (int i : toSpawn) {
                    TacosMonster mob = WzXML.MOB.createMob(i);

                    mob.setPosition(getPosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810119:
                        case 8810120:
                        case 8810121:
                        case 8810122:
                            spongy = mob;
                            break;
                    }
                }
                if (spongy != null) {
                    map.spawnRevives(spongy, this.getObjectId());
                    for (TacosMonster mons : map.getAllMonsters()) {
                        if (mons.getObjectId() != spongy.getObjectId() && (mons.getSponge() == this || mons.getSummonOption() == this.getObjectId())) { //sponge was this, please update
                            mons.setSponge(spongy);
                            mons.setSummonOption(spongy.getObjectId());
                        }
                    }
                }
                break;
            case 8810026:
            case 8810130:
            case 8820008:
            case 8820009:
            case 8820010:
            case 8820011:
            case 8820012:
            case 8820013: {
                List<TacosMonster> mobs = new ArrayList<>();

                for (int i : toSpawn) {
                    TacosMonster mob = WzXML.MOB.createMob(i);

                    mob.setPosition(getPosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810018: // Horntail Sponge
                        case 8810118:
                        case 8820009: // PinkBeanSponge0
                        case 8820010: // PinkBeanSponge1
                        case 8820011: // PinkBeanSponge2
                        case 8820012: // PinkBeanSponge3
                        case 8820013: // PinkBeanSponge4
                        case 8820014: // PinkBeanSponge5
                            spongy = mob;
                            break;
                        default:
                            mobs.add(mob);
                            break;
                    }
                }
                if (spongy != null) {
                    map.spawnRevives(spongy, this.getObjectId());

                    for (TacosMonster i : mobs) {
                        i.setSponge(spongy);
                        map.spawnRevives(i, this.getObjectId());
                    }
                }
                break;
            }
            default: {
                for (int i : toSpawn) {
                    TacosMonster mob = WzXML.MOB.createMob(i);

                    mob.setPosition(getPosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.spawnRevives(mob, this.getObjectId());

                    if (mob.getId() == 9300216) {
                        map.broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Sound, PB_FieldEffect.builder().wz_path("Dojang/clear").build()));
                        map.broadcastPacket(ResCField.FieldEffect(OpsFieldEffect.FieldEffect_Screen, PB_FieldEffect.builder().wz_path("dojang/end/clear").build()));
                    }
                }
                break;
            }
        }
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public void setCarnivalTeam(byte team) {
        carnivalTeam = team;
    }

    public byte getCarnivalTeam() {
        return carnivalTeam;
    }

    public void switchShammosController(MapleCharacter newController, boolean immediateAggro) {
        if (getId() == 9300275 && map.getId() >= 921120100 && map.getId() < 921120500) { //shammos
            if (lastNodeController != -1 && lastNodeController != newController.getId()) { //new controller, please re update
                resetShammos(newController.getClient());
            } else {
                setLastNodeController(newController.getId());
            }
        }
    }

    public void resetShammos(TacosClient client) {
        map.killAllMonsters(true);
        map.broadcastPacket(ResCWvsContext.BroadcastMsg(OpsBroadcastMsg.BM_EVENT, PB_BroadcastMsg.builder().message("A player has moved too far from Shammos. Shammos is going back to the start.").build()));
        for (MapleCharacter chr : map.getAllPlayers()) {
            chr.changeMapPortal(chr.getMap(), chr.getMap().getPortal(0));
        }
        MapScriptMethods.startScript_FirstUser(client, "shammos_Fenter");
    }

    public void sendSpawnData(TacosClient client) {
        if (!isAlive()) {
            return;
        }
        client.SendPacket(ResCMobPool.MobEnterField(this));
        if (reflectpack != null) {
            client.SendPacket(reflectpack);
        }
        if (lastNode >= 0) {
            client.SendPacket(ResCMobPool.MobRequestResultEscortInfo(this, map));
            if (getId() == 9300275 && map.getId() >= 921120100 && map.getId() < 921120500) { //shammos
                if (lastNodeController != -1) { //new controller, please re update. sendSpawn only comes when you get too far then come back anyway
                    resetShammos(client);
                } else {
                    setLastNodeController(client.getPlayer().getId());
                }
            }
        }
    }

    public void sendDestroyData(TacosClient client) {
        if (lastNode == -1) {
            client.SendPacket(ResCMobPool.MobLeaveField(this, OpsMobLeaveField.MOBLEAVEFIELD_REMAINHP));
        }
        if (getId() == 9300275 && map.getId() >= 921120100 && map.getId() < 921120500) { //shammos
            resetShammos(client);
        }
    }

    public OdinEventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public void applyStatus(MapleCharacter from, MonsterStatusEffect status, boolean poison, long duration, boolean venom) {
        applyStatus(from, status, poison, duration, venom, true);
    }

    public void applyStatus(MapleCharacter from, MonsterStatusEffect status, boolean poison, long duration, boolean venom, boolean checkboss) {
        if (!isAlive()) {
            return;
        }
        Skill skilz = SkillFactory.getSkill(status.getSkill());
        if (skilz != null) {
            switch (stats.getEffectiveness(skilz.getElement())) {
                case IMMUNE:
                case STRONG:
                    return;
                case NORMAL:
                case WEAK:
                    break;
                default:
                    return;
            }
        }
        // compos don't have an elemental (they have 2 - so we have to hack here...)
        int statusSkill = status.getSkill();
        switch (statusSkill) {
            case 2111006: { // FP compo
                switch (stats.getEffectiveness(Element.POISON)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            }
            case 2211006: { // IL compo
                switch (stats.getEffectiveness(Element.ICE)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            }
            case 4120005:
            case 4220005:
            case 14110004: {
                switch (stats.getEffectiveness(Element.POISON)) {
                    case WEAK:
                        return;
                }
                break;
            }
        }
        MonsterStatus stat = status.getStati();
        if (stats.isNoDoom() && stat == MonsterStatus.DOOM) {
            return;
        }

        if (stats.isBoss()) {
            if (stat == MonsterStatus.STUN) {
                return;
            }
            if (checkboss && stat != (MonsterStatus.SPEED) && stat != (MonsterStatus.NINJA_AMBUSH) && stat != (MonsterStatus.WATK)) {
                return;
            }
        }
        if (venom) {
            int poisonLevel = 0;
            int matk = 0;

            switch (from.getJob()) {
                case 412:
                    poisonLevel = from.getSkillLevel(SkillFactory.getSkill(4120005));
                    if (poisonLevel <= 0) {
                        return;
                    }
                    matk = SkillFactory.getSkill(4120005).getEffect(poisonLevel).getMatk();
                    break;
                case 422:
                    poisonLevel = from.getSkillLevel(SkillFactory.getSkill(4220005));
                    if (poisonLevel <= 0) {
                        return;
                    }
                    matk = SkillFactory.getSkill(4220005).getEffect(poisonLevel).getMatk();
                    break;
                case 1411:
                case 1412:
                    poisonLevel = from.getSkillLevel(SkillFactory.getSkill(14110004));
                    if (poisonLevel <= 0) {
                        return;
                    }
                    matk = SkillFactory.getSkill(14110004).getEffect(poisonLevel).getMatk();
                    break;
                case 434:
                    poisonLevel = from.getSkillLevel(SkillFactory.getSkill(4340001));
                    if (poisonLevel <= 0) {
                        return;
                    }
                    matk = SkillFactory.getSkill(4340001).getEffect(poisonLevel).getMatk();
                    break;
                default:
                    return; // Hack, using venom without the job required
            }
        }
    }

    public void setFake(boolean fake) {
        this.fake = fake;
    }

    public MapleMap getMap() {
        return map;
    }

    public List<SimpleImmutableEntry<Integer, Integer>> getSkills() {
        return stats.getSkills();
    }

    public boolean hasSkill(int skillId, int level) {
        return stats.hasSkill(skillId, level);
    }

    public long getLastSkillUsed(int skillId) {
        if (usedSkills.containsKey(skillId)) {
            return usedSkills.get(skillId);
        }
        return 0;
    }

    public void setLastSkillUsed(int skillId, long now, long cooltime) {
        switch (skillId) {
            case 140:
                usedSkills.put(skillId, now + (cooltime * 2));
                usedSkills.put(141, now);
                break;
            case 141:
                usedSkills.put(skillId, now + (cooltime * 2));
                usedSkills.put(140, now + cooltime);
                break;
            default:
                usedSkills.put(skillId, now + cooltime);
                break;
        }
    }

    public byte getNoSkills() {
        return stats.getNoSkills();
    }

    public boolean isFirstAttack() {
        return stats.isFirstAttack();
    }

    public int getBuffToGive() {
        return stats.getBuffToGive();
    }

    public TacosMonster getParent() {
        return this;
    }

    private static class AttackingMapleCharacter {

        private MapleCharacter attacker;
        private long lastAttackTime;

        public AttackingMapleCharacter(MapleCharacter attacker, long lastAttackTime) {
            super();
            this.attacker = attacker;
            this.lastAttackTime = lastAttackTime;
        }

        public long getLastAttackTime() {
            return lastAttackTime;
        }

        public MapleCharacter getAttacker() {
            return attacker;
        }
    }

    private interface AttackerEntry {

        List<AttackingMapleCharacter> getAttackers();

        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime);

        public long getDamage();

        public boolean contains(MapleCharacter chr);

        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill);
    }

    private final class SingleAttackerEntry implements AttackerEntry {

        private long damage = 0;
        private long lastAttackTime;
        private int chrid;
        private int channel;

        public SingleAttackerEntry(MapleCharacter from, int cserv) {
            this.chrid = from.getId();
            this.channel = cserv;
        }

        @Override
        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
            if (chrid == from.getId()) {
                this.damage += damage;
                if (updateAttackTime) {
                    lastAttackTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public List<AttackingMapleCharacter> getAttackers() {
            MapleCharacter chr = map.getPlayerById(chrid);
            if (chr != null) {
                return Collections.singletonList(new AttackingMapleCharacter(chr, lastAttackTime));
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public boolean contains(MapleCharacter chr) {
            return chrid == chr.getId();
        }

        @Override
        public long getDamage() {
            return damage;
        }

        @Override
        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill) {
            MapleCharacter chr = map.getPlayerById(chrid);
            if (chr != null && chr.isAlive()) {
                giveExpToCharacter(chr, baseExp, mostDamage, 1, (byte) 0, (byte) 0, (byte) 0, lastSkill);
            }
        }

        @Override
        public int hashCode() {
            return chrid;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            SingleAttackerEntry other = (SingleAttackerEntry) obj;
            return chrid == other.chrid;
        }
    }

    private static final class ExpMap {

        public final int exp;
        public final byte ptysize;
        public final byte Class_Bonus_EXP;
        public final byte Premium_Bonus_EXP;

        public ExpMap(int exp, byte ptysize, byte Class_Bonus_EXP, byte Premium_Bonus_EXP) {
            super();
            this.exp = exp;
            this.ptysize = ptysize;
            this.Class_Bonus_EXP = Class_Bonus_EXP;
            this.Premium_Bonus_EXP = Premium_Bonus_EXP;
        }
    }

    private static final class OnePartyAttacker {

        public MapleParty lastKnownParty;
        public long damage;
        public long lastAttackTime;

        public OnePartyAttacker(MapleParty lastKnownParty, long damage) {
            super();
            this.lastKnownParty = lastKnownParty;
            this.damage = damage;
            this.lastAttackTime = System.currentTimeMillis();
        }
    }

    private class PartyAttackerEntry implements AttackerEntry {

        private long totDamage;
        private final Map<Integer, OnePartyAttacker> attackers = new HashMap<>(6);
        private int partyid;
        private int channel;

        public PartyAttackerEntry(int partyid, int cserv) {
            this.partyid = partyid;
            this.channel = cserv;
        }

        public List<AttackingMapleCharacter> getAttackers() {
            List<AttackingMapleCharacter> ret = new ArrayList<>(attackers.size());
            for (Entry<Integer, OnePartyAttacker> entry : attackers.entrySet()) {
                MapleCharacter chr = map.getPlayerById(entry.getKey());
                if (chr != null) {
                    ret.add(new AttackingMapleCharacter(chr, entry.getValue().lastAttackTime));
                }
            }
            return ret;
        }

        private Map<MapleCharacter, OnePartyAttacker> resolveAttackers() {
            Map<MapleCharacter, OnePartyAttacker> ret = new HashMap<>(attackers.size());
            for (Entry<Integer, OnePartyAttacker> aentry : attackers.entrySet()) {
                MapleCharacter chr = map.getPlayerById(aentry.getKey());
                if (chr != null) {
                    ret.put(chr, aentry.getValue());
                }
            }
            return ret;
        }

        @Override
        public boolean contains(MapleCharacter chr) {
            return attackers.containsKey(chr.getId());
        }

        @Override
        public long getDamage() {
            return totDamage;
        }

        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime) {
            OnePartyAttacker oldPartyAttacker = attackers.get(from.getId());
            if (oldPartyAttacker != null) {
                oldPartyAttacker.damage += damage;
                oldPartyAttacker.lastKnownParty = from.getParty();
                if (updateAttackTime) {
                    oldPartyAttacker.lastAttackTime = System.currentTimeMillis();
                }
            } else {
                // TODO actually this causes wrong behaviour when the party changes between attacks
                // only the last setup will get exp - but otherwise we'd have to store the full party
                // constellation for every attack/everytime it changes, might be wanted/needed in the
                // future but not now
                OnePartyAttacker onePartyAttacker = new OnePartyAttacker(from.getParty(), damage);
                attackers.put(from.getId(), onePartyAttacker);
                if (!updateAttackTime) {
                    onePartyAttacker.lastAttackTime = 0;
                }
            }
            totDamage += damage;
        }

        @Override
        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill) {
            MapleCharacter pchr, highest = null;
            long iDamage, highestDamage = 0;
            int iexp = 0;
            MapleParty party;
            double averagePartyLevel, expWeight, levelMod, innerBaseExp, expFraction;
            List<MapleCharacter> expApplicable;
            Map<MapleCharacter, ExpMap> expMap = new HashMap<>(6);
            byte Class_Bonus_EXP;
            byte Premium_Bonus_EXP;
            byte added_partyinc = 0;

            for (Entry<MapleCharacter, OnePartyAttacker> attacker : resolveAttackers().entrySet()) {
                party = attacker.getValue().lastKnownParty;
                averagePartyLevel = 0;

                Class_Bonus_EXP = 0;
                Premium_Bonus_EXP = 0;
                expApplicable = new ArrayList<>();
                for (MaplePartyCharacter partychar : party.getMembers()) {
                    if (attacker.getKey().getLevel() - partychar.getLevel() <= 5 || stats.getLevel() - partychar.getLevel() <= 5) {
                        pchr = map.getPlayerById(partychar.getId());
                        if (pchr != null) {
                            if (pchr.isAlive() && pchr.getMap() == map) {
                                expApplicable.add(pchr);
                                averagePartyLevel += pchr.getLevel();
                                if (pchr.getStat().equippedWelcomeBackRing && Premium_Bonus_EXP == 0) {
                                    Premium_Bonus_EXP = 80;
                                }
                                if (pchr.getStat().hasPartyBonus && added_partyinc < 4) {
                                    added_partyinc++;
                                }
                            }
                        }
                    }
                }
                if (expApplicable.size() > 1) {
                    averagePartyLevel /= expApplicable.size();
                } else {
                    Class_Bonus_EXP = 0; //no class bonus if not in a party.
                }
                iDamage = attacker.getValue().damage;
                if (iDamage > highestDamage) {
                    highest = attacker.getKey();
                    highestDamage = iDamage;
                }
                innerBaseExp = baseExp * ((double) iDamage / totDamage);
                expFraction = innerBaseExp / (expApplicable.size() + 1);

                for (MapleCharacter expReceiver : expApplicable) {
                    iexp = expMap.get(expReceiver) == null ? 0 : expMap.get(expReceiver).exp;
                    expWeight = (expReceiver == attacker.getKey() ? 2.0 : 0.3); //hopefully this is correct o.o -/+0.4
                    levelMod = expReceiver.getLevel() / averagePartyLevel;
                    if (levelMod > 1.0 || attackers.containsKey(expReceiver.getId())) {
                        levelMod = 1.0;
                    }
                    iexp += (int) Math.round(expFraction * expWeight * levelMod);
                    expMap.put(expReceiver, new ExpMap(iexp, (byte) (expApplicable.size() + added_partyinc), Class_Bonus_EXP, Premium_Bonus_EXP));
                }
            }
            ExpMap expmap;
            for (Entry<MapleCharacter, ExpMap> expReceiver : expMap.entrySet()) {
                expmap = expReceiver.getValue();
                giveExpToCharacter(expReceiver.getKey(), expmap.exp, mostDamage ? expReceiver.getKey() == highest : false, expMap.size(), expmap.ptysize, expmap.Class_Bonus_EXP, expmap.Premium_Bonus_EXP, lastSkill);
            }
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = prime * result + partyid;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            PartyAttackerEntry other = (PartyAttackerEntry) obj;
            if (partyid != other.partyid) {
                return false;
            }
            return true;
        }
    }

    public int getStolen() {
        return stolen;
    }

    public void handleSteal(MapleCharacter chr) {
        Skill steal = SkillFactory.getSkill(4201004);
        int level = chr.getSkillLevel(steal);
        if (level > 0 && !getStats().isBoss() && stolen == -1 && steal.getEffect(level).makeChanceResult()) {
            ArrayList<MonsterDropEntry> dropEntry = new ArrayList<>(TacosReward.getMonsterDrops(getId()));
            Collections.shuffle(dropEntry);
            Item idrop;
            for (MonsterDropEntry d : dropEntry) {
                if (d.itemId > 0 && d.questid == 0 && steal.getEffect(level).makeChanceResult()) { //kinda op
                    if (GameConstants.getInventoryType(d.itemId) == MapleInventoryType.EQUIP) {
                        Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(d.itemId);
                        idrop = MapleItemInformationProvider.getInstance().randomizeStats(eq);
                    } else {
                        idrop = new Item(d.itemId, (byte) 0, (short) (d.Maximum != 1 ? Randomizer.nextInt(d.Maximum - d.Minimum) + d.Minimum : 1), (byte) 0);
                    }
                    stolen = d.itemId;
                    map.spawnMobDrop(idrop, map.calcDropPos(new Point(getPosition()), getPosition()), this, chr, (byte) 0, (short) 0, 0);
                    break;
                }
            }
        } else {
            stolen = 0; //failed once, may not go again
        }
    }

    public void setLastNode(int lastNode) {
        this.lastNode = lastNode;
    }

    public int getLastNode() {
        return lastNode;
    }

    public void setLastNodeController(int lastNode) {
        this.lastNodeController = lastNode;
    }

    public void cancelDropItem() {
        if (dropItemSchedule != null) {
            dropItemSchedule.cancel(false);
            dropItemSchedule = null;
        }
    }

    public ServerPacket getNodePacket() {
        return nodepack;
    }

    public void setNodePacket(ServerPacket np) {
        this.nodepack = np;
    }
}
