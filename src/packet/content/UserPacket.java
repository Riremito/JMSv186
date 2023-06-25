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
import handling.channel.handler.MovementParse;
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
import tools.MaplePacketCreator;
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
                OnMove(p, map, chr);
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
            /*
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
            }*/
            case CP_UserMeleeAttack: {
                AttackInfo attack = OnAttack(p, header, chr);
                PlayerHandler.closeRangeAttack(c, attack, false);
                return true;
            }
            case CP_UserShootAttack: {
                AttackInfo attack = OnAttack(p, header, chr);
                PlayerHandler.rangedAttack(c, attack);
                return true;
            }
            case CP_UserMagicAttack: {
                AttackInfo attack = OnAttack(p, header, chr);
                PlayerHandler.MagicDamage(c, attack);
                return true;
            }
            case CP_UserBodyAttack: {
                AttackInfo attack = OnAttack(p, header, chr);
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

    // BMS CUser::OnAttack
    public static final AttackInfo OnAttack(ClientPacket p, ClientPacket.Header header, MapleCharacter chr) {
        final AttackInfo attack = new AttackInfo();

        // attack type
        attack.AttackHeader = header;
        // attacker data
        attack.CharacterId = chr.getId();
        attack.m_nLevel = chr.getLevel();
        attack.nSkillID = 0;
        attack.SkillLevel = 0;
        attack.nMastery = chr.getStat().passive_mastery();
        attack.nBulletItemID = 0;
        attack.X = chr.getPosition().x;
        attack.Y = chr.getPosition().y;

        attack.FieldKey = p.Decode1();

        // DR_Check
        if (186 <= ServerConfig.version) {
            p.Decode4(); // pDrInfo.dr0
            p.Decode4(); // pDrInfo.dr1
        }

        attack.HitKey = p.Decode1(); // nDamagePerMob | (16 * nCount)

        // DR_Check
        if (186 <= ServerConfig.version) {
            p.Decode4(); // pDrInfo.dr2
            p.Decode4(); // pDrInfo.dr3
        }

        attack.nSkillID = p.Decode4();
        attack.skill = attack.nSkillID; // old

        if (0 < attack.nSkillID) {
            // skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            attack.SkillLevel = chr.getSkillLevel(attack.nSkillID);
        }

        // v95 1 byte cd->nCombatOrders
        if (186 <= ServerConfig.version) {
            p.Decode4(); // get_rand of DR_Check
            p.Decode4(); // Crc32 of DR_Check
            p.Decode4(); // Crc
            // v95 4 bytes SKILLLEVELDATA::GetCrc
        }

        attack.tKeyDown = 0;
        if (attack.is_keydown_skill()) {
            attack.tKeyDown = p.Decode4();
        }

        attack.BuffKey = p.Decode1();

        if (ServerConfig.version <= 131) {
            attack.AttackActionKey = p.Decode1();
        } else {
            attack.AttackActionKey = p.Decode2(); // nAttackAction & 0x7FFF | (bLeft << 15)

        }

        // v95 4 bytes crc
        attack.nAttackActionType = p.Decode1();
        attack.nAttackSpeed = p.Decode1();
        attack.tAttackTime = p.Decode4();

        if (186 <= ServerConfig.version) {
            p.Decode4(); // dwID
        }

        if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
            attack.ProperBulletPosition = p.Decode2();
            attack.pnCashItemPos = p.Decode2();
            attack.nShootRange0a = p.Decode1(); // nShootRange0a, GetShootRange0 func, is AOE or not, TT/ Avenger = 41, Showdown = 0

            if (0 < attack.nShootRange0a && !attack.IsShadowMeso() && chr.getBuffedValue(MapleBuffStat.SOULARROW) == null) {
                IItem BulletItem;
                if (0 < attack.pnCashItemPos) {
                    BulletItem = chr.getInventory(MapleInventoryType.CASH).getItem(attack.pnCashItemPos);
                } else {
                    BulletItem = chr.getInventory(MapleInventoryType.USE).getItem(attack.ProperBulletPosition);
                }
                if (BulletItem != null) {
                    attack.nBulletItemID = BulletItem.getItemId();
                }
            }
        }

        int damage;
        List<Pair<Integer, Boolean>> allDamageNumbers;
        attack.allDamage = new ArrayList<AttackPair>();

        if (attack.IsMesoExplosion()) { // Meso Explosion
            return parseMesoExplosion(p, attack);
        }

        for (int i = 0; i < attack.GetMobCount(); i++) {
            int nTargetID = p.Decode4();

            // v131 to v186 OK
            p.Decode1(); // v366->nHitAction
            p.Decode1(); // v366->nForeAction & 0x7F | (v156 << 7)
            p.Decode1(); // v366->nFrameIdx
            p.Decode1(); // CalcDamageStatIndex & 0x7F | v158
            p.Decode2(); // Mob Something
            p.Decode2(); // Mob Something
            p.Decode2(); // Mob Something
            p.Decode2(); // Mob Something
            p.Decode2(); // v366->tDelay

            allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();

            for (int j = 0; j < attack.GetDamagePerMob(); j++) {
                damage = p.Decode4(); // 366->aDamage[i]

                allDamageNumbers.add(new Pair<Integer, Boolean>(Integer.valueOf(damage), false));
            }

            if (186 <= ServerConfig.version) {
                p.Decode4(); // CMob::GetCrc(v366->pMob)
            }

            attack.allDamage.add(new AttackPair(Integer.valueOf(nTargetID), allDamageNumbers));
        }

        if (186 <= ServerConfig.version) {
            if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
                p.Decode4(); // v292->CUser::CLife::IVecCtrlOwner::vfptr->GetPos?
            }
        }
        // is_wildhunter_job
        // byte 2, m_ptBodyRelMove.y

        attack.position = new Point();
        attack.position.x = p.Decode2();
        attack.position.y = p.Decode2();
        return attack;
    }

    public static final AttackInfo parseMesoExplosion(ClientPacket p, final AttackInfo ret) {
        //System.out.println(lea.toString(true));
        byte bullets;
        int damage;
        if (ret.GetDamagePerMob() == 0) {
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

        for (int i = 0; i < ret.GetMobCount(); i++) {
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

    public static boolean OnMove(ClientPacket p, MapleMap map, MapleCharacter chr) {
        final Point Original_Pos = new Point();

        if (ServerConfig.version >= 186) {
            p.Decode4(); // -1
            p.Decode4(); // -1
        }

        p.Decode1(); // unk

        if (ServerConfig.version >= 186) {
            p.Decode4(); // -1
            p.Decode4(); // -1
            p.Decode4();
            p.Decode4();
            p.Decode4();
        }

        // v131 = start xy, v186 = updated xy
        Original_Pos.x = p.Decode2(); // start y
        Original_Pos.y = p.Decode2(); // start y

        if (ServerConfig.version >= 186) {
            Original_Pos.x = chr.getPosition().x;
            Original_Pos.y = chr.getPosition().y;
        }

        if (ServerConfig.version >= 186) {
            p.Decode2();
            p.Decode2();
        }

        List<LifeMovementFragment> res = null;

        try {
            // player OK
            res = MobPacket.parseMovement(p, 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            Debug.ErrorLog("AIOBE Type1");
            return false;
        }

        if (res == null) {
            Debug.ErrorLog("AIOBE Type1 res == null");
            return false;
        }

        // update char position
        if (chr.isHidden()) {
            chr.setLastRes(res); // crap
            map.broadcastGMMessage(chr, UserPacket.movePlayer(chr.getId(), res, Original_Pos), false);
        } else {
            map.broadcastMessage(chr, UserPacket.movePlayer(chr.getId(), res, Original_Pos), false);
        }

        MovementParse.updatePosition(res, chr, 0);
        final Point pos = chr.getPosition();
        map.movePlayer(chr, pos);
        if (chr.getFollowId() > 0 && chr.isFollowOn() && chr.isFollowInitiator()) {
            final MapleCharacter fol = map.getCharacterById(chr.getFollowId());
            if (fol != null) {
                final Point original_pos = fol.getPosition();
                fol.getClient().getSession().write(MaplePacketCreator.moveFollow(Original_Pos, original_pos, pos, res));
                MovementParse.updatePosition(res, fol, 0);
                map.broadcastMessage(fol, UserPacket.movePlayer(fol.getId(), res, original_pos), false);
            } else {
                chr.checkFollow();
            }
        }

        /*
        WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = clones[i].get();
                final List<LifeMovementFragment> res3 = new ArrayList<LifeMovementFragment>(res2);
                CloneTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        try {
                            if (clone.getMap() == map) {
                                if (clone.isHidden()) {
                                    clone.setLastRes(res3);
                                } else {
                                    map.broadcastMessage(clone, UserPacket.movePlayer(clone.getId(), res3, Original_Pos), false);
                                }
                                MovementParse.updatePosition(res3, clone, 0);
                                map.movePlayer(clone, pos);
                            }
                        } catch (Exception e) {
                            //very rarely swallowed
                        }
                    }
                }, 500 * i + 500);
            }
        }
         */
        // Fall Down Floor
        int count = chr.getFallCounter();
        if (map.getFootholds().findBelow(chr.getPosition()) == null && chr.getPosition().y > chr.getOldPosition().y && chr.getPosition().x == chr.getOldPosition().x) {
            if (count > 10) {
                chr.changeMap(map, map.getPortal(0));
                chr.setFallCounter(0);
            } else {
                chr.setFallCounter(++count);
            }
        } else if (count > 0) {
            chr.setFallCounter(0);
        }
        chr.setOldPosition(new Point(chr.getPosition()));

        return true;
    }

}
