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
// Quest.wz startscript : q20311s - q20315s (JMS186), 神獣の涙 (3次転職), 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_TearOfTheDivineBird extends QuestScriptMan {

    private final int quest_soul_master = 20311; // 神獣の涙(ソウルマスター) (20311 - 20315)
    private final int job_soul_master = 1111; // ソウルマスター 3次 (1111, 1211, 1311, 1411, 1511)
    private final int item_medal = 1142068; // 上級騎士の勲章
    private final int required_level = 70;

    @Override
    public boolean questStart(int status, int answer) {
        int job = this.job_soul_master + (100 * (getQuestId() - this.quest_soul_master));
        // いいえを選択した
        if (getLastAction() == 0 && status == 0) {
            // TODO : 文章 (js の英文を翻訳)
            say("まだ準備ができていないようですね。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("あなたが変身術師から取り戻してきた宝石は、神獣の涙です。神獣の力の結晶なのです。暗黒の魔法使いの手に渡っていたら、私たちはみな破滅していたでしょう。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("大きな災いになりかねないところを防いでくれたあなたの功績に、女神様が新しい称号を授けてくださいました。受け取る準備はできていますか？");
                return true;
            }
            case 2 -> {
                if ((getLevel() - this.required_level) * 3 < getRemainingSp()) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("まだ#bSP#kが残りすぎていますね。それでは新しい称号は得られません。1次と2次のスキルにもっとSPを使うことを強く勧めます。", false, true);
                    return false;
                }
                if (!canHold(this.item_medal, 1)) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("インベントリに空きを作ってください。");
                    return false;
                }
                gainItem(this.item_medal, 1);
                changeJob(job);
                gainAp(5);
                // TODO : 文章 (js の英文を翻訳)
                say("今この時をもって、あなたは上級騎士です。これからはシグナス騎士団の上級騎士という新しい称号にふさわしい品位と礼節をもって行動してください。あなたの栄光が今のように輝き続けますように。");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
