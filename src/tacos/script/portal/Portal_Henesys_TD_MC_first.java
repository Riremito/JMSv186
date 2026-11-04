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
// Map.wz portal script : TD_MC_first (JMS186), js の処理を利用
public class Portal_Henesys_TD_MC_first extends PortalScriptMan {

    private final int map_mushroom_forest = 106020000; // キノコ城 : キノコの森町角

    @Override
    public boolean enter() {
        int level = getLevel();
        if (level < 30 || 38 < level) {
            // TODO : 文章 (js の英文を翻訳, 原文 : Unable to approach due to force off the ground.)
            playerMessage(5, "地面から放たれる力のせいで近づくことができません。");
            return false;
        }
        return warp(this.map_mushroom_forest, 1);
    }
}
