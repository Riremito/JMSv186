/*
 * Copyright (C) 2025 Riremito
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

import tacos.client.TacosClient;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Riremito
 */
public class DQ_Character_slots {

    public static final String DB_TABLE_NAME = "character_slots";

    public static boolean load(TacosClient client) {
        int charslots = client.getCharSlots();

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE accid = ? AND worldid = ?")) {
                ps.setInt(1, client.getId());
                ps.setInt(2, client.getSelectedWorld());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        charslots = rs.getInt("charslots");
                        client.setCharSlots(charslots); // set
                    } else {
                        try (PreparedStatement psu = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (accid, worldid, charslots) VALUES (?, ?, ?)")) {
                            psu.setInt(1, client.getId());
                            psu.setInt(2, client.getSelectedWorld());
                            psu.setInt(3, charslots);
                            psu.executeUpdate();
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getCharacterSlots");
        }

        return false;
    }
}
