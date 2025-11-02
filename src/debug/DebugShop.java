/*
 * Copyright (C) 2025 Riremito
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
package debug;

import client.MapleCharacter;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import data.wz.DW_Item;
import data.wz.ids.DWI_LoadXML;
import data.wz.ids.DWI_Validation;
import java.util.ArrayList;
import java.util.List;
import packet.ClientPacket;
import packet.ops.OpsShop;
import packet.response.ResCShopDlg;
import packet.response.wrapper.ResWrapper;
import provider.MapleData;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;

/**
 *
 * @author Riremito
 */
public class DebugShop {

    protected static final int DEFAULT_NPC_ID = 1012003;
    protected static final int ITEM_ID_FOR_UNINITIALIZED_QUANTITY = 2000000;

    public static boolean OnUserShopRequestHook(MapleCharacter chr, ClientPacket cp) {
        DebugShop ds = chr.getDebugShop();

        byte shop_req = cp.Decode1();

        switch (OpsShop.find(shop_req)) {
            case ShopReq_Buy: {
                short unk1 = cp.Decode2();
                byte unk2 = (ServerConfig.JMS194orLater() && !Version.GreaterOrEqual(Region.EMS, 89)) ? cp.Decode1() : 0;
                int item_id = cp.Decode4();
                short quantity = cp.Decode2();

                ds.buy(chr, item_id, quantity);
                return true;
            }
            case ShopReq_Sell: {
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                short quantity = cp.Decode2();

                ds.sell(chr, item_slot, item_id, quantity);
                return true;
            }
            case ShopReq_Recharge: {
                short item_slot = cp.Decode2();

                ds.recharge(chr, item_slot);
                return true;
            }
            case ShopReq_Close: {
                ds.end(chr);
                return false;
            }
            default: {
                break;
            }
        }

        DebugLogger.ErrorLog("OnUserShopRequestHook : not coded = " + shop_req);
        return false;
    }

    private int npc_id = DEFAULT_NPC_ID;
    private List<ShopStock> shopStocks = null;

    public DebugShop() {
        shopStocks = new ArrayList<>();
    }

    public int getNpcId() {
        return this.npc_id;
    }

    public List<ShopStock> getShopStocks() {
        return shopStocks;
    }

    public boolean start(MapleCharacter chr) {
        chr.DebugMsg("DebugShop : started.");
        if (0 < this.shopStocks.size()) {
            if (GameConstants.isRechargable(this.shopStocks.get(0).item_id)) {
                List<ShopStock> old_shopStocks = shopStocks;
                shopStocks = new ArrayList<>();
                addItem(ITEM_ID_FOR_UNINITIALIZED_QUANTITY);
                shopStocks.addAll(old_shopStocks);
            }
        }
        chr.setDebugShop(this);
        chr.SendPacket(ResCShopDlg.OpenShopDlg_DS(this));
        return true;
    }

    public boolean end(MapleCharacter chr) {
        chr.DebugMsg("DebugShop : finished.");
        chr.setDebugShop(null);
        return true;
    }

    public boolean addItem(int item_id) {
        if (!DWI_Validation.isValidItemID(item_id)) {
            DebugLogger.ErrorLog("DebugShop : addItem, invalid item id = " + item_id);
            return false;
        }

        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        int item_slot_max = miip.getSlotMax(item_id);
        int item_price = (int) miip.getPrice(item_id) * item_slot_max;
        int item_quantity = GameConstants.isRechargable(item_id) ? item_slot_max : 1;

        if (item_price <= 1) {
            item_price = 298000; // test
        } else {
            item_price *= 10;
        }

        ShopStock st = new ShopStock();
        st.item_id = item_id;
        st.item_price = item_price;
        st.item_quantity = item_quantity;
        st.item_slot_max = item_slot_max;
        shopStocks.add(st);
        return true;
    }

    public boolean addItem(int item_id, int item_price, int item_quantity, int item_slot_max) {
        if (!DWI_Validation.isValidItemID(item_id)) {
            DebugLogger.ErrorLog("DebugShop : addItem, invalid item id = " + item_id);
            return false;
        }

        ShopStock st = new ShopStock();
        st.item_id = item_id;
        st.item_price = item_price;
        st.item_quantity = item_quantity;
        st.item_slot_max = item_slot_max;
        shopStocks.add(st);
        return true;
    }

    public boolean addItemRecharge(int item_id, int item_recharge_price) {
        if (!DWI_Validation.isValidItemID(item_id)) {
            DebugLogger.ErrorLog("DebugShop : addItemRecharge, invalid item id = " + item_id);
            return false;
        }

        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        int item_slot_max = miip.getSlotMax(item_id);
        ShopStock st = new ShopStock();
        st.item_id = item_id;
        st.item_price = 0; // hide
        st.item_recharge_price = item_recharge_price;
        st.item_slot_max = item_slot_max;
        shopStocks.add(st);
        return true;
    }

    public boolean setRechargeAll() {
        return setRechargeAll(50);
    }

    public boolean setRechargeAll(int item_recharge_price) {
        int item_sub_types[] = {207, 233};
        for (int item_sub_type : item_sub_types) {
            MapleData md_item_sub_type = DW_Item.getItemImg(item_sub_type);
            if (md_item_sub_type != null) {
                for (MapleData md_item : md_item_sub_type.getChildren()) {
                    int item_id = Integer.parseInt(md_item.getName());
                    this.addItemRecharge(item_id, item_recharge_price);
                }
            }
        }
        return true;
    }

    public boolean setItemTest(int item_sub_type) {
        int item_count = 0;
        MapleData md_item_sub_type = DW_Item.getItemImg(item_sub_type);
        if (md_item_sub_type != null) {
            for (MapleData md_item : md_item_sub_type.getChildren()) {
                int item_id = Integer.parseInt(md_item.getName());
                this.addItem(item_id);
                item_count++;
            }
        }

        DebugLogger.InfoLog("DebugShop : setItemTest(" + item_sub_type + "), loaded " + item_count + " items.");
        return true;
    }

    public boolean setRandomItems(int count) {
        for (int i = 0; i < count; i++) {
            this.addItem(DWI_LoadXML.getItem().getRandom());
        }
        return true;
    }

    private ShopStock getStock(int item_id) {
        for (ShopStock ss : shopStocks) {
            if (ss.item_price <= 0) {
                continue;
            }
            if (ss.item_id == item_id) {
                return ss;
            }
        }
        return null;
    }

    private ShopStock getRecharge(int item_id) {
        for (ShopStock ss : shopStocks) {
            if (ss.item_price != 0) {
                continue;
            }
            if (ss.item_id == item_id) {
                return ss;
            }
        }
        return null;
    }

    public boolean buy(MapleCharacter chr, int item_id, int quantity) {
        ShopStock ss = getStock(item_id);
        if (ss == null) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoStock));
            return false;
        }

        int item_price = ss.item_price * quantity;
        if (chr.getMeso() < item_price) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoMoney));
            return false;
        }

        if (!MapleInventoryManipulator.checkSpace(chr.getClient(), item_id, quantity, "")) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyUnknown));
            return false;
        }

        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        MapleInventoryManipulator.addById(chr.getClient(), item_id, (short) quantity); // bool...?
        chr.gainMeso(-item_price, false);
        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuySuccess));
        chr.DebugMsg("DebugShop : buy " + item_id);
        return true;
    }

    public boolean sell(MapleCharacter chr, short item_slot, int item_id, int quantity) {
        boolean is_recharge_item = GameConstants.isRechargable(item_id);
        IItem item = chr.getInventory(GameConstants.getInventoryType(item_id)).getItem(item_slot);
        if (item == null) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
            return false;
        }
        if (item.getItemId() != item_id) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
            return false;
        }
        if (item.getQuantity() < quantity || quantity < 0) {
            if (!is_recharge_item) {
                chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
                return false;
            }
        }
        if (is_recharge_item) {
            quantity = (int) item.getQuantity();
        }
        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        int item_price = is_recharge_item ? (int) (miip.getWholePrice(item.getItemId()) / (double) miip.getSlotMax(chr.getClient(), item.getItemId())) : (int) miip.getPrice(item.getItemId());
        item_price *= quantity;
        if (item_price < 0) {
            item_price = 0;
            DebugLogger.ErrorLog("item price set to 0 : " + item_id + " (" + quantity + ")");
        }
        if (item_price + chr.getMeso() < 0) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
            return false;
        }

        MapleInventoryManipulator.removeFromSlot(chr.getClient(), GameConstants.getInventoryType(item_id), item_slot, (short) quantity, false);
        chr.gainMeso(item_price, false);
        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
        return true;
    }

    public boolean recharge(MapleCharacter chr, short item_slot) {
        IItem item = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);
        if (item == null) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_RechargeUnknown));
            return false;
        }
        int item_id = item.getItemId();
        if (!GameConstants.isRechargable(item_id)) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_RechargeUnknown));
            return false;
        }
        ShopStock ss = getRecharge(item_id);
        if (ss == null) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_RechargeUnknown));
            return false;
        }
        if (ss.item_slot_max <= item.getQuantity()) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_RechargeUnknown));
            return false;
        }
        int recharge_count = ss.item_slot_max - item.getQuantity();
        int rechager_price = ss.item_recharge_price * recharge_count;
        if (chr.getMeso() < rechager_price) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_RechargeNoMoney));
            return false;
        }

        chr.gainMeso(-rechager_price, false);
        item.setQuantity((short) ss.item_slot_max);
        chr.SendPacket(ResWrapper.updateInventorySlot(MapleInventoryType.USE, item, false));
        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_RechargeSuccess));
        return true;
    }

    public class ShopStock {

        public int item_id = 0;
        public int item_price = 2100000000;
        public int item_quantity = 1;
        public int item_recharge_price = 0;
        public int item_slot_max = 1;
    }

}
