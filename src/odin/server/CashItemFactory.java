package odin.server;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import odin.server.CashItemInfo.CashModInfo;
import tacos.database.query.DQ_CashshopModifiedItems;
import odin.provider.IMapleData;
import tacos.wz.WzDataTool;
import tacos.wz.WzXML;

public class CashItemFactory {

    private final static CashItemFactory instance = new CashItemFactory();
    private final static int[] bestItems = new int[]{10002819, 50100010, 50200001, 10002147, 60000073};
    private boolean initialized = false;
    private final Map<Integer, CashItemInfo> itemStats = new HashMap<Integer, CashItemInfo>();
    private final Map<Integer, List<CashItemInfo>> itemPackage = new HashMap<Integer, List<CashItemInfo>>();
    private final Map<Integer, CashModInfo> itemMods = new HashMap<Integer, CashModInfo>();

    public static final CashItemFactory getInstance() {
        return instance;
    }

    protected CashItemFactory() {
    }

    public void initialize() {
        final List<Integer> itemids = new ArrayList<>();
        for (IMapleData field : WzXML.ETC.getCommodity().getChildren()) {
            final int itemId = WzDataTool.getIntPath("ItemId", field, 0);
            final int SN = WzDataTool.getIntPath("SN", field, 0);

            final CashItemInfo stats = new CashItemInfo(itemId,
                    WzDataTool.getIntPath("Count", field, 1),
                    WzDataTool.getIntPath("Price", field, 0), SN,
                    WzDataTool.getIntPath("Period", field, 0),
                    WzDataTool.getIntPath("Gender", field, 2),
                    WzDataTool.getIntPath("OnSale", field, 0) > 0);

            if (SN > 0) {
                itemStats.put(SN, stats);
            }

            if (itemId > 0) {
                itemids.add(itemId);
            }
        }
        for (int i : itemids) {
            getPackageItems(i);
        }
        for (int i : itemStats.keySet()) {
            getModInfo(i);
            getItem(i); //init the modinfo's citem
        }
        initialized = true;
    }

    public final CashItemInfo getItem(int item_SN) {
        final CashItemInfo cii = itemStats.get(item_SN);

        // OK
        if (cii != null) {
            return cii;
        }

        // Load
        for (IMapleData field : WzXML.ETC.getCommodity().getChildren()) {
            int SN = WzDataTool.getIntPath("SN", field, 0);

            if (SN <= 0 || item_SN != SN) {
                continue;
            }

            int ItemId = WzDataTool.getIntPath("ItemId", field, 0);

            CashItemInfo stats = new CashItemInfo(ItemId,
                    WzDataTool.getIntPath("Count", field, 1),
                    WzDataTool.getIntPath("Price", field, 0),
                    SN,
                    WzDataTool.getIntPath("Period", field, 0),
                    WzDataTool.getIntPath("Gender", field, 2),
                    WzDataTool.getIntPath("OnSale", field, 0) > 0);

            itemStats.put(SN, stats);
            return stats;
        }

        return null;
    }

    public final int getItemSN(int itemid) {
        for (Entry<Integer, CashItemInfo> ci : itemStats.entrySet()) {
            if (ci.getValue().getId() == itemid) {
                return ci.getValue().getSN();
            }
        }

        // Load
        for (IMapleData field : WzXML.ETC.getCommodity().getChildren()) {
            int ItemId = WzDataTool.getIntPath("ItemId", field, 0);
            if (ItemId != itemid) {
                continue;
            }

            int SN = WzDataTool.getIntPath("SN", field, 0);
            CashItemInfo stats = new CashItemInfo(ItemId,
                    WzDataTool.getIntPath("Count", field, 1),
                    WzDataTool.getIntPath("Price", field, 0),
                    SN,
                    WzDataTool.getIntPath("Period", field, 0),
                    WzDataTool.getIntPath("Gender", field, 2),
                    WzDataTool.getIntPath("OnSale", field, 0) > 0);

            itemStats.put(SN, stats);
            return SN;
        }

        return 0;
    }

    public final List<CashItemInfo> getPackageItems(int itemId) {
        if (itemPackage.get(itemId) != null) {
            return itemPackage.get(itemId);
        }
        final List<CashItemInfo> packageItems = new ArrayList<>();

        if (WzXML.ETC.getCashPackage() == null || WzXML.ETC.getCashPackage().getChildByPath(itemId + "/SN") == null) {
            return null;
        }
        for (IMapleData d : WzXML.ETC.getCashPackage().getChildByPath(itemId + "/SN").getChildren()) {
            packageItems.add(itemStats.get(WzDataTool.getInt(d, 0)));
        }
        itemPackage.put(itemId, packageItems);
        return packageItems;
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
