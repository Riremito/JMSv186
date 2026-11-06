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
// Quest.wz endscript : q20104e, q20105e (JMS186), ナイトウォーカーの道, ストライカーの道, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Cygnus_FirstJobAdvancement extends QuestScriptMan {

    private final int quest_night_walker = 20104; // ナイトウォーカーの道
    private final int job_night_walker = 1400;
    private final int job_striker = 1500;
    private final int item_night_walker_claw = 1472061; // 初歩盗賊の篭手
    private final int item_night_walker_star = 2070015; // 初歩盗賊の手裏剣
    private final int item_striker_knuckle = 1482014; // 初心者海賊のナックル
    private final int item_medal = 1142066; // 見習い騎士の勲章

    @Override
    public boolean questEnd(int status, int answer) {
        boolean night_walker = (getQuestId() == this.quest_night_walker);
        // いいえを選択した
        if (getLastAction() == 0 && status == -1) {
            say("This is an important decision to make.", false, true);
            safeDispose();
            return true;
        }
        switch (status) {
            case 0 -> {
                askYesNo("Have you made your decision? The decision will be final, so think carefully before deciding what to do. Are you sure you want to become a " + (night_walker ? "Night Walker" : "Striker") + "?");
                return true;
            }
            case 1 -> {
                say("I have just molded your body to make it perfect for a Soul Master. If you wish to become more powerful, use Stat Window (S) to raise the appropriate stats. If you arn't sure what to raise, just click on #bAuto#k.", false, true);
                int job = night_walker ? this.job_night_walker : this.job_striker;
                if (getJob() != job) {
                    if (night_walker) {
                        gainItem(this.item_night_walker_claw, 1);
                        gainItem(this.item_night_walker_star, 800);
                        gainItem(this.item_night_walker_star, 800);
                    } else {
                        gainItem(this.item_striker_knuckle, 1);
                    }
                    gainItem(this.item_medal, 1);
                    expandInventory(1, 4);
                    expandInventory(4, 4);
                    changeJob(job);
                }
                forceCompleteQuest();
                questEndEffect();
                return true;
            }
            case 2 -> {
                say("I have also expanded your inventory slot counts for your equipment and etc. inventory. Use those slots wisely and fill them up with items required for Knights to carry.", true, true);
                return true;
            }
            case 3 -> {
                say("I have also given you a hint of #bSP#k, so open the #bSkill Menu#k to acquire new skills. Of course, you can't raise them at all once, and there are some skills out there where you won't be able to acquire them unless you master the basic skills first.", true, true);
                return true;
            }
            case 4 -> {
                say("Unlike your time as a Nobless, once you become the Soul Master, you will lost a portion of your EXP when you run out of HP, okay?", true, true);
                return true;
            }
            case 5 -> {
                say("Now... I want you to go out there and show the world how the Knights of Cygnus operate.", true, true);
                safeDispose();
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
