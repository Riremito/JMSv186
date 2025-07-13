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
package packet.ops;

import config.Region;
import config.Version;

/**
 *
 * @author Riremito
 */
public enum OpsShop implements IPacketOps {
    ShopReq_Buy,
    ShopReq_Sell,
    ShopReq_Recharge,
    ShopReq_Close,
    ShopRes_BuySuccess,
    ShopRes_BuyNoStock,
    ShopRes_BuyNoMoney,
    ShopRes_BuyUnknown,
    ShopRes_SellSuccess,
    ShopRes_SellNoStock,
    ShopRes_SellIncorrectRequest,
    ShopRes_SellUnkonwn,
    ShopRes_RechargeSuccess,
    ShopRes_RechargeNoStock,
    ShopRes_RechargeNoMoney,
    ShopRes_RechargeIncorrectRequest,
    ShopRes_RechargeUnknown,
    ShopRes_BuyNoToken,
    ShopRes_LimitLevel_Less,
    ShopRes_LimitLevel_More,
    ShopRes_CantBuyAnymore,
    ShopRes_TradeBlocked,
    ShopRes_BuyLimit,
    ShopRes_ServerMsg,
    UNKNOWN(-1);

    private int value;

    OpsShop(int val) {
        this.value = val;
    }

    OpsShop() {
        this.value = -1;
    }

    @Override
    public int get() {
        return this.value;
    }

    @Override
    public void set(int val) {
        this.value = val;
    }

    public static OpsShop find(int val) {
        for (final OpsShop ops : values()) {
            if (ops.get() == val) {
                return ops;
            }
        }
        return UNKNOWN;
    }

    public static void init() {
        if (Version.GreaterOrEqual(Region.JMS, 302)) {
            ShopReq_Buy.set(0);
            ShopReq_Sell.set(1);
            ShopReq_Recharge.set(2);
            ShopReq_Close.set(3);
            ShopRes_BuySuccess.set(0);
            // 1 : ?
            ShopRes_BuyNoStock.set(2);
            ShopRes_BuyNoMoney.set(3);
            // 4 : 名声度の等級
            ShopRes_BuyUnknown.set(5);
            //ShopRes_SellSuccess.set(6);
            ShopRes_SellSuccess.set(0); // not coded.
            ShopRes_SellNoStock.set(7);
            ShopRes_SellIncorrectRequest.set(8);
            ShopRes_SellUnkonwn.set(9);
            ShopRes_RechargeSuccess.set(10);
            ShopRes_RechargeNoStock.set(11);
            ShopRes_RechargeNoMoney.set(12);
            ShopRes_RechargeIncorrectRequest.set(13);
            ShopRes_RechargeUnknown.set(14);
            ShopRes_BuyNoToken.set(15);
            ShopRes_LimitLevel_Less.set(16);
            ShopRes_LimitLevel_More.set(17);
            return;
        }
        if (Version.GreaterOrEqual(Region.JMS, 194)) {
            ShopReq_Buy.set(0);
            ShopReq_Sell.set(1);
            ShopReq_Recharge.set(2);
            ShopReq_Close.set(3);
            ShopRes_BuySuccess.set(0);
            // ?_?
            ShopRes_BuyNoStock.set(2);
            ShopRes_BuyNoMoney.set(3);
            ShopRes_BuyUnknown.set(4);
            ShopRes_SellSuccess.set(5);
            ShopRes_SellNoStock.set(6);
            ShopRes_SellIncorrectRequest.set(7);
            ShopRes_SellUnkonwn.set(8);
            ShopRes_RechargeSuccess.set(9);
            ShopRes_RechargeNoStock.set(10);
            ShopRes_RechargeNoMoney.set(11);
            ShopRes_RechargeIncorrectRequest.set(12);
            ShopRes_RechargeUnknown.set(13);
            ShopRes_BuyNoToken.set(14);
            ShopRes_LimitLevel_Less.set(15);
            ShopRes_LimitLevel_More.set(16);
            ShopRes_CantBuyAnymore.set(17);
            ShopRes_TradeBlocked.set(18);
            // 19 : パチンコ玉を最後に購入してから、31日以上経過しています。
            // 20 : 着用できない性別のため、アイテム交換はできません。
            ShopRes_BuyLimit.set(23);
            ShopRes_ServerMsg.set(24);
            return;
        }
        // JMS186
        ShopReq_Buy.set(0);
        ShopReq_Sell.set(1);
        ShopReq_Recharge.set(2);
        ShopReq_Close.set(3);
        ShopRes_BuySuccess.set(0);
        ShopRes_BuyNoStock.set(1);
        ShopRes_BuyNoMoney.set(2);
        ShopRes_BuyUnknown.set(3);
        ShopRes_SellSuccess.set(4);
        ShopRes_SellNoStock.set(5);
        ShopRes_SellIncorrectRequest.set(6);
        ShopRes_SellUnkonwn.set(7);
        ShopRes_RechargeSuccess.set(8);
        ShopRes_RechargeNoStock.set(9);
        ShopRes_RechargeNoMoney.set(10);
        ShopRes_RechargeIncorrectRequest.set(11);
        ShopRes_RechargeUnknown.set(12);
        return;
    }
}
