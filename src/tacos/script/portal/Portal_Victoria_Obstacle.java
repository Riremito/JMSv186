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
// Map.wz portal script : obstacle (JMS186), js の処理を利用
public class Portal_Victoria_Obstacle extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (isQuestFinished(2318)) {
            if (!haveItem(2430014)) {
                return warp(106020400);
            } else {
                // TODO : 文章 (js の英文を翻訳, 原文 : First, use the Killer Mushroom Spores to remove the barrier.)
                playerMessage("まずは殺キノコスプレーを使って結界を取り除こう。");
            }
        }
        return true;
    }
}
