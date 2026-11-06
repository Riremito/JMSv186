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
// Quest.wz startscript : q20311s - q20315s (JMS186), 神獣の涙 (3次転職), 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Cygnus_TearOfTheDivineBird extends QuestScriptMan {

    private final int quest_soul_master = 20311; // 神獣の涙(ソウルマスター) (20311 - 20315)
    private final int job_soul_master = 1111; // ソウルマスター 3次 (1111, 1211, 1311, 1411, 1511)
    private final int item_medal = 1142068; // 上級騎士の勲章
    private final int required_level = 70;

    @Override
    public boolean questStart(int status, int answer) {
        int job = this.job_soul_master + (100 * (getQuestId() - this.quest_soul_master));
        // いいえを選択した
        if (getLastAction() == 0 && status == 0) {
            say("You are not ready yet.", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("The jewel you brought back from the Transformer is the tear of the Divine Bird. It's the crystal of it's power. If the Black Wizard has his hands on this, then spells doom for all of us.", false, true);
                return true;
            }
            case 1 -> {
                askYesNo("For your effort in preventing a potentially serious disaster, the Godess has bestowed upon a new title for you. Are you ready to accept it?");
                return true;
            }
            case 2 -> {
                if ((getLevel() - this.required_level) * 3 < getRemainingSp()) {
                    say("You still have way too much #bSP#k with you. You can't earn a new title like that. I strongly urge you to use more SP on your 1st and second level skills.", false, true);
                    return false;
                }
                if (!canHold(this.item_medal, 1)) {
                    say("Please make space in your inventory.");
                    return false;
                }
                gainItem(this.item_medal, 1);
                changeJob(job);
                gainAp(5);
                say(", as of this moment, you are now the Knight Sergeant. From this moment on, you shall carry yourself with dignity and respect befitting your new title The Knight Sergeant of Knights of cygnus. May your glory shines as bright as it is right now.");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
