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
            // TODO : 文章 (js の英文を翻訳)
            say("ふむ、#p1013101#ならすぐにやってくれただろうに。", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("ここ数日、農場の#o1210100#の様子がおかしいんだ。わけもなく怒ったり、いらいらしたりしている。心配になって今朝早く農場に来てみたら、案の定、何匹かの#o1210100#が垣根を越えて逃げ出してしまったようなんだ。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("#o1210100#を探しに行く前に、壊れた垣根を直さないといけない。幸い、それほどひどく壊れてはいないんだ。#t" + this.item_board + "#が少しあればすぐに直せる。#b#t" + this.item_board + "##kを#b3個#k持ってきてくれないか、エヴァン？");
                return true;
            }
            case 2 -> {
                forceStartQuest();
                // TODO : 文章 (js の英文を翻訳)
                say("おお、助かるよ。#b#t" + this.item_board + "##kは近くにいる#r#o0130100##kが持っているはずだ。それほど強くはないが、危なくなったらスキルやアイテムを使うんだぞ。", false, true);
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
                // TODO : 文章 (js の英文を翻訳)
                say("おお、#t" + this.item_board + "#を全部持ってきてくれたのか。さすがは私の子だ！お礼に何をあげようか…ええと…そうだ！ \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i" + this.item_chair + "# 1 #t" + this.item_chair + "# \r\n#i" + this.item_milk + "# 15 #t" + this.item_milk + "#s \r\n#i" + this.item_juice + "# 15 #t" + this.item_juice + "#s \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " 経験値", false, true);
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
                // TODO : 文章 (js の英文を翻訳)
                say("ほら、垣根を直したときに余った木の板で、新しい椅子を作ったんだ。大したものには見えないかもしれないが、丈夫だぞ。きっと役に立つはずだ。", true, true);
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
