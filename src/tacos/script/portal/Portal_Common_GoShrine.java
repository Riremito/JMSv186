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
// Map.wz portal script : goShrine (JMS186), js の処理を利用
public class Portal_Common_GoShrine extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (getLevel() < 50) {
            // TODO : 文章 (js の英文を翻訳, 原文 : You must be at least level 50.)
            playerMessage(5, "レベル50以上でなければなりません。");
            return false;
        }
        warp(950101000, 0);
        return true;
    }
}
