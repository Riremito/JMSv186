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
// Quest.wz startscript : q20408s (JMS186), 女王の騎士団長 (4次転職), 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Cygnus_CaptainKnight extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == -1) {
            say("I guess you are not ready to tackle on the responsibilities of an official knight.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askYesNo("You've saved Erev. Do you want to become a Captain Knight?");
                return true;
            }
            case 1 -> {
                forceCompleteQuest();
                int job = getJob();
                if (job == 1111 || job == 1211 || job == 1311 || job == 1411 || job == 1511) {
                    changeJob(job + 1);
                }
                say("You are now an official knight of the Knights of Cygnus.", false, true);
                return true;
            }
            default -> {
            }
        }
        // js では status 2 の処理がなく、status 3 のメッセージは表示されない
        return false;
    }
}
