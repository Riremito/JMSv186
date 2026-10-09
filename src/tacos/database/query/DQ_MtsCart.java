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
public class DQ_MtsCart {

    public static final String DB_TABLE_NAME = "mts_cart";

    public static List<Integer> getItemIds(int characterId) {
        List<Integer> ids = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
                ps.setInt(1, characterId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ids.add(rs.getInt("itemid"));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getItemIds");
        }

        return ids;
    }

    public static boolean replace(int characterId, List<Integer> cart, int owedNX) {
        try {
            Connection con = DatabaseConnection.getConnection();

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
                ps.setInt(1, characterId);
                ps.execute();
            }

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " VALUES(DEFAULT, ?, ?)")) {
                ps.setInt(1, characterId);
                for (int i : cart) {
                    ps.setInt(2, i);
                    ps.executeUpdate();
                }
                if (owedNX > 0) {
                    ps.setInt(2, -owedNX);
                    ps.executeUpdate();
                }
            }

            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "replace");
        }

        return false;
    }

}
