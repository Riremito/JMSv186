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
package tacos.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class TacosDB {

    public static final TableMonsterBook MONSTER_BOOK = new TableMonsterBook();

    protected final String DB_TABLE_NAME;
    protected String PREVIOUS_SQL;

    protected TacosDB(String table_name) {
        this.DB_TABLE_NAME = table_name;
        this.PREVIOUS_SQL = "";
    }

    protected Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        PREVIOUS_SQL = sql;
        //DebugLogger.DebugLog(PREVIOUS_SQL);
        return getConnection().prepareStatement(sql);
    }

    protected void error() {
        DebugLogger.DBErrorLog(DB_TABLE_NAME, "error, " + PREVIOUS_SQL);
    }

    protected boolean setManual() {
        try {
            Connection con = getConnection();
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setManual is failed.");
        }
        return false;
    }

    protected boolean setAuto() {
        try {
            Connection con = getConnection();
            con.setAutoCommit(true);
            con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setAuto is failed.");
        }
        return false;
    }

    protected boolean commit() {
        try {
            Connection con = getConnection();
            con.commit();
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "commit is failed.");
        }
        return false;
    }

    protected boolean rollback() {
        DebugLogger.DBErrorLog(DB_TABLE_NAME, "rollback");
        try {
            Connection con = getConnection();
            con.rollback();
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "rollback is failed.");
        }
        return false;
    }
}
