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
import java.sql.ResultSet;
import java.sql.SQLException;
import odin.client.MapleCharacter;
import tacos.database.DatabaseConnection;
import tacos.database.LazyData;
import tacos.database.LazyDataNames;
import tacos.database.LazyDataTypes;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Root {

    public static final String DB_TABLE_NAME = "__root";

    public static boolean get(MapleCharacter chr, LazyData ld) {
        LazyDataNames ldn = ld.getDataName();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT `value_int`, `value_str` from " + DB_TABLE_NAME + " where maple_id = ? AND character_id = ? AND data_name = ?;")) {
                ps.setInt(1, chr.getAccountId());
                ps.setInt(2, chr.getId());
                ps.setString(3, ldn.getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (ldn.getType() == LazyDataTypes.TYPE_INT) {
                            int value_int = rs.getInt("value_int");
                            ld.setInt(value_int);
                            return true;
                        }
                        if (ldn.getType() == LazyDataTypes.TYPE_STR) {
                            String value_str = rs.getString("value_str");
                            ld.setStr(value_str);
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "get");
        }

        return false;
    }

    public static boolean setInt(MapleCharacter chr, LazyData ld) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (maple_id, character_id, data_name, value_int) VALUES (?, ?, ?, ?);")) {
                ps.setInt(1, chr.getAccountId());
                ps.setInt(2, chr.getId());
                ps.setString(3, ld.getDataName().getName());
                ps.setInt(4, ld.getInt());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setInt");
        }

        return false;
    }

    public static boolean setStr(MapleCharacter chr, LazyData ld) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (maple_id, character_id, data_name, value_str) VALUES (?, ?, ?, ?);")) {
                ps.setInt(1, chr.getAccountId());
                ps.setInt(2, chr.getId());
                ps.setString(3, ld.getDataName().getName());
                ps.setString(4, ld.getStr());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setStr");
        }

        return false;
    }

    public static boolean updateInt(MapleCharacter chr, LazyData ld) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET `value_int` = ? WHERE maple_id = ? AND character_id = ? AND data_name = ?;")) {
                ps.setInt(1, ld.getInt());
                ps.setInt(2, chr.getAccountId());
                ps.setInt(3, chr.getId());
                ps.setString(4, ld.getDataName().getName());
                ps.execute();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateInt");
        }

        return false;
    }

    public static boolean updateStr(MapleCharacter chr, LazyData ld) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET `value_str` = ? WHERE maple_id = ? AND character_id = ? AND data_name = ?;")) {
                ps.setString(1, ld.getStr());
                ps.setInt(2, chr.getAccountId());
                ps.setInt(3, chr.getId());
                ps.setString(4, ld.getDataName().getName());
                ps.execute();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateStr");
        }

        return false;
    }
}
