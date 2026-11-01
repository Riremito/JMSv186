package odin.server;

import tacos.server.map.object.TacosPet;
import tacos.wz.WzXML;
import tacos.client.TacosCharacter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import odin.client.inventory.Item;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryType;
import tacos.database.query.DQ_Shopitems;
import tacos.database.query.DQ_Shops;
import tacos.packet.ops.OpsShop;
import tacos.packet.response.ResCShopDlg;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_InvOp;
import tacos.server.TacosWorld;
import tacos.wz.WzDataStorage;

public class MapleShop {

    private static final Set<Integer> rechargeableItems = new LinkedHashSet<>();
    private int id;
    private int npcId;
    private List<MapleShopItem> items;

    static {
        rechargeableItems.add(2070000);
        rechargeableItems.add(2070001);
        rechargeableItems.add(2070002);
        rechargeableItems.add(2070003);
        rechargeableItems.add(2070004);
        rechargeableItems.add(2070005);
        rechargeableItems.add(2070006);
        rechargeableItems.add(2070007);
        rechargeableItems.add(2070008);
        rechargeableItems.add(2070009);
        rechargeableItems.add(2070010);
        rechargeableItems.add(2070011);
        rechargeableItems.add(2070012);
        rechargeableItems.add(2070013);
        rechargeableItems.add(2070016);
        rechargeableItems.add(2070019); // Magic Throwing Star
        rechargeableItems.add(2330000);
        rechargeableItems.add(2330001);
        rechargeableItems.add(2330002);
        rechargeableItems.add(2330003);
        rechargeableItems.add(2330004);
        rechargeableItems.add(2330005);
        rechargeableItems.add(2330007);
        rechargeableItems.add(2331000); // Capsules
        rechargeableItems.add(2332000); // Capsules
    }

    public MapleShop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
        items = new LinkedList<>();
    }

    // used by script.
    public boolean add(int item_id, int price) {
        return addItem(new MapleShopItem(MapleItemInformationProvider.getInstance().getSlotMax(item_id), item_id, price, 0, 0));
    }

    public boolean addItem(MapleShopItem item) {
        if (!WzDataStorage.ITEM.check(item.getItemId())) {
            return false;
        }

        items.add(item);
        return true;
    }

    public void sendShop(TacosCharacter chr) {
        chr.setShop(this);
        chr.SendPacket(ResCShopDlg.OpenShopDlg(chr.getClient(), getNpcId(), items));
    }

    public boolean buy(TacosCharacter chr, int itemId, short quantity) {
        MapleShopItem item = findById(itemId);

        if (quantity <= 0 || item == null) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoStock));
            return false;
        }

        int price = GameConstants.isRechargable(itemId) ? item.getPrice() : (item.getPrice() * quantity);

        if (item.getPrice() < 0 || chr.getMeso() < price) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoMoney));
            return false;
        }

        if (!MapleInventoryManipulator.checkSpace(chr, itemId, quantity, "")) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyUnknown));
            return false;
        }

        // the required item and the bought item go in one packet
        PB_InvOp.Builder io = PB_InvOp.builder();
        if (0 < item.getReqItem()) {
            if (2 <= quantity) {
                chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyUnknown));
                return false;
            }

            MapleInventoryManipulator.removeById(chr, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ(), false, io);
        }

        chr.gainMeso(-price, false);

        if (GameConstants.isPet(itemId)) {
            MapleInventoryManipulator.addById(chr, itemId, quantity, "", TacosPet.createPet(itemId, TacosWorld.getNextItemUniqueId()), -1, io);
        } else {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            if (GameConstants.isRechargable(itemId)) {
                quantity = ii.getSlotMax(item.getItemId());
            }

            MapleInventoryManipulator.addById(chr, itemId, quantity, null, null, 0, io);
        }
        chr.sendInventoryOperation(false, io);

        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuySuccess));
        return true;
    }

    public void sell(TacosCharacter chr, MapleInventoryType type, byte slot, short quantity) {
        if (quantity == 0xFFFF || quantity == 0) {
            quantity = 1;
        }
        Item item = chr.getInventory(type).getItem(slot);
        if (item == null) {
            return;
        }

        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            quantity = item.getQuantity();
        }
        if (quantity < 0) {
            return;
        }
        short iQuant = item.getQuantity();
        if (iQuant == 0xFFFF) {
            iQuant = 1;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.cantSell(item.getItemId())) {
            return;
        }
        if (quantity <= iQuant && iQuant > 0) {
            MapleInventoryManipulator.removeFromSlot(chr, type, slot, quantity, false);
            double price;
            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                price = ii.getWholePrice(item.getItemId()) / (double) ii.getSlotMax(item.getItemId());
            } else {
                price = ii.getPrice(item.getItemId());
            }
            int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0);
            if (price != -1.0 && recvMesos > 0) {
                chr.gainMeso(recvMesos, false);
            }
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
        }
    }

    public boolean recharge(TacosCharacter chr, byte slot) {
        Item item = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (item == null || (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId()))) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellNoStock));
            return false;
        }
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(item.getItemId());
        int skill = GameConstants.getMasterySkill(chr.getJob());

        if (skill != 0) {
            slotMax += chr.getSkillLevel(WzXML.SKILL.getSkill().get(skill)) * 10;
        }
        if (item.getQuantity() < slotMax) {
            int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (chr.getMeso() >= price) {
                item.setQuantity(slotMax);
                chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().update(MapleInventoryType.USE, item).build()));
                chr.gainMeso(-price, false, true, false);
                chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
                return true;
            }
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
            return false;
        }
        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_ServerMsg));
        return false;
    }

    private MapleShopItem findById(int itemId) {
        for (MapleShopItem item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public static MapleShop createFromDB(int npc_id) {
        MapleShop ret = DQ_Shops.load(npc_id);
        if (ret == null) {
            return null;
        }

        List<Integer> recharges = new ArrayList<>(rechargeableItems);
        for (DQ_Shopitems.Row row : DQ_Shopitems.loadByShopId(ret.getId())) {
            if (GameConstants.isThrowingStar(row.itemId) || GameConstants.isBullet(row.itemId)) {
                MapleShopItem starItem = new MapleShopItem((short) 1, row.itemId, row.price, row.reqItem, row.reqItemQ);
                ret.addItem(starItem);
                if (rechargeableItems.contains(starItem.getItemId())) {
                    recharges.remove(Integer.valueOf(starItem.getItemId()));
                }
            } else {
                ret.addItem(new MapleShopItem((short) 1000, row.itemId, row.price, row.reqItem, row.reqItemQ));
            }
        }
        for (Integer recharge : recharges) {
            ret.addItem(new MapleShopItem((short) 1000, recharge, 0, 0, 0));
        }
        return ret;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getId() {
        return id;
    }

    public static class MapleShopItem {

        private short buyable;
        private int itemId;
        private int price;
        private int reqItem;
        private int reqItemQ;

        public MapleShopItem(short buyable, int itemId, int price, int reqItem, int reqItemQ) {
            this.buyable = buyable;
            this.itemId = itemId;
            this.price = price;
            this.reqItem = reqItem;
            this.reqItemQ = reqItemQ;
        }

        public int getItemId() {
            return itemId;
        }

        public int getPrice() {
            return price;
        }

        public int getReqItem() {
            return reqItem;
        }

        public int getReqItemQ() {
            return reqItemQ;
        }
    }
}
