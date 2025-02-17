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
package packet.response.wrapper;

import client.MapleCharacter;
import client.MapleQuestStatus;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import config.ServerConfig;
import constants.GameConstants;
import handling.MaplePacket;
import java.util.List;
import packet.ServerPacket;
import packet.ops.OpsBroadcastMsg;
import packet.ops.OpsDropPickUpMessage;
import packet.ops.OpsFieldEffect;
import packet.ops.OpsFieldEffectArg;
import packet.ops.OpsFriend;
import packet.ops.OpsFriendArg;
import packet.ops.OpsMessage;
import packet.ops.OpsMessageArg;
import packet.ops.OpsQuestRecordMessage;
import packet.ops.OpsScriptMan;
import packet.response.ResCField;
import packet.response.ResCScriptMan;
import packet.response.ResCStage;
import packet.response.ResCWvsContext;
import packet.response.struct.GW_ItemSlotBase;
import packet.response.struct.InvOp;
import packet.response.struct.TestHelper;
import server.Randomizer;
import server.maps.MapleMap;
import tools.HexTool;
import tools.StringUtil;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 *
 * @author Riremito
 */
public class ResWrapper {

    public static MaplePacket getInventoryFull() {
        return ResCWvsContext.InventoryOperation(true, null);
    }

    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item) {
        return addInventorySlot(type, item, false);
    }

    public static MaplePacket addInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        InvOp io = new InvOp();
        io.add(type, item);
        return ResCWvsContext.InventoryOperation(fromDrop, io);
    }

    public static MaplePacket updateInventorySlot(MapleInventoryType type, IItem item, boolean fromDrop) {
        InvOp io = new InvOp();
        io.update(type, item);
        return ResCWvsContext.InventoryOperation(fromDrop, io);
    }

    public static MaplePacket dropInventoryItemUpdate(MapleInventoryType type, IItem item) {
        InvOp io = new InvOp();
        io.update(type, item);
        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static final MaplePacket updatePet(final MaplePet pet, final IItem item) {
        InvOp io = new InvOp();
        // ペットと装備の更新時はアイテムを削除する必要はなく、同一スロットにアイテムを追加するだけで良い
        // アイテム削除を行うとペットと装備固有のクエストが再発生する
        io.add(MapleInventoryType.CASH, item);
        return ResCWvsContext.InventoryOperation(false, io);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, int src, int dst) {
        return moveInventoryItem(type, src, dst, (byte) -1);
    }

    public static MaplePacket moveInventoryItem(MapleInventoryType type, int src, int dst, short equipIndicator) {
        InvOp io = new InvOp();
        io.move(type, src, dst);
        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket dropInventoryItem(MapleInventoryType type, short src) {
        InvOp io = new InvOp();
        io.remove(type, src);
        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket clearInventoryItem(MapleInventoryType type, short slot, boolean fromDrop) {
        InvOp io = new InvOp();
        io.remove(type, slot);
        return ResCWvsContext.InventoryOperation(fromDrop, io);
    }

    public static MaplePacket scrolledItem(IItem scroll, IItem item, boolean destroyed, boolean potential) {
        InvOp io = new InvOp();

        // 書
        if (0 < scroll.getQuantity()) {
            io.update(GameConstants.getInventoryType(scroll.getItemId()), scroll);
        } else {
            io.remove(GameConstants.getInventoryType(scroll.getItemId()), scroll.getPosition());
        }

        // 装備
        if (!destroyed) {
            //io.remove(GameConstants.getInventoryType(item.getItemId()), item.getPosition());
            io.add(GameConstants.getInventoryType(item.getItemId()), item);
        } else {
            io.remove(GameConstants.getInventoryType(item.getItemId()), item.getPosition());
        }

        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket updateSpecialItemUse(IItem item, byte invType) {
        return updateSpecialItemUse(item, invType, item.getPosition());
    }

    public static MaplePacket updateSpecialItemUse(IItem item, byte invType, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.get());
        mplew.write(0); // could be from drop
        mplew.write(2); // always 2
        mplew.write(3); // quantity > 0 (?)
        mplew.write(invType); // Inventory type
        mplew.writeShort(pos); // item slot
        mplew.write(0);
        mplew.write(invType);
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        TestHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(2); //?
        }
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeWithRestInventoryItem(MapleInventoryType type, short src, short dst, short srcQ, short dstQ) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.get());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 01"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.writeShort(srcQ);
        mplew.write(HexTool.getByteArrayFromHexString("01"));
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(dstQ);
        return mplew.getPacket();
    }

    public static MaplePacket moveAndMergeInventoryItem(MapleInventoryType type, short src, short dst, short total) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.get());
        mplew.write(HexTool.getByteArrayFromHexString("01 02 03"));
        mplew.write(type.getType());
        mplew.writeShort(src);
        mplew.write(1); // merge mode?
        mplew.write(type.getType());
        mplew.writeShort(dst);
        mplew.writeShort(total);
        return mplew.getPacket();
    }

    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType) {
        return updateSpecialItemUse_(item, invType, item.getPosition());
    }

    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType, short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.get());
        mplew.write(0); // could be from drop
        mplew.write(1); // always 2
        mplew.write(0); // quantity > 0 (?)
        mplew.write(invType); // Inventory type
        if (item.getType() == 1) {
            mplew.writeShort(pos);
        } else {
            mplew.write(pos);
        }
        TestHelper.addItemInfo(mplew, item, true, true);
        if (item.getPosition() < 0) {
            mplew.write(1); //?
        }
        return mplew.getPacket();
    }

    public static MaplePacket getShowInventoryFull() {
        return getShowInventoryStatus(OpsDropPickUpMessage.PICKUP_INVENTORY_FULL);
    }

    // メガホン
    public static MaplePacket MegaphoneBlue(String text) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.BM_SPEAKERCHANNEL.get());
        p.EncodeStr(text);
        return p.get();
    }

    // ドクロ拡声器
    public static MaplePacket MegaphoneSkull(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.BM_SKULLSPEAKER.get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.get();
    }

    // ハート拡声器
    public static MaplePacket MegaphoneHeart(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.BM_HEARTSPEAKER.get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.get();
    }

    // アイテム拡声器
    public static MaplePacket MegaphoneItem(String text, byte channel, byte ear, byte showitem, IItem item) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.BM_ITEMSPEAKER.get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        p.Encode1(showitem);
        if (showitem != 0) {
            p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        }
        return p.get();
    }

    // 三連拡声器
    public static MaplePacket MegaphoneTriple(List<String> text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.MEGAPHONE_TRIPLE.get());
        // 1行目
        p.EncodeStr(text.get(0));
        p.Encode1((byte) text.size());
        for (int i = 1; i < text.size(); i++) {
            p.EncodeStr(text.get(i));
        }
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.get();
    }

    // 拡声器
    public static MaplePacket Megaphone(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.BM_SPEAKERWORLD.get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.get();
    }

    public static final MaplePacket GainEXP_Monster(final int gain, final boolean white, final int partyinc, final int Class_Bonus_EXP, final int Equipment_Bonus_EXP, final int Premium_Bonus_EXP) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_IncEXPMessage;
        ma.Inc_EXP_TextColor = white ? 1 : 0;
        ma.Inc_EXP = gain;
        ma.InChat = 0;
        ma.Inc_EXP_EventBonus = 0;
        ma.Inc_EXP_WeddingBonus = 0;
        ma.Inc_EXP_PartyBonus = partyinc;
        ma.Inc_EXP_EquipmentBonus = Equipment_Bonus_EXP;
        ma.Inc_EXP_PremiumBonus = Premium_Bonus_EXP;
        ma.Inc_EXP_RainbowWeekBonus = 0;
        ma.Inc_EXP_ClassBonus = Class_Bonus_EXP;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket updateQuestMobKills(final MapleQuestStatus status) {
        final StringBuilder sb = new StringBuilder();
        for (final int kills : status.getMobKills().values()) {
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
        }
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_QuestRecordMessage;
        ma.QuestID = (short) status.getQuest().getId();
        ma.qt = OpsQuestRecordMessage.QUEST_UPDATE;
        ma.str = sb.toString();
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket showGainCard(int itemid) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_DropPickUpMessage;
        ma.dt = OpsDropPickUpMessage.PICKUP_MONSTER_CARD;
        ma.ItemID = itemid;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket getGPMsg(int inc_gp) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_IncGPMessage;
        ma.Inc_GP = inc_gp;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket getStatusMsg(int itemid) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_GiveBuffMessage;
        ma.ItemID = itemid;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket getSPMsg(byte inc_sp, short jobid) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_IncSPMessage;
        ma.JobID = jobid;
        ma.Inc_SP = inc_sp;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket updateInfoQuest(final int quest, final String data) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_QuestRecordExMessage;
        ma.QuestID = (short) quest;
        ma.str = data;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket getShowFameGain(int inc_fame) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_IncPOPMessage;
        ma.Inc_Fame = inc_fame;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket DropPickUpMessage(int itemId, short quantity) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_DropPickUpMessage;
        ma.dt = OpsDropPickUpMessage.PICKUP_ITEM;
        ma.ItemID = itemId;
        ma.Inc_ItemCount = quantity;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket getShowInventoryStatus(OpsDropPickUpMessage dm) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_DropPickUpMessage;
        ma.dt = dm;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket GainEXP_Others(final int gain, final boolean inChat, final boolean white) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_IncEXPMessage;
        ma.Inc_EXP_TextColor = white ? 1 : 0;
        ma.Inc_EXP = gain;
        ma.InChat = inChat ? 1 : 0;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket showMesoGain(int gain, boolean inChat) {
        if (!inChat) {
            OpsMessageArg ma = new OpsMessageArg();
            ma.mt = OpsMessage.MS_DropPickUpMessage;
            ma.dt = OpsDropPickUpMessage.PICKUP_MESO;
            ma.Inc_Meso = gain;
            return ResCWvsContext.Message(ma);
        }
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_IncMoneyMessage;
        ma.Inc_Meso = gain;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket GainTamaMessage(int inc_tama) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_JMS_Pachinko;
        ma.Inc_Tama = inc_tama;
        return ResCWvsContext.Message(ma);
    }
    // CWvsContext::OnOpenFullClientDownloadLink
    // CWvsContext::OnMemoResult
    // CWvsContext::OnMapTransferResult
    // CWvsContext::OnAntiMacroResult
    // CWvsContext::OnClaimResult
    // CWvsContext::OnSetClaimSvrAvailableTime
    // CWvsContext::OnClaimSvrStatusChanged
    // CWvsContext::OnSetTamingMobInfo
    // CWvsContext::OnQuestClear
    // CWvsContext::OnEntrustedShopCheckResult
    // CWvsContext::OnSkillLearnItemResult
    // CWvsContext::OnSkillResetItemResult
    // CWvsContext::OnGatherItemResult
    // CWvsContext::OnSortItemResult
    // CWvsContext::OnSueCharacterResult
    // CWvsContext::OnTradeMoneyLimit
    // CWvsContext::OnSetGender
    // CWvsContext::OnGuildBBSPacket
    // CWvsContext::OnCharacterInfo
    // CWvsContext::OnPartyResult
    // CWvsContext::OnExpedtionResult
    // CWvsContext::OnFriendResult
    // CWvsContext::OnGuildResult
    // CWvsContext::OnAllianceResult
    // CWvsContext::OnTownPortal
    // CWvsContext::OnOpenGate
    // CWvsContext::OnBroadcastMsg
    // CWvsContext::OnIncubatorResult
    // CWvsContext::OnShopScannerResult
    // CWvsContext::OnShopLinkResult
    // CWvsContext::OnMarriageRequest
    // CWvsContext::OnMarriageResult
    // CWvsContext::OnWeddingGiftResult
    // CWvsContext::OnNotifyMarriedPartnerMapTransfer
    // CWvsContext::OnCashPetFoodResult
    // CWvsContext::OnSetWeekEventMessage
    // CWvsContext::OnSetPotionDiscountRate
    // CWvsContext::OnBridleMobCatchFail
    // CWvsContext::OnImitatedNPCResult
    // CWvsContext::OnImitatedNPCData
    // CWvsContext::OnLimitedNPCDisableInfo
    // CWvsContext::OnMonsterBookSetCard
    // CWvsContext::OnMonsterBookSetCover
    // CWvsContext::OnHourChanged
    // CWvsContext::OnMiniMapOnOff
    // CWvsContext::OnConsultAuthkeyUpdate
    // CWvsContext::OnClassCompetitionAuthkeyUpdate
    // CWvsContext::OnWebBoardAuthkeyUpdate
    // CWvsContext::OnSessionValue
    // CWvsContext::OnPartyValue
    // CWvsContext::OnFieldSetVariable
    // CWvsContext::OnBonusExpRateChanged
    // CWvsContext::OnPotionDiscountRateChanged
    // CWvsContext::OnFamilyChartResult
    // CWvsContext::OnFamilyInfoResult
    // CWvsContext::OnFamilyResult
    // CWvsContext::OnFamilyJoinRequest
    // CWvsContext::OnFamilyJoinRequestResult
    // CWvsContext::OnFamilyJoinAccepted
    // CWvsContext::OnFamilyPrivilegeList
    // CWvsContext::OnFamilyFamousPointIncResult
    // CWvsContext::OnFamilyNotifyLoginOrLogout
    // CWvsContext::OnFamilySetPrivilege
    // CWvsContext::OnFamilySummonRequest
    // CWvsContext::OnNotifyLevelUp
    // CWvsContext::OnNotifyWedding
    // CWvsContext::OnNotifyJobChange
    // CWvsContext::OnMapleTVUseRes
    // CWvsContext::OnAvatarMegaphoneRes
    // CWvsContext::OnSetAvatarMegaphone
    // CWvsContext::OnClearAvatarMegaphone
    // CWvsContext::OnCancelNameChangeResult
    // CWvsContext::OnCancelTransferWorldResult
    // CWvsContext::OnDestroyShopResult
    // CWvsContext::OnFakeGMNotice
    // CWvsContext::OnSuccessInUsegachaponBox
    // CWvsContext::OnNewYearCardRes
    // CWvsContext::OnRandomMorphRes
    // CWvsContext::OnCancelNameChangebyOther
    // CWvsContext::OnSetBuyEquipExt
    // CWvsContext::OnSetPassenserRequest
    // CWvsContext::OnScriptProgressMessage
    // CWvsContext::OnDataCRCCheckFailed
    // CWvsContext::OnCakePieEventResult
    // CWvsContext::OnUpdateGMBoard
    // CWvsContext::OnShowSlotMessage
    // CWvsContext::OnWildHunterInfo
    // CWvsContext::OnAccountMoreInfo
    // CWvsContext::OnFindFirend
    // CWvsContext::OnStageChange
    // CWvsContext::OnDragonBallBox
    // CWvsContext::OnAskWhetherUsePamsSong
    // CWvsContext::OnTransferChannel
    // CWvsContext::OnDisallowedDeliveryQuestList
    // CWvsContext::OnMacroSysDataInit

    public static final MaplePacket updateQuest(final MapleQuestStatus quest) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_QuestRecordMessage;
        ma.QuestID = (short) quest.getQuest().getId();
        ma.qt = OpsQuestRecordMessage.get(quest.getStatus());
        ma.str = quest.getCustomData() != null ? quest.getCustomData() : "";
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket showQuestMsg(String msg) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_SystemMessage;
        ma.str = msg;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket itemExpired(int itemid) {
        OpsMessageArg ma = new OpsMessageArg();
        ma.mt = OpsMessage.MS_CashItemExpireMessage;
        ma.ItemID = itemid;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket MapNameDisplay(final int mapid) {
        return ResCField.FieldEffect(new OpsFieldEffectArg(OpsFieldEffect.FieldEffect_Screen, "maplemap/enter/" + mapid));
    }

    public static MaplePacket playSound(String sound) {
        return ResCField.FieldEffect(new OpsFieldEffectArg(OpsFieldEffect.FieldEffect_Sound, sound));
    }

    public static MaplePacket showEffect(String effect) {
        return ResCField.FieldEffect(new OpsFieldEffectArg(OpsFieldEffect.FieldEffect_Screen, effect));
    }

    public static MaplePacket musicChange(String song) {
        return ResCField.FieldEffect(new OpsFieldEffectArg(OpsFieldEffect.FieldEffect_ChangeBGM, song));
    }

    public static MaplePacket environmentChange(String env, int mode) {
        return ResCField.FieldEffect(new OpsFieldEffectArg(OpsFieldEffect.find(mode), env));
    }

    // test
    public static void MiroSlot(MapleCharacter chr) {
        chr.SendPacket(showEffect("miro/frame"));
        chr.SendPacket(showEffect("miro/RR1/" + Randomizer.nextInt(4)));
        chr.SendPacket(showEffect("miro/RR2/" + Randomizer.nextInt(4)));
        chr.SendPacket(showEffect("miro/RR3/" + Randomizer.nextInt(5)));
        chr.SendPacket(playSound("quest2288/" + Randomizer.nextInt(9))); // test bgm
    }

    public static MaplePacket updateBuddylist(MapleCharacter chr) {
        OpsFriendArg frs = new OpsFriendArg();
        frs.flag = OpsFriend.FriendRes_LoadFriend_Done;
        frs.chr = chr;
        return ResCWvsContext.FriendResult(frs);
    }

    public static MaplePacket requestBuddylistAdd(int friend_id, String name, int level, int job) {
        OpsFriendArg frs = new OpsFriendArg();
        frs.flag = OpsFriend.FriendRes_Invite;
        frs.friend_id = friend_id;
        frs.friend_channel = 0; // todo
        frs.friend_name = name;
        frs.friend_level = level;
        frs.friend_job = job;
        frs.friend_tag = "\u30de\u30a4\u53cb\u672a\u6307\u5b9a"; // JMS
        return ResCWvsContext.FriendResult(frs);
    }

    public static MaplePacket updateBuddyChannel(int friend_id, int friend_channel) {
        OpsFriendArg frs = new OpsFriendArg();
        frs.flag = OpsFriend.FriendRes_Notify;
        frs.friend_id = friend_id;
        frs.friend_channel = friend_channel;
        return ResCWvsContext.FriendResult(frs);
    }

    public static MaplePacket updateBuddyCapacity(int capacity) {
        OpsFriendArg frs = new OpsFriendArg();
        frs.flag = OpsFriend.FriendRes_IncMaxCount_Done;
        frs.nFriendMax = capacity;
        return ResCWvsContext.FriendResult(frs);
    }

    // test
    public static MaplePacket buddylistMessage(OpsFriend flag) {
        OpsFriendArg frs = new OpsFriendArg();
        frs.flag = flag;
        return ResCWvsContext.FriendResult(frs);
    }

    // プレイヤー情報の初期化
    public static final MaplePacket getCharInfo(final MapleCharacter chr) {
        return ResCStage.SetField(chr, true, null, 0);
    }

    public static final MaplePacket enableActions() {
        return ResCWvsContext.StatChanged(null, 1, 0);
    }

    // マップ移動
    public static final MaplePacket getWarpToMap(final MapleMap to, final int spawnPoint, final MapleCharacter chr) {
        if (ServerConfig.JMS302orLater()) {
            return ResCStage.SetField_JMS_302(chr, 1, false, to, spawnPoint, 0);
        }
        return ResCStage.SetField(chr, false, to, spawnPoint);
    }

    public static MaplePacket getNPCTalkText(int npc, String talk) {
        return ResCScriptMan.ScriptMessage(npc, OpsScriptMan.SM_ASKTEXT, (byte) 0, talk, false, false);
    }

    public static MaplePacket getNPCTalk(int npc, OpsScriptMan smt, String talk, String endBytes, byte type) {
        byte[] ebb = HexTool.getByteArrayFromHexString(endBytes);
        boolean prev = false;
        boolean next = false;
        if (ebb.length == 2) {
            if (ebb[0] == 1) {
                prev = true;
            }
            if (ebb[0] == 2) {
                next = true;
            }
        }
        return ResCScriptMan.ScriptMessage(npc, smt, type, talk, prev, next);
    }

    public static MaplePacket getNPCTalkNum(int npc, String talk, int def, int min, int max) {
        ServerPacket sp = new ServerPacket();
        sp.EncodeBuffer(ResCScriptMan.ScriptMessage(npc, OpsScriptMan.SM_ASKNUMBER, (byte) 0, talk, false, false).getBytes());
        sp.Encode4(def);
        sp.Encode4(min);
        sp.Encode4(max);
        return sp.get();
    }

    public static final MaplePacket getMapSelection(final int npcid, final String sel) {
        return ResCScriptMan.ScriptMessage(npcid, OpsScriptMan.SM_ASKSLIDEMENU, (byte) 0, sel, false, false);
    }

    public static MaplePacket getNPCTalkStyle(int npc, String talk, int... args) {
        ServerPacket sp = new ServerPacket();
        sp.EncodeBuffer(ResCScriptMan.ScriptMessage(npc, OpsScriptMan.SM_ASKAVATAR, (byte) 0, talk, false, false).getBytes());
        sp.Encode1(args.length);
        for (int i = 0; i < args.length; i++) {
            sp.Encode4(args[i]);
        }
        return sp.get();
    }

    public static MaplePacket showItemUnavailable() {
        return ResWrapper.getShowInventoryStatus(OpsDropPickUpMessage.PICKUP_UNAVAILABLE);
    }

    public static MaplePacket removeMapEffect() {
        return ResCField.startMapEffect(null, 0, false);
    }

    public static final MaplePacket sendGhostStatus(final String type, final String amount) {
        return ResCWvsContext.sendString(3, type, amount); //Red_Stage(1-5), Blue_Stage, blueTeamDamage, redTeamDamage
    }

    public static MaplePacket MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static final MaplePacket sendGhostPoint(final String type, final String amount) {
        return ResCWvsContext.sendString(2, type, amount); //PRaid_Point (0-1500???)
    }

    //show_status_info - 01 53 1E 01
    //10/08/14/19/11
    //update_quest_info - 08 53 1E 00 00 00 00 00 00 00 00
    //show_status_info - 01 51 1E 01 01 00 30
    //update_quest_info - 08 51 1E 00 00 00 00 00 00 00 00
    public static final MaplePacket sendPyramidEnergy(final String type, final String amount) {
        return ResCWvsContext.sendString(1, type, amount);
    }

    public static MaplePacket serverMessage(String message) {
        return ResCWvsContext.serverMessage(4, 0, message, false);
    }

    public static MaplePacket serverNotice(int type, String message) {
        return ResCWvsContext.serverMessage(type, 0, message, false);
    }

    public static MaplePacket serverNotice(int type, int channel, String message) {
        return ResCWvsContext.serverMessage(type, channel, message, false);
    }

    public static MaplePacket serverNotice(int type, int channel, String message, boolean smegaEar) {
        return ResCWvsContext.serverMessage(type, channel, message, smegaEar);
    }

}
