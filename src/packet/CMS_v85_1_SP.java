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

/**
 *
 * @author Riremito
 */
public class CMS_v85_1_SP {

    public static void Set() {
        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0000);
        ServerPacket.Header.LP_WorldInformation.Set(0x0009);
        ServerPacket.Header.LP_SelectWorldResult.Set(0x000A);
        ServerPacket.Header.LP_SelectCharacterResult.Set(0x0018);
        ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x000C);
        ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0011);
        ServerPacket.Header.LP_DeleteCharacterResult.Set(0x0012);
        ServerPacket.Header.LP_MigrateCommand.Set(0x000B);
        ServerPacket.Header.LOGIN_AUTH.Set(0x001F);

        ServerPacket.Header.LP_SetField.Set(0x008A);
    }
}
