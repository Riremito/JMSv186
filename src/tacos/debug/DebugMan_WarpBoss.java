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
package tacos.debug;

import java.util.ArrayList;
import odin.client.MapleCharacter;
import tacos.wz.WzDataStorage;

/**
 *
 * @author Riremito
 */
public class DebugMan_WarpBoss extends DebugMan implements IDebugMan {

    private class WarpBossData {

        private int map_id;
        private int mob_id;
    }

    private final ArrayList<WarpBossData> warp_boss_data = new ArrayList<>();
    private int target_map_id = 910000000;

    public boolean add(int map_id, int mob_id) {
        if (!WzDataStorage.MAP.check(map_id) || !WzDataStorage.MOB.check(mob_id)) {
            return false;
        }

        WarpBossData wbd = new WarpBossData();
        wbd.map_id = map_id;
        wbd.mob_id = mob_id;

        this.warp_boss_data.add(wbd);
        return true;
    }

    @Override
    public boolean start(MapleCharacter chr) {
        if (this.warp_boss_data.isEmpty()) {
            return false;
        }
        super.start(this, chr);
        return true;
    }

    @Override
    public boolean end(MapleCharacter chr) {
        super.end(chr);
        return true;
    }

    @Override
    public boolean action(MapleCharacter chr, int status, int answer) {
        switch (status) {
            case 0: {
                NpcTag nt = new NpcTag();
                for (int index = 0; index < this.warp_boss_data.size(); index++) {
                    nt.addMenu(index, "#m" + this.warp_boss_data.get(index).map_id + "# (#o" + this.warp_boss_data.get(index).mob_id + "#)");
                }
                super.askMenu(chr, nt);
                return true;
            }
            case 1: {
                NpcTag nt = new NpcTag();
                if (0 <= answer && answer < this.warp_boss_data.size()) {
                    nt.add("go to #m" + this.warp_boss_data.get(answer).map_id + "# (#o" + this.warp_boss_data.get(answer).mob_id + "#)");
                    this.target_map_id = this.warp_boss_data.get(answer).map_id;
                    super.say(chr, nt, true, false);
                    return true;
                }
                return false;
            }
            case 2: {
                chr.changeMapById(this.target_map_id);
                return false;
            }
            default: {
                break;
            }
        }
        return false;
    }

    public void setMasterMonsters() {
        add(104000400, 2220000);
        add(101030404, 3220000);
        add(260010201, 3220001);
        add(230020100, 4220001);
        add(110040000, 5220001);
        add(100040105, 5220002);
        add(100040106, 5220002);
        add(220050000, 5220003);
        add(220050100, 5220003);
        add(220050200, 5220003);
        add(107000300, 6220000);
        add(107000400, 6220000);
        add(221040301, 6220001);
        add(250010304, 7220000);
        add(222010310, 7220001);
        add(250010503, 7220002);
        add(250010504, 7220002);
        add(200010300, 8220000);
        add(261030000, 8220002);
        add(240040401, 8220003);
    }
}
