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
// Quest.wz startscript : q21700s (JMS186), 新たな始まり, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_NewBeginning extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 3) {
            say("No? Are you saying you can train on your own? I'm just letting you know that you'll get better results if you train with an instructor. You can't live in this world alone. You must learn to get along with other people.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say(8, "It seems like you've started to remember things. Your Polearm must have recognized you. This means you are surely #bAran, the wielder of Polearms#k. Is there anything else you remember? Skills you used with the Polearm perhaps? Anything?", false, true);
                return true;
            }
            case 1 -> {
                say(2, "#b(You tell her that you remember a few skills.)#k", true, true);
                return true;
            }
            case 2 -> {
                say(8, "That's not a lot, but it's progress. Our focus, then, should be to get you back to the state before you were frozen. You may have lost your memory, but I'm sure it won't take long for you to recover the abilities that your body remembers.", true, true);
                return true;
            }
            case 3 -> {
                say(2, "How do I recover my abilities?", true, true);
                return true;
            }
            case 4 -> {
                askAccept("There is only one way to do that. Train! Train! Train! Train! If you continue to train, your body will instinctively remember its abilities. To help you through the process, I'll introduce you to an instructor.");
                return true;
            }
            case 5 -> {
                forceStartQuest();
                say("I gave you a #bPolearm#k because I figured it would be best for you to use a weapon you're familiar with. It will be useful in your training.", false, true);
                return true;
            }
            case 6 -> {
                say("You'll find a Training Center if you exit to the #bleft#k. There, you''ll meet #b#p1202006##k. I'm a bit worried because I think he may be struggling with bouts of Alzheimer's, but he spent a long time researching skills to help you. I'm sure you'll learn a thing or two from him.", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
