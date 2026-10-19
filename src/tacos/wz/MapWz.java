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
package tacos.wz;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import tacos.config.Content;
import tacos.constants.TacosConstants;

/**
 *
 * @author Riremito
 */
public class MapWz extends WzXML {

    public MapWz() {
        super(Content.Wz_SingleFile.get() ? "Data.wz/Map" : "Map.wz");
    }

    public MapleData getImg(int map_id) {
        String target_img_path = String.format("Map/Map%d/%09d.img", (map_id / 100000000), map_id);
        return getData(target_img_path);
    }

    // info
    @Getter
    public class MapInfo {

        private boolean clock;
        private int link;
        private int returnMap;
        private int createMobInterval;
        private Float mobRate;
        private int everlast;
        private int town;
        private int needSkillForFly;
        private int personalShop;
        private int lvForceMove;
        private long decHP;
        private int decHPInterval;
        private int protectItem;
        private int forcedReturn;
        private int timeLimit;
        private int fieldLimit;
        private String onFirstUserEnter;
        private String onUserEnter;
        private Float recovery;
        private int fixedMobCapacity;
        private int consumeItemCoolTime;
        private int timeMobId;
        private String timeMobMessage;
    }

    // foothold
    @Getter
    public class FootholdInfo {

        private int id;
        private int x1;
        private int y1;
        private int x2;
        private int y2;
        private int prev;
        private int next;
    }

    // portal
    @Getter
    public class PortalInfo {

        private int pt;
        private String pn;
        private String tn;
        private int tm;
        private int x;
        private int y;
        private String script;
    }

    // life
    @Getter
    public class LifeInfo {

        private int nodeId;
        private String type;
        private int cy;
        private int f;
        private int fh;
        private int hide;
        private int id;
        private int mobTime;
        private int rx0;
        private int rx1;
        private int x;
        private int y;
    }

    // reactor
    @Getter
    public class ReactorInfo {

        private int nodeId;
        private int f;
        private int id;
        private String name;
        private int reactorTime;
        private int x;
        private int y;
    }

    // nodeInfo
    @Getter
    public class NodeInfo {

        private int id;
        private int key;
        private int x;
        private int y;
        private int attr;
        private final List<Integer> edge = new ArrayList<>();
    }

    // 1-7/obj
    @Getter
    public class PlatformInfo {

        private String name;
        private int snCount;
        private int start;
        private int speed;
        private int x1;
        private int y1;
        private int x2;
        private int y2;
        private int r;
        private final List<Integer> sn = new ArrayList<>();
    }

    // area
    @Getter
    public class AreaInfo {

        private int x1;
        private int y1;
        private int x2;
        private int y2;
    }

    // monsterCarnival/mobGenPos, guardianGenPos
    @Getter
    public class GenPosInfo {

        private int x;
        private int y;
        private int fh;
        private int cy;
        private int team;
    }

    // monsterCarnival/mob
    @Getter
    public class CarnivalMobInfo {

        private int id;
        private int spendCP;
    }

    @Getter
    public class MapImgInfo {

        private MapInfo info;
        private final List<FootholdInfo> footholds = new ArrayList<>();
        private final List<PortalInfo> portals = new ArrayList<>();
        private final List<LifeInfo> lifes = new ArrayList<>();
        private boolean hasReactor;
        private final List<ReactorInfo> reactors = new ArrayList<>();
        private boolean hasNodeInfo;
        private Integer nodeStart;
        private Integer nodeEnd;
        private final List<NodeInfo> nodes = new ArrayList<>();
        private final List<PlatformInfo> platforms = new ArrayList<>();
        private final List<AreaInfo> areas = new ArrayList<>();
        private final List<GenPosInfo> carnivalMobGenPos = new ArrayList<>();
        private final List<CarnivalMobInfo> carnivalMobs = new ArrayList<>();
        private final List<GenPosInfo> carnivalGuardianGenPos = new ArrayList<>();
        private final List<Integer> carnivalSkills = new ArrayList<>();
    }

    public MapInfo getMapInfo(int map_id) {
        MapleData md_map = getImg(map_id);
        if (md_map == null) {
            return null;
        }
        return parseMapInfo(md_map);
    }

    public MapImgInfo getMapImgInfo(int map_id) {
        MapleData md_map = getImg(map_id);
        if (md_map == null) {
            return null;
        }

        MapImgInfo ret = new MapImgInfo();
        ret.info = parseMapInfo(md_map);

        for (MapleData md_foothold_root : md_map.getChildByPath("foothold")) {
            for (MapleData md_foothold_cat : md_foothold_root) {
                for (MapleData md_foothold : md_foothold_cat) {
                    FootholdInfo fh = new FootholdInfo();
                    fh.id = Integer.parseInt(md_foothold.getName());
                    fh.x1 = md_foothold.getInt("x1");
                    fh.y1 = md_foothold.getInt("y1");
                    fh.x2 = md_foothold.getInt("x2");
                    fh.y2 = md_foothold.getInt("y2");
                    fh.prev = md_foothold.getInt("prev");
                    fh.next = md_foothold.getInt("next");
                    ret.footholds.add(fh);
                }
            }
        }

        for (MapleData md_portal : md_map.getChildByPath("portal")) {
            PortalInfo portal = new PortalInfo();
            portal.pt = md_portal.getInt("pt");
            portal.pn = md_portal.getString("pn");
            portal.tn = md_portal.getString("tn");
            portal.tm = md_portal.getInt("tm");
            portal.x = md_portal.getInt("x");
            portal.y = md_portal.getInt("y");
            portal.script = md_portal.getString("script");
            ret.portals.add(portal);
        }

        for (MapleData md_life : md_map.getChildByPath("life")) {
            LifeInfo life = new LifeInfo();
            life.nodeId = Integer.parseInt(md_life.getName());
            life.type = md_life.getString("type");
            life.cy = md_life.getInt("cy");
            life.f = md_life.getInt("f");
            life.fh = md_life.getInt("fh");
            life.hide = md_life.getInt("hide");
            life.id = md_life.getInt("id");
            life.mobTime = md_life.getInt("mobTime");
            life.rx0 = md_life.getInt("rx0");
            life.rx1 = md_life.getInt("rx1");
            life.x = md_life.getInt("x");
            life.y = md_life.getInt("y");
            ret.lifes.add(life);
        }

        MapleData md_reactors = md_map.getChildByPath("reactor");
        if (md_reactors != null) {
            ret.hasReactor = true;
            for (MapleData md_reactor : md_reactors.getChildren()) {
                ReactorInfo reactor = new ReactorInfo();
                reactor.nodeId = Integer.parseInt(md_reactor.getName());
                reactor.f = md_reactor.getInt("f");
                reactor.id = md_reactor.getInt("id");
                reactor.name = md_reactor.getString("name");
                reactor.reactorTime = md_reactor.getInt("reactorTime");
                reactor.x = md_reactor.getInt("x");
                reactor.y = md_reactor.getInt("y");
                ret.reactors.add(reactor);
            }
        }

        MapleData md_node_info = md_map.getChildByPath("nodeInfo");
        if (md_node_info != null) {
            ret.hasNodeInfo = true;
            for (MapleData md_node : md_node_info) {
                try {
                    if (md_node.getName().equals("start")) {
                        ret.nodeStart = md_node_info.getInt(md_node.getName());
                        continue;
                    } else if (md_node.getName().equals("end")) {
                        ret.nodeEnd = md_node_info.getInt(md_node.getName());
                        continue;
                    }
                    NodeInfo node = new NodeInfo();
                    MapleData md_edge = md_node.getChildByPath("edge");
                    if (md_edge != null) {
                        for (MapleData edge : md_edge) {
                            node.edge.add(md_edge.getInt(edge.getName(), -1));
                        }
                    }
                    node.id = Integer.parseInt(md_node.getName());
                    node.key = md_node.getInt("key");
                    node.x = md_node.getInt("x");
                    node.y = md_node.getInt("y");
                    node.attr = md_node.getInt("attr");
                    ret.nodes.add(node);
                } catch (NumberFormatException e) {
                } //start, end, edgeInfo = we dont need it
            }
        }

        for (int i = 1; i <= 7; i++) {
            if (md_map.getChildByPath(String.valueOf(i)) != null && md_map.getChildByPath(i + "/obj") != null) {
                for (MapleData md_obj : md_map.getChildByPath(i + "/obj")) {
                    PlatformInfo platform = new PlatformInfo();
                    platform.snCount = md_obj.getInt("SN_count");
                    platform.name = md_obj.getString("name");
                    platform.speed = md_obj.getInt("speed");
                    platform.start = md_obj.getInt("start", 2);
                    platform.x1 = md_obj.getInt("x1");
                    platform.y1 = md_obj.getInt("y1");
                    platform.x2 = md_obj.getInt("x2");
                    platform.y2 = md_obj.getInt("y2");
                    platform.r = md_obj.getInt("r");
                    for (int x = 0; x < platform.snCount; x++) {
                        platform.sn.add(md_obj.getInt("SN" + x));
                    }
                    ret.platforms.add(platform);
                }
            }
        }

        MapleData md_areas = md_map.getChildByPath("area");
        if (md_areas != null) {
            for (MapleData md_area : md_areas) {
                AreaInfo area = new AreaInfo();
                area.x1 = md_area.getInt("x1");
                area.y1 = md_area.getInt("y1");
                area.x2 = md_area.getInt("x2");
                area.y2 = md_area.getInt("y2");
                ret.areas.add(area);
            }
        }

        MapleData md_mc = md_map.getChildByPath("monsterCarnival");
        if (md_mc != null) {
            MapleData md_mob_gen_pos = md_mc.getChildByPath("mobGenPos");
            if (md_mob_gen_pos != null) {
                for (MapleData md_pos : md_mob_gen_pos) {
                    GenPosInfo pos = new GenPosInfo();
                    pos.x = md_pos.getInt("x");
                    pos.y = md_pos.getInt("y");
                    pos.fh = md_pos.getInt("fh");
                    pos.cy = md_pos.getInt("cy");
                    pos.team = md_pos.getInt("team", -1);
                    ret.carnivalMobGenPos.add(pos);
                }
            }
            MapleData md_mob = md_mc.getChildByPath("mob");
            if (md_mob != null) {
                for (MapleData md_mob_entry : md_mob) {
                    CarnivalMobInfo mob = new CarnivalMobInfo();
                    mob.id = md_mob_entry.getInt("id");
                    mob.spendCP = md_mob_entry.getInt("spendCP");
                    ret.carnivalMobs.add(mob);
                }
            }
            MapleData md_guardian_gen_pos = md_mc.getChildByPath("guardianGenPos");
            if (md_guardian_gen_pos != null) {
                for (MapleData md_pos : md_guardian_gen_pos) {
                    GenPosInfo pos = new GenPosInfo();
                    pos.x = md_pos.getInt("x");
                    pos.y = md_pos.getInt("y");
                    pos.team = md_pos.getInt("team", -1);
                    ret.carnivalGuardianGenPos.add(pos);
                }
            }
            MapleData md_skill = md_mc.getChildByPath("skill");
            if (md_skill != null) {
                for (MapleData md_skill_id : md_skill) {
                    ret.carnivalSkills.add(md_skill.getInt(md_skill_id.getName()));
                }
            }
        }

        return ret;
    }

    private MapInfo parseMapInfo(MapleData md_map) {
        MapInfo ret = new MapInfo();
        ret.clock = md_map.getChildByPath("clock") != null;
        ret.link = md_map.getInt("info/link", -1);
        ret.returnMap = md_map.getInt("info/returnMap");
        ret.createMobInterval = md_map.getInt("info/createMobInterval", 7000);
        ret.mobRate = md_map.getFloat("info/mobRate");
        ret.everlast = md_map.getInt("info/everlast");
        ret.town = md_map.getInt("info/town");
        ret.needSkillForFly = md_map.getInt("info/needSkillForFly");
        ret.personalShop = md_map.getInt("info/personalShop");
        ret.lvForceMove = md_map.getInt("info/lvForceMove");
        ret.decHP = md_map.getLong("info/decHP", 0L);
        ret.decHPInterval = md_map.getInt("info/decHPInterval", 10000);
        ret.protectItem = md_map.getInt("info/protectItem");
        ret.forcedReturn = md_map.getInt("info/forcedReturn", TacosConstants.DEFAULT_FORCED_RETURN_MAP_ID);
        ret.timeLimit = md_map.getInt("info/timeLimit", -1);
        ret.fieldLimit = md_map.getInt("info/fieldLimit");
        ret.onFirstUserEnter = md_map.getString("info/onFirstUserEnter");
        ret.onUserEnter = md_map.getString("info/onUserEnter");
        ret.recovery = md_map.getFloat("info/recovery", 1.0f);
        ret.fixedMobCapacity = md_map.getInt("info/fixedMobCapacity");
        ret.consumeItemCoolTime = md_map.getInt("info/consumeItemCoolTime");
        ret.timeMobId = md_map.getInt("info/timeMob/id");
        ret.timeMobMessage = md_map.getString("info/timeMob/message", null);
        return ret;
    }
}
