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
package tacos.packet.response.data;

import odin.client.MapleCharacter;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import tacos.config.Region;
import tacos.config.Config;
import tacos.config.Version;
import odin.constants.GameConstants;
import odin.handling.world.OdinWorld;
import odin.handling.world.guild.MapleGuild;
import tacos.packet.ServerPacket;
import tacos.packet.response.struct.Structure;
import odin.server.shops.AbstractPlayerStore;
import odin.server.shops.IMaplePlayerShop;

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
        } else if (Config.JMS164orLater()) {
            data.Encode1(chr.getLevel());
        }
        data.EncodeStr(chr.getName());
        if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76)) {
            data.EncodeStr("");
        }
        // guild
        MapleGuild gs = null;
        if (0 < chr.getGuildId()) {
            gs = OdinWorld.Guild.getGuild(chr.getGuildId());
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
        if (Config.JMS164orLater()) {
            data.Encode8(0); // buff mask.
        }
        data.Encode8(0); // buff mask.
        if (Config.JMS164orLater()) {
            if (Version.GreaterOrEqual(Region.JMS, 187)) {
                data.Encode4(0); // buff mask.
            }
            data.Encode1(0); //start of energy charge
            data.Encode1(0);
            data.Encode2(chr.getJob());
        }
        data.EncodeBuffer(DataAvatarLook.Encode(chr));
        data.Encode4(0); //this is CHARID to follow
        if (Config.JMS164orLater()) {
            data.Encode4(0); //probably charid following
            data.Encode4(0);
            if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76)) {
                data.Encode4(0);
                data.Encode4(0);
                data.Encode4(0);
            }
            data.Encode4(0);
        }
        data.Encode4(chr.getActiveEffectItem());
        data.Encode4(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        data.Encode2(chr.getPosition().x);
        data.Encode2(chr.getPosition().y);
        data.Encode1(chr.getStance());
        data.Encode2(0); // FH
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            data.Encode1(0);// bShowAdminEffect
        }
        data.Encode1(0); // pet size
        data.Encode4(chr.getMount().getLevel()); // mount lvl
        data.Encode4(chr.getMount().getExp()); // exp
        data.Encode4(chr.getMount().getFatigue()); // tiredness
        // MiniRoomBalloon (ゲーム) 1 byte flag + data
        data.EncodeBuffer(Structure.AnnounceBox(chr)); // m_nMiniRoomType
        // ADBoardBalloon (黒板) 1 byte flag + data
        {
            data.Encode1(chr.getADBoard() != null && chr.getADBoard().length() > 0 ? 1 : 0); // m_bADBoardRemote
            if (chr.getADBoard() != null && chr.getADBoard().length() > 0) {
                data.EncodeStr(chr.getADBoard());
            }
        }
        data.Encode1(0); // CoupleRecord, count4 -> buf0x10 4
        data.Encode1(0); // FriendRecord, count4 -> buf0x10 4
        // MarriageRecord 1 byte flag + data
        {
            data.Encode1(0); // MarriageRecord
        }
        data.Encode1(chr.getEffectMask()); // Effect
        if (Version.GreaterOrEqual(Region.GMS, 95)) {
            data.Encode1(0); // NewYearCardRecord
        }
        data.Encode4(0); // not in KMST, in GMS v95: m_nPhase
        // 特殊マップ専用
        // MonsterCarnival
        if (chr.checkSpecificMap(980000000, 1000) || chr.checkSpecificMap(980030000, 1000)) {
            data.Encode1((chr.getCarnivalParty() != null) ? chr.getCarnivalParty().getTeam() : 0); // sub_5CD27E
        } // Coconut
        else if (chr.checkSpecificMap(109080000, 1000)) {
            data.Encode1(chr.getCoconutTeam()); // 0059F0ED
        }

        return data.getBytes();
    }

    public static byte[] Init_JMS147(MapleCharacter chr) {
        MapleGuild guild = null;
        IMaplePlayerShop shop = chr.getPlayerShop();
        if (0 < chr.getGuildId()) {
            guild = OdinWorld.Guild.getGuild(chr.getGuildId());
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
        data.Encode4(chr.getActiveEffectItem());
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
        boolean is_adboard = (chr.getADBoard() != null) && (0 < chr.getADBoard().length());
        data.Encode1(is_adboard ? 1 : 0); // m_bADBoardRemote
        if (is_adboard) {
            data.EncodeStr(chr.getADBoard());
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
        return data.getBytes();
    }

    public static byte[] Init_JMS302(MapleCharacter chr) {
        MapleGuild guild = null;
        IMaplePlayerShop shop = chr.getPlayerShop();
        if (0 < chr.getGuildId()) {
            guild = OdinWorld.Guild.getGuild(chr.getGuildId());
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
        data.Encode4(chr.getActiveEffectItem());
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
        boolean is_adboard = (chr.getADBoard() != null) && (0 < chr.getADBoard().length());
        data.Encode1(is_adboard ? 1 : 0); // m_bADBoardRemote
        if (is_adboard) {
            data.EncodeStr(chr.getADBoard());
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
        return data.getBytes();
    }
}
