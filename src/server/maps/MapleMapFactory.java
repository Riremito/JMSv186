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
package server.maps;

import config.property.Property_Java;
import config.Region;
import config.ServerConfig;
import config.Version;
import debug.Debug;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import server.PortalFactory;
import server.life.AbstractLoadedMapleLife;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.maps.MapleNodes.MapleNodeInfo;
import server.maps.MapleNodes.MaplePlatform;
import tools.StringUtil;
import wz.LoadData;

public class MapleMapFactory {

    public static MapleDataProvider source;
    public static MapleData nameData;
    private final Map<Integer, MapleMap> maps = new HashMap<Integer, MapleMap>();
    private final Map<Integer, MapleMap> instanceMap = new HashMap<Integer, MapleMap>();
    private static final Map<Integer, MapleNodes> mapInfos = new HashMap<Integer, MapleNodes>();
    private final ReentrantLock lock = new ReentrantLock(true);
    private int channel;

    public final MapleMap getMap(final int mapid) {
        return getMap(mapid, true, true, true);
    }

    //backwards-compatible
    public final MapleMap getMap(final int mapid, final boolean respawns, final boolean npcs) {
        return getMap(mapid, respawns, npcs, true);
    }

    public final MapleMap getMap(int mapid, final boolean respawns, final boolean npcs, final boolean reactors) {
        Integer omapid = Integer.valueOf(mapid);
        MapleMap map = maps.get(omapid);
        if (map == null) {
            lock.lock();
            try {
                map = maps.get(omapid);
                if (map != null) {
                    return map;
                }

                MapleData mapData;
                try {
                    mapData = source.getData(getMapName(mapid));
                } catch (Exception e) {
                    // 存在しないMapIDが指定された場合は指定MapIDへ強制移動する
                    Debug.ErrorLog("Invalid MapID = " + mapid);
                    mapid = ServerConfig.error_mapid;
                    omapid = Integer.valueOf(mapid);
                    mapData = source.getData(getMapName(mapid));
                }
                //MapleData mapData = source.getData(getMapName(mapid));

                MapleData link = mapData.getChildByPath("info/link");
                if (link != null) {
                    mapData = source.getData(getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
                }

                float monsterRate = 0;
                if (respawns) {
                    MapleData mobRate = mapData.getChildByPath("info/mobRate");
                    if (mobRate != null) {
                        monsterRate = ((Float) mobRate.getData()).floatValue();
                    }
                }
                map = new MapleMap(mapid, channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);

                PortalFactory portalFactory = new PortalFactory();
                for (MapleData portal : mapData.getChildByPath("portal")) {
                    map.addPortal(portalFactory.makePortal(map, MapleDataTool.getInt(portal.getChildByPath("pt")), portal));
                }
                List<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
                Point lBound = new Point();
                Point uBound = new Point();
                MapleFoothold fh;

                for (MapleData footRoot : mapData.getChildByPath("foothold")) {
                    for (MapleData footCat : footRoot) {
                        for (MapleData footHold : footCat) {
                            fh = new MapleFoothold(new Point(
                                    MapleDataTool.getInt(footHold.getChildByPath("x1")), MapleDataTool.getInt(footHold.getChildByPath("y1"))), new Point(
                                    MapleDataTool.getInt(footHold.getChildByPath("x2")), MapleDataTool.getInt(footHold.getChildByPath("y2"))), Integer.parseInt(footHold.getName()));
                            fh.setPrev((short) MapleDataTool.getInt(footHold.getChildByPath("prev")));
                            fh.setNext((short) MapleDataTool.getInt(footHold.getChildByPath("next")));

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
                map.setFootholds(fTree);

                int bossid = -1;
                String msg = null;
                if (mapData.getChildByPath("info/timeMob") != null) {
                    bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
                    msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
                }

                // load life data (npc, monsters)
                String type;
                AbstractLoadedMapleLife myLife;

                for (MapleData life : mapData.getChildByPath("life")) {
                    type = MapleDataTool.getString(life.getChildByPath("type"));
                    if (npcs || !type.equals("n")) {
                        myLife = loadLife(life, MapleDataTool.getString(life.getChildByPath("id")), type);

                        if (myLife instanceof MapleMonster) {
                            final MapleMonster mob = (MapleMonster) myLife;

                            map.addMonsterSpawn(mob,
                                    MapleDataTool.getInt("mobTime", life, 0),
                                    (byte) MapleDataTool.getInt("team", life, -1),
                                    mob.getId() == bossid ? msg : null);

                        } else if (myLife != null) {
                            if (!Debug.CheckNPCBlock(myLife.getId(), MapleLifeFactory.getNPC(myLife.getId()).getName())) {
                                map.addMapObject(myLife);
                            }
                        }
                    }
                }

                // 設定ファイルに定義されたNPCを設置
                if ((Region.IsJMS() && Version.getVersion() == 186)) {
                    Path file = Paths.get(Property_Java.getDir_Scripts() + "map/" + mapid + ".txt");
                    try {
                        if (!Files.notExists(file)) {
                            List<String> text;
                            text = Files.readAllLines(file); // UTF-8
                            for (int i = 0; i < text.size(); i++) {
                                String[] npc_data = text.get(i).split(",");
                                if (npc_data.length == 4) {

                                    int npc_id = Integer.parseInt(npc_data[0]);
                                    int npc_x = Integer.parseInt(npc_data[1]);
                                    int npc_y = Integer.parseInt(npc_data[2]);
                                    int npc_fh = Integer.parseInt(npc_data[3]);

                                    MapleNPC npc = MapleLifeFactory.getNPC(npc_id);
                                    if (npc != null && !npc.getName().equals("MISSINGNO")) {
                                        npc.setPosition(new Point(npc_x, npc_y));
                                        npc.setCy(npc_y);
                                        npc.setRx0(npc_x + 50);
                                        npc.setRx1(npc_x - 50);
                                        npc.setFh(npc_fh);
                                        npc.setCustom(true);
                                        map.addMapObject(npc);
                                        //Debug.DebugLog("Spawn NPC, NPC = " + npc.getName() + " (" + npc_id + "), Map = " + MapleDataTool.getString("streetName", nameData.getChildByPath(getMapStringName(omapid))) + " - " + MapleDataTool.getString("mapName", nameData.getChildByPath(getMapStringName(omapid))) + " (" + mapid + ")");
                                    }
                                } else {
                                    Debug.ErrorLog("spawn npc format error: " + mapid);
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MapleMapFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                addAreaBossSpawn(map);
                map.setCreateMobInterval((short) MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
                map.loadMonsterRate(true);
                map.setNodes(loadNodes(mapid, mapData));

                //load reactor data
                String id;
                if (reactors && mapData.getChildByPath("reactor") != null) {
                    for (MapleData reactor : mapData.getChildByPath("reactor")) {
                        id = MapleDataTool.getString(reactor.getChildByPath("id"));
                        if (id != null) {
                            map.spawnReactor(loadReactor(reactor, id, (byte) MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                        }
                    }
                }

                try {
                    map.setMapName(MapleDataTool.getString("mapName", nameData.getChildByPath(getMapStringName(omapid)), ""));
                    map.setStreetName(MapleDataTool.getString("streetName", nameData.getChildByPath(getMapStringName(omapid)), ""));
                } catch (Exception e) {
                    map.setMapName("");
                    map.setStreetName("");
                }
                map.setClock(mapData.getChildByPath("clock") != null); //clock was changed in wz to have x,y,width,height
                map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
                map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
                map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
                map.setPersonalShop(MapleDataTool.getInt(mapData.getChildByPath("info/personalShop"), 0) > 0);
                map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
                map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
                map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
                map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
                map.setForcedReturnMap(MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
                map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
                map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
                map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
                map.setUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
                map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1));
                map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
                map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));

                maps.put(omapid, map);
            } finally {
                lock.unlock();
            }
        }
        return map;
    }

    public MapleMap getInstanceMap(final int instanceid) {
        return instanceMap.get(instanceid);
    }

    public void removeInstanceMap(final int instanceid) {
        if (isInstanceMapLoaded(instanceid)) {
            getInstanceMap(instanceid).checkStates("");
            instanceMap.remove(instanceid);
        }
    }

    public void removeMap(final int instanceid) {
        if (isMapLoaded(instanceid)) {
            getMap(instanceid).checkStates("");
            maps.remove(instanceid);
        }
    }

    public MapleMap CreateInstanceMap(int mapid, boolean respawns, boolean npcs, boolean reactors, int instanceid) {
        if (isInstanceMapLoaded(instanceid)) {
            return getInstanceMap(instanceid);
        }
        MapleData mapData = source.getData(getMapName(mapid));
        MapleData link = mapData.getChildByPath("info/link");
        if (link != null) {
            mapData = source.getData(getMapName(MapleDataTool.getIntConvert("info/link", mapData)));
        }

        float monsterRate = 0;
        if (respawns) {
            MapleData mobRate = mapData.getChildByPath("info/mobRate");
            if (mobRate != null) {
                monsterRate = ((Float) mobRate.getData()).floatValue();
            }
        }
        MapleMap map = new MapleMap(mapid, channel, MapleDataTool.getInt("info/returnMap", mapData), monsterRate);

        PortalFactory portalFactory = new PortalFactory();
        for (MapleData portal : mapData.getChildByPath("portal")) {
            map.addPortal(portalFactory.makePortal(map, MapleDataTool.getInt(portal.getChildByPath("pt")), portal));
        }
        List<MapleFoothold> allFootholds = new LinkedList<MapleFoothold>();
        Point lBound = new Point();
        Point uBound = new Point();
        for (MapleData footRoot : mapData.getChildByPath("foothold")) {
            for (MapleData footCat : footRoot) {
                for (MapleData footHold : footCat) {
                    MapleFoothold fh = new MapleFoothold(new Point(
                            MapleDataTool.getInt(footHold.getChildByPath("x1")), MapleDataTool.getInt(footHold.getChildByPath("y1"))), new Point(
                            MapleDataTool.getInt(footHold.getChildByPath("x2")), MapleDataTool.getInt(footHold.getChildByPath("y2"))), Integer.parseInt(footHold.getName()));
                    fh.setPrev((short) MapleDataTool.getInt(footHold.getChildByPath("prev")));
                    fh.setNext((short) MapleDataTool.getInt(footHold.getChildByPath("next")));

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
        for (MapleFoothold fh : allFootholds) {
            fTree.insert(fh);
        }
        map.setFootholds(fTree);
        int bossid = -1;
        String msg = null;
        if (mapData.getChildByPath("info/timeMob") != null) {
            bossid = MapleDataTool.getInt(mapData.getChildByPath("info/timeMob/id"), 0);
            msg = MapleDataTool.getString(mapData.getChildByPath("info/timeMob/message"), null);
        }

        // load life data (npc, monsters)
        String type;
        AbstractLoadedMapleLife myLife;

        for (MapleData life : mapData.getChildByPath("life")) {
            type = MapleDataTool.getString(life.getChildByPath("type"));
            if (npcs || !type.equals("n")) {
                myLife = loadLife(life, MapleDataTool.getString(life.getChildByPath("id")), type);

                if (myLife instanceof MapleMonster) {
                    final MapleMonster mob = (MapleMonster) myLife;

                    map.addMonsterSpawn(mob,
                            MapleDataTool.getInt("mobTime", life, 0),
                            (byte) MapleDataTool.getInt("team", life, -1),
                            mob.getId() == bossid ? msg : null);

                } else {
                    map.addMapObject(myLife);
                }
            }
        }
        addAreaBossSpawn(map);
        map.setCreateMobInterval((short) MapleDataTool.getInt(mapData.getChildByPath("info/createMobInterval"), 9000));
        map.loadMonsterRate(true);
        map.setNodes(loadNodes(mapid, mapData));

        //load reactor data
        String id;
        if (reactors && mapData.getChildByPath("reactor") != null) {
            for (MapleData reactor : mapData.getChildByPath("reactor")) {
                id = MapleDataTool.getString(reactor.getChildByPath("id"));
                if (id != null) {
                    map.spawnReactor(loadReactor(reactor, id, (byte) MapleDataTool.getInt(reactor.getChildByPath("f"), 0)));
                }
            }
        }
        try {
            map.setMapName(MapleDataTool.getString("mapName", nameData.getChildByPath(getMapStringName(mapid)), ""));
            map.setStreetName(MapleDataTool.getString("streetName", nameData.getChildByPath(getMapStringName(mapid)), ""));
        } catch (Exception e) {
            map.setMapName("");
            map.setStreetName("");
        }
        map.setClock(MapleDataTool.getInt(mapData.getChildByPath("info/clock"), 0) > 0);
        map.setEverlast(MapleDataTool.getInt(mapData.getChildByPath("info/everlast"), 0) > 0);
        map.setTown(MapleDataTool.getInt(mapData.getChildByPath("info/town"), 0) > 0);
        map.setSoaring(MapleDataTool.getInt(mapData.getChildByPath("info/needSkillForFly"), 0) > 0);
        map.setForceMove(MapleDataTool.getInt(mapData.getChildByPath("info/lvForceMove"), 0));
        map.setHPDec(MapleDataTool.getInt(mapData.getChildByPath("info/decHP"), 0));
        map.setHPDecInterval(MapleDataTool.getInt(mapData.getChildByPath("info/decHPInterval"), 10000));
        map.setHPDecProtect(MapleDataTool.getInt(mapData.getChildByPath("info/protectItem"), 0));
        map.setForcedReturnMap(MapleDataTool.getInt(mapData.getChildByPath("info/forcedReturn"), 999999999));
        map.setTimeLimit(MapleDataTool.getInt(mapData.getChildByPath("info/timeLimit"), -1));
        map.setFieldLimit(MapleDataTool.getInt(mapData.getChildByPath("info/fieldLimit"), 0));
        map.setFirstUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onFirstUserEnter"), ""));
        map.setUserEnter(MapleDataTool.getString(mapData.getChildByPath("info/onUserEnter"), ""));
        map.setRecoveryRate(MapleDataTool.getFloat(mapData.getChildByPath("info/recovery"), 1));
        map.setFixedMob(MapleDataTool.getInt(mapData.getChildByPath("info/fixedMobCapacity"), 0));
        map.setConsumeItemCoolTime(MapleDataTool.getInt(mapData.getChildByPath("info/consumeItemCoolTime"), 0));

        instanceMap.put(instanceid, map);
        return map;
    }

    public int getLoadedMaps() {
        return maps.size();
    }

    public boolean isMapLoaded(int mapId) {
        return maps.containsKey(mapId);
    }

    public boolean isInstanceMapLoaded(int instanceid) {
        return instanceMap.containsKey(instanceid);
    }

    public void clearLoadedMap() {
        maps.clear();
    }

    public Collection<MapleMap> getAllMaps() {
        return maps.values();
    }

    public Collection<MapleMap> getAllInstanceMaps() {
        return instanceMap.values();
    }

    private AbstractLoadedMapleLife loadLife(MapleData life, String id, String type) {
        AbstractLoadedMapleLife myLife = MapleLifeFactory.getLife(Integer.parseInt(id), type);
        if (myLife == null) {
            return null;
        }
        myLife.setCy(MapleDataTool.getInt(life.getChildByPath("cy")));
        MapleData dF = life.getChildByPath("f");
        if (dF != null) {
            myLife.setF(MapleDataTool.getInt(dF));
        }
        myLife.setFh(MapleDataTool.getInt(life.getChildByPath("fh")));
        myLife.setRx0(MapleDataTool.getInt(life.getChildByPath("rx0")));
        myLife.setRx1(MapleDataTool.getInt(life.getChildByPath("rx1")));
        myLife.setPosition(new Point(MapleDataTool.getInt(life.getChildByPath("x")), MapleDataTool.getInt(life.getChildByPath("y"))));

        if (MapleDataTool.getInt("hide", life, 0) == 1 && myLife instanceof MapleNPC) {
            myLife.setHide(true);
//		} else if (hide > 1) {
//			System.err.println("Hide > 1 ("+ hide +")");
        }
        return myLife;
    }

    private final MapleReactor loadReactor(final MapleData reactor, final String id, final byte FacingDirection) {
        final MapleReactorStats stats = MapleReactorFactory.getReactor(Integer.parseInt(id));
        final MapleReactor myReactor = new MapleReactor(stats, Integer.parseInt(id));

        stats.setFacingDirection(FacingDirection);
        myReactor.setPosition(new Point(MapleDataTool.getInt(reactor.getChildByPath("x")), MapleDataTool.getInt(reactor.getChildByPath("y"))));
        myReactor.setDelay(MapleDataTool.getInt(reactor.getChildByPath("reactorTime")) * 1000);
        myReactor.setState((byte) 0);
        myReactor.setName(MapleDataTool.getString(reactor.getChildByPath("name"), ""));

        return myReactor;
    }

    private String getMapName(int mapid) {
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

    private void addAreaBossSpawn(final MapleMap map) {
        int monsterid;
        int mobtime;
        String msg;
        Point pos1, pos2, pos3;

        switch (map.getId()) {
            // マノ
            case 104000400:
                mobtime = 2700;
                monsterid = 2220000;
                // OK
                msg = "涼しい気運が濃く立ち込めながらマノが現れました。";
                pos1 = new Point(439, 185);
                pos2 = new Point(301, -85);
                pos3 = new Point(107, -355);
                break;
            // スタンピ
            case 101030404:
                mobtime = 2700;
                monsterid = 3220000;
                // OK
                msg = "岩山を響く足音とともにスタンピが現れました。";
                pos1 = new Point(867, 1282);
                pos2 = new Point(810, 1570);
                pos3 = new Point(838, 2197);
                break;
            // キンクラン
            case 110040000:
                mobtime = 1200;
                monsterid = 5220001;
                // OK
                msg = "砂浜に怪しいキンクランが現れました。";
                pos1 = new Point(-355, 179);
                pos2 = new Point(-1283, -113);
                pos3 = new Point(-571, -593);
                break;
            // タイルン
            case 250010304:
                mobtime = 2100;
                monsterid = 7220000;
                // メッセージ探し中
                //msg = "Tae Roon appeared with a loud growl.";
                // 適当
                msg = "唸り声と共にタイルンが現れました。";
                pos1 = new Point(-210, 33);
                pos2 = new Point(-234, 393);
                pos3 = new Point(-654, 33);
                break;
            // エリジャー
            case 200010300:
                mobtime = 1200;
                monsterid = 8220000;
                // OK
                msg = "黒い旋風を巻き起こしながらエリジャーが現れました。";
                pos1 = new Point(665, 83);
                pos2 = new Point(672, -217);
                pos3 = new Point(-123, -217);
                break;
            // 神仙妖怪
            case 250010503:
                mobtime = 1800;
                monsterid = 7220002;
                // OK
                msg = "気持ち悪い猫の鳴き声が聞えます。";
                pos1 = new Point(-303, 543);
                pos2 = new Point(227, 543);
                pos3 = new Point(719, 543);
                break;
            // おキツネ様
            case 222010310:
                mobtime = 2700;
                monsterid = 7220001;
                // OK
                msg = "月の光が薄くなり、長い狐の鳴き声とともにおキツネ様の気運が感じられます。";
                pos1 = new Point(-169, -147);
                pos2 = new Point(-517, 93);
                pos3 = new Point(247, 93);
                break;
            // ダイル, マップ2つ
            case 107000300:
                mobtime = 1800;
                monsterid = 6220000;
                //msg = "The huge crocodile Dale has come out from the swamp.";
                // 適当
                msg = "沼から巨大ワニのダイルが出てきました。";
                pos1 = new Point(710, 118);
                pos2 = new Point(95, 119);
                pos3 = new Point(-535, 120);
                break;
            // パウスト
            case 100040105:
                mobtime = 1800;
                monsterid = 5220002;
                //msg = "The blue fog became darker when Faust appeared.";
                // msg = "パウストが出ました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "青い霧が暗くなりパウストが現れました。";
                pos1 = new Point(1000, 278);
                pos2 = new Point(557, 278);
                pos3 = new Point(95, 278);
                break;
            case 100040106:
                mobtime = 1800;
                monsterid = 5220002;
                //msg = "The blue fog became darker when Faust appeared.";
                //msg = "パウストが出ました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "青い霧が暗くなりパウストが現れました。";
                pos1 = new Point(1000, 278);
                pos2 = new Point(557, 278);
                pos3 = new Point(95, 278);
                break;
            // タイマー, マップ3つ
            case 220050100:
                mobtime = 1500;
                monsterid = 5220003;
                //msg = "Click clock! Timer has appeared with an irregular clock sound.";
                //msg = "タイマーが出ました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "不規則な針音と共にタイマーが現れました。";
                pos1 = new Point(-467, 1032);
                pos2 = new Point(532, 1032);
                pos3 = new Point(-47, 1032);
                break;
            // ジェノ
            case 221040301:
                mobtime = 2400;
                monsterid = 6220001;
                //msg = "Jeno has appeared with a heavy sound of machinery.";
                //msg = "ジェノが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "騒音と共にジェノが現れました。";
                pos1 = new Point(-4134, 416);
                pos2 = new Point(-4283, 776);
                pos3 = new Point(-3292, 776);
                break;
            // レヴィアタン
            case 240040401:
                mobtime = 7200;
                monsterid = 8220003;
                //msg = "Leviathan has appeared with a cold wind from over the gorge.";
                //msg = "レヴィアタンが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "渓谷から冷風と共にレビィアタンが現れました。";
                pos1 = new Point(-15, 2481);
                pos2 = new Point(127, 1634);
                pos3 = new Point(159, 1142);
                break;
            // デウ
            case 260010201:
                mobtime = 3600;
                monsterid = 3220001;
                //msg = "Dewu slowly appeared out of the sand dust.";
                //msg = "デウが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "砂塵からゆっくりとデウが出てきました。";
                pos1 = new Point(-215, 275);
                pos2 = new Point(298, 275);
                pos3 = new Point(592, 275);
                break;
            // キメラ
            case 261030000:
                mobtime = 2700;
                monsterid = 8220002;
                //msg = "Chimera has appeared out of the darkness of the underground with a glitter in her eyes.";
                //msg = "キメラが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "地下の暗闇から目を光らせながらキメラが現れました。";
                pos1 = new Point(-1094, -405);
                pos2 = new Point(-772, -116);
                pos3 = new Point(-108, 181);
                break;
            // セルフ
            case 230020100:
                mobtime = 2700;
                monsterid = 4220000;
                //msg = "A strange shell has appeared from a grove of seaweed.";
                //msg = "セルフが現れました。";
                // 日本語のテキストがおかしいので適当に翻訳
                msg = "海草の塔から怪しいセルフが現れました。";
                pos1 = new Point(-291, -20);
                pos2 = new Point(-272, -500);
                pos3 = new Point(-462, 640);
                break;
            default:
                return;
        }

        if (!LoadData.IsValidMobID(monsterid)) {
            Debug.ErrorLog("Invalid Mob ID = " + monsterid);
            return;
        }

        map.addAreaMonsterSpawn(MapleLifeFactory.getMonster(monsterid), pos1, pos2, pos3, mobtime, msg);
    }

    private MapleNodes loadNodes(final int mapid, final MapleData mapData) {
        MapleNodes nodeInfo = mapInfos.get(mapid);
        if (nodeInfo == null) {
            nodeInfo = new MapleNodes(mapid);
            if (mapData.getChildByPath("nodeInfo") != null) {
                for (MapleData node : mapData.getChildByPath("nodeInfo")) {
                    try {
                        if (node.getName().equals("start")) {
                            nodeInfo.setNodeStart(MapleDataTool.getInt(node, 0));
                            continue;
                        } else if (node.getName().equals("end")) {
                            nodeInfo.setNodeEnd(MapleDataTool.getInt(node, 0));
                            continue;
                        }
                        List<Integer> edges = new ArrayList<Integer>();
                        if (node.getChildByPath("edge") != null) {
                            for (MapleData edge : node.getChildByPath("edge")) {
                                edges.add(MapleDataTool.getInt(edge, -1));
                            }
                        }
                        final MapleNodeInfo mni = new MapleNodeInfo(
                                Integer.parseInt(node.getName()),
                                MapleDataTool.getIntConvert("key", node, 0),
                                MapleDataTool.getIntConvert("x", node, 0),
                                MapleDataTool.getIntConvert("y", node, 0),
                                MapleDataTool.getIntConvert("attr", node, 0), edges);
                        nodeInfo.addNode(mni);
                    } catch (NumberFormatException e) {
                    } //start, end, edgeInfo = we dont need it
                }
                nodeInfo.sortNodes();
            }
            for (int i = 1; i <= 7; i++) {
                if (mapData.getChildByPath(String.valueOf(i)) != null && mapData.getChildByPath(i + "/obj") != null) {
                    for (MapleData node : mapData.getChildByPath(i + "/obj")) {
                        int sn_count = MapleDataTool.getIntConvert("SN_count", node, 0);
                        String name = MapleDataTool.getString("name", node, "");
                        int speed = MapleDataTool.getIntConvert("speed", node, 0);
                        if (sn_count <= 0 || speed <= 0 || name.equals("")) {
                            continue;
                        }
                        final List<Integer> SN = new ArrayList<Integer>();
                        for (int x = 0; x < sn_count; x++) {
                            SN.add(MapleDataTool.getIntConvert("SN" + x, node, 0));
                        }
                        final MaplePlatform mni = new MaplePlatform(
                                name, MapleDataTool.getIntConvert("start", node, 2), speed,
                                MapleDataTool.getIntConvert("x1", node, 0),
                                MapleDataTool.getIntConvert("y1", node, 0),
                                MapleDataTool.getIntConvert("x2", node, 0),
                                MapleDataTool.getIntConvert("y2", node, 0),
                                MapleDataTool.getIntConvert("r", node, 0), SN);
                        nodeInfo.addPlatform(mni);
                    }
                }
            }
            // load areas (EG PQ platforms)
            if (mapData.getChildByPath("area") != null) {
                int x1, y1, x2, y2;
                Rectangle mapArea;
                for (MapleData area : mapData.getChildByPath("area")) {
                    x1 = MapleDataTool.getInt(area.getChildByPath("x1"));
                    y1 = MapleDataTool.getInt(area.getChildByPath("y1"));
                    x2 = MapleDataTool.getInt(area.getChildByPath("x2"));
                    y2 = MapleDataTool.getInt(area.getChildByPath("y2"));
                    mapArea = new Rectangle(x1, y1, (x2 - x1), (y2 - y1));
                    nodeInfo.addMapleArea(mapArea);
                }
            }
            if (mapData.getChildByPath("monsterCarnival") != null) {
                final MapleData mc = mapData.getChildByPath("monsterCarnival");
                if (mc.getChildByPath("mobGenPos") != null) {
                    for (MapleData area : mc.getChildByPath("mobGenPos")) {
                        nodeInfo.addMonsterPoint(MapleDataTool.getInt(area.getChildByPath("x")),
                                MapleDataTool.getInt(area.getChildByPath("y")),
                                MapleDataTool.getInt(area.getChildByPath("fh")),
                                MapleDataTool.getInt(area.getChildByPath("cy")),
                                MapleDataTool.getInt("team", area, -1));
                    }
                }
                if (mc.getChildByPath("mob") != null) {
                    for (MapleData area : mc.getChildByPath("mob")) {
                        nodeInfo.addMobSpawn(MapleDataTool.getInt(area.getChildByPath("id")), MapleDataTool.getInt(area.getChildByPath("spendCP")));
                    }
                }
                if (mc.getChildByPath("guardianGenPos") != null) {
                    for (MapleData area : mc.getChildByPath("guardianGenPos")) {
                        nodeInfo.addGuardianSpawn(new Point(MapleDataTool.getInt(area.getChildByPath("x")), MapleDataTool.getInt(area.getChildByPath("y"))), MapleDataTool.getInt("team", area, -1));
                    }
                }
                if (mc.getChildByPath("skill") != null) {
                    for (MapleData area : mc.getChildByPath("skill")) {
                        nodeInfo.addSkillId(MapleDataTool.getInt(area));
                    }
                }
            }
            mapInfos.put(mapid, nodeInfo);
        }
        return nodeInfo;
    }
}
