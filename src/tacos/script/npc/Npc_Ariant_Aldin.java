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
// Npc.wz script : face_ariant2 (JMS131-302), lua_script_jp の face_ariant2 は中身がないため js の処理を利用
public class Npc_Ariant_Aldin extends NpcSalon {

    private static final int item_coupon_5152029 = 5152029; // 整形外科一般券(アリアント)

    public Npc_Ariant_Aldin() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152029, true,
                        // TODO : 文章 (適当)
                        "一般券を使うと、顔がランダムで新しい顔に変わります…それでも#b#t5152029##kを使いますか？",
                        // TODO : 文章 (適当)
                        "なるほど…本当にそうしたいのか、ゆっくり考えてください。決心がついたら教えてくださいね。",
                        // TODO : 文章 (適当)
                        "新しい顔を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…この病院専用のクーポンを持っていないようですね…申し訳ないけれど、クーポンがないと整形はできないんです。",
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
