/*
 * Copyright (C) 2026 Riremito
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

import odin.client.MapleCharacter;
import odin.server.shops.ShopDispatch;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class RD_Structure {

    // addAnnounceBox
    public static final byte[] AnnounceBox(MapleCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (chr.getPlayerShop() != null && ShopDispatch.isOwner(chr.getPlayerShop(), chr) && ShopDispatch.getShopType(chr.getPlayerShop()) != 1 && ShopDispatch.isAvailable(chr.getPlayerShop())) {
            data.EncodeBuffer(Interaction(chr.getPlayerShop()));
        } else {
            data.Encode1(0);
        }

        return data.getBytes();
    }

    // addInteraction
    public static final byte[] Interaction(Object shop) {
        ServerPacket data = new ServerPacket();

        data.Encode1(ShopDispatch.getGameType(shop));
        data.Encode4(ShopDispatch.getObjectId(shop));
        data.EncodeStr(ShopDispatch.getDescription(shop));
        if (ShopDispatch.getShopType(shop) != 1) {
            data.Encode1(ShopDispatch.getPassword(shop).length() > 0 ? 1 : 0); //password = false
        }
        data.Encode1(ShopDispatch.getItemId(shop) % 10);
        data.Encode1(ShopDispatch.getSize(shop)); //current size
        data.Encode1(ShopDispatch.getMaxSize(shop)); //full slots... 4 = 4-1=3 = has slots, 1-1=0 = no slots
        if (ShopDispatch.getShopType(shop) != 1) {
            data.Encode1(ShopDispatch.isOpen(shop) ? 0 : 1);
        }

        return data.getBytes();
    }
}
