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
// Quest.wz startscript : q20016s (JMS186), 暗黒の魔法使い, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Cygnus_DoYouKnowTheBlackMagician extends QuestScriptMan {

    private final int reward_exp = 380;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 7) {
            say("Oh, do you still have some questions? Talk to me again and I'll explain it to you from the very beginning.", false, true);
            safeDispose();
            return true;
        }
        switch (status) {
            case 0 -> {
                say("Hello, #h0#. Welcome to #p1101000# Knights. My name is #p1101002# and I am currently serving as the young Empress's Tactician. We'd better get acquainted since we'll be seeing a lot of each other. Haha!", false, true);
                return true;
            }
            case 1 -> {
                say("I'm sure you have a lot of questions since everything happened so quickly. I'll explain it all, one by one, from where you are to what you're here to do.", true, true);
                return true;
            }
            case 2 -> {
                say("This island is called Erev. Thanks to the Empress's magic, this island usually floats around like a boat in the sky and patrols around Maple World. Right now, however, we've stopped here for a reason.", true, true);
                return true;
            }
            case 3 -> {
                say("The young Empress is the ruler of Maple World. What? This is the first time you've heard of her? Ah, yes. Well, she's the ruler of Maple World but she doesn't like to control it. She watches from afar to make sure that all is well. Well, at least that's her usual role.", true, true);
                return true;
            }
            case 4 -> {
                say("But that's not the case right now. We've been finding signs all over Maple World that foreshadow the revival of the Black Mage. We can't have the Black Mage come back to terrorize Maple World as he has in the past!", true, true);
                return true;
            }
            case 5 -> {
                say("But that was ages ago and people today don''t realize how scary the Black Mage is. We've all become spoiled by the peaceful Maple World we enjoy today and forgotten how chaotic and frightening Maple World once was. If we don't do something, the Black Mage will once again rule Maple World!", true, true);
                return true;
            }
            case 6 -> {
                say("This is why the young Empress has decided to take matters into her own hands. She''s forming a knighthood of brave Maplers to defeat the Black Mage once and for all. You know what you need to do, right? I''m sure you have an idea since you, yourself, signed up to be a Knight.", true, true);
                return true;
            }
            case 7 -> {
                say("We have to get stronger so we can defeat the Black Mage if he revives. Our primary goal is to prevent him from destroying Maple World, and you will play a prominent role in that.", true, true);
                return true;
            }
            case 8 -> {
                askAccept("That concludes my explanation. Have I answered all your questions? \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp");
                return true;
            }
            case 9 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    gainExp(this.reward_exp);
                    forceCompleteQuest();
                }
                say("I'm glad you're clear about our current situation, but you know, at your current level, you're not even strong enough to face the Black Mage's minions, let alone the Black Mage himself. Not even his minions' minions, as a matter of fact. How will you protect Maple World at your current level?", false, true);
                return true;
            }
            case 10 -> {
                say("Although you've been accepted into the knighthood, you cannot be recognized as a knight yet. You are not an Official Knight because you're not even a Knight-in-Training. If you remain at your current level, you'll be nothing more than the handyman of #p1101000# Knights.", true, true);
                return true;
            }
            case 11 -> {
                say("But no one starts as a strong Knight on day one. The Empress didn''t want someone strong. She wanted someone with courage whom she could develop into a strong Knight through rigorous training. So, you should first become a Knight-in-Training. We'll talk about your missions when you get to that point.", true, true);
                return true;
            }
            case 12 -> {
                say("Take the portal on the left to reach the Training Forest. There, you will find #p1102000#, the Training Instructor, who will teach you how to become stronger. I don''t want to find you wandering around aimlessly until you reach Lv. 10, you hear?", true, false);
                safeDispose();
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
