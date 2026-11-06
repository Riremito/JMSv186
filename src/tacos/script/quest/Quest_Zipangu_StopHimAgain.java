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
// Quest.wz startscript : q50003s, endscript : q50003e (JMS186), 何度でも止めてみせる！, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Zipangu_StopHimAgain extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            say("...What is it? Ah, I see that he's coming really close!", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askAccept("Watch out, because he seems... much more powerful than before. Do not underestimate him!");
                return true;
            }
            case 1 -> {
                forceStartQuest();
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        forceCompleteQuest();
        questEndEffect();
        return false;
    }
}
