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

import odin.server.maps.SavedLocationType;

/**
 *
 * @author Riremito
 */
public class Npc_Common_DimensionalMirror extends NpcScriptMan {

    private final String[] names = {
        "アリアント闘技場",
        "武陵道場",
        "モンスターカーニバル",
        "モンスターカーニバル2nd",
        "霧の海の幽霊船",
        "ネトのピラミッド",
        "未開通区域",
        "幸福の村",
        "黄金の寺院"
    };
    private final int[] map_ids = {
        980010000, // アリアント闘技大会 : 闘技場の控え室
        925020000, // 武陵道場 : 武陵道場
        980000000, // モンスターカーニバル : モンスターカーニバル入口
        980030000, // モンスターカーニバル2nd : シュピゲルマンの事務室
        923020000, // 霧の海 : 座礁された幽霊船
        926010000, // 隠れ道 : ピラミッドの丘
        910320000, // ヒドゥンストリート : 捨てられた地下鉄の歴史
        180000000, // GM･Hazard : 撮影現場
        950100000 // 隠れ道 : 金箔寺
    };
    // TODO : portal (0, 0, 4, 4, 0, 4, 0, 0, 9)

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                for (int i = 0; i < this.names.length; i++) {
                    nt.add("#" + i + "#" + this.names[i]);
                }
                askSlideMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                getPlayer().saveLocation(SavedLocationType.MULUNG_TC);
                warp(this.map_ids[answer]); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
