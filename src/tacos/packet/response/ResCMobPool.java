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

import odin.client.MapleCharacter;
import tacos.config.Region;
import tacos.config.Config;
import tacos.config.Version;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.ServerPacket;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleNodes;
import tacos.client.TacosBuff;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsAttackIndex;
import tacos.packet.ops.OpsMobAppear;
import tacos.packet.ops.OpsMobLeaveField;

/**
 *
 * @author Riremito
 */
public class ResCMobPool {

    // CMobPool::OnMobEnterField
    public static ServerPacket MobEnterField(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEnterField);

        sp.Encode4(monster.getObjectId()); // dwMobID

        if (Version.LessOrEqual(Region.KMS, 1)) {
            sp.Encode4(monster.getId());
            sp.EncodeBuffer(CMob_Init(monster));
            return sp;
        }

        sp.Encode1(1); // 1 = Control normal, 5 = Control none
        sp.Encode4(monster.getId());

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }

        sp.EncodeBuffer(CMob_SetTemporaryStat(monster));
        sp.EncodeBuffer(CMob_Init(monster)); // if mob is not found in the map, extra data is read.
        return sp;
    }

    // CMob::SetTemporaryStat
    public static byte[] CMob_SetTemporaryStat(MapleMonster monster) {
        ServerPacket data = new ServerPacket();

        int[] buff_mask = TacosBuff.getMobBuffBuffer();
        for (int index = 0; index < buff_mask.length; index++) {
            data.Encode4(buff_mask[buff_mask.length - 1 - index]);
        }

        // MobStat::DecodeTemporary
        // TODO
        return data.getBytes();
    }

    // CMob::Init
    public static byte[] CMob_Init(MapleMonster monster) {
        ServerPacket data = new ServerPacket();

        data.Encode2(monster.getPosition().x); // m_ptPosPrev.x
        data.Encode2(monster.getPosition().y); // m_ptPosPrev.y
        data.Encode1(monster.getStance()); // m_nMoveAction_CS
        data.Encode2(monster.getFh()); // pvcMobActiveObj, Fh  causes fall down, credit to 垂垂 for fixing mob fall down issue
        data.Encode2(monster.getOriginFh()); // m_pInterface

        OpsMobAppear ops_at = monster.getAT();
        switch (ops_at) {
            case MOBAPPEAR_NORMAL, MOBAPPEAR_REGEN, MOBAPPEAR_SUSPENDED, MOBAPPEAR_DELAY -> {
                data.Encode1(monster.getAT().get()); // nAppearType
            }
            case MOBAPPEAR_REVIVED -> {
                data.Encode1(monster.getAT().get()); // nAppearType
                data.Encode4(monster.getLinkOid()); // dwOption
            }
            case MOBAPPEAR_EFFECT -> {
                data.Encode1(monster.getATEx()); // nAppearType
                data.Encode4(monster.getLinkOid()); // dwOption
            }
        }

        // KMS1
        if (Version.LessOrEqual(Region.KMS, 1)) {
            data.EncodeBuffer(CMob_SetTemporaryStat(monster));
            return data.getBytes();
        }

        // KMS31
        if (Version.LessOrEqual(Region.KMS, 31)) {
            return data.getBytes();
        }

        // KMS41
        data.Encode1(monster.getCarnivalTeam()); // m_nTeamForMCarnival

        // JMS131
        if (Version.LessOrEqual(Region.JMS, 131)) {
            return data.getBytes();
        }
        // JMS146
        if (Config.JMS146orLater()) {
            data.Encode4(0); // nEffectItemID
        }
        // JMS186, GMS95
        // not in KMST330, TWMS125
        if (Config.JMS165orLater()) {
            data.Encode4(0); // m_nPhase
        }

        return data.getBytes();
    }

    // CMobPool::OnMobLeaveField
    public static ServerPacket MobLeaveField(MapleMonster monster, OpsMobLeaveField dead_type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobLeaveField);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(dead_type.get()); // m_nDeadType

        if (dead_type == OpsMobLeaveField.MOBLEAVEFIELD_SWALLOW) {
            sp.Encode4(0); // m_dwSwallowCharacterID
        }

        return sp;
    }

    // CMobPool::OnMobChangeController
    public static ServerPacket MobChangeController(MapleMonster monster, boolean aggro) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobChangeController);

        int nLevel = aggro ? 2 : 1;
        sp.Encode1(nLevel); // nLevel, local or not.

        // GMS95
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            // nLevel != 0 && CClientOptMan::GetOpt & 2
            /*
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(0);
             */
        }

        sp.Encode4(monster.getObjectId()); // dwMobId

        if (nLevel != 0) {
            if (Version.LessOrEqual(Region.KMS, 1)) {
                // none
            } else {
                sp.Encode1(1); // nCalcDamageIndex, 1 = Control normal, 5 = Control none
            }
            // CMobPool::SetLocalMob
            sp.Encode4(monster.getId()); // dwTemplateID

            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode1(0);
            }

            if (Version.LessOrEqual(Region.KMS, 1)) {
                // none.
            } else {
                sp.EncodeBuffer(CMob_SetTemporaryStat(monster));
            }
            sp.EncodeBuffer(CMob_Init(monster)); // if mob is not found in the map, extra data is read.
        } else {
            // CMobPool::SetRemoteMob
            // no packet data.
        }

        return sp;
    }

    public static ServerPacket MobChangeController(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobChangeController);

        sp.Encode1(0);
        sp.Encode4(monster.getObjectId());
        return sp;
    }

    // CMob::OnMove
    public static ServerPacket MobMove(MapleMonster monster, boolean bNextAttackPossible, int bLeft, int mob_skill, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobMove);

        sp.Encode4(monster.getObjectId()); // dwMobIDs

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            // none
        } else if (Config.JMS186orLater()
                || Version.GreaterOrEqual(Region.KMS, 95)) {
            sp.Encode1(0); // bNotForceLandingWhenDiscard
            sp.Encode1(0); // bNotChangeAction
        }

        sp.Encode1(bNextAttackPossible ? 1 : 0); // bNextAttackPossible
        sp.Encode1(bLeft); // bLeft
        sp.Encode4(mob_skill);

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
            sp.Encode1(0);
        } else if (Config.JMS186orLater()
                || Version.GreaterOrEqual(Region.KMS, 95)) {
            sp.Encode4(0); //  if this is not 0, Encode4 x2 x loop count
            sp.Encode4(0); //  if this is not 0, Encode4 x loop count
        }

        sp.EncodeBuffer(data.get());
        return sp;
    }

    // CMob::OnCtrlAck
    public static ServerPacket MobCtrlAck(MapleMonster monster, short moveid, int skillId, int skillLevel) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobCtrlAck);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode2(moveid);
        sp.Encode1(monster.isControllerHasAggro() ? 1 : 0);
        sp.Encode2(monster.getMp());
        sp.Encode1(skillId);
        sp.Encode1(skillLevel);

        if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76)
                || Version.GreaterOrEqual(Region.KMS, 95)) {
            sp.Encode4(0);
        }

        return sp;
    }

    // CMob::OnStatSet
    public static ServerPacket MobStatSet(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobStatSet);

        sp.Encode4(monster.getObjectId()); // dwMobID
        // TODO
        return sp;
    }

    // CMob::OnStatReset
    public static ServerPacket MobStatReset(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobStatReset);

        sp.Encode4(monster.getObjectId()); // dwMobID
        // TODO
        return sp;
    }

    // CMob::OnSuspendReset
    public static ServerPacket MobSuspendReset(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSuspendReset);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(1);
        return sp;
    }

    // CMob::OnAffected
    public static ServerPacket MobAffected(MapleMonster monster, int nSkillID, int tStart) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobAffected);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(nSkillID); // nSkillID
        sp.Encode2(tStart); // tStart (val + update_time)
        return sp;
    }

    // CMob::OnDamaged
    public static ServerPacket MobDamaged(MapleMonster monster, int nDamage, int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobDamaged);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(type); // 2 = hide.
        sp.Encode4(nDamage); // nDamage

        if (type != 0) {
            sp.Encode4((int) monster.getHp());
            sp.Encode4((int) monster.getMobMaxHp());
        }

        return sp;
    }

    // CMob::OnSpecialEffectBySkill
    public static ServerPacket MobSpecialEffectBySkill(MapleMonster monster, MapleCharacter chr, int nSkillID, int tDelay) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSpecialEffectBySkill);

        sp.Encode4(monster.getObjectId());// dwMobID
        sp.Encode4(nSkillID); // nSkillID
        sp.Encode4(chr.getId()); // dwCharacterID
        sp.Encode2(tDelay); // tDelay
        return sp;
    }

    // CMob::OnHPIndicator
    public static ServerPacket MobHPIndicator(MapleMonster monster, int m_nHPpercentage) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobHPIndicator);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(m_nHPpercentage); // m_nHPpercentage
        return sp;
    }

    // CMobPool::OnMobCrcKeyChanged
    public static ServerPacket MobCrcKeyChanged(MapleMonster monster, int m_dwMobCrcKey) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobCrcKeyChanged);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(m_dwMobCrcKey); // m_dwMobCrcKey
        return sp;
    }

    // CMob::OnCatchEffect
    public static ServerPacket MobCatchEffect(MapleMonster monster, boolean bSuccess) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobCatchEffect);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(bSuccess ? 1 : 0); // bSuccess
        sp.Encode1(1); // tDelay. 1 = 270 ms
        return sp;
    }

    // CMob::OnEffectByItem
    public static ServerPacket MobEffectByItem(MapleMonster monster, int nItemID, boolean bSuccess) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEffectByItem);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(nItemID); // nItemID
        sp.Encode1(bSuccess ? 1 : 0); // bSuccess
        return sp;
    }

    // CMob::OnMobSpeaking
    public static ServerPacket MobSpeaking(MapleMonster monster, int nSpeakInfo, int nSpeech) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSpeaking);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(nSpeakInfo); // nSpeakInfo
        sp.Encode4(nSpeech); // nSpeech
        return sp;
    }

    // CMob::OnIncMobChargeCount
    public static ServerPacket MobChargeCount(MapleMonster monster, int m_nMobChargeCount, int m_bAttackReady) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobChargeCount);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(m_nMobChargeCount); // m_nMobChargeCount
        sp.Encode4(m_bAttackReady); // m_bAttackReady
        return sp;
    }

    // CMob::OnMobSkillDelay
    public static ServerPacket MobSkillDelay(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSkillDelay);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(0); // m_delaySkill.tSkillDelayTime
        sp.Encode4(0); // m_delaySkill.nSkillID
        sp.Encode4(0); // m_delaySkill.nSLV
        sp.Encode4(0); // m_delaySkill.nOption
        return sp;
    }

    // CMob::OnEscortFullPath
    public static ServerPacket MobRequestResultEscortInfo(MapleMonster monster, MapleMap map) {
        //idk.
        if (monster.getNodePacket() != null) {
            return monster.getNodePacket();
        }

        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobRequestResultEscortInfo);

        sp.Encode4(monster.getObjectId()); //?
        sp.Encode4(map.getNodeInfo().getNodes().size());
        sp.Encode4(monster.getPosition().x);
        sp.Encode4(monster.getPosition().y);
        for (MapleNodes.MapleNodeInfo mni : map.getNodeInfo().getNodes()) {
            sp.Encode4(mni.x);
            sp.Encode4(mni.y);
            sp.Encode4(mni.attr);
            if (mni.attr == 2) {
                //msg
                sp.Encode4(500); //? talkMonster
            }
        }

        sp.EncodeZeroBytes(6);
        monster.setNodePacket(sp);

        return monster.getNodePacket();
    }

    // CMob::OnEscortStopEndPermmision
    public static ServerPacket MobEscortStopEndPermmision(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEscortStopEndPermmision);

        sp.Encode4(monster.getObjectId()); // dwMobID
        return sp;
    }

    // CMob::OnEscortStopSay
    public static ServerPacket MobEscortStopSay(MapleMonster monster, int nChatBalloon, String msg) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEscortStopSay);

        boolean is_msg = !msg.equals("");

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(500); // m_tEscortStopActTime
        sp.Encode4(nChatBalloon); // nChatBalloon
        sp.Encode1(nChatBalloon <= 0 ? 0 : 1); // bWeather
        sp.Encode1(is_msg ? 1 : 0);

        if (is_msg) {
            sp.EncodeStr(msg);
            sp.Encode4(0); // m_nEscortStopAct
        }

        return sp;
    }

    // CMob::OnEscortReturnBefore
    public static ServerPacket MobEscortReturnBefore(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEscortReturnBefore);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(0);
        return sp;
    }

    // CMob::OnNextAttack
    public static ServerPacket MobNextAttack(MapleMonster monster, int nForceAttackIdx) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobNextAttack);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(nForceAttackIdx); // nForceAttackIdx
        return sp;
    }

    // CMob::OnMobAttackedByMob
    public static ServerPacket MobAttackedByMob(MapleMonster monster, int nAttackIdx, int nDamage) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobAttackedByMob);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(nAttackIdx); // nAttackIdx
        sp.Encode4(nDamage); // nDamage

        // -2 < nAttackIdx
        if (1 <= nAttackIdx || nAttackIdx == OpsAttackIndex.AttackIndex_Mob_Physical.get() || nAttackIdx == OpsAttackIndex.AttackIndex_Mob_Magic.get()) {
            sp.Encode4(monster.getId()); // dwMobTemplateID
            sp.Encode1(0); // bLeft
        }

        return sp;
    }
}
