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
// Quest.wz startscript : q22008s, endscript : q22008e (JMS186), 裏庭の狐の追放, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_ChaseAwayTheFoxes extends QuestScriptMan {

    private final int item_wand = 1372043; // 初歩魔法使いのワンド
    private final int item_milk = 2022621; // 美味しい牛乳
    private final int item_juice = 2022622; // 美味しいジュース
    private final int reward_exp = 910;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            say("Are you scared of the #o9300385#es? Don't tell anyone you're related to me. That's shameful.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askAccept("It's strange. The chickens are acting funny. They used to hatch way more #t4032451#s. Do you think the Foxes have something to do with it? If so, we better hurry up and do something.");
                return true;
            }
            case 1 -> {
                forceStartQuest();
                say("Right? Let us go and defeat those Foxes. Go on ahead and defeat #r10 #o9300385#es#k in #b#m100030103##k first. I'll follow you and take care of what's left behind. Now, hurry over to #m100030103#!", false, true);
                return true;
            }
            case 2 -> {
                evanTutorial("UI/tutorial/evan/10/0");
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
                say("Did you defeat the Cunning Foxes?", false, true);
                return true;
            }
            case 1 -> {
                say(3, "#bWhat happened to slaying the Foxes left behind?", true, true);
                return true;
            }
            case 2 -> {
                say("Oh, that? Haha. I did chase them, sort of, but I wanted to make sure that they do not catch up to you. I wouldn't want you eaten by a #o9300385# or anything. So I just let them be.", true, true);
                return true;
            }
            case 3 -> {
                say(3, "#bAre you sure you weren't just hiding because you were scared of the Foxes?", true, true);
                return true;
            }
            case 4 -> {
                say("What? No way! Sheesh, I fear nothing!", true, true);
                return true;
            }
            case 5 -> {
                say(3, "#bWatch out! There's a #o9300385# right behind you!", true, true);
                return true;
            }
            case 6 -> {
                say("Eeeek! Mommy!", true, true);
                return true;
            }
            case 7 -> {
                say(3, "#b...", true, true);
                return true;
            }
            case 8 -> {
                say("...", true, true);
                return true;
            }
            case 9 -> {
                say("You little brat! I'm your older brother. Don't you mess with me! Your brother has a weak heart, you know. Don't surprise me like that!", true, true);
                return true;
            }
            case 10 -> {
                say(3, "#b(This is why I don't want to call you Older Brother...)", true, true);
                return true;
            }
            case 11 -> {
                say("Hmph! Anyway, I'm glad you were able to defeat the #o9300385#es. As a reward, I'll give you something an adventurer gave me a long time ago. Here you are. \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_wand + "# 1 #t" + this.item_wand + "# \r\n#i" + this.item_milk + "# 25 #t" + this.item_milk + "# \r\n#i" + this.item_juice + "# 25 #t" + this.item_juice + "#s \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp", true, true);
                return true;
            }
            case 12 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    forceCompleteQuest();
                    questEndEffect();
                    gainItem(this.item_wand, 1);
                    gainItem(this.item_milk, 25);
                    gainItem(this.item_juice, 25);
                    gainExp(this.reward_exp);
                }
                say("#bThis is a weapon that Magicians use. It's a Wand#k. You probably won't really need it, but it'll make you look important if you carry it around. Hahahahaha.", true, true);
                return true;
            }
            case 13 -> {
                say("Anyway, the Foxes have increased, right? How weird is that? Why are they growing day by day? We should really look into it and get to the bottom of this.", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
