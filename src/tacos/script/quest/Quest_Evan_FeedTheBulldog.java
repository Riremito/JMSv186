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
// Quest.wz startscript : q22001s (JMS186), エゾオオカミに餌をあげる, 旧 js (scripts_JMS) を参考に実装, endscript なし (js の end は未使用)
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_FeedTheBulldog extends QuestScriptMan {

    private final int item_food = 4032447; // オオカミの餌

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            say("Stop being lazy. Do you want to see your brother bitten by a dog? Hurry up! Talk to me again and accept the quest!", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Haha. I had a good laugh. Hahaha. But enough with that nonsense. Feed #p1013102#, would you?", false, true);
                return true;
            }
            case 1 -> {
                say(3, "#bWhat? That's Utah's job!#k", true, true);
                return true;
            }
            case 2 -> {
                askAccept("You little brat! I told you to call me Older Brother! You know how much #p1013102# hates me. He'll bite me if I go near him. You feed him. He likes you.");
                return true;
            }
            case 3 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    gainItem(this.item_food, 1);
                    forceStartQuest();
                }
                say("Hurry up and head #bleft#k to feed #b#p1013102##k. He's been barking to be fed all morning.", false, true);
                return true;
            }
            case 4 -> {
                say("Feed #p1013102# and come back to see me.", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
