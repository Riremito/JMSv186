package packet;

// BB後でまともに動作するバージョン
// v187.0は動作しませんでした
public class v188_0_SP {

    public static void Set() {
        //test
        // MTS
        // CITC::OnPacket
        ServerPacket.Header.LP_BEGIN_ITC.Set(0);
        {
            // CITC::OnChargeParamResult
            ServerPacket.Header.LP_ITCChargeParamResult.Set(0x0194);
            // CITC::OnQueryCashResult
            ServerPacket.Header.LP_ITCQueryCashResult.Set(0x0195);
            // CITC::OnNormalItemResult
            ServerPacket.Header.LP_ITCNormalItemResult.Set(0x0196);
        }
        ServerPacket.Header.LP_END_ITC.Set(0);

        // CClientSocket::ProcessPacket
        {
            ServerPacket.Header.LP_MigrateCommand.Set(0x0008);
            ServerPacket.Header.LP_AliveReq.Set(0x0009);
            ServerPacket.Header.LP_SecurityPacket.Set(0x000C);
        }

        // CLogin::OnPacket, 005E2E80
        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0000); // OK
        ServerPacket.Header.LP_CheckPinCodeResult.Set(0x0016); // 違うかも
        ServerPacket.Header.LOGIN_AUTH.Set(0x0015); // v188 005E2F84

        ServerPacket.Header.LP_BEGIN_SOCKET.Set(0);
        {
            // Header.LP_CheckPasswordResult.Set(0x0000);
            ServerPacket.Header.LP_WorldInformation.Set(ServerPacket.Header.LP_CheckPasswordResult.Get() + 2);
            ServerPacket.Header.LP_SelectWorldResult.Set(ServerPacket.Header.LP_CheckPasswordResult.Get() + 3);
            ServerPacket.Header.LP_SelectCharacterResult.Set(ServerPacket.Header.LP_CheckPasswordResult.Get() + 4);
            ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(ServerPacket.Header.LP_CheckPasswordResult.Get() + 5);
            ServerPacket.Header.LP_CreateNewCharacterResult.Set(ServerPacket.Header.LP_CheckPasswordResult.Get() + 6);
            ServerPacket.Header.LP_DeleteCharacterResult.Set(ServerPacket.Header.LP_CheckPasswordResult.Get() + 7);
        }

        // CStage::OnPacket, 0073BAC0
        ServerPacket.Header.LP_SetField.Set(0x007E); // OK
        ServerPacket.Header.LP_BEGIN_STAGE.Set(0);
        {
            // Header.LP_SetField.Set(0x007E);
            ServerPacket.Header.LP_SetITC.Set(ServerPacket.Header.LP_SetField.Get() + 0x01);
            ServerPacket.Header.LP_SetCashShop.Set(ServerPacket.Header.LP_SetField.Get() + 0x02);
        }

        // NPC
        ServerPacket.Header.LP_NpcEnterField.Set(0x0125); // OK
        ServerPacket.Header.LP_BEGIN_NPCPOOL.Set(0);
        {
            // Header.LP_NpcEnterField.Set(0x0125);
            ServerPacket.Header.LP_NpcLeaveField.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 1);
            ServerPacket.Header.LP_NpcChangeController.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 2);
            // CNpcPool::OnNpcPacket
            ServerPacket.Header.LP_BEGIN_NPC.Set(0);
            {
                ServerPacket.Header.LP_NpcMove.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 3);
                ServerPacket.Header.LP_NpcUpdateLimitedInfo.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 4);
                ServerPacket.Header.LP_NpcSpecialAction.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 5);
            }
            ServerPacket.Header.LP_END_NPC.Set(0);
            // CNpcPool::OnNpcTemplatePacket
            ServerPacket.Header.LP_BEGIN_NPCTEMPLATE.Set(0);
            {
                ServerPacket.Header.LP_NpcSetScript.Set(ServerPacket.Header.LP_NpcEnterField.Get() + 6);
            }
            ServerPacket.Header.LP_END_NPCTEMPLATE.Set(0);
        }

        // CScriptMan::OnPacket, NPC会話
        ServerPacket.Header.LP_ScriptMessage.Set(0x015B);
        ServerPacket.Header.LP_OpenShopDlg.Set(ServerPacket.Header.LP_ScriptMessage.Get() + 1);
        ServerPacket.Header.LP_ShopResult.Set(ServerPacket.Header.LP_ScriptMessage.Get() + 2);

        // test
        // CWvsContext::OnPacket
        ServerPacket.Header.LP_InventoryOperation.Set(0x0018);
        ServerPacket.Header.LP_StatChanged.Set(ServerPacket.Header.LP_InventoryOperation.Get() + 2);
        //Header.LP_CharacterInfo.Set(Header.LP_InventoryOperation.Get() + 26);

        ServerPacket.Header.LP_BroadcastMsg.Set(0x003D); // v188
        ServerPacket.Header.LP_UserChat.Set(0x00A4); // v188 test v186+1

        ServerPacket.Header.LP_UserChatNLCPQ.Set(ServerPacket.Header.LP_UserChat.Get() + 1);
        ServerPacket.Header.LP_UserADBoard.Set(ServerPacket.Header.LP_UserChat.Get() + 2);
        ServerPacket.Header.LP_UserMiniRoomBalloon.Set(ServerPacket.Header.LP_UserChat.Get() + 3);
        ServerPacket.Header.LP_UserConsumeItemEffect.Set(ServerPacket.Header.LP_UserChat.Get() + 4);
        ServerPacket.Header.LP_UserItemUpgradeEffect.Set(ServerPacket.Header.LP_UserChat.Get() + 5);
        ServerPacket.Header.LP_UserItemHyperUpgradeEffect.Set(ServerPacket.Header.LP_UserChat.Get() + 6);
        ServerPacket.Header.LP_UserItemOptionUpgradeEffect.Set(ServerPacket.Header.LP_UserChat.Get() + 7);
        ServerPacket.Header.LP_UserItemReleaseEffect.Set(ServerPacket.Header.LP_UserChat.Get() + 8);
        ServerPacket.Header.LP_UserItemUnreleaseEffect.Set(ServerPacket.Header.LP_UserChat.Get() + 9);

        ServerPacket.Header.LP_DropEnterField.Set(0x0130); // v188
        ServerPacket.Header.LP_DropLeaveField.Set(ServerPacket.Header.LP_DropEnterField.Get() + 1);

        //Header.LP_MobEnterField.Set(0x010B);
        //Header.LP_MobLeaveField.Set(0x010C);
        // 末尾
        ServerPacket.Header.LP_NO.Set(0x19A);

        //UpdateHeader();
    }
}
