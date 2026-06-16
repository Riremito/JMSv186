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
import odin.client.inventory.MapleInventoryType;
import tacos.client.TacosCharacter;
import tacos.config.DeveloperMode;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Inventoryslot {

    public static final String DB_TABLE_NAME = "inventoryslot";

    public static boolean add(TacosCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, chr.getId());
                ps.setByte(2, (byte) DeveloperMode.DM_INV_SLOT_EQUIP.getInt()); // Eq
                ps.setByte(3, (byte) DeveloperMode.DM_INV_SLOT_USE.getInt()); // Use
                ps.setByte(4, (byte) DeveloperMode.DM_INV_SLOT_SETUP.getInt()); // Setup
                ps.setByte(5, (byte) DeveloperMode.DM_INV_SLOT_ETC.getInt()); // ETC
                ps.setByte(6, (byte) DeveloperMode.DM_INV_SLOT_CASH.getInt()); // Cash
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add");
            DatabaseConnection.rollback();
        } finally {
            DatabaseConnection.commit();
            DatabaseConnection.setAuto();
        }

        return false;
    }

    public static boolean load(TacosCharacter chr) {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, chr.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    chr.getInventory(MapleInventoryType.EQUIP).setSlotLimit(rs.getByte("equip"));
                    chr.getInventory(MapleInventoryType.USE).setSlotLimit(rs.getByte("use"));
                    chr.getInventory(MapleInventoryType.SETUP).setSlotLimit(rs.getByte("setup"));
                    chr.getInventory(MapleInventoryType.ETC).setSlotLimit(rs.getByte("etc"));
                    chr.getInventory(MapleInventoryType.CASH).setSlotLimit(rs.getByte("cash"));
                    return true;
                }
            }
        } catch (SQLException ex) {
        }

        DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        return false;
    }

    public static boolean save(TacosCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET `equip` = ?, `use` = ?, `setup` = ?, `etc` = ?, `cash` = ? WHERE `characterid` = ?")) {
            ps.setInt(1, chr.getInventory(MapleInventoryType.EQUIP).getSlotLimit());
            ps.setInt(2, chr.getInventory(MapleInventoryType.USE).getSlotLimit());
            ps.setInt(3, chr.getInventory(MapleInventoryType.SETUP).getSlotLimit());
            ps.setInt(4, chr.getInventory(MapleInventoryType.ETC).getSlotLimit());
            ps.setInt(5, chr.getInventory(MapleInventoryType.CASH).getSlotLimit());
            ps.setInt(6, chr.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "save");
            DatabaseConnection.rollback();
        } finally {
            DatabaseConnection.commit();
            DatabaseConnection.setAuto();
        }

        return false;
    }
}
