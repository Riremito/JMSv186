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
// Map.wz portal script : outDarkEreb (JMS186), js の処理を利用
public class Portal_Ereve_OutDarkEreb extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (isQuestFinished(20407)) {
            return warp(130000000, "sp");
        } else {
            // TODO : 文章 (js の英文を翻訳, 原文 : I have to defeat the Black Witch first!)
            playerMessage(5, "先に黒魔女を倒さなければ！");
            return false;
        }
    }
}
