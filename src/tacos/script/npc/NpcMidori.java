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
public class NpcMidori extends NpcSalon {

    private static final int item_coupon_5150008 = 5150008; // 調髪一般券(ショーワ)
    private static final int item_coupon_5151008 = 5151008; // 染毛一般券(ショーワ)

    public NpcMidori() {
        super(
                null,
                // 翻訳
                "こんにちは、私はここのアシスタントです。#b#t5150008##kか#b#t5151008##kを持っているなら、髪型を変えさせてください… ",
                new SalonEntry(
                        // 翻訳
                        "スタイル変更（一般クーポン）",
                        KIND_HAIR, item_coupon_5150008, true,
                        "一般クーポンを使うと、ランダムな新しい髪型に変わります。#b#t5150008##kを使って髪型を変えますか？",
                        "わかりました。準備ができたら話しかけてください。",
                        "さあ、鏡をどうぞ。新しい髪型はいかがですか？アシスタントの仕事にしてはなかなかでしょう？また変えたくなったら来てくださいね！",
                        "本当に申し訳ありませんが、当店指定のクーポンをお持ちでないようです。クーポンがないと髪を切れないんです。ごめんなさい。",
                        null,
                        new int[][]{
                            // 男性
                            {30000, 30120, 30140, 30190, 30210, 30270, 30290, 30360, 30220, 30370, 30400, 30440, 30440},
                            // 女性
                            {31030, 31050, 31070, 31100, 31120, 31130, 31250, 31340, 31210, 31350, 31400, 31440, 31520}
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（一般クーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151008, true,
                        "一般クーポンを使うと、髪の色が「ランダム」に変わります。#b#t5151008##kを使って髪を染めますか？",
                        "わかりました。準備ができたら話しかけてください。",
                        "さあ、鏡をどうぞ。新しい髪の色はいかがですか？アシスタントの仕事にしてはなかなかでしょう？また変えたくなったら来てくださいね！",
                        "本当に申し訳ありませんが、当店指定のクーポンをお持ちでないようです。クーポンがないと髪を染められないんです。ごめんなさい。",
                        null,
                        new int[][]{
                            {0, 2, 7, 3, 5}
                        })
        );
    }
}
