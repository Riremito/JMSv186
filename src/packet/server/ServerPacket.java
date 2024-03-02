// サーバー側から送信されるパケットのヘッダの定義
package packet.server;

import config.ServerConfig;
import handling.ByteArrayMaplePacket;
import handling.MaplePacket;
import java.util.ArrayList;
import java.util.Properties;

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
        LP_JMS_PINKBEAN_PORTAL_CREATE, // ItemID 2420004
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

    public static boolean Load(Properties props) {

        for (Header header : Header.values()) {
            String[] vars = props.getProperty(header.name(), "@FFFF").trim().split(" ");
            int base = -1;
            int offset = 0;
            switch (vars.length) {
                case 1: {
                    if ("@FFFF".length() <= vars[0].length() && vars[0].charAt(0) == '@') {
                        base = Integer.parseInt(vars[0].substring(1), 16);
                    } else {
                        base = Integer.parseInt(vars[0]);
                    }
                    break;
                }
                case 3: {
                    // + or -
                    if (vars[1].length() != 1 || (vars[1].charAt(0) != '+' && vars[1].charAt(0) != '-')) {
                        continue;
                    }
                    offset = Integer.parseInt(vars[2]);
                    if (vars[1].charAt(0) == '-') {
                        offset = -offset;
                    }
                    // get base value
                    if ("@FFFF".length() <= vars[0].length() && vars[0].charAt(0) == '@') {
                        base = Integer.parseInt(vars[0].substring(1), 16);
                    } else {
                        for (Header base_header : Header.values()) {
                            if (base_header.name().equals(vars[0])) {
                                base = base_header.Get();
                                break;
                            }
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            if (base == -1) {
                continue;
            }

            header.Set((short) (base + offset));
        }

        return true;
    }

}
