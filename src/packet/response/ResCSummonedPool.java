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

import odin.client.MapleCharacter;
import config.Region;
import config.ServerConfig;
import config.Version;
import server.network.MaplePacket;
import java.util.List;
import packet.request.parse.ParseCMovePath;
import packet.ServerPacket;
import packet.response.data.DataAvatarLook;
import odin.server.life.SummonAttackEntry;
import odin.server.maps.MapleSummon;
import odin.tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCSummonedPool {

    public static MaplePacket spawnSummon(MapleSummon summon, boolean animated) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SummonedEnterField);
        sp.Encode4(summon.getOwnerId());
        if (ServerConfig.JMS164orLater()) {
            sp.Encode4(summon.getObjectId());
        }
        sp.Encode4(summon.getSkill());
        if (ServerConfig.JMS186orLater()) {
            sp.Encode1(summon.getOwnerLevel() - 1);
        }
        sp.Encode1(summon.getSkillLevel());
        sp.Encode2((short) summon.getPosition().x);
        sp.Encode2((short) summon.getPosition().y);
        sp.Encode1(summon.getSkill() == 32111006 ? 5 : 4); // summon.getStance();
        sp.Encode2(summon.getFh());
        sp.Encode1(summon.getMovementType().getValue());
        sp.Encode1(summon.getSummonType()); // 0 = Summon can't attack - but puppets don't attack with 1 either ^.-
        sp.Encode1(animated ? 0 : 1);
        if (ServerConfig.JMS186orLater()) {
            final MapleCharacter chr = summon.getOwner();
            sp.Encode1(summon.getSkill() == 4341006 && chr != null ? 1 : 0); //mirror target
            if (summon.getSkill() == 4341006 && chr != null) {
                sp.EncodeBuffer(DataAvatarLook.Encode(chr));
            }
        }
        return sp.get();
    }

    public static MaplePacket removeSummon(MapleSummon summon, boolean animated) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SummonedLeaveField);
        sp.Encode4(summon.getOwnerId());
        if (Version.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkill());
        } else {
            sp.Encode4(summon.getObjectId());
        }
        sp.Encode1(animated ? 4 : 1);
        return sp.get();
    }

    // v131 broken
    public static MaplePacket summonAttack(final int cid, final int summonSkillId, final byte animation, final List<SummonAttackEntry> allDamage, final int level) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SummonedAttack);
        sp.Encode4(cid);
        sp.Encode4(summonSkillId);
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
        return sp.get();
    }

    public static MaplePacket moveSummon(MapleSummon summon, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SummonedMove);
        sp.Encode4(summon.getOwnerId());
        // very old summon type
        if (Version.LessOrEqual(Region.JMS, 131)) {
            sp.Encode4(summon.getSkill());
        } else {
            sp.Encode4(summon.getObjectId());
        }

        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    public static MaplePacket damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SummonedHit);
        sp.Encode4(cid);
        sp.Encode4(summonSkillId);
        sp.Encode1(unkByte);
        sp.Encode4(damage);
        sp.Encode4(monsterIdFrom);
        sp.Encode1(0);
        return sp.get();
    }

    public static MaplePacket summonSkill(int cid, int summonSkillId, int newStance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SummonedSkill.get());
        mplew.writeInt(cid);
        mplew.writeInt(summonSkillId);
        mplew.write(newStance);
        return mplew.getPacket();
    }

}
