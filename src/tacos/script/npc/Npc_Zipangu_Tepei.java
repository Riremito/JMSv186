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
                            {
                                30000, // かりあげ(黒)
                                30120, // 学生カット(黒)
                                30140, // まげ(黒)
                                30190, // パンプキン(黒)
                                30210, // タフガイ(黒)
                                30270, // イガグリ(黒)
                                30290, // シチサン(黒)
                                30360, // バンビ(黒)
                                30220, // リーゼント(黒)
                                30370, // パイロット(黒)
                                30400, // タトゥー(黒)
                                30440, // スイング(黒)
                                30510 // ロキ(黒)
                            },
                            // 女性
                            {
                                31030, // 三つ編み(黒)
                                31050, // ショート(黒)
                                31070, // パンクパーマ(黒)
                                31100, // フラワーピン(黒)
                                31120, // 片編み(黒)
                                31130, // 両編み(黒)
                                31250, // オカッパ(黒)
                                31340, // リプル(黒)
                                31210, // 小町(黒)
                                31350, // クローバー(黒)
                                31400, // ボーイッシュ(黒)
                                31440, // チャーミング(黒)
                                31520 // ストリーム(黒)
                            }
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
