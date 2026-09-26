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
 */
package tacos.packet.request;

import odin.client.Skill;
import odin.client.MapleCharacter;
import tacos.client.TacosClient;
import odin.client.SkillFactory;
import odin.client.status.MonsterStatus;
import odin.client.status.MonsterStatusEffect;
import tacos.config.Region;
import tacos.debug.DebugLogger;
import java.util.ArrayList;
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
import tacos.config.Config;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsMobLeaveField;
import tacos.packet.ops.OpsMoveAbility;
import tacos.server.map.object.TacosSummon;

/**
 *
 * @author Riremito
 */
public class ReqCSummonedPool {

    /*
        CUser::OnSummonedPacket (JMS187)
        CSummonedPool::OnPacket (JMS188)
     */
    public static boolean OnPacket(TacosClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        int m_dwSummonedID = cp.Decode4(); // older version = SkillID

        TacosSummon summon = null;
        if (Config.LessOrEqual(Region.JMS, 131)) {
            summon = chr.getSummon();
            if (summon.getSkillID() != m_dwSummonedID) {
                return false;
            }
        } else {
            summon = map.getSummonByOid(m_dwSummonedID);
        }

        if (summon == null) {
            // already removed.
            return true;
        }

        switch (header) {
            case CP_SummonedMove: {
                OnMove(chr, cp, summon);
                return true;
            }
            case CP_SummonedAttack: {
                OnAttack(chr, cp, summon);
                return true;
            }
            case CP_SummonedSkill: {
                // CSummoned::OnSkill
                break;
            }
            case CP_SummonedHit: {
                OnHit(chr, cp, summon);
                return true;
            }
            case CP_Remove: {
                // CSummoned::OnRemoved
                break;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("Not coded: " + header.name());
        return false;
    }

    // CSummoned::OnMove
    public static boolean OnMove(MapleCharacter chr, ClientPacket cp, TacosSummon summon) {
        if (summon.getMoveAbility() == OpsMoveAbility.MOVEABILITY_STOP) {
            return false;
        }

        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            summon.update(move_path);
        }

        chr.getMap().broadcastMessageTo(chr, ResCSummonedPool.SummonedMove(summon, move_path), summon.getPosition());
        return true;
    }

    // CSummoned::OnAttack
    public static void OnAttack(MapleCharacter chr, ClientPacket cp, TacosSummon summon) {
        MapleMap map = chr.getMap();

        if (Config.Equal(Region.KMST, 330)) {
            int tick = cp.Decode4();
            byte animation = cp.Decode1();
            byte numAttacked = cp.Decode1();
            short x = cp.Decode2(); // x
            short y = cp.Decode2(); // y
            short x2 = cp.Decode2(); // x
            short y2 = cp.Decode2(); // y

            List<SummonAttackEntry> allDamage = new ArrayList<>();

            for (int i = 0; i < numAttacked; i++) {
                int mob_object_id = cp.Decode4();
                int mob_id = cp.Decode4();
                byte unk1 = cp.Decode1();
                byte unk2 = cp.Decode1();
                byte unk3 = cp.Decode1();
                byte unk4 = cp.Decode1();
                short unk5 = cp.Decode2();
                short unk6 = cp.Decode2();
                short unk7 = cp.Decode2();
                short unk8 = cp.Decode2();
                short unk9 = cp.Decode2();
                int damage = cp.Decode4();

                MapleMonster mob = map.getMonsterByOid(mob_object_id);

                if (mob == null) {
                    continue;
                }

                allDamage.add(new SummonAttackEntry(mob, damage));
            }

            map.broadcastMessageTo(chr, ResCSummonedPool.SummonedAttack(summon, animation, allDamage, chr.getLevel()), summon.getPosition());

            Skill summonSkill = SkillFactory.getSkill(summon.getSkillID());
            MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSLV());

            if (summonEffect == null) {
                return;
            }
            for (SummonAttackEntry attackEntry : allDamage) {
                int toDamage = attackEntry.getDamage();
                MapleMonster mob = attackEntry.getMonster();

                if (toDamage > 0 && !summonEffect.getMonsterStati().isEmpty()) {
                    if (summonEffect.makeChanceResult()) {
                        for (Map.Entry<MonsterStatus, Integer> z : summonEffect.getMonsterStati().entrySet()) {
                            mob.applyStatus(chr, new MonsterStatusEffect(z.getKey(), z.getValue(), summonSkill.getId(), null, false), summonEffect.isPoison(), 4000, false);
                        }
                    }
                }
                mob.damage(chr, toDamage, true);
                if (!mob.isAlive()) {
                    chr.SendPacket(ResCMobPool.MobLeaveField(mob, OpsMobLeaveField.MOBLEAVEFIELD_ETC));
                }
            }
            return;
        }

        int unk10 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        int unk11 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        int tick = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 147) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        int unk12 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        int unk13 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        byte animation = cp.Decode1();
        int unk14 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        int unk15 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        byte numAttacked = cp.Decode1();
        short unk16 = cp.Decode2(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // x
        short unk17 = cp.Decode2(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // y
        short unk18 = cp.Decode2(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // x
        short unk19 = cp.Decode2(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // y

        List<SummonAttackEntry> allDamage = new ArrayList<>();

        for (int i = 0; i < numAttacked; i++) {
            MapleMonster mob = map.getMonsterByOid(cp.Decode4());
            if (mob == null) {
                continue;
            }
            int unk20 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)); // MobID
            byte unk21 = cp.Decode1();
            byte unk22 = cp.Decode1();
            byte unk23 = cp.Decode1();
            byte unk24 = cp.Decode1();
            short unk25 = cp.Decode2();
            short unk26 = cp.Decode2();
            short unk27 = cp.Decode2();
            short unk28 = cp.Decode2();
            short unk29 = cp.Decode2();
            int damage = cp.Decode4();
            allDamage.add(new SummonAttackEntry(mob, damage));
        }

        short unk30 = cp.Decode2(Config.LessOrEqual(Region.JMS, 147)); // X
        short unk31 = cp.Decode2(Config.LessOrEqual(Region.JMS, 147)); // Y

        map.broadcastMessageTo(chr, ResCSummonedPool.SummonedAttack(summon, animation, allDamage, chr.getLevel()), summon.getPosition());

        for (SummonAttackEntry attackEntry : allDamage) {
            int toDamage = attackEntry.getDamage();
            final MapleMonster mob = attackEntry.getMonster();
            mob.damage(chr, toDamage, true);
            if (!mob.isAlive()) {
                chr.SendPacket(ResCMobPool.MobLeaveField(mob, OpsMobLeaveField.MOBLEAVEFIELD_ETC));
            }
        }
    }

    // CSummoned::OnHit
    public static void OnHit(MapleCharacter chr, ClientPacket cp, TacosSummon summon) {
        int unkByte = cp.Decode1();
        int damage = cp.Decode4();
        int monsterIdFrom = cp.Decode4();

        int summon_hp = Math.max(0, summon.getHp() - damage);
        summon.setHp(summon_hp);
        chr.getMap().broadcastMessageTo(chr, ResCSummonedPool.SummonedHit(summon, damage, unkByte, monsterIdFrom), summon.getPosition());
        if (summon.getHp() <= 0) {
            chr.removeSummon();
        }
    }
}
