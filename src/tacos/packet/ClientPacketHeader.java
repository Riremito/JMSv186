/*
 * Copyright (C) 2025 Riremito
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
package tacos.packet;

/**
 *
 * @author Riremito
 */
public enum ClientPacketHeader implements IPacketHeader {
    // 独自仕様
    CP_CUSTOM_,
    CP_CUSTOM_WZ_HASH,
    CP_CUSTOM_MEMORY_SCAN,
    // v414.1 test
    //CP_TEST1(0x66),
    //CP_TEST2(0xA3),
    // Names from v95 PDB
    // ログインサーバー
    CP_BEGIN_SOCKET,
    CP_CheckPassword, // LOGIN_PASSWORD
    CP_Check2ndPassword, // v188 test
    CP_Check2ndPassword_cancel, // v188 test
    CP_GuestIDLogin,
    CP_AccountInfoRequest,
    CP_WorldInfoRequest, // SERVERLIST_REQUEST
    CP_SelectWorld, // CHARLIST_REQUEST
    CP_CheckUserLimit,
    CP_ConfirmEULA,
    CP_SetGender,
    CP_CheckPinCode,
    CP_UpdatePinCode,
    CP_WorldRequest,
    CP_LogoutWorld,
    CP_ViewAllChar,
    CP_SelectCharacterByVAC,
    CP_VACFlagSet,
    CP_CheckNameChangePossible,
    CP_RegisterNewCharacter,
    CP_CheckTransferWorldPossible,
    CP_SelectCharacter, // CHAR_SELECT
    CP_MigrateIn, // PLAYER_LOGGEDIN
    CP_CheckDuplicatedID, // CHECK_CHAR_NAME
    CP_CreateNewCharacter, // CREATE_CHAR
    CP_CreateNewCharacterInCS,
    CP_DeleteCharacter, // DELETE_CHAR
    CP_AliveAck,
    CP_ExceptionLog,
    CP_SecurityPacket,
    CP_JMS_CheckGameGuardUpdated, // JMS v131 (GG)
    CP_EnableSPWRequest,
    CP_CheckSPWRequest,
    CP_EnableSPWRequestByACV,
    CP_CheckSPWRequestByACV,
    CP_CheckOTPRequest,
    CP_CheckDeleteCharacterOTP,
    CP_CreateSecurityHandle,
    CP_SSOErrorLog,
    CP_ClientDumpLog,
    CP_CheckExtraCharInfo,
    CP_CreateNewCharacter_Ex,
    CP_JMS_MapLogin,
    CP_JMS_SafetyPassword,
    CP_JMS_GetMapLogin, // 名称不明, ログイン画面に到達
    CP_END_SOCKET,
    // ゲームサーバー
    CP_BEGIN_USER,
    CP_UserTransferFieldRequest, // CHANGE_MAP
    CP_UserTransferChannelRequest, // CHANGE_CHANNEL
    CP_UserMigrateToCashShopRequest, // ENTER_CASH_SHOP
    CP_UserMove, // MOVE_PLAYER
    CP_UserSitRequest, // CANCEL_CHAIR
    CP_UserPortableChairSitRequest, // USE_CHAIR
    CP_UserMeleeAttack, // CLOSE_RANGE_ATTACK
    CP_UserShootAttack, // RANGED_ATTACK
    CP_UserMagicAttack, // MAGIC_ATTACK
    CP_UserBodyAttack,
    CP_UserMovingShootAttackPrepare,
    CP_UserHit, // TAKE_DAMAGE
    CP_UserAttackUser,
    CP_UserChat, // GENERAL_CHAT
    CP_UserADBoardClose,
    CP_UserEmotion, // FACE_EXPRESSION
    CP_UserActivateEffectItem, // USE_ITEMEFFECT
    CP_UserUpgradeTombEffect,
    CP_UserHP,
    CP_Premium,
    CP_UserBanMapByMob,
    CP_UserMonsterBookSetCover,
    CP_UserSelectNpc, // NPC_TALK
    CP_UserRemoteShopOpenRequest,
    CP_UserScriptMessageAnswer, // NPC_TALK_MORE
    CP_UserShopRequest, // NPC_SHOP
    CP_UserTrunkRequest, // STORAGE
    CP_UserEntrustedShopRequest,
    CP_UserStoreBankRequest,
    CP_UserParcelRequest,
    CP_UserEffectLocal,
    CP_ShopScannerRequest,
    CP_ShopLinkRequest,
    CP_AdminShopRequest,
    CP_UserGatherItemRequest,
    CP_UserSortItemRequest,
    CP_UserChangeSlotPositionRequest, // ITEM_MOVE
    CP_UserStatChangeItemUseRequest, // USE_ITEM
    CP_UserStatChangeItemCancelRequest, // CANCEL_ITEM_EFFECT
    CP_UserStatChangeByPortableChairRequest,
    CP_UserMobSummonItemUseRequest, // USE_SUMMON_BAG
    CP_UserPetFoodItemUseRequest, // PET_FOOD
    CP_UserTamingMobFoodItemUseRequest, // USE_MOUNT_FOOD
    CP_UserScriptItemUseRequest,
    CP_UserConsumeCashItemUseRequest, // USE_CASH_ITEM
    CP_UserDestroyPetItemRequest,
    CP_UserBridleItemUseRequest, // USE_CATCH_ITEM
    CP_UserSkillLearnItemUseRequest, // USE_SKILL_BOOK
    CP_UserSkillResetItemUseRequest,
    CP_JMS_MONSTERBOOK_SET,
    CP_UserShopScannerItemUseRequest, // USE_OWL_MINERVA
    CP_UserMapTransferItemUseRequest, // USE_TELE_ROCK
    CP_UserPortalScrollUseRequest, // USE_RETURN_SCROLL
    CP_UserUpgradeItemUseRequest, // USE_UPGRADE_SCROLL
    CP_UserHyperUpgradeItemUseRequest,
    CP_UserItemOptionUpgradeItemUseRequest,
    CP_UserUIOpenItemUseRequest,
    CP_UserItemReleaseRequest,
    CP_UserAbilityUpRequest, // DISTRIBUTE_AP
    CP_UserAbilityMassUpRequest,
    CP_UserChangeStatRequest, // HEAL_OVER_TIME
    CP_UserChangeStatRequestByItemOption,
    CP_UserSkillUpRequest, // DISTRIBUTE_SP
    CP_UserSkillUseRequest, // SPECIAL_MOVE
    CP_UserSkillCancelRequest, // CANCEL_BUFF
    CP_UserSkillPrepareRequest, // SKILL_EFFECT
    CP_UserDropMoneyRequest, // MESO_DROP
    CP_UserGivePopularityRequest, // GIVE_FAME
    CP_UserPartyRequest,
    CP_UserCharacterInfoRequest, // CHAR_INFO_REQUEST
    CP_UserActivatePetRequest, // SPAWN_PET
    CP_UserTemporaryStatUpdateRequest, // CANCEL_DEBUFF
    CP_UserPortalScriptRequest, // CHANGE_MAP_SPECIAL
    CP_UserPortalTeleportRequest, // USE_INNER_PORTAL
    CP_UserMapTransferRequest, // TROCK_ADD_MAP
    CP_UserAntiMacroItemUseRequest,
    CP_UserAntiMacroSkillUseRequest,
    CP_UserAntiMacroQuestionResult,
    CP_UserClaimRequest,
    CP_UserQuestRequest, // QUEST_ACTION
    CP_UserCalcDamageStatSetRequest,
    CP_UserThrowGrenade,
    CP_UserMacroSysDataModified,
    CP_UserSelectNpcItemUseRequest,
    CP_UserLotteryItemUseRequest,
    CP_UserItemMakeRequest,
    CP_UserSueCharacterRequest,
    CP_UserUseGachaponBoxRequest,
    CP_UserUseGachaponRemoteRequest,
    CP_UserUseWaterOfLife,
    CP_UserRepairDurabilityAll,
    CP_UserRepairDurability,
    CP_UserQuestRecordSetState,
    CP_UserClientTimerEndRequest,
    CP_UserFollowCharacterRequest,
    CP_UserFollowCharacterWithdraw,
    CP_UserSelectPQReward,
    CP_UserRequestPQReward,
    CP_SetPassenserResult,
    CP_BroadcastMsg,
    CP_GroupMessage, // PARTYCHAT
    CP_Whisper, // WHISPER
    CP_CoupleMessage,
    CP_Messenger,
    CP_MiniRoom,
    CP_PartyRequest,
    CP_PartyResult,
    CP_ExpeditionRequest,
    CP_PartyAdverRequest,
    CP_GuildRequest,
    CP_GuildResult,
    CP_Admin,
    CP_Log,
    CP_FriendRequest,
    CP_MemoRequest,
    CP_MemoFlagRequest,
    CP_EnterTownPortalRequest,
    CP_EnterOpenGateRequest,
    CP_SlideRequest,
    CP_FuncKeyMappedModified,
    CP_RPSGame,
    CP_MarriageRequest,
    CP_WeddingWishListRequest,
    CP_WeddingProgress,
    CP_GuestBless,
    CP_BoobyTrapAlert,
    CP_StalkBegin,
    CP_AllianceRequest,
    CP_AllianceResult,
    CP_FamilyChartRequest,
    CP_FamilyInfoRequest,
    CP_FamilyRegisterJunior,
    CP_FamilyUnregisterJunior,
    CP_FamilyUnregisterParent,
    CP_FamilyJoinResult,
    CP_FamilyUsePrivilege,
    CP_FamilySetPrecept,
    CP_FamilySummonResult,
    CP_ChatBlockUserReq,
    CP_GuildBBS,
    CP_JMS_InstancePortalEnter,
    CP_JMS_InstancePortalCreate,
    CP_JMS_MapleGift,
    CP_UserMigrateToITCRequest,
    CP_UserExpUpItemUseRequest,
    CP_UserTempExpUseRequest,
    CP_JMS_JUKEBOX, // ItemID 2150001
    CP_NewYearCardRequest,
    CP_RandomMorphRequest,
    CP_CashItemGachaponRequest,
    CP_CashGachaponOpenRequest,
    CP_JMS_Poll_Answer,
    CP_ChangeMaplePointRequest,
    CP_TalkToTutor,
    CP_RequestIncCombo,
    CP_MobCrcKeyChangedReply,
    CP_RequestSessionValue,
    CP_UpdateGMBoard,
    CP_AccountMoreInfo,
    CP_FindFriend,
    CP_AcceptAPSPEvent,
    CP_UserDragonBallBoxRequest,
    CP_UserDragonBallSummonRequest,
    CP_BEGIN_PET,
    CP_PetMove, // MOVE_PET
    CP_PetAction,
    CP_PetInteractionRequest,
    CP_PetDropPickUpRequest,
    CP_PetStatChangeItemUseRequest,
    CP_PetUpdateExceptionListRequest,
    CP_END_PET,
    CP_BEGIN_SUMMONED,
    CP_SummonedMove,
    CP_SummonedAttack,
    CP_SummonedHit,
    CP_SummonedSkill,
    CP_Remove,
    CP_END_SUMMONED,
    CP_BEGIN_DRAGON,
    CP_DragonMove,
    CP_END_DRAGON,
    CP_QuickslotKeyMappedModified,
    CP_PassiveskillInfoUpdate,
    CP_UpdateScreenSetting,
    CP_UserAttackUser_Specific,
    CP_UserPamsSongUseRequest,
    CP_QuestGuideRequest,
    CP_UserRepeatEffectRemove,
    CP_JMS_FarmEnter,
    CP_JMS_FarmLeave,
    CP_END_USER,
    CP_BEGIN_FIELD,
    CP_BEGIN_LIFEPOOL,
    CP_BEGIN_MOB,
    CP_MobMove, // MOVE_LIFE
    CP_MobApplyCtrl, // AUTO_AGGRO
    CP_MobDropPickUpRequest,
    CP_MobHitByObstacle,
    CP_MobHitByMob,
    CP_MobSelfDestruct,
    CP_MobAttackMob,
    CP_MobSkillDelayEnd,
    CP_MobTimeBombEnd,
    CP_MobEscortCollision,
    CP_MobRequestEscortInfo,
    CP_MobEscortStopEndRequest,
    CP_END_MOB,
    CP_BEGIN_NPC,
    CP_NpcMove,
    CP_NpcSpecialAction,
    CP_END_NPC,
    CP_END_LIFEPOOL,
    CP_BEGIN_DROPPOOL,
    CP_DropPickUpRequest,
    CP_END_DROPPOOL,
    CP_BEGIN_REACTORPOOL,
    CP_ReactorHit,
    CP_ReactorTouch,
    CP_RequireFieldObstacleStatus,
    CP_END_REACTORPOOL,
    CP_BEGIN_EVENT_FIELD,
    CP_EventStart,
    CP_SnowBallHit,
    CP_SnowBallTouch,
    CP_CoconutHit,
    CP_TournamentMatchTable,
    CP_PulleyHit,
    CP_END_EVENT_FIELD,
    CP_BEGIN_MONSTER_CARNIVAL_FIELD,
    CP_MCarnivalRequest,
    CP_END_MONSTER_CARNIVAL_FIELD,
    CP_CONTISTATE, // 船の状態取得
    CP_BEGIN_PARTY_MATCH,
    CP_INVITE_PARTY_MATCH,
    CP_CANCEL_INVITE_PARTY_MATCH,
    CP_END_PARTY_MATCH,
    CP_RequestFootHoldInfo,
    CP_FootHoldInfo,
    BEANS_OPERATION,
    BEANS_UPDATE,
    CP_JMS_PachinkoPrizes,
    CP_END_FIELD,
    // ポイントショップ
    CP_BEGIN_CASHSHOP,
    CP_CashShopChargeParamRequest,
    CP_CashShopQueryCashRequest,
    CP_CashShopCashItemRequest,
    CP_CashShopCheckCouponRequest,
    CP_JMS_RECOMMENDED_AVATAR,
    CP_CashShopMemberShopRequest,
    CP_CashShopGiftMateInfoRequest,
    CP_CashShopSearchLog,
    CP_CashShopCoodinationRequest,
    CP_CashShopCheckMileageRequest,
    CP_CashShopNaverUsageInfoRequest,
    CP_END_CASHSHOP,
    CP_CheckSSN2OnCreateNewCharacter,
    CP_CheckSPWOnCreateNewCharacter,
    CP_FirstSSNOnCreateNewCharacter,
    // 多分絵具とかアイテム集める系のUI
    CP_BEGIN_RAISE,
    CP_RaiseRefesh,
    CP_RaiseUIState,
    CP_RaiseIncExp,
    CP_RaiseAddPiece,
    CP_END_RAISE,
    CP_SendMateMail,
    CP_RequestGuildBoardAuthKey,
    CP_RequestConsultAuthKey,
    CP_RequestClassCompetitionAuthKey,
    CP_RequestWebBoardAuthKey,
    CP_BEGIN_ITEMUPGRADE,
    // ビシャスのハンマー
    CP_GoldHammerRequest,
    CP_GoldHammerComplete,
    CP_ItemUpgradeComplete,
    CP_END_ITEMUPGRADE,
    CP_BEGIN_BATTLERECORD,
    CP_BATTLERECORD_ONOFF_REQUEST,
    CP_END_BATTLERECORD,
    CP_BEGIN_MAPLETV,
    CP_MapleTVSendMessageRequest,
    CP_MapleTVUpdateViewCount,
    CP_END_MAPLETV,
    // MTS
    CP_BEGIN_ITC,
    CP_ITCChargeParamRequest,
    CP_ITCQueryCashRequest,
    CP_ITCItemRequest,
    CP_END_ITC,
    CP_BEGIN_CHARACTERSALE,
    CP_CheckDuplicatedIDInCS,
    CP_END_CHARACTERSALE,
    CP_LogoutGiftSelect,
    CP_NO,
    // JMS headers
    GM_COMMAND_MAPLETV, // Super Megaphone Exploitのパケット
    // old header names
    SOLOMON, // 名称不明
    CYGNUS_SUMMON, // 名称不明
    UPDATE_QUEST, // 不明
    QUEST_ITEM, // 不明
    USE_ITEM_QUEST, // 多分Quest Value Addition Exploitのパケット
    // ヘッダに対応する処理の名前を定義
    UNKNOWN_BEGIN,
    UNKNOWN,
    UNKNOWN_END;

    // 定義値の変更や取得
    private int value;

    ClientPacketHeader(int val) {
        this.value = val;
    }

    ClientPacketHeader() {
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

    // enum range check by ordinal number
    public boolean between(ClientPacketHeader low, ClientPacketHeader high) {
        if (low.ordinal() <= ordinal() && ordinal() <= high.ordinal()) {
            return true;
        }
        return false;
    }

}
