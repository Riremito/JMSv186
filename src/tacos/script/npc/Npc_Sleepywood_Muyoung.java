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
package tacos.script.npc;

/**
 *
 * @author Riremito
 */
// Npc.wz script : balog_InOut (JMS176-302), js の処理を利用
public class Npc_Sleepywood_Muyoung extends NpcScriptMan {

    private final int map_balrog_entrance = 105100100; // バルログの寺院の入口 (String.wz に名前なし)

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : 文章 (js の英文を翻訳)
                askYesNo("今出ると、最初からやり直しになります。本当に出ますか？");
                return true;
            }
            case 1 -> {
                warp(this.map_balrog_entrance);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
