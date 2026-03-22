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
package tacos.server.map;

import java.awt.Point;
import odin.client.MapleCharacter;
import odin.client.MapleClient;
import odin.server.maps.MapleMap;
import tacos.constants.TacosConstants;
import tacos.script.TacosScriptPortal;

/**
 *
 * @author Riremito
 */
public class TacosPortal {

    public static final int MAP_PORTAL = 2;
    public static final int DOOR_PORTAL = 6;

    private int id;
    private int type;
    private String name;
    private String target;
    private String scriptName;
    private Point position;
    private int targetmap;
    private boolean portalState = true;

    public TacosPortal(final int type) {
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMysticDoorId() {
        int val = (int) ((byte) this.id);
        return val + 128;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public int getTargetMapId() {
        return this.targetmap;
    }

    public void setTargetMapId(int targetmapid) {
        this.targetmap = targetmapid;
    }

    public int getType() {
        return this.type;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getScriptName() {
        return this.scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public boolean getPortalState() {
        return this.portalState;
    }

    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }

    public boolean enterPortal(MapleClient client) {
        MapleCharacter chr = client.getPlayer();

        if (!this.portalState) {
            return false;
        }

        chr.checkFollow(); // not checked.

        // script portal
        if (this.scriptName != null) {
            return TacosScriptPortal.getInstance().enter(this, client);
        }
        // normal portal
        if (this.scriptName == null) {
            // undefiend
            if (this.targetmap == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
                return false;
            }
            MapleMap map_to = chr.getChannelServer().getMapFactory().getMap(this.targetmap);
            TacosPortal portal_to = map_to.getPortal(this.target);
            // find portal failed.
            if (portal_to == null) {
                portal_to = map_to.getPortal(0);
            }
            // never executed.
            if (portal_to == null) {
                return false;
            }
            chr.changeMapPortal(map_to, portal_to);
            return true;
        }

        return false;
    }

}
