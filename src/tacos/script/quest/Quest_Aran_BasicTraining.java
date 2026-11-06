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
// Quest.wz startscript : q21015s - q21018s (JMS186), 基礎体力鍛錬1, 基礎体力鍛錬2, 基礎体力鍛錬3, 基礎体力テスト, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_BasicTraining extends QuestScriptMan {

    private final int quest_training_1 = 21015; // 基礎体力鍛錬1
    private final int quest_training_2 = 21016; // 基礎体力鍛錬2
    private final int quest_training_3 = 21017; // 基礎体力鍛錬3

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_training_1) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == 1) {
                say("What are you so hesitant about? You're a hero! You gotta strike while the iron is hot! Come on, let''s do this!", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    say("Alright, I've done enough explaining for now. Let''s move on to the next stage. What's the next stage, you ask? I just told you. Train as hard as you can until you become strong enough to defeat the Black Mage with a single blow.", false, true);
                    return true;
                }
                case 1 -> {
                    say("You may have been a hero in the past, but that was hundreds of years ago. Even if it weren''t for the curse of the Black Mage, all those years you spent frozen in time have stiffened your body. You must loosen up a bit and slowly regain your agility. How do you do that, you ask?", true, true);
                    return true;
                }
                case 2 -> {
                    askAccept("Don''t you know that you must first master the fundamentals? So the wise thing to do is to begin with #bBasic Training#k. Oh, of course, I forgot that you lost your memory. Well, that''s why I''m here. You''ll just have to experience it yourself. Shall we begin?");
                    return true;
                }
                case 3 -> {
                    forceStartQuest();
                    avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        if (getQuestId() == this.quest_training_2) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == -1) {
                say("Are you not ready to hunt the #o0100132#s yet? Always proceed if and only if you are fully ready. There''s nothing worse than engaging in battles without sufficient preparation.", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    askAccept("Shall we continue with your Basic Training? Before accepting, please make sure you have properly equipped your sword and your skills and potions are readily accessible.");
                    return true;
                }
                case 1 -> {
                    forceStartQuest();
                    avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        if (getQuestId() == this.quest_training_3) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == 4) {
                say(2, "#b(You declined out of fear, but it''s not like you can run away like this. Take a big breath, calm down, and try again.)#k", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    say(8, "It seems like you''re warmed up now. This is when rigorous training can really help you build a strong foundation. Let''s proceed with the Basic Training, shall we?", false, true);
                    return true;
                }
                case 1 -> {
                    say(8, "Go defeat some #r#o0100133#s#k in #b#m140020200##k this time. I think about  #r20#k should do it. Go on ahead and... Hm? Do you have something you''d like to say?", true, true);
                    return true;
                }
                case 2 -> {
                    say(2, "Isn''t the number getting bigger and bigger?", true, true);
                    return true;
                }
                case 3 -> {
                    say(8, "Of course it is. What, are you not happy with 20? Would you like to defeat 100 of them instead? Oh, how about 999 of them? Someone in Sleepywood would be able to do it easily. After all, we are training...", true, true);
                    return true;
                }
                case 4 -> {
                    say(2, "Oh no, no, no. Twenty is plenty", true, true);
                    return true;
                }
                case 5 -> {
                    askAccept("You don't have to be so modest. I understand your desire to quickly become the hero you once were. This sort of attitude is what makes you a hero.");
                    return true;
                }
                case 6 -> {
                    forceStartQuest();
                    say(2, "#b(You accepted, thinking you might end up having to 999 of them if you let her keep talking.)#k", false, true);
                    return true;
                }
                case 7 -> {
                    say(8, "Please go ahead and slay 20 #o0100133#s.", true, true);
                    return true;
                }
                case 8 -> {
                    avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 基礎体力テスト
        // 受諾しなかった
        if (getLastAction() == 0 && status == 0) {
            say("Oh, is 5 not enough? If you feel the need to train further, please feel free to slay more than that. If you slay all of them, I''ll just have to look the other way even if it breaks my heart, since they will have been sacrificed for a good cause...", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                say("Now, you will undergo a test that will determine whether you''re fit or not. All you have to do is take on the most powerful monster on this island, #o0100134#s. About #r50#k of them would suffice, but...", false, true);
                return true;
            }
            case 1 -> {
                askAccept("We can't have you wipe out the entire population of #o0100134#s, since they aren't many of them out there. How about 5 of them? You're here to train, not to destroy the ecosystem.");
                return true;
            }
            case 2 -> {
                forceStartQuest();
                avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
