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
                            {30030, 30020, 30000, 30660, 30250, 30190, 30150, 30050, 30280, 30240, 30300, 30160},
                            // 女性
                            {31040, 31000, 31550, 31150, 31280, 31160, 31120, 31290, 31270, 31030, 31230, 31010}
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
