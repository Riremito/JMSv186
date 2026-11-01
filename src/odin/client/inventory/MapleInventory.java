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
package odin.client.inventory;

import odin.constants.GameConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tacos.packet.ops.OpsBodyPart;

public class MapleInventory implements Iterable<Item> {

    private Map<Integer, Item> inventory;
    private byte slotLimit = 96;
    private MapleInventoryType type;

    public MapleInventory(MapleInventoryType type) {
        this.inventory = new LinkedHashMap<>();
        this.type = type;
    }

    public void resetForClone() {
        this.inventory = new LinkedHashMap<>();
    }

    public void addSlot(byte slot) {
        this.slotLimit += slot;

        if (slotLimit > 96) {
            slotLimit = 96;
        }
    }

    public void setSlotLimit(byte slot) {
        if (slot > 96) {
            slot = 96;
        }
        slotLimit = slot;
    }

    public Item findByUniqueId(long itemId) {
        for (Item item : inventory.values()) {
            if (item.getUniqueId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public int countById(int itemId) {
        int possesed = 0;
        for (Item item : inventory.values()) {
            if (item.getItemId() == itemId) {
                possesed += item.getQuantity();
            }
        }
        return possesed;
    }

    public Collection<Item> list() {
        return inventory.values();
    }

    public int addItem(Item item) {
        int slotId = getNextFreeSlot();
        if (slotId < 0) {
            return -1;
        }
        inventory.put(slotId, item);
        item.setPosition(slotId);
        return slotId;
    }

    public void addFromDB(Item item) {
        if (item.getPosition() < 0 && !type.equals(MapleInventoryType.EQUIPPED)) {
            // This causes a lot of stuck problem, until we are done with position checking
            return;
        }
        inventory.put(item.getPosition(), item);
    }

    public void move(int sSlot, int dSlot, int slotMax) {
        if (dSlot > slotLimit) {
            return;
        }
        Item source = inventory.get(sSlot);
        Item target = inventory.get(dSlot);
        if (source == null) {
            return;
        }
        if (target == null) {
            source.setPosition(dSlot);
            inventory.put(dSlot, source);
            inventory.remove(sSlot);
        } else if (target.getItemId() == source.getItemId() && !GameConstants.isThrowingStar(source.getItemId()) && !GameConstants.isBullet(source.getItemId()) && target.getOwner().equals(source.getOwner()) && target.getExpiration() == source.getExpiration()) {
            if (type.getType() == MapleInventoryType.EQUIP.getType() || type.getType() == MapleInventoryType.CASH.getType()) {
                swap(target, source);
            } else if (source.getQuantity() + target.getQuantity() > slotMax) {
                source.setQuantity(((source.getQuantity() + target.getQuantity()) - slotMax));
                target.setQuantity(slotMax);
            } else {
                target.setQuantity((source.getQuantity() + target.getQuantity()));
                inventory.remove(sSlot);
            }
        } else {
            swap(target, source);
        }
    }

    private void swap(Item source, Item target) {
        inventory.remove(source.getPosition());
        inventory.remove(target.getPosition());
        int swapPos = source.getPosition();
        source.setPosition(target.getPosition());
        target.setPosition(swapPos);
        inventory.put(source.getPosition(), source);
        inventory.put(target.getPosition(), target);
    }

    public boolean removeItem(int slot) {
        return removeItem(slot, 1, false);
    }

    // returns true if the slot was removed.
    public boolean removeItem(int slot, int quantity, boolean allowZero) {
        Item item = inventory.get(slot);
        if (item == null) { // TODO is it ok not to throw an exception here?
            return false;
        }
        // Equip keeps its quantity at 1, so decide by the remaining quantity instead of item.getQuantity().
        int remaining = Math.max(item.getQuantity() - quantity, 0);
        item.setQuantity(remaining);
        if (remaining == 0 && !allowZero) {
            removeSlot(slot);
            return true;
        }
        return false;
    }

    public void removeSlot(int slot) {
        inventory.remove(slot);
    }

    public boolean isFull() {
        return inventory.size() >= slotLimit;
    }

    public boolean isFull(int margin) {
        return inventory.size() + margin >= slotLimit;
    }

    public int getNextItem(int slot) {
        for (int i = slot; i <= slotLimit; i++) {
            if (inventory.keySet().contains(i)) {
                return i;
            }
        }
        return 0;
    }

    public int getNumFreeSlot() {
        if (isFull()) {
            return 0;
        }
        int free = 0;
        for (int i = 1; i <= slotLimit; i++) {
            if (!inventory.keySet().contains(i)) {
                free++;
            }
        }
        return free;
    }

    public MapleInventoryType getType() {
        return type;
    }

    public Item getItemByBodyPart(OpsBodyPart body_part) {
        if (type != MapleInventoryType.EQUIPPED) {
            return null;
        }
        return inventory.get(body_part.get());
    }

    // used by script
    public byte getSlotLimit() {
        return slotLimit;
    }

    // used by script
    public Item findById(int itemId) {
        for (Item item : inventory.values()) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    // used by script
    public List<Item> listById(int itemId) {
        List<Item> ret = new ArrayList<>();
        for (Item item : inventory.values()) {
            if (item.getItemId() == itemId) {
                ret.add(item);
            }
        }
        if (ret.size() > 1) {
            Collections.sort(ret, (item1, item2) -> Item.comparePosition(item1, item2));
        }
        return ret;
    }

    // used by script
    public Item getItem(int slot) {
        return inventory.get(slot);
    }

    // used by script
    public int getNextFreeSlot() {
        if (isFull()) {
            return -1;
        }
        for (int i = 1; i <= slotLimit; i++) {
            if (!inventory.keySet().contains(i)) {
                return i;
            }
        }
        return -1;
    }

    // used by script
    @Override
    public Iterator<Item> iterator() {
        return Collections.unmodifiableCollection(inventory.values()).iterator();
    }
}
