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
package packet.response;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleDisease;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import config.ServerConfig;
import constants.GameConstants;
import handling.MaplePacket;
import handling.channel.handler.AttackInfo;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.util.ArrayList;
import java.util.List;
import packet.ServerPacket;
import packet.ops.OpsUserEffect;
import packet.request.struct.CMovePath;
import packet.response.struct.AvatarLook;
import packet.response.struct.Structure;
import packet.response.struct.TestHelper;
import server.MapleStatEffect;
import server.Randomizer;
import tools.AttackPair;
import tools.Pair;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResCUserRemote {

    public static MaplePacket SetActivePortableChair(int characterid, int itemid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSetActivePortableChair);
        sp.Encode4(characterid);
        sp.Encode4(itemid);
        return sp.get();
    }

    // removePlayerFromMap
    public static MaplePacket removePlayerFromMap(int player_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserLeaveField);
        sp.Encode4(player_id);
        return sp.get();
    }

    public static MaplePacket movePlayer(MapleCharacter chr, CMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserMove);
        sp.Encode4(chr.getId());
        sp.EncodeBuffer(data.get());
        return sp.get();
    }

    public static MaplePacket skillCancel(MapleCharacter chr, int skillId) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserSkillCancel);
        sp.Encode4(chr.getId());
        sp.Encode4(skillId);
        return sp.get();
    }

    // spawnPlayerMapobject
    // CUserPool::OnUserEnterField
    public static MaplePacket spawnPlayerMapobject(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEnterField);
        sp.Encode4(chr.getId());
        // 自分のキャラクターの場合はここで終了
        if (ServerConfig.JMS164orLater()) {
            sp.Encode1(chr.getLevel());
        }
        sp.EncodeStr(chr.getName());
        if (ServerConfig.JMS194orLater()) {
            sp.EncodeStr("");
        }
        // guild
        MapleGuild gs = null;
        if (0 < chr.getGuildId()) {
            gs = World.Guild.getGuild(chr.getGuildId());
        }
        if (gs != null) {
            // guild info
            sp.EncodeStr(gs.getName());
            sp.Encode2(gs.getLogoBG());
            sp.Encode1(gs.getLogoBGColor());
            sp.Encode2(gs.getLogo());
            sp.Encode1(gs.getLogoColor());
        } else {
            // empty guild
            sp.EncodeStr("");
            sp.Encode2(0);
            sp.Encode1(0);
            sp.Encode2(0);
            sp.Encode1(0);
        }
        List<Pair<Integer, Boolean>> buffvalue = new ArrayList<Pair<Integer, Boolean>>();
        if (ServerConfig.JMS164orLater()) {
            long fbuffmask = 16646144L;
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
            sp.Encode8(fbuffmask);
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
        sp.Encode8(buffmask);
        if (ServerConfig.JMS164orLater()) {
            // buffmask
            if (ServerConfig.JMS194orLater()) {
                sp.Encode4(0);
            }
            for (Pair<Integer, Boolean> i : buffvalue) {
                if (i.right) {
                    sp.Encode2(i.left.shortValue());
                } else {
                    sp.Encode1(i.left.byteValue());
                }
            }
            final int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
            //CHAR_MAGIC_SPAWN is really just tickCount
            //this is here as it explains the 7 "dummy" buffstats which are placed into every character
            //these 7 buffstats are placed because they have irregular packet structure.
            //they ALL have writeShort(0); first, then a long as their variables, then server tick count
            //0x80000, 0x100000, 0x200000, 0x400000, 0x800000, 0x1000000, 0x2000000
            sp.Encode1(0); //start of energy charge
            sp.Encode1(0);
            if (ServerConfig.IsPreBB()) {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode2(0); //start of dash_speed
                sp.Encode8(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode2(0); //start of dash_jump
                sp.Encode8(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode2(0); //start of Monster Riding
                int buffSrc = chr.getBuffSource(MapleBuffStat.MONSTER_RIDING);
                if (buffSrc > 0) {
                    final IItem c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118 /*-122*/);
                    final IItem mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18 /*-22*/);
                    if (GameConstants.getMountItem(buffSrc) == 0 && c_mount != null) {
                        sp.Encode4(c_mount.getItemId());
                    } else if (GameConstants.getMountItem(buffSrc) == 0 && mount != null) {
                        sp.Encode4(mount.getItemId());
                    } else {
                        sp.Encode4(GameConstants.getMountItem(buffSrc));
                    }
                    sp.Encode4(buffSrc);
                } else {
                    sp.Encode8(0);
                }
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode8(0); //speed infusion behaves differently here
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode4(1);
                sp.Encode8(0); //homing beacon
                sp.Encode1(0);
                sp.Encode2(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode4(0); //and finally, something ive no idea
                sp.Encode8(0);
                sp.Encode1(1);
                sp.Encode4(CHAR_MAGIC_SPAWN);
                sp.Encode2(0);
            }
            sp.Encode2(chr.getJob());
        }
        sp.EncodeBuffer(AvatarLook.Encode(chr));
        sp.Encode4(0); //this is CHARID to follow
        if (ServerConfig.JMS164orLater()) {
            sp.Encode4(0); //probably charid following
            sp.Encode4(0);
            if (ServerConfig.JMS194orLater()) {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
            }
            sp.Encode4(0);
        }
        sp.Encode4(chr.getItemEffect());
        sp.Encode4(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        sp.Encode2(chr.getPosition().x);
        sp.Encode2(chr.getPosition().y);
        sp.Encode1(chr.getStance());
        sp.Encode2(0); // FH
        sp.Encode1(0); // pet size
        sp.Encode4(chr.getMount().getLevel()); // mount lvl
        sp.Encode4(chr.getMount().getExp()); // exp
        sp.Encode4(chr.getMount().getFatigue()); // tiredness
        // MiniRoomBalloon (ゲーム) 1 byte flag + data
        sp.EncodeBuffer(Structure.AnnounceBox(chr));
        // ADBoardBalloon (黒板) 1 byte flag + data
        {
            sp.Encode1(chr.getChalkboard() != null && chr.getChalkboard().length() > 0 ? 1 : 0);
            if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
                sp.EncodeStr(chr.getChalkboard());
            }
        }
        sp.Encode1(0); //count4 -> buf0x10 4
        sp.Encode1(0); //count4 -> buf0x10 4
        // MarriageRecord 1 byte flag + data
        {
            sp.Encode1(0);
        }
        sp.Encode1(chr.getEffectMask()); // Effect
        sp.Encode4(0); // not in KMST, in GMS v95: m_nPhase
        // 特殊マップ専用
        // MonsterCarnival
        if (chr.checkSpecificMap(980000000, 1000) || chr.checkSpecificMap(980030000, 1000)) {
            sp.Encode1((chr.getCarnivalParty() != null) ? chr.getCarnivalParty().getTeam() : 0); // sub_5CD27E
        } // Coconut
        else if (chr.checkSpecificMap(109080000, 1000)) {
            sp.Encode1(chr.getCoconutTeam()); // 0059F0ED
        }
        return sp.get();
    }

    public static MaplePacket useChalkboard(final int charid, final String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserADBoard.get());
        mplew.writeInt(charid);
        if (msg == null || msg.length() <= 0) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket itemEffect(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserSetActiveEffectItem.get());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        return mplew.getPacket();
    }

    public static MaplePacket fishingCaught(int chrid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_Fishing_Caught);
        sp.Encode4(chrid);
        return sp.get();
    }

    public static MaplePacket cancelForeignDebuff(int cid, long mask, boolean first) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatReset.get());
        mplew.writeInt(cid);
        mplew.writeLong(first ? mask : 0);
        mplew.writeLong(first ? 0 : mask);
        return mplew.getPacket();
    }

    public static MaplePacket showMonsterRiding(int cid, List<Pair<MapleBuffStat, Integer>> statups, int itemId, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.get());
        mplew.writeInt(cid);
        writeLongMask(mplew, statups);
        mplew.writeShort(0);
        mplew.writeInt(itemId);
        mplew.writeInt(skillId);
        mplew.writeInt(0);
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket cancelForeignBuff(int cid, List<MapleBuffStat> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatReset.get());
        mplew.writeInt(cid);
        writeLongMaskFromList(mplew, statups);
        return mplew.getPacket();
    }

    static void writeLongMaskFromList(MaplePacketLittleEndianWriter mplew, List<MapleBuffStat> statups) {
        long firstmask = 0;
        long secondmask = 0;
        for (MapleBuffStat statup : statups) {
            if (statup.isFirst()) {
                firstmask |= statup.getValue();
            } else {
                secondmask |= statup.getValue();
            }
        }
        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }
        if (ServerConfig.version > 131) {
            mplew.writeLong(firstmask);
        }
        mplew.writeLong(secondmask);
    }

    static void writeLongMask(MaplePacketLittleEndianWriter mplew, List<Pair<MapleBuffStat, Integer>> statups) {
        long firstmask = 0;
        long secondmask = 0;
        for (Pair<MapleBuffStat, Integer> statup : statups) {
            if (statup.getLeft().isFirst()) {
                firstmask |= statup.getLeft().getValue();
            } else {
                secondmask |= statup.getLeft().getValue();
            }
        }
        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }
        if (ServerConfig.version > 131) {
            mplew.writeLong(firstmask);
        }
        mplew.writeLong(secondmask);
    }

    public static MaplePacket giveForeignBuff(int cid, List<Pair<MapleBuffStat, Integer>> statups, MapleStatEffect effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.get());
        mplew.writeInt(cid);
        writeLongMask(mplew, statups);
        for (Pair<MapleBuffStat, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
        }
        mplew.writeShort(0); // same as give_buff
        if (effect.isMorph()) {
            mplew.write(0);
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket giveForeignDebuff(int cid, final List<Pair<MapleDisease, Integer>> statups, int skillid, int level) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.get());
        mplew.writeInt(cid);
        writeLongDiseaseMask(mplew, statups);
        if (skillid == 125) {
            mplew.writeShort(0);
        }
        mplew.writeShort(skillid);
        mplew.writeShort(level);
        mplew.writeShort(0); // same as give_buff
        mplew.writeShort(900); //Delay
        return mplew.getPacket();
    }

    // List<Pair<MapleDisease, Integer>>
    static void writeLongDiseaseMask(MaplePacketLittleEndianWriter mplew, List<Pair<MapleDisease, Integer>> statups) {
        long firstmask = 0;
        long secondmask = 0;
        for (Pair<MapleDisease, Integer> statup : statups) {
            if (statup.getLeft().isFirst()) {
                firstmask |= statup.getLeft().getValue();
            } else {
                secondmask |= statup.getLeft().getValue();
            }
        }
        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }
        if (ServerConfig.version > 131) {
            mplew.writeLong(firstmask);
        }
        mplew.writeLong(secondmask);
    }

    public static MaplePacket giveForeignPirate(List<Pair<MapleBuffStat, Integer>> statups, int duration, int cid, int skillid) {
        final boolean infusion = skillid == 5121009 || skillid == 15111005;
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.get());
        mplew.writeInt(cid);
        ResCUserRemote.writeLongMask(mplew, statups);
        mplew.writeShort(0);
        for (Pair<MapleBuffStat, Integer> stat : statups) {
            mplew.writeInt(stat.getRight().intValue());
            mplew.writeLong(skillid);
            mplew.writeZeroBytes(infusion ? 7 : 1);
            mplew.writeShort(duration); //duration... seconds
        }
        mplew.writeShort(infusion ? 600 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket giveEnergyChargeTest(int cid, int bar, int bufflength) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserTemporaryStatSet.get());
        mplew.writeInt(cid);
        mplew.writeLong(MapleBuffStat.ENERGY_CHARGE.getValue());
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeInt(0);
        mplew.writeInt(1555445060); //?
        mplew.writeShort(0);
        mplew.writeInt(Math.min(bar, 10000)); // 0 = no bar, 10000 = full bar
        mplew.writeLong(0); //skillid, but its 0 here
        mplew.write(0);
        mplew.writeInt(bar >= 10000 ? bufflength : 0); //short - bufflength...50
        return mplew.getPacket();
    }

    public static final MaplePacket showPetLevelUp(final MapleCharacter chr, final int index) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.get());
        mplew.writeInt(chr.getId());
        mplew.write(4);
        mplew.write(0);
        mplew.writeInt(index);
        return mplew.getPacket();
    }

    public static MaplePacket showRewardItemAnimation(int itemId, String effect, int from_playerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.get());
        mplew.writeInt(from_playerid);
        mplew.write(15);
        mplew.writeInt(itemId);
        mplew.write(effect != null && effect.length() > 0 ? 1 : 0);
        if (effect != null && effect.length() > 0) {
            mplew.writeMapleAsciiString(effect);
        }
        return mplew.getPacket();
    }

    //its likely that durability items use this
    public static final MaplePacket showHpHealed(final int cid, final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.get());
        mplew.writeInt(cid);
        mplew.write(10); //Type
        mplew.writeInt(amount);
        return mplew.getPacket();
    }

    public static MaplePacket showBuffeffect(int cid, int skillid, int effectid) {
        return showBuffeffect(cid, skillid, effectid, (byte) 3);
    }

    public static MaplePacket showBuffeffect(int cid, int skillid, int effectid, byte direction) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.get());
        mplew.writeInt(cid);
        mplew.write(effectid); //ehh?
        mplew.writeInt(skillid);
        mplew.write(1); //skill level = 1 for the lulz
        mplew.write(1); //actually skill level ? 0 = dosnt show
        if (direction != (byte) 3) {
            mplew.write(direction);
        }
        return mplew.getPacket();
    }

    public static MaplePacket showForeignItemLevelupEffect(int cid) {
        return showSpecialEffect(cid, OpsUserEffect.UserEffect_ItemLevelUp.get());
    }

    public static MaplePacket showForeginCardEffect(int id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserEffectRemote);
        sp.Encode4(id);
        sp.Encode1(14); //14
        return sp.get();
    }

    public static final MaplePacket ItemMakerResultTo(MapleCharacter chr, boolean is_success) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.get());
        mplew.writeInt(chr.getId());
        mplew.write(17);
        mplew.writeInt(is_success ? 0 : 1);
        return mplew.getPacket();
    }

    public static MaplePacket showSpecialEffect(int cid, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.get());
        mplew.writeInt(cid);
        mplew.write(effect);
        return mplew.getPacket();
    }

    public static MaplePacket showForeignEffect(int cid, int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEffectRemote.get());
        mplew.writeInt(cid);
        mplew.write(effect); // 0 = Level up, 8 = job change
        return mplew.getPacket();
    }

    public static MaplePacket updatePartyMemberHP(int cid, int curhp, int maxhp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserHP.get());
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);
        return mplew.getPacket();
    }

    public static MaplePacket damagePlayer(int skill, int monsteridfrom, int cid, int damage, int fake, byte direction, int reflect, boolean is_pg, int oid, int pos_x, int pos_y) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserHit.get());
        mplew.writeInt(cid);
        mplew.write(skill);
        mplew.writeInt(damage);
        mplew.writeInt(monsteridfrom);
        mplew.write(direction);
        if (reflect > 0) {
            mplew.write(reflect);
            mplew.write(is_pg ? 1 : 0);
            mplew.writeInt(oid);
            mplew.write(6);
            mplew.writeShort(pos_x);
            mplew.writeShort(pos_y);
            mplew.write(0);
        } else {
            mplew.writeShort(0);
        }
        mplew.writeInt(damage);
        if (fake > 0) {
            mplew.writeInt(fake);
        }
        return mplew.getPacket();
    }

    // CLifePool::OnUserAttack
    public static MaplePacket UserAttack(AttackInfo attack) {
        ServerPacket p = new ServerPacket(attack.GetHeader());
        p.Encode4(attack.CharacterId);
        p.Encode1(attack.HitKey);
        if (ServerConfig.JMS164orLater()) {
            p.Encode1(attack.m_nLevel);
        }
        p.Encode1(attack.SkillLevel); // nPassiveSLV
        if (0 < attack.nSkillID) {
            p.Encode4(attack.nSkillID); // nSkillID
        }
        if (ServerConfig.JMS164orLater()) {
            p.Encode1(attack.BuffKey); // bSerialAttack
        }
        if (ServerConfig.JMS131orEarlier()) {
            p.Encode1(attack.AttackActionKey);
        } else {
            p.Encode2(attack.AttackActionKey);
        }
        p.Encode1(attack.nAttackSpeed); // nActionSpeed
        p.Encode1(attack.nMastery); // nMastery
        p.Encode4(attack.nBulletItemID); // nBulletItemID
        for (AttackPair oned : attack.allDamage) {
            if (oned.attack != null) {
                p.Encode4(oned.objectid);
                p.Encode1(7);
                if (attack.IsMesoExplosion()) {
                    p.Encode1(oned.attack.size());
                }
                for (Pair<Integer, Boolean> eachd : oned.attack) {
                    if (ServerConfig.JMS131orEarlier()) {
                        p.Encode4(eachd.left.intValue() | ((eachd.right ? 1 : 0) << 31));
                    } else {
                        p.Encode1(eachd.right ? 1 : 0);
                        p.Encode4(eachd.left.intValue());
                    }
                }
            }
        }
        if (attack.IsQuantumExplosion()) {
            p.Encode4(attack.tKeyDown);
        }
        if (ServerConfig.JMS164orLater()) {
            if (attack.GetHeader() == ServerPacket.Header.LP_UserShootAttack) {
                p.Encode2(attack.X);
                p.Encode2(attack.Y);
            }
        }
        return p.get();
    }

    public static MaplePacket facialExpression(MapleCharacter from, int expression) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserEmotion.get());
        mplew.writeInt(from.getId());
        mplew.writeInt(expression);
        mplew.writeInt(-1); //itemid of expression use
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket updateCharLook(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserAvatarModified.get());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        TestHelper.addCharLook(mplew, chr, false);
        Pair<List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(mplew, rings.getLeft());
        addRingInfo(mplew, rings.getRight());
        mplew.writeZeroBytes(5); //probably marriage ring (1) -> charid to follow (4)
        return mplew.getPacket();
    }

    public static void addRingInfo(MaplePacketLittleEndianWriter mplew, List<MapleRing> rings) {
        mplew.write(rings.size());
        for (MapleRing ring : rings) {
            mplew.writeInt(1);
            mplew.writeLong(ring.getRingId());
            mplew.writeLong(ring.getPartnerRingId());
            mplew.writeInt(ring.getItemId());
        }
    }

    public static MaplePacket skillEffect(MapleCharacter from, int skillId, byte level, byte flags, byte speed, byte unk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_UserSkillPrepare.get());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.write(flags);
        mplew.write(speed);
        mplew.write(unk); // Direction ??
        return mplew.getPacket();
    }

}
