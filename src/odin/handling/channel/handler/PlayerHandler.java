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
package odin.handling.channel.handler;

import java.awt.Point;
import odin.client.inventory.IItem;
import odin.client.ISkill;
import odin.client.SkillFactory;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import odin.client.MapleBuffStat;
import odin.client.MapleClient;
import odin.client.MapleCharacter;
import odin.client.PlayerStats;
import tacos.config.Region;
import tacos.config.Version;
import tacos.wz.data.MobWz;
import tacos.wz.data.SkillWz;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import odin.server.MapleStatEffect;
import odin.server.life.MapleMonster;
import odin.server.life.MobAttackInfo;
import odin.server.life.MobSkill;
import odin.server.maps.FieldLimitType;
import odin.server.maps.MapleMap;
import tacos.packet.ops.OpsAttackIndex;
import tacos.packet.ops.OpsSkill;
import tacos.packet.response.ResCUserRemote.UserHitData;

public class PlayerHandler {

    public static boolean OnUserHit(MapleCharacter chr, ClientPacket cp) {

        MapleMap map = chr.getMap();
        UserHitData uhd = new UserHitData();

        uhd.dwCharacterID = chr.getId();

        int unk1 = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode4() : 0;
        int time = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        uhd.nAttackIdx = cp.Decode1();
        byte nMagicElemAttr = Version.LessOrEqual(Region.KMS, 43) ? 0 : cp.Decode1();
        uhd.nDamage = cp.Decode4();
        byte unk3 = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode1() : 0;
        byte unk4 = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode1() : 0;

        boolean is_mob_attack = false;
        int mpattack = 0;
        boolean is_pg = false;
        boolean isDeadlyAttack = false;
        PlayerStats stats = chr.getStat();
        OpsAttackIndex ops = OpsAttackIndex.find(uhd.nAttackIdx);
        int m_dwMobID = 0;

        switch (ops) {
            case AttackIndex_Counter:
            case AttackIndex_Obstacle:
            case AttackIndex_Stat: {
                // no mob.
                short dwObstacleData = cp.Decode2();
                break;
            }
            default: {
                if (uhd.nAttackIdx < 0) {
                    // not coded.
                    chr.DebugMsg("OnUserHit : not coded, nAttackIdx =" + uhd.nAttackIdx + ", nDamage = " + uhd.nDamage);
                    return true;
                }
                // mob attack.
            }
            case AttackIndex_Mob_Physical:
            case AttackIndex_Mob_Magic: {
                // mob attack.
                is_mob_attack = true;
                uhd.dwTemplateID = cp.Decode4(); // mob wz id.
                m_dwMobID = cp.Decode4(); // mob object id.
                uhd.nLeft = cp.Decode1();
                uhd.nReflect = cp.Decode1();
                byte unk7 = cp.Decode1();
                //
                if (uhd.nReflect != 0 || unk7 == 2) {
                    // 1-4-1-2-2-2-2
                    uhd.bPowerGuard = cp.Decode1();
                    uhd.m_dwMobID = cp.Decode4(); // mob object id.
                    uhd.nHitAction = cp.Decode1();
                    uhd.ptHit_x = cp.Decode2();
                    uhd.ptHit_y = cp.Decode2();
                    short chr_x = cp.Decode2();
                    short chr_y = cp.Decode2();
                }
                break;
            }
        }

        short unk8 = Version.GreaterOrEqual(Region.JMS, 187) ? cp.Decode1() : 0;

        chr.DebugMsg("OnUserHit : nAttackIdx =" + uhd.nAttackIdx + ", nDamage = " + uhd.nDamage);

        uhd.nDelta = uhd.nDamage;
        if (!is_mob_attack) {
            if (uhd.nDamage < 0) {
                // hack.
                return true;
            }
            map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
            chr.getStat().setHp(chr.getStat().getHp() - uhd.nDamage);
            chr.sendStatChanged();
            return true;
        }

        MapleMonster monster = map.getMonsterByOid(m_dwMobID);
        if (monster == null || monster.getId() != uhd.dwTemplateID) {
            return true;
        }
        // fake skill.
        if (uhd.nDamage == -1) {
            OpsSkill fake_skill = chr.getFakeSkill();
            if (fake_skill == OpsSkill.UNKNOWN) {
                // hack.
                return true;
            }
            uhd.nSkillID = fake_skill.get();
            map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
            return true;
        }
        if (uhd.nDamage < 0) {
            return true;
        }

        if (uhd.nReflect != 0) {
            if (uhd.bPowerGuard != 0) {
                Integer rate = chr.getBuffedValue(MapleBuffStat.POWERGUARD);
                if (rate == null) {
                    return true;
                }
                int reflect_damage = (int) (uhd.nDamage / 100.0 * rate);
                uhd.nDelta = uhd.nDamage - reflect_damage;
                monster.damage(chr, reflect_damage, true);
                chr.getStat().setHp(chr.getStat().getHp() - uhd.nDelta);
                map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);
                chr.sendStatChanged();
                chr.DebugMsg("PowerGuard : " + uhd.nDamage + " -> " + uhd.nDelta + ", " + reflect_damage);
                return true;
            }
        }

        MobAttackInfo attackInfo = MobWz.get().getMobAttackInfo(monster, uhd.nAttackIdx);
        if (attackInfo != null) {
            isDeadlyAttack = attackInfo.isDeadlyAttack();
            mpattack = isDeadlyAttack ? (stats.getMp() - 1) : attackInfo.getMpBurn();
            MobSkill mob_skill = SkillWz.get().getMobSkillData(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
            if (mob_skill != null) {
                if (uhd.nDamage != 0) {
                    mob_skill.applyEffect(chr, monster, false);
                }
            }
            monster.setMp(monster.getMp() - attackInfo.getMpCon());
        }

        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            chr.cancelMorphs();
        }
        if (0 < uhd.nReflect) {
            MobSkill skill = SkillWz.get().getMobSkillData(0, uhd.nReflect);
            if (skill != null) {
                skill.applyEffect(chr, monster, false);
            }
        }
        switch (chr.getJob()) {
            case 112: {
                ISkill skill = SkillFactory.getSkill(1120004);
                if (chr.getSkillLevel(skill) > 0) {
                    uhd.nDelta = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * uhd.nDamage);
                }
                break;
            }
            case 122: {
                ISkill skill = SkillFactory.getSkill(1220005);
                if (chr.getSkillLevel(skill) > 0) {
                    uhd.nDelta = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * uhd.nDamage);
                }
                break;
            }
            case 132: {
                ISkill skill = SkillFactory.getSkill(1320005);
                if (chr.getSkillLevel(skill) > 0) {
                    uhd.nDelta = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * uhd.nDamage);
                }
                break;
            }
        }
        MapleStatEffect magicShield = chr.getStatForBuff(MapleBuffStat.MAGIC_SHIELD);
        if (magicShield != null) {
            uhd.nDelta -= (int) ((magicShield.getX() / 100.0) * uhd.nDamage);
        }
        MapleStatEffect blueAura = chr.getStatForBuff(MapleBuffStat.BLUE_AURA);
        if (blueAura != null) {
            uhd.nDelta -= (int) ((blueAura.getY() / 100.0) * uhd.nDamage);
        }
        if (chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC) != null && chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB) != null) {
            double buff = chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC).doubleValue();
            double buffz = chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB).doubleValue();
            if ((int) ((buff / 100.0) * chr.getStat().getMaxHp()) <= uhd.nDamage) {
                uhd.nDelta = uhd.nDamage - (int) ((buffz / 100.0) * uhd.nDamage);
                chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
                chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
            }
        }
        if (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD) != null) {
            int hploss = 0, mploss = 0;
            if (isDeadlyAttack) {
                if (stats.getHp() > 1) {
                    hploss = stats.getHp() - 1;
                }
                if (stats.getMp() > 1) {
                    mploss = stats.getMp() - 1;
                }
                if (chr.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                    mploss = 0;
                }
                chr.addMPHP(-hploss, -mploss);
            } else {
                mploss = (int) (uhd.nDamage * (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD).doubleValue() / 100.0)) + mpattack;
                hploss = uhd.nDamage - mploss;
                if (chr.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                    mploss = 0;
                } else if (mploss > stats.getMp()) {
                    mploss = stats.getMp();
                    hploss = uhd.nDamage - mploss + mpattack;
                }
                chr.addMPHP(-hploss, -mploss);
            }

        } else if (chr.getBuffedValue(MapleBuffStat.MESOGUARD) != null) {
            uhd.nDelta = (uhd.nDamage % 2 == 0) ? uhd.nDamage / 2 : (uhd.nDamage / 2 + 1);

            int mesoloss = (int) (uhd.nDamage * (chr.getBuffedValue(MapleBuffStat.MESOGUARD).doubleValue() / 100.0));
            if (chr.getMeso() < mesoloss) {
                chr.gainMeso(-chr.getMeso(), false);
                chr.cancelBuffStats(MapleBuffStat.MESOGUARD);
            } else {
                chr.gainMeso(-mesoloss, false);
            }
            if (isDeadlyAttack && stats.getMp() > 1) {
                mpattack = stats.getMp() - 1;
            }
            chr.addMPHP(-uhd.nDelta, -mpattack);
        } else {
            if (isDeadlyAttack) {
                chr.addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0, stats.getMp() > 1 ? -(stats.getMp() - 1) : 0);
            } else {
                chr.addMPHP(-uhd.nDamage, -mpattack);
            }
        }
        chr.handleBattleshipHP(-uhd.nDamage);

        chr.sendStatChanged();
        map.broadcastMessage(chr, ResCUserRemote.UserHit(uhd), false);

        if (chr.isCloning()) {
            MapleCharacter chr_clone = chr.getClone();
            uhd.dwCharacterID = chr_clone.getId();
            map.broadcastMessageClone(chr_clone, ResCUserRemote.UserHit(uhd));
        }
        return true;
    }

    public static final void UseItemEffect(final int itemId, final MapleClient c, final MapleCharacter chr) {
        final IItem toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);
        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
            chr.updateInv();
            return;
        }
        if (itemId != 5510000) {
            chr.setItemEffect(itemId);
        }
        chr.getMap().broadcastMessage(chr, ResCUserRemote.UserSetActiveEffectItem(chr, itemId), false);
    }

    public static final void SkillEffect(MapleCharacter chr, int skill_id, byte skill_level, short action, byte m_nPrepareSkillActionSpeed) {

        final ISkill skill = SkillFactory.getSkill(skill_id);
        if (chr == null) {
            return;
        }
        final int skilllevel_serv = chr.getSkillLevel(skill);

        if (skilllevel_serv > 0 && skilllevel_serv == skill_level && skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            chr.getMap().broadcastMessage(chr, ResCUserRemote.UserSkillPrepare(chr, skill_id, skill_level, action, m_nPrepareSkillActionSpeed), false);
        }

        // クローン : 暴風とか
        if (chr.isCloning()) {
            MapleCharacter chr_clone = chr.getClone();
            chr.getMap().broadcastMessageClone(chr_clone, ResCUserRemote.UserSkillPrepare(chr_clone, skill_id, skill_level, action, m_nPrepareSkillActionSpeed));
        }
    }

    public static final void SpecialMove(MapleCharacter chr, ClientPacket cp, int skillid, int skillLevel, Point pos) {
        final ISkill skill = SkillFactory.getSkill(skillid);
        final MapleStatEffect effect = skill.getEffect(chr.getSkillLevel(GameConstants.getLinkedAranSkill(skillid)));

        if (effect.getCooldown() > 0) {
            if (chr.skillisCooling(skillid)) {
                chr.sendStatChanged(true);
                return;
            }
            if (skillid != 5221006) { // Battleship
                chr.SendPacket(ResCUserLocal.SkillCooltimeSet(skillid, effect.getCooldown()));
                chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown() * 1000);
            }
        }

        switch (skillid) {
            case 1121001:
            case 1221001:
            case 1321001:
            case 9001020: // GM magnet
                /*
                final byte number_of_mobs = slea.readByte();
                slea.skip(3);
                for (int i = 0; i < number_of_mobs; i++) {
                    int mobId = slea.readInt();

                    final MapleMonster mob = chr.getMap().getMonsterByOid(mobId);
                    if (mob != null) {
//			chr.getMap().broadcastMessage(chr, MaplePacketCreator.showMagnet(mobId, slea.readByte()), chr.getPosition());
                        mob.switchController(chr, mob.isControllerHasAggro());
                    }
                }
                chr.getMap().broadcastMessage(chr, ResCUserRemote.showBuffeffect(chr.getId(), skillid, 1, slea.readByte()), chr.getPosition());
                c.getSession().write(MaplePacketCreator.enableActions());
                 */
                chr.sendStatChanged(true);
                break;
            default:
                if (pos == null) {
                    pos = chr.getPosition();
                }

                if (effect.isMagicDoor()) { // Mystic Door
                    if (!FieldLimitType.MysticDoor.check(chr.getMap().getFieldLimit())) {
                        effect.applyTo(chr, pos);
                    } else {
                        chr.sendStatChanged(true);
                    }
                } else {
                    final int mountid = MapleStatEffect.parseMountInfo(chr, skill.getId());
                    if (mountid != 0 && mountid != GameConstants.getMountItem(skill.getId()) && chr.getBuffedValue(MapleBuffStat.MONSTER_RIDING) == null && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118/*-122*/) == null) {
                        if (!GameConstants.isMountItemAvailable(mountid, chr.getJob())) {
                            chr.sendStatChanged(true);
                            return;
                        }
                    }
                    effect.applyTo(chr, pos);
                }
                break;
        }
    }
}
