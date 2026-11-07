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
package tacos.script.npc;

import odin.client.inventory.Item;
import tacos.client.TacosCharacter;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import java.util.ArrayList;
import tacos.packet.ops.OpsShop;
import tacos.packet.response.ResCShopDlg;
import tacos.packet.response.ResCWvsContext;
import tacos.packet.response.builder.PB_InvOp;
import odin.server.MapleInventoryManipulator;
import odin.server.MapleItemInformationProvider;
import tacos.debug.DebugLogger;
import tacos.server.TacosWorld;
import tacos.server.map.object.TacosPet;
import tacos.wz.WzXML;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class NpcShopMan {

    private static final int ITEM_ID_FOR_UNINITIALIZED_QUANTITY = 2000000;
    private final int npc_id;
    private ArrayList<ShopStock> shopStocks;

    public NpcShopMan(int npc_id) {
        this.npc_id = npc_id;
        this.shopStocks = new ArrayList<>();
    }

    public int getNpcId() {
        return this.npc_id;
    }

    public ArrayList<ShopStock> getShopStocks() {
        return this.shopStocks;
    }

    public boolean start(TacosCharacter chr) {
        chr.DebugMsg("NpcShopMan : started.");
        if (!this.shopStocks.isEmpty()) {
            if (GameConstants.isRechargable(this.shopStocks.get(0).item_id)) {
                ArrayList<ShopStock> old_shopStocks = this.shopStocks;
                this.shopStocks = new ArrayList<>();
                addItem(ITEM_ID_FOR_UNINITIALIZED_QUANTITY);
                this.shopStocks.addAll(old_shopStocks);
            }
        }
        chr.setNpcShop(this);
        chr.SendPacket(ResCShopDlg.OpenShopDlg(this));
        return true;
    }

    public boolean end(TacosCharacter chr) {
        chr.DebugMsg("NpcShopMan : finished.");
        chr.setNpcShop(null);
        return true;
    }

    public boolean addItem(int item_id) {
        if (!WzDataStorage.ITEM.check(item_id)) {
            DebugLogger.ErrorLog("NpcShopMan : addItem, invalid item id = " + item_id);
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
        this.shopStocks.add(st);
        return true;
    }

    public boolean addItem(int item_id, int item_price) {
        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        int item_slot_max = miip.getSlotMax(item_id);
        return addItem(item_id, item_price, 1, item_slot_max);
    }

    public boolean addItem(int item_id, int item_price, int item_quantity, int item_slot_max) {
        if (!WzDataStorage.ITEM.check(item_id)) {
            DebugLogger.ErrorLog("NpcShopMan : addItem, invalid item id = " + item_id);
            return false;
        }

        ShopStock st = new ShopStock();
        st.item_id = item_id;
        st.item_price = item_price;
        st.item_quantity = item_quantity;
        st.item_slot_max = item_slot_max;
        this.shopStocks.add(st);
        return true;
    }

    public boolean addItemRecharge(int item_id, int item_recharge_price) {
        if (!WzDataStorage.ITEM.check(item_id)) {
            DebugLogger.ErrorLog("NpcShopMan : addItemRecharge, invalid item id = " + item_id);
            return false;
        }

        MapleItemInformationProvider miip = MapleItemInformationProvider.getInstance();
        int item_slot_max = miip.getSlotMax(item_id);
        ShopStock st = new ShopStock();
        st.item_id = item_id;
        st.item_price = 0; // hide
        st.item_recharge_price = item_recharge_price;
        st.item_slot_max = item_slot_max;
        this.shopStocks.add(st);
        return true;
    }

    public boolean setRechargeAll() {
        return setRechargeAll(50);
    }

    public boolean setRechargeAll(int item_recharge_price) {
        int item_sub_types[] = {207, 233};
        for (int item_sub_type : item_sub_types) {
            for (int item_id : WzXML.ITEM.getItemIds(item_sub_type)) {
                this.addItemRecharge(item_id, item_recharge_price);
            }
        }
        return true;
    }

    private ShopStock getStock(int item_id) {
        for (ShopStock ss : this.shopStocks) {
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
        for (ShopStock ss : this.shopStocks) {
            if (ss.item_price != 0) {
                continue;
            }
            if (ss.item_id == item_id) {
                return ss;
            }
        }
        return null;
    }

    public boolean buy(TacosCharacter chr, int item_id, int quantity) {
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

        if (!MapleInventoryManipulator.checkSpace(chr, item_id, quantity, "")) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyUnknown));
            return false;
        }
        if (GameConstants.isPet(item_id)) {
            MapleInventoryManipulator.addById(chr, item_id, (short) quantity, "", TacosPet.createPet(item_id, TacosWorld.getNextItemUniqueId()), -1);
        } else {
            MapleInventoryManipulator.addById(chr, item_id, (short) quantity); // bool...?
        }
        chr.gainMeso(-item_price, false);
        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuySuccess));
        chr.DebugMsg("NpcShopMan : buy " + item_id);
        return true;
    }

    public boolean sell(TacosCharacter chr, short item_slot, int item_id, int quantity) {
        boolean is_recharge_item = GameConstants.isRechargable(item_id);
        Item item = chr.getInventory(GameConstants.getInventoryType(item_id)).getItem(item_slot);
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
        int item_price = miip.getWholePrice(item.getItemId());
        if (is_recharge_item) {
            double unit_price = miip.getPrice(item.getItemId());
            item_price += (int) (unit_price * miip.getSlotMax(item.getItemId()));
        } else {
            item_price *= quantity;
        }
        if (item_price < 0) {
            item_price = 0;
            DebugLogger.ErrorLog("item price set to 0 : " + item_id + " (" + quantity + ")");
        }
        if (item_price + chr.getMeso() < 0) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
            return false;
        }

        MapleInventoryManipulator.removeFromSlot(chr, GameConstants.getInventoryType(item_id), item_slot, (short) quantity, false);
        chr.gainMeso(item_price, false);
        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
        return true;
    }

    public boolean recharge(TacosCharacter chr, short item_slot) {
        Item item = chr.getInventory(MapleInventoryType.USE).getItem(item_slot);
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
        chr.SendPacket(ResCWvsContext.InventoryOperation(false, PB_InvOp.builder().update(MapleInventoryType.USE, item).build()));
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
