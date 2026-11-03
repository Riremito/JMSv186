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
public class NpcRomi extends NpcSalon {

    private static final int item_coupon_5153001 = 5153001; // スキンケア券(オルビス)

    public NpcRomi() {
        super(
                // 翻訳
                "あら、こんにちは！オルビスクリニックへ#Gようこそ:ようこそ#～！私のようなハリのある健康的な肌になりたくありませんか？#b#t5153001##kがあれば、私たちにすべてお任せいただいて、いつも憧れていた肌になれますよ～！",
                null,
                new SalonEntry(
                        null,
                        KIND_SKIN, item_coupon_5153001, false,
                        "当店の専用マシンなら、施術後の見た目を事前に確認できます。どんなスキンケアをご希望ですか？お好みのスタイルを選んでください…",
                        null,
                        "さあ、結果をご覧ください！いかがですか？思いどおりになりましたか？きっと本当に望みどおりになったと思います。また来てくださいね～",
                        "施術に必要なクーポンをお持ちでないようですね。申し訳ありませんが、施術はできないようです。",
                        null,
                        new int[][]{
                            {0, 1, 2, 3, 4}
                        })
        );
    }
}
