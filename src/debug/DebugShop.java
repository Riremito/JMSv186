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
import config.ServerConfig;
import data.wz.ids.DWI_Validation;
import java.util.ArrayList;
import java.util.List;
import packet.ClientPacket;
import packet.ops.OpsShop;
import packet.response.ResCShopDlg;

/**
 *
 * @author Riremito
 */
public class DebugShop {

    protected static final int DEFAULT_NPC_ID = 1012003;

    public static boolean OnUserShopRequestHook(MapleCharacter chr, ClientPacket cp) {
        DebugShop ds = chr.getDebugShop();

        byte shop_req = cp.Decode1();

        switch (OpsShop.find(shop_req)) {
            case ShopReq_Buy: {
                short unk1 = cp.Decode2();
                byte unk2 = ServerConfig.JMS194orLater() ? cp.Decode1() : 0;
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

                ds.recharge(chr);
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

        Debug.ErrorLog("OnUserShopRequestHook : not coded = " + shop_req);
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
        chr.setDebugShop(this);
        chr.SendPacket(ResCShopDlg.OpenShopDlg_DS(this));
        return true;
    }

    public boolean end(MapleCharacter chr) {
        chr.DebugMsg("DebugShop : finished.");
        chr.setDebugShop(null);
        return true;
    }

    public boolean add(int item_id, int item_price) {
        if (!DWI_Validation.isValidItemID(item_id)) {
            Debug.ErrorLog("DebugShop : add, invalid item id = " + item_id);
            return false;
        }

        ShopStock st = new ShopStock();
        st.item_id = item_id;
        st.item_price = item_price;
        shopStocks.add(st);
        return true;
    }

    private ShopStock getStock(int item_id) {
        for (ShopStock ss : shopStocks) {
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

        if (chr.getMeso() < ss.item_price) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuyNoMoney));
            return false;
        }

        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_BuySuccess));
        return true;
    }

    public boolean sell(MapleCharacter chr, short item_slot, int item_id, int quantity) {
        if (false) {
            chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellUnkonwn));
            return false;
        }

        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_SellSuccess));
        return true;
    }

    public boolean recharge(MapleCharacter chr) {
        chr.SendPacket(ResCShopDlg.ShopResult(OpsShop.ShopRes_RechargeSuccess));
        return true;
    }

    public class ShopStock {

        public int item_id;
        public int item_price;
    }

}
