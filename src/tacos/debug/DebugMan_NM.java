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
import tacos.server.map.TacosMap;
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
            case 0: {
                NpcScriptTag nt = new NpcScriptTag();
                int current_map_id = getPlayer().getMap().getId();
                int map_index = DWI_Random.getMapIndex(current_map_id);
                maps = new int[number_of_maps * 2 + 1];

                for (int i = 0; i < number_of_maps * 2 + 1; i++) {
                    maps[i] = DWI_Random.getMapByIndex(map_index + i - number_of_maps);
                    if (maps[i] == -1) {
                        continue;
                    }
                    if (maps[i] == current_map_id) {
                        nt.addMenuRed(i, maps[i] + " : #m" + maps[i] + "#");
                    } else {
                        nt.addMenu(i, maps[i] + " : #m" + maps[i] + "#");
                    }
                }
                askMenu(nt);
                return true;
            }
            case 1: {
                NpcScriptTag nt = new NpcScriptTag();
                if (answer < number_of_maps * 2 + 1) {
                    nt.add("go to " + maps[answer] + " : #m" + maps[answer] + "#");
                    target_map_id = maps[answer];
                    say(nt, true, false);
                    return true;
                }
                return false;
            }
            case 2: {
                TacosMap map = getPlayer().findMap(target_map_id);
                getPlayer().changeMapPortal(map, map.getPortalById(0));
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }
}
