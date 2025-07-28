package wz;

import debug.DebugLoadTime;
import server.MapleItemInformationProvider;

// バージョンごとにwzの構造が変わるので、変わっていたら読み取り方法もそれに合わせて変更する必要がある
public class LoadData {

    public static void LoadDataFromXML() {
        DebugLoadTime dlt = new DebugLoadTime("TEST");
        dlt.End();

        dlt = new DebugLoadTime("MapleItemInformationProvider");
        MapleItemInformationProvider.getInstance().load();
        dlt.End();
    }
}
