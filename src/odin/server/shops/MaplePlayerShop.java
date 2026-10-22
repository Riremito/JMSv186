/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

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
package odin.server.shops;

import java.util.ArrayList;
import tacos.packet.ops.OpsMRLeave;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import java.util.List;
import java.util.LinkedList;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import odin.server.MapleInventoryManipulator;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.ServerPacket;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.ItemLoader;
import tacos.database.query.DQ_Hiredmerch;
import tacos.server.TacosWorld;
import java.sql.SQLException;

public class MaplePlayerShop {

    public final static byte HIRED_MERCHANT = 1;
    public final static byte PLAYER_SHOP = 2;
    public final static byte OMOK = 3;
    public final static byte MATCH_CARD = 4;

    private Point position = new Point();
    private int objectId;

    protected boolean open = false;
    protected boolean available = false;
    protected String ownerName;
    protected String des;
    protected String pass;
    protected int ownerId;
    protected int owneraccount;
    protected int itemId;
    protected int channel;
    protected int map;
    protected AtomicInteger meso = new AtomicInteger(0);
    protected TacosCharacter chrs[];
    protected List<String> visitors = new LinkedList<>();
    protected List<BoughtItem> bought = new LinkedList<>();
    protected List<MaplePlayerShopItem> items = new LinkedList<>();

    private int boughtnumber = 0;
    private List<String> bannedList = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public MaplePlayerShop(TacosCharacter owner, int itemId, String desc) {
        this.setPosition(owner.getPosition());
        this.ownerName = owner.getName();
        this.ownerId = owner.getId();
        this.owneraccount = owner.getAccountId();
        this.itemId = itemId;
        this.des = desc;
        this.pass = "";
        this.map = owner.getMapId();
        this.channel = owner.getClient().getChannelId();
        chrs = new TacosCharacter[3];
        for (int i = 0; i < chrs.length; i++) {
            chrs[i] = null;
        }
    }

    public Point getPosition() {
        return new Point(position);
    }

    public void setPosition(Point position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int id) {
        this.objectId = id;
    }

    public int getMaxSize() {
        return chrs.length + 1;
    }

    public int getSize() {
        return getFreeSlot() == -1 ? getMaxSize() : getFreeSlot();
    }

    public void broadcastToVisitors(ServerPacket packet) {
        broadcastToVisitors(packet, true);
    }

    public void broadcastToVisitors(ServerPacket packet, boolean owner) {
        for (TacosCharacter chr : chrs) {
            if (chr != null) {
                chr.SendPacket(packet);
            }
        }
        if (getShopType() != HIRED_MERCHANT && owner && getMCOwner() != null) {
            getMCOwner().SendPacket(packet);
        }
    }

    public void broadcastToVisitors(ServerPacket packet, int exception) {
        for (TacosCharacter chr : chrs) {
            if (chr != null && getVisitorSlot(chr) != exception) {
                chr.SendPacket(packet);
            }
        }
        if (getShopType() != HIRED_MERCHANT && getMCOwner() != null && exception != ownerId) {
            getMCOwner().SendPacket(packet);
        }
    }

    public int getMeso() {
        return meso.get();
    }

    public void setMeso(int meso) {
        this.meso.set(meso);
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean saveItems() {
        if (getShopType() != HIRED_MERCHANT) { //hired merch only
            return false;
        }
        Integer packageid = DQ_Hiredmerch.add(ownerId, owneraccount, meso.get());
        if (packageid == null) {
            return false;
        }
        List<SimpleImmutableEntry<Item, MapleInventoryType>> iters = new ArrayList<>();
        Item item;
        for (MaplePlayerShopItem pItems : items) {
            if (pItems.item == null || pItems.bundles <= 0) {
                continue;
            }
            if (pItems.item.getQuantity() <= 0 && !GameConstants.isRechargable(pItems.item.getItemId())) {
                continue;
            }
            item = pItems.item.copy();
            item.setQuantity((short) (item.getQuantity() * pItems.bundles));
            iters.add(new SimpleImmutableEntry<>(item, GameConstants.getInventoryType(item.getItemId())));
        }
        try {
            ItemLoader.HIRED_MERCHANT.saveItems(iters, packageid, owneraccount, ownerId);
            return true;
        } catch (SQLException se) {
        }
        return false;
    }

    public TacosCharacter getVisitor(int num) {
        return chrs[num];
    }

    public void addVisitor(TacosCharacter visitor) {
        int i = getFreeSlot();
        if (i > 0) {
            broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MRP_Enter(i, visitor));
            chrs[i - 1] = visitor;
            if (!isOwner(visitor)) {
                visitors.add(visitor.getName());
            }
        }
    }

    public void removeVisitor(TacosCharacter visitor) {
        byte slot = getVisitorSlot(visitor);
        boolean shouldUpdate = getFreeSlot() == -1;
        if (slot > 0) {
            broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(slot), slot);
            chrs[slot - 1] = null;
        }
    }

    public byte getVisitorSlot(TacosCharacter visitor) {
        for (byte i = 0; i < chrs.length; i++) {
            if (chrs[i] != null && chrs[i].getId() == visitor.getId()) {
                return (byte) (i + 1);
            }
        }
        if (visitor.getId() == ownerId) { //can visit own store in merch, otherwise not.
            return 0;
        }
        return -1;
    }

    public void removeAllVisitors(int error, int type) {
        for (int i = 0; i < chrs.length; i++) {
            TacosCharacter visitor = getVisitor(i);
            if (visitor != null) {
                if (type != -1) {
                    visitor.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(type, error));
                }
                broadcastToVisitors(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(getVisitorSlot(visitor)), getVisitorSlot(visitor));
                visitor.setPlayerShop(null);
                chrs[i] = null;
            }
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getOwnerAccId() {
        return owneraccount;
    }

    public String getDescription() {
        if (des == null) {
            return "";
        }
        return des;
    }

    public List<SimpleImmutableEntry<Byte, TacosCharacter>> getVisitors() {
        List<SimpleImmutableEntry<Byte, TacosCharacter>> chrz = new LinkedList<>();
        for (byte i = 0; i < chrs.length; i++) { //include owner or no
            if (chrs[i] != null) {
                chrz.add(new SimpleImmutableEntry<>((byte) (i + 1), chrs[i]));
            }
        }
        return chrz;
    }

    public List<MaplePlayerShopItem> getItems() {
        return items;
    }

    public void addItem(MaplePlayerShopItem item) {
        items.add(item);
    }

    public boolean removeItem(int item) {
        return false;
    }

    public void removeFromSlot(int slot) {
        items.remove(slot);
    }

    public byte getFreeSlot() {
        for (byte i = 0; i < chrs.length; i++) {
            if (chrs[i] == null) {
                return (byte) (i + 1);
            }
        }
        return -1;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean isOwner(TacosCharacter chr) {
        return chr.getId() == ownerId && chr.getName().equals(ownerName);
    }

    public String getPassword() {
        if (pass == null) {
            return "";
        }
        return pass;
    }

    public TacosCharacter getMCOwner() {
        return getMap().getPlayerById(ownerId);
    }

    public TacosMap getMap() {
        return TacosWorld.find(0).getChannelServer(channel).findMap(map);
    }

    public int getGameType() {
        if (getShopType() == HIRED_MERCHANT) { //hiredmerch
            return 5;
        } else if (getShopType() == PLAYER_SHOP) { //shop lol
            return 4;
        } else if (getShopType() == OMOK) { //omok
            return 1;
        } else if (getShopType() == MATCH_CARD) { //matchcard
            return 2;
        }
        return 0;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean b) {
        this.available = b;
    }

    public List<BoughtItem> getBoughtItems() {
        return bought;
    }

    public static final class BoughtItem {

        public int id;
        public int quantity;
        public int totalPrice;
        public String buyer;

        public BoughtItem(int id, int quantity, int totalPrice, String buyer) {
            this.id = id;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.buyer = buyer;
        }
    }

    public void buy(TacosCharacter chr, int item, short quantity) {
        MaplePlayerShopItem pItem = items.get(item);
        if (pItem.bundles > 0) {
            Item newItem = pItem.item.copy();
            newItem.setQuantity((short) (quantity * newItem.getQuantity()));
            byte flag = newItem.getFlag();

            if (ItemFlag.KARMA_EQ.check(flag)) {
                newItem.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
            } else if (ItemFlag.KARMA_USE.check(flag)) {
                newItem.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
            }
            int gainmeso = pItem.price * quantity;
            if (chr.getMeso() >= gainmeso) {
                if (getMCOwner().getMeso() + gainmeso > 0 && MapleInventoryManipulator.checkSpace(chr, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner()) && MapleInventoryManipulator.addFromDrop(chr, newItem, false)) {
                    pItem.bundles -= quantity;
                    bought.add(new BoughtItem(newItem.getItemId(), quantity, gainmeso, chr.getName()));
                    chr.gainMeso(-gainmeso, false);
                    getMCOwner().gainMeso(gainmeso, false);
                    if (pItem.bundles <= 0) {
                        boughtnumber++;
                        if (boughtnumber == items.size()) {
                            closeShop(false, true, OpsMRLeave.PSLeave_NoMoreItem.get());
                            return;
                        }
                    }
                } else {
                    chr.Alert("Your inventory is full.");
                }
            } else {
                chr.Alert("You do not have enough mesos.");
                //}
            }
            getMCOwner().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh(this));
        }
    }

    public byte getShopType() {
        return PLAYER_SHOP;
    }

    public void closeShop(boolean saveItems, boolean remove, int reason) {
        TacosCharacter owner = getMCOwner();
        removeAllVisitors(reason, 1);
        getMap().removePlayerShop(this.getObjectId());

        for (MaplePlayerShopItem items : getItems()) {
            if (items.bundles > 0) {
                Item newItem = items.item.copy();
                newItem.setQuantity((short) (items.bundles * newItem.getQuantity()));
                if (MapleInventoryManipulator.addFromDrop(owner, newItem, false)) {
                    items.bundles = 0;
                } else {
                    saveItems(); //O_o
                    break;
                }
            }
        }

        owner.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(0, reason));
        owner.setPlayerShop(null);
    }

    public boolean isBanned(String name) {
        if (bannedList.contains(name)) {
            return true;
        }
        return false;
    }

    public static class MaplePlayerShopItem {

        public Item item;
        public short bundles;
        public int price;

        public MaplePlayerShopItem(Item item, short bundles, int price) {
            this.item = item;
            this.bundles = bundles;
            this.price = price;
        }
    }
}
