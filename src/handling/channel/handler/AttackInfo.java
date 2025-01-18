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
package handling.channel.handler;

import java.util.List;
import java.awt.Point;

import client.ISkill;
import constants.GameConstants;
import client.MapleCharacter;
import client.SkillFactory;
import config.ServerConfig;
import packet.ClientPacket;
import packet.ServerPacket;
import server.MapleStatEffect;
import tools.AttackPair;

public class AttackInfo {

    public int skill;
    public List<AttackPair> allDamage;
    public Point position;
    public boolean real = true;

    public ClientPacket.Header AttackHeader;
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
    public int BuffKey;
    public int AttackActionKey;
    public int nAttackActionType;
    public int nAttackSpeed;
    public int tAttackTime;
    public short ProperBulletPosition;
    public short pnCashItemPos;
    public int nShootRange0a;

    public ServerPacket.Header GetHeader() {
        switch (AttackHeader) {
            case CP_UserMeleeAttack: {
                return ServerPacket.Header.LP_UserMeleeAttack;
            }
            case CP_UserShootAttack: {
                return ServerPacket.Header.LP_UserShootAttack;
            }
            case CP_UserMagicAttack: {
                return ServerPacket.Header.LP_UserMagicAttack;
            }
            case CP_UserBodyAttack: {
                return ServerPacket.Header.LP_UserBodyAttack;
            }
            default: {
                break;
            }
        }
        return ServerPacket.Header.UNKNOWN;
    }

    // hit count per mob
    public int GetDamagePerMob() {
        return HitKey & 0x0F; // nDamagePerMob_1
    }

    // number of mobs
    public int GetMobCount() {
        return (HitKey >> 4) & 0x0F; // nCount
    }

    public boolean IsMesoExplosion() {
        return nSkillID == 4211006;
    }

    public boolean IsShadowMeso() {
        return nSkillID == 4111004;
    }

    public boolean is_keydown_skill() {
        switch (nSkillID) {
            // Melee
            case 5101004: // Corkscrew
            case 15101003: // Cygnus corkscrew
            case 5201002: // Gernard
            case 14111006: // Poison bomb
            case 4341002:
            case 4341003:
            // Shoot
            case 9001011: // (GM) Lightning Vulcan
            case 3121004: // Hurricane
            case 3221001: // Pierce
            case 5221004: // Rapidfire
            case 13111002: // Cygnus Hurricane
            case 33121009:
            // Magic
            case 2121001: // Quantum Explosion
            case 2221001: // Quantum Explosion
            case 2321001: // Quantum Explosion
            case 22121000: //breath
            case 22151001: {
                return true;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public boolean IsQuantumExplosion() {
        switch (nSkillID) {
            case 2121001: // Quantum Explosion
            case 2221001: // Quantum Explosion
            case 2321001: // Quantum Explosion
            {
                return true;
            }
            default: {
                break;
            }
        }
        return false;

    }

    public boolean IsFinalAfterSlashBlast() {
        return (BuffKey & 0x01) > 0;
    }

    public boolean IsShadowPartner() {
        return ((BuffKey >> 3) & 0x01) > 0;
    }

    public int GetAttackAction() {
        if (ServerConfig.JMS131orEarlier()) {
            return AttackActionKey & 0x7F;
        }
        return AttackActionKey & 0x7FFF;
    }

    public boolean IsLeft() {
        if (ServerConfig.JMS131orEarlier()) {
            return ((AttackActionKey >> 7) & 0x01) > 0;
        }
        return ((AttackActionKey >> 15) & 0x01) > 0;
    }

    public final MapleStatEffect getAttackEffect(final MapleCharacter chr, int skillLevel, final ISkill skill_) {
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
}
