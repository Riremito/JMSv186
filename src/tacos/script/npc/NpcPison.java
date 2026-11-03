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
public class NpcPison extends NpcScriptMan {

    @Override
    public boolean action(int status, int answer) {
        int map_id = getPlayer().getSavedLocation(SavedLocationType.FLORINA);
        if (map_id < 0) {
            // TODO : 記録したマップがない場合の処理
            return false;
        }
        switch (status) {
            case 0 -> {
                // TODO : 文章が不明 (元のスクリプトの仮の文章)
                NpcScriptTag nt = new NpcScriptTag();
                nt.add("#b#m" + map_id + "##kに戻りますか。");
                say(nt, false, true);
                return true;
            }
            case 1 -> {
                // TODO : 記録したマップの削除
                warp(map_id); // TODO : portal name.
                return false;
            }
            default -> {
            }
        }
        return false;
    }
}
