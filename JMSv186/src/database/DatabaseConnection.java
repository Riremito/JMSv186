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
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import server.ServerProperties;

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
    private static String url, user, password;

    public static final void LoadConfig() {
        Properties p = ServerProperties.LoadConfig("properties/database.properties");
        url = p.getProperty("database.url");
        user = p.getProperty("database.user");
        password = p.getProperty("database.password");
    }

    public static final Connection getConnection() {
        return connected.get();
    }

    public static final void closeAll() throws SQLException {
        for (final Connection disconnect : ThreadLocalConnection.allConnections) {
            disconnect.close();
        }
    }

    private static final class ThreadLocalConnection extends ThreadLocal<Connection> {

        public static final Collection<Connection> allConnections = new LinkedList<Connection>();

        @Override
        protected final Connection initialValue() {
            try {
                Class.forName("com.mysql.jdbc.Driver"); // touch the mysql driver
            } catch (final ClassNotFoundException e) {
                System.err.println("ERROR" + e);
            }
            try {
                final Connection con = DriverManager.getConnection(url, user, password);
                allConnections.add(con);
                return con;
            } catch (SQLException e) {
                System.err.println("ERROR" + e);
                return null;
            }
        }
    }
}
