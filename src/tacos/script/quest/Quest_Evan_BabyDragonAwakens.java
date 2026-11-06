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
// Quest.wz startscript : q22500s (JMS186), ちびドラゴン目覚める, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_BabyDragonAwakens extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 11) {
            say("You don't believe me? Grrrrr, you're getting me mad!", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("I'm finally here! *inhales* Ah, this must be air I'm breathing. And that, that must be the sun! And that, a tree! And that, a plant! And that, a flower! Woohahahaha! This is incredible! This is much better than I imagined the world to be while I was trapped inside the egg. And you... Are you my master? Hm, I pictured you differently.", false, true);
                return true;
            }
            case 1 -> {
                say(3, "#bWhoooooa, it talks!", true, true);
                return true;
            }
            case 2 -> {
                say("My master is strange. I guess I can't do anything about it now, since the pact has been made. *sigh* Well, good to meet you. We'll be seeing a lot of each other.", true, true);
                return true;
            }
            case 3 -> {
                say(3, "#bEh? What do you mean? We'll be seeing a lot of each other? What pact?", true, true);
                return true;
            }
            case 4 -> {
                say("What do you mean what do I mean?! You woke me from the Egg. You're my master! So of course it's your responsibility to take care of me and train me and help me become a strong Dragon. Obviously!", true, true);
                return true;
            }
            case 5 -> {
                say(3, "#bWhaaat? A Dragon? You're a Dragon?! I don't get it... Why am I your master? What are you talking about?", true, true);
                return true;
            }
            case 6 -> {
                say("What are YOU talking about? Your spirit made a pact with my spirit! We're pretty much the same person now. Do I really have to explain? As a result, you've become my master. We're bound by the pact. You can't change your mind... The pact cannot be broken.", true, true);
                return true;
            }
            case 7 -> {
                say(3, "#bWait, wait, wait. Let me get this straight. You're saying I have no choice but to help you?", true, true);
                return true;
            }
            case 8 -> {
                say("Yuuup! Heeeey...! What's with the face? You...don't want to be my master?", true, true);
                return true;
            }
            case 9 -> {
                say(3, "#bNo... It's not that... I just don't know if I'm ready for a pet.", true, true);
                return true;
            }
            case 10 -> {
                say("A p-p-pet?! Did you just call me a pet?! How dare... Why, I'm a Dragon! The strongest being in the world!", true, true);
                return true;
            }
            case 11 -> {
                say(3, "#b...#b(You stare at him skeptically. He looks like a lizard. A puny little one, at that.)#k", true, true);
                return true;
            }
            case 12 -> {
                askAccept("Why are you looking at me like that?! Just watch! See what I can do with my power. Ready?");
                return true;
            }
            case 13 -> {
                forceStartQuest();
                say("Command me to slay the #r#o1210100#s#k! Do it now! I'll show you how fast a Dragon can defeat the #o1210100#s! Goooo, charge!", false, true);
                return true;
            }
            case 14 -> {
                say("Wait a minute! Did you distribute your AP? I'm heavily affected by my master's #bINT and LUK#k! If you really want to see what I can do, distribute your AP and #bequip your Magician equipment#k before you use the skill!", true, true);
                return true;
            }
            case 15 -> {
                evanTutorial("UI/tutorial/evan/11/0");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
