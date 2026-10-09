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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Notes {

    public static final String DB_TABLE_NAME = "notes";

    public static void sendNote(String to, String name, String msg, int fame) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (`to`, `from`, `message`, `timestamp`, `gift`) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, to);
                ps.setString(2, name);
                ps.setString(3, msg);
                ps.setLong(4, System.currentTimeMillis());
                ps.setInt(5, fame);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "sendNote");
        }
    }


    /**
     * Returns the `gift` value of the note with this id, or {@code null} if
     * no such note exists (or the lookup failed).
     */
    public static Integer getGift(int noteId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT gift FROM " + DB_TABLE_NAME + " WHERE `id`=?")) {
                ps.setInt(1, noteId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("gift");
                    }
                }
            }
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getGift");
        }

        return null;
    }

    public static boolean deleteById(int noteId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE `id`=?")) {
                ps.setInt(1, noteId);
                ps.execute();
                return true;
            }
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "deleteById");
        }

        return false;
    }
}
