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
package packet.response;

import server.network.MaplePacket;
import packet.ServerPacket;
import server.shops.HiredMerchant;

/**
 *
 * @author Riremito
 */
public class ResCEmployeePool {

    public static MaplePacket EmployeeEnterField(HiredMerchant hm) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_EmployeeEnterField);

        sp.Encode4(hm.getOwnerId()); // dwEmployerID
        sp.Encode4(hm.getItemId()); // dwTemplateID (Employee NPC Look)
        // CEmployee::Init
        {
            sp.Encode2(hm.getPosition().x); // m_ptPos.x
            sp.Encode2(hm.getPosition().y); // m_ptPos.y
            sp.Encode2(hm.getFH()); // Foothold
            sp.EncodeStr(hm.getOwnerName());
        }
        // CEmployee::SetBalloon
        {
            // TestHelper.addInteraction(mplew, hm);
            int m_nMiniRoomType = hm.getGameType();
            sp.Encode1(m_nMiniRoomType); // m_nMiniRoomType
            if (m_nMiniRoomType != 0) {
                sp.Encode4(hm.getStoreId()); // m_dwMiniRoomSN
                sp.EncodeStr(hm.getDescription());
                sp.Encode1(hm.getItemSubType() % 100); // nSpec (Store Look)
                sp.Encode1(0); // nCurUsers
                sp.Encode1(3); // nMaxUsers
            }
        }
        return sp.get();
    }

    public static MaplePacket EmployeeLeaveField(HiredMerchant hm) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_EmployeeLeaveField);

        sp.Encode4(hm.getOwnerId());
        return sp.get();
    }

}
