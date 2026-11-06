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
// Quest.wz startscript : q20520s (JMS186), 騎士の品位, 旧 js (scripts_JMS) を参考に実装
public class Quest_Cygnus_KnightsDignity extends QuestScriptMan {

    private final int item_letter = 4032208; // ナインハートの紹介状

    @Override
    public boolean questStart(int status, int answer) {
        gainItem(this.item_letter, 1);
        forceStartQuest();
        return false;
    }
}
