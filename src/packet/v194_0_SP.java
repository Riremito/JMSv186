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

        ServerPacket.Header.LP_UserChat.Set(0x00B0); // チャット

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
        }

        // NPC v194
        {
            ServerPacket.Header.LP_ImitatedNPCData.Set(0x0058);
            ServerPacket.Header.LP_LimitedNPCDisableInfo.Set(0x0059);
            ServerPacket.Header.LP_NpcEnterField.Set(0x013F); // NPC召喚
            ServerPacket.Header.LP_NpcLeaveField.Set(0x0140); // NPC消滅
            ServerPacket.Header.LP_NpcChangeController.Set(0x0141);
        }
        // Drop v194 test
        {
            ServerPacket.Header.LP_DropEnterField.Set(0x014A);
            // LP_DropReleaseAllFreeze is not used
            ServerPacket.Header.LP_DropLeaveField.Set(0x014C);
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

    }
}
