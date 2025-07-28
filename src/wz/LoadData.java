package wz;

import client.SkillFactory;
import config.property.Property_Java;
import debug.Debug;
import debug.DebugLoadTime;
import java.io.File;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import server.CashItemFactory;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import server.maps.MapleMapFactory;

// バージョンごとにwzの構造が変わるので、変わっていたら読み取り方法もそれに合わせて変更する必要がある
public class LoadData {

    public static void LoadDataFromXML() {
        DebugLoadTime dlt = new DebugLoadTime("TEST");
        dlt.End();

        // gomi
        dlt = new DebugLoadTime("MapleLifeFactory");
        MapleLifeFactory.loadQuestCounts();
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
