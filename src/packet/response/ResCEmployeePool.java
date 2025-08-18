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
        sp.EncodeBuffer(CEmployee_Init(hm));
        sp.EncodeBuffer(CEmployee_SetBalloon(hm));
        return sp.get();
    }

    public static MaplePacket EmployeeLeaveField(HiredMerchant hm) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_EmployeeLeaveField);

        sp.Encode4(hm.getOwnerId());
        return sp.get();
    }

    public static MaplePacket EmployeeMiniRoomBalloon(HiredMerchant hm) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_EmployeeMiniRoomBalloon);

        sp.Encode4(hm.getOwnerId());
        sp.EncodeBuffer(CEmployee_SetBalloon(hm));
        return sp.get();
    }

    private static byte[] CEmployee_Init(HiredMerchant hm) {
        ServerPacket data = new ServerPacket();

        data.Encode2(hm.getPosition().x); // m_ptPos.x
        data.Encode2(hm.getPosition().y); // m_ptPos.y
        data.Encode2(hm.getFH()); // Foothold
        data.EncodeStr(hm.getOwnerName());
        return data.get().getBytes();
    }

    private static byte[] CEmployee_SetBalloon(HiredMerchant hm) {
        ServerPacket data = new ServerPacket();

        int m_nMiniRoomType = hm.getGameType();
        data.Encode1(m_nMiniRoomType); // m_nMiniRoomType
        if (m_nMiniRoomType != 0) {
            data.Encode4(hm.getStoreId()); // m_dwMiniRoomSN
            data.EncodeStr(hm.getDescription());
            data.Encode1(hm.getItemSubType() % 100); // nSpec (Store Look)
            data.Encode1(hm.getSize()); // nCurUsers
            data.Encode1(hm.getMaxSize()); // nMaxUsers
        }
        return data.get().getBytes();
    }

}
