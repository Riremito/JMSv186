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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import tacos.client.TacosCharacter;
import tacos.client.TacosHoldInventory;
import tacos.packet.ops.OpsMRLeave;
import tacos.packet.response.ResCMiniRoomBaseDlg;

/**
 *
 * @author Riremito
 */
public class Trade {

    private static final int MAX_ITEMS = 9;
    private final TacosCharacter chr;
    // 0 = the user who opened the trade, 1 = the invited user
    private final int slot;
    private final boolean isPointTrade;
    // the items and meso put in this trade, saved with the character until the trade ends.
    private final TacosHoldInventory hold;
    private Trade partner = null;
    private boolean entered = false;
    private boolean confirmed = false;

    private Trade(TacosCharacter chr, int slot, boolean isPointTrade) {
        this.chr = chr;
        this.slot = slot;
        this.isPointTrade = isPointTrade;
        this.hold = chr.getMiniRoomInventory();
        chr.setTrade(this);
    }

    public TacosCharacter getChr() {
        return this.chr;
    }

    public Trade getPartner() {
        return this.partner;
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean isPointTrade() {
        return this.isPointTrade;
    }

    // the user is in the dialog, the invited user has accepted.
    public boolean isEntered() {
        return this.entered;
    }

    // both users are in the dialog and nothing is confirmed yet.
    private boolean isEditable() {
        return this.partner != null && this.entered && this.partner.entered && !this.confirmed;
    }

    // MRP_Create
    public static void start(TacosCharacter chr, boolean isPointTrade) {
        // items left from a trade or a personal shop that did not fit in the inventory.
        if (!chr.returnMiniRoomInventory()) {
            chr.Notice("Make room in your inventory first.");
            return;
        }
        if (chr.isInMiniRoom()) {
            chr.Notice("You are already in a trade");
            return;
        }
        Trade trade = new Trade(chr, 0, isPointTrade);
        trade.entered = true;
        chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_TradingRoom(chr, trade));
    }

    // MRP_Invite
    public void invite(TacosCharacter target) {
        if (this.slot != 0 || this.partner != null) {
            return;
        }
        if (target == null || target.isInMiniRoom() || !target.getMiniRoomInventory().isEmpty()) {
            this.chr.Notice("The other player is already trading with someone else.");
            cancel();
            return;
        }
        this.partner = new Trade(target, 1, this.isPointTrade);
        this.partner.partner = this;
        target.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Invite(this.chr, this.isPointTrade));
    }

    // MRP_InviteResult, the invited user declined.
    public void decline() {
        if (this.slot != 1 || this.entered) {
            return;
        }
        this.partner.chr.Notice(this.chr.getName() + " has declined your trade request");
        cancel();
    }

    // MRP_Enter, the invited user accepted.
    public void enter() {
        if (this.slot != 1 || this.entered || this.partner.chr.getTrade() != this.partner) {
            this.chr.Notice("The other player has already closed the trade");
            return;
        }
        this.entered = true;
        this.partner.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Enter(this.slot, this.chr));
        this.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_EnterResult_TradingRoom(this.chr, this));
    }

    // MRP_Chat
    public void chat(String message) {
        String text = this.chr.getName() + " : " + message;
        this.chr.dropMessage(-2, text);
        if (this.partner != null && this.partner.entered) {
            this.partner.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Chat(1, text));
        }
    }

    // TRP_PutItem
    public void putItem(MapleInventoryType type, short item_slot, short quantity, byte target_slot) {
        if (!isEditable() || type == null || MAX_ITEMS <= this.hold.getItems().size()) {
            return;
        }
        Item item = this.chr.getInventory(type).getItem(item_slot);
        if (item == null || GameConstants.isPet(item.getItemId())) {
            return;
        }
        // throwing stars and bullets are put as a whole.
        boolean is_rechargeable = GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId());
        if (is_rechargeable) {
            quantity = (short) item.getQuantity();
        }
        if (quantity <= 0 || item.getQuantity() < quantity) {
            return;
        }
        if ((type == MapleInventoryType.EQUIP || type == MapleInventoryType.CASH) && quantity != 1) {
            return;
        }
        byte flag = item.getFlag();
        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
            this.chr.updateInv();
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if ((ii.isDropRestricted(item.getItemId()) || ii.isAccountShared(item.getItemId())) && !(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
            this.chr.updateInv();
            return;
        }
        Item trade_item = item.copy();
        trade_item.setQuantity(quantity);
        trade_item.setPosition(getTargetSlot(target_slot));
        MapleInventoryManipulator.removeFromSlot(this.chr, type, item_slot, quantity, true);
        this.hold.addItem(trade_item);
        this.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_PutItem((byte) 0, trade_item));
        this.partner.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_PutItem((byte) 1, trade_item));
    }

    // the requested trade slot (1-9), or the first free one when it is used.
    private short getTargetSlot(byte target_slot) {
        boolean used = (target_slot < 1 || MAX_ITEMS < target_slot);
        for (Item item : this.hold.getItems()) {
            if (item.getPosition() == target_slot) {
                used = true;
            }
        }
        if (!used) {
            return target_slot;
        }
        for (short position = 1; position <= MAX_ITEMS; position++) {
            boolean free = true;
            for (Item item : this.hold.getItems()) {
                if (item.getPosition() == position) {
                    free = false;
                }
            }
            if (free) {
                return position;
            }
        }
        return MAX_ITEMS;
    }

    // TRP_PutMoney
    public void putMoney(int meso) {
        if (!isEditable() || meso <= 0 || this.chr.getMeso() < meso || this.hold.getMeso() + meso <= 0) {
            return;
        }
        this.chr.gainMeso(-meso, false, true, false);
        this.hold.addMeso(meso);
        this.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_PutMoney((byte) 0, this.hold.getMeso()));
        this.partner.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_PutMoney((byte) 1, this.hold.getMeso()));
    }

    // TRP_Trade, the items are exchanged when both users confirmed.
    public void confirm() {
        if (!isEditable()) {
            return;
        }
        this.confirmed = true;
        this.partner.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_TRP_Trade());
        if (!this.partner.confirmed) {
            return;
        }
        OpsMRLeave result = canReceive(this.partner.hold);
        if (result == null) {
            result = this.partner.canReceive(this.hold);
        }
        if (result != null) {
            end(result);
            return;
        }
        List<Item> items = new ArrayList<>(this.hold.getItems());
        int meso = this.hold.getMeso();
        List<Item> partner_items = new ArrayList<>(this.partner.hold.getItems());
        int partner_meso = this.partner.hold.getMeso();
        this.hold.clear();
        this.partner.hold.clear();
        receive(partner_items, partner_meso);
        this.partner.receive(items, meso);
        this.chr.setTrade(null);
        this.partner.chr.setTrade(null);
        this.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(this.slot, OpsMRLeave.TRLeave_TradeDone));
        this.partner.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(this.partner.slot, OpsMRLeave.TRLeave_TradeDone));
    }

    // null = fine, the inventory slots or the meso would go over the limit, or the item can only be owned once.
    private OpsMRLeave canReceive(TacosHoldInventory goods) {
        int meso = goods.getMeso() - GameConstants.getTaxAmount(goods.getMeso());
        if (0 < meso && this.chr.getMeso() + meso < 0) {
            return OpsMRLeave.TRLeave_TradeFail;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Map<MapleInventoryType, Integer> needed = new EnumMap<>(MapleInventoryType.class);
        for (Item item : goods.getItems()) {
            MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
            needed.merge(type, 1, Integer::sum);
            if (ii.isPickupRestricted(item.getItemId()) && this.chr.getInventory(type).findById(item.getItemId()) != null) {
                return OpsMRLeave.TRLeave_TradeFail_OnlyItem;
            }
        }
        for (Map.Entry<MapleInventoryType, Integer> entry : needed.entrySet()) {
            if (this.chr.getInventory(entry.getKey()).getNumFreeSlot() < entry.getValue()) {
                return OpsMRLeave.TRLeave_TradeFail;
            }
        }
        return null;
    }

    private void receive(List<Item> items, int meso) {
        for (Item item : items) {
            byte flag = item.getFlag();
            if (ItemFlag.KARMA_EQ.check(flag)) {
                item.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
            } else if (ItemFlag.KARMA_USE.check(flag)) {
                item.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
            }
            MapleInventoryManipulator.addFromDrop(this.chr, item, false);
        }
        if (0 < meso) {
            this.chr.gainMeso(meso - GameConstants.getTaxAmount(meso), false, true, false);
        }
    }

    // MRP_Leave, disconnection or a failed trade, everything goes back to the inventory of both users.
    public void cancel() {
        end(OpsMRLeave.MRLeave_Closed);
    }

    private void end(OpsMRLeave reason) {
        giveBack(reason);
        if (this.partner != null) {
            this.partner.giveBack(reason);
        }
    }

    private void giveBack(OpsMRLeave reason) {
        if (this.chr.getTrade() != this) {
            return;
        }
        // what does not fit stays in the hold and is saved with the character.
        this.hold.returnTo(this.chr);
        this.chr.setTrade(null);
        if (this.entered) {
            this.chr.SendPacket(ResCMiniRoomBaseDlg.MiniRoom_MRP_Leave(this.slot, reason));
        }
    }
}
