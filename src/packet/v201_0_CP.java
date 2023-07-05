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
public class v201_0_CP {

    public static void Set() {
        // @0001 03 C9 00 00 00, region version packet
        ClientPacket.Header.CP_CheckPassword.Set(0x0002);
        ClientPacket.Header.CP_WorldInfoRequest.Set(0x0004);
        //ClientPacket.Header.CP_SelectWorld.Set(0x0004);
        //ClientPacket.Header.CP_CheckUserLimit.Set(0x0005);
        //ClientPacket.Header.CP_SelectCharacter.Set(0x0006);
        //ClientPacket.Header.CP_MigrateIn.Set(0x0007);
        ClientPacket.Header.CP_CheckDuplicatedID.Set(0x0008);
        ClientPacket.Header.CP_ViewAllChar.Set(0x000B);
        //ClientPacket.Header.CP_CreateNewCharacter.Set(0x000B);
        //ClientPacket.Header.CP_DeleteCharacter.Set(0x000D);
        //ClientPacket.Header.CP_ExceptionLog.Set(0x0010);
        ClientPacket.Header.CP_CreateSecurityHandle.Set(0x0019); // MapLogin, name wrong?
    }
}
