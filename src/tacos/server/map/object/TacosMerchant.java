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
package tacos.server.map.object;

import java.util.AbstractMap;
import odin.server.shops.IPlayerShop;
import tacos.client.TacosCharacter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsMRLeave;
import tacos.packet.response.ResCMiniRoomBaseDlg;
import tacos.server.TacosRoom;
import odin.client.inventory.Item;
import odin.server.shops.MaplePlayerShop.MaplePlayerShopItem;
import tacos.server.map.TacosMap;
import odin.client.inventory.Item.ItemFlag;
import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;
import tacos.server.TacosWorld;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.AbstractMap.SimpleImmutableEntry;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.ItemLoader;
import tacos.database.query.DQ_Hiredmerch;
import java.sql.SQLException;

/**
 *
 * @author Riremito
 */
public class TacosMerchant extends TacosMapObject implements IPlayerShop {

    private boolean open = false;
    private final TacosCharacter[] visitors = new TacosCharacter[4];
    private final List<String> visitors_names = new LinkedList<>();
    private final List<String> blacklist = new LinkedList<>();
    private final int room_id;

    public int getRoomId() {
        return this.room_id;
    }

    public boolean enter(TacosCharacter visitor) {
        if (getOwnerId() == visitor.getId()) {
            setOpen(false);
            removeAllVisitors(OpsMRLeave.MRLeave_UserRequest.get(), 0);
            visitor.setPlayerShop(this);
            visitor.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_EntrustedShop(visitor, this, false));
            return true;
        }
        if (!isOpen()) {
            visitor.DebugMsg("商店の主人が物品整理中でございます。もうしばらく後でご利用ください。");
            return false;
        }
        if (getFreeSlot() == -1) {
            visitor.DebugMsg("This shop has reached it's maximum capacity, please come by later.");
            return false;
        }
        if (isInBlackList(visitor.getName())) {
            visitor.DebugMsg("You have been banned from this store.");
            return false;
        }
        visitor.setPlayerShop(this);
        addVisitor(visitor);
        visitor.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_EntrustedShop(visitor, this, false));
        return true;
    }

    public boolean leave(TacosCharacter visitor) {
        if (getOwnerId() == visitor.getId()) {
            setOpen(true);
            visitor.setPlayerShop(null);
            return true;
        }
        visitor.setPlayerShop(null);
        removeVisitor(visitor);
        return true;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isOpen() {
        return this.open;
    }

    private ArrayList<TacosCharacter> getAllVisitors() {
        ArrayList<TacosCharacter> ret = new ArrayList<>();
        for (TacosCharacter visitor : this.visitors) {
            if (visitor != null) {
                ret.add(visitor);
            }
        }
        return ret;
    }

    public void SendPacket(ServerPacket packet) {
        for (TacosCharacter visitor : getAllVisitors()) {
            visitor.SendPacket(packet);
        }
    }

    // merge
    public void broadcastToVisitors(ServerPacket packet) {
        SendPacket(packet);
    }

    public int getMaxSize() {
        return visitors.length + 1;
    }

    public int getSize() {
        return getFreeSlot() == -1 ? getMaxSize() : getFreeSlot();
    }

    public TacosCharacter getVisitor(int num) {
        return this.visitors[num];
    }

    public void addVisitor(TacosCharacter visitor) {
        int i = getFreeSlot();
        if (0 < i) {
            SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Enter(i, visitor));
            visitors[i - 1] = visitor;
            if (getOwnerId() != visitor.getId()) {
                this.visitors_names.add(visitor.getName());
            }
        }
    }

    public void removeVisitor(TacosCharacter visitor) {
        byte slot = getVisitorSlot(visitor);
        if (0 < slot) {
            SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(slot));
            visitors[slot - 1] = null;
        }
    }

    public byte getVisitorSlot(TacosCharacter visitor) {
        for (byte i = 0; i < visitors.length; i++) {
            if (visitors[i] != null && visitors[i].getId() == visitor.getId()) {
                return (byte) (i + 1);
            }
        }
        if (visitor.getId() == getOwnerId()) {
            return 0;
        }
        return -1;
    }

    public void removeAllVisitors(int error, int type) {
        for (int i = 0; i < visitors.length; i++) {
            TacosCharacter visitor = getVisitor(i);
            if (visitor != null) {
                if (type != -1) {
                    visitor.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(type, error));
                }
                SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(getVisitorSlot(visitor)));
                visitor.setPlayerShop(null);
                visitors[i] = null;
            }
        }
    }

    public List<AbstractMap.SimpleImmutableEntry<Byte, TacosCharacter>> getVisitors() {
        List<AbstractMap.SimpleImmutableEntry<Byte, TacosCharacter>> chrz = new LinkedList<>();
        for (byte i = 0; i < visitors.length; i++) { //include owner or no
            if (visitors[i] != null) {
                chrz.add(new AbstractMap.SimpleImmutableEntry<>((byte) (i + 1), visitors[i]));
            }
        }
        return chrz;
    }

    public byte getFreeSlot() {
        for (byte i = 0; i < visitors.length; i++) {
            if (visitors[i] == null) {
                return (byte) (i + 1);
            }
        }
        return -1;
    }

    public boolean isInBlackList(String bl) {
        return blacklist.contains(bl);
    }

    public void addBlackList(String bl) {
        blacklist.add(bl);
    }

    public void removeBlackList(String bl) {
        blacklist.remove(bl);
    }

    public void sendBlackList(TacosCharacter chr) {
        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_DeliverBlackList(blacklist));
    }

    public void sendVisitor(TacosCharacter chr) {
        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_ESP_DeliverVisitList(visitors_names));
    }


    public final static byte HIRED_MERCHANT = 1;
    public final static byte PLAYER_SHOP = 2;
    public final static byte OMOK = 3;
    public final static byte MATCH_CARD = 4;

    protected boolean available = false;
    protected String ownerName;
    protected String des;
    protected String pass;
    protected int owneraccount;
    protected int itemId;
    protected int channel;
    protected int map;
    protected AtomicInteger meso = new AtomicInteger(0);
    protected List<BoughtItem> bought = new LinkedList<>();
    protected List<MaplePlayerShopItem> items = new LinkedList<>();
    private int storeid;
    private int foothold_id;
    private int item_sub_type;
    private long start;

    @SuppressWarnings("unchecked")
    public TacosMerchant(TacosCharacter owner, int itemId, String desc) {
        TacosRoom room = owner.getWorld().createRoom();
        room.setMerchant(this);
        this.room_id = room.getId();
        this.setPosition(owner.getPosition());
        this.ownerName = owner.getName();
        this.owneraccount = owner.getAccountId();
        this.itemId = itemId;
        this.des = desc;
        this.pass = "";
        this.map = owner.getMapId();
        this.channel = owner.getClient().getChannelId();
        this.item_sub_type = itemId % 100;
        this.foothold_id = owner.getFootholdId();
        start = System.currentTimeMillis();
        setOwnerId(owner.getId());
    }

    public int getMeso() {
        return meso.get();
    }

    public void setMeso(int meso) {
        this.meso.set(meso);
    }

    public boolean saveItems() {
        if (getShopType() != HIRED_MERCHANT) { //hired merch only
            return false;
        }
        Integer packageid = DQ_Hiredmerch.add(getOwnerId(), owneraccount, meso.get());
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
            ItemLoader.HIRED_MERCHANT.saveItems(iters, packageid, owneraccount, getOwnerId());
            return true;
        } catch (SQLException se) {
        }
        return false;
    }

    public String getOwnerName() {
        return ownerName;
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

    public int getItemId() {
        return itemId;
    }

    public boolean isOwner(TacosCharacter chr) {
        return chr.getId() == getOwnerId() && chr.getName().equals(ownerName);
    }

    public String getPassword() {
        if (pass == null) {
            return "";
        }
        return pass;
    }

    public TacosCharacter getMCOwner() {
        return getMap().getPlayerById(getOwnerId());
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

    public void setTest(int owner_id, int fh, int st, int store_id) {
        this.foothold_id = fh;
        this.item_sub_type = st % 100;
        this.storeid = store_id;
        setOwnerId(owner_id); // overwritten
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

    public void setStoreid(int storeid) {
        this.storeid = storeid;
    }

    public List<MaplePlayerShopItem> searchItem(int itemSearch) {
        List<MaplePlayerShopItem> itemz = new LinkedList<>();
        for (MaplePlayerShopItem item : items) {
            if (item.item.getItemId() == itemSearch && item.bundles > 0) {
                itemz.add(item);
            }
        }
        return itemz;
    }

    public void buy(TacosCharacter chr, int item, short quantity) {
        MaplePlayerShopItem pItem = items.get(item);
        Item shopItem = pItem.item;
        Item newItem = shopItem.copy();
        short perbundle = newItem.getQuantity();
        newItem.setQuantity((short) (quantity * perbundle));

        byte flag = newItem.getFlag();

        if (ItemFlag.KARMA_EQ.check(flag)) {
            newItem.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
        } else if (ItemFlag.KARMA_USE.check(flag)) {
            newItem.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
        }

        if (MapleInventoryManipulator.checkSpace(chr, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner()) && MapleInventoryManipulator.addFromDrop(chr, newItem, false)) {
            pItem.bundles -= quantity; // Number remaining in the store
            bought.add(new BoughtItem(newItem.getItemId(), quantity, (pItem.price * quantity), chr.getName()));

            int gainmeso = getMeso() + (pItem.price * quantity);
            setMeso(gainmeso - GameConstants.EntrustedStoreTax(gainmeso));
            chr.gainMeso(-pItem.price * quantity, false);
            saveItems();
        } else {
            chr.Alert("Your inventory is full.");
            chr.updateInv();
        }
    }

    public void closeShop(boolean saveItems, boolean remove, int reason) {
        if (saveItems) {
            saveItems();
        }
        if (remove) {
            TacosWorld.find(0).getChannelServer(channel).removeMerchant(this); // TODO : fix
        }
        getMap().removeMerchant(this);
    }

    public int getTimeLeft() {
        return (int) ((System.currentTimeMillis() - start) / 1000);
    }

    public int getStoreId() {
        return storeid;
    }
}
