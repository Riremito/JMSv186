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
// Quest.wz startscript : q21013s, endscript : q21013e (JMS186), 英雄のためのプレゼント, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_GiftForTheHero extends QuestScriptMan {

    private final int item_chair = 3010062; // バンブーチェア
    private final int item_bamboo = 4032309; // 青竹
    private final int item_wood = 4032310; // 木材
    private final int reward_exp = 95;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            say("I'm sure it will come in handy during your journey. Please, don't decline my offer.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askMenu("Ah, you're the hero. I've been dying to meet you.  \r\n#b#L0#(Seems a bit shy...)#l");
                return true;
            }
            case 1 -> {
                askAccept("I have something I''ve been wanting to give you as a gift for a very long time... I know you''re busy, especially since you''re on your way to town, but will you accept my gift?");
                return true;
            }
            case 2 -> {
                forceStartQuest();
                say(1, "The parts of the gift have been packed inside a box nearby. Sorry to trouble you, but could you break the box and bring me a #b#t" + this.item_bamboo + "##k and some #b#t" + this.item_wood + "##k? I''ll assemble them for you right away.", false, true);
                return true;
            }
            case 3 -> {
                tutorMsg(18);
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
                say("Ah, you've brought all the components. Give me a few seconds to assemble them... Like this... And like that... and... \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_chair + "# 1 #t" + this.item_chair + "# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp", false, true);
                return true;
            }
            case 1 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    gainItem(this.item_chair, 1);
                    gainExp(this.reward_exp);
                    forceCompleteQuest();
                    questEndEffect();
                }
                say(1, "Here, a fully-assembled chair, just for you! I''ve always wanted to give you a chair as a gift, because I know a hero can occasionally use some good rest. Tee hee.", true, true);
                return true;
            }
            case 2 -> {
                say(1, "A hero is not invincible. A hero is human. I''m sure you will face challenges and even falter at times. But you are a hero because you have what it takes to overcome any obstacles you may encounter.", true, true);
                return true;
            }
            case 3 -> {
                tutorMsg(19);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
