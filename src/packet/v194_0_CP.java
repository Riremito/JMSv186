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
public class v194_0_CP {

    public static void Set() {
        ClientPacket.Header.CP_CheckPassword.Set(0x0001);
        ClientPacket.Header.CP_WorldInfoRequest.Set(0x0003);
        ClientPacket.Header.CP_SelectWorld.Set(0x0004);
        ClientPacket.Header.CP_CheckUserLimit.Set(0x0005);
        ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
        ClientPacket.Header.CP_MigrateIn.Set(0x0007);
        ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
        ClientPacket.Header.CP_ViewAllChar.Set(0x000A);
        ClientPacket.Header.CP_CreateNewCharacter.Set(0x000B);
        ClientPacket.Header.CP_DeleteCharacter.Set(0x000D);

        ClientPacket.Header.CP_CreateSecurityHandle.Set(0x0018); // MapLogin, name wrong?

        ClientPacket.Header.CP_UserTransferFieldRequest.Set(0x001B);
        ClientPacket.Header.CP_UserTransferChannelRequest.Set(0x001C);
        ClientPacket.Header.CP_UserMigrateToCashShopRequest.Set(0x001D);
        ClientPacket.Header.CP_UserMove.Set(0x001E);
        ClientPacket.Header.CP_UserChat.Set(0x002A);
        ClientPacket.Header.CP_UserPortalScriptRequest.Set(0x006E);
        ClientPacket.Header.CP_UserPortalTeleportRequest.Set(0x0070);
        ClientPacket.Header.CP_UserMigrateToITCRequest.Set(0x00C1);

    }
}
