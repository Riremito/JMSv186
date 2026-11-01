package odin.server;

import java.awt.Point;
import tacos.client.TacosCharacter;
import tacos.server.map.object.TacosPet;
import tacos.wz.CharacterWz.EquipInfo;
import java.util.function.Predicate;
import odin.constants.GameConstants;
import odin.client.inventory.Equip;
import odin.client.inventory.Item;
import odin.client.inventory.Item.ItemFlag;
import tacos.client.TacosCharacter.PlayerStats;
import odin.client.inventory.MapleInventoryType;
import tacos.packet.ops.OpsMessage;
import tacos.packet.ops.OpsDropPickUpMessage;
import tacos.packet.response.builder.PB_Message;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_InvOp;
import odin.server.maps.AramiaFireWorks;
import tacos.server.TacosWorld;

public class MapleInventoryManipulator {

    public static boolean addbyItem(TacosCharacter chr, Item item) {
        return addbyItem(chr, item, false) >= 0;
    }

    // fromcs : the cash shop sends its own packet, returns the new slot or -1
    public static short addbyItem(TacosCharacter chr, Item item, boolean fromcs) {
        if (!fromcs) {
            PB_InvOp.Builder io = PB_InvOp.builder();
            boolean added = addbyItem(chr, item, io);
            chr.sendInventoryOperation(false, io);
            return added ? item.getPosition() : -1;
        }
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        short slot = chr.getInventory(type).addItem(item);
        if (slot != -1) {
            chr.havePartyQuest(item.getItemId());
        }
        return slot;
    }

    public static boolean addbyItem(TacosCharacter chr, Item item, PB_InvOp.Builder io) {
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        if (!addNewSlot(chr, type, item, io)) {
            sendInventoryFull(chr);
            return false;
        }
        chr.havePartyQuest(item.getItemId());
        return true;
    }

    public static int getUniqueId(int itemId, TacosPet pet) {
        if (GameConstants.isPet(itemId)) {
            return (pet != null) ? pet.getUniqueId() : TacosWorld.getNextItemUniqueId();
        }
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) {
            return TacosWorld.getNextItemUniqueId();
        }
        return -1;
    }

    public static boolean addById(TacosCharacter chr, int itemId, short quantity) {
        return addById(chr, itemId, quantity, null, null, 0);
    }

    // period : days
    public static boolean addById(TacosCharacter chr, int itemId, short quantity, String owner, TacosPet pet, long period) {
        PB_InvOp.Builder io = PB_InvOp.builder();
        boolean added = addById(chr, itemId, quantity, owner, pet, period, io);
        chr.sendInventoryOperation(false, io);
        return added;
    }

    public static boolean addById(TacosCharacter chr, int itemId, short quantity, String owner, TacosPet pet, long period, PB_InvOp.Builder io) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && chr.haveItem(itemId, 1, true, false)) {
            sendPickupUnavailable(chr);
            return false;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int uniqueId = getUniqueId(itemId, pet);
        if (type.equals(MapleInventoryType.EQUIP)) {
            if (quantity != 1) {
                return false;
            }
            Item equip = ii.getDefaultEquipById(itemId);
            if (owner != null) {
                equip.setOwner(owner);
            }
            equip.setUniqueId(uniqueId);
            if (period > 0) {
                equip.setExpiration(getExpiration(period));
            }
            if (!addNewSlot(chr, type, equip, io)) {
                sendInventoryFull(chr);
                return false;
            }
            chr.havePartyQuest(itemId);
            return true;
        }

        if (GameConstants.isRechargable(itemId)) {
            // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
            Item stack = new Item(itemId, (byte) 0, quantity, (byte) 0, uniqueId);
            if (period > 0) {
                stack.setExpiration(getExpiration(period));
            }
            if (!addNewSlot(chr, type, stack, io)) {
                sendInventoryFull(chr);
                return false;
            }
            chr.updateInv();
            chr.havePartyQuest(itemId);
            return true;
        }

        short slotMax = ii.getSlotMax(itemId);
        quantity = fillExistingSlots(chr, type, itemId, quantity, slotMax, eItem -> (owner == null || eItem.getOwner().equals(owner)) && eItem.getExpiration() == -1, io);
        // add new slots if there is still something left
        while (quantity > 0) {
            short newQuantity = (short) Math.min(quantity, slotMax);
            if (newQuantity == 0) {
                chr.havePartyQuest(itemId);
                chr.updateInv();
                return false;
            }
            quantity -= newQuantity;
            Item stack = new Item(itemId, (byte) 0, newQuantity, (byte) 0, uniqueId);
            if (owner != null) {
                stack.setOwner(owner);
            }
            if (period > 0) {
                stack.setExpiration(getExpiration(period));
            }
            if (pet != null) {
                stack.setPet(pet);
            }
            if (!addNewSlot(chr, type, stack, io)) {
                sendInventoryFull(chr);
                return false;
            }
            if (pet != null) {
                pet.setInventoryPosition(stack.getPosition());
            }
        }
        chr.havePartyQuest(itemId);
        return true;
    }

    // period (days) to the expiration time
    private static long getExpiration(long period) {
        return System.currentTimeMillis() + period * 24 * 60 * 60 * 1000;
    }

    // puts the item into a free slot, returns false when the inventory is full
    private static boolean addNewSlot(TacosCharacter chr, MapleInventoryType type, Item item, PB_InvOp.Builder io) {
        if (chr.getInventory(type).addItem(item) == -1) {
            return false;
        }
        io.add(type, item);
        return true;
    }

    // fills the existing stacks of the item first, returns the quantity left
    private static short fillExistingSlots(TacosCharacter chr, MapleInventoryType type, int itemId, short quantity, short slotMax, Predicate<Item> canStack, PB_InvOp.Builder io) {
        for (Item stack : chr.getInventory(type).listById(itemId)) {
            if (quantity <= 0) {
                break;
            }
            short oldQuantity = stack.getQuantity();
            if (oldQuantity < slotMax && canStack.test(stack)) {
                short newQuantity = (short) Math.min(oldQuantity + quantity, slotMax);
                quantity -= (newQuantity - oldQuantity);
                stack.setQuantity(newQuantity);
                io.update(type, stack);
            }
        }
        return quantity;
    }

    private static void sendInventoryFull(TacosCharacter chr) {
        sendDropPickUpMessage(chr, OpsDropPickUpMessage.PICKUP_INVENTORY_FULL);
    }

    private static void sendPickupUnavailable(TacosCharacter chr) {
        sendDropPickUpMessage(chr, OpsDropPickUpMessage.PICKUP_UNAVAILABLE);
    }

    private static void sendDropPickUpMessage(TacosCharacter chr, OpsDropPickUpMessage message) {
        chr.updateInv();
        chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(message).build()));
    }

    // returns one of the received items, null when nothing was received
    public static Item addbyId_Gachapon(TacosCharacter chr, int itemId, short quantity) {
        if (chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1 || chr.getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1 || chr.getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1 || chr.getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1) {
            return null;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && chr.haveItem(itemId, 1, true, false)) {
            sendPickupUnavailable(chr);
            return null;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        PB_InvOp.Builder io = PB_InvOp.builder();
        if (type.equals(MapleInventoryType.EQUIP)) {
            if (quantity != 1) {
                return null;
            }
            Item equip = ii.getEquipById(itemId);
            if (!addNewSlot(chr, type, equip, io)) {
                return null;
            }
            chr.sendInventoryOperation(true, io);
            chr.havePartyQuest(itemId);
            return equip;
        }

        if (GameConstants.isRechargable(itemId)) {
            // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
            Item stack = new Item(itemId, (byte) 0, quantity, (byte) 0);
            if (!addNewSlot(chr, type, stack, io)) {
                return null;
            }
            chr.sendInventoryOperation(false, io);
            chr.havePartyQuest(itemId);
            return stack;
        }

        short slotMax = ii.getSlotMax(itemId);
        short left = fillExistingSlots(chr, type, itemId, quantity, slotMax, eItem -> eItem.getExpiration() == -1, io);
        Item received = (left < quantity) ? chr.getInventory(type).findById(itemId) : null;
        // add new slots if there is still something left
        while (left > 0) {
            short newQuantity = (short) Math.min(left, slotMax);
            if (newQuantity == 0) {
                break;
            }
            left -= newQuantity;
            Item stack = new Item(itemId, (byte) 0, newQuantity, (byte) 0);
            if (!addNewSlot(chr, type, stack, io)) {
                break;
            }
            received = stack;
        }
        chr.sendInventoryOperation(false, io);
        if (received != null) {
            chr.havePartyQuest(itemId);
        }
        return received;
    }

    public static boolean addFromDrop(TacosCharacter chr, Item item, boolean show) {
        return addFromDrop(chr, item, show, false);
    }

    // enhance : the potential of the dropped equip is revealed
    public static boolean addFromDrop(TacosCharacter chr, Item item, boolean show, boolean enhance) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int itemId = item.getItemId();
        if (ii.isPickupRestricted(itemId) && chr.haveItem(itemId, 1, true, false)) {
            sendPickupUnavailable(chr);
            return false;
        }
        int before = chr.itemQuantity(itemId);
        short quantity = item.getQuantity();
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        PB_InvOp.Builder io = PB_InvOp.builder();

        if (type.equals(MapleInventoryType.EQUIP)) {
            if (quantity != 1) {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
            if (enhance) {
                ((Equip) item).getEnhancement().usePotentialScroll();
            }
            if (!addNewSlot(chr, type, item, io)) {
                sendInventoryFull(chr);
                return false;
            }
            chr.sendInventoryOperation(true, io);
        } else if (GameConstants.isRechargable(itemId)) {
            // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
            if (!addNewSlot(chr, type, newStackOf(item, quantity), io)) {
                sendInventoryFull(chr);
                return false;
            }
            chr.sendInventoryOperation(false, io);
            chr.updateInv();
        } else {
            if (quantity <= 0) {
                return false;
            }
            short slotMax = ii.getSlotMax(itemId);
            quantity = fillExistingSlots(chr, type, itemId, quantity, slotMax, eItem -> item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration(), io);
            // add new slots if there is still something left
            while (quantity > 0) {
                short newQuantity = (short) Math.min(quantity, slotMax);
                if (!addNewSlot(chr, type, newStackOf(item, newQuantity), io)) {
                    chr.sendInventoryOperation(true, io);
                    sendInventoryFull(chr);
                    // the rest stays on the dropped item
                    item.setQuantity(quantity);
                    return false;
                }
                quantity -= newQuantity;
            }
            chr.sendInventoryOperation(true, io);
        }
        if (before == 0) {
            switch (itemId) {
                case AramiaFireWorks.KEG_ID:
                    chr.Notice("You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                case AramiaFireWorks.SUN_ID:
                    chr.Notice("You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                case AramiaFireWorks.DEC_ID:
                    chr.Notice("You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
            }
        }
        chr.havePartyQuest(itemId);
        if (show) {
            chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_ITEM).ItemID(itemId).Inc_ItemCount(item.getQuantity()).build()));
        }
        return true;
    }

    // a new stack with the same properties as the dropped item
    private static Item newStackOf(Item item, short quantity) {
        Item stack = new Item(item.getItemId(), (byte) 0, quantity, item.getFlag());
        stack.setExpiration(item.getExpiration());
        stack.setOwner(item.getOwner());
        stack.setPet(item.getPet());
        return stack;
    }

    public static boolean checkSpace(TacosCharacter chr, int itemId, int quantity, String owner) {
        if (chr == null) {
            return false;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && chr.haveItem(itemId, 1, true, false)) {
            chr.updateInv();
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemId)) {
            return false;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        if (chr.getInventory(type) == null) {
            return false;
        }
        if (type.equals(MapleInventoryType.EQUIP)) {
            return !chr.getInventory(type).isFull();
        }
        short slotMax = ii.getSlotMax(itemId);
        if (!GameConstants.isRechargable(itemId)) {
            // the existing stacks take as much as they can first
            for (Item stack : chr.getInventory(type).listById(itemId)) {
                short oldQuantity = stack.getQuantity();
                if (oldQuantity < slotMax && owner != null && owner.equals(stack.getOwner())) {
                    short newQuantity = (short) Math.min(oldQuantity + quantity, slotMax);
                    quantity -= (newQuantity - oldQuantity);
                }
                if (quantity <= 0) {
                    break;
                }
            }
        }
        // new slots for the rest
        int numSlotsNeeded = (slotMax > 0) ? (int) Math.ceil(((double) quantity) / slotMax) : 1;
        return !chr.getInventory(type).isFull(numSlotsNeeded - 1);
    }

    public static void removeFromSlot(TacosCharacter chr, MapleInventoryType type, short slot, short quantity, boolean unlock) {
        removeFromSlot(chr, type, slot, quantity, unlock, false);
    }

    public static void removeFromSlot(TacosCharacter chr, MapleInventoryType type, short slot, short quantity, boolean unlock, boolean consume) {
        if (chr == null) {
            return;
        }
        PB_InvOp.Builder io = PB_InvOp.builder();
        removeFromSlot(chr, type, slot, quantity, consume, io);
        chr.sendInventoryOperation(unlock, io);
    }

    // consume : a used up rechargeable item stays in its slot with 0 left
    public static void removeFromSlot(TacosCharacter chr, MapleInventoryType type, short slot, short quantity, boolean consume, PB_InvOp.Builder io) {
        if (chr == null || chr.getInventory(type) == null) {
            return;
        }
        Item item = chr.getInventory(type).getItem(slot);
        if (item == null) {
            return;
        }
        boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
        if (chr.getInventory(type).removeItem(slot, quantity, allowZero)) {
            io.remove(type, slot);
        } else {
            io.update(type, item);
        }
    }

    // returns false when the player did not have enough, what they had is removed anyway
    public static boolean removeById(TacosCharacter chr, MapleInventoryType type, int itemId, int quantity, boolean unlock, boolean consume) {
        PB_InvOp.Builder io = PB_InvOp.builder();
        boolean removed = removeById(chr, type, itemId, quantity, consume, io);
        chr.sendInventoryOperation(unlock, io);
        return removed;
    }

    public static boolean removeById(TacosCharacter chr, MapleInventoryType type, int itemId, int quantity, boolean consume, PB_InvOp.Builder io) {
        int remaining = quantity;
        for (Item item : chr.getInventory(type).listById(itemId)) {
            if (remaining <= item.getQuantity()) {
                removeFromSlot(chr, type, item.getPosition(), (short) remaining, consume, io);
                remaining = 0;
                break;
            }
            remaining -= item.getQuantity();
            removeFromSlot(chr, type, item.getPosition(), item.getQuantity(), consume, io);
        }
        return remaining <= 0;
    }

    public static void move(TacosCharacter chr, MapleInventoryType type, short src, short dst) {
        if (src < 0 || dst < 0 || dst > chr.getInventory(type).getSlotLimit() || src == dst) {
            return;
        }
        Item source = chr.getInventory(type).getItem(src);
        Item target = chr.getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        short targetQuantity = (target != null) ? target.getQuantity() : -1;
        short sourceQuantity = source.getQuantity();
        short slotMax = MapleItemInformationProvider.getInstance().getSlotMax(source.getItemId());
        chr.getInventory(type).move(src, dst, slotMax);

        PB_InvOp.Builder io = PB_InvOp.builder();
        if (!type.equals(MapleInventoryType.EQUIP) && target != null
                && target.getItemId() == source.getItemId()
                && target.getOwner().equals(source.getOwner())
                && target.getExpiration() == source.getExpiration()
                && !GameConstants.isRechargable(source.getItemId())
                && !type.equals(MapleInventoryType.CASH)) {
            if ((targetQuantity + sourceQuantity) > slotMax) {
                // アイテム個数がMAXを超過する場合は古い移動前のスロットも維持
                io.update(type, source).update(type, target);
            } else {
                // アイテム個数がMAXを超過しない場合は古い移動前のスロットを削除
                io.move(type, src, target.getPosition()).remove(type, src).update(type, target);
            }
        } else {
            io.move(type, src, dst);
        }
        chr.sendInventoryOperation(true, io);
    }

    // the equips moved by the operations change the avatar
    private static void sendEquipOperation(TacosCharacter chr, PB_InvOp.Builder io) {
        if (io.isEmpty()) {
            return;
        }
        chr.sendInventoryOperation(true, io);
        chr.equipChanged();
    }

    public static void equip(TacosCharacter chr, short src, short dst) {
        if (chr == null) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        PlayerStats stat = chr.getStat();
        Equip source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src);

        if (source == null || source.getDurability() == 0) {
            chr.updateInv();
            return;
        }

        EquipInfo info = source.getInfo();
        if (dst < -999 && !GameConstants.isEvanDragonItem(source.getItemId()) && dst != -1400) {
            chr.updateInv();
            return;
        } else if (dst >= -999 && dst < -99 && info.getCash() == 0) {
            chr.updateInv();
            return;
        }
        if (!ii.canEquip(info, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), stat.getTotalStr(), stat.getTotalDex(), stat.getTotalLuk(), stat.getTotalInt(), stat.levelBonus)) {
            chr.updateInv();
            return;
        }
        if (!ii.isCash(source.getItemId()) && !GameConstants.isMountItemAvailable(source.getItemId(), chr.getJob())) {
            chr.updateInv();
            return;
        }
        if (GameConstants.isKatara(source.getItemId())) {
            dst = (byte) -10; //shield slot
        }
        if (GameConstants.isEvanDragonItem(source.getItemId()) && (chr.getJob() < 2200 || chr.getJob() > 2218)) {
            chr.updateInv();
            return;
        }

        // the items taken off and the equip itself go in one packet
        PB_InvOp.Builder io = PB_InvOp.builder();
        switch (dst) {
            case -6: { // Bottom
                Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                if (top != null && GameConstants.isOverall(top.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        sendInventoryFull(chr);
                        return;
                    }
                    unequip(chr, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot(), io);
                }
                break;
            }
            case -5: { // Top
                Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                Item bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -6);
                if (top != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull(bottom != null ? 1 : 0)) {
                        sendInventoryFull(chr);
                        return;
                    }
                    unequip(chr, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot(), io);
                }
                if (bottom != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        sendEquipOperation(chr, io);
                        sendInventoryFull(chr);
                        return;
                    }
                    unequip(chr, (byte) -6, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot(), io);
                }
                break;
            }
            case -10: { // Shield
                Item weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
                if (GameConstants.isKatara(source.getItemId())) {
                    if ((chr.getJob() != 900 && (chr.getJob() < 430 || chr.getJob() > 434)) || weapon == null || !GameConstants.isDagger(weapon.getItemId())) {
                        sendInventoryFull(chr);
                        return;
                    }
                } else if (weapon != null && GameConstants.isTwoHanded(weapon.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        sendInventoryFull(chr);
                        return;
                    }
                    unequip(chr, (byte) -11, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot(), io);
                }
                break;
            }
            case -11: { // Weapon
                Item shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
                if (shield != null && GameConstants.isTwoHanded(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        sendInventoryFull(chr);
                        return;
                    }
                    unequip(chr, (byte) -10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot(), io);
                }
                break;
            }
        }
        source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst); // Currently equipping
        if (source == null) {
            sendEquipOperation(chr, io);
            chr.updateInv();
            return;
        }
        boolean flagChanged = false;
        if (info.getEquipTradeBlock() == 1) { // Block trade when equipped.
            byte flag = source.getFlag();
            if (!ItemFlag.UNTRADEABLE.check(flag)) {
                flag |= ItemFlag.UNTRADEABLE.getValue();
                source.setFlag(flag);
                flagChanged = true;
            }
        }

        chr.getInventory(MapleInventoryType.EQUIP).removeSlot(src);
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(dst);
        }
        source.setPosition(dst);
        chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(MapleInventoryType.EQUIP).addFromDB(target);
        }
        io.move(MapleInventoryType.EQUIP, src, dst);
        if (flagChanged) {
            // the item is encoded when the packet is built, so it is sent after the move with its new position
            io.add(MapleInventoryType.EQUIP, source);
        }
        sendEquipOperation(chr, io);
    }

    public static void unequip(TacosCharacter chr, short src, short dst) {
        PB_InvOp.Builder io = PB_InvOp.builder();
        if (unequip(chr, src, dst, io)) {
            sendEquipOperation(chr, io);
        }
    }

    private static boolean unequip(TacosCharacter chr, short src, short dst, PB_InvOp.Builder io) {
        Equip source = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);

        if (dst < 0 || source == null) {
            return false;
        }
        if (target != null && src <= 0) { // do not allow switching with equip
            chr.updateInv();
            return false;
        }
        chr.getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            chr.getInventory(MapleInventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        chr.getInventory(MapleInventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }
        io.move(MapleInventoryType.EQUIP, src, dst);
        return true;
    }

    public static boolean drop(TacosCharacter chr, MapleInventoryType type, short src, short quantity) {
        return drop(chr, type, src, quantity, false);
    }

    // npcInduced : pets can be dropped too
    public static boolean drop(TacosCharacter chr, MapleInventoryType type, short src, short quantity, boolean npcInduced) {
        if (chr == null) {
            return false;
        }
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        Item source = chr.getInventory(type).getItem(src);
        if (quantity < 0 || source == null || (!npcInduced && GameConstants.isPet(source.getItemId())) || (quantity == 0 && !GameConstants.isRechargable(source.getItemId()))) {
            chr.updateInv();
            return false;
        }

        byte flag = source.getFlag();
        if (quantity > source.getQuantity()) {
            chr.updateInv();
            return false;
        }
        if (ItemFlag.LOCK.check(flag) || (quantity != 1 && type == MapleInventoryType.EQUIP)) { // hack
            chr.updateInv();
            return false;
        }
        Item dropped;
        if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            // the split stack needs its own object
            dropped = source.copy();
            dropped.setQuantity(quantity);
            source.setQuantity((short) (source.getQuantity() - quantity));
            chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().update(type, source).build()));
        } else {
            dropped = source;
            chr.getInventory(type).removeSlot(src);
            chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().remove((src < 0 ? MapleInventoryType.EQUIP : type), src).build()));
            if (src < 0) {
                chr.equipChanged();
            }
        }
        // the karma scissors flag is used up when the item is dropped
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isDropRestricted(dropped.getItemId()) || ii.isAccountShared(dropped.getItemId())) {
            if (ItemFlag.KARMA_EQ.check(flag)) {
                dropped.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
            } else if (ItemFlag.KARMA_USE.check(flag)) {
                dropped.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
            }
        }
        chr.getMap().spawnItemDrop(chr, chr, dropped, new Point(chr.getPosition()), true, true);
        return true;
    }
}
