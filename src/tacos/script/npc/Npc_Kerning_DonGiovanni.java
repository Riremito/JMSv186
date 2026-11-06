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
public class Npc_Kerning_DonGiovanni extends NpcSalon {

    private static final int item_coupon_5150003 = 5150003; // 調髪特別券(カニング)
    private static final int item_coupon_5151003 = 5151003; // 染毛特別券(カニング)

    public Npc_Kerning_DonGiovanni() {
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
                            {
                                30030, // 丸坊主(黒)
                                30020, // 長髪(黒)
                                30000, // かりあげ(黒)
                                30780, // ブレード(黒)
                                30130, // パーマ(黒)
                                30350, // ワイルド(黒)
                                30190, // パンプキン(黒)
                                30110, // とんがり(黒)
                                30180, // ライオンA(黒)
                                30050, // プリンス(黒)
                                30040, // 短髪(灰)
                                30160 // サンダー(黒)
                            },
                            // 女性
                            {
                                31050, // ショート(黒)
                                31040, // パンク(黒)
                                31000, // キューティー(黒)
                                31760, // ストレートツインテールA(黒)
                                31060, // シック(黒)
                                31090, // ポニーテールA(黒)
                                31330, // マジック(黒)
                                31020, // ウェーブ(黒)
                                31130, // 両編み(黒)
                                31120, // 片編み(黒)
                                31140, // チャイナA(黒)
                                31010 // ウインド(黒)
                            }
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
