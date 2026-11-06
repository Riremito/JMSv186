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
// Quest.wz startscript : q21012s, endscript : q21012e (JMS186), 消えた能力, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_MissingAbility extends QuestScriptMan {

    private final int item_red_potion = 2000022; // リエン特製赤い薬
    private final int item_blue_potion = 2000023; // リエン特製青い薬
    private final int reward_exp = 57;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            say("Hm... You don''t think that would help? Think about it. It could help, you know...", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Welcome hero! What's that? You want to know how I knew who you were? That's easy. I eavesdropped on some people talking loudly next to me. I''m sure the rumor has spread through the entire island already. Everyone knows that you've returned!", false, true);
                return true;
            }
            case 1 -> {
                say("Anyway, what's with the long face? Is something wrong? Hm? You''re not sure whether you''re really a hero or not? You lost your memory?! No way... It must be because you were trapped inside the ice for hundreds and hundreds of years.", true, true);
                return true;
            }
            case 2 -> {
                askAccept("Hm, how about you trying out that sword? Wouldn't that bring back some memories? How about #bfighting some monsters#k?");
                return true;
            }
            case 3 -> {
                forceStartQuest();
                say("It just so happens that there are a lot of #r#o9300383#s#k near here. How about defeating just #r3#k of them? It could help you remember a thing or two.", false, true);
                return true;
            }
            case 4 -> {
                say(1, "Ah, you''ve also forgotten how to use your skills? #bPlace skills in the quick slots for easy access.#k You can also place consumable items in the slots, so use the slots to your advantage.", true, true);
                return true;
            }
            case 5 -> {
                tutorMsg(17);
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == -1) {
            say("What? You don't want the potion?", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askYesNo("Hm... Your expression tells me that the exercise didn''t jog any memories. But don''t you worry. They''ll come back, eventually. Here, drink this potion and power up! \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_red_potion + "# 10 #t" + this.item_red_potion + "# \r\n#i" + this.item_blue_potion + "# 10 #t" + this.item_blue_potion + "# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp");
                return true;
            }
            case 1 -> {
                gainItem(this.item_red_potion, 10);
                gainItem(this.item_blue_potion, 10);
                gainExp(this.reward_exp);
                forceCompleteQuest();
                questEndEffect();
                // js の sendOkS は type が反映されない
                say(2, "#b(Even if you''re really the hero everyone says you are... What good are you without any skills?)#k", false, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
