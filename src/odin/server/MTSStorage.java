/*
This file is part of the ZeroFusion MapleStory Server
Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>
ZeroFusion organized by "RMZero213" <RMZero213@hotmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server;

import odin.constants.GameConstants;
import odin.client.inventory.Item;
import odin.client.inventory.ItemLoader;
import odin.client.inventory.MapleInventoryType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import tacos.database.query.DQ_MtsItems;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.server.TacosITC;

public class MTSStorage {
    //stores all carts all mts items, updates every hour

    private long lastUpdate = System.currentTimeMillis();
    private static MTSStorage instance;
    private final Map<Integer, MTSCart> idToCart;
    private final AtomicInteger packageId;
    private final Map<Integer, MTSItemInfo> buyNow; //packageid to mtsiteminfo
    private boolean end = false;
    //mts_cart is just characterid, itemid
    //mts_items is id/packageid, tab(byte), price, characterid, seller, expiration

    public MTSStorage() {
        //System.out.println("Loading MTSStorage::");
        idToCart = new LinkedHashMap<>();
        buyNow = new LinkedHashMap<>();
        packageId = new AtomicInteger(1);
    }

    public static MTSStorage getInstance() {
        return instance;
    }

    public static void load() {

        if (instance == null) {
            instance = new MTSStorage();
            instance.loadBuyNow();
        }

    }

    public boolean check(int packageid) {
        return getSingleItem(packageid) != null;
    }

    public boolean checkCart(int packageid, int charID) {
        MTSItemInfo item = getSingleItem(packageid);
        return item != null && item.getCharacterId() != charID;
    }

    public MTSItemInfo getSingleItem(int packageid) {
        return buyNow.get(packageid);
    }

    public void addToBuyNow(MTSCart cart, Item item, int price, int cid, String seller, long expiration) {
        int id;
        id = packageId.incrementAndGet();
        buyNow.put(id, new MTSItemInfo(price, item, seller, id, cid, expiration));
        cart.addToNotYetSold(id);
    }

    public boolean removeFromBuyNow(int id, int cidBought, boolean check) {
        Item item = null;
        if (buyNow.containsKey(id)) {
            MTSItemInfo r = buyNow.get(id);
            if (!check || r.getCharacterId() == cidBought) {
                item = r.getItem();
                buyNow.remove(id);
            }
        }
        if (item != null) {
            for (Entry<Integer, MTSCart> c : idToCart.entrySet()) {
                c.getValue().removeFromCart(id);
                c.getValue().removeFromNotYetSold(id);
                if (c.getKey() == cidBought) {
                    c.getValue().addToInventory(item);
                }
            }
        }
        return item != null;
    }

    public void loadBuyNow() {
        int lastPackage = 0;
        int cId;
        Map<Integer, SimpleImmutableEntry<Item, MapleInventoryType>> items;
        try {
            for (DQ_MtsItems.TabOneRow row : DQ_MtsItems.getTabOneRows()) {
                lastPackage = row.id;
                cId = row.characterId;
                if (!idToCart.containsKey(cId)) {
                    idToCart.put(cId, new MTSCart(cId));
                }
                items = ItemLoader.MTS.loadItems(false, lastPackage);
                if (items != null && !items.isEmpty()) {
                    for (SimpleImmutableEntry<Item, MapleInventoryType> i : items.values()) {
                        buyNow.put(lastPackage, new MTSItemInfo(row.price, i.getKey(), row.seller, lastPackage, cId, row.expiration));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        packageId.set(lastPackage);
    }

    public void saveBuyNow(boolean isShutDown) {
        if (this.end) {
            return;
        }
        this.end = isShutDown;
        if (isShutDown) {
            System.out.println("Saving MTS...");
        }
        Map<Integer, ArrayList<Item>> expire = new HashMap<>();
        List<Integer> toRemove = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<Integer, ArrayList<SimpleImmutableEntry<Item, MapleInventoryType>>> items = new HashMap<>();
        List<DQ_MtsItems.TabOneRow> rowsToSave = new ArrayList<>();
        for (MTSItemInfo m : buyNow.values()) {
            if (now > m.getEndingDate()) {
                if (!expire.containsKey(m.getCharacterId())) {
                    expire.put(m.getCharacterId(), new ArrayList<>());
                }
                expire.get(m.getCharacterId()).add(m.getItem());
                toRemove.add(m.getId());
                items.put(m.getId(), null); //destroy from the mtsitems.
            } else {
                rowsToSave.add(new DQ_MtsItems.TabOneRow(m.getId(), m.getCharacterId(), m.getPrice(), m.getSeller(), m.getEndingDate()));
                if (!items.containsKey(m.getId())) {
                    items.put(m.getId(), new ArrayList<>());
                }
                items.get(m.getId()).add(new SimpleImmutableEntry<>(m.getItem(), GameConstants.getInventoryType(m.getItem().getItemId())));
            }
        }
        for (int i : toRemove) {
            buyNow.remove(i);
        }
        DQ_MtsItems.replaceTabOne(rowsToSave);
        if (isShutDown) {
            System.out.println("Saving MTS items...");
        }
        try {
            for (Entry<Integer, ArrayList<SimpleImmutableEntry<Item, MapleInventoryType>>> ite : items.entrySet()) {
                ItemLoader.MTS.saveItems(ite.getValue(), ite.getKey());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (isShutDown) {
            System.out.println("Saving MTS carts...");
        }
        try {
            for (Entry<Integer, MTSCart> c : idToCart.entrySet()) {
                for (int i : toRemove) {
                    c.getValue().removeFromCart(i);
                    c.getValue().removeFromNotYetSold(i);
                }
                if (expire.containsKey(c.getKey())) {
                    for (Item item : expire.get(c.getKey())) {
                        c.getValue().addToInventory(item);
                    }
                }
                c.getValue().save();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        lastUpdate = System.currentTimeMillis();
    }

    public void checkExpirations() {
        if ((System.currentTimeMillis() - lastUpdate) > 3600000) { //every hour
            saveBuyNow(false);
        }
    }

    public MTSCart getCart(int characterId) {
        MTSCart ret;
        ret = idToCart.get(characterId);
        if (ret == null) {
            try {
                ret = new MTSCart(characterId);
                idToCart.put(characterId, ret);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public List<MTSItemInfo> getCurrentNotYetSold(MTSCart cart) {
        List<MTSItemInfo> nys = new ArrayList<>();
        MTSItemInfo r;
        List<Integer> nyss = new ArrayList<>(cart.getNotYetSold());
        for (int i : nyss) {
            r = buyNow.get(i);
            if (r == null) {
                cart.removeFromNotYetSold(i);
            } else {
                nys.add(r);
            }
        }
        return nys;
    }

    public List<MTSItemInfo> getBuyNow(int type, int page) {
        //page * 16 = FIRST item thats displayed
        int size = buyNow.size() / 16 + (buyNow.size() % 16 > 0 ? 1 : 0);
        List<MTSItemInfo> ret = new ArrayList<>();
        List<MTSItemInfo> rett = new ArrayList<>(buyNow.values());
        if (page > size) {
            page = 0;
        }
        MTSItemInfo r;
        for (int i = page * 16; i < page * 16 + 16; i++) {
            if (buyNow.size() >= i + 1) {
                r = rett.get(i); //by index
                if (r != null && (type == 0 || GameConstants.getInventoryType(r.getItem().getItemId()).getType() == type)) {
                    ret.add(r);
                }
            } else {
                break;
            }
        }
        return ret;
    }

    public List<MTSItemInfo> getCartItems(MTSCart cart) {
        List<MTSItemInfo> ret = new ArrayList<>();
        MTSItemInfo r;
        List<Integer> cartt = new ArrayList<>(cart.getCart());
        for (int i : cartt) { //by packageid
            r = buyNow.get(i);
            if (r == null) {
                cart.removeFromCart(i);
            } else if (cart.getType() == 0 || GameConstants.getInventoryType(r.getItem().getItemId()).getType() == cart.getType()) {
                ret.add(r);
            }
        }
        return ret;
    }

    public static class MTSItemInfo {

        private int price;
        private int id; //packageid
        private int cid;
        private Item item;
        private String seller;
        private long date;

        public MTSItemInfo(int price, Item item, String seller, int id, int cid, long date) {
            this.item = item;
            this.price = price;
            this.seller = seller;
            this.id = id;
            this.cid = cid;
            this.date = date;
        }

        public Item getItem() {
            return item;
        }

        public int getPrice() {
            return price;
        }

        public int getRealPrice() {
            return price + getTaxes();
        }

        public int getTaxes() {
            return TacosITC.m_nCommissionBase + (int) (price * TacosITC.m_nCommissionRate / 100);
        }

        public int getId() {
            return id;
        }

        public int getCharacterId() {
            return cid;
        }

        public long getEndingDate() {
            return date;
        }

        public String getSeller() {
            return seller;
        }
    }
}
