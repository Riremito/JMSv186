/*
 * Copyright (C) 2025 Riremito
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
package tacos.packet.response;

import tacos.packet.ServerPacket;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCAdminShopDlg {

    // CAdminShopDlg::OnPacket, not tested.
    public static ServerPacket AdminShopResult(int result) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AdminShopResult);

        sp.Encode1(4);
        sp.Encode1(result);
        return sp;
    }

    // CAdminShopDlg::OnPacket, not tested.
    public static ServerPacket AdminShopCommodity(int npc_id) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_AdminShopCommodity);

        sp.Encode4(npc_id);
        sp.Encode2(0); // count
        return sp;
    }
}
