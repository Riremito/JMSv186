/*
 * Copyright (C) 2025 Riremito
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

import java.util.ArrayList;
import odin.client.MapleCharacter;
import odin.client.inventory.MapleRing;
import tacos.config.Region;
import java.util.List;
import java.util.Map;
import tacos.packet.ServerPacket;
import tacos.packet.ops.arg.ArgUserEffect;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.data.RD_AvatarLook;
import tacos.packet.response.data.RD_CUser;
import tacos.client.TacosCharacter;
import tacos.config.Config;
import tacos.config.ContentCustom;
import tacos.constants.TacosConstants;
import tacos.packet.ServerPacketHeader;
import tacos.packet.ops.OpsSkill;
import tacos.packet.request.parse.ParseCUser_Attack;

/**
 *
 * @author Riremito
 */
public class ResCUserRemote {

    // CUserRemote::OnMove
    public static ServerPacket UserMove(MapleCharacter chr, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserMove);

        sp.Encode4(chr.getId());
        sp.EncodeBuffer(data.get());
        return sp;
    }

    // CUserRemote::OnAttack
    public static ServerPacket UserAttack(MapleCharacter chr, ParseCUser_Attack attack) {
        ServerPacket sp = new ServerPacket(attack.getHeader());
        boolean is_hide_damage = ContentCustom.CC_HIDE_DAMAGE.get();

        sp.Encode4(attack.CharacterId); // dwCharacterID
        sp.Encode1(is_hide_damage ? attack.HitKey & 0xF0 : attack.HitKey); // nDamagePerMob, & 0xF0 to hide damages.

        if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)) {
            sp.Encode1(attack.m_nLevel); // m_nLevel
        }

        sp.Encode1(attack.SkillLevel); // nSLV

        if (attack.SkillLevel != 0) {
            sp.Encode4(attack.nSkillID); // nSkillID
        }

        OpsSkill skill = OpsSkill.find(attack.nSkillID);
        if (Config.PostBB()) {
            if (skill == OpsSkill.SNIPER_STRAFE) {
                OpsSkill passive_skill = OpsSkill.CROSSBOWMASTER_ULTIMATE_STRAFE;
                int nPassiveSLV = chr.getSkillLevel(passive_skill);
                sp.Encode1(nPassiveSLV); // nPassiveSLV
                if (nPassiveSLV != 0) {
                    sp.Encode4(passive_skill.get()); // pPassiveSkill
                }
            }
        }

        sp.Encode1(attack.BuffKey); // bSerialAttack

        if (Config.LessOrEqual(Region.JMS, 147)) {
            sp.Encode1(attack.AttackActionKey);
        } else {
            sp.Encode2(attack.AttackActionKey);
        }

        sp.Encode1(attack.nAttackSpeed); // nActionSpeed
        sp.Encode1(attack.nMastery); // nMastery
        sp.Encode4(attack.nBulletItemID); // nBulletItemID

        for (Map.Entry<Integer, ArrayList<Integer>> entry : attack.damages.entrySet()) {
            sp.Encode4(entry.getKey()); // mob object id.
            sp.Encode1(7);
            if (is_hide_damage) {
                continue;
            }
            if (TacosConstants.is_mesp_explosion(attack.nSkillID)) {
                sp.Encode1(entry.getValue().size()); // hits
            }
            for (Integer damage : entry.getValue()) {
                if (Config.LessOrEqual(Region.JMS, 147) || Config.LessOrEqual(Region.KMST, 330)) {
                    sp.Encode4(damage); // damage
                } else {
                    sp.Encode1((damage & 0x80000000) != 0 ? 1 : 0); // critical.
                    sp.Encode4(damage & 0x7FFFFFFF);
                }
            }
        }

        if (TacosConstants.is_keydown_skill_remote(attack.nSkillID)) {
            sp.Encode4(attack.tKeyDown);
        }

        if (Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 65) || Config.GreaterOrEqual(Region.JMS, 164) || Config.GreaterOrEqual(Region.CMS, 73) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54)) {
            if (attack.getHeader() == ServerPacketHeader.LP_UserShootAttack) {
                sp.Encode2(attack.X);
                sp.Encode2(attack.Y);
            }
        }

        return sp;
    }

    // CUserRemote::OnSkillPrepare
    public static ServerPacket UserSkillPrepare(MapleCharacter chr, int skill_id, byte skill_level, short action, byte m_nPrepareSkillActionSpeed) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSkillPrepare);

        sp.Encode4(chr.getId());
        sp.Encode4(skill_id); // nSkillID
        sp.Encode1(skill_level); // skill level

        if (Config.GreaterOrEqual(Region.JMS, 186)) {
            sp.Encode2(action); // action (2 bytes)
        } else {
            sp.Encode1(action);
        }

        sp.Encode1(m_nPrepareSkillActionSpeed); // m_nPrepareSkillActionSpeed
        return sp;
    }

    // CUserRemote::OnSkillCancel
    public static ServerPacket UserSkillCancel(MapleCharacter chr, int nSkillID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSkillCancel);

        sp.Encode4(chr.getId());
        sp.Encode4(nSkillID);
        return sp;
    }

    public static class UserHitData {

        public int dwCharacterID = 0; // remote user id.
        public int nAttackIdx = 0;
        public int nDamage = 0; // real damage.
        public int dwTemplateID = 0; // attacker mob id.
        public int nLeft = 0;
        public int nReflect = 0;
        public int bPowerGuard = 0;
        public int m_dwMobID = 0; // damaged mob object id.
        public int nHitAction = 0;
        public int ptHit_x = 0;
        public int ptHit_y = 0;
        public int bGuard = 0;
        public int nDelta = 0; // damage number to show.
        public int nSkillID = 0; // fake skill id.
    }

    // CUserRemote::OnHit
    public static ServerPacket UserHit(UserHitData uhd) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserHit);

        sp.Encode4(uhd.dwCharacterID);
        sp.Encode1(uhd.nAttackIdx);
        sp.Encode4(uhd.nDamage); // internal damage

        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0); // critical
        }

        if (uhd.dwTemplateID != 0) {
            sp.Encode4(uhd.dwTemplateID); // dwTemplateID
            sp.Encode1(uhd.nLeft); // bLeft

            if (Config.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode4(0);
                sp.Encode4(0);
            }

            sp.Encode1(uhd.nReflect);
            if (uhd.nReflect != 0) {
                sp.Encode1(uhd.bPowerGuard);
                sp.Encode4(uhd.m_dwMobID);
                sp.Encode1(uhd.nHitAction);
                sp.Encode2(uhd.ptHit_x);
                sp.Encode2(uhd.ptHit_y);
            }

            sp.Encode1(uhd.bGuard); // bGuard
        }

        sp.Encode4(uhd.nDelta); // nDelta
        if (uhd.nDelta < 0) {
            sp.Encode4(uhd.nSkillID); // fake skill id.
        }

        return sp;
    }

    // CUser::OnEmotion
    public static ServerPacket UserEmotion(MapleCharacter chr, int expression) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEmotion);

        sp.Encode4(chr.getId()); // remote
        sp.EncodeBuffer(RD_CUser.Emotion(expression));
        return sp;
    }

    // CUser::SetActiveEffectItem
    public static ServerPacket UserSetActiveEffectItem(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSetActiveEffectItem);

        sp.Encode4(chr.getId());
        sp.Encode4(chr.getActiveEffectItem());
        return sp;
    }

    // CUserRemote::OnSetActivePortableChair
    public static ServerPacket UserSetActivePortableChair(int characterid, int itemid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSetActivePortableChair);

        sp.Encode4(characterid);
        sp.Encode4(itemid);

        if (Config.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode4(0);
        }

        return sp;
    }

    // CUserRemote::OnAvatarModified
    public static ServerPacket UserAvatarModified(TacosCharacter chr, int flag) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserAvatarModified);

        sp.Encode4(chr.getId());
        sp.Encode1(flag);

        if ((flag & 0x01) != 0) {
            sp.EncodeBuffer(RD_AvatarLook.Encode(chr));
        }
        if ((flag & 0x02) != 0) {
            sp.Encode1(0); // nSpeed_CS
        }
        if ((flag & 0x04) != 0) {
            sp.Encode1(0); // CarryItemEffect
        }
        sp.Encode1(0); // Couple -> data
        sp.Encode1(0); // Friendship -> data
        sp.Encode1(0); // Marriage -> data
        sp.Encode4(0); // m_nCompletedSetItemID
        return sp;
    }

    // CUser::OnEffect
    public static ServerPacket UserEffectRemote(ArgUserEffect arg) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(arg.chr.getId());
        sp.EncodeBuffer(ResCUserLocal.EffectData(arg));
        return sp;
    }

    // CUserRemote::OnSetTemporaryStat
    // CUserRemote::OnResetTemporaryStat
    // CUserRemote::OnReceiveHP
    public static ServerPacket UserHP(int cid, int curhp, int maxhp) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserHP);

        sp.Encode4(cid);
        sp.Encode4(curhp);
        sp.Encode4(maxhp);
        return sp;
    }

    public static ServerPacket showPetLevelUp(MapleCharacter chr, int index) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(chr.getId());
        sp.Encode1(4);
        sp.Encode1(0);
        sp.Encode4(index);
        return sp;
    }

    public static ServerPacket showRewardItemAnimation(int itemId, String effect, int from_playerid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(from_playerid);
        sp.Encode1(15);
        sp.Encode4(itemId);
        sp.Encode1(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            sp.EncodeStr(effect);
        }
        return sp;
    }

    //its likely that durability items use this
    public static ServerPacket showHpHealed(int cid, final int amount) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(cid);
        sp.Encode1(10); //Type
        sp.Encode4(amount);
        return sp;
    }

    public static ServerPacket ItemMakerResultTo(MapleCharacter chr, boolean is_success) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(chr.getId());
        sp.Encode1(17);
        sp.Encode4(is_success ? 0 : 1);
        return sp;
    }

    public static byte[] addRingInfo(List<MapleRing> rings) {
        ServerPacket data = new ServerPacket();

        data.Encode1(rings.size());
        for (MapleRing ring : rings) {
            data.Encode4(1);
            data.Encode8(ring.getRingId());
            data.Encode8(ring.getPartnerRingId());
            data.Encode4(ring.getItemId());
        }

        return data.getBytes();
    }
}
