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
public class Npc_Henesys_MsTan extends NpcSalon {

    private static final int item_coupon_5153000 = 5153000; // スキンケア券(ヘネシス)

    public Npc_Henesys_MsTan() {
        super(
                // 翻訳
                "あら、こんにちは！ヘネシスクリニックへ#Gようこそ:ようこそ#！私のようなハリのある健康的な肌になりたくありませんか？#b#t5153000##kがあれば、私たちにすべてお任せいただいて、いつも憧れていた肌になれますよ～！",
                null,
                new SalonEntry(
                        null,
                        KIND_SKIN, item_coupon_5153000, false,
                        "当店の専用マシンなら、施術後の見た目を事前に確認できます。どんなスキンケアをご希望ですか？お好みのスタイルを選んでください…",
                        null,
                        "さあ、鏡をどうぞ、見てください！  いかがですか？私みたいにきれいで輝く肌になったでしょう？うふふ～きっと気に入っていただけたと思います。また来てくださいね～",
                        "うーん…施術に必要なスキンケアクーポンをお持ちでないようですね。申し訳ありませんが、施術はできません。",
                        null,
                        new int[][]{
                            {0, 1, 2, 3, 4}
                        })
        );
    }
}
