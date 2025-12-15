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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import tacos.config.CodePage;

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
            DebugLogger.ExceptionLog("Database Error : " + DB_TABLE_NAME);
        }
        return false;
    }

    public static final MapleClientState getLoginState(MapleClient client) {
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT loggedin, lastlogin, `birthday` + 0 AS `bday` FROM " + DB_TABLE_NAME + " WHERE id = ?");
            ps.setInt(1, client.getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                throw new DatabaseException("Everything sucks");
            }
            MapleClientState state = MapleClientState.find(rs.getByte("loggedin"));

            if (state == MapleClientState.LOGIN_SERVER_TRANSITION || state == MapleClientState.CHANGE_CHANNEL) {
                if (rs.getTimestamp("lastlogin").getTime() + 20000 < System.currentTimeMillis()) { // connecting to chanserver timeout
                    state = MapleClientState.LOGIN_NOTLOGGEDIN;
                    updateLoginState(client, state);
                }
            }
            rs.close();
            ps.close();
            if (state == MapleClientState.LOGIN_LOGGEDIN) {
                client.setLoggedIn(true);
            } else {
                client.setLoggedIn(false);
            }
            return state;
        } catch (SQLException e) {
            client.setLoggedIn(false);
            throw new DatabaseException("error getting login state", e);
        }
    }

    public static void updateLoginState(MapleClient client, MapleClientState newstate) {
        String ip_addr = client.getSessionIPAddress();
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?");
            ps.setInt(1, newstate.get());
            ps.setString(2, ip_addr);
            ps.setInt(3, client.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.err.println("error updating login state" + e);
        }
        if (newstate == MapleClientState.LOGIN_NOTLOGGEDIN || newstate == MapleClientState.LOGIN_WAITING) {
            client.setLoggedIn(false);
            client.setServerTransition(false);
        } else {
            boolean serverTransition = (newstate == MapleClientState.LOGIN_SERVER_TRANSITION || newstate == MapleClientState.CHANGE_CHANNEL);
            client.setServerTransition(serverTransition);
            client.setLoggedIn(!serverTransition);
        }
    }

    public static String BYTEtoString(byte b) {
        byte high = (byte) ((b >> 4) & 0x0F);
        byte low = (byte) (b & 0x0F);
        high += (high <= 0x09) ? 0x30 : 0x37;
        low += (low <= 0x09) ? 0x30 : 0x37;
        return new String(new byte[]{high, low});
    }

    public static String DatatoString(byte hex[]) {
        String data = "";

        for (byte b : hex) {
            data += BYTEtoString(b);
        }

        return data;
    }

    public static String getSHA256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(CodePage.getCodePage()), 0, text.length());
            return DatatoString(md.digest());
        } catch (NoSuchAlgorithmException ex) {
        }
        DebugLogger.ErrorLog("getSHA256");
        return null;
    }

    public static String getSalt() {
        String salt = "";
        Random rand = new Random();
        for (int i = 0; i < 8; i++) {
            salt += BYTEtoString((byte) (rand.nextInt() % 0x100));
        }
        return salt;
    }

    // salt : null
    public static boolean autoRegister(String maple_id, String password) {
        String password1_hash = getSHA256(password);
        String password2_hash = getSHA256(DEFAULT_PASSWORD2);
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

            DebugLogger.InfoLog("[AutoRegister] \"" + maple_id + "\"");
            return true;
        } catch (SQLException e) {
            DebugLogger.ExceptionLog("Database Error : " + DB_TABLE_NAME);
        }

        return false;
    }

    public static int login(MapleClient client, String maple_id, String password) {
        int loginok = 5;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE name = ?");
            ps.setString(1, maple_id);
            ResultSet rs = ps.executeQuery();

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
                        client.setLoggedIn(false);
                        loginok = 7;
                    } else {
                        if (password1_hash.equals(getSHA256((password1_salt == null) ? password : password + password1_salt))) {
                            loginok = 0;
                            if (password1_salt == null) {
                                // set salt
                                PreparedStatement pss = con.prepareStatement("UPDATE `" + DB_TABLE_NAME + "` SET `password` = ?, `salt` = ? WHERE id = ?");
                                try {
                                    String salt_db = getSalt();
                                    pss.setString(1, getSHA256(password + salt_db));
                                    pss.setString(2, salt_db);
                                    pss.setInt(3, accId);
                                    pss.executeUpdate();
                                } finally {
                                    pss.close();
                                }
                            }
                        } else {
                            client.setLoggedIn(false);
                            loginok = 4;
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
            DebugLogger.ExceptionLog("checkLoginIP");
        }

        if (!ret) {
            DebugLogger.ErrorLog("checkLoginIP");
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
