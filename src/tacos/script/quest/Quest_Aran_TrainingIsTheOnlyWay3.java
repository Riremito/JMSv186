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
// Quest.wz startscript : q21703s, endscript : q21703e (JMS186), 修行だけが唯一の道！3, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_TrainingIsTheOnlyWay3 extends QuestScriptMan {

    private final int skill_combo_ability = 21000000; // コンボアビリティ
    private final int skill_master_level = 10;
    private final int reward_exp = 2800;

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == 5) {
            say("I know it takes an incredible amount of strength and will to outdo your instructor, but you weren't meant to let yourself wither away. You must move on to bigger and better things! You must do everything you can to embrace your heroic nature!", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Your abilities are really beginning to take shape. I am surprised that an old man like me was able to help you. I''m tearing up just thinking about how happy it makes me to have been of assistance to you. *Sniff sniff*", false, true);
                return true;
            }
            case 1 -> {
                say(2, "#b(You didn't even train that long with him... Why is he crying?)#k", true, true);
                return true;
            }
            case 2 -> {
                say("Alright, here''s the third and the final stage of training. Your last opponent is... #r#o9300343#s#k! Do you know anything about #o1210100#s?", true, true);
                return true;
            }
            case 3 -> {
                say(2, "Well, a little bit...", true, true);
                return true;
            }
            case 4 -> {
                say("They are natural warriors! They''re born with a voracious appetite for food. They devour any food that''s visible the moment they sweep by. Terrifying, isn''t it?", true, true);
                return true;
            }
            case 5 -> {
                say(2, "#b(Is that really true?)#k", true, true);
                return true;
            }
            case 6 -> {
                askAccept("Okay, now... #bEnter the Training Center again#k, defeat #r30#k #o9300343#s, and show me what you're made of! You'll have to exert all your energy to defeat them! Go, go, go! Rise above me!");
                return true;
            }
            case 7 -> {
                forceStartQuest();
                say("Now go and take on those monstrous #o9300343#s!");
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == 1) {
            say("Are you reluctant to leave your instructor? *Sniff sniff* I'm so moved, but you can't stop here. You are destined for bigger and better things!", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Ah, you've come back after defeating all 30 #o9300343#s. I knew you had it in you... Even though you have no memories and few abilities, I could see that you were different! How? Because you're carrying around a Polearm, obviously!", false, true);
                return true;
            }
            case 1 -> {
                say(2, "#b(Is he pulling your leg?)#k'", true, true);
                return true;
            }
            case 2 -> {
                askYesNo("I have nothing more to teach you, as you've surpassed my level of skill. Go now! Don't look back! This old man is happy to have served as your instructor.");
                return true;
            }
            case 3 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    forceCompleteQuest();
                    questEndEffect();
                    teachSkill(this.skill_combo_ability, getSkillLevel(this.skill_combo_ability), this.skill_master_level);
                    gainExp(this.reward_exp);
                }
                say(2, "(You remembered the #bCombo Ability#k skill! You were skeptical of the training at first, since the old man suffers from Alzheimer's and all, but boy, was it effective!)", false, true);
                avatarOriented("Effect/BasicEff.img/AranGetSkill");
                return true;
            }
            case 4 -> {
                say("Now report back to #p1201000#. I know she'll be ecstatic when she sees the progress you've made!", true, false);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
