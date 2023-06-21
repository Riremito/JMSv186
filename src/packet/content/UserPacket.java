// User
package packet.content;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.DebugConfig;
import config.ServerConfig;
import constants.GameConstants;
import debug.Debug;
import handling.MaplePacket;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.PlayerHandler;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.ClientPacket;
import packet.ServerPacket;
import packet.Structure;
import server.Randomizer;
import server.maps.MapleMap;
import server.movement.LifeMovementFragment;
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
            case CP_UserMeleeAttack: {
                AttackInfo attack = parseDmgM(p);
                PlayerHandler.closeRangeAttack(c, attack, false);
                return true;
            }
            case CP_UserShootAttack: {
                AttackInfo attack = parseDmgR(p);
                PlayerHandler.rangedAttack(c, attack);
                return true;
            }
            case CP_UserMagicAttack: {
                AttackInfo attack = parseDmgMa(p);

                PlayerHandler.MagicDamage(c, attack);
                return true;
            }
            case CP_UserBodyAttack: {
                AttackInfo attack = parseDmgM(p);
                PlayerHandler.closeRangeAttack(c, attack, true);
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
                PlayerHandler.TakeDamage(p, c, c.getPlayer());
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
            case CP_UserSkillPrepareRequest: {
                PlayerHandler.SkillEffect(p, c.getPlayer());
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public static final AttackInfo parseDmgM(ClientPacket p) {
        //System.out.println(lea.toString());
        final AttackInfo ret = new AttackInfo();

        p.Decode1(); // skip
        if (ServerConfig.version >= 186) {
            p.Decode4(); // -1
            p.Decode4(); // -1
        }

        ret.tbyte = p.Decode1();
        //System.out.println("TBYTE: " + tbyte);
        ret.targets = (byte) ((ret.tbyte >>> 4) & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);

        if (ServerConfig.version >= 186) {
            p.Decode4(); // -1
            p.Decode4(); // -1
        }

        ret.skill = p.Decode4();

        if (DebugConfig.log_damage) {
            Debug.DebugLog(p.GetOpcodeName() + ": Skill = " + ret.skill);
        }

        if (ServerConfig.version >= 186) {
            p.Decode4();
            p.Decode4();
            p.Decode4();
        }

        switch (ret.skill) {
            case 5101004: // Corkscrew
            case 15101003: // Cygnus corkscrew
            case 5201002: // Gernard
            case 14111006: // Poison bomb
            case 4341002:
            case 4341003:
                ret.charge = p.Decode4();
                break;
            default:
                ret.charge = 0;
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

        ret.allDamage = new ArrayList<AttackPair>();

        if (ret.skill == 4211006) { // Meso Explosion
            return parseMesoExplosion(p, ret);
        }
        int oid, damage;
        List<Pair<Integer, Boolean>> allDamageNumbers;

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
        return ret;
    }

    public static final AttackInfo parseDmgR(ClientPacket p) {
        //System.out.println(lea.toString()); //<-- packet needs revision
        final AttackInfo ret = new AttackInfo();

        p.Decode1(); // skip
        if (ServerConfig.version >= 186) {
            p.Decode4(); // -1
            p.Decode4(); // -1
        }

        ret.tbyte = p.Decode1();
        //System.out.println("TBYTE: " + tbyte);
        ret.targets = (byte) ((ret.tbyte >>> 4) & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);

        if (ServerConfig.version >= 186) {
            p.Decode4(); // -1
            p.Decode4(); // -1
        }

        ret.skill = p.Decode4();

        if (DebugConfig.log_damage) {
            Debug.DebugLog(p.GetOpcodeName() + ": Skill = " + ret.skill);
        }

        if (ServerConfig.version >= 186) {
            p.Decode4();
            p.Decode4();
            p.Decode4();
        }

        switch (ret.skill) {
            case 3121004: // Hurricane
            case 3221001: // Pierce
            case 5221004: // Rapidfire
            case 13111002: // Cygnus Hurricane
            case 33121009:
                ret.charge = p.Decode4();
                break;
        }
        ret.charge = -1;

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

        ret.slot = (byte) p.Decode2();
        ret.csstar = (byte) p.Decode2();
        ret.AOE = p.Decode1(); // is AOE or not, TT/ Avenger = 41, Showdown = 0

        int damage, oid;
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

        if (ServerConfig.version >= 186) {
            p.Decode4(); // ?
        }

        ret.position = new Point();
        ret.position.x = p.Decode2();
        ret.position.y = p.Decode2();
        return ret;
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
        return ret;
    }

    public static final AttackInfo parseMesoExplosion(ClientPacket p, final AttackInfo ret) {
        //System.out.println(lea.toString(true));
        byte bullets;
        int damage;
        if (ret.hits == 0) {
            p.Decode4();
            bullets = p.Decode1();
            for (int j = 0; j < bullets; j++) {
                damage = p.Decode4();

                if (DebugConfig.log_damage) {
                    Debug.DebugLog(p.GetOpcodeName() + ": damage = " + damage);
                }
                ret.allDamage.add(new AttackPair(Integer.valueOf(damage), null));
                p.Decode1();
            }
            p.Decode2(); // 8F 02
            return ret;
        }

        int oid;
        List<Pair<Integer, Boolean>> allDamageNumbers;

        for (int i = 0; i < ret.targets; i++) {
            oid = p.Decode4();
            // ?
            p.Decode4();
            p.Decode4();
            p.Decode4();
            bullets = p.Decode1();
            allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < bullets; j++) {
                damage = p.Decode4();

                if (DebugConfig.log_damage) {
                    Debug.DebugLog(p.GetOpcodeName() + ": damage = " + damage);
                }
                allDamageNumbers.add(new Pair<Integer, Boolean>(Integer.valueOf(damage), false)); //m.e. never crits
            }

            if (ServerConfig.version >= 186) {
                p.Decode4(); // CRC of monster [Wz Editing]
            }

            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers));
        }
        p.Decode4();
        bullets = p.Decode1();

        for (int j = 0; j < bullets; j++) {
            damage = p.Decode4();

            if (DebugConfig.log_damage) {
                Debug.DebugLog(p.GetOpcodeName() + ": damage = " + damage);
            }
            ret.allDamage.add(new AttackPair(Integer.valueOf(damage), null));
            p.Decode2();
        }

        p.Decode2(); // 8F 02/ 63 02
        return ret;
    }

    // spawnPlayerMapobject
    public static MaplePacket spawnPlayerMapobject(MapleCharacter chr) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_UserEnterField);
        p.Encode4(chr.getId());
        // 自分のキャラクターの場合はここで終了

        if (ServerConfig.version > 131) {
            p.Encode1(chr.getLevel());
        }

        p.EncodeStr(chr.getName());

        if (chr.getGuildId() <= 0) {
            if (ServerConfig.version <= 131) {
                p.EncodeStr("");
                p.Encode2(0);
                p.Encode1(0);
                p.Encode2(0);
                p.Encode1(0);
            } else {
                p.Encode4(0);
                p.Encode4(0);
            }
        } else {
            final MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                p.EncodeStr(gs.getName());
                p.Encode2(gs.getLogoBG());
                p.Encode1(gs.getLogoBGColor());
                p.Encode2(gs.getLogo());
                p.Encode1(gs.getLogoColor());
            } else {
                p.Encode4(0);
                p.Encode4(0);
            }
        }

        List<Pair<Integer, Boolean>> buffvalue = new ArrayList<Pair<Integer, Boolean>>();
        if (ServerConfig.version > 131) {
            long fbuffmask = 0xFE0000L;
            if (chr.getBuffedValue(MapleBuffStat.SOARING) != null) {
                fbuffmask |= MapleBuffStat.SOARING.getValue();
            }
            if (chr.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) != null) {
                fbuffmask |= MapleBuffStat.MIRROR_IMAGE.getValue();
            }
            if (chr.getBuffedValue(MapleBuffStat.DARK_AURA) != null) {
                fbuffmask |= MapleBuffStat.DARK_AURA.getValue();
            }
            if (chr.getBuffedValue(MapleBuffStat.BLUE_AURA) != null) {
                fbuffmask |= MapleBuffStat.BLUE_AURA.getValue();
            }
            if (chr.getBuffedValue(MapleBuffStat.YELLOW_AURA) != null) {
                fbuffmask |= MapleBuffStat.YELLOW_AURA.getValue();
            }

            p.Encode8(fbuffmask);
        }

        long buffmask = 0;

        if (chr.getBuffedValue(MapleBuffStat.DARKSIGHT) != null && !chr.isHidden()) {
            buffmask |= MapleBuffStat.DARKSIGHT.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.COMBO) != null) {
            buffmask |= MapleBuffStat.COMBO.getValue();
            buffvalue.add(new Pair<Integer, Boolean>(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.COMBO).intValue()), false));
        }
        if (chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) {
            buffmask |= MapleBuffStat.SHADOWPARTNER.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.SOULARROW) != null) {
            buffmask |= MapleBuffStat.SOULARROW.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.DIVINE_BODY) != null) {
            buffmask |= MapleBuffStat.DIVINE_BODY.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.BERSERK_FURY) != null) {
            buffmask |= MapleBuffStat.BERSERK_FURY.getValue();
        }
        if (chr.getBuffedValue(MapleBuffStat.MORPH) != null) {
            buffmask |= MapleBuffStat.MORPH.getValue();
            buffvalue.add(new Pair<Integer, Boolean>(Integer.valueOf(chr.getBuffedValue(MapleBuffStat.MORPH).intValue()), true));
        }

        p.Encode8(buffmask);

        if (ServerConfig.version > 131) {
            for (Pair<Integer, Boolean> i : buffvalue) {
                if (i.right) {
                    p.Encode2(i.left.shortValue());
                } else {
                    p.Encode1(i.left.byteValue());
                }
            }
            final int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
            //CHAR_MAGIC_SPAWN is really just tickCount
            //this is here as it explains the 7 "dummy" buffstats which are placed into every character
            //these 7 buffstats are placed because they have irregular packet structure.
            //they ALL have writeShort(0); first, then a long as their variables, then server tick count
            //0x80000, 0x100000, 0x200000, 0x400000, 0x800000, 0x1000000, 0x2000000

            p.Encode2(0); //start of energy charge
            p.Encode8(0);
            p.Encode1(1);
            p.Encode4(CHAR_MAGIC_SPAWN);
            p.Encode2(0); //start of dash_speed
            p.Encode8(0);
            p.Encode1(1);
            p.Encode4(CHAR_MAGIC_SPAWN);
            p.Encode2(0); //start of dash_jump
            p.Encode8(0);
            p.Encode1(1);
            p.Encode4(CHAR_MAGIC_SPAWN);
            p.Encode2(0); //start of Monster Riding
            int buffSrc = chr.getBuffSource(MapleBuffStat.MONSTER_RIDING);
            if (buffSrc > 0) {
                final IItem c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118/*-122*/);
                final IItem mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18/*-22*/);
                if (GameConstants.getMountItem(buffSrc) == 0 && c_mount != null) {
                    p.Encode4(c_mount.getItemId());
                } else if (GameConstants.getMountItem(buffSrc) == 0 && mount != null) {
                    p.Encode4(mount.getItemId());
                } else {
                    p.Encode4(GameConstants.getMountItem(buffSrc));
                }
                p.Encode4(buffSrc);
            } else {
                p.Encode8(0);
            }
            p.Encode1(1);
            p.Encode4(CHAR_MAGIC_SPAWN);
            p.Encode8(0); //speed infusion behaves differently here
            p.Encode1(1);
            p.Encode4(CHAR_MAGIC_SPAWN);
            p.Encode4(1);
            p.Encode8(0); //homing beacon
            p.Encode1(0);
            p.Encode2(0);
            p.Encode1(1);
            p.Encode4(CHAR_MAGIC_SPAWN);
            p.Encode4(0); //and finally, something ive no idea
            p.Encode8(0);
            p.Encode1(1);
            p.Encode4(CHAR_MAGIC_SPAWN);
            p.Encode2(0);
            p.Encode2(chr.getJob());
        }

        Structure.CharLook(p, chr, false); // to do buffer

        p.Encode4(0);//this is CHARID to follow

        if (ServerConfig.version > 131) {
            p.Encode4(0); //probably charid following
            p.Encode8(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000))); //max is like 100. but w/e
        }

        p.Encode4(chr.getItemEffect());
        p.Encode4(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        p.Encode2(chr.getPosition().x);
        p.Encode2(chr.getPosition().y);
        p.Encode1(chr.getStance());
        p.Encode2(0); // FH
        p.Encode1(0); // pet size
        p.Encode4(chr.getMount().getLevel()); // mount lvl
        p.Encode4(chr.getMount().getExp()); // exp
        p.Encode4(chr.getMount().getFatigue()); // tiredness
        p.EncodeBuffer(Structure.AnnounceBox(chr));
        p.Encode1(chr.getChalkboard() != null && chr.getChalkboard().length() > 0 ? 1 : 0);
        if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
            p.EncodeStr(chr.getChalkboard());
        }

        // v131ここまでOK, ここからおかしい
        p.EncodeBuffer(Structure.addRingInfo(chr)); // byte x3?
        // Berserk?

        p.Encode2(0);
        p.Encode4(0);
        if (chr.getCarnivalParty() != null) {
            p.Encode1(chr.getCarnivalParty().getTeam());
        } else if (chr.getMapId() == 109080000) {
            p.Encode1(chr.getCoconutTeam()); //is it 0/1 or is it 1/2?
        }
        return p.Get();
    }

    // removePlayerFromMap
    public static MaplePacket removePlayerFromMap(int player_id) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_UserLeaveField);

        p.Encode4(player_id);
        return p.Get();
    }

    // movePlayer
    public static MaplePacket movePlayer(int player_id, List<LifeMovementFragment> moves, Point startPos) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_UserMove);

        p.Encode4(player_id);
        p.Encode2(startPos.x);
        p.Encode2(startPos.y);

        if (ServerConfig.version > 131) {
            p.Encode4(0);
        }

        p.EncodeBuffer(MobPacket.serializeMovementList(moves)); // to do move class
        return p.Get();
    }

}
