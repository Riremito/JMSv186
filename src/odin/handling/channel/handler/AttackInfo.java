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

import java.util.List;
import java.awt.Point;

import odin.client.ISkill;
import odin.constants.GameConstants;
import odin.client.MapleCharacter;
import odin.client.SkillFactory;
import odin.server.MapleStatEffect;
import odin.tools.AttackPair;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ServerPacketHeader;

public class AttackInfo {

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
            case CP_UserMeleeAttack: {
                return ServerPacketHeader.LP_UserMeleeAttack;
            }
            case CP_UserShootAttack: {
                return ServerPacketHeader.LP_UserShootAttack;
            }
            case CP_UserMagicAttack: {
                return ServerPacketHeader.LP_UserMagicAttack;
            }
            case CP_UserBodyAttack: {
                return ServerPacketHeader.LP_UserBodyAttack;
            }
            default: {
                break;
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
