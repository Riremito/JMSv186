/*
 * Copyright (C) 2024 Riremito
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
 */
package packet;

import packet.ClientPacket.Header;

/**
 *
 * @author Riremito
 */
public class CMS_v85_1_CP {

    public static void Set() {
        Header.CP_CheckPassword.Set(0x0001);
        Header.CP_WorldInfoRequest.Set(0x0002);
        Header.CP_CheckUserLimit.Set(0x0005);
        Header.CP_SelectWorld.Set(0x0009);
        Header.CP_SelectCharacter.Set(0x000A);
        Header.CP_MigrateIn.Set(0x000B);
        Header.CP_CheckDuplicatedID.Set(0x000C);
        Header.CP_CreateNewCharacter.Set(0x0011);
        Header.CP_CreateSecurityHandle.Set(0x0020);

        // test
        Header.CP_UserChat.Set(0x0032);
        Header.CP_UserSelectNpc.Set(0x003B);
        Header.CP_UserPortalScriptRequest.Set(0x006C);
        Header.CP_UserPortalTeleportRequest.Set(Header.CP_UserPortalScriptRequest.Get() + 1);

        Header.CP_UserTransferFieldRequest.Set(0x0023);
        Header.CP_UserTransferChannelRequest.Set(Header.CP_UserTransferFieldRequest.Get() + 1);
        Header.CP_UserMigrateToCashShopRequest.Set(Header.CP_UserTransferFieldRequest.Get() + 2);
    }
}
