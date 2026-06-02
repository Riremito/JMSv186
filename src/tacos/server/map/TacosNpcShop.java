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
import java.util.TreeMap;
import odin.client.MapleCharacter;
import odin.provider.IMapleData;
import tacos.debug.DebugLogger;
import tacos.debug.DebugShop;
import tacos.wz.ServerImg;
import tacos.wz.WzDataStorage;
import tacos.wz.WzDataTool;

/**
 *
 * @author Riremito
 */
public class TacosNpcShop {
    
    public static class ShopItem {
        
        public int item;
        public int price;
        public int period;
        public int stock;
        public double unitPrice;
    }
    
    private static final TreeMap<Integer, ArrayList<ShopItem>> SHOPS = new TreeMap<>();
    
    public static boolean checkNpcShop(MapleCharacter chr, int npc_id) {
        ArrayList<ShopItem> items = getShop(npc_id);
        // not a npc shop.
        if (items == null) {
            return false;
        }
        chr.DebugMsg("NpcShop : " + npc_id);
        // open npc shop.
        DebugShop ds = new DebugShop(npc_id);
        for (ShopItem item : items) {
            ds.addItem(item.item, item.price, 1, 1);
        }
        ds.start(chr);
        return true;
    }
    
    public static ArrayList<ShopItem> getShop(int npc_id) {
        ArrayList<ShopItem> items = SHOPS.get(npc_id);
        
        if (items != null) {
            return items;
        }
        
        if (!SHOPS.containsKey(npc_id)) {
            IMapleData md_npc_shop = ServerImg.SI.getNpcShop().getChildByPath(String.format("%07d", npc_id));
            if (md_npc_shop != null) {
                items = new ArrayList<>();
                for (IMapleData md_data : md_npc_shop.getChildren()) {
                    ShopItem item = new ShopItem();
                    item.item = WzDataTool.getIntPath("item", md_data, 0);
                    item.price = WzDataTool.getIntPath("price", md_data, 0);
                    item.period = WzDataTool.getIntPath("period", md_data, 0);
                    item.stock = WzDataTool.getIntPath("stock", md_data, 0);
                    String unitPrice_str = WzDataTool.getStringPath("prob", md_data, "[R8]0.0").replace("[R8]", "");
                    item.unitPrice = Double.parseDouble(unitPrice_str);
                    if (item.price == 0) {
                        // TODO : star and bullet recharge.
                        continue;
                    }
                    if (!WzDataStorage.ITEM.check(item.item)) {
                        DebugLogger.XmlLog("getShop : " + npc_id + ", invalid item id = " + item.item);
                        continue;
                    }
                    items.add(item);
                }
            }
            SHOPS.put(npc_id, items);
        }
        
        return items;
    }
}
