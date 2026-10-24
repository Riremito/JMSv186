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
import tacos.debug.DebugLogger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import tacos.database.DatabaseConnection;

/**
 *
 * @author Riremito
 */
public class DQ_Questinfo {

    public static final String DB_TABLE_NAME = "questinfo";

    public static Map<Integer, String> loadAll(int characterId) {
        try {
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
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "loadAll");
        }
        return new LinkedHashMap<>();
    }

    public static boolean deleteAndSaveAll(Connection con, int characterId, Map<Integer, String> questinfo) {
        try {
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
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "deleteAndSaveAll");
        }
        return false;
    }

}
