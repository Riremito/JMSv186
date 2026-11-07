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
// Quest.wz startscript : q21704s (JMS186), とても小さな一歩, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_VerySmallStep extends QuestScriptMan {

    private final int reward_exp = 500;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 1) {
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(8, "修行はどうでしたか？ペンギン先生の#p1202006#は大げさなところがありますし、物忘れがひどいので心配していたのですが、きっと役に立ったはずです。彼は長い間、英雄たちのスキルを研究してきたのですから。", false, true);
                return true;
            }
            case 1 -> {
                // TODO : 文章 (js の英文を翻訳)
                say(2, "#b（コンボアビリティのスキルを思い出したことを伝えた。）#k", true, true);
                return true;
            }
            case 2 -> {
                // TODO : 文章 (js の英文を翻訳)
                askAccept("よかったですね！でも正直なところ、#p1202006#の修行の方法というよりは、あなたの体が昔の能力を覚えていたからだと思います。#b修行を続ければ、体はもっと多くのスキルを思い出すはずです#k！  \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0#" + this.reward_exp + " 経験値");
                return true;
            }
            case 3 -> {
                forceCompleteQuest();
                questEndEffect();
                gainExp(this.reward_exp);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
