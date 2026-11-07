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
// Npc.wz script : hair_shaolin3 (JMS146-302), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮), String.wz の会話から坊主頭とスキンヘッドのみとしている
public class Npc_Shaolin_LiaoPan extends NpcSalon {

    private static final int item_coupon_5150036 = 5150036; // 調髪特別券（大雄宝殿）

    public Npc_Shaolin_LiaoPan() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_HAIR, item_coupon_5150036, false,
                        // TODO : 文章 (適当)
                        "新しい髪型にしてあげますよ。#b#t5150036##kがあれば、お好みの髪型を選んでください。",
                        null,
                        // TODO : 文章 (適当)
                        "はい、できあがりました。新しい髪型を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…このお店のクーポンをお持ちでないようですね…クーポンがないとカットできないんです。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30030, // 丸坊主(黒)
                                30430 // スキンヘッド(黒)
                            },
                            // 女性
                            {
                                31430 // スキンヘッド(黒)
                            }
                        })
        );
    }
}
