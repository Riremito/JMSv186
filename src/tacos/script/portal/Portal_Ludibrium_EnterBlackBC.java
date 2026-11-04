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
// Map.wz portal script : enterBlackBC (JMS186), js の処理を利用
public class Portal_Ludibrium_EnterBlackBC extends PortalScriptMan {

    @Override
    public boolean enter() {
        if (isQuestActive(22583)) {
            forceCompleteQuest(22583);
            // TODO : 文章 (js の英文を翻訳, 原文 : Free spirits released.)
            playerMessage(5, "囚われた精霊を解放しました。");
        }
        if (isQuestActive(22584)) {
            forceCompleteQuest(22584);
            // TODO : 文章 (js の英文を翻訳, 原文 : Door lock destroyed.)
            playerMessage(5, "扉の鍵を破壊しました。");
        }
        warp(220011001, 0);
        return false;
    }
}
