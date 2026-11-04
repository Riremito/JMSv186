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
public class Npc_Orbis_Rinz extends NpcSalon {

    private static final int item_coupon_5150004 = 5150004; // 調髪一般券(オルビス)
    private static final int item_coupon_5151004 = 5151004; // 染毛一般券(オルビス)

    public Npc_Orbis_Rinz() {
        super(
                null,
                // 翻訳
                "私はアシスタントの#p2012007#。#b#t5150004##kか#b#t5151004##kを持ってる？持ってるなら、髪を私に任せてみない？髪をどうしたい？",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（一般クーポン）",
                        KIND_HAIR, item_coupon_5150004, true,
                        "一般クーポンを使うと、髪型が「ランダム」に変わるわ。自分でも思いもよらなかった新しいスタイルになるかもしれないわよ。それでも#b#t5150004##kを使って髪型を変える？",
                        "わかったわ…よく考えて、それでも変えたかったら、また話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪型はどう？最高にイケてるとは言えないかもしれないけど、なかなかいいじゃない？また変えたくなったら寄ってね。",
                        "うーん…正しいクーポンを持っていないみたいね。残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {30000, 30020, 30030, 30230, 30240, 30260, 30270, 30280, 30290, 30340, 30610, 30440, 30400},
                            // 女性
                            {31000, 31030, 31040, 31110, 31220, 31230, 31240, 31250, 31260, 31270, 31320, 31700, 31620, 31610}
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151004, true,
                        "一般クーポンを使うと、髪の色がランダムに変わるわ。それでも#b#t5151004##kを使って、ガラッと変える？",
                        "わかったわ…よく考えて、それでも変えたかったら、また話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪の色はどう？最高にイケてるとは言えないかもしれないけど、なかなかいいじゃない？また見た目を変えたくなったら寄ってね。",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を染められないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 1, 7, 3, 4, 5}
                        })
        );
    }
}
