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
package tacos.client;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import odin.client.inventory.Item;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;

/**
 * Items and meso taken out of the inventory while they are in a trade, a
 * personal shop, a hired merchant or the store bank.
 *
 * @author Riremito
 */
public class TacosHoldInventory {

    private final ArrayList<Item> items = new ArrayList<>();
    private int meso = 0;

    public ArrayList<Item> getItems() {
        return this.items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public void setItems(List<Item> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public int getMeso() {
        return this.meso;
    }

    public void setMeso(int meso) {
        this.meso = meso;
    }

    public void addMeso(int meso) {
        this.meso += meso;
    }

    public boolean isEmpty() {
        return this.items.isEmpty() && this.meso == 0;
    }

    public void clear() {
        this.items.clear();
        this.meso = 0;
    }

    // every item gets a slot of its own, stacking is not counted.
    public boolean hasRoomIn(TacosCharacter chr) {
        Map<MapleInventoryType, Integer> needed = new EnumMap<>(MapleInventoryType.class);
        for (Item item : this.items) {
            needed.merge(GameConstants.getInventoryType(item.getItemId()), 1, Integer::sum);
        }
        for (Map.Entry<MapleInventoryType, Integer> entry : needed.entrySet()) {
            if (chr.getInventory(entry.getKey()).getNumFreeSlot() < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    // one of the items can only be owned once and the character already has it.
    public boolean hasOnlyItemIn(TacosCharacter chr) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (Item item : this.items) {
            if (ii.isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1, true, false)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMesoRoomIn(TacosCharacter chr) {
        return 0 <= chr.getMeso() + this.meso;
    }

    public boolean canReturnTo(TacosCharacter chr) {
        return hasRoomIn(chr) && !hasOnlyItemIn(chr) && hasMesoRoomIn(chr);
    }

    // moves everything into the character's inventory, what does not fit stays here.
    public void returnTo(TacosCharacter chr) {
        Iterator<Item> it = this.items.iterator();
        while (it.hasNext()) {
            if (MapleInventoryManipulator.addFromDrop(chr, it.next(), false)) {
                it.remove();
            }
        }
        if (0 < this.meso && chr.gainMeso(this.meso, false)) {
            this.meso = 0;
        }
    }
}
