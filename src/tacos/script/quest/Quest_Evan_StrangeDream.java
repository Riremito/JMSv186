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
// Quest.wz startscript : q22000s, endscript : q22000e (JMS186), 不思議な夢, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_StrangeDream extends QuestScriptMan {

    private final int reward_exp = 20;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 6) {
            say("Hm? Don't you want to tell Utah? You have to be nice to your brother, dear.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Did you sleep well,Evan?", false, true);
                return true;
            }
            case 1 -> {
                say(3, "#bYes,what about you,Mom?#k", true, true);
                return true;
            }
            case 2 -> {
                say("I did as well. But you seem so tired. Are you sure you slept okay? Did the thunder and lightning last night keep you up?", true, true);
                return true;
            }
            case 3 -> {
                say(3, "#bOh, no. It's not that, Mom. I just had a strange dream last night.#k", true, true);
                return true;
            }
            case 4 -> {
                say("A strange dream? What kind of strange dream?", true, true);
                return true;
            }
            case 5 -> {
                say(3, "#bWell...#k", true, true);
                return true;
            }
            case 6 -> {
                say(3, "#b(You explain that you met a dragon in your dream.)", true, true);
                return true;
            }
            case 7 -> {
                askAccept("Hahaha, a dragon? That's incredible. I'm glad he didn't swallow you whole! You should tell Utah about your dream. I'm sure he'll enjoy it.");
                return true;
            }
            case 8 -> {
                forceStartQuest();
                say("#bUtah#k went to the #bFront Porch#k to feed the Bull Dog. You'll see him right outside.", false, true);
                return true;
            }
            case 9 -> {
                evanTutorial("UI/tutorial/evan/1/0");
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
                say("Hey, Evan. You up? What's with the dark circles under your eyes? Didn't sleep well? Huh? A strange dream? What was it about? Whoa? A dream about a dragon?", false, true);
                return true;
            }
            case 1 -> {
                say("Muahahahahaha, a dragon? Are you serious? I don't know how to interpret dreams, but that sounds like a good one! Did you see a dog in your dream, too? Hahaha!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp", true, true);
                return true;
            }
            case 2 -> {
                gainExp(this.reward_exp);
                evanTutorial("UI/tutorial/evan/2/0");
                forceCompleteQuest();
                questEndEffect();
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
