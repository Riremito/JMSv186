package packet;

public class v131_0_SP {

    public static void Set() {
        // ログインサーバー
        // CLogin::OnPacket
        ServerPacket.Header.LP_BEGIN_SOCKET.Set(0);
        {
            ServerPacket.Header.LP_CheckPasswordResult.Set(0x0001);
            ServerPacket.Header.LP_WorldInformation.Set(0x0003);
            ServerPacket.Header.LP_SelectWorldResult.Set(0x0004);
            ServerPacket.Header.LP_SelectCharacterResult.Set(0x0005);
            ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0006);
            ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0007);
            ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0008);
            ServerPacket.Header.LP_MigrateCommand.Set(0x0009);
            ServerPacket.Header.LP_T_UpdateGameGuard.Set(0x0011); // ログイン画面のボタンを有効化
        }
        ServerPacket.Header.LP_END_SOCKET.Set(0);

        // ゲームサーバー
        // CWvsContext::OnPacket
        ServerPacket.Header.LP_BEGIN_CHARACTERDATA.Set(0);
        {
            ServerPacket.Header.LP_InventoryOperation.Set(0x0016);
            ServerPacket.Header.LP_InventoryGrow.Set(0x0017);
            ServerPacket.Header.LP_StatChanged.Set(0x0018);
            ServerPacket.Header.LP_TemporaryStatSet.Set(0x0019);
            ServerPacket.Header.LP_TemporaryStatReset.Set(0x001A);
            ServerPacket.Header.LP_ChangeSkillRecordResult.Set(0x001B);
            //ServerPacket.Header.LP_SkillUseResult.Set(0x001C); // ?
            ServerPacket.Header.LP_GivePopularityResult.Set(0x001D);
            ServerPacket.Header.LP_Message.Set(0x001E);
            ServerPacket.Header.LP_MemoResult.Set(0x001F);
            ServerPacket.Header.LP_MapTransferResult.Set(0x0020);
            ServerPacket.Header.LP_InitialQuizStart.Set(0x0021);
            //ServerPacket.Header.LP_ClaimResult.Set(0x002A); // @002A [02-03, 41-47]..., 通報後のダイアログ通知
            //ServerPacket.Header.LP_SetClaimSvrAvailableTime.Set(0x002B);
            //ServerPacket.Header.LP_ClaimSvrStatusChanged.Set(0x002C);
            //ServerPacket.Header.LP_SetTamingMobInfo.Set(0x002D);
            ServerPacket.Header.LP_QuestClear.Set(0x0027);
            ServerPacket.Header.LP_EntrustedShopCheckResult.Set(0x0028);
            ServerPacket.Header.LP_SkillLearnItemResult.Set(0x0029);
            // not used
            ServerPacket.Header.LP_CharacterInfo.Set(0x002B);
            ServerPacket.Header.LP_PartyResult.Set(0x002C);
            ServerPacket.Header.LP_FriendResult.Set(0x002D);
            ServerPacket.Header.LP_GuildRequest.Set(0x002E);
            ServerPacket.Header.LP_GuildResult.Set(0x002F);
            ServerPacket.Header.LP_TownPortal.Set(0x0030);
            ServerPacket.Header.LP_BroadcastMsg.Set(0x0031);
            //ServerPacket.Header.LP_ShopScannerResult.Set(0x0041);
            //ServerPacket.Header.LP_ShopLinkResult.Set(0x0042);
            //ServerPacket.Header.LP_MarriageRequest.Set(0x0043);
            //ServerPacket.Header.LP_MarriageResult.Set(0x0044);
            //ServerPacket.Header.LP_WeddingGiftResult.Set(0x0045); // @0045 [09], ウェディング登録? @0091が送信される
            //ServerPacket.Header.LP_MarriedPartnerMapTransfer.Set(0x0046); // @0046 int,int
            //ServerPacket.Header.LP_CashPetFoodResult.Set(0x0047); // @0047 [01]..., 現在ペットはこのえさが食べることができません。もう一度確認してください。
            //ServerPacket.Header.LP_SetWeekEventMessage.Set(0x0048);
            //ServerPacket.Header.LP_SetPotionDiscountRate.Set(0x0049);
            //ServerPacket.Header.LP_BridleMobCatchFail.Set(0x004A); // 0x004A @004A ..., 当該モンスターの体力が強くてできません。
            // パチンコ
            // 特に関数は別のテーブルとして独立していない
            {
                ServerPacket.Header.MINIGAME_PACHINKO_UPDATE_TAMA.Set(0x003C);
                // 0x004D パチンコ景品受け取りUI
                // 0x004E @004E int,int, パチンコ球をx子プレゼントします。というダイアログ誤字っているのでたぶん未実装的な奴
            }

            // 0x004F @004F [01 or 03], プレゼントの通知
            // 0x0050 @0050 strig, string..., 相性占い結果UI
            //ServerPacket.Header.FISHING_BOARD_UPDATE.Set(0x0051);
        }
        ServerPacket.Header.LP_END_CHARACTERDATA.Set(0);

        // ステージ切り替え
        // CStage::OnPacket
        ServerPacket.Header.LP_BEGIN_STAGE.Set(0);
        {
            ServerPacket.Header.LP_SetField.Set(0x0047); // v131 OK
            ServerPacket.Header.LP_SetCashShop.Set(0x0048); // v186と前後逆
            ServerPacket.Header.LP_SetITC.Set(0x0049); // v186と前後逆
        }
        ServerPacket.Header.LP_END_STAGE.Set(0);

        // マップ上の処理
        // CField::OnPacket
        ServerPacket.Header.LP_BEGIN_FIELD.Set(0); // 0058254f
        {
            // SERVER_BLOCKED = 0x4D
            //ServerPacket.Header.LP_TransferFieldReqIgnored.Set(0x0084); // @0084 [01-07], マップ移動時のエラーメッセージ (テレポストーン?)
            ServerPacket.Header.LP_TransferChannelReqIgnored.Set(0x004D); // SERVER_BLOCKED?
            ServerPacket.Header.LP_FieldSpecificData.Set(0x004E);
            ServerPacket.Header.LP_GroupMessage.Set(0x004F);
            ServerPacket.Header.LP_Whisper.Set(0x0050);
            ServerPacket.Header.LP_MobSummonItemUseResult.Set(0x0051);
            ServerPacket.Header.LP_FieldEffect.Set(0x0052); // BOSS_ENV?
            ServerPacket.Header.LP_BlowWeather.Set(0x0053);
            ServerPacket.Header.LP_PlayJukeBox.Set(0x0054);
            ServerPacket.Header.LP_AdminResult.Set(0x0055);
            ServerPacket.Header.LP_Quiz.Set(0x0056);
            ServerPacket.Header.LP_Desc.Set(0x0057);

            // 船の駅の時刻
            ServerPacket.Header.LP_Clock.Set(0x0058);
            // 船
            // CField_ContiMove::OnPacket
            {
                ServerPacket.Header.LP_CONTIMOVE.Set(0x0059);
                ServerPacket.Header.LP_CONTISTATE.Set(0x005A);
            }
            ServerPacket.Header.LP_DestroyClock.Set(0x005E);

            // プレイヤー
            // CUserPool::OnPacket
            ServerPacket.Header.LP_BEGIN_USERPOOL.Set(0);
            {
                ServerPacket.Header.LP_UserEnterField.Set(0x0060);
                ServerPacket.Header.LP_UserLeaveField.Set(0x0061);

                // CUserPool::OnUserCommonPacket
                ServerPacket.Header.LP_BEGIN_USERCOMMON.Set(0);
                {
                    ServerPacket.Header.LP_UserChat.Set(0x0063);
                    // UPDATE_CHAR_BOX = 0x64?
                    //ServerPacket.Header.LP_UserADBoard.Set(0x00A5);
                    //ServerPacket.Header.LP_UserMiniRoomBalloon.Set(0x00A6);
                    //ServerPacket.Header.LP_UserConsumeItemEffect.Set(0x00A7);
                    ServerPacket.Header.LP_UserItemUpgradeEffect.Set(0x0066); // 書
                    ServerPacket.Header.FISHING_CAUGHT.Set(0x0067);

                    // ペット
                    // CUser::OnPetPacket
                    ServerPacket.Header.LP_BEGIN_PET.Set(0);
                    {
                        ServerPacket.Header.LP_PetActivated.Set(0x0069);
                        //ServerPacket.Header.LP_PetEvol.Set(0x00B5);
                        //ServerPacket.Header.LP_PetTransferField.Set(0x00B6);
                        ServerPacket.Header.LP_PetMove.Set(0x006B);
                        ServerPacket.Header.LP_PetAction.Set(0x006C);
                        ServerPacket.Header.LP_PetNameChanged.Set(0x006D);
                        ServerPacket.Header.LP_PetActionCommand.Set(0x006E);
                    }
                    ServerPacket.Header.LP_END_PET.Set(0);

                    // 召喚, GMSだとここに入ってない
                    // CSummonedPool::OnPacket
                    ServerPacket.Header.LP_BEGIN_SUMMONED.Set(0);
                    {
                        ServerPacket.Header.LP_SummonedEnterField.Set(0x0071);
                        ServerPacket.Header.LP_SummonedLeaveField.Set(0x0072);
                        ServerPacket.Header.LP_SummonedMove.Set(0x0073);
                        ServerPacket.Header.LP_SummonedAttack.Set(0x0074);
                        ServerPacket.Header.LP_SummonedSkill.Set(0x0075);
                        ServerPacket.Header.LP_SummonedHit.Set(0x0076);
                    }
                    ServerPacket.Header.LP_END_SUMMONED.Set(0);
                }
                ServerPacket.Header.LP_END_USERCOMMON.Set(0);

                // 他のプレイヤー
                // CUserPool::OnPacket (GMS)
                // C_UNK_UserRemote::OnPacket (JMS)
                ServerPacket.Header.LP_BEGIN_USERREMOTE.Set(0);
                {
                    ServerPacket.Header.LP_UserMove.Set(0x007A);
                    ServerPacket.Header.LP_UserMeleeAttack.Set(0x007B);
                    ServerPacket.Header.LP_UserShootAttack.Set(0x007C);
                    ServerPacket.Header.LP_UserMagicAttack.Set(0x007D);
                    ServerPacket.Header.LP_UserSkillPrepare.Set(0x007E);
                    ServerPacket.Header.LP_UserSkillCancel.Set(0x0007F);
                    ServerPacket.Header.LP_UserHit.Set(0x0080);
                    ServerPacket.Header.LP_UserEmotion.Set(0x0081);
                    ServerPacket.Header.LP_UserSetActiveEffectItem.Set(0x0082);
                    ServerPacket.Header.LP_UserSetActivePortableChair.Set(0x0083);
                    ServerPacket.Header.LP_UserAvatarModified.Set(0x0084);
                    ServerPacket.Header.LP_UserEffectRemote.Set(0x0085);
                    ServerPacket.Header.LP_UserTemporaryStatSet.Set(0x0086);
                    ServerPacket.Header.LP_UserTemporaryStatReset.Set(0x0087);
                    ServerPacket.Header.LP_UserHP.Set(0x0088);
                    ServerPacket.Header.LP_UserGuildNameChanged.Set(0x0089);
                    ServerPacket.Header.LP_UserGuildMarkChanged.Set(0x008A);
                }
                ServerPacket.Header.LP_END_USERREMOTE.Set(0);

                // クライアントサイドの処理
                // CUserPool::OnPacket (GMS)
                // C_UNK_UserLocal::OnPacket (JMS)
                ServerPacket.Header.LP_BEGIN_USERLOCAL.Set(0);
                {
                    ServerPacket.Header.LP_UserSitResult.Set(0x008D);
                    ServerPacket.Header.LP_UserEmotionLocal.Set(ServerPacket.Header.LP_UserEmotion.Get());
                    // SHOW_ITEM_GAIN_INCHAT = 0x8E?
                    ServerPacket.Header.LP_UserEffectLocal.Set(ServerPacket.Header.LP_UserEffectRemote.Get());
                    ServerPacket.Header.LP_UserTeleport.Set(0x008F);
                    //ServerPacket.Header.LP_Premium.Set(0x00DE); // 未使用?
                    ServerPacket.Header.LP_MesoGive_Succeeded.Set(0x0091);
                    ServerPacket.Header.LP_MesoGive_Failed.Set(0x00092);
                    ServerPacket.Header.LP_UserQuestResult.Set(0x0093);
                    //ServerPacket.Header.LP_NotifyHPDecByField.Set(0x00E4);
                    ServerPacket.Header.LP_UserPetSkillChanged.Set(0x0095);
                    // COOLDOWN = 0x96?
                    //ServerPacket.Header.LP_UserBalloonMsg.Set(0x00E6); // @00E6 "文字列",short1,short0,byte0, 不明
                    //ServerPacket.Header.LP_PlayEventSound.Set(0x00E7);
                    //ServerPacket.Header.PLAYER_HINT.Set(0x00E8); // LP_PlayMinigameSound?
                    //ServerPacket.Header.LP_UserMakerResult.Set(0x00E9); // パケットの構造が複雑, メルをなくしました。(-xxxx)と表示される
                    //ServerPacket.Header.LP_UserOpenConsultBoard.Set(0x00EA); // COUNSELのUI, @010Bが送信される
                    //ServerPacket.Header.LP_UserOpenClassCompetitionPage.Set(0x00EB); // クラス対抗戦UI
                    //ServerPacket.Header.LP_UserOpenUI.Set(0x00EC); // @00EC [byte], 強制的にUIを開く
                    //ServerPacket.Header.LP_UserOpenUIWithOption.Set(0x00ED);
                    //ServerPacket.Header.LP_SetDirectionMode.Set(0x00EE);
                    //ServerPacket.Header.LP_SetStandAloneMode.Set(0x00EF);
                    //ServerPacket.Header.LP_UserHireTutor.Set(0x00F0);
                    //ServerPacket.Header.LP_UserTutorMsg.Set(0x00F1);
                    //ServerPacket.Header.LP_IncCombo.Set(0x00F2);
                    // パチンコ
                    ServerPacket.Header.TAMA_BOX_SUCCESS.Set(0x0097);
                    ServerPacket.Header.TAMA_BOX_FAILURE.Set(0x0098);
                }
                ServerPacket.Header.LP_END_USERLOCAL.Set(0);
            }
            ServerPacket.Header.LP_END_USERPOOL.Set(0); // 0x0103 未使用のはず

            // Mob情報
            // CMobPool::OnPacket
            ServerPacket.Header.LP_BEGIN_MOBPOOL.Set(0);
            {
                ServerPacket.Header.LP_MobEnterField.Set(0x009C);
                ServerPacket.Header.LP_MobLeaveField.Set(0x009D);
                ServerPacket.Header.LP_MobChangeController.Set(0x009E);

                // Mobの処理
                // C_UNK_Mob::OnPacket (仮)
                ServerPacket.Header.LP_BEGIN_MOB.Set(0);
                {
                    ServerPacket.Header.LP_MobMove.Set(0x00A0);
                    ServerPacket.Header.LP_MobCtrlAck.Set(0x00A1);
                    //ServerPacket.Header.LP_MobCtrlHint.Set(0x0109);
                    ServerPacket.Header.LP_MobStatSet.Set(0x00A3);
                    ServerPacket.Header.LP_MobStatReset.Set(0x00A4);
                    // MOB_TO_MOB_DAMAGE = 0xA5 どれか
                    //ServerPacket.Header.LP_MobSuspendReset.Set(0x010C);
                    //ServerPacket.Header.LP_MobAffected.Set(0x00A5);
                    ServerPacket.Header.LP_MobDamaged.Set(0x00A7);
                    //ServerPacket.Header.LP_MobHPIndicator.Set(0x0112);
                    ServerPacket.Header.SHOW_MAGNET.Set(0x00AA);
                    ServerPacket.Header.LP_MobCatchEffect.Set(0x00BB);
                    //ServerPacket.Header.LP_MobSpeaking.Set(0x0115);
                    //ServerPacket.Header.MONSTER_PROPERTIES.Set(0x0117);
                    //ServerPacket.Header.REMOVE_TALK_MONSTER.Set(0x0118);
                    //ServerPacket.Header.TALK_MONSTER.Set(0x0119);
                }
                ServerPacket.Header.LP_END_MOB.Set(0);
            }
            ServerPacket.Header.LP_END_MOBPOOL.Set(0); // 0x011D 未使用のはず

            // NPC
            // CNpcPool::OnPacket
            ServerPacket.Header.LP_BEGIN_NPCPOOL.Set(0); // 00754707
            {
                ServerPacket.Header.LP_NpcEnterField.Set(0x00AF);
                ServerPacket.Header.LP_NpcLeaveField.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 1);
                ServerPacket.Header.LP_NpcChangeController.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 2);
                // CNpcPool::OnNpcPacket
                ServerPacket.Header.LP_BEGIN_NPC.Set(0); // 007548b1
                {
                    // NPC_ACTION = 0xB3 どれ？
                    //ServerPacket.Header.LP_NpcMove.Set(0x0121);
                    //ServerPacket.Header.LP_NpcUpdateLimitedInfo.Set(0x0122);
                    //ServerPacket.Header.LP_NpcSpecialAction.Set(0x0123);
                }
                ServerPacket.Header.LP_END_NPC.Set(0);
                // CNpcPool::OnNpcTemplatePacket
                ServerPacket.Header.LP_BEGIN_NPCTEMPLATE.Set(0);
                {
                    //ServerPacket.Header.LP_NpcSetScript.Set(0x0124);
                }
                ServerPacket.Header.LP_END_NPCTEMPLATE.Set(0);
            }
            ServerPacket.Header.LP_END_NPCPOOL.Set(0);

            // 雇用商人
            // CEmployeePool::OnPacket
            ServerPacket.Header.LP_BEGIN_EMPLOYEEPOOL.Set(0);
            {
                ServerPacket.Header.LP_EmployeeEnterField.Set(0x00BB);
                ServerPacket.Header.LP_EmployeeLeaveField.Set(0x00BC);
                ServerPacket.Header.LP_EmployeeMiniRoomBalloon.Set(0x00BD);
            }
            ServerPacket.Header.LP_END_EMPLOYEEPOOL.Set(0);

            // ドロップアイテム
            // CDropPool::OnPacket
            ServerPacket.Header.LP_BEGIN_DROPPOOL.Set(0);
            {
                ServerPacket.Header.LP_DropEnterField.Set(0x00C0);
                ServerPacket.Header.LP_DropLeaveField.Set(0x00C1);
            }
            ServerPacket.Header.LP_END_DROPPOOL.Set(0);

            // CMessageBoxPool::OnPacket
            ServerPacket.Header.LP_BEGIN_MESSAGEBOXPOOL.Set(0);
            {
                ServerPacket.Header.LP_CreateMessgaeBoxFailed.Set(0x00C4);
                ServerPacket.Header.LP_MessageBoxEnterField.Set(0x00C5);
                ServerPacket.Header.LP_MessageBoxLeaveField.Set(0x00C6);
            }
            ServerPacket.Header.LP_END_MESSAGEBOXPOOL.Set(0);

            // ミスト
            // CAffectedAreaPool::OnPacket
            ServerPacket.Header.LP_BEGIN_AFFECTEDAREAPOOL.Set(0);
            {
                ServerPacket.Header.LP_AffectedAreaCreated.Set(0x00C9);
                ServerPacket.Header.LP_AffectedAreaRemoved.Set(0x00CA);
            }
            ServerPacket.Header.LP_END_AFFECTEDAREAPOOL.Set(0);

            // ミスティックドア
            // CTownPortalPool::OnPacket
            ServerPacket.Header.LP_END_TOWNPORTALPOOL.Set(0);
            {
                ServerPacket.Header.LP_TownPortalCreated.Set(0x00CD);
                ServerPacket.Header.LP_TownPortalRemoved.Set(0x00CE);
            }
            ServerPacket.Header.LP_END_TOWNPORTALPOOL.Set(0);

            // 設置物
            // CReactorPool::OnPacket
            ServerPacket.Header.LP_BEGIN_REACTORPOOL.Set(0);
            {
                ServerPacket.Header.LP_ReactorChangeState.Set(0x00D1);
                //ServerPacket.Header.LP_ReactorMove.Set(0x0138);
                ServerPacket.Header.LP_ReactorEnterField.Set(0x00D3);
                ServerPacket.Header.LP_ReactorLeaveField.Set(0x00D4);
            }
            ServerPacket.Header.LP_END_REACTORPOOL.Set(0);

            // イベント
            ServerPacket.Header.LP_BEGIN_ETCFIELDOBJ.Set(0);
            {
                // CField_SnowBall::OnPacket
                {
                    ServerPacket.Header.LP_SnowBallState.Set(0x00D7);
                    ServerPacket.Header.LP_SnowBallHit.Set(0x00D8);
                    ServerPacket.Header.LP_SnowBallMsg.Set(0x00D9);
                    ServerPacket.Header.LP_SnowBallTouch.Set(0x00DA);
                }
                // CField_Coconut::OnPacket
                {
                    ServerPacket.Header.LP_CoconutHit.Set(0x00DB);
                    ServerPacket.Header.LP_CoconutScore.Set(0x00DC);
                }

                // モンスターカーニバル
                // CField_MonsterCarnival::OnPacket
                // CField_MonsterCarnivalRevive::OnPacket
                {
                    ServerPacket.Header.LP_MCarnivalEnter.Set(0x00DF);
                    ServerPacket.Header.LP_MCarnivalPersonalCP.Set(0x00E0);
                    ServerPacket.Header.LP_MCarnivalTeamCP.Set(0x00E1);
                    //ServerPacket.Header.LP_MCarnivalResultSuccess.Set(0x0146);
                    //ServerPacket.Header.LP_MCarnivalResultFail.Set(0x0147);
                    ServerPacket.Header.LP_MCarnivalDeath.Set(0x00E4);
                    ServerPacket.Header.LP_MCarnivalMemberOut.Set(0x00E5);
                    //ServerPacket.Header.LP_MCarnivalGameResult.Set(0x014A);
                }
            }
            ServerPacket.Header.LP_END_ETCFIELDOBJ.Set(0);

            // NPC会話
            // CScriptMan::OnPacket
            ServerPacket.Header.LP_BEGIN_SCRIPT.Set(0);
            {
                ServerPacket.Header.LP_ScriptMessage.Set(0x00E9);
            }
            ServerPacket.Header.LP_END_SCRIPT.Set(0);

            // 商店
            // CShopDlg::OnPacket
            ServerPacket.Header.LP_BEGIN_SHOP.Set(0);
            {
                ServerPacket.Header.LP_OpenShopDlg.Set(0x00EC);
                ServerPacket.Header.LP_ShopResult.Set(0x00ED);
            }
            ServerPacket.Header.LP_END_SHOP.Set(0);

            // 倉庫
            // CTrunkDlg::OnPacket
            ServerPacket.Header.LP_TrunkResult.Set(0x00EF);

            // プレドリック
            // CStoreBankDlg::OnPacket
            ServerPacket.Header.LP_BEGIN_STOREBANK.Set(0);
            {
                ServerPacket.Header.LP_StoreBankGetAllResult.Set(0x00F1);
                ServerPacket.Header.LP_StoreBankResult.Set(ServerPacket.Header.LP_StoreBankGetAllResult.Get() + 1);
            }
            ServerPacket.Header.LP_END_STOREBANK.Set(0);

            ServerPacket.Header.LP_RPSGame.Set(0x00F4);
            ServerPacket.Header.LP_Messenger.Set(ServerPacket.Header.LP_RPSGame.Get() + 1);
            ServerPacket.Header.LP_MiniRoom.Set(ServerPacket.Header.LP_RPSGame.Get() + 2);

            // CField_Wedding::OnPacket
            ServerPacket.Header.LP_BEGIN_WEDDING.Set(0);
            {
                //ServerPacket.Header.LP_WeddingProgress.Set(0x0165);
                //ServerPacket.Header.LP_WeddingCremonyEnd.Set(0x0166);
            }
            ServerPacket.Header.LP_END_WEDDING.Set(0);

            // パチンコ
            // C_UNK_Pachinko::OnPacket
            ServerPacket.Header.UNK_BEGIN_PACHINKO.Set(0);
            {
                ServerPacket.Header.TIP_BEANS.Set(0x00FF);
                ServerPacket.Header.OPEN_BEANS.Set(0x0100);
                ServerPacket.Header.SHOOT_BEANS.Set(0x0101);
                ServerPacket.Header.UPDATE_BEANS.Set(0x0102); // or 0x103
            }
            ServerPacket.Header.UNK_END_PACHINKO.Set(0);
            // 宅配
            // CParcelDlg::OnPacket
            //ServerPacket.Header.LP_Parcel.Set(0x016C); // 00792325
        }
        ServerPacket.Header.LP_END_FIELD.Set(0);
        // TESTTEST

        // ポイントショップ
        // CCashShop::OnPacket
        ServerPacket.Header.LP_BEGIN_CASHSHOP.Set(0);
        {
            ServerPacket.Header.LP_CashShopChargeParamResult.Set(0x010A);
            ServerPacket.Header.LP_CashShopQueryCashResult.Set(ServerPacket.Header.LP_CashShopChargeParamResult.Get() + 1);
        }
        ServerPacket.Header.LP_END_CASHSHOP.Set(0);

        // キー設定
        // CFuncKeyMappedMan::OnPacket
        ServerPacket.Header.LP_BEGIN_FUNCKEYMAPPED.Set(0);
        {
            ServerPacket.Header.LP_FuncKeyMappedInit.Set(0x0114); // キー設定
            ServerPacket.Header.LP_PetConsumeItemInit.Set(ServerPacket.Header.LP_FuncKeyMappedInit.Get() + 1); // ペット自動回復
        }
        ServerPacket.Header.LP_END_FUNCKEYMAPPED.Set(0);

        // MTS
        // CITC::OnPacket
        ServerPacket.Header.LP_BEGIN_ITC.Set(0);
        {
            // CITC::OnChargeParamResult
            //ServerPacket.Header.LP_ITCChargeParamResult.Set(0x018A);
            // CITC::OnQueryCashResult
            //ServerPacket.Header.LP_ITCQueryCashResult.Set(0x018B);
            // CITC::OnNormalItemResult
            //ServerPacket.Header.LP_ITCNormalItemResult.Set(0x018C);
        }
        ServerPacket.Header.LP_END_ITC.Set(0);

    }
}
