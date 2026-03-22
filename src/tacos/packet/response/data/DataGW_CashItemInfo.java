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
package tacos.packet.response.data;

import odin.client.MapleClient;
import odin.client.inventory.IItem;
import tacos.shared.SharedDate;
import tacos.packet.ServerPacket;
import odin.server.CashItemFactory;

/**
 *
 * @author Riremito
 */
public class DataGW_CashItemInfo {

    // addCashItemInfo
    public static byte[] Encode(IItem item, MapleClient c) {
        ServerPacket data = new ServerPacket();

        data.Encode8(item.getUniqueId());
        data.Encode8(c.getId());
        data.Encode4(item.getItemId());
        data.Encode4(0); // first?
        data.Encode2(item.getQuantity());
        data.EncodeBuffer(item.getOwner(), 13);
        data.Encode8(SharedDate.getMagicalExpirationDate());
        data.Encode8(CashItemFactory.getInstance().getItemSN(item.getItemId()));
        return data.get().getBytes();

    }
}
