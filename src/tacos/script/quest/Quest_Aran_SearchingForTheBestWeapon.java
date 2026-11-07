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
        // TODO : 文章 (js の英文を翻訳)
        say("鉾がほしいだと？はっ！とても強そうには見えないな。お前には無理だ。鉾がほしいなら、ここから西にいる#r#o9001012##kを狩って#b#t" + this.item_proof + "##kを30個集め、俺が間違っていると証明してみせろ！", false, true);
        forceStartQuest();
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                say("はっ！お前の実力はよくわかった…望みのものをくれてやろう。最高の鉾だ！", false, true);
                return true;
            }
            case 1 -> {
                if ((getLevel() - this.required_level) * 3 < getRemainingSp()) {
                    // TODO : 文章 (js の英文を翻訳)
                    say("まだ#bSP#kが残りすぎているな。それでは新しい称号は得られないぞ。1次と2次のスキルにもっとSPを使うことを強く勧める。", false, true);
                    return false;
                }
                // TODO : 文章 (js の英文を翻訳)
                say(2, "記憶がよみがえってくる…", false, true);
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
                // TODO : 文章 (js の英文を翻訳)
                say("はは！望みのものは手に入れただろう、さあ行け！");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
