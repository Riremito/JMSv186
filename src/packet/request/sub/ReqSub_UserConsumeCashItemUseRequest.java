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
package packet.request.sub;

import client.MapleCharacter;
import client.inventory.MapleInventoryType;
import config.ServerConfig;
import debug.Debug;
import packet.ClientPacket;
import packet.response.ResCUser;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class ReqSub_UserConsumeCashItemUseRequest {

    public static boolean OnUserConsumeCashItemUseRequestInternal(MapleCharacter chr, MapleMap map, ClientPacket cp) {
        int timestamp = ServerConfig.JMS180orLater() ? cp.Decode4() : 0;
        short cash_item_slot = cp.Decode2();
        int cash_item_id = cp.Decode4();

        Runnable item_use = chr.checkItemSlot(MapleInventoryType.CASH, cash_item_slot, cash_item_id);
        if (item_use == null) {
            Debug.ErrorLog("OnUserConsumeCashItemUseRequest : invalid item.");
            return true;
        }

        int cash_item_type = cash_item_id / 10000;

        switch (cash_item_type) {
            case 537: // 5370000
            {
                String message = cp.DecodeStr();
                chr.setADBoard(message);
                map.broadcastMessage(ResCUser.UserADBoard(chr));
                //item_use.run();
                return true;
            }
            default: {
                break;
            }
        }

        // not coded.
        Debug.ErrorLog("OnUserConsumeCashItemUseRequest : not coded yet. type = " + cash_item_type);
        return false;
    }
}
