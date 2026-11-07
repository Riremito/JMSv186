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
// Quest.wz endscript : q3514e (JMS186), 感情を売り出す魔法製錬術師, 旧 js (scripts_JMS) を参考に実装
// TODO : js では Act の報酬 (経験値) なしで完了している
public class Quest_Magatia_AlchemistWhoSellsEmotions extends QuestScriptMan {

    @Override
    public boolean questEnd(int status, int answer) {
        forceCompleteQuest();
        questEndEffect();
        return false;
    }
}
