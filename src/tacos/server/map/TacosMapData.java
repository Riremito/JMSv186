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
import tacos.wz.MapleData;
import odin.server.maps.MapleFoothold;
import odin.server.maps.MapleFootholdTree;
import odin.server.maps.MapleMap;
import odin.server.maps.MapleNodes;
import odin.server.maps.MapleReactor;
import odin.server.maps.MapleReactorStats;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.wz.WzDataTool;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosMapData {

    // default is for 800x600.
    private static final int SPLIT_WIDTH = 600;
    private static final int SPLIT_HEIGHT = 450;

    protected enum MapSplitState {
        ACTIVE,
        MOVE,
        MOVE_LEAVE,
        ENTER_MOVE,
        UNKNOWN;
    }

    protected class MapWall {

        private int left;
        private int top;
        private int right;
        private int bottom;
    }

    protected class MapScreen {

        private int width;
        private int height;

        protected int getWidth() {
            return this.width;
        }

        protected int getHeight() {
            return this.height;
        }
    }

    protected class MapSplit {

        private int col;
        private int row;
        private int total;

        protected int getTotal() {
            return this.total;
        }

        protected int find(int x, int y) {
            int area_col = (x - wall.left) / SPLIT_WIDTH;
            int area_row = (y - wall.top) / SPLIT_HEIGHT;
            return (area_row * this.col) + area_col;
        }

        protected ArrayList<MapSplitState> getArea(int x, int y, MapSplitState state) {
            ArrayList<MapSplitState> area_states = new ArrayList<>(Collections.nCopies(this.total, MapSplitState.UNKNOWN));

            int area_number = find(x, y);
            int area_row = area_number / this.col;
            int area_col = area_number % this.col;

            for (int row_index = 0; row_index < this.row; row_index++) {
                if (row_index < (area_row - 1) || (area_row + 1) < row_index) {
                    continue;
                }
                for (int col_index = 0; col_index < this.col; col_index++) {
                    if (col_index < (area_col - 1) || (area_col + 1) < col_index) {
                        continue;
                    }
                    area_states.set((row_index * this.col) + col_index, state);
                }
            }

            return area_states;
        }

        protected ArrayList<MapSplitState> getMoveArea(int prev_x, int prev_y, int next_x, int next_y) {
            ArrayList<MapSplitState> area_states = getArea(prev_x, prev_y, MapSplitState.MOVE_LEAVE);
            ArrayList<MapSplitState> area_enter_move = getArea(next_x, next_y, MapSplitState.ENTER_MOVE);

            for (int index = 0; index < area_states.size(); index++) {
                // move only.
                if (area_states.get(index) == MapSplitState.MOVE_LEAVE) {
                    if (area_enter_move.get(index) == MapSplitState.ENTER_MOVE) {
                        area_states.set(index, MapSplitState.MOVE);
                    }
                    continue;
                }
                // move & leave, move, enter & move
                area_states.set(index, area_enter_move.get(index));
            }

            return area_states;
        }
    }

    protected final MapWall wall = new MapWall();
    protected final MapScreen screen = new MapScreen();
    protected final MapSplit split = new MapSplit();

    private boolean setSplitData() {
        this.wall.left = 0;
        this.wall.top = 0;
        this.wall.right = 0;
        this.wall.bottom = 0;
        this.screen.width = 0;
        this.screen.height = 0;
        this.split.col = 0;
        this.split.row = 0;
        this.split.total = 0;

        // calculate wall coordinates.
        for (MapleFoothold foothold : this.footholds.getAll()) {
            int fh_left = Math.min(foothold.getX1(), foothold.getX2());
            int fh_top = Math.min(foothold.getY1(), foothold.getY2());
            int fh_right = Math.max(foothold.getX1(), foothold.getX2());
            int fh_bottom = Math.max(foothold.getY1(), foothold.getY2()) + 10;
            int fh_width = fh_right - fh_left;

            if (fh_left < (this.wall.left + 30)) {
                this.wall.left = fh_left + 30;
            }
            if (fh_top < (this.wall.top - 300)) {
                this.wall.top = fh_top - 300;
            }
            if ((this.wall.right - 30) < fh_right) {
                this.wall.right = fh_right - 30;
            }
            if (fh_width != 0) {
                if (this.wall.bottom < fh_bottom) {
                    this.wall.bottom = fh_bottom;
                }
            }
        }

        // calculate screen width and height.
        this.screen.width = this.wall.right - this.wall.left;
        this.screen.height = this.wall.bottom - this.wall.top;

        // split.
        this.split.col = (this.screen.width + SPLIT_WIDTH - 1) / SPLIT_WIDTH;
        this.split.row = (this.screen.height + SPLIT_HEIGHT - 1) / SPLIT_HEIGHT;
        this.split.total = this.split.col * this.split.row;
        return true;
    }

    protected int map_id;
    private MapleFootholdTree footholds;
    private Map<Integer, TacosPortal> portals = new HashMap<>();
    private ArrayList<TacosSpawnPoint> monster_spawn_point = new ArrayList<>();
    private ArrayList<TacosNPCSpawnPoint> npc_spawn_point = new ArrayList<>();

    public TacosMapData(int mapid) {
        this.map_id = mapid;
    }

    public int getId() {
        return this.map_id;
    }

    public MapleFootholdTree getFootholds() {
        return this.footholds;
    }

    public ArrayList<TacosSpawnPoint> getMonsterSpawnPoint() {
        return this.monster_spawn_point;
    }

    public ArrayList<TacosNPCSpawnPoint> getNPCSpawnPoint() {
        return this.npc_spawn_point;
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
        MapleData mapData = WzXML.MAP.getImg(this.map_id);
        if (mapData == null) {
            DebugLogger.ErrorLog("loadData : invalid map id = " + this.map_id);
            return false;
        }
        this.link_id = WzDataTool.getInt(mapData.getChildByPath("info/link"), -1);

        if (this.link_id != -1) {
            mapData = WzXML.MAP.getImg(this.link_id);
            if (mapData == null) {
                DebugLogger.ErrorLog("loadData : invalid link id = " + this.link_id);
                return false;
            }
        }

        // load info.
        loadInfo(mapData);
        // load fh.
        loadFootHolds(mapData);
        setSplitData();
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

    public boolean loadFootHolds(MapleData mapData) {
        List<MapleFoothold> allFootholds = new LinkedList<>();
        Point lBound = new Point();
        Point uBound = new Point();

        for (MapleData footRoot : mapData.getChildByPath("foothold")) {
            for (MapleData footCat : footRoot) {
                for (MapleData footHold : footCat) {
                    Point p1 = new Point(WzDataTool.getInt(footHold.getChildByPath("x1")), WzDataTool.getInt(footHold.getChildByPath("y1")));
                    Point p2 = new Point(WzDataTool.getInt(footHold.getChildByPath("x2")), WzDataTool.getInt(footHold.getChildByPath("y2")));
                    MapleFoothold fh = new MapleFoothold(p1, p2, Integer.parseInt(footHold.getName()));
                    fh.setPrev((short) WzDataTool.getInt(footHold.getChildByPath("prev")));
                    fh.setNext((short) WzDataTool.getInt(footHold.getChildByPath("next")));

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
        return true;
    }

    public boolean loadPortals(MapleData mapData) {
        int nextDoorPortal = 0x80;
        for (MapleData portal_data : mapData.getChildByPath("portal")) {
            TacosPortal portal = new TacosPortal(WzDataTool.getInt(portal_data.getChildByPath("pt")));

            portal.setName(WzDataTool.getString(portal_data.getChildByPath("pn")));
            portal.setTarget(WzDataTool.getString(portal_data.getChildByPath("tn")));
            portal.setTargetMapId(WzDataTool.getInt(portal_data.getChildByPath("tm")));
            portal.setPosition(new Point(WzDataTool.getInt(portal_data.getChildByPath("x")), WzDataTool.getInt(portal_data.getChildByPath("y"))));
            String script = WzDataTool.getStringPath("script", portal_data, "");
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
    private int time_mob_id;
    private String time_mob_message;

    public boolean loadInfo(MapleData mapData) {
        this.clock = mapData.getChildByPath("clock") != null;
        this.returnMapId = WzDataTool.getIntPath("info/returnMap", mapData, 0);
        this.createMobInterval = WzDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 7000);
        this.monsterRate = WzDataTool.getFloatPath("info/mobRate", mapData, 0.0f);
        this.everlast = WzDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0;
        this.town = WzDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0;
        this.soaring = WzDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0;
        this.personalShop = WzDataTool.getInt(mapData.getChildByPath("info/personalShop"), 0) > 0;
        this.lvForceMove = WzDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0);
        this.decHP = (int) WzDataTool.getLong(mapData.getChildByPath("info/decHP"), 0L);
        this.decHPInterval = WzDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000);
        this.protectItem = WzDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0);
        this.forcedReturnMap = WzDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID);
        this.timeLimit = WzDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1);
        this.fieldLimit = WzDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0);
        this.onFirstUserEnter = WzDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), "");
        this.onUserEnter = WzDataTool.getString(mapData.getChildByPath("info/onUserEnter"), "");
        this.recoveryRate = WzDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1.0f);
        this.fixedMob = WzDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0);
        this.consumeItemCoolTime = WzDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0);

        if (this.returnMapId == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
            this.returnMapId = this.map_id;
        }

        this.time_mob_id = WzDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
        this.time_mob_message = WzDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
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
    public boolean loadLife(MapleData mapData) {
        for (MapleData life : mapData.getChildByPath("life")) {
            String type = WzDataTool.getString(life.getChildByPath("type"));

            switch (type) {
                case "m" -> {
                    TacosSpawnPoint sp = new TacosSpawnPoint();
                    if (sp.loadData(life)) {
                        this.monster_spawn_point.add(sp);
                    }
                    continue;
                }
                case "n" -> {
                    TacosNPCSpawnPoint sp = new TacosNPCSpawnPoint();
                    if (sp.loadData(life)) {
                        this.npc_spawn_point.add(sp);
                    }
                    continue;
                }
                default -> {
                }
            }
        }
        return true;
    }

    // reactor node.
    public boolean loadReactor(MapleData mapData) {
        MapleData reactors = mapData.getChildByPath("reactor");
        if (reactors == null) {
            return true;
        }

        for (MapleData reactor : reactors) {
            int reactor_id = WzDataTool.getInt(reactor.getChildByPath("id"), -1);
            if (reactor_id == -1) {
                DebugLogger.ErrorLog("loadReactor : failed" + mapData.getParent().getName());
                continue;
            }
            int FacingDirection = WzDataTool.getInt(reactor.getChildByPath("f"), 0);

            MapleReactorStats stats = WzXML.REACTOR.getReactor(reactor_id);
            MapleReactor myReactor = new MapleReactor(stats, reactor_id);

            stats.setFacingDirection((byte) FacingDirection);
            myReactor.setPosition(new Point(WzDataTool.getInt(reactor.getChildByPath("x")), WzDataTool.getInt(reactor.getChildByPath("y"))));
            myReactor.setDelay(WzDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
            myReactor.setState((byte) 0);
            myReactor.setName(WzDataTool.getString(reactor.getChildByPath("name"), ""));

            myReactor.setMap(((MapleMap) this));
            ((MapleMap) this).addReactor(myReactor);
        }

        return true;
    }

    // nodes nodeInfo.
    private MapleNodes nodeInfo;

    public boolean loadNodeInfo(MapleData mapData) {
        this.nodeInfo = new MapleNodes(this.map_id);
        if (mapData.getChildByPath("nodeInfo") != null) {
            for (MapleData node : mapData.getChildByPath("nodeInfo")) {
                try {
                    if (node.getName().equals("start")) {
                        nodeInfo.setNodeStart(WzDataTool.getInt(node, 0));
                        continue;
                    } else if (node.getName().equals("end")) {
                        nodeInfo.setNodeEnd(WzDataTool.getInt(node, 0));
                        continue;
                    }
                    List<Integer> edges = new ArrayList<>();
                    if (node.getChildByPath("edge") != null) {
                        for (MapleData edge : node.getChildByPath("edge")) {
                            edges.add(WzDataTool.getInt(edge, -1));
                        }
                    }
                    final MapleNodes.MapleNodeInfo mni = new MapleNodes.MapleNodeInfo(
                            Integer.parseInt(node.getName()),
                            WzDataTool.getIntPath("key", node, 0),
                            WzDataTool.getIntPath("x", node, 0),
                            WzDataTool.getIntPath("y", node, 0),
                            WzDataTool.getIntPath("attr", node, 0), edges);
                    nodeInfo.addNode(mni);
                } catch (NumberFormatException e) {
                } //start, end, edgeInfo = we dont need it
            }
            nodeInfo.sortNodes();
        }
        for (int i = 1; i <= 7; i++) {
            if (mapData.getChildByPath(String.valueOf(i)) != null && mapData.getChildByPath(i + "/obj") != null) {
                for (MapleData node : mapData.getChildByPath(i + "/obj")) {
                    int sn_count = WzDataTool.getIntPath("SN_count", node, 0);
                    String name = WzDataTool.getStringPath("name", node, "");
                    int speed = WzDataTool.getIntPath("speed", node, 0);
                    if (sn_count <= 0 || speed <= 0 || name.equals("")) {
                        continue;
                    }
                    final List<Integer> SN = new ArrayList<>();
                    for (int x = 0; x < sn_count; x++) {
                        SN.add(WzDataTool.getIntPath("SN" + x, node, 0));
                    }
                    final MapleNodes.MaplePlatform mni = new MapleNodes.MaplePlatform(
                            name, WzDataTool.getIntPath("start", node, 2), speed,
                            WzDataTool.getIntPath("x1", node, 0),
                            WzDataTool.getIntPath("y1", node, 0),
                            WzDataTool.getIntPath("x2", node, 0),
                            WzDataTool.getIntPath("y2", node, 0),
                            WzDataTool.getIntPath("r", node, 0), SN);
                    nodeInfo.addPlatform(mni);
                }
            }
        }
        // load areas (EG PQ platforms)
        if (mapData.getChildByPath("area") != null) {
            int x1, y1, x2, y2;
            Rectangle mapArea;
            for (MapleData area : mapData.getChildByPath("area")) {
                x1 = WzDataTool.getInt(area.getChildByPath("x1"));
                y1 = WzDataTool.getInt(area.getChildByPath("y1"));
                x2 = WzDataTool.getInt(area.getChildByPath("x2"));
                y2 = WzDataTool.getInt(area.getChildByPath("y2"));
                mapArea = new Rectangle(x1, y1, (x2 - x1), (y2 - y1));
                nodeInfo.addMapleArea(mapArea);
            }
        }
        if (mapData.getChildByPath("monsterCarnival") != null) {
            final MapleData mc = mapData.getChildByPath("monsterCarnival");
            if (mc.getChildByPath("mobGenPos") != null) {
                for (MapleData area : mc.getChildByPath("mobGenPos")) {
                    nodeInfo.addMonsterPoint(WzDataTool.getInt(area.getChildByPath("x")),
                            WzDataTool.getInt(area.getChildByPath("y")),
                            WzDataTool.getInt(area.getChildByPath("fh")),
                            WzDataTool.getInt(area.getChildByPath("cy")),
                            WzDataTool.getIntPath("team", area, -1));
                }
            }
            if (mc.getChildByPath("mob") != null) {
                for (MapleData area : mc.getChildByPath("mob")) {
                    nodeInfo.addMobSpawn(WzDataTool.getInt(area.getChildByPath("id")), WzDataTool.getInt(area.getChildByPath("spendCP")));
                }
            }
            if (mc.getChildByPath("guardianGenPos") != null) {
                for (MapleData area : mc.getChildByPath("guardianGenPos")) {
                    nodeInfo.addGuardianSpawn(new Point(WzDataTool.getInt(area.getChildByPath("x")), WzDataTool.getInt(area.getChildByPath("y"))), WzDataTool.getIntPath("team", area, -1));
                }
            }
            if (mc.getChildByPath("skill") != null) {
                for (MapleData area : mc.getChildByPath("skill")) {
                    nodeInfo.addSkillId(WzDataTool.getInt(area));
                }
            }
        }

        return true;
    }

    public MapleNodes getNodeInfo() {
        return this.nodeInfo;
    }
}
