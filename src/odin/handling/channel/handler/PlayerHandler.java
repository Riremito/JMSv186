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
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.wz.data.MobWz;
import tacos.wz.data.SkillWz;
import tacos.packet.ClientPacket;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCUserLocal;
import tacos.packet.response.ResCUserRemote;
import odin.server.MapleStatEffect;
import odin.server.Randomizer;
import odin.server.life.MapleMonster;
import odin.server.life.MobAttackInfo;
import odin.server.life.MobSkill;
import odin.server.maps.FieldLimitType;

public class PlayerHandler {

    public static final void TakeDamage(ClientPacket cp, final MapleClient c, final MapleCharacter chr) {
        //System.out.println(slea.toString());
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            int unk1 = cp.Decode4();
        }

        int timestamp = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode4();
        chr.updateTick(timestamp);

        final byte type = cp.Decode1(); //-4 is mist, -3 and -2 are map damage.
        if (Version.GreaterOrEqual(Region.KMS, 55) || ServerConfig.JMS147orLater()) {
            cp.Decode1(); // Element - 0x00 = elementless, 0x01 = ice, 0x02 = fire, 0x03 = lightning
        }
        int damage = cp.Decode4();

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            byte unk2 = cp.Decode1();
            byte unk3 = cp.Decode1();
        }

        int oid = 0;
        int monsteridfrom = 0;
        int reflect = 0;
        byte direction = 0;
        int pos_x = 0;
        int pos_y = 0;
        int fake = 0;
        int mpattack = 0;
        boolean is_pg = false;
        boolean isDeadlyAttack = false;
        MapleMonster attacker = null;
        if (chr == null || chr.isHidden() || chr.getMap() == null) {
            return;
        }

        if (/*chr.isGM() && */chr.isInvincible()) {
            return;
        }
        final PlayerStats stats = chr.getStat();
        if (type != -2 && type != -3 && type != -4) { // Not map damage
            monsteridfrom = cp.Decode4();
            oid = cp.Decode4();
            attacker = chr.getMap().getMonsterByOid(oid);
            direction = cp.Decode1();

            if (attacker == null) {
                return;
            }
            if (type != -1) { // Bump damage
                final MobAttackInfo attackInfo = MobWz.get().getMobAttackInfo(attacker, type);
                if (attackInfo != null) {
                    if (attackInfo.isDeadlyAttack()) {
                        isDeadlyAttack = true;
                        mpattack = stats.getMp() - 1;
                    } else {
                        mpattack += attackInfo.getMpBurn();
                    }
                    final MobSkill skill = SkillWz.get().getMobSkillData(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                    if (skill != null && (damage == -1 || damage > 0)) {
                        //skill.applyEffect(chr, attacker, false);
                    }
                    attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
                }
            }
        }

        if (damage == -1) {
            fake = 4020002 + ((chr.getJob() / 10 - 40) * 100000);
        } else if (damage < -1) {
            return;
        }

        if (damage > 0) {
            if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
                chr.cancelMorphs();
            }
            //if (slea.available() == 3) {
            byte level = cp.Decode1();
            if (level > 0) {
                final MobSkill skill = SkillWz.get().getMobSkillData(cp.Decode2(), level);
                if (skill != null) {
                    //skill.applyEffect(chr, attacker, false);
                }
            }
            //}
            if (type != -2 && type != -3 && type != -4) {
                final int bouncedam_ = (Randomizer.nextInt(100) < chr.getStat().DAMreflect_rate ? chr.getStat().DAMreflect : 0) + (type == -1 && chr.getBuffedValue(MapleBuffStat.POWERGUARD) != null ? chr.getBuffedValue(MapleBuffStat.POWERGUARD) : 0) + (type == -1 && chr.getBuffedValue(MapleBuffStat.PERFECT_ARMOR) != null ? chr.getBuffedValue(MapleBuffStat.PERFECT_ARMOR) : 0);
                if (bouncedam_ > 0 && attacker != null) {
                    long bouncedamage = (long) (damage * bouncedam_ / 100);
                    bouncedamage = Math.min(bouncedamage, attacker.getMobMaxHp() / 10);
                    attacker.damage(chr, bouncedamage, true);
                    damage -= bouncedamage;
                    chr.getMap().broadcastMessageTo(chr, ResCMobPool.MobDamaged(attacker, bouncedamage), chr.getPosition());
                    is_pg = true;
                }
            }
            if (type != -1 && type != -2 && type != -3 && type != -4) {
                switch (chr.getJob()) {
                    case 112: {
                        final ISkill skill = SkillFactory.getSkill(1120004);
                        if (chr.getSkillLevel(skill) > 0) {
                            damage = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * damage);
                        }
                        break;
                    }
                    case 122: {
                        final ISkill skill = SkillFactory.getSkill(1220005);
                        if (chr.getSkillLevel(skill) > 0) {
                            damage = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * damage);
                        }
                        break;
                    }
                    case 132: {
                        final ISkill skill = SkillFactory.getSkill(1320005);
                        if (chr.getSkillLevel(skill) > 0) {
                            damage = (int) ((skill.getEffect(chr.getSkillLevel(skill)).getX() / 1000.0) * damage);
                        }
                        break;
                    }
                }
            }
            final MapleStatEffect magicShield = chr.getStatForBuff(MapleBuffStat.MAGIC_SHIELD);
            if (magicShield != null) {
                damage -= (int) ((magicShield.getX() / 100.0) * damage);
            }
            final MapleStatEffect blueAura = chr.getStatForBuff(MapleBuffStat.BLUE_AURA);
            if (blueAura != null) {
                damage -= (int) ((blueAura.getY() / 100.0) * damage);
            }
            if (chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC) != null && chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB) != null) {
                double buff = chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_PROC).doubleValue();
                double buffz = chr.getBuffedValue(MapleBuffStat.SATELLITESAFE_ABSORB).doubleValue();
                if ((int) ((buff / 100.0) * chr.getStat().getMaxHp()) <= damage) {
                    damage -= (int) ((buffz / 100.0) * damage);
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
                    //} else if (mpattack > 0) {
                    //    chr.addMPHP(-damage, -mpattack);
                } else {
                    mploss = (int) (damage * (chr.getBuffedValue(MapleBuffStat.MAGIC_GUARD).doubleValue() / 100.0)) + mpattack;
                    hploss = damage - mploss;
                    if (chr.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                        mploss = 0;
                    } else if (mploss > stats.getMp()) {
                        mploss = stats.getMp();
                        hploss = damage - mploss + mpattack;
                    }
                    chr.addMPHP(-hploss, -mploss);
                }

            } else if (chr.getBuffedValue(MapleBuffStat.MESOGUARD) != null) {
                damage = (damage % 2 == 0) ? damage / 2 : (damage / 2 + 1);

                final int mesoloss = (int) (damage * (chr.getBuffedValue(MapleBuffStat.MESOGUARD).doubleValue() / 100.0));
                if (chr.getMeso() < mesoloss) {
                    chr.gainMeso(-chr.getMeso(), false);
                    chr.cancelBuffStats(MapleBuffStat.MESOGUARD);
                } else {
                    chr.gainMeso(-mesoloss, false);
                }
                if (isDeadlyAttack && stats.getMp() > 1) {
                    mpattack = stats.getMp() - 1;
                }
                chr.addMPHP(-damage, -mpattack);
            } else {
                if (isDeadlyAttack) {
                    chr.addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0, stats.getMp() > 1 ? -(stats.getMp() - 1) : 0);
                } else {
                    chr.addMPHP(-damage, -mpattack);
                }
            }
            chr.handleBattleshipHP(-damage);
        }
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, ResCUserRemote.Hit(chr, type, monsteridfrom, damage, direction, reflect, is_pg, oid, fake), false);
        }

        // クローン : 被ダメージ
        if (chr.isCloning()) {
            MapleCharacter chr_clone = chr.getClone();
            chr.getMap().broadcastMessageClone(chr_clone, ResCUserRemote.Hit(chr_clone, type, monsteridfrom, damage, direction, reflect, is_pg, oid, fake));
        }
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
