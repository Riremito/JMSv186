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

import client.MapleQuestStatus;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import handling.MaplePacket;
import java.util.List;
import packet.ServerPacket;
import packet.ops.OpsBroadcastMsg;
import packet.ops.OpsDropPickUpMessage;
import packet.ops.OpsMessage;
import packet.ops.OpsMessageArg;
import packet.ops.OpsQuestRecordMessage;
import packet.response.ResCWvsContext;
import packet.response.struct.GW_ItemSlotBase;
import packet.response.struct.InvOp;
import packet.response.struct.TestHelper;
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
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
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
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
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
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
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
        mplew.writeShort(ServerPacket.Header.LP_InventoryOperation.Get());
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
        p.Encode1(OpsBroadcastMsg.MEGAPHONE_BLUE.Get());
        p.EncodeStr(text);
        return p.Get();
    }

    // ドクロ拡声器
    public static MaplePacket MegaphoneSkull(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.MEGAPHONE_SKULL.Get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.Get();
    }

    // ハート拡声器
    public static MaplePacket MegaphoneHeart(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.MEGAPHONE_HEART.Get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.Get();
    }

    // アイテム拡声器
    public static MaplePacket MegaphoneItem(String text, byte channel, byte ear, byte showitem, IItem item) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.MEGAPHONE_ITEM.Get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        p.Encode1(showitem);
        if (showitem != 0) {
            p.EncodeBuffer(GW_ItemSlotBase.Encode(item));
        }
        return p.Get();
    }

    // 三連拡声器
    public static MaplePacket MegaphoneTriple(List<String> text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.MEGAPHONE_TRIPLE.Get());
        // 1行目
        p.EncodeStr(text.get(0));
        p.Encode1((byte) text.size());
        for (int i = 1; i < text.size(); i++) {
            p.EncodeStr(text.get(i));
        }
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.Get();
    }

    // 拡声器
    public static MaplePacket Megaphone(String text, byte channel, byte ear) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_BroadcastMsg);
        p.Encode1(OpsBroadcastMsg.MEGAPHONE.Get());
        p.EncodeStr(text);
        p.Encode1((byte) (channel - 1));
        p.Encode1(ear);
        return p.Get();
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
        ma.mt = OpsMessage.MS_JMS_PACHINKO;
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

}
