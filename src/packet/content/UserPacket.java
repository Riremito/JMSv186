// User
package packet.content;

import client.MapleCharacter;
import client.MapleClient;
import config.DebugConfig;
import config.ServerConfig;
import debug.Debug;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.PlayerHandler;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.ClientPacket;
import server.maps.MapleMap;
import tools.AttackPair;
import tools.Pair;

public class UserPacket {

    public static boolean OnPacket(ClientPacket p, ClientPacket.Header header, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleMap map = chr.getMap();
        if (map == null) {
            return false;
        }

        switch (header) {
            // Map移動処理
            case CP_UserTransferFieldRequest:
            case CP_UserTransferChannelRequest:
            case CP_UserMigrateToCashShopRequest: {
                return true;
            }
            case CP_UserMove: {
                return true;
            }
            case CP_UserSitRequest: {
                //PlayerHandler.CancelChair(p.readShort(), c, c.getPlayer());
                return true;
            }
            case CP_UserPortableChairSitRequest: {
                //PlayerHandler.UseChair(p.readInt(), c, c.getPlayer());
                return true;
            }
            // 攻撃処理
            case CP_UserMeleeAttack:
            case CP_UserShootAttack: {
                return true;
            }
            case CP_UserMagicAttack: {
                //PlayerHandler.closeRangeAttack(p, c, c.getPlayer(), false);
                //PlayerHandler.rangedAttack(p, c, c.getPlayer());
                //PlayerHandler.MagicDamage(p, c, c.getPlayer());
                AttackInfo attack = parseDmgMa(p);

                // AttackInfo attack = DamageParse.Modify_AttackCrit(parseDmgMa(p), chr, 3);
                PlayerHandler.MagicDamage(c, attack);
                return true;
            }
            case CP_UserChangeStatRequest: {
                if (ServerConfig.version > 131) {
                    p.Decode4(); // time1
                }

                int update_mask = p.Decode4();

                if (update_mask != 0x1400) {
                    Debug.ErrorLog("Heal Flag includes other flag" + update_mask);
                    return false;
                }

                int heal_hp = p.Decode2();
                int heal_mp = p.Decode2();

                p.Decode4(); // time2

                PlayerHandler.Heal(chr, heal_hp, heal_mp);
                return true;
            }

            // 被ダメージ
            case CP_UserHit: {
                //PlayerHandler.TakeDamage(p, c, c.getPlayer());
                return true;
            }
            // チャット
            case CP_UserChat: {
                return true;
            }
            // 表情
            case CP_UserEmotion: {
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static final AttackInfo parseDmgMa(ClientPacket p) {
        final AttackInfo ret = new AttackInfo();

        p.Decode1(); // skip
        if (ServerConfig.version >= 186) {
            p.Decode4(); // -1
            p.Decode4(); // -1
        }

        ret.tbyte = p.Decode1();
        ret.targets = (byte) ((ret.tbyte >>> 4) & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);

        if (ServerConfig.version >= 186) {
            p.Decode4(); // -1
            p.Decode4(); // -1
        }

        ret.skill = p.Decode4(); // SkillID

        if (DebugConfig.log_damage) {
            Debug.DebugLog(p.GetOpcodeName() + ": Skill = " + ret.skill);
        }

        if (ServerConfig.version >= 186) {
            p.Decode4();
            p.Decode4();
            p.Decode4();
        }

        //p.Decode1(); // 0
        // ?
        switch (ret.skill) {
            case 2121001: // Big Bang
            case 2221001:
            case 2321001:
            case 22121000: //breath
            case 22151001:
                ret.charge = p.Decode4();
                break;
            default:
                ret.charge = -1;
                break;
        }

        ret.unk = p.Decode1(); // OK

        if (ServerConfig.version <= 131) {
            ret.display = p.Decode1(); // ?
        } else {
            // v186
            ret.display = (byte) (p.Decode2() & 0xFF); // high = weapon sub class?
        }

        ret.animation = p.Decode1(); // OK
        ret.speed = p.Decode1(); // OK
        ret.lastAttackTickCount = p.Decode4(); // OK

        if (ServerConfig.version >= 186) {
            p.Decode4(); // 0
        }

        int oid, damage;
        List<Pair<Integer, Boolean>> allDamageNumbers;
        ret.allDamage = new ArrayList<AttackPair>();

        for (int i = 0; i < ret.targets; i++) {
            oid = p.Decode4(); // mob object id

            // v131 to v186 OK
            p.Decode1();
            p.Decode1();
            p.Decode1();
            p.Decode1();
            p.Decode2();
            p.Decode2();
            p.Decode2();
            p.Decode2();
            p.Decode2();

            allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();

            for (int j = 0; j < ret.hits; j++) {
                damage = p.Decode4();

                if (DebugConfig.log_damage) {
                    Debug.DebugLog(p.GetOpcodeName() + ": damage = " + damage);
                }

                allDamageNumbers.add(new Pair<Integer, Boolean>(Integer.valueOf(damage), false));
            }

            if (ServerConfig.version >= 186) {
                p.Decode4(); // CRC of monster [Wz Editing]
            }

            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers));
        }

        ret.position = new Point();
        ret.position.x = p.Decode2();
        ret.position.y = p.Decode2();

        if (ServerConfig.version >= 186) {
            p.Decode1();
        }

        return ret;
    }
}
