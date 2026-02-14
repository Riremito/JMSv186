package odin.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import odin.constants.GameConstants;
import odin.client.inventory.ItemLoader;
import odin.client.inventory.IItem;
import odin.client.MapleClient;
import odin.client.inventory.MapleInventoryType;
import tacos.database.DatabaseConnection;
import tacos.database.DatabaseException;
import java.util.EnumMap;
import tacos.packet.response.ResCTrunkDlg;
import tacos.odin.OdinPair;
import tacos.packet.ops.OpsDBCHAR;
import tacos.packet.ops.OpsTrunk;

public class MapleStorage {

    private int id;
    private int accountId;
    private List<IItem> items;
    private int meso;
    private byte slots;
    private boolean changed = false;
    private Map<MapleInventoryType, List<IItem>> typeItems = new EnumMap<>(MapleInventoryType.class);
    private int npc_id = 1012003;

    private MapleStorage(int id, byte slots, int meso, int accountId) {
        this.id = id;
        this.slots = slots;
        this.items = new LinkedList<>();
        this.meso = meso;
        this.accountId = accountId;
    }

    public static int create(int id) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO storages (accountid, slots, meso) VALUES (?, ?, ?)", DatabaseConnection.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, id);
            ps.setInt(2, 4);
            ps.setInt(3, 0);
            ps.executeUpdate();
            int storageid;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    storageid = rs.getInt(1);
                    ps.close();
                    rs.close();
                    return storageid;
                }
            }
        }
        throw new DatabaseException("Inserting char failed.");
    }

    public static MapleStorage loadStorage(int id) {
        MapleStorage ret = null;
        int storeId;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM storages WHERE accountid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                storeId = rs.getInt("storageid");
                ret = new MapleStorage(storeId, rs.getByte("slots"), rs.getInt("meso"), id);
                rs.close();
                ps.close();

                for (OdinPair<IItem, MapleInventoryType> mit : ItemLoader.STORAGE.loadItems(false, id).values()) {
                    ret.items.add(mit.getLeft());
                }
            } else {
                storeId = create(id);
                ret = new MapleStorage(storeId, (byte) 4, 0, id);
                rs.close();
                ps.close();
            }
        } catch (SQLException ex) {
            System.err.println("Error loading storage" + ex);
        }
        return ret;
    }

    public void saveToDB() {
        if (!changed) {
            return;
        }
        try {
            Connection con = DatabaseConnection.getConnection();

            try (PreparedStatement ps = con.prepareStatement("UPDATE storages SET slots = ?, meso = ? WHERE storageid = ?")) {
                ps.setInt(1, slots);
                ps.setInt(2, meso);
                ps.setInt(3, id);
                ps.executeUpdate();
            }

            List<OdinPair<IItem, MapleInventoryType>> listing = new ArrayList<>();
            for (final IItem item : items) {
                listing.add(new OdinPair<>(item, GameConstants.getInventoryType(item.getItemId())));
            }
            ItemLoader.STORAGE.saveItems(listing, accountId);
        } catch (SQLException ex) {
            System.err.println("Error saving storage" + ex);
        }
    }

    public IItem takeOut(int type, int slot) {
        List<IItem> fitems = typeItems.get(MapleInventoryType.getByType((byte) type));
        if (fitems == null || fitems.size() < slot) {
            return null;
        }
        IItem item_get = fitems.get(slot);
        if (item_get == null) {
            return null;
        }

        changed = true;
        items.remove(item_get);
        fitems.remove(item_get);
        return item_get;
    }

    public void store(MapleInventoryType type, IItem item) {
        changed = true;
        items.add(item);
        typeItems.get(type).add(item);
    }

    public List<IItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<IItem> filterItems(MapleInventoryType type) {
        List<IItem> ret = new LinkedList<>();

        for (IItem item : items) {
            if (GameConstants.getInventoryType(item.getItemId()) == type) {
                ret.add(item);
            }
        }
        return ret;
    }

    public void sendStorage(MapleClient client, int npc_id) {
        // sort by inventorytype to avoid confusion
        Collections.sort(items, new Comparator<IItem>() {

            public int compare(IItem o1, IItem o2) {
                if (GameConstants.getInventoryType(o1.getItemId()).getType() < GameConstants.getInventoryType(o2.getItemId()).getType()) {
                    return -1;
                } else if (GameConstants.getInventoryType(o1.getItemId()) == GameConstants.getInventoryType(o2.getItemId())) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        for (MapleInventoryType type : MapleInventoryType.values()) {
            typeItems.put(type, new ArrayList<>(items));
        }
        this.npc_id = npc_id;
        client.SendPacket(ResCTrunkDlg.TrunkResult(this, OpsTrunk.TrunkRes_OpenTrunkDlg));
    }

    public int getNpcId() {
        return this.npc_id;
    }

    private OpsDBCHAR lastModified = OpsDBCHAR.DBCHAR_ALL;

    public OpsDBCHAR getLastModified() {
        return this.lastModified;
    }

    public boolean setLastModified(int type) {
        switch (type) {
            case 1: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTEQUIP);
                return true;
            }
            case 2: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTCONSUME);
                return true;
            }
            case 3: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTINSTALL);
                return true;
            }
            case 4: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTETC);
                return true;
            }
            case 5: {
                setLastModified(OpsDBCHAR.DBCHAR_ITEMSLOTCASH);
                return true;
            }
            default: {
                break;
            }
        }

        return false;
    }

    public void setLastModified(OpsDBCHAR lastModified) {
        this.lastModified = lastModified;
    }

    public int getMeso() {
        return meso;
    }

    public IItem findById(int itemId) {
        for (IItem item : items) {
            if (item.getItemId() == itemId) {
                return item;
            }
        }
        return null;
    }

    public void setMeso(int meso) {
        if (meso < 0) {
            return;
        }
        changed = true;
        this.meso = meso;
    }

    public boolean isFull() {
        return items.size() >= slots;
    }

    public int getSlots() {
        return slots;
    }

    public void increaseSlots(byte gain) {
        changed = true;
        this.slots += gain;
    }

    public void setSlots(byte set) {
        changed = true;
        this.slots = set;
    }

    public void close() {
        typeItems.clear();
    }
}
