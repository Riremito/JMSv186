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
package tacos.server.map.object;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import odin.server.Randomizer;
import odin.server.maps.MapleMap;
import tacos.client.TacosCharacter;
import tacos.client.TacosDoorSkill;
import tacos.debug.DebugLogger;
import tacos.server.map.TacosPortal;

/**
 *
 * @author Riremito
 */
public class TacosMysticDoor extends TacosMapObject {

    private final int nState;
    private final int town_map_id;
    private final int field_map_id;
    private final int skill_id;
    private Point field_pos = new Point();
    private TacosPortal townPortal = null;

    public TacosMysticDoor(TacosCharacter owner, int nState, TacosDoorSkill tds) {
        this.nState = nState;
        this.skill_id = tds.getId();
        this.town_map_id = owner.getMap().getReturnMapId();
        this.field_map_id = owner.getMap().getId();
        setPosition(owner.getPosition());
        setFieldPosition(owner.getPosition());
        setOwnerId(owner.getId());
    }

    public int getState() {
        return this.nState;
    }

    public int getTownMapId() {
        return this.town_map_id;
    }

    public int getFieldMapId() {
        return this.field_map_id;
    }

    public int getSkillId() {
        return this.skill_id;
    }

    public Point getFieldPosition() {
        return this.field_pos.getLocation();
    }

    public void setFieldPosition(Point position) {
        this.field_pos.setLocation(position);
    }

    public void setFieldPosition(int x, int y) {
        this.field_pos.setLocation(x, y);
    }

    public int getFieldX() {
        return this.field_pos.x;
    }

    public int getFieldY() {
        return this.field_pos.y;
    }

    public TacosPortal getTownPortal() {
        return this.townPortal;
    }

    public void setTownPortal(TacosPortal portal) {
        this.townPortal = portal;
    }

    public TacosPortal getFreePortal(MapleMap map_town) {
        List<TacosPortal> freePortals = new ArrayList<>();

        for (TacosPortal portal : map_town.getPortals()) {
            if (portal.getType() == TacosPortal.DOOR_PORTAL) {
                freePortals.add(portal);
                DebugLogger.DebugLog("getFreePortal : " + (byte) portal.getId());
            }
        }
        // already used.
        for (TacosMysticDoor door : map_town.getAllDoors()) {
            freePortals.remove(door.getTownPortal());
        }
        if (freePortals.size() <= 0) {
            return null;
        }

        return freePortals.get(Randomizer.nextInt(freePortals.size()));
    }
}
