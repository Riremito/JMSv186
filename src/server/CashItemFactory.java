package server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import database.DatabaseConnection;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataTool;
import server.CashItemInfo.CashModInfo;

public class CashItemFactory {

    private final static CashItemFactory instance = new CashItemFactory();
    private final static int[] bestItems = new int[]{10002819, 50100010, 50200001, 10002147, 60000073};
    private boolean initialized = false;
    private final Map<Integer, CashItemInfo> itemStats = new HashMap<Integer, CashItemInfo>();
    private final Map<Integer, List<CashItemInfo>> itemPackage = new HashMap<Integer, List<CashItemInfo>>();
    private final Map<Integer, CashModInfo> itemMods = new HashMap<Integer, CashModInfo>();
    public static MapleDataProvider data = null;
    public static MapleData commodity = null;

    public static final CashItemFactory getInstance() {
        return instance;
    }

    protected CashItemFactory() {
    }

    public void initialize() {
        //System.out.println("Loading CashItemFactory :::");
        if (data != null) {
            final List<Integer> itemids = new ArrayList<Integer>();
            for (MapleData field : data.getData("Commodity.img").getChildren()) {
                final int itemId = MapleDataTool.getIntConvert("ItemId", field, 0);
                final int SN = MapleDataTool.getIntConvert("SN", field, 0);

                final CashItemInfo stats = new CashItemInfo(itemId,
                        MapleDataTool.getIntConvert("Count", field, 1),
                        MapleDataTool.getIntConvert("Price", field, 0), SN,
                        MapleDataTool.getIntConvert("Period", field, 0),
                        MapleDataTool.getIntConvert("Gender", field, 2),
                        MapleDataTool.getIntConvert("OnSale", field, 0) > 0);

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
        }
        initialized = true;
    }

    public final CashItemInfo getItem(int item_SN) {
        final CashItemInfo cii = itemStats.get(Integer.valueOf(item_SN));

        // OK
        if (cii != null) {
            return cii;
        }

        // Load
        for (MapleData field : commodity.getChildren()) {
            int SN = MapleDataTool.getIntConvert("SN", field, 0);

            if (SN <= 0 || item_SN != SN) {
                continue;
            }

            int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);

            CashItemInfo stats = new CashItemInfo(ItemId,
                    MapleDataTool.getIntConvert("Count", field, 1),
                    MapleDataTool.getIntConvert("Price", field, 0),
                    SN,
                    MapleDataTool.getIntConvert("Period", field, 0),
                    MapleDataTool.getIntConvert("Gender", field, 2),
                    MapleDataTool.getIntConvert("OnSale", field, 0) > 0);

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
        for (MapleData field : commodity.getChildren()) {
            int ItemId = MapleDataTool.getIntConvert("ItemId", field, 0);
            if (ItemId != itemid) {
                continue;
            }

            int SN = MapleDataTool.getIntConvert("SN", field, 0);
            CashItemInfo stats = new CashItemInfo(ItemId,
                    MapleDataTool.getIntConvert("Count", field, 1),
                    MapleDataTool.getIntConvert("Price", field, 0),
                    SN,
                    MapleDataTool.getIntConvert("Period", field, 0),
                    MapleDataTool.getIntConvert("Gender", field, 2),
                    MapleDataTool.getIntConvert("OnSale", field, 0) > 0);

            itemStats.put(SN, stats);
            return SN;
        }

        return 0;
    }

    public final List<CashItemInfo> getPackageItems(int itemId) {
        if (itemPackage.get(itemId) != null) {
            return itemPackage.get(itemId);
        }
        final List<CashItemInfo> packageItems = new ArrayList<CashItemInfo>();

        final MapleData b = data.getData("CashPackage.img");
        if (b == null || b.getChildByPath(itemId + "/SN") == null) {
            return null;
        }
        for (MapleData d : b.getChildByPath(itemId + "/SN").getChildren()) {
            packageItems.add(itemStats.get(Integer.valueOf(MapleDataTool.getIntConvert(d))));
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
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM cashshop_modified_items WHERE serial = ?");
                ps.setInt(1, sn);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ret = new CashModInfo(sn, rs.getInt("discount_price"), rs.getInt("mark"), rs.getInt("showup") > 0, rs.getInt("itemid"), rs.getInt("priority"), rs.getInt("package") > 0, rs.getInt("period"), rs.getInt("gender"), rs.getInt("count"), rs.getInt("meso"), rs.getInt("unk_1"), rs.getInt("unk_2"), rs.getInt("unk_3"), rs.getInt("extra_flags"));
                    itemMods.put(sn, ret);
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
}
