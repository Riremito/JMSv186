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
// Quest.wz startscript : q20600s, q20610s (JMS186), 終わらない修行, まだ終わらない修行, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Cygnus_EndlessTraining extends QuestScriptMan {

    private final int quest_endless_training = 20600; // 終わらない修行

    @Override
    public boolean questStart(int status, int answer) {
        boolean endless = (getQuestId() == this.quest_endless_training);
        switch (status) {
            case -1 -> {
                // 受諾しなかった
                if (!endless) {
                    say("Well, what you're doing right now doesn't make you look like someone that's humble. You just look complacent by doing that, and that's never a good thing.");
                }
                return false;
            }
            case 0 -> {
                if (endless) {
                    askAccept("#h0#. Have you been slacking off on training since reaching Level 100? We all know how powerful you are, but the training is not complete. Take a look at these Knight Commanders. They train day and night, preparing themselves for the possible encounter with the Black Wizard.");
                } else {
                    askAccept("Have you been mastering your skills? I am sure you've mastered all your skills, which means... it's time for you to learn a #bnew skill#k, right?");
                }
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
}
