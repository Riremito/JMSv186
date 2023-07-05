// サーバー側から送信されるパケットのヘッダの定義
package packet;

import config.ServerConfig;
import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import java.util.ArrayList;

public class ServerPacket {

    // Encoder
    private ArrayList<Byte> packet = new ArrayList<>();
    private int encoded = 0;

    public ServerPacket(Header header) {
        short w = (short) header.Get();

        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public ServerPacket(short w) {
        packet.add((byte) (w & 0xFF));
        packet.add((byte) ((w >> 8) & 0xFF));
        encoded += 2;
    }

    public boolean SetHello() {
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
            if (h.Get() == w) {
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

    public String GetOpcodeName() {
        if (encoded < 2) {
            return Header.UNKNOWN.toString();
        }

        short header = (short) (((short) packet.get(0) & 0xFF) | ((short) packet.get(1) & 0xFF << 8));
        return ToHeader(header).toString();
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
        // 独自仕様
        LP_CUSTOM_,
        LP_CUSTOM_WZ_HASH,
        LP_CUSTOM_CLIENT_PATCH,
        LP_CUSTOM_MEMORY_SCAN,
        // JMS v131.0
        LP_T_UpdateGameGuard,
        // Names from v95 PDB
        // ログインサーバー
        LP_BEGIN_SOCKET,
        LP_CheckPasswordResult,
        LP_GuestIDLoginResult,
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
        LP_AuthenCodeChanged,
        LP_AuthenMessage,
        LP_SecurityPacket,
        //LP_EnableSPWResult,
        //LP_DeleteCharacterOTPRequest,
        //LP_CheckCrcResult,
        LP_LatestConnectedWorld,
        LP_RecommendWorldMessage,
        //LP_CheckExtraCharInfoResult,
        //LP_CheckSPWResult,
        LP_END_SOCKET,
        // ゲームサーバー
        LP_BEGIN_CHARACTERDATA,
        LP_InventoryOperation, // MODIFY_INVENTORY_ITEM
        LP_InventoryGrow, // UPDATE_INVENTORY_SLOT
        LP_StatChanged, // UPDATE_STATS
        LP_TemporaryStatSet, // GIVE_BUFF
        LP_TemporaryStatReset, // CANCEL_BUFF
        LP_ForcedStatSet,
        LP_ForcedStatReset,
        LP_ChangeSkillRecordResult, // UPDATE_SKILLS
        LP_SkillUseResult,
        LP_GivePopularityResult, // FAME_RESPONSE
        LP_Message, // SHOW_STATUS_INFO
        LP_SendOpenFullClientLink,
        LP_MemoResult, // SHOW_NOTES
        LP_MapTransferResult, // TROCK_LOCATIONS
        LP_AntiMacroResult,
        LP_InitialQuizStart, // LIE_DETECTOR
        LP_ClaimResult,
        LP_SetClaimSvrAvailableTime,
        LP_ClaimSvrStatusChanged,
        LP_SetTamingMobInfo,
        LP_QuestClear, // SHOW_QUEST_COMPLETION
        LP_EntrustedShopCheckResult,
        LP_SkillLearnItemResult, // USE_SKILL_BOOK
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
        LP_CharacterInfo, // CHAR_INFO
        LP_PartyResult, // PARTY_OPERATION
        LP_ExpeditionRequest,
        LP_ExpeditionNoti,
        LP_FriendResult, // BUDDYLIST
        LP_GuildRequest,
        LP_GuildResult, // GUILD_OPERATION
        LP_AllianceResult,
        LP_TownPortal, // SPAWN_PORTAL
        LP_OpenGate,
        LP_BroadcastMsg, // SERVERMESSAGE
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
        LP_TransferChannelReqIgnored, // SERVER_BLOCKED
        LP_FieldSpecificData, // SHOW_EQUIP_EFFECT
        LP_GroupMessage, // MULTICHAT
        LP_Whisper, // WHISPER
        LP_CoupleMessage,
        LP_MobSummonItemUseResult,
        LP_FieldEffect, // BOSS_ENV
        LP_FieldObstacleOnOff,
        LP_FieldObstacleOnOffStatus,
        LP_FieldObstacleAllReset,
        LP_BlowWeather, // MAP_EFFECT
        LP_PlayJukeBox, // CASH_SONG
        LP_AdminResult, // GM_EFFECT
        LP_Quiz, // OX_QUIZ
        LP_Desc, // GMEVENT_INSTRUCTIONS
        LP_Clock, // CLOCK
        LP_CONTIMOVE, // BOAT_EFF
        LP_CONTISTATE, // BOAT_EFFECT
        LP_SetQuestClear,
        LP_SetQuestTime,
        LP_Warn,
        LP_SetObjectState,
        LP_DestroyClock, // STOP_CLOCK
        LP_ShowArenaResult,
        LP_StalkResult,
        LP_MassacreIncGauge,
        LP_MassacreResult,
        LP_QuickslotMappedInit,
        LP_FootHoldInfo,
        LP_RequestFootHoldInfo,
        LP_FieldKillCount,
        LP_BEGIN_USERPOOL,
        LP_UserEnterField, // SPAWN_PLAYER
        LP_UserLeaveField, // REMOVE_PLAYER_FROM_MAP
        LP_BEGIN_USERCOMMON,
        LP_UserChat, // CHATTEXT
        LP_UserChatNLCPQ,
        LP_UserADBoard,
        LP_UserMiniRoomBalloon,
        LP_UserConsumeItemEffect,
        LP_UserItemUpgradeEffect, // SHOW_SCROLL_EFFECT
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
        LP_PetActivated, // SPAWN_PET
        LP_PetEvol,
        LP_PetTransferField,
        LP_PetMove, // MOVE_PET
        LP_PetAction, // PET_CHAT
        LP_PetNameChanged, // PET_NAMECHANGE
        LP_PetLoadExceptionList, // PET_COMMAND
        LP_END_PET,
        LP_PetActionCommand,
        LP_BEGIN_DRAGON,
        LP_DragonEnterField,
        LP_DragonMove,
        LP_DragonLeaveField,
        LP_END_DRAGON,
        LP_END_USERCOMMON,
        LP_BEGIN_USERREMOTE,
        LP_UserMove, // MOVE_PLAYER
        LP_UserMeleeAttack, // CLOSE_RANGE_ATTACK
        LP_UserShootAttack, // RANGED_ATTACK 
        LP_UserMagicAttack, // MAGIC_ATTACK
        LP_UserBodyAttack,
        LP_UserSkillPrepare, // SKILL_EFFECT
        LP_UserMovingShootAttackPrepare,
        LP_UserSkillCancel, // CANCEL_SKILL_EFFECT
        LP_UserHit, // DAMAGE_PLAYER
        LP_UserEmotion, // FACIAL_EXPRESSION
        LP_UserSetActiveEffectItem, // SHOW_ITEM_EFFECT
        LP_UserShowUpgradeTombEffect,
        LP_UserSetActivePortableChair, // SHOW_CHAIR
        LP_UserAvatarModified, // UPDATE_CHAR_LOOK
        LP_UserEffectRemote, // SHOW_FOREIGN_EFFECT
        LP_UserTemporaryStatSet, // GIVE_FOREIGN_BUFF
        LP_UserTemporaryStatReset, // CANCEL_FOREIGN_BUFF
        LP_UserHP, // UPDATE_PARTYMEMBER_HP
        LP_UserGuildNameChanged, // LOAD_GUILD_NAME
        LP_UserGuildMarkChanged, // LOAD_GUILD_ICON
        LP_END_USERREMOTE,
        LP_UserThrowGrenade,
        LP_BEGIN_USERLOCAL,
        LP_UserSitResult, // CANCEL_CHAIR 
        LP_UserEmotionLocal,
        LP_UserEffectLocal, // SHOW_ITEM_GAIN_INCHAT?
        LP_UserTeleport, // CURRENT_MAP_WARP
        LP_Premium,
        LP_MesoGive_Succeeded, // MESOBAG_SUCCESS
        LP_MesoGive_Failed, // MESOBAG_FAILURE
        LP_Random_Mesobag_Succeed,
        LP_Random_Mesobag_Failed,
        LP_FieldFadeInOut,
        LP_FieldFadeOutForce,
        LP_UserQuestResult, // UPDATE_QUEST_INFO
        LP_NotifyHPDecByField,
        LP_UserPetSkillChanged, // PET_FLAG_CHANGE
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
        LP_SummonedEnterField, // SPAWN_SUMMON
        LP_SummonedLeaveField, // REMOVE_SUMMON
        LP_SummonedMove, // MOVE_SUMMON
        LP_SummonedAttack, // SUMMON_ATTACK
        LP_SummonedSkill, // SUMMON_SKILL
        LP_END_SUMMONED,
        LP_SummonedHit, // DAMAGE_SUMMON
        LP_BEGIN_MOBPOOL,
        LP_MobEnterField, // SPAWN_MONSTER
        LP_MobLeaveField, // KILL_MONSTER
        LP_MobChangeController, // SPAWN_MONSTER_CONTROL
        LP_BEGIN_MOB,
        LP_MobMove, // MOVE_MONSTER
        LP_MobCtrlAck, // MOVE_MONSTER_RESPONSE 
        LP_MobCtrlHint,
        LP_MobStatSet, // APPLY_MONSTER_STATUS
        LP_MobStatReset, // CANCEL_MONSTER_STATUS
        LP_MobSuspendReset,
        LP_MobAffected, // MOB_TO_MOB_DAMAGE
        LP_MobDamaged, // DAMAGE_MONSTER
        LP_MobSpecialEffectBySkill,
        LP_MobHPChange,
        LP_MobCrcKeyChanged,
        LP_MobHPIndicator,
        LP_MobCatchEffect, // CATCH_MONSTER
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
        LP_END_MOB,
        LP_MobAttackedByMob,
        LP_END_MOBPOOL,
        LP_BEGIN_NPCPOOL,
        LP_NpcEnterField, // SPAWN_NPC
        LP_NpcLeaveField, // REMOVE_NPC
        LP_NpcChangeController, // SPAWN_NPC_REQUEST_CONTROLLER
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
        LP_EmployeeEnterField, // SPAWN_HIRED_MERCHANT
        LP_EmployeeLeaveField, // DESTROY_HIRED_MERCHANT
        LP_END_EMPLOYEEPOOL,
        LP_EmployeeMiniRoomBalloon, // UPDATE_HIRED_MERCHANT
        LP_BEGIN_DROPPOOL,
        LP_DropEnterField, // DROP_ITEM_FROM_MAPOBJECT
        LP_DropReleaseAllFreeze,
        LP_DropLeaveField, // REMOVE_ITEM_FROM_MAP
        LP_END_DROPPOOL,
        LP_BEGIN_MESSAGEBOXPOOL,
        LP_CreateMessgaeBoxFailed, // SPAWN_KITE_ERROR
        LP_MessageBoxEnterField, // SPAWN_KITE
        LP_END_MESSAGEBOXPOOL,
        LP_MessageBoxLeaveField, // REMOVE_KITE
        LP_AffectedAreaCreated, // SPAWN_MIST
        LP_BEGIN_AFFECTEDAREAPOOL,
        LP_AffectedAreaRemoved, // REMOVE_MIST
        LP_END_AFFECTEDAREAPOOL,
        LP_BEGIN_TOWNPORTALPOOL,
        LP_TownPortalCreated, // SPAWN_DOOR
        LP_END_TOWNPORTALPOOL,
        LP_TownPortalRemoved, // REMOVE_DOOR
        LP_BEGIN_OPENGATEPOOL,
        LP_OpenGateCreated,
        LP_END_OPENGATEPOOL,
        LP_OpenGateRemoved,
        LP_BEGIN_REACTORPOOL,
        LP_ReactorChangeState, // REACTOR_HIT
        LP_ReactorMove,
        LP_ReactorEnterField, // REACTOR_SPAWN
        LP_END_REACTORPOOL,
        LP_ReactorLeaveField, // REACTOR_DESTROY
        LP_BEGIN_ETCFIELDOBJ,
        LP_SnowBallState, // ROLL_SNOWBALL
        LP_SnowBallHit, // HIT_SNOWBALL
        LP_SnowBallMsg, // SNOWBALL_MESSAGE
        LP_SnowBallTouch, // LEFT_KNOCK_BACK
        LP_CoconutHit, // HIT_COCONUT
        LP_CoconutScore, // COCONUT_SCORE
        LP_HealerMove,
        LP_PulleyStateChange,
        LP_MCarnivalEnter, // MONSTER_CARNIVAL_START
        LP_MCarnivalPersonalCP, // MONSTER_CARNIVAL_OBTAINED_CP
        LP_MCarnivalTeamCP, // MONSTER_CARNIVAL_PARTY_CP
        LP_MCarnivalResultSuccess, // MONSTER_CARNIVAL_SUMMON?
        LP_MCarnivalResultFail, // MONSTER_CARNIVAL_OPERATION?
        LP_MCarnivalDeath, // MONSTER_CARNIVAL_DIED
        LP_MCarnivalMemberOut, // MONSTER_CARNIVAL_DISBAND
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
        LP_ScriptMessage, // NPC_TALK
        LP_BEGIN_SHOP,
        LP_OpenShopDlg, // OPEN_NPC_SHOP
        LP_END_SHOP, // CONFIRM_SHOP_TRANSACTION
        LP_ShopResult,
        LP_AdminShopResult,
        LP_BEGIN_ADMINSHOP,
        LP_AdminShopCommodity,
        LP_END_ADMINSHOP,
        LP_TrunkResult, // OPEN_STORAGE
        LP_BEGIN_STOREBANK,
        LP_StoreBankGetAllResult, // MERCH_ITEM_MSG
        LP_END_STOREBANK,
        LP_StoreBankResult, // MERCH_ITEM_STORE
        LP_RPSGame, // RPS_GAME
        LP_Messenger, // MESSENGER
        LP_MiniRoom, // PLAYER_INTERACTION
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
        LP_CashShopChargeParamResult, // CS_UPDATE
        LP_CashShopQueryCashResult, // CS_OPERATION
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
        LP_FuncKeyMappedInit, // KEYMAP
        LP_PetConsumeItemInit, // PET_AUTO_HEAL
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
        UNKNOWN,
        // added
        UNK_BEGIN_PACHINKO,
        UNK_END_PACHINKO,
        MINIGAME_PACHINKO_UPDATE_TAMA, // UPDATE_PACHINKO_BALL
        UNKNOWN_RELOAD_MINIMAP,
        UNKNOWN_RELOAD_MAP,
        HELLO(0x000E),
        // unknown
        RELOG_RESPONSE,
        ARIANT_PQ_START,
        ARIANT_SCOREBOARD,
        SERVERSTATUS,
        XMAS_SURPRISE,
        EARN_TITLE_MSG,
        LOGIN_AUTH, // 名称不明
        FISHING_BOARD_UPDATE,
        ENERGY,
        GHOST_POINT,
        GHOST_STATUS,
        FAIRY_PEND_MSG,
        PYRAMID_UPDATE,
        PYRAMID_RESULT,
        FISHING_CAUGHT,
        PLAYER_HINT,
        TAMA_BOX_SUCCESS, // PACHINKO_PAY_SUCCESS
        TAMA_BOX_FAILURE, // PACHINKO_PAY_FAILURE
        GAME_POLL_REPLY,
        TALK_MONSTER,
        GAME_POLL_QUESTION,
        MONSTER_PROPERTIES,
        REMOVE_TALK_MONSTER,
        SHOW_MAGNET,
        CS_UPDATE,
        CS_OPERATION,
        UPDATE_BEANS,
        TIP_BEANS, // PACHINKO_TIPS
        OPEN_BEANS, // PACHINKO_OPEN
        SHOOT_BEANS, // PACHINKO_GAME
        UNKNOWN_END;

        private int value;

        Header(int header) {
            value = header;
        }

        Header() {
            value = 0xFFFF;
        }

        public boolean Set(int header) {
            value = header;
            return true;
        }

        public int Get() {
            return value;
        }
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

        // CStage::OnPacket
        // 8b 44 24 04 83 e8 ?? 74 22 48
        Header.LP_BEGIN_STAGE.Set(0);
        {
            Header.LP_SetField.Set(0x0076);
            Header.LP_SetITC.Set(Header.LP_SetField.Get() + 0x01);
            Header.LP_SetCashShop.Set(Header.LP_SetField.Get() + 0x02);
        }
        Header.LP_END_STAGE.Set(0);

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

    public static void SetCustomHeader() {
        // WZファイルのハッシュ値の確認
        Header.LP_CUSTOM_WZ_HASH.Set(0x77AA);
        // CLIENTのパッチ
        Header.LP_CUSTOM_CLIENT_PATCH.Set(0x77BB);
        // メモリスキャン
        Header.LP_CUSTOM_MEMORY_SCAN.Set(0x77CC);
    }

    // v187はクライアントが壊れているので開発する価値なし
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
        Header.LP_ImitatedNPCData.Set(0x0056 + 1); // NPC
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

    public static void UpdateHeader() {
        // CLogin::OnPacket
        Header.LP_BEGIN_SOCKET.Set(0);
        {
            // Header.LP_CheckPasswordResult.Set(0x0000);
            Header.LP_WorldInformation.Set(Header.LP_CheckPasswordResult.Get() + 2);
            Header.LP_SelectWorldResult.Set(Header.LP_CheckPasswordResult.Get() + 3);
            Header.LP_SelectCharacterResult.Set(Header.LP_CheckPasswordResult.Get() + 4);
            Header.LP_CheckDuplicatedIDResult.Set(Header.LP_CheckPasswordResult.Get() + 5);
            Header.LP_CreateNewCharacterResult.Set(Header.LP_CheckPasswordResult.Get() + 6);
            Header.LP_DeleteCharacterResult.Set(Header.LP_CheckPasswordResult.Get() + 7);
        }
        Header.LP_END_SOCKET.Set(0);

        // CStage::OnPacket
        Header.LP_BEGIN_STAGE.Set(0);
        {
            // Header.LP_SetField.Set(0x007E);
            Header.LP_SetITC.Set(Header.LP_SetField.Get() + 0x01);
            Header.LP_SetCashShop.Set(Header.LP_SetField.Get() + 0x02);
        }

        Header.LP_BEGIN_NPCPOOL.Set(0);
        {
            // Header.LP_NpcEnterField.Set(0x0125);
            Header.LP_NpcLeaveField.Set(Header.LP_NpcEnterField.Get() + 1);
            Header.LP_NpcChangeController.Set(Header.LP_NpcEnterField.Get() + 2);
            // CNpcPool::OnNpcPacket
            Header.LP_BEGIN_NPC.Set(0);
            {
                Header.LP_NpcMove.Set(Header.LP_NpcEnterField.Get() + 3);
                Header.LP_NpcUpdateLimitedInfo.Set(Header.LP_NpcEnterField.Get() + 4);
                Header.LP_NpcSpecialAction.Set(Header.LP_NpcEnterField.Get() + 5);
            }
            Header.LP_END_NPC.Set(0);
            // CNpcPool::OnNpcTemplatePacket
            Header.LP_BEGIN_NPCTEMPLATE.Set(0);
            {
                Header.LP_NpcSetScript.Set(Header.LP_NpcEnterField.Get() + 6);
            }
            Header.LP_END_NPCTEMPLATE.Set(0);
        }

        // CWvsContext::OnPacket
        Header.LP_BEGIN_CHARACTERDATA.Set(0);
        {
            // Header.LP_InventoryOperation.Set(0x0018);
            Header.LP_InventoryGrow.Set(Header.LP_InventoryOperation.Get() + 1);
            Header.LP_StatChanged.Set(Header.LP_InventoryOperation.Get() + 2);
            Header.LP_TemporaryStatSet.Set(Header.LP_InventoryOperation.Get() + 3);
            Header.LP_TemporaryStatReset.Set(Header.LP_InventoryOperation.Get() + 4);
            Header.LP_ForcedStatSet.Set(Header.LP_InventoryOperation.Get() + 5);
            Header.LP_ForcedStatReset.Set(Header.LP_InventoryOperation.Get() + 6);
            Header.LP_ChangeSkillRecordResult.Set(Header.LP_InventoryOperation.Get() + 7);
            Header.LP_SkillUseResult.Set(Header.LP_InventoryOperation.Get() + 8);
            Header.LP_GivePopularityResult.Set(Header.LP_InventoryOperation.Get() + 9);
            Header.LP_Message.Set(Header.LP_InventoryOperation.Get() + 10);
            Header.LP_MemoResult.Set(Header.LP_InventoryOperation.Get() + 11);
            Header.LP_MapTransferResult.Set(Header.LP_InventoryOperation.Get() + 12);
            Header.LP_AntiMacroResult.Set(Header.LP_InventoryOperation.Get() + 13);
            Header.LP_InitialQuizStart.Set(Header.LP_InventoryOperation.Get() + 14);
            Header.LP_ClaimResult.Set(Header.LP_InventoryOperation.Get() + 15);
            Header.LP_SetClaimSvrAvailableTime.Set(Header.LP_InventoryOperation.Get() + 16);
            Header.LP_ClaimSvrStatusChanged.Set(Header.LP_InventoryOperation.Get() + 17);
            Header.LP_SetTamingMobInfo.Set(Header.LP_InventoryOperation.Get() + 18);
            Header.LP_QuestClear.Set(Header.LP_InventoryOperation.Get() + 19);
            Header.LP_EntrustedShopCheckResult.Set(Header.LP_InventoryOperation.Get() + 20);
            Header.LP_SkillLearnItemResult.Set(Header.LP_InventoryOperation.Get() + 21);
            Header.LP_SortItemResult.Set(Header.LP_InventoryOperation.Get() + 22); //逆?
            Header.LP_GatherItemResult.Set(Header.LP_InventoryOperation.Get() + 23);
            // 0x0033 未使用
            // 0x0034 未使用
            //Header.LP_CharacterInfo.Set(Header.LP_InventoryOperation.Get() + 26);
            /*
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
            // 特に関数は別のテーブルとして独立していない
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

            // 一応ここにもあるが、NPCの方参照した方が確認が楽
            {
                //Header.LP_ImitatedNPCData.Set(0x0056);
                //Header.LP_LimitedNPCDisableInfo.Set(0x0057);
            }

            Header.LP_MonsterBookSetCard.Set(0x0058); // OK
            Header.LP_MonsterBookSetCover.Set(0x0059); // OK
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
            Header.LP_SuccessInUseGachaponBox.Set(0x0076);
            Header.LP_ScriptProgressMessage.Set(0x0077);
            Header.LP_DataCRCCheckFailed.Set(0x0078);
            Header.LP_AskUserWhetherUsePamsSong.Set(0x007C);
            Header.LP_MacroSysDataInit.Set(0x007D);
             */
        }
        Header.LP_END_CHARACTERDATA.Set(0);
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
