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
public class Npc_Zipangu_Saeko extends NpcSalon {

    private static final int item_coupon_5152008 = 5152008; // 整形外科一般券(ショーワ)

    public Npc_Zipangu_Saeko() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152008, true,
                        "一般クーポンを使うと、ランダムな新しい顔になるかもしれません。施術後どんな顔になるかは…完全に運次第です。それでも#b#t5152008##kを使いますか？",
                        "わかりました。ゆっくり考えて、それでもやりたければ教えてくださいね？",
                        "手術が完了しました。こちらが鏡です。悪くないでしょう？新しい顔に飽きたら、また寄ってくださいね。",
                        "当店指定の一般整形クーポンをお持ちでないようですね。申し訳ありませんが、一般クーポンがないと整形はできません。",
                        null,
                        new int[][]{
                            // 男性
                            {20002, 20003, 20004, 20007, 20008, 20009, 20010, 20011, 20013, 20016, 20017},
                            // 女性
                            {21003, 21003, 21004, 21005, 21008, 21009, 21010, 21011, 21013, 21016, 21017}
                        })
        );
    }
}
