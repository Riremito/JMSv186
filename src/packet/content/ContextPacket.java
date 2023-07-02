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
package packet.content;

import client.MapleCharacter;
import handling.MaplePacket;
import packet.ServerPacket;
import packet.struct.GW_CharacterStat;

/**
 *
 * @author Riremito
 */
public class ContextPacket {

    // CWvsContext::OnInventoryOperation
    // CWvsContext::OnInventoryGrow
    // CWvsContext::OnStatChanged
    public static final MaplePacket StatChanged(MapleCharacter chr, int unlock, int statmask) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_StatChanged);

        // 0 = lock   -> do not clear lock flag
        // 1 = unlock -> clear lock flag
        p.Encode1(unlock); // CWvsContext->bExclRequestSent
        p.EncodeBuffer(GW_CharacterStat.EncodeChangeStat(chr, statmask));

        // Pet
        if ((statmask & (1 << 3)) > 0) {
            int v5 = 0; // CVecCtrlUser::AddMovementInfo
            p.Encode1(v5);
        }

        return p.Get();
    }
    // CWvsContext::OnTemporaryStatSet
    // CWvsContext::OnTemporaryStatReset
    // CWvsContext::OnForcedStatSet
    // CWvsContext::OnForcedStatReset
    // CWvsContext::OnChangeSkillRecordResult
    // CWvsContext::OnSkillUseResult
    // CWvsContext::OnGivePopularityResult
    // CWvsContext::OnMessage
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
