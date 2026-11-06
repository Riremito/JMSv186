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

/**
 *
 * @author Riremito
 */
// Quest.wz startscript : q3941s, endscript : q3941e (JMS186), 王妃の絹を奪う, lua の処理を利用
// TODO : 変身状態 (getMorphState) が未実装のため、現状では進行できない
public class Quest_Ariant_StealingTheQueensSilk extends QuestScriptMan {

    private final int morph_required = 6; // lua の target.getMorphState == 6
    private final int item_silk = 4031571; // 王妃が注文した絹

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 2) {
            // 翻訳
            say("#p2101004#様ではないですって？いやいや、どこからどう見ても#p2101004#様なのに、一体何をおっしゃっているのか…本当にお加減が悪いようですね。絹はお元気になられたらお渡ししますので、また来てください。");
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getMorphState() != this.morph_required) {
                    // 翻訳
                    say("本当に暑いですね？でも、暑くてこそ本物の砂漠と言えるんじゃないでしょうか？それにしても、#p2101004#様はいつ来られるのやら？");
                    return false;
                }
                // 翻訳
                askMenu("…#p2101004#様ではありませんか？お久しぶりです。今回は王妃様が首を長くして探していらした絹を、幸いにも手に入れておきました。いつもどおり品物は最高級…ところで、どうしてそんなに汗をかいていらっしゃるのですか？\r\n#L0##b（声色を変えて）いや、太陽が暑くてな…#l");
                return true;
            }
            case 1 -> {
                // 翻訳
                askMenu("アリアントが暑くなかったことなんてありましたか？いつもこうですが、#p2101004#様は暑さに強い方だと思っていましたが…あれ、だんだんお顔が赤くなっていきますよ。大丈夫ですか？\r\n#L0##b（声色を変えて）だ、大丈夫だ。心配するな…#l");
                return true;
            }
            case 2 -> {
                // 翻訳
                askMenu("本当に大丈夫なのですか？ただでさえ#p2101004#様は虚弱体質のようですし、やはり薬でも飲まれた方がいいのでは？エルナスの方で手に入れた風邪薬があるのですが、おひとついかがですか？お安くしますよ。\r\n#L0##bだ、大丈夫だと言っているだろう！#l");
                return true;
            }
            case 3 -> {
                // 翻訳
                askAccept("大丈夫ですって？でも、#p2101004#様のお声がいつもとずいぶん違いますよ？本当に風邪をひかれたのでは？どうもいつもの#p2101004#様らしくありません。いつもは#t4010007#を安くしろとばかりおっしゃるのに…おかしいですね。本当に#p2101004#様ですか？");
                return true;
            }
            case 4 -> {
                forceStartQuest();
                // 翻訳
                say("いつもの#p2101004#様らしくありません。#p2101004#様はこんなに口数の少ない方ではなかったと思いますが…何かあったのですか？はっ。お顔がだんだん赤くなっていくのは、どうやらお怒りのようですね。申し訳ありません。すぐに絹を持ってまいります。少々お待ちください。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                if (getMorphState() != this.morph_required) {
                    // 翻訳
                    say("本当に暑くありませんか？喉がからからに渇きますね。");
                    return false;
                }
                // 翻訳
                say("さあ、どうぞ。気をつけて持っていってください。この絹は手に入れるのがとても難しい品です。少しでも破れたところがあったら、王妃様はすぐに#p2101004#様を牢屋に入れてしまわれるでしょう。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_silk + "# #t" + this.item_silk + "# 1個\r\n\r\n", false, true);
                return true;
            }
            case 1 -> {
                if (!exchange(0, this.item_silk, 1)) {
                    // 翻訳
                    say("インベントリの荷物が多くて、絹を入れられませんよ？大事な品を運ぶのですから、少しだけ空きを作ってください。");
                    return false;
                }
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
