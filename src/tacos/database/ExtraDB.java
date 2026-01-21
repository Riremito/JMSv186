/*
 * Copyright (C) 2024 Riremito
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
import odin.client.MapleCharacter;
import tacos.debug.DebugLogger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Riremito
 */
public class ExtraDB {

    private static final String DB_TABLE_NAME = "__root";

    // data
    public static final String DATA_PET_HP = "pet_hp";
    public static final String DATA_PET_MP = "pet_mp";
    public static final String DATA_PET_CURE = "pet_cure";

    public static void loadData(MapleCharacter chr) {
        ExtraDBData pet_hp = get(chr, DATA_PET_HP);
        if (pet_hp.getOk()) {
            chr.setPetAutoHPItem(pet_hp.getInt());
        }
        ExtraDBData pet_mp = get(chr, DATA_PET_MP);
        if (pet_mp.getOk()) {
            chr.setPetAutoMPItem(pet_mp.getInt());
        }
        ExtraDBData pet_cure = get(chr, DATA_PET_CURE);
        if (pet_cure.getOk()) {
            chr.setPetAutoCureItem(pet_cure.getInt());
        }
    }

    public static void saveData(MapleCharacter chr) {
        setInt(chr, DATA_PET_HP, chr.getPetAutoHPItem());
        setInt(chr, DATA_PET_MP, chr.getPetAutoMPItem());
        setInt(chr, DATA_PET_CURE, chr.getPetAutoCureItem());
    }

    public static boolean setInt(MapleCharacter chr, String data_name, int value) {
        ExtraDBData edd = get(chr, data_name);
        if (edd.getOk()) {
            // 更新不要
            if (edd.getInt() == value) {
                return true;
            }

            updateInt(chr, data_name, value);
            return true;
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (maple_id, character_id, data_name, value_int) VALUES (?, ?, ?, ?);")) {
                ps.setInt(1, chr.getAccountID());
                ps.setInt(2, chr.getId());
                ps.setString(3, data_name);
                ps.setInt(4, value);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setInt");
        }

        return false;
    }

    public static boolean setStr(MapleCharacter chr, String data_name, String value) {
        ExtraDBData edd = get(chr, data_name);
        if (edd.getOk()) {
            // 更新不要
            if (edd.getStr().equals(value)) {
                return true;
            }

            updateStr(chr, data_name, value);
            return true;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + DB_TABLE_NAME + " (maple_id, character_id, data_name, value_str) VALUES (?, ?, ?, ?);")) {
                ps.setInt(1, chr.getAccountID());
                ps.setInt(2, chr.getId());
                ps.setString(3, data_name);
                ps.setString(4, value);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "setStr");
        }

        return false;
    }

    private static boolean updateInt(MapleCharacter chr, String data_name, int value) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET `value_int` = ? WHERE maple_id = ? AND character_id = ? AND data_name = ?;")) {
                ps.setInt(1, value);
                ps.setInt(2, chr.getAccountID());
                ps.setInt(3, chr.getId());
                ps.setString(4, data_name);
                ps.execute();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateInt");
        }
        return true;
    }

    private static boolean updateStr(MapleCharacter chr, String data_name, String value) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE " + DB_TABLE_NAME + " SET `value_str` = ? WHERE maple_id = ? AND character_id = ? AND data_name = ?;")) {
                ps.setString(1, value);
                ps.setInt(2, chr.getAccountID());
                ps.setInt(3, chr.getId());
                ps.setString(4, data_name);
                ps.execute();
            }
            return true;
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "updateStr");
        }
        return true;
    }

    public static ExtraDBData get(MapleCharacter chr, String data_name) {
        ExtraDBData edd = new ExtraDBData();

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT `value_int`, `value_str` from " + DB_TABLE_NAME + " where maple_id = ? AND character_id = ? AND data_name = ?;");
            ps.setInt(1, chr.getAccountID());
            ps.setInt(2, chr.getId());
            ps.setString(3, data_name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    edd.setInt(rs.getInt("value_int"));
                    edd.setStr(rs.getString("value_str"));
                    edd.setOk(true);
                }
            }
        } catch (SQLException ex) {
            DebugLogger.DBErrorLog(DB_TABLE_NAME, "get");
        }

        return edd;
    }

}
