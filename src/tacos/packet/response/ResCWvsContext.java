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
package tacos.packet.response;

import odin.client.MapleBuffStat;
import odin.client.MapleCharacter;
import odin.client.MapleDisease;
import odin.client.MapleQuestStatus;
import odin.client.MapleStat;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MapleMount;
import odin.client.inventory.MaplePet;
import tacos.config.Region;
import tacos.config.ServerConfig;
import tacos.config.Version;
import odin.constants.GameConstants;
import tacos.shared.SharedDate;
import tacos.debug.DebugLogger;
import tacos.network.MaplePacket;
import odin.handling.channel.MapleGuildRanking;
import odin.handling.world.MapleParty;
import odin.handling.world.MaplePartyCharacter;
import odin.handling.world.PartyOperation;
import odin.handling.world.World;
import odin.handling.world.family.MapleFamily;
import odin.handling.world.family.MapleFamilyBuff;
import odin.handling.world.family.MapleFamilyCharacter;
import odin.handling.world.guild.MapleBBSThread;
import odin.handling.world.guild.MapleGuild;
import odin.handling.world.guild.MapleGuildAlliance;
import odin.handling.world.guild.MapleGuildCharacter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsBodyPart;
import tacos.packet.ops.arg.ArgBroadcastMsg;
import tacos.packet.ops.OpsChangeStat;
import tacos.packet.ops.OpsEntrustedShop;
import tacos.packet.ops.OpsMapTransfer;
import tacos.packet.ops.arg.ArgFriend;
import tacos.packet.ops.arg.ArgMessage;
import tacos.packet.ops.OpsSecondaryStat;
import tacos.packet.ops.OpsShopScanner;
import tacos.packet.request.sub.ReqSub_UserConsumeCashItemUseRequest;
import tacos.packet.response.data.DataCUIUserInfo;
import tacos.packet.response.data.DataCWvsContext;
import tacos.packet.response.data.DataSecondaryStat;
import tacos.packet.response.data.DataGW_CharacterStat;
import tacos.packet.response.data.DataGW_ItemSlotBase;
import tacos.packet.response.struct.InvOp;
import tacos.packet.response.struct.TestHelper;
import tacos.packet.response.wrapper.WrapCWvsContext;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleStatEffect;
import odin.tools.Pair;
import tacos.packet.response.data.DataAvatarLook;

/**
 *
 * @author Riremito
 */
public class ResCWvsContext {

    // CWvsContext::OnInventoryOperation
    public static MaplePacket InventoryOperation(boolean unlock, InvOp io) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryOperation);
        sp.Encode1(unlock ? 1 : 0);// m_bExclRequestSent, unlock
        sp.Encode1((io == null) ? 0 : io.get().size());

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.Equal(Region.KMST, 391) || Version.GreaterOrEqual(Region.KMS, 197) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0); // unused
        }

        boolean equip_changed = false;
        if (io != null) {
            for (InvOp.InvData v : io.get()) {
                sp.Encode1(v.mode);
                switch (v.mode) {
                    // add
                    case 0: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.item.getPosition());
                        sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(v.item));
                        break;
                    }
                    // update
                    case 1: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.item.getPosition());
                        sp.Encode2(v.item.getQuantity());
                        break;
                    }
                    // move
                    case 2: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.src);
                        sp.Encode2(v.dst);
                        // 装備変更
                        if (v.type.getType() == 1 && (v.src < 0 || v.dst < 0)) {
                            equip_changed = true;
                        }
                        break;
                    }
                    // remove
                    case 3: {
                        sp.Encode1(v.type.getType());
                        sp.Encode2(v.src);
                        // 装備変更
                        if (v.type.getType() == 1 && (v.src < 0)) {
                            equip_changed = true;
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }

            if (equip_changed) {
                sp.Encode1(0); // for CUserLocal::SetSecondaryStatChangedPoint
            }
        }
        return sp.get();
    }

    // CWvsContext::OnChangeSkillRecordResult
    public static final MaplePacket updateSkill(int skillid, int level, int masterlevel, long expiration) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ChangeSkillRecordResult);
        sp.Encode1(1);
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        sp.Encode2(1);
        sp.Encode4(skillid);
        sp.Encode4(level);
        sp.Encode4(masterlevel);
        if (ServerConfig.JMS164orLater()) {
            sp.Encode8(SharedDate.getMagicalExpirationDate());
        }
        sp.Encode1(4);
        return sp.get();
    }

    // CWvsContext::OnTemporaryStatSet
    public static final MaplePacket TemporaryStatSet(MapleStatEffect effect) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);
        sp.EncodeBuffer(DataSecondaryStat.EncodeForLocal(effect));
        sp.Encode2(0); // delay
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104) || Version.GreaterOrEqual(Region.GMS, 111)) {
            sp.Encode1(0);
        }
        sp.Encode1(0); // CUserLocal::SetSecondaryStatChangedPoint
        return sp.get();
    }

    public static MaplePacket cancelBuff(List<MapleBuffStat> statups, MapleStatEffect mse) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatReset);
        int buff_mask[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        ArrayList<Pair<OpsSecondaryStat, Integer>> pss_array = mse.getOss();
        for (Pair<OpsSecondaryStat, Integer> pss : pss_array) {
            buff_mask[pss.getLeft().getN()] |= (1 << pss.getLeft().get());
        }
        if (Version.GreaterOrEqual(Region.EMS, 89)) {
            sp.Encode4(buff_mask[8]);
        }
        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.TWMS, 148) || Version.GreaterOrEqual(Region.CMS, 104)) {
            sp.Encode4(buff_mask[7]);
            sp.Encode4(buff_mask[6]);
            sp.Encode4(buff_mask[5]);
        }
        // JMS v187+
        if (Version.PostBB()) {
            if (!Region.IsIMS() && !Region.IsTHMS()) {
                sp.Encode4(buff_mask[4]);
            }
        }
        if (ServerConfig.JMS146orLater()) {
            sp.Encode4(buff_mask[3]);
            sp.Encode4(buff_mask[2]);
        }
        if (ServerConfig.JMS146orLater()) {
            sp.Encode4(buff_mask[1]);
            sp.Encode4(buff_mask[0]);
        } else {
            // JMS v131
            sp.Encode4(buff_mask[0]);
            sp.Encode4(buff_mask[1]);
        }
        sp.Encode1(0);
        return sp.get();
    }

    // warpper
    public static MaplePacket giveBuff(int buffid, int bufflength, List<Pair<MapleBuffStat, Integer>> statups, MapleStatEffect effect) {
        return TemporaryStatSet(effect);
    }

    // CWvsContext::OnInventoryGrow
    // CWvsContext::OnStatChanged
    public static final MaplePacket StatChanged(MapleCharacter chr, int unlock, int statmask) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_StatChanged);
        // 0 = lock   -> do not clear lock flag
        // 1 = unlock -> clear lock flag
        sp.Encode1(unlock); // CWvsContext->bExclRequestSent
        if ((Region.IsEMS() && !Version.GreaterOrEqual(Region.EMS, 89)) || Version.Between(Region.TWMS, 74, 93)) {
            sp.Encode1(0); // EMS v55
        }
        sp.EncodeBuffer(DataGW_CharacterStat.EncodeChangeStat(chr, statmask));
        if (Version.PreBB()) {
            if (Region.IsJMS()) {
                // Pet
                if ((statmask & OpsChangeStat.CS_PETSN.get()) > 0) {
                    int v5 = 0; // CVecCtrlUser::AddMovementInfo
                    sp.Encode1(v5);
                }
            }
            if (Version.GreaterOrEqual(Region.GMS, 91)) {
                sp.Encode1(0); // not 0 -> Encode1
            }
        } else {
            // v188+
            sp.Encode1(0); // not 0 -> Encode1
            sp.Encode1(0); // not 0 -> Encode4, Encode4
        }
        return sp.get();
    }

    // CWvsContext::OnForcedStatReset
    public static final MaplePacket ForcedStatReset() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ForcedStatReset);
        return sp.get();
    }

    public static final MaplePacket Message(ArgMessage ma) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Message);
        sp.Encode1(ma.mt.get());
        switch (ma.mt) {
            case MS_DropPickUpMessage: {
                sp.Encode1(ma.dt.get());
                switch (ma.dt) {
                    case PICKUP_ITEM: {
                        sp.Encode4(ma.ItemID);
                        sp.Encode4(ma.Inc_ItemCount);
                        break;
                    }
                    case PICKUP_MESO: {
                        if (ServerConfig.JMS164orLater()) {
                            sp.Encode1(0);
                        }
                        sp.Encode4(ma.Inc_Meso);
                        if (Version.LessOrEqual(Region.JMS, 131)) {
                            sp.Encode2(0); // Internet cafe bonus
                        } else {
                            sp.Encode4(0);
                        }
                        break;
                    }
                    case PICKUP_MONSTER_CARD: {
                        sp.Encode4(ma.ItemID);
                        break;
                    }
                    case PICKUP_INVENTORY_FULL:
                    case PICKUP_UNAVAILABLE:
                    case PICKUP_BROKEN: {
                        sp.Encode4(0);
                        sp.Encode4(0);
                        break;
                    }
                    default: {
                        DebugLogger.ErrorLog("Unknown DropPickUp Type" + ma.dt.get());
                        break;
                    }
                }
                break;
            }
            // updateQuest, updateQuestMobKills
            case MS_QuestRecordMessage: {
                sp.Encode2(ma.QuestID);
                sp.Encode1(ma.qt.get());
                switch (ma.qt) {
                    case QUEST_START: {
                        sp.Encode1(0); // 0 or not
                        break;
                    }
                    case QUEST_UPDATE: {
                        sp.EncodeStr(ma.str);
                        break;
                    }
                    case QUEST_COMPLETE: {
                        sp.Encode8(System.currentTimeMillis());
                        break;
                    }
                    default: {
                        DebugLogger.ErrorLog("Unknown QuestRecord Type" + ma.dt.get());
                        break;
                    }
                }
                break;
            }
            // itemExpired
            case MS_CashItemExpireMessage: {
                sp.Encode4(ma.ItemID);
                break;
            }
            case MS_IncEXPMessage: {
                sp.Encode1(ma.Inc_EXP_TextColor);
                sp.Encode4(ma.Inc_EXP);
                sp.Encode1(ma.InChat); // bOnQuest
                sp.Encode4(0);
                sp.Encode1(ma.Inc_EXP_MobEventBonusPercentage); // nMobEventBonusPercentage
                sp.Encode1(0);
                if (Region.IsTHMS() && Version.getVersion() == 87) {
                    sp.Encode4(ma.Inc_EXP_WeddingBonus); // Wedding Bonus EXP(+%d)
                    sp.Encode4(0); // Party Ring Bonus EXP(+%d)
                    sp.Encode4(0); // EXP Bonus Internet Cafe(+ %d)
                    sp.Encode4(0); // Rainbow Week Bonus EXP(+%d)
                } else if (Version.GreaterOrEqual(Region.GMS, 111)) {
                    sp.Encode4(0);
                } else {
                    sp.Encode4(ma.Inc_EXP_WeddingBonus); // 結婚ボーナス経験値
                    sp.Encode4(0); // グループリングボーナスEXP (?)
                }
                if (0 < ma.Inc_EXP_MobEventBonusPercentage) {
                    sp.Encode1(ma.Inc_EXP_PlayTimeHour);
                }
                if (ma.InChat != 0) {
                    sp.Encode1(0);
                }
                sp.Encode1(0); // nPartyBonusEventRate
                sp.Encode4(ma.Inc_EXP_PartyBonus); // グループボーナス経験値
                sp.Encode4(ma.Inc_EXP_EquipmentBonus); // アイテム装着ボーナス経験値
                sp.Encode4(0); // not used
                sp.Encode4(ma.Inc_EXP_RainbowWeekBonus); // レインボーウィークボーナス経験値

                if (Version.GreaterOrEqual(Region.GMS, 111)) {
                    sp.Encode1(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    if (ma.InChat != 0) {
                        sp.Encode4(0);
                    }
                    break;
                }

                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                }
                if (ServerConfig.JMS194orLater()) {
                    sp.Encode1(0); // 0 or not
                }
                break;
            }
            // getSPMsg
            case MS_IncSPMessage: {
                sp.Encode2(ma.JobID);
                sp.Encode1(ma.Inc_SP);
                break;
            }
            // getShowFameGain
            case MS_IncPOPMessage: {
                sp.Encode4(ma.Inc_Fame);
                break;
            }
            // showMesoGain
            case MS_IncMoneyMessage: {
                sp.Encode4(ma.Inc_Meso);
                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode4(-1); // 別の数値だとメッセージ非表示
                }
                break;
            }
            // getGPMsg
            case MS_IncGPMessage: {
                sp.Encode4(ma.Inc_GP);
                break;
            }
            // getStatusMsg
            case MS_GiveBuffMessage: {
                sp.Encode4(ma.ItemID);
                break;
            }
            case MS_GeneralItemExpireMessage: {
                break;
            }
            // showQuestMsg
            case MS_SystemMessage: {
                sp.EncodeStr(ma.str);
                break;
            }
            // updateInfoQuest
            case MS_QuestRecordExMessage: {
                sp.Encode2(ma.QuestID);
                sp.EncodeStr(ma.str);
                break;
            }
            case MS_ItemProtectExpireMessage: {
                break;
            }
            case MS_ItemExpireReplaceMessage: {
                break;
            }
            case MS_SkillExpireMessage: {
                break;
            }
            // updateBeansMSG, GainTamaMessage
            case MS_JMS_Pachinko: {
                sp.Encode4(ma.Inc_Tama);
                break;
            }
            default: {
                break;
            }
        }
        return sp.get();
    }

    public static MaplePacket GatherItemResult(byte type) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GatherItemResult);

        sp.Encode1(0); // unused
        sp.Encode1(type);
        return sp.get();
    }

    public static MaplePacket SortItemResult(byte type) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SortItemResult);

        sp.Encode1(0); // unused
        sp.Encode1(type);
        return sp.get();
    }

    public static final MaplePacket CharacterInfo(MapleCharacter player, boolean isSelf) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_CharacterInfo);
        boolean pet_summoned = false;
        for (final MaplePet pet : player.getPets()) {
            if (pet.getSummoned()) {
                pet_summoned = true;
                break;
            }
        }
        sp.Encode4(player.getId());
        sp.Encode1(player.getLevel());
        sp.Encode2(player.getJob());

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
            sp.Encode4(player.getFame());
        } else {
            sp.Encode2(player.getFame());
        }

        if (ServerConfig.JMS147orLater()) {
            sp.Encode1(player.getMarriageId() > 0 ? 1 : 0); // heart red or gray
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

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }

        sp.EncodeStr(sCommunity);
        if (ServerConfig.JMS147orLater()) {
            sp.EncodeStr(sAlliance);
        }
        // Pre-BB
        if (Version.Between(Region.JMS, 180, 186)) {
            sp.Encode4(0);
            sp.Encode4(0);
        }
        if (Version.PostBB()) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }
        sp.Encode1((player.getPet(0) != null) ? 1 : 0); // pet button clickable
        if (Version.LessOrEqual(Region.JMS, 131)) {
            // inlined?
            if (player.getPet(0) != null) {
                sp.EncodeBuffer(DataCUIUserInfo.SetPetInfo_JMS131(player, player.getPet(0)));
            }
        } else {
            // CUIUserInfo::SetPetInfo
            sp.EncodeBuffer(DataCUIUserInfo.SetPetInfo(player));
        }

        // CUIUserInfo::SetTamingMobInfo
        IItem inv_mount = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
        boolean TamingMobEnabled = false;
        final MapleMount tm = player.getMount();
        if (tm != null && inv_mount != null) {
            TamingMobEnabled = MapleItemInformationProvider.getInstance().getReqLevel(inv_mount.getItemId()) <= player.getLevel();
        }
        sp.Encode1(TamingMobEnabled ? 1 : 0);
        if (tm != null && TamingMobEnabled) {
            sp.Encode4(tm.getLevel());
            sp.Encode4(tm.getExp());
            sp.Encode4(tm.getFatigue());
        }

        // CUIUserInfo::SetWishItemInfo
        final int wishlistSize = player.getWishlistSize();
        sp.Encode1(wishlistSize);
        if (wishlistSize > 0) {
            // CInPacket::DecodeBuffer(v4, iPacket, 4 * wishlistSize);
            final int[] wishlist = player.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                sp.Encode4(wishlist[x]);
            }
        }
        if (ServerConfig.JMS147orLater()) {
            // Monster Book (JMS)
            sp.EncodeBuffer(player.getMonsterBook().MonsterBookInfo(player.getMonsterBookCover()));
        }
        if (ServerConfig.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 84)) {
            // MedalAchievementInfo::Decode
            IItem inv_medal = player.getInventory(MapleInventoryType.EQUIPPED).getItem(OpsBodyPart.BP_MEDAL.getSlot());
            sp.Encode4(inv_medal == null ? 0 : inv_medal.getItemId());
            List<Integer> medalQuests = new ArrayList<Integer>();
            List<MapleQuestStatus> completed = player.getCompletedQuests();
            for (MapleQuestStatus q : completed) {
                if (q.getQuest().getMedalItem() > 0 && GameConstants.getInventoryType(q.getQuest().getMedalItem()) == MapleInventoryType.EQUIP) {
                    //chair kind medal viewmedal is weird
                    medalQuests.add(q.getQuest().getId());
                }
            }
            sp.Encode2(medalQuests.size());
            for (int x : medalQuests) {
                sp.Encode2(x);
                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode8(0);
                }
            }
            // JMS v180-v186, v187以降消滅
            if (Version.PreBB() && Region.IsJMS()) {
                // Chair List
                sp.Encode4(player.getInventory(MapleInventoryType.SETUP).list().size());
                // CInPacket::DecodeBuffer(v4, iPacket, 4 * chairs);
                for (IItem chair : player.getInventory(MapleInventoryType.SETUP).list()) {
                    sp.Encode4(chair.getItemId());
                }
            }

            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode1(0);
                sp.Encode1(0);
                for (int i = 0; i < 3; i++) {
                    sp.Encode4(0);
                    sp.Encode4(0);
                    sp.Encode4(0);
                }
            }

        }
        return sp.get();
    }

    // CWvsContext::OnBroadcastMsg
    public static MaplePacket BroadcastMsg(ArgBroadcastMsg bma) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        sp.Encode1(bma.bm.get());

        switch (bma.bm) {
            case BM_NOTICE: // 青文字 [告知事項]
            case BM_ALERT: // ダイアログ
            case BM_EVENT: // ピンク文字
            {
                sp.EncodeStr(bma.message);
                break;
            }
            case BM_SLIDE: // 画面上部の横スクロールメッセージ
            {
                boolean show_msg = bma.message.length() != 0;
                sp.Encode1(show_msg ? 1 : 0);
                if (show_msg) {
                    sp.EncodeStr(bma.message);
                }
                break;
            }
            case BM_NOTICEWITHOUTPREFIX: // 青文字, アイテム情報
            {
                sp.EncodeStr(bma.message);
                sp.Encode4(bma.item_id);
                break;
            }
            case BM_SPEAKERCHANNEL: // 5070000, メガホン
            {
                String text = ReqSub_UserConsumeCashItemUseRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
                sp.EncodeStr(text);
                break;
            }
            case BM_SPEAKERWORLD: // 5071000, 拡声器
            case BM_HEARTSPEAKER: // 5073000, ハート拡声器
            case BM_SKULLSPEAKER: // 5074000, ドクロ拡声器
            {
                String text = ReqSub_UserConsumeCashItemUseRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
                int channel = bma.chr.getClient().getChannel() - 1;
                sp.EncodeStr(text);
                sp.Encode1(channel);
                sp.Encode1(bma.ear);
                break;
            }
            case BM_ITEMSPEAKER: // 5076000, アイテム拡声器
            {
                String text = ReqSub_UserConsumeCashItemUseRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
                int channel = bma.chr.getClient().getChannel() - 1;
                boolean show_item = bma.item != null;
                sp.EncodeStr(text);
                sp.Encode1(channel);
                sp.Encode1(bma.ear);
                sp.Encode1(show_item ? 1 : 0);
                if (show_item) {
                    sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(bma.item));
                }
                break;
            }
            case MEGAPHONE_TRIPLE: // 5077000, 三連拡声器
            {
                String name = ReqSub_UserConsumeCashItemUseRequest.MegaphoneGetSenderName(bma.chr);
                int channel = bma.chr.getClient().getChannel() - 1;
                String text1 = bma.messages.get(0); // ?_?

                sp.EncodeStr(name + " : " + text1);
                sp.Encode1(bma.messages.size());
                for (int i = 1; i < bma.messages.size(); i++) {
                    sp.EncodeStr(name + " : " + bma.messages.get(i));
                }
                sp.Encode1(channel);
                sp.Encode1(bma.ear);
                break;
            }
            case BM_GACHAPONANNOUNCE: {
                String text = bma.chr.getName() + " : " + bma.message;
                sp.EncodeStr(text);
                sp.Encode4(bma.gashapon_type); // 緑 (0) or 茶色 (-1)
                sp.EncodeBuffer(DataGW_ItemSlotBase.Encode(bma.item));
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    public static MaplePacket showNotes(ResultSet notes, int count) throws SQLException {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MemoResult);

        sp.Encode1(3);
        sp.Encode1(count);
        for (int i = 0; i < count; i++) {
            sp.Encode4(notes.getInt("id"));
            sp.EncodeStr(notes.getString("from"));
            sp.EncodeStr(notes.getString("message"));
            sp.Encode8(TestHelper.getKoreanTimestamp(notes.getLong("timestamp")));
            sp.Encode1(notes.getInt("gift"));
            notes.next();
        }
        return sp.get();
    }

    public static MaplePacket fishingUpdate(byte type, int id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_Fishing_BoardUpdate);
        sp.Encode1(type);
        sp.Encode4(id);
        return sp.get();
    }

    public static MaplePacket getTopMsg(String msg) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ScriptProgressMessage);
        sp.EncodeStr(msg);
        return sp.get();
    }

    public static MaplePacket MapTransferResult(MapleCharacter chr, OpsMapTransfer ops_res, boolean vip) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MapTransferResult);
        sp.Encode1(ops_res.get());
        sp.Encode1(vip ? 1 : 0);

        switch (ops_res) {
            case MapTransferRes_DeleteList:
            case MapTransferRes_RegisterList: {
                int map_list[] = vip ? chr.getRocks() : chr.getRegRocks();
                for (int map_id : map_list) {
                    sp.Encode4(map_id);
                }
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    public static MaplePacket cancelDebuff(long mask, boolean first) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatReset);

        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            sp.EncodeZeroBytes(4);
        }
        sp.Encode8(first ? mask : 0);
        sp.Encode8(first ? 0 : mask);
        sp.Encode1(1);
        return sp.get();
    }

    public static MaplePacket cancelHoming() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatReset);

        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            sp.EncodeZeroBytes(4);
        }
        sp.Encode8(MapleBuffStat.HOMING_BEACON.getValue());
        sp.Encode8(0);
        return sp.get();
    }

    public static MaplePacket updateMount(MapleCharacter chr, boolean levelup) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetTamingMobInfo);

        sp.Encode4(chr.getId());
        sp.Encode4(chr.getMount().getLevel());
        sp.Encode4(chr.getMount().getExp());
        sp.Encode4(chr.getMount().getFatigue());
        sp.Encode1(levelup ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket mountInfo(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetTamingMobInfo);

        sp.Encode4(chr.getId());
        sp.Encode1(1);
        sp.Encode4(chr.getMount().getLevel());
        sp.Encode4(chr.getMount().getExp());
        sp.Encode4(chr.getMount().getFatigue());
        return sp.get();
    }

    public static MaplePacket giveDebuff(final List<Pair<MapleDisease, Integer>> statups, int skillid, int level, int duration) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);

        sp.EncodeBuffer(ResCUserRemote.writeLongDiseaseMask(statups));
        for (Pair<MapleDisease, Integer> statup : statups) {
            sp.Encode2(statup.getRight().shortValue());
            sp.Encode2(skillid);
            sp.Encode2(level);
            sp.Encode4(duration);
        }
        sp.Encode2(0); // ??? wk charges have 600 here o.o
        sp.Encode2(900); //Delay
        sp.Encode1(1);
        return sp.get();
    }

    public static MaplePacket givePirate(List<Pair<MapleBuffStat, Integer>> statups, int duration, int skillid) {
        final boolean infusion = skillid == 5121009 || skillid == 15111005;
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);

        sp.EncodeBuffer(ResCUserRemote.writeLongMask(statups));
        sp.Encode2(0);
        for (Pair<MapleBuffStat, Integer> stat : statups) {
            sp.Encode4(stat.getRight().intValue());
            sp.Encode8(skillid);
            sp.EncodeZeroBytes(infusion ? 6 : 1);
            sp.Encode2(duration);
        }
        sp.Encode2(infusion ? 600 : 0);
        if (!infusion) {
            sp.Encode1(1); //does this only come in dash?
        }
        return sp.get();
    }

    public static MaplePacket giveMount(int buffid, int skillid, List<Pair<MapleBuffStat, Integer>> statups) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);

        sp.EncodeBuffer(ResCUserRemote.writeLongMask(statups));
        sp.Encode2(0);
        sp.Encode4(buffid); // 1902000 saddle
        sp.Encode4(skillid); // skillid
        sp.Encode4(0); // Server tick value
        sp.Encode2(0);
        sp.Encode1(0);
        sp.Encode1(2); // Total buffed times
        return sp.get();
    }

    public static MaplePacket giveEnergyChargeTest(int bar, int bufflength) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);

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

    public static MaplePacket giveHoming(int skillid, int mobid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);

        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            sp.EncodeZeroBytes(4);
        }
        sp.Encode8(MapleBuffStat.HOMING_BEACON.getValue());
        sp.Encode8(0);
        sp.Encode2(0);
        sp.Encode4(1);
        sp.Encode8(skillid);
        sp.Encode1(0);
        sp.Encode4(mobid);
        sp.Encode2(0);
        return sp.get();
    }

    public static MaplePacket partyStatusMessage(int message) {
        /*	* 10: A beginner can't create a party.
         * 1/11/14/19: Your request for a party didn't work due to an unexpected error.
         * 13: You have yet to join a party.
         * 16: Already have joined a party.
         * 17: The party you're trying to join is already in full capacity.
         * 19: Unable to find the requested character in this channel.*/
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PartyResult);

        sp.Encode1(message);
        return sp.get();
    }

    public static MaplePacket partyStatusMessage(int message, String charname) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PartyResult);

        sp.Encode1(message); // 23: 'Char' have denied request to the party.
        sp.EncodeStr(charname);
        return sp.get();
    }

    public static MaplePacket updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PartyResult);

        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                sp.Encode1(12);
                sp.Encode4(party.getId());
                sp.Encode4(target.getId());
                sp.Encode1(op == PartyOperation.DISBAND ? 0 : 1);
                if (op == PartyOperation.DISBAND) {
                    sp.Encode4(target.getId());
                } else {
                    sp.Encode1(op == PartyOperation.EXPEL ? 1 : 0);
                    sp.EncodeStr(target.getName());
                    sp.EncodeBuffer(addPartyStatus(forChannel, party, op == PartyOperation.LEAVE));
                }
                break;
            case JOIN:
                sp.Encode1(15);
                sp.Encode4(party.getId());
                sp.EncodeStr(target.getName());
                sp.EncodeBuffer(addPartyStatus(forChannel, party, false));
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                sp.Encode1(7);
                sp.Encode4(party.getId());
                sp.EncodeBuffer(addPartyStatus(forChannel, party, op == PartyOperation.LOG_ONOFF));
                break;
            case CHANGE_LEADER:
            case CHANGE_LEADER_DC:
                sp.Encode1(31); //test
                sp.Encode4(target.getId());
                sp.Encode1(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                break;
            //1D = expel function not available in this map.
        }
        return sp.get();
    }

    public static MaplePacket partyInvite(MapleCharacter from) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PartyResult);

        sp.Encode1(4);
        sp.Encode4(from.getParty().getId());
        sp.EncodeStr(from.getName());
        sp.Encode4(from.getLevel());
        sp.Encode4(from.getJob());
        sp.Encode1(0);
        return sp.get();
    }

    public static MaplePacket partyCreated(int partyid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_PartyResult);

        sp.Encode1(8);
        sp.Encode4(partyid);
        sp.Encode4(999999999);
        sp.Encode4(999999999);
        sp.Encode8(0);
        return sp.get();
    }

    private static byte[] addPartyStatus(int forchannel, MapleParty party, boolean leaving) {
        ServerPacket sp = new ServerPacket();

        List<MaplePartyCharacter> partymembers = new ArrayList<>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            sp.Encode4(partychar.getId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            sp.EncodeBuffer(partychar.getName(), 13);
        }
        for (MaplePartyCharacter partychar : partymembers) {
            sp.Encode4(partychar.getJobId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            sp.Encode4(partychar.getLevel());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.isOnline()) {
                sp.Encode4(partychar.getChannel() - 1);
            } else {
                sp.Encode4(-2);
            }
        }
        sp.Encode4(party.getLeader().getId());
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel) {
                sp.Encode4(partychar.getMapid());
            } else {
                sp.Encode4(0);
            }
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel && !leaving) {
                sp.Encode4(partychar.getDoorTown());
                sp.Encode4(partychar.getDoorTarget());
                sp.Encode4(partychar.getDoorSkill());
                sp.Encode4(partychar.getDoorPosition().x);
                sp.Encode4(partychar.getDoorPosition().y);
            } else {
                sp.Encode4(leaving ? 999999999 : 0);
                sp.Encode8(leaving ? 999999999 : 0);
                sp.Encode8(leaving ? -1 : 0);
            }
        }

        return sp.get().getBytes();
    }

    public static MaplePacket changeCover(int cardid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MonsterBookSetCover);
        sp.Encode4(cardid);
        return sp.get();
    }

    public static MaplePacket addCard(boolean full, int cardid, int level) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MonsterBookSetCard);
        sp.Encode1(full ? 0 : 1);
        if (!full) {
            sp.Encode4(cardid);
            sp.Encode4(level);
        }
        return sp.get();
    }

    public static MaplePacket guildNotice(int gid, String notice) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(68);
        sp.Encode4(gid);
        sp.EncodeStr(notice);
        return sp.get();
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(bExpelled ? 47 : 44);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.EncodeStr(mgc.getName());
        return sp.get();
    }

    private static byte[] getGuildInfo(MapleGuild guild) {
        ServerPacket data = new ServerPacket();

        data.Encode4(guild.getId());
        data.EncodeStr(guild.getName());

        for (int i = 1; i <= 5; i++) {
            data.EncodeStr(guild.getRankTitle(i));
        }

        data.EncodeBuffer(guild.addMemberData());
        data.Encode4(guild.getCapacity());
        data.Encode2(guild.getLogoBG());
        data.Encode1(guild.getLogoBGColor());
        data.Encode2(guild.getLogo());
        data.Encode1(guild.getLogoColor());
        data.EncodeStr(guild.getNotice());
        data.Encode4(guild.getGP());
        data.Encode4(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);

        return data.get().getBytes();
    }

    public static MaplePacket changeAlliance(MapleGuildAlliance alliance, final boolean in) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(1);
        sp.Encode1(in ? 1 : 0);
        sp.Encode4(in ? alliance.getId() : 0);
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < noGuilds; i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return WrapCWvsContext.updateStat();
            }
        }
        sp.Encode1(noGuilds);
        for (int i = 0; i < noGuilds; i++) {
            sp.Encode4(g[i].getId());
            //must be world
            Collection<MapleGuildCharacter> members = g[i].getMembers();
            sp.Encode4(members.size());
            for (MapleGuildCharacter mgc : members) {
                sp.Encode4(mgc.getId());
                sp.Encode1(in ? mgc.getAllianceRank() : 0);
            }
        }
        return sp.get();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(60);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getJobId());
        return sp.get();
    }

    public static MaplePacket allianceMemberOnline(int alliance, int gid, int id, boolean online) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(14);
        sp.Encode4(alliance);
        sp.Encode4(gid);
        sp.Encode4(id);
        sp.Encode1(online ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, final boolean add) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(4);
        sp.Encode4(add ? alliance.getId() : 0);
        sp.Encode4(guild.getId());
        Collection<MapleGuildCharacter> members = guild.getMembers();
        sp.Encode4(members.size());
        for (MapleGuildCharacter mgc : members) {
            sp.Encode4(mgc.getId());
            sp.Encode1(add ? mgc.getAllianceRank() : 0);
        }
        return sp.get();
    }

    public static MaplePacket disbandAlliance(int alliance) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(29);
        sp.Encode4(alliance);
        return sp.get();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(39);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.EncodeBuffer(mgc.getName(), 13);
        sp.Encode4(mgc.getJobId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getGuildRank()); //should be always 5 but whatevs
        sp.Encode4(mgc.isOnline() ? 1 : 0); //should always be 1 too
        sp.Encode4(1); //? could be guild signature, but doesn't seem to matter
        sp.Encode4(mgc.getAllianceRank()); //should always 3
        return sp.get();
    }

    public static MaplePacket updateAllianceRank(int allianceid, MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(27);
        sp.Encode4(allianceid);
        sp.Encode4(mgc.getId());
        sp.Encode4(mgc.getAllianceRank());
        return sp.get();
    }

    public static MaplePacket getGuildAlliance(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(13);
        if (alliance == null) {
            sp.Encode4(0);
            return sp.get();
        }
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return WrapCWvsContext.updateStat();
            }
        }
        sp.Encode4(noGuilds);
        for (MapleGuild gg : g) {
            sp.EncodeBuffer(getGuildInfo(gg));
        }
        return sp.get();
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(61);
        sp.Encode4(gid);
        sp.Encode4(cid);
        sp.Encode1(bOnline ? 1 : 0);
        return sp.get();
    }

    public static MaplePacket changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(2);
        sp.Encode4(allianceid);
        sp.Encode4(oldLeader);
        sp.Encode4(newLeader);
        return sp.get();
    }

    public static MaplePacket showGuildRanks(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(73);
        sp.Encode4(npcid);
        sp.Encode4(all.size());
        for (MapleGuildRanking.GuildRankingInfo info : all) {
            sp.EncodeStr(info.getName());
            sp.Encode4(info.getGP());
            sp.Encode4(info.getLogo());
            sp.Encode4(info.getLogoColor());
            sp.Encode4(info.getLogoBg());
            sp.Encode4(info.getLogoBgColor());
        }
        return sp.get();
    }

    public static MaplePacket denyGuildInvitation(String charname) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(55);
        sp.EncodeStr(charname);
        return sp.get();
    }

    public static MaplePacket showGuildInfo(MapleCharacter c) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(26); //signature for showing guild info
        if (c == null || c.getMGC() == null) {
            //show empty guild (used for leaving, expelled)
            sp.Encode1(0);
            return sp.get();
        }
        MapleGuild g = World.Guild.getGuild(c.getGuildId());
        if (g == null) {
            //failed to read from DB - don't show a guild
            sp.Encode1(0);
            return sp.get();
        }
        sp.Encode1(1); //bInGuild
        sp.EncodeBuffer(getGuildInfo(g));
        return sp.get();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(66);
        sp.Encode4(gid);
        sp.Encode2(bg);
        sp.Encode1(bgcolor);
        sp.Encode2(logo);
        sp.Encode1(logocolor);
        return sp.get();
    }

    private static byte[] addAllianceInfo(MapleGuildAlliance alliance) {
        ServerPacket data = new ServerPacket();

        data.Encode4(alliance.getId());
        data.EncodeStr(alliance.getName());

        for (int i = 1; i <= 5; i++) {
            data.EncodeStr(alliance.getRank(i));
        }

        data.Encode1(alliance.getNoGuilds());
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            data.Encode4(alliance.getGuildId(i));
        }

        data.Encode4(alliance.getCapacity()); // ????
        data.EncodeStr(alliance.getNotice());

        return data.get().getBytes();
    }

    public static MaplePacket guildDisband(int gid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(50);
        sp.Encode4(gid);
        sp.Encode1(1);
        return sp.get();
    }

    public static MaplePacket getAllianceInfo(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(12);
        sp.Encode1(alliance == null ? 0 : 1); //in an alliance
        if (alliance != null) {
            sp.EncodeBuffer(addAllianceInfo(alliance));
        }
        return sp.get();
    }

    public static MaplePacket updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(25);
        sp.Encode4(allianceid);
        sp.Encode4(oldLeader);
        sp.Encode4(newLeader);
        return sp.get();
    }

    public static MaplePacket addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(18);
        sp.EncodeBuffer(addAllianceInfo(alliance));
        sp.Encode4(newGuild.getId()); //???
        sp.EncodeBuffer(getGuildInfo(newGuild));
        sp.Encode1(0); //???
        return sp.get();
    }

    public static MaplePacket updateAlliance(MapleGuildCharacter mgc, int allianceid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(24);
        sp.Encode4(allianceid);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode4(mgc.getLevel());
        sp.Encode4(mgc.getJobId());
        return sp.get();
    }

    public static MaplePacket sendAllianceInvite(String allianceName, MapleCharacter inviter) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(3);
        sp.Encode4(inviter.getGuildId());
        sp.EncodeStr(inviter.getName());
        //alliance invite did NOT change
        sp.EncodeStr(allianceName);
        return sp.get();
    }

    private static byte[] addThread(MapleBBSThread rs) {
        ServerPacket data = new ServerPacket();

        data.Encode4(rs.localthreadID);
        data.Encode4(rs.ownerID);
        data.EncodeStr(rs.name);
        data.Encode8(TestHelper.getKoreanTimestamp(rs.timestamp));
        data.Encode4(rs.icon);
        data.Encode4(rs.getReplyCount());
        return data.get().getBytes();
    }

    public static MaplePacket showThread(MapleBBSThread thread) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildBBS);

        sp.Encode1(7);
        sp.Encode4(thread.localthreadID);
        sp.Encode4(thread.ownerID);
        sp.Encode8(TestHelper.getKoreanTimestamp(thread.timestamp));
        sp.EncodeStr(thread.name);
        sp.EncodeStr(thread.text);
        sp.Encode4(thread.icon);
        sp.Encode4(thread.getReplyCount());
        for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
            sp.Encode4(reply.replyid);
            sp.Encode4(reply.ownerID);
            sp.Encode8(TestHelper.getKoreanTimestamp(reply.timestamp));
            sp.EncodeStr(reply.content);
        }
        return sp.get();
    }

    public static MaplePacket BBSThreadList(final List<MapleBBSThread> bbs, int start) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildBBS);

        sp.Encode1(6);
        if (bbs == null) {
            sp.Encode1(0);
            sp.Encode8(0);
            return sp.get();
        }
        int threadCount = bbs.size();
        MapleBBSThread notice = null;
        for (MapleBBSThread b : bbs) {
            if (b.isNotice()) {
                //notice
                notice = b;
                break;
            }
        }
        final int ret = notice == null ? 0 : 1;
        sp.Encode1(ret);
        if (notice != null) {
            //has a notice
            sp.EncodeBuffer(addThread(notice));
            threadCount--; //one thread didn't count (because it's a notice)
        }
        if (threadCount < start) {
            //seek to the thread before where we start
            //uh, we're trying to start at a place past possible
            start = 0;
        }
        //each page has 10 threads, start = page # in packet but not here
        sp.Encode4(threadCount);
        final int pages = Math.min(10, threadCount - start);
        sp.Encode4(pages);
        for (int i = 0; i < pages; i++) {
            sp.EncodeBuffer(addThread(bbs.get(start + i + ret))); //because 0 = notice
        }
        return sp.get();
    }

    public static MaplePacket createGuildAlliance(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(15);
        sp.EncodeBuffer(addAllianceInfo(alliance));
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return WrapCWvsContext.updateStat();
            }
        }
        for (MapleGuild gg : g) {
            sp.EncodeBuffer(getGuildInfo(gg));
        }
        return sp.get();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(62);
        sp.Encode4(gid);
        for (String r : ranks) {
            sp.EncodeStr(r);
        }
        return sp.get();
    }

    public static MaplePacket genericGuildMessage(byte code) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(code);
        return sp.get();
    }

    public static MaplePacket removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(16);
        sp.EncodeBuffer(addAllianceInfo(alliance));
        sp.EncodeBuffer(getGuildInfo(expelledGuild));
        sp.Encode1(expelled ? 1 : 0); //1 = expelled, 0 = left
        return sp.get();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(58);
        sp.Encode4(gid);
        sp.Encode1(capacity);
        return sp.get();
    }

    public static MaplePacket updateGP(int gid, int GP) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(72);
        sp.Encode4(gid);
        sp.Encode4(GP);
        return sp.get();
    }

    public static MaplePacket getAllianceUpdate(MapleGuildAlliance alliance) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(23);
        sp.EncodeBuffer(addAllianceInfo(alliance));
        return sp.get();
    }

    public static MaplePacket guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(5);
        sp.Encode4(gid);
        sp.EncodeStr(charName);
        sp.Encode4(levelFrom);
        sp.Encode4(jobFrom);
        return sp.get();
    }

    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GuildResult);

        sp.Encode1(64);
        sp.Encode4(mgc.getGuildId());
        sp.Encode4(mgc.getId());
        sp.Encode1(mgc.getGuildRank());
        return sp.get();
    }

    public static MaplePacket changeAllianceRank(int allianceid, MapleGuildCharacter player) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AllianceResult);

        sp.Encode1(5);
        sp.Encode4(allianceid);
        sp.Encode4(player.getId());
        sp.Encode4(player.getAllianceRank());
        return sp.get();
    }

    public static MaplePacket sendFamilyJoinResponse(boolean accepted, String added) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilyJoinRequestResult);

        sp.Encode1(accepted ? 1 : 0);
        sp.EncodeStr(added);
        return sp.get();
    }

    public static MaplePacket changeRep(int r) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilyFamousPointIncResult);

        sp.Encode4(r);
        sp.Encode4(0);
        return sp.get();
    }

    public static MaplePacket sendFamilyInvite(int cid, int otherLevel, int otherJob, String inviter) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilyJoinRequest);

        sp.Encode4(cid); //the inviter
        sp.Encode4(otherLevel);
        sp.Encode4(otherJob);
        sp.EncodeStr(inviter);
        return sp.get();
    }

    public static byte[] addFamilyCharInfo(MapleFamilyCharacter ldr) {

        ServerPacket data = new ServerPacket();
        data.Encode4(ldr.getId());
        data.Encode4(ldr.getSeniorId());
        data.Encode2(ldr.getJobId());
        data.Encode1(ldr.getLevel());
        data.Encode1(ldr.isOnline() ? 1 : 0);
        data.Encode4(ldr.getCurrentRep());
        data.Encode4(ldr.getTotalRep());
        data.Encode4(ldr.getTotalRep()); //recorded rep to senior
        data.Encode4(ldr.getTotalRep()); //then recorded rep to sensen
        data.Encode8(Math.max(ldr.getChannel(), 0)); //channel->time online
        data.EncodeStr(ldr.getName());
        return data.get().getBytes();
    }

    public static MaplePacket familyLoggedIn(boolean online, String name) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilyNotifyLoginOrLogout);

        sp.Encode1(online ? 1 : 0);
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket familySummonRequest(String name, String mapname) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilySummonRequest);

        sp.EncodeStr(name);
        sp.EncodeStr(mapname);
        return sp.get();
    }

    public static MaplePacket cancelFamilyBuff() {
        return familyBuff(0, 0, 0, 0);
    }

    public static MaplePacket getFamilyData() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilyPrivilegeList);

        List<MapleFamilyBuff.MapleFamilyBuffEntry> entries = MapleFamilyBuff.getBuffEntry();
        sp.Encode4(entries.size()); // Number of events
        for (MapleFamilyBuff.MapleFamilyBuffEntry entry : entries) {
            sp.Encode1(entry.type);
            sp.Encode4(entry.rep);
            sp.Encode4(entry.count);
            sp.EncodeStr(entry.name);
            sp.EncodeStr(entry.desc);
        }
        return sp.get();
    }

    public static MaplePacket familyBuff(int type, int buffnr, int amount, int time) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilySetPrivilege);

        sp.Encode1(type);
        if (type >= 2 && type <= 4) {
            sp.Encode4(buffnr);
            //first int = exp, second int = drop
            sp.Encode4(type == 3 ? 0 : amount);
            sp.Encode4(type == 2 ? 0 : amount);
            sp.Encode1(0);
            sp.Encode4(time);
        }
        return sp.get();
    }

    public static MaplePacket getFamilyPedigree(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilyChartResult);

        sp.Encode4(chr.getId());
        MapleFamily family = World.Family.getFamily(chr.getFamilyId());
        int descendants = 2;
        int gens = 0;
        int generations = 0;
        if (family == null) {
            sp.Encode4(2);
            sp.EncodeBuffer(addFamilyCharInfo(new MapleFamilyCharacter(chr, 0, 0, 0, 0))); //leader
        } else {
            sp.Encode4(family.getMFC(chr.getId()).getPedigree().size() + 1); //+ 1 for leader, but we don't want leader seeing all msgs
            sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(family.getLeaderId())));
            if (chr.getSeniorId() > 0) {
                MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                if (senior.getSeniorId() > 0) {
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(senior.getSeniorId())));
                }
                sp.EncodeBuffer(addFamilyCharInfo(senior));
            }
        }
        sp.EncodeBuffer(addFamilyCharInfo(chr.getMFC() == null ? new MapleFamilyCharacter(chr, 0, 0, 0, 0) : chr.getMFC()));
        if (family != null) {
            if (chr.getSeniorId() > 0) {
                MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                if (senior != null) {
                    if (senior.getJunior1() > 0 && senior.getJunior1() != chr.getId()) {
                        sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(senior.getJunior1())));
                    } else if (senior.getJunior2() > 0 && senior.getJunior2() != chr.getId()) {
                        sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(senior.getJunior2())));
                    }
                }
            }
            if (chr.getJunior1() > 0) {
                sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(chr.getJunior1())));
            }
            if (chr.getJunior2() > 0) {
                sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(chr.getJunior2())));
            }
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior.getJunior1() > 0) {
                    descendants++;
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(junior.getJunior1())));
                }
                if (junior.getJunior2() > 0) {
                    descendants++;
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(junior.getJunior2())));
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior.getJunior1() > 0) {
                    descendants++;
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(junior.getJunior1())));
                }
                if (junior.getJunior2() > 0) {
                    descendants++;
                    sp.EncodeBuffer(addFamilyCharInfo(family.getMFC(junior.getJunior2())));
                }
            }
            gens = family.getGens();
            generations = family.getMemberSize();
        }
        sp.Encode8(descendants);
        sp.Encode4(gens);
        sp.Encode4(-1);
        sp.Encode4(generations);
        if (family != null) {
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior.getJunior1() > 0) {
                    sp.Encode4(junior.getJunior1());
                    sp.Encode4(family.getMFC(junior.getJunior1()).getDescendants());
                }
                if (junior.getJunior2() > 0) {
                    sp.Encode4(junior.getJunior2());
                    sp.Encode4(family.getMFC(junior.getJunior2()).getDescendants());
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior.getJunior1() > 0) {
                    sp.Encode4(junior.getJunior1());
                    sp.Encode4(family.getMFC(junior.getJunior1()).getDescendants());
                }
                if (junior.getJunior2() > 0) {
                    sp.Encode4(junior.getJunior2());
                    sp.Encode4(family.getMFC(junior.getJunior2()).getDescendants());
                }
            }
        }
        List<Pair<Integer, Integer>> b = chr.usedBuffs();
        sp.Encode4(b.size());
        for (Pair<Integer, Integer> ii : b) {
            sp.Encode4(ii.getLeft()); //buffid
            sp.Encode4(ii.getRight()); //times used
        }
        sp.Encode2(2);
        return sp.get();
    }

    public static MaplePacket getFamilyInfo(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilyInfoResult);

        sp.Encode4(chr.getCurrentRep()); //rep
        sp.Encode4(chr.getTotalRep()); // total rep
        sp.Encode4(chr.getTotalRep()); //rep recorded today
        sp.Encode2(chr.getNoJuniors());
        sp.Encode2(2);
        sp.Encode2(chr.getNoJuniors());
        MapleFamily family = World.Family.getFamily(chr.getFamilyId());
        if (family != null) {
            sp.Encode4(family.getLeaderId()); //??? 9D 60 03 00
            sp.EncodeStr(family.getLeaderName());
            sp.EncodeStr(family.getNotice()); //message?
        } else {
            sp.Encode8(0);
        }
        List<Pair<Integer, Integer>> b = chr.usedBuffs();
        sp.Encode4(b.size());
        for (Pair<Integer, Integer> ii : b) {
            sp.Encode4(ii.getLeft()); //buffid
            sp.Encode4(ii.getRight()); //times used
        }
        return sp.get();
    }

    public static MaplePacket getSeniorMessage(String name) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FamilyJoinAccepted);

        sp.EncodeStr(name);
        return sp.get();
    }

    // CWvsContext::OnFriendResult
    public static MaplePacket FriendResult(ArgFriend frs) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FriendResult);
        sp.Encode1(frs.flag.get());
        switch (frs.flag) {
            case FriendRes_LoadFriend_Done:
            case FriendRes_SetFriend_Done:
            case FriendRes_DeleteFriend_Done: {
                sp.EncodeBuffer(DataCWvsContext.CFriend_Reset(frs.chr));
                break;
            }
            case FriendRes_NotifyChange_FriendInfo: {
                break;
            }
            case FriendRes_Invite: {
                // 9
                sp.Encode4(frs.friend_id);
                sp.EncodeStr(frs.friend_name);
                sp.Encode4(frs.friend_level);
                sp.Encode4(frs.friend_job);
                // CWvsContext::CFriend::Insert, 39 bytes
                sp.Encode4(frs.friend_id);
                sp.EncodeBuffer(frs.friend_name, 13);
                sp.Encode1(0);
                sp.Encode4(frs.friend_channel == -1 ? -1 : frs.friend_channel - 1); // please add channel
                sp.EncodeBuffer(frs.friend_tag, 17);
                // 1 byte
                sp.Encode1(1);
                break;
            }
            case FriendRes_SetFriend_FullMe: {
                // none
                break;
            }
            case FriendRes_SetFriend_FullOther: {
                // none
                break;
            }
            case FriendRes_SetFriend_AlreadySet: {
                break;
            }
            case FriendRes_SetFriend_Master: {
                break;
            }
            case FriendRes_SetFriend_UnknownUser: {
                // none
                break;
            }
            case FriendRes_SetFriend_Unknown: {
                break;
            }
            case FriendRes_AcceptFriend_Unknown: {
                break;
            }
            case FriendRes_DeleteFriend_Unknown: {
                break;
            }
            case FriendRes_Notify: {
                sp.Encode4(frs.friend_id);
                sp.Encode1(0);
                sp.Encode4(frs.friend_channel);
                break;
            }
            case FriendRes_IncMaxCount_Done: {
                sp.Encode1(frs.nFriendMax);
                break;
            }
            case FriendRes_IncMaxCount_Unknown: {
                break;
            }
            case FriendRes_PleaseWait: {
                break;
            }
            default: {
                DebugLogger.ErrorLog("FriendResult not coded : " + frs.flag);
                break;
            }
        }

        return sp.get();
    }

    public static MaplePacket ShopScannerResult(OpsShopScanner ops) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ShopScannerResult);

        sp.Encode1(ops.get());
        switch (ops) {
            case ShopScannerRes_SearchResult: {
                sp.Encode4(4000000); // item id
                sp.Encode4(1); // 0 -> fail
                sp.EncodeStr("マノ");
                sp.Encode4(18); // shop id or other channel FM num
                sp.EncodeStr("デンデンのカラ売ります");
                sp.Encode4(200); // 数量
                sp.Encode4(1); // バンドル
                sp.Encode4(500); // 価格
                sp.Encode4(910000018); // map id
                sp.Encode1(1); // channel
                sp.Encode1(0);
                break;
            }
            case ShopScannerRes_LoadHotListResult: {
                int hotlist[] = {4000000, 4000016, 4000019};
                sp.Encode1(hotlist.length);
                for (int item_id : hotlist) {
                    sp.Encode4(item_id);
                }
                break;
            }
            default: {
                break;
            }
        }
        return sp.get();
    }

    public static MaplePacket EntrustedShopCheckResult(OpsEntrustedShop ops_res) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_EntrustedShopCheckResult);

        sp.Encode1(ops_res.get());

        switch (ops_res) {
            case EntrustedShopRes_OpenImpossible_Using: {
                sp.Encode4(910000018); // used last 3 numbers to decide FreeMarket room.
                sp.Encode1(0); // channel
                break;
            }
            case EntrustedShopReq_SetMiniMapColor: {
                // may be res...
                sp.Encode4(123);
                break;
            }
            case EntrustedShopReq_RenameResult: {
                // may be res...
                sp.Encode1(1); // 0 = fail, 1 = success.
                break;
            }
            case EntrustedShopRes_GetPosResult: {
                // client sends change channel packet after this.
                sp.Encode4(0);
                sp.Encode1(0); // channel
                break;
            }
            case EntrustedShopRes_Enter: {
                // client sends enter shop packet after this.
                sp.Encode4(123); // own shop id.
                break;
            }
            default: {
                break;
            }
        }

        return sp.get();
    }

    public static MaplePacket SkillLearnItemResult(MapleCharacter chr, boolean bIsMaterbook, boolean bUsed, boolean bSucceed) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SkillLearnItemResult);

        if (Version.GreaterOrEqual(Region.JMS, 186) || Version.PostBB()) {
            sp.Encode1(1); // bOnExclRequest
        }

        sp.Encode4(chr.getId());
        sp.Encode1(bIsMaterbook ? 1 : 0); // bIsMaterbook
        sp.Encode4(0); // not used
        sp.Encode4(0); // not used
        sp.Encode1(bUsed ? 1 : 0); // bUsed[0]
        sp.Encode1(bSucceed ? 1 : 0); // bSucceed
        return sp.get();
    }

    public static final MaplePacket getSlotUpdate(byte invType, byte newSlots) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_InventoryGrow);

        sp.Encode1(invType);
        sp.Encode1(newSlots);
        return sp.get();
    }

    public static MaplePacket followRequest(int chrid) {
        final ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetPassenserRequest);

        sp.Encode4(chrid);
        return sp.get();
    }

    public static MaplePacket SuccessInUseGachaponBox(int box_item_id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SuccessInUseGachaponBox);
        sp.Encode4(box_item_id);
        return sp.get();
    }

    public static final MaplePacket temporaryStats_Balrog(final MapleCharacter chr) {
        final List<Pair<MapleStat.Temp, Integer>> stats = new ArrayList<Pair<MapleStat.Temp, Integer>>();
        int offset = 1 + (chr.getLevel() - 90) / 20;
        //every 20 levels above 90, +1
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.STR, chr.getStat().getTotalStr() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.DEX, chr.getStat().getTotalDex() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.INT, chr.getStat().getTotalInt() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.LUK, chr.getStat().getTotalLuk() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.WATK, chr.getStat().getTotalWatk() / offset));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.MATK, chr.getStat().getTotalMagic() / offset));
        return temporaryStats(stats);
    }

    public static final MaplePacket temporaryStats(final List<Pair<MapleStat.Temp, Integer>> stats) {
        final ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ForcedStatSet);

        //str 0x1, dex 0x2, int 0x4, luk 0x8
        //level 0x10 = 255
        //0x100 = 999
        //0x200 = 999
        //0x400 = 120
        //0x800 = 140
        int updateMask = 0;
        for (final Pair<MapleStat.Temp, Integer> statupdate : stats) {
            updateMask |= statupdate.getLeft().getValue();
        }
        List<Pair<MapleStat.Temp, Integer>> mystats = stats;
        if (mystats.size() > 1) {
            Collections.sort(mystats, new Comparator<Pair<MapleStat.Temp, Integer>>() {
                @Override
                public int compare(final Pair<MapleStat.Temp, Integer> o1, final Pair<MapleStat.Temp, Integer> o2) {
                    int val1 = o1.getLeft().getValue();
                    int val2 = o2.getLeft().getValue();
                    return val1 < val2 ? -1 : (val1 == val2 ? 0 : 1);
                }
            });
        }
        sp.Encode4(updateMask);
        Integer value;
        for (final Pair<MapleStat.Temp, Integer> statupdate : mystats) {
            value = statupdate.getLeft().getValue();
            if (value >= 1) {
                if (value <= 512) {
                    //level 0x10 - is this really short or some other? (FF 00)
                    sp.Encode2(statupdate.getRight().shortValue());
                } else {
                    sp.Encode1(statupdate.getRight().byteValue());
                }
            }
        }
        return sp.get();
    }

    public static final MaplePacket temporaryStats_Aran() {
        final List<Pair<MapleStat.Temp, Integer>> stats = new ArrayList<Pair<MapleStat.Temp, Integer>>();
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.STR, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.DEX, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.INT, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.LUK, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.WATK, 255));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.ACC, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.AVOID, 999));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.SPEED, 140));
        stats.add(new Pair<MapleStat.Temp, Integer>(MapleStat.Temp.JUMP, 120));
        return temporaryStats(stats);
    }

    public static MaplePacket sendLevelup(boolean family, int level, String name) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_NotifyLevelUp);

        sp.Encode1(family ? 1 : 2);
        sp.Encode4(level);
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket sendJobup(boolean family, int jobid, String name) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_NotifyJobChange);

        sp.Encode1(family ? 1 : 0);
        sp.Encode4(jobid); //or is this a short
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket sendMarriage(boolean family, String name) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_NotifyWedding);

        sp.Encode1(family ? 1 : 0);
        sp.EncodeStr(name);
        return sp.get();
    }

    public static MaplePacket giveFameResponse(int mode, String charname, int newfame) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GivePopularityResult);

        sp.Encode1(0);
        sp.EncodeStr(charname);
        sp.Encode1(mode);
        sp.Encode2(newfame);
        sp.Encode2(0);
        return sp.get();
    }

    public static MaplePacket giveFameErrorResponse(int status) {
        /*	* 0: ok, use giveFameResponse<br>
         * 1: the username is incorrectly entered<br>
         * 2: users under level 15 are unable to toggle with fame.<br>
         * 3: can't raise or drop fame anymore today.<br>
         * 4: can't raise or drop fame for this character for this month anymore.<br>
         * 5: received fame, use receiveFame()<br>
         * 6: level of fame neither has been raised nor dropped due to an unexpected error*/
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GivePopularityResult);

        sp.Encode1(status);
        return sp.get();
    }

    public static MaplePacket receiveFame(int mode, String charnameFrom) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GivePopularityResult);

        sp.Encode1(5);
        sp.EncodeStr(charnameFrom);
        sp.Encode1(mode);
        return sp.get();
    }

    public static MaplePacket sendEngagement(final byte msg, final int item, final MapleCharacter male, final MapleCharacter female) {
        // 0B = Engagement has been concluded.
        // 0D = The engagement is cancelled.
        // 0E = The divorce is concluded.
        // 10 = The marriage reservation has been successsfully made.
        // 12 = Wrong character name
        // 13 = The party in not in the same map.
        // 14 = Your inventory is full. Please empty your E.T.C window.
        // 15 = The person's inventory is full.
        // 16 = The person cannot be of the same gender.
        // 17 = You are already engaged.
        // 18 = The person is already engaged.
        // 19 = You are already married.
        // 1A = The person is already married.
        // 1B = You are not allowed to propose.
        // 1C = The person is not allowed to be proposed to.
        // 1D = Unfortunately, the one who proposed to you has cancelled his proprosal.
        // 1E = The person had declined the proposal with thanks.
        // 1F = The reservation has been cancelled. Try again later.
        // 20 = You cannot cancel the wedding after reservation.
        // 22 = The invitation card is ineffective.
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MarriageResult);

        sp.Encode1(msg); // 1103 custom quest
        switch (msg) {
            case 11: {
                sp.Encode4(0); // ringid or uniqueid
                sp.Encode4(male.getId());
                sp.Encode4(female.getId());
                sp.Encode2(1); //always
                sp.Encode4(item);
                sp.Encode4(item); // wtf?repeat?
                sp.EncodeBuffer(male.getName(), 13);
                sp.EncodeBuffer(female.getName(), 13);
                break;
            }
        }
        return sp.get();
    }

    public static MaplePacket sendEngagementRequest(String name, int cid) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MarriageRequest);

        sp.Encode1(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
        sp.EncodeStr(name); // name
        sp.Encode4(cid); // playerid
        return sp.get();
    }

    public static MaplePacket getPeanutResult(int itemId, short quantity, int itemId2, short quantity2) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_IncubatorResult);

        sp.Encode4(itemId);
        sp.Encode2(quantity);
        sp.Encode4(5060003);
        sp.Encode4(itemId2);
        sp.Encode4(quantity2);
        return sp.get();
    }

    public static MaplePacket SetWeekEventMessage(String text) {
        // not in KMS31
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_SetWeekEventMessage);
        sp.Encode1(-1);
        sp.EncodeStr(text);
        return sp.get();
    }

    public static MaplePacket getShowQuestCompletion(int id) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_QuestClear);

        sp.Encode2(id);
        return sp.get();
    }

    public static MaplePacket getAvatarMega(MapleCharacter chr, int channel, int itemId, String message, boolean ear) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_AvatarMegaphoneUpdateMessage);

        sp.Encode4(itemId);
        sp.EncodeStr(chr.getName());
        sp.EncodeStr(message);
        sp.Encode4(channel - 1); // channel
        sp.Encode1(ear ? 1 : 0);
        sp.EncodeBuffer(DataAvatarLook.Encode(chr));
        return sp.get();
    }

    public static MaplePacket fairyPendantMessage(int type, int percent) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_BonusExpRateChanged);

        sp.Encode2(21); // 0x15
        sp.Encode4(0); // idk
        sp.Encode2(0); // idk
        sp.Encode2(percent); // percent
        sp.Encode2(0); // idk
        return sp.get();
    }

    public static final MaplePacket sendString(final int type, String object, final String amount) {
        ServerPacket.Header header = ServerPacket.Header.UNKNOWN;

        switch (type) {
            case 1:
                header = ServerPacket.Header.LP_SessionValue;
                break;
            case 2:
                header = ServerPacket.Header.LP_PartyValue;
                break;
            case 3:
                header = ServerPacket.Header.LP_FieldSetVariable;
                break;
            default: {
                break;
            }
        }

        ServerPacket sp = new ServerPacket(header);

        sp.EncodeStr(object); //massacre_hit, massacre_cool, massacre_miss, massacre_party, massacre_laststage, massacre_skill
        sp.EncodeStr(amount);
        return sp.get();
    }

    // 0x005E @005E 00, ミニマップ点滅, 再読み込みかも?
    public static MaplePacket ReloadMiniMap() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MiniMapOnOff);
        sp.Encode1((byte) 0);
        return sp.get();
    }

    // パチンコ情報の更新
    public static MaplePacket PachinkoResult(MapleCharacter chr) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_PachinkoResult);
        // 12 bytes
        {
            sp.Encode4(chr.getId()); // キャラクターID (実質不要)
            sp.Encode4(chr.getTama()); // アイテム欄の玉の数に反映される値
            sp.Encode4(0); // 用途不明
        }
        return sp.get();
    }

}
