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
// Quest.wz startscript : q21200s, endscript : q21200e, q21201e (JMS186), 主を待つ武器, 念願の鏡, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Aran_WeaponAwaitingItsMaster extends QuestScriptMan {

    private final int quest_weapon = 21200; // 主を待つ武器
    private final int quest_best_weapon = 21202; // 最高の武器を探して
    private final int quest_unknown = 21203; // TODO : JMS186 には存在しないクエスト (js のまま)
    private final int map_movie = 914090200; // アランの動画 (マップ名なし)

    @Override
    public boolean questStart(int status, int answer) {
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            say("It's really urgent, and you'll regret it if you refuse to. #bIt has something to do with your pole arm,#k which means it has to do with your past. Who knows...? Maybe the pole arm is key to reawakening your abilities...?", false, true);
            return false;
        }
        switch (status) {
            case 0 -> {
                askAccept("How's the training going? Wow, looking at you, I can tell your levels have shot through the roof. That's amazing... well, anyway, I see that you'r busy, but you'll have to return to the island for a bit.");
                return true;
            }
            case 1 -> {
                // TODO : js ではクエストのデータに "3" を設定している (用途不明)
                forceStartQuest(getQuestId(), "3");
                forceCompleteQuest();
                questEndEffect();
                forceStartQuest(this.quest_best_weapon);
                forceStartQuest(this.quest_unknown, "0");
                say("Your #bGiant Pole Arm#k that's being kept in #bRien#k is acting strange all of a sudden. According to the book, the pole arm reacts like this when it's calling for its master. #bMaybe it's calling for you?#k? Please come back to the island and find out.");
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        if (getQuestId() != this.quest_weapon) {
            // 念願の鏡
            forceStartQuest(this.quest_unknown, "0");
            forceStartQuest(this.quest_best_weapon);
            return false;
        }
        // いいえを選択した
        if (getLastAction() == 0 && status == 10) {
            say("Hey, at least you tell me you tried!", false, true);
            return false;
        }
        // 動画を見る
        if (getLastAction() == 0 && status == 12) {
            setStandAloneMode(true);
            setDirectionMode(true);
            warp(this.map_movie);
            return false;
        }
        switch (status) {
            case 0 -> {
                say(2, "Hmmmmmm mmmm mmmmm....", false, true);
                return true;
            }
            case 1 -> {
                say(2, "#b(Giant Pole Arm is buzzing, but who's that boy standing there?)#k", true, true);
                return true;
            }
            case 2 -> {
                say(2, "#b(I've never met him before. He doesn't look human.)#k", true, true);
                return true;
            }
            case 3 -> {
                say("Hey Aran! Do you still not hear me? Seriously, can't you hear me? Ahhh, this is frustrating!", true, true);
                return true;
            }
            case 4 -> {
                say(2, "#b(Whoa, who was that? Sounds like an angry boy...)#k", true, true);
                return true;
            }
            case 5 -> {
                say("Seriously, the one master I had turned out to be trapped in ice for hundreds of years, abandoning the weapon, and now the 'master' can't even hear me?", true, true);
                return true;
            }
            case 6 -> {
                say(2, "Who are you?", true, true);
                return true;
            }
            case 7 -> {
                say("Aran? Do you hear me now? It's me, it's me! I'm your weapon #bMaha the pole arm!#k!", true, true);
                return true;
            }
            case 8 -> {
                say(2, "#b(...Maha? Giant pole Arm actually talks?)#k", true, true);
                return true;
            }
            case 9 -> {
                say("Why do you have that look on your face like you can't believe it? I see that you have lost all your memories, but... did you also forget about me? How can you do that to me??", true, true);
                return true;
            }
            case 10 -> {
                say(2, "I'm sorry, but seriously... I don't remember a thing.", true, true);
                return true;
            }
            case 11 -> {
                askYesNo("Is that all you can say after all those years? I'm sorry? Do you understand how bored I was all by myself for hundreds of years? Bring it out if you can. Bring your memories out! Bring them all out! Dig them up if you need to!");
                return true;
            }
            case 12 -> {
                say(2, "#b(The voice that claims to be Maha the Giant Pole Arm seem quite perturbed. This conversation is going nowhere. I better talk to Lirin first.)#k", false, true);
                forceCompleteQuest();
                questEndEffect();
                return true;
            }
            case 13 -> {
                askYesNo("Would you like to skip the video clip?  Even if you skip the scene, game play will not be affected.");
                return true;
            }
            default -> {
            }
        }
        // 動画を見ない
        return false;
    }
}
