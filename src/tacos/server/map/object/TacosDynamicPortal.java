/*
 * Copyright (C) 2024 Riremito
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
package tacos.server.map.object;

import odin.client.MapleCharacter;
import odin.server.maps.MapleMap;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class TacosDynamicPortal extends TacosMapObject {

    private int item_id; // 2420000 or 2420004
    private int map_id; // 749050200

    public TacosDynamicPortal(int item_id, int map_id, int x, int y) {
        this.item_id = item_id;
        this.map_id = map_id;
        setPosition(x, y);
    }

    public int getItemId() {
        return this.item_id;
    }

    public int getMapId() {
        return this.map_id;
    }

    // official InstancePortal usage. 749050100 to 749050200.
    public boolean leavePinkBeanCakeEvent(MapleCharacter chr) {
        MapleMap map_to = chr.findMap(this.map_id);

        if (map_to == null) {
            return false;
        }

        // no dynamic portal
        TacosPortal spawn_point = map_to.getPortal(0);
        chr.changeMapInternal(map_to, spawn_point.getPosition(), spawn_point);
        return true;
    }

    // unofficial usage.
    public boolean enterDynamicPortal(MapleCharacter chr) {
        MapleMap map_to = chr.findMap(this.map_id);

        if (map_to == null) {
            return false;
        }

        int map_id_from = chr.getPosMap();
        TacosDynamicPortal dynamic_portal_to = map_to.findDynamicPortalLink(map_id_from);
        if (dynamic_portal_to == null) {
            return false;
        }

        chr.changeMapDynamicPortal(map_to, dynamic_portal_to.getPosition());
        return true;
    }
}
