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
public class Npc_Zipangu_Hikekuro extends NpcSalon {

    private static final int item_coupon_5152009 = 5152009; // 整形外科特別券(ショーワ)

    public Npc_Zipangu_Hikekuro() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152009, false,
                        "整形の魔法で、あなたの顔をまったく新しいものに変えてあげられますよ。周りの人から憧れられるような、美しく健康的な顔になりたくありませんか？#b#t5152009##kがあれば、それを叶えてあげます。お好みの顔を選んでください。",
                        null,
                        "はい、施術が終わりました。ほら、鏡をどうぞ。いかがですか？我ながら傑作だと思いますよ。新しい顔に飽きたら、また寄ってくださいね？",
                        "当店指定の整形クーポンをお持ちでないようですね。申し訳ありませんが、クーポンがないと整形はできません。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                20002, // 顔３♂
                                20003, // 顔４♂
                                20004, // 顔５♂
                                20007, // 顔８♂
                                20008, // 顔９♂
                                20009, // 顔１０♂
                                20010, // 顔１１♂
                                20011, // 顔１２♂
                                20013, // 顔１４♂
                                20016, // 顔１６♂
                                20017 // 顔１７♂
                            },
                            // 女性
                            {
                                21003, // 顔４♀
                                21003, // 顔４♀
                                21004, // 顔５♀
                                21005, // 顔６♀
                                21008, // 顔９♀
                                21009, // 顔１０♀
                                21010, // 顔１１♀
                                21011, // 顔１２♀
                                21013, // 顔１４♀
                                21016, // 顔１６♀
                                21017 // 顔１７♀
                            }
                        })
        );
    }
}
