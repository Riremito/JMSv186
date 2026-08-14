/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package odin.server.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tacos.odin.OdinPair;

public class MapleNodes {

    private Map<Integer, MapleNodeInfo> nodes; //used for HOB pq.
    private final List<Rectangle> areas;
    private List<MaplePlatform> platforms;
    private List<MonsterPoint> monsterPoints;
    private List<Integer> skillIds;
    private List<OdinPair<Integer, Integer>> mobsToSpawn;
    private List<OdinPair<Point, Integer>> guardiansToSpawn;
    private int nodeStart = -1, nodeEnd = -1, mapid;
    private boolean firstHighest = true;

    public MapleNodes(int mapid) {
        nodes = new LinkedHashMap<>();
        areas = new ArrayList<>();
        platforms = new ArrayList<>();
        skillIds = new ArrayList<>();
        monsterPoints = new ArrayList<>();
        mobsToSpawn = new ArrayList<>();
        guardiansToSpawn = new ArrayList<>();
        this.mapid = mapid;
    }

    public void setNodeStart(final int ns) {
        this.nodeStart = ns;
    }

    public void setNodeEnd(final int ns) {
        this.nodeEnd = ns;
    }

    public static class MapleNodeInfo {

        public int node, key, x, y, attr;
        public List<Integer> edge;

        public MapleNodeInfo(int node, int key, int x, int y, int attr, List<Integer> edge) {
            this.node = node;
            this.key = key;
            this.x = x;
            this.y = y;
            this.attr = attr;
            this.edge = edge;
        }
    }

    public void addNode(MapleNodeInfo mni) {
        this.nodes.put(mni.key, mni);
    }

    public Collection<MapleNodeInfo> getNodes() {
        return new ArrayList<>(nodes.values());
    }

    public MapleNodeInfo getNode(int index) {
        int i = 1;
        for (MapleNodeInfo x : getNodes()) {
            if (i == index) {
                return x;
            }
            i++;
        }
        return null;
    }

    private int getNextNode(MapleNodeInfo mni) {
        if (mni == null) {
            return -1;
        }

        addNode(mni);

        int ret = -1;
        for (int i : mni.edge) {
            if (nodes.get(i) == null) {
                if (ret != -1 && mapid / 100 == 9211204) {
                    if (!firstHighest) {
                        ret = Math.min(ret, i);
                    } else {
                        firstHighest = false;
                        ret = Math.max(ret, i);
                        break;
                    }
                } else {
                    ret = i;
                }
            }
        }
        return ret;
    }

    public void sortNodes() {
        if (nodes.size() <= 0 || nodeStart < 0) {
            return;
        }

        Map<Integer, MapleNodeInfo> unsortedNodes = new HashMap<>(nodes);
        int nodeSize = unsortedNodes.size();
        nodes.clear();
        int nextNode = getNextNode(unsortedNodes.get(nodeStart));

        while (nodes.size() != nodeSize && nextNode >= 0) {
            nextNode = getNextNode(unsortedNodes.get(nextNode));
        }
    }

    public void addMapleArea(final Rectangle rec) {
        areas.add(rec);
    }

    public List<Rectangle> getAreas() {
        return new ArrayList<>(areas);
    }

    public Rectangle getArea(final int index) {
        return getAreas().get(index);
    }

    public static class MaplePlatform {

        public String name;
        public int start, speed, x1, y1, x2, y2, r;
        public List<Integer> SN;

        public MaplePlatform(String name, int start, int speed, int x1, int y1, int x2, int y2, int r, List<Integer> SN) {
            this.name = name;
            this.start = start;
            this.speed = speed;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.r = r;
            this.SN = SN;
        }
    }

    public void addPlatform(MaplePlatform mp) {
        this.platforms.add(mp);
    }

    public List<MaplePlatform> getPlatforms() {
        return new ArrayList<>(platforms);
    }

    public static class MonsterPoint {

        public int x, y, fh, cy, team;

        public MonsterPoint(int x, int y, int fh, int cy, int team) {
            this.x = x;
            this.y = y;
            this.fh = fh;
            this.cy = cy;
            this.team = team;
        }
    }

    public List<MonsterPoint> getMonsterPoints() {
        return monsterPoints;
    }

    public void addMonsterPoint(int x, int y, int fh, int cy, int team) {
        this.monsterPoints.add(new MonsterPoint(x, y, fh, cy, team));
    }

    public void addMobSpawn(int mobId, int spendCP) {
        this.mobsToSpawn.add(new OdinPair<>(mobId, spendCP));
    }

    public List<OdinPair<Integer, Integer>> getMobsToSpawn() {
        return mobsToSpawn;
    }

    public void addGuardianSpawn(Point guardian, int team) {
        this.guardiansToSpawn.add(new OdinPair<>(guardian, team));
    }

    public List<OdinPair<Point, Integer>> getGuardians() {
        return guardiansToSpawn;
    }

    public List<Integer> getSkillIds() {
        return skillIds;
    }

    public void addSkillId(int z) {
        this.skillIds.add(z);
    }
}
