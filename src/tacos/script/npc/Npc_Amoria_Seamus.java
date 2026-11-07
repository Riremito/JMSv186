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
public class Npc_Amoria_Seamus extends NpcSalon {

    private static final int item_coupon_5150019 = 5150019; // 調髪一般券(ウェディング)
    private static final int item_coupon_5151016 = 5151016; // 染毛一般券(ウェディング)

    public Npc_Amoria_Seamus() {
        super(
                null,
                // 翻訳
                "調子はどうだい？試してみたい新しいヘアスタイルがいくつかあるんだ…どうだい？#b#t5150019##kか#b#t5151016##kを持っているなら、髪型を変えさせてくれ…",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（EXPクーポン）",
                        KIND_HAIR, item_coupon_5150019, true,
                        "EXPクーポンを使うと、髪型が「ランダム」に変わるんだ。俺が考えた実験的な新スタイルになるかもしれないよ。#b#t5150019##kを使って、本当に髪型を変えるかい？",
                        "なるほど…もう少し考えて、やりたくなったら来てくれ。",
                        "はい、鏡をどうぞ。新しい髪型だ！どうだい？最高にイケてるとは言えないけど、なかなかクールだと思うよ！また変えたくなったら来てくれ！",
                        "うーん…本当に正しいクーポンを持ってるかい？悪いけど、クーポンがないとヘアカットはできないよ。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30570, // エレガンス2(黒)
                                30450, // リミックス(黒)
                                30410, // ストローク(黒)
                                30200, // ウィンド(黒)
                                30050, // プリンス(黒)
                                30230, // ショック(黒)
                                30290, // シチサン(黒)
                                30300, // ロマンス(黒)
                                30250, // アフロ(黒)
                                30690 // メトロ(黒)
                            },
                            // 女性
                            {
                                31570, // メイド(黒)
                                31480, // ショートウェーブ(黒)
                                31150, // ストレート(黒)
                                31160, // 編みキュート(黒)
                                31020, // ウェーブ(黒)
                                31260, // デイジー(黒)
                                31230, // ローズマリー(黒)
                                31220, // ニューゼン(黒)
                                31110, // ポニーテールB(黒)
                                31490 // アップ(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151016, true,
                        "#b#t5151016##kを使うと、髪の色がランダムに変わるよ。それでもヘアカラークーポン（EXP）を使って髪を染めるかい？",
                        "なるほど…もう少し考えて、やりたくなったら来てくれ。",
                        "はい、鏡をどうぞ。新しい髪型だ！どうだい？最高にイケてるとは言えないけど、なかなかクールだと思うよ！また変えたくなったら来てくれ！",
                        "うーん…本当に正しいクーポンを持ってるかい？悪いけど、クーポンがないとヘアカットはできないよ。",
                        null,
                        new int[][]{
                            // 男性
                            {0, 3, 5, 7, 4, 1},
                            // 女性
                            {0, 3, 5, 7, 1}
                        })
        );
    }
}
