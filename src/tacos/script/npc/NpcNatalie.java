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
public class NpcNatalie extends NpcSalon {

    private static final int item_coupon_5150001 = 5150001; // 調髪特別券(ヘネシス)
    private static final int item_coupon_5151001 = 5151001; // 染毛特別券(ヘネシス)

    public NpcNatalie() {
        super(
                null,
                // 翻訳
                "私は#p1012103#、このヘアサロンのオーナーよ。#b#t5150001##kか#b#t5151001##kを持っているなら、ヘアスタイルは私に任せて。何にするか選んでね。",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（VIPクーポン）",
                        KIND_HAIR, item_coupon_5150001, false,
                        "あなたの髪型を変えて、素敵にしてあげられるわ。ちょっとイメチェンしてみない？#b#t5150001##kがあれば、変えてあげるわよ。好きなのを選んでね。",
                        null,
                        "ほら見て！！どう？我ながら芸術作品だと思うわ！ははは。新しい髪型にしたくなったら私のところに来てね。いつでも素敵にしてあげるから！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {30030, 30020, 30000, 30480, 30310, 30330, 30060, 30150, 30410, 30210, 30140, 30120, 30200},
                            // 女性
                            {31050, 31040, 31000, 31700, 31150, 31310, 31300, 31160, 31100, 31410, 31030, 31080, 31070}
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（VIPクーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151001, false,
                        "あなたの髪型を変えて、素敵にしてあげられるわ。ちょっとイメチェンしてみない？#b#t5151001##kがあれば、変えてあげるわよ。好きなのを選んでね。",
                        null,
                        "ほら見て！！どう？我ながら芸術作品だと思うわ！ははは。新しい髪型にしたくなったら私のところに来てね。いつでも素敵にしてあげるから！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 1, 6, 2, 7, 4}
                        })
        );
    }
}
