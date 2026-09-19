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

import java.util.concurrent.ScheduledFuture;
import odin.client.inventory.Item;
import odin.client.inventory.ItemFlag;
import odin.constants.GameConstants;
import odin.client.MapleCharacter;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import tacos.packet.response.ResCEmployeePool;
import odin.server.MapleInventoryManipulator;
import odin.server.Timer.EtcTimer;
import odin.server.maps.MapleMap;
import tacos.client.TacosClient;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.server.TacosWorld;
import java.awt.Point;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import tacos.packet.ServerPacket;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.ItemLoader;
import tacos.database.query.DQ_Hiredmerch;
import java.sql.SQLException;

public class HiredMerchant {

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
    protected WeakReference<MapleCharacter> chrs[];
    protected List<String> visitors = new LinkedList<>();
    protected List<BoughtItem> bought = new LinkedList<>();
    protected List<MaplePlayerShopItem> items = new LinkedList<>();

    public ScheduledFuture<?> schedule;
    private List<String> blacklist;
    private int storeid;
    private int foothold_id;
    private int item_sub_type;
    private long start;

    @SuppressWarnings("unchecked")
    public HiredMerchant(MapleCharacter owner, int itemId, String desc) {
        this.setPosition(owner.getPosition());
        this.ownerName = owner.getName();
        this.ownerId = owner.getId();
        this.owneraccount = owner.getAccountId();
        this.itemId = itemId;
        this.des = desc;
        this.pass = "";
        this.map = owner.getMapId();
        this.channel = owner.getClient().getChannelId();
        chrs = new WeakReference[3];
        for (int i = 0; i < chrs.length; i++) {
            chrs[i] = new WeakReference<>(null);
        }
        this.item_sub_type = itemId % 100;
        this.foothold_id = owner.getFH();
        start = System.currentTimeMillis();
        blacklist = new LinkedList<>();
        this.schedule = EtcTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                closeShop(true, true, 0);
            }
        }, 1000 * 60 * 60 * 24);
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
        for (WeakReference<MapleCharacter> chr : chrs) {
            if (chr != null && chr.get() != null) {
                chr.get().SendPacket(packet);
            }
        }
        if (getShopType() != HIRED_MERCHANT && owner && getMCOwner() != null) {
            getMCOwner().SendPacket(packet);
        }
    }

    public void broadcastToVisitors(ServerPacket packet, int exception) {
        for (WeakReference<MapleCharacter> chr : chrs) {
            if (chr != null && chr.get() != null && getVisitorSlot(chr.get()) != exception) {
                chr.get().SendPacket(packet);
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

    public MapleCharacter getVisitor(int num) {
        return chrs[num].get();
    }

    public void update() {
        if (isAvailable()) {
            getMap().broadcastMessage(ResCEmployeePool.EmployeeMiniRoomBalloon(this));
        }
    }

    public void addVisitor(MapleCharacter visitor) {
        int i = getFreeSlot();
        if (i > 0) {
            broadcastToVisitors(ResCMiniRoomBaseDlg.shopVisitorAdd(visitor, i));
            chrs[i - 1] = new WeakReference<>(visitor);
            if (!isOwner(visitor)) {
                visitors.add(visitor.getName());
            }
            if (i == 3) {
                update();
            }
        }
    }

    public void removeVisitor(MapleCharacter visitor) {
        final byte slot = getVisitorSlot(visitor);
        boolean shouldUpdate = getFreeSlot() == -1;
        if (slot > 0) {
            broadcastToVisitors(ResCMiniRoomBaseDlg.shopVisitorLeave(slot), slot);
            chrs[slot - 1] = new WeakReference<>(null);
            if (shouldUpdate) {
                update();
            }
        }
    }

    public byte getVisitorSlot(MapleCharacter visitor) {
        for (byte i = 0; i < chrs.length; i++) {
            if (chrs[i] != null && chrs[i].get() != null && chrs[i].get().getId() == visitor.getId()) {
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
            MapleCharacter visitor = getVisitor(i);
            if (visitor != null) {
                if (type != -1) {
                    visitor.SendPacket(ResCMiniRoomBaseDlg.shopErrorMessage(error, type));
                }
                broadcastToVisitors(ResCMiniRoomBaseDlg.shopVisitorLeave(getVisitorSlot(visitor)), getVisitorSlot(visitor));
                visitor.setPlayerShop(null);
                chrs[i] = new WeakReference<>(null);
            }
        }
        update();
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

    public List<SimpleImmutableEntry<Byte, MapleCharacter>> getVisitors() {
        List<SimpleImmutableEntry<Byte, MapleCharacter>> chrz = new LinkedList<>();
        for (byte i = 0; i < chrs.length; i++) { //include owner or no
            if (chrs[i] != null && chrs[i].get() != null) {
                chrz.add(new SimpleImmutableEntry<>((byte) (i + 1), chrs[i].get()));
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
            if (chrs[i] == null || chrs[i].get() == null) {
                return (byte) (i + 1);
            }
        }
        return -1;
    }

    public int getItemId() {
        return itemId;
    }

    public boolean isOwner(MapleCharacter chr) {
        return chr.getId() == ownerId && chr.getName().equals(ownerName);
    }

    public String getPassword() {
        if (pass == null) {
            return "";
        }
        return pass;
    }

    public MapleCharacter getMCOwner() {
        return getMap().getPlayerById(ownerId);
    }

    public MapleMap getMap() {
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

        public BoughtItem(final int id, final int quantity, final int totalPrice, final String buyer) {
            this.id = id;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.buyer = buyer;
        }
    }

    public void setTest(int owner_id, int fh, int st, int store_id) {
        this.ownerId = owner_id; // overwritten
        this.foothold_id = fh;
        this.item_sub_type = st % 100;
        this.storeid = store_id;
        //super.setObjectId(this.ownerId - 1);
    }

    public int getFH() {
        return this.foothold_id;
    }

    public int getItemSubType() {
        return this.item_sub_type;
    }

    public byte getShopType() {
        return HIRED_MERCHANT;
    }

    public final void setStoreid(final int storeid) {
        this.storeid = storeid;
    }

    public List<MaplePlayerShopItem> searchItem(final int itemSearch) {
        final List<MaplePlayerShopItem> itemz = new LinkedList<>();
        for (MaplePlayerShopItem item : items) {
            if (item.item.getItemId() == itemSearch && item.bundles > 0) {
                itemz.add(item);
            }
        }
        return itemz;
    }

    public void buy(TacosClient client, int item, short quantity) {
        MapleCharacter chr = client.getPlayer();
        final MaplePlayerShopItem pItem = items.get(item);
        final Item shopItem = pItem.item;
        final Item newItem = shopItem.copy();
        final short perbundle = newItem.getQuantity();
        newItem.setQuantity((short) (quantity * perbundle));

        byte flag = newItem.getFlag();

        if (ItemFlag.KARMA_EQ.check(flag)) {
            newItem.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
        } else if (ItemFlag.KARMA_USE.check(flag)) {
            newItem.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
        }

        if (MapleInventoryManipulator.checkSpace(client, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner()) && MapleInventoryManipulator.addFromDrop(client, newItem, false)) {
            pItem.bundles -= quantity; // Number remaining in the store
            bought.add(new BoughtItem(newItem.getItemId(), quantity, (pItem.price * quantity), client.getPlayer().getName()));

            final int gainmeso = getMeso() + (pItem.price * quantity);
            setMeso(gainmeso - GameConstants.EntrustedStoreTax(gainmeso));
            client.getPlayer().gainMeso(-pItem.price * quantity, false);
            saveItems();
        } else {
            client.getPlayer().dropMessage(1, "Your inventory is full.");
            chr.updateInv();
        }
    }

    public void closeShop(boolean saveItems, boolean remove, int reason) {
        if (schedule != null) {
            schedule.cancel(false);
        }
        if (saveItems) {
            saveItems();
        }
        if (remove) {
            TacosWorld.find(0).getChannelServer(channel).removeMerchant(this); // TODO : fix
            getMap().broadcastMessage(ResCEmployeePool.EmployeeLeaveField(this));
        }
        getMap().removeHiredMerchant(this.getObjectId());
        schedule = null;
    }

    public int getTimeLeft() {
        return (int) ((System.currentTimeMillis() - start) / 1000);
    }

    public final int getStoreId() {
        return storeid;
    }

    public final boolean isInBlackList(final String bl) {
        return blacklist.contains(bl);
    }

    public final void addBlackList(final String bl) {
        blacklist.add(bl);
    }

    public final void removeBlackList(final String bl) {
        blacklist.remove(bl);
    }

    public final void sendBlackList(MapleCharacter chr) {
        chr.SendPacket(ResCMiniRoomBaseDlg.MerchantBlackListView(blacklist));
    }

    public final void sendVisitor(MapleCharacter chr) {
        chr.SendPacket(ResCMiniRoomBaseDlg.MerchantVisitorView(visitors));
    }
}
