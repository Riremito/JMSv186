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
import odin.server.maps.SavedLocationType;
import tacos.database.DatabaseConnection;

/**
 * NOTE: load/save here declare {@code throws SQLException} instead of
 * catching it internally, matching the original MapleCharacter behavior
 * (see DQ_Questinfo for the same rationale).
 *
 * @author Riremito
 */
public class DQ_Savedlocations {

    public static final String DB_TABLE_NAME = "savedlocations";

    public static Map<Integer, Integer> loadAll(int characterId) throws SQLException {
        Map<Integer, Integer> ret = new LinkedHashMap<>();
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT `locationtype`,`map` FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ret.put(rs.getInt("locationtype"), rs.getInt("map"));
                }
            }
        }
        return ret;
    }

    public static void deleteAndSaveAll(Connection con, int characterId, int[] savedLocations) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            ps.executeUpdate();
        }

        try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, `locationtype`, `map`) VALUES (?, ?, ?)")) {
            ps.setInt(1, characterId);
            for (final SavedLocationType savedLocationType : SavedLocationType.values()) {
                if (savedLocations[savedLocationType.getValue()] != -1) {
                    ps.setInt(2, savedLocationType.getValue());
                    ps.setInt(3, savedLocations[savedLocationType.getValue()]);
                    ps.execute();
                }
            }
        }
    }

}
