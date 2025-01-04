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
package packet.response.struct;

import client.MapleClient;
import client.inventory.IItem;
import config.ServerConfig;
import packet.ServerPacket;
import server.CashItemFactory;

/**
 *
 * @author Riremito
 */
public class GW_CashItemInfo {

    // addCashItemInfo
    public static byte[] Encode(IItem item, MapleClient c) {
        ServerPacket sp = new ServerPacket();
        sp.Encode8(item.getUniqueId());
        sp.Encode8(c.getAccID());
        sp.Encode4(item.getItemId());
        sp.Encode4(0); // first?
        sp.Encode2(item.getQuantity());
        sp.EncodeBuffer(item.getOwner(), 13);
        sp.Encode8(ServerConfig.expiration_date);
        sp.Encode8(CashItemFactory.getInstance().getItemSN(item.getItemId()));
        return sp.Get().getBytes();

    }
}
