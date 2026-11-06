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
// Quest.wz startscript : q22002s, endscript : q22002e (JMS186), ランチはサンドイッチ, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_SandwichForLunch extends QuestScriptMan {

    private final int item_sandwich = 2022620; // 手作りサンドイッチ
    private final int item_hat = 1003028; // 麦わら帽子
    private final int item_milk = 2022621; // 美味しい牛乳
    private final int item_juice = 2022622; // 美味しいジュース
    private final int reward_exp = 60;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            say("Oh, what? Aren't you going to have breakfast? Breakfast is the most important meal of the day! Talk to me again if you change your mind. If you don't, I'm going to eat it myself.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Did you feed #p1013102#? You should have some breakfast now then, Evan. Today's breakfast is a #t" + this.item_sandwich + "#. I've brought it with me. Hee hee. I was going to eat it myself if you didn't agree to feed #p1013102#.", false, true);
                return true;
            }
            case 1 -> {
                askAccept("Here, I'll give you this #bSandwich#k, so #bgo talk to mom when you finish eating#k. She says she has something to tell you.");
                return true;
            }
            case 2 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    forceStartQuest();
                    gainItem(this.item_sandwich, 1);
                }
                say(3, "#b(Mom has something to say? Eat your #t" + this.item_sandwich + "# and head back inside the house.)#k", true, true);
                return true;
            }
            case 3 -> {
                evanTutorial("UI/tutorial/evan/3/0");
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
                say("Did you eat your breakfast, Evan? Then, will you do me a favor?\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i" + this.item_hat + "# 1 #t" + this.item_hat + "#  \r\n#i" + this.item_milk + "# 5 #t" + this.item_milk + "#s \r\n#i" + this.item_juice + "# 5 #t" + this.item_juice + "# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp", false, true);
                return true;
            }
            case 1 -> {
                forceCompleteQuest();
                questEndEffect();
                evanTutorial("UI/tutorial/evan/4/0");
                gainItem(this.item_hat, 1);
                gainItem(this.item_milk, 5);
                gainItem(this.item_juice, 5);
                gainExp(this.reward_exp);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
