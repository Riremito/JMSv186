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

import client.LoginCryptoLegacy;
import client.MapleClient;
import database.DatabaseConnection;
import debug.Debug;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Riremito
 */
public class DQ_Accounts {

    public static final String DB_TABLE_NAME = "accounts";
    private static final String DEFAULT_PASSWORD2 = "777777";
    private static final int DEFAULT_POINT = 10000000;
    private static final byte DEFAULT_GENDER = 0;

    public static boolean resetLoginState() {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException ex) {
            Debug.ExceptionLog("Database Error : " + DB_TABLE_NAME);
        }
        return false;
    }

    public static boolean autoRegister(String maple_id, String password) {
        String password1_hash = null;
        String password2_hash = null;
        try {
            password1_hash = LoginCryptoLegacy.encodeSHA1(password);
            password2_hash = LoginCryptoLegacy.encodeSHA1(DEFAULT_PASSWORD2);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MapleClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MapleClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (name, password, 2ndpassword, ACash, gender) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, maple_id);
            ps.setString(2, password1_hash);
            ps.setString(3, password2_hash);
            ps.setInt(4, DEFAULT_POINT);
            ps.setByte(5, DEFAULT_GENDER);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            rs.close();
            ps.close();

            Debug.InfoLog("[AutoRegister] \"" + maple_id + "\"");
            return true;
        } catch (SQLException e) {
            Debug.ExceptionLog("Database Error : " + DB_TABLE_NAME);
        }

        return false;
    }

}
