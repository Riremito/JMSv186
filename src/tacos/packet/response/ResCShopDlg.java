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
package tacos.packet.response;

import odin.client.MapleClient;
import tacos.config.Region;
import tacos.config.Config;
import tacos.config.Version;
import odin.constants.GameConstants;
import tacos.shared.SharedDate;
import tacos.debug.DebugShop;
import java.util.List;
import tacos.packet.ServerPacket;
import tacos.packet.ops.OpsShop;
import odin.server.MapleItemInformationProvider;
import odin.server.MapleShopItem;
import tacos.packet.ServerPacketHeader;

/**
 *
 * @author Riremito
 */
public class ResCShopDlg {

    // CShopDlg::OnPacket
    public static ServerPacket ShopResult(OpsShop ops, int level) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_ShopResult);

        sp.Encode1(ops.get());

        switch (ops) {
            case ShopRes_SellSuccess: {
                /*
                if (Version.GreaterOrEqual(Region.JMS, 302)) {
                    sp.Encode1(0);
                    // CShopDlg::SetShopDlg
                    sp.Encode4(0);
                    sp.Encode4(9030000); // m_dwNpcTemplateID
                    sp.Encode1(0); // 1 = 十字旅団
                    //sp.Encode1(0);
                    sp.Encode2(0);
                }
                 */
                break;
            }
            case ShopRes_LimitLevel_Less:
            case ShopRes_LimitLevel_More: {
                sp.Encode4(level);
                break;
            }
            default: {
                break;
            }
        }

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode1(0);
        }
        if (Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode1(0);
        }

        return sp;
    }

    public static ServerPacket ShopResult(OpsShop ops) {
        return ResCShopDlg.ShopResult(ops, 0);
    }

    // CShopDlg::OnPacket
    public static ServerPacket OpenShopDlg_DS(DebugShop ds) {
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_OpenShopDlg);

        if (!Version.GreaterOrEqual(Region.EMS, 89)) {
            if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76)) {
                sp.Encode1(0);
            }
        }

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode4(0);
        }

        sp.Encode4(ds.getNpcId()); // m_dwNpcTemplateID

        if (!Version.GreaterOrEqual(Region.EMS, 89)) {
            if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76) || Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode1(0);
            }
        }

        sp.Encode2(ds.getShopStocks().size()); // nCount

        for (DebugShop.ShopStock ss : ds.getShopStocks()) {
            sp.Encode4(ss.item_id); // nItemID
            sp.Encode4(ss.item_price); // nPrice

            if (Version.GreaterOrEqual(Region.GMS, 91)) {
                sp.Encode1(0); // nDiscountRate
            }

            if (Config.JMS180orLater() || Version.GreaterOrEqual(Region.GMS, 83)) {
                sp.Encode4(0); // nTokenItemID
                sp.Encode4(0); // nTokenPrice
            }

            if (Config.JMS186orLater()) {
                sp.Encode4(0); // nItemPeriod
            }

            if (Config.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 84) || Version.GreaterOrEqual(Region.GMS, 83)) {
                sp.Encode4(0); // nLevelLimited
            }

            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }
            if (Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
                sp.Encode8(0);
                sp.Encode8(SharedDate.getMagicalExpirationDate());
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }
            // 207 || 233
            if (GameConstants.isRechargable(ss.item_id)) {
                // dUnitPrice (8)
                sp.EncodeDouble((ss.item_price != 0) ? 0.0 : (double) ss.item_recharge_price);
                // nQuantity is unitialized if you put recharge item in first shop slot,
                // you need to put other item in first slot to initialize quantity value.
            } else {
                sp.Encode2(ss.item_quantity); // nQuantity
            }
            if (Config.JMS146orLater()) {
                sp.Encode2(ss.item_slot_max); // nMaxPerSlot
            }

            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode1(0);
            }
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
            }
            if (Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                sp.EncodeZeroBytes(32);
            }
        }
        return sp;
    }

    public static ServerPacket OpenShopDlg(MapleClient c, int sid, List<MapleShopItem> items) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ServerPacket sp = new ServerPacket(ServerPacketHeader.LP_OpenShopDlg);

        if (!Version.GreaterOrEqual(Region.EMS, 89)) {
            if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76)) {
                sp.Encode1(0);
            }
        }

        if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
            sp.Encode4(0);
        }

        sp.Encode4(sid); // m_dwNpcTemplateID

        if (!Version.GreaterOrEqual(Region.EMS, 89)) {
            if (Version.GreaterOrEqual(Region.KMS, 114) || Version.GreaterOrEqual(Region.KMST, 391) || Version.GreaterOrEqual(Region.JMS, 194) || Version.GreaterOrEqual(Region.JMST, 110) || Version.GreaterOrEqual(Region.EMS, 76) || Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode1(0);
            }
        }

        sp.Encode2(items.size()); // item count
        for (MapleShopItem item : items) {
            sp.Encode4(item.getItemId());
            sp.Encode4(item.getPrice());

            if (Version.GreaterOrEqual(Region.GMS, 91)) {
                sp.Encode1(0); // nDiscountRate
            }

            if (Config.JMS180orLater() || Version.GreaterOrEqual(Region.GMS, 83)) {
                sp.Encode4(item.getReqItem()); // nTokenItemID
                sp.Encode4(item.getReqItemQ()); // nTokenPrice
            }

            if (Config.JMS186orLater()) {
                sp.Encode4(0); // nItemPeriod
            }

            if (Config.JMS180orLater() || Version.GreaterOrEqual(Region.KMS, 84) || Version.GreaterOrEqual(Region.GMS, 83)) {
                sp.Encode4(0); // nLevelLimited
            }

            if (Version.GreaterOrEqual(Region.JMS, 302)) {
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }

            if (Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
                sp.Encode8(0);
                sp.Encode8(SharedDate.getMagicalExpirationDate());
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }

            // 207 || 233
            if (GameConstants.isRechargable(item.getItemId())) {
                sp.EncodeDouble(ii.getPrice(item.getItemId()));
            } else {
                sp.Encode2(1); // nQuantity
            }
            if (Config.JMS146orLater()) {
                sp.Encode2(ii.getSlotMax(c, item.getItemId())); // nMaxPerSlot
            }
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.EMS, 89) || Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode1(0);
            }
            if (Version.GreaterOrEqual(Region.JMS, 302) || Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
            }
            if (Version.GreaterOrEqual(Region.GMS, 131)) {
                sp.Encode4(0);
                sp.Encode4(0);
                sp.Encode4(0);
                sp.EncodeZeroBytes(32);
            }
        }
        return sp;
    }

}
