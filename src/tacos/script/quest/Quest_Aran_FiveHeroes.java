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
// Quest.wz startscript : q21100s (JMS186), 5人の英雄, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_FiveHeroes extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 5) {
            // 原文ママ
            say("Oh, is 5 not enough? If you feel the need to train further, please feel free to slay more than that. If you slay all of them, I''ll just have to look the other way even if it breaks my heart, since they will have been sacrificed for a good cause...", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say(8, "There isn't much record left of the heores that fought against the Black Mage. Even in the Book of Prophecy, the only information available is that there were five of them. There is nothing about who they were or what they looked like. Is there anything you remember? Anything at all?", false, true);
                return true;
            }
            case 1 -> {
                say(2, "I don't remember a thing...", true, true);
                return true;
            }
            case 2 -> {
                say(8, "As I expected. Of course, the curse of the Black Mage was strong enough to wipe out all of your memory. But even if that''s the case, there has got to be a point where the past will uncover, especially now that we are certain you are one of the heroes. I know you've lost your armor and weapon during the battle but... Oh, yes, yes. I almost forgot! Your #bweapon#k!", true, true);
                return true;
            }
            case 3 -> {
                say(2, "My weapon?", true, true);
                return true;
            }
            case 4 -> {
                say(8, "I found an incredible weapon while digging through blocks of ice a while back. I figured the weapon belonged to a hero, so I brought it to town and placed it somewhere in the center of the town. Haven't you seen it? #bThe #p1201001##k... \r\r#i4032372#\r\rIt looks like this...", true, true);
                return true;
            }
            case 5 -> {
                say(2, "Come to think of it, I did see a #p1201001# in town.", true, true);
                return true;
            }
            case 6 -> {
                askAccept("Yes, that's it. According to what's been recorded, the weapon of a hero will recognize its rightful owner, and if you''re the hero that used the #p1201001#, the #p1201001# will react when you grab the #p1201001#. Please go find the #b#p1201001# and click on it.#k");
                return true;
            }
            case 7 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    forceCompleteQuest();
                    questEndEffect();
                }
                // js の sendOkS は type が反映されない
                say(8, "If the #p1201001# reacts to you, then we'll know that you're #bAran#k, the hero that wielded a #p1201001#.", false, false);
                showReservedEffect("Effect/Direction1.img/aranTutorial/ClickPoleArm");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
