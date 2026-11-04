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
// Npc.wz script : hair_shaolin1 (JMS146-302), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮)
public class Npc_Shaolin_ChiuBuran extends NpcSalon {

    private static final int item_coupon_5150035 = 5150035; // 調髪特別券（宋山里）
    private static final int item_coupon_5151030 = 5151030; // 染毛特別券（宋山里）

    public Npc_Shaolin_ChiuBuran() {
        super(
                null,
                // TODO : 文章 (適当)
                "いらっしゃい！宋山里の美容室へようこそ。#b#t5150035##kか#b#t5151030##kを持っているなら、髪をどうするか決めてくれ…",
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "スタイル変更（特別券）",
                        KIND_HAIR, item_coupon_5150035, false,
                        // TODO : 文章 (適当)
                        "新しい髪型にしてあげますよ。#b#t5150035##kがあれば、お好みの髪型を選んでください。",
                        null,
                        // TODO : 文章 (適当)
                        "はい、できあがりました。新しい髪型を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…このお店のクーポンをお持ちでないようですね…クーポンがないとカットできないんです。",
                        null,
                        new int[][]{
                            // 男性
                            {30030, 30020, 30000, 30780, 30130, 30350, 30190, 30110, 30180, 30050, 30040, 30160},
                            // 女性
                            {31050, 31040, 31000, 31760, 31060, 31090, 31330, 31020, 31130, 31120, 31140, 31010}
                        }),
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "ヘアカラー（特別券）",
                        KIND_HAIR_COLOR, item_coupon_5151030, false,
                        // TODO : 文章 (適当)
                        "髪の色を変えてあげますよ。#b#t5151030##kがあれば、お好みの色を選んでください。",
                        null,
                        // TODO : 文章 (適当)
                        "はい、できあがりました。新しい髪の色を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…このお店のクーポンをお持ちでないようですね…クーポンがないと染められないんです。",
                        null,
                        new int[][]{
                            {0, 1, 2, 3, 4, 5, 6, 7}
                        })
        );
    }
}
