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

import packet.ServerPacket.Header;

/**
 *
 * @author Riremito
 */
public class CMS_v85_1_SP {

    public static void Set() {
        // Login
        Header.LP_CheckPasswordResult.Set(0x0000);
        Header.LP_CheckUserLimitResult.Set(0x0006);
        Header.LP_WorldInformation.Set(0x0009);
        Header.LP_SelectWorldResult.Set(0x000A);
        Header.LP_SelectCharacterResult.Set(0x000B);
        Header.LP_CheckDuplicatedIDResult.Set(0x000C);
        Header.LP_CreateNewCharacterResult.Set(0x0011);
        Header.LP_DeleteCharacterResult.Set(0x0012);
        Header.LP_MigrateCommand.Set(0x0018);
        Header.LOGIN_AUTH.Set(0x001F);

        // 0021 - 0089 CWvsContext::OnPacket
        // 008A - 008C CStage::OnPacket
        Header.LP_SetField.Set(0x008A);
        Header.LP_SetITC.Set(Header.LP_SetField.Get() + 1);
        Header.LP_SetCashShop.Set(Header.LP_SetField.Get() + 2);

        // CField::OnPacket
        // CNpcPool::OnPacket
        Header.LP_ImitatedNPCData.Set(0x005D);
        Header.LP_LimitedNPCDisableInfo.Set(0x005E);
        Header.LP_NpcEnterField.Set(0x012D);
        Header.LP_NpcLeaveField.Set(0x012E);
        Header.LP_NpcChangeController.Set(0x012F);
        Header.LP_NpcMove.Set(0x0130);
        Header.LP_NpcUpdateLimitedInfo.Set(0x0131);
        Header.LP_NpcSpecialAction.Set(0x0133);
        Header.LP_NpcSetScript.Set(0x0134);
        // CScriptMan::OnPacket
        Header.LP_ScriptMessage.Set(0x0174);
    }
}
