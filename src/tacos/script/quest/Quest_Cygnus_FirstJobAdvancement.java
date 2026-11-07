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
// Quest.wz endscript : q20104e, q20105e (JMS186), ナイトウォーカーの道, ストライカーの道, 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_FirstJobAdvancement extends QuestScriptMan {

    private final int quest_night_walker = 20104; // ナイトウォーカーの道
    private final int job_night_walker = 1400;
    private final int job_striker = 1500;
    private final int item_night_walker_claw = 1472061; // 初歩盗賊の篭手
    private final int item_night_walker_star = 2070015; // 初歩盗賊の手裏剣
    private final int item_striker_knuckle = 1482014; // 初心者海賊のナックル
    private final int item_medal = 1142066; // 見習い騎士の勲章

    @Override
    public boolean questEnd(int status, int answer) {
        boolean night_walker = (getQuestId() == this.quest_night_walker);
        // いいえを選択した
        if (getLastAction() == 0 && status == -1) {
            // TODO : 文章 (js の英文を翻訳)
            say("これは大切な決断です。", false, true);
            safeDispose();
            return true;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("決心はつきましたか？一度決めたら変えられませんので、よく考えてから決めてください。本当に" + (night_walker ? "ナイトウォーカー" : "ストライカー") + "になりますか？");
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("あなたの体をソウルマスターにふさわしいものに作り変えました。もっと強くなりたいなら、能力値ウィンドウ（S）で適切な能力値を上げてください。何を上げればいいかわからなければ、#b自動#kをクリックするだけで大丈夫です。", false, true);
                int job = night_walker ? this.job_night_walker : this.job_striker;
                if (getJob() != job) {
                    if (night_walker) {
                        gainItem(this.item_night_walker_claw, 1);
                        gainItem(this.item_night_walker_star, 800);
                        gainItem(this.item_night_walker_star, 800);
                    } else {
                        gainItem(this.item_striker_knuckle, 1);
                    }
                    gainItem(this.item_medal, 1);
                    expandInventory(1, 4);
                    expandInventory(4, 4);
                    changeJob(job);
                }
                forceCompleteQuest();
                questEndEffect();
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("装備とETCのインベントリのスロット数も増やしておきました。騎士として持ち歩くべきアイテムで、スロットを上手に活用してください。", true, true);
                return true;
            }
            case 3 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("#bSP#kも少し与えておきましたので、#bスキルウィンドウ#kを開いて新しいスキルを覚えてください。もちろん一度にすべて上げることはできませんし、基本のスキルを極めないと覚えられないスキルもあります。", true, true);
                return true;
            }
            case 4 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ノーブレスだったころとは違い、ソウルマスターになるとHPがなくなったときに経験値を一部失いますので、気をつけてくださいね。", true, true);
                return true;
            }
            case 5 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("さあ…世界に出て、シグナス騎士団の活躍を見せてきてください。", true, true);
                safeDispose();
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
