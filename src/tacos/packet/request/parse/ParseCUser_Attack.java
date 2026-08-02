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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import odin.client.ISkill;
import odin.client.MapleCharacter;
import odin.client.SkillFactory;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleStatEffect;
import odin.server.life.MapleMonster;
import tacos.client.TacosBuff.Buff;
import tacos.client.TacosCalcDamage;
import tacos.config.Region;
import tacos.config.Version;
import tacos.constants.TacosConstants;
import tacos.packet.ClientPacket;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsSkill;

/**
 *
 * @author Riremito
 */
public class ParseCUser_Attack {

    public int skill;
    public List<Integer> allMeso;
    public Point position;
    public boolean real = true;
    public ClientPacketHeader header;
    public int CharacterId;
    public int X;
    public int Y;
    public int SkillLevel;
    public int nMastery;
    public int nBulletItemID = 0;
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
    public HashMap<Integer, ArrayList<Integer>> damages = new HashMap<>();

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

    public boolean setCritical(MapleCharacter chr) {
        int critical_rate = chr.getCriticalRate().get();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : this.damages.entrySet()) {
            int rand_counter = 0;
            int[] randoms = chr.getCalcDamage().getRandoms(7);

            int mob_object_id = entry.getKey();
            MapleMonster monster = chr.getMap().getMonsterByOid(mob_object_id);
            boolean is_boss = false;
            if (monster != null) {
                is_boss = monster.getStats().isBoss();
            }
            ArrayList<Integer> damage_list = entry.getValue();
            for (int i = 0; i < damage_list.size(); i++) {
                rand_counter++;
                rand_counter++; // NON ADMIN
                // SKILL.
                rand_counter++; // DAMAGE
                if (Version.PreBB()) {
                    rand_counter++;
                }
                if (chr.getCalcDamage().isNextAttackCritical() || TacosCalcDamage.getRand(randoms[rand_counter++ % 7], 0.0, 100.0) < critical_rate) { // CRITICAL
                    if (Version.PostBB()) {
                        rand_counter++; // CRITICAL DAMAGE
                    }
                    damage_list.set(i, damage_list.get(i) | 1 << 31); // critical bit.
                }
                // BOSS.
                if (Version.PostBB()) {
                    if (is_boss) {
                        rand_counter++;
                    }
                }
                // SHADOW MESO.
            }
        }

        if (this.exJablin != 0) {
            chr.getCalcDamage().setNextAttackCritical(true);
        }

        return true;
    }

    public boolean setBullet(MapleCharacter chr) {
        if (this.nShootRange0a == 0) {
            // soul arrow?
            return false;
        }
        for (Buff buff : chr.getBuff().getAll()) {
            switch (OpsSkill.find(buff.buff_id)) {
                case HUNTER_SOUL_ARROW_BOW:
                case CROSSBOWMAN_SOUL_ARROW_CROSSBOW:
                case WINDBREAKER_SOUL_ARROW_BOW:
                case WILDHUNTER_SOUL_ARROW_CROSSBOW: {
                    // soul arrow.
                    return false;
                }
                default: {
                    break;
                }
            }
        }
        if (this.pnCashItemPos != 0) {
            IItem item = chr.getInventory(MapleInventoryType.CASH).getItem(this.pnCashItemPos);
            if (item != null) {
                this.nBulletItemID = item.getItemId();
            }
            return true;
        }
        if (this.ProperBulletPosition != 0) {
            IItem item = chr.getInventory(MapleInventoryType.USE).getItem(this.ProperBulletPosition);
            if (item != null) {
                this.nBulletItemID = item.getItemId();
            }
            return true;
        }
        return false;
    }

    // BMS CUser::OnAttack
    public static ParseCUser_Attack parse(MapleCharacter chr, ClientPacketHeader header, ClientPacket cp) {
        ParseCUser_Attack attack = new ParseCUser_Attack();
        attack.header = header;
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
        if (Version.LessOrEqual(Region.KMS, 114) || Version.LessOrEqual(Region.KMST, 330)) {
            // ?
        } else if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) || Version.GreaterOrEqual(Region.BMS, 24)) {
            cp.Decode4(); // pDrInfo.dr0
            cp.Decode4(); // pDrInfo.dr1
        }
        attack.HitKey = cp.Decode1(); // nDamagePerMob | (16 * nCount)
        // DR_Check
        if (Version.LessOrEqual(Region.KMS, 114) || Version.LessOrEqual(Region.KMST, 330)) {
            // ?
        } else if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) || Version.GreaterOrEqual(Region.BMS, 24)) {
            cp.Decode4(); // pDrInfo.dr2
            cp.Decode4(); // pDrInfo.dr3
        }
        attack.nSkillID = cp.Decode4();
        attack.skill = attack.nSkillID;
        if (0 < attack.nSkillID) {
            attack.SkillLevel = chr.getSkillLevel(attack.nSkillID);
        }
        // v95 1 byte cd->nCombatOrders
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            cp.Decode1();
        }
        if (Version.LessOrEqual(Region.KMS, 114) || Version.LessOrEqual(Region.KMST, 330)) {
            // none
        } else if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) || Version.GreaterOrEqual(Region.BMS, 24)) {
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
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            // none
        } else if (Version.PostBB()) {
            cp.Decode1();
        }
        if (Version.LessOrEqual(Region.KMS, 95) || Version.GreaterOrEqual(Region.GMS, 95)) {
            // ?
        } else if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 65) || Version.GreaterOrEqual(Region.JMS, 164) || Version.GreaterOrEqual(Region.CMS, 73) || Version.GreaterOrEqual(Region.TWMS, 94) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 72) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 54)
                || Version.GreaterOrEqual(Region.BMS, 24)) {
            cp.Decode4(); // Crc
        }
        attack.tKeyDown = 0;
        if (TacosConstants.is_keydown_skill(attack.nSkillID)) {
            attack.tKeyDown = cp.Decode4();
        }
        attack.BuffKey = cp.Decode1();
        if (Version.GreaterOrEqual(Region.KMST, 330) || Version.GreaterOrEqual(Region.JMS, 187) || Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76) || Version.GreaterOrEqual(Region.GMS, 95)) {
            if (attack.header == ClientPacketHeader.CP_UserShootAttack) {
                attack.exJablin = cp.Decode1();
            }
        }
        if (Version.LessOrEqual(Region.KMS, 65) || Version.LessOrEqual(Region.JMS, 165) || Region.BMS.check()) {
            attack.AttackActionKey = cp.Decode1();
        } else {
            attack.AttackActionKey = cp.Decode2(); // nAttackAction & 0x7FFF | (bLeft << 15)
        }
        if (Version.PostBB()) {
            if (Version.LessOrEqual(Region.KMST, 330) || Version.Equal(Region.JMS, 187)) {
                // none
            } else {
                cp.Decode4(); // JMS188
            }
        }
        // v95 4 bytes crc
        attack.nAttackActionType = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1();
        attack.nAttackSpeed = cp.Decode1();
        attack.tAttackTime = Version.LessOrEqual(Region.KMS, 1) ? 0 : cp.Decode4();
        if (Version.GreaterOrEqual(Region.KMS, 95) || Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70)) {
            cp.Decode4(); // dwID
        }
        if (attack.header == ClientPacketHeader.CP_UserShootAttack) {
            attack.ProperBulletPosition = cp.Decode2();
            attack.pnCashItemPos = cp.Decode2();
            attack.nShootRange0a = cp.Decode1();
        }
        for (int i = 0; i < attack.getMobCount(); i++) {
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

            ArrayList<Integer> damage_list = new ArrayList<>();
            if (!TacosConstants.is_mesp_explosion(attack.nSkillID)) {
                cp.Decode2(); // v366->tDelay
                for (int j = 0; j < attack.getDamagePerMob(); j++) {
                    int damage = cp.Decode4(); // 366->aDamage[i]
                    damage_list.add(damage); // add damage.
                }
            } else {
                // meso explosion.
                byte hits = cp.Decode1();
                for (int j = 0; j < hits; j++) {
                    int damage = cp.Decode4();
                    damage_list.add(damage); // add damage.
                }
            }

            if (Version.LessOrEqual(Region.KMS, 65) || Version.LessOrEqual(Region.THMS, 87)) {
                // nothing
            } else if (Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 65) || Version.GreaterOrEqual(Region.JMS, 164) || Version.GreaterOrEqual(Region.CMS, 73) || Version.GreaterOrEqual(Region.TWMS, 94) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 72) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 54)) {
                cp.Decode4(); // CMob::GetCrc(v366->pMob)
            }

            attack.damages.put(nTargetID, damage_list); // add damage list.
        }
        if (Version.GreaterOrEqual(Region.KMS, 65) || Version.PostBB() || Version.GreaterOrEqual(Region.KMS, 92) || Version.GreaterOrEqual(Region.JMS, 180) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70)) {
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

        return attack;
    }
}
