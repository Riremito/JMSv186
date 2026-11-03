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
public class NpcDonGiovanni extends NpcSalon {

    private static final int item_coupon_5150003 = 5150003; // 調髪特別券(カニング)
    private static final int item_coupon_5151003 = 5151003; // 染毛特別券(カニング)

    public NpcDonGiovanni() {
        super(
                null,
                // 翻訳
                "やあ！俺は#p1052100#、このヘアサロンのオーナーさ！#b#t5150003##kか#b#t5151003##kを持っているなら、あとは俺に任せてくれないか？髪をどうするか決めてくれ…",
                new SalonEntry(
                        // 翻訳
                        "スタイル変更（VIPクーポン）",
                        KIND_HAIR, item_coupon_5150003, false,
                        "君の髪型をまったく新しいものに変えてあげられるよ。今の髪型に飽きてないかい？#b#t5150003##kがあれば、新しい髪型にしてあげよう。好みのスタイルを選んでくれ。",
                        null,
                        "よし、新しい髪型を見てごらん。どうだい？我ながら芸術作品だと思うよ！ははは。新しい髪型にしたくなったら俺のところに来な。あとは任せてくれ！",
                        "うーん…正しいクーポンを持っていないみたいだね…残念だけど、クーポンがないと髪を切れないんだ。悪いな、相棒。",
                        null,
                        new int[][]{
                            // 男性
                            {30030, 30020, 30000, 30780, 30130, 30350, 30190, 30110, 30180, 30050, 30040, 30160},
                            // 女性
                            {31050, 31040, 31000, 31760, 31060, 31090, 31330, 31020, 31130, 31120, 31140, 31010}
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（VIPクーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151003, false,
                        "君の髪の色をまったく新しいものに変えてあげられるよ。今の髪に飽きてないかい？#b#t5151003##kがあれば、髪を染めてあげよう。好みの色を選んでくれ！",
                        null,
                        "よし、新しい髪の色を見てごらん。どうだい？我ながら芸術作品だと思うよ！ははは。新しい髪型にしたくなったら俺のところに来な。あとは任せてくれ！",
                        "うーん…正しいクーポンを持っていないみたいだね…残念だけど、クーポンがないと髪を染められないんだ。悪いな、相棒。",
                        null,
                        new int[][]{
                            {0, 2, 7, 3, 5}
                        })
        );
    }
}
