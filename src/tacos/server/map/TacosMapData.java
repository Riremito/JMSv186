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
import odin.provider.IMapleData;
import odin.server.maps.MapleFoothold;
import odin.server.maps.MapleFootholdTree;
import tacos.constants.TacosConstants;
import tacos.wz.TacosWzDataTool;

/**
 *
 * @author Riremito
 */
public class TacosMapData {

    protected int map_id;
    protected int returnMapId;
    private MapleFootholdTree footholds = null;
    private Map<Integer, TacosPortal> portals = new HashMap<>();
    private String mapName, streetName;
    protected TacosMapSplit map_split = new TacosMapSplit();

    public TacosMapData(int mapid, int returnMapId) {
        this.map_id = mapid;
        this.returnMapId = returnMapId;
        if (this.returnMapId == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
            this.returnMapId = mapid;
        }
    }

    public int getId() {
        return this.map_id;
    }

    public int getReturnMapId() {
        return this.returnMapId;
    }

    public MapleFootholdTree getFootholds() {
        return this.footholds;
    }

    public void setFootholds(MapleFootholdTree footholds) {
        this.footholds = footholds;
        this.map_split.setSplit(this.footholds.getAll());
    }

    public TacosMapSplit getMapSplit() {
        return this.map_split;
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

    public TacosPortal getPortal(String portalname) {
        for (TacosPortal port : this.portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public TacosPortal getPortal(int portalid) {
        return this.portals.get(portalid);
    }

    public Collection<TacosPortal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }

    public void addPortal(TacosPortal myPortal) {
        this.portals.put(myPortal.getId(), myPortal);
    }

    public void resetPortals() {
        for (TacosPortal port : this.portals.values()) {
            port.setPortalState(true);
        }
    }

    public TacosPortal findClosestSpawnpoint(Point from) {
        TacosPortal closest = null;
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (TacosPortal portal : this.portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
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

    // load map data from wz.
    private boolean clock;
    private boolean everlast;
    private boolean town;
    private boolean soaring;
    private boolean personalShop;
    private int lvForceMove;
    private int decHP = 0;
    private int decHPInterval;
    private int protectItem;
    private int forcedReturnMap;
    private int timeLimit;
    private int fieldLimit;
    private String onFirstUserEnter;
    private String onUserEnter;
    private float recoveryRate;
    private int fixedMob;
    private int consumeItemCoolTime;

    public boolean loadMapData(IMapleData mapData) {
        this.clock = mapData.getChildByPath("clock") != null;
        this.everlast = TacosWzDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0;
        this.town = TacosWzDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0;
        this.soaring = TacosWzDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0;
        this.personalShop = TacosWzDataTool.getInt(mapData.getChildByPath("info/personalShop"), 0) > 0;
        this.lvForceMove = TacosWzDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0);
        this.decHP = (int) TacosWzDataTool.getLong(mapData.getChildByPath("info/decHP"), 0L);
        this.decHPInterval = TacosWzDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000);
        this.protectItem = TacosWzDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0);
        this.forcedReturnMap = TacosWzDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID);
        this.timeLimit = TacosWzDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1);
        this.fieldLimit = TacosWzDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0);
        this.onFirstUserEnter = TacosWzDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), "");
        this.onUserEnter = TacosWzDataTool.getString(mapData.getChildByPath("info/onUserEnter"), "");
        this.recoveryRate = TacosWzDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0f);
        this.fixedMob = TacosWzDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0);
        this.consumeItemCoolTime = TacosWzDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0);
        return true;
    }

    public boolean hasClock() {
        return this.clock;
    }

    public boolean getEverlast() {
        return this.everlast;
    }

    public boolean isTown() {
        return this.town;
    }

    public boolean canSoar() {
        return this.soaring;
    }

    public boolean allowPersonalShop() {
        return this.personalShop;
    }

    public int getHPDec() {
        return this.decHP;
    }

    public int getHPDecInterval() {
        return this.decHPInterval;
    }

    public int getHPDecProtect() {
        return this.protectItem;
    }

    public int getForcedReturnId() {
        return this.forcedReturnMap;
    }

    public int getTimeLimit() {
        return this.timeLimit;
    }

    public int getFieldLimit() {
        return this.fieldLimit;
    }

    public String getFirstUserEnter() {
        return this.onFirstUserEnter;
    }

    public String getUserEnter() {
        return this.onUserEnter;
    }

    public float getRecoveryRate() {
        return this.recoveryRate;
    }

    public int getFixedMob() {
        return this.fixedMob;
    }

    public int getConsumeItemCoolTime() {
        return this.consumeItemCoolTime;
    }

    // TODO : CAN WE FIX IT?
}
