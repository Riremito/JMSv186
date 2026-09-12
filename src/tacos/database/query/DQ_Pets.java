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
import odin.client.inventory.MaplePet;
import tacos.database.DatabaseConnection;
import tacos.debug.DebugLogger;

/**
 *
 * @author Riremito
 */
public class DQ_Pets {

    public static final String DB_TABLE_NAME = "pets";

    public static boolean load(MaplePet pet, int petId) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM " + DB_TABLE_NAME + " WHERE petid = ?")) {
                ps.setInt(1, petId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return false;
                    }
                    pet.setName(rs.getString("name"));
                    pet.setCloseness(rs.getShort("closeness"));
                    pet.setLevel(rs.getByte("level"));
                    pet.setFullness(rs.getByte("fullness"));
                    pet.setSecondsLeft(rs.getInt("seconds"));
                    return true;
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "load");
        }

        return false;
    }

    public static boolean save(MaplePet pet) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET name = ?, level = ?, closeness = ?, fullness = ?, seconds = ? WHERE petid = ?")) {
                ps.setString(1, pet.getName());
                ps.setByte(2, pet.getLevel());
                ps.setShort(3, pet.getCloseness());
                ps.setByte(4, pet.getFullness());
                ps.setInt(5, pet.getSecondsLeft());
                ps.setInt(6, pet.getUniqueId());
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "save");
        }

        return false;
    }

    public static boolean add(int uniqueid, String name, int level, int closeness, int fullness, int secondsLeft) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (petid, name, level, closeness, fullness, seconds) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setInt(1, uniqueid);
                ps.setString(2, name);
                ps.setByte(3, (byte) level);
                ps.setShort(4, (short) closeness);
                ps.setByte(5, (byte) fullness);
                ps.setInt(6, secondsLeft);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "add");
        }

        return false;
    }

    public static int getMaxPetId() {
        int ret = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT MAX(petid) FROM " + DB_TABLE_NAME)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ret = rs.getInt(1) + 1;
                    }
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "getMaxPetId");
        }

        return ret;
    }

}
