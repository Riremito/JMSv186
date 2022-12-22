// クライアント側から送信されるパケットのヘッダの定義
package packet;

public class OutPacket {

    private byte[] packet;
    private int decoded;

    // MapleのInPacketのDecodeのように送信されたパケットを再度Decodeする
    public OutPacket(byte[] b) {
        packet = b;
        decoded = 0;
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
        short header = (short) (((short) packet[0] & 0xFF) | ((short) packet[1] & 0xFF << 8));
        String text = String.format("@%04X", header);

        for (int i = 2; i < packet.length; i++) {
            text += String.format(" %02X", packet[i]);
        }

        return text;
    }

    public byte Decode1() {
        return (byte) packet[decoded++];
    }

    public short Decode2() {
        return (short) (((short) Decode1() & 0xFF) | (((short) Decode1() & 0xFF) << 8));
    }

    public int Decode4() {
        return (int) (((int) Decode2() & 0xFFFF) | (((int) Decode2() & 0xFFFF) << 16));
    }

    public long Decode8() {
        return (long) (((long) Decode4() & 0xFFFFFFFF) | (((long) Decode4() & 0xFFFFFFFF) << 32));
    }

    public byte[] DecodeBuffer() {
        int length = Decode2();
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }

        return buffer;
    }

    public String DecodeStr() {
        int length = Decode2();
        byte[] buffer = new byte[length + 1];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }
        // 終端文字を読み取る
        buffer[length] = Decode1();

        return new String(buffer);
    }

    public enum Header {
        // Names from v95 PDB
        // ログインサーバー
        CP_BEGIN_SOCKET,
        CP_CheckPassword,
        //CP_GuestIDLogin,
        CP_AccountInfoRequest,
        CP_WorldInfoRequest,
        CP_SelectWorld,
        CP_CheckUserLimit,
        //CP_ConfirmEULA,
        //CP_SetGender,
        CP_CheckPinCode,
        //CP_UpdatePinCode,
        CP_WorldRequest,
        //CP_LogoutWorld,
        CP_ViewAllChar,
        //CP_SelectCharacterByVAC,
        //CP_VACFlagSet,
        //CP_CheckNameChangePossible,
        //CP_RegisterNewCharacter,
        //CP_CheckTransferWorldPossible,
        CP_SelectCharacter,
        CP_MigrateIn,
        CP_CheckDuplicatedID,
        CP_CreateNewCharacter,
        CP_CreateNewCharacterInCS,
        CP_DeleteCharacter,
        CP_AliveAck,
        CP_ExceptionLog,
        CP_SecurityPacket,
        //CP_EnableSPWRequest,
        //CP_CheckSPWRequest,
        //CP_EnableSPWRequestByACV,
        //CP_CheckSPWRequestByACV,
        //CP_CheckOTPRequest,
        //CP_CheckDeleteCharacterOTP,
        //CP_CreateSecurityHandle,
        //CP_SSOErrorLog,
        //CP_ClientDumpLog,
        //CP_CheckExtraCharInfo,
        //CP_CreateNewCharacter_Ex,
        CP_END_SOCKET,
        // ゲームサーバー
        CP_BEGIN_USER,
        CP_UserTransferFieldRequest,
        CP_UserTransferChannelRequest,
        CP_UserMigrateToCashShopRequest,
        CP_UserMove,
        CP_UserSitRequest,
        CP_UserPortableChairSitRequest,
        CP_UserMeleeAttack,
        CP_UserShootAttack,
        CP_UserMagicAttack,
        CP_UserBodyAttack,
        //CP_UserMovingShootAttackPrepare,
        CP_UserHit,
        CP_UserAttackUser,
        CP_UserChat,
        CP_UserADBoardClose,
        CP_UserEmotion,
        CP_UserActivateEffectItem,
        CP_UserUpgradeTombEffect,
        CP_UserHP,
        CP_Premium,
        CP_UserBanMapByMob,
        CP_UserMonsterBookSetCover,
        CP_UserSelectNpc,
        CP_UserRemoteShopOpenRequest,
        CP_UserScriptMessageAnswer,
        CP_UserShopRequest,
        CP_UserTrunkRequest,
        CP_UserEntrustedShopRequest,
        CP_UserStoreBankRequest,
        CP_UserParcelRequest,
        CP_UserEffectLocal,
        CP_ShopScannerRequest,
        CP_ShopLinkRequest,
        CP_AdminShopRequest,
        CP_UserGatherItemRequest,
        CP_UserSortItemRequest,
        CP_UserChangeSlotPositionRequest,
        CP_UserStatChangeItemUseRequest,
        CP_UserStatChangeItemCancelRequest,
        CP_UserStatChangeByPortableChairRequest,
        CP_UserMobSummonItemUseRequest,
        CP_UserPetFoodItemUseRequest,
        CP_UserTamingMobFoodItemUseRequest,
        CP_UserScriptItemUseRequest,
        CP_UserConsumeCashItemUseRequest,
        CP_UserDestroyPetItemRequest,
        CP_UserBridleItemUseRequest,
        CP_UserSkillLearnItemUseRequest,
        //CP_UserSkillResetItemUseRequest,
        CP_UserShopScannerItemUseRequest,
        CP_UserMapTransferItemUseRequest,
        CP_UserPortalScrollUseRequest,
        CP_UserUpgradeItemUseRequest,
        CP_UserHyperUpgradeItemUseRequest,
        CP_UserItemOptionUpgradeItemUseRequest,
        //CP_UserUIOpenItemUseRequest,
        CP_UserItemReleaseRequest,
        CP_UserAbilityUpRequest,
        CP_UserAbilityMassUpRequest,
        CP_UserChangeStatRequest,
        CP_UserChangeStatRequestByItemOption,
        CP_UserSkillUpRequest,
        CP_UserSkillUseRequest,
        CP_UserSkillCancelRequest,
        CP_UserSkillPrepareRequest,
        CP_UserDropMoneyRequest,
        CP_UserGivePopularityRequest,
        CP_UserPartyRequest,
        CP_UserCharacterInfoRequest,
        CP_UserActivatePetRequest,
        CP_UserTemporaryStatUpdateRequest,
        CP_UserPortalScriptRequest,
        CP_UserPortalTeleportRequest,
        CP_UserMapTransferRequest,
        CP_UserAntiMacroItemUseRequest,
        CP_UserAntiMacroSkillUseRequest,
        CP_UserAntiMacroQuestionResult,
        CP_UserClaimRequest,
        CP_UserQuestRequest,
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
        CP_GroupMessage,
        CP_Whisper,
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
        CP_UserMigrateToITCRequest,
        CP_UserExpUpItemUseRequest,
        CP_UserTempExpUseRequest,
        CP_NewYearCardRequest,
        CP_RandomMorphRequest,
        CP_CashItemGachaponRequest,
        CP_CashGachaponOpenRequest,
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
        CP_PetMove,
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
        CP_END_USER,
        CP_BEGIN_FIELD,
        CP_BEGIN_LIFEPOOL,
        CP_BEGIN_MOB,
        CP_MobMove,
        CP_MobApplyCtrl,
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
        CP_CONTISTATE,
        CP_BEGIN_PARTY_MATCH,
        CP_INVITE_PARTY_MATCH,
        CP_CANCEL_INVITE_PARTY_MATCH,
        CP_END_PARTY_MATCH,
        CP_RequestFootHoldInfo,
        CP_FootHoldInfo,
        CP_END_FIELD,
        CP_BEGIN_CASHSHOP,
        CP_CashShopChargeParamRequest,
        CP_CashShopQueryCashRequest,
        CP_CashShopCashItemRequest,
        CP_CashShopCheckCouponRequest,
        CP_CashShopGiftMateInfoRequest,
        CP_END_CASHSHOP,
        CP_CheckSSN2OnCreateNewCharacter,
        CP_CheckSPWOnCreateNewCharacter,
        CP_FirstSSNOnCreateNewCharacter,
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
        // ヘッダに対応する処理の名前を定義
        UNKNOWN,
        REACHED_LOGIN_SCREEN, // 名称不明
        RSA_KEY, // 名称不明
        WHEEL_OF_FORTUNE, // 不明
        CANCEL_DEBUFF,
        CHANGE_MAP_SPECIAL,
        PORTAL_INSIDE_MAP,
        GET_BUFF_REQUEST,
        QUEST_ACTION,
        TROCK_ADD_MAP,
        SKILL_MACRO,
        ITEM_MAKER,
        GM_COMMAND,
        GM_COMMAND_TEXT,
        GM_COMMAND_SERVER_MESSAGE,
        GM_COMMAND_EVENT_START,
        GM_COMMAND_MAPLETV,
        SNOWBALL,
        PET_CHAT,
        REWARD_ITEM,
        REPAIR_ALL,
        REPAIR,
        SOLOMON,
        GACH_EXP,
        FOLLOW_REQUEST,
        FOLLOW_REPLY,
        USE_TREASUER_CHEST,
        PARTYCHAT,
        WHISPER,
        MESSENGER,
        PLAYER_INTERACTION,
        PARTY_OPERATION,
        DENY_PARTY_REQUEST,
        EXPEDITION_OPERATION,
        EXPEDITION_LISTING,
        GUILD_OPERATION,
        DENY_GUILD_REQUEST,
        BUDDYLIST_MODIFY,
        NOTE_ACTION,
        USE_DOOR,
        CHANGE_KEYMAP,
        RPS_GAME,
        RING_ACTION,
        WEDDING_REGISTRY,
        ALLIANCE_OPERATION,
        DENY_ALLIANCE_REQUEST,
        REQUEST_FAMILY,
        OPEN_FAMILY,
        FAMILY_OPERATION,
        DELETE_JUNIOR,
        DELETE_SENIOR,
        ACCEPT_FAMILY,
        USE_FAMILY,
        FAMILY_PRECEPT,
        FAMILY_SUMMON,
        CYGNUS_SUMMON,
        ARAN_COMBO,
        BBS_OPERATION,
        ENTER_MTS,
        AVATAR_RANDOM_BOX_OPEN,
        MOVE_PET,
        PET_COMMAND,
        PET_LOOT,
        PET_AUTO_POT,
        MOVE_SUMMON,
        SUMMON_ATTACK,
        DAMAGE_SUMMON,
        MOVE_DRAGON,
        MOVE_LIFE,
        AUTO_AGGRO,
        FRIENDLY_DAMAGE,
        MONSTER_BOMB,
        HYPNOTIZE_DMG,
        MOB_NODE,
        DISPLAY_NODE,
        NPC_ACTION,
        ITEM_PICKUP,
        DAMAGE_REACTOR,
        TOUCH_REACTOR,
        LEFT_KNOCK_BACK,
        COCONUT,
        MONSTER_CARNIVAL,
        SHIP_OBJECT,
        PARTY_SEARCH_START,
        PARTY_SEARCH_STOP,
        CS_FILL,
        CS_UPDATE,
        BUY_CS_ITEM,
        COUPON_CODE,
        RECOMMENDED_AVATAR,
        ETC_ITEM_UI,
        ETC_ITEM_UI_UPDATE,
        ETC_ITEM_UI_DROP_ITEM,
        MAPLETV,
        UPDATE_QUEST,
        QUEST_ITEM,
        USE_ITEM_QUEST,
        TOUCHING_MTS,
        MTS_TAB,
        BEANS_OPERATION,
        BEANS_UPDATE,
        VICIOUS_HAMMER;

        // 定義値の変更や取得
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
        // ログインサーバー 必須
        Header.CP_CheckPassword.Set(0x0001);
        Header.CP_WorldInfoRequest.Set(0x0003);
        Header.CP_SelectWorld.Set(0x0004);
        Header.CP_SelectCharacter.Set(0x0006);
        Header.CP_MigrateIn.Set(0x0007);
        Header.CP_CheckDuplicatedID.Set(0x0008);
        Header.CP_CreateNewCharacter.Set(0x000B);
        Header.CP_DeleteCharacter.Set(0x000D);
        // ログインサーバー その他
        //Header.SERVERSTATUS_REQUEST.Set(0x0005);
        Header.CP_ExceptionLog.Set(0x000F);
        Header.REACHED_LOGIN_SCREEN.Set(0x0018);
        // ゲームサーバー 必須
        Header.CP_UserTransferFieldRequest.Set(0x001C);
        Header.CP_UserTransferChannelRequest.Set(0x001D);
        Header.CP_UserMove.Set(0x001F);
        Header.CP_UserChat.Set(0x0027);
        Header.CP_UserMeleeAttack.Set(0x0022);

        Header.CP_UserSelectNpc.Set(0x0030);
        Header.CP_UserRemoteShopOpenRequest.Set(Header.CP_UserSelectNpc.Get() + 0x01);
        Header.CP_UserScriptMessageAnswer.Set(Header.CP_UserSelectNpc.Get() + 0x02); // 0032
        Header.CP_UserShopRequest.Set(Header.CP_UserSelectNpc.Get() + 0x03);
        Header.CP_UserTrunkRequest.Set(Header.CP_UserSelectNpc.Get() + 0x04);
        //Header.USE_HIRED_MERCHANT.Set(Header.NPC_TALK.Get() + 0x05);
        // ゲームサーバー その他
        Header.PORTAL_INSIDE_MAP.Set(0x005B);

        // 簡単に確認が可能
        Header.CP_UserMigrateToCashShopRequest.Set(0x001E);
        Header.CP_UserHit.Set(0x0026);
        Header.CP_UserChangeSlotPositionRequest.Set(0x003D);
        Header.CP_UserStatChangeItemUseRequest.Set(0x003E);

        /*
        Header.USE_SUMMON_BAG.Set(0x0044);
        Header.PET_FOOD.Set(0x0045);
        Header.USE_MOUNT_FOOD.Set(0x0046);
        Header.USE_SCRIPTED_NPC_ITEM.Set(0x0047);
        Header.USE_CASH_ITEM.Set(0x0048);
        Header.USE_CATCH_ITEM.Set(0x004A);
        Header.USE_SKILL_BOOK.Set(0x004B);
        Header.OWL_USE_ITEM_VERSION_SEARCH.Set(0x004C);
        Header.USE_TELE_ROCK.Set(0x004D);
         */
        Header.CP_UserPortalScrollUseRequest.Set(0x004B);
        Header.CP_UserUpgradeItemUseRequest.Set(Header.CP_UserPortalScrollUseRequest.Get() + 0x01);

        Header.CP_UserAbilityUpRequest.Set(0x004D);
        Header.CP_UserAbilityMassUpRequest.Set(Header.CP_UserAbilityUpRequest.Get() + 0x01);

        Header.CP_UserSkillUpRequest.Set(0x0050);
        Header.CP_UserSkillUseRequest.Set(Header.CP_UserSkillUpRequest.Get() + 0x01);
        Header.CP_UserSkillCancelRequest.Set(Header.CP_UserSkillUpRequest.Get() + 0x02);
        Header.CP_UserSkillPrepareRequest.Set(Header.CP_UserSkillUpRequest.Get() + 0x03);
        Header.CP_UserDropMoneyRequest.Set(Header.CP_UserSkillUpRequest.Get() + 0x04);

        Header.CHANGE_MAP_SPECIAL.Set(0x005A);
        Header.PORTAL_INSIDE_MAP.Set(Header.CHANGE_MAP_SPECIAL.Get() + 0x01);
        Header.TROCK_ADD_MAP.Set(Header.CHANGE_MAP_SPECIAL.Get() + 0x02);

        //Header.QUEST_ACTION.Set(0x0061); クエスト情報がおかしくなり、ログイン不可になる
        Header.SKILL_MACRO.Set(0x0064);

        Header.CHANGE_KEYMAP.Set(0x007B);

        Header.ENTER_MTS.Set(0x0091);
    }

    public static void SetForJMSv176() {
        Header.CP_CheckPassword.Set(0x0001);
        Header.CP_WorldInfoRequest.Set(0x0003);
        Header.CP_SelectWorld.Set(0x0004);
        Header.CP_CheckUserLimit.Set(0x0005);
        Header.CP_SelectCharacter.Set(0x0006);
        Header.CP_MigrateIn.Set(0x0007);
        Header.CP_CheckDuplicatedID.Set(0x0008);
        Header.CP_CreateNewCharacter.Set(0x000B);
        Header.CP_DeleteCharacter.Set(0x000D);
        Header.REACHED_LOGIN_SCREEN.Set(0x0017);
        Header.RSA_KEY.Set(0x0019);
        Header.CP_UserChat.Set(0x0028);
        Header.CP_UserTransferChannelRequest.Set(0x001C);
        Header.CP_UserMove.Set(0x001F);
        Header.CHANGE_MAP_SPECIAL.Set(0x005E);
        Header.PORTAL_INSIDE_MAP.Set(0x005F);
    }

    public static void SetForJMSv184() {
        Header.CP_CheckPassword.Set(0x0001);
        Header.CP_WorldInfoRequest.Set(0x0003);
        Header.CP_SelectWorld.Set(0x0004);
        Header.CP_CheckUserLimit.Set(0x0005);
        Header.CP_SelectCharacter.Set(0x0006);
        Header.CP_MigrateIn.Set(0x0007);
        Header.CP_CheckDuplicatedID.Set(0x0008);
        Header.REACHED_LOGIN_SCREEN.Set(0x0017);
        Header.RSA_KEY.Set(0x0019);
        Header.CP_UserChat.Set(0x0028);
        Header.CP_UserTransferFieldRequest.Set(0x001C);
        Header.CP_UserMove.Set(0x001F);
        Header.CHANGE_MAP_SPECIAL.Set(0x005E);
        Header.PORTAL_INSIDE_MAP.Set(0x005F);
    }

    // JMS v186.1 SendPacket
    public static void SetForJMSv186() {
        // ログインサーバー
        Header.CP_BEGIN_SOCKET.Set(0x0000);
        Header.CP_CheckPassword.Set(0x0001);
        // 0x0002, CP_AccountInfoRequest
        Header.CP_WorldInfoRequest.Set(0x0003);
        Header.CP_SelectWorld.Set(0x0004);
        Header.CP_CheckUserLimit.Set(0x0005);
        Header.CP_SelectCharacter.Set(0x0006);
        Header.CP_MigrateIn.Set(0x0007);
        Header.CP_CheckDuplicatedID.Set(0x0008);
        // 0x0009 ???
        // 0x000A @000A, 全キャラクター確認, CP_ViewAllChar
        Header.CP_CreateNewCharacter.Set(0x000B);
        // 0x000C, CP_CreateNewCharacterInCS
        Header.CP_DeleteCharacter.Set(0x000D);
        // 0x000E InPacket @0x0009から送信されるようになっているが未確認, CP_AliveAck
        Header.CP_ExceptionLog.Set(0x000F);
        // 0x0010
        // 0x0011 InPacket 0x000Eから送信される
        // 0x0012
        // 0x0013
        Header.CP_CheckPinCode.Set(0x0014);
        // 0x0015
        // 0x0016
        // 0x0017
        Header.REACHED_LOGIN_SCREEN.Set(0x0018);
        // 0x0019 InPacket 0x0013から送信される
        Header.RSA_KEY.Set(0x001A);
        Header.CP_END_SOCKET.Set(0x001B);

        // ゲームサーバー
        Header.CP_BEGIN_USER.Set(0x001C);
        Header.CP_UserTransferFieldRequest.Set(0x001D);
        Header.CP_UserTransferChannelRequest.Set(0x001E);
        Header.CP_UserMigrateToCashShopRequest.Set(0x001F);
        Header.CP_UserMove.Set(0x0020);
        Header.CP_UserSitRequest.Set(0x0021);
        Header.CP_UserPortableChairSitRequest.Set(0x0022);
        Header.CP_UserMeleeAttack.Set(0x0023);
        Header.CP_UserShootAttack.Set(0x0024);
        Header.CP_UserMagicAttack.Set(0x0025);
        Header.CP_UserBodyAttack.Set(0x0026);
        Header.CP_UserHit.Set(0x0027);
        // 0x0028, CP_UserAttackUser
        Header.CP_UserChat.Set(0x0029);
        Header.CP_UserADBoardClose.Set(0x002A);
        Header.CP_UserEmotion.Set(0x002B);
        Header.CP_UserActivateEffectItem.Set(0x002C);
        Header.WHEEL_OF_FORTUNE.Set(0x002D);
        // 0x0030
        Header.CP_UserMonsterBookSetCover.Set(0x0031);
        Header.CP_UserSelectNpc.Set(0x0032);
        Header.CP_UserRemoteShopOpenRequest.Set(0x0033);
        Header.CP_UserScriptMessageAnswer.Set(0x0034);
        Header.CP_UserShopRequest.Set(0x0035);
        Header.CP_UserTrunkRequest.Set(0x0036);
        Header.CP_UserEntrustedShopRequest.Set(0x0037);
        // 0x0038, CP_UserStoreBankRequest
        Header.CP_UserParcelRequest.Set(0x0039); // OK
        Header.CP_UserEffectLocal.Set(0x003A);
        Header.CP_ShopScannerRequest.Set(0x003B);
        Header.CP_ShopLinkRequest.Set(0x003C);
        // 0x003D InPacket 0x0158, 0x0159から送信される,
        Header.CP_UserSortItemRequest.Set(0x003E);
        Header.CP_UserGatherItemRequest.Set(0x003F);
        Header.CP_UserChangeSlotPositionRequest.Set(0x0040);
        Header.CP_UserStatChangeItemUseRequest.Set(0x0041);
        Header.CP_UserStatChangeItemCancelRequest.Set(0x0042);
        // 0x0043, CP_UserStatChangeByPortableChairRequest
        Header.CP_UserMobSummonItemUseRequest.Set(0x0044);
        Header.CP_UserPetFoodItemUseRequest.Set(0x0045);
        Header.CP_UserTamingMobFoodItemUseRequest.Set(0x0046);
        Header.CP_UserScriptItemUseRequest.Set(0x0047);
        Header.CP_UserConsumeCashItemUseRequest.Set(0x0048);
        // 0x0049, CP_UserDestroyPetItemRequest
        Header.CP_UserBridleItemUseRequest.Set(0x004A);
        Header.CP_UserSkillLearnItemUseRequest.Set(0x004B);
        Header.CP_UserShopScannerItemUseRequest.Set(0x004C);
        Header.CP_UserMapTransferItemUseRequest.Set(0x004D);
        Header.CP_UserPortalScrollUseRequest.Set(0x004E);
        Header.CP_UserUpgradeItemUseRequest.Set(0x004F);
        Header.CP_UserHyperUpgradeItemUseRequest.Set(0x0050);
        Header.CP_UserItemOptionUpgradeItemUseRequest.Set(0x0051);
        Header.CP_UserItemReleaseRequest.Set(0x0052);
        Header.CP_UserAbilityUpRequest.Set(0x0053);
        Header.CP_UserAbilityMassUpRequest.Set(0x0054);
        Header.CP_UserChangeStatRequest.Set(0x0055);
        // 0x0056, CP_UserChangeStatRequestByItemOption
        Header.CP_UserSkillUpRequest.Set(0x0057);
        Header.CP_UserSkillUseRequest.Set(0x0058);
        Header.CP_UserSkillCancelRequest.Set(0x0059);
        Header.CP_UserSkillPrepareRequest.Set(0x005A);
        Header.CP_UserDropMoneyRequest.Set(0x005B);
        Header.CP_UserGivePopularityRequest.Set(0x005C);
        // 0x005D, CP_UserPartyRequest
        Header.CP_UserCharacterInfoRequest.Set(0x005E);
        Header.CP_UserActivatePetRequest.Set(0x005F);
        Header.CANCEL_DEBUFF.Set(0x0060);
        // 0x0061
        Header.CHANGE_MAP_SPECIAL.Set(0x0062);
        Header.PORTAL_INSIDE_MAP.Set(0x0063);
        Header.TROCK_ADD_MAP.Set(0x0064);
        // 0x0065
        // 0x0066
        // 0x0067
        // 0x0068
        Header.QUEST_ACTION.Set(0x0069);
        Header.GET_BUFF_REQUEST.Set(0x006A);
        // 0x006B
        Header.SKILL_MACRO.Set(0x006C);
        // 0x006D
        // 0x006E
        Header.ITEM_MAKER.Set(0x006F);
        Header.REWARD_ITEM.Set(0x0070);
        // 0x0071
        Header.REPAIR_ALL.Set(0x0072);
        Header.REPAIR.Set(0x0073);
        // 0x0074
        // 0x0075
        Header.SOLOMON.Set(0x0076);
        Header.GACH_EXP.Set(0x0077);
        Header.FOLLOW_REQUEST.Set(0x0078);
        Header.FOLLOW_REPLY.Set(0x0079);
        Header.USE_TREASUER_CHEST.Set(0x007A);
        Header.GM_COMMAND_SERVER_MESSAGE.Set(0x007B);
        Header.PARTYCHAT.Set(0x007C);
        Header.WHISPER.Set(0x007D);
        Header.MESSENGER.Set(0x007E);
        Header.PLAYER_INTERACTION.Set(0x007F);
        Header.PARTY_OPERATION.Set(0x0080);
        Header.DENY_PARTY_REQUEST.Set(0x0081);
        Header.EXPEDITION_OPERATION.Set(0x0082);
        Header.EXPEDITION_LISTING.Set(0x0083);
        Header.GUILD_OPERATION.Set(0x0084);
        Header.DENY_GUILD_REQUEST.Set(0x0085);
        Header.GM_COMMAND.Set(0x0086);
        Header.GM_COMMAND_TEXT.Set(0x0087);
        Header.BUDDYLIST_MODIFY.Set(0x0088);
        Header.NOTE_ACTION.Set(0x0089);
        // 0x008A
        Header.USE_DOOR.Set(0x008B);
        // 0x008C
        // 0x008D
        Header.CHANGE_KEYMAP.Set(0x008E);
        Header.RPS_GAME.Set(0x008F);
        Header.RING_ACTION.Set(0x0090);
        Header.WEDDING_REGISTRY.Set(0x0091);
        // 0x0092
        // 0x0093
        // 0x0094
        Header.ALLIANCE_OPERATION.Set(0x0095);
        Header.DENY_ALLIANCE_REQUEST.Set(0x0096);
        Header.REQUEST_FAMILY.Set(0x0097);
        Header.OPEN_FAMILY.Set(0x0098);
        Header.FAMILY_OPERATION.Set(0x0099);
        Header.DELETE_JUNIOR.Set(0x009A);
        Header.DELETE_SENIOR.Set(0x009B);
        Header.ACCEPT_FAMILY.Set(0x009C);
        Header.USE_FAMILY.Set(0x009D);
        Header.FAMILY_PRECEPT.Set(0x009E);
        Header.FAMILY_SUMMON.Set(0x009F);
        Header.CYGNUS_SUMMON.Set(0x00A0);
        Header.ARAN_COMBO.Set(0x00A1);
        // 0x00A2 InPacket 0x0111から送信される
        // 0x00A3
        Header.BBS_OPERATION.Set(0x00A4);
        // 0x00A5
        // 0x00A6
        // 0x00A7
        // 0x00A8
        // 0x00A9
        Header.ENTER_MTS.Set(0x00AA);
        Header.AVATAR_RANDOM_BOX_OPEN.Set(0x00AB);
        // 0x00AC
        // 0x00AD
        Header.MOVE_PET.Set(0x00AE);
        Header.PET_CHAT.Set(0x00AF);
        Header.PET_COMMAND.Set(0x00B0);
        Header.PET_LOOT.Set(0x00B1);
        Header.PET_AUTO_POT.Set(0x00B2);
        // 0x00B3
        // 0x00B4
        // 0x00B5
        Header.MOVE_SUMMON.Set(0x00B6);
        Header.SUMMON_ATTACK.Set(0x00B7);
        Header.DAMAGE_SUMMON.Set(0x00B8);
        // 0x00B9
        // 0x00BA
        // 0x00BB
        // 0x00BC
        Header.MOVE_DRAGON.Set(0x00BD);
        // 0x00BE
        // 0x00BF
        // 0x00C0
        // 0x00C1
        // 0x00C2 InPacket 0x007Cから送信される, ファムの歌を利用する処理 @00C2 [00or01]が送信される01は使用フラグ
        // 0x00C3
        // 0x00C4
        // 0x00C5
        // 0x00C6
        Header.MOVE_LIFE.Set(0x00C7);
        Header.AUTO_AGGRO.Set(0x00C8);
        // 0x00C9
        // 0x00CA
        Header.FRIENDLY_DAMAGE.Set(0x00CB);
        Header.MONSTER_BOMB.Set(0x00CC);
        Header.HYPNOTIZE_DMG.Set(0x00CD);
        // 0x00CE
        // 0x00CF
        Header.MOB_NODE.Set(0x00D0);
        Header.DISPLAY_NODE.Set(0x00D1);
        // 0x00D2
        // 0x00D3
        // 0x00D4
        Header.NPC_ACTION.Set(0x00D5);
        // 0x00D6
        // 0x00D7
        // 0x00D8
        // 0x00D9
        Header.ITEM_PICKUP.Set(0x00DA);
        // 0x00DB
        // 0x00DC
        Header.DAMAGE_REACTOR.Set(0x00DD);
        Header.TOUCH_REACTOR.Set(0x00DE);
        // 0x00DF
        // 0x00E0
        // 0x00E1
        Header.GM_COMMAND_EVENT_START.Set(0x00E2);
        // 0x00E3
        Header.SNOWBALL.Set(0x00E4);
        Header.LEFT_KNOCK_BACK.Set(0x00E5);
        Header.COCONUT.Set(0x00E6);
        // 0x00E7
        // 0x00E8
        Header.MONSTER_CARNIVAL.Set(0x00E9);
        // 0x00EA
        // 0x00EB
        Header.SHIP_OBJECT.Set(0x00EC);
        // 0x00ED
        Header.PARTY_SEARCH_START.Set(0x00EE);
        Header.PARTY_SEARCH_STOP.Set(0x00EF);
        // 0x00F0
        // 0x00F1
        // 0x00F2 InPacket 0x00A0から送信される
        Header.BEANS_OPERATION.Set(0x0000F3);
        Header.BEANS_UPDATE.Set(0x0000F4);
        // 0x00F5
        // 0x00F6
        // 0x00F7
        Header.CS_FILL.Set(0x00F8);
        Header.CS_UPDATE.Set(0x00F9);
        Header.BUY_CS_ITEM.Set(0x00FA);
        Header.COUPON_CODE.Set(0x00FB);
        // 0x00FC UI/CashShop.img/CSMemberShop
        // 0x00FD
        Header.RECOMMENDED_AVATAR.Set(0x00FE);
        // 0x00FF
        // 0x0100
        // 0x0101
        // 0x0102
        // 0x0103
        Header.ETC_ITEM_UI_UPDATE.Set(0x0104);
        Header.ETC_ITEM_UI.Set(0x0105);
        Header.ETC_ITEM_UI_DROP_ITEM.Set(0x0106);
        // 0x0107
        // 0x0108
        // 0x0109
        Header.MAPLETV.Set(0x010A);
        // 0x010B @00EAでCOUNSEL UIを開いたときに送信される
        Header.QUEST_ITEM.Set(0x010C);
        // 0x010D @00EC 1FでメイプルイベントのUIを開いたときに@010D 00が送信される
        // 0x010E
        // 0x010F
        // 0x0110
        Header.MTS_TAB.Set(0x0111);
        // 0x0112
        // 0x0113
        Header.GM_COMMAND_MAPLETV.Set(0x0114);
        // 0x0115
        // 0x0116
        // 0x0117
        // 0x0118
        // 0x0119
        Header.VICIOUS_HAMMER.Set(0x0119);
        // 0x011A
        // 0x011B
        // 0x011C
        // 0x011D
        // 0x011E
        Header.TOUCHING_MTS.Set(0x011F);
    }

    public static void SetForJMSv187() {
        // Login
        Header.CP_CheckPassword.Set(0x0001);
        Header.CP_WorldInfoRequest.Set(0x0003);
        Header.CP_SelectWorld.Set(0x0004);
        Header.CP_CheckUserLimit.Set(0x0005);
        Header.CP_SelectCharacter.Set(0x0006);
        Header.CP_MigrateIn.Set(0x0007);
        Header.CP_CheckDuplicatedID.Set(0x0008);
        Header.CP_CreateNewCharacter.Set(0x000B);
        Header.CP_DeleteCharacter.Set(0x000D);
        Header.CP_ExceptionLog.Set(0x000F);
        Header.CP_CheckPinCode.Set(0x0014);
        Header.REACHED_LOGIN_SCREEN.Set(0x0018);
        Header.RSA_KEY.Set(0x001A);
        // Game
        Header.CP_UserTransferFieldRequest.Set(0x001D);
        Header.CP_UserTransferChannelRequest.Set(0x001E);
        Header.CP_UserMigrateToCashShopRequest.Set(0x001F);
        Header.CP_UserMove.Set(0x0020);
        Header.CP_UserSitRequest.Set(0x0021);
        Header.CP_UserPortableChairSitRequest.Set(0x0022);
        Header.CP_UserMeleeAttack.Set(0x0023);
        Header.CP_UserShootAttack.Set(0x0024);
        Header.CP_UserMagicAttack.Set(0x0025);
        /*
        Header.PASSIVE_ENERGY.Set(0x0026);
        Header.TAKE_DAMAGE.Set(0x0027);
         */
        Header.CP_UserChat.Set(0x0029 + 1);
        Header.CP_UserADBoardClose.Set(0x002A + 1);
        Header.CP_UserEmotion.Set(0x002B + 1);
        Header.CP_UserActivateEffectItem.Set(0x002C + 1);
        Header.WHEEL_OF_FORTUNE.Set(0x002D + 1);
        Header.CP_UserMonsterBookSetCover.Set(0x0031 + 1);
        Header.CP_UserSelectNpc.Set(0x0032 + 1);
        Header.CP_UserRemoteShopOpenRequest.Set(0x0033 + 1);
        Header.CP_UserScriptMessageAnswer.Set(0x0034 + 1);
        Header.CP_UserShopRequest.Set(0x0035 + 1);
        Header.CP_UserTrunkRequest.Set(0x0036 + 1);
        Header.CP_UserEntrustedShopRequest.Set(0x0037 + 1);
        Header.CP_UserParcelRequest.Set(0x0039 + 1);
        Header.CP_UserEffectLocal.Set(0x003A + 1);
        Header.CP_ShopScannerRequest.Set(0x003B + 1);
        Header.CP_ShopLinkRequest.Set(0x003C + 1);
        Header.CP_UserSortItemRequest.Set(0x003E + 1);
        Header.CP_UserGatherItemRequest.Set(0x003F + 1);
        Header.CP_UserChangeSlotPositionRequest.Set(0x0040 + 1);
        /*
        Header.USE_ITEM.Set(0x0041);
        Header.CANCEL_ITEM_EFFECT.Set(0x0042);
        Header.USE_SUMMON_BAG.Set(0x0044);
        Header.PET_FOOD.Set(0x0045);
        Header.USE_MOUNT_FOOD.Set(0x0046);
        Header.USE_SCRIPTED_NPC_ITEM.Set(0x0047);
        Header.USE_CASH_ITEM.Set(0x0048);
        Header.USE_CATCH_ITEM.Set(0x004A);
        Header.USE_SKILL_BOOK.Set(0x004B);
        Header.OWL_USE_ITEM_VERSION_SEARCH.Set(0x004C);
        Header.USE_TELE_ROCK.Set(0x004D);
        Header.USE_RETURN_SCROLL.Set(0x004E);
        Header.USE_UPGRADE_SCROLL.Set(0x004F);
        Header.USE_EQUIP_SCROLL.Set(0x0050);
        Header.USE_POTENTIAL_SCROLL.Set(0x0051);
        Header.USE_MAGNIFY_GLASS.Set(0x0052);
        Header.DISTRIBUTE_AP.Set(0x0053);
        Header.AUTO_ASSIGN_AP.Set(0x0054);
        Header.HEAL_OVER_TIME.Set(0x0055);
        Header.DISTRIBUTE_SP.Set(0x0057);
        Header.SPECIAL_MOVE.Set(0x0058);
        Header.CANCEL_BUFF.Set(0x0059);
        Header.SKILL_EFFECT.Set(0x005A);
         */
        Header.CP_UserDropMoneyRequest.Set(0x005B + 3);
        Header.CP_UserGivePopularityRequest.Set(0x005C + 3);
        Header.CP_UserCharacterInfoRequest.Set(0x005E + 3);
        Header.CP_UserActivatePetRequest.Set(0x005F + 3);
        Header.CANCEL_DEBUFF.Set(0x0060 + 3);
        Header.CHANGE_MAP_SPECIAL.Set(0x0062 + 3);
        Header.PORTAL_INSIDE_MAP.Set(0x0063 + 3);
        Header.TROCK_ADD_MAP.Set(0x0064 + 3);
        Header.QUEST_ACTION.Set(0x0069 + 3);
        /*
        Header.GET_BUFF_REQUEST.Set(0x006A);
        Header.SKILL_MACRO.Set(0x006C);
        Header.ITEM_MAKER.Set(0x006F);
        Header.REWARD_ITEM.Set(0x0070);
        Header.REPAIR_ALL.Set(0x0072);
        Header.REPAIR.Set(0x0073);
        Header.SOLOMON.Set(0x0076);
        Header.GACH_EXP.Set(0x0077);
        Header.FOLLOW_REQUEST.Set(0x0078);
        Header.FOLLOW_REPLY.Set(0x0079);
        Header.USE_TREASUER_CHEST.Set(0x007A);
        Header.GM_COMMAND_SERVER_MESSAGE.Set(0x007B);
        Header.PARTYCHAT.Set(0x007C);
        Header.WHISPER.Set(0x007D);
        Header.MESSENGER.Set(0x007E);
        Header.PLAYER_INTERACTION.Set(0x007F);
        Header.PARTY_OPERATION.Set(0x0080);
        Header.DENY_PARTY_REQUEST.Set(0x0081);
        Header.EXPEDITION_OPERATION.Set(0x0082);
        Header.EXPEDITION_LISTING.Set(0x0083);
        Header.GUILD_OPERATION.Set(0x0084);
        Header.DENY_GUILD_REQUEST.Set(0x0085);
        Header.GM_COMMAND.Set(0x0086);
        Header.GM_COMMAND_TEXT.Set(0x0087);
        Header.BUDDYLIST_MODIFY.Set(0x0088);
        Header.NOTE_ACTION.Set(0x0089);
        Header.USE_DOOR.Set(0x008B);
        Header.CHANGE_KEYMAP.Set(0x008E);
        Header.RPS_GAME.Set(0x008F);
        Header.RING_ACTION.Set(0x0090);
        Header.WEDDING_REGISTRY.Set(0x0091);
        Header.ALLIANCE_OPERATION.Set(0x0095);
        Header.DENY_ALLIANCE_REQUEST.Set(0x0096);
        Header.REQUEST_FAMILY.Set(0x0097);
        Header.OPEN_FAMILY.Set(0x0098);
        Header.FAMILY_OPERATION.Set(0x0099);
        Header.DELETE_JUNIOR.Set(0x009A);
        Header.DELETE_SENIOR.Set(0x009B);
        Header.ACCEPT_FAMILY.Set(0x009C);
        Header.USE_FAMILY.Set(0x009D);
        Header.FAMILY_PRECEPT.Set(0x009E);
        Header.FAMILY_SUMMON.Set(0x009F);
        Header.CYGNUS_SUMMON.Set(0x00A0);
        Header.ARAN_COMBO.Set(0x00A1);
        Header.BBS_OPERATION.Set(0x00A4);
         */
        Header.ENTER_MTS.Set(0x00AF);
        /*
        Header.AVATAR_RANDOM_BOX_OPEN.Set(0x00AB);
        Header.MOVE_PET.Set(0x00AE);
        Header.PET_CHAT.Set(0x00AF);
        Header.PET_COMMAND.Set(0x00B0);
        Header.PET_LOOT.Set(0x00B1);
        Header.PET_AUTO_POT.Set(0x00B2);
        Header.MOVE_SUMMON.Set(0x00B6);
        Header.SUMMON_ATTACK.Set(0x00B7);
        Header.DAMAGE_SUMMON.Set(0x00B8);
        Header.MOVE_DRAGON.Set(0x00BD);
        Header.MOVE_LIFE.Set(0x00C7);
        Header.AUTO_AGGRO.Set(0x00C8);
        Header.FRIENDLY_DAMAGE.Set(0x00CB);
        Header.MONSTER_BOMB.Set(0x00CC);
        Header.HYPNOTIZE_DMG.Set(0x00CD);
        Header.MOB_NODE.Set(0x00D0);
        Header.DISPLAY_NODE.Set(0x00D1);
        Header.NPC_ACTION.Set(0x00D5);
        Header.ITEM_PICKUP.Set(0x00DA);
        Header.DAMAGE_REACTOR.Set(0x00DD);
        Header.TOUCH_REACTOR.Set(0x00DE);
        Header.GM_COMMAND_EVENT_START.Set(0x00E2);
        Header.SNOWBALL.Set(0x00E4);
        Header.LEFT_KNOCK_BACK.Set(0x00E5);
        Header.COCONUT.Set(0x00E6);
        Header.MONSTER_CARNIVAL.Set(0x00E9);
        Header.SHIP_OBJECT.Set(0x00EC);
        Header.PARTY_SEARCH_START.Set(0x00EE);
        Header.PARTY_SEARCH_STOP.Set(0x00EF);
        Header.BEANS_OPERATION.Set(0x0000F3);
        Header.BEANS_UPDATE.Set(0x0000F4);
        Header.CS_FILL.Set(0x00F8);
        Header.CS_UPDATE.Set(0x00F9);
        Header.BUY_CS_ITEM.Set(0x00FA);
        Header.COUPON_CODE.Set(0x00FB);
        Header.RECOMMENDED_AVATAR.Set(0x00FE);
        Header.ETC_ITEM_UI_UPDATE.Set(0x0104);
        Header.ETC_ITEM_UI.Set(0x0105);
        Header.ETC_ITEM_UI_DROP_ITEM.Set(0x0106);
        Header.MAPLETV.Set(0x010A);
        Header.QUEST_ITEM.Set(0x010C);
        Header.MTS_TAB.Set(0x0111);
        Header.GM_COMMAND_MAPLETV.Set(0x0114);
        Header.VICIOUS_HAMMER.Set(0x0119);
        Header.TOUCHING_MTS.Set(0x011F);
         */
    }

    public static void SetForJMSv302() {
        // Login
        Header.CP_CheckPassword.Set(0x0001);
        Header.CP_WorldInfoRequest.Set(0x0003);
        Header.CP_SelectWorld.Set(0x0004);
        Header.CP_CheckUserLimit.Set(0x0005);
        Header.CP_SelectCharacter.Set(0x0006);
        Header.CP_MigrateIn.Set(0x0007);
        Header.CP_CheckDuplicatedID.Set(0x0008);
        Header.CP_CreateNewCharacter.Set(0x000B);
        Header.CP_DeleteCharacter.Set(0x000D);
        Header.CP_ExceptionLog.Set(0x000F);
        //Header.AUTH_SECOND_PASSWORD.Set(0x0014);
        //Header.REACHED_LOGIN_SCREEN.Set(0x0014);
        Header.RSA_KEY.Set(0x0022);
    }

}
