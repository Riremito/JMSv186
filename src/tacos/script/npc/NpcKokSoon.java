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
// Npc.wz script : 9270053 (CMS86-88, EMS76, MSEA102, TWMS122-125), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮)
public class NpcKokSoon extends NpcSalon {

    private static final int item_coupon_5152040 = 5152040; // SEA Cosmetic Lens Coupon (VIP)
    private static final int item_coupon_5152039 = 5152039; // SEA Cosmetic Lens Coupon (REG)

    public NpcKokSoon() {
        super(
                null,
                // TODO : 文章 (適当)
                "いらっしゃい！眼鏡屋です。#b#t5152040##kか#b#t5152039##kがあれば、目の色を変えてあげますよ。",
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "カラーレンズ（VIPクーポン）",
                        KIND_FACE_COLOR, item_coupon_5152040, false,
                        // TODO : 文章 (適当)
                        "目の色を変えてあげますよ。#b#t5152040##kがあれば、お好みの色を選んでください。",
                        null,
                        // TODO : 文章 (適当)
                        "はい、できあがりました。新しい目の色を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…このお店のクーポンをお持ちでないようですね…クーポンがないとレンズを作れないんです。",
                        null,
                        new int[][]{
                            {0, 100, 200, 300, 400, 500, 600, 700}
                        }),
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "カラーレンズ（一般クーポン）",
                        KIND_FACE_COLOR, item_coupon_5152039, true,
                        // TODO : 文章 (適当)
                        "一般券を使うと、目の色がランダムで変わります…それでも#b#t5152039##kを使いますか？",
                        // TODO : 文章 (適当)
                        "なるほど…決心がついたら教えてくださいね。",
                        // TODO : 文章 (適当)
                        "はい、できあがりました。新しい目の色を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…このお店のクーポンをお持ちでないようですね…クーポンがないとレンズを作れないんです。",
                        null,
                        new int[][]{
                            {0, 100, 200, 300, 400, 500, 600, 700}
                        })
        );
    }
}
