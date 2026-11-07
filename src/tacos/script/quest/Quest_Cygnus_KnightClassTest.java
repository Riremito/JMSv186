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
// Quest.wz endscript : q20201e - q20205e (JMS186), 騎士クラス試験 (2次転職), 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_KnightClassTest extends QuestScriptMan {

    private final int quest_soul_master = 20201; // ソウルマスターの騎士クラス試験 (20201 - 20205)
    private final int item_proof_soul_master = 4032096; // テストの証票(ソウルマスター) (4032096 - 4032100)
    private final int item_proof_count = 30;
    private final int job_soul_master = 1110; // ソウルマスター 2次 (1110, 1210, 1310, 1410, 1510)
    private final int item_medal = 1142067; // 正騎士の勲章

    @Override
    public boolean questEnd(int status, int answer) {
        int index = getQuestId() - this.quest_soul_master;
        int item_proof = this.item_proof_soul_master + index;
        int job = this.job_soul_master + (100 * index);
        // いいえを選択した
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("まだ正騎士としての責任を負う準備ができていないようですね。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    forceStartQuest();
                    return false;
                }
                if (!haveItem(item_proof, this.item_proof_count)) {
                    return false;
                }
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("試験の証をすべて持ってきたのですね…わかりました。あなたにはもう正騎士になる資格があると思います。正騎士になりますか？");
                return true;
            }
            case 1 -> {
                if (!canHold(this.item_medal, 1)) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("インベントリがいっぱいです。確認してください。");
                    return false;
                }
                forceCompleteQuest();
                questEndEffect();
                if (getJob() != job) {
                    changeJob(job);
                    removeItem(item_proof, this.item_proof_count);
                    gainItem(this.item_medal, 1);
                }
                // TODO : 文章 (js の英文を翻訳)
                say("見習い騎士は終わりです。あなたは今からシグナス騎士団の正騎士です。", false, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("#bSP#kを与えました。騎士だけが使えるソウルマスターのスキルもいくつか与えましたので、しっかり磨いて、あなたの魂と同じくらい育ててほしいと思います。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("正式にシグナス騎士団の騎士となったのですから、女神の名を汚さないよう、それにふさわしい振る舞いをしてください。", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
