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
package packet.response;

import client.MapleClient;
import config.Region;
import config.ServerConfig;
import config.Version;
import constants.GameConstants;
import handling.MaplePacket;
import java.util.List;
import packet.ServerPacket;
import packet.ops.OpsShop;
import server.MapleItemInformationProvider;
import server.MapleShopItem;
import tools.BitTools;

/**
 *
 * @author Riremito
 */
public class ResCShopDlg {

    // CShopDlg::OnPacket
    public static MaplePacket confirmShopTransaction(OpsShop ops, int level) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ShopResult);

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

        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            sp.Encode1(0);
        }

        return sp.get();
    }

    public static MaplePacket confirmShopTransaction(OpsShop ops) {
        return confirmShopTransaction(ops, 0);
    }

    // CShopDlg::OnPacket
    // CShopDlg::SetShopDlg
    public static MaplePacket getNPCShop(MapleClient c, int sid, List<MapleShopItem> items) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_OpenShopDlg);
        if (ServerConfig.JMS194orLater()) {
            sp.Encode1(0);
        }
        if (ServerConfig.JMS302orLater()) {
            sp.Encode4(0);
        }
        sp.Encode4(sid);
        if (ServerConfig.JMS194orLater()) {
            // 0 = normal shop, 1 is probably coin shop
            sp.Encode1(0); // if 1, Encode1(size), Encode4, EncodeStr
        }
        sp.Encode2(items.size()); // item count
        for (MapleShopItem item : items) {
            sp.Encode4(item.getItemId());
            sp.Encode4(item.getPrice());
            if (ServerConfig.JMS180orLater()) {
                sp.Encode4(item.getReqItem()); // nTokenItemID
                sp.Encode4(item.getReqItemQ()); // nTokenPrice
            }
            if (ServerConfig.JMS186orLater()) {
                sp.Encode4(0); // nItemPeriod
            }
            if (ServerConfig.JMS180orLater() || ServerConfig.KMS84orLater()) {
                sp.Encode4(0); // nLevelLimited
            }
            if (ServerConfig.JMS302orLater()) {
                sp.Encode4(0);
                sp.Encode1(0);
                sp.Encode4(0);
            }
            if (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) {
                sp.Encode2(1); // stacksize o.o
                if (ServerConfig.JMS146orLater()) {
                    sp.Encode2(item.getBuyable());
                }
            } else {
                sp.EncodeZeroBytes(6);
                sp.Encode2(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
                sp.Encode2(ii.getSlotMax(c, item.getItemId()));
            }
            if (ServerConfig.JMS302orLater()) {
                sp.Encode1(0);
                sp.Encode4(0);
            }
        }
        return sp.get();
    }

}
