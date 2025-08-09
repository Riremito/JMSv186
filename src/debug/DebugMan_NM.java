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
package debug;

import client.MapleCharacter;
import data.wz.ids.DWI_Random;
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class DebugMan_NM extends DebugMan implements IDebugMan {

    @Override
    public boolean start(MapleCharacter chr) {
        super.start(this, chr);
        return true;
    }

    @Override
    public boolean end(MapleCharacter chr) {
        super.end(chr);
        return true;
    }

    private int target_map_id = 910000000;
    private int maps[] = null;
    private int number_of_maps = 10;

    @Override
    public boolean action(MapleCharacter chr, int status, int answer) {
        switch (status) {
            case 0: {
                NpcTag nt = new NpcTag();
                int current_map_id = chr.getMap().getId();
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
                super.askMenu(chr, nt);
                return true;
            }
            case 1: {
                NpcTag nt = new NpcTag();
                if (answer < number_of_maps * 2 + 1) {
                    nt.add("go to " + maps[answer] + " : #m" + maps[answer] + "#");
                    target_map_id = maps[answer];
                    super.say(chr, nt, true, false);
                    return true;
                }
                return false;
            }
            case 2: {
                MapleMap map = chr.getClient().getChannelServer().getMapFactory().getMap(target_map_id);
                chr.changeMap(map, map.getPortal(0));
                return false;
            }
            default: {
                break;
            }
        }

        return false;
    }

}
