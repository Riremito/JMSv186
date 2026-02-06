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
import java.sql.SQLException;
import odin.client.MapleCharacter;
import tacos.config.DeveloperMode;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Inventoryslot {

    public static final String DB_TABLE_NAME = "inventoryslot";

    public static boolean add(MapleCharacter chr) {
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

}
