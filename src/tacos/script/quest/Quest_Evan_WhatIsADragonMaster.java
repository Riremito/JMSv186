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
// Quest.wz startscript : q22507s (JMS186), ドラゴンマスターとは？, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_WhatIsADragonMaster extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 13) {
            say("Uh, you're kidding me, right? Tell me your finger slipped! Go ahead and accept the quest.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("I knew it! I knew we were connected, master! When you get stronger, I get stronger, too. And when I get stronger, you can use my strength! That's our pact. I knew I picked a good master!", false, true);
                return true;
            }
            case 1 -> {
                say(2, "#bI see. How did we end up in this pact anyway?#k", true, true);
                return true;
            }
            case 2 -> {
                say("I don't know. I was just an egg. I can't really remember... though I faintly recall you, master, walking toward me in a foggy forest. I remember your surprise upon seeing me. And I was calling out to you in return.", true, true);
                return true;
            }
            case 3 -> {
                say(2, "#b(Wait! That sounds just like that one dream you had... Did the two of you meet in a dream? Is it possible that the giant Dragon you saw in that dream was...Mir?#k", true, true);
                return true;
            }
            case 4 -> {
                say("Master, you and I are one in spirit. I knew it the moment I saw you. That's why I wanted to make the pact with you. No one else. You had to pay the price I set, of course.", true, true);
                return true;
            }
            case 5 -> {
                say(2, "#bI paid a price?#k", true, true);
                return true;
            }
            case 6 -> {
                say("Don't you remember? When you recognized me and touched me? That was the one condition I set. The moment you touched my egg, you and I become one in spirit.", true, true);
                return true;
            }
            case 7 -> {
                say(2, "#One in...spirit?", true, true);
                return true;
            }
            case 8 -> {
                say("Yes! The Spirit Pact! You and I have separate bodies, but we share one spirit. That's why you get stronger when I get stronger, and vice versa! Awesome, right? At least, I think so.", true, true);
                return true;
            }
            case 9 -> {
                say(2, "#bI have no idea what you're talking about, but it sounds like a pretty big deal.#k", true, true);
                return true;
            }
            case 10 -> {
                say("Of course it's a big deal, silly master! You never have to worry about monster again. You have me to protect you now! Go ahead and test me. In fact, let's go right now!", true, true);
                return true;
            }
            case 11 -> {
                say(2, "#bBut it's peaceful here. There are no dangerous monster around.#k", true, true);
                return true;
            }
            case 12 -> {
                say("WHAT?! That's no fun! Don't you like adventuring, master? Fighting monster on behalf of your people, defeating evil, rescuing the innocent, and all that? You're not into that kind of thing?", true, true);
                return true;
            }
            case 13 -> {
                say(2, "#bIt's not part of my five year plan. I'm just kidding, but seriously, I'm a farmer's son...#k", true, true);
                return true;
            }
            case 14 -> {
                askAccept("Bah, well let me tell you this. It's impossible for a Dragon Master to live a peaceful life. I'll have plenty of chances to prove my skills. Trust me, our life will be one big adventure. Promise me that you'll stick with me, okay?");
                return true;
            }
            case 15 -> {
                forceStartQuest();
                say(1, "Hehehe, alright then, master. Let's get to it!", false, true);
                return true;
            }
            case 16 -> {
                say(3, "#b(You're a bit confused, but you are now traveling with Mir the Dragon. Perhaps you'll go on an adventure together, like he said.)#k", true, true);
                return true;
            }
            case 17 -> {
                say(2, "#b(You still have an errand to run. Your dad needs to talk to you, so go and see him now.)#k", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
