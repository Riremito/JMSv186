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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import odin.provider.IMapleData;
import odin.server.life.AbstractLoadedMapleLife;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.life.MapleNPC;
import odin.server.maps.MapleFoothold;
import odin.server.maps.MapleFootholdTree;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleReactorStats;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.wz.TacosWzDataTool;
import tacos.wz.data.ReactorWz;
import tacos.wz.ids.DWI_Block;

/**
 *
 * @author Riremito
 */
public class TacosMapData {

    protected int map_id;
    protected int returnMapId;

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

    public Point calcDropPos(Point initial, Point fallback) {
        Point ret = calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    // portal node.
    private Map<Integer, TacosPortal> portals = new HashMap<>();

    public boolean loadPortals(IMapleData mapData) {
        int nextDoorPortal = 0x80;
        for (IMapleData portal_data : mapData.getChildByPath("portal")) {
            TacosPortal portal = new TacosPortal(TacosWzDataTool.getInt(portal_data.getChildByPath("pt")));

            portal.setName(TacosWzDataTool.getString(portal_data.getChildByPath("pn")));
            portal.setTarget(TacosWzDataTool.getString(portal_data.getChildByPath("tn")));
            portal.setTargetMapId(TacosWzDataTool.getInt(portal_data.getChildByPath("tm")));
            portal.setPosition(new Point(TacosWzDataTool.getInt(portal_data.getChildByPath("x")), TacosWzDataTool.getInt(portal_data.getChildByPath("y"))));
            String script = TacosWzDataTool.getStringPath("script", portal_data, "");
            portal.setScriptName(script.equals("") ? null : script);

            if (portal.getType() == TacosPortal.DOOR_PORTAL) {
                portal.setId(nextDoorPortal);
                nextDoorPortal++;
            } else {
                portal.setId(this.portals.size());
            }

            this.portals.put(portal.getId(), portal);
        }

        return true;
    }

    public TacosPortal getPortal(String portalname) {
        for (TacosPortal port : this.portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public TacosPortal getPortal(int portal_id) {
        return this.portals.get(portal_id);
    }

    public Collection<TacosPortal> getPortals() {
        return Collections.unmodifiableCollection(this.portals.values());
    }

    public void resetPortals() {
        for (TacosPortal portal : this.portals.values()) {
            portal.setPortalState(true);
        }
    }

    public TacosPortal findClosestSpawnpoint(Point from) {
        TacosPortal closest = null;
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (TacosPortal portal : this.portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance && portal.getTargetMapId() == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    // foothold node.
    private MapleFootholdTree footholds;
    protected TacosMapSplit map_split = new TacosMapSplit();

    public boolean loadFootHolds(IMapleData mapData) {
        List<MapleFoothold> allFootholds = new LinkedList<>();
        Point lBound = new Point();
        Point uBound = new Point();

        for (IMapleData footRoot : mapData.getChildByPath("foothold")) {
            for (IMapleData footCat : footRoot) {
                for (IMapleData footHold : footCat) {
                    Point p1 = new Point(TacosWzDataTool.getInt(footHold.getChildByPath("x1")), TacosWzDataTool.getInt(footHold.getChildByPath("y1")));
                    Point p2 = new Point(TacosWzDataTool.getInt(footHold.getChildByPath("x2")), TacosWzDataTool.getInt(footHold.getChildByPath("y2")));
                    MapleFoothold fh = new MapleFoothold(p1, p2, Integer.parseInt(footHold.getName()));
                    fh.setPrev((short) TacosWzDataTool.getInt(footHold.getChildByPath("prev")));
                    fh.setNext((short) TacosWzDataTool.getInt(footHold.getChildByPath("next")));

                    if (fh.getX1() < lBound.x) {
                        lBound.x = fh.getX1();
                    }
                    if (fh.getX2() > uBound.x) {
                        uBound.x = fh.getX2();
                    }
                    if (fh.getY1() < lBound.y) {
                        lBound.y = fh.getY1();
                    }
                    if (fh.getY2() > uBound.y) {
                        uBound.y = fh.getY2();
                    }
                    allFootholds.add(fh);
                }
            }
        }

        MapleFootholdTree fTree = new MapleFootholdTree(lBound, uBound);
        for (MapleFoothold foothold : allFootholds) {
            fTree.insert(foothold);
        }

        this.footholds = fTree;
        this.map_split.setSplit(this.footholds.getAll());
        return true;
    }

    public MapleFootholdTree getFootholds() {
        return this.footholds;
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

    // info node.
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

    public boolean loadInfo(IMapleData mapData) {
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

    // life node.
    public boolean loadLife(IMapleData mapData) {
        int bossid = -1;
        String msg = null;
        if (mapData.getChildByPath("info/timeMob") != null) {
            bossid = TacosWzDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
            msg = TacosWzDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
        }

        for (IMapleData life : mapData.getChildByPath("life")) {
            String type = TacosWzDataTool.getString(life.getChildByPath("type"));
            int npc_id = TacosWzDataTool.getInt(life.getChildByPath("id"), -1);
            if (npc_id == -1) {
                DebugLogger.ErrorLog("loadLife : failed" + mapData.getParent().getName());
                continue;
            }
            AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(npc_id, type);

            if (myLife == null) {
                DebugLogger.ErrorLog("loadLife : failed, " + npc_id);
                continue;
            }

            myLife.setCy(TacosWzDataTool.getInt(life.getChildByPath("cy")));
            IMapleData dF = life.getChildByPath("f");
            if (dF != null) {
                myLife.setF(TacosWzDataTool.getInt(dF));
            }
            myLife.setFh(TacosWzDataTool.getInt(life.getChildByPath("fh")));
            myLife.setRx0(TacosWzDataTool.getInt(life.getChildByPath("rx0")));
            myLife.setRx1(TacosWzDataTool.getInt(life.getChildByPath("rx1")));
            myLife.setPosition(new Point(TacosWzDataTool.getInt(life.getChildByPath("x")), TacosWzDataTool.getInt(life.getChildByPath("y"))));

            if (myLife instanceof MapleNPC) {
                if (TacosWzDataTool.getIntPath("hide", life, 0) == 1) {
                    myLife.setHide(true);
                    DebugLogger.InfoLog("loadLife : hidden npc, " + npc_id);
                }
                if (DWI_Block.checkNpc(myLife.getId())) {
                    DebugLogger.InfoLog("loadLife : blocked npc, " + npc_id);
                    continue;
                }
                ((MapleMap) this).addMapObject(myLife);
            }
            if (myLife instanceof MapleMonster) {
                MapleMonster mob = (MapleMonster) myLife;
                if (DWI_Block.checkMob(myLife.getId())) {
                    DebugLogger.InfoLog("loadLife : blocked mob, " + npc_id);
                    continue;
                }
                ((MapleMap) this).addMonsterSpawn(mob, TacosWzDataTool.getIntPath("mobTime", life, 0), (byte) TacosWzDataTool.getIntPath("team", life, -1), mob.getId() == bossid ? msg : null);
            }
        }
        return true;
    }

    // reactor node.
    public boolean loadReactor(IMapleData mapData) {
        for (IMapleData reactor : mapData.getChildByPath("reactor")) {
            int reactor_id = TacosWzDataTool.getInt(reactor.getChildByPath("id"), -1);
            if (reactor_id == -1) {
                DebugLogger.ErrorLog("loadReactor : failed" + mapData.getParent().getName());
                continue;
            }
            int FacingDirection = TacosWzDataTool.getInt(reactor.getChildByPath("f"), 0);

            MapleReactorStats stats = ReactorWz.get().getReactor(reactor_id);
            MapleReactor myReactor = new MapleReactor(stats, reactor_id);

            stats.setFacingDirection((byte) FacingDirection);
            myReactor.setPosition(new Point(TacosWzDataTool.getInt(reactor.getChildByPath("x")), TacosWzDataTool.getInt(reactor.getChildByPath("y"))));
            myReactor.setDelay(TacosWzDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
            myReactor.setState((byte) 0);
            myReactor.setName(TacosWzDataTool.getString(reactor.getChildByPath("name"), ""));

            myReactor.setMap(((MapleMap) this));
            ((MapleMap) this).addMapObject(myReactor);
        }

        return true;
    }

    // TODO : CAN WE FIX IT?
}
