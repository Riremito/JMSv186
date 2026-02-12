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
import java.sql.Statement;
import odin.client.MapleCharacter;
import odin.client.inventory.IEquip;
import odin.client.inventory.IItem;
import odin.client.inventory.MapleInventory;
import odin.client.inventory.MapleInventoryType;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Inventoryitems {

    public static final String DB_TABLE_NAME = "inventoryitems";

    public static boolean delete(MapleCharacter chr) {
        /*
        if (!DatabaseConnection.setManual()) {
            return false;
        }
         */

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE type = ? AND characterid = ?;")) {
                ps.setByte(1, (byte) InvTypeDB.INVENTORY.get());
                ps.setInt(2, chr.getId());
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "delete");
            //DatabaseConnection.rollback();
        } finally {
            //DatabaseConnection.commit();
            //DatabaseConnection.setAuto();
        }

        return false;
    }

    public static boolean add(MapleCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        delete(chr);

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, itemid, inventorytype, position, quantity, owner, GM_Log, uniqueid, expiredate, flag, type, sender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                for (MapleInventory iv : chr.getInventorys()) {
                    for (IItem item : iv.list()) {
                        ps.setInt(1, chr.getId());
                        ps.setInt(2, item.getItemId());
                        ps.setInt(3, iv.getType().getType()); // equip, consume...
                        ps.setInt(4, item.getPosition());
                        ps.setInt(5, item.getQuantity());
                        ps.setString(6, item.getOwner());
                        ps.setString(7, item.getGMLog());
                        ps.setInt(8, item.getUniqueId());
                        ps.setLong(9, item.getExpiration());
                        ps.setByte(10, item.getFlag());
                        ps.setByte(11, (byte) InvTypeDB.INVENTORY.get()); // inventory, storage...
                        ps.setString(12, item.getGiftFrom());
                        ps.executeUpdate();
                        // equip stat
                        if (iv.getType() == MapleInventoryType.EQUIP || iv.getType() == MapleInventoryType.EQUIPPED) {
                            try (ResultSet rs = ps.getGeneratedKeys()) {
                                if (!rs.next()) {
                                    return false;
                                }
                                int unique_id = rs.getInt(1);
                                DQ_Inventoryequipment.add(unique_id, (IEquip) item);
                            }
                        }
                    }
                }
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add");
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "" + ex);
            DatabaseConnection.rollback();
        } finally {
            DatabaseConnection.commit();
            DatabaseConnection.setAuto();
        }

        return false;
    }
}
