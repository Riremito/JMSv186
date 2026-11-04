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
// Npc.wz script : 9310018 (CMS86-88, EMS76, MSEA102, TWMS77-125), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮)
public class Npc_Shanghai_RuHua extends NpcSalon {

    private static final int item_coupon_5153003 = 5153003; // 豫園護膚中心會員卡

    public Npc_Shanghai_RuHua() {
        super(
                // TODO : 文章 (適当)
                "こんにちは！豫園のスキンケアセンターへようこそ。#b#t5153003##kがあれば、夢見ていた肌になれますよ～！",
                null,
                new SalonEntry(
                        null,
                        KIND_SKIN, item_coupon_5153003, false,
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
