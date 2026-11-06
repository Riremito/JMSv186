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
public class Npc_Henesys_Brittany extends NpcSalon {

    private static final int item_coupon_5150000 = 5150000; // 調髪一般券(ヘネシス)
    private static final int item_coupon_5151000 = 5151000; // 染毛一般券(ヘネシス)

    public Npc_Henesys_Brittany() {
        super(
                null,
                // 翻訳
                "私はアシスタントのブリトニー。もし#b#t5150000##kか#b#t5151000##kを持っていたら、髪型を変えさせてくれない？",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（一般クーポン）",
                        KIND_HAIR, item_coupon_5150000, true,
                        "一般クーポンを使うと、髪型が「ランダム」に変わるわ。自分でも思いもよらなかった新しいスタイルになるかもしれないわよ。#b#t5150000##kを使って、本当に髪型を変える？",
                        "わかったわ…よく考えて、それでも変えたかったら、また話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪型はどう？最高にイケてるとは言えないかもしれないけど、なかなかいいじゃない？また変えたくなったら来てね！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30000, // かりあげ(黒)
                                30020, // 長髪(黒)
                                30030, // 丸坊主(黒)
                                30060, // ロンゲ(黒)
                                30120, // 学生カット(黒)
                                30140, // まげ(黒)
                                30150, // もみあげ(黒)
                                30200, // ウィンド(黒)
                                30210, // タフガイ(黒)
                                30310, // プリン(黒)
                                30330, // ストーム(黒)
                                30410 // ストローク(黒)
                            },
                            // 女性
                            {
                                31000, // キューティー(黒)
                                31030, // 三つ編み(黒)
                                31040, // パンク(黒)
                                31050, // ショート(黒)
                                31080, // フロント(黒)
                                31070, // パンクパーマ(黒)
                                31100, // フラワーピン(黒)
                                31150, // ストレート(黒)
                                31160, // 編みキュート(黒)
                                31300, // ブレイズ(黒)
                                31310, // バニラ(黒)
                                31410 // ロングウルフ(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151000, true,
                        "一般クーポンを使うと、髪の色が「ランダム」に変わるわ。それでも#b#t5151000##kを使って、ガラッと変える？",
                        "わかったわ…よく考えて、それでも変えたかったら、また話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪の色はどう？最高にイケてるとは言えないかもしれないけど、なかなかいいじゃない？",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を染められないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 1, 6, 2, 7, 4}
                        })
        );
    }
}
