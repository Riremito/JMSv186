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
// Npc.wz script : hair_ariant2 (JMS131-302), lua_script_jp の hair_ariant2 は中身がないため js の処理を利用
public class Npc_Ariant_Shati extends NpcSalon {

    private static final int item_coupon_5150026 = 5150026; // 調髪一般券(アリアント)
    private static final int item_coupon_5151021 = 5151021; // 染毛一般券(アリアント)

    public Npc_Ariant_Shati() {
        super(
                null,
                // TODO : 文章 (適当)
                "こんにちは！私はシャティ、#p2100006#さんの弟子です。#b#t5150026##kか#b#t5151021##kをお持ちなら、私に髪を任せてみませんか？",
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "ヘアスタイルの変更(一般券)",
                        KIND_HAIR, item_coupon_5150026, true,
                        // TODO : 文章 (適当)
                        "一般券を使うと、ヘアスタイルがランダムで新しいスタイルに変わります。特別券では選べない、私が考えた新しいヘアスタイルになることもありますよ。#b#t5150026##kを使ってヘアスタイルを変えますか？",
                        // TODO : 文章 (適当)
                        "まだ心の準備ができていないみたいですね。決心がついたら声をかけてください！",
                        // TODO : 文章 (適当)
                        "新しいヘアスタイルを楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "クーポンを持ってきてもらわないと、ヘアスタイルは変えられないんです。忘れていませんか？",
                        null,
                        new int[][]{
                            // 男性
                            {30250, 30350, 30270, 30150, 30300, 30600, 30160, 30700, 30720, 30420},
                            // 女性
                            {31040, 31250, 31310, 31220, 31300, 31680, 31160, 31030, 31230, 31690, 31210, 31170, 31450}
                        }),
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "ヘアカラーの変更(一般券)",
                        KIND_HAIR_COLOR, item_coupon_5151021, true,
                        // TODO : 文章 (適当)
                        "一般券を使うと、ヘアカラーがランダムで新しい色に変わります。#b#t5151021##kを使ってヘアカラーを変えますか？",
                        // TODO : 文章 (適当)
                        "まだ心の準備ができていないみたいですね。決心がついたら声をかけてください！",
                        // TODO : 文章 (適当)
                        "新しいヘアカラーを楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "クーポンを持ってきてもらわないと、ヘアカラーは変えられないんです。忘れていませんか？",
                        null,
                        new int[][]{
                            {0, 1, 2, 3, 4, 5, 6, 7}
                        })
        );
    }
}
