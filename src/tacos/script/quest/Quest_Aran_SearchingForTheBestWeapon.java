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
// Quest.wz startscript : q21202s, endscript : q21202e (JMS186), 最高の武器を探して, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_SearchingForTheBestWeapon extends QuestScriptMan {

    private final int job_aran_2 = 2110; // アラン 2次
    private final int item_medal = 1142130; // 記憶の中のアランの勲章
    private final int item_proof = 4032311; // 資格の印
    private final int item_proof_count = 30;
    private final int quest_mirror = 21201; // 念願の鏡
    private final int required_level = 30;
    private final int[] skills = {
        21001003, // ブースター(鉾)
        21000000, // コンボアビリティ
        21100000, // マスタリー(鉾)
        21100002, // ファイナルチャージ
        21100004, // コンボスマッシュ
        21100005, // コンボドレイン
        21110002, // フルスイング
    };

    @Override
    public boolean questStart(int status, int answer) {
        say("You want a pole arm? Hah! You don't look strong at all. Way outta your league. If you want a pole arm, prove me wrong by hunting #r#o9001012#s#k to the west of here, and find 30 #b#t" + this.item_proof + "##k!", false, true);
        forceStartQuest();
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                say("Hah! You have proven your worth.. and you shall get what you want; the best pole arm possible!", false, true);
                return true;
            }
            case 1 -> {
                if ((getLevel() - this.required_level) * 3 < getRemainingSp()) {
                    say("You still have way too much #bSP#k with you. You can't earn a new title like that. I strongly urge you to use more SP on your 1st and second level skills.", false, true);
                    return false;
                }
                say(2, "My memories are returning...", false, true);
                changeJob(this.job_aran_2);
                gainItem(this.item_medal, 1);
                removeItem(this.item_proof, this.item_proof_count);
                forceCompleteQuest(this.quest_mirror);
                for (int skill_id : this.skills) {
                    teachSkill(skill_id, getSkillLevel(skill_id));
                }
                forceCompleteQuest();
                questEndEffect();
                return true;
            }
            case 2 -> {
                say("Haha! You've got what you want, now leave!");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
