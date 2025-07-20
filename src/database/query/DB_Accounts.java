/*
 * Copyright (C) 2025 Riremito
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
package database.query;

import database.DatabaseConnection;
import debug.Debug;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Riremito
 */
public class DB_Accounts {

    public static final String table_name = "accounts";

    // 初期化
    public static boolean resetLoginState() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE " + table_name + " SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException ex) {
            Debug.ExceptionLog("Database Error : " + table_name);
        }

        return false;
    }
}
