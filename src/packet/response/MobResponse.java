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

import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.ServerConfig;
import debug.Debug;
import handling.MaplePacket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import packet.request.struct.CMovePath;
import packet.ServerPacket;
import packet.response.struct.Structure;
import server.life.MapleMonster;
import server.life.MobSkill;

/**
 *
 * @author Riremito
 */
public class MobResponse {

    // moveMonster
    public static MaplePacket moveMonster(boolean useskill, int skill, int skill1, int skill2, int skill3, int skill4, int oid, CMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MobMove);
        sp.Encode4(oid);
        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion()) || ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsKMS()) {
            sp.Encode1(0); // bNotForceLandingWhenDiscard
            sp.Encode1(0); // bNotChangeAction
        }
        sp.Encode1(useskill ? 1 : 0); // bNextAttackPossible
        sp.Encode1(skill); // bLeft
        // sEffect.m_Data Encode4
        {
            sp.Encode1(skill1); // skillId
            sp.Encode1(skill2); // skillLevel
            sp.Encode1(skill3); // effectDelay
            sp.Encode1(skill4); // effectDelay
        }
        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion()) || ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsKMS()) {
            sp.Encode4(0); //  if this is not 0, Encode4 x2 x loop count
            sp.Encode4(0); //  if this is not 0, Encode4 x loop count
        }
        sp.EncodeBuffer(data.get());
        return sp.Get();
    }

    public static MaplePacket cancelMonsterStatus(int oid, MonsterStatus stat) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobStatReset);
        p.Encode4(oid);
        p.Encode8(Structure.getSpecialLongMask(Collections.singletonList(stat)));
        p.Encode8(Structure.getLongMask(Collections.singletonList(stat)));
        p.Encode1(1); // reflector is 3~!??
        p.Encode1(2); // ? v97
        return p.Get();
    }

    public static MaplePacket applyMonsterStatus(final int oid, final MonsterStatus mse, int x, MobSkill skil) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobStatSet);
        p.Encode4(oid);
        p.Encode8(Structure.getSpecialLongMask(Collections.singletonList(mse)));
        p.Encode8(Structure.getLongMask(Collections.singletonList(mse)));
        p.Encode2(x);
        p.Encode2(skil.getSkillId());
        p.Encode2(skil.getSkillLevel());
        p.Encode2(mse.isEmpty() ? 1 : 0); // might actually be the buffTime but it's not displayed anywhere
        p.Encode2(0); // delay in ms
        p.Encode1(1); // size
        p.Encode1(1); // ? v97
        return p.Get();
    }

    public static MaplePacket applyMonsterStatus(final int oid, final MonsterStatusEffect mse) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobStatSet);
        p.Encode4(oid);
        //aftershock extra int here
        p.Encode8(Structure.getSpecialLongMask(Collections.singletonList(mse.getStati())));
        p.Encode8(Structure.getLongMask(Collections.singletonList(mse.getStati())));
        p.Encode2(mse.getX());
        if (mse.isMonsterSkill()) {
            p.Encode2(mse.getMobSkill().getSkillId());
            p.Encode2(mse.getMobSkill().getSkillLevel());
        } else if (mse.getSkill() > 0) {
            p.Encode4(mse.getSkill());
        }
        p.Encode2(mse.getStati().isEmpty() ? 1 : 0); // might actually be the buffTime but it's not displayed anywhere
        p.Encode2(0); // delay in ms
        p.Encode1(1); // size
        p.Encode1(1); // ? v97
        return p.Get();
    }

    public static MaplePacket applyMonsterStatus(final int oid, final Map<MonsterStatus, Integer> stati, final List<Integer> reflection, MobSkill skil) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobStatSet);
        p.Encode4(oid);
        p.Encode8(Structure.getSpecialLongMask(stati.keySet()));
        p.Encode8(Structure.getLongMask(stati.keySet()));
        for (Map.Entry<MonsterStatus, Integer> mse : stati.entrySet()) {
            p.Encode2(mse.getValue());
            p.Encode2(skil.getSkillId());
            p.Encode2(skil.getSkillLevel());
            p.Encode2(mse.getKey().isEmpty() ? 1 : 0); // might actually be the buffTime but it's not displayed anywhere
        }
        for (Integer ref : reflection) {
            p.Encode4(ref);
        }
        p.Encode4(0);
        p.Encode2(0); // delay in ms
        int size = stati.size(); // size
        if (reflection.size() > 0) {
            size /= 2; // This gives 2 buffs per reflection but it's really one buff
        }
        p.Encode1(size); // size
        p.Encode1(1); // ? v97
        return p.Get();
    }

    // healMonster
    public static MaplePacket Heal(MapleMonster m, final int heal) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobDamaged);
        p.Encode4(m.getObjectId());
        p.Encode1(0);
        p.Encode4(-heal);
        return p.Get();
    }

    // killMonster
    public static MaplePacket Kill(MapleMonster m, int animation) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobLeaveField);
        p.Encode4(m.getObjectId());
        p.Encode1(animation); // 0 = dissapear, 1 = fade out, 2+ = special
        return p.Get();
    }

    // ???
    public static MaplePacket Kill(int oid, int animation) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobLeaveField);
        p.Encode4(oid);
        p.Encode1(animation);
        return p.Get();
    }

    // moveMonsterResponse
    public static MaplePacket moveMonsterResponse(MapleMonster m, short moveid, int skillId, int skillLevel) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MobCtrlAck);
        sp.Encode4(m.getObjectId());
        sp.Encode2(moveid);
        sp.Encode1(m.isControllerHasAggro() ? 1 : 0);
        sp.Encode2(m.getMp());
        sp.Encode1(skillId);
        sp.Encode1(skillLevel);
        if (ServerConfig.IsPostBB() && ((ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion() || ServerConfig.IsKMS()))) {
            sp.Encode4(0);
        }
        return sp.Get();
    }

    // damageMonster
    public static MaplePacket Damage(MapleMonster m, final long damage) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobDamaged);
        p.Encode4(m.getObjectId());
        p.Encode1(0);
        if (damage > Integer.MAX_VALUE) {
            p.Encode4(Integer.MAX_VALUE);
        } else {
            p.Encode4((int) damage);
        }
        return p.Get();
    }

    // ???
    public static MaplePacket Damage(int oid, final long damage) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobDamaged);
        p.Encode4(oid);
        p.Encode1(0);
        if (damage > Integer.MAX_VALUE) {
            p.Encode4(Integer.MAX_VALUE);
        } else {
            p.Encode4((int) damage);
        }
        return p.Get();
    }

    // spawnMonster
    public static MaplePacket Spawn(MapleMonster life, int spawnType, int effect, int link) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MobEnterField);
        sp.Encode4(life.getObjectId());
        sp.Encode1(1); // 1 = Control normal, 5 = Control none
        sp.Encode4(life.getId());
        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 164) {
            sp.Encode4(0); // 後でなおす
        } else {
            sp.EncodeBuffer(Structure.MonsterStatus(life));
        }

        // credit to 垂垂 for fixing mob fall down issue
        if (life.getFh() == 0) {
            Debug.DebugLog("Spawn FH = 0");
        }

        sp.Encode2(life.getPosition().x); // m_ptPosPrev.x
        sp.Encode2(life.getPosition().y); // m_ptPosPrev.y
        sp.Encode1(life.getStance()); // m_nMoveAction_CS
        sp.Encode2(life.getFh()); // pvcMobActiveObj
        sp.Encode2(life.getOriginFh()); // m_pInterface
        sp.Encode1(spawnType);
        if (spawnType == -3 || 0 <= spawnType) {
            sp.Encode4(link); // dwOption
        }
        sp.Encode1(life.getCarnivalTeam()); // m_nTeamForMCarnival
        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion()) || ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsKMS()) {
            sp.Encode4(0); // nEffectItemID
        }
        if ((ServerConfig.IsJMS() && 165 <= ServerConfig.GetVersion()) || ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsKMS()) {
            sp.Encode4(0); // m_nPhase
        }
        return sp.Get();
    }

    public static MaplePacket removeTalkMonster(int oid) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.REMOVE_TALK_MONSTER);
        p.Encode4(oid);
        return p.Get();
    }

    // damageFriendlyMob
    public static MaplePacket damageFriendlyMob(MapleMonster mob, final long damage, final boolean display) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobDamaged);
        p.Encode4(mob.getObjectId());
        p.Encode1(display ? 1 : 2); //false for when shammos changes map!
        if (damage > Integer.MAX_VALUE) {
            p.Encode4(Integer.MAX_VALUE);
        } else {
            p.Encode4((int) damage);
        }
        if (mob.getHp() > Integer.MAX_VALUE) {
            p.Encode4((int) (((double) mob.getHp() / mob.getMobMaxHp()) * Integer.MAX_VALUE));
        } else {
            p.Encode4((int) mob.getHp());
        }
        if (mob.getMobMaxHp() > Integer.MAX_VALUE) {
            p.Encode4(Integer.MAX_VALUE);
        } else {
            p.Encode4((int) mob.getMobMaxHp());
        }
        return p.Get();
    }

    // controlMonster
    public static MaplePacket Control(MapleMonster life, boolean newSpawn, boolean aggro) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MobChangeController);
        sp.Encode1(aggro ? 2 : 1);
        sp.Encode4(life.getObjectId());
        sp.Encode1(1); // 1 = Control normal, 5 = Control none
        sp.Encode4(life.getId());
        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 164) {
            sp.Encode4(0); // 後でなおす
        } else {
            sp.EncodeBuffer(Structure.MonsterStatus(life));
        }

        // credit to 垂垂 for fixing mob fall down issue
        if (life.getFh() == 0) {
            Debug.DebugLog("Control FH = 0");
        }

        sp.Encode2(life.getPosition().x);
        sp.Encode2(life.getPosition().y);
        sp.Encode1(life.getStance()); // Bitfield
        sp.Encode2(life.getFh()); // FH
        sp.Encode2(life.getOriginFh()); // Origin FH
        sp.Encode1(life.isFake() ? -4 : newSpawn ? -2 : -1);
        sp.Encode1(life.getCarnivalTeam());
        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion()) || ServerConfig.IsTWMS() || ServerConfig.IsCMS() || ServerConfig.IsKMS()) {
            sp.Encode4(0);
            sp.Encode4(0);
        }
        return sp.Get();
    }

    // stopControllingMonster
    public static MaplePacket StopControl(MapleMonster m) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MobChangeController);
        sp.Encode1(0);
        sp.Encode4(m.getObjectId());
        return sp.Get();
    }

    // showMonsterHP
    public static MaplePacket ShowHP(MapleMonster m, int remhppercentage) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobHPIndicator);
        p.Encode4(m.getObjectId());
        p.Encode1(remhppercentage);
        return p.Get();
    }

    public static MaplePacket talkMonster(int oid, int itemId, String msg) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.TALK_MONSTER);
        p.Encode4(oid);
        p.Encode4(500); //?
        p.Encode4(itemId);
        p.Encode1(itemId <= 0 ? 0 : 1);
        p.Encode1(msg == null || msg.length() <= 0 ? 0 : 1);
        if (msg != null && msg.length() > 0) {
            p.EncodeStr(msg);
        }
        p.Encode4(1); //?
        return p.Get();
    }

}
