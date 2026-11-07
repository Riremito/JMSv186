/*
 * Copyright (C) 2026 Riremito
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

/**
 *
 * @author Riremito
 */
// Npc.wz script : skin_taiwan1 (JMS131-302), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮)
public class Npc_Taiwan_Kararan extends NpcSalon {

    private static final int item_coupon_5153005 = 5153005; // スキンケアークーポン(夜市場)

    public Npc_Taiwan_Kararan() {
        super(
                // TODO : 文章 (適当)
                "こんにちは！夜市場のスキンケアへようこそ。#b#t5153005##kがあれば、お好みの肌の色にしてあげますよ～！",
                null,
                new SalonEntry(
                        null,
                        KIND_SKIN, item_coupon_5153005, false,
                        // TODO : 文章 (適当)
                        "当店の専用マシンなら、施術後の見た目を先に確認できます。お好みの肌の色を選んでくださいね～",
                        null,
                        // TODO : 文章 (適当)
                        "さあ、鏡を見てください！気に入りましたか？また来てくださいね～",
                        // TODO : 文章 (適当)
                        "うーん…当店のスキンケアクーポンをお持ちでないようですね。クーポンがないと施術はできません。",
                        null,
                        new int[][]{
                            {0, 1, 2, 3, 4}
                        })
        );
    }
}
