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
// Quest.wz endscript : q21720e (JMS186), 人形使いの警告, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
// js ではクエストの状態が 0 の場合にスキルを習得する条件のため完了時にスキルを習得できないが、21703 と同じ進行中 (1) の条件にする
public class Quest_Aran_PuppeteersWarning extends QuestScriptMan {

    private final int skill_booster = 21001003; // ブースター(鉾)
    private final int skill_master_level = 20;
    private final int reward_exp = 3900;

    @Override
    public boolean questEnd(int status, int answer) {
        // いいえを選択した
        if (getLastAction() == 0 && status == 5) {
            return false;
        }
        switch (status) {
            case 0 -> {
                say(8, "What can I do for you? Tru sent me a message saying that you've been training diligently in Victoria Island while helping him with his work. What is it? What? The Black Wings?", false, true);
                return true;
            }
            case 1 -> {
                say(2, "#b(You tell her about the Puppeteer and the Black Wings, and about their mission.)#k", true, true);
                return true;
            }
            case 2 -> {
                say(8, "I see... I didn''t know there was a group called the Black Wings... They must be fools if they''re trying to revive the Black Mage, knowing how dangerous he is.", true, true);
                return true;
            }
            case 3 -> {
                say(2, "That...that''s true...\r\r#b(She''s definitely not afraid to speak her mind.)#k", true, true);
                return true;
            }
            case 4 -> {
                say(8, "The Book of Prophecy states that the hero will revive and fight against the Black Mage. I wasn''t sure if that was true, but this confirms that the Black Mage is still around.", true, true);
                return true;
            }
            case 5 -> {
                say(2, "Aren't you scared?", true, true);
                return true;
            }
            case 6 -> {
                askYesNo("Scared? Pfft. Who cares if the Black Mage appears. You'll be here to protect us. If anything, this makes me want to prepare you for the big battle. Ah, that reminds me, I found a #bskill#k. Would you like to see it?");
                return true;
            }
            case 7 -> {
                if (getQuestStatus(getQuestId()) == 1) {
                    forceCompleteQuest();
                    questEndEffect();
                    teachSkill(this.skill_booster, getSkillLevel(this.skill_booster), this.skill_master_level);
                    gainExp(this.reward_exp);
                }
                avatarOriented("Effect/BasicEff.img/AranGetSkill");
                say(2, "#b(You remembered the Polearm Booster skill!)#k", false, true);
                return true;
            }
            case 8 -> {
                say(8, "This skill was found in an ancient incomprehensible script. I had a hunch it might be a skill you used in the past, and I think I was right. You're not as strong as you used to be, but you'll get there, in time.", true, true);
                return true;
            }
            case 9 -> {
                say(8, "You are steadily becoming more powerful, and I''ll be here to keep motivating you. You have nothing to be afraid of. You will not lose the battle. You didn''t emerge from ice only to lose to the Black Mage, did you? This time, you''ll finish him, once and for all!", true, true);
                return true;
            }
            case 10 -> {
                say(8, "To do so, there''s only one thing you can do. Train, train, train. Head to Victoria Island and continue training. Let''s make sure you become so powerful that the Black Mage doesn''t stand a chance!", true, false);
                return true;
            }
            default -> {
            }
        }
        return false;
    }
}
