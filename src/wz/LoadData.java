package wz;

import config.DebugConfig;
import config.ServerConfig;
import debug.Debug;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import provider.MapleDataDirectoryEntry;
import provider.MapleDataFileEntry;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import server.ItemMakerFactory;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.quest.MapleQuest;

// バージョンごとにwzの構造が変わるので、変わっていたら読み取り方法もそれに合わせて変更する必要がある
public class LoadData {

    public static void LoadDataFromXML() {
        initDataIDs(); // 職業ID
        initQuests();
        initLife();
        initMaker();
        initItemInformation();
    }

    final private static ArrayList<Integer> list_jobid = new ArrayList<Integer>();
    final private static ArrayList<Integer> list_npcid = new ArrayList<Integer>();

    // 職業ID
    public static boolean IsValidJobID(int jobid) {
        return list_jobid.contains(jobid);
    }

    // NPCID
    public static boolean IsValidNPCID(int jobid) {
        return list_npcid.contains(jobid);
    }

    // test for gm command
    public static ArrayList<Integer> GetJobIDs() {
        return list_jobid;
    }

    private static void initDataIDs() {

        // 職業IDをSkill.wzから取得
        {
            MapleDataProvider wz_skill = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Skill.wz"));
            Pattern pattern = Pattern.compile("(\\d+)\\.img"); //  // example) 100.img
            for (MapleDataFileEntry dir : wz_skill.getRoot().getFiles()) {
                Matcher matcher = pattern.matcher(dir.getName());
                if (matcher.matches()) {
                    int jobid = Integer.parseInt(matcher.group(1));
                    list_jobid.add(jobid);
                }
            }
        }

        // NPCIDをNPC.wzから取得
        {
            MapleDataProvider wz_npc = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/NPC.wz"));
            Pattern pattern = Pattern.compile("0*(\\d+)\\.img"); //  // example) 0002000.img
            for (MapleDataFileEntry dir : wz_npc.getRoot().getFiles()) {
                Matcher matcher = pattern.matcher(dir.getName());
                if (matcher.matches()) {
                    int npcid = Integer.parseInt(matcher.group(1));
                    list_npcid.add(npcid);
                }
            }
        }

        if (DebugConfig.initialize_log) {
            Debug.DebugLog("JobID = " + list_jobid);
            Debug.DebugLog("NPCID = " + list_npcid);
        }
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
}
