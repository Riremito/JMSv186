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
import java.util.LinkedHashMap;
import java.util.Map;
import tacos.database.DatabaseConnection;

/**
 * NOTE: load/save here declare {@code throws SQLException} instead of
 * catching it internally, since these are called from
 * MapleCharacter.loadCharFromDB/saveToDB, which manage their own outer
 * try/catch and (for saveToDB) an explicit transaction that must see any
 * failure in order to roll back correctly.
 *
 * @author Riremito
 */
public class DQ_Questinfo {

    public static final String DB_TABLE_NAME = "questinfo";

    public static Map<Integer, String> loadAll(int characterId) throws SQLException {
        Map<Integer, String> ret = new LinkedHashMap<>();
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ret.put(rs.getInt("quest"), rs.getString("customData"));
                }
            }
        }
        return ret;
    }

    public static void deleteAndSaveAll(Connection con, int characterId, Map<Integer, String> questinfo) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (`characterid`, `quest`, `customData`) VALUES (?, ?, ?)")) {
            ps.setInt(1, characterId);
            for (Map.Entry<Integer, String> q : questinfo.entrySet()) {
                ps.setInt(2, q.getKey());
                ps.setString(3, q.getValue());
                ps.execute();
            }
        }
    }

}
