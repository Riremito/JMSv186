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
// Map.wz portal script : enterWitch (JMS186), js の処理を利用
public class Portal_Leafre_EnterWitch extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (isQuestFinished(20407)) {
            return warp(924010200, 0);
        } else if (isQuestFinished(20406)) {
            return warp(924010100, 0);
        } else if (isQuestFinished(20404)) {
            return warp(924010000, 0);
        } else {
            // TODO : 文章 (js の英文を翻訳, 原文 : I shouldn't go here.. it's creepy!)
            playerMessage(5, "ここには行かないほうがよさそう…不気味だ！");
            return false;
        }
    }
}
