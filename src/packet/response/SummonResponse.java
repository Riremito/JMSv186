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
package packet.response;

import client.MapleCharacter;
import config.ServerConfig;
import handling.MaplePacket;
import java.util.List;
import packet.request.struct.CMovePath;
import packet.ServerPacket;
import packet.response.struct.AvatarLook;
import server.life.SummonAttackEntry;
import server.maps.MapleSummon;

/**
 *
 * @author Riremito
 */
public class SummonResponse {

    // v131 broken
    public static MaplePacket summonAttack(final int cid, final int summonSkillId, final byte animation, final List<SummonAttackEntry> allDamage, final int level) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SummonedAttack);
        p.Encode4(cid);
        p.Encode4(summonSkillId);
        if (!(ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131)) {
            p.Encode1(level - 1); //? guess
        }
        p.Encode1(animation);
        p.Encode1(allDamage.size());
        for (final SummonAttackEntry attackEntry : allDamage) {
            p.Encode4(attackEntry.getMonster().getObjectId()); // oid
            if ((ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131)) {
                p.Encode1(6);
            } else {
                p.Encode1(7); // who knows
            }
            p.Encode4(attackEntry.getDamage()); // damage
        }
        return p.Get();
    }

    public static MaplePacket moveSummon(MapleSummon summon, CMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SummonedMove);
        sp.Encode4(summon.getOwnerId());
        // very old summon type
        if ((ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131)) {
            sp.Encode4(summon.getSkill());
        } else {
            sp.Encode4(summon.getObjectId());
        }

        sp.EncodeBuffer(data.get());
        return sp.Get();
    }

    public static MaplePacket damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SummonedHit);
        p.Encode4(cid);
        p.Encode4(summonSkillId);
        p.Encode1(unkByte);
        p.Encode4(damage);
        p.Encode4(monsterIdFrom);
        p.Encode1(0);
        return p.Get();
    }

    public static MaplePacket spawnSummon(MapleSummon summon, boolean animated) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SummonedEnterField);
        p.Encode4(summon.getOwnerId());
        if (!(ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131)) {
            p.Encode4(summon.getObjectId());
        }
        p.Encode4(summon.getSkill());
        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())) {
            p.Encode1(summon.getOwnerLevel() - 1);
        }
        p.Encode1(summon.getSkillLevel());
        p.Encode2((short) summon.getPosition().x);
        p.Encode2((short) summon.getPosition().y);
        p.Encode1(summon.getSkill() == 32111006 ? 5 : 4); // summon.getStance();
        p.Encode2(summon.getFh());
        p.Encode1(summon.getMovementType().getValue());
        p.Encode1(summon.getSummonType()); // 0 = Summon can't attack - but puppets don't attack with 1 either ^.-
        p.Encode1(animated ? 0 : 1);
        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())) {
            final MapleCharacter chr = summon.getOwner();
            p.Encode1(summon.getSkill() == 4341006 && chr != null ? 1 : 0); //mirror target
            if (summon.getSkill() == 4341006 && chr != null) {
                p.EncodeBuffer(AvatarLook.Encode(chr));
            }
        }
        return p.Get();
    }

    public static MaplePacket removeSummon(MapleSummon summon, boolean animated) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SummonedLeaveField);
        p.Encode4(summon.getOwnerId());
        if ((ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 131)) {
            p.Encode4(summon.getSkill());
        } else {
            p.Encode4(summon.getObjectId());
        }
        p.Encode1(animated ? 4 : 1);
        return p.Get();
    }

}
