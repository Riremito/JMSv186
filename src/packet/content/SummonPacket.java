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
package packet.content;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.SummonSkillEntry;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.ServerConfig;
import debug.Debug;
import handling.MaplePacket;
import handling.channel.handler.MovementParse;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import packet.ClientPacket;
import packet.ServerPacket;
import packet.struct.AvatarLook;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.life.SummonAttackEntry;
import server.maps.MapleMap;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import server.movement.LifeMovementFragment;

/**
 *
 * @author Riremito
 */
public class SummonPacket {

    // CUser::OnSummonedPacket
    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        int oid = p.Decode4(); // older version = SkillID

        MapleSummon summon = null;
        if (ServerConfig.version <= 131) {
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
                // SummonHandler.MoveSummon(p, c.getPlayer());
                if (summon.getMovementType() == SummonMovementType.STATIONARY) {
                    return true;
                }
                final Point pos = summon.getPosition();

                if (ServerConfig.version <= 131) {
                    pos.x = (int) p.Decode2();
                    pos.y = (int) p.Decode2();
                } else {
                    p.Decode4(); // -1
                    p.Decode4(); // -1
                }
                final List<LifeMovementFragment> res = MovementPacket.parseMovement(p, 4);

                MovementParse.updatePosition(res, summon, 0);
                if (!summon.isChangedMap()) {
                    chr.getMap().broadcastMessage(chr, moveSummon(summon, pos, res), summon.getPosition());
                }
                return true;
            }
            case CP_SummonedAttack: {
                SummonAttack(p, summon, chr);
                return true;
            }
            case CP_SummonedHit: {
                DamageSummon(p, chr);
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

        Debug.ErrorLog("Not coded: " + p.GetOpcodeName());
        return false;
    }

    public static MaplePacket spawnSummon(MapleSummon summon, boolean animated) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SummonedEnterField);

        p.Encode4(summon.getOwnerId());

        if (131 < ServerConfig.version) {
            p.Encode4(summon.getObjectId());
        }

        p.Encode4(summon.getSkill());

        if (131 < ServerConfig.version) {
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

        if (186 <= ServerConfig.version) {
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

        if (ServerConfig.version <= 131) {
            p.Encode4(summon.getSkill());
        } else {
            p.Encode4(summon.getObjectId());
        }

        p.Encode1(animated ? 4 : 1);
        return p.Get();
    }

    public static MaplePacket moveSummon(MapleSummon summon, Point startPos, List<LifeMovementFragment> moves) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SummonedMove);
        p.Encode4(summon.getOwnerId());

        if (ServerConfig.version <= 131) {
            p.Encode4(summon.getSkill());
        } else {
            p.Encode4(summon.getObjectId());
        }

        p.Encode2((short) startPos.x);
        p.Encode2((short) startPos.y);

        if (131 < ServerConfig.version) {
            p.Encode4(0);
        }

        p.EncodeBuffer(MovementPacket.serializeMovementList(moves));
        return p.Get();
    }

    // v131 broken
    public static MaplePacket summonAttack(final int cid, final int summonSkillId, final byte animation, final List<SummonAttackEntry> allDamage, final int level) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_SummonedAttack);
        p.Encode4(cid);
        p.Encode4(summonSkillId);

        if (131 < ServerConfig.version) {
            p.Encode1(level - 1); //? guess
        }

        p.Encode1(animation);
        p.Encode1(allDamage.size());

        for (final SummonAttackEntry attackEntry : allDamage) {
            p.Encode4(attackEntry.getMonster().getObjectId()); // oid

            if (ServerConfig.version <= 131) {
                p.Encode1(6);
            } else {
                p.Encode1(7); // who knows
            }

            p.Encode4(attackEntry.getDamage()); // damage
        }
        return p.Get();
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

    // SummonAttack
    public static void SummonAttack(ClientPacket p, MapleSummon summon, MapleCharacter chr) {
        final MapleMap map = chr.getMap();

        final SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());

        if (sse == null) {
            return;
        }

        if (131 < ServerConfig.version) {
            p.Decode4();
            p.Decode4();

            int tick = p.Decode4();
            chr.updateTick(tick);
            summon.CheckSummonAttackFrequency(chr, tick);

            p.Decode4();
            p.Decode4();
        }

        final byte animation = p.Decode1();

        if (131 < ServerConfig.version) {
            p.Decode4();
            p.Decode4();
        }

        final byte numAttacked = p.Decode1();

        if (131 < ServerConfig.version) {
            p.Decode2(); // x
            p.Decode2(); // y
            p.Decode2(); // x
            p.Decode2(); // y
        }

        final List<SummonAttackEntry> allDamage = new ArrayList<SummonAttackEntry>();
        chr.getCheatTracker().checkSummonAttack();

        for (int i = 0; i < numAttacked; i++) {
            final MapleMonster mob = map.getMonsterByOid(p.Decode4());

            if (mob == null) {
                continue;
            }

            if (186 <= ServerConfig.version) {
                p.Decode4(); // MobID
            }

            p.Decode1();
            p.Decode1();
            p.Decode1();
            p.Decode1();
            p.Decode2();
            p.Decode2();
            p.Decode2();
            p.Decode2();
            p.Decode2();

            final int damage = p.Decode4();
            allDamage.add(new SummonAttackEntry(mob, damage));
        }

        if (ServerConfig.version <= 131) {
            p.Decode2(); // X
            p.Decode2(); // Y
        }

        if (!summon.isChangedMap()) {
            map.broadcastMessage(chr, summonAttack(summon.getOwnerId(), summon.getObjectId(), animation, allDamage, chr.getLevel()), summon.getPosition());
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
                chr.getClient().SendPacket(MobPacket.Kill(mob, 1));
            }
        }

        if (summon.isGaviota()) {
            chr.getMap().broadcastMessage(SummonPacket.removeSummon(summon, true));
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
                chr.getMap().broadcastMessage(chr, damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getPosition());
                break;
            }
        }
    }
}
