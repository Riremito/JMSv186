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
// Quest.wz startscript : q22004s, endscript : q22004e (JMS186), 垣根の修理, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Evan_FixingTheFence extends QuestScriptMan {

    private final int item_board = 4032498; // 木の切れ端
    private final int item_board_count = 3;
    private final int item_chair = 3010097; // 丈夫なウッドチェア
    private final int item_milk = 2022621; // 美味しい牛乳
    private final int item_juice = 2022622; // 美味しいジュース
    private final int reward_exp = 210;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            say("Hm, #p1013101# would have done it at the drop of a hat.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("The #o1210100#s at the farm have been acting strange these past couple days. They've been angry and irritable for no reason. I was worried so I came out to the farm early this morning and sure enough, it seems like a few of these #o1210100#s got past the fence.", false, true);
                return true;
            }
            case 1 -> {
                askAccept("Before I go and find the #o1210100#s, I should mend the broken fence. Luckily, it wasn't damaged too badly. I just need a few #t" + this.item_board + "#es to fix it right up. Will you bring me #b3#k #b#t" + this.item_board + "##k, Evan?");
                return true;
            }
            case 2 -> {
                forceStartQuest();
                say("Oh, that's very nice of you. You'll be able to find #b#t" + this.item_board + "#es#k from the nearby #r#o0130100#s#k. They're not too strong, but use your skills and items when you find yourself in danger.", false, true);
                return true;
            }
            case 3 -> {
                evanTutorial("UI/tutorial/evan/6/0");
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
                say("Ah, did you bring all the #t" + this.item_board + "#  That's my kid! What shall I give you as a reward... Let's see... Oh, right! \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_chair + "# 1 #t" + this.item_chair + "# \r\n#i" + this.item_milk + "# 15 #t" + this.item_milk + "#s \r\n#i" + this.item_juice + "# 15 #t" + this.item_juice + "#s \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp", false, true);
                return true;
            }
            case 1 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    forceCompleteQuest();
                    questEndEffect();
                    removeItem(this.item_board, this.item_board_count);
                    gainItem(this.item_chair, 1);
                    gainItem(this.item_milk, 15);
                    gainItem(this.item_juice, 15);
                    gainExp(this.reward_exp);
                }
                say("Here, I made this new chair from the wooden boards I had left over after fixing the fence. It may not seem like much, but it's sturdy. I'm sure it'll come in handy.", true, true);
                return true;
            }
            case 2 -> {
                evanTutorial("UI/tutorial/evan/7/0");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
