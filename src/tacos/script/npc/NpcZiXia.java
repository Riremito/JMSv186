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
// Npc.wz script : 9310017 (CMS86-88, EMS76, MSEA102, TWMS77-125), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮)
public class NpcZiXia extends NpcSalon {

    private static final int item_coupon_5150014 = 5150014; // 豫園美髮店一般會員卡
    private static final int item_coupon_5151010 = 5151010; // 豫園染色一般會員卡

    public NpcZiXia() {
        super(
                null,
                // TODO : 文章 (適当)
                "こんにちは！ヘアサロンの助手です。#b#t5150014##kか#b#t5151010##kがあれば、髪をランダムで変えてあげますよ。",
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "スタイル変更（一般会員カード）",
                        KIND_HAIR, item_coupon_5150014, true,
                        // TODO : 文章 (適当)
                        "一般券を使うと、髪型がランダムで新しい髪型に変わります…それでも#b#t5150014##kを使いますか？",
                        // TODO : 文章 (適当)
                        "なるほど…決心がついたら教えてくださいね。",
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
                        "ヘアカラー（一般会員カード）",
                        KIND_HAIR_COLOR, item_coupon_5151010, true,
                        // TODO : 文章 (適当)
                        "一般券を使うと、髪の色がランダムで変わります…それでも#b#t5151010##kを使いますか？",
                        // TODO : 文章 (適当)
                        "なるほど…決心がついたら教えてくださいね。",
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
