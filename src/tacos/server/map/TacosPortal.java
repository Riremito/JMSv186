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
import tacos.script.portal.PortalMan;
import tacos.script.portal.TacosSharedPortal;
import tacos.wz.opt.PortalType;

/**
 *
 * @author Riremito
 */
public class TacosPortal {

    private int id;
    private int town_portal_id = 0; // mystic door.
    private PortalType type;
    private String name;
    private String target;
    private String scriptName;
    private Point position;
    private int target_map_id;
    private boolean disabled = false;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTownPortalId() {
        return this.town_portal_id;
    }

    public void setTownPortalId(int town_portal_id) {
        this.town_portal_id = town_portal_id;
    }

    public PortalType getType() {
        return this.type;
    }

    public void setType(PortalType type) {
        this.type = type;
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

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean enterPortal(TacosCharacter chr) {
        if (this.disabled) {
            return false;
        }
        return chr.changeMap(this.target_map_id, this.target);
    }

    public boolean enterScriptPortal(TacosCharacter chr) {
        if (this.disabled) {
            return false;
        }
        if (this.scriptName.isEmpty()) {
            return false;
        }
        if (TacosSharedPortal.use(chr, this)) {
            return true;
        }
        return PortalMan.enter(chr, this);
    }
}
