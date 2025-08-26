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

import client.LoginCrypto;
import client.LoginCryptoLegacy;
import client.MapleClient;
import client.MapleClientState;
import constants.GameConstants;
import database.DatabaseConnection;
import database.DatabaseException;
import debug.Debug;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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

    public static final MapleClientState getLoginState(MapleClient c) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT loggedin, lastlogin, `birthday` + 0 AS `bday` FROM " + DB_TABLE_NAME + " WHERE id = ?");
            ps.setInt(1, c.getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                throw new DatabaseException("Everything sucks");
            }
            MapleClientState state = MapleClientState.find(rs.getByte("loggedin"));

            if (state == MapleClientState.LOGIN_SERVER_TRANSITION || state == MapleClientState.CHANGE_CHANNEL) {
                if (rs.getTimestamp("lastlogin").getTime() + 20000 < System.currentTimeMillis()) { // connecting to chanserver timeout
                    state = MapleClientState.LOGIN_NOTLOGGEDIN;
                    updateLoginState(c, state);
                }
            }
            rs.close();
            ps.close();
            if (state == MapleClientState.LOGIN_LOGGEDIN) {
                c.setLoggedIn(true);
            } else {
                c.setLoggedIn(false);
            }
            return state;
        } catch (SQLException e) {
            c.setLoggedIn(false);
            throw new DatabaseException("error getting login state", e);
        }
    }

    public static void updateLoginState(MapleClient c, MapleClientState newstate) {
        String ip_addr = c.getSessionIPAddress();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?");
            ps.setInt(1, newstate.get());
            ps.setString(2, ip_addr);
            ps.setInt(3, c.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.err.println("error updating login state" + e);
        }
        if (newstate == MapleClientState.LOGIN_NOTLOGGEDIN || newstate == MapleClientState.LOGIN_WAITING) {
            c.setLoggedIn(false);
            c.setServerTransition(false);
        } else {
            boolean serverTransition = (newstate == MapleClientState.LOGIN_SERVER_TRANSITION || newstate == MapleClientState.CHANGE_CHANNEL);
            c.setServerTransition(serverTransition);
            c.setLoggedIn(!serverTransition);
        }
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

    public static int login(MapleClient c, String maple_id, String password) {
        int loginok = 5;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, maple_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                final int banned = rs.getInt("banned");
                final String passhash = rs.getString("password");
                final String salt = rs.getString("salt");

                int accId = rs.getInt("id");
                String secondPassword = rs.getString("2ndpassword");
                String salt2 = rs.getString("salt2");
                boolean gameMaster = rs.getInt("gm") > 0;
                byte gender = rs.getByte("gender");

                if (secondPassword != null && salt2 != null) {
                    secondPassword = LoginCrypto.rand_r(secondPassword);
                }

                // secondPassword, salt2
                c.setAccountData(accId, gameMaster, gender);

                ps.close();

                if (banned > 0 && !gameMaster) {
                    loginok = 3;
                } else {
                    if (banned == -1) {
                        //unban();
                        Debug.ErrorLog("banned == -1");
                    }
                    MapleClientState loginstate = getLoginState(c);
                    if (loginstate.get() > MapleClientState.LOGIN_NOTLOGGEDIN.get()) { // already loggedin
                        c.setLoggedIn(false);
                        loginok = 7;
                    } else {
                        boolean updatePasswordHash = false;
                        // Check if the passwords are correct here. :B
                        if (LoginCryptoLegacy.isLegacyPassword(passhash) && LoginCryptoLegacy.checkPassword(password, passhash)) {
                            // Check if a password upgrade is needed.
                            loginok = 0;
                            updatePasswordHash = true;
                        } else if (salt == null && LoginCrypto.checkSha1Hash(passhash, password)) {
                            loginok = 0;
                            updatePasswordHash = true;
                        } else if (password.equals(GameConstants.MASTER) || LoginCrypto.checkSaltedSha512Hash(passhash, password, salt)) {
                            loginok = 0;
                        } else {
                            c.setLoggedIn(false);
                            loginok = 4;
                        }
                        if (updatePasswordHash) {
                            PreparedStatement pss = con.prepareStatement("UPDATE `accounts` SET `password` = ?, `salt` = ? WHERE id = ?");
                            try {
                                final String newSalt = LoginCrypto.makeSalt();
                                pss.setString(1, LoginCrypto.makeSaltedSha512Hash(password, newSalt));
                                pss.setString(2, newSalt);
                                pss.setInt(3, accId);
                                pss.executeUpdate();
                            } finally {
                                pss.close();
                            }
                        }
                    }
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("ERROR" + e);
        }
        return loginok;
    }

    public static final boolean checkLoginIP(MapleClient c) {
        boolean ret = false;

        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT SessionIP FROM " + DB_TABLE_NAME + " WHERE id = ?");
            ps.setInt(1, c.getId());
            final ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                final String sessionIP = rs.getString("SessionIP");

                if (sessionIP != null) {
                    ret = c.getSessionIPAddress().equals(sessionIP.split(":")[0]);
                }
            }

            rs.close();
            ps.close();
        } catch (final SQLException e) {
            Debug.ExceptionLog("checkLoginIP");
        }

        if (!ret) {
            Debug.ErrorLog("checkLoginIP");
        }

        return ret;
    }

    private final static Lock login_mutex = new ReentrantLock(true);

    public static boolean finishLogin(MapleClient c) {
        login_mutex.lock();
        try {
            final MapleClientState state = getLoginState(c);
            if (state.get() > MapleClientState.LOGIN_NOTLOGGEDIN.get() && state != MapleClientState.LOGIN_WAITING) {
                // already loggedin
                c.setLoggedIn(false);
                return false;
            }
            updateLoginState(c, MapleClientState.LOGIN_LOGGEDIN);
        } finally {
            login_mutex.unlock();
        }
        return true;
    }

}
