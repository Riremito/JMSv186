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
public class v165_0_SP {

    public static void Set() {
        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0000);
        ServerPacket.Header.LP_WorldInformation.Set(0x0002);
        ServerPacket.Header.LP_SelectWorldResult.Set(0x0003);
        ServerPacket.Header.LP_SelectCharacterResult.Set(0x0004);
        ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0005);
        ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0006);
        ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0007);
        ServerPacket.Header.LP_MigrateCommand.Set(0x0008);

        // 0x0016 - 0x0066 -> +0, add 2
        // CWvsContext::OnPacket
        ServerPacket.Header.LP_BEGIN_CHARACTERDATA.Set(0);
        {
            ServerPacket.Header.LP_InventoryOperation.Set(0x0016); // OK
            ServerPacket.Header.LP_InventoryGrow.Set(0x0017);
            ServerPacket.Header.LP_StatChanged.Set(0x0018);
            ServerPacket.Header.LP_TemporaryStatSet.Set(0x0019);
            ServerPacket.Header.LP_TemporaryStatReset.Set(0x001A);
            ServerPacket.Header.LP_ChangeSkillRecordResult.Set(0x001B);
            ServerPacket.Header.LP_GivePopularityResult.Set(0x001D);
            ServerPacket.Header.LP_Message.Set(0x001E);
            ServerPacket.Header.LP_MapTransferResult.Set(0x0020);
            ServerPacket.Header.LP_InitialQuizStart.Set(0x0021);
            ServerPacket.Header.LP_QuestClear.Set(0x0027);
            ServerPacket.Header.LP_EntrustedShopCheckResult.Set(0x0028);
            ServerPacket.Header.LP_SkillLearnItemResult.Set(0x0029);
            // not used
            ServerPacket.Header.LP_CharacterInfo.Set(0x002B);

            ServerPacket.Header.LP_BroadcastMsg.Set(0x0037);
        }
        ServerPacket.Header.LP_END_CHARACTERDATA.Set(0);

        // 0x0067 - 0x0069 -> +2
        // CStage::OnPacket
        ServerPacket.Header.LP_BEGIN_STAGE.Set(0);
        {
            ServerPacket.Header.LP_SetField.Set(0x0069);
            ServerPacket.Header.LP_SetITC.Set(0x006A);
            ServerPacket.Header.LP_SetCashShop.Set(0x006B);
        }

        // 0x006A - 0x006B -> + 2
        // CMapLoadable::OnPacket
        ServerPacket.Header.LP_BEGIN_MAP.Set(0);
        {
            ServerPacket.Header.LP_SetBackgroundEffect.Set(0x006C);
            // ServerPacket.Header.LP_SetMapObjectVisible.Set(0);
            ServerPacket.Header.LP_ClearBackgroundEffect.Set(0x006D);
        }

        ServerPacket.Header.LP_UserChat.Set(0x0085);

        // 0x0097 - 0x00AA -> +2
        // C_UNK_UserRemote::OnPacket (JMS)
        ServerPacket.Header.LP_BEGIN_USERREMOTE.Set(0);
        {
            ServerPacket.Header.LP_UserMove.Set(0x0097);
            ServerPacket.Header.LP_UserMeleeAttack.Set(ServerPacket.Header.LP_UserMove.Get() + 1);
            ServerPacket.Header.LP_UserShootAttack.Set(ServerPacket.Header.LP_UserMove.Get() + 2);
            ServerPacket.Header.LP_UserMagicAttack.Set(ServerPacket.Header.LP_UserMove.Get() + 3);
            ServerPacket.Header.LP_UserBodyAttack.Set(ServerPacket.Header.LP_UserMove.Get() + 4);
            ServerPacket.Header.LP_UserSkillPrepare.Set(ServerPacket.Header.LP_UserMove.Get() + 5);
            ServerPacket.Header.LP_UserSkillCancel.Set(ServerPacket.Header.LP_UserMove.Get() + 6);
            ServerPacket.Header.LP_UserHit.Set(ServerPacket.Header.LP_UserMove.Get() + 7);
            ServerPacket.Header.LP_UserEmotion.Set(ServerPacket.Header.LP_UserMove.Get() + 8);
            ServerPacket.Header.LP_UserSetActiveEffectItem.Set(ServerPacket.Header.LP_UserMove.Get() + 9);
            ServerPacket.Header.LP_UserShowUpgradeTombEffect.Set(ServerPacket.Header.LP_UserMove.Get() + 10);
            ServerPacket.Header.LP_UserSetActivePortableChair.Set(ServerPacket.Header.LP_UserMove.Get() + 11);
            ServerPacket.Header.LP_UserAvatarModified.Set(ServerPacket.Header.LP_UserMove.Get() + 12);
            ServerPacket.Header.LP_UserEffectRemote.Set(ServerPacket.Header.LP_UserMove.Get() + 13);
            ServerPacket.Header.LP_UserTemporaryStatSet.Set(ServerPacket.Header.LP_UserMove.Get() + 14);
            ServerPacket.Header.LP_UserTemporaryStatReset.Set(ServerPacket.Header.LP_UserMove.Get() + 15);
            ServerPacket.Header.LP_UserHP.Set(ServerPacket.Header.LP_UserMove.Get() + 16);
            ServerPacket.Header.LP_UserGuildNameChanged.Set(ServerPacket.Header.LP_UserMove.Get() + 17);
            ServerPacket.Header.LP_UserGuildMarkChanged.Set(ServerPacket.Header.LP_UserMove.Get() + 18);
            ServerPacket.Header.LP_UserThrowGrenade.Set(ServerPacket.Header.LP_UserMove.Get() + 19);
        }
        ServerPacket.Header.LP_END_USERREMOTE.Set(0);

        // 0x00AB - 0x00C0 -> +2, add 2
        // C_UNK_UserLocal::OnPacket
        ServerPacket.Header.LP_BEGIN_USERLOCAL.Set(0);
        {
            ServerPacket.Header.LP_UserSitResult.Set(0x00AB);
            ServerPacket.Header.LP_UserEmotionLocal.Set(ServerPacket.Header.LP_UserSitResult.Get() + 1);
            ServerPacket.Header.LP_UserTeleport.Set(ServerPacket.Header.LP_UserSitResult.Get() + 2);
            ServerPacket.Header.LP_Premium.Set(ServerPacket.Header.LP_UserSitResult.Get() + 3); // not used
            ServerPacket.Header.LP_MesoGive_Succeeded.Set(ServerPacket.Header.LP_UserSitResult.Get() + 4);
            ServerPacket.Header.LP_MesoGive_Failed.Set(ServerPacket.Header.LP_UserSitResult.Get() + 5);
            ServerPacket.Header.LP_UserQuestResult.Set(ServerPacket.Header.LP_UserSitResult.Get() + 6);
            ServerPacket.Header.LP_NotifyHPDecByField.Set(ServerPacket.Header.LP_UserSitResult.Get() + 7);
            ServerPacket.Header.LP_UserPetSkillChanged.Set(ServerPacket.Header.LP_UserSitResult.Get() + 8);
            ServerPacket.Header.LP_UserBalloonMsg.Set(ServerPacket.Header.LP_UserSitResult.Get() + 9);
            ServerPacket.Header.LP_PlayEventSound.Set(ServerPacket.Header.LP_UserSitResult.Get() + 10);
            // 0x00B6 == v186 0x00E8
            ServerPacket.Header.LP_UserMakerResult.Set(ServerPacket.Header.LP_UserSitResult.Get() + 12);
            ServerPacket.Header.LP_UserOpenConsultBoard.Set(ServerPacket.Header.LP_UserSitResult.Get() + 13);
            ServerPacket.Header.LP_UserOpenClassCompetitionPage.Set(ServerPacket.Header.LP_UserSitResult.Get() + 14);
            // 0x00BA ???
            ServerPacket.Header.LP_SetStandAloneMode.Set(ServerPacket.Header.LP_UserSitResult.Get() + 16);
            // 0x00BC ???
            ServerPacket.Header.TAMA_BOX_SUCCESS.Set(ServerPacket.Header.LP_UserSitResult.Get() + 18);
            ServerPacket.Header.TAMA_BOX_FAILURE.Set(ServerPacket.Header.LP_UserSitResult.Get() + 19);
            ServerPacket.Header.LP_UserRandomEmotion.Set(ServerPacket.Header.LP_UserSitResult.Get() + 20);
            ServerPacket.Header.LP_SkillCooltimeSet.Set(ServerPacket.Header.LP_UserSitResult.Get() + 21);
            // something 2 headers are added
        }
        ServerPacket.Header.LP_END_USERLOCAL.Set(0);

        // NPC v165
        {
            ServerPacket.Header.LP_NpcEnterField.Set(0x00DA);
            ServerPacket.Header.LP_NpcLeaveField.Set(0x00DB);
            ServerPacket.Header.LP_NpcChangeController.Set(0x00DC);
            ServerPacket.Header.LP_ScriptMessage.Set(0x0108);
        }

    }

}
