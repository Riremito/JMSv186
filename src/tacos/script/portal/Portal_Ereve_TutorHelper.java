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
package tacos.script.portal;

/**
 *
 * @author Riremito
 */
// Map.wz portal script : tutorHelper (JMS186), js の処理を利用
public class Portal_Ereve_TutorHelper extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getQuestStatus(20021) == 0) {
            hireTutor(true);
            // TODO : 文章 (js の英文を翻訳, 原文 : Welcome to the world of Maple! My name is Koo, and I will be your guide! I will be here to answer your questions and guide you until you reach Level 10 and become a Knight-in-Training. If you have any questions, double-click me!)
            summonMsg("メイプルの世界へようこそ！私はクー、あなたのガイドです！レベル10になって見習い騎士になるまで、質問に答えたり案内したりします。何か聞きたいことがあったら、私をダブルクリックしてください！");
            forceCompleteQuest(20021);
        }
        return false;
    }
}
