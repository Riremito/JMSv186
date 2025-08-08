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
import handling.channel.handler.PlayerHandler;
import packet.ClientPacket;
import packet.request.ReqCUser_Pet;
import packet.response.ResCUser;
import server.maps.FieldLimitType;
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
            case 504: // 5040000
            {
                byte action = cp.Decode1();
                int map_id = 999999999;
                MapleCharacter target_chr = null;
                if (action == 0) {
                    map_id = cp.Decode4();
                } else {
                    String target_name = cp.DecodeStr();
                    target_chr = chr.getClient().getChannelServer().getPlayerStorage().getCharacterByName(target_name);
                    if (target_chr == null) {
                        return false;
                    }
                    map_id = target_chr.getMap().getId();
                }
                if (FieldLimitType.VipRock.check(chr.getClient().getChannelServer().getMapFactory().getMap(map_id).getFieldLimit())) {
                    return false;
                }
                item_use.run();
                if (action == 0) {
                    PlayerHandler.ChangeMap(chr.getClient(), map_id);
                } else {
                    chr.changeMap(target_chr.getMap(), target_chr.getMap().findClosestSpawnpoint(target_chr.getPosition()));
                }
                return true;
            }
            case 524: {
                // TODO : fix
                ReqCUser_Pet.OnPetFood(chr, MapleInventoryType.CASH, cash_item_slot, cash_item_id);
                return true;
            }
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
