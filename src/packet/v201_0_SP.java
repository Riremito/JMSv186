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
public class v201_0_SP {

    public static void Set() {
        ServerPacket.Header.LP_MigrateCommand.Set(0x000B);
        ServerPacket.Header.LP_AliveReq.Set(0x000C);
        ServerPacket.Header.LP_SecurityPacket.Set(0x000F);

        ServerPacket.Header.LP_CheckPasswordResult.Set(0x0001);
        ServerPacket.Header.LP_GuestIDLoginResult.Set(0x0002);
        ServerPacket.Header.LP_WorldInformation.Set(0x0003);
        ServerPacket.Header.LP_SelectWorldResult.Set(0x0005);
        ServerPacket.Header.LP_SelectCharacterResult.Set(0x0007);
        ServerPacket.Header.LP_CheckDuplicatedIDResult.Set(0x0008);
        ServerPacket.Header.LP_CreateNewCharacterResult.Set(0x0009);
        ServerPacket.Header.LP_DeleteCharacterResult.Set(0x000A);
        ServerPacket.Header.LP_ViewAllCharResult.Set(0x0014);
        ServerPacket.Header.LP_LatestConnectedWorld.Set(0x0016);
        ServerPacket.Header.LP_RecommendWorldMessage.Set(0x0018);
        ServerPacket.Header.LOGIN_AUTH.Set(0x0019); // OK

        ServerPacket.Header.LP_SetField.Set(0x00A1); // ゲームサーバーへ
        ServerPacket.Header.LP_SetITC.Set(0x00A2); // MTSサーバーへ
        ServerPacket.Header.LP_SetCashShop.Set(0x00A3); // ポイントショップサーバーへ
    }
}
