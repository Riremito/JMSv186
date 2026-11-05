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
// Map.wz portal script : enterDisguise0 (JMS186), js の処理を利用
public class Portal_Ereve_EnterDisguise0 extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getJob() >= 1000) {
            if (haveItem(4032179)) {
                // TODO : 文章 (js の英文を翻訳, 原文 : The erev search begins.)
                playerMessage("エレヴの捜索を開始します。");
            }
            return warp(130010000, "east00");
        } else {
            // TODO : 文章 (js の英文を翻訳, 原文 : Only the knights of Cygnus may enter.)
            playerMessage("シグナス騎士団のみ入ることができます。");
            return false;
        }
    }
}
