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
public class Npc_Zipangu_Pong extends NpcScriptMan {

    // 悪霊の神殿
    private final int[] map_temples = {
        809061010, // 隠れ道 : 悪霊の神殿
        950101010 // 隠れ道 : 悪霊の神殿
    };
    // 退場マップ
    private final int[] map_exits = {
        809061100, // 隠れ道 : 退場マップ
        950101100 // 隠れ道 : 退場マップ
    };
    // 金箔寺
    private final int[] map_golden_temples = {
        809060000, // 隠れ道 : 金箔寺
        950100000 // 隠れ道 : 金箔寺
    };
    private final int item_sun_flame = 4001433; // 太陽の炎
    private final int item_sun_flame_count = 30;
    private int index = -1;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                for (int i = 0; i < this.map_exits.length; i++) {
                    if (getMapId() == this.map_exits[i]) {
                        int count = getItemCount(this.item_sun_flame);
                        if (this.item_sun_flame_count < count) {
                            count = this.item_sun_flame_count;
                        }
                        if (0 < count) {
                            removeItem(this.item_sun_flame, count);
                        }
                        warp(this.map_golden_temples[i]);
                        return false;
                    }
                    if (getMapId() == this.map_temples[i]) {
                        this.index = i;
                    }
                }
                if (this.index < 0) {
                    return false;
                }
                // TODO : 文章 (適当)
                askYesNo("今出ると、最初からやり直しになります。本当に出ますか？");
                return true;
            }
            case 1 -> {
                warp(this.map_exits[this.index]);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
