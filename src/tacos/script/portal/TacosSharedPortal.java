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

import tacos.constants.TacosConstants;
import tacos.server.map.TacosMap;
import tacos.client.TacosCharacter;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class TacosSharedPortal {

    // enter
    private int target_map_id;
    private String target_portal_name;
    // leave
    private int return_map_id = TacosConstants.MAP_ID_PERION; // PerionFM!
    private String return_portal_name = "";

    public TacosSharedPortal(int target_map_id, String target_portal_name) {
        this.target_map_id = target_map_id;
        this.target_portal_name = target_portal_name;
    }

    public boolean enter(TacosCharacter chr, TacosPortal portal_from) {
        TacosMap map_from = chr.getMap();
        if (map_from == null) {
            return false;
        }
        if (portal_from == null) {
            return false;
        }
        this.return_map_id = map_from.getId();
        this.return_portal_name = portal_from.getName();
        return chr.changeMap(this.target_map_id, this.target_portal_name);
    }

    public boolean leave(TacosCharacter chr) {
        return chr.changeMap(this.return_map_id, this.return_portal_name);
    }

    public int getReturnMapId() {
        return this.return_map_id;
    }

    public void setReturnMapId(int return_map_id) {
        this.return_map_id = return_map_id;
    }

    public String getReturnPortalName() {
        return this.return_portal_name;
    }

    public void setReturnPortalName(String return_portal_name) {
        this.return_portal_name = return_portal_name;
    }
}
