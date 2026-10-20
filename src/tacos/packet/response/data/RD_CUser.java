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

import odin.client.inventory.MapleInventoryType;
import tacos.client.TacosCharacter;
import odin.constants.GameConstants;
import odin.handling.world.guild.MapleGuild;
import odin.server.shops.ShopDispatch;
import tacos.client.TacosBuff;
import tacos.client.TacosBuff.Buff;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.response.ResCWvsContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import tacos.config.Config;
import tacos.config.Region;
import tacos.packet.ServerPacket;
import tacos.server.map.object.TacosPet;

/**
 *
 * @author Riremito
 */
public class RD_CUser {

    // CUser::OnEmotion
    public static byte[] Emotion(int expression) {
        ServerPacket data = new ServerPacket();
        data.Encode4(expression);
        data.Encode4(-1);
        data.Encode1(0);
        return data.getBytes();
    }

    // CPet::Init
    public static byte[] CPet_Init(TacosPet pet) {
        ServerPacket data = new ServerPacket();

        data.Encode4(pet.getPetItemId());
        data.EncodeStr(pet.getName());
        data.Encode8(pet.getUniqueId());
        data.Encode2(pet.getX());
        data.Encode2(pet.getY());
        data.Encode1(pet.getMoveAction());
        data.Encode2(pet.getFootholdId());

        if (Config.GreaterOrEqual(Region.THMS, 96)) {
            data.Encode1(0);
            data.Encode1(0);
        }

        return data.getBytes();
    }

    // CUserRemote::Init
    public static byte[] CUserRemote_Init(TacosCharacter chr) {
        ServerPacket data = new ServerPacket();

        Object shop = chr.getPlayerShop();
        MapleGuild guild = null;
        if (0 < chr.getGuildId()) {
            guild = chr.getWorld().getGuild().getGuild(chr.getGuildId());
        }

        boolean is_older_version = Config.LessOrEqual(Region.JMS, 131);

        data.Encode1(chr.getLevel(), Config.PostBB() || Config.GreaterOrEqual(Region.KMS, 84) || Config.GreaterOrEqual(Region.JMS, 180) || Config.GreaterOrEqual(Region.CMS, 85) || Config.GreaterOrEqual(Region.TWMS, 94) || Config.GreaterOrEqual(Region.THMS, 87) || Config.GreaterOrEqual(Region.GMS, 72) || Config.GreaterOrEqual(Region.MSEA, 100) || Config.GreaterOrEqual(Region.EMS, 54));
        data.EncodeStr(chr.getName()); // m_sCharacterName
        data.EncodeStr("", Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76));
        // guild.
        data.EncodeStr((guild != null) ? guild.getName() : ""); // m_sGuildName
        data.Encode2((guild != null) ? guild.getLogoBG() : 0); // m_nGuildMarkBg
        data.Encode1((guild != null) ? guild.getLogoBGColor() : 0); // m_nGuildMarkBgColor
        data.Encode2((guild != null) ? guild.getLogo() : 0); // m_nGuildMark
        data.Encode1((guild != null) ? guild.getLogoColor() : 0); // m_nGuildMarkColor

        data.EncodeBuffer(RD_CUser.SecondaryStat_EncodeForRemote_JMS147(chr)); // SecondaryStat::DecodeForRemote
        data.Encode2(chr.getJob(), !is_older_version); // m_nJobCode
        data.EncodeBuffer(RD_AvatarLook.Encode(chr)); // AvatarLook::AvatarLook

        data.Encode4(0); // m_dwDriverID
        data.Encode4(0, Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 180));// m_dwPassenserID
        data.Encode4(0, Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186));// nChocoCount
        data.Encode4(0, Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76));
        data.Encode4(0, Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76));
        data.Encode4(0, Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.GreaterOrEqual(Region.EMS, 76));
        data.Encode4(chr.getActiveEffectItem()); // nActiveEffectItemID
        data.Encode4(0, Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 180)); // m_nCompletedSetItemID
        // CMS73
        data.EncodeStr("", Config.Between(Region.CMS, 73, 85));
        data.EncodeStr("", Config.Between(Region.CMS, 73, 85));
        data.Encode2(0, Config.Between(Region.CMS, 73, 85));
        data.Encode2(0, Config.Between(Region.CMS, 73, 85));

        data.Encode4(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0); // m_nPortableChairID
        data.Encode2(chr.getX()); // x
        data.Encode2(chr.getY()); // y
        data.Encode1(chr.getMoveAction()); // m_nMoveAction
        data.Encode2(chr.getFootholdId()); // foothold
        // GMS95
        data.Encode1(0, Config.GreaterOrEqual(Region.GMS, 95)); // bShowAdminEffect

        // pet.
        for (int i = 0; i < 4; i++) {
            TacosPet pet = chr.getPetByIndex(i);
            data.Encode1(pet != null ? 1 : 0); // 3 -> null
            if (pet == null) {
                break;
            }
            data.EncodeBuffer(RD_CUser.CPet_Init(pet));
        }
        // riding.
        data.Encode4(chr.getMount().getLevel()); // m_nTamingMobLevel
        data.Encode4(chr.getMount().getExp()); // m_nTamingMobExp
        data.Encode4(chr.getMount().getFatigue()); // m_nTamingMobFatigue
        // miniroom.
        data.Encode1((shop != null) ? ShopDispatch.getGameType(shop) : 0); // m_nMiniRoomType
        if (shop != null && ShopDispatch.getGameType(shop) != 0) {
            data.Encode4(ShopDispatch.getObjectId(shop)); // m_dwMiniRoomSN
            data.EncodeStr(ShopDispatch.getDescription(shop)); // m_sMiniRoomTitle
            data.Encode1((ShopDispatch.getPassword(shop).length() != 0) ? 1 : 0); // m_bPrivate
            data.Encode1(ShopDispatch.getItemId(shop) % 10); // m_nGameKind
            data.Encode1(ShopDispatch.getSize(shop)); // m_nCurUsers
            data.Encode1(ShopDispatch.getMaxSize(shop)); // m_nMaxUsers
            data.Encode1(ShopDispatch.isOpen(shop) ? 0 : 1); // m_bGameOn
        }
        // adboard.
        boolean is_adboard = (chr.getADBoard() != null) && (0 < chr.getADBoard().length());
        data.Encode1(is_adboard ? 1 : 0); // m_bADBoardRemote
        if (is_adboard) {
            data.EncodeStr(chr.getADBoard());
        }
        // couple.
        boolean is_couple = false;
        data.Encode1(is_couple ? 1 : 0);
        if (is_couple) {
            data.Encode8(0); // m_liCoupleItemSN
            data.Encode8(0); // m_liPairItemSN
            data.Encode4(0); // nItemID
        }
        // friendship.
        boolean is_friend = false;
        data.Encode1(is_friend ? 1 : 0);
        if (is_friend) {
            data.Encode8(0); // m_liFriendshipItemSN
            data.Encode8(0); // m_liFriendshipPairItemSN
            data.Encode4(0); // nItemID
        }
        // marriage.
        data.Encode1((0 < chr.getMarriageId()) ? 1 : 0);
        if (0 < chr.getMarriageId()) {
            data.Encode4(chr.getId()); // m_dwMarriageCharacterID
            data.Encode4(chr.getMarriageId()); // m_dwMarriagePairCharacterID
            data.Encode4(chr.getMarriageItemId()); // m_nWeddingRingID
        }
        // effect.
        data.Encode1(chr.getEffectMask()); // m_nDelayedEffectFlag
        // unk.
        data.Encode1(0, Config.GreaterOrEqual(Region.KMS, 92) || Config.Equal(Region.JMS, 180) || Config.Equal(Region.CMS, 73));

        // new year card.
        boolean is_new_year_card = false;
        data.Encode1(is_new_year_card ? 1 : 0, Config.GreaterOrEqual(Region.GMS, 95));
        if (is_new_year_card) {
            int new_year_card_count = 0;
            data.Encode4(new_year_card_count);
            for (int i = 0; i < new_year_card_count; i++) {
                data.Encode4(0); // id.
            }
        }

        data.Encode4(0, (Config.PostBB() || Config.GreaterOrEqual(Region.JMS, 186)) && !Config.Equal(Region.KMST, 330)); // m_nPhase, not in KMST330
        return data.getBytes();
    }

    public static byte[] CUserRemote_Init_JMS302(TacosCharacter chr) {
        MapleGuild guild = null;
        Object shop = chr.getPlayerShop();
        if (0 < chr.getGuildId()) {
            guild = chr.getWorld().getGuild().getGuild(chr.getGuildId());
        }

        ServerPacket data = new ServerPacket();

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
        data.EncodeBuffer(RD_CUser.SecondaryStat_EncodeForRemote_JMS302(chr));
        data.Encode2(0);
        data.Encode2(0);
        data.EncodeBuffer(RD_AvatarLook.Encode(chr));
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
        data.Encode2(chr.getX());
        data.Encode2(chr.getY());
        data.Encode1(chr.getMoveAction()); // m_nMoveAction
        data.Encode2(chr.getFootholdId());

        for (int i = 0; i < 4; i++) {
            TacosPet pet = chr.getPetByIndex(i);
            data.Encode1(pet != null ? 1 : 0); // 3 -> null
            if (pet == null) {
                break;
            }
            data.Encode4(0);
            data.EncodeBuffer(RD_CUser.CPet_Init(pet));
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
        data.Encode1((shop != null) ? ShopDispatch.getGameType(shop) : 0); // m_nMiniRoomType
        if (shop != null && ShopDispatch.getGameType(shop) != 0) {
            // AnnounceBox & Interaction : TODO Remove
            data.Encode4(ShopDispatch.getObjectId(shop)); // m_dwMiniRoomSN
            data.EncodeStr(ShopDispatch.getDescription(shop)); // m_sMiniRoomTitle
            data.Encode1((ShopDispatch.getPassword(shop).length() != 0) ? 1 : 0); // m_bPrivate
            data.Encode1(ShopDispatch.getItemId(shop) % 10); // m_nGameKind
            data.Encode1(ShopDispatch.getSize(shop)); // m_nCurUsers
            data.Encode1(ShopDispatch.getMaxSize(shop)); // m_nMaxUsers
            data.Encode1(ShopDispatch.isOpen(shop) ? 0 : 1); // m_bGameOn
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

    // SecondaryStat::DecodeForRemote order, GMS95
    // JMS147 : no rShadowPartner, stats after DefenseState and DojangShield are not used (disabled by OpsSecondaryStat).
    private static final OpsSecondaryStat[] REMOTE_STAT_DECODE_ORDER = {
        OpsSecondaryStat.CTS_Speed,
        OpsSecondaryStat.CTS_ComboCounter,
        OpsSecondaryStat.CTS_WeaponCharge,
        OpsSecondaryStat.CTS_Stun,
        OpsSecondaryStat.CTS_Darkness,
        OpsSecondaryStat.CTS_Seal,
        OpsSecondaryStat.CTS_Weakness,
        OpsSecondaryStat.CTS_Curse,
        OpsSecondaryStat.CTS_Poison,
        OpsSecondaryStat.CTS_ShadowPartner,
        OpsSecondaryStat.CTS_DarkSight,
        OpsSecondaryStat.CTS_SoulArrow,
        OpsSecondaryStat.CTS_Morph,
        OpsSecondaryStat.CTS_Ghost,
        OpsSecondaryStat.CTS_Attract,
        OpsSecondaryStat.CTS_SpiritJavelin,
        OpsSecondaryStat.CTS_BanMap,
        OpsSecondaryStat.CTS_Barrier,
        OpsSecondaryStat.CTS_DojangShield,
        OpsSecondaryStat.CTS_ReverseInput,
        OpsSecondaryStat.CTS_RespectPImmune,
        OpsSecondaryStat.CTS_RespectMImmune,
        OpsSecondaryStat.CTS_DefenseAtt,
        OpsSecondaryStat.CTS_DefenseState,
        OpsSecondaryStat.CTS_DojangBerserk,
        OpsSecondaryStat.CTS_DojangInvincible,
        OpsSecondaryStat.CTS_WindWalk,
        OpsSecondaryStat.CTS_RepeatEffect,
        OpsSecondaryStat.CTS_StopPortion,
        OpsSecondaryStat.CTS_StopMotion,
        OpsSecondaryStat.CTS_Fear,
        OpsSecondaryStat.CTS_MagicShield,
        OpsSecondaryStat.CTS_Flying,
        OpsSecondaryStat.CTS_Frozen,
        OpsSecondaryStat.CTS_SuddenDeath,
        OpsSecondaryStat.CTS_FinalCut,
        OpsSecondaryStat.CTS_Cyclone,
        OpsSecondaryStat.CTS_Sneak,
        OpsSecondaryStat.CTS_MorewildDamageUp,
        OpsSecondaryStat.CTS_Mechanic,
        OpsSecondaryStat.CTS_DarkAura,
        OpsSecondaryStat.CTS_BlueAura,
        OpsSecondaryStat.CTS_YellowAura,
        OpsSecondaryStat.CTS_BlessingArmor
    };

    // buffs shown to other users.
    public static List<Buff> getRemoteBuffs(List<Buff> all) {
        int buff_mask_length = TacosBuff.getBuffBuffer().length;
        List<Buff> buffs = new ArrayList<>();
        for (Buff buff : all) {
            // not active or disabled in this region.
            if (buff.buff_id == 0 || buff.ops.get() < 0 || buff_mask_length <= buff.ops.getNl()) {
                continue;
            }
            if (buff.ops.isTwoState() || Arrays.asList(REMOTE_STAT_DECODE_ORDER).contains(buff.ops)) {
                buffs.add(buff);
            }
        }
        return buffs;
    }

    public static int[] getRemoteBuffMask(List<Buff> buffs) {
        int[] buff_mask = TacosBuff.getBuffBuffer();
        for (Buff buff : buffs) {
            buff_mask[buff.ops.getNl()] |= buff.ops.getNr();
        }
        return buff_mask;
    }

    // SecondaryStat::DecodeForRemote
    public static byte[] SecondaryStat_EncodeForRemote_JMS147(TacosCharacter chr) {
        return SecondaryStat_EncodeForRemote_JMS147(chr, chr.getBuff().getAll());
    }

    public static byte[] SecondaryStat_EncodeForRemote_JMS147(TacosCharacter chr, List<Buff> all) {
        ServerPacket data = new ServerPacket();

        // JMS131
        if (Config.LessOrEqual(Region.JMS, 131)) {
            for (int empty_mask : TacosBuff.getBuffBuffer()) {
                data.Encode4(empty_mask); // uFlagTemp
            }
            return data.getBytes();
        }

        List<Buff> buffs = getRemoteBuffs(all);
        int[] buff_mask = getRemoteBuffMask(buffs);
        for (int index = 0; index < buff_mask.length; index++) {
            data.Encode4(buff_mask[buff_mask.length - 1 - index]); // uFlagTemp
        }

        for (OpsSecondaryStat ops : REMOTE_STAT_DECODE_ORDER) {
            for (Buff buff : buffs) {
                if (buff.ops != ops) {
                    continue;
                }
                switch (ops) {
                    case CTS_Speed, CTS_ComboCounter, CTS_Cyclone -> {
                        data.Encode1(buff.buff_effect); // n
                    }
                    case CTS_Morph, CTS_Ghost -> {
                        data.Encode2(buff.buff_effect); // n
                    }
                    case CTS_SpiritJavelin, CTS_RespectPImmune, CTS_RespectMImmune, CTS_DefenseAtt, CTS_DefenseState, CTS_MagicShield -> {
                        data.Encode4(buff.buff_effect); // n
                    }
                    case CTS_Poison -> {
                        data.Encode2(buff.buff_effect); // n
                        data.Encode4(buff.buff_id); // r
                    }
                    case CTS_ShadowPartner -> {
                        data.Encode4(buff.buff_id, Config.PostBB()); // r, not in JMS147
                    }
                    case CTS_DarkSight, CTS_SoulArrow, CTS_DojangBerserk, CTS_DojangInvincible, CTS_WindWalk, CTS_Flying, CTS_Sneak, CTS_MorewildDamageUp, CTS_BlessingArmor -> {
                        // no data.
                    }
                    default -> {
                        data.Encode4(buff.buff_id); // r
                    }
                }
            }
        }

        // JMS147
        data.Encode1(0); // nDefenseAtt_CS
        data.Encode1(0); // nDefenseState_CS

        for (Buff buff : buffs) {
            if (buff.ops.isTwoState()) {
                ResCWvsContext.EncodeTwoState(data, chr, buff);
            }
        }
        return data.getBytes();
    }

    public static byte[] SecondaryStat_EncodeForRemote_JMS302(TacosCharacter chr) {
        ServerPacket data = new ServerPacket();

        data.EncodeZeroBytes(32);
        data.Encode1(0);
        data.Encode1(0);
        data.Encode1(0);
        return data.getBytes();
    }
}
