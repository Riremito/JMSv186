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
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Queststatusmobs {

    public static final String DB_TABLE_NAME = "queststatusmobs";

    /**
     * NOTE: unlike {@code add}, this declares {@code throws SQLException}
     * instead of catching it internally, since it is called from
     * MapleCharacter.loadCharFromDB (via DQ_Queststatus.loadAll), which
     * relies on the exception propagating to its own outer catch block.
     */
    public static Map<Integer, Integer> loadAll(int queststatusid) throws SQLException {
        Map<Integer, Integer> ret = new LinkedHashMap<>();
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE queststatusid = ?")) {
            ps.setInt(1, queststatusid);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ret.put(rs.getInt("mob"), rs.getInt("count"));
                }
            }
        }
        return ret;
    }

    public static boolean add(int queststatusid, Map<Integer, Integer> killedMobs) {
        if (killedMobs == null) {
            return true;
        }
        if (!DatabaseConnection.setManual()) {
            return false;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement pse = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " VALUES (DEFAULT, ?, ?, ?)");
            for (int mob : killedMobs.keySet()) {
                Integer count = killedMobs.get(mob);
                if (count == null) {
                    count = 0;
                }
                pse.setInt(1, queststatusid);
                pse.setInt(2, mob);
                pse.setInt(3, count);
                pse.executeUpdate();
            }
            return true;
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
