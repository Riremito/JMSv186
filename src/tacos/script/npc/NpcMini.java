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
public class NpcMini extends NpcSalon {

    private static final int item_coupon_5150006 = 5150006; // 調髪一般券（ルディブリアム）
    private static final int item_coupon_5151006 = 5151006; // 染毛一般券（ルディブリアム）

    public NpcMini() {
        super(
                null,
                // 翻訳
                "こんにちは、私はここのアシスタントよ。心配しないで、腕は確かだから。もし#b#t5150006##kか#b#t5151006##kを持っていたら、あとは私に任せてみない？",
                new SalonEntry(
                        // 翻訳
                        "ヘアカット（一般クーポン）",
                        KIND_HAIR, item_coupon_5150006, true,
                        "一般クーポンを使うと、髪型がランダムに新しいスタイルに変わるわ。本当に#b#t5150006##kを使って、ガラッと変える？",
                        "わかったわ。じっくり考えて、それでもやりたかったら気軽に話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪型はどう？最高にイケてるとは言えないかもしれないけど、なかなかいいじゃない？また変えたくなったら寄ってね。",
                        "うーん…正しいクーポンを持っていないみたいね。残念だけど、クーポンがないと髪を切れないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            // 男性
                            {30540, 30640, 30680, 30250, 30190, 30150, 30050, 30280, 30240, 30300, 30160, 30650},
                            // 女性
                            {31540, 31640, 31600, 31150, 31280, 31160, 31120, 31290, 31270, 31030, 31230, 31010, 31680}
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151006, true,
                        "一般クーポンを使うと、髪の色がランダムに変わるわ。本当に#b#t5151006##kを使って、ガラッと変える？",
                        "わかったわ。じっくり考えて、それでもやりたかったら気軽に話しかけてね。",
                        "はい、鏡をどうぞ。新しい髪の色はどう？最高にイケてるとは言えないかもしれないけど、なかなかいいじゃない？また変えたくなったら寄ってね。",
                        "うーん…正しいクーポンを持っていないみたいね…残念だけど、クーポンがないと髪を染められないの。ごめんなさいね。",
                        null,
                        new int[][]{
                            {0, 2, 7, 3, 5, 4}
                        })
        );
    }
}
