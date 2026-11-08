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
import tacos.client.TacosCharacter;
import tacos.constants.TacosConstants;
import tacos.script.portal.PortalMan;

/**
 *
 * @author Riremito
 */
public class TacosPortal {

    public static final int DOOR_PORTAL = 6;

    private int id;
    private int type;
    private String name;
    private String target;
    private String scriptName;
    private Point position;
    private int target_map_id;
    private boolean portalState = true;

    public TacosPortal(int type) {
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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
        return this.target_map_id;
    }

    public void setTargetMapId(int target_map_id) {
        this.target_map_id = target_map_id;
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

    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }

    public boolean enterPortal(TacosCharacter chr) {
        if (!this.portalState) {
            return false;
        }
        // script portal
        if (!this.scriptName.isEmpty()) {
            return PortalMan.enter(chr.getClient(), this);
        }
        // normal portal
        if (this.target_map_id == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
            return false;
        }
        TacosMap map_to = chr.findMap(this.target_map_id);
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
}
