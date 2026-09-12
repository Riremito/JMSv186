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
public class DQ_MtsItems {

    public static final String DB_TABLE_NAME = "mts_items";

    public static List<Integer> getIdsByCharacterId(int characterId) {
        List<Integer> ids = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
                ps.setInt(1, characterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ids.add(rs.getInt("id"));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getIdsByCharacterId");
        }

        return ids;
    }

    public static List<TabOneRow> getTabOneRows() {
        List<TabOneRow> ret = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE tab = 1")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.add(new TabOneRow(rs.getInt("id"), rs.getInt("characterid"), rs.getInt("price"), rs.getString("seller"), rs.getLong("expiration")));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getTabOneRows");
        }

        return ret;
    }

    public static boolean replaceTabOne(List<TabOneRow> rows) {
        try {
            Connection con = DatabaseConnection.getConnection();

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE tab = 1")) {
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?)")) {
                for (TabOneRow row : rows) {
                    ps.setInt(1, row.id);
                    ps.setByte(2, (byte) 1);
                    ps.setInt(3, row.price);
                    ps.setInt(4, row.characterId);
                    ps.setString(5, row.seller);
                    ps.setLong(6, row.expiration);
                    ps.executeUpdate();
                }
            }

            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "replaceTabOne");
        }

        return false;
    }

    public static final class TabOneRow {

        public final int id;
        public final int characterId;
        public final int price;
        public final String seller;
        public final long expiration;

        public TabOneRow(int id, int characterId, int price, String seller, long expiration) {
            this.id = id;
            this.characterId = characterId;
            this.price = price;
            this.seller = seller;
            this.expiration = expiration;
        }
    }

}
