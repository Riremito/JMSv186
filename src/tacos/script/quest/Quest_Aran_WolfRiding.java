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
// Quest.wz startscript : q21600s, q21604s, q21613s, q21618s, endscript : q21604e, q21618e (JMS186), プニの頼み, ヴォルフライディング, オオカミの試験, ヴォルフの進化, 旧 js (scripts_JMS) を参考に実装
public class Quest_Aran_WolfRiding extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        forceStartQuest();
        forceCompleteQuest();
        return false;
    }

    @Override
    public boolean questEnd(int status, int answer) {
        forceStartQuest();
        forceCompleteQuest();
        questEndEffect();
        return false;
    }
}
