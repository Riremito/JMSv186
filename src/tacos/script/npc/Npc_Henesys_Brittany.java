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
                            {30000, 30020, 30030, 30060, 30120, 30140, 30150, 30200, 30210, 30310, 30330, 30410},
                            // 女性
                            {31000, 31030, 31040, 31050, 31080, 31070, 31100, 31150, 31160, 31300, 31310, 31410}
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
