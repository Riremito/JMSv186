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
// Quest.wz endscript : q8185e, q8189e (JMS186), ペットの進化 2, ペットの再進化
public class Quest_Common_PetEvolution extends QuestScriptMan {

    private final int quest_evolution_again = 8189; // ペットの再進化
    private final int cost = 10000;

    @Override
    public boolean questEnd(int status, int answer) {
        // ペットの再進化は最初のメッセージが 1 つ少ない
        if (getQuestId() == this.quest_evolution_again && 1 <= status) {
            status++;
        }
        switch (status) {
            case 0 -> {
                // 翻訳
                say("よし、準備はいいかい？では、進化の石を使ってペットを進化させてみよう。", false, true);
                return true;
            }
            case 1 -> {
                // 翻訳
                say("ただ、どんな姿になるのかは僕にもわからないんだ。もっとワクワクしてこない？さて、進化の様子を見たいなら、このウィンドウを少しだけ下に動かすといいよ。", false, true);
                return true;
            }
            case 2 -> {
                if (getMeso() < this.cost) {
                    // 翻訳
                    say("何だこれは？メルが足りないじゃないか！この大事な瞬間のための準備もしないで、何をしていたんだい？やれやれ。");
                    return false;
                }
                // TODO : lua の inventory.requestPetEvol (ペットの進化) は未実装, 現在は常に失敗する
                int result = -1;
                if (result != 0) {
                    // 翻訳
                    say("うーん…何かがおかしいな。準備ができているかもう一度確認してくれ。");
                    return false;
                }
                // lua の target.incMoney( -10000, 1 )
                exchange(-this.cost);
                forceCompleteQuest();
                // 翻訳
                say("ドラゴンの進化に成功したよ！どうだい？かっこいいだろう？");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
