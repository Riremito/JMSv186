// Mob
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import config.ServerConfig;
import handling.channel.handler.MobHandler;
import packet.ClientPacket;
import packet.request.struct.CMovePath;
import packet.response.ResCMobPool;
import server.Randomizer;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import tools.Pair;

public class ReqCMobPool {

    public static boolean OnPacket(ClientPacket cp, ClientPacket.Header header, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return true;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return true;
        }

        int oid = cp.Decode4();

        MapleMonster monster = map.getMonsterByOid(oid);

        if (monster == null) {
            return true;
        }

        switch (header) {
            case CP_MobMove: {
                OnMove(cp, chr, monster);
                return true;
            }
            case CP_MobApplyCtrl: {
                MobHandler.AutoAggro(chr, monster);
                return true;
            }
            case CP_MobDropPickUpRequest: {
                return true;
            }
            case CP_MobHitByObstacle: {
                return true;
            }
            case CP_MobHitByMob: {
                cp.Decode4(); // skip
                int oid_to = cp.Decode4();

                MapleMonster monster_to = map.getMonsterByOid(oid_to);

                if (monster_to == null) {
                    return false;
                }

                MobHandler.FriendlyDamage(chr, monster, monster_to);
                return true;
            }
            case CP_MobSelfDestruct: {
                MobHandler.MonsterBomb(chr, monster);
                return true;
            }
            case CP_MobAttackMob: {
                cp.Decode4();
                int oid_to = cp.Decode4();

                MapleMonster monster_to = map.getMonsterByOid(oid_to);

                if (monster_to == null) {
                    return false;
                }

                cp.Decode1();

                int damage = cp.Decode4();

                MobHandler.HypnotizeDmg(chr, monster, monster_to, damage);
                return true;
            }
            case CP_MobSkillDelayEnd: {
                return true;
            }
            case CP_MobTimeBombEnd: {
                return true;
            }
            case CP_MobEscortCollision: {
                int newNode = cp.Decode4();
                MobHandler.MobNode(chr, monster, newNode);
                return true;
            }
            case CP_MobRequestEscortInfo: {
                MobHandler.DisplayNode(chr, monster);
                return true;
            }
            case CP_MobEscortStopEndRequest: {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // MoveMonster
    public static boolean OnMove(ClientPacket cp, MapleCharacter chr, MapleMonster monster) {
        final short moveid = cp.Decode2();
        final boolean useSkill = cp.Decode1() > 0;

        MobUsesSkill(chr, monster, moveid, useSkill);

        final byte skill = cp.Decode1();
        // 1st decode4
        final int skill1 = cp.Decode1() & 0xFF;
        final int skill2 = cp.Decode1();
        final int skill3 = cp.Decode1();
        final int skill4 = cp.Decode1();

        if (ServerConfig.JMS186orLater() || ServerConfig.IsKMS()) {
            cp.Decode4(); // 0
            cp.Decode4(); // 0
        }

        cp.Decode1();

        if (ServerConfig.JMS164orLater()) {
            cp.Decode4();
        }

        if (ServerConfig.JMS186orLater() || ServerConfig.IsKMS()) {
            cp.Decode4();
            cp.Decode4();
            cp.Decode4();
        }

        CMovePath data = CMovePath.Decode(cp);
        monster.setStance(data.getAction());
        monster.setPosition(data.getEnd());
        monster.setFh(data.getFootHoldId());

        final MapleMap map = chr.getMap();
        map.moveMonster(monster, monster.getPosition());
        map.broadcastMessage(chr, ResCMobPool.moveMonster(useSkill, skill, skill1, skill2, skill3, skill4, monster.getObjectId(), data), monster.getPosition());
        return true;
    }

    public static void MobUsesSkill(MapleCharacter chr, MapleMonster monster, short moveid, boolean useSkill) {
        int realskill = 0;
        int level = 0;

        if (useSkill) {// && (skill == -1 || skill == 0)) {
            final byte size = monster.getNoSkills();
            boolean used = false;

            if (size > 0) {
                final Pair<Integer, Integer> skillToUse = monster.getSkills().get((byte) Randomizer.nextInt(size));
                realskill = skillToUse.getLeft();
                level = skillToUse.getRight();
                // Skill ID and Level
                final MobSkill mobSkill = MobSkillFactory.getMobSkill(realskill, level);

                if (mobSkill != null && !mobSkill.checkCurrentBuff(chr, monster)) {
                    final long now = System.currentTimeMillis();
                    final long ls = monster.getLastSkillUsed(realskill);

                    if (ls == 0 || ((now - ls) > mobSkill.getCoolTime())) {
                        monster.setLastSkillUsed(realskill, now, mobSkill.getCoolTime());

                        final int reqHp = (int) (((float) monster.getHp() / monster.getMobMaxHp()) * 100); // In case this monster have 2.1b and above HP
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

        chr.getClient().SendPacket(ResCMobPool.moveMonsterResponse(monster, moveid, realskill, level));
    }

}
