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
// Npc.wz script : face_taiwan2 (JMS131-302), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮)
public class Npc_Taiwan_Kwan extends NpcSalon {

    private static final int item_coupon_5152023 = 5152023; // 整形外科一般券(夜市場)

    public Npc_Taiwan_Kwan() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152023, true,
                        // TODO : 文章 (適当)
                        "一般券を使うと、顔がランダムで新しい顔に変わります…それでも#b#t5152023##kを使いますか？",
                        // TODO : 文章 (適当)
                        "なるほど…決心がついたら教えてくださいね。",
                        // TODO : 文章 (適当)
                        "はい、施術が終わりました。新しい顔を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…この病院のクーポンをお持ちでないようですね…クーポンがないと整形はできないんです。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                20000, // 顔１♂
                                20001, // 顔２♂
                                20002, // 顔３♂
                                20003, // 顔４♂
                                20004, // 顔５♂
                                20005, // 顔６♂
                                20006, // 顔７♂
                                20007, // 顔８♂
                                20008, // 顔９♂
                                20012, // 顔１３♂
                                20014 // 顔１５♂
                            },
                            // 女性
                            {
                                21000, // 顔1♀
                                21001, // 顔２♀
                                21002, // 顔３♀
                                21003, // 顔４♀
                                21004, // 顔５♀
                                21005, // 顔６♀
                                21006, // 顔７♀
                                21007, // 顔８♀
                                21008, // 顔９♀
                                21012, // 顔１３♀
                                21014 // 顔１５♀
                            }
                        })
        );
    }
}
