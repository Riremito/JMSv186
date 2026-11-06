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
// Npc.wz script : face_ariant1 (JMS131-302), lua_script_jp の face_ariant1 は中身がないため js の処理を利用
public class Npc_Ariant_Vard extends NpcSalon {

    private static final int item_coupon_5152030 = 5152030; // 整形外科特別券(アリアント)

    public Npc_Ariant_Vard() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152030, false,
                        // TODO : 文章 (適当)
                        "ふむ…美しい顔は、覆いの下でも灼熱の砂漠でも輝くものだ。#b#t5152030##kがあれば、君の顔をもっと素敵にしてあげよう。なりたい顔を選んでくれれば、自慢の腕で生まれ変わらせてあげるよ。",
                        null,
                        // TODO : 文章 (適当)
                        "新しい顔を楽しんでくれ！",
                        // TODO : 文章 (適当)
                        "おや…この病院専用のクーポンを持っていないようだね。クーポンがないと施術はできないんだ。",
                        null,
                        new int[][]{
                            // 男性
                            {
                                20000, // 顔１♂
                                20001, // 顔２♂
                                20002, // 顔３♂
                                20003, // 顔４♂
                                20004, // 顔５♂
                                20005, // 顔６♂
                                20006, // 顔７♂
                                20007, // 顔８♂
                                20008, // 顔９♂
                                20012, // 顔１３♂
                                20014 // 顔１５♂
                            },
                            // 女性
                            {
                                21000, // 顔1♀
                                21001, // 顔２♀
                                21002, // 顔３♀
                                21003, // 顔４♀
                                21004, // 顔５♀
                                21005, // 顔６♀
                                21006, // 顔７♀
                                21007, // 顔８♀
                                21008, // 顔９♀
                                21012, // 顔１３♀
                                21014 // 顔１５♀
                            }
                        })
        );
    }
}
