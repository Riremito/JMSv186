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
import tacos.debug.DebugLogger;
import tacos.server.ServerOdinGame;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class FreeMarketPortal {

    private int return_map_id = TacosConstants.MAP_ID_PERION; // PerionFM!
    private String return_portal_name = null;

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

        return_map_id = map_from.getId();
        return_portal_name = portal_from.getName();

        DebugLogger.DebugLog("enter : " + return_map_id + ", " + return_portal_name);

        MapleMap map_to = ServerOdinGame.getInstance(chr.getClient().getChannel()).getMapFactory().getMap(TacosConstants.MAP_ID_FREE_MARKET);
        if (map_to == null) {
            return false;
        }
        TacosPortal portal_to = map_to.getPortal("out00");
        if (portal_to == null) {
            return false;
        }
        chr.changeMapPortal(map_to, portal_to);
        return true;
    }

    public boolean leave(MapleCharacter chr) {
        if (chr == null) {
            return false;
        }
        MapleMap map_to = ServerOdinGame.getInstance(chr.getClient().getChannel()).getMapFactory().getMap(return_map_id);
        if (map_to == null) {
            return false;
        }
        TacosPortal portal_to = (return_portal_name != null) ? map_to.getPortal(return_portal_name) : map_to.getPortal(0);
        if (portal_to == null) {
            return false;
        }
        chr.changeMapPortal(map_to, portal_to);
        return true;
    }

}
