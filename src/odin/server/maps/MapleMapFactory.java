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

import tacos.wz.data.MapWz;
import tacos.debug.DebugLogger;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import odin.server.maps.MapleNodes.MapleNodeInfo;
import odin.server.maps.MapleNodes.MaplePlatform;
import tacos.server.map.MasterMonster;
import odin.provider.IMapleData;
import tacos.unofficial.CustomMap;
import tacos.wz.TacosWzDataTool;

public class MapleMapFactory {

    private Map<Integer, MapleMap> maps = new HashMap<>();
    private static Map<Integer, MapleNodes> mapInfos = new HashMap<>();
    private int channel;

    public MapleMap getMap(int map_id) {
        MapleMap map = maps.get(map_id);
        if (map != null) {
            return map;
        }

        IMapleData mapData = MapWz.get().getImg(map_id);
        if (mapData == null) {
            DebugLogger.ErrorLog("Invalid MapID : " + map_id);
            return null;
        }

        IMapleData link = mapData.getChildByPath("info/link");
        if (link != null) {
            int link_map_id = TacosWzDataTool.getIntPath("info/link", mapData, 0);
            mapData = MapWz.get().getImg(link_map_id);
        }

        map = new MapleMap(map_id, channel);
        // load info.
        map.loadInfo(mapData);
        // load fh.
        map.loadFootHolds(mapData);
        // load portal.
        map.loadPortals(mapData);
        // load life.
        map.loadLife(mapData);
        // load reactor.
        map.loadReactor(mapData);
        // add custom npc.
        CustomMap.addNPCtoMap(map);

        MasterMonster.addAreaBossSpawn(map);
        map.loadMonsterRate(true);
        map.setNodes(loadNodes(map_id, mapData));

        maps.put(map_id, map);
        return map;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    private MapleNodes loadNodes(final int mapid, final IMapleData mapData) {
        MapleNodes nodeInfo = mapInfos.get(mapid);
        if (nodeInfo == null) {
            nodeInfo = new MapleNodes(mapid);
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
                        final MapleNodeInfo mni = new MapleNodeInfo(
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
                        final MaplePlatform mni = new MaplePlatform(
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
            mapInfos.put(mapid, nodeInfo);
        }
        return nodeInfo;
    }
}
