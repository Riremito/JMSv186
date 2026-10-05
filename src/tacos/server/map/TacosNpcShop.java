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
package tacos.server.map;

import java.util.ArrayList;
import odin.client.MapleCharacter;
import odin.server.MapleItemInformationProvider;
import tacos.debug.DebugShop;
import tacos.wz.ServerImg;
import tacos.wz.ServerImg.NpcShopData;

/**
 *
 * @author Riremito
 */
public class TacosNpcShop {

    public static boolean startNpcShop(MapleCharacter chr, int npc_id) {
        ArrayList<NpcShopData> items = ServerImg.BMS8.getNpcShopData(npc_id);
        // not a npc shop.
        if (items == null) {
            return false;
        }
        // open npc shop.
        DebugShop ds = new DebugShop(npc_id);
        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        for (NpcShopData item : items) {
            int item_slot_max = miip.getSlotMax(item.getItem());
            ds.addItem(item.getItem(), item.getPrice(), 1, item_slot_max);
        }
        ds.start(chr);
        return true;
    }
}
