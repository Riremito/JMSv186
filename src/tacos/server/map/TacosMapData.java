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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import tacos.wz.MapleData;
import odin.server.maps.MapleNodes;
import tacos.constants.TacosConstants;
import tacos.debug.DebugLogger;
import tacos.wz.WzXML;

/**
 *
 * @author Riremito
 */
public class TacosMapData {

    // default is for 800x600.
    private static final int SPLIT_WIDTH = 600;
    private static final int SPLIT_HEIGHT = 450;

    public enum MapSplitState {
        ACTIVE,
        MOVE,
        MOVE_LEAVE,
        ENTER_MOVE,
        UNKNOWN;
    }

    @Getter
    protected class MapWall {

        private int left;
        private int top;
        private int right;
        private int bottom;
    }

    @Getter
    protected class MapScreen {

        private int width;
        private int height;
    }

    @Getter
    public class MapSplit {

        private int col;
        private int row;
        private int total;

        public int find(Point position) {
            return find(position.x, position.y);
        }

        public int find(int x, int y) {
            int area_col = (x - wall.left) / SPLIT_WIDTH;
            int area_row = (y - wall.top) / SPLIT_HEIGHT;
            int area = (area_row * this.col) + area_col;
            if (area <= -1 || this.total <= area) {
                area = 0;
            }
            return area;
        }

        public ArrayList<MapSplitState> getArea(Point pos, MapSplitState state) {
            return getArea(pos.x, pos.y, state);
        }

        public ArrayList<MapSplitState> getArea(int x, int y, MapSplitState state) {
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

    public MapSplit getSplit() {
        return this.split;
    }

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
        for (TacosFoothold foothold : getFootholds().values()) {
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
    private Map<Integer, TacosPortal> portals = new HashMap<>();
    private ArrayList<TacosSpawnPoint> monster_spawn_point = new ArrayList<>();
    private ArrayList<TacosNPCSpawnPoint> npc_spawn_point = new ArrayList<>();
    private ArrayList<TacosReactorSpawnPoint> reactor_spawn_point = new ArrayList<>();

    public TacosMapData(int mapid) {
        this.map_id = mapid;
    }

    public int getId() {
        return this.map_id;
    }

    private final LinkedHashMap<Integer, TacosFoothold> footholds = new LinkedHashMap<>();

    public LinkedHashMap<Integer, TacosFoothold> getFootholds() {
        return this.footholds;
    }

    public TacosFoothold findBelow(int x, int y) {
        return findBelow(new Point(x, y));
    }

    public TacosFoothold findBelow(Point pt) {
        TacosFoothold ground_fh = null;
        for (TacosFoothold foothold : getFootholds().values()) {
            if (foothold.isWall()) {
                continue;
            }
            if (pt.getX() < foothold.getX1() || foothold.getX2() < pt.getX()) {
                continue;
            }
            if (foothold.getY1() < pt.getY()) {
                continue;
            }
            if (ground_fh != null) {
                if (ground_fh.getY1() < foothold.getY1()) {
                    continue;
                }
            }
            ground_fh = foothold;
        }
        return ground_fh;
    }

    public Point calcPointBelow(Point initial) {
        TacosFoothold fh_below = findBelow(initial);
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

    public ArrayList<TacosSpawnPoint> getMonsterSpawnPoint() {
        return this.monster_spawn_point;
    }

    public ArrayList<TacosNPCSpawnPoint> getNPCSpawnPoint() {
        return this.npc_spawn_point;
    }

    public ArrayList<TacosReactorSpawnPoint> getReactorSpawnPoint() {
        return this.reactor_spawn_point;
    }

    // load wz data.
    private int link_id;

    public boolean loadData() {
        MapleData mapData = WzXML.MAP.getImg(this.map_id);
        if (mapData == null) {
            DebugLogger.ErrorLog("loadData : invalid map id = " + this.map_id);
            return false;
        }
        this.link_id = mapData.getInt("info/link", -1);

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
        for (MapleData footRoot : mapData.getChildByPath("foothold")) {
            for (MapleData footCat : footRoot) {
                for (MapleData footHold : footCat) {
                    TacosFoothold fh = new TacosFoothold();
                    fh.setId(Integer.parseInt(footHold.getName()));
                    fh.setX1(footHold.getInt("x1"));
                    fh.setY1(footHold.getInt("y1"));
                    fh.setX2(footHold.getInt("x2"));
                    fh.setY2(footHold.getInt("y2"));
                    fh.setPrev(footHold.getInt("prev"));
                    fh.setNext(footHold.getInt("next"));
                    getFootholds().put(fh.getId(), fh);
                }
            }
        }
        return true;
    }

    public boolean loadPortals(MapleData mapData) {
        int nextDoorPortal = 0x80;
        for (MapleData portal_data : mapData.getChildByPath("portal")) {
            TacosPortal portal = new TacosPortal(portal_data.getInt("pt"));

            portal.setName(portal_data.getString("pn"));
            portal.setTarget(portal_data.getString("tn"));
            portal.setTargetMapId(portal_data.getInt("tm"));
            portal.setPosition(new Point(portal_data.getInt("x"), portal_data.getInt("y")));
            String script = portal_data.getString("script");
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
        this.returnMapId = mapData.getInt("info/returnMap");
        this.createMobInterval = mapData.getInt("info/createMobInterval", 7000);
        this.monsterRate = mapData.getFloat("info/mobRate");
        this.everlast = mapData.getInt("info/everlast") > 0;
        this.town = mapData.getInt("info/town") > 0;
        this.soaring = mapData.getInt("info/needSkillForFly") > 0;
        this.personalShop = mapData.getInt("info/personalShop") > 0;
        this.lvForceMove = mapData.getInt("info/lvForceMove");
        this.decHP = (int) mapData.getLong("info/decHP", 0L);
        this.decHPInterval = mapData.getInt("info/decHPInterval", 10000);
        this.protectItem = mapData.getInt("info/protectItem");
        this.forcedReturnMap = mapData.getInt("info/forcedReturn", TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID);
        this.timeLimit = mapData.getInt("info/timeLimit", -1);
        this.fieldLimit = mapData.getInt("info/fieldLimit");
        this.onFirstUserEnter = mapData.getString("info/onFirstUserEnter");
        this.onUserEnter = mapData.getString("info/onUserEnter");
        this.recoveryRate = mapData.getFloat("info/recovery", 1.0f);
        this.fixedMob = mapData.getInt("info/fixedMobCapacity");
        this.consumeItemCoolTime = mapData.getInt("info/consumeItemCoolTime");

        if (this.returnMapId == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
            this.returnMapId = this.map_id;
        }

        this.time_mob_id = mapData.getInt("info/timeMob/id");
        this.time_mob_message = mapData.getString("info/timeMob/message", null);
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
            String type = life.getString("type");

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
        MapleData md_reactor = mapData.getChildByPath("reactor");
        if (md_reactor == null) {
            return false;
        }
        for (MapleData reactor : md_reactor.getChildren()) {
            TacosReactorSpawnPoint sp = new TacosReactorSpawnPoint();
            if (sp.loadData(reactor)) {
                this.reactor_spawn_point.add(sp);
            }
        }
        return true;
    }

    // nodes nodeInfo.
    private MapleNodes nodeInfo;

    public boolean loadNodeInfo(MapleData mapData) {
        this.nodeInfo = new MapleNodes(this.map_id);
        MapleData md_node_info = mapData.getChildByPath("nodeInfo");
        if (md_node_info != null) {
            for (MapleData node : md_node_info) {
                try {
                    if (node.getName().equals("start")) {
                        nodeInfo.setNodeStart(md_node_info.getInt(node.getName()));
                        continue;
                    } else if (node.getName().equals("end")) {
                        nodeInfo.setNodeEnd(md_node_info.getInt(node.getName()));
                        continue;
                    }
                    List<Integer> edges = new ArrayList<>();
                    MapleData md_edge = node.getChildByPath("edge");
                    if (md_edge != null) {
                        for (MapleData edge : md_edge) {
                            edges.add(md_edge.getInt(edge.getName(), -1));
                        }
                    }
                    final MapleNodes.MapleNodeInfo mni = new MapleNodes.MapleNodeInfo(
                            Integer.parseInt(node.getName()),
                            node.getInt("key"),
                            node.getInt("x"),
                            node.getInt("y"),
                            node.getInt("attr"), edges);
                    nodeInfo.addNode(mni);
                } catch (NumberFormatException e) {
                } //start, end, edgeInfo = we dont need it
            }
            nodeInfo.sortNodes();
        }
        for (int i = 1; i <= 7; i++) {
            if (mapData.getChildByPath(String.valueOf(i)) != null && mapData.getChildByPath(i + "/obj") != null) {
                for (MapleData node : mapData.getChildByPath(i + "/obj")) {
                    int sn_count = node.getInt("SN_count");
                    String name = node.getString("name");
                    int speed = node.getInt("speed");
                    if (sn_count <= 0 || speed <= 0 || name.equals("")) {
                        continue;
                    }
                    final List<Integer> SN = new ArrayList<>();
                    for (int x = 0; x < sn_count; x++) {
                        SN.add(node.getInt("SN" + x));
                    }
                    final MapleNodes.MaplePlatform mni = new MapleNodes.MaplePlatform(
                            name, node.getInt("start", 2), speed,
                            node.getInt("x1"),
                            node.getInt("y1"),
                            node.getInt("x2"),
                            node.getInt("y2"),
                            node.getInt("r"), SN);
                    nodeInfo.addPlatform(mni);
                }
            }
        }
        // load areas (EG PQ platforms)
        if (mapData.getChildByPath("area") != null) {
            int x1, y1, x2, y2;
            Rectangle mapArea;
            for (MapleData area : mapData.getChildByPath("area")) {
                x1 = area.getInt("x1");
                y1 = area.getInt("y1");
                x2 = area.getInt("x2");
                y2 = area.getInt("y2");
                mapArea = new Rectangle(x1, y1, (x2 - x1), (y2 - y1));
                nodeInfo.addMapleArea(mapArea);
            }
        }
        if (mapData.getChildByPath("monsterCarnival") != null) {
            final MapleData mc = mapData.getChildByPath("monsterCarnival");
            if (mc.getChildByPath("mobGenPos") != null) {
                for (MapleData area : mc.getChildByPath("mobGenPos")) {
                    nodeInfo.addMonsterPoint(area.getInt("x"),
                            area.getInt("y"),
                            area.getInt("fh"),
                            area.getInt("cy"),
                            area.getInt("team", -1));
                }
            }
            if (mc.getChildByPath("mob") != null) {
                for (MapleData area : mc.getChildByPath("mob")) {
                    nodeInfo.addMobSpawn(area.getInt("id"), area.getInt("spendCP"));
                }
            }
            if (mc.getChildByPath("guardianGenPos") != null) {
                for (MapleData area : mc.getChildByPath("guardianGenPos")) {
                    nodeInfo.addGuardianSpawn(new Point(area.getInt("x"), area.getInt("y")), area.getInt("team", -1));
                }
            }
            MapleData md_skill = mc.getChildByPath("skill");
            if (md_skill != null) {
                for (MapleData area : md_skill) {
                    nodeInfo.addSkillId(md_skill.getInt(area.getName()));
                }
            }
        }

        return true;
    }

    public MapleNodes getNodeInfo() {
        return this.nodeInfo;
    }
}
