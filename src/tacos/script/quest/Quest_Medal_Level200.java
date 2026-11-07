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
// Quest.wz startscript : q29910s - q29914s (JMS186), 覇王, 魔王, 賢王, 冥王, 海王, 旧 js (scripts_JMS) を参考に実装
public class Quest_Medal_Level200 extends QuestScriptMan {

    private final int quest_warrior = 29910; // 覇王 (29910 - 29914)
    private final int required_level = 200;

    @Override
    public boolean questStart(int status, int answer) {
        // 1 = 戦士, 2 = 魔法使い, 3 = 弓使い, 4 = 盗賊, 5 = 海賊
        int job_type = getQuestId() - this.quest_warrior + 1;
        if (this.required_level <= getLevel() && getJob() / 100 == job_type) {
            forceStartQuest();
        }
        return false;
    }
}
