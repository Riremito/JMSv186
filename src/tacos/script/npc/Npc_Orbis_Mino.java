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
public class Npc_Orbis_Mino extends NpcSalon {

    private static final int item_coupon_5150005 = 5150005; // 調髪特別券(オルビス)
    private static final int item_coupon_5151005 = 5151005; // 染毛特別券(オルビス)
    private static final int item_coupon_5154000 = 5154000; // さばき髪クーポン(オルビス)

    public Npc_Orbis_Mino() {
        super(
                null,
                // 翻訳
                "こんにちは、私は#p2010001#。#b#t5150005##kか#b#t5151005##kを持っているなら、髪は私に任せて。どうするか決めてね。",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（VIPクーポン）",
                        KIND_HAIR, item_coupon_5150005, false,
                        "あなたの髪型をガラッと変えてあげられるわ。イメチェンする#G準備:準備#はできてる？#b#t5150005##kがあれば、変えてあげるわよ。好きなのを選んでね！",
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
                                30490, // フリーダムウルフ(黒)
                                30230, // ショック(黒)
                                30260, // ニューゼン(黒)
                                30280, // モヒカンシャギー(黒)
                                30240, // 栗(黒)
                                30290, // シチサン(黒)
                                30270, // イガグリ(黒)
                                30340 // カスケード(黒)
                            },
                            // 女性
                            {
                                31040, // パンク(黒)
                                31000, // キューティー(黒)
                                31670, // ガリシアップ(黒)
                                31250, // オカッパ(黒)
                                31220, // ニューゼン(黒)
                                31260, // デイジー(黒)
                                31240, // さばき髪(黒)
                                31110, // ポニーテールB(黒)
                                31270, // アン(黒)
                                31030, // 三つ編み(黒)
                                31230 // ローズマリー(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（VIPクーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151005, false,
                        "あなたの髪の色をガラッと変えてあげられるわ。イメチェンする#G準備:準備#はできてる？#b#t5151005##kがあれば、変えてあげるわよ。好きなのを選んでね！",
                        null,
                        "ほら見て！！どう？我ながら芸術作品だと思うわ！ははは。髪の色を変えたくなったら私のところに来てね。いつでも素敵にしてあげるから！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を染められないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 1, 7, 3, 4, 5}
                        }),
                new SalonEntry(
                        // 翻訳
                        "#t5154000#を使う",
                        KIND_HAIR, item_coupon_5154000, true,
                        "#b#t5154000##kを持っているみたいね。このクーポンは、ボサボサヘアが好きな女性のために特別に作られたものなの。使えばすぐにボサボサヘアにしてあげるわ。本当に#b#t5154000##kを使って髪型を変える？",
                        "わかったわ…少し考えて、心が変化を求めたら話しかけてね。",
                        "ほら見て！！どう？我ながら芸術作品だと思うわ！ははは。新しい髪型にしたくなったら私のところに来てね。いつでも素敵にしてあげるから！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        "#b#t5154000##kを使いたいの？ごめんなさい、もうボサボサヘアになっているみたい。また別の機会にしてね。",
                        new int[][]{
                            {
                                31240 // さばき髪(黒)
                            }
                        })
        );
    }
}
