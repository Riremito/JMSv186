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
import server.maps.MapleMap;

/**
 *
 * @author Riremito
 */
public class DebugManTest extends DebugMan implements IDebugMan {

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

    @Override
    public boolean action(MapleCharacter chr, int status, int answer) {
        switch (status) {
            case 0: {
                NpcTag nt = new NpcTag();
                nt.add("DebugManTest...");
                nt.addMenu(11, "#m100000000#");
                nt.addMenu(22, "#m200000000#");
                super.askMenu(chr, nt);
                return true;
            }
            case 1: {
                NpcTag nt = new NpcTag();
                if (answer == 11) {
                    nt.add("go to #m100000000#.");
                    target_map_id = 100000000;
                    super.say(chr, nt, true, false);
                    return true;
                }
                if (answer == 22) {
                    nt.add("go to #m200000000#.");
                    target_map_id = 200000000;
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
