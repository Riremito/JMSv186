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
// Quest.wz startscript : q21712s, q21716s (JMS186), 1番目の情報収集の完了, 2番目の情報収集 完了, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_InformationGathering extends QuestScriptMan {

    private final int quest_first = 21712; // 1番目の情報収集の完了

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_first) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == 1) {
                say("You still don't understand what's going on? I'll explain it to you again if you talk to me one more time.", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    say("#t4032315#... #rThis puppet is making a strange noise#k. You can't hear it with your ears, of course, since it can only be heard by the #o1210102#s. I believe it's this noise that changed the personality of the #o1210102#s.", false, true);
                    return true;
                }
                case 1 -> {
                    askAccept("The #o1210102#s that have been affected by the noise have turned cynical. They've started fighting the non-affected #o1210102#s, which has made all #o1210102#s prepare for combat. #bThe reason for all these changes in the #o1210102#s is this puppet#k! Do you understand?");
                    return true;
                }
                case 2 -> {
                    forceStartQuest();
                    say(9, "I wonder what triggered this in the first place. There is no way this puppet was naturally created, which means someone planned this. I should keep an eye on the #o1210102#s.", false, true);
                    return true;
                }
                case 3 -> {
                    say(2, "#b(You were able to find out what caused the changes in the #o1210102#s. You should report to #p1002104# and deliver the information you''ve gathered.)#k", true, false);
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            say("What? I don''t think there are any suspects besides that kid. Please think again.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say(8, "What did #p1032112# say?", false, true);
                return true;
            }
            case 1 -> {
                say(2, "#b(You tell her what #p1032112# observed.)#k", true, true);
                return true;
            }
            case 2 -> {
                askAccept("A kid with a puppet? That seems very suspicious. I am sure that kid is the reason the Green Mushrooms have suddenly turned violent.");
                return true;
            }
            case 3 -> {
                forceStartQuest();
                say(2, "How dare this kid wreak havoc in the South Forest. Who knows how long it will take to restore the forest... I''ll have to devote most of my time cleaning up the mess.", false, true);
                return true;
            }
            case 4 -> {
                say(2, "#b(You were able to find out what caused the changes in the Green Mushrooms. You should report #p1002104# and deliver the information you''ve collected.)#k", true, false);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
