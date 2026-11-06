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
// Quest.wz startscript : q21010s, endscript : q21010e (JMS186), 英雄の帰還, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_ReturnOfTheHero extends QuestScriptMan {

    private final int item_red_potion = 2000022; // リエン特製赤い薬
    private final int item_blue_potion = 2000023; // リエン特製青い薬
    private final int reward_exp = 16;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 2) {
            say("No no no, you don't have to say no. It's just a potion, anyway. Besides, for a hero like you, I can give you these all day! Let me know when you change your mind.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Hmmm? What's human doing here? Wait, hey there #p1201000#. What brought you here? Oh and... do you know this person, #p1201000#? What? A hero?", false, true);
                return true;
            }
            case 1 -> {
                say("     #i4001170#", true, true);
                return true;
            }
            case 2 -> {
                say("Wait, so I am looking at the very person that your race has been waiting for hundreds of years? Wow!! I could tell the hero looked a bit different from the rest...", true, true);
                return true;
            }
            case 3 -> {
                askAccept("But because of that curse of the Black Wizard that got you trapped in ice for hundreds of years, you do look quite weak. #bHere's a potion for recovery. Please take it#k.");
                return true;
            }
            case 4 -> {
                // TODO : js のコメントでは HP を半分にする処理が必要
                say("Just drink it up first, then we'll continue our talk!", false, true);
                if (getQuestStatus(getQuestId()) == 0) {
                    gainItem(this.item_red_potion, 1);
                    forceStartQuest();
                }
                return true;
            }
            case 5 -> {
                say(3, "#b(Wait, how do I drink this? I don't remember...)#k", true, true);
                return true;
            }
            case 6 -> {
                tutorMsg(0xE);
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                say("I've been searching through blocks of ice inside the cave in hopes of finding our hero, but... I didn't think I'd actually see one in front of me right now! The prophecy is correct! #p1201000#, you were right! Now that the hero has been resurrected, we won't have to worry about the Black Wizard anymore, right?", false, true);
                return true;
            }
            case 1 -> {
                say("Wait, I have been holding onto you for too long. I'm sorry, but I bet you other penguins will react the same way as I did. I know you're busy and all, but on your way to town, #bplease go strike up a conversation with other penguins#k. Everyone will be shocked if the hero is the one initiating a conversation with them! \n\r #fUI/UIWindow.img/QuestIcon/4/0# \r #i" + this.item_red_potion + "# #t" + this.item_red_potion + "# 5 \r #i" + this.item_blue_potion + "# #t" + this.item_blue_potion + "# 5 \n\r #fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp", true, true);
                return true;
            }
            case 2 -> {
                say("Wow, you managed to level up! That means you may have acquired skill points too. In the world of Maple, every level up means 3 skill points. Press #bK#k to open the skill window and find out.", true, true);
                if (getQuestStatus(getQuestId()) == 1) {
                    gainExp(this.reward_exp);
                    gainItem(this.item_red_potion, 5);
                    gainItem(this.item_blue_potion, 5);
                    forceCompleteQuest();
                    questEndEffect();
                }
                return true;
            }
            case 3 -> {
                say(3, "#b(These penguins are so nice to me in every way possible, yet I don't remember them one bit. I better check the skill window first... but how do I do that?)#k", true, true);
                return true;
            }
            case 4 -> {
                tutorMsg(0xF);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
