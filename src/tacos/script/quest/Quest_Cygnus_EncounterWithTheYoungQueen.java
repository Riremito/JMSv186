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
// Quest.wz startscript : q20015s (JMS186), 幼い女王の挨拶, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Cygnus_EncounterWithTheYoungQueen extends QuestScriptMan {

    private final int item_medal = 1142065; // ノーブレスの勲章

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            say("You may be hesitant now, but I can see an incredible amount of courage behind your eyes. Close your eyes and feel the courage and passion inside of you.", false, true);
            safeDispose();
            return true;
        }
        switch (status) {
            case 0 -> {
                say("Did you know? Maple World may look peaceful, but certain areas are filled with forces of darkness. The Black Mage and those who want to revive the Black Mage are threatening Maple World.", false, true);
                return true;
            }
            case 1 -> {
                say("We can't just sit here and do nothing while our enemies get stronger. Our own fear will only come back to haunt us.", true, true);
                return true;
            }
            case 2 -> {
                askAccept("But I won't worry too much. Someone as determined as you will be able to protect Maple World from danger, right? If you are brave enough to volunteer to become one of the Knights, I know I can count on you. \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_medal + "# #t" + this.item_medal + "# - 1");
                return true;
            }
            case 3 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    gainItem(this.item_medal, 1);
                    forceCompleteQuest();
                }
                say("Heehee, I knew you'd say that. But you know you still have a ways to go before you can fight for Maple World, right?", false, true);
                return true;
            }
            case 4 -> {
                say("#p1101002#, my Tactician, who is standing right next to me, will help you become an honorable Knight. I'll be looking forward to your progress. I'm counting on you!", true, false);
                safeDispose();
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
