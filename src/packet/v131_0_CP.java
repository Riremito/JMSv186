package packet;

public class v131_0_CP {

    public static void Set() {
        // ログインサーバー
        ClientPacket.Header.CP_BEGIN_SOCKET.Set(0x0000);
        {
            ClientPacket.Header.CP_CheckPassword.Set(0x0001);
            // 0x0002 something
            ClientPacket.Header.CP_WorldInfoRequest.Set(0x0003);
            ClientPacket.Header.CP_SelectWorld.Set(0x0004);
            ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
            ClientPacket.Header.CP_MigrateIn.Set(0x0007);
            ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
            ClientPacket.Header.CP_CreateNewCharacter.Set(0x0009);
            ClientPacket.Header.CP_DeleteCharacter.Set(0x000A);
            ClientPacket.Header.CP_T_UpdateGameGuard.Set(0x000F); // GameGuardのUpdateが必要かどうか
        }
        ClientPacket.Header.CP_END_SOCKET.Set(0x001B);

        // ゲームサーバー
        ClientPacket.Header.CP_BEGIN_USER.Set(0x001C);
        {
            ClientPacket.Header.CP_UserTransferFieldRequest.Set(0x0015);
            ClientPacket.Header.CP_UserTransferChannelRequest.Set(0x0016);
            ClientPacket.Header.CP_UserMigrateToCashShopRequest.Set(0x0017);
            ClientPacket.Header.CP_UserMove.Set(0x0018);
            ClientPacket.Header.CP_UserSitRequest.Set(0x0019);
            ClientPacket.Header.CP_UserPortableChairSitRequest.Set(0x001A);
            ClientPacket.Header.CP_UserMeleeAttack.Set(0x001B);
            ClientPacket.Header.CP_UserShootAttack.Set(0x001C);
            ClientPacket.Header.CP_UserMagicAttack.Set(0x001D);
            ClientPacket.Header.CP_UserHit.Set(0x001E);
            ClientPacket.Header.CP_UserChat.Set(0x001F);
            ClientPacket.Header.CP_UserEmotion.Set(0x0020);
            ClientPacket.Header.CP_UserActivateEffectItem.Set(0x0021);
            // 0x0030
            //ClientPacket.Header.CP_UserMonsterBookSetCover.Set(0x0031);
            ClientPacket.Header.CP_UserSelectNpc.Set(0x0025);
            ClientPacket.Header.CP_UserScriptMessageAnswer.Set(0x0026);
            ClientPacket.Header.CP_UserShopRequest.Set(0x0027);
            ClientPacket.Header.CP_UserTrunkRequest.Set(0x0028);
            //ClientPacket.Header.CP_UserEntrustedShopRequest.Set(0x0037);
            //ClientPacket.Header.CP_UserStoreBankRequest.Set(0x0038);
            //ClientPacket.Header.CP_UserParcelRequest.Set(0x0039);
            //ClientPacket.Header.CP_UserEffectLocal.Set(0x003A);
            //ClientPacket.Header.CP_ShopScannerRequest.Set(0x003B);
            //ClientPacket.Header.CP_ShopLinkRequest.Set(0x003C);
            ClientPacket.Header.CP_UserChangeSlotPositionRequest.Set(0x002E);
            ClientPacket.Header.CP_UserStatChangeItemUseRequest.Set(0x002F);
            ClientPacket.Header.CP_UserStatChangeItemCancelRequest.Set(0x0030);
            //ClientPacket.Header.CP_UserStatChangeByPortableChairRequest.Set(0x0043);
            ClientPacket.Header.CP_UserMobSummonItemUseRequest.Set(0x0032);
            ClientPacket.Header.CP_UserPetFoodItemUseRequest.Set(0x0033);
            ClientPacket.Header.CP_UserTamingMobFoodItemUseRequest.Set(0x0034);
            ClientPacket.Header.CP_UserConsumeCashItemUseRequest.Set(0x0035);
            ClientPacket.Header.CP_UserBridleItemUseRequest.Set(0x0036);
            ClientPacket.Header.CP_UserSkillLearnItemUseRequest.Set(0x0037);
            ClientPacket.Header.CP_UserShopScannerItemUseRequest.Set(0x0038);
            ClientPacket.Header.CP_UserMapTransferItemUseRequest.Set(0x0039);
            ClientPacket.Header.CP_UserPortalScrollUseRequest.Set(0x003A);
            ClientPacket.Header.CP_UserUpgradeItemUseRequest.Set(0x003B);
            ClientPacket.Header.CP_UserAbilityUpRequest.Set(0x003C);
            ClientPacket.Header.CP_UserChangeStatRequest.Set(0x003D);
            ClientPacket.Header.CP_UserSkillUpRequest.Set(0x003E);

            ClientPacket.Header.CP_UserSkillUseRequest.Set(0x003F);
            ClientPacket.Header.CP_UserSkillCancelRequest.Set(0x0040);
            ClientPacket.Header.CP_UserSkillPrepareRequest.Set(0x0041);

            ClientPacket.Header.CP_UserDropMoneyRequest.Set(0x0042);
            ClientPacket.Header.CP_UserGivePopularityRequest.Set(0x0043);
            //ClientPacket.Header.CP_UserPartyRequest.Set(0x005D);
            ClientPacket.Header.CP_UserCharacterInfoRequest.Set(0x0045);
            ClientPacket.Header.CP_UserActivatePetRequest.Set(0x0046);
            ClientPacket.Header.CP_UserTemporaryStatUpdateRequest.Set(0x0047);
            ClientPacket.Header.CP_UserPortalScriptRequest.Set(0x0048);
            ClientPacket.Header.CP_UserPortalTeleportRequest.Set(0x0049);
            ClientPacket.Header.CP_UserMapTransferRequest.Set(0x004A);
            // ?
            ClientPacket.Header.CP_UserQuestRequest.Set(0x004E);
            //ClientPacket.Header.CP_UserExpUpItemUseRequest.Set(0x0077);
            //ClientPacket.Header.CP_BroadcastMsg.Set(0x007B);
            ClientPacket.Header.CP_GroupMessage.Set(0x0052);
            ClientPacket.Header.CP_Whisper.Set(0x0053);
            ClientPacket.Header.CP_Messenger.Set(0x0054);
            ClientPacket.Header.CP_MiniRoom.Set(0x0055);
            ClientPacket.Header.CP_PartyRequest.Set(0x0056);
            ClientPacket.Header.CP_PartyResult.Set(0x0057);
            ClientPacket.Header.CP_GuildRequest.Set(0x0058);
            ClientPacket.Header.CP_GuildResult.Set(0x0059);
            //ClientPacket.Header.CP_Admin.Set(0x0086);
            //ClientPacket.Header.CP_Log.Set(0x0087);
            ClientPacket.Header.CP_FriendRequest.Set(0x005C);
            ClientPacket.Header.CP_MemoRequest.Set(0x005D);
            ClientPacket.Header.CP_MemoFlagRequest.Set(0x005E);
            ClientPacket.Header.CP_EnterTownPortalRequest.Set(0x005F);
            //ClientPacket.Header.CP_SlideRequest.Set(0x008D);
            ClientPacket.Header.CP_FuncKeyMappedModified.Set(0x0061);
            ClientPacket.Header.CP_RPSGame.Set(0x0062);
            //ClientPacket.Header.CP_MarriageRequest.Set(0x0090);
            //ClientPacket.Header.CP_WeddingWishListRequest.Set(0x0091);
            //ClientPacket.Header.CP_GuildBBS.Set(0x00A4);
            //ClientPacket.Header.CP_UserMigrateToITCRequest.Set(0x00AA);
            //ClientPacket.Header.CP_CashGachaponOpenRequest.Set(0x00AB); // CP_CashItemGachaponRequestかも

            // ペット
            ClientPacket.Header.CP_BEGIN_PET.Set(0);
            {
                ClientPacket.Header.CP_PetMove.Set(0x006D);
                ClientPacket.Header.CP_PetAction.Set(0x006E);
                ClientPacket.Header.CP_PetInteractionRequest.Set(0x006F);
                ClientPacket.Header.CP_PetDropPickUpRequest.Set(0x0070);
                ClientPacket.Header.CP_PetStatChangeItemUseRequest.Set(0x0071);
                ClientPacket.Header.CP_PetUpdateExceptionListRequest.Set(0x0072);
            }
            ClientPacket.Header.CP_END_PET.Set(0);
            // 召喚
            ClientPacket.Header.CP_BEGIN_SUMMONED.Set(0);
            {
                ClientPacket.Header.CP_SummonedMove.Set(0x0074);
                ClientPacket.Header.CP_SummonedAttack.Set(0x0075);
                ClientPacket.Header.CP_SummonedHit.Set(0x0076);
                ClientPacket.Header.CP_SummonedSkill.Set(0x0077);
                ClientPacket.Header.CP_Remove.Set(0x0078);
            }
            ClientPacket.Header.CP_END_SUMMONED.Set(0);
        }

        ClientPacket.Header.CP_END_USER.Set(0);
        // マップ上の処理
        ClientPacket.Header.CP_BEGIN_FIELD.Set(0);
        {
            // マップ上で動く物
            ClientPacket.Header.CP_BEGIN_LIFEPOOL.Set(0);
            {
                // Mob
                ClientPacket.Header.CP_BEGIN_MOB.Set(0);
                {
                    ClientPacket.Header.CP_MobMove.Set(0x007D);
                    ClientPacket.Header.CP_MobApplyCtrl.Set(0x007E);
                    //ClientPacket.Header.CP_MobDropPickUpRequest.Set(0x00C9);
                    //ClientPacket.Header.CP_MobHitByObstacle.Set(0x00CA);
                    //ClientPacket.Header.CP_MobHitByMob.Set(0x00CB);
                    //ClientPacket.Header.CP_MobSelfDestruct.Set(0x00CC);
                    //ClientPacket.Header.CP_MobAttackMob.Set(0x00CD);
                    //ClientPacket.Header.CP_MobSkillDelayEnd.Set(0x00CE);
                    ClientPacket.Header.CP_MobTimeBombEnd.Set(0x0082);
                }
                ClientPacket.Header.CP_END_MOB.Set(0);
                // NPC
                ClientPacket.Header.CP_BEGIN_NPC.Set(0);
                {
                    //ClientPacket.Header.CP_NpcMove.Set(0x00D5);
                    ClientPacket.Header.CP_NpcSpecialAction.Set(0x0085);
                }
                ClientPacket.Header.CP_END_NPC.Set(0);
            }
            ClientPacket.Header.CP_END_LIFEPOOL.Set(0);

            // アイテム回収
            ClientPacket.Header.CP_BEGIN_DROPPOOL.Set(0);
            {
                ClientPacket.Header.CP_DropPickUpRequest.Set(0x0089);
            }
            ClientPacket.Header.CP_END_DROPPOOL.Set(0);

            // 設置物
            ClientPacket.Header.CP_BEGIN_REACTORPOOL.Set(0);
            {
                ClientPacket.Header.CP_ReactorHit.Set(0x008C);
                //ClientPacket.Header.CP_ReactorTouch.Set(0x00DE);
                //ClientPacket.Header.CP_RequireFieldObstacleStatus.Set(0x00DF);
            }

            ClientPacket.Header.CP_END_REACTORPOOL.Set(0);
            // イベント
            ClientPacket.Header.CP_BEGIN_EVENT_FIELD.Set(0);
            {
                //ClientPacket.Header.CP_EventStart.Set(0x00E2);
                //ClientPacket.Header.CP_SnowBallHit.Set(0x00E3);
                //ClientPacket.Header.CP_SnowBallTouch.Set(0x00E4);
                //ClientPacket.Header.LEFT_KNOCK_BACK.Set(0x00E5); // ???
                //ClientPacket.Header.CP_CoconutHit.Set(0x00E6);
            }
            ClientPacket.Header.CP_END_EVENT_FIELD.Set(0);

            // モンスターカーニバル
            ClientPacket.Header.CP_BEGIN_MONSTER_CARNIVAL_FIELD.Set(0);
            {
                //ClientPacket.Header.CP_MCarnivalRequest.Set(0x00E9);
            }
            ClientPacket.Header.CP_END_MONSTER_CARNIVAL_FIELD.Set(0);
            // ステーションの船
            ClientPacket.Header.CP_CONTISTATE.Set(0x0099);
        }
        ClientPacket.Header.CP_END_FIELD.Set(0);
        ClientPacket.Header.BEANS_OPERATION.Set(0x009A);
        ClientPacket.Header.BEANS_UPDATE.Set(0x009B);

        // ポイントショップ
        ClientPacket.Header.CP_BEGIN_CASHSHOP.Set(0);
        {
            ClientPacket.Header.CP_CashShopChargeParamRequest.Set(0x009F);
            ClientPacket.Header.CP_CashShopQueryCashRequest.Set(0x00A0);
        }
        ClientPacket.Header.CP_END_CASHSHOP.Set(0);
    }
}
