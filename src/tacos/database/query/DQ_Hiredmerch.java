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
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Hiredmerch {

    public static final String DB_TABLE_NAME = "hiredmerch";

    // the meso of a hired merchant's goods, and when they were stored (time the merchant was opened or moved to the store bank).
    public static final class HiredmerchRow {

        public final int meso;
        public final long time;

        public HiredmerchRow(int meso, long time) {
            this.meso = meso;
            this.time = time;
        }
    }

    public static HiredmerchRow load(int character_id) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT Mesos, time FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
                ps.setInt(1, character_id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new HiredmerchRow(rs.getInt("Mesos"), rs.getLong("time"));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return null;
    }

    // one row per character.
    public static boolean save(int character_id, int account_id, int meso, long time) {
        if (!deleteByCharacterId(character_id)) {
            return false;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, accountid, Mesos, time) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, character_id);
                ps.setInt(2, account_id);
                ps.setInt(3, meso);
                ps.setLong(4, time);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "save");
        }

        return false;
    }

    public static boolean updateTime(int character_id, long time) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET time = ? WHERE characterid = ?")) {
                ps.setLong(1, time);
                ps.setInt(2, character_id);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateTime");
        }

        return false;
    }

    public static boolean deleteByCharacterId(int character_id) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
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
