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
import odin.client.inventory.MapleMount;
import tacos.client.TacosCharacter;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Mountdata {

    public static final String DB_TABLE_NAME = "mountdata";

    public static boolean add(TacosCharacter chr) {
        if (!DatabaseConnection.setManual()) {
            return false;
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (characterid, `Level`, `Exp`, `Fatigue`) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, chr.getId());
                ps.setByte(2, (byte) 1);
                ps.setInt(3, 0);
                ps.setByte(4, (byte) 0);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add");
            DatabaseConnection.rollback();
        } finally {
            DatabaseConnection.commit();
            DatabaseConnection.setAuto();
        }

        return false;
    }

    /**
     * NOTE: this declares {@code throws SQLException} instead of catching
     * it internally (unlike {@code add}/{@code update} above), matching the
     * original MapleCharacter.loadCharFromDB behavior, which relies on the
     * exception (and the not-found RuntimeException below) propagating to
     * its own outer catch block.
     */
    public static Row load(int characterId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("No mount data found on SQL column");
                }
                return new Row(rs.getByte("Fatigue"), rs.getByte("Level"), rs.getInt("Exp"));
            }
        }
    }

    public static boolean update(MapleMount mount, int characterId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " set `Level` = ?, `Exp` = ?, `Fatigue` = ? WHERE characterid = ?")) {
                ps.setByte(1, (byte) mount.getLevel());
                ps.setInt(2, mount.getExp());
                ps.setByte(3, (byte) mount.getFatigue());
                ps.setInt(4, characterId);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "update");
        }

        return false;
    }

    public static final class Row {
        public final byte fatigue;
        public final byte level;
        public final int exp;
        public Row(byte fatigue, byte level, int exp) {
            this.fatigue = fatigue;
            this.level = level;
            this.exp = exp;
        }
    }
}
