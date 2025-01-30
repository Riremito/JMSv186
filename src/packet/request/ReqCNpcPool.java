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
import packet.ClientPacket;
import server.MapleShop;

/**
 *
 * @author elfenlied
 */
public class ReqCNpcPool {

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

    public static void init() {
        if (ServerConfig.JMS194orLater()) {
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
    public static boolean OnShopPacket(ClientPacket cp, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return false;
        }

        MapleShop shop = chr.getShop();

        if (shop == null) {
            chr.setConversation(0);
            return false;
        }

        byte flag = cp.Decode1();

        switch (CP_ShopFlag.get(flag)) {
            case BUY_ITEM: {
                cp.Decode2();

                if (ServerConfig.JMS194orLater()) {
                    cp.Decode1();
                }

                final int itemId = cp.Decode4();
                final short quantity = cp.Decode2();
                shop.buy(c, chr, itemId, quantity);
                break;
            }
            case SELL_ITEM: {
                final byte slot = (byte) cp.Decode2();
                final int itemId = cp.Decode4();
                final short quantity = cp.Decode2();
                shop.sell(c, GameConstants.getInventoryType(itemId), slot, quantity);
                break;
            }
            case CHARGE_ITEM: {
                final byte slot = (byte) cp.Decode2();
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
                Debug.CPLogError(cp);
                break;
            }
        }

        return false;
    }
    // server
}
