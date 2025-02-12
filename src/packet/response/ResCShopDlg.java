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
import config.ServerConfig;
import constants.GameConstants;
import handling.MaplePacket;
import java.util.List;
import packet.ServerPacket;
import packet.request.ReqCNpcPool;
import server.MapleItemInformationProvider;
import server.MapleShopItem;
import tools.BitTools;

/**
 *
 * @author Riremito
 */
public class ResCShopDlg {

    // CShopDlg::OnPacket
    // confirmShopTransaction
    public static MaplePacket confirmShopTransaction(ReqCNpcPool.SP_ShopFlag flag, int level) {
        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_ShopResult);
        // JMS v188, 00706BA9
        // JMS v194も謎DCを発生させる処理が動くとクライアントがクラッシュするのでクライアントの修正が必要
        sp.Encode1(flag.get()); // 8 = sell, 0 = buy, 0x20 = due to an error
        switch (flag) {
            case ERROR_LEVEL_UNDER:
            case ERROR_LEVEL_HIGH: {
                sp.Encode4(level);
                break;
            }
            default: {
                if (ServerConfig.JMS302orLater()) {
                    sp.Encode1(0);
                }
                break;
            }
        }
        return sp.get();
    }

    public static MaplePacket confirmShopTransaction(ReqCNpcPool.SP_ShopFlag flag) {
        return confirmShopTransaction(flag, 0);
    }

    // CShopDlg::OnPacket
    // CShopDlg::SetShopDlg
    // getNPCShop
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
