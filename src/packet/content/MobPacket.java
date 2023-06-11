// Mob
package packet.content;

import client.MapleCharacter;
import client.MapleClient;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import config.ServerConfig;
import handling.MaplePacket;
import handling.channel.handler.MobHandler;
import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import packet.ClientPacket;
import packet.ServerPacket;
import packet.Structure;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.maps.MapleMap;
import server.movement.LifeMovementFragment;

public class MobPacket {

    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        int oid = p.Decode4();

        MapleMonster monster = map.getMonsterByOid(oid);

        if (monster == null) {
            return false;
        }

        switch (header) {
            case CP_MobMove: {
                //MobHandler.MoveMonster(p, c, c.getPlayer());
                return true;
            }
            case CP_MobApplyCtrl: {
                MobHandler.AutoAggro(oid, chr);
                return true;
            }
            case CP_MobDropPickUpRequest: {
                return true;
            }
            case CP_MobHitByObstacle: {
                return true;
            }
            case CP_MobHitByMob: {
                //MobHandler.FriendlyDamage(p, chr);
                return true;
            }
            case CP_MobSelfDestruct: {
                MobHandler.MonsterBomb(oid, chr);
                return true;
            }
            case CP_MobAttackMob: {
                //MobHandler.HypnotizeDmg(p, chr);
                return true;
            }
            case CP_MobSkillDelayEnd: {
                return true;
            }
            case CP_MobTimeBombEnd: {
                return true;
            }
            case CP_MobEscortCollision: {
                //MobHandler.MobNode(p, chr);
                return true;
            }
            case CP_MobRequestEscortInfo: {
                //MobHandler.DisplayNode(p, chr);
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

    // spawnMonster
    public static MaplePacket Spawn(MapleMonster life, int spawnType, int effect, int link) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobEnterField);

        p.Encode4(life.getObjectId());
        p.Encode1(1); // 1 = Control normal, 5 = Control none
        p.Encode4(life.getId());
        if (ServerConfig.version <= 164) {
            p.Encode4(0); // 後でなおす
        } else {
            p.EncodeBuffer(Structure.MonsterStatus(life));
        }
        p.Encode2(life.getPosition().x);
        p.Encode2(life.getPosition().y);
        p.Encode1(life.getStance());
        p.Encode2(0); // FH
        p.Encode2(life.getFh()); // Origin FH

        if (effect != 0 || link != 0) {
            p.Encode1(effect != 0 ? effect : -3);
            p.Encode4(link);
        } else {
            if (spawnType == 0) {
                p.Encode4(effect);
            }
            p.Encode1(spawnType); // newSpawn ? -2 : -1
            //0xFB when wh spawns
        }
        p.Encode1(life.getCarnivalTeam());

        if (ServerConfig.version > 131) {
            p.Encode4(0);
        }

        if (ServerConfig.version > 164) {
            p.Encode4(0);
        }

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

    // controlMonster
    public static MaplePacket Control(MapleMonster life, boolean newSpawn, boolean aggro) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobChangeController);

        p.Encode1(aggro ? 2 : 1);
        p.Encode4(life.getObjectId());
        p.Encode1(1); // 1 = Control normal, 5 = Control none
        p.Encode4(life.getId());

        if (ServerConfig.version <= 164) {
            p.Encode4(0); // 後でなおす
        } else {
            p.EncodeBuffer(Structure.MonsterStatus(life));
        }

        p.Encode2(life.getPosition().x);
        p.Encode2(life.getPosition().y);
        p.Encode1(life.getStance()); // Bitfield
        p.Encode2(0); // FH
        p.Encode2(life.getFh()); // Origin FH
        p.Encode1(life.isFake() ? -4 : newSpawn ? -2 : -1);
        p.Encode1(life.getCarnivalTeam());

        if (ServerConfig.version > 131) {
            p.Encode4(0);
            p.Encode4(0);
        }

        return p.Get();
    }

    // stopControllingMonster
    public static MaplePacket StopControl(MapleMonster m) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobChangeController);

        p.Encode1(0);
        p.Encode4(m.getObjectId());
        return p.Get();
    }

    // showMonsterHP
    public static MaplePacket ShowHP(MapleMonster m, int remhppercentage) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobHPIndicator);

        p.Encode4(m.getObjectId());
        p.Encode1(remhppercentage);

        return p.Get();
    }

    // showBossHP
    public static MaplePacket ShowBossHP(MapleMonster m) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_FieldEffect);

        p.Encode1(5);
        p.Encode4(m.getId());

        if (m.getHp() > Integer.MAX_VALUE) {
            p.Encode4((int) (((double) m.getHp() / m.getMobMaxHp()) * Integer.MAX_VALUE));
        } else {
            p.Encode4((int) m.getHp());
        }

        if (m.getMobMaxHp() > Integer.MAX_VALUE) {
            p.Encode4(Integer.MAX_VALUE);
        } else {
            p.Encode4((int) m.getMobMaxHp());
        }

        p.Encode1(m.getStats().getTagColor());
        p.Encode1(m.getStats().getTagBgColor());
        return p.Get();
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

    // healMonster
    public static MaplePacket Heal(MapleMonster m, final int heal) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobDamaged);

        p.Encode4(m.getObjectId());
        p.Encode1(0);
        p.Encode4(-heal);
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

    public static MaplePacket cancelMonsterStatus(int oid, MonsterStatus stat) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobStatReset);

        p.Encode4(oid);
        p.Encode8(Structure.getSpecialLongMask(Collections.singletonList(stat)));
        p.Encode8(Structure.getLongMask(Collections.singletonList(stat)));
        p.Encode1(1); // reflector is 3~!??
        p.Encode1(2); // ? v97

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

    // moveMonster
    public static MaplePacket moveMonster(boolean useskill, int skill, int skill1, int skill2, int skill3, int skill4, int oid, Point startPos, Point endPos, List<LifeMovementFragment> moves) {

        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobMove);

        p.Encode4(oid);
        p.Encode2(0); //moveid but always 0
        p.Encode1(useskill ? 1 : 0); //?? I THINK
        p.Encode1(skill);
        p.Encode1(skill1);
        p.Encode1(skill2);
        p.Encode1(skill3);
        p.Encode1(skill4);
        p.EncodeZeroBytes(8); //o.o?
        p.Encode2(startPos.x);
        p.Encode2(startPos.y);
        p.Encode2(8); //? sometimes 0? sometimes 22? sometimes random numbers?
        p.Encode2(1);
        p.EncodeBuffer(serializeMovementList(moves));

        return p.Get();
    }

    private static byte[] serializeMovementList(List<LifeMovementFragment> moves) {
        ServerPacket data = new ServerPacket();
        data.Encode1(moves.size());

        for (LifeMovementFragment move : moves) {
            move.serialize(data);
        }

        return data.Get().getBytes();
    }

    // moveMonsterResponse
    public static MaplePacket moveMonsterResponse(MapleMonster m, short moveid, int skillId, int skillLevel) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_MobCtrlAck);

        p.Encode4(m.getObjectId());
        p.Encode2(moveid);
        p.Encode1(m.isControllerHasAggro() ? 1 : 0);
        p.Encode2(m.getMp());
        p.Encode1(skillId);
        p.Encode1(skillLevel);

        return p.Get();
    }
}
