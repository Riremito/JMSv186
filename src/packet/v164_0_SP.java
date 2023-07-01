package packet;

// 冒険家のみの最終バージョン
public class v164_0_SP {

    public static void UpdateHeaderTest() {
        // 0x0016 - 0x0066
        // 0x004D LP_ImitatedNPCData
        // CWvsContext::OnPacket
        ServerPacket.Header.LP_BEGIN_CHARACTERDATA.Set(0);
        {
            ServerPacket.Header.LP_InventoryOperation.Set(0x0016);
        }
        ServerPacket.Header.LP_END_CHARACTERDATA.Set(0);

        // 0x0067 - 0x0069
        // CStage::OnPacket
        ServerPacket.Header.LP_BEGIN_STAGE.Set(0);
        {
            ServerPacket.Header.LP_SetField.Set(0x0067);
            ServerPacket.Header.LP_SetITC.Set(0x0068);
            ServerPacket.Header.LP_SetCashShop.Set(0x0069);
        }

        // 0x006A - 0x006B
        // CMapLoadable::OnPacket
        ServerPacket.Header.LP_BEGIN_MAP.Set(0);
        {
            ServerPacket.Header.LP_SetBackgroundEffect.Set(0x006A);
            // ServerPacket.Header.LP_SetMapObjectVisible.Set(0);
            ServerPacket.Header.LP_ClearBackgroundEffect.Set(0x006B);
        }

        // 0x007F LP_ShowArenaResult
        // 0x0097 - 0x00AA
        // C_UNK_UserRemote::OnPacket (JMS)
        ServerPacket.Header.LP_BEGIN_USERREMOTE.Set(0);
        {
            ServerPacket.Header.LP_UserMove.Set(0x0097);
            ServerPacket.Header.LP_UserMeleeAttack.Set(0x0098);
            ServerPacket.Header.LP_UserShootAttack.Set(0x0099);
            ServerPacket.Header.LP_UserMagicAttack.Set(0x009A);
            ServerPacket.Header.LP_UserBodyAttack.Set(0x009B);
            ServerPacket.Header.LP_UserSkillPrepare.Set(0x009C);
            ServerPacket.Header.LP_UserSkillCancel.Set(0x009D);
            ServerPacket.Header.LP_UserHit.Set(0x009E);
            ServerPacket.Header.LP_UserEmotion.Set(0x009F); // format changed
            ServerPacket.Header.LP_UserSetActiveEffectItem.Set(0x00A0);
            ServerPacket.Header.LP_UserShowUpgradeTombEffect.Set(0x00A1);
            ServerPacket.Header.LP_UserSetActivePortableChair.Set(0x00A2);
            ServerPacket.Header.LP_UserAvatarModified.Set(0x00A3);
            ServerPacket.Header.LP_UserEffectRemote.Set(0x00A4);
            ServerPacket.Header.LP_UserTemporaryStatSet.Set(0x00A5);
            ServerPacket.Header.LP_UserTemporaryStatReset.Set(0x00A6);
            ServerPacket.Header.LP_UserHP.Set(0x00A7);
            ServerPacket.Header.LP_UserGuildNameChanged.Set(0x00A8);
            ServerPacket.Header.LP_UserGuildMarkChanged.Set(0x00A9);
            ServerPacket.Header.LP_UserThrowGrenade.Set(0x00AA); // format changed
        }
        ServerPacket.Header.LP_END_USERREMOTE.Set(0);

        // 0x00AB - 0x00C0
        // C_UNK_UserLocal::OnPacket
        ServerPacket.Header.LP_BEGIN_USERLOCAL.Set(0);
        {
            ServerPacket.Header.LP_UserSitResult.Set(0x00AB);
            ServerPacket.Header.LP_UserEmotionLocal.Set(0x00AC);
            ServerPacket.Header.LP_UserTeleport.Set(0x00AD);
            ServerPacket.Header.LP_Premium.Set(0x00AE); // not used
            ServerPacket.Header.LP_MesoGive_Succeeded.Set(0x00AF);
            ServerPacket.Header.LP_MesoGive_Failed.Set(0x00B0);
            ServerPacket.Header.LP_UserQuestResult.Set(0x00B1);
            ServerPacket.Header.LP_NotifyHPDecByField.Set(0x00B2);
            ServerPacket.Header.LP_UserPetSkillChanged.Set(0x00B3);
            ServerPacket.Header.LP_UserBalloonMsg.Set(0x00B4);
            ServerPacket.Header.LP_PlayEventSound.Set(0x00B5);
            // 0x00B6 == v186 0x00E8
            ServerPacket.Header.LP_UserMakerResult.Set(0x00B7);
            ServerPacket.Header.LP_UserOpenConsultBoard.Set(0x00B8);
            ServerPacket.Header.LP_UserOpenClassCompetitionPage.Set(0x00B9);
            // 0x00BA ???
            ServerPacket.Header.LP_SetStandAloneMode.Set(0x00BB);
            // 0x00BC ???
            ServerPacket.Header.TAMA_BOX_SUCCESS.Set(0x00BD);
            ServerPacket.Header.TAMA_BOX_FAILURE.Set(0x00BE);
            ServerPacket.Header.LP_UserRandomEmotion.Set(0x00BF);
            ServerPacket.Header.LP_SkillCooltimeSet.Set(0x00C0);
        }
        ServerPacket.Header.LP_END_USERLOCAL.Set(0);

        // 0x00C2 - 0x00C4, 0x00CF
        // CMobPool::OnPacket
        ServerPacket.Header.LP_BEGIN_MOBPOOL.Set(0);
        {
            ServerPacket.Header.LP_MobEnterField.Set(0x00C2);
            ServerPacket.Header.LP_MobLeaveField.Set(0x00C3);
            ServerPacket.Header.LP_MobChangeController.Set(0x00C4);
            ServerPacket.Header.LP_MobCrcKeyChanged.Set(0x00CF);
        }
        ServerPacket.Header.LP_END_MOBPOOL.Set(0);

        // 0x00C5 - 0x00D3
        // C_UNK_Mob::OnPacket
        ServerPacket.Header.LP_BEGIN_MOB.Set(0);
        {
            ServerPacket.Header.LP_MobMove.Set(0x00C5);
            ServerPacket.Header.LP_MobCtrlAck.Set(0x00C6);
            ServerPacket.Header.LP_MobCtrlHint.Set(0x00C7);
            ServerPacket.Header.LP_MobStatSet.Set(0x00C8);
            ServerPacket.Header.LP_MobStatReset.Set(0x00C9);
            ServerPacket.Header.LP_MobSuspendReset.Set(0x00CA);
            ServerPacket.Header.LP_MobAffected.Set(0x00CB);
            ServerPacket.Header.LP_MobDamaged.Set(0x00CC);
            ServerPacket.Header.LP_MobSpecialEffectBySkill.Set(0x00CD);
            ServerPacket.Header.LP_MobHPChange.Set(0x00CE); // not used
            // LP_MobCrcKeyChanged
            ServerPacket.Header.LP_MobHPIndicator.Set(0x00D0);
            ServerPacket.Header.SHOW_MAGNET.Set(0x00D1);
            ServerPacket.Header.LP_MobCatchEffect.Set(0x00D2);
            // 0x00D3 == v186 0x011C
        }
        ServerPacket.Header.LP_END_MOB.Set(0);

        // 0x00D5 - 0x00D7, 0x004D
        // CNpcPool::OnPacket
        ServerPacket.Header.LP_BEGIN_NPCPOOL.Set(0);
        {
            ServerPacket.Header.LP_ImitatedNPCData.Set(0x004D);
            // ServerPacket.Header.LP_LimitedNPCDisableInfo.Set(0);
            ServerPacket.Header.LP_NpcEnterField.Set(0x00D5);
            ServerPacket.Header.LP_NpcLeaveField.Set(0x00D6);
            ServerPacket.Header.LP_NpcChangeController.Set(0x00D7);
        }
        ServerPacket.Header.LP_END_NPCPOOL.Set(0);

        // 0x00D8 - 0x00DA
        // CNpcPool::OnNpcPacket
        ServerPacket.Header.LP_BEGIN_NPC.Set(0);
        {
            ServerPacket.Header.LP_NpcMove.Set(0x00D8);
            ServerPacket.Header.LP_NpcUpdateLimitedInfo.Set(0x00D9);
            ServerPacket.Header.LP_NpcSpecialAction.Set(0x00DA);
        }
        ServerPacket.Header.LP_END_NPC.Set(0);

        // 0x00DB
        // CNpcPool::OnNpcTemplatePacket
        ServerPacket.Header.LP_BEGIN_NPCTEMPLATE.Set(0);
        {
            ServerPacket.Header.LP_NpcSetScript.Set(0x00DB);
        }
        ServerPacket.Header.LP_END_NPCTEMPLATE.Set(0);

        // 0x00DD - 0x00DF
        // CEmployeePool::OnPacket
        ServerPacket.Header.LP_BEGIN_EMPLOYEEPOOL.Set(0);
        {
            ServerPacket.Header.LP_EmployeeEnterField.Set(0x00DD);
            ServerPacket.Header.LP_EmployeeLeaveField.Set(0x00DE);
            ServerPacket.Header.LP_EmployeeMiniRoomBalloon.Set(0x00DF);
        }
        ServerPacket.Header.LP_END_EMPLOYEEPOOL.Set(0);

        // 0x00E0 - 0x00E1
        // CDropPool::OnPacket
        ServerPacket.Header.LP_BEGIN_DROPPOOL.Set(0);
        {
            ServerPacket.Header.LP_DropEnterField.Set(0x00E0);
            ServerPacket.Header.LP_DropLeaveField.Set(0x00E1);
        }
        ServerPacket.Header.LP_END_DROPPOOL.Set(0);

        // 0x00E2 - 0x00E4
        // CMessageBoxPool::OnPacket
        ServerPacket.Header.LP_BEGIN_MESSAGEBOXPOOL.Set(0);
        {
            ServerPacket.Header.LP_CreateMessgaeBoxFailed.Set(0x00E2);
            ServerPacket.Header.LP_MessageBoxEnterField.Set(0x00E3);
            ServerPacket.Header.LP_MessageBoxLeaveField.Set(0x00E4);
        }
        ServerPacket.Header.LP_END_MESSAGEBOXPOOL.Set(0);

        // 0x00E5 - 0x00E6
        // CAffectedAreaPool::OnPacket
        ServerPacket.Header.LP_BEGIN_AFFECTEDAREAPOOL.Set(0);
        {
            ServerPacket.Header.LP_AffectedAreaCreated.Set(0x00E5);
            ServerPacket.Header.LP_AffectedAreaRemoved.Set(0x00E6);
        }
        ServerPacket.Header.LP_END_AFFECTEDAREAPOOL.Set(0);

        // 0x00E7 - 0x00E8
        // CTownPortalPool::OnPacket
        ServerPacket.Header.LP_END_TOWNPORTALPOOL.Set(0);
        {
            ServerPacket.Header.LP_TownPortalCreated.Set(0x00E7);
            ServerPacket.Header.LP_TownPortalRemoved.Set(0x00E8);
        }

        // 0x00E9 - 0x00EC
        // CReactorPool::OnPacket
        ServerPacket.Header.LP_BEGIN_REACTORPOOL.Set(0);
        {
            ServerPacket.Header.LP_ReactorChangeState.Set(0x00E9);
            // ServerPacket.Header.LP_ReactorMove.Set(0x00EA);
            ServerPacket.Header.LP_ReactorEnterField.Set(0x00EB);
            ServerPacket.Header.LP_ReactorLeaveField.Set(0x00EC);
        }
        ServerPacket.Header.LP_END_REACTORPOOL.Set(0);

        // 0x00ED - 0x00F0
        // CField_SnowBall::OnPacket
        {
            ServerPacket.Header.LP_SnowBallState.Set(0x00ED);
            ServerPacket.Header.LP_SnowBallHit.Set(0x00EE);
            ServerPacket.Header.LP_SnowBallMsg.Set(0x00EF);
            ServerPacket.Header.LP_SnowBallTouch.Set(0x00F0);
        }

        // 0x00F1 - 0x00F2
        // CField_Coconut::OnPacket
        {
            ServerPacket.Header.LP_CoconutHit.Set(0x00F1);
            ServerPacket.Header.LP_CoconutScore.Set(0x00F2);
        }

        // 0x00F3 - 0x00F4
        // CField_GuildBoss::OnPacket
        {
            ServerPacket.Header.LP_HealerMove.Set(0x00F3);
            ServerPacket.Header.LP_PulleyStateChange.Set(0x00F4);
        }

        // 0x00F5 - 0x00FC
        // CField_MonsterCarnival::OnPacket
        // CField_MonsterCarnivalRevive::OnPacket
        {
            ServerPacket.Header.LP_MCarnivalEnter.Set(0x00F5);
            ServerPacket.Header.LP_MCarnivalPersonalCP.Set(0x00F6);
            ServerPacket.Header.LP_MCarnivalTeamCP.Set(0x00F7);
            ServerPacket.Header.LP_MCarnivalResultSuccess.Set(0x00F8);
            ServerPacket.Header.LP_MCarnivalResultFail.Set(0x00F9);
            ServerPacket.Header.LP_MCarnivalDeath.Set(0x00FA);
            ServerPacket.Header.LP_MCarnivalMemberOut.Set(0x00FB);
            ServerPacket.Header.LP_MCarnivalGameResult.Set(0x00FC);
        }

        // 0x00FD, 0x007F
        // CField_AriantArena::OnPacket
        {
            ServerPacket.Header.LP_ShowArenaResult.Set(0x007F);
            ServerPacket.Header.LP_ArenaScore.Set(0x00FD);
        }

        // 0x00FE - 0x00FF
        {
            // ???
        }

        // 0x0100
        // CScriptMan::OnPacket
        ServerPacket.Header.LP_BEGIN_SCRIPT.Set(0);
        {
            ServerPacket.Header.LP_ScriptMessage.Set(0x0100);
        }
        ServerPacket.Header.LP_END_SCRIPT.Set(0);

        // 0x0101 - 0x0102
        // CShopDlg::OnPacket
        ServerPacket.Header.LP_BEGIN_SHOP.Set(0);
        {
            ServerPacket.Header.LP_OpenShopDlg.Set(0x0101);
            ServerPacket.Header.LP_ShopResult.Set(0x0102);
        }
        ServerPacket.Header.LP_END_SHOP.Set(0);

        // 0x0103 - 0x0104
        // CAdminShopDlg::OnPacket
        ServerPacket.Header.LP_BEGIN_ADMINSHOP.Set(0);
        {
            ServerPacket.Header.LP_AdminShopResult.Set(0x0103);
            ServerPacket.Header.LP_AdminShopCommodity.Set(0x0104);
        }
        ServerPacket.Header.LP_END_ADMINSHOP.Set(0);

        // 0x0105
        // CTrunkDlg::OnPacket
        {
            ServerPacket.Header.LP_TrunkResult.Set(0x0105);
        }

        // 0x0106 - 0x0107
        // CStoreBankDlg::OnPacket
        ServerPacket.Header.LP_BEGIN_STOREBANK.Set(0);
        {
            ServerPacket.Header.LP_StoreBankGetAllResult.Set(0x0106);
            ServerPacket.Header.LP_StoreBankResult.Set(0x0107);
        }
        ServerPacket.Header.LP_END_STOREBANK.Set(0);

        // 0x0108 - 0x010A
        {
            ServerPacket.Header.LP_RPSGame.Set(0x0108);
            ServerPacket.Header.LP_Messenger.Set(0x0109);
            ServerPacket.Header.LP_MiniRoom.Set(0x010A);
        }

        // 0x010B - 0x010F
        // CField_Tournament::OnPacket
        ServerPacket.Header.LP_BEGIN_TOURNAMENT.Set(0);
        {
            ServerPacket.Header.LP_Tournament.Set(0x010B);
            ServerPacket.Header.LP_TournamentMatchTable.Set(0x010C);
            ServerPacket.Header.LP_TournamentSetPrize.Set(0x010D);
            ServerPacket.Header.LP_TournamentNoticeUEW.Set(0x010E);
            ServerPacket.Header.LP_TournamentAvatarInfo.Set(0x010F); // do nothing
        }
        ServerPacket.Header.LP_END_TOURNAMENT.Set(0);

        // 0x0110 - 0x0111
        // CField_Wedding::OnPacket
        ServerPacket.Header.LP_BEGIN_WEDDING.Set(0);
        {
            ServerPacket.Header.LP_WeddingProgress.Set(0x0110);
            ServerPacket.Header.LP_WeddingCremonyEnd.Set(0x0111);
        }
        ServerPacket.Header.LP_END_WEDDING.Set(0);

        // 0x0112 - 0x0116
        // C_UNK_Pachinko::OnPacket
        ServerPacket.Header.UNK_BEGIN_PACHINKO.Set(0);
        {
            ServerPacket.Header.TIP_BEANS.Set(0x0112);
            ServerPacket.Header.OPEN_BEANS.Set(0x0113);
            ServerPacket.Header.SHOOT_BEANS.Set(0x0114);
            // 0x0115
            ServerPacket.Header.UPDATE_BEANS.Set(0x0116);
        }
        ServerPacket.Header.UNK_END_PACHINKO.Set(0);

        // CParcelDlg::OnPacket
        {
            ServerPacket.Header.LP_Parcel.Set(0x0117); // not checked
        }

        // 0x0118 - 0x0121
        // CCashShop::OnPacket
        ServerPacket.Header.LP_BEGIN_CASHSHOP.Set(0);
        {
            ServerPacket.Header.CS_UPDATE.Set(0x0119);
            ServerPacket.Header.CS_OPERATION.Set(0x011A);
        }
        ServerPacket.Header.LP_END_CASHSHOP.Set(0);

        // 0x0122 - 0x0124
        // CFuncKeyMappedMan::OnPacket
        ServerPacket.Header.LP_BEGIN_FUNCKEYMAPPED.Set(0);
        {
            ServerPacket.Header.LP_FuncKeyMappedInit.Set(0x0122);
            ServerPacket.Header.LP_PetConsumeItemInit.Set(0x0123);
            ServerPacket.Header.LP_PetConsumeMPItemInit.Set(0x0124);
        }
        ServerPacket.Header.LP_END_FUNCKEYMAPPED.Set(0);

        // 0x0125 - 0x0128
        {
            // ???
        }

        // 0x0127 - 0x0129
        // CITC::OnPacket
        ServerPacket.Header.LP_BEGIN_ITC.Set(0);
        {
            ServerPacket.Header.LP_ITCChargeParamResult.Set(0x0127);
            ServerPacket.Header.LP_ITCQueryCashResult.Set(ServerPacket.Header.LP_ITCChargeParamResult.Get() + 1);
            ServerPacket.Header.LP_ITCNormalItemResult.Set(ServerPacket.Header.LP_ITCChargeParamResult.Get() + 2);
        }
        ServerPacket.Header.LP_END_ITC.Set(0);

        // 0x012A - 0x012D
        // CMapleTVMan::OnPacket
        ServerPacket.Header.LP_BEGIN_MAPLETV.Set(0);
        {
            ServerPacket.Header.LP_MapleTVUpdateMessage.Set(0x012A);
            ServerPacket.Header.LP_MapleTVClearMessage.Set(0x012B);
            ServerPacket.Header.LP_MapleTVSendMessageResult.Set(0x012C);
            ServerPacket.Header.LP_BroadSetFlashChangeEvent.Set(0x012D); // do nothing
        }
        ServerPacket.Header.LP_END_MAPLETV.Set(0);
    }

    public static void Set() {
        // ===== Login Server 1 =====
        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0000);
        ServerPacket.Header.LP_WorldInformation.Set(0x0002);
        ServerPacket.Header.LP_SelectWorldResult.Set(0x0003);
        ServerPacket.Header.LP_SelectCharacterResult.Set(0x0004);
        ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0006);
        ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0007);

        ServerPacket.Header.LP_ViewAllCharResult.Set(0x0014);
        ServerPacket.Header.LP_LatestConnectedWorld.Set(0x0016);
        // ===== Change Channel =====
        ServerPacket.Header.LP_MigrateCommand.Set(0x0008);
        // ===== Login Server 2 =====
        ServerPacket.Header.HELLO.Set(0x000E);
        ServerPacket.Header.LOGIN_AUTH.Set(0x0018);
        // ===== Game Server 1 =====
        ServerPacket.Header.LP_InventoryOperation.Set(0x0016);
        ServerPacket.Header.LP_InventoryGrow.Set(ServerPacket.Header.LP_InventoryOperation.Get() + 0x01);
        ServerPacket.Header.LP_StatChanged.Set(ServerPacket.Header.LP_InventoryOperation.Get() + 0x02);
        ServerPacket.Header.LP_BroadcastMsg.Set(0x0037);
        // ===== Cash Shop =====
        // 0x0066 + 1
        ServerPacket.Header.LP_SetField.Set(0x0067);
        ServerPacket.Header.LP_SetITC.Set(ServerPacket.Header.LP_SetField.Get() + 0x01); // 0x0068
        ServerPacket.Header.LP_SetCashShop.Set(ServerPacket.Header.LP_SetField.Get() + 0x02); // 0x0069
        // ===== Game Server 2 =====
        //Header.SERVER_BLOCKED.Set(0x0085);
        //Header.SHOW_EQUIP_EFFECT.Set(0x0086);
        ServerPacket.Header.LP_UserChat.Set(0x0083);
        // ===== Game Server 3 =====
        // 00A3 -> 0083
        //Header.SPAWN_PLAYER.Set(0x00A1);
        //Header.REMOVE_PLAYER_FROM_MAP.Set(0x00A2);
        ServerPacket.Header.LP_UserItemUpgradeEffect.Set(0x0087); // 00A8 -> 0087

        // ===== Mob =====
        ServerPacket.Header.LP_MobEnterField.Set(0x00C2);
        ServerPacket.Header.LP_MobLeaveField.Set(ServerPacket.Header.LP_MobEnterField.Get() + 0x01);
        ServerPacket.Header.LP_MobChangeController.Set(ServerPacket.Header.LP_MobEnterField.Get() + 0x02);
        // ===== Mob Movement ====
        ServerPacket.Header.LP_MobMove.Set(0x00C5);
        ServerPacket.Header.LP_MobCtrlAck.Set(ServerPacket.Header.LP_MobMove.Get() + 0x01);
        // Header.MOVE_MONSTER.Get() + 0x02 は存在しない
        ServerPacket.Header.LP_MobStatSet.Set(ServerPacket.Header.LP_MobMove.Get() + 0x03);
        ServerPacket.Header.LP_MobStatReset.Set(ServerPacket.Header.LP_MobMove.Get() + 0x04);
        // Header.MOVE_MONSTER.Get() + 0x05
        ServerPacket.Header.LP_MobAffected.Set(ServerPacket.Header.LP_MobMove.Get() + 0x06);
        ServerPacket.Header.LP_MobDamaged.Set(ServerPacket.Header.LP_MobMove.Get() + 0x07);
        // Header.MOVE_MONSTER.Get() + 0x08
        // Header.MOVE_MONSTER.Get() + 0x09
        // Header.MOVE_MONSTER.Get() + 0x0A
        ServerPacket.Header.LP_MobHPIndicator.Set(ServerPacket.Header.LP_MobMove.Get() + 0x0B);
        /*
        ServerPacket.Header.SHOW_MAGNET.Set(Header.MOVE_MONSTER.Get() + 0x0C);
        ServerPacket.Header.CATCH_MONSTER.Set(0x0114);
        ServerPacket.Header.MOB_SPEAKING.Set(0x0115);
        // 0x0116 @0116 int,int,int,int, 何らかの変数が更新されるが詳細不明
        ServerPacket.Header.MONSTER_PROPERTIES.Set(0x0117);
        ServerPacket.Header.REMOVE_TALK_MONSTER.Set(0x0118);
        ServerPacket.Header.TALK_MONSTER.Set(0x0119);
         */

        ServerPacket.Header.LP_NpcEnterField.Set(0x00D5);
        ServerPacket.Header.LP_NpcChangeController.Set(0x00D7);
        ServerPacket.Header.LP_ScriptMessage.Set(0x0100); // 00698C63
        ServerPacket.Header.LP_OpenShopDlg.Set(ServerPacket.Header.LP_ScriptMessage.Get() + 0x01);
        ServerPacket.Header.LP_ShopResult.Set(ServerPacket.Header.LP_ScriptMessage.Get() + 0x02);

        //Header.OPEN_STORAGE.Set(0x0104);
        //Header.MERCH_ITEM_MSG.Set(Header.NPC_TALK.Get() + 0x05);
        //Header.MERCH_ITEM_STORE.Set(Header.NPC_TALK.Get() + 0x06);
        //Header.RPS_GAME.Set(Header.NPC_TALK.Get() + 0x07);
        //Header.MESSENGER.Set(Header.NPC_TALK.Get() + 0x08);
        ServerPacket.Header.LP_MiniRoom.Set(ServerPacket.Header.LP_ScriptMessage.Get() + 0x09);

        UpdateHeaderTest();
    }
}
