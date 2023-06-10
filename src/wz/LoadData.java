package wz;

import config.ServerConfig;
import java.io.File;
import provider.MapleDataProviderFactory;
import server.ItemMakerFactory;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.quest.MapleQuest;

// バージョンごとにwzの構造が変わるので、変わっていたら読み取り方法もそれに合わせて変更する必要がある
public class LoadData {

    public static void LoadDataFromXML() {
        initQuests();
        initLife();
        initMaker();
        initItemInformation();
    }

    public static void initQuests() {
        MapleQuest.questData = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Quest.wz"));
        MapleQuest.actions = MapleQuest.questData.getData("Act.img");
        MapleQuest.requirements = MapleQuest.questData.getData("Check.img");
        MapleQuest.info = MapleQuest.questData.getData("QuestInfo.img");

        if (ServerConfig.version >= 164) {
            MapleQuest.pinfo = MapleQuest.questData.getData("PQuest.img");
        }
    }

    public static void initLife() {
        MapleLifeFactory.data = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Mob.wz"));
        MapleLifeFactory.stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/String.wz"));
        MapleLifeFactory.etcDataWZ = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Etc.wz"));
        MapleLifeFactory.mobStringData = MapleLifeFactory.stringDataWZ.getData("Mob.img");
        MapleLifeFactory.npcStringData = MapleLifeFactory.stringDataWZ.getData("Npc.img");

        if (ServerConfig.version >= 164) {
            MapleLifeFactory.npclocData = MapleLifeFactory.etcDataWZ.getData("NpcLocation.img");
        }
    }

    public static void initMaker() {
        if (ServerConfig.version >= 164) {
            ItemMakerFactory.info = MapleDataProviderFactory.getDataProvider(new File(ServerConfig.wz_path + "/Etc.wz")).getData("ItemMake.img");
        }
    }

    public static void initItemInformation() {
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
