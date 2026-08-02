/*
 * Copyright (C) 2026 Riremito
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

import odin.client.MapleCharacter;
import odin.client.MapleClient;
import tacos.config.Region;
import tacos.config.Version;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.ResCMobPool;
import odin.server.Randomizer;
import odin.server.life.MapleMonster;
import odin.server.life.MobSkill;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleNodes;
import tacos.odin.OdinPair;
import tacos.packet.ClientPacketHeader;
import tacos.packet.ops.OpsMobLeaveField;
import tacos.packet.response.wrapper.ResWrapper;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class ReqCMobPool {

    public static boolean OnPacket(MapleClient client, ClientPacketHeader header, ClientPacket cp) {
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        int m_dwMobID = cp.Decode4();

        MapleMonster monster = map.getMonsterByOid(m_dwMobID);
        if (monster == null) {
            return true;
        }

        switch (header) {
            case CP_MobMove: {
                OnMove(chr, cp, monster, map);
                return true;
            }
            case CP_MobApplyCtrl: {
                OnMobApplyCtrl(chr, cp, monster, map);
                return true;
            }
            case CP_MobDropPickUpRequest: {
                OnMobDropPickUpRequest(chr, cp, monster, map);
                return true;
            }
            case CP_MobHitByObstacle: {
                OnMobHitByObstacle(chr, cp, monster, map);
                return true;
            }
            case CP_MobHitByMob: {
                OnMobHitByMob(chr, cp, monster, map);
                return true;
            }
            case CP_MobSelfDestruct: {
                OnMobSelfDestruct(chr, cp, monster, map);
                return true;
            }
            case CP_MobAttackMob: {
                OnMobAttackMob(chr, cp, monster, map);
                return true;
            }
            case CP_MobSkillDelayEnd: {
                OnMobSkillDelayEnd(chr, cp, monster, map);
                return true;
            }
            case CP_MobTimeBombEnd: {
                OnMobTimeBombEnd(chr, cp, monster, map);
                return true;
            }
            case CP_MobEscortCollision: {
                OnMobEscortCollision(chr, cp, monster, map);
                return true;
            }
            case CP_MobRequestEscortInfo: {
                OnMobRequestEscortInfo(chr, cp, monster, map);
                return true;
            }
            case CP_MobEscortStopEndRequest: {
                OnMobEscortStopEndRequest(chr, cp, monster, map);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static boolean OnMove(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        byte unk1 = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode1() : 0;
        short moveid = cp.Decode2();
        boolean bNextAttackPossible = cp.Decode1() > 0;

        MobUsesSkill(chr, monster, moveid, bNextAttackPossible);
        byte bLeft = cp.Decode1();
        int mob_skill = cp.Decode4();

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            // none
            cp.Decode1();
            cp.Decode1();
        } else if (Version.GreaterOrEqual(Region.KMS, 95) || Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70)) {
            cp.Decode4(); // 0
            cp.Decode4(); // 0
        }

        byte unk2 = Version.LessOrEqual(Region.KMS, 31) ? 0 : cp.Decode1(); // 0
        int unk3 = Version.LessOrEqual(Region.KMS, 43) ? 1 : cp.Decode4(); // 1

        if (Version.GreaterOrEqual(Region.KMS, 95) || Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70) || Version.Equal(Region.BMS, 24)) {
            int ffddcc_1 = cp.Decode4(); // 0x00FFDDCC
            int ffddcc_2 = cp.Decode4(); // 0x00FFDDCC
            if (ffddcc_1 != 0x00FFDDCC || ffddcc_2 != 0x00FFDDCC) {
                DebugLogger.DebugLog("0x00FFDDCC... " + String.format("%08X", ffddcc_1) + " | " + String.format("%08X", ffddcc_2));
            }
        }
        if (Version.GreaterOrEqual(Region.KMS, 95) || Version.PostBB() || Version.GreaterOrEqual(Region.JMS, 186) || Version.GreaterOrEqual(Region.CMS, 85) || Version.GreaterOrEqual(Region.TWMS, 121) || Version.GreaterOrEqual(Region.THMS, 87) || Version.GreaterOrEqual(Region.GMS, 91) || Version.GreaterOrEqual(Region.MSEA, 100) || Version.GreaterOrEqual(Region.EMS, 70)) {
            cp.Decode4();
        }

        byte unk4 = Version.GreaterOrEqual(Region.JMS, 302) ? cp.Decode1() : 0;

        ParseCMovePath move_path = new ParseCMovePath();
        if (move_path.Decode(cp)) {
            move_path.update(monster);
        }

        map.broadcastMessageTo(chr, ResCMobPool.MobMove(monster, bNextAttackPossible, bLeft, mob_skill, move_path), monster.getPosition());
        return true;
    }

    public static void MobUsesSkill(MapleCharacter chr, MapleMonster monster, short moveid, boolean useSkill) {
        int realskill = 0;
        int level = 0;

        if (useSkill) {
            byte size = monster.getNoSkills();
            boolean used = false;

            if (size > 0) {
                OdinPair<Integer, Integer> skillToUse = monster.getSkills().get((byte) Randomizer.nextInt(size));
                realskill = skillToUse.getLeft();
                level = skillToUse.getRight();
                // Skill ID and Level
                MobSkill mobSkill = WzXML.SKILL.getMobSkillData(realskill, level);

                if (mobSkill != null) {
                    final long now = System.currentTimeMillis();
                    final long ls = monster.getLastSkillUsed(realskill);

                    if (ls == 0 || ((now - ls) > mobSkill.getCoolTime())) {
                        monster.setLastSkillUsed(realskill, now, mobSkill.getCoolTime());

                        int reqHp = (int) (((float) monster.getHp() / monster.getMobMaxHp()) * 100); // In case this monster have 2.1b and above HP
                        if (reqHp <= mobSkill.getHP()) {
                            used = true;
                            mobSkill.applyEffect(chr, monster, true);
                        }
                    }
                }
            }
            if (!used) {
                realskill = 0;
                level = 0;
            }
        }

        chr.SendPacket(ResCMobPool.MobCtrlAck(monster, moveid, realskill, level));
    }

    public static boolean OnMobApplyCtrl(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        if (monster.getController() == null || map.getCharacterById(monster.getController().getId()) == null) {
            monster.switchController(chr, true);
            return true;
        }
        return true;
    }

    public static boolean OnMobDropPickUpRequest(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        DebugLogger.ErrorLog("OnMobDropPickUpRequest : not coded.");
        return true;
    }

    public static boolean OnMobHitByObstacle(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        int nDamage = cp.Decode4();

        DebugLogger.ErrorLog("OnMobHitByObstacle : not coded.");
        return true;
    }

    /*
        タイラス護衛
        mod id : 9300093
     */
    public static boolean OnMobHitByMob(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        int m_dwCharacterId = cp.Decode4();
        int m_dwMobID = cp.Decode4();

        MapleMonster monster_to = map.getMonsterByOid(m_dwMobID);
        if (monster_to == null || !monster_to.getStats().isFriendly()) {
            DebugLogger.ErrorLog("OnMobHitByMob : err.");
            return false;
        }

        // TODO : fix damage.
        int damage = (int) (monster_to.getMobMaxHp() / 5);

        monster_to.setHp(Math.max(0, monster_to.getHp() - damage));
        map.broadcastMessage(ResCMobPool.MobDamaged(monster_to, damage, 1));

        if (monster_to.getHp() <= 0) {
            map.killMonster(monster_to, chr, false, false, OpsMobLeaveField.MOBLEAVEFIELD_ETC);
        }

        return true;
    }

    /*
        ダークスター等
        mob id : 8500004
     */
    public static boolean OnMobSelfDestruct(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        int nActionType = monster.getStats().getSelfD();
        if (nActionType == -1) {
            return false;
        }
        if ((nActionType & 1) != 0 && monster.getHp() <= monster.getStats().getSelfDHp()) {
            map.killMonster(monster, chr, false, false, OpsMobLeaveField.MOBLEAVEFIELD_SELFDESTRUCT);
            return true;
        }
        if ((nActionType & 2) != 0) {
            map.killMonster(monster, chr, false, false, OpsMobLeaveField.MOBLEAVEFIELD_SELFDESTRUCT);
            return true;
        }
        return true;
    }

    /*
        ホブ帝王の復活
        map id : 921120100
        mob id : 9300275
     */
    public static boolean OnMobAttackMob(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        int m_dwCharacterId = cp.Decode4();
        int m_dwMobID = cp.Decode4();

        MapleMonster monster_to = map.getMonsterByOid(m_dwMobID);
        if (monster_to == null || !monster_to.getStats().isFriendly()) {
            return true;
        }

        byte vx = cp.Decode1();
        int damage = cp.Decode4();
        byte vy = cp.Decode1();
        short x = cp.Decode2();
        short y = cp.Decode2();

        // shamos, 9300275
        DebugLogger.ErrorLog("OnMobAttackMob : not coded");
        return true;
    }

    public static boolean OnMobSkillDelayEnd(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        DebugLogger.ErrorLog("OnMobSkillDelayEnd : not coded");
        return true;
    }

    public static boolean OnMobTimeBombEnd(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        DebugLogger.ErrorLog("OnMobTimeBombEnd : not coded");
        return true;
    }

    public static boolean OnMobEscortCollision(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        int m_nCurrentDestIndex = cp.Decode4();

        DebugLogger.DebugLog("OnMobEscortCollision : ...");

        int nodeSize = map.getNodeInfo().getNodes().size();
        if (monster != null && nodeSize > 0 && nodeSize >= m_nCurrentDestIndex) {
            final MapleNodes.MapleNodeInfo mni = map.getNodeInfo().getNode(m_nCurrentDestIndex);
            if (mni == null) {
                return false;
            }
            if (mni.attr == 2) { //talk
                map.talkMonster("Please escort me carefully.", 5120035, monster); //temporary for now. itemID is located in WZ file
            }
            if (monster.getLastNode() >= m_nCurrentDestIndex) {
                return false;
            }
            monster.setLastNode(m_nCurrentDestIndex);
            if (nodeSize == m_nCurrentDestIndex) { //the last node on the map.
                int newMap = -1;
                switch (chr.getMapId() / 100) {
                    case 9211200:
                        newMap = 921120100;
                        break;
                    case 9211201:
                        newMap = 921120200;
                        break;
                    case 9211202:
                        newMap = 921120300;
                        break;
                    case 9211203:
                        newMap = 921120400;
                        break;
                    case 9211204:
                        map.removeMonster(monster);
                        break;

                }
                if (newMap > 0) {
                    map.broadcastMessage(ResWrapper.BroadCastMsgEvent("Proceed to the next stage."));
                    map.removeMonster(monster);
                }
            }
        }

        return true;
    }

    public static boolean OnMobRequestEscortInfo(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        DebugLogger.DebugLog("OnMobRequestEscortInfo : ...");
        chr.SendPacket(ResCMobPool.MobRequestResultEscortInfo(monster, map));
        return true;
    }

    public static boolean OnMobEscortStopEndRequest(MapleCharacter chr, ClientPacket cp, MapleMonster monster, MapleMap map) {
        DebugLogger.ErrorLog("OnMobEscortStopEndRequest : not coded");
        return true;
    }
}
