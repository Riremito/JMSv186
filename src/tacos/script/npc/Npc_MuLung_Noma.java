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
public class Npc_MuLung_Noma extends NpcSalon {

    private static final int item_coupon_5152027 = 5152027; // 整形外科一般券(武陵)

    public Npc_MuLung_Noma() {
        super(
                null,
                // 翻訳
                "私は#p2090104#、ここの整形外科の助手です！#b#t5152027##kがあれば、ランダムな結果の整形を受けられます。",
            // TODO : 武陵 コンタクトレンズクーポン (5152042) は v186 に存在しないため未実装
                new SalonEntry(
                        // 翻訳
                        "武陵 整形クーポン",
                        KIND_FACE, item_coupon_5152027, true,
                        "一般クーポンを使うと、ランダムなコンタクトレンズになります。#b#t5152027##kを使って、目の印象をガラッと変えてみますか？",
                        "わかります。好みのコンタクトレンズになるか自信がないなら、迷うのも当然ですよね。とにかく急ぐことはありませんから、少し考えてみてください！  変える決心がついたら教えてくださいね！",
                        "じゃーん！ほら、見てごらん！！どうですか？今のあなたの目、本当に素敵ですよ！またぜひ来てくださいね。",
                        "申し訳ありませんが、当店のコンタクトレンズクーポンをお持ちでないようです。クーポンがないと、残念ながら施術はできません。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                20009, // 顔１０♂
                                20000, // 顔１♂
                                20002, // 顔３♂
                                20004, // 顔５♂
                                20005, // 顔６♂
                                20006, // 顔７♂
                                20007, // 顔８♂
                                20012 // 顔１３♂
                            },
                            // 女性
                            {
                                21009, // 顔１０♀
                                21000, // 顔1♀
                                21002, // 顔３♀
                                21003, // 顔４♀
                                21005, // 顔６♀
                                21006, // 顔７♀
                                21008, // 顔９♀
                                21012 // 顔１３♀
                            }
                        })
        );
    }
}
