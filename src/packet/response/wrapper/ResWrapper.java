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
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import server.network.MaplePacket;
import packet.ServerPacket;
import packet.ops.OpsBroadcastMsg;
import packet.ops.arg.ArgBroadcastMsg;
import packet.ops.OpsDropPickUpMessage;
import packet.ops.OpsFieldEffect;
import packet.ops.arg.ArgFieldEffect;
import packet.ops.OpsFriend;
import packet.ops.arg.ArgFriend;
import packet.ops.OpsMessage;
import packet.ops.arg.ArgMessage;
import packet.ops.OpsQuestRecordMessage;
import packet.ops.OpsScriptMan;
import packet.response.ResCField;
import packet.response.ResCScriptMan;
import packet.response.ResCStage;
import packet.response.ResCWvsContext;
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
            io.add(GameConstants.getInventoryType(item.getItemId()), item);
        } else {
            io.remove(GameConstants.getInventoryType(item.getItemId()), item.getPosition());
        }

        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket moveAndMergeInventoryItem(MapleInventoryType type, IItem item, short slot_remove) {
        InvOp io = new InvOp();
        io.move(type, slot_remove, item.getPosition()); // new item frame movement
        io.remove(type, slot_remove);
        io.update(type, item);
        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket moveAndMergeWithRestInventoryItem(MapleInventoryType type, IItem item_max, IItem item_rest) {
        InvOp io = new InvOp();
        io.update(type, item_rest);
        io.update(type, item_max);
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

    // 装着時交換不可など
    public static MaplePacket updateSpecialItemUse_(IItem item, byte invType) {
        InvOp io = new InvOp();
        io.add(GameConstants.getInventoryType(item.getItemId()), item);
        return ResCWvsContext.InventoryOperation(true, io);
    }

    public static MaplePacket getShowInventoryFull() {
        return getShowInventoryStatus(OpsDropPickUpMessage.PICKUP_INVENTORY_FULL);
    }

    public static final MaplePacket GainEXP_Monster(final int gain, final boolean white, final int partyinc, final int Class_Bonus_EXP, final int Equipment_Bonus_EXP, final int Premium_Bonus_EXP) {
        ArgMessage ma = new ArgMessage();
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
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_QuestRecordMessage;
        ma.QuestID = (short) status.getQuest().getId();
        ma.qt = OpsQuestRecordMessage.QUEST_UPDATE;
        ma.str = sb.toString();
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket showGainCard(int itemid) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_DropPickUpMessage;
        ma.dt = OpsDropPickUpMessage.PICKUP_MONSTER_CARD;
        ma.ItemID = itemid;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket getGPMsg(int inc_gp) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_IncGPMessage;
        ma.Inc_GP = inc_gp;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket getStatusMsg(int itemid) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_GiveBuffMessage;
        ma.ItemID = itemid;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket getSPMsg(byte inc_sp, short jobid) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_IncSPMessage;
        ma.JobID = jobid;
        ma.Inc_SP = inc_sp;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket updateInfoQuest(final int quest, final String data) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_QuestRecordExMessage;
        ma.QuestID = (short) quest;
        ma.str = data;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket getShowFameGain(int inc_fame) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_IncPOPMessage;
        ma.Inc_Fame = inc_fame;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket DropPickUpMessage(int itemId, short quantity) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_DropPickUpMessage;
        ma.dt = OpsDropPickUpMessage.PICKUP_ITEM;
        ma.ItemID = itemId;
        ma.Inc_ItemCount = quantity;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket getShowInventoryStatus(OpsDropPickUpMessage dm) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_DropPickUpMessage;
        ma.dt = dm;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket GainEXP_Others(final int gain, final boolean inChat, final boolean white) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_IncEXPMessage;
        ma.Inc_EXP_TextColor = white ? 1 : 0;
        ma.Inc_EXP = gain;
        ma.InChat = inChat ? 1 : 0;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket showMesoGain(int gain, boolean inChat) {
        if (!inChat) {
            ArgMessage ma = new ArgMessage();
            ma.mt = OpsMessage.MS_DropPickUpMessage;
            ma.dt = OpsDropPickUpMessage.PICKUP_MESO;
            ma.Inc_Meso = gain;
            return ResCWvsContext.Message(ma);
        }
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_IncMoneyMessage;
        ma.Inc_Meso = gain;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket GainTamaMessage(int inc_tama) {
        ArgMessage ma = new ArgMessage();
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
    public static MaplePacket BroadCastMsgNoticeOld(String message) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.BM_NOTICE; // 告知事項
        bma.message = message;
        return ResCWvsContext.BroadcastMsg(bma);
    }

    public static MaplePacket BroadCastMsgAlert(String message) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.BM_ALERT; // ダイアログ
        bma.message = message;
        return ResCWvsContext.BroadcastMsg(bma);
    }

    public static MaplePacket BroadCastMsgSlide(String message) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.BM_SLIDE; // 上部メッセージ
        bma.message = message;
        return ResCWvsContext.BroadcastMsg(bma);
    }

    public static MaplePacket BroadCastMsgEvent(String message) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.BM_EVENT; // ピンク文字
        bma.message = message;
        return ResCWvsContext.BroadcastMsg(bma);
    }

    public static MaplePacket BroadCastMsg_SN(int type, String message) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.find((byte) type); // 古いscript (serverNotice)用
        bma.message = message;
        return ResCWvsContext.BroadcastMsg(bma);
    }

    public static MaplePacket BroadCastMsgNotice(String message) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX; // 青文字
        bma.message = message;
        return ResCWvsContext.BroadcastMsg(bma);
    }

    public static MaplePacket BroadCastMsgNoticeItem(String message, int item_id) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.BM_NOTICEWITHOUTPREFIX; // 青文字
        bma.message = message;
        bma.item_id = item_id; // アイテム表示
        return ResCWvsContext.BroadcastMsg(bma);
    }

    public static MaplePacket BroadCastMsgGachaponAnnounce(MapleCharacter chr, IItem item) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.BM_GACHAPONANNOUNCE; // ガシャポン, アバターランダムボックス
        bma.chr = chr;
        bma.message = "をガシャポンで手に入れました。おめでとうございます！";
        bma.item = item; // アイテム表示
        bma.gashapon_type = 0; // -1 = アバターランダムボックス
        return ResCWvsContext.BroadcastMsg(bma);
    }

    public static MaplePacket BroadCastMsgRandomBoxAnnounce(MapleCharacter chr, IItem item) {
        ArgBroadcastMsg bma = new ArgBroadcastMsg();
        bma.bm = OpsBroadcastMsg.BM_GACHAPONANNOUNCE;
        bma.chr = chr;
        bma.message = "をランダムボックスで手に入れました。おめでとうございます！";
        bma.item = item;
        bma.gashapon_type = -1;
        return ResCWvsContext.BroadcastMsg(bma);
    }
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
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_QuestRecordMessage;
        ma.QuestID = (short) quest.getQuest().getId();
        ma.qt = OpsQuestRecordMessage.get(quest.getStatus());
        ma.str = quest.getCustomData() != null ? quest.getCustomData() : "";
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket showQuestMsg(String msg) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_SystemMessage;
        ma.str = msg;
        return ResCWvsContext.Message(ma);
    }

    public static MaplePacket itemExpired(int itemid) {
        ArgMessage ma = new ArgMessage();
        ma.mt = OpsMessage.MS_CashItemExpireMessage;
        ma.ItemID = itemid;
        return ResCWvsContext.Message(ma);
    }

    public static final MaplePacket MapNameDisplay(final int mapid) {
        return ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Screen, "maplemap/enter/" + mapid));
    }

    public static MaplePacket playSound(String sound) {
        return ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Sound, sound));
    }

    public static MaplePacket showEffect(String effect) {
        return ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_Screen, effect));
    }

    public static MaplePacket musicChange(String song) {
        return ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.FieldEffect_ChangeBGM, song));
    }

    public static MaplePacket environmentChange(String env, int mode) {
        return ResCField.FieldEffect(new ArgFieldEffect(OpsFieldEffect.find(mode), env));
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
        ArgFriend frs = new ArgFriend();
        frs.flag = OpsFriend.FriendRes_LoadFriend_Done;
        frs.chr = chr;
        return ResCWvsContext.FriendResult(frs);
    }

    public static MaplePacket requestBuddylistAdd(int friend_id, String name, int level, int job) {
        ArgFriend frs = new ArgFriend();
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
        ArgFriend frs = new ArgFriend();
        frs.flag = OpsFriend.FriendRes_Notify;
        frs.friend_id = friend_id;
        frs.friend_channel = friend_channel;
        return ResCWvsContext.FriendResult(frs);
    }

    public static MaplePacket updateBuddyCapacity(int capacity) {
        ArgFriend frs = new ArgFriend();
        frs.flag = OpsFriend.FriendRes_IncMaxCount_Done;
        frs.nFriendMax = capacity;
        return ResCWvsContext.FriendResult(frs);
    }

    // test
    public static MaplePacket buddylistMessage(OpsFriend flag) {
        ArgFriend frs = new ArgFriend();
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
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
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

    // enableActions
    public static MaplePacket StatChanged(MapleCharacter chr) {
        return ResCWvsContext.StatChanged(chr, 1, 0);
    }

    // CUser::SendCharacterStat(1,0)
    // CWvsContext::OnStatChanged
    public static MaplePacket StatChanged(MapleCharacter chr, int unlock, int statmask) {
        return ResCWvsContext.StatChanged(chr, unlock, statmask);
    }

}
