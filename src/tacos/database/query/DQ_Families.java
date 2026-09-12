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
public class DQ_Families {

    public static final String DB_TABLE_NAME = "families";

    public static FamilyRow load(int familyid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE familyid = ?")) {
                ps.setInt(1, familyid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return new FamilyRow(rs.getInt("leaderid"), rs.getString("notice"));
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return null;
    }

    public static List<Integer> getAllFamilyIds() {
        List<Integer> ret = new ArrayList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT familyid FROM " + DB_TABLE_NAME)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ret.add(rs.getInt("familyid"));
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getAllFamilyIds");
        }

        return ret;
    }

    public static boolean update(int familyid, String notice, int leaderid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET notice = ?, leaderid = ? WHERE familyid = ?")) {
                ps.setString(1, notice);
                ps.setInt(2, leaderid);
                ps.setInt(3, familyid);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "update");
        }

        return false;
    }

    public static boolean delete(int familyid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE familyid = ?")) {
                ps.setInt(1, familyid);
                ps.execute();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "delete");
        }

        return false;
    }

    /**
     * Creates a new family row. Returns the generated familyid, or 0 on
     * failure.
     */
    public static int create(int leaderId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (`leaderid`) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, leaderId);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    return rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    public static final class FamilyRow {

        public final int leaderid;
        public final String notice;

        public FamilyRow(int leaderid, String notice) {
            this.leaderid = leaderid;
            this.notice = notice;
        }
    }

}
