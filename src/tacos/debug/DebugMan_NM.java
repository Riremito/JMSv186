/*
 * Copyright (C) 2025 Riremito
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
package tacos.debug;

import tacos.wz.ids.DWI_Random;
import tacos.script.npc.NpcScriptTag;

/**
 *
 * @author Riremito
 */
public class DebugMan_NM extends DebugNpcScriptMan {

    private int target_map_id = 910000000;
    private int maps[] = null;
    private int number_of_maps = 10;

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                int current_map_id = getPlayer().getMap().getId();
                int map_index = DWI_Random.getMapIndex(current_map_id);
                this.maps = new int[this.number_of_maps * 2 + 1];

                for (int i = 0; i < this.number_of_maps * 2 + 1; i++) {
                    this.maps[i] = DWI_Random.getMapByIndex(map_index + i - this.number_of_maps);
                    if (this.maps[i] == -1) {
                        continue;
                    }
                    if (maps[i] == current_map_id) {
                        nt.addMenuRed(i, this.maps[i] + " : #m" + this.maps[i] + "#");
                    } else {
                        nt.addMenu(i, this.maps[i] + " : #m" + this.maps[i] + "#");
                    }
                }
                askMenu(nt);
                return true;
            }
            case 1 -> {
                NpcScriptTag nt = new NpcScriptTag();
                if (answer < this.number_of_maps * 2 + 1) {
                    nt.add("go to " + this.maps[answer] + " : #m" + this.maps[answer] + "#");
                    this.target_map_id = maps[answer];
                    say(nt, true, false);
                    return true;
                }
                return false;
            }
            case 2 -> {
                getPlayer().changeMap(this.target_map_id);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
