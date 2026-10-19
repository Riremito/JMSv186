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
import java.util.HashMap;
import java.util.Map;
import odin.client.MapleCharacter;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleShop;
import tacos.debug.DebugShop;
import tacos.wz.ServerImg;
import tacos.wz.ServerImg.NpcShopData;

/**
 *
 * @author Riremito
 */
public class TacosNpcShop {

    // create npc shop from server img.
    public static boolean startNpcShop(MapleCharacter chr, int npc_script_id, int npc_id) {
        ArrayList<NpcShopData> items = ServerImg.getAutoNpcShopData(npc_script_id);
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

    // create npc shop from script.
    public static MapleShop createScriptNpcShop(int npc_id) {
        return new MapleShop(npc_id, npc_id);
    }

    // create npc shop from database.
    private static final Map<Integer, MapleShop> shops = new HashMap<>();

    private static MapleShop getDatabaseNpcShop(int npc_id) {
        if (shops.containsKey(npc_id)) {
            return shops.get(npc_id);
        }

        MapleShop ret = MapleShop.createFromDB(npc_id);
        shops.put(npc_id, ret);
        return ret;
    }

    public static boolean startDatabaseNpcShop(MapleCharacter chr, int npc_id) {
        MapleShop shop = getDatabaseNpcShop(npc_id);
        if (shop == null) {
            return false;
        }

        shop.sendShop(chr);
        return true;
    }
}
