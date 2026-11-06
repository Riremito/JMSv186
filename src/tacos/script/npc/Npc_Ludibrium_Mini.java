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
public class Npc_Ludibrium_Mini extends NpcSalon {

    private static final int item_coupon_5150006 = 5150006; // 調髪一般券（ルディブリアム）
    private static final int item_coupon_5151006 = 5151006; // 染毛一般券（ルディブリアム）

    public Npc_Ludibrium_Mini() {
        super(
                null,
                // 翻訳
                "こんにちは、私はここのアシスタントよ。心配しないで、腕は確かだから。もし#b#t5150006##kか#b#t5151006##kを持っていたら、あとは私に任せてみない？",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（一般クーポン）",
                        KIND_HAIR, item_coupon_5150006, true,
                        "一般クーポンを使うと、髪型がランダムに新しいスタイルに変わるわ。本当に#b#t5150006##kを使って、ガラッと変える？",
                        "わかったわ。じっくり考えて、それでもやりたかったら気軽に話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪型はどう？最高にイケてるとは言えないかもしれないけど、なかなかいいじゃない？また変えたくなったら寄ってね。",
                        "うーん…正しいクーポンを持っていないみたいね。残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30540, // タワー(黒)
                                30640, // シャピノ(黒)
                                30680, // ピンアップ(黒)
                                30250, // アフロ(黒)
                                30190, // パンプキン(黒)
                                30150, // もみあげ(黒)
                                30050, // プリンス(黒)
                                30280, // モヒカンシャギー(黒)
                                30240, // 栗(黒)
                                30300, // ロマンス(黒)
                                30160, // サンダー(黒)
                                30650 // テクスチャー(黒)
                            },
                            // 女性
                            {
                                31540, // ジーン(黒)
                                31640, // プティ(黒)
                                31600, // キューアップ(黒)
                                31150, // ストレート(黒)
                                31280, // ソフトウルフ(黒)
                                31160, // 編みキュート(黒)
                                31120, // 片編み(黒)
                                31290, // クール(黒)
                                31270, // アン(黒)
                                31030, // 三つ編み(黒)
                                31230, // ローズマリー(黒)
                                31010, // ウインド(黒)
                                31680 // 撫子(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151006, true,
                        "一般クーポンを使うと、髪の色がランダムに変わるわ。本当に#b#t5151006##kを使って、ガラッと変える？",
                        "わかったわ。じっくり考えて、それでもやりたかったら気軽に話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪の色はどう？最高にイケてるとは言えないかもしれないけど、なかなかいいじゃない？また変えたくなったら寄ってね。",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を染められないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 2, 7, 3, 5, 4}
                        })
        );
    }
}
