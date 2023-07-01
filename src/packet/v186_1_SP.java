package packet;

// BB前最終バージョン
public class v186_1_SP {

    public static void Set() {
        // ログインサーバー
        // CLogin::OnPacket
        ServerPacket.Header.LP_BEGIN_SOCKET.Set(0); // 00699d8d
        {
            // CLogin::OnCheckPasswordResult
            ServerPacket.Header.LP_CheckPasswordResult.Set(0x0000); // 0069a00e
            // CLogin::OnGuestIDLoginResult
            ServerPacket.Header.LP_GuestIDLoginResult.Set(0x0001);
            // CLogin::OnWorldInformation
            ServerPacket.Header.LP_WorldInformation.Set(0x0002);
            // CLogin::OnSelectWorldResult
            ServerPacket.Header.LP_SelectWorldResult.Set(0x0003);
            // CLogin::OnSelectCharacterResult
            ServerPacket.Header.LP_SelectCharacterResult.Set(0x0004);
            // CLogin::OnCheckDuplicatedIDResult
            ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0005);
            // CLogin::OnCreateNewCharacterResult
            ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0006);
            // CLogin::OnDeleteCharacterResult
            ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0007);

            // CClientSocket::ProcessPacket
            // 004bbb9a
            {
                // CClientSocket::OnMigrateCommand
                ServerPacket.Header.LP_MigrateCommand.Set(0x0008); // Change Channel
                // CClientSocket::OnAliveReq
                ServerPacket.Header.LP_AliveReq.Set(0x0009);
                // 0x000A
                // 0x000B
                // CSecurityClient::OnPacket
                ServerPacket.Header.LP_SecurityPacket.Set(0x000C); // HackShield HeartBeat
            }
            // 0x000D CHANNEL_SELECTED?
            // 0x000E @000E ..., @0011 00 00 を送信
            // 0x000F [BYTE]...
            // 0x0010 未使用
            // 0x0011 未使用
            // 0x0012 [Str]
            // 0x0013 安心パスワード
            // 0x0014
            // 0x0015 @0015 [00], 不法プログラムまたは悪性コードが感知されたためゲームを強制終了します。
            // CLogin::OnLatestConnectedWorld
            ServerPacket.Header.LP_LatestConnectedWorld.Set(0x0016);
            // CLogin::OnRecommendWorldMessage
            ServerPacket.Header.LP_RecommendWorldMessage.Set(0x0017);
            // CLogin::???
            ServerPacket.Header.LOGIN_AUTH.Set(0x0018); // JMSオリジナルの可能性が高い
            // 0x0019
            // 0x001A
        }
        ServerPacket.Header.LP_END_SOCKET.Set(0);

        // ゲームサーバー
        // CWvsContext::OnPacket
        ServerPacket.Header.LP_BEGIN_CHARACTERDATA.Set(0); // 00b73785
        {
            ServerPacket.Header.LP_InventoryOperation.Set(0x001B);
            ServerPacket.Header.LP_InventoryGrow.Set(0x001C);
            ServerPacket.Header.LP_StatChanged.Set(0x001D);
            ServerPacket.Header.LP_TemporaryStatSet.Set(0x001E);
            ServerPacket.Header.LP_TemporaryStatReset.Set(0x001F);
            ServerPacket.Header.LP_ForcedStatSet.Set(0x0020);
            ServerPacket.Header.LP_ForcedStatReset.Set(0x0021);
            ServerPacket.Header.LP_ChangeSkillRecordResult.Set(0x0022);
            ServerPacket.Header.LP_SkillUseResult.Set(0x0023);
            ServerPacket.Header.LP_GivePopularityResult.Set(0x0024);
            ServerPacket.Header.LP_Message.Set(0x0025);
            ServerPacket.Header.LP_MemoResult.Set(0x0026);
            ServerPacket.Header.LP_MapTransferResult.Set(0x0027);
            ServerPacket.Header.LP_AntiMacroResult.Set(0x0028); // 未使用?
            ServerPacket.Header.LP_InitialQuizStart.Set(0x0029);
            ServerPacket.Header.LP_ClaimResult.Set(0x002A); // @002A [02-03, 41-47]..., 通報後のダイアログ通知
            ServerPacket.Header.LP_SetClaimSvrAvailableTime.Set(0x002B);
            ServerPacket.Header.LP_ClaimSvrStatusChanged.Set(0x002C);
            ServerPacket.Header.LP_SetTamingMobInfo.Set(0x002D);
            ServerPacket.Header.LP_QuestClear.Set(0x002E);
            ServerPacket.Header.LP_EntrustedShopCheckResult.Set(0x002F);
            ServerPacket.Header.LP_SkillLearnItemResult.Set(0x0030);
            ServerPacket.Header.LP_SortItemResult.Set(0x0031); //逆?
            ServerPacket.Header.LP_GatherItemResult.Set(0x0032);
            // 0x0033 未使用
            // 0x0034 未使用
            ServerPacket.Header.LP_CharacterInfo.Set(0x0035);
            ServerPacket.Header.LP_PartyResult.Set(0x0036);
            ServerPacket.Header.LP_ExpeditionRequest.Set(0x0037);
            ServerPacket.Header.LP_ExpeditionNoti.Set(0x0038);
            ServerPacket.Header.LP_FriendResult.Set(0x0039);
            ServerPacket.Header.LP_GuildRequest.Set(0x003A);
            ServerPacket.Header.LP_GuildResult.Set(0x003B);
            ServerPacket.Header.LP_AllianceResult.Set(0x003C);
            ServerPacket.Header.LP_TownPortal.Set(0x003D);
            ServerPacket.Header.LP_OpenGate.Set(0x003E); // メカニックならこの時点では未実装?
            ServerPacket.Header.LP_BroadcastMsg.Set(0x003F);
            ServerPacket.Header.LP_IncubatorResult.Set(0x0040); // ピグミー
            ServerPacket.Header.LP_ShopScannerResult.Set(0x0041);
            ServerPacket.Header.LP_ShopLinkResult.Set(0x0042);
            ServerPacket.Header.LP_MarriageRequest.Set(0x0043);
            ServerPacket.Header.LP_MarriageResult.Set(0x0044);
            ServerPacket.Header.LP_WeddingGiftResult.Set(0x0045); // @0045 [09], ウェディング登録? @0091が送信される
            ServerPacket.Header.LP_MarriedPartnerMapTransfer.Set(0x0046); // @0046 int,int
            ServerPacket.Header.LP_CashPetFoodResult.Set(0x0047); // @0047 [01]..., 現在ペットはこのえさが食べることができません。もう一度確認してください。
            ServerPacket.Header.LP_SetWeekEventMessage.Set(0x0048);
            ServerPacket.Header.LP_SetPotionDiscountRate.Set(0x0049);
            ServerPacket.Header.LP_BridleMobCatchFail.Set(0x004A); // 0x004A @004A ..., 当該モンスターの体力が強くてできません。
            // 0x004B 未使用
            // パチンコ
            // 特に関数は別のテーブルとして独立していない
            {
                ServerPacket.Header.MINIGAME_PACHINKO_UPDATE_TAMA.Set(0x4C);
                // 0x004D パチンコ景品受け取りUI
                // 0x004E @004E int,int, パチンコ球をx子プレゼントします。というダイアログ誤字っているのでたぶん未実装的な奴
            }
            // 0x004F @004F [01 or 03], プレゼントの通知
            // 0x0050 @0050 strig, string..., 相性占い結果UI
            ServerPacket.Header.FISHING_BOARD_UPDATE.Set(0x0051);
            // 0x0052 @0052 String, 任意メッセージをダイアログに表示
            // 0x0053 @0053 [01 (00, 02は謎)], ワールド変更申請のキャンセル
            // 0x0054 @0054 int, プレイタイム終了まで残りx分x秒です。
            // 0x0055 @0055 byte, なんも処理がされない関数

            // 一応ここにもあるが、NPCの方参照した方が確認が楽
            {
                //Header.LP_ImitatedNPCData.Set(0x0056);
                //Header.LP_LimitedNPCDisableInfo.Set(0x0057);
            }

            ServerPacket.Header.LP_MonsterBookSetCard.Set(0x0058); // OK
            ServerPacket.Header.LP_MonsterBookSetCover.Set(0x0059); // OK
            // 0x0059 BBS_OPERATION?
            // 0x005A @005A String, 任意メッセージをダイアログに表示
            ServerPacket.Header.LP_AvatarMegaphoneRes.Set(0x005B);
            // 0x005C
            // 0x005D
            ServerPacket.Header.UNKNOWN_RELOAD_MINIMAP.Set(0x005E);
            // 0x005F
            // 0x0060
            // 0x0061
            ServerPacket.Header.ENERGY.Set(0x0062);
            ServerPacket.Header.GHOST_POINT.Set(0x0063);
            ServerPacket.Header.GHOST_STATUS.Set(0x0064);
            ServerPacket.Header.FAIRY_PEND_MSG.Set(0x0065);
            ServerPacket.Header.LP_FamilyChartResult.Set(0x0066);
            ServerPacket.Header.LP_FamilyInfoResult.Set(0x0067);
            ServerPacket.Header.LP_FamilyResult.Set(0x0068);
            ServerPacket.Header.LP_FamilyJoinRequest.Set(0x0069);
            ServerPacket.Header.LP_FamilyJoinRequestResult.Set(0x006A);
            ServerPacket.Header.LP_FamilyJoinAccepted.Set(0x006B);
            ServerPacket.Header.LP_FamilyPrivilegeList.Set(0x006C);
            ServerPacket.Header.LP_FamilyFamousPointIncResult.Set(0x006D);
            ServerPacket.Header.LP_FamilyNotifyLoginOrLogout.Set(0x006E);
            ServerPacket.Header.LP_FamilySetPrivilege.Set(0x006F);
            ServerPacket.Header.LP_FamilySummonRequest.Set(0x0070);
            ServerPacket.Header.LP_NotifyLevelUp.Set(0x0071);
            ServerPacket.Header.LP_NotifyWedding.Set(0x0072);
            ServerPacket.Header.LP_NotifyJobChange.Set(0x0073);
            // 0x0074
            ServerPacket.Header.LP_SetPassenserRequest.Set(0x0075);
            ServerPacket.Header.LP_SuccessInUseGachaponBox.Set(0x0076); // @0076 [不明], マジェスティックボックスの中身獲得後のUI
            ServerPacket.Header.LP_ScriptProgressMessage.Set(0x0077); // 画面中央の黄色文字
            ServerPacket.Header.LP_DataCRCCheckFailed.Set(0x0078); // @0078 string, イベンドガイドのNPC会話で任意文字列を表示
            // 0x0079 @0079 [0x02 or], イベンドガイドのNPC会話のエラーメッセージの呼び出し
            // 0x007A
            // 0x007B @007B int,string, 灰色のメッセージ
            ServerPacket.Header.LP_AskUserWhetherUsePamsSong.Set(0x007C); // ファムの歌を利用するか選択するUI, @00C2 [00or01]が送信される01は使用フラグ
            ServerPacket.Header.LP_MacroSysDataInit.Set(0x007D);
        }
        ServerPacket.Header.LP_END_CHARACTERDATA.Set(0);
        // ステージ切り替え
        // CStage::OnPacket
        ServerPacket.Header.LP_BEGIN_STAGE.Set(0); // 00837235
        {
            ServerPacket.Header.LP_SetField.Set(0x007E);
            ServerPacket.Header.LP_SetITC.Set(0x007F);
            ServerPacket.Header.LP_SetCashShop.Set(0x0080);
        }
        ServerPacket.Header.LP_END_STAGE.Set(0);
        // マップ読み込み
        // CMapLoadable::OnPacket
        ServerPacket.Header.LP_BEGIN_MAP.Set(0); // 006e8f24
        {
            ServerPacket.Header.LP_SetBackgroundEffect.Set(0x0081);
            ServerPacket.Header.LP_SetMapObjectVisible.Set(0x0082);
            ServerPacket.Header.LP_ClearBackgroundEffect.Set(0x0083);
        }
        ServerPacket.Header.LP_END_MAP.Set(0);
        // マップ上の処理
        // CField::OnPacket
        ServerPacket.Header.LP_BEGIN_FIELD.Set(0); // 0058254f
        {
            ServerPacket.Header.LP_TransferFieldReqIgnored.Set(0x0084); // @0084 [01-07], マップ移動時のエラーメッセージ (テレポストーン?)
            ServerPacket.Header.LP_TransferChannelReqIgnored.Set(0x0085);
            ServerPacket.Header.LP_FieldSpecificData.Set(0x0086);
            ServerPacket.Header.LP_GroupMessage.Set(0x0087);
            ServerPacket.Header.LP_Whisper.Set(0x0088);
            ServerPacket.Header.LP_MobSummonItemUseResult.Set(0x0089);
            ServerPacket.Header.LP_FieldEffect.Set(0x008A);
            ServerPacket.Header.LP_FieldObstacleOnOff.Set(0x008B);
            ServerPacket.Header.LP_FieldObstacleOnOffStatus.Set(0x008C);
            ServerPacket.Header.LP_FieldObstacleAllReset.Set(0x008D);
            ServerPacket.Header.LP_BlowWeather.Set(0x008E);
            ServerPacket.Header.LP_PlayJukeBox.Set(0x008F);
            ServerPacket.Header.LP_AdminResult.Set(0x0090);
            ServerPacket.Header.LP_Quiz.Set(0x0091);
            ServerPacket.Header.LP_Desc.Set(0x0092);
            // 船の駅の時刻
            ServerPacket.Header.LP_Clock.Set(0x0093);
            // 船
            // CField_ContiMove::OnPacket
            // 005a3d4e
            {
                ServerPacket.Header.LP_CONTIMOVE.Set(0x0094);
                ServerPacket.Header.LP_CONTISTATE.Set(0x0095);
            }
            // 0x0096
            // 0x0097
            // 0x0098
            ServerPacket.Header.LP_DestroyClock.Set(0x0099);
            // 0x009A CField_AriantArena::OnPacketで使用
            // 0x009B
            //Header.PYRAMID_UPDATE.Set(0x009C);
            //Header.PYRAMID_RESULT.Set(0x009D);
            // 0x009E
            ServerPacket.Header.LP_FootHoldInfo.Set(0x009F);
            // 0x00A0 0x00F2を送信

            // プレイヤー
            // CUserPool::OnPacket
            ServerPacket.Header.LP_BEGIN_USERPOOL.Set(0); // 00ac3b21
            {
                ServerPacket.Header.LP_UserEnterField.Set(0x00A1);
                ServerPacket.Header.LP_UserLeaveField.Set(0x00A2);
                // CUserPool::OnUserCommonPacket
                ServerPacket.Header.LP_BEGIN_USERCOMMON.Set(0); // 00ac3e8f
                {
                    ServerPacket.Header.LP_UserChat.Set(0x00A3);
                    ServerPacket.Header.LP_UserChatNLCPQ.Set(0x00A4);
                    ServerPacket.Header.LP_UserADBoard.Set(0x00A5);
                    ServerPacket.Header.LP_UserMiniRoomBalloon.Set(0x00A6);
                    ServerPacket.Header.LP_UserConsumeItemEffect.Set(0x00A7);
                    ServerPacket.Header.LP_UserItemUpgradeEffect.Set(0x00A8); // 書
                    ServerPacket.Header.LP_UserItemHyperUpgradeEffect.Set(0x00A9); // 装備強化の書
                    ServerPacket.Header.LP_UserItemOptionUpgradeEffect.Set(0x00AA); // ミラクルキューブ
                    ServerPacket.Header.LP_UserItemReleaseEffect.Set(0x00AB); // 虫眼鏡
                    ServerPacket.Header.LP_UserItemUnreleaseEffect.Set(0x00AC); // 虫眼鏡?
                    ServerPacket.Header.LP_UserHitByUser.Set(0x00AD); // Damage Effect
                    // 0x00AE
                    // 0x00AF
                    // 0x00B0
                    ServerPacket.Header.LP_UserFollowCharacter.Set(0x00B1);
                    ServerPacket.Header.FISHING_CAUGHT.Set(0x00B2); // 名称不明
                    ServerPacket.Header.LP_ShowPamsSongResult.Set(0x00B3);
                    // ペット
                    // CUser::OnPetPacket
                    ServerPacket.Header.LP_BEGIN_PET.Set(0); // 00a700f7
                    {
                        ServerPacket.Header.LP_PetActivated.Set(0x00B4);
                        ServerPacket.Header.LP_PetEvol.Set(0x00B5);
                        ServerPacket.Header.LP_PetTransferField.Set(0x00B6);
                        ServerPacket.Header.LP_PetMove.Set(0x00B7);
                        ServerPacket.Header.LP_PetAction.Set(0x00B8);
                        ServerPacket.Header.LP_PetNameChanged.Set(0x00B9);
                        ServerPacket.Header.LP_PetLoadExceptionList.Set(0x00BA);
                        ServerPacket.Header.LP_PetActionCommand.Set(0x00BB);
                    }
                    ServerPacket.Header.LP_END_PET.Set(0);
                    // 召喚, GMSだとここに入ってない
                    // CSummonedPool::OnPacket
                    ServerPacket.Header.LP_BEGIN_SUMMONED.Set(0); // 00a701a0
                    {
                        ServerPacket.Header.LP_SummonedEnterField.Set(0x00BC);
                        ServerPacket.Header.LP_SummonedLeaveField.Set(0x00BD);
                        ServerPacket.Header.LP_SummonedMove.Set(0x00BE);
                        ServerPacket.Header.LP_SummonedAttack.Set(0x00BF);
                        ServerPacket.Header.LP_SummonedSkill.Set(0x00C0);
                        ServerPacket.Header.LP_SummonedHit.Set(0x00C1);
                    }
                    ServerPacket.Header.LP_END_SUMMONED.Set(0);
                    // エヴァンのドラゴン
                    // CUser::OnDragonPacket
                    ServerPacket.Header.LP_BEGIN_DRAGON.Set(0); // 00a70476
                    {
                        ServerPacket.Header.LP_DragonEnterField.Set(0x00C2);
                        ServerPacket.Header.LP_DragonMove.Set(0x00C3);
                        ServerPacket.Header.LP_DragonLeaveField.Set(0x00C4);
                    }
                    ServerPacket.Header.LP_END_DRAGON.Set(0);
                }
                ServerPacket.Header.LP_END_USERCOMMON.Set(0);
                // 他のプレイヤー
                // CUserPool::OnPacket (GMS)
                // C_UNK_UserRemote::OnPacket (JMS)
                ServerPacket.Header.LP_BEGIN_USERREMOTE.Set(0); // 00ac4035
                {
                    // 0x00C5 未使用
                    ServerPacket.Header.LP_UserMove.Set(0x00C6);
                    ServerPacket.Header.LP_UserMeleeAttack.Set(0x00C7);
                    ServerPacket.Header.LP_UserShootAttack.Set(0x00C8);
                    ServerPacket.Header.LP_UserMagicAttack.Set(0x00C9);
                    ServerPacket.Header.LP_UserBodyAttack.Set(0x00CA);
                    ServerPacket.Header.LP_UserSkillPrepare.Set(0x00CB);
                    ServerPacket.Header.LP_UserSkillCancel.Set(0x00CC);
                    ServerPacket.Header.LP_UserHit.Set(0x00CD);
                    ServerPacket.Header.LP_UserEmotion.Set(0x00CE);
                    ServerPacket.Header.LP_UserSetActiveEffectItem.Set(0x00CF);
                    ServerPacket.Header.LP_UserShowUpgradeTombEffect.Set(0x00D0);
                    ServerPacket.Header.LP_UserSetActivePortableChair.Set(0x00D1);
                    ServerPacket.Header.LP_UserAvatarModified.Set(0x00D2);
                    ServerPacket.Header.LP_UserEffectRemote.Set(0x00D3);
                    ServerPacket.Header.LP_UserTemporaryStatSet.Set(0x00D4);
                    ServerPacket.Header.LP_UserTemporaryStatReset.Set(0x00D5);
                    ServerPacket.Header.LP_UserHP.Set(0x00D6);
                    ServerPacket.Header.LP_UserGuildNameChanged.Set(0x00D7);
                    ServerPacket.Header.LP_UserGuildMarkChanged.Set(0x00D8);
                    ServerPacket.Header.LP_UserThrowGrenade.Set(0x00D9); // 不明
                }
                ServerPacket.Header.LP_END_USERREMOTE.Set(0);

                // クライアントサイドの処理
                // CUserPool::OnPacket (GMS)
                // C_UNK_UserLocal::OnPacket (JMS)
                ServerPacket.Header.LP_BEGIN_USERLOCAL.Set(0); // 00a8cc68
                {
                    ServerPacket.Header.LP_UserSitResult.Set(0x00DA);
                    ServerPacket.Header.LP_UserEmotionLocal.Set(0x00DB); // 0x00CEと同一
                    ServerPacket.Header.LP_UserEffectLocal.Set(0x00DC); // 0x00D3と同一
                    ServerPacket.Header.LP_UserTeleport.Set(0x00DD);
                    ServerPacket.Header.LP_Premium.Set(0x00DE); // 未使用?
                    ServerPacket.Header.LP_MesoGive_Succeeded.Set(0x00DF);
                    ServerPacket.Header.LP_MesoGive_Failed.Set(0x00E0);
                    ServerPacket.Header.LP_Random_Mesobag_Succeed.Set(0x00E1);
                    ServerPacket.Header.LP_Random_Mesobag_Failed.Set(0x00E2);
                    ServerPacket.Header.LP_UserQuestResult.Set(0x00E3);
                    ServerPacket.Header.LP_NotifyHPDecByField.Set(0x00E4);
                    ServerPacket.Header.LP_UserPetSkillChanged.Set(0x00E5);
                    ServerPacket.Header.LP_UserBalloonMsg.Set(0x00E6); // @00E6 "文字列",short1,short0,byte0, 不明
                    ServerPacket.Header.LP_PlayEventSound.Set(0x00E7);
                    ServerPacket.Header.PLAYER_HINT.Set(0x00E8); // LP_PlayMinigameSound?
                    ServerPacket.Header.LP_UserMakerResult.Set(0x00E9); // パケットの構造が複雑, メルをなくしました。(-xxxx)と表示される
                    ServerPacket.Header.LP_UserOpenConsultBoard.Set(0x00EA); // COUNSELのUI, @010Bが送信される
                    ServerPacket.Header.LP_UserOpenClassCompetitionPage.Set(0x00EB); // クラス対抗戦UI
                    ServerPacket.Header.LP_UserOpenUI.Set(0x00EC); // @00EC [byte], 強制的にUIを開く
                    ServerPacket.Header.LP_UserOpenUIWithOption.Set(0x00ED);
                    ServerPacket.Header.LP_SetDirectionMode.Set(0x00EE);
                    ServerPacket.Header.LP_SetStandAloneMode.Set(0x00EF);
                    ServerPacket.Header.LP_UserHireTutor.Set(0x00F0);
                    ServerPacket.Header.LP_UserTutorMsg.Set(0x00F1);
                    ServerPacket.Header.LP_IncCombo.Set(0x00F2);
                    ServerPacket.Header.TAMA_BOX_SUCCESS.Set(0x00F3); // パチンコ
                    ServerPacket.Header.TAMA_BOX_FAILURE.Set(0x00F4); // パチンコ
                    ServerPacket.Header.LP_UserRandomEmotion.Set(0x00F5);
                    ServerPacket.Header.LP_ResignQuestReturn.Set(0x00F6);
                    ServerPacket.Header.LP_PassMateName.Set(0x00F7);
                    ServerPacket.Header.LP_SetRadioSchedule.Set(0x00F8);
                    ServerPacket.Header.LP_UserOpenSkillGuide.Set(0x00F9); // アラン4次スキルの説明UI
                    ServerPacket.Header.LP_UserNoticeMsg.Set(0x00FA); // OK
                    ServerPacket.Header.LP_UserChatMsg.Set(0x00FB); // OK
                    // 0x00FC
                    // 0x00FD, LP_UserTimeBombAttack? @00FD 時間, 謎のタイマー出現
                    // 0x00FE @00FE int,int,int,int吹っ飛び判定,intダメージ量,..., 攻撃, 被ダメ, KB動作
                    ServerPacket.Header.LP_UserPassiveMove.Set(0x00FF);
                    ServerPacket.Header.LP_UserFollowCharacterFailed.Set(0x0100); // OK
                    ServerPacket.Header.GAME_POLL_QUESTION.Set(0x0101);
                    ServerPacket.Header.LP_SkillCooltimeSet.Set(0x0102);
                }
                ServerPacket.Header.LP_END_USERLOCAL.Set(0);
            }
            ServerPacket.Header.LP_END_USERPOOL.Set(0); // 0x0103 未使用のはず

            // Mob情報
            // CMobPool::OnPacket
            ServerPacket.Header.LP_BEGIN_MOBPOOL.Set(0); // 0072a766
            {
                ServerPacket.Header.LP_MobEnterField.Set(0x0104);
                ServerPacket.Header.LP_MobLeaveField.Set(0x0105);
                ServerPacket.Header.LP_MobChangeController.Set(0x0106);
                // 0x0111, LP_MobCrcKeyChanged @0111 int, @00A2を送信
                // Mobの処理
                // C_UNK_Mob::OnPacket (仮)
                ServerPacket.Header.LP_BEGIN_MOB.Set(0); // 0072a7c5
                {
                    ServerPacket.Header.LP_MobMove.Set(0x0107);
                    ServerPacket.Header.LP_MobCtrlAck.Set(0x0108);
                    ServerPacket.Header.LP_MobCtrlHint.Set(0x0109);
                    ServerPacket.Header.LP_MobStatSet.Set(0x010A);
                    ServerPacket.Header.LP_MobStatReset.Set(0x010B);
                    ServerPacket.Header.LP_MobSuspendReset.Set(0x010C);
                    ServerPacket.Header.LP_MobAffected.Set(0x010D);
                    ServerPacket.Header.LP_MobDamaged.Set(0x010E);
                    // 0x010F, LP_MobSpecialEffectBySkill
                    // 0x0110, LP_MobHPChange 未使用
                    ServerPacket.Header.LP_MobHPIndicator.Set(0x0112);
                    ServerPacket.Header.SHOW_MAGNET.Set(0x0113); // ???
                    ServerPacket.Header.LP_MobCatchEffect.Set(0x0114);
                    ServerPacket.Header.LP_MobSpeaking.Set(0x0115);
                    // 0x0116 @0116 int,int,int,int, 何らかの変数が更新されるが詳細不明
                    ServerPacket.Header.MONSTER_PROPERTIES.Set(0x0117);
                    ServerPacket.Header.REMOVE_TALK_MONSTER.Set(0x0118);
                    ServerPacket.Header.TALK_MONSTER.Set(0x0119);
                    // 0x011A
                    // 0x011B
                    // 0x011C
                }
                ServerPacket.Header.LP_END_MOB.Set(0);
            }
            ServerPacket.Header.LP_END_MOBPOOL.Set(0); // 0x011D 未使用のはず
            // CNpcPool::OnPacket
            ServerPacket.Header.LP_BEGIN_NPCPOOL.Set(0); // 00754707
            {
                ServerPacket.Header.LP_ImitatedNPCData.Set(0x0056); // OK
                ServerPacket.Header.LP_LimitedNPCDisableInfo.Set(0x0057); // OK
                ServerPacket.Header.LP_NpcEnterField.Set(0x011E);
                ServerPacket.Header.LP_NpcLeaveField.Set(0x011F);
                ServerPacket.Header.LP_NpcChangeController.Set(0x0120);
                // CNpcPool::OnNpcPacket
                ServerPacket.Header.LP_BEGIN_NPC.Set(0); // 007548b1
                {
                    ServerPacket.Header.LP_NpcMove.Set(0x0121);
                    ServerPacket.Header.LP_NpcUpdateLimitedInfo.Set(0x0122);
                    ServerPacket.Header.LP_NpcSpecialAction.Set(0x0123);
                }
                ServerPacket.Header.LP_END_NPC.Set(0);
                // CNpcPool::OnNpcTemplatePacket
                ServerPacket.Header.LP_BEGIN_NPCTEMPLATE.Set(0); // 0075494f
                {
                    ServerPacket.Header.LP_NpcSetScript.Set(0x0124); // OK
                }
                ServerPacket.Header.LP_END_NPCTEMPLATE.Set(0);
            }
            ServerPacket.Header.LP_END_NPCPOOL.Set(0); // 0x0125 未使用のはず
            // 雇用商人
            // CEmployeePool::OnPacket
            ServerPacket.Header.LP_BEGIN_EMPLOYEEPOOL.Set(0); // 005546a2
            {
                ServerPacket.Header.LP_EmployeeEnterField.Set(0x0126);
                ServerPacket.Header.LP_EmployeeLeaveField.Set(0x0127);
                ServerPacket.Header.LP_EmployeeMiniRoomBalloon.Set(0x0128);
            }
            ServerPacket.Header.LP_END_EMPLOYEEPOOL.Set(0);
            // ドロップアイテム
            // CDropPool::OnPacket
            ServerPacket.Header.LP_BEGIN_DROPPOOL.Set(0); // 00548071
            {
                ServerPacket.Header.LP_DropEnterField.Set(0x0129);
                ServerPacket.Header.LP_DropLeaveField.Set(0x012A);
            }
            ServerPacket.Header.LP_END_DROPPOOL.Set(0);
            // CMessageBoxPool::OnPacket
            ServerPacket.Header.LP_BEGIN_MESSAGEBOXPOOL.Set(0); // 00705c65
            {
                ServerPacket.Header.LP_CreateMessgaeBoxFailed.Set(0x012B); // OK
                ServerPacket.Header.LP_MessageBoxEnterField.Set(0x012C); // OK
                ServerPacket.Header.LP_MessageBoxLeaveField.Set(0x012D); // OK
            }
            ServerPacket.Header.LP_END_MESSAGEBOXPOOL.Set(0);
            // ミスト
            // CAffectedAreaPool::OnPacket
            ServerPacket.Header.LP_BEGIN_AFFECTEDAREAPOOL.Set(0); // 00438806
            {
                ServerPacket.Header.LP_AffectedAreaCreated.Set(0x012E); // OK
                ServerPacket.Header.LP_AffectedAreaRemoved.Set(0x012F); // OK
            }
            ServerPacket.Header.LP_END_AFFECTEDAREAPOOL.Set(0);
            // ミスティックドア
            // CTownPortalPool::OnPacket
            ServerPacket.Header.LP_END_TOWNPORTALPOOL.Set(0); // 0089923d
            {
                ServerPacket.Header.LP_TownPortalCreated.Set(0x0130); // OK?
                ServerPacket.Header.LP_TownPortalRemoved.Set(0x0131); // OK?
            }
            ServerPacket.Header.LP_END_TOWNPORTALPOOL.Set(0);
            // 0x0132
            // 0x0133
            // 0x0134 crash
            // 0x0135 ポータルを開けませんでした。
            // 0x0136
            // 設置物
            // CReactorPool::OnPacket
            ServerPacket.Header.LP_BEGIN_REACTORPOOL.Set(0); // 007de94f
            {
                ServerPacket.Header.LP_ReactorChangeState.Set(0x0137); // OK
                ServerPacket.Header.LP_ReactorMove.Set(0x0138); // 存在しない
                ServerPacket.Header.LP_ReactorEnterField.Set(0x0139); // OK
                ServerPacket.Header.LP_ReactorLeaveField.Set(0x013A); // OK
            }
            ServerPacket.Header.LP_END_REACTORPOOL.Set(0);
            // イベント
            ServerPacket.Header.LP_BEGIN_ETCFIELDOBJ.Set(0); // ???
            {
                // CField_SnowBall::OnPacket
                // 005e8b95
                {
                    // OK
                    ServerPacket.Header.LP_SnowBallState.Set(0x013B);
                    ServerPacket.Header.LP_SnowBallHit.Set(0x013C);
                    ServerPacket.Header.LP_SnowBallMsg.Set(0x013D);
                    ServerPacket.Header.LP_SnowBallTouch.Set(0x013E);
                }
                // CField_Coconut::OnPacket
                // CocaColaも同じ処理だった
                // 0059F0AA
                {
                    // OK
                    ServerPacket.Header.LP_CoconutHit.Set(0x013F);
                    ServerPacket.Header.LP_CoconutScore.Set(0x0140);
                }
                // CField_GuildBoss::OnPacket
                // 005B6F75
                {
                    // OK
                    ServerPacket.Header.LP_HealerMove.Set(0x0141);
                    ServerPacket.Header.LP_PulleyStateChange.Set(0x0142);
                }
                // CField_MonsterCarnival::OnPacket
                // 005cd297
                // CField_MonsterCarnivalRevive::OnPacket
                // 005cdc16
                {
                    ServerPacket.Header.LP_MCarnivalEnter.Set(0x0143); // CField_MonsterCarnivalRevive::OnPacketにもある
                    ServerPacket.Header.LP_MCarnivalPersonalCP.Set(0x0144);
                    ServerPacket.Header.LP_MCarnivalTeamCP.Set(0x0145);
                    ServerPacket.Header.LP_MCarnivalResultSuccess.Set(0x0146); // OK
                    ServerPacket.Header.LP_MCarnivalResultFail.Set(0x0147); // なんか違う
                    ServerPacket.Header.LP_MCarnivalDeath.Set(0x0148);
                    ServerPacket.Header.LP_MCarnivalMemberOut.Set(0x0149);
                    ServerPacket.Header.LP_MCarnivalGameResult.Set(0x014A); // CField_MonsterCarnivalRevive::OnPacketにもある
                }
                // CField_AriantArena::OnPacket
                // 0059249c
                {
                    // OK
                    ServerPacket.Header.LP_ShowArenaResult.Set(0x009A);
                    ServerPacket.Header.LP_ArenaScore.Set(0x014B);
                }
                // 0x014C 未使用
                // 0059AD23
                {
                    // 0x014D
                    // 0x014E
                }
                // 0x014F @014F byte種類,int残り時間, マップ退場メッセージ
                // CField_Witchtower::OnPacket
                // 005fb0a0
                {
                    ServerPacket.Header.LP_WitchtowerScore.Set(0x0150); // OK
                }
                ServerPacket.Header.LP_HontaleTimer.Set(0x0151); // OK
                ServerPacket.Header.LP_ChaosZakumTimer.Set(0x0152); // OK
                ServerPacket.Header.LP_HontailTimer.Set(0x0153); // 未使用
                ServerPacket.Header.LP_ZakumTimer.Set(0x0154); // OK
            }
            ServerPacket.Header.LP_END_ETCFIELDOBJ.Set(0);
            // NPC会話
            // CScriptMan::OnPacket
            ServerPacket.Header.LP_BEGIN_SCRIPT.Set(0); // 007f9360
            {
                ServerPacket.Header.LP_ScriptMessage.Set(0x0155);
            }
            ServerPacket.Header.LP_END_SCRIPT.Set(0);
            // 商店
            // CShopDlg::OnPacket
            ServerPacket.Header.LP_BEGIN_SHOP.Set(0); // 0080f72f
            {
                ServerPacket.Header.LP_OpenShopDlg.Set(0x0156);
                ServerPacket.Header.LP_ShopResult.Set(0x0157);
            }
            ServerPacket.Header.LP_END_SHOP.Set(0);
            // CAdminShopDlg::OnPacket
            ServerPacket.Header.LP_BEGIN_ADMINSHOP.Set(0); // 00427102
            {
                ServerPacket.Header.LP_AdminShopResult.Set(0x0158); // OK
                ServerPacket.Header.LP_AdminShopCommodity.Set(0x0159); // OK
            }
            ServerPacket.Header.LP_END_ADMINSHOP.Set(0);

            // CTrunkDlg::OnPacket
            ServerPacket.Header.LP_TrunkResult.Set(0x015A); // 008a7467
            // プレドリック
            // CStoreBankDlg::OnPacket
            ServerPacket.Header.LP_BEGIN_STOREBANK.Set(0); // 0086e26a
            {
                ServerPacket.Header.LP_StoreBankGetAllResult.Set(0x015B);
                ServerPacket.Header.LP_StoreBankResult.Set(0x015C);
            }
            ServerPacket.Header.LP_END_STOREBANK.Set(0);

            ServerPacket.Header.LP_RPSGame.Set(0x015D);
            ServerPacket.Header.LP_Messenger.Set(0x015E);
            ServerPacket.Header.LP_MiniRoom.Set(0x015F);
            // イベント
            // CField_Tournament::OnPacket
            ServerPacket.Header.LP_BEGIN_TOURNAMENT.Set(0); // 005EFE97
            {
                ServerPacket.Header.LP_Tournament.Set(0x0160);
                ServerPacket.Header.LP_TournamentMatchTable.Set(0x0161);
                ServerPacket.Header.LP_TournamentSetPrize.Set(0x0162);
                ServerPacket.Header.LP_TournamentNoticeUEW.Set(0x0163);
                ServerPacket.Header.LP_TournamentAvatarInfo.Set(0x0164); // 何もしない関数のため実質未実装
            }
            ServerPacket.Header.LP_END_TOURNAMENT.Set(0);

            // CField_Wedding::OnPacket
            ServerPacket.Header.LP_BEGIN_WEDDING.Set(0); // 005F6F55
            {
                // BYTE DWORD DWORD (GMS) -> DWORD DWORD (JMS)
                ServerPacket.Header.LP_WeddingProgress.Set(0x0165);
                ServerPacket.Header.LP_WeddingCremonyEnd.Set(0x0166); // OK
            }
            ServerPacket.Header.LP_END_WEDDING.Set(0);
            // パチンコ
            // C_UNK_Pachinko::OnPacket (仮)
            ServerPacket.Header.UNK_BEGIN_PACHINKO.Set(0); // 005d7922
            {
                ServerPacket.Header.TIP_BEANS.Set(0x0167);
                ServerPacket.Header.OPEN_BEANS.Set(0x0168);
                ServerPacket.Header.SHOOT_BEANS.Set(0x0169);
                // 0x016A
                ServerPacket.Header.UPDATE_BEANS.Set(0x016B);
            }
            ServerPacket.Header.UNK_END_PACHINKO.Set(0);
            // 宅配
            // CParcelDlg::OnPacket
            ServerPacket.Header.LP_Parcel.Set(0x016C); // 00792325
        }
        ServerPacket.Header.LP_END_FIELD.Set(0);
        // ポイントショップ
        // CCashShop::OnPacket
        ServerPacket.Header.LP_BEGIN_CASHSHOP.Set(0); // 00493582
        {
            // 0x016D
            // 0x016E
            ServerPacket.Header.CS_UPDATE.Set(0x016F);
            ServerPacket.Header.CS_OPERATION.Set(0x0170);
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
        ServerPacket.Header.LP_END_CASHSHOP.Set(0);
        // キー設定
        // CFuncKeyMappedMan::OnPacket
        ServerPacket.Header.LP_BEGIN_FUNCKEYMAPPED.Set(0); // 00609abc
        {
            ServerPacket.Header.LP_FuncKeyMappedInit.Set(0x017C);
            ServerPacket.Header.LP_PetConsumeItemInit.Set(0x017D);
            ServerPacket.Header.LP_PetConsumeMPItemInit.Set(0x017E);
            // 0x017F 不明, JMSオリジナル
        }
        ServerPacket.Header.LP_END_FUNCKEYMAPPED.Set(0);
        // 未使用
        {
            // 0x0180 - 0x0186
        }
        // 不明 0058afb7
        {
            // 0x0187
            // 0x0188
            // 0x0189
        }
        // MTS
        // CITC::OnPacket
        ServerPacket.Header.LP_BEGIN_ITC.Set(0); // 0x0062E9A6
        {
            // CITC::OnChargeParamResult
            ServerPacket.Header.LP_ITCChargeParamResult.Set(0x018A);
            // CITC::OnQueryCashResult
            ServerPacket.Header.LP_ITCQueryCashResult.Set(0x018B);
            // CITC::OnNormalItemResult
            ServerPacket.Header.LP_ITCNormalItemResult.Set(0x018C);
        }
        ServerPacket.Header.LP_END_ITC.Set(0);

        // CMapleTVMan::OnPacket
        ServerPacket.Header.LP_BEGIN_MAPLETV.Set(0); // 006d9728
        {
            ServerPacket.Header.LP_MapleTVUpdateMessage.Set(0x018D); // OK
            ServerPacket.Header.LP_MapleTVClearMessage.Set(0x018E); // OK
            ServerPacket.Header.LP_MapleTVSendMessageResult.Set(0x018F); // @018F [01] [01-03], /MapleTV コマンドのエラーメッセージ処理 (GMコマンドなので通常プレイでは不要)
            ServerPacket.Header.LP_BroadSetFlashChangeEvent.Set(0x0190); // 何もしない関数なので実質未使用
        }
        ServerPacket.Header.LP_END_MAPLETV.Set(0); // 0x191 未使用のはず
        // ビシャスのハンマー
        // GMSだと関数なし
        // C_UNK_GoldHammer::OnPacket
        ServerPacket.Header.LP_BEGIN_GOLDHAMMER.Set(0); // 0058942f
        {
            // 0x0191 未使用
            ServerPacket.Header.LP_GoldHammerResult.Set(0x0192);
            // 0x0193 未使用
            // 0x0194 未使用
        }
        ServerPacket.Header.LP_END_GOLDHAMMER.Set(0);
        // ベガの呪文書
        // CUIVega::OnPacket
        ServerPacket.Header.LP_BEGIN_VEGA.Set(0); // 00589449
        {
            // 0x0195 未使用
            ServerPacket.Header.LP_VegaResult.Set(0x0196);
            // 0x0197 未使用
            // 0x0198 未使用
        }
        ServerPacket.Header.LP_END_VEGA.Set(0);
        // 0x0199 一番最後の関数 0x00D76700が0以外の値のときのみ動作する
        // CWvsContext::OnLogoutGift
        ServerPacket.Header.LP_LogoutGift.Set(0x0199); // 00b6f82e
        // 末尾
        ServerPacket.Header.LP_NO.Set(0x19A);
    }
}
