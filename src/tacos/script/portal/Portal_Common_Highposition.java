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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : highposition (JMS186), js の処理を利用
// 称号 : 頂上に立った者 (ノーチラス, カニングシティ, エリニア, ペリオン, オルビスの順)
public class Portal_Common_Highposition extends PortalScriptMan {

    private final int quest_title = 29004;
    private final int quest_enter = 27017; // InfoQuest
    private final int quest_count = 27018;
    private final int[] maps = {
        120000000, // ノーチラス
        103000000, // カニングシティ
        101010103, // エリニア
        102000000, // ペリオン
        200080100, // オルビス
    };

    @Override
    public boolean enter() {
        if (getLevel() < 15 || isQuestFinished(this.quest_title)) {
            return false;
        }
        if (!isQuestActive(this.quest_title)) {
            forceStartQuest(this.quest_title);
            updateInfoQuest(this.quest_enter, "enter=00000");
            forceStartQuest(this.quest_count, "0");
        }
        String quest = getInfoQuest(this.quest_enter);
        int number = integer(getQuestData(this.quest_count));
        String new_quest = "enter=";
        boolean changed = false;
        for (int i = 0; i < this.maps.length; i++) {
            String flag = substring(quest, i + 6, 1);
            if (getMapId() == this.maps[i] && flag.equals("0")) {
                flag = "1";
                changed = true;
            }
            new_quest += flag;
        }
        if (changed) {
            updateInfoQuest(this.quest_enter, new_quest);
            forceStartQuest(this.quest_count, String.valueOf(number + 1));
            // TODO : 文章 (js の英文を翻訳, 原文 : /5 completed)
            playerMessage(-1, (number + 1) + "/5 達成");
            // TODO : 文章 (js の英文を翻訳, 原文 : Currently taking on the Title - The One Who Stood On Top)
            playerMessage(-1, "称号挑戦中 - 頂上に立った者");
            // TODO : 文章 (js の英文を翻訳, 原文 : Currently taking on the Title - The One Who Stood On Top)
            showQuestMsg("称号挑戦中 - 頂上に立った者 " + (number + 1) + "/5 達成");
        }
        return false;
    }
}
