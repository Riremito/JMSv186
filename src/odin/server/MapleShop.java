package odin.server;

import odin.client.MapleCharacter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import odin.client.MapleClient;
import odin.client.inventory.MapleInventoryType;
import odin.client.inventory.MaplePet;
import tacos.data.wz.ids.DWI_Validation;
import tacos.database.DatabaseConnection;
import tacos.packet.ops.OpsShop;
import tacos.packet.response.ResCShopDlg;
import tacos.packet.response.wrapper.ResWrapper;

public class MapleShop {

    private static final Set<Integer> rechargeableItems = new LinkedHashSet<Integer>();
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
        items = new LinkedList<MapleShopItem>();
    }

    public void addItem(MapleShopItem item) {
        if (DWI_Validation.isValidItemID(item.getItemId())) {
            items.add(item);
        }
    }

    public void sendShop(MapleClient c) {
        c.getPlayer().setShop(this);
        c.SendPacket(ResCShopDlg.OpenShopDlg(c, getNpcId(), items));
    }

    public boolean buy(MapleClient c, MapleCharacter chr, int itemId, short quantity) {
        MapleShopItem item = findById(itemId);

        if (quantity <= 0 || item == null) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoStock));
            return false;
        }

        final int price = GameConstants.isRechargable(itemId) ? item.getPrice() : (item.getPrice() * quantity);

        if (item.getPrice() < 0 || c.getPlayer().getMeso() < price) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoMoney));
            return false;
        }

        if (!MapleInventoryManipulator.checkSpace(c, itemId, quantity, "")) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyUnknown));
            return false;
        }

        if (0 < item.getReqItem()) {
            if (2 <= quantity) {
                chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyUnknown));
                return false;
            }

            MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(item.getReqItem()), item.getReqItem(), item.getReqItemQ(), false, false);
        }

        chr.gainMeso(-price, false);

        if (GameConstants.isPet(itemId)) {
            MapleInventoryManipulator.addById(c, itemId, quantity, "", MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), -1);
        } else {
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            if (GameConstants.isRechargable(itemId)) {
                quantity = ii.getSlotMax(c, item.getItemId());
            }

            MapleInventoryManipulator.addById(c, itemId, quantity);
        }

        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuySuccess));
        return true;
    }

    public void sell(MapleClient c, MapleInventoryType type, byte slot, short quantity) {
        if (quantity == 0xFFFF || quantity == 0) {
            quantity = 1;
        }
        IItem item = c.getPlayer().getInventory(type).getItem(slot);
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
            MapleInventoryManipulator.removeFromSlot(c, type, slot, quantity, false);
            double price;
            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                price = ii.getWholePrice(item.getItemId()) / (double) ii.getSlotMax(c, item.getItemId());
            } else {
                price = ii.getPrice(item.getItemId());
            }
            final int recvMesos = (int) Math.max(Math.ceil(price * quantity), 0);
            if (price != -1.0 && recvMesos > 0) {
                c.getPlayer().gainMeso(recvMesos, false);
            }
            c.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
        }
    }

    public boolean recharge(final MapleClient c, final byte slot) {
        final IItem item = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);

        if (item == null || (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId()))) {
            c.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellNoStock));
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        short slotMax = ii.getSlotMax(c, item.getItemId());
        final int skill = GameConstants.getMasterySkill(c.getPlayer().getJob());

        if (skill != 0) {
            slotMax += c.getPlayer().getSkillLevel(SkillFactory.getSkill(skill)) * 10;
        }
        if (item.getQuantity() < slotMax) {
            final int price = (int) Math.round(ii.getPrice(item.getItemId()) * (slotMax - item.getQuantity()));
            if (c.getPlayer().getMeso() >= price) {
                item.setQuantity(slotMax);
                c.getSession().write(ResWrapper.updateInventorySlot(MapleInventoryType.USE, (Item) item, false));
                c.getPlayer().gainMeso(-price, false, true, false);
                c.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
                return true;
            } else {
                c.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
                return false;
            }
        }
        c.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_ServerMsg));
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
        MapleShop ret = null;
        int shopId;

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(isShopId ? "SELECT * FROM shops WHERE shopid = ?" : "SELECT * FROM shops WHERE npcid = ?");

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                shopId = rs.getInt("shopid");
                ret = new MapleShop(shopId, rs.getInt("npcid"));
                rs.close();
                ps.close();
            } else {
                rs.close();
                ps.close();
                return null;
            }
            ps = con.prepareStatement("SELECT * FROM shopitems WHERE shopid = ? ORDER BY position ASC");
            ps.setInt(1, shopId);
            rs = ps.executeQuery();
            List<Integer> recharges = new ArrayList<Integer>(rechargeableItems);
            while (rs.next()) {
                if (GameConstants.isThrowingStar(rs.getInt("itemid")) || GameConstants.isBullet(rs.getInt("itemid"))) {
                    MapleShopItem starItem = new MapleShopItem((short) 1, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("reqitem"), rs.getInt("reqitemq"));
                    ret.addItem(starItem);
                    if (rechargeableItems.contains(starItem.getItemId())) {
                        recharges.remove(Integer.valueOf(starItem.getItemId()));
                    }
                } else {
                    ret.addItem(new MapleShopItem((short) 1000, rs.getInt("itemid"), rs.getInt("price"), rs.getInt("reqitem"), rs.getInt("reqitemq")));
                }
            }
            for (Integer recharge : recharges) {
                ret.addItem(new MapleShopItem((short) 1000, recharge.intValue(), 0, 0, 0));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Could not load shop" + e);
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
