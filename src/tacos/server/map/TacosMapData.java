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
import java.awt.Rectangle;
import java.util.ArrayList;
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
import odin.server.maps.MapleNodes;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleReactorStats;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.wz.TacosWzDataTool;
import tacos.wz.data.MapWz;
import tacos.wz.data.ReactorWz;
import tacos.wz.ids.DWI_Block;

/**
 *
 * @author Riremito
 */
public class TacosMapData {

    protected int map_id;

    public TacosMapData(int mapid) {
        this.map_id = mapid;
    }

    public int getId() {
        return this.map_id;
    }

    public Point calcDropPos(Point initial, Point fallback) {
        Point ret = calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    // load wz data.
    private int link_id;

    public boolean loadData() {
        IMapleData mapData = MapWz.get().getImg(this.map_id);
        if (mapData == null) {
            DebugLogger.ErrorLog("loadData : invalid map id = " + this.map_id);
            return false;
        }
        this.link_id = TacosWzDataTool.getInt(mapData.getChildByPath("info/link"), -1);

        if (this.link_id != -1) {
            mapData = MapWz.get().getImg(this.link_id);
            if (mapData == null) {
                DebugLogger.ErrorLog("loadData : invalid link id = " + this.link_id);
                return false;
            }
        }

        // load info.
        loadInfo(mapData);
        // load fh.
        loadFootHolds(mapData);
        // load portal.
        loadPortals(mapData);
        // load life.
        loadLife(mapData);
        // load reactor.
        loadReactor(mapData);
        // load nodeInfo.
        loadNodeInfo(mapData);
        return true;
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
    private int returnMapId;
    private int createMobInterval;
    private float monsterRate;
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
        this.returnMapId = TacosWzDataTool.getIntPath("info/returnMap", mapData, 0);
        this.createMobInterval = TacosWzDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000);
        this.monsterRate = TacosWzDataTool.getFloatPath("info/mobRate", mapData, 0.0f);
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

        if (this.returnMapId == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
            this.returnMapId = this.map_id;
        }

        return true;
    }

    public boolean hasClock() {
        return this.clock;
    }

    public int getReturnMapId() {
        return this.returnMapId;
    }

    public long getCreateMobInterval() {
        return this.createMobInterval;
    }

    public float getMonsterRate() {
        return this.monsterRate;
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
        IMapleData reactors = mapData.getChildByPath("reactor");
        if (reactors == null) {
            return true;
        }

        for (IMapleData reactor : reactors) {
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

    // nodes nodeInfo.
    private MapleNodes nodeInfo;

    public boolean loadNodeInfo(IMapleData mapData) {
        this.nodeInfo = new MapleNodes(this.map_id);
        if (mapData.getChildByPath("nodeInfo") != null) {
            for (IMapleData node : mapData.getChildByPath("nodeInfo")) {
                try {
                    if (node.getName().equals("start")) {
                        nodeInfo.setNodeStart(TacosWzDataTool.getInt(node, 0));
                        continue;
                    } else if (node.getName().equals("end")) {
                        nodeInfo.setNodeEnd(TacosWzDataTool.getInt(node, 0));
                        continue;
                    }
                    List<Integer> edges = new ArrayList<>();
                    if (node.getChildByPath("edge") != null) {
                        for (IMapleData edge : node.getChildByPath("edge")) {
                            edges.add(TacosWzDataTool.getInt(edge, -1));
                        }
                    }
                    final MapleNodes.MapleNodeInfo mni = new MapleNodes.MapleNodeInfo(
                            Integer.parseInt(node.getName()),
                            TacosWzDataTool.getIntPath("key", node, 0),
                            TacosWzDataTool.getIntPath("x", node, 0),
                            TacosWzDataTool.getIntPath("y", node, 0),
                            TacosWzDataTool.getIntPath("attr", node, 0), edges);
                    nodeInfo.addNode(mni);
                } catch (NumberFormatException e) {
                } //start, end, edgeInfo = we dont need it
            }
            nodeInfo.sortNodes();
        }
        for (int i = 1; i <= 7; i++) {
            if (mapData.getChildByPath(String.valueOf(i)) != null && mapData.getChildByPath(i + "/obj") != null) {
                for (IMapleData node : mapData.getChildByPath(i + "/obj")) {
                    int sn_count = TacosWzDataTool.getIntPath("SN_count", node, 0);
                    String name = TacosWzDataTool.getStringPath("name", node, "");
                    int speed = TacosWzDataTool.getIntPath("speed", node, 0);
                    if (sn_count <= 0 || speed <= 0 || name.equals("")) {
                        continue;
                    }
                    final List<Integer> SN = new ArrayList<>();
                    for (int x = 0; x < sn_count; x++) {
                        SN.add(TacosWzDataTool.getIntPath("SN" + x, node, 0));
                    }
                    final MapleNodes.MaplePlatform mni = new MapleNodes.MaplePlatform(
                            name, TacosWzDataTool.getIntPath("start", node, 2), speed,
                            TacosWzDataTool.getIntPath("x1", node, 0),
                            TacosWzDataTool.getIntPath("y1", node, 0),
                            TacosWzDataTool.getIntPath("x2", node, 0),
                            TacosWzDataTool.getIntPath("y2", node, 0),
                            TacosWzDataTool.getIntPath("r", node, 0), SN);
                    nodeInfo.addPlatform(mni);
                }
            }
        }
        // load areas (EG PQ platforms)
        if (mapData.getChildByPath("area") != null) {
            int x1, y1, x2, y2;
            Rectangle mapArea;
            for (IMapleData area : mapData.getChildByPath("area")) {
                x1 = TacosWzDataTool.getInt(area.getChildByPath("x1"));
                y1 = TacosWzDataTool.getInt(area.getChildByPath("y1"));
                x2 = TacosWzDataTool.getInt(area.getChildByPath("x2"));
                y2 = TacosWzDataTool.getInt(area.getChildByPath("y2"));
                mapArea = new Rectangle(x1, y1, (x2 - x1), (y2 - y1));
                nodeInfo.addMapleArea(mapArea);
            }
        }
        if (mapData.getChildByPath("monsterCarnival") != null) {
            final IMapleData mc = mapData.getChildByPath("monsterCarnival");
            if (mc.getChildByPath("mobGenPos") != null) {
                for (IMapleData area : mc.getChildByPath("mobGenPos")) {
                    nodeInfo.addMonsterPoint(TacosWzDataTool.getInt(area.getChildByPath("x")),
                            TacosWzDataTool.getInt(area.getChildByPath("y")),
                            TacosWzDataTool.getInt(area.getChildByPath("fh")),
                            TacosWzDataTool.getInt(area.getChildByPath("cy")),
                            TacosWzDataTool.getIntPath("team", area, -1));
                }
            }
            if (mc.getChildByPath("mob") != null) {
                for (IMapleData area : mc.getChildByPath("mob")) {
                    nodeInfo.addMobSpawn(TacosWzDataTool.getInt(area.getChildByPath("id")), TacosWzDataTool.getInt(area.getChildByPath("spendCP")));
                }
            }
            if (mc.getChildByPath("guardianGenPos") != null) {
                for (IMapleData area : mc.getChildByPath("guardianGenPos")) {
                    nodeInfo.addGuardianSpawn(new Point(TacosWzDataTool.getInt(area.getChildByPath("x")), TacosWzDataTool.getInt(area.getChildByPath("y"))), TacosWzDataTool.getIntPath("team", area, -1));
                }
            }
            if (mc.getChildByPath("skill") != null) {
                for (IMapleData area : mc.getChildByPath("skill")) {
                    nodeInfo.addSkillId(TacosWzDataTool.getInt(area));
                }
            }
        }

        return true;
    }

    public MapleNodes getNodeInfo() {
        return this.nodeInfo;
    }

    // TODO : CAN WE FIX IT?
}
