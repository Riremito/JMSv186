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
        ServerPacket.Header.LP_StatChanged.Set(0x001A);
        ServerPacket.Header.LP_ChangeSkillRecordResult.Set(0x001F);
        ServerPacket.Header.LP_SetField.Set(0x0088);
        ServerPacket.Header.LP_SetITC.Set(0x0089);
        ServerPacket.Header.LP_SetCashShop.Set(0x008A);

        ServerPacket.Header.LP_FuncKeyMappedInit.Set(0x019E); // キー設定初期化
        //ServerPacket.Header.LP_PetConsumeItemInit.Set(0x019F);
        //ServerPacket.Header.LP_PetConsumeMPItemInit.Set(0x019A);

    }
}
