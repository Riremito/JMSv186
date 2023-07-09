/*
 * Copyright (C) 2023 Riremito
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
 * You should not develop private server for your business.
 * You should not ban anyone who tries hacking in private server.
 */
package packet;

/**
 *
 * @author Riremito
 */
public class v194_0_SP {

    public static void Set() {
        ServerPacket.Header.LP_MigrateCommand.Set(0x0008);
        ServerPacket.Header.LP_AliveReq.Set(0x0009);
        ServerPacket.Header.LP_SecurityPacket.Set(0x000C);
        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0000);
        ServerPacket.Header.LP_WorldInformation.Set(0x0002);
        ServerPacket.Header.LP_SelectWorldResult.Set(0x0003);
        ServerPacket.Header.LP_SelectCharacterResult.Set(0x0004);
        ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0006);
        ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0007);
        ServerPacket.Header.LP_ViewAllCharResult.Set(0x0011);
        ServerPacket.Header.LP_LatestConnectedWorld.Set(0x0013);
        ServerPacket.Header.LP_RecommendWorldMessage.Set(0x0014);
        ServerPacket.Header.LOGIN_AUTH.Set(0x0015);

        ServerPacket.Header.LP_InventoryOperation.Set(0x0018);
        ServerPacket.Header.LP_StatChanged.Set(0x001A); // ステータス変化
        ServerPacket.Header.LP_ChangeSkillRecordResult.Set(0x001F); // スキルポイント変化
        ServerPacket.Header.LP_BroadcastMsg.Set(0x0040); // メッセージ

        ServerPacket.Header.LP_SetField.Set(0x0088); // ゲームサーバーへ
        ServerPacket.Header.LP_SetITC.Set(0x0089); // MTSサーバーへ
        ServerPacket.Header.LP_SetCashShop.Set(0x008A); // ポイントショップサーバーへ

        // User v194 test
        {
            // CUserPool::OnPacket
            ServerPacket.Header.LP_UserEnterField.Set(0x00AE); // 他プレイヤー
            ServerPacket.Header.LP_UserLeaveField.Set(0x00AF);
            // CUserPool::OnUserCommonPacket
            ServerPacket.Header.LP_UserChat.Set(0x00B0); // チャット
            ServerPacket.Header.LP_UserChatNLCPQ.Set(0x00B1);
            // test
            ServerPacket.Header.LP_UserADBoard.Set(0x00B2); // 黒板
            ServerPacket.Header.LP_UserMiniRoomBalloon.Set(0x00B3);
            ServerPacket.Header.LP_UserConsumeItemEffect.Set(0x00B4);
            ServerPacket.Header.LP_UserItemUpgradeEffect.Set(0x00B5); // 書
            ServerPacket.Header.LP_UserItemHyperUpgradeEffect.Set(0x00B6); // 装備強化の書
            ServerPacket.Header.LP_UserItemOptionUpgradeEffect.Set(0x00B7); // ミラクルキューブ
            ServerPacket.Header.LP_UserItemReleaseEffect.Set(0x00B8); // 虫眼鏡
            ServerPacket.Header.LP_UserItemUnreleaseEffect.Set(0x00B9); // 虫眼鏡
            // 0x00BA 未使用
            // 0x00BB 未使用
            ServerPacket.Header.LP_UserTeslaTriangle.Set(0x00BC);
            ServerPacket.Header.LP_UserFollowCharacter.Set(0x00BD);
            ServerPacket.Header.LP_UserShowPQReward.Set(0x00BE);
            ServerPacket.Header.LP_UserSetPhase.Set(0x00BF);
            // 0x00C0 未使用
            ServerPacket.Header.FISHING_CAUGHT.Set(0x00C1); // 釣り
            ServerPacket.Header.LP_ShowPamsSongResult.Set(0x00C2); // ファムの歌
            // CUser::OnPetPacket
            ServerPacket.Header.LP_PetActivated.Set(0x00C3);
            ServerPacket.Header.LP_PetEvol.Set(0x00C4);
            ServerPacket.Header.LP_PetTransferField.Set(0x00C5);
            ServerPacket.Header.LP_PetMove.Set(0x00C6);
            ServerPacket.Header.LP_PetAction.Set(0x00C7);
            ServerPacket.Header.LP_PetNameChanged.Set(0x00C8);
            ServerPacket.Header.LP_PetLoadExceptionList.Set(0x00C9);
            ServerPacket.Header.LP_PetActionCommand.Set(0x00CA);
            // CUser::OnDragonPacket
            ServerPacket.Header.LP_DragonEnterField.Set(0x00CB);
            ServerPacket.Header.LP_DragonMove.Set(0x00CC);
            ServerPacket.Header.LP_DragonLeaveField.Set(0x00CD); // ?
            // CUserPool::OnUserRemotePacket
            ServerPacket.Header.LP_UserMove.Set(0x00CF); // 2
            ServerPacket.Header.LP_UserMeleeAttack.Set(0x00D0); // 1
            ServerPacket.Header.LP_UserShootAttack.Set(0x00D1); // 1
            ServerPacket.Header.LP_UserMagicAttack.Set(0x00D2); // 1
            ServerPacket.Header.LP_UserBodyAttack.Set(0x00D3); // 1
            ServerPacket.Header.LP_UserSkillPrepare.Set(0x00D4); // 1
            ServerPacket.Header.LP_UserMovingShootAttackPrepare.Set(0x00D5); // 1
            ServerPacket.Header.LP_UserSkillCancel.Set(0x00D6); // 1
            ServerPacket.Header.LP_UserHit.Set(0x00D7); // 1
            ServerPacket.Header.LP_UserEmotion.Set(0x00D8); // 1
            ServerPacket.Header.LP_UserSetActiveEffectItem.Set(0x00D9); // 1
            ServerPacket.Header.LP_UserShowUpgradeTombEffect.Set(0x00DA); // 1
            ServerPacket.Header.LP_UserSetActivePortableChair.Set(0x00DB); // 2
            ServerPacket.Header.LP_UserAvatarModified.Set(0x00DC); // 2
            ServerPacket.Header.LP_UserEffectRemote.Set(0x00DD); // 1
            ServerPacket.Header.LP_UserTemporaryStatSet.Set(0x00DE); // 2
            ServerPacket.Header.LP_UserTemporaryStatReset.Set(0x00DF); // 2
            ServerPacket.Header.LP_UserHP.Set(0x00E0); // 2
            ServerPacket.Header.LP_UserGuildNameChanged.Set(0x00E1); // 2
            ServerPacket.Header.LP_UserGuildMarkChanged.Set(0x00E2); // 2
            // 0x00E3 不明
            ServerPacket.Header.LP_UserThrowGrenade.Set(0x00E4); // 2
            // CUserPool::OnUserLocalPacket
            ServerPacket.Header.LP_UserSitResult.Set(0x00DE5); // 3
            ServerPacket.Header.LP_UserEmotionLocal.Set(0x00E6); // 3
            ServerPacket.Header.LP_UserEffectLocal.Set(0x00E7); // 3
            ServerPacket.Header.LP_UserTeleport.Set(0x00E8); // 3
            // 0x00E9 未使用
            ServerPacket.Header.LP_MesoGive_Succeeded.Set(0x00EA); // 3
            ServerPacket.Header.LP_MesoGive_Failed.Set(0x00EB); // 3
            ServerPacket.Header.LP_FieldFadeInOut.Set(0x00EC); // 3
            ServerPacket.Header.LP_FieldFadeOutForce.Set(0x00ED); // 3
            ServerPacket.Header.LP_Random_Mesobag_Succeed.Set(0x00EE); // 3
            ServerPacket.Header.LP_Random_Mesobag_Failed.Set(0x00EF); // 3
            ServerPacket.Header.LP_UserQuestResult.Set(0x00F0); //3
            ServerPacket.Header.LP_NotifyHPDecByField.Set(0x00F1); //3
            // 0x00F2 不明
            ServerPacket.Header.LP_UserBalloonMsg.Set(0x00F3); //3
            ServerPacket.Header.LP_PlayEventSound.Set(0x00F4);
            ServerPacket.Header.LP_PlayMinigameSound.Set(0x00F5); //3
            // 0x00F6 不明
            ServerPacket.Header.LP_UserMakerResult.Set(0x00F7); //3
            ServerPacket.Header.LP_UserOpenConsultBoard.Set(0x00F8); //3
            ServerPacket.Header.LP_UserOpenClassCompetitionPage.Set(0x00F9); //3
            ServerPacket.Header.LP_UserOpenUI.Set(0x00FA); //3
            ServerPacket.Header.LP_UserOpenUIWithOption.Set(0x00FB); //3
            ServerPacket.Header.LP_SetDirectionMode.Set(0x00FC); //3
            ServerPacket.Header.LP_SetStandAloneMode.Set(0x00FD); //3
            ServerPacket.Header.LP_UserHireTutor.Set(0x00FE); //3
            ServerPacket.Header.LP_UserTutorMsg.Set(0x00FF); //3
            ServerPacket.Header.LP_IncCombo.Set(0x0100); //3
            // 0x0101 Combo
            ServerPacket.Header.TAMA_BOX_SUCCESS.Set(0x0102); // 3
            ServerPacket.Header.TAMA_BOX_FAILURE.Set(0x0103); // 3
            ServerPacket.Header.LP_UserRandomEmotion.Set(0x0104); // 3
            ServerPacket.Header.LP_ResignQuestReturn.Set(0x0105); // 3
            ServerPacket.Header.LP_PassMateName.Set(0x0106); // 3
            ServerPacket.Header.LP_SetRadioSchedule.Set(0x0107); // 3
            ServerPacket.Header.LP_UserOpenSkillGuide.Set(0x0108); // 3
            ServerPacket.Header.LP_UserNoticeMsg.Set(0x0109); // 3
            ServerPacket.Header.LP_UserChatMsg.Set(0x010A); // 3
            ServerPacket.Header.LP_UserBuffzoneEffect.Set(0x010B); // 3
            // 0x010C 不明
            ServerPacket.Header.LP_UserTimeBombAttack.Set(0x010D); // 3
            ServerPacket.Header.LP_UserPassiveMove.Set(0x010E); // 3
            ServerPacket.Header.LP_UserFollowCharacterFailed.Set(0x010F); // 3
            ServerPacket.Header.LP_UserRequestVengeance.Set(0x0110); // 3
            ServerPacket.Header.LP_UserRequestExJablin.Set(0x0111); // 3
            // 0x0112 不明
            // 0x0113 採集
            // 0x0114-0x0116 不明
            ServerPacket.Header.LP_QuestGuideResult.Set(0x0117); // 3
            // 0x0118-0x011B 不明
            ServerPacket.Header.LP_SkillCooltimeSet.Set(0x011C); // 3
        }
        // Summon v194 test
        {
            ServerPacket.Header.LP_SummonedEnterField.Set(0x011E);
            ServerPacket.Header.LP_SummonedLeaveField.Set(0x011F);
            ServerPacket.Header.LP_SummonedMove.Set(0x0120);
            ServerPacket.Header.LP_SummonedAttack.Set(0x0121);
            ServerPacket.Header.LP_SummonedSkill.Set(0x0122);
            ServerPacket.Header.LP_SummonedHit.Set(0x0123);
        }
        // Mob v194
        {
            ServerPacket.Header.LP_MobEnterField.Set(0x0124); // Mob召喚
            ServerPacket.Header.LP_MobLeaveField.Set(0x0125); // Mob消滅
            ServerPacket.Header.LP_MobChangeController.Set(0x0126);
            ServerPacket.Header.LP_MobMove.Set(0x0127); // Mob移動
            ServerPacket.Header.LP_MobCtrlAck.Set(0x0128);
            // OK but comment out
            /*
            ServerPacket.Header.LP_MobStatSet.Set(0x012A);
            ServerPacket.Header.LP_MobStatReset.Set(0x012B);
            ServerPacket.Header.LP_MobSuspendReset.Set(0x012C);
            ServerPacket.Header.LP_MobAffected.Set(0x012D);
            ServerPacket.Header.LP_MobDamaged.Set(0x012E);
            ServerPacket.Header.LP_MobSpecialEffectBySkill.Set(0x012F);
            ServerPacket.Header.LP_MobCrcKeyChanged.Set(0x0131);
            ServerPacket.Header.LP_MobHPIndicator.Set(0x0132);
            ServerPacket.Header.LP_MobCatchEffect.Set(0x0133);
            ServerPacket.Header.LP_MobEffectByItem.Set(0x0134);
            ServerPacket.Header.LP_MobSpeaking.Set(0x0135);
            ServerPacket.Header.LP_MobSkillDelay.Set(0x0136);
            ServerPacket.Header.LP_MobRequestResultEscortInfo.Set(0x0137);
            ServerPacket.Header.LP_MobEscortStopEndPermmision.Set(0x0138);
            ServerPacket.Header.LP_MobEscortStopSay.Set(0x0139);
            ServerPacket.Header.LP_MobEscortReturnBefore.Set(0x013A);
            ServerPacket.Header.LP_MobNextAttack.Set(0x013B);
            ServerPacket.Header.LP_MobTeleport.Set(0x013C);
            ServerPacket.Header.LP_MobAttackedByMob.Set(0x013D);
             */
            // 0x013E 未使用
        }

        // NPC v194
        {
            ServerPacket.Header.LP_ImitatedNPCData.Set(0x0058);
            ServerPacket.Header.LP_LimitedNPCDisableInfo.Set(0x0059);
            ServerPacket.Header.LP_NpcEnterField.Set(0x013F); // NPC召喚
            ServerPacket.Header.LP_NpcLeaveField.Set(0x0140); // NPC消滅
            ServerPacket.Header.LP_NpcChangeController.Set(0x0141);
            ServerPacket.Header.LP_NpcMove.Set(0x0142);
            ServerPacket.Header.LP_NpcUpdateLimitedInfo.Set(0x0143);
            ServerPacket.Header.LP_NpcSpecialAction.Set(0x0144);
            ServerPacket.Header.LP_NpcSetScript.Set(0x0145);
            // 0x0146 未使用
        }
        // CEmployeePool::OnPacket v194 test
        {
            ServerPacket.Header.LP_EmployeeEnterField.Set(0x0147);
            ServerPacket.Header.LP_EmployeeLeaveField.Set(0x0148);
            ServerPacket.Header.LP_EmployeeMiniRoomBalloon.Set(0x0149);
        }
        // Drop v194
        {
            ServerPacket.Header.LP_DropEnterField.Set(0x014A);
            // 0x014B 未使用, LP_DropReleaseAllFreeze is not used
            ServerPacket.Header.LP_DropLeaveField.Set(0x014C);
        }
        // v194 test
        {
            ServerPacket.Header.LP_CreateMessgaeBoxFailed.Set(0x014D);
            ServerPacket.Header.LP_MessageBoxEnterField.Set(0x014E);
            ServerPacket.Header.LP_MessageBoxLeaveField.Set(0x014F);
        }
        // AffectedArea v194
        {
            ServerPacket.Header.LP_AffectedAreaCreated.Set(0x0150);
            ServerPacket.Header.LP_AffectedAreaRemoved.Set(0x0151);
        }
        // TownPortal v194 test
        {
            ServerPacket.Header.LP_TownPortalCreated.Set(0x0152);
            ServerPacket.Header.LP_TownPortalRemoved.Set(0x0153);
        }
        // Gate v194 test
        {
            ServerPacket.Header.LP_OpenGateCreated.Set(0x0154);
            ServerPacket.Header.LP_OpenGateRemoved.Set(0x0155);
        }
        // v194
        {
            // 0x0156
            ServerPacket.Header.LP_JMS_PINKBEAN_PORTAL_CREATE.Set(0x0156);
            // 0x0157
            // @0157 [00 or 01] [itemID], ポータルを開けませんでした。入場にはitemIDが必要です。
            // 0x0158
        }
        // Reactor v194
        {
            ServerPacket.Header.LP_ReactorChangeState.Set(0x0159);
            ServerPacket.Header.LP_ReactorMove.Set(0x015A);
            ServerPacket.Header.LP_ReactorEnterField.Set(0x015B); // 設置物召喚
            ServerPacket.Header.LP_ReactorLeaveField.Set(0x015C); // 設置物消滅
        }

        ServerPacket.Header.LP_ScriptMessage.Set(0x0176); // NPC会話
        ServerPacket.Header.LP_OpenShopDlg.Set(0x0177); // NPC商店
        ServerPacket.Header.LP_ShopResult.Set(0x0178);
        ServerPacket.Header.LP_TrunkResult.Set(0x017B); // 倉庫

        ServerPacket.Header.LP_FuncKeyMappedInit.Set(0x019E); // キー設定初期化
        //ServerPacket.Header.LP_PetConsumeItemInit.Set(0x019F);
        //ServerPacket.Header.LP_PetConsumeMPItemInit.Set(0x019A);

        // CBattleRecordMan::OnPacket v194
        {
            ServerPacket.Header.LP_BattleRecordDotDamageInfo.Set(0x01AE);
            ServerPacket.Header.LP_BattleRecordRequestResult.Set(0x01AF);
            // 0x01B0
        }

    }
}
