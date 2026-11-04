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
public class Npc_MapleIsland_Mai extends NpcScriptMan {

    // 冒険者の修練場
    private final int[] map_ids = {
        1010100, // メイプルロード : 冒険者の修練場1
        1010200, // メイプルロード : 冒険者の修練場2
        1010300, // メイプルロード : 冒険者の修練場3
        1010400 // メイプルロード : 冒険者の修練場4
    };

    @Override
    public boolean action(int status, int answer) {
        // 通常は会話不可
        switch (status) {
            case 0 -> {
                // TODO : クエスト (1041, 1042, 1043, 1044) を進行中の修練場のみ表示
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("基礎を固めたいなら、ここが向いているようだ。どこで修練する？");
                for (int i = 0; i < this.map_ids.length; i++) {
                    nt.addMenu(i, getMapName(this.map_ids[i]));
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                warp(this.map_ids[answer]); // TODO : portal 4
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
