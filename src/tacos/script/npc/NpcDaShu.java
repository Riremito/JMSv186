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
// Npc.wz script : 9310014 (CMS86-88, EMS76, MSEA102, TWMS77-125), lua と js が存在しないため mock として実装
// TODO : 選択可能なスタイルは不明 (仮)
public class NpcDaShu extends NpcSalon {

    private static final int item_coupon_5152017 = 5152017; // 豫園整形手術高級會員卡

    public NpcDaShu() {
        super(
                null,
                null,
                new SalonEntry(
                        null,
                        KIND_FACE, item_coupon_5152017, false,
                        // TODO : 文章 (適当)
                        "あなたの顔をまったく新しいものに変えてあげられますよ。#b#t5152017##kがあれば、お好みの顔を選んでください。",
                        null,
                        // TODO : 文章 (適当)
                        "はい、施術が終わりました。新しい顔を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…この病院のクーポンをお持ちでないようですね…クーポンがないと整形はできないんです。",
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
