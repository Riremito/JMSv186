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
import tacos.wz.MapWz;
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
        MapWz.MapInfo map_info = WzXML.MAP.getMapInfo(this.map_id);
        if (map_info == null) {
            DebugLogger.ErrorLog("loadData : invalid map id = " + this.map_id);
            return false;
        }
        this.link_id = map_info.getLink();

        MapWz.MapImgInfo mapData = WzXML.MAP.getMapImgInfo(this.link_id != -1 ? this.link_id : this.map_id);
        if (mapData == null) {
            DebugLogger.ErrorLog("loadData : invalid link id = " + this.link_id);
            return false;
        }

        // load info.
        loadInfo(mapData.getInfo());
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

    public boolean loadFootHolds(MapWz.MapImgInfo mapData) {
        for (MapWz.FootholdInfo footHold : mapData.getFootholds()) {
            TacosFoothold fh = new TacosFoothold();
            fh.setId(footHold.getId());
            fh.setX1(footHold.getX1());
            fh.setY1(footHold.getY1());
            fh.setX2(footHold.getX2());
            fh.setY2(footHold.getY2());
            fh.setPrev(footHold.getPrev());
            fh.setNext(footHold.getNext());
            getFootholds().put(fh.getId(), fh);
        }
        return true;
    }

    public boolean loadPortals(MapWz.MapImgInfo mapData) {
        int nextDoorPortal = 0x80;
        for (MapWz.PortalInfo portal_data : mapData.getPortals()) {
            TacosPortal portal = new TacosPortal(portal_data.getPt());

            portal.setName(portal_data.getPn());
            portal.setTarget(portal_data.getTn());
            portal.setTargetMapId(portal_data.getTm());
            portal.setPosition(new Point(portal_data.getX(), portal_data.getY()));
            String script = portal_data.getScript();
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

    public boolean loadInfo(MapWz.MapInfo info) {
        this.clock = info.isClock();
        this.returnMapId = info.getReturnMap();
        this.createMobInterval = info.getCreateMobInterval();
        this.monsterRate = info.getMobRate();
        this.everlast = info.getEverlast() > 0;
        this.town = info.getTown() > 0;
        this.soaring = info.getNeedSkillForFly() > 0;
        this.personalShop = info.getPersonalShop() > 0;
        this.lvForceMove = info.getLvForceMove();
        this.decHP = (int) info.getDecHP();
        this.decHPInterval = info.getDecHPInterval();
        this.protectItem = info.getProtectItem();
        this.forcedReturnMap = info.getForcedReturn();
        this.timeLimit = info.getTimeLimit();
        this.fieldLimit = info.getFieldLimit();
        this.onFirstUserEnter = info.getOnFirstUserEnter();
        this.onUserEnter = info.getOnUserEnter();
        this.recoveryRate = info.getRecovery();
        this.fixedMob = info.getFixedMobCapacity();
        this.consumeItemCoolTime = info.getConsumeItemCoolTime();

        if (this.returnMapId == TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID) {
            this.returnMapId = this.map_id;
        }

        this.time_mob_id = info.getTimeMobId();
        this.time_mob_message = info.getTimeMobMessage();
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
    public boolean loadLife(MapWz.MapImgInfo mapData) {
        for (MapWz.LifeInfo life : mapData.getLifes()) {
            String type = life.getType();

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
    public boolean loadReactor(MapWz.MapImgInfo mapData) {
        if (!mapData.isHasReactor()) {
            return false;
        }
        for (MapWz.ReactorInfo reactor : mapData.getReactors()) {
            TacosReactorSpawnPoint sp = new TacosReactorSpawnPoint();
            if (sp.loadData(reactor)) {
                this.reactor_spawn_point.add(sp);
            }
        }
        return true;
    }

    // nodes nodeInfo.
    private MapleNodes nodeInfo;

    public boolean loadNodeInfo(MapWz.MapImgInfo mapData) {
        this.nodeInfo = new MapleNodes(this.map_id);
        if (mapData.isHasNodeInfo()) {
            if (mapData.getNodeStart() != null) {
                nodeInfo.setNodeStart(mapData.getNodeStart());
            }
            if (mapData.getNodeEnd() != null) {
                nodeInfo.setNodeEnd(mapData.getNodeEnd());
            }
            for (MapWz.NodeInfo node : mapData.getNodes()) {
                final MapleNodes.MapleNodeInfo mni = new MapleNodes.MapleNodeInfo(
                        node.getId(),
                        node.getKey(),
                        node.getX(),
                        node.getY(),
                        node.getAttr(), new ArrayList<>(node.getEdge()));
                nodeInfo.addNode(mni);
            }
            nodeInfo.sortNodes();
        }
        for (MapWz.PlatformInfo node : mapData.getPlatforms()) {
            int sn_count = node.getSnCount();
            String name = node.getName();
            int speed = node.getSpeed();
            if (sn_count <= 0 || speed <= 0 || name.equals("")) {
                continue;
            }
            final List<Integer> SN = new ArrayList<>(node.getSn());
            final MapleNodes.MaplePlatform mni = new MapleNodes.MaplePlatform(
                    name, node.getStart(), speed,
                    node.getX1(),
                    node.getY1(),
                    node.getX2(),
                    node.getY2(),
                    node.getR(), SN);
            nodeInfo.addPlatform(mni);
        }
        // load areas (EG PQ platforms)
        for (MapWz.AreaInfo area : mapData.getAreas()) {
            int x1 = area.getX1();
            int y1 = area.getY1();
            int x2 = area.getX2();
            int y2 = area.getY2();
            Rectangle mapArea = new Rectangle(x1, y1, (x2 - x1), (y2 - y1));
            nodeInfo.addMapleArea(mapArea);
        }
        // monsterCarnival
        for (MapWz.GenPosInfo area : mapData.getCarnivalMobGenPos()) {
            nodeInfo.addMonsterPoint(area.getX(),
                    area.getY(),
                    area.getFh(),
                    area.getCy(),
                    area.getTeam());
        }
        for (MapWz.CarnivalMobInfo area : mapData.getCarnivalMobs()) {
            nodeInfo.addMobSpawn(area.getId(), area.getSpendCP());
        }
        for (MapWz.GenPosInfo area : mapData.getCarnivalGuardianGenPos()) {
            nodeInfo.addGuardianSpawn(new Point(area.getX(), area.getY()), area.getTeam());
        }
        for (int skill_id : mapData.getCarnivalSkills()) {
            nodeInfo.addSkillId(skill_id);
        }

        return true;
    }

    public MapleNodes getNodeInfo() {
        return this.nodeInfo;
    }
}
