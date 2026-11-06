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
public class Npc_Zipangu_Midori extends NpcSalon {

    private static final int item_coupon_5150008 = 5150008; // 調髪一般券(ショーワ)
    private static final int item_coupon_5151008 = 5151008; // 染毛一般券(ショーワ)

    public Npc_Zipangu_Midori() {
        super(
                null,
                // 翻訳
                "こんにちは、私はここのアシスタントです。#b#t5150008##kか#b#t5151008##kを持っているなら、髪型を変えさせてください… ",
                new SalonEntry(
                        // 翻訳
                        "スタイル変更（一般クーポン）",
                        KIND_HAIR, item_coupon_5150008, true,
                        "一般クーポンを使うと、ランダムな新しい髪型に変わります。#b#t5150008##kを使って髪型を変えますか？",
                        "わかりました。準備ができたら話しかけてください。",
                        "さあ、鏡をどうぞ。新しい髪型はいかがですか？アシスタントの仕事にしてはなかなかでしょう？また変えたくなったら来てくださいね！",
                        "本当に申し訳ありませんが、当店指定のクーポンをお持ちでないようです。クーポンがないと髪を切れないんです。ごめんなさい。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30000, // かりあげ(黒)
                                30120, // 学生カット(黒)
                                30140, // まげ(黒)
                                30190, // パンプキン(黒)
                                30210, // タフガイ(黒)
                                30270, // イガグリ(黒)
                                30290, // シチサン(黒)
                                30360, // バンビ(黒)
                                30220, // リーゼント(黒)
                                30370, // パイロット(黒)
                                30400, // タトゥー(黒)
                                30440, // スイング(黒)
                                30440 // スイング(黒)
                            },
                            // 女性
                            {
                                31030, // 三つ編み(黒)
                                31050, // ショート(黒)
                                31070, // パンクパーマ(黒)
                                31100, // フラワーピン(黒)
                                31120, // 片編み(黒)
                                31130, // 両編み(黒)
                                31250, // オカッパ(黒)
                                31340, // リプル(黒)
                                31210, // 小町(黒)
                                31350, // クローバー(黒)
                                31400, // ボーイッシュ(黒)
                                31440, // チャーミング(黒)
                                31520 // ストリーム(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151008, true,
                        "一般クーポンを使うと、髪の色が「ランダム」に変わります。#b#t5151008##kを使って髪を染めますか？",
                        "わかりました。準備ができたら話しかけてください。",
                        "さあ、鏡をどうぞ。新しい髪の色はいかがですか？アシスタントの仕事にしてはなかなかでしょう？また変えたくなったら来てくださいね！",
                        "本当に申し訳ありませんが、当店指定のクーポンをお持ちでないようです。クーポンがないと髪を染められないんです。ごめんなさい。",
                        null,
                        new int[][]{
                            {0, 2, 7, 3, 5}
                        })
        );
    }
}
