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
// Quest.wz startscript : q2300s - q2310s, endscript : q2300e - q2310e (JMS186), キノコ王国の事件！, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
// TODO : js では入口まで送ると言いつつ移動処理がない
public class Quest_MushroomCastle_KingdomInDanger extends QuestScriptMan {

    private final int quest_first = 2300; // キノコ王国の事件！ (最初の 1 つだけ処理が異なる)
    private final int quest_next_first = 2312; // 勇者の試験
    private final int quest_next = 2311; // TODO : JMS186 には存在しないクエスト (js のまま)
    private final int item_letter = 4032375; // 転職官の推薦書

    @Override
    public boolean questStart(int status, int answer) {
        boolean first = (getQuestId() == this.quest_first);
        // 受諾しなかった
        if (getLastAction() == 0 && status == -1) {
            say("Really? It's an urgent matter, so if you have some time, please see me.");
            return false;
        }
        // いいえを選択した
        if (getLastAction() == 0 && status == 2) {
            if (!first) {
                say("Really? It's an urgent matter, so if you have some time, please see me.");
                return false;
            }
            // TODO : js では推薦書を渡さずにクエストを開始している
            say("Okay. In that case, I'll just give you the routes to the Kingdom of Mushroom. #bNear the west entrance of Henesys,#k you'll find an #bempty house#k. Enter the house, and turn left to enter#b<Themed Dungeon : Mushroom Castle>#k. That's the entrance to the Kingdom of Mushroom. There's not much time!", false, true);
            forceStartQuest();
            return false;
        }
        switch (status) {
            case 0 -> {
                askAccept("Now that you have made the job advancement, you look like you're ready for this. I have something I'd like to ask you for help. Are you willing to listen?");
                return true;
            }
            case 1 -> {
                say("What happened is that the #bKingdom of Mushroom#k is currently in disarray. Kingdom of Mushroom is located near Henesys, featuring the peace-loving, intelligent King Mush. Recently, he began to feel ill, so he decided to appoint his only daughter #bPrincess Violetta#k. Something must have happened since then for the kingdom to be in its current state.", false, true);
                return true;
            }
            case 2 -> {
                say("I am not aware of the exact details, but it's obvious something terrible had taken place, so I think it'll be better if you go there and assess the damage yourself. An explorer like you seem more than capable of saving Kingdom of Mushroom. I have just written you a #brecommendation letter#k, so I suggest you head over to Kingdom of Mushroom immediately and look for the #bHead Patrol Officer#k.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + this.item_letter + "# #t" + this.item_letter + "#", false, true);
                return true;
            }
            case 3 -> {
                askYesNo("By the way, do you know where Kingdom of Mushroom is located? It'll be okay if you can find your way there, but if you don't mind, I can take you straight to the entrance.");
                return true;
            }
            case 4 -> {
                gainItem(this.item_letter, 1);
                forceStartQuest();
                return false;
            }
            default -> {
            }
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        switch (status) {
            case 0 -> {
                say("Hmmm? Is that a #brecommendation letter from the job instructor#k??! What is this, are you the one that came to save us, the Kingdom of Mushroom?", false, true);
                return true;
            }
            case 1 -> {
                say("Hmmm... okay. Since the letter is from the job instructor, I suppose you are really the one. I apologize for not introducing myself to you earlier. I'm the #bHead Security Officer#k in charge of protecting King Mush. As you can see, this temporary hideout is protected by the team of security and soldiers. Our situation may be dire, but nevertheless, welcome to Kingdom of Mushroom.", true, true);
                return true;
            }
            case 2 -> {
                removeItem(this.item_letter, 1);
                forceCompleteQuest();
                questEndEffect();
                if (getQuestId() == this.quest_first) {
                    forceStartQuest(this.quest_next_first);
                } else {
                    forceStartQuest(this.quest_next);
                }
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
