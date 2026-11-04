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
// Map.wz portal script : rienTutor7 (JMS186), js の処理を利用
public class Portal_Rien_RienTutor7 extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getQuestStatus(21014) == 2 || getJob() != 2000) {
            return warp(140010100, 2);
        } else {
            // TODO : 文章 (js の英文を翻訳, 原文 : The town of Rien is to the right. Take the portal on the right and go into town to meet Lilin.)
            playerMessage(5, "リエンの村は右側です。右のポータルから村に入ってリリンに会いましょう。");
            return false;
        }
    }
}
