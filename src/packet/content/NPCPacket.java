/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.content;

import client.MapleClient;
import config.ServerConfig;
import constants.GameConstants;
import handling.MaplePacket;
import java.util.List;
import packet.ServerPacket;
import server.MapleItemInformationProvider;
import server.MapleShopItem;
import server.life.MapleNPC;
import tools.BitTools;

/**
 *
 * @author elfenlied
 */
public class NPCPacket {

    public static MaplePacket spawnNPC(MapleNPC life, boolean show) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_NpcEnterField);

        p.Encode4(life.getObjectId());
        p.Encode4(life.getId());
        p.Encode2(life.getPosition().x);
        p.Encode2(life.getCy());
        p.Encode1(life.getF() == 1 ? 0 : 1);
        p.Encode2(life.getFh());
        p.Encode2(life.getRx0());
        p.Encode2(life.getRx1());
        p.Encode1(show ? 1 : 0);

        if (194 <= ServerConfig.version) {
            p.Encode1(0);
        }

        return p.Get();
    }

    public static MaplePacket removeNPC(final int objectid) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_NpcLeaveField);

        p.Encode4(objectid);
        return p.Get();
    }

    // CShopDlg::OnPacket
    // CShopDlg::SetShopDlg
    // getNPCShop
    public static MaplePacket getNPCShop(MapleClient c, int sid, List<MapleShopItem> items) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_OpenShopDlg);

        if (194 <= ServerConfig.version) {
            p.Encode1(0);
        }

        p.Encode4(sid);

        if (194 <= ServerConfig.version) {
            p.Encode1(0); // if 1, Encode1(size), Encode4, EncodeStr
        }

        p.Encode2(items.size()); // item count
        for (MapleShopItem item : items) {
            p.Encode4(item.getItemId());
            p.Encode4(item.getPrice());
            if (ServerConfig.version <= 164) {
                // nothing
            } else {
                p.Encode4(item.getReqItem());
                p.Encode4(item.getReqItemQ());
                p.Encode8(0);
            }
            if (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) {
                p.Encode2(1); // stacksize o.o
                p.Encode2(item.getBuyable());
            } else {
                p.EncodeZeroBytes(6);
                p.Encode2(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
                p.Encode2(ii.getSlotMax(c, item.getItemId()));
            }
        }
        return p.Get();
    }

    // CShopDlg::OnPacket
    // confirmShopTransaction
    public static MaplePacket confirmShopTransaction(byte code) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ShopResult);

        p.Encode1(code); // 8 = sell, 0 = buy, 0x20 = due to an error
        return p.Get();
    }
}
