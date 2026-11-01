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
import java.sql.SQLException;

/**
 *
 * @author Riremito
 */
public class DQ_Achievements {

    public static final String DB_TABLE_NAME = "achievements";

    public static boolean deleteByAccountId(Connection con, int accountId) {
        try {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE accountid = ?")) {
                ps.setInt(1, accountId);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "deleteByAccountId");
        }
        return false;
    }

}
