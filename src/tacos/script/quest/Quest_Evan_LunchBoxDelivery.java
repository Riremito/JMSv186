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
// Quest.wz startscript : q22003s (JMS186), お弁当配達, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
// js では status 2 の処理が抜けていて会話が進まないため、status 3, 4 の処理を status 2, 3 で行う
public class Quest_Evan_LunchBoxDelivery extends QuestScriptMan {

    private final int item_lunch_box = 4032448; // 愛が込められたお弁当

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            say("Good kids listen to their mothers. Now, Evan, be a good kid and talk to me again.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askAccept("Your #bDad#k forgot his Lunch Box when he left for the farm this morning. Will you #bdeliver this Lunch Box#k to your Dad in #b#m100030300##k, honey?");
                return true;
            }
            case 1 -> {
                forceStartQuest();
                say("Heehee, my Evan is such a good kid! Head #bleft after you exit the house#k. Rush over to your dad. I'm sure he's starving.", false, true);
                if (!haveItem(this.item_lunch_box, 1)) {
                    gainItem(this.item_lunch_box, 1);
                }
                return true;
            }
            case 2 -> {
                say("Come back to me if you happen to lose the Lunch Box. I'll make his lunch again.", true, true);
                return true;
            }
            case 3 -> {
                evanTutorial("UI/tutorial/evan/5/0");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
