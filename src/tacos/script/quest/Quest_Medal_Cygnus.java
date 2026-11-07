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
// Quest.wz startscript : q29904s - q29909s (JMS186), シグナス騎士団の勲章, 旧 js (scripts_JMS) を参考に実装
public class Quest_Medal_Cygnus extends QuestScriptMan {

    @Override
    public boolean questStart(int status, int answer) {
        int job = getJob();
        boolean ok = switch (getQuestId()) {
            // ノーブレス
            case 29904, 29905 -> 1000 <= job && job < 2000;
            // 見習い騎士
            case 29906 -> 1000 < job && job < 2000;
            // 正騎士
            case 29907 -> 1000 < job && 0 < job % 100 && job < 2000;
            // 上級騎士
            case 29908 -> 1000 < job && 0 < job % 10 && job < 2000;
            // 騎士団長
            case 29909 -> 1000 < job && 1 < job % 10 && job < 2000;
            default -> false;
        };
        if (ok) {
            forceStartQuest();
        }
        return false;
    }
}
