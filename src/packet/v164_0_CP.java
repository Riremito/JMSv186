package packet;

// 冒険家のみの最終バージョン
public class v164_0_CP {

    public static void Set() {

        // ログインサーバー 必須
        ClientPacket.Header.CP_CheckPassword.Set(0x0001);
        ClientPacket.Header.CP_WorldInfoRequest.Set(0x0003);
        ClientPacket.Header.CP_SelectWorld.Set(0x0004);
        ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
        ClientPacket.Header.CP_MigrateIn.Set(0x0007);
        ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
        ClientPacket.Header.CP_ViewAllChar.Set(0x000A);
        ClientPacket.Header.CP_CreateNewCharacter.Set(0x000B);
        ClientPacket.Header.CP_DeleteCharacter.Set(0x000D);
        // ログインサーバー その他
        //Header.SERVERSTATUS_REQUEST.Set(0x0005);
        ClientPacket.Header.CP_ExceptionLog.Set(0x000F);
        ClientPacket.Header.REACHED_LOGIN_SCREEN.Set(0x0018);
        // ゲームサーバー 必須
        ClientPacket.Header.CP_UserTransferFieldRequest.Set(0x001C);
        ClientPacket.Header.CP_UserTransferChannelRequest.Set(0x001D);
        ClientPacket.Header.CP_UserMove.Set(0x001F);
        ClientPacket.Header.CP_UserChat.Set(0x0027);
        ClientPacket.Header.CP_UserMeleeAttack.Set(0x0022);
        ClientPacket.Header.CP_UserShootAttack.Set(0x0023);
        ClientPacket.Header.CP_UserMagicAttack.Set(0x0024);
        ClientPacket.Header.CP_UserHit.Set(0x0026);

        ClientPacket.Header.CP_UserSelectNpc.Set(0x0030);
        ClientPacket.Header.CP_UserRemoteShopOpenRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 0x01);
        ClientPacket.Header.CP_UserScriptMessageAnswer.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 0x02); // 0032
        ClientPacket.Header.CP_UserShopRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 0x03);
        ClientPacket.Header.CP_UserTrunkRequest.Set(ClientPacket.Header.CP_UserSelectNpc.Get() + 0x04);
        //Header.USE_HIRED_MERCHANT.Set(Header.NPC_TALK.Get() + 0x05);
        // ゲームサーバー その他
        ClientPacket.Header.CP_UserPortalTeleportRequest.Set(0x005B);

        // 簡単に確認が可能
        ClientPacket.Header.CP_UserMigrateToCashShopRequest.Set(0x001E);
        ClientPacket.Header.CP_UserHit.Set(0x0026);
        ClientPacket.Header.CP_UserChangeSlotPositionRequest.Set(0x003D);
        ClientPacket.Header.CP_UserStatChangeItemUseRequest.Set(0x003E);

        /*
        ClientPacket.Header.USE_SUMMON_BAG.Set(0x0044);
        ClientPacket.Header.PET_FOOD.Set(0x0045);
        ClientPacket.Header.USE_MOUNT_FOOD.Set(0x0046);
        ClientPacket.Header.USE_SCRIPTED_NPC_ITEM.Set(0x0047);
        ClientPacket.Header.USE_CASH_ITEM.Set(0x0048);
        ClientPacket.Header.USE_CATCH_ITEM.Set(0x004A);
        ClientPacket.Header.USE_SKILL_BOOK.Set(0x004B);
        ClientPacket.Header.OWL_USE_ITEM_VERSION_SEARCH.Set(0x004C);
        ClientPacket.Header.USE_TELE_ROCK.Set(0x004D);
         */
        ClientPacket.Header.CP_UserPortalScrollUseRequest.Set(0x004B);
        ClientPacket.Header.CP_UserUpgradeItemUseRequest.Set(ClientPacket.Header.CP_UserPortalScrollUseRequest.Get() + 0x01);

        ClientPacket.Header.CP_UserAbilityUpRequest.Set(0x004D);
        ClientPacket.Header.CP_UserAbilityMassUpRequest.Set(ClientPacket.Header.CP_UserAbilityUpRequest.Get() + 0x01);

        ClientPacket.Header.CP_UserSkillUpRequest.Set(0x0050);
        ClientPacket.Header.CP_UserSkillUseRequest.Set(ClientPacket.Header.CP_UserSkillUpRequest.Get() + 0x01);
        ClientPacket.Header.CP_UserSkillCancelRequest.Set(ClientPacket.Header.CP_UserSkillUpRequest.Get() + 0x02);
        ClientPacket.Header.CP_UserSkillPrepareRequest.Set(ClientPacket.Header.CP_UserSkillUpRequest.Get() + 0x03);
        ClientPacket.Header.CP_UserDropMoneyRequest.Set(ClientPacket.Header.CP_UserSkillUpRequest.Get() + 0x04);

        ClientPacket.Header.CP_UserPortalScriptRequest.Set(0x005A);
        ClientPacket.Header.CP_UserPortalTeleportRequest.Set(ClientPacket.Header.CP_UserPortalScriptRequest.Get() + 0x01);
        ClientPacket.Header.CP_UserMapTransferRequest.Set(ClientPacket.Header.CP_UserPortalScriptRequest.Get() + 0x02);

        //Header.QUEST_ACTION.Set(0x0061); クエスト情報がおかしくなり、ログイン不可になる
        ClientPacket.Header.CP_UserMacroSysDataModified.Set(0x0064);

        ClientPacket.Header.CP_FuncKeyMappedModified.Set(0x007B);

        ClientPacket.Header.CP_UserMigrateToITCRequest.Set(0x0091);

        ClientPacket.Header.CP_BEGIN_FIELD.Set(0x00A2);
        {
            // マップ上で動く物
            ClientPacket.Header.CP_BEGIN_LIFEPOOL.Set(0x00A3);
            {
                // Mob
                ClientPacket.Header.CP_BEGIN_MOB.Set(0x00A4);
                {
                    ClientPacket.Header.CP_MobMove.Set(0x00A5);
                    /*
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
                     */
                }
                //Header.CP_END_MOB.Set(0x00D3);
            }
        }

        ClientPacket.Header.CP_DropPickUpRequest.Set(0x00B3);
        ClientPacket.Header.CP_ReactorHit.Set(0x00B6);
    }

}
