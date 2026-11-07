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
// Quest.wz startscript : q20016s (JMS186), 暗黒の魔法使い, 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_DoYouKnowTheBlackMagician extends QuestScriptMan {

    private final int reward_exp = 380;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 7) {
            // TODO : 文章 (js の英文を翻訳)
            say("おや、まだ何か質問がありますか？もう一度話しかけてくれれば、最初から説明しますよ。", false, true);
            safeDispose();
            return true;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("こんにちは、#h0#。#p1101000#騎士団へようこそ。私は#p1101002#、幼い女王陛下の軍師を務めています。これからよく顔を合わせることになるでしょうから、仲良くしましょう。ははは！", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("あっという間の出来事だったので、わからないことがたくさんあるでしょう。ここがどこなのか、あなたがここで何をするのか、ひとつずつ説明しましょう。", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("この島はエレヴといいます。女王陛下の魔法のおかげで、この島は普段は空に浮かぶ船のようにメイプルワールドを巡回しています。ですが、今はわけあってここに停まっているのです。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("幼い女王陛下はメイプルワールドの統治者です。え？初めて聞いた？ああ、そうでしょうね。陛下はメイプルワールドの統治者ですが、世界を支配することを好みません。すべてが平穏であるよう、遠くから見守っておられるのです。少なくとも、それが普段の役目です。", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ですが、今はそうではありません。メイプルワールドのあちこちで、暗黒の魔法使いの復活を予感させる兆しが見つかっているのです。暗黒の魔法使いがよみがえって、かつてのようにメイプルワールドを恐怖に陥れるようなことがあってはなりません！", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ですが、それははるか昔のことで、今の人々は暗黒の魔法使いの恐ろしさを知りません。みんな今の平和なメイプルワールドに慣れきってしまい、かつてメイプルワールドがどれほど混乱し、恐ろしい場所だったかを忘れてしまったのです。このまま何もしなければ、暗黒の魔法使いが再びメイプルワールドを支配してしまうでしょう！", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("だからこそ、幼い女王陛下は自ら動くことを決意されたのです。暗黒の魔法使いを完全に倒すため、勇敢なメイプラーたちによる騎士団を結成されています。自分が何をすべきか、わかりますね？自ら騎士に志願したのですから、見当はついているでしょう。", true, true);
                return true;
            }
            case 7 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("暗黒の魔法使いが復活したときに倒せるよう、私たちは強くならなければなりません。最大の目標は、暗黒の魔法使いがメイプルワールドを滅ぼすのを防ぐことです。そして、あなたはそこで重要な役割を果たすことになります。", true, true);
                return true;
            }
            case 8 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("説明は以上です。疑問はすべて解けましたか？ \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#fUI/UIWindow.img/QuestIcon/8/0#" + this.reward_exp + " 経験値");
                return true;
            }
            case 9 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    gainExp(this.reward_exp);
                    forceCompleteQuest();
                }
                // TODO : 文章 (js の英文を翻訳)
                say("今の状況をわかってもらえてうれしいですが、今のあなたのレベルでは、暗黒の魔法使い本人どころか、その手下と戦うことすらできません。実のところ、手下の手下にもかなわないでしょう。今のレベルでどうやってメイプルワールドを守るのですか？", false, true);
                return true;
            }
            case 10 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("騎士団に入ることは認められましたが、まだ騎士として認められたわけではありません。見習い騎士ですらないのですから、正騎士でもありません。今のレベルのままでは、#p1101000#騎士団の雑用係にすぎませんよ。", true, true);
                return true;
            }
            case 11 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ですが、最初から強い騎士などいません。女王陛下は強い人を求めたのではありません。厳しい修行を通して強い騎士に育てられる、勇気ある人を求めたのです。ですから、まずは見習い騎士になってください。任務についてはそれから話しましょう。", true, true);
                return true;
            }
            case 12 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("左のポータルから修練の森へ行けます。そこにいる修練教官の#p1102000#が、強くなる方法を教えてくれるでしょう。レベル10になるまで、あてもなくうろうろしていてはいけませんよ、いいですね？", true, false);
                safeDispose();
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
