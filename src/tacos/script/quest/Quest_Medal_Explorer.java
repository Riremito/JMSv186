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
// Quest.wz startscript : q29900s - q29903s, endscript : q29900e - q29903e (JMS186), 冒険家の勲章, 旧 js (scripts_JMS) を参考に実装
public class Quest_Medal_Explorer extends QuestScriptMan {

    private final int quest_beginner = 29900; // 初心者冒険家 (29900 - 29903)
    private final int item_medal_beginner = 1142107; // 初心者冒険家の勲章 (1142107 - 1142110)
    private final int job_cygnus = 1000; // ノーブレス (冒険家は 1 - 999)

    @Override
    public boolean questStart(int status, int answer) {
        int job = getJob();
        boolean ok = switch (getQuestId()) {
            // 初心者冒険家
            case 29900 -> 0 < job && job < this.job_cygnus;
            // ジュニア冒険家
            case 29901 -> 0 < job % 100 && job < this.job_cygnus;
            // ベテラン冒険家
            case 29902 -> 0 < job % 10 && job < this.job_cygnus;
            // マスター冒険家
            case 29903 -> 1 < job % 10 && job < this.job_cygnus;
            default -> false;
        };
        if (ok) {
            forceStartQuest();
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        int job = getJob();
        boolean ok = switch (getQuestId()) {
            // 初心者冒険家
            case 29900 -> 0 < job && job < this.job_cygnus;
            // ジュニア冒険家
            case 29901 -> 0 < job % 100 && job < this.job_cygnus;
            // ベテラン冒険家
            case 29902 -> 0 < job % 10 && job < this.job_cygnus;
            // マスター冒険家
            case 29903 -> 1 < job % 10 && job < this.job_cygnus;
            default -> false;
        };
        int item_medal = this.item_medal_beginner + (getQuestId() - this.quest_beginner);
        if (canHold(item_medal, 1) && !haveItem(item_medal, 1) && ok) {
            gainItem(item_medal, 1);
            forceStartQuest();
            forceCompleteQuest();
        }
        return false;
    }
}
