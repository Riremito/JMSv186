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
// Quest.wz endscript : q21720e (JMS186), 人形使いの警告, 旧 js (scripts_JMS) を参考に実装
// js ではクエストの状態が 0 の場合にスキルを習得する条件のため完了時にスキルを習得できないが、21703 と同じ進行中 (1) の条件にする
public class Quest_Aran_PuppeteersWarning extends QuestScriptMan {

    private final int skill_booster = 21001003; // ブースター(鉾)
    private final int skill_master_level = 20;
    private final int reward_exp = 3900;

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == 5) {
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "何の用ですか？#p1002104#から、あなたがビクトリアアイランドで彼の仕事を手伝いながら熱心に修行していると連絡がありました。どうしたのですか？何ですって？ブラックウィング？", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（人形使いとブラックウィング、そして彼らの目的について話した。）#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "そうですか…ブラックウィングという組織があったなんて知りませんでした…暗黒の魔法使いがどれほど危険かわかっていながら復活させようとするなんて、愚か者に違いありません。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "そ…そうだな…\r\r#b（言いたいことははっきり言う人のようだ。）#k", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "予言書には、英雄がよみがえって暗黒の魔法使いと戦うと書かれています。本当かどうかわかりませんでしたが、これで暗黒の魔法使いがまだ存在していることがはっきりしました。", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "怖くないのか？", true, true);
                return true;
            }
            case 6 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("怖い？ふん。暗黒の魔法使いが現れたって構いません。あなたが私たちを守ってくれるのですから。むしろ、大きな戦いに備えてあなたを鍛えたくなりました。ああ、そういえば#bスキル#kを見つけたのです。見てみますか？");
                return true;
            }
            case 7 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    forceCompleteQuest();
                    questEndEffect();
                    teachSkill(this.skill_booster, getSkillLevel(this.skill_booster), this.skill_master_level);
                    gainExp(this.reward_exp);
                }
                avatarOriented("Effect/BasicEff.img/AranGetSkill");
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（ブースター(鉾)のスキルを思い出した！）#k", false, true);
                return true;
            }
            case 8 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "このスキルは、解読できない古代の文書から見つけたものです。あなたが昔使っていたスキルではないかと思ったのですが、どうやら当たっていたようですね。昔ほど強くはありませんが、いずれ取り戻せるでしょう。", true, true);
                return true;
            }
            case 9 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "あなたは着実に強くなっています。私もここであなたを励まし続けます。恐れることは何もありません。あなたは負けません。暗黒の魔法使いに負けるために氷の中から目覚めたわけではないでしょう？今度こそ、彼を完全に倒すのです！", true, true);
                return true;
            }
            case 10 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "そのためにできることはひとつだけです。修行、修行、修行です。ビクトリアアイランドへ行って修行を続けてください。暗黒の魔法使いが手も足も出ないほど強くなりましょう！", true, false);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
