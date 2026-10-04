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

/**
 *
 * @author Riremito
 */
public class DebugMan_Warp extends DebugMan implements IDebugMan {

    private NpcTag nt_first = new NpcTag();
    private final ArrayList<Integer> selection_list = new ArrayList<>();
    private int target_map_id = 910000000;

    public DebugMan_Warp(ArrayList<Integer> map_ids) {
        this.selection_list.addAll(map_ids);
        setFirstTalk();
    }

    private void setFirstTalk() {
        this.nt_first = new NpcTag();

        for (int index = 0; index < this.selection_list.size(); index++) {
            this.nt_first.addMenu(index, this.selection_list.get(index) + " : #m" + this.selection_list.get(index) + "#");
        }
    }

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

    @Override
    public boolean action(MapleCharacter chr, int status, int answer) {
        switch (status) {
            case 0: {
                super.askMenu(chr, this.nt_first);
                return true;
            }
            case 1: {
                NpcTag nt = new NpcTag();
                if (0 <= answer && answer < this.selection_list.size()) {
                    nt.add("go to " + this.selection_list.get(answer) + " : #m" + this.selection_list.get(answer) + "#");
                    this.target_map_id = this.selection_list.get(answer);
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
}
