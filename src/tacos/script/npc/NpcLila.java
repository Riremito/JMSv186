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
// Npc.wz script : skin_ariant1 (JMS131-302), lua_script_jp の skin_ariant1 は中身がないため js の処理を利用
public class NpcLila extends NpcSalon {

    private static final int item_coupon_5153007 = 5153007; // スキンケアークーポン(アリアント)

    public NpcLila() {
        super(
                // TODO : 文章 (適当)
                "ほほほ～いらっしゃいませ。アリアントのスキンケアへようこそ。女王様もお通いになる有名なお店なんですよ。#b#t5153007##kをお持ちなら、あとはお任せください。今日はお肌の手入れをしていきませんか？",
                null,
                new SalonEntry(
                        null,
                        KIND_SKIN, item_coupon_5153007, false,
                        // TODO : 文章 (適当)
                        "当店の専用マシンなら、施術後の姿を前もって確認できます。どんな肌にしますか？お好きなものを選んでください…",
                        null,
                        // TODO : 文章 (適当)
                        "新しいお肌を楽しんでくださいね！",
                        // TODO : 文章 (適当)
                        "うーん…当店のスキンケアクーポンをお持ちでないようですね。クーポンがないと施術はできないんです。",
                        null,
                        new int[][]{
                            {0, 1, 2, 3, 4}
                        })
        );
    }
}
