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
// Quest.wz startscript : q21703s, endscript : q21703e (JMS186), 修行だけが唯一の道！3, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_TrainingIsTheOnlyWay3 extends QuestScriptMan {

    private final int skill_combo_ability = 21000000; // コンボアビリティ
    private final int skill_master_level = 10;
    private final int reward_exp = 2800;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 5) {
            // TODO : 文章 (js の英文を翻訳)
            say("教官を超えるには並々ならぬ力と意志が必要なのはわかる。だが、ここで立ち止まっていてはいかん。もっと大きなことを目指して進むのだ！英雄としての本性を取り戻すために、できることは何でもするのだ！", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("お前の能力は本当に形になってきたな。わしのような年寄りが役に立てたとは驚きだ。お前の力になれたと思うと、うれしくて涙が出てくるわい。*ぐすん*", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（それほど長く一緒に修行したわけでもないのに…なぜ泣いているんだろう？）#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("よし、これが修行の3番目、最後の段階だ。最後の相手は…#r#o9300343##kだ！#o1210100#について何か知っているか？", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "まあ、少しは…", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("奴らは生まれながらの戦士だ！生まれつき食べ物に目がなくてな。通りすがりに目に入った食べ物は何でも平らげてしまうのだ。恐ろしいだろう？", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（それは本当だろうか？）#k", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("よし、では…#bもう一度修練場に入って#k、#o9300343#を#r30匹#k倒し、お前の実力を見せてみろ！全力を出さなければ倒せんぞ！行け、行け、行け！わしを超えてみせろ！");
                return true;
            }
            case 7 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("さあ、あの恐ろしい#o9300343#を倒してくるのだ！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == 1) {
            // TODO : 文章 (js の英文を翻訳)
            say("教官と離れるのが名残惜しいのか？*ぐすん* 感動したぞ。だが、ここで立ち止まってはいかん。お前はもっと大きなことを成し遂げる運命なのだ！", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("おお、#o9300343#を30匹すべて倒して戻ってきたか。お前ならできると思っていた…記憶もなく能力もほとんどないのに、お前はほかとは違うとわかっていた！なぜかって？鉾を持ち歩いているからに決まっているだろう！", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（からかわれているのだろうか？）#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("もうお前に教えることは何もない。お前はわしの腕を超えたのだ。さあ行け！振り返るな！お前の教官を務められて、この年寄りは幸せだったぞ。");
                return true;
            }
            case 3 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    forceCompleteQuest();
                    questEndEffect();
                    teachSkill(this.skill_combo_ability, getSkillLevel(this.skill_combo_ability), this.skill_master_level);
                    gainExp(this.reward_exp);
                }
                // TODO : 文章 (js の英文を翻訳)
                say(2, "（#bコンボアビリティ#kのスキルを思い出した！物忘れのひどい老人の修行なので最初は半信半疑だったが、驚くほど効果があった！）", false, true);
                avatarOriented("Effect/BasicEff.img/AranGetSkill");
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("さあ、#p1201000#に報告しに行くのだ。お前の成長ぶりを見たら、きっと大喜びするだろう！", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
