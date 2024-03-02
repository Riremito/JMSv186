// User
package packet.content;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
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
import handling.world.guild.MapleGuildAlliance;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.client.ClientPacket;
import packet.server.ServerPacket;
import packet.Structure;
import packet.struct.AvatarLook;
import server.MapleItemInformationProvider;
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

            case CP_UserCharacterInfoRequest: {
                //PlayerHandler.CharInfoRequest(character_id, c, c.getPlayer());
                OnCharacterInfoRequest(p, chr, map);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    // enableActions
    public static void SendCharacterStat(MapleCharacter chr) {
        SendCharacterStat(chr, 1, 0);
    }

    // CUser::SendCharacterStat(1,0)
    // CWvsContext::OnStatChanged
    public static void SendCharacterStat(MapleCharacter chr, int unlock, int statmask) {
        chr.SendPacket(ContextPacket.StatChanged(chr, unlock, statmask));
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
            if (188 <= ServerConfig.version) {
                p.Decode1();
            }
            // v95 4 bytes SKILLLEVELDATA::GetCrc
        }

        if (164 <= ServerConfig.version) {
            p.Decode4(); // Crc
        }

        attack.tKeyDown = 0;
        if (attack.is_keydown_skill()) {
            attack.tKeyDown = p.Decode4();
        }

        if (194 <= ServerConfig.version) {
            if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
                p.Decode1();
            }
        }

        attack.BuffKey = p.Decode1();

        if (ServerConfig.version <= 165) {
            attack.AttackActionKey = p.Decode1();
        } else {
            attack.AttackActionKey = p.Decode2(); // nAttackAction & 0x7FFF | (bLeft << 15)
        }

        if (188 <= ServerConfig.version) {
            p.Decode4();
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
        List<Pair<Integer, Boolean>> allDamageNumbers = null;
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

            if (164 <= ServerConfig.version) {
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

        if (DebugConfig.log_damage) {
            if (allDamageNumbers != null) {
                Debug.DebugLog(p.GetOpcodeName() + ": damage = " + allDamageNumbers);
            }
        }

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
    // CUserPool::OnUserEnterField
    public static MaplePacket spawnPlayerMapobject(MapleCharacter chr) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_UserEnterField);
        p.Encode4(chr.getId());
        // 自分のキャラクターの場合はここで終了

        if (131 < ServerConfig.GetVersion()) {
            p.Encode1(chr.getLevel());
        }

        p.EncodeStr(chr.getName());

        if (194 <= ServerConfig.GetVersion()) {
            p.EncodeStr("");
        }

        // guild
        MapleGuild gs = null;
        if (0 < chr.getGuildId()) {
            gs = World.Guild.getGuild(chr.getGuildId());
        }
        if (gs != null) {
            // guild info
            p.EncodeStr(gs.getName());
            p.Encode2(gs.getLogoBG());
            p.Encode1(gs.getLogoBGColor());
            p.Encode2(gs.getLogo());
            p.Encode1(gs.getLogoColor());
        } else {
            // empty guild
            p.EncodeStr("");
            p.Encode2(0);
            p.Encode1(0);
            p.Encode2(0);
            p.Encode1(0);
        }

        List<Pair<Integer, Boolean>> buffvalue = new ArrayList<Pair<Integer, Boolean>>();
        if (131 < ServerConfig.GetVersion()) {
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

        if (131 < ServerConfig.GetVersion()) {
            // buffmask
            if (194 <= ServerConfig.GetVersion()) {
                p.Encode4(0);
            }

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

            p.Encode1(0); //start of energy charge
            p.Encode1(0);

            if (ServerConfig.GetVersion() <= 186) {
                p.Encode4(0);
                p.Encode4(0);
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
            }

            p.Encode2(chr.getJob());
        }

        p.EncodeBuffer(AvatarLook.Encode(chr));

        p.Encode4(0);//this is CHARID to follow

        if (131 < ServerConfig.GetVersion()) {
            p.Encode4(0); //probably charid following
            p.Encode4(0);

            if (194 <= ServerConfig.GetVersion()) {
                p.Encode4(0);
                p.Encode4(0);
                p.Encode4(0);
            }

            p.Encode4(0);
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
        // MiniRoomBalloon (ゲーム) 1 byte flag + data
        p.EncodeBuffer(Structure.AnnounceBox(chr));
        // ADBoardBalloon (黒板) 1 byte flag + data
        {
            p.Encode1(chr.getChalkboard() != null && chr.getChalkboard().length() > 0 ? 1 : 0);
            if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
                p.EncodeStr(chr.getChalkboard());
            }
        }
        p.Encode1(0);//count4 -> buf0x10 4
        p.Encode1(0);//count4 -> buf0x10 4
        // MarriageRecord 1 byte flag + data
        {
            p.Encode1(0);
        }
        p.Encode1(chr.getEffectMask()); // Effect
        p.Encode4(0); // not in KMST, in GMS v95: m_nPhase

        // 特殊マップ専用
        // MonsterCarnival
        if (chr.checkSpecificMap(980000000, 1000) || chr.checkSpecificMap(980030000, 1000)) {
            p.Encode1((chr.getCarnivalParty() != null) ? chr.getCarnivalParty().getTeam() : 0); // sub_5CD27E
        } // Coconut
        else if (chr.checkSpecificMap(109080000, 1000)) {
            p.Encode1(chr.getCoconutTeam()); // 0059F0ED
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
        p.Encode2((short) startPos.x);
        p.Encode2((short) startPos.y);

        if (131 < ServerConfig.version) {
            p.Encode4(0);
        }

        p.EncodeBuffer(MovementPacket.serializeMovementList(moves)); // to do move class
        return p.Get();
    }

    public static boolean OnMove(ClientPacket p, MapleMap map, MapleCharacter chr) {
        final Point Original_Pos = new Point();

        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Decode4(); // -1
            p.Decode4(); // -1
        }

        p.Decode1(); // unk

        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Decode4(); // -1
            p.Decode4(); // -1
            p.Decode4();
            p.Decode4();
        }

        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Decode4();
        }

        // v131 = start xy, v186 = updated xy
        Original_Pos.x = (int) p.Decode2(); // start y
        Original_Pos.y = (int) p.Decode2(); // start y

        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            Original_Pos.x = chr.getPosition().x;
            Original_Pos.y = chr.getPosition().y;
        }

        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            p.Decode2();
            p.Decode2();
        }

        List<LifeMovementFragment> res = null;

        try {
            // player OK
            res = MovementPacket.CMovePath_Decode(p, 1);
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

    // CUser::OnCharacterInfoRequest
    // CharInfoRequest
    public static final boolean OnCharacterInfoRequest(ClientPacket p, MapleCharacter chr, MapleMap map) {
        // CCheatInspector::InspectExclRequestTime
        final int update_time = p.Decode4();
        final int m_dwCharacterId = p.Decode4();
        final MapleCharacter player = map.getCharacterById(m_dwCharacterId); // CUser::FindUser

        if (player == null || player.isClone()) {
            // CUser::SendCharacterStat
            SendCharacterStat(chr); // ea
            return false;
        }

        chr.SendPacket(CharacterInfo(player, chr.getId() == m_dwCharacterId));
        return true;
    }

    public static final MaplePacket CharacterInfo(MapleCharacter player, boolean isSelf) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_CharacterInfo);

        boolean pet_summoned = false;
        for (final MaplePet pet : player.getPets()) {
            if (pet.getSummoned()) {
                pet_summoned = true;
                break;
            }
        }

        p.Encode4(player.getId());
        p.Encode1(player.getLevel());
        p.Encode2(player.getJob());
        p.Encode2(player.getFame());

        if (131 < ServerConfig.GetVersion()) {
            p.Encode1(player.getMarriageId() > 0 ? 1 : 0); // heart red or gray
        }

        String sCommunity = "-";
        String sAlliance = "";

        // Guild
        if (player.getGuildId() <= 0) {
            MapleGuild guild = World.Guild.getGuild(player.getGuildId());
            if (guild != null) {
                sCommunity = guild.getName();

                // Alliance
                if (guild.getAllianceId() > 0) {
                    MapleGuildAlliance alliance = World.Alliance.getAlliance(guild.getAllianceId());

                    if (alliance != null) {
                        sAlliance = alliance.getName();
                    }
                }
            }
        }

        p.EncodeStr(sCommunity);

        if (131 < ServerConfig.GetVersion()) {
            p.EncodeStr(sAlliance);

            if (ServerConfig.GetVersion() <= 186) {
                p.Encode4(0);
                p.Encode4(0);
                p.Encode1(isSelf ? 1 : 0);
                p.Encode1(pet_summoned ? 1 : 0);
            } else {
                p.Encode1(pet_summoned ? 1 : 0); // v188+ not used
                p.Encode1(isSelf ? 1 : 0);
            }
        }

        // CUIUserInfo::SetMultiPetInfo
        if (188 <= ServerConfig.GetVersion()) {
            p.Encode1(pet_summoned ? 1 : 0); // pet info on
        }

        IItem inv_pet = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -114);
        final int peteqid = inv_pet != null ? inv_pet.getItemId() : 0;
        int pet_count = 0;
        for (final MaplePet pet : player.getPets()) {
            if (pet.getSummoned()) {

                if (0 < pet_count) {
                    p.Encode1(1); // Next Pet
                }

                p.Encode4(pet.getPetItemId()); // petid

                if (194 <= ServerConfig.GetVersion()) {
                    p.Encode4(0);
                }

                p.EncodeStr(pet.getName());
                p.Encode1(pet.getLevel()); // nLevel
                p.Encode2(pet.getCloseness()); // pet closeness
                p.Encode1(pet.getFullness()); // pet fullness
                p.Encode2(0); // pet.getFlags()
                p.Encode4(peteqid);
                pet_count++;
            }
        }

        if (0 < pet_count || ServerConfig.GetVersion() <= 131) {
            p.Encode1(0); // End of pet
        }

        // CUIUserInfo::SetTamingMobInfo
        IItem inv_mount = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
        boolean TamingMobEnabled = false;
        final MapleMount tm = player.getMount();

        if (tm != null && inv_mount != null) {
            TamingMobEnabled = MapleItemInformationProvider.getInstance().getReqLevel(inv_mount.getItemId()) <= player.getLevel();
        }

        p.Encode1(TamingMobEnabled ? 1 : 0);
        if (tm != null && TamingMobEnabled) {
            p.Encode4(tm.getLevel());
            p.Encode4(tm.getExp());
            p.Encode4(tm.getFatigue());
        }

        // CUIUserInfo::SetWishItemInfo
        final int wishlistSize = player.getWishlistSize();
        p.Encode1(wishlistSize);
        if (wishlistSize > 0) {
            // CInPacket::DecodeBuffer(v4, iPacket, 4 * wishlistSize);
            final int[] wishlist = player.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                p.Encode4(wishlist[x]);
            }
        }

        if (131 < ServerConfig.GetVersion()) {
            // Monster Book (JMS)
            p.EncodeBuffer(player.getMonsterBook().MonsterBookInfo(player.getMonsterBookCover()));

            // MedalAchievementInfo::Decode
            IItem inv_medal = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -46);
            p.Encode4(inv_medal == null ? 0 : inv_medal.getItemId());
            List<Integer> medalQuests = new ArrayList<Integer>();
            List<MapleQuestStatus> completed = player.getCompletedQuests();
            for (MapleQuestStatus q : completed) {
                if (q.getQuest().getMedalItem() > 0 && GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP) { //chair kind medal viewmedal is weird
                    medalQuests.add(q.getQuest().getId());
                }
            }
            p.Encode2(medalQuests.size());
            for (int x : medalQuests) {
                p.Encode2(x);
            }

            if (ServerConfig.GetVersion() <= 186) {
                // Chair List
                p.Encode4(player.getInventory(MapleInventoryType.SETUP).list().size());
                // CInPacket::DecodeBuffer(v4, iPacket, 4 * chairs);
                for (IItem chair : player.getInventory(MapleInventoryType.SETUP).list()) {
                    p.Encode4(chair.getItemId());
                }
            }
        }
        return p.Get();
    }
}
