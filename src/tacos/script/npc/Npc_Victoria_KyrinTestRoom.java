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
public class Npc_Victoria_KyrinTestRoom extends NpcScriptMan {

    // 隠れ道 : 海賊のテスト場
    private final int[] map_tests = {
        108000500,
        108000501,
        108000502,
        108000503
    };
    // テスト場ごとに必要なアイテム
    private final int[] item_crystals = {
        4031857, // 強力な風の結晶
        4031857, // 強力な風の結晶
        4031856, // 強力な力の結晶
        4031856 // 強力な力の結晶
    };
    private final int item_crystal_count = 15;
    private final int map_exit = 120000101; // ノーチラス : 航海室

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                for (int i = 0; i < this.map_tests.length; i++) {
                    if (getMapId() != this.map_tests[i]) {
                        continue;
                    }
                    int item_id = this.item_crystals[i];
                    if (getItemCount(item_id) < this.item_crystal_count) {
                        // TODO : 文章 (適当)
                        say("#b#t" + item_id + "##kを" + this.item_crystal_count + "個集めてきなさい。");
                        return false;
                    }
                    // TODO : 文章 (適当)
                    say("#b#t" + item_id + "##kを" + this.item_crystal_count + "個持ってきたのね！おめでとう。外に出してあげるわ。", false, true);
                    return true;
                }
                return false;
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
