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
public class Npc_Common_Catherine extends NpcScriptMan {

    private final int map_pachinko_parlor = 809030000; // パチンコ屋

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("パチンコ屋へ移動しますか？");
                askYesNo(nt);
                return true;
            }
            case 1 -> {
                // TODO : パチンコ屋用の SavedLocationType
                saveLocation(SavedLocationType.MULUNG_TC);
                warp(this.map_pachinko_parlor, "out00");
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
