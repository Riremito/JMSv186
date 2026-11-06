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
public class Npc_Kerning_Andre extends NpcSalon {

    private static final int item_coupon_5150002 = 5150002; // 調髪一般券(カニング)
    private static final int item_coupon_5151002 = 5151002; // 染毛一般券(カニング)

    public Npc_Kerning_Andre() {
        super(
                null,
                // 翻訳
                "俺は#p1052101#、#p1052100#のアシスタントさ。#b#t5150002##kか#b#t5151002##kを持っているなら、髪型を変えさせてくれ…",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（一般クーポン）",
                        KIND_HAIR, item_coupon_5150002, true,
                        "一般クーポンを使うと、髪型が「ランダム」に変わるんだ。自分でも思いもよらなかった新しいスタイルになるかもしれないよ。#b#t5150002##kを使って、本当に髪型を変えるかい？",
                        "なるほど…もう少し考えて、やりたくなったら来てくれ。",
                        "はい、鏡をどうぞ。新しい髪型だ！どうだい？最高にイケてるとは言えないけど、なかなかクールだと思うよ！また変えたくなったら来てくれ！",
                        "うーん…本当に正しいクーポンを持ってるかい？悪いけど、クーポンがないとヘアカットはできないよ。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30000, // かりあげ(黒)
                                30020, // 長髪(黒)
                                30030, // 丸坊主(黒)
                                30040, // 短髪(灰)
                                30050, // プリンス(黒)
                                30110, // とんがり(黒)
                                30130, // パーマ(黒)
                                30160, // サンダー(黒)
                                30180, // ライオンA(黒)
                                30190, // パンプキン(黒)
                                30350, // ワイルド(黒)
                                30610, // ロックスター(黒)
                                30440, // スイング(黒)
                                30400 // タトゥー(黒)
                            },
                            // 女性
                            {
                                31000, // キューティー(黒)
                                31010, // ウインド(黒)
                                31020, // ウェーブ(黒)
                                31040, // パンク(黒)
                                31050, // ショート(黒)
                                31060, // シック(黒)
                                31090, // ポニーテールA(黒)
                                31120, // 片編み(黒)
                                31130, // 両編み(黒)
                                31140, // チャイナA(黒)
                                31330, // マジック(黒)
                                31700, // キューティーベビー（黒）
                                31620, // オッキード(黒)
                                31610 // ラデア(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151002, true,
                        "一般クーポンを使うと、髪の色がランダムに変わるんだ。それでも#b#t5151002##kを使って髪を染めるかい？",
                        "なるほど…もう少し考えて、やりたくなったら来てくれ。",
                        "はい、鏡をどうぞ。新しい髪型だ！どうだい？最高にイケてるとは言えないけど、なかなかクールだと思うよ！また変えたくなったら来てくれ！",
                        "うーん…本当に正しいクーポンを持ってるかい？悪いけど、クーポンがないとヘアカットはできないよ。",
                        null,
                        new int[][]{
                            {0, 2, 7, 3, 5}
                        })
        );
    }
}
