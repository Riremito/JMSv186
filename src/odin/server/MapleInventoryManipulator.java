package odin.server;

import java.awt.Point;
import tacos.client.TacosCharacter;
import tacos.server.map.object.TacosPet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    public static short addbyItem(TacosCharacter chr, Item item, boolean fromcs) {
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        short newSlot = chr.getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                sendInventoryFull(chr);
            }
            return newSlot;
        }
        if (!fromcs) {
            chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(type, item).build()));
        }
        chr.havePartyQuest(item.getItemId());
        return newSlot;
    }

    public static int getUniqueId(int itemId, TacosPet pet) {
        int uniqueid = -1;
        if (GameConstants.isPet(itemId)) {
            if (pet != null) {
                uniqueid = pet.getUniqueId();
            } else {
                uniqueid = TacosWorld.getNextItemUniqueId();
            }
        } else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) { //less work to do
            uniqueid = TacosWorld.getNextItemUniqueId(); //shouldnt be generated yet, so put it here
        }
        return uniqueid;
    }

    public static boolean addById(TacosCharacter chr, int itemId, short quantity) {
        return addById(chr, itemId, quantity, null, null, 0);
    }

    public static boolean addById(TacosCharacter chr, int itemId, short quantity, String owner) {
        return addById(chr, itemId, quantity, owner, null, 0);
    }

    public static byte addId(TacosCharacter chr, int itemId, short quantity, String owner) {
        return addId(chr, itemId, quantity, owner, null, 0);
    }

    public static boolean addById(TacosCharacter chr, int itemId, short quantity, String owner, TacosPet pet) {
        return addById(chr, itemId, quantity, owner, pet, 0);
    }

    public static boolean addById(TacosCharacter chr, int itemId, short quantity, String owner, TacosPet pet, long period) {
        return addId(chr, itemId, quantity, owner, pet, period) >= 0;
    }

    public static byte addId(TacosCharacter chr, int itemId, short quantity, String owner, TacosPet pet, long period) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && chr.haveItem(itemId, 1, true, false)) {
            sendPickupUnavailable(chr);
            return -1;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int uniqueid = getUniqueId(itemId, pet);
        if (type.equals(MapleInventoryType.EQUIP)) {
            if (quantity != 1) {
                return -1;
            }
            Item nEquip = ii.getDefaultEquipById(itemId);
            if (owner != null) {
                nEquip.setOwner(owner);
            }
            nEquip.setUniqueId(uniqueid);
            if (period > 0) {
                nEquip.setExpiration(getExpiration(period));
            }
            short newSlot = chr.getInventory(type).addItem(nEquip);
            if (newSlot == -1) {
                sendInventoryFull(chr);
                return -1;
            }
            chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(type, nEquip).build()));
            chr.havePartyQuest(itemId);
            return (byte) newSlot;
        }

        short slotMax = ii.getSlotMax(itemId);
        if (GameConstants.isRechargable(itemId)) {
            // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
            Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0, uniqueid);
            short newSlot = chr.getInventory(type).addItem(nItem);
            if (newSlot == -1) {
                sendInventoryFull(chr);
                return -1;
            }
            if (period > 0) {
                nItem.setExpiration(getExpiration(period));
            }
            chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(type, nItem).build()));
            chr.updateInv();
            chr.havePartyQuest(itemId);
            return (byte) newSlot;
        }

        // first update all existing slots to slotMax
        quantity = fillExistingSlots(chr, type, itemId, quantity, slotMax, eItem -> (owner == null || eItem.getOwner().equals(owner)) && eItem.getExpiration() == -1, false);
        // add new slots if there is still something left
        short newSlot = -1;
        while (quantity > 0) {
            short newQ = (short) Math.min(quantity, slotMax);
            if (newQ == 0) {
                chr.havePartyQuest(itemId);
                chr.updateInv();
                return (byte) newSlot;
            }
            quantity -= newQ;
            Item nItem = new Item(itemId, (byte) 0, newQ, (byte) 0, uniqueid);
            newSlot = chr.getInventory(type).addItem(nItem);
            if (newSlot == -1) {
                sendInventoryFull(chr);
                return -1;
            }
            if (owner != null) {
                nItem.setOwner(owner);
            }
            if (period > 0) {
                nItem.setExpiration(getExpiration(period));
            }
            if (pet != null) {
                nItem.setPet(pet);
                pet.setInventoryPosition(newSlot);
            }
            chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(type, nItem).build()));
        }
        chr.havePartyQuest(itemId);
        return (byte) newSlot;
    }

    // period (days) to the expiration time
    private static long getExpiration(long period) {
        return System.currentTimeMillis() + period * 24 * 60 * 60 * 1000;
    }

    // fills the existing stacks of the item first, returns the quantity left
    private static short fillExistingSlots(TacosCharacter chr, MapleInventoryType type, int itemId, short quantity, short slotMax, Predicate<Item> canStack, boolean unlock) {
        for (Item eItem : chr.getInventory(type).listById(itemId)) {
            if (quantity <= 0) {
                break;
            }
            short oldQ = eItem.getQuantity();
            if (oldQ < slotMax && canStack.test(eItem)) {
                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                quantity -= (newQ - oldQ);
                eItem.setQuantity(newQ);
                chr.SendPacket(ResCWvsContext.InventoryOperation(unlock, PB_InvOp.builder().update(type, eItem).build()));
            }
        }
        return quantity;
    }

    private static void sendInventoryFull(TacosCharacter chr) {
        chr.updateInv();
        chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_INVENTORY_FULL).build()));
    }

    private static void sendPickupUnavailable(TacosCharacter chr) {
        chr.updateInv();
        chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_UNAVAILABLE).build()));
    }

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
        if (type.equals(MapleInventoryType.EQUIP)) {
            if (quantity != 1) {
                return null;
            }
            Item item = ii.getEquipById(itemId);
            if (chr.getInventory(type).addItem(item) == -1) {
                return null;
            }
            chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().add(type, item).build()));
            chr.havePartyQuest(item.getItemId());
            return item;
        }

        short slotMax = ii.getSlotMax(itemId);
        List<Item> existing = chr.getInventory(type).listById(itemId);
        if (GameConstants.isRechargable(itemId)) {
            // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
            Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0);
            if (chr.getInventory(type).addItem(nItem) == -1) {
                return null;
            }
            chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(type, nItem).build()));
            chr.havePartyQuest(nItem.getItemId());
            return nItem;
        }

        Item nItem = null;
        boolean recieved = false;
        // first update all existing slots to slotMax
        Iterator<Item> it = existing.iterator();
        while (quantity > 0 && it.hasNext()) {
            nItem = it.next();
            short oldQ = nItem.getQuantity();
            if (oldQ < slotMax) {
                recieved = true;
                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                quantity -= (newQ - oldQ);
                nItem.setQuantity(newQ);
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().update(type, nItem).build()));
            }
        }
        // add new slots if there is still something left
        while (quantity > 0) {
            short newQ = (short) Math.min(quantity, slotMax);
            if (newQ == 0) {
                break;
            }
            quantity -= newQ;
            nItem = new Item(itemId, (byte) 0, newQ, (byte) 0);
            if (chr.getInventory(type).addItem(nItem) == -1) {
                return recieved ? nItem : null;
            }
            recieved = true;
            chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(type, nItem).build()));
        }
        if (!recieved) {
            return null;
        }
        chr.havePartyQuest(nItem.getItemId());
        return nItem;
    }

    public static boolean addFromDrop(TacosCharacter chr, Item item, boolean show) {
        return addFromDrop(chr, item, show, false);
    }

    public static boolean addFromDrop(TacosCharacter chr, Item item, boolean show, boolean enhance) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        if (ii.isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1, true, false)) {
            sendPickupUnavailable(chr);
            return false;
        }
        int before = chr.itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());

        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) {
                    return false;
                }
                // first update all existing slots to slotMax
                final Item dropped = item;
                quantity = fillExistingSlots(chr, type, item.getItemId(), quantity, slotMax, eItem -> dropped.getOwner().equals(eItem.getOwner()) && dropped.getExpiration() == eItem.getExpiration(), true);
                // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    quantity -= newQ;
                    Item nItem = new Item(item.getItemId(), (byte) 0, newQ, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    short newSlot = chr.getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        sendInventoryFull(chr);
                        item.setQuantity((short) (quantity + newQ));
                        return false;
                    }
                    chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().add(type, nItem).build()));
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                Item nItem = new Item(item.getItemId(), (byte) 0, quantity, item.getFlag());
                nItem.setExpiration(item.getExpiration());
                nItem.setOwner(item.getOwner());
                nItem.setPet(item.getPet());
                short newSlot = chr.getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    sendInventoryFull(chr);
                    return false;
                }
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().add(type, nItem).build()));
                chr.updateInv();
            }
        } else {
            if (quantity == 1) {
                // new equip.
                if (enhance) {
                    Equip equip = (Equip) item;
                    equip.getEnhancement().usePotentialScroll();
                }
                short newSlot = chr.getInventory(type).addItem(item);

                if (newSlot == -1) {
                    sendInventoryFull(chr);
                    return false;
                }
                chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().add(type, item).build()));
            } else {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
        }
        if (before == 0) {
            switch (item.getItemId()) {
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
        chr.havePartyQuest(item.getItemId());
        if (show) {
            chr.SendPacket(ResCWvsContext.Message(OpsMessage.MS_DropPickUpMessage, PB_Message.builder().dt(OpsDropPickUpMessage.PICKUP_ITEM).ItemID(item.getItemId()).Inc_ItemCount(item.getQuantity()).build()));
        }
        return true;
    }

    public static boolean checkSpace(TacosCharacter chr, int itemid, int quantity, String owner) {
        if (chr == null) {
            return false;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemid) && chr.haveItem(itemid, 1, true, false)) {
            chr.updateInv();
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
            return false;
        }
        MapleInventoryType type = GameConstants.getInventoryType(itemid);
        if (chr.getInventory(type) == null) {
            return false;
        }
        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(itemid);
            List<Item> existing = chr.getInventory(type).listById(itemid);
            if (!GameConstants.isRechargable(itemid) && existing.size() > 0) { // first update all existing slots to slotMax
                for (Item eItem : existing) {
                    short oldQ = eItem.getQuantity();
                    if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                        short newQ = (short) Math.min(oldQ + quantity, slotMax);
                        quantity -= (newQ - oldQ);
                    }
                    if (quantity <= 0) {
                        break;
                    }
                }
            }
            // add new slots if there is still something left
            int numSlotsNeeded;
            if (slotMax > 0) {
                numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
            } else {
                numSlotsNeeded = 1;
            }
            return !chr.getInventory(type).isFull(numSlotsNeeded - 1);
        }
        return !chr.getInventory(type).isFull();
    }

    public static void removeFromSlot(TacosCharacter chr, MapleInventoryType type, short slot, short quantity, boolean unlock) {
        removeFromSlot(chr, type, slot, quantity, unlock, false);
    }

    public static void removeFromSlot(TacosCharacter chr, MapleInventoryType type, short slot, short quantity, boolean unlock, boolean consume) {
        if (chr == null || chr.getInventory(type) == null) {
            return;
        }
        Item item = chr.getInventory(type).getItem(slot);
        if (item != null) {
            boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            chr.getInventory(type).removeItem(slot, quantity, allowZero);

            if (item.getQuantity() == 0 && !allowZero) {
                chr.SendPacket(ResCWvsContext.InventoryOperation(unlock, PB_InvOp.builder().remove(type, item.getPosition()).build()));
            } else {
                chr.SendPacket(ResCWvsContext.InventoryOperation(unlock, PB_InvOp.builder().update(type, item).build()));
            }
        }
    }

    public static boolean removeById(TacosCharacter chr, MapleInventoryType type, int itemId, int quantity, boolean fromDrop, boolean consume) {
        int remaining = quantity;
        for (Item item : chr.getInventory(type).listById(itemId)) {
            if (remaining <= item.getQuantity()) {
                removeFromSlot(chr, type, item.getPosition(), (short) remaining, fromDrop, consume);
                remaining = 0;
                break;
            }
            remaining -= item.getQuantity();
            removeFromSlot(chr, type, item.getPosition(), item.getQuantity(), fromDrop, consume);
        }
        return remaining <= 0;
    }

    public static void move(TacosCharacter chr, MapleInventoryType type, short src, short dst) {
        if (src < 0 || dst < 0 || dst > chr.getInventory(type).getSlotLimit() || src == dst) {
            return;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Item source = chr.getInventory(type).getItem(src);
        Item initialTarget = chr.getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        short oldsrcQ = source.getQuantity();
        short slotMax = ii.getSlotMax(source.getItemId());
        chr.getInventory(type).move(src, dst, slotMax);

        if (!type.equals(MapleInventoryType.EQUIP) && initialTarget != null
                && initialTarget.getItemId() == source.getItemId()
                && initialTarget.getOwner().equals(source.getOwner())
                && initialTarget.getExpiration() == source.getExpiration()
                && !GameConstants.isRechargable(source.getItemId())
                && !type.equals(MapleInventoryType.CASH)) {
            if ((olddstQ + oldsrcQ) > slotMax) {
                // アイテム個数がMAXを超過する場合は古い移動前のスロットも維持
                chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().update(type, source).update(type, initialTarget).build()));
            } else {
                // アイテム個数がMAXを超過しない場合は古い移動前のスロットを削除
                chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().move(type, src, initialTarget.getPosition()).remove(type, src).update(type, initialTarget).build()));
            }
        } else {
            chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().move(type, src, dst).build()));
        }
    }

    public static void equip(TacosCharacter chr, short src, short dst) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (chr == null) {
            return;
        }
        PlayerStats statst = chr.getStat();
        Equip source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src);

        if (source == null || source.getDurability() == 0) {
            chr.updateInv();
            return;
        }

        Map<String, Integer> stats = ii.getEquipStats(source.getItemId());
        if (dst < -999 && !GameConstants.isEvanDragonItem(source.getItemId()) && dst != -1400) {
            chr.updateInv();
            return;
        } else if (dst >= -999 && dst < -99 && stats.get("cash") == 0) {
            chr.updateInv();
            return;
        }
        if (!ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), chr.getStat().levelBonus)) {
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

        switch (dst) {
            case -6: { // Top
                Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                if (top != null && GameConstants.isOverall(top.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        sendInventoryFull(chr);
                        return;
                    }
                    unequip(chr, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -5: {
                Item top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                Item bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -6);
                if (top != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull(bottom != null && GameConstants.isOverall(source.getItemId()) ? 1 : 0)) {
                        sendInventoryFull(chr);
                        return;
                    }
                    unequip(chr, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                if (bottom != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        sendInventoryFull(chr);
                        return;
                    }
                    unequip(chr, (byte) -6, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
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
                    unequip(chr, (byte) -11, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
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
                    unequip(chr, (byte) -10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
        }
        source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src); // Equip
        Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst); // Currently equipping
        if (source == null) {
            chr.updateInv();
            return;
        }
        if (stats.get("equipTradeBlock") == 1) { // Block trade when equipped.
            byte flag = source.getFlag();
            if (!ItemFlag.UNTRADEABLE.check(flag)) {
                flag |= ItemFlag.UNTRADEABLE.getValue();
                source.setFlag(flag);
                chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().add(GameConstants.getInventoryType(source.getItemId()), source).build()));
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
        chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().move(MapleInventoryType.EQUIP, src, dst).build()));
        chr.equipChanged();
    }

    public static void unequip(TacosCharacter chr, short src, short dst) {
        Equip source = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(dst);

        if (dst < 0 || source == null) {
            return;
        }
        if (target != null && src <= 0) { // do not allow switching with equip
            chr.updateInv();
            return;
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
        chr.SendPacket(ResCWvsContext.InventoryOperation(true, PB_InvOp.builder().move(MapleInventoryType.EQUIP, src, dst).build()));
        chr.equipChanged();
    }

    public static boolean drop(TacosCharacter chr, MapleInventoryType type, short src, short quantity) {
        return drop(chr, type, src, quantity, false);
    }

    public static boolean drop(TacosCharacter chr, MapleInventoryType type, short src, short quantity, boolean npcInduced) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if (chr == null) {
            return false;
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
