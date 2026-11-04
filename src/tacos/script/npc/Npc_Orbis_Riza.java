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
public class Npc_Orbis_Riza extends NpcSalon {

    private static final int item_coupon_5152004 = 5152004; // 整形外科一般券(オルビス)

    public Npc_Orbis_Riza() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152004, true,
                        "一般クーポンを使うと、どんな顔になるかわかりません…それでも#b#t5152004##kを使いますか？",
                        "なるほど…本当にそれでいいのか、少し考えてみてください。決まったら教えてくださいね。",
                        "施術が終わりました…ご覧ください、鏡をどうぞ…いかがですか？我ながら芸術作品だと思いますよ…ははは、新しい顔に飽きたら、また呼んでくださいね？",
                        "うーん…ここで使えるクーポンをお持ちでないようですね…申し訳ありませんが、クーポンがないと整形はできません。",
                        null,
                        new int[][]{
                            // 男性
                            {20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20012, 20014},
                            // 女性
                            {21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21012, 21014}
                        })
        );
    }
}
