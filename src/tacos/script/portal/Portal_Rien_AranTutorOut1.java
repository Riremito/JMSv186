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
// Map.wz portal script : aranTutorOut1 (JMS186), js の処理を利用
public class Portal_Rien_AranTutorOut1 extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getQuestStatus(21000) == 0) {
            // TODO : 文章 (js の英文を翻訳, 原文 : You can only exit after you accept the quest from Athena Pierce, who is to your right.)
            playerMessage(5, "右にいるヘレナからクエストを受けてから出ることができます。");
            return false;
        } else {
            teachSkill(20000017, 0, -1);
            teachSkill(20000018, 0, -1);
            teachSkill(20000017, 1, 0);
            teachSkill(20000018, 1, 0);
            return warp(914000200, "east00");
        }
    }
}
