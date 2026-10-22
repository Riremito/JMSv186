/*
 * Copyright (C) 2024 Riremito
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
package tacos.packet.response;

import tacos.config.Region;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.ServerPacket;
import tacos.config.Config;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.RD_CSummoned;
import tacos.server.map.object.TacosSummon;
import tacos.server.map.object.TacosSummon.TacosSummonAttackInfo;
import tacos.server.map.object.TacosSummon.TacosSummonAttackInfo.SummonDamage;

/**
 *
 * @author Riremito
 */
public class ResCSummonedPool {

    // CSummonedPool::OnCreated
    public static ServerPacket SummonedEnterField(TacosSummon summon, boolean animated) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedEnterField);

        sp.Encode4(summon.getOwnerId()); // m_dwCharacterId
        sp.Encode4(summon.getObjectId(), Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 48) || Config.GreaterOrEqual(Region.JMS, 147) || Config.GreaterOrEqual(Region.CMS, 63) || Config.GreaterOrEqual(Region.TWMS, 74) || Config.GreaterOrEqual(Region.THMS, 0) || Config.GreaterOrEqual(Region.GMS, 62) || Config.GreaterOrEqual(Region.MSEA, 0) || Config.GreaterOrEqual(Region.EMS, 0)); // m_dwSummonedID
        sp.Encode4(summon.getSkillID()); // m_nSkillID
        sp.Encode1(summon.getOwnerLevel() - 1, Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)); // m_nCharLevel
        sp.Encode1(summon.getSLV()); // m_nSLV
        sp.EncodeBuffer(RD_CSummoned.Init(summon, animated));
        return sp;
    }

    // CSummonedPool::OnRemoved
    public static ServerPacket SummonedLeaveField(TacosSummon summon, boolean animated) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedLeaveField);

        sp.Encode4(summon.getOwnerId());
        if (Config.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkillID());
        } else {
            sp.Encode4(summon.getObjectId());
        }
        sp.Encode1(animated ? 4 : 1); // LEAVE_TYPE_LEAVE_FIELD, LEAVE_TYPE_SUMMONED_DEAD
        return sp;
    }

    // CSummonedPool::OnMove
    public static ServerPacket SummonedMove(TacosSummon summon, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedMove);

        sp.Encode4(summon.getOwnerId());
        // very old summon type
        if (Config.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkillID());
        } else {
            sp.Encode4(summon.getObjectId());
        }

        sp.EncodeBuffer(data.get()); // unused data in the end?
        return sp;
    }

    // CSummonedPool::OnAttack
    public static ServerPacket SummonedAttack(TacosSummon summon, TacosSummonAttackInfo attack_info) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedAttack);

        sp.Encode4(summon.getOwnerId()); // dwCharacterID

        if (Config.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkillID()); // nSkillID
        } else {
            sp.Encode4(summon.getObjectId()); // object id
        }

        // CSummoned::OnAttack
        sp.Encode1(attack_info.getCharLevel(), Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // m_nCharLevel
        sp.Encode1(attack_info.getAction()); // (bLeft << 7) | nAction
        sp.Encode1(attack_info.getDamages().size()); // nMobCount
        // ATTACKINFO
        for (SummonDamage damage : attack_info.getDamages()) {
            sp.Encode4(damage.getMobObjectId()); // dwMobID
            sp.Encode1(damage.getHitAction()); // nHitAction
            sp.Encode4(damage.getDamage()); // aDamage[i], aDamageSvr[0]
        }

        sp.Encode1(0); // unused, JMS187
        return sp;
    }

    // CSummonedPool::OnSkill
    public static ServerPacket SummonedSkill(TacosSummon summon,/*int cid, int summonSkillId*/ int newStance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedSkill);

        sp.Encode4(summon.getOwnerId());

        if (Config.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkillID());
        } else {
            sp.Encode4(summon.getObjectId());
        }

        sp.Encode1(newStance); // not stance?
        return sp;
    }

    // CSummonedPool::OnHit
    public static ServerPacket SummonedHit(TacosSummon summon, int damage, int unkByte, int monsterIdFrom) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedHit);

        sp.Encode4(summon.getOwnerId());

        if (Config.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkillID());
        } else {
            sp.Encode4(summon.getObjectId());
        }

        sp.Encode1(unkByte);
        sp.Encode4(damage);
        sp.Encode4(monsterIdFrom);
        sp.Encode1(0);
        return sp;
    }
}
