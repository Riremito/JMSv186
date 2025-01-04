// User
package packet.request;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.SkillFactory;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.DebugConfig;
import config.ServerConfig;
import constants.GameConstants;
import debug.Debug;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.PlayerHandler;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.ClientPacket;
import packet.request.struct.CMovePath;
import packet.response.ResCUserLocal;
import packet.response.ResCUserRemote;
import packet.response.ResCWvsContext;
import packet.response.UserResponse;
import server.Randomizer;
import server.maps.MapleMap;
import tools.AttackPair;
import tools.Pair;

public class ReqCUser {

    public static boolean OnPacket(ClientPacket cp, ClientPacket.Header header, MapleClient c) {
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
                OnMove(cp, map, chr);
                return true;
            }
            case CP_UserSitRequest: {
                OnSitRequest(cp, chr);
                return true;
            }
            case CP_UserPortableChairSitRequest: {
                OnPortableChairSitRequest(cp, chr);
                return true;
            }
            case CP_UserMeleeAttack: {
                AttackInfo attack = OnAttack(cp, header, chr);
                PlayerHandler.closeRangeAttack(c, attack, false);
                return true;
            }
            case CP_UserShootAttack: {
                AttackInfo attack = OnAttack(cp, header, chr);
                PlayerHandler.rangedAttack(c, attack);
                return true;
            }
            case CP_UserMagicAttack: {
                AttackInfo attack = OnAttack(cp, header, chr);
                PlayerHandler.MagicDamage(c, attack);
                return true;
            }
            case CP_UserBodyAttack: {
                AttackInfo attack = OnAttack(cp, header, chr);
                PlayerHandler.closeRangeAttack(c, attack, true);
                return true;
            }
            case CP_UserChangeStatRequest: {
                if (ServerConfig.IsJMS() && 131 < ServerConfig.GetVersion()) {
                    cp.Decode4(); // time1
                }

                int update_mask = cp.Decode4();

                if (update_mask != 0x1400) {
                    Debug.ErrorLog("Heal Flag includes other flag" + update_mask);
                    return false;
                }

                int heal_hp = cp.Decode2();
                int heal_mp = cp.Decode2();

                cp.Decode4(); // time2

                PlayerHandler.Heal(chr, heal_hp, heal_mp);
                return true;
            }

            // 被ダメージ
            case CP_UserHit: {
                PlayerHandler.TakeDamage(cp, c, c.getPlayer());
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
                PlayerHandler.SkillEffect(cp, c.getPlayer());
                return true;
            }

            case CP_UserCharacterInfoRequest: {
                //PlayerHandler.CharInfoRequest(character_id, c, c.getPlayer());
                OnCharacterInfoRequest(cp, chr, map);
                return true;
            }
            // AP使用
            case CP_UserAbilityUpRequest: {
                OnAbilityUpRequest(cp, chr);
                return true;
            }
            case CP_UserAbilityMassUpRequest: {
                OnAbilityMassUpRequest(cp, chr);
                return true;
            }
            // SP使用
            case CP_UserSkillUpRequest: {
                OnSkillUpRequest(cp, chr);
                return true;
            }
            // buff
            case CP_UserSkillCancelRequest: {
                OnSkillCanselRequest(cp, chr);
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
        chr.SendPacket(ResCWvsContext.StatChanged(chr, unlock, statmask));
    }

    // BMS CUser::OnAttack
    public static final AttackInfo OnAttack(ClientPacket cp, ClientPacket.Header header, MapleCharacter chr) {
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

        attack.FieldKey = cp.Decode1();

        // DR_Check
        if (ServerConfig.JMS180orLater()
                && !ServerConfig.IsKMS()) {
            cp.Decode4(); // pDrInfo.dr0
            cp.Decode4(); // pDrInfo.dr1
        }

        attack.HitKey = cp.Decode1(); // nDamagePerMob | (16 * nCount)

        // DR_Check
        if (ServerConfig.JMS180orLater()
                && !ServerConfig.IsKMS()) {
            cp.Decode4(); // pDrInfo.dr2
            cp.Decode4(); // pDrInfo.dr3
        }

        attack.nSkillID = cp.Decode4();
        attack.skill = attack.nSkillID; // old

        if (0 < attack.nSkillID) {
            // skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            attack.SkillLevel = chr.getSkillLevel(attack.nSkillID);
        }

        // v95 1 byte cd->nCombatOrders
        if (ServerConfig.JMS180orLater()
                && !ServerConfig.IsKMS()) {
            cp.Decode4(); // get_rand of DR_Check
            cp.Decode4(); // Crc32 of DR_Check
            // v95 4 bytes SKILLLEVELDATA::GetCrc
        }

        if (ServerConfig.IsPostBB()) {
            cp.Decode1();
        }

        if (ServerConfig.JMS164orLater()) {
            cp.Decode4(); // Crc
        }

        attack.tKeyDown = 0;
        if (attack.is_keydown_skill()) {
            attack.tKeyDown = cp.Decode4();
        }

        if (ServerConfig.JMS194orLater()) {
            if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
                cp.Decode1();
            }
        }

        attack.BuffKey = cp.Decode1();

        if (ServerConfig.JMS165orEarlier()) {
            attack.AttackActionKey = cp.Decode1();
        } else {
            attack.AttackActionKey = cp.Decode2(); // nAttackAction & 0x7FFF | (bLeft << 15)
        }

        if (ServerConfig.IsPostBB()) {
            cp.Decode4();
        }

        // v95 4 bytes crc
        attack.nAttackActionType = cp.Decode1();
        attack.nAttackSpeed = cp.Decode1();
        attack.tAttackTime = cp.Decode4();

        if (ServerConfig.JMS186orLater()
                || ServerConfig.KMS95orLater()) {
            cp.Decode4(); // dwID
        }

        if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
            attack.ProperBulletPosition = cp.Decode2();
            attack.pnCashItemPos = cp.Decode2();
            attack.nShootRange0a = cp.Decode1(); // nShootRange0a, GetShootRange0 func, is AOE or not, TT/ Avenger = 41, Showdown = 0

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
            return parseMesoExplosion(cp, attack);
        }

        for (int i = 0; i < attack.GetMobCount(); i++) {
            int nTargetID = cp.Decode4();

            // v131 to v186 OK
            cp.Decode1(); // v366->nHitAction
            cp.Decode1(); // v366->nForeAction & 0x7F | (v156 << 7)
            cp.Decode1(); // v366->nFrameIdx
            cp.Decode1(); // CalcDamageStatIndex & 0x7F | v158
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something
            cp.Decode2(); // Mob Something
            cp.Decode2(); // v366->tDelay

            allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();

            for (int j = 0; j < attack.GetDamagePerMob(); j++) {
                damage = cp.Decode4(); // 366->aDamage[i]

                allDamageNumbers.add(new Pair<Integer, Boolean>(Integer.valueOf(damage), false));
            }

            if (ServerConfig.JMS164orLater()) {
                cp.Decode4(); // CMob::GetCrc(v366->pMob)
            }

            attack.allDamage.add(new AttackPair(Integer.valueOf(nTargetID), allDamageNumbers));
        }

        if (!ServerConfig.JMS165orEarlier()) {
            if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
                cp.Decode4(); // v292->CUser::CLife::IVecCtrlOwner::vfptr->GetPos?
            }
        }
        // is_wildhunter_job
        // byte 2, m_ptBodyRelMove.y

        attack.position = new Point();
        attack.position.x = cp.Decode2();
        attack.position.y = cp.Decode2();

        if (DebugConfig.log_damage) {
            if (allDamageNumbers != null) {
                Debug.DebugLog(cp.GetOpcodeName() + ": damage = " + allDamageNumbers);
            }
        }

        return attack;
    }

    public static final AttackInfo parseMesoExplosion(ClientPacket cp, final AttackInfo ret) {
        //System.out.println(lea.toString(true));
        byte bullets;
        int damage;
        if (ret.GetDamagePerMob() == 0) {
            cp.Decode4();
            bullets = cp.Decode1();
            for (int j = 0; j < bullets; j++) {
                damage = cp.Decode4();

                if (DebugConfig.log_damage) {
                    Debug.DebugLog(cp.GetOpcodeName() + ": damage = " + damage);
                }
                ret.allDamage.add(new AttackPair(Integer.valueOf(damage), null));
                cp.Decode1();
            }
            cp.Decode2(); // 8F 02
            return ret;
        }

        int oid;
        List<Pair<Integer, Boolean>> allDamageNumbers;

        for (int i = 0; i < ret.GetMobCount(); i++) {
            oid = cp.Decode4();
            // ?
            cp.Decode4();
            cp.Decode4();
            cp.Decode4();
            bullets = cp.Decode1();
            allDamageNumbers = new ArrayList<Pair<Integer, Boolean>>();
            for (int j = 0; j < bullets; j++) {
                damage = cp.Decode4();

                if (DebugConfig.log_damage) {
                    Debug.DebugLog(cp.GetOpcodeName() + ": damage = " + damage);
                }
                allDamageNumbers.add(new Pair<Integer, Boolean>(Integer.valueOf(damage), false)); //m.e. never crits
            }

            if (!(ServerConfig.IsJMS() && ServerConfig.GetVersion() < 186)) {
                cp.Decode4(); // CRC of monster [Wz Editing]
            }

            ret.allDamage.add(new AttackPair(Integer.valueOf(oid), allDamageNumbers));
        }
        cp.Decode4();
        bullets = cp.Decode1();

        for (int j = 0; j < bullets; j++) {
            damage = cp.Decode4();

            if (DebugConfig.log_damage) {
                Debug.DebugLog(cp.GetOpcodeName() + ": damage = " + damage);
            }
            ret.allDamage.add(new AttackPair(Integer.valueOf(damage), null));
            cp.Decode2();
        }

        cp.Decode2(); // 8F 02/ 63 02
        return ret;
    }

    public static boolean OnMove(ClientPacket cp, MapleMap map, MapleCharacter chr) {
        if (chr.isHidden()) {
            return false;
        }

        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            cp.Decode4(); // -1
            cp.Decode4(); // -1
        }

        cp.Decode1(); // unk

        if ((ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            cp.Decode4(); // -1
            cp.Decode4(); // -1
            cp.Decode4();
            cp.Decode4();
        }

        if ((ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion())
                || ServerConfig.IsTWMS()
                || ServerConfig.IsCMS()) {
            cp.Decode4();
        }

        CMovePath data = CMovePath.Decode(cp);
        chr.setPosition(data.getEnd());
        chr.setStance(data.getAction());
        map.movePlayer(chr, chr.getPosition());
        map.broadcastMessage(chr, UserResponse.movePlayer(chr, data), false);
        return true;
    }

    public static boolean OnSitRequest(ClientPacket cp, MapleCharacter chr) {
        short map_chair_id = cp.Decode2();

        boolean is_cancel = (map_chair_id == -1);

        if (is_cancel) {
            // 釣り
            if (chr.getChair() == 3011000) {
                chr.cancelFishingTask();
            }
            chr.getMap().broadcastMessage(chr, ResCUserRemote.SetActivePortableChair(chr.getId(), 0), false);
        }

        chr.setChair(is_cancel ? 0 : map_chair_id);
        chr.SendPacket(ResCUserLocal.SitResult(map_chair_id));
        return true;
    }

    public static boolean OnPortableChairSitRequest(ClientPacket cp, MapleCharacter chr) {
        int item_id = cp.Decode4();

        IItem toUse = chr.getInventory(MapleInventoryType.SETUP).findById(item_id);
        if (toUse == null) {
            return false;
        }

        // 釣り
        if (item_id == 3011000) {
            int fishing_level = 0;
            for (IItem item : chr.getInventory(MapleInventoryType.CASH).list()) {
                if (fishing_level <= 1 && item.getItemId() == 5340000) {
                    fishing_level = 1;
                }
                if (item.getItemId() == 5340001) {
                    fishing_level = 2;
                    break;
                }
            }
            if (fishing_level > 0) {
                chr.startFishingTask(fishing_level == 2);
            }
        }

        chr.setChair(item_id);
        chr.getMap().broadcastMessage(chr, ResCUserRemote.SetActivePortableChair(chr.getId(), item_id), false);
        SendCharacterStat(chr); // ?_?
        return true;
    }

    // CUser::OnCharacterInfoRequest
    // CharInfoRequest
    public static final boolean OnCharacterInfoRequest(ClientPacket cp, MapleCharacter chr, MapleMap map) {
        // CCheatInspector::InspectExclRequestTime
        final int update_time = cp.Decode4();
        final int m_dwCharacterId = cp.Decode4();
        final MapleCharacter player = map.getCharacterById(m_dwCharacterId); // CUser::FindUser

        if (player == null || player.isClone()) {
            // CUser::SendCharacterStat
            SendCharacterStat(chr); // ea
            return false;
        }

        chr.SendPacket(UserResponse.CharacterInfo(player, chr.getId() == m_dwCharacterId));
        return true;
    }

    public static boolean OnAbilityUpRequest(ClientPacket cp, MapleCharacter chr) {

        int time_stamp = cp.Decode4();
        long flag = 0;

        if (ServerConfig.JMS302orLater()) {
            flag = cp.Decode8();
        } else {
            flag = cp.Decode4();
        }

        chr.updateTick(time_stamp);
        return OnAbilityUpRequestInternal(chr, flag);
    }

    public static boolean OnAbilityUpRequestInternal(MapleCharacter chr, long flag) {
        final PlayerStats stat = chr.getStat();
        final int job = chr.getJob();
        if (chr.getRemainingAp() > 0) {
            switch ((int) flag) { // need to fix
                case 64:
                    // Str
                    if (stat.getStr() >= 999) {
                        return false;
                    }
                    stat.setStr((short) (stat.getStr() + 1));
                    break;
                case 128:
                    // Dex
                    if (stat.getDex() >= 999) {
                        return false;
                    }
                    stat.setDex((short) (stat.getDex() + 1));
                    break;
                case 256:
                    // Int
                    if (stat.getInt() >= 999) {
                        return false;
                    }
                    stat.setInt((short) (stat.getInt() + 1));
                    break;
                case 512:
                    // Luk
                    if (stat.getLuk() >= 999) {
                        return false;
                    }
                    stat.setLuk((short) (stat.getLuk() + 1));
                    break;
                case 2048:
                    // HP
                    int maxhp = stat.getMaxHp();
                    if (chr.getHpApUsed() >= 10000 || maxhp >= 30000) {
                        return false;
                    }
                    if (job == 0) {
                        // Beginner
                        maxhp += Randomizer.rand(8, 12);
                    } else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212)) {
                        // Warrior
                        ISkill improvingMaxHP = SkillFactory.getSkill(1000001);
                        int improvingMaxHPLevel = chr.getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(20, 25);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getX();
                        }
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job))) {
                        // Magician
                        maxhp += Randomizer.rand(10, 20);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312)) {
                        // Bowman
                        maxhp += Randomizer.rand(16, 20);
                    } else if ((job >= 500 && job <= 522) || (job >= 3500 && job <= 3512)) {
                        // Pirate
                        ISkill improvingMaxHP = SkillFactory.getSkill(5100000);
                        int improvingMaxHPLevel = chr.getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(18, 22);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1500 && job <= 1512) {
                        // Pirate
                        ISkill improvingMaxHP = SkillFactory.getSkill(15100000);
                        int improvingMaxHPLevel = chr.getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(18, 22);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1100 && job <= 1112) {
                        // Soul Master
                        ISkill improvingMaxHP = SkillFactory.getSkill(11000000);
                        int improvingMaxHPLevel = chr.getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(36, 42);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1200 && job <= 1212) {
                        // Flame Wizard
                        maxhp += Randomizer.rand(15, 21);
                    } else if (job >= 2000 && job <= 2112) {
                        // Aran
                        maxhp += Randomizer.rand(40, 50);
                    } else {
                        // GameMaster
                        maxhp += Randomizer.rand(50, 100);
                    }
                    maxhp = (short) Math.min(30000, Math.abs(maxhp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxHp(maxhp);
                    break;
                case 8192:
                    // MP
                    int maxmp = stat.getMaxMp();
                    if (chr.getHpApUsed() >= 10000 || stat.getMaxMp() >= 30000) {
                        return false;
                    }
                    if (job == 0) {
                        // Beginner
                        maxmp += Randomizer.rand(6, 8);
                    } else if (job >= 100 && job <= 132) {
                        // Warrior
                        maxmp += Randomizer.rand(2, 4);
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3200 && job <= 3212)) {
                        // Magician
                        ISkill improvingMaxMP = SkillFactory.getSkill(2000001);
                        int improvingMaxMPLevel = chr.getSkillLevel(improvingMaxMP);
                        maxmp += Randomizer.rand(18, 20);
                        if (improvingMaxMPLevel >= 1) {
                            maxmp += improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2;
                        }
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 522) || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512)) {
                        // Bowman
                        maxmp += Randomizer.rand(10, 12);
                    } else if (job >= 1100 && job <= 1112) {
                        // Soul Master
                        maxmp += Randomizer.rand(6, 9);
                    } else if (job >= 1200 && job <= 1212) {
                        // Flame Wizard
                        ISkill improvingMaxMP = SkillFactory.getSkill(12000000);
                        int improvingMaxMPLevel = chr.getSkillLevel(improvingMaxMP);
                        maxmp += Randomizer.rand(18, 20);
                        if (improvingMaxMPLevel >= 1) {
                            maxmp += improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2;
                        }
                    } else if (job >= 2000 && job <= 2112) {
                        // Aran
                        maxmp += Randomizer.rand(6, 9);
                    } else {
                        // GameMaster
                        maxmp += Randomizer.rand(50, 100);
                    }
                    maxmp = (short) Math.min(30000, Math.abs(maxmp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxMp(maxmp);
                    break;
                default: {
                    chr.UpdateStat(true);
                    return false;
                }
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
        }
        chr.UpdateStat(true);

        return true;
    }

    public static boolean OnAbilityMassUpRequest(ClientPacket cp, MapleCharacter chr) {
        int time_stamp = cp.Decode4();
        int count = cp.Decode4(); // ループ数

        int PrimaryStat = 0;
        int amount = 0;
        int SecondaryStat = 0;
        int amount2 = 0;

        for (int i = 0; i < count; i++) {
            long stat = 0;

            if (ServerConfig.JMS302orLater()) {
                stat = cp.Decode8();
            } else {
                stat = cp.Decode4();
            }

            int point = cp.Decode4();

            // test
            if (i == 0) {
                PrimaryStat = (int) stat;
                amount = point;
            } else if (i == 1) {
                SecondaryStat = (int) stat;
                amount2 = point;
            }
        }

        if (amount < 0 || amount2 < 0) {
            return false;
        }

        chr.updateTick(time_stamp);

        final PlayerStats playerst = chr.getStat();
        if (chr.getRemainingAp() == amount + amount2) {
            switch (PrimaryStat) {
                case 64:
                    // Str
                    if (playerst.getStr() + amount > 999) {
                        return false;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount));
                    break;
                case 128:
                    // Dex
                    if (playerst.getDex() + amount > 999) {
                        return false;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount));
                    break;
                case 256:
                    // Int
                    if (playerst.getInt() + amount > 999) {
                        return false;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount));
                    break;
                case 512:
                    // Luk
                    if (playerst.getLuk() + amount > 999) {
                        return false;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount));
                    break;
                default:
                    chr.UpdateStat(true);
                    return false;
            }
            switch (SecondaryStat) {
                case 64:
                    // Str
                    if (playerst.getStr() + amount2 > 999) {
                        return false;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount2));
                    break;
                case 128:
                    // Dex
                    if (playerst.getDex() + amount2 > 999) {
                        return false;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount2));
                    break;
                case 256:
                    // Int
                    if (playerst.getInt() + amount2 > 999) {
                        return false;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount2));
                    break;
                case 512:
                    // Luk
                    if (playerst.getLuk() + amount2 > 999) {
                        return false;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount2));
                    break;
                default:
                    chr.UpdateStat(true);
                    return false;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            chr.UpdateStat(true);
        }

        return true;
    }

    public static boolean OnSkillUpRequest(ClientPacket cp, MapleCharacter chr) {
        int time_stamp = cp.Decode4();
        int skill_id = cp.Decode4();

        chr.updateTick(time_stamp);
        return OnSkillUpRequestInternal(chr, skill_id);
    }

    public static boolean OnSkillUpRequestInternal(MapleCharacter chr, int skill_id) {
        boolean isBeginnerSkill = false;
        final int remainingSp;
        chr.setLastSkillUp(skill_id);
        switch (skill_id) {
            case 1000:
            case 1001:
            case 1002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(1000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(1001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(1002));
                remainingSp = Math.min(chr.getLevel() - 1, 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 10001000:
            case 10001001:
            case 10001002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(10001000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(10001001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(10001002));
                remainingSp = Math.min(chr.getLevel() - 1, 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 20001000:
            case 20001001:
            case 20001002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(20001000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(20001001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(20001002));
                remainingSp = Math.min(chr.getLevel() - 1, 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 20011000:
            case 20011001:
            case 20011002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(20011000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(20011001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(20011002));
                remainingSp = Math.min(chr.getLevel() - 1, 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 30001000:
            case 30001001:
            case 30000002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(30001000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(30001001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(30000002));
                remainingSp = Math.min(chr.getLevel() - 1, 9) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true; //resist can max ALL THREE
                break;
            }
            default: {
                remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skill_id));
                break;
            }
        }
        final ISkill skill = SkillFactory.getSkill(skill_id);
        if (skill.hasRequiredSkill()) {
            if (chr.getSkillLevel(SkillFactory.getSkill(skill.getRequiredSkillId())) < skill.getRequiredSkillLevel()) {
                Debug.ErrorLog("Use SP 1 = " + skill_id);
                return false;
            }
        }
        final int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
        final int curLevel = chr.getSkillLevel(skill);
        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if ((skill.isFourthJob() && chr.getMasterLevel(skill) == 0) || (!skill.isFourthJob() && maxlevel < 10 && !isBeginnerSkill)) {
                Debug.ErrorLog("Use SP 2 = " + skill_id);
                return false;
            }
        }
        for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
                chr.dropMessage(1, "You may not add this skill.");
                Debug.ErrorLog("Use SP 3 = " + skill_id);
                return false;
            }
        }
        if ((remainingSp > 0 && curLevel + 1 <= maxlevel) && skill.canBeLearnedBy(chr.getJob())) {
            if (!isBeginnerSkill) {
                final int skillbook = GameConstants.getSkillBookForSkill(skill_id);
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - 1, skillbook);
            }
            chr.UpdateStat(false);
            chr.changeSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
            return true;
        }
        if ((remainingSp > 0 && curLevel + 1 <= maxlevel) && isBeginnerSkill) {
            chr.UpdateStat(false);
            chr.changeSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
            return true;
        }
        Debug.ErrorLog("Use SP 4 = " + skill_id);
        return false;
    }

    // CancelBuffHandler
    public static boolean OnSkillCanselRequest(ClientPacket cp, MapleCharacter chr) {
        int skill_id = cp.Decode4();
        ISkill skill = SkillFactory.getSkill(skill_id);

        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0);
            chr.getMap().broadcastMessage(chr, UserResponse.skillCancel(chr, skill_id), false);
        } else {
            chr.cancelEffect(skill.getEffect(1), false, -1);
        }

        return true;
    }

}
