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
// Quest.wz startscript : q21000s, q21001s, endscript : q21001e (JMS186), 子供を助けてください1, 子供を助けてください2, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_RescueTheChild extends QuestScriptMan {

    private final int quest_rescue_1 = 21000; // 子供を助けてください1
    private final int item_child = 4001271; // はぐれた子供
    private final int map_forest = 914000300; // ブラックロード : 森の最奥
    private final int map_movie = 914090010; // アランの動画 (マップ名なし)
    private final int map_ice_cave = 140090000; // スノーアイランド : 氷洞窟

    @Override
    public boolean questStart(int status, int answer) {
        if (getQuestId() == this.quest_rescue_1) {
            // 受諾しなかった
            if (getLastAction() == 0 && status == -1) {
                say("No, Aran... There's no point of leaving here if we're to just leave the kid here all by himself. I know it's a lot to ask... but please reconsider!", false, true);
                return false;
            }
            switch (status) {
                case 0 -> {
                    askAccept("Wait, where's the kid? Oh no, he must be stuck in the forest! We need to bring the kid back here before the ark leaves! Aran... please go in there and find the kid for me! I know it's a lot to ask considering you're injured... but you're our only hope!");
                    return true;
                }
                case 1 -> {
                    // TODO : js ではクエストのデータに文字化けした値 ("..w?PGﾃ・・") を設定している
                    forceStartQuest();
                    // js では status 1 が重複しており "The most important thing right now is not to panic, Aran. ..." は表示されない
                    say("#bThe kid is probably somewhere deep in the forest#k! We need to leave right now before the Black Wizard finds us, so please hurry!", false, true);
                    return true;
                }
                case 2 -> {
                    say("Please rescue the kid from the forest, Aran! We cannot afford any more casualties at the hands of the Black Wizard!", true, true);
                    return true;
                }
                case 3 -> {
                    avatarOriented("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
                    return false;
                }
                default -> {
                }
            }
            return false;
        }
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            say("Nooooo! Aran wants to leave me alone here!!..", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askAccept("O.(Shivering) I...was...so...scared...in...here. Please take me to Athena Pierce!");
                return true;
            }
            case 1 -> {
                if (getQuestStatus(getQuestId()) == 0) {
                    gainItem(this.item_child, 1);
                    forceStartQuest();
                }
                warp(this.map_forest);
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
        if (getLastAction() == 0 && status == -1) {
            say("Where's the kid? If you brought the kid with you, hand him over to me!", false, true);
            return false;
        }
        // 動画を見る
        if (getLastAction() == 0 && status == 7) {
            if (haveItem(this.item_child, 1)) {
                removeItem(this.item_child, 1);
            }
            setStandAloneMode(true);
            setDirectionMode(true);
            forceCompleteQuest();
            questEndEffect();
            warp(this.map_movie);
            return false;
        }
        switch (status) {
            case 0 -> {
                askYesNo("Ahhh, you're safe! What about the kid? Where's the kid?");
                return true;
            }
            case 1 -> {
                say("..Oh phew... thank goodness..", false, true);
                return true;
            }
            case 2 -> {
                say(3, "Get on board right now! We do not have much time!", true, true);
                return true;
            }
            case 3 -> {
                say("Yes, yes. We don't have much time for that. I can feel the force of Black Wizard creeping up ever so close, and I have a feeling the Wizard has located the ark! If we don't leave now, then we will be attacked!", true, true);
                return true;
            }
            case 4 -> {
                say(3, "Leave now!", true, true);
                return true;
            }
            case 5 -> {
                say("Aran! Get on board right now! I understand that you want to join them in the battle, but... it's too late! Let your friends take care of the Black Wizard, and you should get on right now and escape to Victoria Island!", true, true);
                return true;
            }
            case 6 -> {
                say(3, "No, I can't do that!", true, true);
                return true;
            }
            case 7 -> {
                say(3, "Athena Pierce, you take care of these people and head over to Victoria Island. I promise you, I will not die. I will meet you there at the island soon. I better help my friends out and battle the Black Wizard once and for all!", true, true);
                return true;
            }
            case 8 -> {
                askYesNo("Would you like to skip the video clip?  Even if you skip the scene, game play will not be affected.");
                return true;
            }
            case 9 -> {
                // 動画を見ない
                if (haveItem(this.item_child, 1)) {
                    removeItem(this.item_child, 1);
                }
                forceCompleteQuest();
                questEndEffect();
                warp(this.map_ice_cave);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
