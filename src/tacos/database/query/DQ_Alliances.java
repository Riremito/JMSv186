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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Alliances {

    public static final String DB_TABLE_NAME = "alliances";

    public static AllianceRow load(int id) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE id = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    int[] guilds = new int[5];
                    String[] ranks = new String[5];
                    for (int i = 1; i < 6; i++) {
                        guilds[i - 1] = rs.getInt("guild" + i);
                        ranks[i - 1] = rs.getString("rank" + i);
                    }
                    return new AllianceRow(rs.getString("name"), rs.getInt("capacity"), guilds, ranks, rs.getInt("leaderid"), rs.getString("notice"));
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return null;
    }

    public static List<Integer> getAllAllianceIds() {
        List<Integer> ret = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT id FROM " + DB_TABLE_NAME)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.add(rs.getInt("id"));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getAllAllianceIds");
        }

        return ret;
    }

    /**
     * Mirrors the original MapleGuildAlliance.createToDb SQL exactly,
     * including its pre-existing name-check logic (the check is inverted:
     * it returns -1 when NO existing alliance has this name, and otherwise
     * falls through to insert a duplicate-named row). This looks like a
     * pre-existing bug in the original code, preserved here rather than
     * fixed.
     */
    public static int create(int leaderId, String name, int guild1, int guild2) {
        int ret = -1;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT id FROM " + DB_TABLE_NAME + " WHERE name = ?")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {// name taken
                        return ret;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement("insert into " + DB_TABLE_NAME + " (name, guild1, guild2, leaderid) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setInt(2, guild1);
                ps.setInt(3, guild2);
                ps.setInt(4, leaderId);
                ps.execute();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        ret = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "create");
        }

        return ret;
    }

    public static boolean delete(int allianceid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("delete from " + DB_TABLE_NAME + " where id = ?")) {
                ps.setInt(1, allianceid);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "delete");
        }

        return false;
    }

    public static boolean update(int allianceid, int[] guilds, String[] ranks, int capacity, int leaderid, String notice) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " set guild1 = ?, guild2 = ?, guild3 = ?, guild4 = ?, guild5 = ?, rank1 = ?, rank2 = ?, rank3 = ?, rank4 = ?, rank5 = ?, capacity = ?, leaderid = ?, notice = ? where id = ?")) {
                for (int i = 0; i < 5; i++) {
                    ps.setInt(i + 1, guilds[i] < 0 ? 0 : guilds[i]);
                    ps.setString(i + 6, ranks[i]);
                }
                ps.setInt(11, capacity);
                ps.setInt(12, leaderid);
                ps.setString(13, notice);
                ps.setInt(14, allianceid);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "update");
        }

        return false;
    }

    public static final class AllianceRow {

        public final String name;
        public final int capacity;
        public final int[] guilds;
        public final String[] ranks;
        public final int leaderid;
        public final String notice;

        public AllianceRow(String name, int capacity, int[] guilds, String[] ranks, int leaderid, String notice) {
            this.name = name;
            this.capacity = capacity;
            this.guilds = guilds;
            this.ranks = ranks;
            this.leaderid = leaderid;
            this.notice = notice;
        }
    }

}
