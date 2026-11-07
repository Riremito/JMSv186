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
public class Npc_MuLung_Lilishu extends NpcSalon {

    private static final int item_coupon_5150024 = 5150024; // 調髪一般券(武陵)
    private static final int item_coupon_5151019 = 5151019; // 染毛一般券(武陵)

    public Npc_MuLung_Lilishu() {
        super(
                null,
                // 翻訳
                "私はこの店のヘアアシスタントです。もし#b#t5150024##kか#b#t5151019##kを持っていたら、髪型を変えさせてもらえませんか？",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（EXPクーポン）",
                        KIND_HAIR, item_coupon_5150024, true,
                        "一般クーポンを使うと、髪型が「ランダム」に変わります。それでも#b#t5150024##kを使って、ガラッと変えますか？",
                        "わかったわ…よく考えて、それでも変えたかったら話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪型はどう？完璧な仕上がりとは言えないかもしれないけど、なかなかいいじゃない？また変えたくなったら来てね！",
                        "うーん…当店指定のクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30700, // スプリン(黒)
                                30720, // ハイライトロング(黒)
                                30420, // サムライ(黒)
                                30250, // アフロ(黒)
                                30150, // もみあげ(黒)
                                30240, // 栗(黒)
                                30350, // ワイルド(黒)
                                30180, // ライオンA(黒)
                                30300, // ロマンス(黒)
                                30270, // イガグリ(黒)
                                30160 // サンダー(黒)
                            },
                            // 女性
                            {
                                31690, // スプリン(黒)
                                31210, // 小町(黒)
                                31170, // ドレッド(黒)
                                31310, // バニラ(黒)
                                31300, // ブレイズ(黒)
                                31160, // 編みキュート(黒)
                                31030, // 三つ編み(黒)
                                31250, // オカッパ(黒)
                                31220, // ニューゼン(黒)
                                31230 // ローズマリー(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151019, true,
                        "一般クーポンを使うと、髪の色が「ランダム」に変わります。それでも#b#t5151019##kを使って、ガラッと変えますか？",
                        "わかったわ…よく考えて、それでも変えたかったら話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪の色はどう？完璧な仕上がりとは言えないかもしれないけど、なかなかいいじゃない？",
                        "うーん…当店指定のクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を染められないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 1, 7, 3, 5, 6}
                        })
        );
    }
}
