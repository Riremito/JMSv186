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
package packet.response.data;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.util.ArrayList;
import java.util.List;
import packet.ServerPacket;
import packet.response.struct.Structure;
import server.Randomizer;
import server.shops.AbstractPlayerStore;
import server.shops.IMaplePlayerShop;
import tools.Pair;

/**
 *
 * @author Riremito
 */
public class DataCUserRemote {

    // CUserRemote::Init
    public static byte[] Init(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (Version.LessOrEqual(Region.KMS, 65)) {
            // nothing
        } else if (ServerConfig.JMS164orLater()) {
            data.Encode1(chr.getLevel());
        }
        data.EncodeStr(chr.getName());
        if (ServerConfig.JMS194orLater()) {
            data.EncodeStr("");
        }
        // guild
        MapleGuild gs = null;
        if (0 < chr.getGuildId()) {
            gs = World.Guild.getGuild(chr.getGuildId());
        }
        if (gs != null) {
            // guild info
            data.EncodeStr(gs.getName());
            data.Encode2(gs.getLogoBG());
            data.Encode1(gs.getLogoBGColor());
            data.Encode2(gs.getLogo());
            data.Encode1(gs.getLogoColor());
        } else {
            // empty guild
            data.EncodeStr("");
            data.Encode2(0);
            data.Encode1(0);
            data.Encode2(0);
            data.Encode1(0);
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
            data.Encode8(fbuffmask);
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
        data.Encode8(buffmask);
        if (ServerConfig.JMS164orLater()) {
            // buffmask
            if (ServerConfig.JMS194orLater()) {
                data.Encode4(0);
            }
            for (Pair<Integer, Boolean> i : buffvalue) {
                if (i.right) {
                    data.Encode2(i.left.shortValue());
                } else {
                    data.Encode1(i.left.byteValue());
                }
            }
            final int CHAR_MAGIC_SPAWN = Randomizer.nextInt();
            //CHAR_MAGIC_SPAWN is really just tickCount
            //this is here as it explains the 7 "dummy" buffstats which are placed into every character
            //these 7 buffstats are placed because they have irregular packet structure.
            //they ALL have writeShort(0); first, then a long as their variables, then server tick count
            //0x80000, 0x100000, 0x200000, 0x400000, 0x800000, 0x1000000, 0x2000000
            data.Encode1(0); //start of energy charge
            data.Encode1(0);
            if (Version.PreBB()) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode1(1);
                data.Encode4(CHAR_MAGIC_SPAWN);
                data.Encode2(0); //start of dash_speed
                data.Encode8(0);
                data.Encode1(1);
                data.Encode4(CHAR_MAGIC_SPAWN);
                data.Encode2(0); //start of dash_jump
                data.Encode8(0);
                data.Encode1(1);
                data.Encode4(CHAR_MAGIC_SPAWN);
                data.Encode2(0); //start of Monster Riding
                int buffSrc = chr.getBuffSource(MapleBuffStat.MONSTER_RIDING);
                if (buffSrc > 0) {
                    final IItem c_mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118 /*-122*/);
                    final IItem mount = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18 /*-22*/);
                    if (GameConstants.getMountItem(buffSrc) == 0 && c_mount != null) {
                        data.Encode4(c_mount.getItemId());
                    } else if (GameConstants.getMountItem(buffSrc) == 0 && mount != null) {
                        data.Encode4(mount.getItemId());
                    } else {
                        data.Encode4(GameConstants.getMountItem(buffSrc));
                    }
                    data.Encode4(buffSrc);
                } else {
                    data.Encode8(0);
                }
                data.Encode1(1);
                data.Encode4(CHAR_MAGIC_SPAWN);
                data.Encode8(0); //speed infusion behaves differently here
                data.Encode1(1);
                data.Encode4(CHAR_MAGIC_SPAWN);
                data.Encode4(1);
                data.Encode8(0); //homing beacon
                data.Encode1(0);
                data.Encode2(0);
                data.Encode1(1);
                data.Encode4(CHAR_MAGIC_SPAWN);
                data.Encode4(0); //and finally, something ive no idea
                data.Encode8(0);
                data.Encode1(1);
                data.Encode4(CHAR_MAGIC_SPAWN);
                data.Encode2(0);
            }
            data.Encode2(chr.getJob());
        }
        data.EncodeBuffer(DataAvatarLook.Encode(chr));
        data.Encode4(0); //this is CHARID to follow
        if (ServerConfig.JMS164orLater()) {
            data.Encode4(0); //probably charid following
            data.Encode4(0);
            if (ServerConfig.JMS194orLater()) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
            }
            data.Encode4(0);
        }
        data.Encode4(chr.getItemEffect());
        data.Encode4(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        data.Encode2(chr.getPosition().x);
        data.Encode2(chr.getPosition().y);
        data.Encode1(chr.getStance());
        data.Encode2(0); // FH
        data.Encode1(0); // pet size
        data.Encode4(chr.getMount().getLevel()); // mount lvl
        data.Encode4(chr.getMount().getExp()); // exp
        data.Encode4(chr.getMount().getFatigue()); // tiredness
        // MiniRoomBalloon (ゲーム) 1 byte flag + data
        data.EncodeBuffer(Structure.AnnounceBox(chr));
        // ADBoardBalloon (黒板) 1 byte flag + data
        {
            data.Encode1(chr.getChalkboard() != null && chr.getChalkboard().length() > 0 ? 1 : 0);
            if (chr.getChalkboard() != null && chr.getChalkboard().length() > 0) {
                data.EncodeStr(chr.getChalkboard());
            }
        }
        data.Encode1(0); //count4 -> buf0x10 4
        data.Encode1(0); //count4 -> buf0x10 4
        // MarriageRecord 1 byte flag + data
        {
            data.Encode1(0);
        }
        data.Encode1(chr.getEffectMask()); // Effect
        data.Encode4(0); // not in KMST, in GMS v95: m_nPhase
        // 特殊マップ専用
        // MonsterCarnival
        if (chr.checkSpecificMap(980000000, 1000) || chr.checkSpecificMap(980030000, 1000)) {
            data.Encode1((chr.getCarnivalParty() != null) ? chr.getCarnivalParty().getTeam() : 0); // sub_5CD27E
        } // Coconut
        else if (chr.checkSpecificMap(109080000, 1000)) {
            data.Encode1(chr.getCoconutTeam()); // 0059F0ED
        }

        return data.get().getBytes();
    }

    public static byte[] Init_JMS147(MapleCharacter chr) {
        MapleGuild guild = null;
        IMaplePlayerShop shop = chr.getPlayerShop();
        if (0 < chr.getGuildId()) {
            guild = World.Guild.getGuild(chr.getGuildId());
        }
        ServerPacket data = new ServerPacket();
        // CUserRemote::Init
        data.EncodeStr(chr.getName());
        data.EncodeStr((guild != null) ? guild.getName() : "");
        data.Encode2((guild != null) ? guild.getLogoBG() : 0);
        data.Encode1((guild != null) ? guild.getLogoBGColor() : 0);
        data.Encode2((guild != null) ? guild.getLogo() : 0);
        data.Encode1((guild != null) ? guild.getLogoColor() : 0);
        data.EncodeBuffer(DataSecondaryStat.EncodeForRemote_JMS147(chr));
        data.Encode2(0);
        data.EncodeBuffer(DataAvatarLook.Encode(chr));
        data.Encode4(0); // m_dwDriverID
        data.Encode4(chr.getItemEffect());
        data.Encode4(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        data.Encode2(chr.getPosition().x);
        data.Encode2(chr.getPosition().y);
        data.Encode1(chr.getStance()); // m_nMoveAction
        data.Encode2(chr.getFH());
        for (int i = 0; i < 4; i++) {
            MaplePet pet = chr.getPet(i);
            data.Encode1(pet != null ? 1 : 0); // 3 -> null
            if (pet == null) {
                break;
            }
            data.EncodeBuffer(DataCPet.Init(pet));
        }
        data.Encode4(chr.getMount().getLevel()); // m_nTamingMobLevel
        data.Encode4(chr.getMount().getExp()); // m_nTamingMobExp
        data.Encode4(chr.getMount().getFatigue()); // m_nTamingMobFatigue
        data.Encode1((shop != null) ? shop.getGameType() : 0); // m_nMiniRoomType
        if (shop != null && shop.getGameType() != 0) {
            // AnnounceBox & Interaction : TODO Remove
            data.Encode4(((AbstractPlayerStore) shop).getObjectId()); // m_dwMiniRoomSN
            data.EncodeStr(shop.getDescription()); // m_sMiniRoomTitle
            data.Encode1((shop.getPassword().length() != 0) ? 1 : 0); // m_bPrivate
            data.Encode1(shop.getItemId() % 10); // m_nGameKind
            data.Encode1(shop.getSize()); // m_nCurUsers
            data.Encode1(shop.getMaxSize()); // m_nMaxUsers
            data.Encode1(shop.isOpen() ? 0 : 1); // m_bGameOn
        }
        boolean is_adboard = (chr.getChalkboard() != null) && (0 < chr.getChalkboard().length());
        data.Encode1(is_adboard ? 1 : 0); // m_bADBoardRemote
        if (is_adboard) {
            data.EncodeStr(chr.getChalkboard());
        }

        boolean is_couple = false;
        data.Encode1(is_couple ? 1 : 0);
        if (is_couple) {
            data.Encode8(0);
            data.Encode8(0);
            data.Encode4(0);
        }
        boolean is_friend = false;
        data.Encode1(is_friend ? 1 : 0);
        if (is_friend) {
            data.Encode8(0);
            data.Encode8(0);
            data.Encode4(0);
        }
        data.Encode1((0 < chr.getMarriageId()) ? 1 : 0);
        if (0 < chr.getMarriageId()) {
            data.Encode4(chr.getId()); // m_dwMarriageCharacterID
            data.Encode4(chr.getMarriageId()); // m_dwMarriagePairCharacterID
            data.Encode4(chr.getMarriageItemId()); // m_nWeddingRingID
        }
        data.Encode1(chr.getEffectMask()); // m_nDelayedEffectFlag
        return data.get().getBytes();
    }

    public static byte[] Init_JMS302(MapleCharacter chr) {
        MapleGuild guild = null;
        IMaplePlayerShop shop = chr.getPlayerShop();
        if (0 < chr.getGuildId()) {
            guild = World.Guild.getGuild(chr.getGuildId());
        }
        ServerPacket data = new ServerPacket();
        // CUserRemote::Init
        data.Encode1(chr.getLevel());
        data.EncodeStr(chr.getName());
        data.EncodeStr("");
        data.EncodeStr((guild != null) ? guild.getName() : "");
        data.Encode2((guild != null) ? guild.getLogoBG() : 0);
        data.Encode1((guild != null) ? guild.getLogoBGColor() : 0);
        data.Encode2((guild != null) ? guild.getLogo() : 0);
        data.Encode1((guild != null) ? guild.getLogoColor() : 0);
        data.Encode4(0);
        data.Encode4(0);
        data.Encode1(0);
        data.Encode1(0);
        data.EncodeBuffer(DataSecondaryStat.EncodeForRemote_JMS302(chr));
        data.Encode2(0);
        data.Encode2(0);
        data.EncodeBuffer(DataAvatarLook.Encode(chr));
        data.Encode4(0); // m_dwDriverID
        data.Encode4(0); // m_dwPassenserID
        // sub_D0E280
        {
            int unk_count = 0;
            data.Encode4(0);
            data.Encode4(0);
            data.Encode4(unk_count);
            for (int i = 0; i < unk_count; i++) {
                data.Encode4(0);
                data.Encode4(0);
            }
        }
        data.Encode4(0);
        data.Encode4(0);
        data.Encode4(0);
        data.Encode4(0);
        data.Encode4(0);
        data.Encode4(0);
        data.Encode4(0);
        data.Encode4(chr.getItemEffect());
        data.Encode4(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        data.Encode2(chr.getPosition().x);
        data.Encode2(chr.getPosition().y);
        data.Encode1(chr.getStance()); // m_nMoveAction
        data.Encode2(chr.getFH());

        for (int i = 0; i < 4; i++) {
            MaplePet pet = chr.getPet(i);
            data.Encode1(pet != null ? 1 : 0); // 3 -> null
            if (pet == null) {
                break;
            }
            data.Encode4(0);
            data.EncodeBuffer(DataCPet.Init(pet));
        }

        int unk_count = 0;
        data.Encode1(unk_count);
        for (int i = 0; i < unk_count; i++) {
            // unk
        }
        data.Encode1(0);
        {
            // unk
        }
        data.Encode4(chr.getMount().getLevel()); // m_nTamingMobLevel
        data.Encode4(chr.getMount().getExp()); // m_nTamingMobExp
        data.Encode4(chr.getMount().getFatigue()); // m_nTamingMobFatigue
        data.Encode1((shop != null) ? shop.getGameType() : 0); // m_nMiniRoomType
        if (shop != null && shop.getGameType() != 0) {
            // AnnounceBox & Interaction : TODO Remove
            data.Encode4(((AbstractPlayerStore) shop).getObjectId()); // m_dwMiniRoomSN
            data.EncodeStr(shop.getDescription()); // m_sMiniRoomTitle
            data.Encode1((shop.getPassword().length() != 0) ? 1 : 0); // m_bPrivate
            data.Encode1(shop.getItemId() % 10); // m_nGameKind
            data.Encode1(shop.getSize()); // m_nCurUsers
            data.Encode1(shop.getMaxSize()); // m_nMaxUsers
            data.Encode1(shop.isOpen() ? 0 : 1); // m_bGameOn
        }
        boolean is_adboard = (chr.getChalkboard() != null) && (0 < chr.getChalkboard().length());
        data.Encode1(is_adboard ? 1 : 0); // m_bADBoardRemote
        if (is_adboard) {
            data.EncodeStr(chr.getChalkboard());
        }

        boolean unk_data_1 = false;
        boolean unk_data_2 = false;
        data.Encode1(unk_data_1 ? 1 : 0);
        if (unk_data_1) {
            data.Encode4(0);
            data.EncodeZeroBytes(16);
            data.Encode4(0);
        }
        data.Encode1(unk_data_2 ? 1 : 0);
        if (unk_data_2) {
            data.Encode4(0);
            data.EncodeZeroBytes(16);
            data.Encode4(0);
        }
        data.Encode1((0 < chr.getMarriageId()) ? 1 : 0);
        if (0 < chr.getMarriageId()) {
            data.Encode4(chr.getId()); // m_dwMarriageCharacterID
            data.Encode4(chr.getMarriageId()); // m_dwMarriagePairCharacterID
            data.Encode4(chr.getMarriageItemId()); // m_nWeddingRingID
        }
        data.Encode1(chr.getEffectMask()); // m_nDelayedEffectFlag
        {
            if ((chr.getEffectMask() & (0x08 | 0x10 | 0x20)) != 0) {
                data.Encode4(0); // delay
            }
        }
        data.Encode4(0);
        data.Encode4(0);
        return data.get().getBytes();
    }
}
