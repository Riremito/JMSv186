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
// Quest.wz startscript : q50217s, q50218s, endscript : q50233e, q50234e (JMS186), レッドパンダの友達, オチャマルの友達, レッドパンダと親しくなろう！, オチャマルと親しくなろう！, 旧 js (scripts_JMS) を参考に実装
public class Quest_Common_PetFriends extends QuestScriptMan {

    private final int quest_red_panda = 50217; // レッドパンダの友達

    @Override
    public boolean questStart(int status, int answer) {
        // js ではレッドパンダの友達は処理なし
        if (getQuestId() != this.quest_red_panda) {
            forceStartQuest();
        }
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        // js では完了ではなく開始している
        forceStartQuest();
        return false;
    }
}
