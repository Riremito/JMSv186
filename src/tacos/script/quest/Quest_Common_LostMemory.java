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
// Quest.wz startscript : q3529s, q3539s (JMS186), 失われた思い出見付け, 失った思い出を探して, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
public class Quest_Common_LostMemory extends QuestScriptMan {

    private final int quest_memory = 3507; // js で同時に完了するクエスト

    @Override
    public boolean questStart(int status, int answer) {
        say("Oh my gosh, you've grown so much since we first met! You've lost your memories? I'll take care of that.", false, true);
        forceCompleteQuest();
        forceCompleteQuest(this.quest_memory);
        return false;
    }
}
