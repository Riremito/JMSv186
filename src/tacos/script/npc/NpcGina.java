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
public class NpcGina extends NpcSalon {

    private static final int item_coupon_5153002 = 5153002; // スキンケアークーポン（ルディブリアム）

    public NpcGina() {
        super(
                // 翻訳
                "あら、こんにちは！ルディブリアムクリニックへ#Gようこそ:ようこそ#！日焼けしたセクシーな肌に興味はありますか？それとも白くて美しい肌はいかがですか？#b#t5153002##kがあれば、私たちにすべてお任せいただいて、夢見ていた肌になれますよ～！",
                null,
                new SalonEntry(
                        null,
                        KIND_SKIN, item_coupon_5153002, false,
                        "当店の専用マシンなら、施術の「前」に、施術後の見た目を確認できます。どんな見た目をご希望ですか？お好みのスタイルを選んでくださいね～",
                        null,
                        "さあ、鏡を見てください！いかがですか？気に入りましたか？きっと望みどおりになったと思います。また来てくださいね～",
                        "うーん…今は当店のスキンケアクーポンをお持ちでないようですね。クーポンがないと、申し訳ありませんが施術はできません。",
                        null,
                        new int[][]{
                            {0, 1, 2, 3, 4}
                        })
        );
    }
}
