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
// Quest.wz startscript, endscript (JMS186), エヴァンのクエスト, 旧 js (scripts_JMS) を参考に実装
// TODO : js の仮の処理 (本来の会話や転職などの処理がなく、クエストの開始や完了のみ)
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_Simplified extends QuestScriptMan {

    private final int item_letter = 4032455; // グリドの手紙
    private final int item_black_key = 4032471; // 黒い鍵
    private final int item_scale = 4032502; // ドラゴン 鱗
    private final int item_shining_scale = 4032503; // 光るドラゴン 鱗
    private final int item_accelerant = 4032468; // 急性成長促進剤
    private final int item_accelerant_count = 10;
    private final int quest_make_accelerant = 22568; // 成長促進剤を製造
    private final int reward_exp_black_key = 7100;

    @Override
    public boolean questStart(int status, int answer) {
        switch (getQuestId()) {
            // 手紙配達
            case 22510 -> {
                if (!canHold(this.item_letter, 1)) {
                    say("Please have some space.", false, true);
                    return false;
                }
                say("Go deliver this to Chief Stan of Henesys.", false, true);
                gainItem(this.item_letter, 1);
                forceStartQuest();
                return false;
            }
            // ドラゴンマスターのお仕事
            case 22512 -> {
                say("Go talk to Rina of Henesys.", false, true);
                forceStartQuest();
                return false;
            }
            // 修練あるのみ
            case 22514 -> {
                say("Go talk to Chief Stan of Henesys.", false, true);
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            // 絶えないヘンケルの妄想
            case 22518 -> {
                say("Please eliminate 100 Spores.", false, true);
                forceStartQuest();
                return false;
            }
            // カニングシティーでの捜査 : ネーラ
            case 22536 -> {
                say("Let's talk to Nella of Kerning City.", false, true);
                forceStartQuest();
                return false;
            }
            // 本の行方は？1
            case 22541 -> {
                say("Go talk to Icarus in Kerning City.", false, true);
                forceStartQuest();
                return false;
            }
            // 秘密組織への加入条件1
            case 22560 -> {
                say("Please kill 150 Curse Eye.", false, true);
                forceStartQuest();
                return false;
            }
            // ミルの考え
            case 22565 -> {
                say("Let's talk to Mir.", false, true);
                forceStartQuest();
                gainSp(2, 3);
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            // 秘密組織　二回目の任務
            case 22575 -> {
                if (!canHold(this.item_black_key, 1)) {
                    say("Make ETC space.", false, true);
                    return false;
                }
                gainItem(this.item_black_key, 1);
                forceStartQuest();
                forceCompleteQuest();
                questEndEffect();
                gainExp(this.reward_exp_black_key);
                gainSp(1, 4);
                return false;
            }
            // 秘密組織に関する疑問
            case 22578 -> {
                say("Let's talk to Mir.", false, true);
                forceStartQuest();
                gainSp(2, 4);
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            // 秘密組織　三回目の任務の前に
            case 22581 -> {
                say("Talk to Hiver in Ludibrium Village.", false, true);
                forceStartQuest();
                return false;
            }
            // 秘密組織に関する疑惑
            case 22585 -> {
                say("Let's talk to Mir.", false, true);
                forceStartQuest();
                gainSp(1, 5);
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            // 脱皮直後１
            case 22602 -> {
                gainItem(this.item_scale, 1);
                forceStartQuest();
                return false;
            }
            // 脱皮直後2
            case 22603 -> {
                gainItem(this.item_shining_scale, 1);
                forceStartQuest();
                return false;
            }
            default -> {
                // 22100 - 22109, ドラゴンマスター 1次転職 - 10次転職 (js では転職の処理をスキップ)
                // 22401, 22403, 22406, 22411, ドラゴンライディング (js ではスキップ)
                // 22591, 22593, 22594, 22595
                forceStartQuest();
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
        }
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (getQuestId()) {
            // ドラゴンの種類と特徴(下)
            case 22546 -> {
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            // オニックスドラゴンに関する知識3
            case 22564 -> {
                gainSp(1, 2);
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            // 秘密組織　初回の任務
            case 22567 -> {
                if (getQuestStatus(this.quest_make_accelerant) == 2 || haveItem(this.item_accelerant, this.item_accelerant_count)) {
                    gainSp(2, 3);
                    forceCompleteQuest();
                    questEndEffect();
                    return false;
                }
                say("Make a growth accelerant.", false, true);
                forceStartQuest();
                return false;
            }
            // 秘密組織　三回目の任務
            case 22582 -> {
                forceStartQuest();
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
