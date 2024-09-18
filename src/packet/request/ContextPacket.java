/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet.request;

import client.MapleQuestStatus;
import config.ServerConfig;
import debug.Debug;
import handling.MaplePacket;
import packet.ServerPacket;
import tools.StringUtil;

/**
 *
 * @author Riremito
 */
public class ContextPacket {

    // CWvsContext::OnTemporaryStatReset
    // CWvsContext::OnForcedStatSet
    // CWvsContext::OnForcedStatReset
    // CWvsContext::OnChangeSkillRecordResult
    // CWvsContext::OnSkillUseResult
    // CWvsContext::OnGivePopularityResult
    // CWvsContext::OnMessage
    public enum MessageType {
        // v186
        MS_DropPickUpMessage(0),
        MS_QuestRecordMessage(1),
        MS_CashItemExpireMessage(2),
        MS_IncEXPMessage(3),
        MS_IncSPMessage(4),
        MS_IncPOPMessage(5),
        MS_IncMoneyMessage(6),
        MS_IncGPMessage(7),
        MS_GiveBuffMessage(8),
        MS_GeneralItemExpireMessage(9),
        MS_SystemMessage(10),
        MS_QuestRecordExMessage(11),
        MS_ItemProtectExpireMessage(12),
        MS_ItemExpireReplaceMessage(13),
        MS_SkillExpireMessage(14),
        MS_JMS_PACHINKO(15),
        UNKNOWN(-1);

        private int value;

        MessageType(int flag) {
            value = flag;
        }

        MessageType() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }
    }

    public enum DropPickUpMessageType {
        PICKUP_ITEM(0),
        PICKUP_MESO(1),
        PICKUP_MONSTER_CARD(2),
        PICKUP_INVENTORY_FULL(-1), // any value
        PICKUP_UNAVAILABLE(-2),
        PICKUP_BROKEN(-3),
        UNKNOWN(-1);

        private int value;

        DropPickUpMessageType(int flag) {
            value = flag;
        }

        DropPickUpMessageType() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }
    }

    public enum QuestRecordMessageType {
        QUEST_START(0),
        QUEST_UPDATE(1),
        QUEST_COMPLETE(2),
        UNKNOWN(-1);

        private int value;

        QuestRecordMessageType(int flag) {
            value = flag;
        }

        QuestRecordMessageType() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }

        public static QuestRecordMessageType get(int v) {
            for (final QuestRecordMessageType f : QuestRecordMessageType.values()) {
                if (f.get() == v) {
                    return f;
                }
            }
            return QuestRecordMessageType.UNKNOWN;
        }
    }

    public static void Message_Init() {
        if ((ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion())) {
            MessageType.MS_DropPickUpMessage.set(0);
            MessageType.MS_QuestRecordMessage.set(1);
            MessageType.MS_CashItemExpireMessage.set(2);
            MessageType.MS_IncEXPMessage.set(3);
            MessageType.MS_IncSPMessage.set(4);
            MessageType.MS_IncPOPMessage.set(5);
            MessageType.MS_IncMoneyMessage.set(6);
            MessageType.MS_IncGPMessage.set(7);
            // 8 貢献度
            MessageType.MS_GiveBuffMessage.set(9);
            MessageType.MS_GeneralItemExpireMessage.set(10);
            MessageType.MS_SystemMessage.set(11);
            MessageType.MS_QuestRecordExMessage.set(12);
            MessageType.MS_ItemProtectExpireMessage.set(13);
            MessageType.MS_ItemExpireReplaceMessage.set(14);
            MessageType.MS_SkillExpireMessage.set(15);
            MessageType.MS_JMS_PACHINKO.set(16);
        }
    }

    public static class MessageArg {

        public MessageType mt = MessageType.UNKNOWN;
        public DropPickUpMessageType dt;

        public String str = "";
        public int ItemID = 0;
        public int Inc_ItemCount = 0;
        public short QuestID = 0;
        public QuestRecordMessageType qt;
        public int Inc_EXP_TextColor = 0; // white yellow
        public int Inc_EXP = 0;
        public int InChat = 0; // chat or not
        public int Inc_EXP_MobEventBonusPercentage = 0;
        public int Inc_EXP_PlayTimeHour = 0;
        public int Inc_EXP_EventBonus = 0;
        public int Inc_EXP_WeddingBonus = 0;
        public int Inc_EXP_PartyBonus = 0;
        public int Inc_EXP_EquipmentBonus = 0;
        public int Inc_EXP_PremiumBonus = 0;
        public int Inc_EXP_RainbowWeekBonus = 0;
        public int Inc_EXP_ClassBonus = 0;
        public short JobID = 0;
        public byte Inc_SP = 0;
        public int Inc_Fame = 0;
        public int Inc_Meso = 0;
        public int Inc_GP = 0;
        public int Inc_Tama = 0;

    }

    public static final MaplePacket Message(MessageArg ma) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_Message);

        p.Encode1(ma.mt.get());

        switch (ma.mt) {
            case MS_DropPickUpMessage: {
                p.Encode1(ma.dt.get());
                switch (ma.dt) {
                    case PICKUP_ITEM: {
                        p.Encode4(ma.ItemID);
                        p.Encode4(ma.Inc_ItemCount);
                        break;
                    }
                    case PICKUP_MESO: {
                        if (!(ServerConfig.IsJMS() && ServerConfig.GetVersion() < 164)) {
                            p.Encode1(0);
                        }

                        p.Encode4(ma.Inc_Meso);

                        if ((ServerConfig.IsJMS() && ServerConfig.GetVersion() < 164)) {
                            p.Encode2(0); // Internet cafe bonus
                        } else {
                            p.Encode4(0);
                        }

                        break;
                    }
                    case PICKUP_MONSTER_CARD: {
                        p.Encode4(ma.ItemID);
                        break;
                    }
                    case PICKUP_INVENTORY_FULL:
                    case PICKUP_UNAVAILABLE:
                    case PICKUP_BROKEN: {
                        p.Encode4(0);
                        p.Encode4(0);
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
                p.Encode2(ma.QuestID);
                p.Encode1(ma.qt.get());
                switch (ma.qt) {
                    case QUEST_START: {
                        p.Encode1(0); // 0 or not
                        break;
                    }
                    case QUEST_UPDATE: {
                        p.EncodeStr(ma.str);
                        break;
                    }
                    case QUEST_COMPLETE: {
                        p.Encode8(System.currentTimeMillis());
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
                p.Encode4(ma.ItemID);
                break;
            }
            case MS_IncEXPMessage: {
                p.Encode1(ma.Inc_EXP_TextColor);
                p.Encode4(ma.Inc_EXP);
                p.Encode1(ma.InChat); // bOnQuest
                p.Encode4(0);
                p.Encode1(ma.Inc_EXP_MobEventBonusPercentage); // nMobEventBonusPercentage
                p.Encode1(0);
                p.Encode4(ma.Inc_EXP_WeddingBonus); // 結婚ボーナス経験値
                p.Encode4(0); // グループリングボーナスEXP (?)

                if (0 < ma.Inc_EXP_MobEventBonusPercentage) {
                    p.Encode1(ma.Inc_EXP_PlayTimeHour);
                }

                if (ma.InChat != 0) {
                    p.Encode1(0);
                }

                p.Encode1(0); // nPartyBonusEventRate
                p.Encode4(ma.Inc_EXP_PartyBonus); // グループボーナス経験値
                p.Encode4(ma.Inc_EXP_EquipmentBonus); // アイテム装着ボーナス経験値
                p.Encode4(0); // not used
                p.Encode4(ma.Inc_EXP_RainbowWeekBonus); // レインボーウィークボーナス経験値

                if (194 <= ServerConfig.GetVersion()) {
                    p.Encode1(0); // 0 or not
                }

                break;
            }
            // getSPMsg
            case MS_IncSPMessage: {
                p.Encode2(ma.JobID);
                p.Encode1(ma.Inc_SP);
                break;
            }
            // getShowFameGain
            case MS_IncPOPMessage: {
                p.Encode4(ma.Inc_Fame);
                break;
            }
            // showMesoGain
            case MS_IncMoneyMessage: {
                p.Encode4(ma.Inc_Meso);
                break;
            }
            // getGPMsg
            case MS_IncGPMessage: {
                p.Encode4(ma.Inc_GP);
                break;
            }
            // getStatusMsg
            case MS_GiveBuffMessage: {
                p.Encode4(ma.ItemID);
                break;
            }
            case MS_GeneralItemExpireMessage: {
                break;
            }
            // showQuestMsg
            case MS_SystemMessage: {
                p.EncodeStr(ma.str);
                break;
            }
            // updateInfoQuest
            case MS_QuestRecordExMessage: {
                p.Encode2(ma.QuestID);
                p.EncodeStr(ma.str);
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
            case MS_JMS_PACHINKO: {
                p.Encode4(ma.Inc_Tama);
                break;
            }
            default: {
                break;
            }
        }

        return p.Get();
    }

    public static MaplePacket DropPickUpMessage(int itemId, short quantity) {

        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_DropPickUpMessage;
        ma.dt = DropPickUpMessageType.PICKUP_ITEM;
        ma.ItemID = itemId;
        ma.Inc_ItemCount = quantity;

        return ContextPacket.Message(ma);
    }

    public static MaplePacket getShowInventoryStatus(DropPickUpMessageType dm) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_DropPickUpMessage;
        ma.dt = dm;

        return ContextPacket.Message(ma);
    }

    public static MaplePacket showGainCard(int itemid) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_DropPickUpMessage;
        ma.dt = DropPickUpMessageType.PICKUP_MONSTER_CARD;
        ma.ItemID = itemid;

        return ContextPacket.Message(ma);
    }

    public static final MaplePacket updateQuest(final MapleQuestStatus quest) {

        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_QuestRecordMessage;
        ma.QuestID = (short) quest.getQuest().getId();
        ma.qt = QuestRecordMessageType.get(quest.getStatus());
        ma.str = quest.getCustomData() != null ? quest.getCustomData() : "";

        return ContextPacket.Message(ma);
    }

    public static final MaplePacket updateQuestMobKills(final MapleQuestStatus status) {

        final StringBuilder sb = new StringBuilder();
        for (final int kills : status.getMobKills().values()) {
            sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
        }

        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_QuestRecordMessage;
        ma.QuestID = (short) status.getQuest().getId();
        ma.qt = QuestRecordMessageType.QUEST_UPDATE;
        ma.str = sb.toString();

        return ContextPacket.Message(ma);
    }

    public static MaplePacket itemExpired(int itemid) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_CashItemExpireMessage;
        ma.ItemID = itemid;

        return ContextPacket.Message(ma);
    }

    public static final MaplePacket GainEXP_Monster(final int gain, final boolean white, final int partyinc, final int Class_Bonus_EXP, final int Equipment_Bonus_EXP, final int Premium_Bonus_EXP) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_IncEXPMessage;
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

        return ContextPacket.Message(ma);
    }

    public static final MaplePacket GainEXP_Others(final int gain, final boolean inChat, final boolean white) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_IncEXPMessage;
        ma.Inc_EXP_TextColor = white ? 1 : 0;
        ma.Inc_EXP = gain;
        ma.InChat = inChat ? 1 : 0;

        return ContextPacket.Message(ma);
    }

    public static MaplePacket getSPMsg(byte inc_sp, short jobid) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_IncSPMessage;
        ma.JobID = jobid;
        ma.Inc_SP = inc_sp;

        return ContextPacket.Message(ma);
    }

    public static final MaplePacket showMesoGain(int gain, boolean inChat) {
        if (!inChat) {
            MessageArg ma = new MessageArg();
            ma.mt = MessageType.MS_DropPickUpMessage;
            ma.dt = DropPickUpMessageType.PICKUP_MESO;
            ma.Inc_Meso = gain;

            return ContextPacket.Message(ma);
        }

        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_IncMoneyMessage;
        ma.Inc_Meso = gain;

        return ContextPacket.Message(ma);

    }

    public static final MaplePacket getShowFameGain(int inc_fame) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_IncPOPMessage;
        ma.Inc_Fame = inc_fame;

        return ContextPacket.Message(ma);
    }

    public static MaplePacket getGPMsg(int inc_gp) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_IncGPMessage;
        ma.Inc_GP = inc_gp;

        return ContextPacket.Message(ma);
    }

    public static MaplePacket getStatusMsg(int itemid) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_GiveBuffMessage;
        ma.ItemID = itemid;

        return ContextPacket.Message(ma);
    }

    public static MaplePacket showQuestMsg(String msg) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_SystemMessage;
        ma.str = msg;

        return ContextPacket.Message(ma);
    }

    public static final MaplePacket updateInfoQuest(final int quest, final String data) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_QuestRecordExMessage;
        ma.QuestID = (short) quest;
        ma.str = data;

        return ContextPacket.Message(ma);
    }

    public static final MaplePacket GainTamaMessage(int inc_tama) {
        MessageArg ma = new MessageArg();
        ma.mt = MessageType.MS_JMS_PACHINKO;
        ma.Inc_Tama = inc_tama;

        return ContextPacket.Message(ma);
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
}
