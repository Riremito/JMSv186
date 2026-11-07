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
// Quest.wz startscript : q29924s - q29928s (JMS186), アランの勲章, 旧 js (scripts_JMS) を参考に実装
public class Quest_Medal_Aran extends QuestScriptMan {

    private final int quest_awakened = 29924; // 目覚めたアラン (29924 - 29928)
    private final int item_medal_awakened = 1142129; // 目覚めたアランの勲章 (1142129 - 1142133)
    private final int[] required_levels = {10, 30, 70, 120, 200};
    private final int job_aran = 21; // アラン (2100 - 2112)

    @Override
    public boolean questStart(int status, int answer) {
        int index = getQuestId() - this.quest_awakened;
        int item_medal = this.item_medal_awakened + index;
        if (canHold(item_medal, 1) && !haveItem(item_medal, 1) && this.required_levels[index] <= getLevel() && getJob() / 100 == this.job_aran) {
            gainItem(item_medal, 1);
            forceStartQuest();
            forceCompleteQuest();
        }
        return false;
    }
}
