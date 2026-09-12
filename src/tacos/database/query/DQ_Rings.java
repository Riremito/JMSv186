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
import odin.client.inventory.MapleRing;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Rings {

    public static final String DB_TABLE_NAME = "rings";

    public static MapleRing load(int ringId, boolean equipped) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE ringId = ?")) {
                ps.setInt(1, ringId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        MapleRing ret = new MapleRing(ringId, rs.getInt("partnerRingId"), rs.getInt("partnerChrId"), rs.getInt("itemid"), rs.getString("partnerName"));
                        ret.setEquipped(equipped);
                        return ret;
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return null;
    }

    public static boolean add(int itemid, MapleCharacter chr, String player, int id, int[] ringId) {
        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (ringId, itemid, partnerChrId, partnerName, partnerRingId) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, ringId[0]);
            ps.setInt(2, itemid);
            ps.setInt(3, chr.getId());
            ps.setString(4, chr.getName());
            ps.setInt(5, ringId[1]);
            ps.executeUpdate();
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add : 1");
            return false;
        }

        try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (ringId, itemid, partnerChrId, partnerName, partnerRingId) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, ringId[1]);
            ps.setInt(2, itemid);
            ps.setInt(3, id);
            ps.setString(4, player);
            ps.setInt(5, ringId[0]);
            ps.executeUpdate();
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add : 2");
            return false;
        }

        return true;
    }

    public static void remove(MapleCharacter player) {
        try {
            Connection con = DatabaseConnection.getConnection();
            int otherId;
            int otherotherId;
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE partnerChrId = ?")) {
                ps.setInt(1, player.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return;
                    }
                    otherId = rs.getInt("partnerRingId");
                    otherotherId = rs.getInt("ringId");
                }
            }
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM " + DB_TABLE_NAME + " WHERE ringId = ? OR ringId = ?")) {
                ps.setInt(1, otherotherId);
                ps.setInt(2, otherId);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "remove");
        }
    }

    public static int getMaxRingId() {
        int ret = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT MAX(ringid) FROM " + DB_TABLE_NAME)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ret = rs.getInt(1) + 1;
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getMaxRingId");
        }

        return ret;
    }

    public static int getMaxPartnerRingId() {
        int ret = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT MAX(partnerringid) FROM " + DB_TABLE_NAME)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ret = rs.getInt(1) + 1;
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getMaxPartnerRingId");
        }

        return ret;
    }

}
