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
package packet.response;

import client.BuddylistEntry;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleDisease;
import client.MapleQuestStatus;
import client.MapleStat;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import debug.Debug;
import handling.MaplePacket;
import handling.channel.MapleGuildRanking;
import handling.channel.handler.InventoryHandler;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleBBSThread;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import handling.world.guild.MapleGuildCharacter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import packet.ServerPacket;
import packet.ops.OpsBodyPart;
import packet.ops.arg.ArgBroadcastMsg;
import packet.ops.OpsChangeStat;
import packet.ops.arg.ArgFriend;
import packet.ops.arg.ArgMessage;
import packet.ops.OpsSecondaryStat;
import packet.request.ItemRequest;
import packet.response.data.DataCUIUserInfo;
import packet.response.data.DataSecondaryStat;
import packet.response.data.DataGW_CharacterStat;
import packet.response.data.DataGW_ItemSlotBase;
import packet.response.struct.InvOp;
import packet.response.struct.TestHelper;
import packet.response.wrapper.ResWrapper;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.shops.HiredMerchant;
import server.shops.MaplePlayerShopItem;
import tools.Pair;
import tools.StringUtil;
import tools.data.output.LittleEndianWriter;
import tools.data.output.MaplePacketLittleEndianWriter;

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

        if (ServerConfig.JMS302orLater() || ServerConfig.KMST391() || ServerConfig.KMS197orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
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
        if (ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            sp.Encode1(0);
        }
        sp.Encode2(1);
        sp.Encode4(skillid);
        sp.Encode4(level);
        sp.Encode4(masterlevel);
        if (ServerConfig.JMS164orLater()) {
            sp.Encode8(ServerConfig.expiration_date);
        }
        sp.Encode1(4);
        return sp.get();
    }

    // CWvsContext::OnTemporaryStatSet
    public static final MaplePacket TemporaryStatSet(MapleStatEffect effect) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_TemporaryStatSet);
        sp.EncodeBuffer(DataSecondaryStat.EncodeForLocal(effect));
        sp.Encode2(0); // delay
        if (ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
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
        if (ServerConfig.EMS89orLater()) {
            sp.Encode4(buff_mask[8]);
        }
        if (ServerConfig.JMS302orLater() || ServerConfig.EMS89orLater() || ServerConfig.TWMS148orLater() || ServerConfig.CMS104orLater()) {
            sp.Encode4(buff_mask[7]);
            sp.Encode4(buff_mask[6]);
            sp.Encode4(buff_mask[5]);
        }
        // JMS v187+
        if (ServerConfig.IsPostBB()) {
            if (!ServerConfig.IsIMS() && !ServerConfig.IsTHMS()) {
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
        if ((ServerConfig.IsEMS() && !ServerConfig.EMS89orLater()) || (ServerConfig.TWMS74orLater() && !ServerConfig.TWMS94orLater())) {
            sp.Encode1(0); // EMS v55
        }
        sp.EncodeBuffer(DataGW_CharacterStat.EncodeChangeStat(chr, statmask));
        if (ServerConfig.IsPreBB()) {
            if (ServerConfig.IsJMS()) {
                // Pet
                if ((statmask & OpsChangeStat.CS_PETSN.get()) > 0) {
                    int v5 = 0; // CVecCtrlUser::AddMovementInfo
                    sp.Encode1(v5);
                }
            }
            if (ServerConfig.GMS91orLater()) {
                sp.Encode1(0); // not 0 -> Encode1
            }
        } else {
            // v188+
            sp.Encode1(0); // not 0 -> Encode1
            sp.Encode1(0); // not 0 -> Encode4, Encode4
        }
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
                        if (ServerConfig.JMS131orEarlier()) {
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
                        Debug.ErrorLog("Unknown DropPickUp Type" + ma.dt.get());
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
                        Debug.ErrorLog("Unknown QuestRecord Type" + ma.dt.get());
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
                if (ServerConfig.IsTHMS() && ServerConfig.GetVersion() == 87) {
                    sp.Encode4(ma.Inc_EXP_WeddingBonus); // Wedding Bonus EXP(+%d)
                    sp.Encode4(0); // Party Ring Bonus EXP(+%d)
                    sp.Encode4(0); // EXP Bonus Internet Cafe(+ %d)
                    sp.Encode4(0); // Rainbow Week Bonus EXP(+%d)
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
                if (ServerConfig.JMS302orLater()) {
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

    public static MaplePacket finishedGather(byte type) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_GatherItemResult);
        sp.Encode1(0); // unused
        sp.Encode1(type);
        return sp.get();
    }

    public static MaplePacket finishedSort(byte type) {
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

        if (ServerConfig.JMS302orLater()) {
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

        if (ServerConfig.JMS302orLater()) {
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
        if (ServerConfig.IsPostBB()) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }
        sp.Encode1((player.getPet(0) != null) ? 1 : 0); // pet button clickable
        // CUIUserInfo::SetMultiPetInfo
        sp.EncodeBuffer(DataCUIUserInfo.SetMultiPetInfo(player));
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
        if (ServerConfig.JMS180orLater() || ServerConfig.KMS84orLater()) {
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
                if (ServerConfig.JMS302orLater()) {
                    sp.Encode8(0);
                }
            }
            // JMS v180-v186, v187以降消滅
            if (ServerConfig.IsPreBB() && ServerConfig.IsJMS()) {
                // Chair List
                sp.Encode4(player.getInventory(MapleInventoryType.SETUP).list().size());
                // CInPacket::DecodeBuffer(v4, iPacket, 4 * chairs);
                for (IItem chair : player.getInventory(MapleInventoryType.SETUP).list()) {
                    sp.Encode4(chair.getItemId());
                }
            }

            if (ServerConfig.JMS302orLater()) {
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
                String text = ItemRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
                sp.EncodeStr(text);
                break;
            }
            case BM_SPEAKERWORLD: // 5071000, 拡声器
            case BM_HEARTSPEAKER: // 5073000, ハート拡声器
            case BM_SKULLSPEAKER: // 5074000, ドクロ拡声器
            {
                String text = ItemRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
                int channel = bma.chr.getClient().getChannel() - 1;
                sp.EncodeStr(text);
                sp.Encode1(channel);
                sp.Encode1(bma.ear);
                break;
            }
            case BM_ITEMSPEAKER: // 5076000, アイテム拡声器
            {
                String text = ItemRequest.MegaphoneGetSenderName(bma.chr) + " : " + bma.message;
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
                String name = ItemRequest.MegaphoneGetSenderName(bma.chr);
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
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MemoResult.get());
        mplew.write(3);
        mplew.write(count);
        for (int i = 0; i < count; i++) {
            mplew.writeInt(notes.getInt("id"));
            mplew.writeMapleAsciiString(notes.getString("from"));
            mplew.writeMapleAsciiString(notes.getString("message"));
            mplew.writeLong(TestHelper.getKoreanTimestamp(notes.getLong("timestamp")));
            mplew.write(notes.getInt("gift"));
            notes.next();
        }
        return mplew.getPacket();
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

    public static MaplePacket getTrockRefresh(MapleCharacter chr, boolean vip, boolean delete) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MapTransferResult.get());
        mplew.write(delete ? 2 : 3);
        mplew.write(vip ? 1 : 0);
        if (vip) {
            int[] map = chr.getRocks();
            for (int i = 0; i < 10; i++) {
                mplew.writeInt(map[i]);
            }
        } else {
            int[] map = chr.getRegRocks();
            for (int i = 0; i < 5; i++) {
                mplew.writeInt(map[i]);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket cancelDebuff(long mask, boolean first) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatReset.get());
        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }
        mplew.writeLong(first ? mask : 0);
        mplew.writeLong(first ? 0 : mask);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket cancelHoming() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatReset.get());
        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }
        mplew.writeLong(MapleBuffStat.HOMING_BEACON.getValue());
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    public static MaplePacket updateMount(MapleCharacter chr, boolean levelup) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetTamingMobInfo.get());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        mplew.write(levelup ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket mountInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetTamingMobInfo.get());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        return mplew.getPacket();
    }

    public static MaplePacket giveDebuff(final List<Pair<MapleDisease, Integer>> statups, int skillid, int level, int duration) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.get());
        ResCUserRemote.writeLongDiseaseMask(mplew, statups);
        for (Pair<MapleDisease, Integer> statup : statups) {
            mplew.writeShort(statup.getRight().shortValue());
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeInt(duration);
        }
        mplew.writeShort(0); // ??? wk charges have 600 here o.o
        mplew.writeShort(900); //Delay
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket givePirate(List<Pair<MapleBuffStat, Integer>> statups, int duration, int skillid) {
        final boolean infusion = skillid == 5121009 || skillid == 15111005;
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.get());
        ResCUserRemote.writeLongMask(mplew, statups);
        mplew.writeShort(0);
        for (Pair<MapleBuffStat, Integer> stat : statups) {
            mplew.writeInt(stat.getRight().intValue());
            mplew.writeLong(skillid);
            mplew.writeZeroBytes(infusion ? 6 : 1);
            mplew.writeShort(duration);
        }
        mplew.writeShort(infusion ? 600 : 0);
        if (!infusion) {
            mplew.write(1); //does this only come in dash?
        }
        return mplew.getPacket();
    }

    public static MaplePacket giveMount(int buffid, int skillid, List<Pair<MapleBuffStat, Integer>> statups) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.get());
        ResCUserRemote.writeLongMask(mplew, statups);
        mplew.writeShort(0);
        mplew.writeInt(buffid); // 1902000 saddle
        mplew.writeInt(skillid); // skillid
        mplew.writeInt(0); // Server tick value
        mplew.writeShort(0);
        mplew.write(0);
        mplew.write(2); // Total buffed times
        return mplew.getPacket();
    }

    public static MaplePacket giveEnergyChargeTest(int bar, int bufflength) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.get());
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

    public static MaplePacket giveHoming(int skillid, int mobid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_TemporaryStatSet.get());
        if (194 <= ServerConfig.version) {
            mplew.writeZeroBytes(4);
        }
        mplew.writeLong(MapleBuffStat.HOMING_BEACON.getValue());
        mplew.writeLong(0);
        mplew.writeShort(0);
        mplew.writeInt(1);
        mplew.writeLong(skillid);
        mplew.write(0);
        mplew.writeInt(mobid);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket partyStatusMessage(int message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /*	* 10: A beginner can't create a party.
         * 1/11/14/19: Your request for a party didn't work due to an unexpected error.
         * 13: You have yet to join a party.
         * 16: Already have joined a party.
         * 17: The party you're trying to join is already in full capacity.
         * 19: Unable to find the requested character in this channel.*/
        mplew.writeShort(ServerPacket.Header.LP_PartyResult.get());
        mplew.write(message);
        return mplew.getPacket();
    }

    public static MaplePacket partyStatusMessage(int message, String charname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_PartyResult.get());
        mplew.write(message); // 23: 'Char' have denied request to the party.
        mplew.writeMapleAsciiString(charname);
        return mplew.getPacket();
    }

    public static MaplePacket updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_PartyResult.get());
        switch (op) {
            case DISBAND:
            case EXPEL:
            case LEAVE:
                mplew.write(12);
                mplew.writeInt(party.getId());
                mplew.writeInt(target.getId());
                mplew.write(op == PartyOperation.DISBAND ? 0 : 1);
                if (op == PartyOperation.DISBAND) {
                    mplew.writeInt(target.getId());
                } else {
                    mplew.write(op == PartyOperation.EXPEL ? 1 : 0);
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, op == PartyOperation.LEAVE);
                }
                break;
            case JOIN:
                mplew.write(15);
                mplew.writeInt(party.getId());
                mplew.writeMapleAsciiString(target.getName());
                addPartyStatus(forChannel, party, mplew, false);
                break;
            case SILENT_UPDATE:
            case LOG_ONOFF:
                mplew.write(7);
                mplew.writeInt(party.getId());
                addPartyStatus(forChannel, party, mplew, op == PartyOperation.LOG_ONOFF);
                break;
            case CHANGE_LEADER:
            case CHANGE_LEADER_DC:
                mplew.write(31); //test
                mplew.writeInt(target.getId());
                mplew.write(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                break;
            //1D = expel function not available in this map.
        }
        return mplew.getPacket();
    }

    public static MaplePacket partyInvite(MapleCharacter from) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_PartyResult.get());
        mplew.write(4);
        mplew.writeInt(from.getParty().getId());
        mplew.writeMapleAsciiString(from.getName());
        mplew.writeInt(from.getLevel());
        mplew.writeInt(from.getJob());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static MaplePacket partyCreated(int partyid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_PartyResult.get());
        mplew.write(8);
        mplew.writeInt(partyid);
        mplew.writeInt(999999999);
        mplew.writeInt(999999999);
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    private static void addPartyStatus(int forchannel, MapleParty party, LittleEndianWriter lew, boolean leaving) {
        List<MaplePartyCharacter> partymembers = new ArrayList<MaplePartyCharacter>(party.getMembers());
        while (partymembers.size() < 6) {
            partymembers.add(new MaplePartyCharacter());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeAsciiString(partychar.getName(), 13);
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getJobId());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            lew.writeInt(partychar.getLevel());
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.isOnline()) {
                lew.writeInt(partychar.getChannel() - 1);
            } else {
                lew.writeInt(-2);
            }
        }
        lew.writeInt(party.getLeader().getId());
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel) {
                lew.writeInt(partychar.getMapid());
            } else {
                lew.writeInt(0);
            }
        }
        for (MaplePartyCharacter partychar : partymembers) {
            if (partychar.getChannel() == forchannel && !leaving) {
                lew.writeInt(partychar.getDoorTown());
                lew.writeInt(partychar.getDoorTarget());
                lew.writeInt(partychar.getDoorSkill());
                lew.writeInt(partychar.getDoorPosition().x);
                lew.writeInt(partychar.getDoorPosition().y);
            } else {
                lew.writeInt(leaving ? 999999999 : 0);
                lew.writeLong(leaving ? 999999999 : 0);
                lew.writeLong(leaving ? -1 : 0);
            }
        }
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
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(68);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(notice);
        return mplew.getPacket();
    }

    //someone leaving, mode == 0x2c for leaving, 0x2f for expelled
    public static MaplePacket memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(bExpelled ? 47 : 44);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeMapleAsciiString(mgc.getName());
        return mplew.getPacket();
    }

    private static void getGuildInfo(MaplePacketLittleEndianWriter mplew, MapleGuild guild) {
        mplew.writeInt(guild.getId());
        mplew.writeMapleAsciiString(guild.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(guild.getRankTitle(i));
        }
        guild.addMemberData(mplew);
        mplew.writeInt(guild.getCapacity());
        mplew.writeShort(guild.getLogoBG());
        mplew.write(guild.getLogoBGColor());
        mplew.writeShort(guild.getLogo());
        mplew.write(guild.getLogoColor());
        mplew.writeMapleAsciiString(guild.getNotice());
        mplew.writeInt(guild.getGP());
        mplew.writeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
    }

    public static MaplePacket changeAlliance(MapleGuildAlliance alliance, final boolean in) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(1);
        mplew.write(in ? 1 : 0);
        mplew.writeInt(in ? alliance.getId() : 0);
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < noGuilds; i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return ResWrapper.enableActions();
            }
        }
        mplew.write(noGuilds);
        for (int i = 0; i < noGuilds; i++) {
            mplew.writeInt(g[i].getId());
            //must be world
            Collection<MapleGuildCharacter> members = g[i].getMembers();
            mplew.writeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                mplew.writeInt(mgc.getId());
                mplew.write(in ? mgc.getAllianceRank() : 0);
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(60);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());
        return mplew.getPacket();
    }

    public static MaplePacket allianceMemberOnline(int alliance, int gid, int id, boolean online) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(14);
        mplew.writeInt(alliance);
        mplew.writeInt(gid);
        mplew.writeInt(id);
        mplew.write(online ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, final boolean add) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(4);
        mplew.writeInt(add ? alliance.getId() : 0);
        mplew.writeInt(guild.getId());
        Collection<MapleGuildCharacter> members = guild.getMembers();
        mplew.writeInt(members.size());
        for (MapleGuildCharacter mgc : members) {
            mplew.writeInt(mgc.getId());
            mplew.write(add ? mgc.getAllianceRank() : 0);
        }
        return mplew.getPacket();
    }

    public static MaplePacket disbandAlliance(int alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(29);
        mplew.writeInt(alliance);
        return mplew.getPacket();
    }

    public static MaplePacket newGuildMember(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(39);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeAsciiString(StringUtil.getRightPaddedStr(mgc.getName(), '\u0000', 13));
        mplew.writeInt(mgc.getJobId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getGuildRank()); //should be always 5 but whatevs
        mplew.writeInt(mgc.isOnline() ? 1 : 0); //should always be 1 too
        mplew.writeInt(1); //? could be guild signature, but doesn't seem to matter
        mplew.writeInt(mgc.getAllianceRank()); //should always 3
        return mplew.getPacket();
    }

    public static MaplePacket updateAllianceRank(int allianceid, MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(27);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getAllianceRank());
        return mplew.getPacket();
    }

    public static MaplePacket getGuildAlliance(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(13);
        if (alliance == null) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return ResWrapper.enableActions();
            }
        }
        mplew.writeInt(noGuilds);
        for (MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket guildMemberOnline(int gid, int cid, boolean bOnline) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(61);
        mplew.writeInt(gid);
        mplew.writeInt(cid);
        mplew.write(bOnline ? 1 : 0);
        return mplew.getPacket();
    }

    public static MaplePacket changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(2);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static MaplePacket showGuildRanks(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(73);
        mplew.writeInt(npcid);
        mplew.writeInt(all.size());
        for (MapleGuildRanking.GuildRankingInfo info : all) {
            mplew.writeMapleAsciiString(info.getName());
            mplew.writeInt(info.getGP());
            mplew.writeInt(info.getLogo());
            mplew.writeInt(info.getLogoColor());
            mplew.writeInt(info.getLogoBg());
            mplew.writeInt(info.getLogoBgColor());
        }
        return mplew.getPacket();
    }

    public static MaplePacket denyGuildInvitation(String charname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(55);
        mplew.writeMapleAsciiString(charname);
        return mplew.getPacket();
    }

    public static MaplePacket showGuildInfo(MapleCharacter c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(26); //signature for showing guild info
        if (c == null || c.getMGC() == null) {
            //show empty guild (used for leaving, expelled)
            mplew.write(0);
            return mplew.getPacket();
        }
        MapleGuild g = World.Guild.getGuild(c.getGuildId());
        if (g == null) {
            //failed to read from DB - don't show a guild
            mplew.write(0);
            return mplew.getPacket();
        }
        mplew.write(1); //bInGuild
        getGuildInfo(mplew, g);
        return mplew.getPacket();
    }

    public static MaplePacket guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(66);
        mplew.writeInt(gid);
        mplew.writeShort(bg);
        mplew.write(bgcolor);
        mplew.writeShort(logo);
        mplew.write(logocolor);
        return mplew.getPacket();
    }

    private static void addAllianceInfo(MaplePacketLittleEndianWriter mplew, MapleGuildAlliance alliance) {
        mplew.writeInt(alliance.getId());
        mplew.writeMapleAsciiString(alliance.getName());
        for (int i = 1; i <= 5; i++) {
            mplew.writeMapleAsciiString(alliance.getRank(i));
        }
        mplew.write(alliance.getNoGuilds());
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            mplew.writeInt(alliance.getGuildId(i));
        }
        mplew.writeInt(alliance.getCapacity()); // ????
        mplew.writeMapleAsciiString(alliance.getNotice());
    }

    public static MaplePacket guildDisband(int gid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(50);
        mplew.writeInt(gid);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static MaplePacket getAllianceInfo(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(12);
        mplew.write(alliance == null ? 0 : 1); //in an alliance
        if (alliance != null) {
            addAllianceInfo(mplew, alliance);
        }
        return mplew.getPacket();
    }

    public static MaplePacket updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(25);
        mplew.writeInt(allianceid);
        mplew.writeInt(oldLeader);
        mplew.writeInt(newLeader);
        return mplew.getPacket();
    }

    public static MaplePacket addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(18);
        addAllianceInfo(mplew, alliance);
        mplew.writeInt(newGuild.getId()); //???
        getGuildInfo(mplew, newGuild);
        mplew.write(0); //???
        return mplew.getPacket();
    }

    public static MaplePacket updateAlliance(MapleGuildCharacter mgc, int allianceid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(24);
        mplew.writeInt(allianceid);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.writeInt(mgc.getLevel());
        mplew.writeInt(mgc.getJobId());
        return mplew.getPacket();
    }

    public static MaplePacket sendAllianceInvite(String allianceName, MapleCharacter inviter) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(3);
        mplew.writeInt(inviter.getGuildId());
        mplew.writeMapleAsciiString(inviter.getName());
        //alliance invite did NOT change
        mplew.writeMapleAsciiString(allianceName);
        return mplew.getPacket();
    }

    private static void addThread(MaplePacketLittleEndianWriter mplew, MapleBBSThread rs) {
        mplew.writeInt(rs.localthreadID);
        mplew.writeInt(rs.ownerID);
        mplew.writeMapleAsciiString(rs.name);
        mplew.writeLong(TestHelper.getKoreanTimestamp(rs.timestamp));
        mplew.writeInt(rs.icon);
        mplew.writeInt(rs.getReplyCount());
    }

    public static MaplePacket showThread(MapleBBSThread thread) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildBBS.get());
        mplew.write(7);
        mplew.writeInt(thread.localthreadID);
        mplew.writeInt(thread.ownerID);
        mplew.writeLong(TestHelper.getKoreanTimestamp(thread.timestamp));
        mplew.writeMapleAsciiString(thread.name);
        mplew.writeMapleAsciiString(thread.text);
        mplew.writeInt(thread.icon);
        mplew.writeInt(thread.getReplyCount());
        for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
            mplew.writeInt(reply.replyid);
            mplew.writeInt(reply.ownerID);
            mplew.writeLong(TestHelper.getKoreanTimestamp(reply.timestamp));
            mplew.writeMapleAsciiString(reply.content);
        }
        return mplew.getPacket();
    }

    public static MaplePacket BBSThreadList(final List<MapleBBSThread> bbs, int start) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildBBS.get());
        mplew.write(6);
        if (bbs == null) {
            mplew.write(0);
            mplew.writeLong(0);
            return mplew.getPacket();
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
        mplew.write(ret);
        if (notice != null) {
            //has a notice
            addThread(mplew, notice);
            threadCount--; //one thread didn't count (because it's a notice)
        }
        if (threadCount < start) {
            //seek to the thread before where we start
            //uh, we're trying to start at a place past possible
            start = 0;
        }
        //each page has 10 threads, start = page # in packet but not here
        mplew.writeInt(threadCount);
        final int pages = Math.min(10, threadCount - start);
        mplew.writeInt(pages);
        for (int i = 0; i < pages; i++) {
            addThread(mplew, bbs.get(start + i + ret)); //because 0 = notice
        }
        return mplew.getPacket();
    }

    public static MaplePacket createGuildAlliance(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(15);
        addAllianceInfo(mplew, alliance);
        final int noGuilds = alliance.getNoGuilds();
        MapleGuild[] g = new MapleGuild[noGuilds];
        for (int i = 0; i < alliance.getNoGuilds(); i++) {
            g[i] = World.Guild.getGuild(alliance.getGuildId(i));
            if (g[i] == null) {
                return ResWrapper.enableActions();
            }
        }
        for (MapleGuild gg : g) {
            getGuildInfo(mplew, gg);
        }
        return mplew.getPacket();
    }

    public static MaplePacket rankTitleChange(int gid, String[] ranks) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(62);
        mplew.writeInt(gid);
        for (String r : ranks) {
            mplew.writeMapleAsciiString(r);
        }
        return mplew.getPacket();
    }

    public static MaplePacket genericGuildMessage(byte code) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(code);
        return mplew.getPacket();
    }

    public static MaplePacket removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(16);
        addAllianceInfo(mplew, alliance);
        getGuildInfo(mplew, expelledGuild);
        mplew.write(expelled ? 1 : 0); //1 = expelled, 0 = left
        return mplew.getPacket();
    }

    public static MaplePacket guildCapacityChange(int gid, int capacity) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(58);
        mplew.writeInt(gid);
        mplew.write(capacity);
        return mplew.getPacket();
    }

    public static MaplePacket updateGP(int gid, int GP) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(72);
        mplew.writeInt(gid);
        mplew.writeInt(GP);
        return mplew.getPacket();
    }

    public static MaplePacket getAllianceUpdate(MapleGuildAlliance alliance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(23);
        addAllianceInfo(mplew, alliance);
        return mplew.getPacket();
    }

    public static MaplePacket guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(5);
        mplew.writeInt(gid);
        mplew.writeMapleAsciiString(charName);
        mplew.writeInt(levelFrom);
        mplew.writeInt(jobFrom);
        return mplew.getPacket();
    }

    public static MaplePacket changeRank(MapleGuildCharacter mgc) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GuildResult.get());
        mplew.write(64);
        mplew.writeInt(mgc.getGuildId());
        mplew.writeInt(mgc.getId());
        mplew.write(mgc.getGuildRank());
        return mplew.getPacket();
    }

    public static MaplePacket changeAllianceRank(int allianceid, MapleGuildCharacter player) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AllianceResult.get());
        mplew.write(5);
        mplew.writeInt(allianceid);
        mplew.writeInt(player.getId());
        mplew.writeInt(player.getAllianceRank());
        return mplew.getPacket();
    }

    public static MaplePacket sendFamilyJoinResponse(boolean accepted, String added) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilyJoinRequestResult.get());
        mplew.write(accepted ? 1 : 0);
        mplew.writeMapleAsciiString(added);
        return mplew.getPacket();
    }

    public static MaplePacket changeRep(int r) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilyFamousPointIncResult.get());
        mplew.writeInt(r);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static MaplePacket sendFamilyInvite(int cid, int otherLevel, int otherJob, String inviter) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilyJoinRequest.get());
        mplew.writeInt(cid); //the inviter
        mplew.writeInt(otherLevel);
        mplew.writeInt(otherJob);
        mplew.writeMapleAsciiString(inviter);
        return mplew.getPacket();
    }

    public static void addFamilyCharInfo(MapleFamilyCharacter ldr, MaplePacketLittleEndianWriter mplew) {
        mplew.writeInt(ldr.getId());
        mplew.writeInt(ldr.getSeniorId());
        mplew.writeShort(ldr.getJobId());
        mplew.write(ldr.getLevel());
        mplew.write(ldr.isOnline() ? 1 : 0);
        mplew.writeInt(ldr.getCurrentRep());
        mplew.writeInt(ldr.getTotalRep());
        mplew.writeInt(ldr.getTotalRep()); //recorded rep to senior
        mplew.writeInt(ldr.getTotalRep()); //then recorded rep to sensen
        mplew.writeLong(Math.max(ldr.getChannel(), 0)); //channel->time online
        mplew.writeMapleAsciiString(ldr.getName());
    }

    public static MaplePacket familyLoggedIn(boolean online, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilyNotifyLoginOrLogout.get());
        mplew.write(online ? 1 : 0);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static MaplePacket familySummonRequest(String name, String mapname) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilySummonRequest.get());
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(mapname);
        return mplew.getPacket();
    }

    public static MaplePacket cancelFamilyBuff() {
        return familyBuff(0, 0, 0, 0);
    }

    public static MaplePacket getFamilyData() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilyPrivilegeList.get());
        List<MapleFamilyBuff.MapleFamilyBuffEntry> entries = MapleFamilyBuff.getBuffEntry();
        mplew.writeInt(entries.size()); // Number of events
        for (MapleFamilyBuff.MapleFamilyBuffEntry entry : entries) {
            mplew.write(entry.type);
            mplew.writeInt(entry.rep);
            mplew.writeInt(entry.count);
            mplew.writeMapleAsciiString(entry.name);
            mplew.writeMapleAsciiString(entry.desc);
        }
        return mplew.getPacket();
    }

    public static MaplePacket familyBuff(int type, int buffnr, int amount, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilySetPrivilege.get());
        mplew.write(type);
        if (type >= 2 && type <= 4) {
            mplew.writeInt(buffnr);
            //first int = exp, second int = drop
            mplew.writeInt(type == 3 ? 0 : amount);
            mplew.writeInt(type == 2 ? 0 : amount);
            mplew.write(0);
            mplew.writeInt(time);
        }
        return mplew.getPacket();
    }

    public static MaplePacket getFamilyPedigree(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilyChartResult.get());
        mplew.writeInt(chr.getId());
        MapleFamily family = World.Family.getFamily(chr.getFamilyId());
        int descendants = 2;
        int gens = 0;
        int generations = 0;
        if (family == null) {
            mplew.writeInt(2);
            addFamilyCharInfo(new MapleFamilyCharacter(chr, 0, 0, 0, 0), mplew); //leader
        } else {
            mplew.writeInt(family.getMFC(chr.getId()).getPedigree().size() + 1); //+ 1 for leader, but we don't want leader seeing all msgs
            addFamilyCharInfo(family.getMFC(family.getLeaderId()), mplew);
            if (chr.getSeniorId() > 0) {
                MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                if (senior.getSeniorId() > 0) {
                    addFamilyCharInfo(family.getMFC(senior.getSeniorId()), mplew);
                }
                addFamilyCharInfo(senior, mplew);
            }
        }
        addFamilyCharInfo(chr.getMFC() == null ? new MapleFamilyCharacter(chr, 0, 0, 0, 0) : chr.getMFC(), mplew);
        if (family != null) {
            if (chr.getSeniorId() > 0) {
                MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                if (senior != null) {
                    if (senior.getJunior1() > 0 && senior.getJunior1() != chr.getId()) {
                        addFamilyCharInfo(family.getMFC(senior.getJunior1()), mplew);
                    } else if (senior.getJunior2() > 0 && senior.getJunior2() != chr.getId()) {
                        addFamilyCharInfo(family.getMFC(senior.getJunior2()), mplew);
                    }
                }
            }
            if (chr.getJunior1() > 0) {
                addFamilyCharInfo(family.getMFC(chr.getJunior1()), mplew);
            }
            if (chr.getJunior2() > 0) {
                addFamilyCharInfo(family.getMFC(chr.getJunior2()), mplew);
            }
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior.getJunior1() > 0) {
                    descendants++;
                    addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                }
                if (junior.getJunior2() > 0) {
                    descendants++;
                    addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior.getJunior1() > 0) {
                    descendants++;
                    addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                }
                if (junior.getJunior2() > 0) {
                    descendants++;
                    addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                }
            }
            gens = family.getGens();
            generations = family.getMemberSize();
        }
        mplew.writeLong(descendants);
        mplew.writeInt(gens);
        mplew.writeInt(-1);
        mplew.writeInt(generations);
        if (family != null) {
            if (chr.getJunior1() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                if (junior.getJunior1() > 0) {
                    mplew.writeInt(junior.getJunior1());
                    mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                }
                if (junior.getJunior2() > 0) {
                    mplew.writeInt(junior.getJunior2());
                    mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                }
            }
            if (chr.getJunior2() > 0) {
                MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                if (junior.getJunior1() > 0) {
                    mplew.writeInt(junior.getJunior1());
                    mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                }
                if (junior.getJunior2() > 0) {
                    mplew.writeInt(junior.getJunior2());
                    mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                }
            }
        }
        List<Pair<Integer, Integer>> b = chr.usedBuffs();
        mplew.writeInt(b.size());
        for (Pair<Integer, Integer> ii : b) {
            mplew.writeInt(ii.getLeft()); //buffid
            mplew.writeInt(ii.getRight()); //times used
        }
        mplew.writeShort(2);
        return mplew.getPacket();
    }

    public static MaplePacket getFamilyInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilyInfoResult.get());
        mplew.writeInt(chr.getCurrentRep()); //rep
        mplew.writeInt(chr.getTotalRep()); // total rep
        mplew.writeInt(chr.getTotalRep()); //rep recorded today
        mplew.writeShort(chr.getNoJuniors());
        mplew.writeShort(2);
        mplew.writeShort(chr.getNoJuniors());
        MapleFamily family = World.Family.getFamily(chr.getFamilyId());
        if (family != null) {
            mplew.writeInt(family.getLeaderId()); //??? 9D 60 03 00
            mplew.writeMapleAsciiString(family.getLeaderName());
            mplew.writeMapleAsciiString(family.getNotice()); //message?
        } else {
            mplew.writeLong(0);
        }
        List<Pair<Integer, Integer>> b = chr.usedBuffs();
        mplew.writeInt(b.size());
        for (Pair<Integer, Integer> ii : b) {
            mplew.writeInt(ii.getLeft()); //buffid
            mplew.writeInt(ii.getRight()); //times used
        }
        return mplew.getPacket();
    }

    public static MaplePacket getSeniorMessage(String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_FamilyJoinAccepted.get());
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    // CWvsContext::OnFriendResult
    public static MaplePacket FriendResult(ArgFriend frs) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_FriendResult);
        sp.Encode1(frs.flag.get());
        switch (frs.flag) {
            case FriendRes_LoadFriend_Done:
            case FriendRes_SetFriend_Done:
            case FriendRes_DeleteFriend_Done: {
                sp.EncodeBuffer(Reset_Encode(frs.chr));
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
                Debug.ErrorLog("FriendResult not coded : " + frs.flag);
                break;
            }
        }
        return sp.get();
    }

    // CWvsContext::CFriend::Reset
    public static byte[] Reset_Encode(MapleCharacter chr) {
        Collection<BuddylistEntry> friend_list = chr.getBuddylist().getBuddies();
        ServerPacket data_friend = new ServerPacket();
        ServerPacket data_in_shop = new ServerPacket();
        for (BuddylistEntry friend : friend_list) {
            // 39 bytes
            data_friend.Encode4(friend.getCharacterId());
            data_friend.EncodeBuffer(friend.getName(), 13);
            data_friend.Encode1(0);
            data_friend.Encode4(friend.getChannel() == -1 ? -1 : friend.getChannel() - 1);
            data_friend.EncodeBuffer(friend.getGroup(), 17);
            // 4 bytes
            data_in_shop.Encode4(0);
        }
        ServerPacket data = new ServerPacket();
        data.Encode1(friend_list.size());
        data.EncodeBuffer(data_friend.get().getBytes());
        data.EncodeBuffer(data_in_shop.get().getBytes());
        return data.get().getBytes();
    }

    public static MaplePacket getOwlOpen() {
        //best items! hardcoded
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ShopScannerResult.get());
        mplew.write(7);
        mplew.write(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            mplew.writeInt(i);
        } //these are the most searched items. too lazy to actually make
        return mplew.getPacket();
    }

    public static MaplePacket getOwlSearched(final int itemSearch, final List<HiredMerchant> hms) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ShopScannerResult.get());
        mplew.write(6);
        mplew.writeInt(0);
        mplew.writeInt(itemSearch);
        int size = 0;
        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }
        mplew.writeInt(size);
        for (HiredMerchant hm : hms) {
            final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
            for (MaplePlayerShopItem item : items) {
                mplew.writeMapleAsciiString(hm.getOwnerName());
                mplew.writeInt(hm.getMap().getId());
                mplew.writeMapleAsciiString(hm.getDescription());
                mplew.writeInt(item.item.getQuantity()); //I THINK.
                mplew.writeInt(item.bundles); //I THINK.
                mplew.writeInt(item.price);
                switch (InventoryHandler.OWL_ID) {
                    case 0:
                        mplew.writeInt(hm.getOwnerId()); //store ID
                        break;
                    case 1:
                        mplew.writeInt(hm.getStoreId());
                        break;
                    default:
                        mplew.writeInt(hm.getObjectId());
                        break;
                }
                mplew.write(hm.getFreeSlot() == -1 ? 1 : 0);
                mplew.write(GameConstants.getInventoryType(itemSearch).getType()); //position?
                if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                    TestHelper.addItemInfo(mplew, item.item, true, true);
                }
            }
        }
        return mplew.getPacket();
    }

    public static final MaplePacket sendTitleBox() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_EntrustedShopCheckResult.get());
        /*
        0x07    店を開く
        0x09    プレドリックから～
        0x0A    他のキャラクターがアイテムを使用中
        0x0B    今は開店できません
         */
        mplew.write(7);
        return mplew.getPacket();
    }

    public static MaplePacket useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SkillLearnItemResult.get());
        mplew.write(0); //?
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeInt(maxlevel);
        mplew.write(canuse ? 1 : 0);
        mplew.write(success ? 1 : 0);
        return mplew.getPacket();
    }

    public static final MaplePacket getSlotUpdate(byte invType, byte newSlots) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryGrow.get());
        mplew.write(invType);
        mplew.write(newSlots);
        return mplew.getPacket();
    }

    public static MaplePacket followRequest(int chrid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetPassenserRequest.get());
        mplew.writeInt(chrid);
        return mplew.getPacket();
    }

    public static final MaplePacket temporaryStats_Reset() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ForcedStatReset.get());
        return mplew.getPacket();
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
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_ForcedStatSet.get());
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
        mplew.writeInt(updateMask);
        Integer value;
        for (final Pair<MapleStat.Temp, Integer> statupdate : mystats) {
            value = statupdate.getLeft().getValue();
            if (value >= 1) {
                if (value <= 512) {
                    //level 0x10 - is this really short or some other? (FF 00)
                    mplew.writeShort(statupdate.getRight().shortValue());
                } else {
                    mplew.write(statupdate.getRight().byteValue());
                }
            }
        }
        return mplew.getPacket();
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
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_NotifyLevelUp.get());
        mplew.write(family ? 1 : 2);
        mplew.writeInt(level);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static MaplePacket sendJobup(boolean family, int jobid, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_NotifyJobChange.get());
        mplew.write(family ? 1 : 0);
        mplew.writeInt(jobid); //or is this a short
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static MaplePacket sendMarriage(boolean family, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_NotifyWedding.get());
        mplew.write(family ? 1 : 0);
        mplew.writeMapleAsciiString(name);
        return mplew.getPacket();
    }

    public static MaplePacket giveFameResponse(int mode, String charname, int newfame) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GivePopularityResult.get());
        mplew.write(0);
        mplew.writeMapleAsciiString(charname);
        mplew.write(mode);
        mplew.writeShort(newfame);
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static MaplePacket giveFameErrorResponse(int status) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /*	* 0: ok, use giveFameResponse<br>
         * 1: the username is incorrectly entered<br>
         * 2: users under level 15 are unable to toggle with fame.<br>
         * 3: can't raise or drop fame anymore today.<br>
         * 4: can't raise or drop fame for this character for this month anymore.<br>
         * 5: received fame, use receiveFame()<br>
         * 6: level of fame neither has been raised nor dropped due to an unexpected error*/
        mplew.writeShort(ServerPacket.Header.LP_GivePopularityResult.get());
        mplew.write(status);
        return mplew.getPacket();
    }

    public static MaplePacket receiveFame(int mode, String charnameFrom) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_GivePopularityResult.get());
        mplew.write(5);
        mplew.writeMapleAsciiString(charnameFrom);
        mplew.write(mode);
        return mplew.getPacket();
    }

    public static MaplePacket sendEngagement(final byte msg, final int item, final MapleCharacter male, final MapleCharacter female) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
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
        mplew.writeShort(ServerPacket.Header.LP_MarriageResult.get());
        mplew.write(msg); // 1103 custom quest
        switch (msg) {
            case 11: {
                mplew.writeInt(0); // ringid or uniqueid
                mplew.writeInt(male.getId());
                mplew.writeInt(female.getId());
                mplew.writeShort(1); //always
                mplew.writeInt(item);
                mplew.writeInt(item); // wtf?repeat?
                mplew.writeAsciiString(male.getName(), 13);
                mplew.writeAsciiString(female.getName(), 13);
                break;
            }
        }
        return mplew.getPacket();
    }

    public static MaplePacket sendEngagementRequest(String name, int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_MarriageRequest.get());
        mplew.write(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
        mplew.writeMapleAsciiString(name); // name
        mplew.writeInt(cid); // playerid
        return mplew.getPacket();
    }

    public static MaplePacket getPeanutResult(int itemId, short quantity, int itemId2, short quantity2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_IncubatorResult.get());
        mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeInt(5060003);
        mplew.writeInt(itemId2);
        mplew.writeInt(quantity2);
        return mplew.getPacket();
    }

    public static MaplePacket yellowChat(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_SetWeekEventMessage.get());
        mplew.write(-1); //could be something like mob displaying message.
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    public static MaplePacket getShowQuestCompletion(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_QuestClear.get());
        mplew.writeShort(id);
        return mplew.getPacket();
    }

    public static MaplePacket getAvatarMega(MapleCharacter chr, int channel, int itemId, String message, boolean ear) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_AvatarMegaphoneUpdateMessage.get());
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeMapleAsciiString(message);
        mplew.writeInt(channel - 1); // channel
        mplew.write(ear ? 1 : 0);
        TestHelper.addCharLook(mplew, chr, true);
        return mplew.getPacket();
    }

    public static MaplePacket fairyPendantMessage(int type, int percent) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_BonusExpRateChanged.get());
        mplew.writeShort(21); // 0x15
        mplew.writeInt(0); // idk
        mplew.writeShort(0); // idk
        mplew.writeShort(percent); // percent
        mplew.writeShort(0); // idk
        return mplew.getPacket();
    }

    public static final MaplePacket sendString(final int type, final String object, final String amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        switch (type) {
            case 1:
                mplew.writeShort(ServerPacket.Header.LP_SessionValue.get());
                break;
            case 2:
                mplew.writeShort(ServerPacket.Header.LP_PartyValue.get());
                break;
            case 3:
                mplew.writeShort(ServerPacket.Header.LP_FieldSetVariable.get());
                break;
        }
        mplew.writeMapleAsciiString(object); //massacre_hit, massacre_cool, massacre_miss, massacre_party, massacre_laststage, massacre_skill
        mplew.writeMapleAsciiString(amount);
        return mplew.getPacket();
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
