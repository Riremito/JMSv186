/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package tacos.packet.request;

import odin.client.ISkill;
import odin.client.MapleBuffStat;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.client.SkillFactory;
import odin.client.SummonSkillEntry;
import odin.client.status.MonsterStatus;
import odin.client.status.MonsterStatusEffect;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tacos.packet.ClientPacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCMobPool;
import tacos.packet.response.ResCSummonedPool;
import odin.server.MapleStatEffect;
import odin.server.life.MapleMonster;
import odin.server.life.SummonAttackEntry;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleSummon;
import odin.server.maps.SummonMovementType;

/**
 *
 * @author Riremito
 */
public class ReqCSummonedPool {

    // CUser::OnSummonedPacket
    public static boolean OnPacket(MapleClient c, ClientPacket.Header header, ClientPacket cp) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        int oid = cp.Decode4(); // older version = SkillID

        MapleSummon summon = null;
        if (Version.LessOrEqual(Region.JMS, 131)) {
            for (MapleSummon sms : chr.getSummons().values()) {
                if (sms.getSkill() == oid) {
                    summon = sms;
                    break;
                }
            }
        } else {
            summon = map.getSummonByOid(oid);
        }

        if (summon == null) {
            return false;
        }

        switch (header) {
            case CP_SummonedMove: {
                // CSummoned::OnMove
                // CField::OnSummonedMove
                OnMove(cp, chr, summon);
                return true;
            }
            case CP_SummonedAttack: {
                SummonAttack(cp, summon, chr);
                return true;
            }
            case CP_SummonedHit: {
                DamageSummon(cp, chr);
                return true;
            }
            case CP_SummonedSkill: {
                // ?
                break;
            }
            case CP_Remove: {
                // ?
                break;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("Not coded: " + cp.GetOpcodeName());
        return false;
    }

    public static boolean OnMove(ClientPacket cp, MapleCharacter chr, MapleSummon summon) {
        if (summon.getMovementType() == SummonMovementType.STATIONARY || summon.isChangedMap()) {
            return false;
        }

        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            move_path.update(summon);
        }

        chr.getMap().broadcastMessage(chr, ResCSummonedPool.moveSummon(summon, move_path), summon.getPosition());
        return true;
    }

    // SummonAttack
    public static void SummonAttack(ClientPacket cp, MapleSummon summon, MapleCharacter chr) {
        final MapleMap map = chr.getMap();

        final SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());

        if (sse == null) {
            return;
        }

        if (ServerConfig.JMS164orLater()) {
            cp.Decode4();
            cp.Decode4();

            int tick = cp.Decode4();
            chr.updateTick(tick);
            summon.CheckSummonAttackFrequency(chr, tick);

            cp.Decode4();
            cp.Decode4();
        }

        final byte animation = cp.Decode1();

        if (ServerConfig.JMS164orLater()) {
            cp.Decode4();
            cp.Decode4();
        }

        final byte numAttacked = cp.Decode1();

        if (ServerConfig.JMS164orLater()) {
            cp.Decode2(); // x
            cp.Decode2(); // y
            cp.Decode2(); // x
            cp.Decode2(); // y
        }

        final List<SummonAttackEntry> allDamage = new ArrayList<SummonAttackEntry>();

        for (int i = 0; i < numAttacked; i++) {
            final MapleMonster mob = map.getMonsterByOid(cp.Decode4());

            if (mob == null) {
                continue;
            }

            if (ServerConfig.JMS186orLater()) {
                cp.Decode4(); // MobID
            }

            cp.Decode1();
            cp.Decode1();
            cp.Decode1();
            cp.Decode1();
            cp.Decode2();
            cp.Decode2();
            cp.Decode2();
            cp.Decode2();
            cp.Decode2();

            final int damage = cp.Decode4();
            allDamage.add(new SummonAttackEntry(mob, damage));
        }

        if (Version.LessOrEqual(Region.JMS, 131)) {
            cp.Decode2(); // X
            cp.Decode2(); // Y
        }

        if (!summon.isChangedMap()) {
            map.broadcastMessage(chr, ResCSummonedPool.summonAttack(summon, animation, allDamage, chr.getLevel()), summon.getPosition());
        }
        final ISkill summonSkill = SkillFactory.getSkill(summon.getSkill());
        final MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());

        if (summonEffect == null) {
            return;
        }
        for (SummonAttackEntry attackEntry : allDamage) {
            final int toDamage = attackEntry.getDamage();
            final MapleMonster mob = attackEntry.getMonster();

            if (toDamage > 0 && summonEffect.getMonsterStati().size() > 0) {
                if (summonEffect.makeChanceResult()) {
                    for (Map.Entry<MonsterStatus, Integer> z : summonEffect.getMonsterStati().entrySet()) {
                        mob.applyStatus(chr, new MonsterStatusEffect(z.getKey(), z.getValue(), summonSkill.getId(), null, false), summonEffect.isPoison(), 4000, false);
                    }
                }
            }
            mob.damage(chr, toDamage, true);
            chr.checkMonsterAggro(mob);
            if (!mob.isAlive()) {
                chr.getClient().SendPacket(ResCMobPool.Kill(mob, 1));
            }
        }

        if (summon.isGaviota()) {
            chr.getMap().broadcastMessage(ResCSummonedPool.removeSummon(summon, true));
            chr.getMap().removeMapObject(summon);
            chr.removeVisibleMapObject(summon);
            chr.cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            chr.cancelEffectFromBuffStat(MapleBuffStat.REAPER);
        }
    }

    public static final void DamageSummon(ClientPacket p, final MapleCharacter chr) {
        final int unkByte = p.Decode1();
        final int damage = p.Decode4();
        final int monsterIdFrom = p.Decode4();

        final Iterator<MapleSummon> iter = chr.getSummons().values().iterator();
        MapleSummon summon;

        while (iter.hasNext()) {
            summon = iter.next();
            if (summon.isPuppet() && summon.getOwnerId() == chr.getId()) { //We can only have one puppet(AFAIK O.O) so this check is safe.
                summon.addHP((short) -damage);
                if (summon.getHP() <= 0) {
                    chr.cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
                }
                chr.getMap().broadcastMessage(chr, ResCSummonedPool.damageSummon(summon, damage, unkByte, monsterIdFrom), summon.getPosition());
                break;
            }
        }
    }
}
