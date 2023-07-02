package packet;

// BB後でまともに動作するバージョン
// v187.0は動作しませんでした
public class v188_0_CP {

    public static void Set() {
        ClientPacket.Header.CP_UserMigrateToITCRequest.Set(0x00AC); // OK
        // Login
        ClientPacket.Header.CP_CheckPassword.Set(0x0001);
        ClientPacket.Header.CP_WorldInfoRequest.Set(0x0003);
        ClientPacket.Header.CP_SelectWorld.Set(0x0004);
        ClientPacket.Header.CP_CheckUserLimit.Set(0x0005);
        ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
        ClientPacket.Header.CP_MigrateIn.Set(0x0007);
        ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
        ClientPacket.Header.CP_ViewAllChar.Set(0x000A);
        ClientPacket.Header.CP_CreateNewCharacter.Set(0x000B);
        ClientPacket.Header.CP_DeleteCharacter.Set(0x000D);
        ClientPacket.Header.CP_ExceptionLog.Set(0x000F); // OK
        ClientPacket.Header.CP_Check2ndPassword.Set(0x0012); // 2次パスワード、ログイン画面
        //Header.CP_Check2ndPassword_cancel.Set(0x00C3); // 2次パスワード、閉じる
        //Header.REACHED_LOGIN_SCREEN.Set(0x0017); // changed
        ClientPacket.Header.CP_CreateSecurityHandle.Set(0x0017);

        // ゲームサーバー
        ClientPacket.Header.CP_BEGIN_USER.Set(0x0019);
        {
            ClientPacket.Header.CP_UserTransferFieldRequest.Set(0x001A); // v188
            {
                ClientPacket.Header.CP_UserTransferChannelRequest.Set(ClientPacket.Header.CP_UserTransferFieldRequest.Get() + 1);
                ClientPacket.Header.CP_UserMigrateToCashShopRequest.Set(ClientPacket.Header.CP_UserTransferFieldRequest.Get() + 2);
                ClientPacket.Header.CP_UserMove.Set(ClientPacket.Header.CP_UserTransferFieldRequest.Get() + 3);
                ClientPacket.Header.CP_UserSitRequest.Set(ClientPacket.Header.CP_UserTransferFieldRequest.Get() + 4);
                ClientPacket.Header.CP_UserPortableChairSitRequest.Set(ClientPacket.Header.CP_UserTransferFieldRequest.Get() + 5);
            }
            ClientPacket.Header.CP_UserMeleeAttack.Set(0x0020); // v188
            {
                ClientPacket.Header.CP_UserShootAttack.Set(ClientPacket.Header.CP_UserMeleeAttack.Get() + 1);
                ClientPacket.Header.CP_UserMagicAttack.Set(ClientPacket.Header.CP_UserMeleeAttack.Get() + 2);
                ClientPacket.Header.CP_UserBodyAttack.Set(ClientPacket.Header.CP_UserMeleeAttack.Get() + 3);
                ClientPacket.Header.CP_UserHit.Set(ClientPacket.Header.CP_UserMeleeAttack.Get() + 4);
                ClientPacket.Header.CP_UserAttackUser.Set(ClientPacket.Header.CP_UserMeleeAttack.Get() + 5);
            }
            ClientPacket.Header.CP_UserChat.Set(0x0027); // v188
            {
                ClientPacket.Header.CP_UserADBoardClose.Set(ClientPacket.Header.CP_UserChat.Get() + 1);
                ClientPacket.Header.CP_UserEmotion.Set(ClientPacket.Header.CP_UserChat.Get() + 2);  // v188
                ClientPacket.Header.CP_UserActivateEffectItem.Set(ClientPacket.Header.CP_UserChat.Get() + 3);
            }
            //Header.WHEEL_OF_FORTUNE.Set(0x002D); // 不明
            // 0x0030
            //Header.CP_UserMonsterBookSetCover.Set(0x0031);

            // OK
            ClientPacket.Header.CP_UserSelectNpc.Set(0x0030); // v188

            ClientPacket.Header.CP_UserRemoteShopOpenRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 1);
            ClientPacket.Header.CP_UserScriptMessageAnswer.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 2);
            ClientPacket.Header.CP_UserShopRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 3);
            ClientPacket.Header.CP_UserTrunkRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 4);
            ClientPacket.Header.CP_UserEntrustedShopRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 5);
            ClientPacket.Header.CP_UserStoreBankRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 6);
            ClientPacket.Header.CP_UserParcelRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 7);
            ClientPacket.Header.CP_UserEffectLocal.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 8);

            //Header.CP_ShopScannerRequest.Set(0x003B);
            //Header.CP_ShopLinkRequest.Set(0x003C);
            //Header.CP_AdminShopRequest.Set(0x003D); // ServerPacket 0x0158, 0x0159から送信される, CP_AdminShopRequest?
            ClientPacket.Header.CP_UserSortItemRequest.Set(0x003C); // v188
            ClientPacket.Header.CP_UserGatherItemRequest.Set(0x003D);
            ClientPacket.Header.CP_UserChangeSlotPositionRequest.Set(0x003E); // v188

            ClientPacket.Header.CP_UserPortalScrollUseRequest.Set(0x004D);
            ClientPacket.Header.CP_UserUpgradeItemUseRequest.Set(0x004E); // v188
            ClientPacket.Header.CP_UserHyperUpgradeItemUseRequest.Set(0x004F);
            ClientPacket.Header.CP_UserItemOptionUpgradeItemUseRequest.Set(0x0050);
            ClientPacket.Header.CP_UserItemReleaseRequest.Set(0x0052); // v188
            ClientPacket.Header.CP_UserAbilityUpRequest.Set(0x0053);
            ClientPacket.Header.CP_UserAbilityMassUpRequest.Set(0x0054);
            ClientPacket.Header.CP_UserChangeStatRequest.Set(0x0055);
            ClientPacket.Header.CP_UserChangeStatRequestByItemOption.Set(0x0056);
            ClientPacket.Header.CP_UserSkillUpRequest.Set(0x0057); // v188
            ClientPacket.Header.CP_UserSkillUseRequest.Set(0x0058);
            ClientPacket.Header.CP_UserSkillCancelRequest.Set(0x0059);
            ClientPacket.Header.CP_UserSkillPrepareRequest.Set(0x005A);
            ClientPacket.Header.CP_UserDropMoneyRequest.Set(0x005B);
            /*
            ClientPacket.Header.CP_UserStatChangeItemUseRequest.Set(0x0041);
            ClientPacket.Header.CP_UserStatChangeItemCancelRequest.Set(0x0042);
            ClientPacket.Header.CP_UserStatChangeByPortableChairRequest.Set(0x0043);
            ClientPacket.Header.CP_UserMobSummonItemUseRequest.Set(0x0044);
            ClientPacket.Header.CP_UserPetFoodItemUseRequest.Set(0x0045);
            ClientPacket.Header.CP_UserTamingMobFoodItemUseRequest.Set(0x0046);
            ClientPacket.Header.CP_UserScriptItemUseRequest.Set(0x0047);
            ClientPacket.Header.CP_UserConsumeCashItemUseRequest.Set(0x0048);
            ClientPacket.Header.CP_UserDestroyPetItemRequest.Set(0x0049);
            ClientPacket.Header.CP_UserBridleItemUseRequest.Set(0x004A);
            ClientPacket.Header.CP_UserSkillLearnItemUseRequest.Set(0x004B);
            ClientPacket.Header.CP_UserShopScannerItemUseRequest.Set(0x004C);
            ClientPacket.Header.CP_UserMapTransferItemUseRequest.Set(0x004D);
            ClientPacket.Header.CP_UserPortalScrollUseRequest.Set(0x004E);
            ClientPacket.Header.CP_UserUpgradeItemUseRequest.Set(0x004F);
            ClientPacket.Header.CP_UserHyperUpgradeItemUseRequest.Set(0x0050);
            ClientPacket.Header.CP_UserItemOptionUpgradeItemUseRequest.Set(0x0051);
            ClientPacket.Header.CP_UserItemReleaseRequest.Set(0x0052);
            ClientPacket.Header.CP_UserAbilityUpRequest.Set(0x0053);
            ClientPacket.Header.CP_UserAbilityMassUpRequest.Set(0x0054);
            ClientPacket.Header.CP_UserChangeStatRequest.Set(0x0055);
             */
            ClientPacket.Header.CP_UserDropMoneyRequest.Set(0x005B); // v188
            ClientPacket.Header.CP_UserGivePopularityRequest.Set(0x005C);
            ClientPacket.Header.CP_UserPartyRequest.Set(0x005D);
            ClientPacket.Header.CP_UserCharacterInfoRequest.Set(0x005E); // v188
            ClientPacket.Header.CP_UserActivatePetRequest.Set(0x005F);
            ClientPacket.Header.CP_UserTemporaryStatUpdateRequest.Set(0x0060);
            // 0x0061 ???, CP_UserRegisterPetAutoBuffRequest
            ClientPacket.Header.CP_UserPortalScriptRequest.Set(0x0062); // v188
            ClientPacket.Header.CP_UserPortalTeleportRequest.Set(0x0063); // v188
            ClientPacket.Header.CP_UserMapTransferRequest.Set(0x0064); // v188
            ClientPacket.Header.CP_UserAntiMacroItemUseRequest.Set(0x0065);
            ClientPacket.Header.CP_UserAntiMacroSkillUseRequest.Set(0x0066);
            ClientPacket.Header.CP_UserAntiMacroQuestionResult.Set(0x0067);
            ClientPacket.Header.CP_UserClaimRequest.Set(0x0068);
            ClientPacket.Header.CP_UserQuestRequest.Set(0x0069); // v188
            ClientPacket.Header.CP_UserCalcDamageStatSetRequest.Set(0x006A);
            ClientPacket.Header.CP_UserThrowGrenade.Set(0x006B);
            ClientPacket.Header.CP_UserMacroSysDataModified.Set(0x006C); // v188
            /*
            ClientPacket.Header.CP_UserSelectNpcItemUseRequest.Set(0x006D);
            ClientPacket.Header.CP_UserLotteryItemUseRequest.Set(0x006E);
            ClientPacket.Header.CP_UserItemMakeRequest.Set(0x006F);
            ClientPacket.Header.CP_UserUseGachaponBoxRequest.Set(0x0070);
            // 0x0071, CP_UserUseGachaponRemoteRequest or CP_UserUseWaterOfLife
            ClientPacket.Header.CP_UserRepairDurabilityAll.Set(0x0072);
            ClientPacket.Header.CP_UserRepairDurability.Set(0x0073);
            ClientPacket.Header.CP_UserFollowCharacterRequest.Set(0x0074);
            ClientPacket.Header.CP_UserFollowCharacterWithdraw.Set(0x0076);
            ClientPacket.Header.CP_UserExpUpItemUseRequest.Set(0x0077); // OK
            //Header.CP_UserQuestRecordSetState.Set(0x0074);
            //Header.CP_UserClientTimerEndRequest.Set(0x0075);
            //Header.SOLOMON.Set(0x0076);
            ClientPacket.Header.USE_TREASUER_CHEST.Set(0x007A); // 不明
            ClientPacket.Header.CP_BroadcastMsg.Set(0x007B);
            ClientPacket.Header.CP_GroupMessage.Set(0x007C);
            ClientPacket.Header.CP_Whisper.Set(0x007D);
            ClientPacket.Header.CP_Messenger.Set(0x007E);
            ClientPacket.Header.CP_MiniRoom.Set(0x007F);
            ClientPacket.Header.CP_PartyRequest.Set(0x0080);
            ClientPacket.Header.CP_PartyResult.Set(0x0081);
            ClientPacket.Header.CP_ExpeditionRequest.Set(0x0082);
            ClientPacket.Header.CP_PartyAdverRequest.Set(0x0083);
            ClientPacket.Header.CP_GuildRequest.Set(0x0084);
            ClientPacket.Header.CP_GuildResult.Set(0x0085);
            ClientPacket.Header.CP_Admin.Set(0x0086);
            ClientPacket.Header.CP_Log.Set(0x0087);
            ClientPacket.Header.CP_FriendRequest.Set(0x0088);
            ClientPacket.Header.CP_MemoRequest.Set(0x0089);
            ClientPacket.Header.CP_MemoFlagRequest.Set(0x008A);
            ClientPacket.Header.CP_EnterTownPortalRequest.Set(0x008B);
            ClientPacket.Header.CP_EnterOpenGateRequest.Set(0x008C);
            ClientPacket.Header.CP_SlideRequest.Set(0x008D);
            ClientPacket.Header.CP_FuncKeyMappedModified.Set(0x008E);
            ClientPacket.Header.CP_RPSGame.Set(0x008F);
            //Header.CP_MarriageRequest.Set(0x0090);
            //Header.CP_WeddingWishListRequest.Set(0x0091);
            // 0x0092, CP_WeddingProgress
            // 0x0093, CP_GuestBless
            // 0x0094, CP_BoobyTrapAlert or CP_StalkBegin=
            ClientPacket.Header.CP_AllianceRequest.Set(0x0095);
            ClientPacket.Header.CP_AllianceResult.Set(0x0096);
            ClientPacket.Header.CP_FamilyChartRequest.Set(0x0097);
            ClientPacket.Header.CP_FamilyInfoRequest.Set(0x0098);
            ClientPacket.Header.CP_FamilyRegisterJunior.Set(0x0099);
            ClientPacket.Header.CP_FamilyUnregisterJunior.Set(0x009A);
            ClientPacket.Header.CP_FamilyUnregisterParent.Set(0x009B);
            ClientPacket.Header.CP_FamilyJoinResult.Set(0x009C);
            ClientPacket.Header.CP_FamilyUsePrivilege.Set(0x009D);
            ClientPacket.Header.CP_FamilySetPrecept.Set(0x009E);
            ClientPacket.Header.CP_FamilySummonResult.Set(0x009F);
            ClientPacket.Header.CYGNUS_SUMMON.Set(0x00A0);
            ClientPacket.Header.CP_RequestIncCombo.Set(0x00A1);
            // 0x00A2 ServerPacket 0x0111から送信される
            // 0x00A3
            ClientPacket.Header.CP_GuildBBS.Set(0x00A4);
            // 0x00A5
            // 0x00A6
            // 0x00A7
            // 0x00A8
            // 0x00A9
            ClientPacket.Header.CP_UserMigrateToITCRequest.Set(0x00AA);
            ClientPacket.Header.CP_CashGachaponOpenRequest.Set(0x00AB); // CP_CashItemGachaponRequestかも
            // 0x00AC

            // ペット
            ClientPacket.Header.CP_BEGIN_PET.Set(0x00AD);
            {
                ClientPacket.Header.CP_PetMove.Set(0x00AE);
                ClientPacket.Header.CP_PetAction.Set(0x00AF);
                ClientPacket.Header.CP_PetInteractionRequest.Set(0x00B0);
                ClientPacket.Header.CP_PetDropPickUpRequest.Set(0x00B1);
                ClientPacket.Header.CP_PetStatChangeItemUseRequest.Set(0x00B2);
                ClientPacket.Header.CP_PetUpdateExceptionListRequest.Set(0x00B3);
            }
            ClientPacket.Header.CP_END_PET.Set(0x00B4);
            // 召喚
            ClientPacket.Header.CP_BEGIN_SUMMONED.Set(0x00B5);
            {
                ClientPacket.Header.CP_SummonedMove.Set(0x00B6);
                ClientPacket.Header.CP_SummonedAttack.Set(0x00B7);
                ClientPacket.Header.CP_SummonedHit.Set(0x00B8);
                ClientPacket.Header.CP_SummonedSkill.Set(0x00B9);
                ClientPacket.Header.CP_Remove.Set(0x00BA);
            }
            ClientPacket.Header.CP_END_SUMMONED.Set(0x00BB);
            // エヴァンのドラゴン
            ClientPacket.Header.CP_BEGIN_DRAGON.Set(0x00BC);
            {
                ClientPacket.Header.CP_DragonMove.Set(0x00BD);
            }
            ClientPacket.Header.CP_END_DRAGON.Set(0x00BE);

            ClientPacket.Header.CP_QuickslotKeyMappedModified.Set(0x00BF); // キー設定からクイックスロットを編集
            // 0x00C0
            // 0x00C1
            ClientPacket.Header.CP_UserPamsSongUseRequest.Set(0x00C2); // ServerPacket 0x007Cから送信される, ファムの歌を利用する処理 @00C2 [00or01]が送信される01は使用フラグ
        }
        ClientPacket.Header.CP_END_USER.Set(0x00C3);
        // マップ上の処理
        ClientPacket.Header.CP_BEGIN_FIELD.Set(0x00C4);
        {
            // マップ上で動く物
            ClientPacket.Header.CP_BEGIN_LIFEPOOL.Set(0x00C5);
            {
                // Mob
                ClientPacket.Header.CP_BEGIN_MOB.Set(0x00C6);
                {
                    ClientPacket.Header.CP_MobMove.Set(0x00C7);
                    ClientPacket.Header.CP_MobApplyCtrl.Set(0x00C8);
                    ClientPacket.Header.CP_MobDropPickUpRequest.Set(0x00C9);
                    ClientPacket.Header.CP_MobHitByObstacle.Set(0x00CA);
                    ClientPacket.Header.CP_MobHitByMob.Set(0x00CB);
                    ClientPacket.Header.CP_MobSelfDestruct.Set(0x00CC);
                    ClientPacket.Header.CP_MobAttackMob.Set(0x00CD);
                    ClientPacket.Header.CP_MobSkillDelayEnd.Set(0x00CE);
                    ClientPacket.Header.CP_MobTimeBombEnd.Set(0x00CF);
                    ClientPacket.Header.CP_MobEscortCollision.Set(0x00D0);
                    ClientPacket.Header.CP_MobRequestEscortInfo.Set(0x00D1);
                    ClientPacket.Header.CP_MobEscortStopEndRequest.Set(0x00D2);
                }
                ClientPacket.Header.CP_END_MOB.Set(0x00D3);
                // NPC
                ClientPacket.Header.CP_BEGIN_NPC.Set(0x00D4);
                {
                    ClientPacket.Header.CP_NpcMove.Set(0x00D5);
                    ClientPacket.Header.CP_NpcSpecialAction.Set(0x00D6);
                }
                ClientPacket.Header.CP_END_NPC.Set(0x00D7);
            }
            ClientPacket.Header.CP_END_LIFEPOOL.Set(0x00D8);
             */
            // アイテム回収
            ClientPacket.Header.CP_BEGIN_DROPPOOL.Set(0x00DF);
            {
                ClientPacket.Header.CP_DropPickUpRequest.Set(0x00E0); // v188
            }
            ClientPacket.Header.CP_END_DROPPOOL.Set(0x00E1);
            /*
            // 設置物
            ClientPacket.Header.CP_BEGIN_REACTORPOOL.Set(0x00DC);
            {
                ClientPacket.Header.CP_ReactorHit.Set(0x00DD);
                ClientPacket.Header.CP_ReactorTouch.Set(0x00DE);
                ClientPacket.Header.CP_RequireFieldObstacleStatus.Set(0x00DF);
            }
            ClientPacket.Header.CP_END_REACTORPOOL.Set(0x00E0);
            // イベント
            ClientPacket.Header.CP_BEGIN_EVENT_FIELD.Set(0x00E1);
            {
                ClientPacket.Header.CP_EventStart.Set(0x00E2);
                ClientPacket.Header.CP_SnowBallHit.Set(0x00E3);
                ClientPacket.Header.CP_SnowBallTouch.Set(0x00E4);
                ClientPacket.Header.LEFT_KNOCK_BACK.Set(0x00E5); // ???
                ClientPacket.Header.CP_CoconutHit.Set(0x00E6);
            }
            ClientPacket.Header.CP_END_EVENT_FIELD.Set(0x00E7);
            // モンスターカーニバル
            ClientPacket.Header.CP_BEGIN_MONSTER_CARNIVAL_FIELD.Set(0x00E8);
            {
                ClientPacket.Header.CP_MCarnivalRequest.Set(0x00E9);
            }
            ClientPacket.Header.CP_END_MONSTER_CARNIVAL_FIELD.Set(0x00EA);
            // 0x00EB
            // ステーションの船
            ClientPacket.Header.CP_CONTISTATE.Set(0x00EC);
            // グループ
            ClientPacket.Header.CP_BEGIN_PARTY_MATCH.Set(0x00ED);
            {
                ClientPacket.Header.CP_INVITE_PARTY_MATCH.Set(0x00EE);
                ClientPacket.Header.CP_CANCEL_INVITE_PARTY_MATCH.Set(0x00EF);
            }
            ClientPacket.Header.CP_END_PARTY_MATCH.Set(0x00F0);
        }
        ClientPacket.Header.CP_END_FIELD.Set(0x00F1);
        }
             */
        }

        ClientPacket.Header.CP_MobMove.Set(0x00CD);
        ClientPacket.Header.CP_MobApplyCtrl.Set(0x00CE);
    }
}
