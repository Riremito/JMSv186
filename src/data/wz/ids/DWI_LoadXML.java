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

import config.Content;
import debug.DebugLoadTime;
import debug.DebugLogger;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import odin.provider.MapleData;
import odin.provider.MapleDataDirectoryEntry;
import odin.provider.MapleDataFileEntry;
import odin.provider.MapleDataProvider;
import odin.provider.MapleDataProviderFactory;
import odin.provider.MapleDataTool;

/**
 *
 * @author Riremito
 */
public class DWI_LoadXML {

    private static final DWI_List ids_skin = new DWI_List(ids -> DWI_LoadXML.LoadSkinXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Character" : "Character.wz", "0*(\\d+)\\.img", ids));
    private static final DWI_List ids_face = new DWI_List(ids -> DWI_LoadXML.LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Character/Face" : "Character.wz/Face", "0*(\\d+)\\.img", ids));
    private static final DWI_List ids_hair = new DWI_List(ids -> DWI_LoadXML.LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Character/Hair" : "Character.wz/Hair", "0*(\\d+)\\.img", ids));
    private static final DWI_List ids_job = new DWI_List(ids -> DWI_LoadXML.LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Skill" : "Skill.wz", "(\\d+)\\.img", ids));
    private static final DWI_List ids_map = new DWI_List(ids -> DWI_LoadXML.LoadMapXMLs(ids));
    private static final DWI_List ids_item = new DWI_List(ids -> DWI_LoadXML.LoadItemXMLs(ids));
    private static final DWI_List ids_npc = new DWI_List(ids -> DWI_LoadXML.LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/NPC" : "NPC.wz", "0*(\\d+)\\.img", ids));
    private static final DWI_List ids_mob = new DWI_List(ids -> DWI_LoadXML.LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Mob" : "Mob.wz", "0*(\\d+)\\.img", ids));

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

    public static final ArrayList<Integer> reactorids = new ArrayList<Integer>();

    public static int LoadSkinXMLs(String path, String regex, ArrayList<Integer> list) {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(path);
        if (wz == null) {
            DebugLogger.ErrorLog("wz path: " + path);
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
        LoadItemXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Item/Cash/" : "Item.wz/Cash/", list);
        LoadItemXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Item/Consume/" : "Item.wz/Consume/", list);
        LoadItemXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Item/Etc/" : "Item.wz/Etc/", list);
        LoadItemXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Item/Install/" : "Item.wz/Install/", list);
        LoadEquipXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Character/" : "Character.wz/", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Item/Pet/" : "Item.wz/Pet/", "0*(\\d+)\\.img", list);
        dlt.End();
        return list.size();
    }

    public static int LoadItemXMLs(String path, ArrayList<Integer> list) {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(path);
        if (wz == null) {
            DebugLogger.ErrorLog("wz path: " + path);
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
                        DebugLogger.DebugLog("invalid item data = " + dir.getName() + " -> " + data.getName());
                    }
                }
            }
        }
        return list.size();
    }

    public static int LoadXMLs(String path, String regex, ArrayList<Integer> list) {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(path);
        if (wz == null) {
            DebugLogger.ErrorLog("wz path: " + path);
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
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map0" : "Map.wz/Map/Map0", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map1" : "Map.wz/Map/Map1", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map2" : "Map.wz/Map/Map2", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map3" : "Map.wz/Map/Map3", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map4" : "Map.wz/Map/Map4", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map5" : "Map.wz/Map/Map5", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map6" : "Map.wz/Map/Map6", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map7" : "Map.wz/Map/Map7", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map8" : "Map.wz/Map/Map8", "0*(\\d+)\\.img", list);
        LoadXMLs(Content.Wz_SingleFile.get() ? "Data.wz/Map/Map/Map9" : "Map.wz/Map/Map9", "0*(\\d+)\\.img", list);
        dlt.End();
        return list.size();
    }

    public static int LoadEquipXMLs(String path, ArrayList<Integer> list) {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(path);
        if (wz == null) {
            DebugLogger.ErrorLog("wz path: " + path);
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
            DebugLogger.DebugLog("dir = " + map_dir.getName());
            for (MapleDataFileEntry dir : map_dir.getFiles()) {
                Matcher img_matcher = img_pattern.matcher(dir.getName());
                if (img_matcher.matches()) {
                    int map_id = Integer.parseInt(img_matcher.group(1));
                    MapleDataProvider map_root = MapleDataProviderFactory.getDataProvider("Map.wz/Map/" + map_dir.getName() + "/");
                    MapleData map_data = map_root.getData(dir.getName());
                    if (MapleDataTool.getInt("info/town", map_data) != 0) {
                        int map_id_return = MapleDataTool.getInt("info/returnMap", map_data);
                        if (map_id_return == map_id) {
                            DebugLogger.DebugLog("town mapid = " + map_id);
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
