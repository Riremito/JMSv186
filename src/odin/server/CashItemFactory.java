package odin.server;

import java.util.List;
import java.util.ArrayList;
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

    public static final CashItemFactory getInstance() {
        return instance;
    }

    protected CashItemFactory() {
    }

    public final CashItemInfo getItem(int item_SN) {
        final CashItemInfo cii = itemStats.get(item_SN);

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

    public final int getItemSN(int itemid) {
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

    public final CashModInfo getModInfo(int sn) {
        CashModInfo ret = itemMods.get(sn);
        if (ret == null) {
            if (initialized) {
                return null;
            }
            ret = DQ_CashshopModifiedItems.load(sn);
            if (ret != null) {
                itemMods.put(sn, ret);
            }
        }
        return ret;
    }
}
