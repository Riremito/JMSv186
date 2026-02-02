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
package tacos.database.query;

import odin.client.MapleClient;
import tacos.constants.MapleClientState;
import tacos.database.DatabaseConnection;
import tacos.database.DatabaseException;
import tacos.debug.DebugLogger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import tacos.config.CodePage;
import tacos.tools.TacosTools;

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
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET loggedin = 0")) {
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "resetLoginState");
        }
        return false;
    }

    public static MapleClientState getLoginState(MapleClient client) {
        try {
            Connection con = DatabaseConnection.getConnection();
            MapleClientState state;
            try (PreparedStatement ps = con.prepareStatement("SELECT loggedin, lastlogin, `birthday` + 0 AS `bday` FROM " + DB_TABLE_NAME + " WHERE id = ?")) {
                ps.setInt(1, client.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        ps.close();
                        throw new DatabaseException("Everything sucks");
                    }
                    state = MapleClientState.find(rs.getByte("loggedin"));
                    if (state == MapleClientState.LOGIN_SERVER_TRANSITION || state == MapleClientState.CHANGE_CHANNEL) {
                        if (rs.getTimestamp("lastlogin").getTime() + 20000 < System.currentTimeMillis()) { // connecting to chanserver timeout
                            state = MapleClientState.LOGIN_NOTLOGGEDIN;
                            updateLoginState(client, state);
                        }
                    }
                }
            }
            return state;
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getLoginState");
            throw new DatabaseException("error getting login state", e);
        }
    }

    public static void updateLoginState(MapleClient client, MapleClientState newstate) {
        String ip_addr = client.getIPAddress();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?")) {
                ps.setInt(1, newstate.get());
                ps.setString(2, ip_addr);
                ps.setInt(3, client.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateLoginState");
        }
    }

    public static String getSHA256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(CodePage.getCodePage()), 0, text.length());
            return TacosTools.DatatoString(md.digest());
        } catch (NoSuchAlgorithmException ex) {
        }
        DebugLogger.ErrorLog("getSHA256");
        return null;
    }

    public static String getSalt() {
        String salt = "";
        Random rand = new Random();
        for (int i = 0; i < 8; i++) {
            salt += TacosTools.BYTEtoString((byte) (rand.nextInt() % 0x100));
        }
        return salt;
    }

    // salt : null
    public static boolean autoRegister(String maple_id, String password) {
        String password1_hash = getSHA256(password);
        String password2_hash = getSHA256(DEFAULT_PASSWORD2);
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (name, password, 2ndpassword, ACash, gender) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, maple_id);
                ps.setString(2, password1_hash);
                ps.setString(3, password2_hash);
                ps.setInt(4, DEFAULT_POINT);
                ps.setByte(5, DEFAULT_GENDER);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                }
            }

            DebugLogger.InfoLog("[AutoRegister] \"" + maple_id + "\"");
            return true;
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "autoRegister");
        }

        return false;
    }

    public static boolean updatePassword(MapleClient client, String password) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE `" + DB_TABLE_NAME + "` SET `password` = ?, `salt` = ? WHERE id = ?")) {
                String password1_salt = getSalt();
                ps.setString(1, getSHA256(password + password1_salt));
                ps.setString(2, password1_salt);
                ps.setInt(3, client.getId());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updatePassword");
        }

        return false;
    }

    public static boolean resetPassword(String maple_id, String password) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE `" + DB_TABLE_NAME + "` SET `password` = ?, `salt` = ? WHERE name = ?")) {
                String password1_salt = getSalt();
                ps.setString(1, getSHA256(password + password1_salt));
                ps.setString(2, password1_salt);
                ps.setString(3, maple_id);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "resetPassword");
        }
        return false;
    }

    public static int login(MapleClient client, String maple_id, String password) {
        int loginok = 5;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE name = ?")) {
                ps.setString(1, maple_id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int accId = rs.getInt("id");
                        int banned = rs.getInt("banned");
                        String password1_hash = rs.getString("password");
                        String password1_salt = rs.getString("salt");
                        String password2_hash = rs.getString("2ndpassword");
                        String password2_salt = rs.getString("salt2");
                        boolean gameMaster = rs.getInt("gm") > 0;
                        byte gender = rs.getByte("gender");
                        ps.close();

                        client.setId(accId);
                        client.setPassword2Hash(password2_hash);
                        client.setPassword2Salt(password2_salt);
                        client.setGameMaster(gameMaster);
                        client.setGender(gender);

                        if (banned > 0 && !gameMaster) {
                            loginok = 3;
                        } else {
                            if (banned == -1) {
                                DebugLogger.ErrorLog("banned == -1");
                            }
                            MapleClientState loginstate = getLoginState(client);
                            if (loginstate.get() > MapleClientState.LOGIN_NOTLOGGEDIN.get()) { // already loggedin
                                loginok = 7;
                            } else {
                                if (password1_hash.equals(getSHA256((password1_salt == null) ? password : password + password1_salt))) {
                                    loginok = 0;
                                    if (password1_salt == null) {
                                        // set salt
                                        updatePassword(client, password);
                                    }
                                } else {
                                    loginok = 4;
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "login");
        }

        return loginok;
    }

    public static boolean checkLoginIP(MapleClient c) {
        boolean ret = false;

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT SessionIP FROM " + DB_TABLE_NAME + " WHERE id = ?")) {
                ps.setInt(1, c.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        final String sessionIP = rs.getString("SessionIP");

                        if (sessionIP != null) {
                            ret = c.getIPAddress().equals(sessionIP.split(":")[0]);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "checkLoginIP");
        }

        if (!ret) {
            DebugLogger.ErrorLog("checkLoginIP");
        }

        return ret;
    }

    public static boolean finishLogin(MapleClient c) {
        MapleClientState state = getLoginState(c);
        if (state.get() > MapleClientState.LOGIN_NOTLOGGEDIN.get() && state != MapleClientState.LOGIN_WAITING) {
            return false;
        }
        updateLoginState(c, MapleClientState.LOGIN_LOGGEDIN);
        return true;
    }

}
