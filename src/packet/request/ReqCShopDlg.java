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
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import config.ServerConfig;
import constants.GameConstants;
import debug.DebugLogger;
import packet.ClientPacket;
import packet.ops.OpsShop;
import server.MapleShop;

/**
 *
 * @author Riremito
 */
public class ReqCShopDlg {

    // CShopDlg::OnPacket
    public static boolean OnPacket(ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleShop shop = chr.getShop();

        if (shop == null) {
            chr.setConversation(0);
            return false;
        }

        byte flag = cp.Decode1();

        switch (OpsShop.find(flag)) {
            case ShopReq_Buy: {
                cp.Decode2();

                if (ServerConfig.JMS194orLater()) {
                    cp.Decode1();
                }

                final int itemId = cp.Decode4();
                final short quantity = cp.Decode2();
                shop.buy(c, chr, itemId, quantity);
                break;
            }
            case ShopReq_Sell: {
                final byte slot = (byte) cp.Decode2();
                final int itemId = cp.Decode4();
                final short quantity = cp.Decode2();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                break;
            }
            case ShopReq_Recharge: {
                final byte slot = (byte) cp.Decode2();
                shop.recharge(c, slot);
                break;
            }
            case ShopReq_Close: {
                chr.setConversation(0);
                return true;
            }
            default: {
                // not coded
                chr.setConversation(0);
                DebugLogger.DebugLog("ReqCShopDlg : not coded.");
                break;
            }
        }

        return false;
    }

}
