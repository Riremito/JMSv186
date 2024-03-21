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
package packet.server.response;

import handling.MaplePacket;
import packet.server.ServerPacket;
import server.maps.MapleDynamicPortal;

/**
 *
 * @author Riremito
 */
public class ItemResponse {

    public static MaplePacket CreatePinkBeanEventPortal(MapleDynamicPortal dynamic_portal) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_JMS_PINKBEAN_PORTAL_CREATE);
        sp.Encode1(1);
        sp.Encode4(dynamic_portal.getItemID()); // item id
        sp.Encode4(dynamic_portal.getObjectId()); // object id
        sp.Encode2(dynamic_portal.getPosition().x);
        sp.Encode2(dynamic_portal.getPosition().y);
        sp.Encode4(0);
        sp.Encode4(0);
        sp.Encode2(dynamic_portal.getPosition().x);
        sp.Encode2(dynamic_portal.getPosition().y);
        return sp.Get();
    }

    public static MaplePacket sendMesobagSuccess(int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MesoGive_Succeeded);
        sp.Encode4(mesos);
        return sp.Get();
    }

    public static MaplePacket sendMesobagFailed() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_MesoGive_Failed);
        return sp.Get();
    }

    public static MaplePacket RandomMesoBagSuccess(byte type, int mesos) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Random_Mesobag_Succeed);
        sp.Encode1(type);
        sp.Encode4(mesos);
        return sp.Get();
    }

    public static MaplePacket RandomMesoBagFailed() {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_Random_Mesobag_Failed);
        return sp.Get();
    }

}
