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
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Dueypackages {

    public static final String DB_TABLE_NAME = "dueypackages";
    public static boolean deleteByCharacterId(int character_id) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE RecieverId = ?")) {
                ps.setInt(1, character_id);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "deleteByCharacterId");
        }
        return false;
    }
}
