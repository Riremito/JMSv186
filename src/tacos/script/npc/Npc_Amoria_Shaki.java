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
public class Npc_Amoria_Shaki extends NpcSalon {

    private static final int item_coupon_5152021 = 5152021; // 整形外科一般券(ウェディング)

    public Npc_Amoria_Shaki() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152021, true,
                        "#b#t5152021##kを使えば、あなたの#Gパートナー:パートナー#が笑顔になるような素敵な仕上がりにしてあげられますよ。",
                        "なるほど…本当にそれでいいのか、少し考えてみてください。決まったら教えてくださいね。",
                        "ほら、そんなに悪くなかったでしょう…新しい顔、とてもいいと思いますよ！ワクワクする新しい顔になりたくなったら、いつでもまた来てください！",
                        "うーん…ここで使えるクーポンをお持ちでないようですね…申し訳ありませんが、クーポンがないと整形はできません。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                20018, // 顔２１♂
                                20019, // 顔２２♂
                                20000, // 顔１♂
                                20001, // 顔２♂
                                20003, // 顔４♂
                                20004, // 顔５♂
                                20005, // 顔６♂
                                20006, // 顔７♂
                                20007, // 顔８♂
                                20008 // 顔９♂
                            },
                            // 女性
                            {
                                21018, // 顔２０♀
                                21019, // 顔２１♀
                                21001, // 顔２♀
                                21002, // 顔３♀
                                21003, // 顔４♀
                                21004, // 顔５♀
                                21005, // 顔６♀
                                21006, // 顔７♀
                                21007, // 顔８♀
                                21012 // 顔１３♀
                            }
                        })
        );
    }
}
