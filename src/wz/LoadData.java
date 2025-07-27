package wz;

import client.SkillFactory;
import config.property.Property_Java;
import config.ServerConfig;
import data.wz.ids.DWI_List;
import debug.Debug;
import debug.DebugLoadTime;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import provider.MapleData;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemFactory;
import server.ItemMakerFactory;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.maps.MapleMapFactory;

// バージョンごとにwzの構造が変わるので、変わっていたら読み取り方法もそれに合わせて変更する必要がある
public class LoadData {

    public static void LoadDataFromXML() {
        DebugLoadTime dlt = new DebugLoadTime("initLife");
        initLife();
        dlt.End();

        dlt = new DebugLoadTime("initMaker");
        initMaker();
        dlt.End();

        // gomi
        dlt = new DebugLoadTime("MapleLifeFactory");
        MapleLifeFactory.loadQuestCounts();
        dlt.End();

        dlt = new DebugLoadTime("ItemMakerFactory");
        ItemMakerFactory.getInstance();
        dlt.End();

        dlt = new DebugLoadTime("MapleItemInformationProvider");
        MapleItemInformationProvider.getInstance().load();
        dlt.End();

        dlt = new DebugLoadTime("SkillFactory");
        SkillFactory.getSkill(99999999);
        dlt.End();

        dlt = new DebugLoadTime("initMapleMapFactory");
        initMapleMapFactory();
        dlt.End();

        dlt = new DebugLoadTime("initCashItemFactory");
        initCashItemFactory();
        dlt.End();
    }

    final private static DWI_List jobids = new DWI_List((ids) -> LoadXMLs("Skill.wz", "(\\d+)\\.img", ids));
    final private static DWI_List skinids = new DWI_List((ids) -> LoadSkinXMLs("Character.wz", "0*(\\d+)\\.img", ids));
    final private static DWI_List faceids = new DWI_List((ids) -> LoadXMLs("Character.wz/Face", "0*(\\d+)\\.img", ids));
    final private static DWI_List hairids = new DWI_List((ids) -> LoadXMLs("Character.wz/Hair", "0*(\\d+)\\.img", ids));
    final private static DWI_List npcids = new DWI_List((ids) -> LoadXMLs("NPC.wz", "0*(\\d+)\\.img", ids));
    final private static DWI_List mobids = new DWI_List((ids) -> LoadXMLs("Mob.wz", "0*(\\d+)\\.img", ids));
    final private static DWI_List mapids = new DWI_List((ids) -> LoadMapXMLs(ids));
    final private static DWI_List itemids = new DWI_List((ids) -> LoadItemXMLs(ids));

    final private static ArrayList<Integer> reactorids = new ArrayList<Integer>();
    final private static ArrayList<Integer> skillids = new ArrayList<Integer>();
    final private static ArrayList<Integer> morphids = new ArrayList<Integer>();
    final private static ArrayList<Integer> taimingmobids = new ArrayList<Integer>();
    final public static ArrayList<Integer> potential_rare = new ArrayList<Integer>();
    final public static ArrayList<Integer> potential_epic = new ArrayList<Integer>();
    final public static ArrayList<Integer> potential_unique = new ArrayList<Integer>();
    final public static ArrayList<Integer> potential_legendary = new ArrayList<Integer>();

    public static boolean IsValidJobID(int id) {
        return jobids.isValidID(id);
    }

    public static boolean IsValidSkinID(int id) {
        return skinids.isValidID(id);
    }

    public static boolean IsValidFaceID(int id) {
        return faceids.isValidID(id);
    }

    public static boolean IsValidHairID(int id) {
        return hairids.isValidID(id);
    }

    public static boolean IsValidMapID(int id) {
        return mapids.isValidID(id);
    }

    public static boolean IsValidNPCID(int id) {
        return npcids.isValidID(id);
    }

    public static boolean IsValidMobID(int id) {
        return mobids.isValidID(id);
    }

    public static boolean IsValidReactorID(int id) {
        return reactorids.contains(id);
    }

    public static boolean IsValidItemID(int id) {
        return itemids.isValidID(id);
    }

    // test for gm command
    public static ArrayList<Integer> GetJobIDs() {
        return jobids.getIds();
    }

    public static int GetMapIDIndex(int id) {
        return mapids.getIds().indexOf(id);
    }

    public static int GetMapIDByIndex(int index) {
        if (index < 0 || mapids.getIds().size() <= index) {
            return -1;
        }
        return mapids.getIds().get(index);
    }

    public enum DataType {
        SKIN,
        FACE,
        HAIR,
        JOB,
        MAP,
        NPC,
        MOB,
        ITEM,
        UNKNOWN;
    }

    public static int GetRandomID(DataType dt) {
        Random rand = new Random();

        switch (dt) {
            case SKIN: {
                return skinids.getRandom();
            }
            case FACE: {
                return faceids.getRandom();
            }
            case HAIR: {
                return hairids.getRandom();
            }
            case JOB: {
                return jobids.getRandom();
            }
            case MAP: {
                return mapids.getRandom();
            }
            case NPC: {
                return npcids.getRandom();
            }
            case MOB: {
                return mobids.getRandom();
            }
            case ITEM: {
                return itemids.getRandom();
            }
            default: {
                break;
            }
        }

        return -1;
    }

    public static int getRandomPotential(int rank) {
        Random rand = new Random();
        if (rank == 1) {
            return potential_rare.get(rand.nextInt(potential_rare.size()));
        }
        if (rank == 2) {
            return potential_epic.get(rand.nextInt(potential_epic.size()));
        }
        if (rank == 3) {
            return potential_unique.get(rand.nextInt(potential_unique.size()));
        }
        if (rank == 4) {
            return potential_legendary.get(rand.nextInt(potential_legendary.size()));
        }
        return 0;
    }

    private static int LoadMapXMLs(ArrayList<Integer> list) {
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

    private static int LoadItemXMLs(ArrayList<Integer> list) {
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

    private static int LoadTownMaps() {
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

    private static int LoadItemXMLs(String path, ArrayList<Integer> list) {
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

    private static int LoadEquipXMLs(String path, ArrayList<Integer> list) {
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

    private static int LoadSkinXMLs(String path, String regex, ArrayList<Integer> list) {
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

    private static int LoadXMLs(String path, String regex, ArrayList<Integer> list) {
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

    private static void initLife() {
        MapleLifeFactory.data = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/Mob.wz"));
        MapleLifeFactory.stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/String.wz"));
        MapleLifeFactory.etcDataWZ = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/Etc.wz"));
        MapleLifeFactory.mobStringData = MapleLifeFactory.stringDataWZ.getData("Mob.img");
        MapleLifeFactory.npcStringData = MapleLifeFactory.stringDataWZ.getData("Npc.img");

        if (ServerConfig.JMS164orLater()) {
            MapleLifeFactory.npclocData = MapleLifeFactory.etcDataWZ.getData("NpcLocation.img");
        }
    }

    private static void initMaker() {
        if (ServerConfig.JMS164orLater()) {
            ItemMakerFactory.info = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/Etc.wz")).getData("ItemMake.img");
        }
    }

    private static void initMapleMapFactory() {
        MapleMapFactory.source = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/Map.wz"));

        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/String.wz"));

        if (wz == null) {
            Debug.ErrorLog("initMapleMapFactory");
            return;
        }

        MapleMapFactory.nameData = wz.getData("Map.img");
    }

    private static void initCashItemFactory() {
        CashItemFactory.data = MapleDataProviderFactory.getDataProvider(new File(Property_Java.getDir_WzXml() + "/Etc.wz"));
        CashItemFactory.commodity = CashItemFactory.data.getData("Commodity.img");
    }
}
