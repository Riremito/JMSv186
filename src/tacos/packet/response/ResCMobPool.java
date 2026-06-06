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

import odin.client.status.MonsterStatus;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.ServerPacket;
import odin.server.life.MapleMonster;
import odin.server.life.MobSkill;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleNodes;
import tacos.client.TacosBuff;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCMobPool {

    public static MaplePacket MobEnterField(MapleMonster monster, int spawnType, int effect, int link) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEnterField);

        sp.Encode4(monster.getObjectId());
        if (Version.LessOrEqual(Region.KMS, 1)) {

        } else {
            sp.Encode1(1); // 1 = Control normal, 5 = Control none
        }
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

        if (Version.LessOrEqual(Region.KMS, 1)) {
            sp.Encode4(0); // mob stat?
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

    public static MaplePacket MobLeaveField(MapleMonster monster, int animation) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobLeaveField);

        sp.Encode4(monster.getObjectId());
        sp.Encode1(animation); // 0 = dissapear, 1 = fade out, 2+ = special
        return sp.get();
    }

    // controlMonster
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

        if (nLevel != 0) {
            if (Version.LessOrEqual(Region.KMS, 1)) {
            } else {
                sp.Encode1(1); // nCalcDamageIndex, 1 = Control normal, 5 = Control none
            }
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

    public static MaplePacket MobMove(MapleMonster monster, boolean bNextAttackPossible, int bLeft, int mob_skill, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobMove);

        sp.Encode4(monster.getObjectId()); // mob object id

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

    public static MaplePacket MobCtrlAck(MapleMonster monster, short moveid, int skillId, int skillLevel) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobCtrlAck);

        sp.Encode4(monster.getObjectId());
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

    public static MaplePacket MobStatSet(int oid, MonsterStatus mse, int x, MobSkill skil) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobStatSet);

        sp.Encode4(oid);
        sp.Encode8(0);
        sp.Encode8(0);
        sp.Encode2(x);
        sp.Encode2(skil.getSkillId());
        sp.Encode2(skil.getSkillLevel());
        sp.Encode2(mse.isEmpty() ? 1 : 0); // might actually be the buffTime but it's not displayed anywhere
        sp.Encode2(0); // delay in ms
        sp.Encode1(1); // size
        sp.Encode1(1); // ? v97
        return sp.get();
    }

    public static MaplePacket MobStatReset(int oid, MonsterStatus stat) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobStatReset);

        sp.Encode4(oid);
        sp.Encode8(0);
        sp.Encode8(0);
        sp.Encode1(1); // reflector is 3~!??
        sp.Encode1(2); // ? v97
        return sp.get();
    }

    public static MaplePacket MobDamaged(MapleMonster monster, int nDamage, int type) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobDamaged);

        sp.Encode4(monster.getObjectId());
        sp.Encode1(type); // 2 = hide.
        sp.Encode4(nDamage);
        if (type != 0) {
            sp.Encode4((int) monster.getHp());
            sp.Encode4((int) monster.getMobMaxHp());
        }
        return sp.get();
    }

    public static MaplePacket MobEscortReturnBefore(int oid) {
        ServerPacket p = new ServerPacket(ServerPacketHeader.LP_MobEscortReturnBefore);
        p.Encode4(oid);
        return p.get();
    }

    public static MaplePacket MobHPIndicator(MapleMonster monster, int remhppercentage) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobHPIndicator);

        sp.Encode4(monster.getObjectId());
        sp.Encode1(remhppercentage);
        return sp.get();
    }

    public static MaplePacket MobEscortStopSay(int oid, int itemId, String msg) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEscortStopSay);
        sp.Encode4(oid);
        sp.Encode4(500); //?
        sp.Encode4(itemId);
        sp.Encode1(itemId <= 0 ? 0 : 1);
        sp.Encode1(msg == null || msg.length() <= 0 ? 0 : 1);
        if (msg != null && msg.length() > 0) {
            sp.EncodeStr(msg);
        }
        sp.Encode4(1); //?
        return sp.get();
    }

    public static MaplePacket MobEffectByItem(int mobid, int itemid, byte success) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobEffectByItem);

        sp.Encode4(mobid);
        sp.Encode4(itemid);
        sp.Encode1(success);
        return sp.get();
    }

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

    // CMob::OnCatchEffect
    public static MaplePacket MobCatchEffect(MapleMonster monster, byte bSuccess) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_MobCatchEffect);

        sp.Encode4(monster.getObjectId());
        sp.Encode1(bSuccess);
        return sp.get();
    }
}
