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
// Quest.wz endscript : q2230e (JMS186), 謎の小さな卵, 旧 js (scripts_JMS) を参考に実装
// TODO : 文章 (js の英文のまま)
// TODO : js ではルーンスネイル (5000054) の入手処理がコメントアウトされており、メッセージを表示して完了するだけ
public class Quest_Ellinia_MysteriousSmallEgg extends QuestScriptMan {

    @Override
    public boolean questEnd(int status, int answer) {
        say("Snail Roon is no longer available.", false, true);
        forceCompleteQuest();
        questEndEffect();
        return false;
    }
}
