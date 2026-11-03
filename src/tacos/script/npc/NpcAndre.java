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
public class NpcAndre extends NpcSalon {

    private static final int item_coupon_5150002 = 5150002; // 調髪一般券(カニング)
    private static final int item_coupon_5151002 = 5151002; // 染毛一般券(カニング)

    public NpcAndre() {
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
                            {30000, 30020, 30030, 30040, 30050, 30110, 30130, 30160, 30180, 30190, 30350, 30610, 30440, 30400},
                            // 女性
                            {31000, 31010, 31020, 31040, 31050, 31060, 31090, 31120, 31130, 31140, 31330, 31700, 31620, 31610}
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
