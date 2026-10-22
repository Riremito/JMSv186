package odin.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import odin.server.CashItemInfo.CashModInfo;
import tacos.database.query.DQ_CashshopModifiedItems;
import tacos.wz.WzXML;

public class CashItemFactory {

    private final static CashItemFactory instance = new CashItemFactory();
    private boolean initialized = false;
    private final Map<Integer, CashItemInfo> itemStats = new HashMap<>();
    private final Map<Integer, CashModInfo> itemMods = new HashMap<>();

    public static CashItemFactory getInstance() {
        return instance;
    }

    private CashItemFactory() {
    }

    public CashItemInfo getItem(int item_SN) {
        CashItemInfo cii = itemStats.get(item_SN);

        // OK
        if (cii != null) {
            return cii;
        }

        // Load
        CashItemInfo stats = WzXML.ETC.findCommodityBySN(item_SN);
        if (stats != null) {
            itemStats.put(stats.getSN(), stats);
        }
        return stats;
    }

    public int getItemSN(int itemid) {
        for (Entry<Integer, CashItemInfo> ci : itemStats.entrySet()) {
            if (ci.getValue().getId() == itemid) {
                return ci.getValue().getSN();
            }
        }

        // Load
        CashItemInfo stats = WzXML.ETC.findCommodityByItemId(itemid);
        if (stats != null) {
            itemStats.put(stats.getSN(), stats);
            return stats.getSN();
        }

        return 0;
    }
}
