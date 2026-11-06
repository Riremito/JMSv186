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
public class Npc_MuLung_Pata extends NpcSalon {

    private static final int item_coupon_5152028 = 5152028; // 整形外科特別券(武陵)

    public Npc_MuLung_Pata() {
        super(
                null,
                // 翻訳
                "私はここ武陵のお店で整形を担当しています！目は体の中で一番大切な部分だと思っています。#b#t5152028##kがあれば、あなたにぴったりのレンズを処方できますよ。",
            // TODO : 武陵 VIPコンタクトレンズクーポン (5152041) は v186 に存在しないため未実装
                new SalonEntry(
                        // 翻訳
                        "武陵 コンタクトレンズクーポン",
                        KIND_FACE, item_coupon_5152028, false,
                        "当店の特別な機械を使えば、施術後の見た目を事前に確認できます。どんなレンズにしますか？お好みのスタイルを選んでください…",
                        null,
                        "じゃーん！ほら、見てごらん！！どうですか？今のあなたの目、本当に素敵ですよ！またぜひ来てくださいね。",
                        "申し訳ありませんが、当店のコンタクトレンズクーポンをお持ちでないようです。クーポンがないと、残念ながら施術はできません。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                20010, // 顔１１♂
                                20000, // 顔１♂
                                20002, // 顔３♂
                                20004, // 顔５♂
                                20005, // 顔６♂
                                20006, // 顔７♂
                                20007, // 顔８♂
                                20012, // 顔１３♂
                                20009 // 顔１０♂
                            },
                            // 女性
                            {
                                21011, // 顔１２♀
                                21000, // 顔1♀
                                21002, // 顔３♀
                                21003, // 顔４♀
                                21005, // 顔６♀
                                21006, // 顔７♀
                                21008, // 顔９♀
                                21012, // 顔１３♀
                                21009 // 顔１０♀
                            }
                        })
        );
    }
}
