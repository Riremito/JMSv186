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
import java.sql.SQLException;
import java.util.Map;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Queststatusmobs {

    public static final String DB_TABLE_NAME = "queststatusmobs";

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
