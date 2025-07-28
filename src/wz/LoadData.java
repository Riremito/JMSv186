package wz;

import client.SkillFactory;
import config.property.Property_Java;
import config.ServerConfig;
import debug.Debug;
import debug.DebugLoadTime;
import java.io.File;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
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
