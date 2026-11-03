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
public class NpcSubwayExit extends NpcScriptMan {

    private final int map_exit = 103000100; // 地下鉄 : 切符売り場

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 翻訳
                askYesNo("この装置は外につながっている。あきらめてここから出ますか？次にここに来たときは、スタート地点からやり直さなければならない…");
                return true;
            }
            case 1 -> {
                warp(this.map_exit);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
