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
package packet;

import config.ServerConfig;
import server.network.ByteArrayMaplePacket;
import server.network.MaplePacket;
import java.util.ArrayList;

public class ServerPacket {

    // Encoder
    private ArrayList<Byte> packet = new ArrayList<>();
    private int encoded = 0;

    public ServerPacket(Header header) {
        short w = (short) header.get();

        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public ServerPacket(short w) {
        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public boolean setHello() {
        if (encoded < 2) {
            return false;
        }
        int data_size = encoded - 2;
        packet.set(0, (byte) (data_size & 0xFF));
        packet.set(1, (byte) ((data_size >> 8) & 0xFF));
        return true;
    }

    public void Encode1(byte b) {
        packet.add(b);
        encoded += 1;
    }

    public ServerPacket() {
        // データ構造用
    }

    public void Encode2(short w) {
        Encode1((byte) (w & 0xFF));
        Encode1((byte) ((w >> 8) & 0xFF));
    }

    public void Encode4(int dw) {
        Encode2((short) (dw & 0xFFFF));
        Encode2((short) ((dw >> 16) & 0xFFFF));
    }

    public void Encode8(long qw) {
        Encode4((int) (qw & 0xFFFFFFFF));
        Encode4((int) ((qw >> 32) & 0xFFFFFFFF));
    }

    public void EncodeStr(String str) {
        byte[] b = str.getBytes(ServerConfig.utf8 ? ServerConfig.codepage_utf8 : ServerConfig.codepage_ascii);
        Encode2((short) b.length);

        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
    }

    public void EncodeBuffer(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
    }

    public void EncodeBuffer(String str, int size) {
        byte[] b = str.getBytes(ServerConfig.utf8 ? ServerConfig.codepage_utf8 : ServerConfig.codepage_ascii);
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        for (int i = 0; i < (size - b.length); i++) {
            Encode1(0);
        }
    }

    public void EncodeBuffer(byte[] b, int size) {
        for (int i = 0; i < b.length; i++) {
            Encode1(b[i]);
        }
        for (int i = 0; i < (size - b.length); i++) {
            Encode1(0);
        }
    }

    public static Header ToHeader(short w) {
        for (final Header h : Header.values()) {
            if (h.get() == w) {
                return h;
            }
        }

        return Header.UNKNOWN;
    }

    public String Packet() {
        byte[] b = new byte[encoded];
        for (int i = 0; i < encoded; i++) {
            b[i] = packet.get(i);
        }

        short header = (short) (((short) b[0] & 0xFF) | ((short) b[1] & 0xFF << 8));
        String text = String.format("@%04X", header);

        for (int i = 2; i < encoded; i++) {
            text += String.format(" %02X", b[i]);
        }

        return text;
    }

    public String getOpcodeName() {
        if (encoded < 2) {
            return Header.UNKNOWN.toString();
        }

        short header = (short) (((short) packet.get(0) & 0xFF) | ((short) packet.get(1) & 0xFF << 8));
        return ToHeader(header).toString();
    }

    public MaplePacket get() {
        byte[] b = new byte[encoded];
        for (int i = 0; i < encoded; i++) {
            b[i] = packet.get(i);
        }
        return new ByteArrayMaplePacket(b);
    }

    public void Encode1(int b) {
        packet.add((byte) b);
        encoded += 1;
    }

    public void Encode2(int w) {
        Encode1((byte) ((short) w & 0xFF));
        Encode1((byte) (((short) w >> 8) & 0xFF));
    }

    public void EncodeZeroBytes(int length) {
        for (int i = 0; i < length; i++) {
            Encode1(0);
        }
    }

    public enum Header implements IHeader {
        // CClientSocket::ProcessPacket, CLogin::OnPacket
        LP_BEGIN_SOCKET,
        LP_CheckPasswordResult,
        LP_GuestIDLoginResult,
        LP_AccountInfoResult,
        LP_CheckUserLimitResult,
        LP_SetAccountResult,
        LP_ConfirmEULAResult,
        LP_CheckPinCodeResult,
        LP_UpdatePinCodeResult,
        LP_ViewAllCharResult,
        LP_SelectCharacterByVACResult,
        LP_WorldInformation,
        LP_SelectWorldResult,
        LP_SelectCharacterResult,
        LP_CheckDuplicatedIDResult,
        LP_CreateNewCharacterResult,
        LP_DeleteCharacterResult,
        LP_MigrateCommand,
        LP_AliveReq,
        LP_AuthenCodeChanged,
        LP_AuthenMessage,
        LP_SecurityPacket,
        LP_JMS_GuardInspectProcess,
        LP_JMS_CheckGameGuardUpdatedResult, // JMS131, KMST330
        LP_JMS_SafetyPasswordResult,
        LP_EnableSPWResult,
        LP_DeleteCharacterOTPRequest,
        LP_CheckCrcResult,
        LP_LatestConnectedWorld,
        LP_RecommendWorldMessage,
        LP_JMS_SetMapLogin,
        LP_CheckExtraCharInfoResult,
        LP_CheckSPWResult,
        LP_END_SOCKET,
        // CWvsContext::OnPacket
        LP_BEGIN_CHARACTERDATA,
        LP_InventoryOperation,
        LP_InventoryGrow,
        LP_StatChanged,
        LP_TemporaryStatSet,
        LP_TemporaryStatReset,
        LP_ForcedStatSet,
        LP_ForcedStatReset,
        LP_ChangeSkillRecordResult,
        LP_SkillUseResult,
        LP_GivePopularityResult,
        LP_Message,
        LP_SendOpenFullClientLink,
        LP_MemoResult,
        LP_MapTransferResult,
        LP_AntiMacroResult,
        LP_InitialQuizStart,
        LP_ClaimResult,
        LP_SetClaimSvrAvailableTime,
        LP_ClaimSvrStatusChanged,
        LP_SetTamingMobInfo,
        LP_QuestClear,
        LP_EntrustedShopCheckResult,
        LP_SkillLearnItemResult,
        LP_SkillResetItemResult,
        LP_GatherItemResult,
        LP_SortItemResult,
        LP_RemoteShopOpenResult,
        LP_SueCharacterResult,
        LP_MigrateToCashShopResult,
        LP_TradeMoneyLimit,
        LP_SetGender,
        LP_GuildBBS,
        LP_PetDeadMessage,
        LP_CharacterInfo,
        LP_PartyResult,
        LP_ExpeditionRequest,
        LP_ExpeditionNoti,
        LP_FriendResult,
        LP_GuildRequest,
        LP_GuildResult,
        LP_AllianceResult,
        LP_TownPortal,
        LP_OpenGate,
        LP_BroadcastMsg,
        LP_IncubatorResult,
        LP_ShopScannerResult,
        LP_ShopLinkResult,
        LP_MarriageRequest,
        LP_MarriageResult,
        LP_WeddingGiftResult,
        LP_MarriedPartnerMapTransfer,
        LP_CashPetFoodResult,
        LP_SetWeekEventMessage,
        LP_SetPotionDiscountRate,
        LP_BridleMobCatchFail,
        LP_JMS_PachinkoResult,
        LP_JMS_PachinkoPrizes,
        LP_JMS_PachinkoGift,
        LP_JMS_MapleGift,
        LP_JMS_PredictHarmonyResult,
        LP_JMS_Fishing_BoardUpdate,
        LP_JMS_WorldTransferResult,
        LP_JMS_PlayTimeCountDown,
        LP_ImitatedNPCResult,
        LP_ImitatedNPCData, // -> CNpcPool::OnPacket
        LP_LimitedNPCDisableInfo, // -> CNpcPool::OnPacket
        LP_MonsterBookSetCard,
        LP_MonsterBookSetCover,
        LP_HourChanged,
        LP_MiniMapOnOff,
        LP_ConsultAuthkeyUpdate,
        LP_ClassCompetitionAuthkeyUpdate,
        LP_WebBoardAuthkeyUpdate,
        LP_SessionValue,
        LP_PartyValue,
        LP_FieldSetVariable,
        LP_BonusExpRateChanged,
        LP_PotionDiscountRateChanged,
        LP_FamilyChartResult,
        LP_FamilyInfoResult,
        LP_FamilyResult,
        LP_FamilyJoinRequest,
        LP_FamilyJoinRequestResult,
        LP_FamilyJoinAccepted,
        LP_FamilyPrivilegeList,
        LP_FamilyFamousPointIncResult,
        LP_FamilyNotifyLoginOrLogout,
        LP_FamilySetPrivilege,
        LP_FamilySummonRequest,
        LP_NotifyLevelUp,
        LP_NotifyWedding,
        LP_NotifyJobChange,
        LP_IncRateChanged,
        LP_MapleTVUseRes,
        LP_AvatarMegaphoneRes,
        LP_AvatarMegaphoneUpdateMessage,
        LP_AvatarMegaphoneClearMessage,
        LP_CancelNameChangeResult,
        LP_CancelTransferWorldResult,
        LP_DestroyShopResult,
        LP_FAKEGMNOTICE,
        LP_SuccessInUseGachaponBox,
        LP_NewYearCardRes,
        LP_RandomMorphRes,
        LP_CancelNameChangeByOther,
        LP_SetBuyEquipExt,
        LP_SetPassenserRequest,
        LP_ScriptProgressMessage,
        LP_DataCRCCheckFailed,
        LP_CakePieEventResult,
        LP_UpdateGMBoard,
        LP_ShowSlotMessage,
        LP_WildHunterInfo,
        LP_AccountMoreInfo,
        LP_FindFirend,
        LP_StageChange,
        LP_DragonBallBox,
        LP_AskUserWhetherUsePamsSong,
        LP_TransferChannel,
        LP_DisallowedDeliveryQuestList,
        LP_MacroSysDataInit,
        LP_END_CHARACTERDATA,
        // CStage::OnPacket
        LP_BEGIN_STAGE,
        LP_SetField,
        LP_SetITC,
        LP_SetCashShop,
        LP_END_STAGE,
        // CMapLoadable::OnPacket
        LP_BEGIN_MAP,
        LP_SetBackgroundEffect,
        LP_SetMapObjectVisible,
        LP_ClearBackgroundEffect,
        LP_END_MAP,
        // CField::OnPacket
        LP_BEGIN_FIELD,
        LP_TransferFieldReqIgnored,
        LP_TransferChannelReqIgnored,
        LP_FieldSpecificData,
        LP_GroupMessage,
        LP_Whisper,
        LP_CoupleMessage,
        LP_MobSummonItemUseResult,
        LP_FieldEffect,
        LP_FieldObstacleOnOff,
        LP_FieldObstacleOnOffStatus,
        LP_FieldObstacleAllReset,
        LP_BlowWeather,
        LP_PlayJukeBox,
        LP_AdminResult,
        LP_Quiz,
        LP_Desc,
        LP_Clock,
        LP_CONTIMOVE,
        LP_CONTISTATE,
        LP_SetQuestClear,
        LP_SetQuestTime,
        LP_Warn,
        LP_SetObjectState,
        LP_DestroyClock,
        LP_ShowArenaResult, // -> CField_AriantArena::OnPacket
        LP_StalkResult,
        LP_MassacreIncGauge,
        LP_MassacreResult,
        LP_QuickslotMappedInit,
        LP_FootHoldInfo,
        LP_RequestFootHoldInfo,
        LP_FieldKillCount,
        // CUserPool::OnPacket
        LP_BEGIN_USERPOOL,
        LP_UserEnterField,
        LP_UserLeaveField,
        // CUserPool::OnUserCommonPacket
        LP_BEGIN_USERCOMMON,
        LP_UserChat,
        LP_UserChatNLCPQ,
        LP_UserADBoard,
        LP_UserMiniRoomBalloon,
        LP_UserConsumeItemEffect,
        LP_UserItemUpgradeEffect,
        LP_UserItemHyperUpgradeEffect,
        LP_UserItemOptionUpgradeEffect,
        LP_UserItemReleaseEffect,
        LP_UserItemUnreleaseEffect,
        LP_UserHitByUser,
        LP_UserTeslaTriangle,
        LP_UserFollowCharacter,
        LP_UserShowPQReward,
        LP_UserSetPhase,
        LP_SetPortalUsable,
        LP_JMS_Fishing_Caught,
        LP_ShowPamsSongResult,
        // CUser::OnPetPacket
        LP_BEGIN_PET,
        LP_PetActivated,
        LP_PetEvol,
        LP_PetTransferField,
        LP_PetMove,
        LP_PetAction,
        LP_PetNameChanged,
        LP_PetLoadExceptionList,
        LP_PetActionCommand,
        LP_END_PET,
        // CUser::OnDragonPacket
        LP_BEGIN_DRAGON,
        LP_DragonEnterField,
        LP_DragonMove,
        LP_DragonLeaveField,
        LP_END_DRAGON,
        LP_END_USERCOMMON,
        // CUserPool::OnUserRemotePacket
        LP_BEGIN_USERREMOTE,
        LP_UserMove,
        LP_UserMeleeAttack,
        LP_UserShootAttack,
        LP_UserMagicAttack,
        LP_UserBodyAttack,
        LP_UserSkillPrepare,
        LP_UserMovingShootAttackPrepare,
        LP_UserSkillCancel,
        LP_UserHit,
        LP_UserEmotion,
        LP_UserSetActiveEffectItem,
        LP_UserShowUpgradeTombEffect,
        LP_UserSetActivePortableChair,
        LP_UserAvatarModified,
        LP_UserEffectRemote,
        LP_UserTemporaryStatSet,
        LP_UserTemporaryStatReset,
        LP_UserHP,
        LP_UserGuildNameChanged,
        LP_UserGuildMarkChanged,
        LP_UserThrowGrenade,
        LP_END_USERREMOTE,
        // CUserPool::OnUserLocalPacket
        LP_BEGIN_USERLOCAL,
        LP_UserSitResult,
        LP_UserEmotionLocal,
        LP_UserEffectLocal,
        LP_UserTeleport,
        LP_Premium,
        LP_MesoGive_Succeeded,
        LP_MesoGive_Failed,
        LP_Random_Mesobag_Succeed,
        LP_Random_Mesobag_Failed,
        LP_FieldFadeInOut,
        LP_FieldFadeOutForce,
        LP_UserQuestResult,
        LP_NotifyHPDecByField,
        LP_UserPetSkillChanged,
        LP_UserBalloonMsg,
        LP_PlayEventSound,
        LP_PlayMinigameSound,
        LP_UserMakerResult,
        LP_UserOpenConsultBoard,
        LP_UserOpenClassCompetitionPage,
        LP_UserOpenUI,
        LP_UserOpenUIWithOption,
        LP_SetDirectionMode,
        LP_SetStandAloneMode,
        LP_UserHireTutor,
        LP_UserTutorMsg,
        LP_IncCombo,
        LP_JMS_Pachinko_BoxSuccess,
        LP_JMS_Pachinko_BoxFailure,
        LP_UserRandomEmotion,
        LP_ResignQuestReturn,
        LP_PassMateName,
        LP_SetRadioSchedule,
        LP_UserOpenSkillGuide,
        LP_UserNoticeMsg,
        LP_UserChatMsg,
        LP_UserBuffzoneEffect,
        LP_UserGoToCommoditySN,
        LP_UserDamageMeter,
        LP_UserTimeBombAttack,
        LP_UserPassiveMove,
        LP_UserFollowCharacterFailed,
        LP_UserRequestVengeance,
        LP_UserRequestExJablin,
        LP_UserAskAPSPEvent,
        LP_QuestGuideResult,
        LP_UserDeliveryQuest,
        LP_JMS_Poll_Question,
        LP_SkillCooltimeSet,
        LP_END_USERLOCAL,
        LP_END_USERPOOL,
        // CSummonedPool::OnPacket
        LP_BEGIN_SUMMONED,
        LP_SummonedEnterField,
        LP_SummonedLeaveField,
        LP_SummonedMove,
        LP_SummonedAttack,
        LP_SummonedSkill,
        LP_SummonedHit,
        LP_END_SUMMONED,
        // CMobPool::OnPacket
        LP_BEGIN_MOBPOOL,
        LP_MobEnterField,
        LP_MobLeaveField,
        LP_MobChangeController,
        // CMobPool::OnMobPacket
        LP_BEGIN_MOB,
        LP_MobMove,
        LP_MobCtrlAck,
        LP_MobCtrlHint,
        LP_MobStatSet,
        LP_MobStatReset,
        LP_MobSuspendReset,
        LP_MobAffected,
        LP_MobDamaged,
        LP_MobSpecialEffectBySkill,
        LP_MobHPChange, // reserved
        LP_MobCrcKeyChanged, // -> CMobPool::OnPacket
        LP_MobHPIndicator,
        LP_JMS_Mob_Magnet,
        LP_MobCatchEffect,
        LP_MobEffectByItem,
        LP_MobSpeaking,
        LP_MobChargeCount,
        LP_MobSkillDelay,
        LP_MobRequestResultEscortInfo,
        LP_MobEscortStopEndPermmision,
        LP_MobEscortStopSay,
        LP_MobEscortReturnBefore,
        LP_MobNextAttack,
        LP_MobTeleport,
        LP_MobAttackedByMob,
        LP_END_MOB,
        LP_END_MOBPOOL,
        // CNpcPool::OnPacket
        LP_BEGIN_NPCPOOL,
        LP_NpcEnterField,
        LP_NpcLeaveField,
        LP_NpcChangeController,
        // CNpcPool::OnNpcPacket
        LP_BEGIN_NPC,
        LP_NpcMove,
        LP_NpcUpdateLimitedInfo,
        LP_NpcSpecialAction,
        LP_END_NPC,
        // CNpcPool::OnNpcTemplatePacket
        LP_BEGIN_NPCTEMPLATE,
        LP_NpcSetScript,
        LP_END_NPCTEMPLATE,
        LP_END_NPCPOOL,
        // CEmployeePool::OnPacket
        LP_BEGIN_EMPLOYEEPOOL,
        LP_EmployeeEnterField,
        LP_EmployeeLeaveField,
        LP_EmployeeMiniRoomBalloon,
        LP_END_EMPLOYEEPOOL,
        // CDropPool::OnPacket
        LP_BEGIN_DROPPOOL,
        LP_DropEnterField,
        LP_DropReleaseAllFreeze,
        LP_DropLeaveField,
        LP_END_DROPPOOL,
        // CMessageBoxPool::OnPacket
        LP_BEGIN_MESSAGEBOXPOOL,
        LP_CreateMessgaeBoxFailed,
        LP_MessageBoxEnterField,
        LP_MessageBoxLeaveField,
        LP_END_MESSAGEBOXPOOL,
        // CAffectedAreaPool::OnPacket
        LP_BEGIN_AFFECTEDAREAPOOL,
        LP_AffectedAreaCreated,
        LP_AffectedAreaRemoved,
        LP_END_AFFECTEDAREAPOOL,
        // CTownPortalPool::OnPacket
        LP_BEGIN_TOWNPORTALPOOL,
        LP_TownPortalCreated,
        LP_TownPortalRemoved,
        LP_END_TOWNPORTALPOOL,
        // COpenGatePool::OnPacket
        LP_BEGIN_OPENGATEPOOL,
        LP_OpenGateCreated,
        LP_OpenGateRemoved,
        LP_END_OPENGATEPOOL,
        // CInstancePortalPool
        LP_BEGIN_JMS_INSTANCEPOTALPOOL,
        LP_JMS_InstancePortalCreated, // ItemID 2420004
        LP_JMS_InstancePortalRemoved,
        LP_END_JMS_INSTANCEPOTALPOOL,
        // CReactorPool::OnPacket
        LP_BEGIN_REACTORPOOL,
        LP_ReactorChangeState,
        LP_ReactorMove,
        LP_ReactorEnterField,
        LP_ReactorLeaveField,
        LP_END_REACTORPOOL,
        // CField_
        LP_BEGIN_ETCFIELDOBJ,
        // CField_SnowBall::OnPacket
        LP_SnowBallState,
        LP_SnowBallHit,
        LP_SnowBallMsg,
        LP_SnowBallTouch,
        // CField_Coconut::OnPacket
        LP_CoconutHit,
        LP_CoconutScore,
        // CField_GuildBoss::OnPacket
        LP_HealerMove,
        LP_PulleyStateChange,
        // CField_MonsterCarnival::OnPacket
        LP_MCarnivalEnter,
        LP_MCarnivalPersonalCP,
        LP_MCarnivalTeamCP,
        LP_MCarnivalResultSuccess,
        LP_MCarnivalResultFail,
        LP_MCarnivalDeath,
        LP_MCarnivalMemberOut,
        LP_MCarnivalGameResult,
        // CField_AriantArena::OnPacket
        LP_ArenaScore,
        // CField_Battlefield::OnPacket
        LP_BattlefieldEnter, // ?
        LP_BattlefieldScore,
        LP_BattlefieldTeamChanged,
        // CField_Witchtower::OnPacket
        LP_WitchtowerScore,
        // CField::OnPacket
        LP_HontaleTimer,
        LP_ChaosZakumTimer,
        LP_HontailTimer,
        LP_ZakumTimer,
        LP_END_ETCFIELDOBJ,
        // CScriptMan::OnPacket
        LP_BEGIN_SCRIPT,
        LP_ScriptMessage,
        LP_END_SCRIPT,
        // CShopDlg::OnPacket
        LP_BEGIN_SHOP,
        LP_OpenShopDlg,
        LP_ShopResult,
        LP_END_SHOP,
        // CAdminShopDlg::OnPacket
        LP_BEGIN_ADMINSHOP,
        LP_AdminShopResult,
        LP_AdminShopCommodity,
        LP_END_ADMINSHOP,
        // CTrunkDlg::OnPacket
        LP_TrunkResult,
        // CStoreBankDlg::OnPacket
        LP_BEGIN_STOREBANK,
        LP_StoreBankGetAllResult,
        LP_StoreBankResult,
        LP_END_STOREBANK,
        // CField::OnPacket
        LP_RPSGame,
        LP_Messenger,
        LP_MiniRoom,
        // CField_Tournament::OnPacket
        LP_BEGIN_TOURNAMENT,
        LP_Tournament,
        LP_TournamentMatchTable,
        LP_TournamentSetPrize,
        LP_TournamentNoticeUEW,
        LP_TournamentAvatarInfo,
        LP_END_TOURNAMENT,
        // CField_Wedding::OnPacket
        LP_BEGIN_WEDDING,
        LP_WeddingProgress,
        LP_WeddingCremonyEnd,
        LP_END_WEDDING,
        // CField_Pachinko
        LP_BEGIN_JMS_PACHINKO,
        LP_JMS_PachinkoMessage,
        LP_JMS_PachinkoOpen,
        LP_JMS_PachinkoPlay,
        LP_JMS_PachinkoUpdate,
        LP_END_JMS_PACHINKO,
        // CParcelDlg::OnPacket
        LP_Parcel,
        LP_END_FIELD,
        // CCashShop::OnPacket
        LP_BEGIN_CASHSHOP,
        LP_CashShopChargeParamResult,
        LP_JMS_POINTSHOP_PRESENT_DIALOG,
        LP_CashShopQueryCashResult,
        LP_CashShopCashItemResult,
        LP_CashShopPurchaseExpChanged,
        LP_CashShopGiftMateInfoResult,
        LP_JMS_POINTSHOP_FORCE_REQUEST,
        LP_JMS_POINTSHOP_KOC_PRESENT_DIALOG,
        LP_JMS_POINTSHOP_FREE_COUPON_DIALOG,
        LP_JMS_GACHAPON_STAMP_AND_OTOSHIDAMA_DIALOG,
        LP_CashShopCheckDuplicatedIDResult,
        LP_CashShopCheckNameChangePossibleResult,
        LP_CashShopRegisterNewCharacterResult,
        LP_CashShopCheckTransferWorldPossibleResult,
        LP_CashShopGachaponStampItemResult,
        LP_CashShopCashItemGachaponResult,
        LP_CashShopCashGachaponOpenResult,
        LP_ChangeMaplePointResult,
        LP_CashShopOneADay,
        LP_CashShopNoticeFreeCashItem,
        LP_CashShopMemberShopResult,
        LP_END_CASHSHOP,
        // CFuncKeyMappedMan::OnPacket
        LP_BEGIN_FUNCKEYMAPPED,
        LP_FuncKeyMappedInit,
        LP_PetConsumeItemInit,
        LP_PetConsumeMPItemInit,
        LP_JMS_PetConsumeCureItemInit,
        LP_END_FUNCKEYMAPPED,
        LP_CheckSSN2OnCreateNewCharacterResult,
        LP_CheckSPWOnCreateNewCharacterResult,
        LP_FirstSSNOnCreateNewCharacterResult,
        // CMapleTVMan::OnPacket
        LP_BEGIN_MAPLETV,
        LP_MapleTVUpdateMessage,
        LP_MapleTVClearMessage,
        LP_MapleTVSendMessageResult,
        LP_BroadSetFlashChangeEvent,
        LP_END_MAPLETV,
        // CITC::OnPacket
        LP_BEGIN_ITC,
        LP_ITCChargeParamResult,
        LP_ITCQueryCashResult,
        LP_ITCNormalItemResult,
        LP_END_ITC,
        // CUICharacterSaleDlg::OnPacket
        LP_BEGIN_CHARACTERSALE,
        LP_CheckDuplicatedIDResultInCS,
        LP_CreateNewCharacterResultInCS,
        LP_CreateNewCharacterFailInCS,
        LP_CharacterSale,
        LP_END_CHARACTERSALE,
        // CUIGoldHammer::OnPacket
        LP_BEGIN_GOLDHAMMER,
        LP_GoldHammere_s,
        LP_GoldHammerResult,
        LP_GoldHammere_e,
        LP_END_GOLDHAMMER,
        // CBattleRecordMan::OnPacket
        LP_BEGIN_BATTLERECORD,
        LP_BattleRecord_s,
        LP_BattleRecordDotDamageInfo,
        LP_BattleRecordRequestResult,
        LP_BattleRecord_e,
        LP_END_BATTLERECORD,
        // CUIItemUpgrade::OnPacket
        LP_BEGIN_ITEMUPGRADE,
        LP_ItemUpgrade_s,
        LP_ItemUpgradeResult,
        LP_ItemUpgradeFail,
        LP_ItemUpgrade_e,
        LP_END_ITEMUPGRADE,
        // CUIVega::OnPacket
        LP_BEGIN_VEGA,
        LP_Vega_s,
        LP_VegaResult,
        LP_VegaFail,
        LP_Vega_e,
        LP_END_VEGA,
        // CField::OnPacket
        LP_LogoutGift, // CWvsContext::OnLogoutGift
        LP_NO,
        // 独自仕様
        LP_CUSTOM_,
        LP_CUSTOM_WZ_HASH,
        LP_CUSTOM_CLIENT_PATCH,
        LP_CUSTOM_MEMORY_SCAN,
        UNKNOWN;

        private int value;

        Header(int val) {
            this.value = val;
        }

        Header() {
            this.value = -1;
        }

        @Override
        public int get() {
            return this.value;
        }

        @Override
        public void set(int val) {
            this.value = val;
        }
    }

}
