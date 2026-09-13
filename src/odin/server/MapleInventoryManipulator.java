package odin.server;

import java.awt.Point;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import odin.client.inventory.MapleInventoryIdentifier;
import odin.constants.GameConstants;
import odin.client.inventory.Equip;
import odin.client.inventory.IItem;
import odin.client.inventory.InventoryException;
import odin.client.inventory.Item;
import odin.client.inventory.ItemFlag;
import odin.client.PlayerStats;
import odin.client.inventory.MaplePet;
import odin.client.MapleCharacter;
import odin.client.inventory.MapleInventoryType;
import tacos.config.Region;
import tacos.packet.ops.OpsCashItem;
import tacos.packet.response.ResCCashShop;
import tacos.packet.response.wrapper.ResWrapper;
import odin.server.maps.AramiaFireWorks;
import tacos.client.TacosClient;
import tacos.config.Config;

public class MapleInventoryManipulator {

    public static void addRing(MapleCharacter chr, int itemId, int ringId, int sn) {
        CashItemInfo csi = CashItemFactory.getInstance().getItem(sn);
        if (csi == null) {
            return;
        }
        IItem ring = chr.getCashInventory().toItem(csi, ringId);
        if (ring == null || ring.getUniqueId() != ringId || ring.getUniqueId() <= 0 || ring.getItemId() != itemId) {
            return;
        }
        chr.getCashInventory().addToInventory(ring);
        chr.SendPacket(ResCCashShop.CashItemResult(OpsCashItem.CashItemRes_Buy_Done, chr.getClient(), new ResCCashShop.CashItemStruct(ring)));
    }

    public static boolean addbyItem(final TacosClient client, final IItem item) {
        return addbyItem(client, item, false) >= 0;
    }

    public static short addbyItem(final TacosClient client, final IItem item, final boolean fromcs) {
        MapleCharacter chr = client.getPlayer();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());
        final short newSlot = client.getPlayer().getInventory(type).addItem(item);
        if (newSlot == -1) {
            if (!fromcs) {
                chr.updateInv();
                client.SendPacket(ResWrapper.getShowInventoryFull());
            }
            return newSlot;
        }
        if (!fromcs) {
            client.SendPacket(ResWrapper.addInventorySlot(type, item));
        }
        client.getPlayer().havePartyQuest(item.getItemId());
        return newSlot;
    }

    public static int getUniqueId(int itemId, MaplePet pet) {
        int uniqueid = -1;
        if (GameConstants.isPet(itemId)) {
            if (pet != null) {
                uniqueid = pet.getUniqueId();
            } else {
                uniqueid = MapleInventoryIdentifier.getInstance();
            }
        } else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || MapleItemInformationProvider.getInstance().isCash(itemId)) { //less work to do
            uniqueid = MapleInventoryIdentifier.getInstance(); //shouldnt be generated yet, so put it here
        }
        return uniqueid;
    }

    public static boolean addById(TacosClient client, int itemId, short quantity) {
        return addById(client, itemId, quantity, null, null, 0);
    }

    public static boolean addById(TacosClient client, int itemId, short quantity, String owner) {
        return addById(client, itemId, quantity, owner, null, 0);
    }

    public static byte addId(TacosClient client, int itemId, short quantity, String owner) {
        return addId(client, itemId, quantity, owner, null, 0);
    }

    public static boolean addById(TacosClient client, int itemId, short quantity, String owner, MaplePet pet) {
        return addById(client, itemId, quantity, owner, pet, 0);
    }

    public static boolean addById(TacosClient client, int itemId, short quantity, String owner, MaplePet pet, long period) {
        return addId(client, itemId, quantity, owner, pet, period) >= 0;
    }

    public static byte addId(TacosClient client, int itemId, short quantity, String owner, MaplePet pet, long period) {
        MapleCharacter chr = client.getPlayer();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && client.getPlayer().haveItem(itemId, 1, true, false)) {
            chr.updateInv();
            client.SendPacket(ResWrapper.showItemUnavailable());
            return -1;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);
        int uniqueid = getUniqueId(itemId, pet);
        short newSlot = -1;
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(client, itemId);
            final List<IItem> existing = client.getPlayer().getInventory(type).listById(itemId);
            if (!GameConstants.isRechargable(itemId)) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<IItem> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            Item eItem = (Item) i.next();
                            short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && (eItem.getOwner().equals(owner) || owner == null) && eItem.getExpiration() == -1) {
                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                client.SendPacket(ResWrapper.updateInventorySlot(type, eItem, false));
                            }
                        } else {
                            break;
                        }
                    }
                }
                Item nItem;
                // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        nItem = new Item(itemId, (byte) 0, newQ, (byte) 0, uniqueid);

                        newSlot = client.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1) {
                            chr.updateInv();
                            client.SendPacket(ResWrapper.getShowInventoryFull());
                            return -1;
                        }
                        if (owner != null) {
                            nItem.setOwner(owner);
                        }
                        if (period > 0) {
                            nItem.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                        }
                        if (pet != null) {
                            nItem.setPet(pet);
                            pet.setInventoryPosition(newSlot);
                            client.getPlayer().addPet(pet);
                        }
                        client.SendPacket(ResWrapper.addInventorySlot(type, nItem));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                            break;
                        }
                    } else {
                        client.getPlayer().havePartyQuest(itemId);
                        chr.updateInv();
                        return (byte) newSlot;
                    }
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0, uniqueid);
                newSlot = client.getPlayer().getInventory(type).addItem(nItem);

                if (newSlot == -1) {
                    chr.updateInv();
                    client.SendPacket(ResWrapper.getShowInventoryFull());
                    return -1;
                }
                if (period > 0) {
                    nItem.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }

                client.SendPacket(ResWrapper.addInventorySlot(type, nItem));
                chr.updateInv();
            }
        } else {
            if (quantity == 1) {
                final IItem nEquip = ii.getEquipById(itemId);
                if (owner != null) {
                    nEquip.setOwner(owner);
                }
                nEquip.setUniqueId(uniqueid);
                if (period > 0) {
                    nEquip.setExpiration(System.currentTimeMillis() + (period * 24 * 60 * 60 * 1000));
                }
                newSlot = client.getPlayer().getInventory(type).addItem(nEquip);
                if (newSlot == -1) {
                    chr.updateInv();
                    client.SendPacket(ResWrapper.getShowInventoryFull());
                    return -1;
                }
                client.SendPacket(ResWrapper.addInventorySlot(type, nEquip));
            } else {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
        }
        client.getPlayer().havePartyQuest(itemId);
        return (byte) newSlot;
    }

    public static IItem addbyId_Gachapon(final TacosClient client, final int itemId, short quantity) {
        MapleCharacter chr = client.getPlayer();
        if (client.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() == -1 || client.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() == -1 || client.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() == -1 || client.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() == -1) {
            return null;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemId) && client.getPlayer().haveItem(itemId, 1, true, false)) {
            chr.updateInv();
            client.SendPacket(ResWrapper.showItemUnavailable());
            return null;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemId);

        if (!type.equals(MapleInventoryType.EQUIP)) {
            short slotMax = ii.getSlotMax(client, itemId);
            final List<IItem> existing = client.getPlayer().getInventory(type).listById(itemId);

            if (!GameConstants.isRechargable(itemId)) {
                IItem nItem = null;
                boolean recieved = false;

                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<IItem> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            nItem = (Item) i.next();
                            short oldQ = nItem.getQuantity();

                            if (oldQ < slotMax) {
                                recieved = true;

                                short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                nItem.setQuantity(newQ);
                                client.SendPacket(ResWrapper.updateInventorySlot(type, nItem, false));
                            }
                        } else {
                            break;
                        }
                    }
                }
                // add new slots if there is still something left
                while (quantity > 0) {
                    short newQ = (short) Math.min(quantity, slotMax);
                    if (newQ != 0) {
                        quantity -= newQ;
                        nItem = new Item(itemId, (byte) 0, newQ, (byte) 0);
                        final short newSlot = client.getPlayer().getInventory(type).addItem(nItem);
                        if (newSlot == -1 && recieved) {
                            return nItem;
                        } else if (newSlot == -1) {
                            return null;
                        }
                        recieved = true;
                        client.SendPacket(ResWrapper.addInventorySlot(type, nItem));
                        if (GameConstants.isRechargable(itemId) && quantity == 0) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (recieved) {
                    client.getPlayer().havePartyQuest(nItem.getItemId());
                    return nItem;
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(itemId, (byte) 0, quantity, (byte) 0);
                final short newSlot = client.getPlayer().getInventory(type).addItem(nItem);

                if (newSlot == -1) {
                    return null;
                }
                client.SendPacket(ResWrapper.addInventorySlot(type, nItem));
                client.getPlayer().havePartyQuest(nItem.getItemId());
                return nItem;
            }
        } else {
            if (quantity == 1) {
                final IItem item = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                final short newSlot = client.getPlayer().getInventory(type).addItem(item);

                if (newSlot == -1) {
                    return null;
                }
                client.SendPacket(ResWrapper.addInventorySlot(type, item, true));
                client.getPlayer().havePartyQuest(item.getItemId());
                return item;
            } else {
                throw new InventoryException("Trying to create equip with non-one quantity");
            }
        }
        return null;
    }

    public static boolean addFromDrop(final TacosClient client, final IItem item, final boolean show) {
        return addFromDrop(client, item, show, false);
    }

    public static boolean addFromDrop(final TacosClient client, IItem item, final boolean show, final boolean enhance) {
        MapleCharacter chr = client.getPlayer();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        if (ii.isPickupRestricted(item.getItemId()) && client.getPlayer().haveItem(item.getItemId(), 1, true, false)) {
            chr.updateInv();
            client.SendPacket(ResWrapper.showItemUnavailable());
            return false;
        }
        final int before = client.getPlayer().itemQuantity(item.getItemId());
        short quantity = item.getQuantity();
        final MapleInventoryType type = GameConstants.getInventoryType(item.getItemId());

        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(client, item.getItemId());
            final List<IItem> existing = client.getPlayer().getInventory(type).listById(item.getItemId());
            if (!GameConstants.isRechargable(item.getItemId())) {
                if (quantity <= 0) { //wthchr.updateInv();
                    client.SendPacket(ResWrapper.showItemUnavailable());
                    return false;
                }
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    Iterator<IItem> i = existing.iterator();
                    while (quantity > 0) {
                        if (i.hasNext()) {
                            final Item eItem = (Item) i.next();
                            final short oldQ = eItem.getQuantity();
                            if (oldQ < slotMax && item.getOwner().equals(eItem.getOwner()) && item.getExpiration() == eItem.getExpiration()) {
                                final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                                quantity -= (newQ - oldQ);
                                eItem.setQuantity(newQ);
                                client.SendPacket(ResWrapper.updateInventorySlot(type, eItem, true));
                            }
                        } else {
                            break;
                        }
                    }
                }
                // add new slots if there is still something left
                while (quantity > 0) {
                    final short newQ = (short) Math.min(quantity, slotMax);
                    quantity -= newQ;
                    final Item nItem = new Item(item.getItemId(), (byte) 0, newQ, item.getFlag());
                    nItem.setExpiration(item.getExpiration());
                    nItem.setOwner(item.getOwner());
                    nItem.setPet(item.getPet());
                    short newSlot = client.getPlayer().getInventory(type).addItem(nItem);
                    if (newSlot == -1) {
                        chr.updateInv();
                        client.SendPacket(ResWrapper.getShowInventoryFull());
                        item.setQuantity((short) (quantity + newQ));
                        return false;
                    }
                    client.SendPacket(ResWrapper.addInventorySlot(type, nItem, true));
                }
            } else {
                // Throwing Stars and Bullets - Add all into one slot regardless of quantity.
                final Item nItem = new Item(item.getItemId(), (byte) 0, quantity, item.getFlag());
                nItem.setExpiration(item.getExpiration());
                nItem.setOwner(item.getOwner());
                nItem.setPet(item.getPet());
                final short newSlot = client.getPlayer().getInventory(type).addItem(nItem);
                if (newSlot == -1) {
                    chr.updateInv();
                    client.SendPacket(ResWrapper.getShowInventoryFull());
                    return false;
                }
                client.SendPacket(ResWrapper.addInventorySlot(type, nItem));
                chr.updateInv();
            }
        } else {
            if (quantity == 1) {
                if (enhance) {
                    item = checkEnhanced(item, client.getPlayer());
                }
                final short newSlot = client.getPlayer().getInventory(type).addItem(item);

                if (newSlot == -1) {
                    chr.updateInv();
                    client.SendPacket(ResWrapper.getShowInventoryFull());
                    return false;
                }
                client.SendPacket(ResWrapper.addInventorySlot(type, item, true));
            } else {
                throw new RuntimeException("Trying to create equip with non-one quantity");
            }
        }
        if (before == 0) {
            switch (item.getItemId()) {
                case AramiaFireWorks.KEG_ID:
                    client.getPlayer().dropMessage(5, "You have gained a Powder Keg, you can give this in to Aramia of Henesys.");
                    break;
                case AramiaFireWorks.SUN_ID:
                    client.getPlayer().dropMessage(5, "You have gained a Warm Sun, you can give this in to Maple Tree Hill through @joyce.");
                    break;
                case AramiaFireWorks.DEC_ID:
                    client.getPlayer().dropMessage(5, "You have gained a Tree Decoration, you can give this in to White Christmas Hill through @joyce.");
                    break;
            }
        }
        client.getPlayer().havePartyQuest(item.getItemId());
        if (show) {
            client.SendPacket(ResWrapper.DropPickUpMessage(item.getItemId(), item.getQuantity()));
        }
        return true;
    }

    private static final IItem checkEnhanced(final IItem before, final MapleCharacter chr) {
        if (Config.LessOrEqual(Region.KMS, 95) || Config.LessOrEqual(Region.JMS, 185) || Region.BMS.check()) {
            return before;
        }
        if (before instanceof Equip) {
            final Equip eq = (Equip) before;
            if (eq.getHidden() == 0 && (eq.getUpgradeSlots() >= 1 || eq.getLevel() >= 1) && Randomizer.nextInt(100) > 80) { //20% chance of pot?
                eq.resetPotential(false, false);
                // 未確認アイテム獲得 (?)
            }
        }
        return before;
    }

    private static int rand(int min, int max) {
        return Math.abs((int) Randomizer.rand(min, max));
    }

    public static boolean checkSpace(final TacosClient client, final int itemid, int quantity, final String owner) {
        MapleCharacter chr = client.getPlayer();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.isPickupRestricted(itemid) && client.getPlayer().haveItem(itemid, 1, true, false)) {
            chr.updateInv();
            return false;
        }
        if (quantity <= 0 && !GameConstants.isRechargable(itemid)) {
            return false;
        }
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        if (client == null || client.getPlayer() == null || client.getPlayer().getInventory(type) == null) { //wtf is causing this?
            return false;
        }
        if (!type.equals(MapleInventoryType.EQUIP)) {
            final short slotMax = ii.getSlotMax(client, itemid);
            final List<IItem> existing = client.getPlayer().getInventory(type).listById(itemid);
            if (!GameConstants.isRechargable(itemid)) {
                if (existing.size() > 0) { // first update all existing slots to slotMax
                    for (IItem eItem : existing) {
                        final short oldQ = eItem.getQuantity();
                        if (oldQ < slotMax && owner != null && owner.equals(eItem.getOwner())) {
                            final short newQ = (short) Math.min(oldQ + quantity, slotMax);
                            quantity -= (newQ - oldQ);
                        }
                        if (quantity <= 0) {
                            break;
                        }
                    }
                }
            }
            // add new slots if there is still something left
            final int numSlotsNeeded;
            if (slotMax > 0) {
                numSlotsNeeded = (int) (Math.ceil(((double) quantity) / slotMax));
            } else {
                numSlotsNeeded = 1;
            }
            return !client.getPlayer().getInventory(type).isFull(numSlotsNeeded - 1);
        } else {
            return !client.getPlayer().getInventory(type).isFull();
        }
    }

    public static void removeFromSlot(final TacosClient client, final MapleInventoryType type, final short slot, final short quantity, final boolean unlock) {
        removeFromSlot(client, type, slot, quantity, unlock, false);
    }

    public static void removeFromSlot(final TacosClient client, final MapleInventoryType type, final short slot, short quantity, final boolean unlock, final boolean consume) {
        if (client.getPlayer() == null || client.getPlayer().getInventory(type) == null) {
            return;
        }
        final IItem item = client.getPlayer().getInventory(type).getItem(slot);
        if (item != null) {
            final boolean allowZero = consume && GameConstants.isRechargable(item.getItemId());
            client.getPlayer().getInventory(type).removeItem(slot, quantity, allowZero);

            if (item.getQuantity() == 0 && !allowZero) {
                client.SendPacket(ResWrapper.clearInventoryItem(type, item.getPosition(), unlock));
            } else {
                client.SendPacket(ResWrapper.updateInventorySlot(type, (Item) item, unlock));
            }
        }
    }

    public static boolean removeById(TacosClient client, final MapleInventoryType type, final int itemId, final int quantity, final boolean fromDrop, final boolean consume) {
        int remremove = quantity;
        for (IItem item : client.getPlayer().getInventory(type).listById(itemId)) {
            if (remremove <= item.getQuantity()) {
                removeFromSlot(client, type, item.getPosition(), (short) remremove, fromDrop, consume);
                remremove = 0;
                break;
            } else {
                remremove -= item.getQuantity();
                removeFromSlot(client, type, item.getPosition(), item.getQuantity(), fromDrop, consume);
            }
        }
        return remremove <= 0;
    }

    public static void move(final TacosClient client, final MapleInventoryType type, final short src, final short dst) {
        if (src < 0 || dst < 0 || dst > client.getPlayer().getInventory(type).getSlotLimit() || src == dst) {
            return;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final IItem source = client.getPlayer().getInventory(type).getItem(src);
        final IItem initialTarget = client.getPlayer().getInventory(type).getItem(dst);
        if (source == null) {
            return;
        }
        short olddstQ = -1;
        if (initialTarget != null) {
            olddstQ = initialTarget.getQuantity();
        }
        final short oldsrcQ = source.getQuantity();
        final short slotMax = ii.getSlotMax(client, source.getItemId());
        client.getPlayer().getInventory(type).move(src, dst, slotMax);

        if (!type.equals(MapleInventoryType.EQUIP) && initialTarget != null
                && initialTarget.getItemId() == source.getItemId()
                && initialTarget.getOwner().equals(source.getOwner())
                && initialTarget.getExpiration() == source.getExpiration()
                && !GameConstants.isRechargable(source.getItemId())
                && !type.equals(MapleInventoryType.CASH)) {
            if ((olddstQ + oldsrcQ) > slotMax) {
                // アイテム個数がMAXを超過する場合は古い移動前のスロットも維持
                client.SendPacket(ResWrapper.moveAndMergeWithRestInventoryItem(type, initialTarget, source));
            } else {
                // アイテム個数がMAXを超過しない場合は古い移動前のスロットを削除
                client.SendPacket(ResWrapper.moveAndMergeInventoryItem(type, initialTarget, src));
            }
        } else {
            client.SendPacket(ResWrapper.moveInventoryItem(type, src, dst));
        }
    }

    public static void equip(final TacosClient client, final short src, short dst) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        MapleCharacter chr = client.getPlayer();
        if (chr == null) {
            return;
        }
        final PlayerStats statst = client.getPlayer().getStat();
        Equip source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src);
        Equip target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst);

        if (source == null || source.getDurability() == 0) {
            chr.updateInv();
            return;
        }

        final Map<String, Integer> stats = ii.getEquipStats(source.getItemId());
        if (dst < -999 && !GameConstants.isEvanDragonItem(source.getItemId()) && dst != -1400) {
            chr.updateInv();
            return;
        } else if (dst >= -999 && dst < -99 && stats.get("cash") == 0) {
            chr.updateInv();
            return;
        }
        if (!ii.canEquip(stats, source.getItemId(), chr.getLevel(), chr.getJob(), chr.getFame(), statst.getTotalStr(), statst.getTotalDex(), statst.getTotalLuk(), statst.getTotalInt(), client.getPlayer().getStat().levelBonus)) {
            chr.updateInv();
            return;
        }
        /*
        if (GameConstants.isWeapon(source.getItemId()) && dst != -10 && dst != -11) {
            //AutobanManager.getInstance().autoban(c, "Equipment hack, itemid " + source.getItemId() + " to slot " + dst);
            return;
        }
         */
        if (!ii.isCash(source.getItemId()) && !GameConstants.isMountItemAvailable(source.getItemId(), client.getPlayer().getJob())) {
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
                final IItem top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                if (top != null && GameConstants.isOverall(top.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        chr.updateInv();
                        client.SendPacket(ResWrapper.getShowInventoryFull());
                        return;
                    }
                    unequip(client, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -5: {
                final IItem top = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -5);
                final IItem bottom = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -6);
                if (top != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull(bottom != null && GameConstants.isOverall(source.getItemId()) ? 1 : 0)) {
                        chr.updateInv();
                        client.SendPacket(ResWrapper.getShowInventoryFull());
                        return;
                    }
                    unequip(client, (byte) -5, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                if (bottom != null && GameConstants.isOverall(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        chr.updateInv();
                        client.SendPacket(ResWrapper.getShowInventoryFull());
                        return;
                    }
                    unequip(client, (byte) -6, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -10: { // Shield
                IItem weapon = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
                if (GameConstants.isKatara(source.getItemId())) {
                    if ((chr.getJob() != 900 && (chr.getJob() < 430 || chr.getJob() > 434)) || weapon == null || !GameConstants.isDagger(weapon.getItemId())) {
                        chr.updateInv();
                        client.SendPacket(ResWrapper.getShowInventoryFull());
                        return;
                    }
                } else if (weapon != null && GameConstants.isTwoHanded(weapon.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        chr.updateInv();
                        client.SendPacket(ResWrapper.getShowInventoryFull());
                        return;
                    }
                    unequip(client, (byte) -11, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
            case -11: { // Weapon
                IItem shield = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
                if (shield != null && GameConstants.isTwoHanded(source.getItemId())) {
                    if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                        chr.updateInv();;
                        client.SendPacket(ResWrapper.getShowInventoryFull());
                        return;
                    }
                    unequip(client, (byte) -10, chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot());
                }
                break;
            }
        }
        source = (Equip) chr.getInventory(MapleInventoryType.EQUIP).getItem(src); // Equip
        target = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem(dst); // Currently equipping
        if (source == null) {
            chr.updateInv();
            return;
        }
        if (stats.get("equipTradeBlock") == 1) { // Block trade when equipped.
            byte flag = source.getFlag();
            if (!ItemFlag.UNTRADEABLE.check(flag)) {
                flag |= ItemFlag.UNTRADEABLE.getValue();
                source.setFlag(flag);
                client.SendPacket(ResWrapper.updateSpecialItemUse_(source, GameConstants.getInventoryType(source.getItemId()).getType()));
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
        if (source.getItemId() == 1122017) {
            chr.startFairySchedule(true, true);
        }
        client.SendPacket(ResWrapper.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (byte) 2));
        chr.equipChanged();
    }

    public static void unequip(final TacosClient client, final short src, final short dst) {
        MapleCharacter chr = client.getPlayer();
        Equip source = (Equip) client.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(src);
        Equip target = (Equip) client.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);

        if (dst < 0 || source == null) {
            return;
        }
        if (target != null && src <= 0) { // do not allow switching with equip
            chr.updateInv();
            return;
        }
        client.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeSlot(src);
        if (target != null) {
            client.getPlayer().getInventory(MapleInventoryType.EQUIP).removeSlot(dst);
        }
        source.setPosition(dst);
        client.getPlayer().getInventory(MapleInventoryType.EQUIP).addFromDB(source);
        if (target != null) {
            target.setPosition(src);
            client.getPlayer().getInventory(MapleInventoryType.EQUIPPED).addFromDB(target);
        }
        if (source.getItemId() == 1122017) {
            client.getPlayer().cancelFairySchedule(true);
        }
        client.SendPacket(ResWrapper.moveInventoryItem(MapleInventoryType.EQUIP, src, dst, (byte) 1));
        client.getPlayer().equipChanged();
    }

    public static boolean drop(final TacosClient client, MapleInventoryType type, final short src, final short quantity) {
        return drop(client, type, src, quantity, false);
    }

    public static boolean drop(final TacosClient client, MapleInventoryType type, final short src, short quantity, final boolean npcInduced) {
        MapleCharacter chr = client.getPlayer();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (src < 0) {
            type = MapleInventoryType.EQUIPPED;
        }
        if (client.getPlayer() == null) {
            return false;
        }
        final IItem source = client.getPlayer().getInventory(type).getItem(src);
        if (quantity < 0 || source == null || (!npcInduced && GameConstants.isPet(source.getItemId())) || (quantity == 0 && !GameConstants.isRechargable(source.getItemId()))) {
            chr.updateInv();
            return false;
        }

        final byte flag = source.getFlag();
        if (quantity > source.getQuantity()) {
            chr.updateInv();
            return false;
        }
        if (ItemFlag.LOCK.check(flag) || (quantity != 1 && type == MapleInventoryType.EQUIP)) { // hack
            chr.updateInv();
            return false;
        }
        final Point dropPos = new Point(client.getPlayer().getPosition());
        if (quantity < source.getQuantity() && !GameConstants.isRechargable(source.getItemId())) {
            final IItem target = source.copy();
            target.setQuantity(quantity);
            source.setQuantity((short) (source.getQuantity() - quantity));
            client.SendPacket(ResWrapper.dropInventoryItemUpdate(type, source));

            if (ii.isDropRestricted(target.getItemId()) || ii.isAccountShared(target.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    target.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), target, dropPos, true, true);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    target.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), target, dropPos, true, true);
                } else {
                    //c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), target, dropPos, true, true);
                }
            } else {
                if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                    //c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), target, dropPos);
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), target, dropPos, true, true);
                } else {
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), target, dropPos, true, true);
                }
            }
        } else {
            client.getPlayer().getInventory(type).removeSlot(src);
            client.SendPacket(ResWrapper.dropInventoryItem((src < 0 ? MapleInventoryType.EQUIP : type), src));
            if (src < 0) {
                client.getPlayer().equipChanged();
            }
            if (ii.isDropRestricted(source.getItemId()) || ii.isAccountShared(source.getItemId())) {
                if (ItemFlag.KARMA_EQ.check(flag)) {
                    source.setFlag((byte) (flag - ItemFlag.KARMA_EQ.getValue()));
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), source, dropPos, true, true);
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    source.setFlag((byte) (flag - ItemFlag.KARMA_USE.getValue()));
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), source, dropPos, true, true);
                } else {
                    //c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), source, dropPos, true, true);
                }
            } else {
                if (GameConstants.isPet(source.getItemId()) || ItemFlag.UNTRADEABLE.check(flag)) {
                    //c.getPlayer().getMap().disappearingItemDrop(c.getPlayer(), c.getPlayer(), source, dropPos);
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), source, dropPos, true, true);
                } else {
                    client.getPlayer().getMap().spawnItemDrop(client.getPlayer(), client.getPlayer(), source, dropPos, true, true);
                }
            }
        }
        return true;
    }
}
