// クライアント側から送信されるパケットのヘッダの定義
package packet;

import config.ServerConfig;

public class ClientPacket {

    private byte[] packet;
    private int decoded;

    // MapleのInPacketのDecodeのように送信されたパケットを再度Decodeする
    public ClientPacket(byte[] b) {
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

    public String GetOpcodeName() {
        if (packet.length < 2) {
            return Header.UNKNOWN.toString();
        }

        short header = (short) (((short) packet[0] & 0xFF) | ((short) packet[1] & 0xFF << 8));
        return ToHeader(header).toString();
    }

    public Header GetOpcode() {
        if (packet.length < 2) {
            return Header.UNKNOWN;
        }

        short header = (short) (((short) packet[0] & 0xFF) | ((short) packet[1] & 0xFF << 8));
        return ToHeader(header);
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
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            buffer[i] = Decode1();
        }

        String s = new String(buffer, ServerConfig.utf8 ? ServerConfig.codepage_utf8 : ServerConfig.codepage_ascii);
        byte[] bytes = s.getBytes(ServerConfig.utf8 ? ServerConfig.codepage_utf8 : ServerConfig.codepage_ascii);
        String conv_str = new String(bytes, ServerConfig.utf8 ? ServerConfig.codepage_utf8 : ServerConfig.codepage_ascii);

        return conv_str;
    }

    public enum Header {
        // 独自仕様
        CP_CUSTOM_,
        CP_CUSTOM_WZ_HASH,
        CP_CUSTOM_MEMORY_SCAN,
        // JMS v131.0
        CP_T_UpdateGameGuard,
        // Names from v95 PDB
        // ログインサーバー
        CP_BEGIN_SOCKET,
        CP_CheckPassword, // LOGIN_PASSWORD
        CP_Check2ndPassword, // v188 test
        CP_Check2ndPassword_cancel, // v188 test
        //CP_GuestIDLogin,
        CP_AccountInfoRequest,
        CP_WorldInfoRequest, // SERVERLIST_REQUEST
        CP_SelectWorld, // CHARLIST_REQUEST
        CP_CheckUserLimit,
        //CP_ConfirmEULA,
        //CP_SetGender,
        CP_CheckPinCode,
        //CP_UpdatePinCode,
        CP_WorldRequest,
        CP_LogoutWorld,
        CP_ViewAllChar,
        //CP_SelectCharacterByVAC,
        //CP_VACFlagSet,
        //CP_CheckNameChangePossible,
        //CP_RegisterNewCharacter,
        //CP_CheckTransferWorldPossible,
        CP_SelectCharacter, // CHAR_SELECT
        CP_MigrateIn, // PLAYER_LOGGEDIN
        CP_CheckDuplicatedID, // CHECK_CHAR_NAME
        CP_CreateNewCharacter, // CREATE_CHAR
        CP_CreateNewCharacterInCS,
        CP_DeleteCharacter, // DELETE_CHAR
        CP_AliveAck,
        CP_ExceptionLog,
        CP_SecurityPacket,
        //CP_EnableSPWRequest,
        //CP_CheckSPWRequest,
        //CP_EnableSPWRequestByACV,
        //CP_CheckSPWRequestByACV,
        //CP_CheckOTPRequest,
        //CP_CheckDeleteCharacterOTP,
        CP_CreateSecurityHandle,
        //CP_SSOErrorLog,
        //CP_ClientDumpLog,
        //CP_CheckExtraCharInfo,
        //CP_CreateNewCharacter_Ex,
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
        //CP_UserMovingShootAttackPrepare,
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
        //CP_UserSkillResetItemUseRequest,
        CP_UserShopScannerItemUseRequest, // USE_OWL_MINERVA
        CP_UserMapTransferItemUseRequest, // USE_TELE_ROCK
        CP_UserPortalScrollUseRequest, // USE_RETURN_SCROLL
        CP_UserUpgradeItemUseRequest, // USE_UPGRADE_SCROLL
        CP_UserHyperUpgradeItemUseRequest,
        CP_UserItemOptionUpgradeItemUseRequest,
        //CP_UserUIOpenItemUseRequest,
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
        //CP_UserSueCharacterRequest,
        CP_UserUseGachaponBoxRequest,
        //CP_UserUseGachaponRemoteRequest,
        //CP_UserUseWaterOfLife,
        CP_UserRepairDurabilityAll,
        CP_UserRepairDurability,
        CP_UserQuestRecordSetState,
        CP_UserClientTimerEndRequest,
        CP_UserFollowCharacterRequest,
        CP_UserFollowCharacterWithdraw,
        //CP_UserSelectPQReward,
        //CP_UserRequestPQReward,
        CP_SetPassenserResult,
        CP_BroadcastMsg,
        CP_GroupMessage, // PARTYCHAT
        CP_Whisper, // WHISPER
        //CP_CoupleMessage,
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
        //CP_QuestGuideRequest,
        //CP_UserRepeatEffectRemove,
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
        CP_DropPickUpRequest, // ITEM_PICKUP
        CP_END_DROPPOOL,
        CP_BEGIN_REACTORPOOL,
        CP_ReactorHit, // DAMAGE_REACTOR
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
        //CP_RequestFootHoldInfo,
        //CP_FootHoldInfo,
        CP_END_FIELD,
        // ポイントショップ
        CP_BEGIN_CASHSHOP,
        CP_CashShopChargeParamRequest,
        CP_CashShopQueryCashRequest,
        CP_CashShopCashItemRequest,
        CP_CashShopCheckCouponRequest,
        CP_CashShopGiftMateInfoRequest,
        CP_END_CASHSHOP,
        //CP_CheckSSN2OnCreateNewCharacter,
        //CP_CheckSPWOnCreateNewCharacter,
        //CP_FirstSSNOnCreateNewCharacter,
        // 多分絵具とかアイテム集める系のUI
        CP_BEGIN_RAISE,
        CP_RaiseRefesh,
        CP_RaiseUIState,
        CP_RaiseIncExp,
        CP_RaiseAddPiece,
        CP_END_RAISE,
        //CP_SendMateMail,
        //CP_RequestGuildBoardAuthKey,
        //CP_RequestConsultAuthKey,
        //CP_RequestClassCompetitionAuthKey,
        //CP_RequestWebBoardAuthKey,
        CP_BEGIN_ITEMUPGRADE,
        // ビシャスのハンマー
        CP_GoldHammerRequest,
        //CP_GoldHammerComplete,
        //CP_ItemUpgradeComplete,
        CP_END_ITEMUPGRADE,
        CP_BEGIN_BATTLERECORD,
        CP_BATTLERECORD_ONOFF_REQUEST,
        CP_END_BATTLERECORD,
        //CP_BEGIN_MAPLETV,
        CP_MapleTVSendMessageRequest,
        //CP_MapleTVUpdateViewCount,
        //CP_END_MAPLETV,
        // MTS
        CP_BEGIN_ITC,
        CP_ITCChargeParamRequest,
        CP_ITCQueryCashRequest,
        CP_ITCItemRequest,
        CP_END_ITC,
        //CP_BEGIN_CHARACTERSALE,
        //CP_CheckDuplicatedIDInCS,
        //CP_END_CHARACTERSALE,
        CP_LogoutGiftSelect,
        CP_NO,
        // ヘッダに対応する処理の名前を定義
        UNKNOWN_BEGIN,
        UNKNOWN,
        REACHED_LOGIN_SCREEN, // 名称不明, ログイン画面に到達
        WHEEL_OF_FORTUNE, // 不明
        GM_COMMAND_MAPLETV, // Super Megaphone Exploitのパケット
        SOLOMON, // 名称不明
        USE_TREASUER_CHEST, // 名称不明
        CYGNUS_SUMMON, // 名称不明
        LEFT_KNOCK_BACK, // イベント関連?
        RECOMMENDED_AVATAR, // おすすめアバター
        UPDATE_QUEST, // 不明
        QUEST_ITEM, // 不明
        USE_ITEM_QUEST, // 多分Quest Value Addition Exploitのパケット
        BEANS_OPERATION, // PACHINKO_GAME, パチンコ
        BEANS_UPDATE, // PACHINKO_UPDATE, パチンコ
        UNKNOWN_END;

        // 定義値の変更や取得
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
        Header.CP_CheckPassword.Set(0x0001);
        Header.CP_WorldInfoRequest.Set(0x0003);
        Header.CP_SelectWorld.Set(0x0004);
        Header.CP_CheckUserLimit.Set(0x0005);
        Header.CP_SelectCharacter.Set(0x0006);
        Header.CP_MigrateIn.Set(0x0007);
        Header.CP_CheckDuplicatedID.Set(0x0008);
        Header.CP_CreateNewCharacter.Set(0x000B);
        Header.CP_DeleteCharacter.Set(0x000C);
        Header.REACHED_LOGIN_SCREEN.Set(0x0017);
        Header.CP_CreateSecurityHandle.Set(0x0019);

        Header.CP_BEGIN_USER.Set(0x001B);
        {
            Header.CP_UserTransferFieldRequest.Set(0x001C);
            Header.CP_UserTransferChannelRequest.Set(0x001D);
            Header.CP_UserMigrateToCashShopRequest.Set(0x001E);
            Header.CP_UserMove.Set(0x001F);
            Header.CP_UserChat.Set(0x0027);
        }
        Header.CP_UserMigrateToITCRequest.Set(0x0099);

        Header.CP_UserPortalScriptRequest.Set(0x005A);
        Header.CP_UserPortalTeleportRequest.Set(0x005B);
    }

    public static void SetForJMSv184() {
        // ログインサーバー
        Header.CP_BEGIN_SOCKET.Set(0x0000);
        {
            Header.CP_CheckPassword.Set(0x0001);
            Header.CP_AccountInfoRequest.Set(0x0002);
            Header.CP_WorldInfoRequest.Set(0x0003);
            Header.CP_SelectWorld.Set(0x0004);
            Header.CP_CheckUserLimit.Set(0x0005);
            Header.CP_SelectCharacter.Set(0x0006);
            Header.CP_MigrateIn.Set(0x0007);
            Header.CP_CheckDuplicatedID.Set(0x0008);
            Header.CP_ViewAllChar.Set(0x000A);
            Header.CP_CreateNewCharacter.Set(0x000B);
            Header.CP_CreateNewCharacterInCS.Set(0x000C);
            Header.CP_DeleteCharacter.Set(0x000D);
            Header.CP_AliveAck.Set(0x000E);
            Header.CP_ExceptionLog.Set(0x000F);
            Header.CP_SecurityPacket.Set(0x0010);
            Header.CP_CheckPinCode.Set(0x0014);
            Header.REACHED_LOGIN_SCREEN.Set(0x0017);
            Header.CP_CreateSecurityHandle.Set(0x0019);
        }
        Header.CP_END_SOCKET.Set(0x001B);
        Header.CP_UserChat.Set(0x0028);
        Header.CP_UserTransferFieldRequest.Set(0x001C);
        Header.CP_UserMove.Set(0x001F);
        Header.CP_UserPortalScriptRequest.Set(0x005E);
        Header.CP_UserPortalTeleportRequest.Set(0x005F);
    }

    // チート対策
    public static void SetCustomHeader() {
        // WZファイルのハッシュ値の送信
        Header.CP_CUSTOM_WZ_HASH.Set(0x77AA);
        // メモリスキャン
        Header.CP_CUSTOM_MEMORY_SCAN.Set(0x77BB);
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
        Header.CP_CreateSecurityHandle.Set(0x001A);
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
        Header.CP_UserTemporaryStatUpdateRequest.Set(0x0060 + 3);
        Header.CP_UserPortalScriptRequest.Set(0x0062 + 3);
        Header.CP_UserPortalTeleportRequest.Set(0x0063 + 3);
        Header.CP_UserMapTransferRequest.Set(0x0064 + 3);
        Header.CP_UserQuestRequest.Set(0x0069 + 3);
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
        Header.CP_UserMigrateToITCRequest.Set(0x00AF);
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
        Header.CP_CheckPassword.Set(0x0015); // OK
        Header.CP_Check2ndPassword.Set(0x0026); // OK
        Header.CP_WorldInfoRequest.Set(0x0017); // OK
        Header.CP_CheckDuplicatedID.Set(0x001C); // OK
        Header.CP_CreateNewCharacter.Set(0x001E); // OK

        Header.CP_SelectWorld.Set(0x0018);
        Header.CP_CheckUserLimit.Set(0x0005);
        Header.CP_SelectCharacter.Set(0x0006);
        Header.CP_MigrateIn.Set(0x0007);
        Header.CP_DeleteCharacter.Set(0x000D);

        //Header.AUTH_SECOND_PASSWORD.Set(0x0014);
        Header.REACHED_LOGIN_SCREEN.Set(0x002B); // or 0x0014
    }

}
