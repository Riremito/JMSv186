// User
package packet.client.request;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.DebugConfig;
import config.ServerConfig;
import debug.Debug;
import handling.channel.handler.AttackInfo;
import handling.channel.handler.PlayerHandler;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.client.ClientPacket;
import packet.client.request.struct.CMovePath;
import packet.server.response.ContextResponse;
import packet.server.response.UserResponse;
import server.maps.MapleMap;
import tools.AttackPair;
import tools.Pair;

public class UserRequest {

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
                //PlayerHandler.CancelChair(p.readShort(), c, c.getPlayer());
                return true;
            }
            case CP_UserPortableChairSitRequest: {
                //PlayerHandler.UseChair(p.readInt(), c, c.getPlayer());
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
        chr.SendPacket(ContextResponse.StatChanged(chr, unlock, statmask));
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
        if (ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion()) {
            cp.Decode4(); // pDrInfo.dr0
            cp.Decode4(); // pDrInfo.dr1
        }

        attack.HitKey = cp.Decode1(); // nDamagePerMob | (16 * nCount)

        // DR_Check
        if (ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion()) {
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
        if (ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion()) {
            cp.Decode4(); // get_rand of DR_Check
            cp.Decode4(); // Crc32 of DR_Check
            if (ServerConfig.IsPostBB()) {
                cp.Decode1();
            }
            // v95 4 bytes SKILLLEVELDATA::GetCrc
        }

        if (ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion() || ServerConfig.IsKMS()) {
            cp.Decode4(); // Crc
        }

        attack.tKeyDown = 0;
        if (attack.is_keydown_skill()) {
            attack.tKeyDown = cp.Decode4();
        }

        if (ServerConfig.IsPostBB() && ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
            if (attack.AttackHeader == ClientPacket.Header.CP_UserShootAttack) {
                cp.Decode1();
            }
        }

        attack.BuffKey = cp.Decode1();

        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 165) {
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

        if (ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion() || ServerConfig.IsKMS()) {
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

            if (ServerConfig.IsJMS() && 164 <= ServerConfig.GetVersion() || ServerConfig.IsKMS()) {
                cp.Decode4(); // CMob::GetCrc(v366->pMob)
            }

            attack.allDamage.add(new AttackPair(Integer.valueOf(nTargetID), allDamageNumbers));
        }

        if (ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion() || ServerConfig.IsKMS()) {
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

            if (ServerConfig.version >= 186) {
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
