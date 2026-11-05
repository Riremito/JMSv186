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
// Map.wz portal script : balog_end (JMS186), js の処理を利用
// TODO : JMS186 に存在しないマップ
public class Portal_Sleepywood_Balog_end extends PortalScriptMan {

    private final int item_balrog_fur = 4001261; // バルログの毛皮
    private final int map_easy_balrog = 105100301;
    private final int map_balrog_entrance = 105100100;

    @Override
    public boolean enter() {
        if (!canHold(this.item_balrog_fur)) {
            // TODO : 文章 (js の英文を翻訳, 原文 : Please make 1 ETC room.)
            playerMessage(5, "ETCインベントリに空きを1つ作ってください。");
            return false;
        }
        gainExpR(getMapId() == this.map_easy_balrog ? 260000 : 520000);
        // TODO : js では gainNX (2000 or 3000)
        gainItem(this.item_balrog_fur, 1);
        return warp(this.map_balrog_entrance, "sp");
    }
}
