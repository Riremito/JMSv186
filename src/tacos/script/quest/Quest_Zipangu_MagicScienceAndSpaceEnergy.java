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
// Quest.wz endscript : q50012e (JMS186), 魔術と科学と宇宙エネルギーと, 旧 js (scripts_JMS) を参考に実装
public class Quest_Zipangu_MagicScienceAndSpaceEnergy extends QuestScriptMan {

    private final int quest_unknown = 50015; // TODO : JMS186 には存在しないクエスト (js のまま)

    @Override
    public boolean questEnd(int status, int answer) {
        if (getQuestStatus(getQuestId()) == 0) {
            forceStartQuest();
            return false;
        }
        forceCompleteQuest(this.quest_unknown);
        forceCompleteQuest();
        questEndEffect();
        return false;
    }
}
