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
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Gifts {

    public static final String DB_TABLE_NAME = "gifts";

    public static boolean add(int recipient, String from, String message, int sn, int uniqueid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " VALUES (DEFAULT, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, recipient);
                ps.setString(2, from);
                ps.setString(3, message);
                ps.setInt(4, sn);
                ps.setInt(5, uniqueid);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add");
        }

        return false;
    }

    /**
     * Loads all pending gifts for the given recipient character and clears
     * them from the table. Returns null if the operation failed.
     */
    public static List<GiftRow> loadAndClear(int recipientCharacterId) {
        List<GiftRow> gifts = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE `recipient` = ?")) {
                ps.setInt(1, recipientCharacterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gifts.add(new GiftRow(rs.getInt("sn"), rs.getInt("uniqueid"), rs.getString("from"), rs.getString("message")));
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE `recipient` = ?")) {
                ps.setInt(1, recipientCharacterId);
                ps.executeUpdate();
            }

            return gifts;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "loadAndClear");
        }

        return null;
    }

    public static final class GiftRow {

        public final int sn;
        public final int uniqueid;
        public final String from;
        public final String message;

        public GiftRow(int sn, int uniqueid, String from, String message) {
            this.sn = sn;
            this.uniqueid = uniqueid;
            this.from = from;
            this.message = message;
        }
    }

}
