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
public class Npc_Amoria_Julius extends NpcSalon {

    private static final int item_coupon_5150020 = 5150020; // 調髪特別券(ウェディング)
    private static final int item_coupon_5151017 = 5151017; // 染毛特別券(ウェディング)

    public Npc_Amoria_Julius() {
        super(
                null,
                // 翻訳
                "いらっしゃい！僕の名前は#p9201015#。#b#t5150020##kか#b#t5151017##kを持っているなら、バルログもびっくりして倒れるようなヘアチェンジをしてあげるよ！どのスタイルにする？ ",
            // TODO : スタイル変更（VIP会員クーポン） (5420000) は会員制クーポンのため未実装
                new SalonEntry(
                        // 翻訳
                        "スタイル変更（VIPクーポン）",
                        KIND_HAIR, item_coupon_5150020, false,
                        "君の髪型をまったく新しいものに変えてあげられるよ。今の髪型に飽きてないかい？#b#t5150020##kがあれば、新しい髪型にしてあげよう。好みのスタイルを選んでくれ。",
                        null,
                        "よし、新しい髪型を見てごらん。どうだい？我ながら芸術作品だと思うよ！ははは。新しい髪型にしたくなったら俺のところに来な。あとは任せてくれ！",
                        "うーん…正しいクーポンを持っていないみたいだね。クーポンがないと髪を切れないんだ。悪いな、相棒。",
                        null,
                        new int[][]{
                            // 男性
                            {30580, 30590, 30280, 30670, 30410, 30200, 30050, 30230, 30290, 30300, 30250},
                            // 女性
                            {31580, 31590, 31310, 31200, 31150, 31160, 31020, 31260, 31230, 31220, 31110}
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（VIPクーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151017, false,
                        "君の髪の色をまったく新しいものに変えてあげられるよ。今の髪に飽きてないかい？#b#t5151017##kがあれば、髪を染めてあげよう。好みの色を選んでくれ！",
                        null,
                        "よし、新しい髪の色を見てごらん。どうだい？我ながら芸術作品だと思うよ！ははは。新しい髪型にしたくなったら俺のところに来な。あとは任せてくれ！",
                        "うーん…正しいクーポンを持っていないみたいだね…残念だけど、クーポンがないと髪を染められないんだ。悪いな、相棒。",
                        null,
                        new int[][]{
                            {0, 3, 5, 7, 4, 1}
                        })
        );
    }
}
