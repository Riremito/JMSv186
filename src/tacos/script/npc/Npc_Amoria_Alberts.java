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
public class Npc_Amoria_Alberts extends NpcSalon {

    private static final int item_coupon_5152022 = 5152022; // 整形外科特別券(ウェディング)

    public Npc_Amoria_Alberts() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152022, false,
                        "百万メルの顔になる準備はできましたか？#b#t5152022##kがあれば、施術が終わるころには別人のようになっていることを保証しますよ！",
                        null,
                        "あなたの新しい顔は本物の芸術作品ですね…最高の医者のところに来てよかったでしょう！新しい顔になりたくなったら、いつでもまた来てください！",
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
