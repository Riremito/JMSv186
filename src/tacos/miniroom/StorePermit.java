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
package tacos.miniroom;

import java.util.ArrayList;
import java.util.List;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import tacos.client.TacosCharacter;
import tacos.client.TacosHoldInventory;
import tacos.packet.ops.OpsMREnterResult;
import tacos.packet.ops.OpsMRLeave;
import tacos.packet.ops.OpsMiniRoomType;
import tacos.packet.ops.OpsPSBuy;
import tacos.packet.response.ResCMiniRoomBaseDlg;

/**
 *
 * @author Riremito
 */
public class StorePermit extends MiniRoom {

    private static final int MAX_VISITORS = 3;
    private static final int MAX_ITEMS = 10;
    // the items in this shop, saved with the owner while the shop is opened.
    private final TacosHoldInventory hold;
    private final List<MaplePlayerShopItem> items = new ArrayList<>();
    private final List<String> blackList = new ArrayList<>();
    private boolean open = false;

    public StorePermit(TacosCharacter owner, int itemId, String description) {
        super(owner, itemId, description, MAX_VISITORS);
        this.hold = owner.getMiniRoomInventory();
    }

    @Override
    public OpsMiniRoomType getMiniRoomType() {
        return OpsMiniRoomType.MR_PersonalShop;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }

    @Override
    public boolean isGameOn() {
        return false;
    }

    @Override
    protected void detach(TacosCharacter chr) {
        chr.setPlayerShop(null);
    }

    public boolean isOpen() {
        return this.open;
    }

    public List<MaplePlayerShopItem> getItems() {
        return this.items;
    }

    public int getMaxItems() {
        return MAX_ITEMS;
    }

    private void updateHold() {
        this.hold.setItems(getStocks(this.items));
    }

    // MRP_Create, the dialog for the setup is opened.
    public void create() {
        getOwner().setPlayerShop(this);
        getMap().addPlayerShop(this);
        getOwner().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_PersonalShop(getOwner(), this));
    }

    // MRP_Balloon, the shop is opened and shown over the owner.
    public void open() {
        if (this.open) {
            return;
        }
        this.open = true;
        updateBalloon();
    }

    // PSP_PutItem, only while the shop is being set up.
    public void putItem(TacosCharacter chr, MapleInventoryType type, short slot, short bundles, short per_bundle, int price) {
        if (this.open || !isOwner(chr) || MAX_ITEMS <= this.items.size()) {
            chr.updateStat();
            return;
        }
        MaplePlayerShopItem item = MaplePlayerShopItem.takeFromInventory(chr, type, slot, bundles, per_bundle, price);
        if (item == null) {
            return;
        }
        this.items.add(item);
        updateHold();
        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh(this.items));
    }

    // PSP_MoveItemToInventory, only while the shop is being set up.
    public void moveItemToInventory(TacosCharacter chr, int index) {
        if (!this.open && isOwner(chr) && 0 <= index && index < this.items.size() && this.items.get(index).returnTo(chr)) {
            this.items.remove(index);
            updateHold();
        }
        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh(this.items));
        chr.updateStat(); // CWvsContext::SetExclRequestSent
    }

    // MRP_Enter from a visitor.
    public OpsMREnterResult enter(TacosCharacter visitor) {
        if (!this.open) {
            return OpsMREnterResult.MREnterResult_NoRoom;
        }
        if (this.blackList.contains(visitor.getName())) {
            return OpsMREnterResult.MREnterResult_OnBlockedList;
        }
        if (isFull()) {
            return OpsMREnterResult.MREnterResult_Full;
        }
        visitor.setPlayerShop(this);
        addVisitor(visitor);
        visitor.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_PersonalShop(visitor, this));
        updateBalloon();
        return OpsMREnterResult.MREnterResult_Success;
    }

    // MRP_Leave, the dialog is already closed. the owner closes the shop.
    public void leave(TacosCharacter chr) {
        if (isOwner(chr)) {
            close(OpsMRLeave.MRLeave_HostOut);
            return;
        }
        removeVisitor(chr);
        updateBalloon();
    }

    // the owner closed the shop, logged out or every item is sold, the items go back to the owner.
    public void close(OpsMRLeave reason) {
        kickAllVisitors(reason);
        getMap().removePlayerShop(getObjectId());
        // what does not fit stays in the hold and is saved with the owner.
        updateHold();
        this.items.clear();
        this.hold.returnTo(getOwner());
        getOwner().SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(0, reason));
        detach(getOwner());
        updateBalloon();
    }

    // PSP_Ban, the visitor can not enter again.
    public void ban(TacosCharacter chr, String name) {
        if (!isOwner(chr)) {
            return;
        }
        for (int slot = 1; slot < getMaxUsers(); slot++) {
            TacosCharacter visitor = getUser(slot);
            if (visitor != null && visitor.getName().equals(name)) {
                kickVisitor(visitor, OpsMRLeave.MRLeave_Kicked);
                addBlackList(chr, name);
                updateBalloon();
                return;
            }
        }
    }

    // PSP_DeliverBlackList, PSP_AddBlackList, the black list of the client config.
    public void addBlackList(TacosCharacter chr, String name) {
        if (isOwner(chr) && !this.blackList.contains(name)) {
            this.blackList.add(name);
        }
    }

    // PSP_DeleteBlackList
    public void deleteBlackList(TacosCharacter chr, String name) {
        if (isOwner(chr)) {
            this.blackList.remove(name);
        }
    }

    // PSP_BuyItem from a visitor.
    public void buy(TacosCharacter chr, int index, short quantity) {
        OpsPSBuy result = buyItem(chr, index, quantity);
        // CWvsContext::SetExclRequestSent is cleared by the stat update.
        chr.updateStat();
        if (result != OpsPSBuy.PSBuy_Success) {
            chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_BuyResult(result));
            return;
        }
        broadcast(ResCMiniRoomBaseDlg.MiniRoom_PSP_Refresh(this.items));
        for (MaplePlayerShopItem item : this.items) {
            if (0 < item.bundles) {
                return;
            }
        }
        close(OpsMRLeave.PSLeave_NoMoreItem);
    }

    private OpsPSBuy buyItem(TacosCharacter chr, int index, short quantity) {
        TacosCharacter owner = getOwner();
        if (!this.open || getSlot(chr) <= 0 || index < 0 || this.items.size() <= index) {
            return OpsPSBuy.PSBuy_Unknown;
        }
        if (this.items.get(index).bundles < quantity) {
            return OpsPSBuy.PSBuy_NoStock;
        }
        if (!MaplePlayerShopItem.isValidBuy(this.items, index, quantity)) {
            return OpsPSBuy.PSBuy_Unknown;
        }
        MaplePlayerShopItem shop_item = this.items.get(index);
        int price = shop_item.price * quantity;
        if (chr.getMeso() < price) {
            return OpsPSBuy.PSBuy_NoMoney;
        }
        if (owner.getMeso() + price < 0) {
            return OpsPSBuy.PSBuy_HostTooMuchMoney;
        }
        Item new_item = shop_item.getSoldItem(quantity);
        if (MapleItemInformationProvider.getInstance().isPickupRestricted(new_item.getItemId()) && chr.haveItem(new_item.getItemId(), 1, true, false)) {
            return OpsPSBuy.PSBuy_OnlyItem;
        }
        if (!MapleInventoryManipulator.checkSpace(chr, new_item.getItemId(), new_item.getQuantity(), new_item.getOwner()) || !MapleInventoryManipulator.addFromDrop(chr, new_item, false)) {
            return OpsPSBuy.PSBuy_NoSlot;
        }
        shop_item.bundles -= quantity;
        updateHold();
        chr.gainMeso(-price, false);
        owner.gainMeso(price, false);
        owner.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_PSP_AddSoldItem(index, quantity, chr.getName()));
        return OpsPSBuy.PSBuy_Success;
    }

    // the items left in the shop, one item per listing.
    public static List<Item> getStocks(List<MaplePlayerShopItem> items) {
        List<Item> ret = new ArrayList<>();
        for (MaplePlayerShopItem item : items) {
            if (0 < item.bundles) {
                ret.add(item.getStock());
            }
        }
        return ret;
    }

    // a listing of a personal shop or a hired merchant.
    public static class MaplePlayerShopItem {

        public Item item;
        public short bundles;
        public int price;

        public MaplePlayerShopItem(Item item, short bundles, int price) {
            this.item = item;
            this.bundles = bundles;
            this.price = price;
        }

        public Item getStock() {
            Item stock = item.copy();
            stock.setQuantity((short) (item.getQuantity() * bundles));
            return stock;
        }

        // the item for the buyer, the karma flag is removed.
        public Item getSoldItem(short quantity) {
            Item sold = item.copy();
            sold.setQuantity((short) (quantity * item.getQuantity()));
            byte flag = sold.getFlag();
            if (ItemFlag.KARMA_EQ.check(flag)) {
                sold.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
            } else if (ItemFlag.KARMA_USE.check(flag)) {
                sold.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
            }
            return sold;
        }

        // PSP_BuyItem, ESP_BuyItem
        public static boolean isValidBuy(List<MaplePlayerShopItem> items, int index, short quantity) {
            if (index < 0 || items.size() <= index || quantity <= 0) {
                return false;
            }
            MaplePlayerShopItem item = items.get(index);
            long total_quantity = (long) item.item.getQuantity() * quantity;
            long price = (long) item.price * quantity;
            return quantity <= item.bundles && total_quantity <= Short.MAX_VALUE && price <= Integer.MAX_VALUE;
        }

        // PSP_PutItem, ESP_PutItem, the item is taken out of the inventory.
        public static MaplePlayerShopItem takeFromInventory(TacosCharacter chr, MapleInventoryType type, short slot, short bundles, short per_bundle, int price) {
            if (type == null || price <= 0 || bundles <= 0 || per_bundle <= 0) {
                return null;
            }
            Item inv_item = chr.getInventory(type).getItem(slot);
            int total_quantity = bundles * per_bundle;
            if (inv_item == null || Short.MAX_VALUE < total_quantity || inv_item.getQuantity() < total_quantity) {
                return null;
            }
            byte flag = inv_item.getFlag();
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                chr.updateStat();
                return null;
            }
            if ((ii.isDropRestricted(inv_item.getItemId()) || ii.isAccountShared(inv_item.getItemId())) && !(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                chr.updateStat();
                return null;
            }
            Item sell_item = inv_item.copy();
            // throwing stars and bullets are sold as one bundle.
            if (GameConstants.isThrowingStar(inv_item.getItemId()) || GameConstants.isBullet(inv_item.getItemId())) {
                MapleInventoryManipulator.removeFromSlot(chr, type, slot, inv_item.getQuantity(), true);
                return new MaplePlayerShopItem(sell_item, (short) 1, price);
            }
            MapleInventoryManipulator.removeFromSlot(chr, type, slot, (short) total_quantity, true);
            sell_item.setQuantity(per_bundle);
            return new MaplePlayerShopItem(sell_item, bundles, price);
        }

        // PSP_MoveItemToInventory, ESP_MoveItemToInventory, the stock goes back to the inventory.
        public boolean returnTo(TacosCharacter chr) {
            int quantity = bundles * item.getQuantity();
            if (bundles <= 0 || quantity <= 0 || Short.MAX_VALUE < quantity) {
                return false;
            }
            if (!MapleInventoryManipulator.checkSpace(chr, item.getItemId(), quantity, item.getOwner())) {
                return false;
            }
            item.setQuantity((short) quantity);
            MapleInventoryManipulator.addFromDrop(chr, item, false);
            bundles = 0;
            return true;
        }
    }
}
