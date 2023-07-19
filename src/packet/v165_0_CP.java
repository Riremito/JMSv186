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
public class v165_0_CP {

    public static void Set() {
        ClientPacket.Header.CP_CheckPassword.Set(0x0001);
        ClientPacket.Header.CP_WorldInfoRequest.Set(0x0003);
        ClientPacket.Header.CP_SelectWorld.Set(0x0004);
        ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
        ClientPacket.Header.CP_MigrateIn.Set(0x0007);
        ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
        ClientPacket.Header.CP_CreateNewCharacter.Set(0x000B);
        ClientPacket.Header.CP_DeleteCharacter.Set(0x000C);
        ClientPacket.Header.CP_ExceptionLog.Set(0x000E);
        ClientPacket.Header.REACHED_LOGIN_SCREEN.Set(0x0018);
        // -1?
        ClientPacket.Header.CP_UserTransferFieldRequest.Set(0x001B);// -1
        ClientPacket.Header.CP_UserTransferChannelRequest.Set(0x001C); // -1
        ClientPacket.Header.CP_UserMigrateToCashShopRequest.Set(0x001D);
        ClientPacket.Header.CP_UserMove.Set(0x001E); // -1

        ClientPacket.Header.CP_UserMeleeAttack.Set(0x0022);

        ClientPacket.Header.CP_UserChat.Set(0x0026); // -1

        ClientPacket.Header.CP_UserSelectNpc.Set(0x002F);
        ClientPacket.Header.CP_UserScriptMessageAnswer.Set(0x0031);
        ClientPacket.Header.CP_UserShopRequest.Set(0x0032);
        ClientPacket.Header.CP_UserTrunkRequest.Set(0x0033);

        ClientPacket.Header.CP_UserChangeSlotPositionRequest.Set(0x003C);
        ClientPacket.Header.CP_UserStatChangeItemUseRequest.Set(0x003D);

        ClientPacket.Header.CP_UserConsumeCashItemUseRequest.Set(0x0044);

        ClientPacket.Header.CP_UserPortalScrollUseRequest.Set(0x004A);
        ClientPacket.Header.CP_UserUpgradeItemUseRequest.Set(0x004B);
        ClientPacket.Header.CP_UserAbilityUpRequest.Set(0x004C);
        ClientPacket.Header.CP_UserAbilityMassUpRequest.Set(0x004D);
        ClientPacket.Header.CP_UserSkillUpRequest.Set(0x004F);
        ClientPacket.Header.CP_UserSkillUseRequest.Set(0x0050);
        ClientPacket.Header.CP_UserSkillCancelRequest.Set(0x0051);
        ClientPacket.Header.CP_UserSkillPrepareRequest.Set(0x0052);
        ClientPacket.Header.CP_UserDropMoneyRequest.Set(0x0053);

        ClientPacket.Header.CP_UserPortalScriptRequest.Set(0x0059); // -1
        ClientPacket.Header.CP_UserPortalTeleportRequest.Set(0x005A); // -1
        ClientPacket.Header.CP_UserMapTransferRequest.Set(0x005B); // -1

        ClientPacket.Header.CP_UserExpUpItemUseRequest.Set(0x0067);
        ClientPacket.Header.CP_UserTempExpUseRequest.Set(0x0068);

        ClientPacket.Header.CP_UserMigrateToITCRequest.Set(0x0092); // +1
    }
}
