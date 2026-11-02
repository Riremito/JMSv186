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
package tacos.packet.request;

import tacos.client.TacosCharacter;
import tacos.debug.DebugLogger;
import tacos.packet.ClientPacket;
import tacos.packet.ops.OpsShop;
import tacos.config.Config;
import tacos.config.Region;
import tacos.script.npc.NpcShopMan;

/**
 *
 * @author Riremito
 */
public class ReqCShopDlg {

    // CShopDlg::OnPacket
    public static boolean OnPacket(TacosCharacter chr, ClientPacket cp) {
        NpcShopMan npc_shop = chr.getNpcShop();
        if (npc_shop == null) {
            DebugLogger.ErrorLog("ReqCShopDlg : no shop.");
            return false;
        }

        byte shop_req = cp.Decode1();

        switch (OpsShop.find(shop_req)) {
            case ShopReq_Buy -> {
                short unk1 = cp.Decode2();
                byte unk2 = cp.Decode1(Config.GreaterOrEqual(Region.KMS, 114) || Config.GreaterOrEqual(Region.KMST, 391) || Config.GreaterOrEqual(Region.JMS, 194) || Config.GreaterOrEqual(Region.JMST, 110) || Config.Equal(Region.EMS, 76));
                int item_id = cp.Decode4();
                short quantity = cp.Decode2();

                npc_shop.buy(chr, item_id, quantity);
                return true;
            }
            case ShopReq_Sell -> {
                short item_slot = cp.Decode2();
                int item_id = cp.Decode4();
                short quantity = cp.Decode2();

                npc_shop.sell(chr, item_slot, item_id, quantity);
                return true;
            }
            case ShopReq_Recharge -> {
                short item_slot = cp.Decode2();

                npc_shop.recharge(chr, item_slot);
                return true;
            }
            case ShopReq_Close -> {
                npc_shop.end(chr);
                return true;
            }
            default -> {
            }
        }

        DebugLogger.ErrorLog("ReqCShopDlg : not coded, " + shop_req);
        return false;
    }
}
