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
public class Npc_Ludibrium_Miyu extends NpcSalon {

    private static final int item_coupon_5150007 = 5150007; // 調髪特別券（ルディブリアム）
    private static final int item_coupon_5151007 = 5151007; // 染毛特別券　（ルディブリアム）

    public Npc_Ludibrium_Miyu() {
        super(
                null,
                // 翻訳
                "#Gいらっしゃい:いらっしゃい#、#Gいらっしゃい:いらっしゃい#、ルディブリアム美容室へ#Gようこそ:ようこそ#！もしかして#b#t5150007##kか#b#t5151007##kを持ってる？持ってるなら、髪を私に任せてみない？髪をどうするか決めてね…",
            // TODO : スタイル変更（VIP会員クーポン） (5420005) は会員制クーポンのため未実装
                new SalonEntry(
                        // 翻訳
                        "スタイル変更（VIPクーポン）",
                        KIND_HAIR, item_coupon_5150007, false,
                        "あなたの髪型をガラッと変えてあげられるわ。イメチェンする#G準備:準備#はできてない？#b#t5150007##kがあれば、あとは私に任せて。好みのスタイルを選んでね…",
                        null,
                        "ほら見て！！どう？我ながら芸術作品だと思うわ！ははは。新しい髪型にしたくなったら私のところに来てね。いつでも素敵にしてあげるから！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                30030, // 丸坊主(黒)
                                30020, // 長髪(黒)
                                30000, // かりあげ(黒)
                                30660, // ショートウルフ(黒)
                                30250, // アフロ(黒)
                                30190, // パンプキン(黒)
                                30150, // もみあげ(黒)
                                30050, // プリンス(黒)
                                30280, // モヒカンシャギー(黒)
                                30240, // 栗(黒)
                                30300, // ロマンス(黒)
                                30160 // サンダー(黒)
                            },
                            // 女性
                            {
                                31040, // パンク(黒)
                                31000, // キューティー(黒)
                                31550, // チャイナB(黒)
                                31150, // ストレート(黒)
                                31280, // ソフトウルフ(黒)
                                31160, // 編みキュート(黒)
                                31120, // 片編み(黒)
                                31290, // クール(黒)
                                31270, // アン(黒)
                                31030, // 三つ編み(黒)
                                31230, // ローズマリー(黒)
                                31010 // ウインド(黒)
                            }
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（VIPクーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151007, false,
                        "あなたの髪の色をガラッと変えてあげられるわ。イメチェンする#G準備:準備#はできてない？#b#t5151007##kがあれば、あとは私に任せて。好みの色を選んでね！",
                        null,
                        "ほら見て！！どう？我ながら芸術作品だと思うわ！ははは。新しい髪型にしたくなったら私のところに来てね。いつでも素敵にしてあげるから！",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 2, 7, 3, 5, 4}
                        })
        );
    }
}
