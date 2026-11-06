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
// Quest.wz endscript : q50004e, q50005e (JMS186), コアブレイズ始動, コアブレイズ暴走, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Zipangu_CoreBlaze extends QuestScriptMan {

    @Override
    public boolean questEnd(int status, int answer) {
        if (getQuestStatus(getQuestId()) == 0) {
            forceStartQuest();
            return false;
        }
        switch (status) {
            case 0 -> {
                say("(I re-entered Shibuya of 2102. What I see down there is... #p9120033#!) \n......You are......!", false, true);
                return true;
            }
            case 1 -> {
                say("...Ah, so you're sent here to defeat #o9400296#. Seriously, I'm so sorry...\n(As he uttered those words, #p9120033# couldn't bear looking at me in the eye)", true, true);
                return true;
            }
            case 2 -> {
                say("The enemy headquarters is located at the center of Roppongi, at #bRoppongi Mall#k. Of course, you won't be able to enter the face-on. At the main lobby, an army of robots called #o9400287# stand by, serving as security. Your first task is to enter the building wile faking those robots out.\n(As he mentioned these, #p9120033# handed me a single copy of globe.)", true, true);
                return true;
            }
            case 3 -> {
                say("The truth is, there have been underground pathways that leads from Shibuya to Roppongi Mall. Using this will allow you to enter the building without being noticed by #o9400287#. This is the map that'll lead you to there. It's just a straight line, so you should have no trouble entering there, but I'll give you the map nontheless.", true, true);
                return true;
            }
            case 4 -> {
                say("Please head over to Shibuya in year 2102, and use the undergroud path to enter the mall. Since the mall is the headquarters, you may encounter a number of monsters you wil have never seen. Please do not underestimate them. Good luck!");
                forceCompleteQuest();
                questEndEffect();
                safeDispose();
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
