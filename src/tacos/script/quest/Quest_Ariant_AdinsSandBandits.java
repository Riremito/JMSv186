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
package tacos.script.quest;

import tacos.server.TacosFieldSet;

/**
 *
 * @author Riremito
 */
// Quest.wz startscript : q3933s (JMS186), アディンの砂絵団, lua の処理を利用
// TODO : FieldSet "Adin" が FieldSet.img に存在しないため入場できない
public class Quest_Ariant_AdinsSandBandits extends QuestScriptMan {

    private final String field_set_adin = "Adin";

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            // 翻訳
            say("心の準備が必要なのか？あまり緊張するなよ。");
            return false;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("君がこんなに強いとは思わなかったよ。君ほどなら砂影団の団員になれるかもしれないって気がしてきたぞ？砂影団員にとって一番大切なのは強さで、君は十分強いみたいだからな。でも、やっぱりもう一度だけ試してみたいんだけど、どうだい？いいかな？", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                askAccept("君の本当の強さを確かめるには、やっぱり体でぶつかってみるしかないだろう？君と手合わせしてみたいんだ。心配するなって。君を傷つけるつもりはない。俺の分身が相手をしてやるよ。今すぐ手合わせを始めてもいいかい？");
                return true;
            }
            case 2 -> {
                // 翻訳
                say("よし。自信満々だな。");
                return true;
            }
            case 3 -> {
                int result = enterFieldSet(this.field_set_adin, 0);
                forceStartQuest();
                if (result != TacosFieldSet.ENTER_SUCCESS) {
                    // 翻訳
                    say("ああ…ちょっと待ってくれ。今、誰かが手合わせの場を使っているみたいだ。少ししてからまた来てくれ。");
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
