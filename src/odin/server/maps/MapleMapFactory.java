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

import tacos.config.DeveloperMode;
import tacos.wz.data.MapWz;
import tacos.wz.data.ReactorWz;
import tacos.wz.data.StringWz;
import tacos.wz.ids.DWI_Block;
import tacos.debug.DebugLogger;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import odin.server.life.AbstractLoadedMapleLife;
import odin.server.life.MapleLifeFactory;
import odin.server.life.MapleMonster;
import odin.server.life.MapleNPC;
import odin.server.maps.MapleNodes.MapleNodeInfo;
import odin.server.maps.MapleNodes.MaplePlatform;
import odin.tools.StringUtil;
import tacos.server.map.MasterMonster;
import odin.provider.IMapleData;
import tacos.unofficial.CustomMap;
import tacos.wz.TacosWzDataTool;

public class MapleMapFactory {

    private Map<Integer, MapleMap> maps = new HashMap<>();
    private static Map<Integer, MapleNodes> mapInfos = new HashMap<>();
    private int channel;

    public MapleMap getMap(int map_id) {
        Integer omapid = map_id;
        MapleMap map = maps.get(omapid);
        if (map != null) {
            return map;
        }

        IMapleData mapData;
        try {
            mapData = MapWz.get().getData(getMapName(map_id));
        } catch (Exception e) {
            // 存在しないMapIDが指定された場合は指定MapIDへ強制移動する
            DebugLogger.ErrorLog("Invalid MapID = " + map_id);
            map_id = DeveloperMode.DM_ERROR_MAP_ID.getInt();
            omapid = map_id;
            mapData = MapWz.get().getData(getMapName(map_id));
        }

        IMapleData link = mapData.getChildByPath("info/link");
        if (link != null) {
            mapData = MapWz.get().getData(getMapName(TacosWzDataTool.getIntPath("info/link", mapData, 0)));
        }

        float monsterRate = TacosWzDataTool.getFloatPath("info/mobRate", mapData, 0.0f);
        map = new MapleMap(map_id, channel, TacosWzDataTool.getIntPath("info/returnMap", mapData, 0), monsterRate);

        // load portal.
        map.loadPortals(mapData);
        // load fh.
        map.loadFootHolds(mapData);

        int bossid = -1;
        String msg = null;
        if (mapData.getChildByPath("info/timeMob") != null) {
            bossid = TacosWzDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
            msg = TacosWzDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
        }

        // load life data (npc, monsters)
        String type;
        AbstractLoadedMapleLife myLife;

        for (IMapleData life : mapData.getChildByPath("life")) {
            type = TacosWzDataTool.getString(life.getChildByPath("type"));
            myLife = loadLife(life, TacosWzDataTool.getString(life.getChildByPath("id")), type);
            if (myLife instanceof MapleMonster) {
                final MapleMonster mob = (MapleMonster) myLife;
                // hide mob
                if (!DWI_Block.checkMob(myLife.getId())) {
                    map.addMonsterSpawn(mob,
                            TacosWzDataTool.getIntPath("mobTime", life, 0),
                            (byte) TacosWzDataTool.getIntPath("team", life, -1),
                            mob.getId() == bossid ? msg : null);
                }

            } else if (myLife != null) {
                // hide npc
                if (!DWI_Block.checkNpc(myLife.getId())) {
                    map.addMapObject(myLife);
                }
            }
        }

        // add custom npc.
        CustomMap.addNPCtoMap(map);

        MasterMonster.addAreaBossSpawn(map);
        map.setCreateMobInterval(TacosWzDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
        map.loadMonsterRate(true);
        map.setNodes(loadNodes(map_id, mapData));

        //load reactor data
        String id;
        if (mapData.getChildByPath("reactor") != null) {
            for (IMapleData reactor : mapData.getChildByPath("reactor")) {
                id = TacosWzDataTool.getString(reactor.getChildByPath("id"));
                if (id != null) {
                    map.spawnReactor(loadReactor(reactor, id, (byte) TacosWzDataTool.getInt(reactor.getChildByPath("f"), 0)));
                }
            }
        }

        try {
            map.setMapName(TacosWzDataTool.getStringPath("mapName", StringWz.get().getMap().getChildByPath(getMapStringName(omapid)), ""));
            map.setStreetName(TacosWzDataTool.getStringPath("streetName", StringWz.get().getMap().getChildByPath(getMapStringName(omapid)), ""));
        } catch (Exception e) {
            map.setMapName("");
            map.setStreetName("");
        }
        // load info.
        map.loadMapData(mapData);
        maps.put(omapid, map);
        return map;
    }

    private AbstractLoadedMapleLife loadLife(IMapleData life, String id, String type) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(Integer.parseInt(id), type);
        if (myLife == null) {
            return null;
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

        if (TacosWzDataTool.getIntPath("hide", life, 0) == 1 && myLife instanceof MapleNPC) {
            myLife.setHide(true);
//		} else if (hide > 1) {
//			System.err.println("Hide > 1 ("+ hide +")");
        }
        return myLife;
    }

    private MapleReactor loadReactor(final IMapleData reactor, final String id, final byte FacingDirection) {
        final MapleReactorStats stats = ReactorWz.get().getReactor(Integer.parseInt(id));
        final MapleReactor myReactor = new MapleReactor(stats, Integer.parseInt(id));

        stats.setFacingDirection(FacingDirection);
        myReactor.setPosition(new Point(TacosWzDataTool.getInt(reactor.getChildByPath("x")), TacosWzDataTool.getInt(reactor.getChildByPath("y"))));
        myReactor.setDelay(TacosWzDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
        myReactor.setState((byte) 0);
        myReactor.setName(TacosWzDataTool.getString(reactor.getChildByPath("name"), ""));

        return myReactor;
    }

    public static String getMapName(int mapid) {
        String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9);
        StringBuilder builder = new StringBuilder("Map/Map");
        builder.append(mapid / 100000000);
        builder.append("/");
        builder.append(mapName);
        builder.append(".img");

        mapName = builder.toString();
        return mapName;
    }

    // ?_? TODO : FIX!
    private String getMapStringName(int mapid) {
        StringBuilder builder = new StringBuilder();
        if (mapid < 100000000) {
            builder.append("maple");
        } else if ((mapid >= 100000000 && mapid < 200000000) || mapid / 100000 == 5540) {
            builder.append("victoria");
        } else if (mapid >= 200000000 && mapid < 300000000) {
            builder.append("ossyria");
        } else if (mapid >= 300000000 && mapid < 400000000) {
            builder.append("elin");
        } else if (mapid >= 500000000 && mapid < 510000000) {
            builder.append("thai");
        } else if (mapid >= 540000000 && mapid < 600000000) {
            builder.append("SG");
        } else if (mapid >= 600000000 && mapid < 620000000) {
            builder.append("MasteriaGL");
        } else if ((mapid >= 670000000 && mapid < 677000000) || (mapid >= 678000000 && mapid < 682000000)) {
            builder.append("global");
        } else if (mapid >= 677000000 && mapid < 678000000) {
            builder.append("Episode1GL");
        } else if (mapid >= 682000000 && mapid < 683000000) {
            builder.append("HalloweenGL");
        } else if (mapid >= 683000000 && mapid < 684000000) {
            builder.append("event");
        } else if (mapid >= 684000000 && mapid < 685000000) {
            builder.append("event_5th");
        } else if (mapid >= 700000000 && mapid < 700000300) {
            builder.append("wedding");
        } else if (mapid >= 701000000 && mapid < 701020000) {
            builder.append("china");
        } else if (mapid >= 800000000 && mapid < 900000000) {
            builder.append("jp");
        } else {
            builder.append("etc");
        }
        builder.append("/");
        builder.append(mapid);

        return builder.toString();
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
