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
public class Npc_Zipangu_Tepei extends NpcSalon {

    private static final int item_coupon_5150009 = 5150009; // 調髪特別券(ショーワ)
    private static final int item_coupon_5151009 = 5151009; // 染毛特別券(ショーワ)

    public Npc_Zipangu_Tepei() {
        super(
                null,
                // 翻訳
                "こんにちは！私はこのヘアサロンのオーナーです。お会いできてうれしいです！#b#t5150009##kか#b#t5151009##kを持っているなら、あとは私に任せてみませんか？髪をどうするか決めてください… ",
                new SalonEntry(
                        // 翻訳
                        "スタイル変更（VIPクーポン）",
                        KIND_HAIR, item_coupon_5150009, false,
                        "あなたの髪型をまったく新しいものに変えてあげられますよ。今の髪型に飽きていませんか？#b#t5150009##kがあれば、それを叶えてあげます。なりたいヘアスタイルを選んでください。",
                        null,
                        "さあ、見てください！新しい髪型ですよ！どうですか？この道では私が一番ですからね、あははは！今の髪型に飽きたら、誰に話しかければいいかわかりますよね！",
                        "え？当店指定のヘアサロンクーポンをお持ちでないようですね。クーポンがないとヘアカットはできません。",
                        null,
                        new int[][]{
                            // 男性
                            {30000, 30120, 30140, 30190, 30210, 30270, 30290, 30360, 30220, 30370, 30400, 30440, 30510},
                            // 女性
                            {31030, 31050, 31070, 31100, 31120, 31130, 31250, 31340, 31210, 31350, 31400, 31440, 31520}
                        }),
                new SalonEntry(
                        // 翻訳
                        "ヘアカラー（VIPクーポン）",
                        KIND_HAIR_COLOR, item_coupon_5151009, false,
                        "あなたの髪の色をまったく新しいものに変えてあげられますよ。今の髪に飽きていませんか？#b#t5151009##kがあれば、それを叶えてあげます。なりたい髪の色を選んでください。",
                        null,
                        "さあ、見てください！新しい髪の色ですよ！どうですか？この道では私が一番ですからね、あははは！今の髪の色に飽きたら、誰に話しかければいいかわかりますよね！",
                        "え？当店指定のヘアサロンクーポンをお持ちでないようですね。クーポンがないと髪を染められません。",
                        null,
                        new int[][]{
                            {0, 2, 7, 3, 5}
                        })
        );
    }
}
