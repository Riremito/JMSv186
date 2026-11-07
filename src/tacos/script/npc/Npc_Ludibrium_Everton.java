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
public class Npc_Ludibrium_Everton extends NpcSalon {

    private static final int item_coupon_5152006 = 5152006; // 整形外科一般券（ルディブリアム）

    public Npc_Ludibrium_Everton() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152006, true,
                        "一般クーポンを使うと、どんな顔になるかわかりません…それでも#b#t5152006##kを使いますか？",
                        "わかりました。少し考えてみて、それでもやりたければ教えてくださいね？",
                        "施術が終わりました…ご覧ください、鏡をどうぞ…いかがですか？我ながら芸術作品だと思いますよ…ははは、新しい顔に飽きたら、また呼んでくださいね？",
                        "うーん…ここで使えるクーポンをお持ちでないようですね…申し訳ありませんが、クーポンがないと整形はできません。",
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
