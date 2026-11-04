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
public class Npc_Aquarium_FoggySeaDolphin extends NpcScriptMan {

    private final int map_aquarium = 230000000; // アクアリウム : アクアリウム

    @Override
    public boolean action(int status, int answer) {
        switch (status) {
            case 0 -> {
                // 記録したマップがない場合はアクアリウムへ戻す
                int map_id = getPlayer().getSavedLocation(SavedLocationType.MULUNG_TC);
                if (map_id < 0) {
                    map_id = this.map_aquarium;
                }
                getPlayer().clearSavedLocation(SavedLocationType.MULUNG_TC);
                warp(map_id);
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
