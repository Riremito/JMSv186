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

import odin.client.inventory.Equip;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import odin.client.inventory.Item;
import odin.constants.GameConstants;
import odin.client.inventory.MaplePet;
import odin.client.inventory.ItemLoader;
import tacos.client.TacosClient;
import odin.client.inventory.MapleRing;
import odin.client.inventory.MapleInventoryIdentifier;
import odin.client.inventory.MapleInventoryType;
import tacos.database.query.DQ_Gifts;
import tacos.packet.response.ResCCashShop;
import java.util.AbstractMap.SimpleImmutableEntry;

public class CashShop {

    private int accountId;
    private int characterId;
    private ItemLoader factory;
    private List<Item> inventory = new ArrayList<>();
    private List<Integer> uniqueids = new ArrayList<>();

    public CashShop(int accountId, int characterId, int jobType) throws SQLException {
        this.accountId = accountId;
        this.characterId = characterId;

        if (jobType / 1000 == 1) {
            factory = ItemLoader.CASHSHOP_CYGNUS;
        } else if ((jobType / 100 == 21 || jobType / 100 == 20) && jobType != 2001) {
            factory = ItemLoader.CASHSHOP_ARAN;
        } else if (jobType == 2001 || jobType / 100 == 22) {
            factory = ItemLoader.CASHSHOP_EVAN;
        } else if (jobType >= 3000) {
            factory = ItemLoader.CASHSHOP_RESIST;
        } else if (jobType / 10 == 43) {
            factory = ItemLoader.CASHSHOP_DB;
        } else {
            factory = ItemLoader.CASHSHOP_EXPLORER;
        }

        for (SimpleImmutableEntry<Item, MapleInventoryType> item : factory.loadItems(false, accountId).values()) {
            inventory.add(item.getKey());
        }
    }

    public int getItemsSize() {
        return inventory.size();
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public Item findByCashId(long cashId) {
        for (Item item : inventory) {
            if (item.getUniqueId() == cashId) {
                return item;
            }
        }

        return null;
    }

    public Item findItem(int item_id) {
        for (Item item : inventory) {
            if (item.getItemId() == item_id) {
                return item;
            }
        }
        return null;
    }

    public void checkExpire(TacosClient client) {
        List<Item> toberemove = new ArrayList<>();
        for (Item item : inventory) {
            if (item != null && !GameConstants.isPet(item.getItemId()) && item.getExpiration() > 0 && item.getExpiration() < System.currentTimeMillis()) {
                toberemove.add(item);
            }
        }
        if (!toberemove.isEmpty()) {
            for (Item item : toberemove) {
                removeFromInventory(item);
                client.SendPacket(ResCCashShop.cashItemExpired(item.getUniqueId()));
            }
            toberemove.clear();
        }
    }

    public Item toItem(CashItemInfo cItem) {
        return toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), "");
    }

    public Item toItem(CashItemInfo cItem, String gift) {
        return toItem(cItem, MapleInventoryManipulator.getUniqueId(cItem.getId(), null), gift);
    }

    public Item toItem(CashItemInfo cItem, int uniqueid) {
        return toItem(cItem, uniqueid, "");
    }

    public Item toItem(CashItemInfo cItem, int uniqueid, String gift) {
        if (uniqueid <= 0) {
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        long period = cItem.getPeriod();
        if (period <= 0 || GameConstants.isPet(cItem.getId())) {
            period = 45;
        }
        Item ret = null;
        if (GameConstants.getInventoryType(cItem.getId()) == MapleInventoryType.EQUIP) {
            Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(cItem.getId());
            eq.setUniqueId(uniqueid);
            eq.setExpiration((long) (System.currentTimeMillis() + (long) (period * 24 * 60 * 60 * 1000)));
            eq.setGiftFrom(gift);
            if (GameConstants.isEffectRing(cItem.getId()) && uniqueid > 0) {
                MapleRing ring = MapleRing.loadFromDb(uniqueid);
                if (ring != null) {
                    eq.setRing(ring);
                }
            }
            ret = eq.copy();
        } else {
            Item item = new Item(cItem.getId(), (byte) 0, (short) cItem.getCount(), (byte) 0, uniqueid);
            item.setExpiration((long) (System.currentTimeMillis() + (long) (period * 24 * 60 * 60 * 1000)));
            item.setGiftFrom(gift);
            if (GameConstants.isPet(cItem.getId())) {
                final MaplePet pet = MaplePet.createPet(cItem.getId(), uniqueid);
                if (pet != null) {
                    item.setPet(pet);
                }
            }
            ret = item.copy();
        }
        return ret;
    }

    public void addToInventory(Item item) {
        inventory.add(item);
    }

    public void removeFromInventory(Item item) {
        inventory.remove(item);
    }

    public void gift(int recipient, String from, String message, int sn) {
        gift(recipient, from, message, sn, 0);
    }

    public void gift(int recipient, String from, String message, int sn, int uniqueid) {
        DQ_Gifts.add(recipient, from, message, sn, uniqueid);
    }

    public boolean canSendNote(int uniqueid) {
        return uniqueids.contains(uniqueid);
    }

    public void sendedNote(int uniqueid) {
        for (int i = 0; i < uniqueids.size(); i++) {
            if (uniqueids.get(i) == uniqueid) {
                uniqueids.remove(i);
            }
        }
    }

    public void save() throws SQLException {
        List<SimpleImmutableEntry<Item, MapleInventoryType>> itemsWithType = new ArrayList<>();

        for (Item item : inventory) {
            itemsWithType.add(new SimpleImmutableEntry<>(item, GameConstants.getInventoryType(item.getItemId())));
        }

        factory.saveItems(itemsWithType, accountId);
    }
}
