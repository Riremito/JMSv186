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

import tacos.client.TacosCharacter;
import odin.server.shops.IPlayerShop;
import tacos.packet.ServerPacket;

/**
 *
 * @author Riremito
 */
public class RD_Structure {

    // addAnnounceBox
    public static byte[] AnnounceBox(TacosCharacter chr) {
        ServerPacket data = new ServerPacket();

        if (chr.getBalloonShop() != null) {
            data.EncodeBuffer(Interaction(chr.getBalloonShop()));
        } else {
            data.Encode1(0);
        }

        return data.getBytes();
    }

    // addInteraction
    public static byte[] Interaction(IPlayerShop shop) {
        ServerPacket data = new ServerPacket();

        data.Encode1(shop.getGameType());
        data.Encode4(shop.getObjectId());
        data.EncodeStr(shop.getDescription());
        if (shop.getShopType() != 1) {
            data.Encode1(shop.getPassword().length() > 0 ? 1 : 0); //password = false
        }
        data.Encode1(shop.getItemId() % 10);
        data.Encode1(shop.getSize()); //current size
        data.Encode1(shop.getMaxSize()); //full slots... 4 = 4-1=3 = has slots, 1-1=0 = no slots
        if (shop.getShopType() != 1) {
            data.Encode1(shop.isOpen() ? 0 : 1);
        }

        return data.getBytes();
    }
}
