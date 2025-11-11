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

import odin.client.MapleClient;
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

    public static boolean setCharacterSlots(MapleClient c) {
        int charslots = c.getCharSlots();

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE accid = ? AND worldid = ?");
            ps.setInt(1, c.getId());
            ps.setInt(2, c.getWorld());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                charslots = rs.getInt("charslots");
                c.setCharSlots(charslots); // set
            } else {
                PreparedStatement psu = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (accid, worldid, charslots) VALUES (?, ?, ?)");
                psu.setInt(1, c.getId());
                psu.setInt(2, c.getWorld());
                psu.setInt(3, charslots);
                psu.executeUpdate();
                psu.close();
            }
            rs.close();
            ps.close();
            return true;
        } catch (SQLException sqlE) {
            DebugLogger.ExceptionLog("getCharacterSlots");
        }

        return false;
    }

    public static boolean gainCharacterSlot(MapleClient c) {
        if (c.getCharSlots() >= 15) {
            return false;
        }
        int charslots = c.getCharSlots() + 1;

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET charslots = ? WHERE worldid = ? AND accid = ?");
            ps.setInt(1, charslots);
            ps.setInt(2, c.getWorld());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException sqlE) {
            DebugLogger.ExceptionLog("gainCharacterSlot");
        }
        return false;
    }
}
