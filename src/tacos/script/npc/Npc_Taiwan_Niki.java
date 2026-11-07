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
// Npc.wz script : hair_taiwan1 (JMS131-302), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮)
public class Npc_Taiwan_Niki extends NpcSalon {

    private static final int item_coupon_5150022 = 5150022; // 調髪特別券(夜市場)
    private static final int item_coupon_5151015 = 5151015; // 染毛特別券(夜市場)

    public Npc_Taiwan_Niki() {
        super(
                null,
                // TODO : 文章 (適当)
                "いらっしゃい！夜市場の美容室へようこそ。#b#t5150022##kか#b#t5151015##kを持っているなら、髪をどうするか決めてくれ…",
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "スタイル変更（特別券）",
                        KIND_HAIR, item_coupon_5150022, false,
                        // TODO : 文章 (適当)
                        "新しい髪型にしてあげますよ。#b#t5150022##kがあれば、お好みの髪型を選んでください。",
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
                                30020, // 長髪(黒)
                                30000, // かりあげ(黒)
                                30780, // ブレード(黒)
                                30130, // パーマ(黒)
                                30350, // ワイルド(黒)
                                30190, // パンプキン(黒)
                                30110, // とんがり(黒)
                                30180, // ライオンA(黒)
                                30050, // プリンス(黒)
                                30040, // 短髪(灰)
                                30160 // サンダー(黒)
                            },
                            // 女性
                            {
                                31050, // ショート(黒)
                                31040, // パンク(黒)
                                31000, // キューティー(黒)
                                31760, // ストレートツインテールA(黒)
                                31060, // シック(黒)
                                31090, // ポニーテールA(黒)
                                31330, // マジック(黒)
                                31020, // ウェーブ(黒)
                                31130, // 両編み(黒)
                                31120, // 片編み(黒)
                                31140, // チャイナA(黒)
                                31010 // ウインド(黒)
                            }
                        }),
                new SalonEntry(
                        // TODO : 文章 (適当)
                        "ヘアカラー（特別券）",
                        KIND_HAIR_COLOR, item_coupon_5151015, false,
                        // TODO : 文章 (適当)
                        "髪の色を変えてあげますよ。#b#t5151015##kがあれば、お好みの色を選んでください。",
                        null,
                        // TODO : 文章 (適当)
                        "はい、できあがりました。新しい髪の色を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…このお店のクーポンをお持ちでないようですね…クーポンがないと染められないんです。",
                        null,
                        new int[][]{
                            {0, 1, 2, 3, 4, 5, 6, 7}
                        })
        );
    }
}
