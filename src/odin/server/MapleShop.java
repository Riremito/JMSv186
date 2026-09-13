package odin.server;

import odin.client.MapleCharacter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import odin.client.inventory.IItem;
import odin.client.inventory.Item;
import odin.client.SkillFactory;
import odin.constants.GameConstants;
import odin.client.inventory.MapleInventoryIdentifier;
import tacos.client.TacosClient;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import tacos.database.query.DQ_Shopitems;
import tacos.database.query.DQ_Shops;
import tacos.packet.ops.OpsShop;
import tacos.packet.response.ResCShopDlg;
import tacos.packet.response.wrapper.ResWrapper;
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
//	rechargeableItems.add(2070014); // Doesn't Exist [Devil Rain]
//	rechargeableItems.add(2070015); // Beginner Star
        rechargeableItems.add(2070016);
//	rechargeableItems.add(2070017); // Doesn't Exist
//        rechargeableItems.add(2070018); // Balanced Fury
        rechargeableItems.add(2070019); // Magic Throwing Star

        rechargeableItems.add(2330000);
        rechargeableItems.add(2330001);
        rechargeableItems.add(2330002);
        rechargeableItems.add(2330003);
        rechargeableItems.add(2330004);
        rechargeableItems.add(2330005);
//	rechargeableItems.add(2330006); // Beginner Bullet
        rechargeableItems.add(2330007);

        rechargeableItems.add(2331000); // Capsules
        rechargeableItems.add(2332000); // Capsules
    }

    /**
     * Creates a new instance of MapleShop
     */
    public MapleShop(int id, int npcId) {
        this.id = id;
        this.npcId = npcId;
        items = new LinkedList<>();
    }

    public void addItem(MapleShopItem item) {
        if (WzDataStorage.ITEM.check(item.getItemId())) {
            items.add(item);
        }
    }

    public void sendShop(TacosClient client) {
        client.getPlayer().setShop(this);
        client.SendPacket(ResCShopDlg.OpenShopDlg(client, getNpcId(), items));
    }

    public boolean buy(TacosClient client, MapleCharacter chr, int itemId, short quantity) {
        MapleShopItem item = findById(itemId);

        if (quantity <= 0 || item == null) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoStock));
            return false;
        }

        final int price = GameConstants.isRechargable(itemId) ? item.getPrice() : (item.getPrice() * quantity);

        if (item.getPrice() < 0 || client.getPlayer().getMeso() < price) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoMoney));
            return false;
        }

        if (!MapleInventoryManipulator.checkSpace(client, itemId, quantity, "")) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyUnknown));
            return false;
        }

        if (0 < item.getReqItem()) {
            if (2 <= quantity) {
                chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyUnknown));
                return false;
            }

            MapleInventoryManipulator.removeById(client, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ(), false, false);
        }

        chr.gainMeso(-price, false);

        if (GameConstants.isPet(itemId)) {
            MapleInventoryManipulator.addById(client, itemId, quantity, "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1);
        } else {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            if (GameConstants.isRechargable(itemId)) {
                quantity = ii.getSlotMax(client, item.getItemId());
            }

            MapleInventoryManipulator.addById(client, itemId, quantity);
        }

        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuySuccess));
        return true;
    }

    public void sell(TacosClient client, MapleInventoryType type, byte slot, short quantity) {
        if (quantity == 0xFFFF || quantity == 0) {
            quantity = 1;
        }
        IItem item = client.getPlayer().getInventory(type).getItem(slot);
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
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.cantSell(item.getItemId())) {
            return;
        }
        if (quantity <= iQuant && iQuant > 0) {
            MapleInventoryManipulator.removeFromSlot(client, type, slot, quantity, false);
            double price;
            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                price = ii.getWholePrice(item.getItemId()) / (double) ii.getSlotMax(client, item.getItemId());
            } else {
                price = ii.getPrice(item.getItemId());
            }
            final int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0);
            if (price != -1.0 && recvMesos > 0) {
                client.getPlayer().gainMeso(recvMesos, false);
            }
            client.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
        }
    }

    public boolean recharge(final TacosClient client, final byte slot) {
        final IItem item = client.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (item == null || (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId()))) {
            client.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellNoStock));
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(client, item.getItemId());
        final int skill = GameConstants.getMasterySkill(client.getPlayer().getJob());

        if (skill != 0) {
            slotMax += client.getPlayer().getSkillLevel(SkillFactory.getSkill(skill)) * 10;
        }
        if (item.getQuantity() < slotMax) {
            final int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (client.getPlayer().getMeso() >= price) {
                item.setQuantity(slotMax);
                client.SendPacket(ResWrapper.updateInventorySlot(MapleInventoryType.USE, (Item) item, false));
                client.getPlayer().gainMeso(-price, false, true, false);
                client.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
                return true;
            } else {
                client.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
                return false;
            }
        }
        client.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_ServerMsg));
        return false;
    }

    protected MapleShopItem findById(int itemId) {
        for (MapleShopItem item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public static MapleShop createFromDB(int id, boolean isShopId) {
        MapleShop ret = DQ_Shops.load(id, isShopId);
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
            ret.addItem(new MapleShopItem((short) 1000, recharge.intValue(), 0, 0, 0));
        }
        return ret;
    }

    public int getNpcId() {
        return npcId;
    }

    public int getId() {
        return id;
    }
}
