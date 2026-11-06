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
// Quest.wz endscript : q22007e (JMS186), 卵を集める, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_CollectingEggs extends QuestScriptMan {

    private final int item_egg = 4032451; // たまご
    private final int reward_exp = 360;

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == 0) {
            say("Hm? That's strange. The Incubator wasn't installed properly. Try again.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Oh, did you bring the #t" + this.item_egg + "#? Here, give it to me. I'll give you the Incubator then.", false, true);
                return true;
            }
            case 1 -> {
                askYesNo("Alright, here you go. I have no idea how you use it, but it's yours... \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp");
                return true;
            }
            case 2 -> {
                forceCompleteQuest();
                questEndEffect();
                gainExp(this.reward_exp);
                if (haveItem(this.item_egg, 1)) {
                    removeItem(this.item_egg, 1);
                }
                evanTutorial("UI/tutorial/evan/9/0");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
