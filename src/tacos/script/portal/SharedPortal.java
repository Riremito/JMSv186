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
package tacos.script.portal;

import odin.client.MapleCharacter;
import odin.server.maps.MapleMap;
import tacos.constants.TacosConstants;
import tacos.server.ServerOdinGame;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class SharedPortal {

    // enter
    private int target_map_id;
    private String target_portal_name;
    // leave
    private int return_map_id = TacosConstants.MAP_ID_PERION; // PerionFM!
    private String return_portal_name = null;

    public SharedPortal(int target_map_id, String target_portal_name) {
        this.target_map_id = target_map_id;
        this.target_portal_name = target_portal_name;
    }

    private boolean usePortal(MapleCharacter chr, int map_id, String portal_name) {
        MapleMap map_to = ServerOdinGame.getInstance(chr.getClient().getChannelId()).getMapFactory().getMap(map_id);
        if (map_to == null) {
            return false;
        }
        TacosPortal portal_to = (portal_name != null) ? map_to.getPortal(portal_name) : map_to.getPortal(0);
        if (portal_to == null) {
            return false;
        }
        chr.changeMapPortal(map_to, portal_to);
        return true;
    }

    public boolean enter(MapleCharacter chr, TacosPortal portal_from) {
        if (chr == null) {
            return false;
        }
        MapleMap map_from = chr.getMap();
        if (map_from == null) {
            return false;
        }
        if (portal_from == null) {
            return false;
        }

        this.return_map_id = map_from.getId();
        this.return_portal_name = portal_from.getName();
        return usePortal(chr, this.target_map_id, this.target_portal_name);
    }

    public boolean leave(MapleCharacter chr) {
        if (chr == null) {
            return false;
        }
        return usePortal(chr, this.return_map_id, this.return_portal_name);
    }

}
