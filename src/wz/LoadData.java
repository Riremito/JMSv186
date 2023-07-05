package wz;

import client.SkillFactory;
import config.ServerConfig;
import debug.Debug;
import debug.DebugLoadTime;
import handling.login.LoginInformationProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import provider.MapleData;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.CashItemFactory;
import server.ItemMakerFactory;
import server.MapleCarnivalFactory;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;

// バージョンごとにwzの構造が変わるので、変わっていたら読み取り方法もそれに合わせて変更する必要がある
public class LoadData {

    public static void LoadDataFromXML() {
        // test
        if (ServerConfig.version >= 414) {
            return;
        }

        DebugLoadTime dlt = new DebugLoadTime("initDataIDs");
        initDataIDs(); // 職業ID
        dlt.End();

        initForbiddenName();
        initQuests();
        initLife();
        initMaker();
        initItemInformation();

        // gomi
        MapleLifeFactory.loadQuestCounts();
        ItemMakerFactory.getInstance();
        MapleItemInformationProvider.getInstance().load();
        SkillFactory.getSkill(99999999);
        MapleCarnivalFactory.getInstance().initialize();
        initMapleMapFactory();
    }

    final private static ArrayList<Integer> jobids = new ArrayList<Integer>();
    final private static ArrayList<Integer> skinids = new ArrayList<Integer>();
    final private static ArrayList<Integer> faceids = new ArrayList<Integer>();
    final private static ArrayList<Integer> hairids = new ArrayList<Integer>();
    final private static ArrayList<Integer> mapids = new ArrayList<Integer>();
    final private static ArrayList<Integer> npcids = new ArrayList<Integer>();
    final private static ArrayList<Integer> mobids = new ArrayList<Integer>();
    final private static ArrayList<Integer> reactorids = new ArrayList<Integer>();
    final private static ArrayList<Integer> itemids = new ArrayList<Integer>();
    final private static ArrayList<Integer> skillids = new ArrayList<Integer>();
    final private static ArrayList<Integer> morphids = new ArrayList<Integer>();
    final private static ArrayList<Integer> taimingmobids = new ArrayList<Integer>();

    public static boolean IsValidJobID(int id) {
        return jobids.contains(id);
    }

    public static boolean IsValidSkinID(int id) {
        return skinids.contains(id);
    }

    public static boolean IsValidFaceID(int id) {
        return faceids.contains(id);
    }

    public static boolean IsValidHairID(int id) {
        return hairids.contains(id);
    }

    public static boolean IsValidMapID(int id) {
        return mapids.contains(id);
    }

    public static boolean IsValidNPCID(int id) {
        return npcids.contains(id);
    }

    public static boolean IsValidMobID(int id) {
        return mobids.contains(id);
    }

    public static boolean IsValidReactorID(int id) {
        return reactorids.contains(id);
    }

    // test for gm command
    public static ArrayList<Integer> GetJobIDs() {
        return jobids;
    }

    public static int GetMapIDIndex(int id) {
        return mapids.indexOf(id);
    }

    public static int GetMapIDByIndex(int index) {
        if (index < 0 || mapids.size() <= index) {
            return -1;
        }
        return mapids.get(index);
    }

    public enum DataType {
        SKIN,
        FACE,
        HAIR,
        JOB,
        MAP,
        NPC,
        MOB,
        UNKNOWN;
    }

    public static int GetRandomID(DataType dt) {
        Random rand = new Random();

        switch (dt) {
            case SKIN: {
                return skinids.get(rand.nextInt(skinids.size()));
            }
            case FACE: {
                return faceids.get(rand.nextInt(faceids.size()));
            }
            case HAIR: {
                return hairids.get(rand.nextInt(hairids.size()));
            }
            case JOB: {
                return jobids.get(rand.nextInt(jobids.size()));
            }
            case MAP: {
                return mapids.get(rand.nextInt(mapids.size()));
            }
            case NPC: {
                return npcids.get(rand.nextInt(npcids.size()));
            }
            case MOB: {
                return mobids.get(rand.nextInt(mobids.size()));
            }
            default: {
                break;
            }
        }

        return -1;
    }

    private static void initDataIDs() {
        // 職業ID
        LoadXMLs("Skill.wz", "(\\d+)\\.img", jobids);
        // 肌色, 顔, 髪型
        LoadXMLs("Character.wz", "0*(\\d+)\\.img", skinids);
        LoadXMLs("Character.wz/Face", "0*(\\d+)\\.img", faceids);
        LoadXMLs("Character.wz/Hair", "0*(\\d+)\\.img", hairids);
        // NPC
        LoadXMLs("NPC.wz", "0*(\\d+)\\.img", npcids);
        // Mob
        LoadXMLs("Mob.wz", "0*(\\d+)\\.img", mobids);
        // Map
        LoadXMLs("Map.wz/Map/Map0", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map1", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map2", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map3", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map4", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map5", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map6", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map7", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map8", "0*(\\d+)\\.img", mapids);
        LoadXMLs("Map.wz/Map/Map9", "0*(\\d+)\\.img", mapids);

        Debug.DebugLog("JobIDs = " + jobids.size());
        Debug.DebugLog("SkinIDs = " + skinids.size());
        Debug.DebugLog("FaceIDs = " + faceids.size());
        Debug.DebugLog("HairIDs = " + hairids.size());
        Debug.DebugLog("NPCIDs = " + npcids.size());
        Debug.DebugLog("MobIDs = " + mobids.size());
        Debug.DebugLog("MapIDs = " + mapids.size());
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

    private static void initQuests() {
        MapleQuest.questData = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Quest.wz"));
        MapleQuest.actions = MapleQuest.questData.getData("Act.img");
        MapleQuest.requirements = MapleQuest.questData.getData("Check.img");
        MapleQuest.info = MapleQuest.questData.getData("QuestInfo.img");

        if (ServerConfig.version >= 164) {
            MapleQuest.pinfo = MapleQuest.questData.getData("PQuest.img");
        }
    }

    private static void initLife() {
        MapleLifeFactory.data = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Mob.wz"));
        MapleLifeFactory.stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/String.wz"));
        MapleLifeFactory.etcDataWZ = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Etc.wz"));
        MapleLifeFactory.mobStringData = MapleLifeFactory.stringDataWZ.getData("Mob.img");
        MapleLifeFactory.npcStringData = MapleLifeFactory.stringDataWZ.getData("Npc.img");

        if (ServerConfig.version >= 164) {
            MapleLifeFactory.npclocData = MapleLifeFactory.etcDataWZ.getData("NpcLocation.img");
        }
    }

    private static void initMaker() {
        if (ServerConfig.version >= 164) {
            ItemMakerFactory.info = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Etc.wz")).getData("ItemMake.img");
        }
    }

    private static void initItemInformation() {
        MapleItemInformationProvider.etcData = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Etc.wz"));
        MapleItemInformationProvider.itemData = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Item.wz"));
        MapleItemInformationProvider.equipData = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Character.wz"));
        MapleItemInformationProvider.stringData = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/String.wz"));

        if (ServerConfig.version < 164) {
            MapleItemInformationProvider.cashStringData = MapleItemInformationProvider.stringData.getData("Item.img").getChildByPath("Cash");
            MapleItemInformationProvider.consumeStringData = MapleItemInformationProvider.stringData.getData("Item.img").getChildByPath("Con");
            MapleItemInformationProvider.eqpStringData = MapleItemInformationProvider.stringData.getData("Item.img").getChildByPath("Eqp");
            MapleItemInformationProvider.etcStringData = MapleItemInformationProvider.stringData.getData("Item.img").getChildByPath("Etc");
            MapleItemInformationProvider.insStringData = MapleItemInformationProvider.stringData.getData("Item.img").getChildByPath("Ins");
            MapleItemInformationProvider.petStringData = MapleItemInformationProvider.stringData.getData("Item.img").getChildByPath("Pet");
        } else {
            MapleItemInformationProvider.cashStringData = MapleItemInformationProvider.stringData.getData("Cash.img");

            if (ServerConfig.version < 184) {
                MapleItemInformationProvider.consumeStringData = MapleItemInformationProvider.stringData.getData("Consume.img").getChildByPath("Con");
            } else {
                MapleItemInformationProvider.consumeStringData = MapleItemInformationProvider.stringData.getData("Consume.img");
            }

            MapleItemInformationProvider.eqpStringData = MapleItemInformationProvider.stringData.getData("Eqp.img").getChildByPath("Eqp");
            MapleItemInformationProvider.etcStringData = MapleItemInformationProvider.stringData.getData("Etc.img").getChildByPath("Etc");
            MapleItemInformationProvider.insStringData = MapleItemInformationProvider.stringData.getData("Ins.img");
            MapleItemInformationProvider.petStringData = MapleItemInformationProvider.stringData.getData("Pet.img");
        }
    }

    // login server
    private static void initForbiddenName() {
        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Etc.wz"));

        if (wz == null) {
            Debug.ErrorLog("initForbiddenName");
            return;
        }

        final MapleData nameData = wz.getData("ForbiddenName.img");

        for (final MapleData data : nameData.getChildren()) {
            LoginInformationProvider.ForbiddenName.add(MapleDataTool.getString(data));
        }

    }

    private static void initMapleMapFactory() {
        MapleMapFactory.source = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Map.wz"));

        MapleDataProvider wz = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/String.wz"));

        if (wz == null) {
            Debug.ErrorLog("initMapleMapFactory");
            return;
        }

        MapleMapFactory.nameData = wz.getData("Map.img");
    }

    private static void initCashItemFactory() {
        CashItemFactory.data = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Etc.wz"));
    }
}
