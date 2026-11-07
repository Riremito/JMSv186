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
public class Npc_Henesys_Natalie extends NpcSalon {

    private static final int item_coupon_5150001 = 5150001; // 調髪特別券(ヘネシス)
    private static final int item_coupon_5151001 = 5151001; // 染毛特別券(ヘネシス)

    public Npc_Henesys_Natalie() {
        super(
                null,
                // 翻訳
                "私は#p1012103#、このヘアサロンのオーナーよ。#b#t5150001##kか#b#t5151001##kを持っているなら、ヘアスタイルは私に任せて。何にするか選んでね。",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（VIPクーポン）",
                        KIND_HAIR, item_coupon_5150001, false,
                        "あなたの髪型を変えて、素敵にしてあげられるわ。ちょっとイメチェンしてみない？#b#t5150001##kがあれば、変えてあげるわよ。好きなのを選んでね。",
                        null,
                        "ほら見て！！どう？我ながら芸術作品だと思うわ！ははは。新しい髪型にしたくなったら私のところに来てね。いつでも素敵にしてあげるから！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30030, // 丸坊主(黒)
                                30020, // 長髪(黒)
                                30000, // かりあげ(黒)
                                30480, // スマート(黒)
                                30310, // プリン(黒)
                                30330, // ストーム(黒)
                                30060, // ロンゲ(黒)
                                30150, // もみあげ(黒)
                                30410, // ストローク(黒)
                                30210, // タフガイ(黒)
                                30140, // まげ(黒)
                                30120, // 学生カット(黒)
                                30200 // ウィンド(黒)
                            },
                            // 女性
                            {
                                31050, // ショート(黒)
                                31040, // パンク(黒)
                                31000, // キューティー(黒)
                                31700, // キューティーベビー（黒）
                                31150, // ストレート(黒)
                                31310, // バニラ(黒)
                                31300, // ブレイズ(黒)
                                31160, // 編みキュート(黒)
                                31100, // フラワーピン(黒)
                                31410, // ロングウルフ(黒)
                                31030, // 三つ編み(黒)
                                31080, // フロント(黒)
                                31070 // パンクパーマ(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（VIPクーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151001, false,
                        "あなたの髪型を変えて、素敵にしてあげられるわ。ちょっとイメチェンしてみない？#b#t5151001##kがあれば、変えてあげるわよ。好きなのを選んでね。",
                        null,
                        "ほら見て！！どう？我ながら芸術作品だと思うわ！ははは。新しい髪型にしたくなったら私のところに来てね。いつでも素敵にしてあげるから！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 1, 6, 2, 7, 4}
                        })
        );
    }
}
