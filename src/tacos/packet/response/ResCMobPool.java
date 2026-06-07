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
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.ServerPacket;
import odin.server.life.MapleMonster;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleNodes;
import tacos.client.TacosBuff;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsAttackIndex;

/**
 *
 * @author Riremito
 */
public class ResCMobPool {

    public static MaplePacket MobEnterField_KMS1(MapleMonster monster, int spawnType, int effect, int link) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEnterField);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(monster.getId());

        // CMob::Init
        sp.Encode2(monster.getPosition().x); // m_ptPosPrev.x
        sp.Encode2(monster.getPosition().y); // m_ptPosPrev.y
        sp.Encode1(monster.getStance()); // m_nMoveAction_CS
        sp.Encode2(monster.getFh()); // pvcMobActiveObj
        sp.Encode2(monster.getOriginFh()); // m_pInterface
        sp.Encode1(spawnType);

        if (spawnType == -3 || 0 <= spawnType) {
            sp.Encode4(link); // dwOption
        }

        sp.Encode4(0); // mob stat?
        return sp.get();
    }

    // CMobPool::OnMobEnterField
    public static MaplePacket MobEnterField(MapleMonster monster, int spawnType, int effect, int link) {
        if (Version.Equal(Region.KMS, 1)) {
            return MobEnterField_KMS1(monster, spawnType, effect, link);
        }

        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEnterField);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(1); // 1 = Control normal, 5 = Control none
        sp.Encode4(monster.getId());

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }

        // CMob::SetTemporaryStat
        int[] buff_mask = TacosBuff.getMobBuffBuffer();
        for (int index = 0; index < buff_mask.length; index++) {
            sp.Encode4(buff_mask[buff_mask.length - 1 - index]);
        }
        // MobStat::DecodeTemporary

        // credit to 垂垂 for fixing mob fall down issue
        if (monster.getFh() == 0) {
            DebugLogger.DebugLog("MobEnterField : FH = 0");
        }

        // CMob::Init
        sp.Encode2(monster.getPosition().x); // m_ptPosPrev.x
        sp.Encode2(monster.getPosition().y); // m_ptPosPrev.y
        sp.Encode1(monster.getStance()); // m_nMoveAction_CS
        sp.Encode2(monster.getFh()); // pvcMobActiveObj
        sp.Encode2(monster.getOriginFh()); // m_pInterface
        sp.Encode1(spawnType);

        if (spawnType == -3 || 0 <= spawnType) {
            sp.Encode4(link); // dwOption
        }

        if (Version.LessOrEqual(Region.KMS, 31)) {
            return sp.get();
        }

        // KMS41
        sp.Encode1(monster.getCarnivalTeam()); // m_nTeamForMCarnival

        if (Version.LessOrEqual(Region.JMS, 131)) {
            return sp.get();
        }
        // JMS146
        if (ServerConfig.JMS146orLater()) {
            sp.Encode4(0); // nEffectItemID
        }
        // JMS186, GMS95
        // not in KMST330, TWMS125
        if (ServerConfig.JMS165orLater()) {
            sp.Encode4(0); // m_nPhase
        }

        return sp.get();
    }

    // CMobPool::OnMobLeaveField
    public static MaplePacket MobLeaveField(MapleMonster monster, int animation) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobLeaveField);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(animation); // 0 = dissapear, 1 = fade out, 2+ = special
        return sp.get();
    }

    // CMobPool::OnMobChangeController
    public static MaplePacket MobChangeController(MapleMonster monster, boolean aggro) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobChangeController);

        int nLevel = aggro ? 2 : 1;
        sp.Encode1(nLevel); // nLevel, local or not.

        // GMS95
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            // nLevel != 0 && CClientOptMan::GetOpt & 2
            /*
            sp.Encode4(0);
            sp.Encode4(0);
            sp.Encode4(0);
             */
        }

        sp.Encode4(monster.getObjectId()); // dwMobId

        if (Version.LessOrEqual(Region.KMS, 1)) {
            return sp.get();
        }

        if (nLevel != 0) {
            sp.Encode1(1); // nCalcDamageIndex, 1 = Control normal, 5 = Control none
            // CMobPool::SetLocalMob
            sp.Encode4(monster.getId()); // dwTemplateID

            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode1(0);
            }

            // CMob::SetTemporaryStat
            int[] buff_mask = TacosBuff.getMobBuffBuffer();
            for (int index = 0; index < buff_mask.length; index++) {
                sp.Encode4(buff_mask[buff_mask.length - 1 - index]);
            }
            // MobStat::DecodeTemporary
        } else {
            // CMobPool::SetRemoteMob
            // no packet data.
        }

        return sp.get();
    }

    public static MaplePacket MobChangeController(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobChangeController);

        sp.Encode1(0);
        sp.Encode4(monster.getObjectId());
        return sp.get();
    }

    // CMob::OnMove
    public static MaplePacket MobMove(MapleMonster monster, boolean bNextAttackPossible, int bLeft, int mob_skill, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobMove);

        sp.Encode4(monster.getObjectId()); // dwMobIDs

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            // none
        } else if (ServerConfig.JMS186orLater()
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
        } else if (ServerConfig.JMS186orLater()
                || Version.GreaterOrEqual(Region.KMS, 95)) {
            sp.Encode4(0); //  if this is not 0, Encode4 x2 x loop count
            sp.Encode4(0); //  if this is not 0, Encode4 x loop count
        }

        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    // CMob::OnCtrlAck
    public static MaplePacket MobCtrlAck(MapleMonster monster, short moveid, int skillId, int skillLevel) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobCtrlAck);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode2(moveid);
        sp.Encode1(monster.isControllerHasAggro() ? 1 : 0);
        sp.Encode2(monster.getMp());
        sp.Encode1(skillId);
        sp.Encode1(skillLevel);

        if (ServerConfig.JMS194orLater()
                || Version.GreaterOrEqual(Region.KMS, 95)) {
            sp.Encode4(0);
        }

        return sp.get();
    }

    // CMob::OnStatSet
    public static MaplePacket MobStatSet(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobStatSet);

        sp.Encode4(monster.getObjectId()); // dwMobID
        // TODO
        return sp.get();
    }

    // CMob::OnStatReset
    public static MaplePacket MobStatReset(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobStatReset);

        sp.Encode4(monster.getObjectId()); // dwMobID
        // TODO
        return sp.get();
    }

    // CMob::OnSuspendReset
    public static MaplePacket MobSuspendReset(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSuspendReset);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(1);
        return sp.get();
    }

    // CMob::OnAffected
    public static MaplePacket MobAffected(MapleMonster monster, int nSkillID, int tStart) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobAffected);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(nSkillID); // nSkillID
        sp.Encode2(tStart); // tStart (val + update_time)
        return sp.get();
    }

    // CMob::OnDamaged
    public static MaplePacket MobDamaged(MapleMonster monster, int nDamage, int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobDamaged);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(type); // 2 = hide.
        sp.Encode4(nDamage); // nDamage

        if (type != 0) {
            sp.Encode4((int) monster.getHp());
            sp.Encode4((int) monster.getMobMaxHp());
        }

        return sp.get();
    }

    // CMob::OnSpecialEffectBySkill
    public static MaplePacket MobSpecialEffectBySkill(MapleMonster monster, MapleCharacter chr, int nSkillID, int tDelay) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSpecialEffectBySkill);

        sp.Encode4(monster.getObjectId());// dwMobID
        sp.Encode4(nSkillID); // nSkillID
        sp.Encode4(chr.getId()); // dwCharacterID
        sp.Encode2(tDelay); // tDelay
        return sp.get();
    }

    // CMob::OnHPIndicator
    public static MaplePacket MobHPIndicator(MapleMonster monster, int m_nHPpercentage) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobHPIndicator);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(m_nHPpercentage); // m_nHPpercentage
        return sp.get();
    }

    // CMobPool::OnMobCrcKeyChanged
    public static MaplePacket MobCrcKeyChanged(MapleMonster monster, int m_dwMobCrcKey) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobCrcKeyChanged);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(m_dwMobCrcKey); // m_dwMobCrcKey
        return sp.get();
    }

    // CMob::OnCatchEffect
    public static MaplePacket MobCatchEffect(MapleMonster monster, boolean bSuccess) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobCatchEffect);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(bSuccess ? 1 : 0); // bSuccess
        sp.Encode1(1); // tDelay. 1 = 270 ms
        return sp.get();
    }

    // CMob::OnEffectByItem
    public static MaplePacket MobEffectByItem(MapleMonster monster, int nItemID, boolean bSuccess) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEffectByItem);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(nItemID); // nItemID
        sp.Encode1(bSuccess ? 1 : 0); // bSuccess
        return sp.get();
    }

    // CMob::OnMobSpeaking
    public static MaplePacket MobSpeaking(MapleMonster monster, int nSpeakInfo, int nSpeech) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSpeaking);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(nSpeakInfo); // nSpeakInfo
        sp.Encode4(nSpeech); // nSpeech
        return sp.get();
    }

    // CMob::OnIncMobChargeCount
    public static MaplePacket MobChargeCount(MapleMonster monster, int m_nMobChargeCount, int m_bAttackReady) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobChargeCount);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(m_nMobChargeCount); // m_nMobChargeCount
        sp.Encode4(m_bAttackReady); // m_bAttackReady
        return sp.get();
    }

    // CMob::OnMobSkillDelay
    public static MaplePacket MobSkillDelay(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobSkillDelay);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(0); // m_delaySkill.tSkillDelayTime
        sp.Encode4(0); // m_delaySkill.nSkillID
        sp.Encode4(0); // m_delaySkill.nSLV
        sp.Encode4(0); // m_delaySkill.nOption
        return sp.get();
    }

    // CMob::OnEscortFullPath
    public static MaplePacket MobRequestResultEscortInfo(MapleMonster monster, MapleMap map) {
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
        monster.setNodePacket(sp.get());

        return monster.getNodePacket();
    }

    // CMob::OnEscortStopEndPermmision
    public static MaplePacket MobEscortStopEndPermmision(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEscortStopEndPermmision);

        sp.Encode4(monster.getObjectId()); // dwMobID
        return sp.get();
    }

    // CMob::OnEscortStopSay
    public static MaplePacket MobEscortStopSay(MapleMonster monster, int nChatBalloon, String msg) {
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

        return sp.get();
    }

    // CMob::OnEscortReturnBefore
    public static MaplePacket MobEscortReturnBefore(MapleMonster monster) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEscortReturnBefore);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(0);
        return sp.get();
    }

    // CMob::OnNextAttack
    public static MaplePacket MobNextAttack(MapleMonster monster, int nForceAttackIdx) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobNextAttack);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode4(nForceAttackIdx); // nForceAttackIdx
        return sp.get();
    }

    // CMob::OnMobAttackedByMob
    public static MaplePacket MobAttackedByMob(MapleMonster monster, int nAttackIdx, int nDamage) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobAttackedByMob);

        sp.Encode4(monster.getObjectId()); // dwMobID
        sp.Encode1(nAttackIdx); // nAttackIdx
        sp.Encode4(nDamage); // nDamage

        // -2 < nAttackIdx
        if (1 <= nAttackIdx || nAttackIdx == OpsAttackIndex.AttackIndex_Mob_Physical.get() || nAttackIdx == OpsAttackIndex.AttackIndex_Mob_Magic.get()) {
            sp.Encode4(monster.getId()); // dwMobTemplateID
            sp.Encode1(0); // bLeft
        }

        return sp.get();
    }
}
