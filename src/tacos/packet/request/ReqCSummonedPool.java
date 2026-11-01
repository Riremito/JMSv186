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

import tacos.server.map.object.TacosMonster;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.client.TacosClient;
import tacos.config.Region;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCSummonedPool;
import tacos.config.Config;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsMoveAbility;
import tacos.packet.ops.OpsSkill_BB;
import tacos.server.map.object.TacosSummon;
import tacos.server.map.object.TacosSummon.TacosSummonAttackInfo;
import tacos.server.map.object.TacosSummon.TacosSummonAttackInfo.SummonDamage;

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
        TacosCharacter chr = client.getPlayer();
        if (chr == null) {
            return false;
        }

        TacosMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        int m_dwSummonedID = cp.Decode4(); // older version = SkillID

        TacosSummon summon = null;
        if (Config.LessOrEqual(Region.JMS, 131)) {
            for (TacosSummon player_summon : chr.getSummons()) {
                if (player_summon.getSkillID() == m_dwSummonedID) {
                    summon = player_summon;
                    break;
                }
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
                OnSkill(chr, cp, summon);
                return true;
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
    public static boolean OnMove(TacosCharacter chr, ClientPacket cp, TacosSummon summon) {
        if (summon.getMoveAbility() == OpsMoveAbility.MOVEABILITY_STOP) {
            return false;
        }

        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            summon.update(move_path);
            chr.getMap().splitSendPacket(chr, ResCSummonedPool.SummonedMove(summon, move_path), chr.getId());
        }

        return true;
    }

    // CSummoned::OnAttack
    public static void OnAttack(TacosCharacter chr, ClientPacket cp, TacosSummon summon) {
        TacosMap map = chr.getMap();

        boolean is_dr_check = Config.Equal(Region.KMST, 330);
        if (!is_dr_check) {
            int dr0 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
            int dr1 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        }
        int update_time = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 147) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        if (!is_dr_check) {
            int dr2 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
            int dr3 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        }
        byte nAction = cp.Decode1(); // nAction & 0x7F | (bLeft << 7)
        if (!is_dr_check) {
            int dwKey = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
            int Crc32 = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        }
        byte nMobCount = cp.Decode1();
        short x1 = cp.Decode2(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // x
        short y1 = cp.Decode2(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // y
        short x2 = cp.Decode2(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // x
        short y2 = cp.Decode2(Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)); // y
        int RepeatSkillPoint = cp.Decode4(Config.GreaterOrEqual(Region.GMS, 95) || Config.GreaterOrEqual(Region.JMS, 302));

        TacosSummonAttackInfo attack_info = new TacosSummonAttackInfo();
        attack_info.setCharLevel(chr.getLevel());
        attack_info.setAction(nAction);

        for (int i = 0; i < nMobCount; i++) {
            int mob_object_id = cp.Decode4();
            int dwTemplateID = cp.Decode4(Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 121) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 91) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 70)); // dwTemplateID
            byte nHitAction = cp.Decode1(); // nHitAction
            byte nForeAction = cp.Decode1(); // nForeAction & 0x7F | (IsLeft << 7)
            byte nFrameIdx = cp.Decode1(); // nFrameIdx
            byte CalcDamageStatIndex = cp.Decode1(); // CalcDamageStatIndex
            short unk25 = cp.Decode2();
            short unk26 = cp.Decode2();
            short unk27 = cp.Decode2();
            short unk28 = cp.Decode2();
            short tDelay = cp.Decode2(); // tDelay
            int damage = cp.Decode4(); // aDamage[i]

            attack_info.addDamage(mob_object_id, nHitAction, damage);
        }
        int Crc = cp.Decode4(Config.GreaterOrEqual(Region.GMS, 95)); // skill CRC
        short unk30 = cp.Decode2(Config.LessOrEqual(Region.JMS, 147)); // X
        short unk31 = cp.Decode2(Config.LessOrEqual(Region.JMS, 147)); // Y

        map.splitSendPacket(summon, ResCSummonedPool.SummonedAttack(summon, attack_info), chr.getId());

        for (SummonDamage damage : attack_info.getDamages()) {
            TacosMonster monster = map.getMonsterByOid(damage.getMobObjectId());
            if (monster != null) {
                monster.damage(chr, damage.getDamage(), true);
            }
        }
        if (summon.getSkill() == OpsSkill_BB.VALKYRIE_GABIOTA) {
            chr.removeSummon(summon);
        }
    }

    // CSummoned::OnSkill
    public static boolean OnSkill(TacosCharacter chr, ClientPacket cp, TacosSummon summon) {
        int nSkillID = cp.Decode4();
        byte unk1 = cp.Decode1();

        OpsSkill_BB summoned_skill = OpsSkill_BB.find(nSkillID);
        switch (summon.getSkill()) {
            case DARKKNIGHT_BEHOLDER -> {
                if (summoned_skill == OpsSkill_BB.DARKKNIGHT_BEHOLDERS_HEALING) {
                    // ダークスピリットヒール
                    return true;
                }
                if (summoned_skill == OpsSkill_BB.DARKKNIGHT_BEHOLDERS_BUFF) {
                    // ダークスピリットアップ
                    byte buff_type = cp.Decode1();
                    // TODO : random buff.
                    return true;
                }
            }
            default -> {
            }
        }

        DebugLogger.ErrorLog("OnSkill : " + nSkillID + " (" + summoned_skill + ")" + ", not coded.");
        return false;
    }

    // CSummoned::OnHit
    public static void OnHit(TacosCharacter chr, ClientPacket cp, TacosSummon summon) {
        int unkByte = cp.Decode1();
        int damage = cp.Decode4();
        int monsterIdFrom = cp.Decode4();

        int summon_hp = Math.max(0, summon.getHp() - damage);
        summon.setHp(summon_hp);
        chr.getMap().splitSendPacket(chr, ResCSummonedPool.SummonedHit(summon, damage, unkByte, monsterIdFrom), chr.getId());
        if (summon.getHp() <= 0) {
            chr.removeSummon(summon);
        }
    }
}
