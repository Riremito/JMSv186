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
package tacos.packet.request.parse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import odin.client.ISkill;
import odin.client.MapleCharacter;
import odin.client.SkillFactory;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleStatEffect;
import odin.server.life.MapleMonster;
import tacos.client.TacosCalcDamage;
import tacos.config.DeveloperMode;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPair;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ParseCUser_Attack {

    public static class AttackPair {

        public int objectid;
        public List<OdinPair<Integer, Boolean>> attack;

        public AttackPair(int objectid, List<OdinPair<Integer, Boolean>> attack) {
            this.objectid = objectid;
            this.attack = attack;
        }
    }

    public long[] randoms;
    public int rand_size = 7;
    public int rand_counter = 0;
    public int skill;
    public List<AttackPair> allDamage;
    public List<Integer> allMeso;
    public Point position;
    public boolean real = true;
    public ClientPacketHeader header;
    public int CharacterId;
    public int X;
    public int Y;
    public int SkillLevel;
    public int nMastery;
    public int nBulletItemID;
    public int m_nLevel;
    public int FieldKey;
    public int HitKey;
    public int nSkillID;
    public int tKeyDown;
    public int exJablin = 0;
    public int BuffKey;
    public int AttackActionKey;
    public int nAttackActionType;
    public int nAttackSpeed;
    public int tAttackTime;
    public short ProperBulletPosition;
    public short pnCashItemPos;
    public int nShootRange0a;

    public ServerPacketHeader getHeader() {
        switch (header) {
            case CP_UserMeleeAttack -> {
                return ServerPacketHeader.LP_UserMeleeAttack;
            }
            case CP_UserShootAttack -> {
                return ServerPacketHeader.LP_UserShootAttack;
            }
            case CP_UserMagicAttack -> {
                return ServerPacketHeader.LP_UserMagicAttack;
            }
            case CP_UserBodyAttack -> {
                return ServerPacketHeader.LP_UserBodyAttack;
            }
            default -> {
            }
        }
        return ServerPacketHeader.UNKNOWN;
    }

    // hit count per mob
    public int getDamagePerMob() {
        return HitKey & 0x0F; // nDamagePerMob_1
    }

    // number of mobs
    public int getMobCount() {
        return (HitKey >> 4) & 0x0F; // nCount
    }

    public MapleStatEffect getAttackEffect(MapleCharacter chr, int skillLevel, ISkill skill_) {
        if (GameConstants.isMulungSkill(skill) || GameConstants.isPyramidSkill(skill)) {
            skillLevel = 1;
        } else if (skillLevel <= 0) {
            return null;
        }
        if (GameConstants.isLinkedAranSkill(skill)) {
            final ISkill skillLink = SkillFactory.getSkill(skill);
            return skillLink.getEffect(skillLevel);
        }
        return skill_.getEffect(skillLevel);
    }

    // BMS CUser::OnAttack
    public static ParseCUser_Attack parse(MapleCharacter chr, ClientPacketHeader header, ClientPacket cp) {
        ParseCUser_Attack attack = new ParseCUser_Attack();
        // attack type
        attack.header = header;
        // attacker data
        attack.CharacterId = chr.getId();
        attack.m_nLevel = chr.getLevel();
        attack.nSkillID = 0;
        attack.SkillLevel = 0;
        attack.nMastery = chr.getStat().passive_mastery();
        attack.nBulletItemID = 0;
        attack.X = chr.getPosition().x;
        attack.Y = chr.getPosition().y;
        attack.FieldKey = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
        // DR_Check
        if (Version.Equal(Region.KMST, 330) || Version.LessOrEqual(Region.KMS, 114)) {
            // ?
        } else if (ServerConfig.JMS180orLater() || Version.Equal(Region.BMS, 24)) {
            cp.Decode4(); // pDrInfo.dr0
            cp.Decode4(); // pDrInfo.dr1
        }
        attack.HitKey = cp.Decode1(); // nDamagePerMob | (16 * nCount)
        // DR_Check
        if (Version.Equal(Region.KMST, 330) || Version.LessOrEqual(Region.KMS, 114)) {
            // ?
        } else if (ServerConfig.JMS180orLater() || Version.Equal(Region.BMS, 24)) {
            cp.Decode4(); // pDrInfo.dr2
            cp.Decode4(); // pDrInfo.dr3
        }
        attack.nSkillID = cp.Decode4();
        attack.skill = attack.nSkillID; // old
        if (0 < attack.nSkillID) {
            // skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            attack.SkillLevel = chr.getSkillLevel(attack.nSkillID);
        }
        // v95 1 byte cd->nCombatOrders
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            cp.Decode1();
        }
        if (Version.Equal(Region.KMST, 330) || Version.LessOrEqual(Region.KMS, 114)) {
            // none
        } else if (ServerConfig.JMS180orLater() || Version.Equal(Region.BMS, 24)) {
            cp.Decode4(); // get_rand of DR_Check
            cp.Decode4(); // Crc32 of DR_Check
            // v95 4 bytes SKILLLEVELDATA::GetCrc
            if (Version.GreaterOrEqual(Region.GMS, 95)) {
                cp.Decode4();
                cp.Decode4();
                if (attack.header == ClientPacketHeader.CP_UserMagicAttack) {
                    cp.Decode4();
                    cp.Decode4();
                    cp.Decode4();
                    cp.Decode4();
                    cp.Decode4();
                    cp.Decode4();
                }
            }
        }
        if (Version.PostBB() && !Version.GreaterOrEqual(Region.GMS, 95)) {
            cp.Decode1();
        }
        if (Version.LessOrEqual(Region.KMS, 95) || Version.GreaterOrEqual(Region.GMS, 95)) {
            // ?
        } else if (ServerConfig.JMS164orLater() || Version.Equal(Region.BMS, 24)) {
            cp.Decode4(); // Crc
        }
        attack.tKeyDown = 0;
        if (TacosConstants.is_keydown_skill(attack.nSkillID)) {
            attack.tKeyDown = cp.Decode4();
        }
        attack.BuffKey = cp.Decode1();
        if (Version.Equal(Region.KMST, 330) || Version.GreaterOrEqual(Region.JMS, 187) || Version.GreaterOrEqual(Region.KMS, 114) || ServerConfig.JMS194orLater() || Version.GreaterOrEqual(Region.GMS, 95)) {
            if (attack.header == ClientPacketHeader.CP_UserShootAttack) {
                attack.exJablin = cp.Decode1();
            }
        }
        if (Version.LessOrEqual(Region.KMS, 65) || Version.LessOrEqual(Region.JMS, 165) || Version.Equal(Region.BMS, 24)) {
            attack.AttackActionKey = cp.Decode1();
        } else {
            attack.AttackActionKey = cp.Decode2(); // nAttackAction & 0x7FFF | (bLeft << 15)
        }
        if (Version.PostBB()) {
            if (Version.Equal(Region.KMST, 330) || Version.Equal(Region.JMS, 187)) {
                // none
            } else {
                cp.Decode4(); // JMS188
            }
        }
        // v95 4 bytes crc
        attack.nAttackActionType = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
        attack.nAttackSpeed = cp.Decode1();
        attack.tAttackTime = Version.LessOrEqual(Region.KMS, 1) ? 0 : cp.Decode4();
        if (Version.GreaterOrEqual(Region.KMS, 95) || ServerConfig.JMS186orLater()) {
            cp.Decode4(); // dwID
        }
        if (attack.header == ClientPacketHeader.CP_UserShootAttack) {
            attack.ProperBulletPosition = cp.Decode2();
            attack.pnCashItemPos = cp.Decode2();
            attack.nShootRange0a = cp.Decode1(); // nShootRange0a, GetShootRange0 func, is AOE or not, TT/ Avenger = 41, Showdown = 0
            if (0 < attack.nShootRange0a && !TacosConstants.is_shadow_meso(attack.nSkillID) /* SOUL ARROW CHECK.*/) {
                IItem BulletItem;
                if (0 < attack.pnCashItemPos) {
                    BulletItem = chr.getInventory(MapleInventoryType.CASH).getItem(attack.pnCashItemPos);
                } else {
                    BulletItem = chr.getInventory(MapleInventoryType.USE).getItem(attack.ProperBulletPosition);
                }
                if (BulletItem != null) {
                    attack.nBulletItemID = BulletItem.getItemId();
                }
            }
        }
        int damage;
        attack.allDamage = new ArrayList<>();
        int critical_rate = chr.getCriticalRate().get(); // TODO : fix (postBB NL)
        for (int i = 0; i < attack.getMobCount(); i++) {
            List<OdinPair<Integer, Boolean>> allDamageNumbers = new ArrayList<>();
            int nTargetID = cp.Decode4();
            // v131 to v186 OK
            cp.Decode1(); // v366->nHitAction
            cp.Decode1(); // v366->nForeAction & 0x7F | (v156 << 7)
            cp.Decode1(); // v366->nFrameIdx
            cp.Decode1(); // CalcDamageStatIndex & 0x7F | v158
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something

            MapleMonster monster = chr.getMap().getMonsterByOid(nTargetID);
            boolean is_boss = false;
            if (monster != null) {
                is_boss = monster.getStats().isBoss();
            }
            attack.rand_counter = 0;
            attack.randoms = chr.getCalcDamage().getRandoms(attack.rand_size);
            if (!TacosConstants.is_mesp_explosion(attack.nSkillID)) {
                cp.Decode2(); // v366->tDelay
                for (int j = 0; j < attack.getDamagePerMob(); j++) {
                    attack.rand_counter++;
                    attack.rand_counter++; // NON ADMIN
                    // SKILL.
                    attack.rand_counter++; // DAMAGE
                    damage = cp.Decode4(); // 366->aDamage[i]
                    boolean critical = false;
                    if (Version.PreBB()) {
                        attack.rand_counter++;
                    }
                    if (chr.getCalcDamage().isNextAttackCritical() || TacosCalcDamage.getRand(attack.randoms[attack.rand_counter++ % attack.rand_size], 0.0, 100.0) < critical_rate) { // CRITICAL
                        critical = true;
                        if (Version.PostBB()) {
                            attack.rand_counter++; // CRITICAL DAMAGE
                        }
                    }

                    //chr.DebugMsg(String.format("%d : %d = " + critical, j, damage));
                    allDamageNumbers.add(new OdinPair<>(damage, critical));
                    // BOSS.
                    if (Version.PostBB()) {
                        if (is_boss) {
                            attack.rand_counter++;
                        }
                    }
                    // SHADOW MESO.
                }
            } else {
                // meso explosion.
                byte hits = cp.Decode1();
                for (int j = 0; j < hits; j++) {
                    damage = cp.Decode4();
                    allDamageNumbers.add(new OdinPair<>(damage, false));
                }
            }
            if (Version.LessOrEqual(Region.KMS, 65) || Version.Equal(Region.THMS, 87)) {
                // nothing
            } else if (ServerConfig.JMS164orLater()) {
                cp.Decode4(); // CMob::GetCrc(v366->pMob)
            }
            attack.allDamage.add(new AttackPair(nTargetID, allDamageNumbers));
            if (DeveloperMode.DM_CHECK_DAMAGE.get()) {
                DebugLogger.DebugLog(header.name() + ": damage = " + allDamageNumbers);
            }
        }
        if (Version.GreaterOrEqual(Region.KMS, 65) || ServerConfig.JMS180orLater()) {
            if (attack.header == ClientPacketHeader.CP_UserShootAttack) {
                cp.Decode2();
                cp.Decode2();
            }
        }
        // is_wildhunter_job
        // byte 2, m_ptBodyRelMove.y
        attack.position = new Point();
        attack.position.x = cp.Decode2();
        attack.position.y = cp.Decode2();
        if (TacosConstants.is_mesp_explosion(attack.nSkillID)) {
            attack.allMeso = new ArrayList<>();
            byte bullets = cp.Decode1();
            for (int i = 0; i < bullets; i++) {
                int drop_id = cp.Decode4();
                short drop_used = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode2() : cp.Decode1(); // 0 = no damage?
                attack.allMeso.add(drop_id);
            }
            short tTotFrameDelay = cp.Decode2();
        }

        if (attack.exJablin != 0) {
            chr.getCalcDamage().setNextAttackCritical(true);
        }

        return attack;
    }
}
