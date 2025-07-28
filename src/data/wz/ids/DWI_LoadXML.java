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
package data.wz.ids;

import debug.Debug;
import debug.DebugLoadTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

/**
 *
 * @author Riremito
 */
public class DWI_LoadXML {

    private static final DWI_List ids_skin = new DWI_List(ids -> DWI_LoadXML.LoadSkinXMLs("Character.wz", "0*(\\d+)\\.img", ids));
    private static final DWI_List ids_face = new DWI_List(ids -> DWI_LoadXML.LoadXMLs("Character.wz/Face", "0*(\\d+)\\.img", ids));
    private static final DWI_List ids_hair = new DWI_List(ids -> DWI_LoadXML.LoadXMLs("Character.wz/Hair", "0*(\\d+)\\.img", ids));
    private static final DWI_List ids_job = new DWI_List(ids -> DWI_LoadXML.LoadXMLs("Skill.wz", "(\\d+)\\.img", ids));
    private static final DWI_List ids_map = new DWI_List(ids -> DWI_LoadXML.LoadMapXMLs(ids));
    private static final DWI_List ids_item = new DWI_List(ids -> DWI_LoadXML.LoadItemXMLs(ids));
    private static final DWI_List ids_npc = new DWI_List(ids -> DWI_LoadXML.LoadXMLs("NPC.wz", "0*(\\d+)\\.img", ids));
    private static final DWI_List ids_mob = new DWI_List(ids -> DWI_LoadXML.LoadXMLs("Mob.wz", "0*(\\d+)\\.img", ids));

    public static DWI_List getSkin() {
        return ids_skin;
    }

    public static DWI_List getFace() {
        return ids_face;
    }

    public static DWI_List getHair() {
        return ids_hair;
    }

    public static DWI_List getJob() {
        return ids_job;
    }

    public static DWI_List getMap() {
        return ids_map;
    }

    public static DWI_List getItem() {
        return ids_item;
    }

    public static DWI_List getNpc() {
        return ids_npc;
    }

    public static DWI_List getMob() {
        return ids_mob;
    }

    public static final ArrayList<Integer> potential_unique = new ArrayList<Integer>();
    public static final ArrayList<Integer> potential_legendary = new ArrayList<Integer>();
    public static final ArrayList<Integer> potential_rare = new ArrayList<Integer>();
    public static final ArrayList<Integer> potential_epic = new ArrayList<Integer>();
    public static final ArrayList<Integer> reactorids = new ArrayList<Integer>();

    public static int LoadSkinXMLs(String path, String regex, ArrayList<Integer> list) {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(path);
        if (wz == null) {
            Debug.ErrorLog("wz path: " + path);
            return 0;
        }
        Pattern pattern = Pattern.compile(regex);
        for (MapleDataFileEntry dir : wz.getRoot().getFiles()) {
            Matcher matcher = pattern.matcher(dir.getName());
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1)) % 100;
                if (!list.contains(id)) {
                    list.add(id);
                }
            }
        }
        return list.size();
    }

    public static int LoadItemXMLs(ArrayList<Integer> list) {
        DebugLoadTime dlt = new DebugLoadTime("LoadItemXMLs");
        LoadItemXMLs("Item.wz/Cash/", list);
        LoadItemXMLs("Item.wz/Consume/", list);
        LoadItemXMLs("Item.wz/Etc/", list);
        LoadItemXMLs("Item.wz/Install/", list);
        LoadEquipXMLs("Character.wz/", list);
        LoadXMLs("Item.wz/Pet/", "0*(\\d+)\\.img", list);
        dlt.End();
        return list.size();
    }

    public static int LoadItemXMLs(String path, ArrayList<Integer> list) {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(path);
        if (wz == null) {
            Debug.ErrorLog("wz path: " + path);
            return 0;
        }
        Pattern img_pattern = Pattern.compile("0*(\\d+)\\.img");
        Pattern id_pattern = Pattern.compile("0*(\\d+)");
        for (MapleDataFileEntry dir : wz.getRoot().getFiles()) {
            Matcher img_matcher = img_pattern.matcher(dir.getName());
            if (img_matcher.matches()) {
                for (MapleData data : wz.getData(dir.getName()).getChildren()) {
                    Matcher id_matcher = id_pattern.matcher(data.getName());
                    if (id_matcher.matches()) {
                        int id = Integer.parseInt(data.getName());
                        list.add(id);
                    } else {
                        Debug.DebugLog("invalid item data = " + dir.getName() + " -> " + data.getName());
                    }
                }
            }
        }
        return list.size();
    }

    public static int LoadXMLs(String path, String regex, ArrayList<Integer> list) {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(path);
        if (wz == null) {
            Debug.ErrorLog("wz path: " + path);
            return 0;
        }
        Pattern pattern = Pattern.compile(regex);
        for (MapleDataFileEntry dir : wz.getRoot().getFiles()) {
            Matcher matcher = pattern.matcher(dir.getName());
            if (matcher.matches()) {
                int id = Integer.parseInt(matcher.group(1));
                list.add(id);
            }
        }
        return list.size();
    }

    public static int LoadMapXMLs(ArrayList<Integer> list) {
        DebugLoadTime dlt = new DebugLoadTime("LoadMapXMLs");
        LoadXMLs("Map.wz/Map/Map0", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map1", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map2", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map3", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map4", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map5", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map6", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map7", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map8", "0*(\\d+)\\.img", list);
        LoadXMLs("Map.wz/Map/Map9", "0*(\\d+)\\.img", list);
        dlt.End();
        return list.size();
    }

    public static int LoadEquipXMLs(String path, ArrayList<Integer> list) {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(path);
        if (wz == null) {
            Debug.ErrorLog("wz path: " + path);
            return 0;
        }
        Pattern img_pattern = Pattern.compile("0*(\\d+)\\.img");
        for (MapleDataDirectoryEntry equip_dir : wz.getRoot().getSubdirectories()) {
            for (MapleDataFileEntry dir : equip_dir.getFiles()) {
                Matcher img_matcher = img_pattern.matcher(dir.getName());
                if (img_matcher.matches()) {
                    int id = Integer.parseInt(img_matcher.group(1));
                    // ignore hair
                    if (1000000 <= id) {
                        list.add(id);
                    }
                }
            }
        }
        return list.size();
    }

    public static int LoadTownMaps() {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider("Map.wz/Map");
        Pattern img_pattern = Pattern.compile("0*(\\d+)\\.img");
        for (MapleDataDirectoryEntry map_dir : wz.getRoot().getSubdirectories()) {
            Debug.DebugLog("dir = " + map_dir.getName());
            for (MapleDataFileEntry dir : map_dir.getFiles()) {
                Matcher img_matcher = img_pattern.matcher(dir.getName());
                if (img_matcher.matches()) {
                    int map_id = Integer.parseInt(img_matcher.group(1));
                    MapleDataProvider map_root = MapleDataProviderFactory.getDataProvider("Map.wz/Map/" + map_dir.getName() + "/");
                    MapleData map_data = map_root.getData(dir.getName());
                    if (MapleDataTool.getInt("info/town", map_data) != 0) {
                        int map_id_return = MapleDataTool.getInt("info/returnMap", map_data);
                        if (map_id_return == map_id) {
                            Debug.DebugLog("town mapid = " + map_id);
                        }
                    }
                }
            }
        }
        return 0;
    }

    // test for gm command
    public static ArrayList<Integer> GetJobIDs() {
        return DWI_LoadXML.ids_job.getIds();
    }

}
