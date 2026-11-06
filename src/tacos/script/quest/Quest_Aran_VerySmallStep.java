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
// TODO : 文章 (js の英文のまま)
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
                say(8, "How did the training go? The Penguin Teacher #p1202006# likes to exaggerate and it worried me knowing that he has bouts of Alzheimer's, but I'm sure he helped you. He's been studying the skills of heroes for a very long time.", false, true);
                return true;
            }
            case 1 -> {
                say(2, "#b(You tell her that you were able to remember the Combo Ability skill.)#k", true, true);
                return true;
            }
            case 2 -> {
                askAccept("That's great! Honestly, though, I think it has less to do with the method of #p1202006#'s training and more to do with your body remembering its old abilities. #bI'm sure your body will remember more skills as you continue to train#k!  \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# " + this.reward_exp + " exp");
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
