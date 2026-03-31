/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tacos.database;

import java.io.BufferedReader;
import java.io.FileReader;
import tacos.property.Property_Database;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import tacos.config.Region;
import tacos.config.Version;
import tacos.debug.DebugLogger;

/**
 * All OdinMS servers maintain a Database Connection. This class therefore
 * "singletonices" the connection per process.
 *
 *
 * @author Frz
 */
public class DatabaseConnection {

    private static final ThreadLocal<Connection> connected = new ThreadLocalConnection();
    public static final int RETURN_GENERATED_KEYS = 1;

    public static final Connection getConnection() {
        return connected.get();
    }

    public static boolean setManual() {
        DebugLogger.DebugLog("setManual");
        try {
            Connection con = connected.get();
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            return true;
        } catch (SQLException ex) {
            DebugLogger.ErrorLog("setManual");
        }
        return false;
    }

    public static boolean setAuto() {
        DebugLogger.DebugLog("setAuto");
        try {
            Connection con = connected.get();
            con.setAutoCommit(true);
            con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            return true;
        } catch (SQLException ex) {
            DebugLogger.ErrorLog("setAuto");
        }
        return false;
    }

    public static boolean commit() {
        DebugLogger.DebugLog("commit");
        try {
            Connection con = connected.get();
            con.commit();
            return true;
        } catch (SQLException ex) {
            DebugLogger.ErrorLog("commit");
        }
        return false;
    }

    public static boolean rollback() {
        DebugLogger.DebugLog("rollback");
        try {
            Connection con = connected.get();
            con.rollback();
            return true;
        } catch (SQLException ex) {
            DebugLogger.ErrorLog("rollback");
        }
        return false;
    }

    public static final void closeAll() throws SQLException {
        for (final Connection disconnect : ThreadLocalConnection.allConnections) {
            disconnect.close();
        }
    }

    public static boolean checkDatabase() {
        try {
            Connection con = DatabaseConnection.getConnection();
            DatabaseMetaData dbmd = con.getMetaData();
            try (ResultSet rs = dbmd.getTables(Region.GetRegionName().toLowerCase() + "_v" + Version.getVersion(), null, "accounts", new String[]{"TABLE"})) {
                if (rs.next()) {
                    DebugLogger.SetupLog("Database");
                    return true;
                }
                DebugLogger.SetupLog("Database : initializing...");
                // create tables from sql.
                String sql_path = "sql/jms_v147_empty.sql";
                try (BufferedReader reader = new BufferedReader(new FileReader(sql_path))) {
                    Statement statement = con.createStatement();
                    String line;
                    StringBuilder sqlBuilder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                            continue;
                        }
                        sqlBuilder.append(line).append(" ");
                        if (line.trim().endsWith(";")) {
                            statement.execute(sqlBuilder.toString());
                            sqlBuilder.setLength(0);
                        }
                    }
                    DebugLogger.SetupLog("Database : import OK.");
                    return true;
                } catch (Exception e) {
                    DebugLogger.ErrorLog("Database : import ERROR.");
                    return false;
                }
            }
        } catch (SQLException ex) {
            DebugLogger.ErrorLog("Database : ERROR.");
        }
        return false;
    }

    private static final class ThreadLocalConnection extends ThreadLocal<Connection> {

        public static final Collection<Connection> allConnections = new LinkedList<>();

        @Override
        protected final Connection initialValue() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // touch the mysql driver
            } catch (final ClassNotFoundException e) {
                System.err.println("ERROR" + e);
            }
            try {
                Connection con = DriverManager.getConnection(Property_Database.getUrl(), Property_Database.getUser(), Property_Database.getPassword());
                allConnections.add(con);
                return con;
            } catch (SQLException ex) {
                System.getLogger(DatabaseConnection.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            DebugLogger.ErrorLog("Database : not found.");
            return null;
        }
    }
}
