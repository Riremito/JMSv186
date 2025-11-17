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
import odin.tools.Pair;

/**
 *
 * @author Riremito
 */
public class ResCUserRemote {

    // CUserRemote::OnMove
    public static MaplePacket Move(MapleCharacter chr, ParseCMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserMove);
        sp.Encode4(chr.getId());
        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    // CUserRemote::OnAttack
    public static MaplePacket UserAttack(AttackInfo attack) {
        ServerPacket sp = new ServerPacket(attack.GetHeader());

        if (Version.LessOrEqual(Region.JMS, 147)) {
            sp.Encode4(attack.CharacterId);
            sp.Encode1(attack.HitKey);
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
                    if (attack.IsMesoExplosion()) {
                        sp.Encode1(oned.attack.size());
                    }
                    for (Pair<Integer, Boolean> eachd : oned.attack) {
                        sp.Encode4(eachd.left.intValue() | ((eachd.right ? 1 : 0) << 31));
                    }
                }
            }
            if (attack.IsQuantumExplosion()) {
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
                if (attack.IsMesoExplosion()) {
                    sp.Encode1(oned.attack.size());
                }
                for (Pair<Integer, Boolean> eachd : oned.attack) {
                    if (Version.LessOrEqual(Region.JMS, 131)) {
                        sp.Encode4(eachd.left.intValue() | ((eachd.right ? 1 : 0) << 31));
                    } else {
                        sp.Encode1(eachd.right ? 1 : 0);
                        sp.Encode4(eachd.left.intValue());
                    }
                }
            }
        }
        if (attack.IsQuantumExplosion()) {
            sp.Encode4(attack.tKeyDown);
        }
        if (ServerConfig.JMS164orLater()) {
            if (attack.GetHeader() == ServerPacket.Header.LP_UserShootAttack) {
                sp.Encode2(attack.X);
                sp.Encode2(attack.Y);
            }
        }
        return sp.get();
    }

    // CUserRemote::OnSkillPrepare
    public static MaplePacket SkillPrepare(MapleCharacter chr, int skill_id, byte skill_level, short action, byte m_nPrepareSkillActionSpeed) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSkillPrepare);

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
    public static MaplePacket SkillCancel(MapleCharacter chr, int skillId) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSkillCancel);
        sp.Encode4(chr.getId());
        sp.Encode4(skillId);
        return sp.get();
    }

    // CUserRemote::OnHit
    public static MaplePacket Hit(MapleCharacter chr, int attack_index, int mob_id, int damage, byte left, int reflect, boolean is_pg, int mob_object_id, int fake_skill_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserHit);

        sp.Encode4(chr.getId());
        sp.Encode1(attack_index); // nAttackIdx
        sp.Encode4(damage); // nDamage

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0); // critical
        }

        // -1
        if (-1 <= attack_index) {
            sp.Encode4(mob_id);
            sp.Encode1(left); // bLeft

            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode4(0);
                sp.Encode4(0);
            }

            sp.Encode1(reflect);
            if (reflect != 0) {
                sp.Encode1(is_pg ? 1 : 0);
                sp.Encode4(mob_object_id);
                sp.Encode1(6); // データ無視の可能性あり
                sp.Encode2(0); // データ無視の可能性あり, X
                sp.Encode2(0); // データ無視の可能性あり, Y
            }

            sp.Encode1(0); // stance flag, skill id = 33110000
        }
        // -2
        sp.Encode4(damage); // for 4120002
        if (damage == -1) {
            sp.Encode4(fake_skill_id); // skill_id == 4120002
        }
        return sp.get();
    }

    // CUser::OnEmotion
    public static MaplePacket Emotion(MapleCharacter chr, int expression) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEmotion);

        sp.Encode4(chr.getId()); // remote
        sp.EncodeBuffer(DataCUser.Emotion(expression));
        return sp.get();
    }

    // CUser::SetActiveEffectItem
    public static MaplePacket SetActiveEffectItem(MapleCharacter chr, int itemid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSetActiveEffectItem);

        sp.Encode4(chr.getId());
        sp.Encode4(itemid);
        return sp.get();
    }

    // CUserRemote::OnSetActivePortableChair
    public static MaplePacket SetActivePortableChair(int characterid, int itemid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSetActivePortableChair);
        sp.Encode4(characterid);
        sp.Encode4(itemid);
        return sp.get();
    }

    // CUserRemote::OnAvatarModified
    public static MaplePacket AvatarModified(MapleCharacter chr, int flag) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserAvatarModified);

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
    public static MaplePacket EffectRemote(ArgUserEffect arg) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectRemote);
        sp.Encode4(arg.chr.getId());
        sp.EncodeBuffer(ResCUserLocal.EffectData(arg));
        return sp.get();
    }

    // CUserRemote::OnSetTemporaryStat
    // CUserRemote::OnResetTemporaryStat
    // CUserRemote::OnReceiveHP
    public static MaplePacket updatePartyMemberHP(int cid, int curhp, int maxhp) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserHP);

        sp.Encode4(cid);
        sp.Encode4(curhp);
        sp.Encode4(maxhp);
        return sp.get();
    }

    public static MaplePacket cancelForeignDebuff(int cid, long mask, boolean first) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTemporaryStatReset);

        sp.Encode4(cid);
        sp.Encode8(first ? mask : 0);
        sp.Encode8(first ? 0 : mask);
        return sp.get();
    }

    public static MaplePacket showMonsterRiding(int cid, List<Pair<MapleBuffStat, Integer>> statups, int itemId, int skillId) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTemporaryStatSet);

        sp.Encode4(cid);
        sp.EncodeBuffer(writeLongMask(statups));
        sp.Encode2(0);
        sp.Encode4(itemId);
        sp.Encode4(skillId);
        sp.Encode4(0);
        sp.Encode2(0);
        sp.Encode1(0);
        sp.Encode1(0);
        return sp.get();
    }

    public static MaplePacket cancelForeignBuff(int cid, List<MapleBuffStat> statups) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTemporaryStatReset);

        sp.Encode4(cid);
        sp.EncodeBuffer(writeLongMaskFromList(statups));
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

    public static byte[] writeLongMask(List<Pair<MapleBuffStat, Integer>> statups) {
        ServerPacket data = new ServerPacket();

        long firstmask = 0;
        long secondmask = 0;
        for (Pair<MapleBuffStat, Integer> statup : statups) {
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

    public static MaplePacket giveForeignBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups, MapleStatEffect effect) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTemporaryStatSet);

        sp.Encode4(cid);
        sp.EncodeBuffer(writeLongMask(statups));
        for (Pair<MapleBuffStat, Integer> statup : statups) {
            sp.Encode2(statup.getRight().shortValue());
        }
        sp.Encode2(0); // same as give_buff
        if (effect.isMorph()) {
            sp.Encode1(0);
        }
        sp.Encode1(0);
        return sp.get();
    }

    public static MaplePacket giveForeignDebuff(int cid, final List<Pair<MapleDisease, Integer>> statups, int skillid, int level) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTemporaryStatSet);

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
    public static byte[] writeLongDiseaseMask(List<Pair<MapleDisease, Integer>> statups) {
        ServerPacket data = new ServerPacket();

        long firstmask = 0;
        long secondmask = 0;
        for (Pair<MapleDisease, Integer> statup : statups) {
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

    public static MaplePacket giveForeignPirate(List<Pair<MapleBuffStat, Integer>> statups, int duration, int cid, int skillid) {
        final boolean infusion = skillid == 5121009 || skillid == 15111005;
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTemporaryStatSet);

        sp.Encode4(cid);
        sp.EncodeBuffer(writeLongMask(statups));

        sp.Encode2(0);
        for (Pair<MapleBuffStat, Integer> stat : statups) {
            sp.Encode4(stat.getRight().intValue());
            sp.Encode8(skillid);
            sp.EncodeZeroBytes(infusion ? 7 : 1);
            sp.Encode2(duration); //duration... seconds
        }
        sp.Encode2(infusion ? 600 : 0);
        return sp.get();
    }

    public static MaplePacket giveEnergyChargeTest(int cid, int bar, int bufflength) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserTemporaryStatSet);

        sp.Encode4(cid);
        sp.Encode8(MapleBuffStat.ENERGY_CHARGE.getValue());
        sp.Encode8(0);
        sp.Encode2(0);
        sp.Encode4(0);
        sp.Encode4(1555445060); //?
        sp.Encode2(0);
        sp.Encode4(Math.min(bar, 10000)); // 0 = no bar, 10000 = full bar
        sp.Encode8(0); //skillid, but its 0 here
        sp.Encode1(0);
        sp.Encode4(bar >= 10000 ? bufflength : 0); //short - bufflength...50
        return sp.get();
    }

    public static final MaplePacket showPetLevelUp(final MapleCharacter chr, final int index) {
        final ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectRemote);

        sp.Encode4(chr.getId());
        sp.Encode1(4);
        sp.Encode1(0);
        sp.Encode4(index);
        return sp.get();
    }

    public static MaplePacket showRewardItemAnimation(int itemId, String effect, int from_playerid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectRemote);

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
    public static final MaplePacket showHpHealed(final int cid, final int amount) {
        final ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectRemote);

        sp.Encode4(cid);
        sp.Encode1(10); //Type
        sp.Encode4(amount);
        return sp.get();
    }

    public static final MaplePacket ItemMakerResultTo(MapleCharacter chr, boolean is_success) {
        final ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectRemote);

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
