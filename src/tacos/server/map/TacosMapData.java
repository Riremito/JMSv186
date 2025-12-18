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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import odin.server.MaplePortal;
import odin.server.maps.MapleFoothold;
import odin.server.maps.MapleFootholdTree;
import tacos.constants.TacosConstants;

/**
 *
 * @author Riremito
 */
public class TacosMapData {

    protected int mapid;
    protected int returnMapId;
    private MapleFootholdTree footholds = null;
    protected int fieldLimit;
    protected int timeLimit;
    protected int decHPInterval = 10000;
    protected int forcedReturnMap = 999999999;
    private float recoveryRate;
    private int protectItem = 0;
    private int decHP = 0;
    private int lvForceMove = 0;
    private Map<Integer, MaplePortal> portals = new HashMap<>();
    private boolean town;
    private boolean clock;
    private String mapName, streetName;

    public TacosMapData(int mapid, int returnMapId) {
        this.mapid = mapid;
        this.returnMapId = returnMapId;
        if (this.returnMapId == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
            this.returnMapId = mapid;
        }
    }

    public int getId() {
        return this.mapid;
    }

    public int getReturnMapId() {
        return this.returnMapId;
    }

    public MapleFootholdTree getFootholds() {
        return this.footholds;
    }

    public void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public Point calcPointBelow(Point initial) {
        MapleFoothold fh_below = this.footholds.findBelow(initial);
        if (fh_below == null) {
            return null;
        }
        int dropY = fh_below.getY1();
        if (!fh_below.isWall() && fh_below.getY1() != fh_below.getY2()) {
            double s1 = Math.abs(fh_below.getY2() - fh_below.getY1());
            double s2 = Math.abs(fh_below.getX2() - fh_below.getX1());
            if (fh_below.getY2() < fh_below.getY1()) {
                dropY = fh_below.getY1() - (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh_below.getX1()) / Math.cos(Math.atan(s1 / s2))));
            } else {
                dropY = fh_below.getY1() + (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh_below.getX1()) / Math.cos(Math.atan(s1 / s2))));
            }
        }
        return new Point(initial.x, dropY);
    }

    public Point calcDropPos(Point initial, Point fallback) {
        Point ret = calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    public MaplePortal getPortal(String portalname) {
        for (MaplePortal port : this.portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public MaplePortal getPortal(int portalid) {
        return this.portals.get(portalid);
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }

    public void addPortal(MaplePortal myPortal) {
        this.portals.put(myPortal.getId(), myPortal);
    }

    public void resetPortals() {
        for (MaplePortal port : this.portals.values()) {
            port.setPortalState(true);
        }
    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = null;
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : this.portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public int getFieldLimit() {
        return this.fieldLimit;
    }

    public void setFieldLimit(int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getHPDecInterval() {
        return this.decHPInterval;
    }

    public void setHPDecInterval(int delta) {
        this.decHPInterval = delta;
    }

    public int getForcedReturnId() {
        return this.forcedReturnMap;
    }

    public void setForcedReturnMap(int mapid) {
        this.forcedReturnMap = mapid;
    }

    public float getRecoveryRate() {
        return this.recoveryRate;
    }

    public void setRecoveryRate(float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getHPDecProtect() {
        return this.protectItem;
    }

    public void setHPDecProtect(int delta) {
        this.protectItem = delta;
    }

    public int getHPDec() {
        return this.decHP;
    }

    public void setHPDec(int delta) {
        this.decHP = delta;
    }

    public final void setForceMove(int fm) {
        this.lvForceMove = fm;
    }

    public boolean isTown() {
        return this.town;
    }

    public void setTown(boolean town) {
        this.town = town;
    }

    public boolean hasClock() {
        return this.clock;
    }

    public void setClock(boolean hasClock) {
        this.clock = hasClock;
    }

    public String getMapName() {
        return this.mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getStreetName() {
        return this.streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

}
