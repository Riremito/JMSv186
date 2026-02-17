/*
 * Copyright (C) 2026 Riremito
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */
package tacos.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import odin.client.inventory.IItem;
import odin.client.inventory.ItemLoader;
import odin.client.inventory.MapleInventoryType;
import odin.constants.GameConstants;
import tacos.client.TacosStorage;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;
import tacos.odin.OdinPair;

/**
 *
 * @author Riremito
 */
public class DQ_Storages {

    public static final String DB_TABLE_NAME = "storages";

    public static boolean load(TacosStorage storage) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE accountid = ?;")) {
                ps.setInt(1, storage.getAccountId());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int meso = rs.getInt("meso");
                    int slot = rs.getByte("slots");
                    storage.setMeso(meso);
                    storage.setSlot(slot);
                    return true;
                }

                return false;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return false;
    }

    public static boolean loadItems(TacosStorage storage) {
        ArrayList<IItem> items = storage.getItems();
        items.clear();
        try {
            for (OdinPair<IItem, MapleInventoryType> mit : ItemLoader.STORAGE.loadItems(false, storage.getAccountId()).values()) {
                items.add(mit.getLeft());
            }
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }

    public static boolean create(TacosStorage storage) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (accountid, meso, slots) VALUES (?, ?, ?);", DatabaseConnection.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, storage.getAccountId());
                ps.setInt(2, storage.getMeso());
                ps.setInt(3, storage.getSlot());
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "create");
        } finally {
            DatabaseConnection.commit();
        }

        return false;
    }

    public static boolean update(TacosStorage storage) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET slots = ?, meso = ? WHERE accountid = ?;")) {
                ps.setInt(1, storage.getSlot());
                ps.setInt(2, storage.getMeso());
                ps.setInt(3, storage.getAccountId());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "update");
        }

        return false;
    }

    public static boolean updateItems(TacosStorage storage) {
        List<OdinPair<IItem, MapleInventoryType>> listing = new ArrayList<>();
        for (final IItem item : storage.getItems()) {
            listing.add(new OdinPair<>(item, GameConstants.getInventoryType(item.getItemId())));
        }
        try {
            ItemLoader.STORAGE.saveItems(listing, storage.getAccountId());
        } catch (SQLException ex) {
            return false;
        }
        return true;
    }

}
