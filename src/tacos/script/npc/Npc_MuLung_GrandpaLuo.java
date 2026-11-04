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
public class Npc_MuLung_GrandpaLuo extends NpcSalon {

    private static final int item_coupon_5150025 = 5150025; // 調髪特別券(武陵)
    private static final int item_coupon_5151020 = 5151020; // 染毛特別券(武陵)

    public Npc_MuLung_GrandpaLuo() {
        super(
                null,
                // 翻訳
                "武陵ヘアショップへようこそ。#b#t5150025##kか#b#t5151020##kを持っているなら、ヘアスタイルは私にお任せください。何にするか選んでください。",
            // TODO : スタイル変更（VIP会員クーポン） (5420006) は会員制クーポンのため未実装
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（VIPクーポン）",
                        KIND_HAIR, item_coupon_5150025, false,
                        "あなたの髪型をガラッと変えて、素敵にしてあげられますよ。ちょっとイメチェンしてみませんか？変えてあげますよ。#b#t5150025##kで好きなのを選んでください",
                        null,
                        "はい、鏡をどうぞ。新しい髪型はどう？完璧な仕上がりとは言えないかもしれないけど、なかなかいいじゃない？また変えたくなったら来てね！",
                        "うーん…当店指定のクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {30600, 30750, 30250, 30150, 30240, 30350, 30180, 30300, 30270, 30160},
                            // 女性
                            {31460, 31040, 31180, 31310, 31300, 31160, 31030, 31250, 31220, 31230}
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（VIPクーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151020, false,
                        "あなたの髪の色をガラッと変えて、素敵にしてあげられますよ。ちょっとイメチェンしてみませんか？#b#t5151020##kで変えてあげますよ。好きなのを選んでください。",
                        null,
                        "はい、鏡をどうぞ。新しい髪の色はどう？完璧な仕上がりとは言えないかもしれないけど、なかなかいいじゃない？",
                        "うーん…当店指定のクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を染められないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 1, 7, 3, 5, 6}
                        })
        );
    }
}
