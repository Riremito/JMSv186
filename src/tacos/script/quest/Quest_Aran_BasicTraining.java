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
// Quest.wz startscript : q21015s - q21018s (JMS186), 基礎体力鍛錬1, 基礎体力鍛錬2, 基礎体力鍛錬3, 基礎体力テスト, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_BasicTraining extends QuestScriptMan {

    private final int quest_training_1 = 21015; // 基礎体力鍛錬1
    private final int quest_training_2 = 21016; // 基礎体力鍛錬2
    private final int quest_training_3 = 21017; // 基礎体力鍛錬3

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_training_1) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == 1) {
                // TODO : 文章 (js の英文を翻訳)
                say("何をためらっているのですか？あなたは英雄なのですよ！鉄は熱いうちに打てと言うでしょう！さあ、始めましょう！", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say("さて、説明はこのくらいにしておきましょう。次の段階に進みますよ。次の段階は何かって？さっき言いましたよね。暗黒の魔法使いを一撃で倒せるくらい強くなるまで、全力で修行するのです。", false, true);
                    return true;
                }
                case 1 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say("あなたは昔は英雄だったかもしれませんが、それは何百年も前のことです。暗黒の魔法使いの呪いがなかったとしても、長い年月凍りついていたせいで体が固まってしまっています。少しずつ体をほぐして、素早さを取り戻さなければなりません。どうやってって？", true, true);
                    return true;
                }
                case 2 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("何事もまずは基本からだということを知らないのですか？ですから、#b基礎体力鍛錬#kから始めるのが賢明です。ああ、そういえば記憶をなくしているのでしたね。だからこそ私がいるのです。実際に体験してもらうしかありません。始めましょうか？");
                    return true;
                }
                case 3 -> {
                    forceStartQuest();
                    avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        if (getQuestId() == this.quest_training_2) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == -1) {
                // TODO : 文章 (js の英文を翻訳)
                say("まだ#o0100132#を狩る準備ができていないのですか？準備が完全に整ってから進むようにしてください。準備不足のまま戦いに挑むことほど悪いことはありません。", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("基礎体力鍛錬を続けますか？受ける前に、剣をきちんと装備して、スキルと薬をすぐに使えるようにしておいてください。");
                    return true;
                }
                case 1 -> {
                    forceStartQuest();
                    avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        if (getQuestId() == this.quest_training_3) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == 4) {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（怖くなって断ってしまったが、このまま逃げるわけにはいかない。深呼吸をして落ち着いてから、もう一度挑戦しよう。）#k", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say(8, "体がほぐれてきたようですね。こういうときこそ、厳しい修行で基礎をしっかり固めることができるのです。基礎体力鍛錬を続けましょうか？", false, true);
                    return true;
                }
                case 1 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say(8, "今度は#b#m140020200##kで#r#o0100133##kを倒してきてください。#r20匹#kくらいでいいでしょう。では行ってきて…ん？何か言いたいことでもあるのですか？", true, true);
                    return true;
                }
                case 2 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "数がどんどん増えていませんか？", true, true);
                    return true;
                }
                case 3 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say(8, "もちろんです。何ですか、20匹では不満ですか？100匹倒してみますか？それとも999匹にしましょうか？スリーピーウッドには簡単にやってのける人もいるでしょう。何しろ修行ですから…", true, true);
                    return true;
                }
                case 4 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "いえいえ、20匹で十分です", true, true);
                    return true;
                }
                case 5 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    askAccept("そんなに謙遜しなくてもいいのですよ。かつての英雄の姿に早く戻りたいという気持ちはわかります。そういう姿勢こそが、あなたを英雄たらしめるのです。");
                    return true;
                }
                case 6 -> {
                    forceStartQuest();
                    // TODO : 文章 (js の英文を翻訳)
                    say(2, "#b（このまま話をさせておくと本当に999匹倒すことになりそうなので、受け入れることにした。）#k", false, true);
                    return true;
                }
                case 7 -> {
                    // TODO : 文章 (js の英文を翻訳)
                    say(8, "#o0100133#を20匹倒してきてください。", true, true);
                    return true;
                }
                case 8 -> {
                    avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 基礎体力テスト
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            // TODO : 文章 (js の英文を翻訳)
            say("あら、5匹では物足りませんか？もっと修行が必要だと思うなら、それ以上倒しても構いませんよ。全部倒してしまっても、心は痛みますが見なかったことにしましょう。立派な目的のための犠牲ですから…", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("では、あなたにふさわしいかどうかを確かめる試験を受けてもらいます。この島で一番強いモンスター、#o0100134#を相手にするだけです。#r50匹#kくらいで十分だと思いますが…", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("#o0100134#はそれほど数が多くないので、全滅させるわけにはいきませんね。5匹でどうでしょう？あなたは修行をしに来たのであって、生態系を壊しに来たのではありませんから。");
                return true;
            }
            case 2 -> {
                forceStartQuest();
                avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
