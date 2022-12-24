// サーバー側から送信されるパケットのヘッダの定義
package packet;

import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import java.util.ArrayList;

public class InPacket {

    // Encoder
    private ArrayList<Byte> packet = new ArrayList<>();
    private int encoded = 0;

    public InPacket(Header header) {
        short w = (short) header.Get();

        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public InPacket(short w) {
        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public void Encode1(byte b) {
        packet.add(b);
        encoded += 1;
    }

    public InPacket() {
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
        byte[] b = str.getBytes();
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
        byte[] b = str.getBytes();
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

    public MaplePacket Get() {
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

    public enum Header {
        // Names from v95 PDB
        // ログインサーバー
        LP_BEGIN_SOCKET,
        LP_CheckPasswordResult,
        //LP_GuestIDLoginResult,
        //LP_AccountInfoResult,
        LP_CheckUserLimitResult,
        //LP_SetAccountResult,
        //LP_ConfirmEULAResult,
        LP_CheckPinCodeResult,
        LP_UpdatePinCodeResult,
        LP_ViewAllCharResult,
        //LP_SelectCharacterByVACResult,
        LP_WorldInformation,
        LP_SelectWorldResult,
        LP_SelectCharacterResult,
        LP_CheckDuplicatedIDResult,
        LP_CreateNewCharacterResult,
        LP_DeleteCharacterResult,
        LP_MigrateCommand,
        LP_AliveReq,
        //LP_AuthenCodeChanged,
        //LP_AuthenMessage,
        LP_SecurityPacket,
        //LP_EnableSPWResult,
        //LP_DeleteCharacterOTPRequest,
        //LP_CheckCrcResult,
        //LP_LatestConnectedWorld,
        //LP_RecommendWorldMessage,
        //LP_CheckExtraCharInfoResult,
        //LP_CheckSPWResult,
        LP_END_SOCKET,
        // ゲームサーバー
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
        LP_ImitatedNPCResult,
        LP_ImitatedNPCData,
        LP_LimitedNPCDisableInfo,
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
        LP_END_CHARACTERDATA,
        LP_MacroSysDataInit,
        LP_BEGIN_STAGE,
        LP_SetField,
        LP_SetITC,
        LP_END_STAGE,
        LP_SetCashShop,
        LP_BEGIN_MAP,
        LP_SetBackgroundEffect,
        LP_SetMapObjectVisible,
        LP_ClearBackgroundEffect,
        LP_END_MAP,
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
        LP_ShowArenaResult,
        LP_StalkResult,
        LP_MassacreIncGauge,
        LP_MassacreResult,
        LP_QuickslotMappedInit,
        LP_FootHoldInfo,
        LP_RequestFootHoldInfo,
        LP_FieldKillCount,
        LP_BEGIN_USERPOOL,
        LP_UserEnterField,
        LP_UserLeaveField,
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
        LP_ShowPamsSongResult,
        LP_BEGIN_PET,
        LP_PetActivated,
        LP_PetEvol,
        LP_PetTransferField,
        LP_PetMove,
        LP_PetAction,
        LP_PetNameChanged,
        LP_PetLoadExceptionList,
        LP_END_PET,
        LP_PetActionCommand,
        LP_BEGIN_DRAGON,
        LP_DragonEnterField,
        LP_DragonMove,
        LP_DragonLeaveField,
        LP_END_DRAGON,
        LP_END_USERCOMMON,
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
        LP_END_USERREMOTE,
        LP_UserThrowGrenade,
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
        LP_END_USERLOCAL,
        LP_SkillCooltimeSet,
        LP_END_USERPOOL,
        LP_BEGIN_SUMMONED,
        LP_SummonedEnterField,
        LP_SummonedLeaveField,
        LP_SummonedMove,
        LP_SummonedAttack,
        LP_SummonedSkill,
        LP_END_SUMMONED,
        LP_SummonedHit,
        LP_BEGIN_MOBPOOL,
        LP_MobEnterField,
        LP_MobLeaveField,
        LP_MobChangeController,
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
        LP_MobHPChange,
        LP_MobCrcKeyChanged,
        LP_MobHPIndicator,
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
        LP_END_MOB,
        LP_MobAttackedByMob,
        LP_END_MOBPOOL,
        LP_BEGIN_NPCPOOL,
        LP_NpcEnterField,
        LP_NpcLeaveField,
        LP_NpcChangeController,
        LP_BEGIN_NPC,
        LP_NpcMove,
        LP_NpcUpdateLimitedInfo,
        LP_END_NPC,
        LP_NpcSpecialAction,
        LP_BEGIN_NPCTEMPLATE,
        LP_END_NPCTEMPLATE,
        LP_NpcSetScript,
        LP_END_NPCPOOL,
        LP_BEGIN_EMPLOYEEPOOL,
        LP_EmployeeEnterField,
        LP_EmployeeLeaveField,
        LP_END_EMPLOYEEPOOL,
        LP_EmployeeMiniRoomBalloon,
        LP_BEGIN_DROPPOOL,
        LP_DropEnterField,
        LP_DropReleaseAllFreeze,
        LP_DropLeaveField,
        LP_END_DROPPOOL,
        LP_BEGIN_MESSAGEBOXPOOL,
        LP_CreateMessgaeBoxFailed,
        LP_MessageBoxEnterField,
        LP_END_MESSAGEBOXPOOL,
        LP_MessageBoxLeaveField,
        LP_AffectedAreaCreated,
        LP_BEGIN_AFFECTEDAREAPOOL,
        LP_AffectedAreaRemoved,
        LP_END_AFFECTEDAREAPOOL,
        LP_BEGIN_TOWNPORTALPOOL,
        LP_TownPortalCreated,
        LP_END_TOWNPORTALPOOL,
        LP_TownPortalRemoved,
        LP_BEGIN_OPENGATEPOOL,
        LP_OpenGateCreated,
        LP_END_OPENGATEPOOL,
        LP_OpenGateRemoved,
        LP_BEGIN_REACTORPOOL,
        LP_ReactorChangeState,
        LP_ReactorMove,
        LP_ReactorEnterField,
        LP_END_REACTORPOOL,
        LP_ReactorLeaveField,
        LP_BEGIN_ETCFIELDOBJ,
        LP_SnowBallState,
        LP_SnowBallHit,
        LP_SnowBallMsg,
        LP_SnowBallTouch,
        LP_CoconutHit,
        LP_CoconutScore,
        LP_HealerMove,
        LP_PulleyStateChange,
        LP_MCarnivalEnter,
        LP_MCarnivalPersonalCP,
        LP_MCarnivalTeamCP,
        LP_MCarnivalResultSuccess,
        LP_MCarnivalResultFail,
        LP_MCarnivalDeath,
        LP_MCarnivalMemberOut,
        LP_MCarnivalGameResult,
        LP_ArenaScore,
        LP_BattlefieldEnter,
        LP_BattlefieldScore,
        LP_BattlefieldTeamChanged,
        LP_WitchtowerScore,
        LP_HontaleTimer,
        LP_ChaosZakumTimer,
        LP_HontailTimer,
        LP_END_ETCFIELDOBJ,
        LP_ZakumTimer,
        LP_BEGIN_SCRIPT,
        LP_END_SCRIPT,
        LP_ScriptMessage,
        LP_BEGIN_SHOP,
        LP_OpenShopDlg,
        LP_END_SHOP,
        LP_ShopResult,
        LP_AdminShopResult,
        LP_BEGIN_ADMINSHOP,
        LP_AdminShopCommodity,
        LP_END_ADMINSHOP,
        LP_TrunkResult,
        LP_BEGIN_STOREBANK,
        LP_StoreBankGetAllResult,
        LP_END_STOREBANK,
        LP_StoreBankResult,
        LP_RPSGame,
        LP_Messenger,
        LP_MiniRoom,
        LP_BEGIN_TOURNAMENT,
        LP_Tournament,
        LP_TournamentMatchTable,
        LP_TournamentSetPrize,
        LP_TournamentNoticeUEW,
        LP_END_TOURNAMENT,
        LP_TournamentAvatarInfo,
        LP_BEGIN_WEDDING,
        LP_WeddingProgress,
        LP_END_WEDDING,
        LP_WeddingCremonyEnd,
        LP_END_FIELD,
        LP_Parcel,
        LP_BEGIN_CASHSHOP,
        LP_CashShopChargeParamResult,
        LP_CashShopQueryCashResult,
        LP_CashShopCashItemResult,
        LP_CashShopPurchaseExpChanged,
        LP_CashShopGiftMateInfoResult,
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
        LP_BEGIN_FUNCKEYMAPPED,
        LP_FuncKeyMappedInit,
        LP_PetConsumeItemInit,
        LP_END_FUNCKEYMAPPED,
        LP_PetConsumeMPItemInit,
        LP_CheckSSN2OnCreateNewCharacterResult,
        LP_CheckSPWOnCreateNewCharacterResult,
        LP_FirstSSNOnCreateNewCharacterResult,
        LP_BEGIN_MAPLETV,
        LP_MapleTVUpdateMessage,
        LP_MapleTVClearMessage,
        LP_MapleTVSendMessageResult,
        LP_BroadSetFlashChangeEvent,
        LP_END_MAPLETV,
        LP_BEGIN_ITC,
        LP_ITCChargeParamResult,
        LP_ITCQueryCashResult,
        LP_END_ITC,
        LP_ITCNormalItemResult,
        LP_BEGIN_CHARACTERSALE,
        LP_CheckDuplicatedIDResultInCS,
        LP_CreateNewCharacterResultInCS,
        LP_CreateNewCharacterFailInCS,
        LP_CharacterSale,
        LP_END_CHARACTERSALE,
        LP_BEGIN_GOLDHAMMER,
        LP_GoldHammere_s,
        LP_GoldHammerResult,
        LP_END_GOLDHAMMER,
        LP_GoldHammere_e,
        LP_BEGIN_BATTLERECORD,
        LP_BattleRecord_s,
        LP_BattleRecordDotDamageInfo,
        LP_BattleRecordRequestResult,
        LP_BattleRecord_e,
        LP_END_BATTLERECORD,
        LP_BEGIN_ITEMUPGRADE,
        LP_ItemUpgrade_s,
        LP_ItemUpgradeResult,
        LP_ItemUpgradeFail,
        LP_END_ITEMUPGRADE,
        LP_ItemUpgrade_e,
        LP_BEGIN_VEGA,
        LP_Vega_s,
        LP_VegaResult,
        LP_VegaFail,
        LP_END_VEGA,
        LP_Vega_e,
        LP_LogoutGift,
        LP_NO,
        // ヘッダに対応する処理の名前を定義
        UNKNOWN_BEGIN,
        // added
        MINIGAME_PACHINKO_UPDATE_TAMA,
        UNKNOWN_RELOAD_MINIMAP,
        UNKNOWN_RELOAD_MAP,
        HELLO(0x000E),
        // unknown
        LOGIN_AUTH, // 名称不明
        FISHING_BOARD_UPDATE,
        PLAYER_NPC,
        ENERGY,
        GHOST_POINT,
        GHOST_STATUS,
        FAIRY_PEND_MSG,
        PYRAMID_UPDATE,
        PYRAMID_RESULT,
        FISHING_CAUGHT,
        PLAYER_HINT,
        TAMA_BOX_SUCCESS,
        TAMA_BOX_FAILURE,
        GAME_POLL_REPLY,
        TALK_MONSTER,
        GAME_POLL_QUESTION,
        MONSTER_PROPERTIES,
        REMOVE_TALK_MONSTER,
        SHOW_MAGNET,
        CS_UPDATE,
        CS_OPERATION,
        UPDATE_BEANS,
        TIP_BEANS,
        OPEN_BEANS,
        SHOOT_BEANS,
        UNKNOWN_END;

        private int value;

        Header(int header) {
            value = header;
        }

        Header() {
            value = 0xFFFF;
        }

        private boolean Set(int header) {
            value = header;
            return true;
        }

        public int Get() {
            return value;
        }
    }

    public static void SetForJMSv164() {
        // ===== Login Server 1 =====
        Header.LP_CheckPasswordResult.Set(0x0000);
        Header.LP_WorldInformation.Set(0x0002);
        Header.LP_SelectWorldResult.Set(0x0003);
        Header.LP_SelectCharacterResult.Set(0x0004);
        Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        Header.LP_CreateNewCharacterResult.Set(0x0006);
        Header.LP_DeleteCharacterResult.Set(0x0007);
        // ===== Change Channel =====
        Header.LP_MigrateCommand.Set(0x0008);
        // ===== Login Server 2 =====
        Header.HELLO.Set(0x000E);
        Header.LOGIN_AUTH.Set(0x0018);
        // ===== Game Server 1 =====
        Header.LP_InventoryOperation.Set(0x0016);
        Header.LP_InventoryGrow.Set(Header.LP_InventoryOperation.Get() + 0x01);
        Header.LP_StatChanged.Set(Header.LP_InventoryOperation.Get() + 0x02);
        Header.LP_BroadcastMsg.Set(0x0037);
        // ===== Cash Shop =====
        // 0x0066 + 1
        Header.LP_SetField.Set(0x0067);
        Header.LP_SetITC.Set(Header.LP_SetField.Get() + 0x01); // 0x0068
        Header.LP_SetCashShop.Set(Header.LP_SetField.Get() + 0x02); // 0x0069
        // ===== Game Server 2 =====
        //Header.SERVER_BLOCKED.Set(0x0085);
        //Header.SHOW_EQUIP_EFFECT.Set(0x0086);
        Header.LP_UserChat.Set(0x0083);
        // ===== Game Server 3 =====
        // 00A3 -> 0083
        //Header.SPAWN_PLAYER.Set(0x00A1);
        //Header.REMOVE_PLAYER_FROM_MAP.Set(0x00A2);
        Header.LP_UserItemUpgradeEffect.Set(0x0087); // 00A8 -> 0087

        // ===== Mob =====
        Header.LP_MobEnterField.Set(0x00C2);
        Header.LP_MobLeaveField.Set(Header.LP_MobEnterField.Get() + 0x01);
        Header.LP_MobChangeController.Set(Header.LP_MobEnterField.Get() + 0x02);
        // ===== Mob Movement ====
        Header.LP_MobMove.Set(0x00C5);
        Header.LP_MobCtrlAck.Set(Header.LP_MobMove.Get() + 0x01);
        // Header.MOVE_MONSTER.Get() + 0x02 は存在しない
        Header.LP_MobStatSet.Set(Header.LP_MobMove.Get() + 0x03);
        Header.LP_MobStatReset.Set(Header.LP_MobMove.Get() + 0x04);
        // Header.MOVE_MONSTER.Get() + 0x05
        Header.LP_MobAffected.Set(Header.LP_MobMove.Get() + 0x06);
        Header.LP_MobDamaged.Set(Header.LP_MobMove.Get() + 0x07);
        // Header.MOVE_MONSTER.Get() + 0x08
        // Header.MOVE_MONSTER.Get() + 0x09
        // Header.MOVE_MONSTER.Get() + 0x0A
        Header.LP_MobHPIndicator.Set(Header.LP_MobMove.Get() + 0x0B);
        /*
        Header.SHOW_MAGNET.Set(Header.MOVE_MONSTER.Get() + 0x0C);
        Header.CATCH_MONSTER.Set(0x0114);
        Header.MOB_SPEAKING.Set(0x0115);
        // 0x0116 @0116 int,int,int,int, 何らかの変数が更新されるが詳細不明
        Header.MONSTER_PROPERTIES.Set(0x0117);
        Header.REMOVE_TALK_MONSTER.Set(0x0118);
        Header.TALK_MONSTER.Set(0x0119);
         */

        Header.LP_NpcEnterField.Set(0x00D5);
        Header.LP_NpcChangeController.Set(0x00D7);
        Header.LP_ScriptMessage.Set(0x0100); // 00698C63
        Header.LP_OpenShopDlg.Set(Header.LP_ScriptMessage.Get() + 0x01);
        Header.LP_ShopResult.Set(Header.LP_ScriptMessage.Get() + 0x02);

        //Header.OPEN_STORAGE.Set(0x0104);
        //Header.MERCH_ITEM_MSG.Set(Header.NPC_TALK.Get() + 0x05);
        //Header.MERCH_ITEM_STORE.Set(Header.NPC_TALK.Get() + 0x06);
        //Header.RPS_GAME.Set(Header.NPC_TALK.Get() + 0x07);
        //Header.MESSENGER.Set(Header.NPC_TALK.Get() + 0x08);
        Header.LP_MiniRoom.Set(Header.LP_ScriptMessage.Get() + 0x09);
    }

    public static void SetForJMSv176() {
        Header.LP_CheckPasswordResult.Set(0x0000);
        Header.LP_WorldInformation.Set(0x0002);
        Header.LP_SelectWorldResult.Set(0x0003);
        Header.LP_SelectCharacterResult.Set(0x0004);
        Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        Header.LP_CreateNewCharacterResult.Set(0x0006);
        Header.LP_DeleteCharacterResult.Set(0x0007);
        Header.LP_MigrateCommand.Set(0x0008);
        Header.LP_AliveReq.Set(0x0009);
        Header.LOGIN_AUTH.Set(0x0018);

        Header.LP_SetField.Set(0x007B); // wrong

        Header.LP_NpcEnterField.Set(0xFFFF);
        Header.LP_FamilyPrivilegeList.Set(0xFFFF);
        Header.LP_FuncKeyMappedInit.Set(0xFFFF);
        Header.LP_BroadcastMsg.Set(0xFFFF);
        Header.LP_FamilyInfoResult.Set(0xFFFF);
        Header.LP_SetCashShop.Set(0xFFFF);
    }

    public static void SetForJMSv184() {
        Header.LP_CheckPasswordResult.Set(0x0000);
        Header.LP_WorldInformation.Set(0x0002);
        Header.LP_SelectWorldResult.Set(0x0003);
        Header.LP_SelectCharacterResult.Set(0x0004);
        Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        Header.LP_CreateNewCharacterResult.Set(0x0006);
        Header.LP_DeleteCharacterResult.Set(0x0007);
        Header.LP_MigrateCommand.Set(0x0008);
        Header.LP_AliveReq.Set(0x0009);
        Header.LOGIN_AUTH.Set(0x0018);

        Header.LP_SetField.Set(0x007B);

        Header.LP_NpcEnterField.Set(0xFFFF);
        Header.LP_FamilyPrivilegeList.Set(0xFFFF);
        Header.LP_FuncKeyMappedInit.Set(0xFFFF);
        Header.LP_BroadcastMsg.Set(0xFFFF);
        Header.LP_FamilyInfoResult.Set(0xFFFF);
        Header.LP_SetCashShop.Set(0xFFFF);
    }

    // JMS v186.1 ProcessPacket
    public static void SetForJMSv186() {
        // ログインサーバー
        Header.LP_BEGIN_SOCKET.Set(0);
        {
            Header.LP_CheckPasswordResult.Set(0x0000);
            // 0x0001, LP_CheckUserLimitResult or LP_AccountInfoResult
            Header.LP_WorldInformation.Set(0x0002);
            Header.LP_SelectWorldResult.Set(0x0003);
            Header.LP_SelectCharacterResult.Set(0x0004);
            Header.LP_CheckDuplicatedIDResult.Set(0x0005);
            Header.LP_CreateNewCharacterResult.Set(0x0006);
            Header.LP_DeleteCharacterResult.Set(0x0007);
            Header.LP_MigrateCommand.Set(0x0008); // Change Channel
            Header.LP_AliveReq.Set(0x0009);
            // 0x000A
            // 0x000B
            Header.LP_SecurityPacket.Set(0x000C); // HackShield HeartBeat
            // 0x000D CHANNEL_SELECTED?
            // 0x000E @000E ..., @0011 00 00 を送信
            // 0x000F 未使用
            // 0x0010 未使用
            // 0x0011 未使用
            // 0x0012
            // 0x0013
            // 0x0014
            // 0x0015 @0015 [00], 不法プログラムまたは悪性コードが感知されたためゲームを強制終了します。
            Header.LP_CheckPinCodeResult.Set(0x0016); // 違うかも
            // 0x0017
            Header.LOGIN_AUTH.Set(0x0018);
            // 0x0019
            // 0x001A
        }
        Header.LP_END_SOCKET.Set(0);

        // ゲームサーバー
        Header.LP_BEGIN_CHARACTERDATA.Set(0);
        {
            Header.LP_InventoryOperation.Set(0x001B);
            Header.LP_InventoryGrow.Set(0x001C);
            Header.LP_StatChanged.Set(0x001D);
            Header.LP_TemporaryStatSet.Set(0x001E);
            Header.LP_TemporaryStatReset.Set(0x001F);
            Header.LP_ForcedStatSet.Set(0x0020);
            Header.LP_ForcedStatReset.Set(0x0021);
            Header.LP_ChangeSkillRecordResult.Set(0x0022);
            Header.LP_SkillUseResult.Set(0x0023);
            Header.LP_GivePopularityResult.Set(0x0024);
            Header.LP_Message.Set(0x0025);
            Header.LP_MemoResult.Set(0x0026);
            Header.LP_MapTransferResult.Set(0x0027);
            Header.LP_AntiMacroResult.Set(0x0028); // 未使用?
            Header.LP_InitialQuizStart.Set(0x0029);
            Header.LP_ClaimResult.Set(0x002A); // @002A [02-03, 41-47]..., 通報後のダイアログ通知
            Header.LP_SetClaimSvrAvailableTime.Set(0x002B);
            Header.LP_ClaimSvrStatusChanged.Set(0x002C);
            Header.LP_SetTamingMobInfo.Set(0x002D);
            Header.LP_QuestClear.Set(0x002E);
            Header.LP_EntrustedShopCheckResult.Set(0x002F);
            Header.LP_SkillLearnItemResult.Set(0x0030);
            Header.LP_SortItemResult.Set(0x0031); //逆?
            Header.LP_GatherItemResult.Set(0x0032);
            // 0x0033 未使用
            // 0x0034 未使用
            Header.LP_CharacterInfo.Set(0x0035);
            Header.LP_PartyResult.Set(0x0036);
            Header.LP_ExpeditionRequest.Set(0x0037);
            Header.LP_ExpeditionNoti.Set(0x0038);
            Header.LP_FriendResult.Set(0x0039);
            Header.LP_GuildRequest.Set(0x003A);
            Header.LP_GuildResult.Set(0x003B);
            Header.LP_AllianceResult.Set(0x003C);
            Header.LP_TownPortal.Set(0x003D);
            Header.LP_OpenGate.Set(0x003E); // メカニックならこの時点では未実装?
            Header.LP_BroadcastMsg.Set(0x003F);
            Header.LP_IncubatorResult.Set(0x0040); // ピグミー
            Header.LP_ShopScannerResult.Set(0x0041);
            Header.LP_ShopLinkResult.Set(0x0042);
            Header.LP_MarriageRequest.Set(0x0043);
            Header.LP_MarriageResult.Set(0x0044);
            Header.LP_WeddingGiftResult.Set(0x0045); // @0045 [09], ウェディング登録? @0091が送信される
            Header.LP_MarriedPartnerMapTransfer.Set(0x0046); // @0046 int,int
            Header.LP_CashPetFoodResult.Set(0x0047); // @0047 [01]..., 現在ペットはこのえさが食べることができません。もう一度確認してください。
            Header.LP_SetWeekEventMessage.Set(0x0048);
            Header.LP_SetPotionDiscountRate.Set(0x0049);
            Header.LP_BridleMobCatchFail.Set(0x004A); // 0x004A @004A ..., 当該モンスターの体力が強くてできません。
            // 0x004B 未使用
            // パチンコ
            {
                Header.MINIGAME_PACHINKO_UPDATE_TAMA.Set(0x4C);
                // 0x004D パチンコ景品受け取りUI
                // 0x004E @004E int,int, パチンコ球をx子プレゼントします。というダイアログ誤字っているのでたぶん未実装的な奴
            }
            // 0x004F @004F [01 or 03], プレゼントの通知
            // 0x0050 @0050 strig, string..., 相性占い結果UI
            Header.FISHING_BOARD_UPDATE.Set(0x0051);
            // 0x0052 @0052 String, 任意メッセージをダイアログに表示
            // 0x0053 @0053 [01 (00, 02は謎)], ワールド変更申請のキャンセル
            // 0x0054 @0054 int, プレイタイム終了まで残りx分x秒です。
            // 0x0055 @0055 byte, なんも処理がされない関数
            Header.PLAYER_NPC.Set(0x0056);
            Header.LP_MonsterBookSetCard.Set(0x0057);
            Header.LP_MonsterBookSetCover.Set(0x0058);
            // 0x0059 BBS_OPERATION?
            // 0x005A @005A String, 任意メッセージをダイアログに表示
            Header.LP_AvatarMegaphoneRes.Set(0x005B);
            // 0x005C
            // 0x005D
            Header.UNKNOWN_RELOAD_MINIMAP.Set(0x005E);
            // 0x005F
            // 0x0060
            // 0x0061
            Header.ENERGY.Set(0x0062);
            Header.GHOST_POINT.Set(0x0063);
            Header.GHOST_STATUS.Set(0x0064);
            Header.FAIRY_PEND_MSG.Set(0x0065);
            Header.LP_FamilyChartResult.Set(0x0066);
            Header.LP_FamilyInfoResult.Set(0x0067);
            Header.LP_FamilyResult.Set(0x0068);
            Header.LP_FamilyJoinRequest.Set(0x0069);
            Header.LP_FamilyJoinRequestResult.Set(0x006A);
            Header.LP_FamilyJoinAccepted.Set(0x006B);
            Header.LP_FamilyPrivilegeList.Set(0x006C);
            Header.LP_FamilyFamousPointIncResult.Set(0x006D);
            Header.LP_FamilyNotifyLoginOrLogout.Set(0x006E);
            Header.LP_FamilySetPrivilege.Set(0x006F);
            Header.LP_FamilySummonRequest.Set(0x0070);
            Header.LP_NotifyLevelUp.Set(0x0071);
            Header.LP_NotifyWedding.Set(0x0072);
            Header.LP_NotifyJobChange.Set(0x0073);
            // 0x0074
            Header.LP_SetPassenserRequest.Set(0x0075);
            Header.LP_SuccessInUseGachaponBox.Set(0x0076); // @0076 [不明], マジェスティックボックスの中身獲得後のUI
            Header.LP_ScriptProgressMessage.Set(0x0077); // 画面中央の黄色文字
            Header.LP_DataCRCCheckFailed.Set(0x0078); // @0078 string, イベンドガイドのNPC会話で任意文字列を表示
            // 0x0079 @0079 [0x02 or], イベンドガイドのNPC会話のエラーメッセージの呼び出し
            // 0x007A
            // 0x007B @007B int,string, 灰色のメッセージ
            Header.LP_AskUserWhetherUsePamsSong.Set(0x007C); // ファムの歌を利用するか選択するUI, @00C2 [00or01]が送信される01は使用フラグ
            Header.LP_MacroSysDataInit.Set(0x007D);
        }
        Header.LP_END_CHARACTERDATA.Set(0);
        // ステージ切り替え
        Header.LP_BEGIN_STAGE.Set(0);
        {
            Header.LP_SetField.Set(0x007E);
            Header.LP_SetITC.Set(0x007F);
            Header.LP_SetCashShop.Set(0x0080);
        }
        Header.LP_END_STAGE.Set(0);
        // マップ読み込み
        Header.LP_BEGIN_MAP.Set(0);
        {
            Header.LP_SetBackgroundEffect.Set(0x0081);
            Header.LP_SetMapObjectVisible.Set(0x0082);
            Header.LP_ClearBackgroundEffect.Set(0x0083);
        }
        Header.LP_END_MAP.Set(0);
        // マップ上の処理
        Header.LP_BEGIN_FIELD.Set(0);
        {
            Header.LP_TransferFieldReqIgnored.Set(0x0084); // @0084 [01-07], マップ移動時のエラーメッセージ (テレポストーン?)
            Header.LP_TransferChannelReqIgnored.Set(0x0085);
            Header.LP_FieldSpecificData.Set(0x0086);
            Header.LP_GroupMessage.Set(0x0087);
            Header.LP_Whisper.Set(0x0088);
            Header.LP_MobSummonItemUseResult.Set(0x0089);
            Header.LP_FieldEffect.Set(0x008A);
            Header.LP_FieldObstacleOnOff.Set(0x008B);
            Header.LP_FieldObstacleOnOffStatus.Set(0x008C);
            Header.LP_FieldObstacleAllReset.Set(0x008D);
            Header.LP_BlowWeather.Set(0x008E);
            Header.LP_PlayJukeBox.Set(0x008F);
            Header.LP_AdminResult.Set(0x0090);
            Header.LP_Quiz.Set(0x0091);
            Header.LP_Desc.Set(0x0092);
            Header.LP_Clock.Set(0x0093);
            //Header.BOAT_EFF.Set(0x0094);
            //Header.BOAT_EFFECT.Set(0x0095);
            // 0x0096
            // 0x0097
            // 0x0098
            Header.LP_DestroyClock.Set(0x0099);
            // 0x009A 未使用
            // 0x009B
            //Header.PYRAMID_UPDATE.Set(0x009C);
            //Header.PYRAMID_RESULT.Set(0x009D);
            // 0x009E
            Header.LP_FootHoldInfo.Set(0x009F);
            // 0x00A0 0x00F2を送信

            // プレイヤー
            Header.LP_BEGIN_USERPOOL.Set(0);
            {
                Header.LP_UserEnterField.Set(0x00A1);
                Header.LP_UserLeaveField.Set(0x00A2);
                Header.LP_BEGIN_USERCOMMON.Set(0);
                {
                    Header.LP_UserChat.Set(0x00A3);
                    Header.LP_UserChatNLCPQ.Set(0x00A4);
                    Header.LP_UserADBoard.Set(0x00A5);
                    Header.LP_UserMiniRoomBalloon.Set(0x00A6);
                    Header.LP_UserConsumeItemEffect.Set(0x00A7);
                    Header.LP_UserItemUpgradeEffect.Set(0x00A8); // 書
                    Header.LP_UserItemHyperUpgradeEffect.Set(0x00A9); // 装備強化の書
                    Header.LP_UserItemOptionUpgradeEffect.Set(0x00AA); // ミラクルキューブ
                    Header.LP_UserItemReleaseEffect.Set(0x00AB); // 虫眼鏡
                    Header.LP_UserItemUnreleaseEffect.Set(0x00AC); // 虫眼鏡?
                    Header.LP_UserHitByUser.Set(0x00AD); // Damage Effect
                    // 0x00AE
                    // 0x00AF
                    // 0x00B0
                    Header.LP_UserFollowCharacter.Set(0x00B1);
                    Header.FISHING_CAUGHT.Set(0x00B2); // 名称不明
                    Header.LP_ShowPamsSongResult.Set(0x00B3);
                    // ペット
                    Header.LP_BEGIN_PET.Set(0);
                    {
                        Header.LP_PetActivated.Set(0x00B4);
                        Header.LP_PetEvol.Set(0x00B5);
                        Header.LP_PetTransferField.Set(0x00B6);
                        Header.LP_PetMove.Set(0x00B7);
                        Header.LP_PetAction.Set(0x00B8);
                        Header.LP_PetNameChanged.Set(0x00B9);
                        Header.LP_PetLoadExceptionList.Set(0x00BA);
                        Header.LP_PetActionCommand.Set(0x00BB);
                    }
                    Header.LP_END_PET.Set(0);
                    // 召喚
                    Header.LP_BEGIN_SUMMONED.Set(0);
                    {
                        Header.LP_SummonedEnterField.Set(0x00BC);
                        Header.LP_SummonedLeaveField.Set(0x00BD);
                        Header.LP_SummonedMove.Set(0x00BE);
                        Header.LP_SummonedAttack.Set(0x00BF);
                        Header.LP_SummonedSkill.Set(0x00C0);
                        Header.LP_SummonedHit.Set(0x00C1);
                    }
                    Header.LP_END_SUMMONED.Set(0);
                    // エヴァンのドラゴン
                    Header.LP_BEGIN_DRAGON.Set(0);
                    {
                        Header.LP_DragonEnterField.Set(0x00C2);
                        Header.LP_DragonMove.Set(0x00C3);
                        Header.LP_DragonLeaveField.Set(0x00C4);
                    }
                    Header.LP_END_DRAGON.Set(0);
                }
                Header.LP_END_USERCOMMON.Set(0);
                // 他のプレイヤー
                Header.LP_BEGIN_USERREMOTE.Set(0);
                {
                    // 0x00C5 未使用
                    Header.LP_UserMove.Set(0x00C6);
                    Header.LP_UserMeleeAttack.Set(0x00C7);
                    Header.LP_UserShootAttack.Set(0x00C8);
                    Header.LP_UserMagicAttack.Set(0x00C9);
                    Header.LP_UserBodyAttack.Set(0x00CA);
                    Header.LP_UserSkillPrepare.Set(0x00CB);
                    Header.LP_UserSkillCancel.Set(0x00CC);
                    Header.LP_UserHit.Set(0x00CD);
                    Header.LP_UserEmotion.Set(0x00CE);
                    Header.LP_UserSetActiveEffectItem.Set(0x00CF);
                    Header.LP_UserShowUpgradeTombEffect.Set(0x00D0);
                    Header.LP_UserSetActivePortableChair.Set(0x00D1);
                    Header.LP_UserAvatarModified.Set(0x00D2);
                    Header.LP_UserEffectRemote.Set(0x00D3);
                    Header.LP_UserTemporaryStatSet.Set(0x00D4);
                    Header.LP_UserTemporaryStatReset.Set(0x00D5);
                    Header.LP_UserHP.Set(0x00D6);
                    Header.LP_UserGuildNameChanged.Set(0x00D7);
                    Header.LP_UserGuildMarkChanged.Set(0x00D8);
                }
                Header.LP_END_USERREMOTE.Set(0);

                Header.LP_UserThrowGrenade.Set(0x00D9); // 不明
                // クライアントサイドの処理
                Header.LP_BEGIN_USERLOCAL.Set(0);
                {
                    Header.LP_UserSitResult.Set(0x00DA);
                    Header.LP_UserEmotionLocal.Set(0x00DB); // 0x00CEと同一
                    Header.LP_UserEffectLocal.Set(0x00DC); // 0x00D3と同一
                    Header.LP_UserTeleport.Set(0x00DD);
                    Header.LP_Premium.Set(0x00DE); // 未使用?
                    Header.LP_MesoGive_Succeeded.Set(0x00DF);
                    Header.LP_MesoGive_Failed.Set(0x00E0);
                    Header.LP_Random_Mesobag_Succeed.Set(0x00E1);
                    Header.LP_Random_Mesobag_Failed.Set(0x00E2);
                    Header.LP_UserQuestResult.Set(0x00E3);
                    Header.LP_NotifyHPDecByField.Set(0x00E4);
                    Header.LP_UserPetSkillChanged.Set(0x00E5);
                    Header.LP_UserBalloonMsg.Set(0x00E6); // @00E6 "文字列",short1,short0,byte0, 不明
                    Header.LP_PlayEventSound.Set(0x00E7);
                    Header.PLAYER_HINT.Set(0x00E8); // LP_PlayMinigameSound?
                    Header.LP_UserMakerResult.Set(0x00E9); // パケットの構造が複雑, メルをなくしました。(-xxxx)と表示される
                    Header.LP_UserOpenConsultBoard.Set(0x00EA); // COUNSELのUI, @010Bが送信される
                    Header.LP_UserOpenClassCompetitionPage.Set(0x00EB); // クラス対抗戦UI
                    Header.LP_UserOpenUI.Set(0x00EC); // @00EC [byte], 強制的にUIを開く
                    Header.LP_UserOpenUIWithOption.Set(0x00ED);
                    Header.LP_SetDirectionMode.Set(0x00EE);
                    Header.LP_SetStandAloneMode.Set(0x00EF);
                    Header.LP_UserHireTutor.Set(0x00F0);
                    Header.LP_UserTutorMsg.Set(0x00F1);
                    Header.LP_IncCombo.Set(0x00F2);
                    Header.TAMA_BOX_SUCCESS.Set(0x00F3); // パチンコ
                    Header.TAMA_BOX_FAILURE.Set(0x00F4); // パチンコ
                    Header.LP_UserRandomEmotion.Set(0x00F5);
                    Header.LP_ResignQuestReturn.Set(0x00F6);
                    Header.LP_PassMateName.Set(0x00F7);
                    Header.LP_SetRadioSchedule.Set(0x00F8);
                    Header.LP_UserOpenSkillGuide.Set(0x00F9); // アラン4次スキルの説明UI
                    Header.LP_UserNoticeMsg.Set(0x00FA); // OK
                    Header.LP_UserChatMsg.Set(0x00FB); // OK
                    // 0x00FC
                    // 0x00FD, LP_UserTimeBombAttack? @00FD 時間, 謎のタイマー出現
                    // 0x00FE @00FE int,int,int,int吹っ飛び判定,intダメージ量,..., 攻撃, 被ダメ, KB動作
                    Header.LP_UserPassiveMove.Set(0x00FF);
                    Header.LP_UserFollowCharacterFailed.Set(0x0100); // OK
                    Header.GAME_POLL_QUESTION.Set(0x0101);
                    Header.LP_SkillCooltimeSet.Set(0x0102);
                }
                Header.LP_END_USERLOCAL.Set(0);
            }
            Header.LP_END_USERPOOL.Set(0); // 0x0103 未使用のはず

            // Mob情報
            Header.LP_BEGIN_MOBPOOL.Set(0);
            {
                Header.LP_MobEnterField.Set(0x0104);
                Header.LP_MobLeaveField.Set(0x0105);
                Header.LP_MobChangeController.Set(0x0106);
                // Mobの処理
                Header.LP_BEGIN_MOB.Set(0);
                {
                    Header.LP_MobMove.Set(0x0107);
                    Header.LP_MobCtrlAck.Set(0x0108);
                    Header.LP_MobCtrlHint.Set(0x0109);
                    Header.LP_MobStatSet.Set(0x010A);
                    Header.LP_MobStatReset.Set(0x010B);
                    Header.LP_MobSuspendReset.Set(0x010C);
                    Header.LP_MobAffected.Set(0x010D);
                    Header.LP_MobDamaged.Set(0x010E);
                    // 0x010F, LP_MobSpecialEffectBySkill
                    // 0x0110, LP_MobHPChange 未使用
                    // 0x0111, LP_MobCrcKeyChanged @0111 int, @00A2を送信
                    Header.LP_MobHPIndicator.Set(0x0112);
                    Header.SHOW_MAGNET.Set(0x0113); // ???
                    Header.LP_MobCatchEffect.Set(0x0114);
                    Header.LP_MobSpeaking.Set(0x0115);
                    // 0x0116 @0116 int,int,int,int, 何らかの変数が更新されるが詳細不明
                    Header.MONSTER_PROPERTIES.Set(0x0117);
                    Header.REMOVE_TALK_MONSTER.Set(0x0118);
                    Header.TALK_MONSTER.Set(0x0119);
                    // 0x011A
                    // 0x011B
                }
                Header.LP_END_MOB.Set(0);
                // 0x011C
            }
            Header.LP_END_MOBPOOL.Set(0); // 0x011D 未使用のはず
            // NPC情報
            Header.LP_BEGIN_NPCPOOL.Set(0);
            {
                Header.LP_NpcEnterField.Set(0x011E);
                Header.LP_NpcLeaveField.Set(0x011F);
                Header.LP_NpcChangeController.Set(0x0120);
                // NPC
                Header.LP_BEGIN_NPC.Set(0);
                {
                    Header.LP_NpcMove.Set(0x0121);
                    Header.LP_NpcUpdateLimitedInfo.Set(0x0122);
                    Header.LP_NpcSpecialAction.Set(0x0123);
                }
                Header.LP_END_NPC.Set(0);
                // ???
                Header.LP_BEGIN_NPCTEMPLATE.Set(0);
                {
                    Header.LP_NpcSetScript.Set(0x0124);
                }
                Header.LP_END_NPCTEMPLATE.Set(0);
            }
            Header.LP_END_NPCPOOL.Set(0); // 0x0125 未使用のはず
            // 雇用商人
            Header.LP_BEGIN_EMPLOYEEPOOL.Set(0);
            {
                Header.LP_EmployeeEnterField.Set(0x0126);
                Header.LP_EmployeeLeaveField.Set(0x0127);
                Header.LP_EmployeeMiniRoomBalloon.Set(0x0128);
            }
            Header.LP_END_EMPLOYEEPOOL.Set(0);
            // ドロップアイテム
            Header.LP_BEGIN_DROPPOOL.Set(0);
            {
                Header.LP_DropEnterField.Set(0x0129);
                Header.LP_DropLeaveField.Set(0x012A);
            }
            Header.LP_END_DROPPOOL.Set(0);
            // ???
            Header.LP_BEGIN_MESSAGEBOXPOOL.Set(0);
            {
                Header.LP_CreateMessgaeBoxFailed.Set(0x012B);
                Header.LP_MessageBoxEnterField.Set(0x012C);
                Header.LP_MessageBoxLeaveField.Set(0x012D);
            }
            Header.LP_END_MESSAGEBOXPOOL.Set(0);
            // ミスト
            Header.LP_BEGIN_AFFECTEDAREAPOOL.Set(0);
            {
                Header.LP_AffectedAreaCreated.Set(0x012E);
                Header.LP_AffectedAreaRemoved.Set(0x012F);
            }
            Header.LP_END_AFFECTEDAREAPOOL.Set(0);
            // ミスティックドア
            Header.LP_END_TOWNPORTALPOOL.Set(0);
            {
                Header.LP_TownPortalCreated.Set(0x0130);
                Header.LP_TownPortalRemoved.Set(0x0131);
            }
            Header.LP_END_TOWNPORTALPOOL.Set(0);
            // 0x0132
            // 0x0133
            // 0x0134 crash
            // 0x0135 ポータルを開けませんでした。
            // 0x0136
            // 設置物
            Header.LP_BEGIN_REACTORPOOL.Set(0);
            {
                Header.LP_ReactorChangeState.Set(0x0137);
                Header.LP_ReactorMove.Set(0x0138);
                Header.LP_ReactorEnterField.Set(0x0139);
                Header.LP_ReactorLeaveField.Set(0x013A);
            }
            Header.LP_END_REACTORPOOL.Set(0);
            // イベント
            Header.LP_BEGIN_ETCFIELDOBJ.Set(0);
            {
                Header.LP_SnowBallState.Set(0x013B);
                Header.LP_SnowBallHit.Set(0x013C);
                Header.LP_SnowBallMsg.Set(0x013D);
                Header.LP_SnowBallTouch.Set(0x013E);
                Header.LP_CoconutHit.Set(0x013F);
                Header.LP_CoconutScore.Set(0x0140);
                Header.LP_HealerMove.Set(0x0141);
                Header.LP_PulleyStateChange.Set(0x0142);
                // ずれてるかも
                Header.LP_MCarnivalEnter.Set(0x0143);
                Header.LP_MCarnivalPersonalCP.Set(0x0144);
                Header.LP_MCarnivalTeamCP.Set(0x0145);
                Header.LP_MCarnivalResultSuccess.Set(0x0146);
                Header.LP_MCarnivalResultFail.Set(0x0147);
                Header.LP_MCarnivalDeath.Set(0x0148);
                Header.LP_MCarnivalMemberOut.Set(0x0149);
                Header.LP_MCarnivalGameResult.Set(0x014A);
                // 0x014B 未使用
                // 0x014C 未使用
                // 0x014D 未使用
                // 0x014E 未使用
                // 0x014F @014F byte種類,int残り時間, マップ退場メッセージ
                // 0x0150 未使用
                Header.LP_HontaleTimer.Set(0x0151); // OK
                Header.LP_ChaosZakumTimer.Set(0x0152); // OK
                Header.LP_HontailTimer.Set(0x0153);
                Header.LP_ZakumTimer.Set(0x0154); // OK
            }
            Header.LP_END_ETCFIELDOBJ.Set(0);
            // NPC会話
            Header.LP_BEGIN_SCRIPT.Set(0);
            {
                Header.LP_ScriptMessage.Set(0x0155);
            }
            Header.LP_END_SCRIPT.Set(0);
            // 商店
            Header.LP_BEGIN_SHOP.Set(0);
            {
                Header.LP_OpenShopDlg.Set(0x0156);
                Header.LP_ShopResult.Set(0x0157);
            }
            Header.LP_END_SHOP.Set(0);
            // ???
            Header.LP_BEGIN_ADMINSHOP.Set(0);
            {
                // 0x0158
                // 0x0159
            }
            Header.LP_END_ADMINSHOP.Set(0);

            Header.LP_TrunkResult.Set(0x015A); // 倉庫
            // プレドリック
            Header.LP_BEGIN_STOREBANK.Set(0);
            {
                Header.LP_StoreBankGetAllResult.Set(0x015B);
                Header.LP_StoreBankResult.Set(0x015C);
            }
            Header.LP_END_STOREBANK.Set(0);

            Header.LP_RPSGame.Set(0x015D);
            Header.LP_Messenger.Set(0x015E);
            Header.LP_MiniRoom.Set(0x015F);
            // 0x0160 未使用
            // 0x0161 未使用
            // 0x0162 未使用
            // 0x0163 未使用
            // 0x0164 未使用
            // 0x0165 未使用
            // 0x0166 未使用
            // パチンコ
            {
                Header.TIP_BEANS.Set(0x0167);
                Header.OPEN_BEANS.Set(0x0168);
                Header.SHOOT_BEANS.Set(0x0169);
                // 0x016A
                Header.UPDATE_BEANS.Set(0x016B);
            }
            Header.LP_Parcel.Set(0x016C); // 宅配
        }
        Header.LP_END_FIELD.Set(0);
        // ポイントショップ
        Header.LP_BEGIN_CASHSHOP.Set(0);
        {
            // 0x016D
            // 0x016E
            Header.CS_UPDATE.Set(0x016F);
            Header.CS_OPERATION.Set(0x0170);
            // 0x0171
            // 0x0172
            // 0x0173
            // 0x0174
            // 0x0175
            // 0x0176
            // 0x0177
            // 0x0178
            // 0x0179
            // 0x017A
            // 0x017B
        }
        Header.LP_END_CASHSHOP.Set(0);
        // キー設定
        Header.LP_BEGIN_FUNCKEYMAPPED.Set(0);
        {
            Header.LP_FuncKeyMappedInit.Set(0x017C);
            Header.LP_PetConsumeItemInit.Set(0x017D);
            Header.LP_PetConsumeMPItemInit.Set(0x017E);
        }
        Header.LP_END_FUNCKEYMAPPED.Set(0);
        // 0x017F
        // 0x0180 未使用
        // 0x0181 未使用
        // 0x0182 未使用
        // 0x0183 未使用
        // 0x0184 未使用
        // 0x0185 未使用
        // 0x0186 未使用
        // 0x0187 未使用
        //Header.GET_MTS_TOKENS.Set(0x0188);
        //Header.MTS_OPERATION.Set(0x0189);
        // 0x018A 未使用
        // 0x018B 未使用
        Header.LP_BEGIN_MAPLETV.Set(0); // 0x018C 未使用のはず
        {
            // 0x018D, LP_MapleTVUpdateMessage
            // 0x018E, LP_MapleTVClearMessage
            // 0x018F, LP_MapleTVSendMessageResult @018F [01] [01-03], /MapleTV コマンドのエラーメッセージ処理 (GMコマンドなので通常プレイでは不要)
            // 0x0190, LP_BroadSetFlashChangeEvent 未使用 (何もしない関数)
        }
        Header.LP_END_MAPLETV.Set(0); // 0x191 未使用のはず
        // ビシャスのハンマー
        Header.LP_BEGIN_GOLDHAMMER.Set(0);
        {
            // 0x0191 未使用
            Header.LP_GoldHammerResult.Set(0x0192);
            // 0x0193 未使用
            // 0x0194 未使用
        }
        Header.LP_END_GOLDHAMMER.Set(0);
        // ベガの呪文書
        Header.LP_BEGIN_VEGA.Set(0);
        {
            // 0x0195 未使用
            Header.LP_VegaResult.Set(0x0196);
            // 0x0197 未使用
            // 0x0198 未使用
        }
        Header.LP_END_VEGA.Set(0);
        // 0x0199 一番最後の関数 0x00D76700が0以外の値のときのみ動作する
        Header.LP_LogoutGift.Set(0x0199); // 他に該当する処理がないので多分ログアウトギフト
        // 末尾
        Header.LP_NO.Set(0x19A);
    }

    public static void SetForJMSv187() {
        // ログインサーバー
        Header.LP_CheckPasswordResult.Set(0x0000);
        Header.LP_WorldInformation.Set(0x0002);
        Header.LP_SelectWorldResult.Set(0x0003);
        Header.LP_SelectCharacterResult.Set(0x0004);
        Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        Header.LP_CreateNewCharacterResult.Set(0x0006);
        Header.LP_DeleteCharacterResult.Set(0x0007);
        Header.LP_MigrateCommand.Set(0x0008);
        Header.LP_AliveReq.Set(0x0009);
        Header.LP_CheckPinCodeResult.Set(0x0016);
        Header.LOGIN_AUTH.Set(0x0018);
        // ゲームサーバー
        Header.LP_InventoryOperation.Set(0x001B);
        Header.LP_InventoryGrow.Set(0x001C);
        //Header.UPDATE_STATS.Set(0x001D);
        Header.LP_TemporaryStatSet.Set(0x001E);
        Header.LP_TemporaryStatReset.Set(0x001F);
        Header.LP_ForcedStatSet.Set(0x0020);
        Header.LP_ForcedStatReset.Set(0x0021);
        Header.LP_ChangeSkillRecordResult.Set(0x0022);
        Header.LP_GivePopularityResult.Set(0x0024);
        Header.LP_Message.Set(0x0025);
        Header.LP_MemoResult.Set(0x0026);
        Header.LP_MapTransferResult.Set(0x0027);
        Header.LP_SetTamingMobInfo.Set(0x002D); // OK
        Header.LP_QuestClear.Set(0x002E);
        Header.LP_EntrustedShopCheckResult.Set(0x002F);
        Header.LP_SkillLearnItemResult.Set(0x0030);
        // 釣り改変?
        //Header.FINISH_SORT.Set(0x0031);
        //Header.FINISH_GATHER.Set(0x0032);
        // 0x0033
        Header.LP_CharacterInfo.Set(0x0035 + 1);
        Header.LP_PartyResult.Set(0x0036 + 1);
        Header.LP_ExpeditionNoti.Set(0x0038 + 1);
        Header.LP_FriendResult.Set(0x0039 + 1);
        Header.LP_GuildResult.Set(0x003B + 1);
        Header.LP_AllianceResult.Set(0x003C + 1);
        Header.LP_TownPortal.Set(0x003D + 1);
        Header.LP_BroadcastMsg.Set(0x003F + 1);
        Header.LP_IncubatorResult.Set(0x0040 + 1);
        Header.LP_ShopScannerResult.Set(0x0041 + 1);
        Header.LP_MarriageRequest.Set(0x0043 + 1);
        Header.LP_MarriageResult.Set(0x0044 + 1);
        Header.LP_SetWeekEventMessage.Set(0x0048 + 1);  // 基準点
        Header.MINIGAME_PACHINKO_UPDATE_TAMA.Set(0x4C + 1); // 基準点
        Header.FISHING_BOARD_UPDATE.Set(0x0051 + 1);
        Header.PLAYER_NPC.Set(0x0056 + 1); // NPC
        Header.LP_MonsterBookSetCard.Set(0x0057 + 1); // NPC
        Header.LP_MonsterBookSetCover.Set(0x0058 + 1);
        Header.LP_AvatarMegaphoneRes.Set(0x005B + 1);
        Header.UNKNOWN_RELOAD_MINIMAP.Set(0x005E + 1);
        Header.ENERGY.Set(0x0062 + 1);
        Header.GHOST_POINT.Set(0x0063 + 1);
        Header.GHOST_STATUS.Set(0x0064 + 1);
        Header.FAIRY_PEND_MSG.Set(0x0065 + 1);
        Header.LP_FamilyChartResult.Set(0x0066 + 1);
        Header.LP_FamilyInfoResult.Set(0x0067 + 1);
        Header.LP_FamilyResult.Set(0x0068 + 1);
        Header.LP_FamilyJoinRequest.Set(0x0069 + 1);
        Header.LP_FamilyJoinRequestResult.Set(0x006A + 1);
        Header.LP_FamilyJoinAccepted.Set(0x006B + 1);
        Header.LP_FamilyPrivilegeList.Set(0x006C + 1);
        Header.LP_FamilyFamousPointIncResult.Set(0x006D + 1);
        Header.LP_FamilyNotifyLoginOrLogout.Set(0x006E + 1);
        Header.LP_FamilySetPrivilege.Set(0x006F + 1);
        Header.LP_FamilySummonRequest.Set(0x0070 + 1);
        Header.LP_NotifyLevelUp.Set(0x0071 + 1);
        Header.LP_NotifyWedding.Set(0x0072 + 1);
        Header.LP_NotifyJobChange.Set(0x0073 + 1);
        Header.LP_SetPassenserRequest.Set(0x0075 + 1);
        Header.LP_ScriptProgressMessage.Set(0x0077 + 1);
        // 0x007A - 0x007Fなんか増えた
        // サーバー切り替え系
        Header.LP_SetField.Set(0x007E + 3);
        Header.LP_SetITC.Set(0x007F + 3);
        Header.LP_SetCashShop.Set(0x0080 + 3);
        // Recv2 0084-00A0 -> 0087-00A3
        Header.LP_TransferChannelReqIgnored.Set(0x0085 + 3);
        Header.LP_FieldSpecificData.Set(0x0086 + 3);
        Header.LP_GroupMessage.Set(0x0087 + 3);
        Header.LP_Whisper.Set(0x0088 + 3);
        Header.LP_MobSummonItemUseResult.Set(0x0089 + 3);
        Header.LP_FieldEffect.Set(0x008A + 3);
        Header.LP_FieldObstacleOnOff.Set(0x008B + 3);
        Header.LP_FieldObstacleOnOffStatus.Set(0x008C + 3);
        Header.LP_BlowWeather.Set(0x008E + 3);
        Header.LP_PlayJukeBox.Set(0x008F + 3);
        Header.LP_AdminResult.Set(0x0090 + 3);
        Header.LP_Quiz.Set(0x0091 + 3);
        Header.LP_Desc.Set(0x0092 + 3);
        Header.LP_DestroyClock.Set(0x0099 + 3);
        Header.LP_FootHoldInfo.Set(0x009F + 3);
        // GameServer Player 0xA1 -> 0xA5
        Header.LP_UserEnterField.Set(0x00A1 + 4);
        Header.LP_UserLeaveField.Set(0x00A2 + 4);
        Header.LP_UserChat.Set(0x00A3 + 4);
        Header.LP_UserChatNLCPQ.Set(0x00A4 + 4);
        Header.LP_UserADBoard.Set(0x00A5 + 4);
        Header.LP_UserMiniRoomBalloon.Set(0x00A6 + 4);
        Header.LP_UserItemUpgradeEffect.Set(0x00A8 + 4);
        Header.LP_UserItemReleaseEffect.Set(0x00AB + 4);
        Header.LP_UserItemUnreleaseEffect.Set(0x00AC + 4);
        Header.LP_UserFollowCharacter.Set(0x00B1 + 4);
        Header.FISHING_CAUGHT.Set(0x00B2 + 4);
        Header.LP_ShowPamsSongResult.Set(0x00B3 + 4);

        Header.LP_PetActivated.Set(0x00B4 + 5);
        Header.LP_PetMove.Set(0x00B7 + 5);
        Header.LP_PetAction.Set(0x00B8 + 5);
        Header.LP_PetNameChanged.Set(0x00B9 + 5);
        Header.LP_PetActionCommand.Set(0x00BB + 5);

        Header.LP_SummonedSkill.Set(0x00C0 + 5);
        Header.LP_SummonedHit.Set(0x00C1 + 5);
        Header.LP_DragonEnterField.Set(0x00C2 + 5);
        Header.LP_DragonMove.Set(0x00C3 + 5);
        Header.LP_DragonLeaveField.Set(0x00C4 + 5);

        Header.LP_UserMove.Set(0x00C6 + 5);
        Header.LP_UserMeleeAttack.Set(0x00C7 + 5);
        Header.LP_UserShootAttack.Set(0x00C8 + 5);
        Header.LP_UserMagicAttack.Set(0x00C9 + 5);
        Header.LP_UserBodyAttack.Set(0x00CA + 5);
        Header.LP_UserSkillPrepare.Set(0x00CB + 5);
        Header.LP_UserSkillCancel.Set(0x00CC + 5);
        Header.LP_UserHit.Set(0x00CD + 5);
        Header.LP_UserEmotion.Set(0x00CE + 5);
        // added 0x00D4
        Header.LP_UserSetActiveEffectItem.Set(0x00CF + 6);
        Header.LP_UserAvatarModified.Set(0x00D2 + 6);
        Header.LP_UserEffectRemote.Set(0x00D3);
        Header.LP_UserTemporaryStatSet.Set(0x00D4 + 6);
        Header.LP_UserTemporaryStatReset.Set(0x00D5 + 6);
        Header.LP_UserHP.Set(0x00D6 + 6);
        Header.LP_UserGuildNameChanged.Set(0x00D7 + 6);
        Header.LP_UserGuildMarkChanged.Set(0x00D8 + 6);
        /*
        Header.CANCEL_CHAIR.Set(0x00DA);
        Header.SHOW_ITEM_GAIN_INCHAT.Set(0x00DC);
        Header.CURRENT_MAP_WARP.Set(0x00DD);
        Header.MESOBAG_SUCCESS.Set(0x00DF);
        Header.MESOBAG_FAILURE.Set(0x00E0);
        Header.RANDOM_MESOBAG_SUCCESS.Set(0x00E1);
        Header.RANDOM_MESOBAG_FAILURE.Set(0x00E2);
        Header.UPDATE_QUEST_INFO.Set(0x00E3);
        Header.PLAYER_HINT.Set(0x00E8);
        Header.REPAIR_WINDOW.Set(0x00ED);
        Header.CYGNUS_INTRO_LOCK.Set(0x00EE);
        Header.CYGNUS_INTRO_DISABLE_UI.Set(0x00EF);
        Header.SUMMON_HINT.Set(0x00F0);
        Header.SUMMON_HINT_MSG.Set(0x00F1);
        Header.ARAN_COMBO.Set(0x00F2);
        Header.TAMA_BOX_SUCCESS.Set(0x00F3);
        Header.TAMA_BOX_FAILURE.Set(0x00F4);
        Header.GAME_POLL_REPLY.Set(0x00FA);
        Header.FOLLOW_MESSAGE.Set(0x00FB);
        Header.FOLLOW_MOVE.Set(0x00FF);
        Header.FOLLOW_MSG.Set(0x0100);
        Header.GAME_POLL_QUESTION.Set(0x0101);
        Header.COOLDOWN.Set(0x0102);
        Header.SPAWN_MONSTER.Set(0x0104);
        Header.KILL_MONSTER.Set(0x0105);
        Header.SPAWN_MONSTER_CONTROL.Set(0x0106);
        Header.MOVE_MONSTER.Set(0x0107);
        Header.MOVE_MONSTER_RESPONSE.Set(0x0108);
        Header.APPLY_MONSTER_STATUS.Set(0x010A);
        Header.CANCEL_MONSTER_STATUS.Set(0x010B);
        Header.MOB_TO_MOB_DAMAGE.Set(0x010D);
        Header.DAMAGE_MONSTER.Set(0x010E);
        Header.SHOW_MONSTER_HP.Set(0x0112);
        Header.SHOW_MAGNET.Set(0x0113);
        Header.CATCH_MONSTER.Set(0x0114);
        Header.MOB_SPEAKING.Set(0x0115);
        Header.MONSTER_PROPERTIES.Set(0x0117);
        Header.REMOVE_TALK_MONSTER.Set(0x0118);
        Header.TALK_MONSTER.Set(0x0119);
         */
        Header.LP_NpcEnterField.Set(0x011E + 9);
        Header.LP_NpcLeaveField.Set(0x011F + 9);
        Header.LP_NpcChangeController.Set(0x0120 + 9);
        Header.LP_NpcMove.Set(0x0121 + 9);
        /*
        Header.SPAWN_HIRED_MERCHANT.Set(0x0126);
        Header.DESTROY_HIRED_MERCHANT.Set(0x0127);
        Header.UPDATE_HIRED_MERCHANT.Set(0x0128);
        Header.DROP_ITEM_FROM_MAPOBJECT.Set(0x0129);
        Header.REMOVE_ITEM_FROM_MAP.Set(0x012A);
        Header.KITE_MESSAGE.Set(0x012B);
        Header.SPAWN_KITE.Set(0x012C);
        Header.REMOVE_KITE.Set(0x012D);
        Header.SPAWN_MIST.Set(0x012E);
        Header.REMOVE_MIST.Set(0x012F);
        Header.SPAWN_DOOR.Set(0x0130);
        Header.REMOVE_DOOR.Set(0x0131);
        Header.REACTOR_HIT.Set(0x0137);
        Header.REACTOR_SPAWN.Set(0x0139);
        Header.REACTOR_DESTROY.Set(0x013A);
        Header.CHAOS_HORNTAIL_SHRINE.Set(0x0151);
        Header.CHAOS_ZAKUM_SHRINE.Set(0x0152);
        Header.ZAKUM_SHRINE.Set(0x0154);
         */
        Header.LP_ScriptMessage.Set(0x0155 + 8);
        Header.LP_OpenShopDlg.Set(0x0156 + 8);
        Header.LP_ShopResult.Set(0x0157 + 8);
        Header.LP_TrunkResult.Set(0x015A + 8);
        /*
        Header.MERCH_ITEM_MSG.Set(0x015B);
        Header.MERCH_ITEM_STORE.Set(0x015C);
        Header.RPS_GAME.Set(0x015D);
        Header.MESSENGER.Set(0x015E);
        Header.PLAYER_INTERACTION.Set(0x015F);
        Header.TIP_BEANS.Set(0x0167);
        Header.OPEN_BEANS.Set(0x0168);
        Header.SHOOT_BEANS.Set(0x0169);
        Header.UPDATE_BEANS.Set(0x016B);
        Header.DUEY.Set(0x016C);
        Header.CS_UPDATE.Set(0x016F);
        Header.CS_OPERATION.Set(0x0170);
        Header.KEYMAP.Set(0x017C);
        Header.PET_AUTO_HP.Set(0x017D);
        Header.PET_AUTO_MP.Set(0x017E);
        Header.VICIOUS_HAMMER.Set(0x0192);
        Header.VEGA_SCROLL.Set(0x0196);
         */
    }

    public static void SetForJMSv302() {
        // ログインサーバー
        Header.LP_CheckPasswordResult.Set(0x0000);
        Header.LP_WorldInformation.Set(0x0002);
        Header.LP_SelectWorldResult.Set(0x0003);
        Header.LP_SelectCharacterResult.Set(0x0004);
        Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        Header.LP_CreateNewCharacterResult.Set(0x0006);
        Header.LP_DeleteCharacterResult.Set(0x0007);
        Header.LP_MigrateCommand.Set(0x0008);
        Header.LP_AliveReq.Set(0x0009);
        Header.LP_CheckPinCodeResult.Set(0x0016);
        Header.LOGIN_AUTH.Set(0x0018);
    }

}
