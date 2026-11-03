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
public class NpcPata extends NpcSalon {

    private static final int item_coupon_5152028 = 5152028; // 整形外科特別券(武陵)

    public NpcPata() {
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
                            {20010, 20000, 20002, 20004, 20005, 20006, 20007, 20012, 20009},
                            // 女性
                            {21011, 21000, 21002, 21003, 21005, 21006, 21008, 21012, 21009}
                        })
        );
    }
}
