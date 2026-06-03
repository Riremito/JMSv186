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

import odin.client.MapleBuffStat;
import odin.client.MapleCharacter;
import odin.client.MapleDisease;
import odin.client.inventory.MapleRing;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import tacos.network.MaplePacket;
import odin.handling.channel.handler.AttackInfo;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ops.arg.ArgUserEffect;
import tacos.packet.request.parse.ParseCMovePath;
import tacos.packet.response.data.DataAvatarLook;
import tacos.packet.response.data.DataCUser;
import odin.server.MapleStatEffect;
import odin.tools.AttackPair;
import tacos.client.TacosCharacter;
import tacos.config.ContentCustom;
import tacos.constants.TacosConstants;
import tacos.odin.OdinPair;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCUserRemote {

    // CUserRemote::OnMove
    public static MaplePacket UserMove(MapleCharacter chr, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserMove);

        sp.Encode4(chr.getId());
        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    // CUserRemote::OnAttack
    public static MaplePacket UserAttack(AttackInfo attack) {
        ServerPacket sp = new ServerPacket(attack.getHeader());
        boolean is_hide_damage = ContentCustom.CC_HIDE_DAMAGE.get();

        if (Version.LessOrEqual(Region.JMS, 147)) {
            sp.Encode4(attack.CharacterId);
            sp.Encode1(is_hide_damage ? attack.HitKey & 0xF0 : attack.HitKey); // & 0xF0 to hide damages.
            sp.Encode1(attack.SkillLevel); // nPassiveSLV
            if (0 < attack.nSkillID) {
                sp.Encode4(attack.nSkillID); // nSkillID
            }
            sp.Encode1(attack.BuffKey); // bSerialAttack
            sp.Encode1(attack.AttackActionKey);
            sp.Encode1(attack.nAttackActionType);
            sp.Encode1(attack.nAttackSpeed); // nActionSpeed
            sp.Encode4(attack.nBulletItemID); // nBulletItemID
            for (AttackPair oned : attack.allDamage) {
                if (oned.attack != null) {
                    sp.Encode4(oned.objectid);
                    sp.Encode1(7);
                    if (is_hide_damage) {
                        continue;
                    }
                    if (TacosConstants.is_mesp_explosion(attack.nSkillID)) {
                        sp.Encode1(oned.attack.size());
                    }
                    for (OdinPair<Integer, Boolean> eachd : oned.attack) {
                        sp.Encode4(eachd.getLeft() | ((eachd.getRight() ? 1 : 0) << 31));
                    }
                }
            }
            if (TacosConstants.is_keydown_skill_remote(attack.nSkillID)) {
                sp.Encode4(attack.tKeyDown);
            }
            return sp.get();
        }

        sp.Encode4(attack.CharacterId);
        sp.Encode1(attack.HitKey);
        if (ServerConfig.JMS164orLater()) {
            sp.Encode1(attack.m_nLevel);
        }
        sp.Encode1(attack.SkillLevel); // nPassiveSLV
        if (0 < attack.nSkillID) {
            sp.Encode4(attack.nSkillID); // nSkillID
        }
        if (ServerConfig.JMS164orLater()) {
            sp.Encode1(attack.BuffKey); // bSerialAttack
        }
        if (Version.LessOrEqual(Region.JMS, 147)) {
            sp.Encode1(attack.AttackActionKey);
        } else {
            sp.Encode2(attack.AttackActionKey);
        }
        sp.Encode1(attack.nAttackSpeed); // nActionSpeed
        sp.Encode1(attack.nMastery); // nMastery
        sp.Encode4(attack.nBulletItemID); // nBulletItemID
        for (AttackPair oned : attack.allDamage) {
            if (oned.attack != null) {
                sp.Encode4(oned.objectid);
                sp.Encode1(7);
                if (TacosConstants.is_mesp_explosion(attack.nSkillID)) {
                    sp.Encode1(oned.attack.size());
                }
                for (OdinPair<Integer, Boolean> eachd : oned.attack) {
                    if (Version.LessOrEqual(Region.JMS, 131) || Version.Equal(Region.KMST, 330)) {
                        sp.Encode4(eachd.getLeft() | ((eachd.getRight() ? 1 : 0) << 31));
                    } else {
                        sp.Encode1(eachd.getRight() ? 1 : 0);
                        sp.Encode4(eachd.getLeft());
                    }
                }
            }
        }
        if (TacosConstants.is_keydown_skill_remote(attack.nSkillID)) {
            sp.Encode4(attack.tKeyDown);
        }
        if (ServerConfig.JMS164orLater()) {
            if (attack.getHeader() == ServerPacketHeader.LP_UserShootAttack) {
                sp.Encode2(attack.X);
                sp.Encode2(attack.Y);
            }
        }
        return sp.get();
    }

    // CUserRemote::OnSkillPrepare
    public static MaplePacket UserSkillPrepare(MapleCharacter chr, int skill_id, byte skill_level, short action, byte m_nPrepareSkillActionSpeed) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSkillPrepare);

        sp.Encode4(chr.getId());
        sp.Encode4(skill_id); // nSkillID
        sp.Encode1(skill_level); // skill level

        if (Version.GreaterOrEqual(Region.JMS, 186)) {
            sp.Encode2(action); // action (2 bytes)
        } else {
            sp.Encode1(action);
        }

        sp.Encode1(m_nPrepareSkillActionSpeed); // m_nPrepareSkillActionSpeed
        return sp.get();
    }

    // CUserRemote::OnSkillCancel
    public static MaplePacket UserSkillCancel(MapleCharacter chr, int nSkillID) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSkillCancel);

        sp.Encode4(chr.getId());
        sp.Encode4(nSkillID);
        return sp.get();
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
    public static MaplePacket UserHit(UserHitData uhd) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserHit);

        sp.Encode4(uhd.dwCharacterID);
        sp.Encode1(uhd.nAttackIdx);
        sp.Encode4(uhd.nDamage); // internal damage

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0); // critical
        }

        if (uhd.dwTemplateID != 0) {
            sp.Encode4(uhd.dwTemplateID); // dwTemplateID
            sp.Encode1(uhd.nLeft); // bLeft

            if (Version.GreaterOrEqual(Region.JMS, 302)) {
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

        return sp.get();
    }

    // CUser::OnEmotion
    public static MaplePacket UserEmotion(MapleCharacter chr, int expression) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEmotion);

        sp.Encode4(chr.getId()); // remote
        sp.EncodeBuffer(DataCUser.Emotion(expression));
        return sp.get();
    }

    // CUser::SetActiveEffectItem
    public static MaplePacket UserSetActiveEffectItem(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSetActiveEffectItem);

        sp.Encode4(chr.getId());
        sp.Encode4(chr.getActiveEffectItem());
        return sp.get();
    }

    // CUserRemote::OnSetActivePortableChair
    public static MaplePacket UserSetActivePortableChair(int characterid, int itemid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserSetActivePortableChair);

        sp.Encode4(characterid);
        sp.Encode4(itemid);

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode4(0);
        }

        return sp.get();
    }

    // CUserRemote::OnAvatarModified
    public static MaplePacket UserAvatarModified(TacosCharacter chr, int flag) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserAvatarModified);

        sp.Encode4(chr.getId());
        sp.Encode1(flag);

        if ((flag & 0x01) != 0) {
            sp.EncodeBuffer(DataAvatarLook.Encode(chr));
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
        return sp.get();
    }

    // CUser::OnEffect
    public static MaplePacket UserEffectRemote(ArgUserEffect arg) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(arg.chr.getId());
        sp.EncodeBuffer(ResCUserLocal.EffectData(arg));
        return sp.get();
    }

    // CUserRemote::OnSetTemporaryStat
    // CUserRemote::OnResetTemporaryStat
    // CUserRemote::OnReceiveHP
    public static MaplePacket UserHP(int cid, int curhp, int maxhp) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserHP);

        sp.Encode4(cid);
        sp.Encode4(curhp);
        sp.Encode4(maxhp);
        return sp.get();
    }

    public static MaplePacket cancelForeignDebuff(int cid, long mask, boolean first) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTemporaryStatReset);

        sp.Encode4(cid);
        sp.Encode8(first ? mask : 0);
        sp.Encode8(first ? 0 : mask);
        return sp.get();
    }

    public static byte[] writeLongMaskFromList(List<MapleBuffStat> statups) {
        ServerPacket data = new ServerPacket();

        long firstmask = 0;
        long secondmask = 0;
        for (MapleBuffStat statup : statups) {
            if (statup.isFirst()) {
                firstmask |= statup.getValue();
            } else {
                secondmask |= statup.getValue();
            }
        }
        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            data.EncodeZeroBytes(4);
        }
        if (Version.GreaterOrEqual(Region.JMS, 164)) {
            data.Encode8(firstmask);
        }

        data.Encode8(secondmask);

        return data.get().getBytes();
    }

    public static byte[] writeLongMask(List<OdinPair<MapleBuffStat, Integer>> statups) {
        ServerPacket data = new ServerPacket();

        long firstmask = 0;
        long secondmask = 0;
        for (OdinPair<MapleBuffStat, Integer> statup : statups) {
            if (statup.getLeft().isFirst()) {
                firstmask |= statup.getLeft().getValue();
            } else {
                secondmask |= statup.getLeft().getValue();
            }
        }
        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            data.EncodeZeroBytes(4);
        }
        if (Version.GreaterOrEqual(Region.JMS, 164)) {
            data.Encode8(firstmask);
        }
        data.Encode8(secondmask);

        return data.get().getBytes();
    }

    public static MaplePacket giveForeignBuff(int cid, List<OdinPair<MapleBuffStat, Integer>> statups, MapleStatEffect effect) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTemporaryStatSet);

        sp.Encode4(cid);
        sp.EncodeBuffer(writeLongMask(statups));
        for (OdinPair<MapleBuffStat, Integer> statup : statups) {
            sp.Encode2(statup.getRight().shortValue());
        }
        sp.Encode2(0); // same as give_buff
        if (effect.isMorph()) {
            sp.Encode1(0);
        }
        sp.Encode1(0);
        return sp.get();
    }

    public static MaplePacket giveForeignDebuff(int cid, final List<OdinPair<MapleDisease, Integer>> statups, int skillid, int level) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserTemporaryStatSet);

        sp.Encode4(cid);
        sp.EncodeBuffer(writeLongDiseaseMask(statups));

        if (skillid == 125) {
            sp.Encode2(0);
        }
        sp.Encode2(skillid);
        sp.Encode2(level);
        sp.Encode2(0); // same as give_buff
        sp.Encode2(900); //Delay
        return sp.get();
    }

    // List<Pair<MapleDisease, Integer>>
    public static byte[] writeLongDiseaseMask(List<OdinPair<MapleDisease, Integer>> statups) {
        ServerPacket data = new ServerPacket();

        long firstmask = 0;
        long secondmask = 0;
        for (OdinPair<MapleDisease, Integer> statup : statups) {
            if (statup.getLeft().isFirst()) {
                firstmask |= statup.getLeft().getValue();
            } else {
                secondmask |= statup.getLeft().getValue();
            }
        }
        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            data.EncodeZeroBytes(4);
        }
        if (Version.GreaterOrEqual(Region.JMS, 164)) {
            data.Encode8(firstmask);
        }
        data.Encode8(secondmask);

        return data.get().getBytes();
    }

    public static MaplePacket showPetLevelUp(MapleCharacter chr, int index) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(chr.getId());
        sp.Encode1(4);
        sp.Encode1(0);
        sp.Encode4(index);
        return sp.get();
    }

    public static MaplePacket showRewardItemAnimation(int itemId, String effect, int from_playerid) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(from_playerid);
        sp.Encode1(15);
        sp.Encode4(itemId);
        sp.Encode1(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            sp.EncodeStr(effect);
        }
        return sp.get();
    }

    //its likely that durability items use this
    public static MaplePacket showHpHealed(int cid, final int amount) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(cid);
        sp.Encode1(10); //Type
        sp.Encode4(amount);
        return sp.get();
    }

    public static MaplePacket ItemMakerResultTo(MapleCharacter chr, boolean is_success) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_UserEffectRemote);

        sp.Encode4(chr.getId());
        sp.Encode1(17);
        sp.Encode4(is_success ? 0 : 1);
        return sp.get();
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

        return data.get().getBytes();
    }

}
