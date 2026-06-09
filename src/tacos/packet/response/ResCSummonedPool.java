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
import tacos.config.ServerConfig;
import tacos.config.Version;
import java.util.List;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.ServerPacket;
import odin.server.life.SummonAttackEntry;
import odin.server.maps.MapleSummon;
import tacos.packet.ServerPacketHeader;
import tacos.packet.response.data.DataCSummoned;

/**
 *
 * @author Riremito
 */
public class ResCSummonedPool {

    public static ServerPacket SummonedEnterField(MapleSummon summon, boolean animated) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedEnterField);

        sp.Encode4(summon.getOwnerId()); // m_dwCharacterId
        if (ServerConfig.JMS147orLater()) {
            sp.Encode4(summon.getObjectId()); // m_dwSummonedID
        }
        sp.Encode4(summon.getSkill()); // m_nSkillID
        if (ServerConfig.JMS186orLater()) {
            sp.Encode1(summon.getOwnerLevel() - 1); // m_nCharLevel
        }

        sp.Encode1(summon.getSkillLevel()); // m_nSLV
        sp.EncodeBuffer(DataCSummoned.Init(summon, animated));
        return sp;
    }

    public static ServerPacket SummonedLeaveField(MapleSummon summon, boolean animated) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedLeaveField);

        sp.Encode4(summon.getOwnerId());
        if (Version.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkill());
        } else {
            sp.Encode4(summon.getObjectId());
        }
        sp.Encode1(animated ? 4 : 1); // LEAVE_TYPE_LEAVE_FIELD, LEAVE_TYPE_SUMMONED_DEAD
        return sp;
    }

    public static ServerPacket SummonedMove(MapleSummon summon, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedMove);

        sp.Encode4(summon.getOwnerId());
        // very old summon type
        if (Version.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkill());
        } else {
            sp.Encode4(summon.getObjectId());
        }

        sp.EncodeBuffer(data.get()); // unused data in the end?
        return sp;
    }

    // v131 broken
    public static ServerPacket SummonedAttack(MapleSummon summon, byte animation, List<SummonAttackEntry> allDamage, int level) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedAttack);

        sp.Encode4(summon.getOwnerId());
        sp.Encode4(summon.getSkill());
        if (ServerConfig.JMS164orLater()) {
            sp.Encode1(level - 1); //? guess
        }
        sp.Encode1(animation);
        sp.Encode1(allDamage.size());
        for (final SummonAttackEntry attackEntry : allDamage) {
            sp.Encode4(attackEntry.getMonster().getObjectId()); // oid
            if (Version.LessOrEqual(Region.JMS, 131)) {
                sp.Encode1(6);
            } else {
                sp.Encode1(7); // who knows
            }
            sp.Encode4(attackEntry.getDamage()); // damage
        }
        return sp;
    }

    public static ServerPacket SummonedSkill(MapleSummon summon,/*int cid, int summonSkillId*/ int newStance) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedSkill);
        /*
            // JMS147
            Header(@007B); // 0046F5FB
            Encode4(#111); // 007A8D87
            Encode4(#100017); // 0077E3BB
            Encode1(#8); // 00674ADA
         */
        sp.Encode4(summon.getOwnerId());
        sp.Encode4(summon.getObjectId());
        sp.Encode1(newStance); // not stance?
        return sp;
    }

    public static ServerPacket SummonedHit(MapleSummon summon, int damage, int unkByte, int monsterIdFrom) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_SummonedHit);

        sp.Encode4(summon.getOwnerId());
        sp.Encode4(summon.getSkill());
        sp.Encode1(unkByte);
        sp.Encode4(damage);
        sp.Encode4(monsterIdFrom);
        sp.Encode1(0);
        return sp;
    }

}
