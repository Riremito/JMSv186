/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.request;

import client.MapleCharacter;
import client.MapleClient;
import config.ServerConfig;
import constants.GameConstants;
import debug.Debug;
import handling.MaplePacket;
import java.util.List;
import packet.ClientPacket;
import packet.ServerPacket;
import server.MapleItemInformationProvider;
import server.MapleShop;
import server.MapleShopItem;
import server.life.MapleNPC;
import tools.BitTools;

/**
 *
 * @author elfenlied
 */
public class NPCPacket {

    public enum CP_ShopFlag {
        // v186
        BUY_ITEM(0),
        SELL_ITEM(1),
        CHARGE_ITEM(2),
        CLOSE_SHOP(3),
        UNKNOWN;

        private int value;

        CP_ShopFlag(int flag) {
            value = flag;
        }

        CP_ShopFlag() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }

        public static CP_ShopFlag get(int v) {
            for (final CP_ShopFlag f : CP_ShopFlag.values()) {
                if (f.get() == v) {
                    return f;
                }
            }
            return CP_ShopFlag.UNKNOWN;
        }
    }

    public enum SP_ShopFlag {
        SUCCESS_BUY(0), // 購入成功
        SUCCESS_SELL(8), // 売却成功
        ERROR_SOLD_OUT,
        ERROR_MESO,
        ERROR_INVENTORY_FULL,
        ERROR_COIN,
        ERROR_LEVEL_UNDER,
        ERROR_LEVEL_HIGH,
        ERROR_UNIQUE_ITEM,
        ERROR_TRADE_BAN,
        BUY_TAMA,
        ERROR_GENDER,
        ERROR_HORNTAIL_NECKLACE, // 多分
        ERROR, // 存在しないIDを指定でもOK
        UNKNOWN;

        private int value;

        SP_ShopFlag(int flag) {
            value = flag;
        }

        SP_ShopFlag() {
            value = -1;
        }

        public boolean set(int flag) {
            value = flag;
            return true;
        }

        public int get() {
            return value;
        }

        public static SP_ShopFlag get(int v) {
            for (final SP_ShopFlag f : SP_ShopFlag.values()) {
                if (f.get() == v) {
                    return f;
                }
            }
            return SP_ShopFlag.UNKNOWN;
        }
    }

    public static void Init() {
        if (ServerConfig.IsJMS() && ServerConfig.GetVersion() <= 194) {
            // v194
            // 0, 5 = OK
            // 0 OK?
            // 1 OK?
            // 2,6,10 物品不足が不足しています
            // 3,11 メルが足りません。
            // 4 アイテム空間に空きがあるか確認してみてください。
            // 5,9,22 null
            // 14 アイテムが足りません
            // 15 decode4, レベル以下のみ購入可能です。
            // 16 decode4, レベル以上のみ購入可能です。
            // 17 これ以上所持することができないアイテムです。
            // 18 アイテムやメルを移動することができません。お問い合わせください。 (TRADE BAN)
            // 19 パチンコ玉を最後に購入してから、31日以上経過しています。
            // 20 着用できない性別のため、アイテム交換はできません。
            // 23 このアイテムは１個以上所持できません。
            // 24 エラーが発生して取引できませんでした。

            SP_ShopFlag.SUCCESS_BUY.set(0); // 最後に追加されたアイテム欄に移動
            SP_ShopFlag.ERROR_SOLD_OUT.set(2); // 売り切れ, 公式サーバーではアイテムの在庫数が決まっていて売り切れる
            SP_ShopFlag.ERROR_MESO.set(3); // メル不足
            SP_ShopFlag.ERROR_INVENTORY_FULL.set(4);
            SP_ShopFlag.SUCCESS_SELL.set(5); // 何もしない
            SP_ShopFlag.ERROR_COIN.set(14); // メダル交換
            SP_ShopFlag.ERROR_LEVEL_UNDER.set(15); // レベル制限
            SP_ShopFlag.ERROR_LEVEL_HIGH.set(16); // レベル制限
            SP_ShopFlag.ERROR_UNIQUE_ITEM.set(17); // 固有アイテム
            SP_ShopFlag.ERROR_TRADE_BAN.set(18);
            SP_ShopFlag.BUY_TAMA.set(19); // 不明
            SP_ShopFlag.ERROR_GENDER.set(20); // 性別不一致
            SP_ShopFlag.ERROR_HORNTAIL_NECKLACE.set(23); // 固有アイテムの一部, 多分ホーンテイルのネックレス?
            SP_ShopFlag.ERROR.set(24); // 24 or invalid value
        }
    }

    // client
    // CShopDlg::OnPacket
    // NPCShop
    public static boolean OnShopPacket(ClientPacket p, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleShop shop = chr.getShop();

        if (shop == null) {
            chr.setConversation(0);
            return false;
        }

        byte flag = p.Decode1();

        switch (CP_ShopFlag.get(flag)) {
            case BUY_ITEM: {
                p.Decode2();

                if (ServerConfig.IsPostBB() && ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
                    p.Decode1();
                }

                final int itemId = p.Decode4();
                final short quantity = p.Decode2();
                shop.buy(c, chr, itemId, quantity);
                break;
            }
            case SELL_ITEM: {
                final byte slot = (byte) p.Decode2();
                final int itemId = p.Decode4();
                final short quantity = p.Decode2();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                break;
            }
            case CHARGE_ITEM: {
                final byte slot = (byte) p.Decode2();
                shop.recharge(c, slot);
                break;
            }
            case CLOSE_SHOP: {
                chr.setConversation(0);
                return true;
            }
            default: {
                // not coded
                chr.setConversation(0);
                Debug.ErrorLog("NPCPacket");
                Debug.PacketLog(p);
                break;
            }
        }

        return false;
    }
    // server

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

        if (194 <= ServerConfig.GetVersion()) {
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

        ServerPacket sp = new ServerPacket(ServerPacket.Header.LP_OpenShopDlg);

        if (ServerConfig.IsPostBB() && ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
            sp.Encode1(0);
        }

        sp.Encode4(sid);

        if (ServerConfig.IsPostBB() && ServerConfig.IsJMS() && 194 <= ServerConfig.GetVersion()) {
            // 0 = normal shop, 1 is probably coin shop
            sp.Encode1(0); // if 1, Encode1(size), Encode4, EncodeStr
        }

        sp.Encode2(items.size()); // item count
        for (MapleShopItem item : items) {
            sp.Encode4(item.getItemId());
            sp.Encode4(item.getPrice());

            if ((ServerConfig.IsJMS() && 180 <= ServerConfig.GetVersion()) || ServerConfig.IsKMS()) {
                sp.Encode4(item.getReqItem()); // nTokenItemID
                sp.Encode4(item.getReqItemQ()); // nTokenPrice
                if (ServerConfig.IsPostBB() || (ServerConfig.IsJMS() && 186 <= ServerConfig.GetVersion())) {
                    sp.Encode4(0); // nItemPeriod
                }
                sp.Encode4(0); // nLevelLimited
            }

            if (!GameConstants.isThrowingStar(item.getItemId()) && !GameConstants.isBullet(item.getItemId())) {
                sp.Encode2(1); // stacksize o.o
                sp.Encode2(item.getBuyable());
            } else {
                sp.EncodeZeroBytes(6);
                sp.Encode2(BitTools.doubleToShortBits(ii.getPrice(item.getItemId())));
                sp.Encode2(ii.getSlotMax(c, item.getItemId()));
            }
        }

        return sp.Get();
    }

    // CShopDlg::OnPacket
    // confirmShopTransaction
    public static MaplePacket confirmShopTransaction(SP_ShopFlag flag, int level) {
        ServerPacket p = new ServerPacket(ServerPacket.Header.LP_ShopResult);

        p.Encode1(flag.get()); // 8 = sell, 0 = buy, 0x20 = due to an error

        switch (flag) {
            case ERROR_LEVEL_UNDER:
            case ERROR_LEVEL_HIGH: {
                p.Encode4(level);
                break;
            }
            default: {
                break;
            }
        }

        return p.Get();
    }

    public static MaplePacket confirmShopTransaction(SP_ShopFlag flag) {
        return confirmShopTransaction(flag, 0);
    }
}
