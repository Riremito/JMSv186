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
public class Npc_OSSS_OS4Shuttle extends NpcScriptMan {

    private final int[] map_ids = {
        502010200, // 現在のある日 : 墜落した宇宙船の深海
        502010300, // 現在のある日 : 化石鉱物の鉱山
        502010400, // 現在のある日 : 加工工場輸送路
        502010500 // 現在のある日 : 加工工場防御船
    };
    private final String[] map_names = {
        "深海に移動する",
        "鉱山に移動する",
        "加工工場に輸送する",
        "加工工場の防御船に移動する"
    };
    private final int map_index_defense_ship = 3;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // TODO : VisitorleaveDirectionMode?
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("どこへ出発しますか？");
                for (int i = 0; i < this.map_ids.length; i++) {
                    nt.addMenu(i, this.map_names[i]);
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                if (answer < 0 || this.map_ids.length <= answer) {
                    return false;
                }
                // TODO : 加工工場防御船への入場条件
                if (answer == this.map_index_defense_ship) {
                    say("入場禁止");
                    return false;
                }
                warp(this.map_ids[answer], "sp");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
