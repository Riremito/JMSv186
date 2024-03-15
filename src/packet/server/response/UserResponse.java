/*
 * Copyright (C) 2024 Riremito
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
package packet.server.response;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleQuestStatus;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import config.ServerConfig;
import constants.GameConstants;
import handling.MaplePacket;
import handling.world.World;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import packet.client.request.MovementPacket;
import packet.client.request.struct.CMovePath;
import packet.server.ServerPacket;
import packet.server.response.struct.AvatarLook;
import packet.server.response.struct.Structure;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.movement.LifeMovementFragment;
import tools.Pair;

/**
 *
 * @author Riremito
 */
public class UserResponse {

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
                if (q.getQuest().getMedalItem() > 0 && GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP) {
                    //chair kind medal viewmedal is weird
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

    public static MaplePacket movePlayer(MapleCharacter chr, CMovePath data) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_UserMove);

        sp.Encode4(chr.getId());
        sp.EncodeBuffer(data.get());
        return sp.Get();
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
                    final IItem c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118 /*-122*/);
                    final IItem mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18 /*-22*/);
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
        p.Encode4(0); //this is CHARID to follow
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
        p.Encode1(0); //count4 -> buf0x10 4
        p.Encode1(0); //count4 -> buf0x10 4
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

}
